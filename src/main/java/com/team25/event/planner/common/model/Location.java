package com.team25.event.planner.common.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {
    private String country;
    private String city;
    private String address;
    private Long latitude;
    private Long longitude;
}
