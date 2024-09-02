package yayeogi.Green3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yayeogi.Green3.repository.SystemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemService {

    @Autowired
    private SystemRepository systemRepository;

    public Map<Integer, Double> getMonthlySales(int year) {
        Map<Integer, Double> monthlySales = new HashMap<>();

        // 호텔 예약 매출
        List<Object[]> hotelSales = systemRepository.findMonthlySalesForHotels(year);
        for (Object[] result : hotelSales) {
            Integer month = (Integer) result[0];
            Double totalSales = (Double) result[1];
            monthlySales.put(month, totalSales);
        }

        return monthlySales;
    }
}
