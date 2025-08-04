package com.team25.event.planner.common.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {
    private String country;
    private String city;
    private String address;
    private Double latitude;
    private Double longitude;

    public boolean sameLocationName(Location other) {
        if(other == null) return false;
        return Objects.equals(country, other.country) && Objects.equals(city, other.city) && Objects.equals(address, other.address);
    }
}
