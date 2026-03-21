package net.est.uxagile.mapper;

import net.est.uxagile.domain.model.Payment;
import net.est.uxagile.domain.model.PaymentMethod;
import net.est.uxagile.dto.PaymentDtos;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentDtos.PaymentMethodResponse toMethodResponse(PaymentMethod paymentMethod) {
        PaymentDtos.PaymentMethodResponse response = new PaymentDtos.PaymentMethodResponse();
        response.setId(paymentMethod.getId());
        response.setProvider(paymentMethod.getProvider());
        response.setBrand(paymentMethod.getBrand());
        response.setLast4(paymentMethod.getLast4());
        response.setExpiryMonth(paymentMethod.getExpiryMonth());
        response.setExpiryYear(paymentMethod.getExpiryYear());
        response.setDefaultMethod(paymentMethod.getDefaultMethod());
        return response;
    }

    public PaymentDtos.PaymentResponse toPaymentResponse(Payment payment) {
        PaymentDtos.PaymentResponse response = new PaymentDtos.PaymentResponse();
        response.setRideId(payment.getRide().getId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setReference(payment.getProviderReference());
        return response;
    }
}
