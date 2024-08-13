package yayeogi.Green3.service;

import yayeogi.Green3.DTO.HotelDTO;
import yayeogi.Green3.DTO.HotelReviewsDTO;
import yayeogi.Green3.entity.Hotel;
import yayeogi.Green3.entity.HotelReview;
import yayeogi.Green3.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yayeogi.Green3.repository.HotelReviewRepository;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelReviewRepository hotelReviewRepository;


    // Country와 Location에 따라 호텔을 검색
    public List<HotelDTO> getHotelsByCountryAndLocation(Integer country, Integer location) {
        List<Hotel> hotels = hotelRepository.findByCountryAndLocation(country, location);
        return hotels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<HotelDTO> getHotelsDetail(Integer hotelId) {
        List<Hotel> hotels = hotelRepository.findByHotelId(hotelId);
        return hotels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }

    // 모든 호텔 정보 가져오기
    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 국가의 호텔 정보 가져오기
    public List<HotelDTO> getHotelsByCountry(Integer country) {
        return hotelRepository.findByCountry(country)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 위치의 호텔 정보 가져오기
    public List<HotelDTO> getHotelsByLocation(Integer location) {
        try {
            return hotelRepository.findByLocation(location)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();  // 로그에 출력
            throw new RuntimeException("Error fetching hotels by location", e);
        }
    }

    // 호텔 정보 생성
    public HotelDTO createHotel(HotelDTO hotelDTO) throws SQLException {
        Hotel hotel = convertToEntity(hotelDTO);
        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToDTO(savedHotel);
    }

    // 호텔 ID로 호텔 정보 가져오기
    public HotelDTO getHotelById(Integer id) {
        Hotel hotel = hotelRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        return convertToDTO(hotel);
    }

    // 호텔 정보 업데이트
    @Transactional
    public HotelDTO updateHotel(Integer id, HotelDTO hotelDTO) {
        Hotel hotel = hotelRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));

        // DTO의 데이터를 Entity에 업데이트
        hotel.setCheckinTime(hotelDTO.getCheckinTime());
        hotel.setCheckoutTime(hotelDTO.getCheckoutTime());
        hotel.setCountry(hotelDTO.getCountry());
        hotel.setHotelDetail(hotelDTO.getHotelDetail());
        hotel.setHotelMainImg(hotelDTO.getHotelMainImg());
        hotel.setHotelImg1(hotelDTO.getHotelImg1());
        hotel.setHotelImg2(hotelDTO.getHotelImg2());
        hotel.setHotelImg3(hotelDTO.getHotelImg3());
        hotel.setHotelImg4(hotelDTO.getHotelImg4());
        hotel.setHotelImg5(hotelDTO.getHotelImg5());
        hotel.setHotelName(hotelDTO.getHotelName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setPrice(hotelDTO.getPrice());
        hotel.setAddress(hotelDTO.getAddress());

        Hotel updatedHotel = hotelRepository.save(hotel);
        return convertToDTO(updatedHotel);
    }

    // 호텔 정보 삭제
    @Transactional
    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Hotel not found with id: " + id);
        }
        hotelRepository.deleteById(id);
    }

    // 호텔 이미지를 파일로 저장
    public void saveHotelImages(Long hotelId, String basePath) throws SQLException, IOException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        // Blob 배열로 이미지 저장
        Blob[] imageBlobs = {
                hotel.getHotelMainImg(),
                hotel.getHotelImg1(),
                hotel.getHotelImg2(),
                hotel.getHotelImg3(),
                hotel.getHotelImg4(),
                hotel.getHotelImg5()
        };

        for (int i = 0; i < imageBlobs.length; i++) {
            Blob imageBlob = imageBlobs[i];
            if (imageBlob != null) {
                String filePath = basePath + "/hotel_" + hotelId + "_img" + (i + 1) + ".jpg";
                saveBlobToFile(imageBlob, filePath);
            }
        }
    }

    // Blob을 파일로 저장하는 메서드
    private void saveBlobToFile(Blob blob, String filePath) throws SQLException, IOException {
        try (InputStream inputStream = blob.getBinaryStream();
             FileOutputStream outputStream = new FileOutputStream(filePath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setHotelId(hotel.getHotelId());
        dto.setHotelName(hotel.getHotelName());
        dto.setAddress(hotel.getAddress());
        dto.setPrice(hotel.getPrice());
        dto.setCheckinTime(hotel.getCheckinTime());
        dto.setCheckoutTime(hotel.getCheckoutTime());
        dto.setCountry(hotel.getCountry());
        dto.setLocation(hotel.getLocation());
        dto.setHotelDetail(hotel.getHotelDetail());
        dto.setHotelMainImgBase64(convertImageToBase64(hotel.getHotelMainImg()));
        dto.setHotelImg1Base64(convertImageToBase64(hotel.getHotelImg1()));
        dto.setHotelImg2Base64(convertImageToBase64(hotel.getHotelImg2()));
        dto.setHotelImg3Base64(convertImageToBase64(hotel.getHotelImg3()));
        dto.setHotelImg4Base64(convertImageToBase64(hotel.getHotelImg4()));
        dto.setHotelImg5Base64(convertImageToBase64(hotel.getHotelImg5()));

        // 나머지 필드 설정
        return dto;
    }


    private Hotel convertToEntity(HotelDTO hotelDTO) throws SQLException {
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelDTO.getHotelId());  // 필드 이름과 타입에 맞게 설정
        hotel.setHotelName(hotelDTO.getHotelName());
        hotel.setAddress(hotelDTO.getAddress());
        hotel.setPrice(hotelDTO.getPrice());
        // Blob 타입이 아닌 Base64 문자열을 Blob으로 변환하는 로직 추가 필요
        hotel.setHotelMainImg(convertBase64ToBlob(hotelDTO.getHotelMainImgBase64()));
        hotel.setHotelDetail(hotelDTO.getHotelDetail());
        hotel.setCheckinTime(hotelDTO.getCheckinTime());
        hotel.setCheckoutTime(hotelDTO.getCheckoutTime());
        hotel.setCountry(hotelDTO.getCountry());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setHotelImg1(hotelDTO.getHotelImg1());
        hotel.setHotelImg2(hotelDTO.getHotelImg2());
        hotel.setHotelImg3(hotelDTO.getHotelImg3());
        hotel.setHotelImg4(hotelDTO.getHotelImg4());
        hotel.setHotelImg5(hotelDTO.getHotelImg5());
        return hotel;
    }

    private String convertImageToBase64(Blob blob) {
        try {
            if (blob != null) {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // 예외 처리
        }
        return null;
    }

    private Blob convertBase64ToBlob(String base64) throws SerialException, SQLException {
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        return new SerialBlob(imageBytes);
    }


    // Blob을 Base64로 변환하는 메서드
    private String convertBlobToBase64(Blob blob) {
        try (InputStream inputStream = blob.getBinaryStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<HotelDTO> getRandomHotels(Integer count) {
        List<HotelDTO> allHotels = hotelRepository.findAll().stream()
                .map(hotel -> {
                    try {
                        // Hotel 엔티티를 HotelDTO로 변환
                        HotelDTO hotelDTO = new HotelDTO();
                        hotelDTO.setHotelId(hotel.getHotelId());
                        hotelDTO.setHotelName(hotel.getHotelName());
                        hotelDTO.setHotelDetail(hotel.getHotelDetail());
                        hotelDTO.setHotelMainImg(hotel.getHotelMainImg());
                        hotelDTO.setHotelImg1(hotel.getHotelImg1());
                        hotelDTO.setHotelImg2(hotel.getHotelImg2());
                        hotelDTO.setHotelImg3(hotel.getHotelImg3());
                        hotelDTO.setHotelImg4(hotel.getHotelImg4());
                        hotelDTO.setHotelImg5(hotel.getHotelImg5());
                        hotelDTO.setCheckinState(hotel.getCheckinState());
                        hotelDTO.setCheckoutState(hotel.getCheckoutState());
                        hotelDTO.setCheckinTime(hotel.getCheckinTime());
                        hotelDTO.setCheckoutTime(hotel.getCheckoutTime());
                        hotelDTO.setCountry(hotel.getCountry());
                        hotelDTO.setLocation(hotel.getLocation());
                        hotelDTO.setPrice(hotel.getPrice());
                        hotelDTO.setAddress(hotel.getAddress());

                        // Base64 변환 처리
                        if (hotel.getHotelMainImg() != null) {
                            Blob blob = hotel.getHotelMainImg();
                            InputStream inputStream = blob.getBinaryStream();
                            byte[] imageBytes = inputStream.readAllBytes();
                            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                            hotelDTO.setHotelMainImgBase64(base64Image);
                        }
                        return hotelDTO;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());

        // 무작위로 섞기
        Collections.shuffle(allHotels);

        // 최대 count 개수만큼 선택
        return allHotels.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelReviewsDTO> getHotelReviews(Integer hotelId) {
        // 호텔 ID로 호텔 리뷰 목록을 가져옴
        List<HotelReview> hotelReviews = hotelReviewRepository.findByHotelId(Long.valueOf(hotelId));

        // 가져온 리뷰 목록을 DTO로 변환하여 반환
        return hotelReviews.stream()
                .map(this::convertToHotelReviewsDTO)
                .collect(Collectors.toList());
    }

    private HotelReviewsDTO convertToHotelReviewsDTO(HotelReview hotelReview) {
        HotelReviewsDTO dto = new HotelReviewsDTO();
        dto.setReview_id(hotelReview.getReviewId());
        dto.setEmail(hotelReview.getEmail());
        dto.setRating(hotelReview.getRating());
        dto.setReviews(hotelReview.getReviewText());
        dto.setUse_check(hotelReview.getUseCheck());
        dto.setHotel_id(hotelReview.getHotel().getHotelId());

        return dto;
    }

    public double getAverageRating(Long hotelId) {
        List<HotelReview> reviews = hotelReviewRepository.findByHotelId(hotelId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (HotelReview review : reviews) {
            sum += review.getRating();
        }

        return sum / reviews.size();
    }

    public List<Hotel> searchHotelsByCityOrName(String query) {
        // query를 이용하여 DB에서 검색합니다.
        return hotelRepository.findByCityOrNameContaining(query);
    }
}

