package yayeogi.Green3.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelReviewsDTO {
    private Integer review_id;  // Integer로 유지
    private String email;
    private Integer rating;
    private String reviews;
    private Integer use_check;
    private Integer hotel_id;
}
