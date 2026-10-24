package com.example.distributed_lovebale.account_service.entity;

import com.example.distributed_lovable.common_lib.enums.SubscriptionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Many users can have one subscription, one subscription can have many users
    @ManyToOne(fetch = FetchType.LAZY)
    // need to have a user column to link the subscription to a user
    @JoinColumn(nullable = false, name="user_id")
    User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name="plan_id")
    Plan plan;

    @Enumerated(EnumType.STRING)
    SubscriptionStatus status;

    //  User can see subscription status in strip dashboard
    String stripeSubscriptionId;// gateway subscription id

    // will change periodically if subscription active
    Instant currentPeriodStart;
    Instant currentPeriodEnd;

    Boolean cancelAtPeriodEnd = false;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
}