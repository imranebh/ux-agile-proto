package net.est.uxagile.controller;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.security.SecurityUtils;
import net.est.uxagile.service.RideService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/realtime")
@RequiredArgsConstructor
public class RealtimeController {

    private final RideService rideService;

    @GetMapping("/rides/{rideId}/stream")
    public SseEmitter stream(@PathVariable Long rideId) {
        return rideService.subscribeForPassenger(rideId, SecurityUtils.currentUserEmail());
    }
}
