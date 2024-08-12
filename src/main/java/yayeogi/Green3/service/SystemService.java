package yayeogi.Green3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yayeogi.Green3.repository.SystemRepository;

import java.time.LocalDate;

@Service
public class SystemService {

    @Autowired
    private SystemRepository systemRepository;

    public Double getAveragePriceByCheckinDate(LocalDate checkinDate) {
        return systemRepository.findAveragePriceByCheckinDate(checkinDate);
    }

    // 필요한 경우 다른 통계 계산 로직 추가
}
