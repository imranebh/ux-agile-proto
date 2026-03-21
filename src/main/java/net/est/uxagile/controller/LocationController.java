package net.est.uxagile.controller;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.integration.MapsProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final MapsProvider mapsProvider;

    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam("q") String query) {
        return mapsProvider.autocomplete(query);
    }
}
