package net.est.uxagile.controller;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.dto.InvoiceDtos;
import net.est.uxagile.security.SecurityUtils;
import net.est.uxagile.service.InvoiceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{rideId}")
    public InvoiceDtos.InvoiceResponse getByRide(@PathVariable Long rideId) {
        return invoiceService.getInvoice(SecurityUtils.currentUserEmail(), rideId);
    }
}
