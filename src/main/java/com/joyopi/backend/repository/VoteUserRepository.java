package com.joyopi.backend.repository;

import com.joyopi.backend.model.VoteUser;
import com.joyopi.backend.model.VoteRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VoteUserRepository extends JpaRepository<VoteUser, Long> {
    List<VoteUser> findByRoomOrderByJoinOrderAsc(VoteRoom room);

    Optional<VoteUser> findBySessionId(String sessionId);

    int countByRoom(VoteRoom room);

    List<VoteUser> findByRoomAndConnectedTrueOrderByJoinOrderAsc(VoteRoom room);
}
