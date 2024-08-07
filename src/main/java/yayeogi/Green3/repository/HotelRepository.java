package yayeogi.Green3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yayeogi.Green3.entity.Hotel;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByHotelNameContainingIgnoreCase(String searchTerm);


}
