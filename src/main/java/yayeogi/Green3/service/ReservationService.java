package yayeogi.Green3.service;

import yayeogi.Green3.entity.ReservationFlight;
import yayeogi.Green3.repository.ReservationFlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationFlightRepository reservationFlightRepository;

    @Autowired
    public ReservationService(ReservationFlightRepository reservationFlightRepository) {
        this.reservationFlightRepository = reservationFlightRepository;
    }

    // 사용자의 이메일로 예약 정보를 찾는 메서드
    public List<ReservationFlight> findReservationsByUserEmail(String email) {
        return reservationFlightRepository.findByUserId(email);
    }

    // 예약 정보를 저장하는 메서드
    public ReservationFlight saveReservationFlight(ReservationFlight reservationFlight) {
        return reservationFlightRepository.save(reservationFlight);
    }


    public ReservationFlight findById(Long id) {
        Optional<ReservationFlight> reservation = reservationFlightRepository.findById(id);
        return reservation.orElse(null);
    }
}
