package yayeogi.Green3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yayeogi.Green3.entity.Hotel;
import yayeogi.Green3.entity.HotelReservation;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelReservationRepository extends JpaRepository<HotelReservation, Integer> {

    // 이메일로 예약 조회
    List<HotelReservation> findByEmail(String email);

    // 호텔과 날짜 범위로 예약 조회
    @Query("SELECT r FROM HotelReservation r WHERE r.hotel = :hotel AND " +
            "((r.checkinDate <= :checkoutDate) AND (r.checkoutDate >= :checkinDate))")
    List<HotelReservation> findByHotelAndDateRange(
            @Param("hotel") Hotel hotel,
            @Param("checkinDate") String checkinDate,
            @Param("checkoutDate") String checkoutDate
    );

    // 이메일로 예약 삭제
    void deleteByEmail(String email);

    // 예약 ID로 삭제
    void deleteById(Integer id);
}
