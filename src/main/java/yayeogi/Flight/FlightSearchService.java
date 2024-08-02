package yayeogi.Flight;

import com.amadeus.Params;
import com.amadeus.Amadeus;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FlightSearchService {

    private final Logger logger = LoggerFactory.getLogger(FlightSearchService.class);
    private final Amadeus amadeus;
    private final Gson gson;

    public FlightSearchService(@Value("${amadeus.api.key}") String apiKey,
                               @Value("${amadeus.api.secret}") String apiSecret) {
        this.amadeus = Amadeus.builder(apiKey, apiSecret).build();
        this.gson = new GsonBuilder().create();
    }

    private String formatDate(String date) {
        // 날짜 형식 변환 로직 추가
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            logger.error("날짜 형식 변환 오류: {}", e.getMessage());
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다.");
        }
    }

    public String searchFlightsAsString(String origin, String destination, String departureDate, String returnDate, int adults) {
        if (origin == null || destination == null || departureDate == null) {
            throw new IllegalArgumentException("Origin, destination, and departureDate cannot be null");
        }

        String formattedDepartureDate = formatDate(departureDate);
        String formattedReturnDate = returnDate != null ? formatDate(returnDate) : null;

        try {
            logger.info("Requesting outbound flights...");
            FlightOfferSearch[] outboundOffers = amadeus.shopping.flightOffersSearch.get(
                    Params.with("originLocationCode", origin)
                            .and("destinationLocationCode", destination)
                            .and("departureDate", formattedDepartureDate)
                            .and("adults", adults)
                            .and("max", 50)
            );

            FlightOfferSearch[] inboundOffers = null;
            if (formattedReturnDate != null) {
                logger.info("Requesting inbound flights...");
                inboundOffers = amadeus.shopping.flightOffersSearch.get(
                        Params.with("originLocationCode", destination)
                                .and("destinationLocationCode", origin)
                                .and("departureDate", formattedReturnDate)
                                .and("adults", adults)
                                .and("max", 50)
                );
            }

            // 결과를 JSON 문자열로 변환
            String outboundJson = gson.toJson(outboundOffers);
            String inboundJson = gson.toJson(inboundOffers != null ? inboundOffers : new FlightOfferSearch[0]);

            // 결과를 JSON 문자열로 조합
            return "{\"outboundDeparture\": " + outboundJson + ", \"inboundReturn\": " + inboundJson + "}";
        } catch (ResponseException e) {
            logger.error("API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("API 호출 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("예기치 않은 오류 발생: {}", e.getMessage());
            throw new RuntimeException("예기치 않은 오류 발생: " + e.getMessage(), e);
        }
    }
}
