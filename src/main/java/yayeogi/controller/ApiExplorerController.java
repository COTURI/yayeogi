package yayeogi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;

@RestController
public class ApiExplorerController {

    @Value("${api.service.key}")
    private String serviceKey;

    private final String apiUrl = "http://openapi.airport.co.kr/service/rest/AirportCodeList/getAirportCodeList";

    @GetMapping("/api/airports")
    public String getAirportCodes(@RequestParam(value = "serviceKey", required = false) String serviceKeyParam) {
        String keyToUse = (serviceKeyParam != null) ? serviceKeyParam : this.serviceKey;

        String encodedServiceKey;
        try {
            encodedServiceKey = URLEncoder.encode(keyToUse, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error encoding the service key.";
        }

        String url = apiUrl + "?" + "serviceKey=" + encodedServiceKey;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return "Request failed.";
                    })
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Request failed.";
        }
    }
}


/* 쓸때 써야하는 주소
http://localhost:8080/api/airports?serviceKey=XQLXUs77FcLzGWINNjLCiomKx%2B0%2Fq5oRC47Bgdu3Uz%2FjnXlZ163EEGID3JOr6LcoGzeoQT3smmDqan0XH8WSFw%3D%3D*/
