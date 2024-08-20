package yayeogi.Green3.repository;

import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yayeogi.Green3.DTO.HotelDTO;
import yayeogi.Green3.entity.Hotel;
import yayeogi.Green3.entity.HotelReview;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByCountryAndLocation(Integer country, Integer location);
    List<Hotel> findByCountry(Integer country);
    List<Hotel> findByLocation(Integer location);
    List<Hotel> findByHotelId(Integer hotelId);
    @Query("SELECT h FROM Hotel h WHERE LOWER(h.address) LIKE LOWER(CONCAT('%', :address, '%'))")
    List<Hotel> findByAddressContainingIgnoreCase(@Param("address") String address);

  /*  @Query("SELECT h FROM Hotel h WHERE " +
            "(:address IS NULL OR h.address LIKE %:address%) AND " +
            "(:checkin_date IS NULL OR h.checkin_date >= :checkin_date) AND " +
            "(:checkout_date IS NULL OR h.checkout_date <= :checkout_date) AND " +
            "(:guests IS NULL OR h.guests = :guests)")
    List<Hotel> searchHotels(@Param("address") String address,
                             @Param("checkin_date") String checkin_date,
                             @Param("checkout_date") String checkout_date,
                             @Param("guests") String guests);*/
}

