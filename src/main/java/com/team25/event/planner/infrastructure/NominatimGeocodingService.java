package com.team25.event.planner.infrastructure;

import com.team25.event.planner.common.dto.LatLongDTO;
import com.team25.event.planner.common.dto.LocationRequestDTO;
import com.team25.event.planner.common.exception.ServerError;
import com.team25.event.planner.common.service.GeocodingService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class NominatimGeocodingService implements GeocodingService {
    private static final Logger logger = LoggerFactory.getLogger(NominatimGeocodingService.class);

    private final WebClient webClient;

    @Autowired
    public NominatimGeocodingService(@Value("${nominatim.base-url}") String nominatimSearchUrl) {
        webClient = WebClient.create(nominatimSearchUrl);
    }

    @Override
    public LatLongDTO getLatLong(LocationRequestDTO location) throws ServerError {
        try {
            final List<LatLonResponse> locationCandidates = webClient.get()
                    .uri(
                            uriBuilder -> uriBuilder
                                    .path("search")
                                    .queryParam("format", "json")
                                    .queryParam("country", location.getCountry())
                                    .queryParam("city", location.getCity())
                                    .queryParam("street", location.getAddress())
                                    .build()
                    ).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<LatLonResponse>>() {
                    })
                    .block();

            if (locationCandidates == null) {
                logger.error("Received null response for location: {}", location);
                throw new ServerError("Failed to calculate location", 500);
            }

            if (locationCandidates.isEmpty()) {
                logger.warn("GeocodingService could not find location {}", location);
                return new LatLongDTO(0.0, 0.0);
            }

            final LatLonResponse latLon = locationCandidates.getFirst();
            return new LatLongDTO(latLon.lat, latLon.lon);

        } catch (WebClientResponseException e) {
            logger.error("Failed to fetch latitude and longitude for location: {}", location, e);
            throw new ServerError("Failed to calculate location", 500);
        }
    }

    @Data
    private static class LatLonResponse {
        private Double lat;
        private Double lon;
    }
}
