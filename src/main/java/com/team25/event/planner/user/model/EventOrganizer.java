package com.team25.event.planner.user.model;

import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.user.converter.PhoneNumberConverter;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_organizers")
public class EventOrganizer extends User{
    @Embedded
    private Location livingAddress;

    @Column(columnDefinition = "varchar(16)")
    @Convert(converter = PhoneNumberConverter.class)
    @Valid
    private PhoneNumber phoneNumber;
}
