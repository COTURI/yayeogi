package yayeogi.Green3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yayeogi.Green3.entity.HotelReview;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface HotelReviewRepository extends JpaRepository<HotelReview, Long> {
     List<HotelReview> findByHotelId(Long hotelId);

}

