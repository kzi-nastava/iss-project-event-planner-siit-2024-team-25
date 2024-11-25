package com.team25.event.planner.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Money {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Amount is  required field")
    @Column(nullable = false)
    private double amount;

    @NotNull(message = "Currency is required field")
    @Column(nullable = false)
    private String currency;

}
