package yayeogi.Green3.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class hotels_detail {
    private int id;
    private Long image;
    private String hotel_name;
    private double star_rating;
    private double rating;
    private String description;
    private int checkin_status;
    private int checkout_status;
    private String checkin_date;
    private String checkout_date;
    private String address;


}
