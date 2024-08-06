package yayeogi.Green3.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "hotel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Hotel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Integer hotelId;

    @Column(name = "hotel_name", length = 30, nullable = false)
    private String hotelName;

    @Column(name = "hotel_detail", length = 1000, nullable = false)
    private String hotelDetail;

    @Lob
    @Column(name = "hotel_mainimg", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] hotelMainImg;

    @Lob
    @Column(name = "hotel_img1", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] hotelImg1;

    @Lob
    @Column(name = "hotel_img2", columnDefinition = "LONGBLOB")
    private byte[] hotelImg2;

    @Lob
    @Column(name = "hotel_img3", columnDefinition = "LONGBLOB")
    private byte[] hotelImg3;

    @Lob
    @Column(name = "hotel_img4", columnDefinition = "LONGBLOB")
    private byte[] hotelImg4;

    @Lob
    @Column(name = "hotel_img5", columnDefinition = "LONGBLOB")
    private byte[] hotelImg5;

    @Column(name = "checkin_state", columnDefinition = "TINYINT DEFAULT 0")
    private Integer checkinState;

    @Column(name = "checkout_state", columnDefinition = "TINYINT DEFAULT 1")
    private Integer checkoutState;

    @Column(name = "checkin_time", nullable = false)
    private Integer checkinTime;

    @Column(name = "checkout_time", nullable = false)
    private Integer checkoutTime;

    @Column(name = "country", nullable = false)
    private Integer country;

    @Column(name = "location", nullable = false)
    private Integer location;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "checkin_date")
    private LocalDate checkinDate;

    @Column(name = "checkout_date")
    private LocalDate checkoutDate;

    // 주소 필드 추가
    @Column(name = "address", length = 255, nullable = false)
    private String address;
}
