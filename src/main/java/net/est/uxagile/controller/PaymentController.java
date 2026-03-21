package net.est.uxagile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.est.uxagile.dto.PaymentDtos;
import net.est.uxagile.security.SecurityUtils;
import net.est.uxagile.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/methods")
    public List<PaymentDtos.PaymentMethodResponse> methods() {
        return paymentService.methods(SecurityUtils.currentUserEmail());
    }

    @PostMapping("/methods")
    public PaymentDtos.PaymentMethodResponse addMethod(@Valid @RequestBody PaymentDtos.AddPaymentMethodRequest request) {
        return paymentService.addMethod(SecurityUtils.currentUserEmail(), request);
    }

    @PostMapping("/charge-ride/{rideId}")
    public PaymentDtos.PaymentResponse chargeRide(@PathVariable Long rideId) {
        return paymentService.chargeRide(SecurityUtils.currentUserEmail(), rideId);
    }
}
