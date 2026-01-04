package com.joyopi.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class VoteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private VoteRoom room;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String sessionId;

    private int joinOrder;

    private boolean isHost = false;

    private boolean connected = true;

    private java.time.LocalDateTime lastPingTime;
}
