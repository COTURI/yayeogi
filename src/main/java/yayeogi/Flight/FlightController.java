package yayeogi.Flight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightSearchService flightSearchService;

    @Autowired
    public FlightController(FlightSearchService flightSearchService) {
        this.flightSearchService = flightSearchService;
    }

    @GetMapping
    public ResponseEntity<String> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam int adults) {

        try {
            // FlightSearchService를 사용하여 비행기 검색
            String result = flightSearchService.searchFlightsAsString(origin, destination, departureDate, returnDate, adults);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"검색 중 오류 발생: " + e.getMessage() + "\"}");
        }
    }
}
