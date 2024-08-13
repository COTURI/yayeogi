package yayeogi.Green3.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String departureDate; // 출국일

    @Column(nullable = false)
    private String departureTime; // 출발시간

    @Column(nullable = false)
    private String arrivalTime; // 도착시간

    @Column(name = "user_id", nullable = false)
    private String userId; // 예약자 ID (User 엔티티의 email과 매핑됨)

    @Column
    private String returnDate; // 귀국일 (선택)

    @Column
    private String returnDepartureTime; // 귀국일 출발시간 (선택)

    @Column
    private String returnArrivalTime; // 귀국일 도착시간 (선택)

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, referencedColumnName = "email")
    private User user; // 예약자와의 연관관계 설정
}