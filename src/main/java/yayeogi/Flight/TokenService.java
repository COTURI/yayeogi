package yayeogi.Flight;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TokenService {

    @Value("${amadeus.api.key}")
    private String apiKey;

    @Value("${amadeus.api.secret}")
    private String apiSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {
        String tokenUrl = "https://test.api.amadeus.com/v1/security/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes()));
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                return jsonResponse.path("access_token").asText();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to parse access token response.");
            }
        } else {
            throw new RuntimeException("Failed to obtain access token. Status code: " + response.getStatusCode());
        }
    }
}
