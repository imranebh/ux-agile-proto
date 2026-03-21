package net.est.uxagile.realtime;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseRealtimeTrackingService implements RealtimeTrackingService {

    private final Map<Long, List<SseEmitter>> emittersByRide = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long rideId) {
        SseEmitter emitter = new SseEmitter(0L);
        emittersByRide.computeIfAbsent(rideId, key -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(rideId, emitter));
        emitter.onTimeout(() -> removeEmitter(rideId, emitter));
        return emitter;
    }

    @Override
    public void publishRideUpdate(Long rideId, Object payload) {
        send(rideId, "ride-update", payload);
    }

    @Override
    public void publishDriverLocation(Long rideId, Object payload) {
        send(rideId, "driver-location", payload);
    }

    private void send(Long rideId, String event, Object payload) {
        List<SseEmitter> emitters = emittersByRide.getOrDefault(rideId, List.of());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(event).data(payload));
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
    }

    private void removeEmitter(Long rideId, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByRide.get(rideId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}
