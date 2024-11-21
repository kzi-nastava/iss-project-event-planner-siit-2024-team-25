package com.team25.event.planner.offering.service.model;

import com.team25.event.planner.offering.common.model.Offering;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Service")
@Data
public class Service extends Offering{
    private String specifcs;
    private int duration;
    private int reservationDeadline;
    private int cancellationDeadline;

    @Enumerated(EnumType.STRING)
    private ReservationType reservationType;


}
