package com.team25.event.planner.event.model;

import jakarta.persistence.*;
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
public class EventInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Guest email is required")
    private String guestEmail;

    @NotNull(message = "Invitation code is required")
    private String invitationCode;

    @NotNull(message = "Event invitation status is required")
    @Enumerated(EnumType.STRING)
    private EventInvitationStatus status;

    @OneToOne
    private Event event;

}
