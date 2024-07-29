package yayeogi.Flight;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlightSearchService {

    private final Amadeus amadeus;
    private final Gson gson;

    public FlightSearchService(@Value("${amadeus.api.key}") String apiKey,
                               @Value("${amadeus.api.secret}") String apiSecret) {
        this.amadeus = Amadeus.builder(apiKey, apiSecret).build();
        this.gson = new GsonBuilder().create(); // Gson 객체 초기화
    }

    public String searchFlightsAsString(String origin, String destination, String departureDate, int adults) {
        try {
            // FlightOfferSearch API 호출
            FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(
                    Params.with("originLocationCode", origin)
                            .and("destinationLocationCode", destination)
                            .and("departureDate", departureDate)
                            .and("adults", adults)
                            .and("max", 10) // 최대 5개의 결과
            );

            // Gson을 사용하여 JSON 문자열로 변환
            String json = gson.toJson(flightOffers);

            // JSON 문자열을 Java 객체로 변환
            Type flightOfferType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> flightOfferList = gson.fromJson(json, flightOfferType);

            // 중복된 항공편 제거
            Set<String> seenIds = new HashSet<>();
            List<Map<String, Object>> uniqueFlights = flightOfferList.stream()
                    .filter(flight -> {
                        String id = (String) flight.get("id");
                        return seenIds.add(id); // 중복된 id가 없는 경우에만 추가
                    })
                    .collect(Collectors.toList());

            // 중복 제거된 데이터를 JSON 문자열로 변환
            return gson.toJson(uniqueFlights);

        } catch (ResponseException e) {
            e.printStackTrace();
            return "{}"; // 빈 JSON 객체 반환
        }
    }
}
