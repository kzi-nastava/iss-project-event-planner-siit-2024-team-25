package com.team25.event.planner.offering.service.model;

import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Service extends Offering{
    public Service(Long id, String name, String description, double price, double discount, List<String> images, boolean isVisible,
                   boolean isAvailable, OfferingType status, Collection<EventType> eventTypes, OfferingCategory offeringCategory, Owner owner,
                   String specifics, int duration, int reservationDeadline, int cancellationDeadline, ReservationType reservationType, List<User> users) {
        super(id, name, description, price, discount, images, isVisible, isAvailable, status, eventTypes, offeringCategory, owner);
        this.specifics = specifics;
        this.duration = duration;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.reservationType = reservationType;
        this.users = users;
    }

    private String specifics;
    private int duration;
    private int reservationDeadline;
    private int cancellationDeadline;

    @Enumerated(EnumType.STRING)
    private ReservationType reservationType;

    @ManyToMany(mappedBy = "favoriteServices")
    @ToString.Exclude
    private List<User> users;


}
