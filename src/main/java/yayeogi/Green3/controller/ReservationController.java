package yayeogi.Green3.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import yayeogi.Green3.entity.ReservationFlight;
import yayeogi.Green3.entity.User;
import yayeogi.Green3.repository.ReservationFlightRepository;
import yayeogi.Green3.service.ReservationService;

import java.util.List;

@Controller
public class ReservationController {

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @Autowired
    private final ReservationService reservationService;

    @Autowired
    private ReservationFlightRepository reservationFlightRepository;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservation-detail/{id}")
    public String getReservationDetail(@PathVariable Long id, Model model) {
        ReservationFlight reservation = reservationService.findById(id);
        if (reservation != null) {
            model.addAttribute("reservation", reservation);
            return "reservation-detail"; // Thymeleaf 템플릿 이름
        } else {
            model.addAttribute("message", "예약 정보를 찾을 수 없습니다.");
            return "error"; // 에러 페이지 이름
        }
    }

    @GetMapping("/confirmation")
    public String getReservationConfirmation(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            String email = user.getEmail();  // 세션에서 사용자 이메일 가져오기

            List<ReservationFlight> reservations = reservationService.findReservationsByUserEmail(email);
            if (reservations.isEmpty()) {
                model.addAttribute("message", "예약 정보가 없습니다.");
            } else {
                model.addAttribute("reservations", reservations);
            }
        } else {
            model.addAttribute("message", "로그인 상태가 아닙니다.");
        }
        return "reservation-confirmation-flight";
    }

    @PostMapping("/reservation/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, Model model) {
        try {
            reservationService.cancelReservation(id);
            model.addAttribute("message", "예약이 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            model.addAttribute("message", "예약 취소 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return "redirect:/confirmation"; // 취소 후 목록 페이지로 리디렉션
    }

    @PostMapping("/reservation")
    public String createReservation(@ModelAttribute ReservationFlight reservationFlight, HttpSession session, Model model) {
        User authenticatedUser = (User) session.getAttribute("user");

        if (authenticatedUser == null) {
            model.addAttribute("message", "사용자 정보가 세션에 없습니다. 로그인 상태를 확인해주세요.");
            return "error";
        }

        String userId = authenticatedUser.getEmail();
        reservationFlight.setUserId(userId);
        System.out.println(userId);

        String accessToken = (String) session.getAttribute("accessToken");
        if (accessToken == null) {
            session.setAttribute("pendingReservation", reservationFlight);
            return "redirect:/kakao-login";
        }

        session.setAttribute("reservationFlight", reservationFlight);
        return "redirect:/payment";
    }
    @GetMapping("/kakao-callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session) throws JsonProcessingException {
        final String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String accessToken = root.path("access_token").asText();
            session.setAttribute("accessToken", accessToken);

            // 예약 페이지에서 저장된 예약 정보 확인
            ReservationFlight pendingReservation = (ReservationFlight) session.getAttribute("pendingReservation");
            if (pendingReservation != null) {
                session.removeAttribute("pendingReservation");
                session.setAttribute("reservationFlight", pendingReservation);
                return "redirect:/payment";
            }

            return "redirect:/reservation";
        } else {
            return "error"; // 에러 페이지로 리디렉션
        }
    }
}