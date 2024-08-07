package yayeogi.Green3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yayeogi.Green3.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    // 추가적인 쿼리 메소드 정의 가능
}
