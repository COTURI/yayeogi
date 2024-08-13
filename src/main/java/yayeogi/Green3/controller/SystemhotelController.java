package yayeogi.Green3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yayeogi.Green3.service.SystemService;

import java.time.LocalDate;

@RestController
public class SystemhotelController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/api/hotel-statistics")
    public Integer getHotelStatistics(@RequestParam("checkinDate") String checkinDate) {
        LocalDate date = LocalDate.parse(checkinDate);
        return systemService.getAveragePriceByCheckinDate(date);
    }

}
