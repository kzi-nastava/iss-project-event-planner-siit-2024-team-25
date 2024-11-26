package com.team25.event.planner.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Money {

    @NotNull(message = "Amount is  required field")
    private double amount;

    @NotNull(message = "Currency is required field")
    private String currency;

}
