package net.est.uxagile.service;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.domain.model.Invoice;
import net.est.uxagile.domain.model.Payment;
import net.est.uxagile.domain.model.Ride;
import net.est.uxagile.dto.InvoiceDtos;
import net.est.uxagile.exception.ApiException;
import net.est.uxagile.repository.InvoiceRepository;
import net.est.uxagile.repository.RideRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final RideRepository rideRepository;

    @Transactional
    public Invoice generateIfMissing(Ride ride, Payment payment) {
        return invoiceRepository.findByRide(ride).orElseGet(() -> {
            Invoice invoice = new Invoice();
            invoice.setRide(ride);
            invoice.setPayment(payment);
            invoice.setAmount(payment.getAmount());
            invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            return invoiceRepository.save(invoice);
        });
    }

    public InvoiceDtos.InvoiceResponse getInvoice(String email, Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ride not found"));
        if (!ride.getPassenger().getEmail().equalsIgnoreCase(email)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your ride invoice");
        }
        Invoice invoice = invoiceRepository.findByRide(ride)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Invoice not found"));
        InvoiceDtos.InvoiceResponse response = new InvoiceDtos.InvoiceResponse();
        response.setRideId(rideId);
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setAmount(invoice.getAmount());
        response.setIssuedAt(invoice.getIssuedAt());
        return response;
    }
}
