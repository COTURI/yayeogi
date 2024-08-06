package yayeogi.config;

import com.amadeus.Amadeus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {



    @Bean
    public Amadeus amadeus(@Value("${amadeus.api.key}") String apiKey,
                           @Value("${amadeus.api.secret}") String apiSecret) {
        return Amadeus.builder(apiKey, apiSecret).build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
