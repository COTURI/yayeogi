package yayeogi.Green3.DTO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;

@Getter
@Setter
public class HotelDTO {
    private Integer hotelId;  // Integer로 유지
    private String checkinTime;
    private String checkoutTime;
    private Integer country;
    private String hotelDetail;
    private Blob hotelMainImg;
    private Blob hotelImg1;
    private Blob hotelImg2;
    private Blob hotelImg3;
    private Blob hotelImg4;
    private Blob hotelImg5;
    private String hotelName;
    private Integer location;
    private String price;
    private String address;
    private Integer checkinState;
    private Integer checkoutState;
    private String hotelMainImgBase64;
    private String hotelImg1Base64;
    private String hotelImg2Base64;
    private String hotelImg3Base64;
    private String hotelImg4Base64;
    private String hotelImg5Base64;
    private Integer clas;



    // Getters and Setters (Lombok으로 자동 생성)
}
