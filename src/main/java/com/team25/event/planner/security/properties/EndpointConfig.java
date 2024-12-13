package com.team25.event.planner.security.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EndpointConfig {
    private String path;
    private List<String> methods;
}
