package yayeogi.Green3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yayeogi.Green3.entity.HotelReservation;

import java.time.LocalDate;

@Repository
public interface SystemRepository extends JpaRepository<HotelReservation, Integer> {

    @Query("SELECT AVG(CAST(h.hotel.price AS double)) FROM HotelReservation h WHERE h.checkinDate = :checkinDate")
    Double findAveragePriceByCheckinDate(@Param("checkinDate") LocalDate checkinDate);

}
