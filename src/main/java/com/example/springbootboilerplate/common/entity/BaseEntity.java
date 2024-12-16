package com.example.springbootboilerplate.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@MappedSuperclass
@NoArgsConstructor
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = getNow();
        this.updatedAt = getNow();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = getNow();
    }

    private LocalDateTime getNow() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}