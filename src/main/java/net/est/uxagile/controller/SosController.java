package net.est.uxagile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.est.uxagile.dto.SosDtos;
import net.est.uxagile.security.SecurityUtils;
import net.est.uxagile.service.SosService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class SosController {

    private final SosService sosService;

    @PostMapping
    public SosDtos.SosResponse create(@Valid @RequestBody SosDtos.SosRequest request) {
        return sosService.create(SecurityUtils.currentUserEmail(), request);
    }
}
