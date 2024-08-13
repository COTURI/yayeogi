package yayeogi.Green3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yayeogi.Green3.repository.ReservationFlightRepository;

@Controller
public class ReservationController {

    @Autowired
    private ReservationFlightRepository reservationFlightRepository;

    @GetMapping("/reservation-confirmation")
    public String getReservationConfirmation(@RequestParam("email") String email, Model model) {
        if (email == null || email.isEmpty()) {
            model.addAttribute("message", "이메일 주소가 제공되지 않았습니다.");
            return "reservation-confirmation-flight";
        }

        var reservations = reservationFlightRepository.findByUserId(email);
        if (reservations.isEmpty()) {
            model.addAttribute("message", "예약 정보가 없습니다.");
        } else {
            model.addAttribute("reservations", reservations);
        }
        return "reservation-confirmation-flight";
    }
}
