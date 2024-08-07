package yayeogi.Green3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yayeogi.Green3.entity.Hotel;
import yayeogi.Green3.repository.HotelRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public void saveHotel() {
        try {
            Hotel hotel = Hotel.builder()
                    .hotelName("오리엔스호텔앤레지던스")
                    .hotelDetail("오리엔스 호텔 & 레지던스 명동은 충무로역(지하철 3호선 및 4호선) 4번 출구에서 도보로 3분 거리에 위치하여 편리한 입지를 자랑하고, 취사가 가능한 우아한 객실, 피트니스 센터, 비즈니스 센터를 보유하고 있습니다.\n" +
                            "\n" +
                            "리셉션 데스크는 환전 서비스를 제공합니다. 각 객실에 간이 주방, 소파, 평면 케이블 TV가 있습니다. 전용 욕실에는 욕조, 샤워 시설, 무료 세면도구가 구비되어 있습니다.,;;")
                    .checkinTime(1500)
                    .checkoutTime(1200)
                    .country(82)
                    .location(2)
                    .price(102400)
                    .hotelMainImg(loadImage("C:/Users/Manic-063/Desktop/t/third/img/oriens1.png"))
                    .hotelImg1(loadImage("C:/Users/Manic-063/Desktop/t/third/img/oriens2.png"))
                    .hotelImg2(loadImage("C:/Users/Manic-063/Desktop/t/third/img/oriens3.png"))
                    .hotelImg3(loadImage("C:/Users/Manic-063/Desktop/t/third/img/oriens4.png"))
                    .hotelImg4(loadImage("C:/Users/Manic-063/Desktop/t/third/img/oriens5.png"))
                    .hotelImg5(loadImage("C:/Users/Manic-063/Desktop/t/third/img/oriens6.png"))
                    .address("삼일대로2길 50, 중구, 서울, 04627, 대한민국")
                    .build();

            hotelRepository.save(hotel);
        } catch (IOException e) {
            e.printStackTrace(); // 개발 중에는 표준 오류 스트림으로 출력
        }
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll(); // 모든 호텔을 반환
    }
    public Hotel getHotelById(int id) {
        Optional<Hotel> hotel = hotelRepository.findById((long) id);
        return hotel.orElse(null); // 또는 Optional 처리 방식에 맞게 처리
    }


    private byte[] loadImage(String filePath) throws IOException {
        File file = new File(filePath);
        return Files.readAllBytes(file.toPath()); // 이미지 파일을 바이트 배열로 변환
    }
}
