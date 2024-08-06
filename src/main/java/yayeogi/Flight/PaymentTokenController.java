package yayeogi.Flight;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/api")
public class PaymentTokenController {

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Value("${kakao.pay.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/get-token")
    @ResponseBody
    public ResponseEntity<String> getToken(@RequestBody String code) {
        String url = apiUrl + "/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String body = "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=http://localhost:8080/payment" +
                "&code=" + code;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to get token");
        }
    }

    @PostMapping("/process-payment")
    @ResponseBody
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) {
        String url = apiUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + paymentRequest.getToken());
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        try {
            String body = "cid=" + URLEncoder.encode(clientId, "UTF-8") +
                    "&partner_order_id=" + URLEncoder.encode(paymentRequest.getOrderId(), "UTF-8") +
                    "&partner_user_id=" + URLEncoder.encode(paymentRequest.getUserId(), "UTF-8") +
                    "&item_name=" + URLEncoder.encode(paymentRequest.getItemName(), "UTF-8") +
                    "&quantity=" + URLEncoder.encode(String.valueOf(paymentRequest.getQuantity()), "UTF-8") +
                    "&total_amount=" + URLEncoder.encode(String.valueOf(paymentRequest.getTotalAmount()), "UTF-8") +
                    "&tax_free_amount=" + URLEncoder.encode(String.valueOf(paymentRequest.getTaxFreeAmount()), "UTF-8") +
                    "&approval_url=http://localhost:8080/payment/success" +
                    "&fail_url=http://localhost:8080/payment/fail" +
                    "&cancel_url=http://localhost:8080/payment/cancel";

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process payment");
            }
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Encoding error");
        }
    }

    public static class PaymentRequest {
        private String token;
        private String orderId;
        private String userId;
        private String itemName;
        private int quantity;
        private int totalAmount;
        private int taxFreeAmount;

        // getters and setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(int totalAmount) {
            this.totalAmount = totalAmount;
        }

        public int getTaxFreeAmount() {
            return taxFreeAmount;
        }

        public void setTaxFreeAmount(int taxFreeAmount) {
            this.taxFreeAmount = taxFreeAmount;
        }
    }
}
