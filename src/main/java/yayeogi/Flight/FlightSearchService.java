package yayeogi.Flight;

import com.amadeus.Params;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amadeus.Amadeus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.amadeus.resources.FlightOfferSearch;

@Service
public class FlightSearchService {

    private final Amadeus amadeus;
    private final Gson gson;

    public FlightSearchService(@Value("${amadeus.api.key}") String apiKey,
                               @Value("${amadeus.api.secret}") String apiSecret) {
        this.amadeus = Amadeus.builder(apiKey, apiSecret).build();
        this.gson = new GsonBuilder().create();
    }

    private String formatDate(String date) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        // 날짜 형식 변환이 필요한 경우, 여기에 로직 추가
        return date;
    }

    public String searchFlightsAsString(String origin, String destination, String departureDate, String returnDate, int adults) {
        if (origin == null || destination == null || departureDate == null) {
            throw new IllegalArgumentException("Origin, destination, and departureDate cannot be null");
        }

        String formattedDepartureDate = formatDate(departureDate);
        String formattedReturnDate = returnDate != null ? formatDate(returnDate) : null;

        try {
            System.out.println("Requesting outbound flights...");
            FlightOfferSearch[] outboundOffers = amadeus.shopping.flightOffersSearch.get(
                    Params.with("originLocationCode", origin)
                            .and("destinationLocationCode", destination)
                            .and("departureDate", formattedDepartureDate)
                            .and("adults", adults)
                            .and("max", 50)
            );

            FlightOfferSearch[] inboundOffers = null;
            if (formattedReturnDate != null) {
                System.out.println("Requesting inbound flights...");
                inboundOffers = amadeus.shopping.flightOffersSearch.get(
                        Params.with("originLocationCode", destination)
                                .and("destinationLocationCode", origin)
                                .and("departureDate", formattedReturnDate)
                                .and("adults", adults)
                                .and("max", 50)
                );
            }

            // JSON 응답 형식 조정
            String outboundJson = gson.toJson(outboundOffers);
            String inboundJson = gson.toJson(inboundOffers != null ? inboundOffers : new FlightOfferSearch[0]);

            return "{\"outboundDeparture\": " + outboundJson + ", \"inboundReturn\": " + inboundJson + "}";
        } catch (Exception e) {
            e.printStackTrace();  // 상세한 에러 로그 출력
            throw new RuntimeException("API 호출 실패: " + e.getMessage(), e);
        }
    }
}
