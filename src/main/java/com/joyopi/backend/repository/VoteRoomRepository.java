package com.joyopi.backend.repository;

import com.joyopi.backend.model.VoteRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VoteRoomRepository extends JpaRepository<VoteRoom, Long> {
    Optional<VoteRoom> findByRoomCodeAndActiveTrue(String roomCode);

    List<VoteRoom> findByActiveTrue();

    long countByActiveTrue();
}
