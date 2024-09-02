package yayeogi.Green3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yayeogi.Green3.entity.Hotel;
import yayeogi.Green3.entity.HotelReservation;
import yayeogi.Green3.repository.HotelReservationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class HotelReservationService {


    private final HotelReservationRepository hotelReservationRepository;

    @Autowired
    public HotelReservationService(HotelReservationRepository hotelReservationRepository) {
        this.hotelReservationRepository = hotelReservationRepository;
    }

    // 이메일로 예약 조회
    public List<HotelReservation> findHotelReservationsByUserEmail(String email) {
        return hotelReservationRepository.findByEmail(email);
    }

    // 예약 저장
    public HotelReservation saveHotelReservation(HotelReservation hotelReservation) {
        return hotelReservationRepository.save(hotelReservation);
    }

    // 예약 ID로 삭제
    public void cancelHotelReservationById(Integer reservationId) {
        hotelReservationRepository.deleteById(reservationId);
    }

    // 이메일로 예약 삭제
    public void cancelHotelReservationByEmail(String email) {
        hotelReservationRepository.deleteByEmail(email);
    }

    // 예약 ID로 조회
    public HotelReservation findById(Integer reservationId) {
        Optional<HotelReservation> reservation = hotelReservationRepository.findById(reservationId);
        return reservation.orElse(null);
    }

    // 호텔과 날짜 범위로 예약 조회
    public List<HotelReservation> findReservationsByHotelAndDateRange(Hotel hotel, String checkinDate, String checkoutDate) {
        return hotelReservationRepository.findByHotelAndDateRange(hotel, checkinDate, checkoutDate);
    }
}
