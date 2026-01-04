package com.joyopi.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class VoteRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 4)
    private String roomCode;

    private boolean active = true;

    private Long currentTurnUserId;

    private LocalDateTime createdAt = LocalDateTime.now();
}
