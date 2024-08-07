package yayeogi.Green3.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Hotelreviews")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "reviews", length = 150, nullable = false)
    private String reviewText;

    @Column(name = "use_check", columnDefinition = "TINYINT DEFAULT 0")
    private Integer useCheck;

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private User user;
}