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

    @GetMapping("/search")
    public ResponseEntity<Object> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam int adults) {

        // 검증 로직 추가
        if (adults < 1 || adults >= 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("adults 파라미터는 1 이상 10 미만이어야 합니다."));
        }

        try {
            // flightSearchService 호출 (currency 파라미터 제거)
            String result = flightSearchService.searchFlightsAsString(origin, destination, departureDate, returnDate, adults);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("검색 중 오류 발생: " + e.getMessage()));
        }
    }

    // ErrorResponse 클래스를 사용하여 구조화된 오류 응답 반환
    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}