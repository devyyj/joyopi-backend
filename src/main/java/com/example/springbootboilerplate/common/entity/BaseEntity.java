package com.example.springbootboilerplate.common.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@ToString
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