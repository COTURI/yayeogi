package yayeogi.Flight;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class KakaoPayService {

    @Value("${kakao.secret.key}")  // 실제 REST API 키를 사용해야 합니다.
    private String kakaoSecretKey;

    @Value("${kakao.pay.api.url}")
    private String kakaoPayApiUrl;

    private final RestTemplate restTemplate;

    public KakaoPayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String preparePayment(String orderId, String userId, String itemName, int quantity, int totalAmount, int taxFreeAmount) throws Exception {
        String requestUrl = kakaoPayApiUrl + "/v1/payment/ready";

        // 요청 본문
        String params = "cid=" + URLEncoder.encode("TC0ONETIME", StandardCharsets.UTF_8.name()) +
                "&partner_order_id=" + URLEncoder.encode(orderId, StandardCharsets.UTF_8.name()) +
                "&partner_user_id=" + URLEncoder.encode(userId, StandardCharsets.UTF_8.name()) +
                "&item_name=" + URLEncoder.encode(itemName, StandardCharsets.UTF_8.name()) +
                "&quantity=" + quantity +
                "&total_amount=" + totalAmount +
                "&tax_free_amount=" + taxFreeAmount +
                "&approval_url=" + URLEncoder.encode("http://localhost:8080/approval", StandardCharsets.UTF_8.name()) +
                "&fail_url=" + URLEncoder.encode("http://localhost:8080/fail", StandardCharsets.UTF_8.name()) +
                "&cancel_url=" + URLEncoder.encode("http://localhost:8080/cancel", StandardCharsets.UTF_8.name());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoSecretKey);
        headers.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(requestUrl), HttpMethod.POST, requestEntity, String.class);

        // 응답으로부터 결제 준비 ID를 반환합니다.
        return responseEntity.getBody();
    }
}
