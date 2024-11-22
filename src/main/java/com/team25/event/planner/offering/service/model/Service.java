package com.team25.event.planner.offering.service.model;

import com.team25.event.planner.offering.common.model.Offering;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Service extends Offering{
    private String specifics;
    private int duration;
    private int reservationDeadline;
    private int cancellationDeadline;

    @Enumerated(EnumType.STRING)
    private ReservationType reservationType;


}
