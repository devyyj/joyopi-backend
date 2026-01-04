package com.joyopi.backend.service;

import com.joyopi.backend.model.VoteRoom;
import com.joyopi.backend.repository.VoteRecordRepository;
import com.joyopi.backend.repository.VoteRoomRepository;
import com.joyopi.backend.repository.VoteUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteRoomCleanupService {

    private final VoteRoomRepository roomRepository;
    private final VoteUserRepository userRepository;
    private final VoteRecordRepository recordRepository;

    /**
     * 참여자가 없는 유령 방을 1분마다 정리합니다.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cleanupGhostRooms() {
        List<VoteRoom> allRooms = roomRepository.findAll();

        for (VoteRoom room : allRooms) {
            int userCount = userRepository.countByRoom(room);

            if (userCount == 0) {
                log.info("Cleaning up ghost room: code={}, id={}", room.getRoomCode(), room.getId());
                try {
                    // 삭제 순서: 기록 -> 방 (외래키 제약 조건 고려)
                    recordRepository.deleteByRoom(room);
                    recordRepository.flush();
                    roomRepository.delete(room);
                    roomRepository.flush();
                    log.info("Ghost room {} cleaned up successfully.", room.getRoomCode());
                } catch (Exception e) {
                    log.error("Failed to clean up room {}: {}", room.getRoomCode(), e.getMessage());
                }
            }
        }
    }
}
