package yayeogi.Green3.repository;

import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
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

}

