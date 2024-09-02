
package yayeogi.Green3.controller;

import yayeogi.Green3.entity.Hotel;
import yayeogi.Green3.entity.HotelReservation;
import yayeogi.Green3.repository.HotelRepository;
import yayeogi.Green3.repository.HotelReservationRepository;
import yayeogi.Green3.repository.UserRepository;
import yayeogi.Green3.service.HotelReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import yayeogi.Green3.entity.User;
import yayeogi.Green3.service.ReservationService;

import java.util.List;

@Controller
public class HotelReservationController {

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri1}")
    private String kakaoRedirectUri1;


    @Autowired
    private final HotelReservationService hotelReservationService;

    @Autowired
    private HotelReservationRepository hotelReservationRepository;

    @Autowired
    private HotelRepository hotelRepository;


    private final UserRepository userRepository;
    @Autowired
    public HotelReservationController(HotelReservationService hotelReservationService,UserRepository userRepository ) {
        this.hotelReservationService = hotelReservationService;
        this.userRepository = userRepository;
    }



    // 예약 확인
    @GetMapping("/HotelConfirmation")
    public String getReservationConfirmation(HttpServletRequest request, Model model) {


            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                String email = user.getEmail();  // 세션에서 사용자 이메
                // 일 가져오기

                // 이메일로 예약 정보를 조회
                List<HotelReservation> reservations = hotelReservationService.findHotelReservationsByUserEmail(email);
                if (reservations.isEmpty()) {
                    model.addAttribute("message", "예약 정보가 없습니다.");
                } else {
                    model.addAttribute("reservations", reservations);
                }
            } else {
                model.addAttribute("message", "로그인 상태가 아닙니다.");
            }
            return "reservation-confirmation-hotel";



    }


    // 예약 취소
    @PostMapping("/HotelReservation/cancel/{reservationId}")
    public String cancelReservation(@PathVariable Integer reservationId, Model model) {
        try {
            hotelReservationService.cancelHotelReservationById(reservationId);
            model.addAttribute("message", "예약이 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            model.addAttribute("message", "예약 취소 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return "redirect:/confirmation"; // 취소 후 목록 페이지로 리디렉션
    }

    @PostMapping("/HotelReservation")
    public String createReservation(@ModelAttribute HotelReservation hotelReservation, HttpSession session, Model model,HotelRepository hotelRepository) {
        User authenticatedUser = (User) session.getAttribute("user");
       if (authenticatedUser == null) {
            model.addAttribute("message", "사용자 정보가 세션에 없습니다. 로그인 상태를 확인해주세요.");
            return "error";
        }

/*
        // 세션에서 호텔 ID를 가져와서 Hotel 객체를 조회
        Integer hotelId = (Integer) session.getAttribute("hotelId");
       if (hotelId == null) {
            model.addAttribute("message", "호텔 정보가 세션에 없습니다.");
            return "error";
        }

        Hotel hotel = hotelRepository.findById(Long.valueOf(hotelId))
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));
*/

        // 사용자 이메일을 HotelReservation에 설정
        hotelReservation.setEmail(authenticatedUser.getEmail());
       /* hotelReservation.setHotel(hotel); // Hotel 객체를 설정*/

        // 액세스 토큰을 세션에서 가져옴
        String accessToken = (String) session.getAttribute("accessToken");
        if (accessToken == null) {
            session.setAttribute("pendingReservation", hotelReservation);
            return "redirect:/kakao-login1";
        }

        // 액세스 토큰이 있는 경우 예약을 처리하고 리디렉션
        session.setAttribute("hotelReservation", hotelReservation);
        return "redirect:/HotelPayment";
    }

    @GetMapping("/kakao-login1")
    public String kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri1 +
                "&response_type=code";
        return "redirect:" + kakaoAuthUrl;
    }


    // 카카오 콜백 처리
    @GetMapping("/kakao-callback1")
    public String kakaoCallback1(@RequestParam("code") String code, HttpSession session) throws JsonProcessingException {
        final String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri1); // 새로운 URI 사용
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String accessToken = root.path("access_token").asText();
            session.setAttribute("accessToken", accessToken);
            System.out.println("AccessToken saved to session: " + accessToken);

            HotelReservation pendingReservation = (HotelReservation) session.getAttribute("pendingReservation");
            System.out.println("PendingReservation retrieved from session: " + pendingReservation);

            if (pendingReservation != null) {
                session.removeAttribute("pendingReservation");
                session.setAttribute("HotelReservation", pendingReservation);
                System.out.println("HotelReservation saved to session after callback: " + pendingReservation);
                return "redirect:/HotelPayment";
            } else {
                System.out.println("PendingReservation is null, redirecting to HotelReservation");
                return "redirect:/HotelReservation";
            }
        } else {
            System.err.println("Error occurred during Kakao OAuth process: " + response.getStatusCode());
            return "error"; // 에러 페이지로 리디렉션
        }
    }


}
