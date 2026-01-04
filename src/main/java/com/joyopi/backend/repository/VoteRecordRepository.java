package com.joyopi.backend.repository;

import com.joyopi.backend.model.VoteRecord;
import com.joyopi.backend.model.VoteRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    List<VoteRecord> findByRoom(VoteRoom room);

    @Modifying
    @Transactional
    void deleteByRoom(VoteRoom room);
}
