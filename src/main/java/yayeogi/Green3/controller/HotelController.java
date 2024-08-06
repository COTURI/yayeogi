package yayeogi.Green3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yayeogi.Green3.entity.Hotel;
import yayeogi.Green3.service.HotelService;

@Controller
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping("/hotel")
    public String getHotelById(@RequestParam("id") int id, Model model) {
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel != null) {
            model.addAttribute("hotel", hotel);
            return "hotelDetail";
        }
        return "error"; // 또는 404 페이지로 이동
    }
}
