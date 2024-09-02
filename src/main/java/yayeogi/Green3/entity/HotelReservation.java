package yayeogi.Green3.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "hotel_reservation")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "checkindate", nullable = false)
    private String checkinDate;

    @Column(name = "checkoutdate", nullable = false)
    private String checkoutDate;

    @Column(name = "guest_adult", columnDefinition = "INT(2) DEFAULT 2")
    private Integer guestAdult;



    @Column(name = "rooms", columnDefinition = "INT(2) DEFAULT 1")
    private Integer rooms;

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id") // 호텔 엔티티와 연결할 외래 키 컬럼명
    private Hotel hotel; // Hotel 엔티티를 직접 참조


}
