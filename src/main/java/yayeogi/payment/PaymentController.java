package yayeogi.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class PaymentController {

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @GetMapping("/payment")
    public ModelAndView startPayment(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            return new ModelAndView("redirect:/paymentlogin");
        }

        String paymentUrl = "https://kapi.kakao.com/v1/payment/ready";
        HttpHeaders paymentHeaders = new HttpHeaders();
        paymentHeaders.set("Authorization", "Bearer " + accessToken);
        paymentHeaders.set("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> paymentParams = new LinkedMultiValueMap<>();
        paymentParams.add("cid", "TC0ONETIME"); // 테스트용 CID
        paymentParams.add("partner_order_id", "partner_order_id");
        paymentParams.add("partner_user_id", "partner_user_id");
        paymentParams.add("item_name", "Item");
        paymentParams.add("quantity", "1");
        paymentParams.add("total_amount", "1000");
        paymentParams.add("vat_amount", "0");
        paymentParams.add("tax_free_amount", "0");
        paymentParams.add("approval_url", "http://localhost:8080/approve");
        paymentParams.add("cancel_url", "http://localhost:8080/cancel");
        paymentParams.add("fail_url", "http://localhost:8080/fail");

        HttpEntity<MultiValueMap<String, String>> paymentEntity = new HttpEntity<>(paymentParams, paymentHeaders);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> paymentResponse = restTemplate.exchange(paymentUrl, HttpMethod.POST, paymentEntity, String.class);

            if (paymentResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(paymentResponse.getBody());
                String redirectUrl = root.path("next_redirect_pc_url").asText();
                String tid = root.path("tid").asText();
                session.setAttribute("tid", tid);
                return new ModelAndView("redirect:" + redirectUrl);
            } else {
                return new ModelAndView("error").addObject("error", "결제 요청 중 오류 발생: " + paymentResponse.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("error").addObject("error", "결제 요청 중 오류 발생: " + e.getMessage());
        }
    }

    @GetMapping("/paymentlogin")
    public RedirectView paymentLogin() {
        String authorizationUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public ModelAndView callback(@RequestParam("code") String code, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String accessToken = extractAccessToken(response.getBody());
            session.setAttribute("accessToken", accessToken);

            // 결제 페이지로 리디렉션
            return new ModelAndView("redirect:/payment");
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", "Failed to get access token");
            return mav;
        }
    }

    private String extractAccessToken(String body) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(body);
            return root.path("access_token").asText();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/approve")
    public String approvePayment(@RequestParam("pg_token") String pgToken, HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        String tid = (String) session.getAttribute("tid");

        if (accessToken == null || tid == null) {
            return "redirect:/error";
        }

        String kakaoPayApproveUrl = "https://kapi.kakao.com/v1/payment/approve";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("partner_order_id", "partner_order_id");
        params.add("partner_user_id", "partner_user_id");
        params.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(kakaoPayApproveUrl, HttpMethod.POST, entity, String.class);
            System.out.println("Approve Response: " + response.getBody()); // 응답 출력

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            // 결제 성공 메시지를 세션에 저장
            session.setAttribute("alertMessage", "결제가 성공적으로 완료되었습니다!");





            return "redirect:/confirmation"; //
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("alertMessage", "결제 처리 중 오류 발생");
            return "redirect:/error";
        }
    }

    @GetMapping("/cancel")
    public String cancelPayment() {
        return "payment-cancelled";
    }

    @GetMapping("/fail")
    public String failPayment() {
        return "payment-failed";
    }
}
