package yayeogi.Green3.DTO;

import org.springframework.data.jpa.domain.Specification;
import yayeogi.Green3.entity.Hotel;

import java.util.List;

public class HotelSpecification {

    public static Specification<Hotel> hasAddress(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("address"), address);
        };
    }

    public static Specification<Hotel> hasCheckinDate(String checkin_Date) {

        return (root, query, criteriaBuilder) -> {
            if (checkin_Date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("checkinDate"), checkin_Date);
        };
    }

    public static Specification<Hotel> hasCheckoutDate(String checkout_Date) {
        return (root, query, criteriaBuilder) -> {
            if (checkout_Date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("checkoutDate"), checkout_Date);
        };
    }

    public static Specification<Hotel> hasGuest(Integer guest) {
        return (root, query, criteriaBuilder) -> {
            if (guest == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("reservations").get("guestAdult"), guest);
        };
    }

    public static Specification<Hotel> hasRatings(List<Integer> ratings) {
        return (root, query, criteriaBuilder) -> {
            if (ratings == null || ratings.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("rating").in(ratings);
        };
    }
}
