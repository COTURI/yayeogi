package yayeogi.Green3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yayeogi.Green3.entity.ReservationFlight;

import java.util.List;

@Repository
public interface ReservationFlightRepository extends JpaRepository<ReservationFlight, Long> {
    List<ReservationFlight> findByUserId(String userId);
}