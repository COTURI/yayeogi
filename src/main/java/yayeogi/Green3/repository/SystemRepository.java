package yayeogi.Green3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yayeogi.Green3.entity.HotelReservation;

import java.util.List;

@Repository
public interface SystemRepository extends JpaRepository<HotelReservation, Integer> {

    @Query("SELECT MONTH(hr.checkinDate), SUM(h.price * hr.rooms) FROM HotelReservation hr JOIN hr.hotel h " +
            "WHERE YEAR(hr.checkinDate) = :year " +
            "GROUP BY MONTH(hr.checkinDate)")
    List<Object[]> findMonthlySalesForHotels(int year);
}
