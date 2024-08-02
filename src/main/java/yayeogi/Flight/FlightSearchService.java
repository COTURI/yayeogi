package yayeogi.Flight;

import com.amadeus.Params;
import com.amadeus.Amadeus;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
        return date; // 실제로 날짜 형식 변환이 필요하다면 여기에 로직 추가
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

            String outboundJson = gson.toJson(outboundOffers);
            String inboundJson = gson.toJson(inboundOffers != null ? inboundOffers : new FlightOfferSearch[0]);

            return "{\"outboundDeparture\": " + outboundJson + ", \"inboundReturn\": " + inboundJson + "}";
        } catch (ResponseException e) {
            System.err.println("API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("API 호출 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("예기치 않은 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("예기치 않은 오류 발생: " + e.getMessage(), e);
        }
    }
}
