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
    private LocalDate checkinDate;

    @Column(name = "checkoutdate", nullable = false)
    private LocalDate checkoutDate;

    @Column(name = "guest_adult", columnDefinition = "INT(2) DEFAULT 2")
    private Integer guestAdult;

    @Column(name = "guest_kid", columnDefinition = "INT(2) DEFAULT 0")
    private Integer guestKid;

    @Column(name = "rooms", columnDefinition = "INT(2) DEFAULT 1")
    private Integer rooms;

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private User user;
}
