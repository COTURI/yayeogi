package yayeogi.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
import yayeogi.Green3.service.ReservationService;
import yayeogi.Green3.entity.ReservationFlight;
import yayeogi.Green3.service.ReservationService;

import java.util.Map;

@Controller
public class PaymentController {

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    private static final String PAYMENT_URL = "https://kapi.kakao.com/v1/payment/ready";
    private static final String APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";
    private static final String CID = "TC0ONETIME"; // 테스트용 CID

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/kakao-login")
    public String kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code";
        return "redirect:" + kakaoAuthUrl;
    }

    @PostMapping("/payment")
    public ModelAndView handlePayment(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        ReservationFlight reservationFlight = (ReservationFlight) session.getAttribute("reservationFlight");

        if (accessToken == null || reservationFlight == null) {
            return new ModelAndView("redirect:/kakao-login");
        }

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
            ResponseEntity<String> paymentResponse = restTemplate.exchange(PAYMENT_URL, HttpMethod.POST, paymentEntity, String.class);
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

    @GetMapping("/payment")
    public ModelAndView preparePayment(HttpSession session) {
        ReservationFlight reservationFlight = (ReservationFlight) session.getAttribute("reservationFlight");

        if (reservationFlight == null) {
            return new ModelAndView("redirect:/reservation");
        }

        // 결제 요청을 서버에서 자동으로 처리하도록 클라이언트를 준비합니다.
        return new ModelAndView("payment")
                .addObject("reservationFlight", reservationFlight);
    }

    @GetMapping("/approve")
    public String approvePayment(@RequestParam("pg_token") String pgToken, HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        String tid = (String) session.getAttribute("tid");
        ReservationFlight reservationFlight = (ReservationFlight) session.getAttribute("reservationFlight");

        if (accessToken == null || tid == null || reservationFlight == null) {
            return "redirect:/error";
        }


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
            ResponseEntity<String> response = restTemplate.exchange(APPROVE_URL, HttpMethod.POST, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            session.setAttribute("alertMessage", "결제가 성공적으로 완료되었습니다!");

            reservationService.saveReservationFlight(reservationFlight);
            session.removeAttribute("reservationFlight");

            return "redirect:/confirmation";
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