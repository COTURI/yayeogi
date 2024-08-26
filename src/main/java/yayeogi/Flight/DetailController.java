package yayeogi.Flight;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
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

    @GetMapping("/indexmore")
    public ModelAndView indexmore(
            @RequestParam(required = false) String departureAirport,
            @RequestParam(required = false) String arrivalAirport,
            @RequestParam(required = false) String departureTime,
            @RequestParam(required = false) String arrivalTime,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String carrierCode,
            @RequestParam(required = false) String returnDepartureAirport,
            @RequestParam(required = false) String returnArrivalAirport,
            @RequestParam(required = false) String returnDepartureTime,
            @RequestParam(required = false) String returnArrivalTime,
            @RequestParam(required = false) String returnPrice,
            @RequestParam(required = false) String returnCarrierCode) {

        // Log the received parameters to check their values
        System.out.println("departureAirport: " + (departureAirport != null && !departureAirport.isEmpty() ? departureAirport : "정보 없음"));
        System.out.println("arrivalAirport: " + (arrivalAirport != null && !arrivalAirport.isEmpty() ? arrivalAirport : "정보 없음"));
        System.out.println("departureTime: " + (departureTime != null && !departureTime.isEmpty() ? departureTime : "정보 없음"));
        System.out.println("arrivalTime: " + (arrivalTime != null && !arrivalTime.isEmpty() ? arrivalTime : "정보 없음"));
        System.out.println("price: " + (price != null && !price.isEmpty() ? price : "정보 없음"));
        System.out.println("carrierCode: " + (carrierCode != null && !carrierCode.isEmpty() ? carrierCode : "정보 없음"));
        System.out.println("returnDepartureAirport: " + (returnDepartureAirport != null && !returnDepartureAirport.isEmpty() ? returnDepartureAirport : "정보 없음"));
        System.out.println("returnArrivalAirport: " + (returnArrivalAirport != null && !returnArrivalAirport.isEmpty() ? returnArrivalAirport : "정보 없음"));
        System.out.println("returnDepartureTime: " + (returnDepartureTime != null && !returnDepartureTime.isEmpty() ? returnDepartureTime : "정보 없음"));
        System.out.println("returnArrivalTime: " + (returnArrivalTime != null && !returnArrivalTime.isEmpty() ? returnArrivalTime : "정보 없음"));
        System.out.println("returnPrice: " + (returnPrice != null && !returnPrice.isEmpty() ? returnPrice : "정보 없음"));
        System.out.println("returnCarrierCode: " + (returnCarrierCode != null && !returnCarrierCode.isEmpty() ? returnCarrierCode : "정보 없음"));

        ModelAndView mav = new ModelAndView("indexmore");
        mav.addObject("departureAirport", departureAirport != null && !departureAirport.isEmpty() ? departureAirport : "정보 없음");
        mav.addObject("arrivalAirport", arrivalAirport != null && !arrivalAirport.isEmpty() ? arrivalAirport : "정보 없음");
        mav.addObject("departureTime", departureTime != null && !departureTime.isEmpty() ? departureTime : "정보 없음");
        mav.addObject("arrivalTime", arrivalTime != null && !arrivalTime.isEmpty() ? arrivalTime : "정보 없음");
        mav.addObject("price", price != null && !price.isEmpty() ? price : "정보 없음");
        mav.addObject("carrierCode", carrierCode != null && !carrierCode.isEmpty() ? carrierCode : "정보 없음");
        mav.addObject("returnDepartureAirport", returnDepartureAirport != null && !returnDepartureAirport.isEmpty() ? returnDepartureAirport : "정보 없음");
        mav.addObject("returnArrivalAirport", returnArrivalAirport != null && !returnArrivalAirport.isEmpty() ? returnArrivalAirport : "정보 없음");
        mav.addObject("returnDepartureTime", returnDepartureTime != null && !returnDepartureTime.isEmpty() ? returnDepartureTime : "정보 없음");
        mav.addObject("returnArrivalTime", returnArrivalTime != null && !returnArrivalTime.isEmpty() ? returnArrivalTime : "정보 없음");
        mav.addObject("returnPrice", returnPrice != null && !returnPrice.isEmpty() ? returnPrice : "정보 없음");
        mav.addObject("returnCarrierCode", returnCarrierCode != null && !returnCarrierCode.isEmpty() ? returnCarrierCode : "정보 없음");

        mav.addObject("airports", airports);
        mav.addObject("airlines", airlines);

        return mav;
    }

    @GetMapping("/reservation")
    public ModelAndView reservation(
            @RequestParam(required = false) String departureAirport,
            @RequestParam(required = false) String arrivalAirport,
            @RequestParam(required = false) String departureTime,
            @RequestParam(required = false) String arrivalTime,
            @RequestParam(required = false) String returnDepartureAirport,
            @RequestParam(required = false) String returnArrivalAirport,
            @RequestParam(required = false) String returnDepartureTime,
            @RequestParam(required = false) String returnArrivalTime) {

        ModelAndView mav = new ModelAndView("reservation");
        mav.addObject("departureAirport", departureAirport != null ? departureAirport : "정보 없음");
        mav.addObject("arrivalAirport", arrivalAirport != null ? arrivalAirport : "정보 없음");
        mav.addObject("departureTime", departureTime != null ? departureTime : "정보 없음");
        mav.addObject("arrivalTime", arrivalTime != null ? arrivalTime : "정보 없음");
        mav.addObject("returnDepartureAirport", returnDepartureAirport != null ? returnDepartureAirport : "정보 없음");
        mav.addObject("returnArrivalAirport", returnArrivalAirport != null ? returnArrivalAirport : "정보 없음");
        mav.addObject("returnDepartureTime", returnDepartureTime != null ? returnDepartureTime : "정보 없음");
        mav.addObject("returnArrivalTime", returnArrivalTime != null ? returnArrivalTime : "정보 없음");

        return mav;
    }
}