package yayeogi.Flight;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightController {

    private final FlightSearchService flightSearchService;

    public FlightController(FlightSearchService flightSearchService) {
        this.flightSearchService = flightSearchService;
    }

    @GetMapping("/flights")
    public ResponseEntity<String> searchFlights(@RequestParam String origin,
                                                @RequestParam String destination,
                                                @RequestParam String departureDate,
                                                @RequestParam(required = false) String returnDate,
                                                @RequestParam int adults) {
        try {
            String jsonResponse = flightSearchService.searchFlightsAsString(origin, destination, departureDate, returnDate, adults);
            return ResponseEntity.ok(jsonResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal Server Error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal Server Error\"}");
        }
    }
}
