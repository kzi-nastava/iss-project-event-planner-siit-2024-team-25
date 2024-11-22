package com.team25.event.planner.offering.service.controller;

import com.team25.event.planner.offering.service.model.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("api/services")
public class ServiceController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Service>> getServices() {
        Collection<Service> services = new ArrayList<>();

        Service service = new Service();
        service.setId(1L);
        service.setName("Service 1");

        Service service2 = new Service();
        service2.setId(1L);
        service2.setName("Service 2");

        services.add(service);
        services.add(service2);
        return new ResponseEntity<Collection<Service>>(services, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Service> createService(@RequestBody Service service)throws Exception {
        Service service1 = new Service();
        service1.setId(1L);
        service1.setName(service.getName());
        service1.setDescription(service.getDescription());

        return new ResponseEntity<Service>(service1, HttpStatus.CREATED);
    }
}
