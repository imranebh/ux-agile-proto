package net.est.uxagile.service;

import net.est.uxagile.domain.enums.PaymentStatus;
import net.est.uxagile.dto.PaymentDtos;
import net.est.uxagile.dto.RideDtos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PaymentFlowServiceTest {

    @Autowired
    private RideService rideService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void shouldChargeCompletedRideAndGenerateInvoice() {
        rideService.triggerSafetyCheck(1L, "passenger@autostop.dev");
        rideService.validateDriver(1L, "passenger@autostop.dev");
        rideService.startRide(1L, "passenger@autostop.dev");
        rideService.arriveRide(1L, "passenger@autostop.dev");
        rideService.completeRide(1L, "passenger@autostop.dev");

        PaymentDtos.PaymentResponse payment = paymentService.chargeRide("passenger@autostop.dev", 1L);
        assertEquals(PaymentStatus.SUCCEEDED, payment.getStatus());

        var invoice = invoiceService.getInvoice("passenger@autostop.dev", 1L);
        assertEquals(1L, invoice.getRideId());
    }
}
