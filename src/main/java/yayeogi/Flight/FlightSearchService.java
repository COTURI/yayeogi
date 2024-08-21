package yayeogi.Flight;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class FlightSearchService {

    @Value("${amadeus.api.key}")
    private String apiKey;

    @Value("${amadeus.api.secret}")
    private String apiSecret;

    private final TokenService tokenService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public FlightSearchService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public String searchFlightsAsString(String origin, String destination, String departureDate,
                                        String returnDate, int adults) {
        try {
            // 액세스 토큰을 얻습니다.
            String accessToken = tokenService.getAccessToken();
            System.out.println("Access Token: " + accessToken); // 로그로 토큰 확인

            // API 요청 URL 구성
            String url = "https://test.api.amadeus.com/v2/shopping/flight-offers?" +
                    "originLocationCode=" + origin +
                    "&destinationLocationCode=" + destination +
                    "&departureDate=" + departureDate +
                    "&returnDate=" + (returnDate != null ? returnDate : "") +
                    "&adults=" + adults +
                    "&max=50";

            // 요청 헤더 구성
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // REST API 호출
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                // 디버깅을 위해 상태 코드와 응답 본문 출력
                System.out.println("Response Status Code: " + response.getStatusCode());
                System.out.println("Response Body: " + response.getBody());
                throw new RuntimeException("Failed to get flight offers. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{}"; // 빈 JSON 객체 반환
        }
    }

    // URL 인코딩 메서드 추가
    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
    }
}