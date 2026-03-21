package net.est.uxagile.service;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.domain.enums.PaymentStatus;
import net.est.uxagile.domain.enums.RideStatus;
import net.est.uxagile.domain.model.Payment;
import net.est.uxagile.domain.model.PaymentMethod;
import net.est.uxagile.domain.model.Ride;
import net.est.uxagile.domain.model.User;
import net.est.uxagile.dto.PaymentDtos;
import net.est.uxagile.exception.ApiException;
import net.est.uxagile.integration.PaymentProvider;
import net.est.uxagile.mapper.PaymentMapper;
import net.est.uxagile.repository.PaymentMethodRepository;
import net.est.uxagile.repository.PaymentRepository;
import net.est.uxagile.repository.RideRepository;
import net.est.uxagile.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;
    private final PaymentProvider paymentProvider;
    private final PaymentMapper paymentMapper;
    private final InvoiceService invoiceService;

    public List<PaymentDtos.PaymentMethodResponse> methods(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return paymentMethodRepository.findByUser(user).stream().map(paymentMapper::toMethodResponse).toList();
    }

    @Transactional
    public PaymentDtos.PaymentMethodResponse addMethod(String email, PaymentDtos.AddPaymentMethodRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        String token = paymentProvider.tokenizeCard(request.getCardNumber(), request.getExpMonth(), request.getExpYear(), request.getCvv());

        PaymentMethod method = new PaymentMethod();
        method.setUser(user);
        method.setProvider(request.getProvider().toUpperCase());
        method.setBrand(request.getBrand().toUpperCase());
        method.setLast4(request.getCardNumber().substring(request.getCardNumber().length() - 4));
        method.setToken(token);
        method.setExpiryMonth(request.getExpMonth());
        method.setExpiryYear(request.getExpYear());
        method.setDefaultMethod(paymentMethodRepository.findByUser(user).isEmpty());
        paymentMethodRepository.save(method);
        return paymentMapper.toMethodResponse(method);
    }

    @Transactional
    public PaymentDtos.PaymentResponse chargeRide(String email, Long rideId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ride not found"));

        if (!ride.getPassenger().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your ride");
        }
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Payment is only allowed after ride completion");
        }

        Payment existing = paymentRepository.findByRide(ride).orElse(null);
        if (existing != null && existing.getStatus() == PaymentStatus.SUCCEEDED) {
            return paymentMapper.toPaymentResponse(existing);
        }

        PaymentMethod method = paymentMethodRepository.findByUserAndDefaultMethodTrue(user)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No default payment method"));

        var amount = ride.getFinalPrice() != null ? ride.getFinalPrice() : ride.getEstimatedPrice();
        var charge = paymentProvider.charge(method.getToken(), amount);
        Payment payment = existing == null ? new Payment() : existing;
        payment.setRide(ride);
        payment.setPayer(user);
        payment.setPaymentMethod(method);
        payment.setAmount(amount);
        payment.setStatus(charge.success() ? PaymentStatus.SUCCEEDED : PaymentStatus.FAILED);
        payment.setProviderReference(charge.reference());
        paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            invoiceService.generateIfMissing(ride, payment);
        }

        return paymentMapper.toPaymentResponse(payment);
    }
}
