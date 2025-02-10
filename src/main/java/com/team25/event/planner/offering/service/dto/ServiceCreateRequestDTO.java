package com.team25.event.planner.offering.service.dto;

import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.service.model.ReservationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCreateRequestDTO {
        private String name;
        private String description;
        private double price;
        private double discount;
        private List<MultipartFile> images;
        private boolean visible;
        private boolean available;
        private String specifics;
        private ReservationType reservationType;
        private int duration;
        private int reservationDeadline;
        private int cancellationDeadline;
        private int minimumArrangement;
        private int maximumArrangement;
        private List<Long> eventTypesIDs;
        private Long offeringCategoryID;
        private String offeringCategoryName;
        private Long ownerId;
}
