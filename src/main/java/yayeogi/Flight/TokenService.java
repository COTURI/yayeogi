package yayeogi.Flight;

import org.springframework.beans.factory.annotation.Value;
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

    public String getAccessToken() {
        String tokenUrl = "https://test.api.amadeus.com/v1/security/oauth2/token";
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.postForObject(tokenUrl,
                createTokenRequest(),
                String.class);

        try {
            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse.path("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private org.springframework.http.HttpEntity<String> createTokenRequest() {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes()));
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        return new org.springframework.http.HttpEntity<>("grant_type=client_credentials", headers);
    }
}
