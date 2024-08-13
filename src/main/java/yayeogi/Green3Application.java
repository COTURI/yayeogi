package yayeogi;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import yayeogi.Green3.service.HotelService;

@SpringBootApplication
public class Green3Application implements CommandLineRunner {

	@Autowired
	private HotelService hotelService;

	public static void main(String[] args) {
		SpringApplication.run(Green3Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// 애플리케이션 시작 시 saveHotel() 메서드를 호출하여 데이터베이스에 호텔 정보를 저장
		/*hotelService.saveHotel();*/

	}
}
