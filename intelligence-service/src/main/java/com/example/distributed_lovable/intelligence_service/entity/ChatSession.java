package com.example.distributed_lovable.intelligence_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
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


@Entity
@Table(name = "chat_sessions")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatSession {

    @EmbeddedId
    private ChatSessionId id;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @MapsId("projectId")
//    @JoinColumn(name = "project_id", nullable = false, updatable = false)
//    Project project;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @MapsId("userId")
//    @JoinColumn(name = "user_id", nullable = false, updatable = false)
//    User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt; // soft delete
}