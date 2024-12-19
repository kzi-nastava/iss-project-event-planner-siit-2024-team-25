package com.team25.event.planner.event.model;

import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Money money;

    @ManyToOne
    private OfferingCategory offeringCategory;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; //event_id
}
