package com.team25.event.planner.user.model;

import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.user.converter.PhoneNumberConverter;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "owners")
public class Owner extends User {
    @NotNull(message = "Company name is required")
    @Column(nullable = false)
    private String companyName;

    @Embedded
    private Location companyAddress;

    @Column(columnDefinition = "varchar(16)")
    @Convert(converter = PhoneNumberConverter.class)
    @Valid
    private PhoneNumber contactPhone;

    @NotNull(message = "Description name is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}
