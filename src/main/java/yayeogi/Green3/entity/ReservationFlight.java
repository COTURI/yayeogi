package yayeogi.Green3.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservation_flight")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_flight_id")
    private Long reservationFlightId; // 고유 ID 필드

    @Column(nullable = false)
    private LocalDate departureDate; // 출국일

    @Column(nullable = false)
    private String departureTime; // 출발시간

    @Column(nullable = false)
    private String arrivalTime; // 도착시간

    @Column(name = "user_id", nullable = false)
    private String userId; // 예약자 ID (User 엔티티의 email과 매핑됨)

    @Column
    private LocalDate returnDate; // 귀국일 (선택)

    @Column
    private String returnDepartureTime; // 귀국일 출발시간 (선택)

    @Column
    private String returnArrivalTime; // 귀국일 도착시간 (선택)

    // 예약자 정보
    @Column(name = "passenger_first_name", nullable = false)
    private String passengerFirstName; // 예약자 영문 이름

    @Column(name = "passenger_last_name", nullable = false)
    private String passengerLastName; // 예약자 영문 성

    @Column(name = "country_code", nullable = false)
    private String countryCode; // 예약자 국가/지역 번호

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber; // 예약자 전화번호

    @Column(name = "email", nullable = false)
    private String email; // 예약자 이메일

    // 탑승객 정보
    @Column(name = "pax_first_name", nullable = false)
    private String paxFirstName; // 탑승객 영문 이름

    @Column(name = "pax_last_name", nullable = false)
    private String paxLastName; // 탑승객 영문 성

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth; // 탑승객 생년월일

    @Column(name = "nationality", nullable = false)
    private String nationality; // 탑승객 국적

    @Column(name = "passport_number", nullable = false)
    private String passportNumber; // 탑승객 여권 번호

    @Column(name = "passport_issue_country", nullable = false)
    private String passportIssueCountry; // 탑승객 여권 발행 국가/지역

    @Column(name = "passport_expiry_date", nullable = false)
    private LocalDate passportExpiryDate; // 탑승객 여권 만료일
}
