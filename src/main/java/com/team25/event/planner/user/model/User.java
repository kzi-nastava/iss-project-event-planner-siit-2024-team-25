package com.team25.event.planner.user.model;

import com.team25.event.planner.communication.model.Notification;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.model.Service;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    @NotNull(message = "Last name is required")
    @Column(nullable = false)
    private String lastName;

    private String profilePictureUrl;

    @NotNull(message = "User role name is required")
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserRole userRole;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserStatus userStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Account account;

    @ManyToMany
    @JoinTable(
            name = "user_blocks",
            joinColumns = @JoinColumn(name = "blocker_id"), // User who blocks
            inverseJoinColumns = @JoinColumn(name = "blocked_id") // User who is blocked
    )
    private List<User> blockedUsers;

    @ManyToMany(mappedBy = "blockedUsers")
    private List<User> blockedByUsers; // Users who have blocked this user

    @ManyToMany
    @JoinTable(name = "favorite_services")
    private List<Service> favoriteServices;

    @ManyToMany
    @JoinTable(name = "favorite_products")
    private List<Product> favoriteProducts;

    @ManyToMany
    @JoinTable(name = "favorite_events")
    private List<Event> favoriteEvents;

    @ManyToMany
    private List<Notification> notifications;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

