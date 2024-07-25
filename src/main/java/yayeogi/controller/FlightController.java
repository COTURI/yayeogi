package yayeogi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yayeogi.Flight.FlightService;

@RestController
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights")
    public String getFlightDestinations(@RequestParam String origin, @RequestParam double maxPrice) throws Exception {
        return flightService.getFlightDestinations(origin, maxPrice);
    }
}