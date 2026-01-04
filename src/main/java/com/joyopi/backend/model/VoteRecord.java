package com.joyopi.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class VoteRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private VoteRoom room;

    private String voteValue;

    private LocalDateTime votedAt = LocalDateTime.now();
}
