package yayeogi.Flight;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Controller
public class DetailController {

    private Map<String, String> airports;
    private Map<String, String> airlines;

    public DetailController() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Load airports data
        Resource airportsResource = new ClassPathResource("static/json/airports.json");
        airports = mapper.readValue(airportsResource.getFile(), Map.class);

        // Load airlines data
        Resource airlinesResource = new ClassPathResource("static/json/airlines.json");
        airlines = mapper.readValue(airlinesResource.getFile(), Map.class);
    }

    @GetMapping("/detail")
    public ModelAndView detail(
            @RequestParam String flightId,
            @RequestParam String departureAirport,
            @RequestParam String arrivalAirport,
            @RequestParam(required = false) String departureTime,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String carrierCode,
            @RequestParam(required = false) String returnCarrierCode,
            @RequestParam(required = false) String returnPrice,
            @RequestParam(required = false) String returnDate) {

        ModelAndView mav = new ModelAndView("detail");
        mav.addObject("flightId", flightId);
        mav.addObject("departureAirport", departureAirport);
        mav.addObject("arrivalAirport", arrivalAirport);
        mav.addObject("departureTime", departureTime);
        mav.addObject("price", price);
        mav.addObject("currency", currency);
        mav.addObject("carrierCode", carrierCode);
        mav.addObject("returnCarrierCode", returnCarrierCode);
        mav.addObject("returnPrice", returnPrice);
        mav.addObject("returnDate", returnDate);

        mav.addObject("airports", airports);
        mav.addObject("airlines", airlines);

        return mav;
    }

    @GetMapping("/reservation")
    public ModelAndView reservation(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String departureDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(required = false) String adults,
            @RequestParam(required = false) String carrierCode,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String returnCarrierCode,
            @RequestParam(required = false) String returnPrice) {

        ModelAndView mav = new ModelAndView("reservation");
        mav.addObject("origin", origin);
        mav.addObject("destination", destination);
        mav.addObject("departureDate", departureDate);
        mav.addObject("returnDate", returnDate);
        mav.addObject("adults", adults);
        mav.addObject("carrierCode", carrierCode);
        mav.addObject("price", price);
        mav.addObject("currency", currency);
        mav.addObject("returnCarrierCode", returnCarrierCode);
        mav.addObject("returnPrice", returnPrice);

        mav.addObject("airports", airports);
        mav.addObject("airlines", airlines);

        return mav;
    }
}
