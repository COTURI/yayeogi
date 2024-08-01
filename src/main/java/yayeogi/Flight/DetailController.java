package yayeogi.Flight;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DetailController {

    @GetMapping("/detail")
    public ModelAndView detail(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam String returnDate,
            @RequestParam String adults,
            @RequestParam String carrierCode,
            @RequestParam String price,
            @RequestParam String currency,
            @RequestParam String returnCarrierCode, // 돌아오는 항공편의 항공사 코드
            @RequestParam String returnPrice) {     // 돌아오는 항공편 가격

        ModelAndView mav = new ModelAndView("detail");
        mav.addObject("origin", origin);
        mav.addObject("destination", destination);
        mav.addObject("departureDate", departureDate);
        mav.addObject("returnDate", returnDate);
        mav.addObject("adults", adults);
        mav.addObject("carrierCode", carrierCode);
        mav.addObject("price", price);
        mav.addObject("currency", currency);
        mav.addObject("returnCarrierCode", returnCarrierCode); // 돌아오는 항공편 항공사 코드
        mav.addObject("returnPrice", returnPrice);             // 돌아오는 항공편 가격

        return mav;
    }

    @GetMapping("/reservation")
    public ModelAndView reservation(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam String returnDate,
            @RequestParam String adults,
            @RequestParam String carrierCode,
            @RequestParam String price,
            @RequestParam String currency,
            @RequestParam String returnCarrierCode, // 돌아오는 항공편의 항공사 코드
            @RequestParam String returnPrice) {     // 돌아오는 항공편 가격

        ModelAndView mav = new ModelAndView("reservation");
        mav.addObject("origin", origin);
        mav.addObject("destination", destination);
        mav.addObject("departureDate", departureDate);
        mav.addObject("returnDate", returnDate);
        mav.addObject("adults", adults);
        mav.addObject("carrierCode", carrierCode);
        mav.addObject("price", price);
        mav.addObject("currency", currency);
        mav.addObject("returnCarrierCode", returnCarrierCode); // 돌아오는 항공편 항공사 코드
        mav.addObject("returnPrice", returnPrice);             // 돌아오는 항공편 가격

        return mav;
    }
}
