package com.team25.event.planner.offering.service.model;

import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ManyToMany(mappedBy = "favoriteServices")
    private List<User> users;


}
