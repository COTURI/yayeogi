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
                                                @RequestParam int adults) {
        try {
            // FlightSearchService를 통해 비즈니스 로직 수행
            String jsonResponse = flightSearchService.searchFlightsAsString(origin, destination, departureDate, adults);

            // JSON 문자열을 직접 반환
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // HTTP 500 상태 코드 반환
        }
    }
}
