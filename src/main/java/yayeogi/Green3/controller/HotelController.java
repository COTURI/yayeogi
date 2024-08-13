package yayeogi.Green3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import yayeogi.Green3.DTO.HotelDTO;
import yayeogi.Green3.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping("/mains")
    public ResponseEntity<List<HotelDTO>> getHotelsByLocation(@RequestParam Integer location) {
        List<HotelDTO> hotels = hotelService.getHotelsByLocation(location);
        return ResponseEntity.ok(hotels);
    }
    @GetMapping("/country")
    public ResponseEntity<List<HotelDTO>> getHotelsByCountry(@RequestParam Integer country) {
        // 로그를 추가하여 파라미터 값을 확인
        System.out.println("Received country: " + country);

        try {
            List<HotelDTO> hotels = hotelService.getHotelsByCountry(country);
            return ResponseEntity.ok(hotels);
        } catch (Exception e) {
            // 예외 발생 시 500 오류와 함께 예외 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/main")
    public String getHotels(Model model) {
        // 다양한 조건으로 호텔 정보를 가져옴
        List<HotelDTO> hotelsByCountry82 = hotelService.getHotelsByCountry(82);
        List<HotelDTO> hotelsByLocation2 = hotelService.getHotelsByLocation(2);

        // 예를 들어 3개의 카드를 만들고 싶다면, 호텔 리스트를 조정하거나 합치는 로직을 작성할 수 있습니다.
        List<HotelDTO> selectedHotels = hotelsByCountry82; // Country 82에 맞는 호텔 리스트 예시

        // Base64 변환 처리
        selectedHotels.forEach(hotel -> {
            try {
                if (hotel.getHotelMainImg() != null) {
                    InputStream inputStream = hotel.getHotelMainImg().getBinaryStream();
                    byte[] imageBytes = inputStream.readAllBytes();
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    hotel.setHotelMainImgBase64(base64Image); // Base64 문자열을 추가 필드에 저장
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 모델에 추가
        model.addAttribute("hotelsMain", selectedHotels);
        return "hotelsMain"; // Thymeleaf 템플릿 이름
    }


    @GetMapping
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        List<HotelDTO> hotels = hotelService.getAllHotels();
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HotelDTO> createHotel(@RequestBody HotelDTO hotelDTO) throws SQLException {
        HotelDTO createdHotel = hotelService.createHotel(hotelDTO);
        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable("id") Integer id) {
        try {
            HotelDTO hotel = hotelService.getHotelById(id);
            return new ResponseEntity<>(hotel, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDTO> updateHotel(@PathVariable("id") Integer id, @RequestBody HotelDTO hotelDTO) {
        try {
            HotelDTO updatedHotel = hotelService.updateHotel(id, hotelDTO);
            return new ResponseEntity<>(updatedHotel, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable("id") Integer id) {
        try {
            hotelService.deleteHotel(Long.valueOf(id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/deals")
    public String getRandomHotels(Model model) {
        // 전체 호텔 목록에서 무작위로 6개를 가져옴
        List<HotelDTO> selectedHotels = hotelService.getRandomHotels(6);

        model.addAttribute("hotels", selectedHotels);
        return "deals"; // 'deals.html' 템플릿을 반환
    }
}
