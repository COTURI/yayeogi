package yayeogi.Flight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FlightService {
    private final TokenService tokenService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public FlightService(TokenService tokenService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getFlightDestinations(String origin, double maxPrice) throws Exception {
        // maxPrice를 long으로 변환
        long maxPriceLong = (long) maxPrice;

        // API URL 생성
        String url = String.format("https://test.api.amadeus.com/v1/shopping/flight-destinations?origin=%s&maxPrice=%d", origin, maxPriceLong);

        // Authorization 토큰 가져오기
        String token = tokenService.getAccessToken();
        System.out.println("Authorization Token: " + token);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출 및 응답 처리
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            System.out.println("Flight API Response: " + response.getStatusCode()); // 응답 로그 추가
            System.out.println("Flight API Response Body: " + response.getBody()); // 응답 내용 로그 추가
            return parseFlightData(response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Client Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            throw e;
        }
    }

    private String parseFlightData(String jsonResponse) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode flights = rootNode.path("data");

        StringBuilder result = new StringBuilder();
        for (JsonNode flight : flights) {
            String origin = flight.path("origin").asText();
            String destination = flight.path("destination").asText();
            String departureDate = flight.path("departureDate").asText();
            String returnDate = flight.path("returnDate").asText();
            String price = flight.path("price").path("total").asText();

            result.append(String.format("Origin: %s, Destination: %s, Departure Date: %s, Return Date: %s, Price: %s\n",
                    origin, destination, departureDate, returnDate, price));
        }
        return result.toString();
    }
}
