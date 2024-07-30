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
            @RequestParam String adults,
            @RequestParam String carrierCode,
            @RequestParam String departureAirport,
            @RequestParam String arrivalAirport,
            @RequestParam String price,
            @RequestParam String currency) {

        ModelAndView mav = new ModelAndView("detail");
        mav.addObject("origin", origin);
        mav.addObject("destination", destination);
        mav.addObject("departureDate", departureDate);
        mav.addObject("adults", adults);
        mav.addObject("carrierCode", carrierCode);
        mav.addObject("departureAirport", departureAirport);
        mav.addObject("arrivalAirport", arrivalAirport);
        mav.addObject("price", price);
        mav.addObject("currency", currency);

        return mav;
    }

    @GetMapping("/reservation")
    public ModelAndView reservation(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam String adults,
            @RequestParam String carrierCode,
            @RequestParam String price,
            @RequestParam String currency) {

        ModelAndView mav = new ModelAndView("reservation");
        mav.addObject("origin", origin);
        mav.addObject("destination", destination);
        mav.addObject("departureDate", departureDate);
        mav.addObject("adults", adults);
        mav.addObject("carrierCode", carrierCode);
        mav.addObject("price", price);
        mav.addObject("currency", currency);

        return mav;
    }
}
