package yayeogi.Green3.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;

@Entity
@Table(name = "hotels")
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
    @Column(name = "hotel_mainimg", columnDefinition = "LONGBLOB")
    private Blob hotelMainImg;

    @Lob
    @Column(name = "hotel_img1", columnDefinition = "LONGBLOB")
    private Blob hotelImg1;

    @Lob
    @Column(name = "hotel_img2", columnDefinition = "LONGBLOB")
    private Blob hotelImg2;

    @Lob
    @Column(name = "hotel_img3", columnDefinition = "LONGBLOB")
    private Blob hotelImg3;

    @Lob
    @Column(name = "hotel_img4", columnDefinition = "LONGBLOB")
    private Blob hotelImg4;

    @Lob
    @Column(name = "hotel_img5", columnDefinition = "LONGBLOB")
    private Blob hotelImg5;

    @Column(name = "checkin_state",     columnDefinition = "TINYINT DEFAULT 0")
    private Integer checkinState;

    @Column(name = "checkout_state", columnDefinition = "TINYINT DEFAULT 1")
    private Integer checkoutState;

    @Column(name = "checkin_time", nullable = false)
    private String checkinTime;

    @Column(name = "checkout_time", nullable = false)
    private String checkoutTime;

    @Column(name = "country", nullable = false)
    private Integer country;

    @Column(name = "location", nullable = false)
    private Integer location;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "checkin_date")
    private String checkinDate;

    @Column(name = "checkout_date")
    private String checkoutDate;

    @Column(name = "address", length = 255, nullable = false)
    private String address;
}

