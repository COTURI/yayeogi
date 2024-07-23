package yayeogi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ApiExplorerController {

    private final String apiUrl = "http://openapi.airport.co.kr/service/rest/AirportCodeList/getAirportCodeList";

    @GetMapping("/api/airports")
    public String getAirportCodes(@RequestParam String serviceKey) {
        String url = apiUrl + "?" + "serviceKey=" + "XQLXUs77FcLzGWINNjLCiomKx+0/q5oRC47Bgdu3Uz/jnXlZ163EEGID3JOr6LcoGzeoQT3smmDqan0XH8WSFw==";

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        return response;
    }
}