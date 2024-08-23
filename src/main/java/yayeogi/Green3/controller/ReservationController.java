package yayeogi.Green3.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import yayeogi.Green3.entity.ReservationFlight;
import yayeogi.Green3.entity.User;
import yayeogi.Green3.repository.ReservationFlightRepository;
import yayeogi.Green3.service.ReservationService;

import java.util.List;

@Controller
public class ReservationController {

    @Autowired
    private final ReservationService reservationService;

    @Autowired
    private ReservationFlightRepository reservationFlightRepository;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
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

    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@RequestBody ReservationFlight reservationFlight) {
        try {
            // 예약 객체를 데이터베이스에 저장
            reservationFlightRepository.save(reservationFlight);

            return ResponseEntity.ok().body(new ResponseMessage(true, "예약이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(false, "예약 중 오류가 발생했습니다."));
        }
    }
}
