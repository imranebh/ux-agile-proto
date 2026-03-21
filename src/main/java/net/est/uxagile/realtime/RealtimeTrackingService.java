package net.est.uxagile.realtime;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface RealtimeTrackingService {
    SseEmitter subscribe(Long rideId);
    void publishRideUpdate(Long rideId, Object payload);
    void publishDriverLocation(Long rideId, Object payload);
}
