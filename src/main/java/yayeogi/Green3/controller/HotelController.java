package yayeogi.Green3.controller;

import yayeogi.Green3.entity.Hotel;
import yayeogi.Green3.entity.User;
import yayeogi.Green3.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/hotel")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }


    // 호텔 특가 페이지 보기
    @GetMapping("/deals")
    public String showDealsPage(Model model) {

        return "deals"; // 'deals.html' 템플릿을 반환
    }

    // 호텔 상세 정보 보기
    @GetMapping("/detail")
    public String getHotelById(@RequestParam("id") int id, Model model) {
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel != null) {
            model.addAttribute("hotel", hotel);
            return "detail"; // 'detail.html' 템플릿을 반환
        }
        return "error"; // 404 또는 에러 페이지
    }

    // 호텔 등록 페이지


    // 호텔 등록 처리


}
