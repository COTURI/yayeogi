package yayeogi.Green3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yayeogi.Green3.entity.ReservationFlight;

import java.util.List;

public interface ReservationFlightRepository extends JpaRepository<ReservationFlight, Long> {
    List<ReservationFlight> findByUserId(String userId);
}