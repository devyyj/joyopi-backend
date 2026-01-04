package com.joyopi.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyopi.backend.model.VoteRecord;
import com.joyopi.backend.model.VoteRoom;
import com.joyopi.backend.model.VoteUser;
import com.joyopi.backend.repository.VoteRecordRepository;
import com.joyopi.backend.repository.VoteRoomRepository;
import com.joyopi.backend.repository.VoteUserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class VoteHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final VoteRoomRepository roomRepository;
    private final VoteUserRepository userRepository;
    private final VoteRecordRepository recordRepository;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public VoteHandler(VoteRoomRepository roomRepository, VoteUserRepository userRepository,
            VoteRecordRepository recordRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.recordRepository = recordRepository;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void cleanupInactiveUsers() {
        // 1. 세션 맵에서 닫힌 세션 정리
        List<String> closedSessionIds = sessions.entrySet().stream()
                .filter(entry -> !entry.getValue().isOpen())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (String sessionId : closedSessionIds) {
            try {
                handleLeaveRoomInternal(sessionId);
                sessions.remove(sessionId);
            } catch (IOException e) {
                // 무시
            }
        }

        // 2. DB에 남아있으나 세션이 만료되거나 PING 응답이 없는 참여자 정리
        java.time.LocalDateTime cutoff = java.time.LocalDateTime.now().minusSeconds(30);
        List<VoteRoom> activeRooms = roomRepository.findByActiveTrue();
        for (VoteRoom room : activeRooms) {
            List<VoteUser> users = userRepository.findByRoomOrderByJoinOrderAsc(room);
            for (VoteUser user : users) {
                WebSocketSession session = sessions.get(user.getSessionId());
                boolean sessionMissing = (session == null || !session.isOpen());
                boolean pingExpired = (user.getLastPingTime() != null && user.getLastPingTime().isBefore(cutoff));

                if (sessionMissing || pingExpired) {
                    try {
                        handleLeaveRoomInternal(user.getSessionId());
                    } catch (IOException e) {
                        // 무시
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        // 모든 메시지 수신 시 해당 사용자의 마지막 활동 시간 갱신
        userRepository.findBySessionId(session.getId()).ifPresent(user -> {
            user.setLastPingTime(java.time.LocalDateTime.now());
            userRepository.save(user);
        });

        String payload = message.getPayload();
        @SuppressWarnings("unchecked")
        Map<String, Object> data = objectMapper.readValue(payload, Map.class);
        String type = (String) data.get("type");

        switch (type) {
            case "GET_ROOM_COUNT":
                handleGetRoomCount(session);
                break;
            case "CREATE_ROOM":
                handleCreateRoom(session);
                break;
            case "JOIN_ROOM":
                handleJoinRoom(session, data);
                break;
            case "START_VOTE":
                handleStartVote(session);
                break;
            case "SUBMIT_VOTE":
                handleSubmitVote(session, data);
                break;
            case "FINISH_VOTE":
                handleFinishVote(session);
                break;
            case "SKIP_TURN":
                handleSkipTurn(session);
                break;
            case "PING":
                sendMessage(session, Map.of("type", "PONG"));
                break;
            case "LEAVE_ROOM":
                handleLeaveRoom(session);
                break;
        }
    }

    private void handleGetRoomCount(WebSocketSession session) throws IOException {
        long count = roomRepository.countByActiveTrue();
        sendMessage(session, Map.of("type", "ROOM_COUNT", "count", count));
    }

    private void broadcastGlobalRoomCount() {
        try {
            long count = roomRepository.countByActiveTrue();
            String payload = objectMapper.writeValueAsString(Map.of("type", "ROOM_COUNT", "count", count));
            TextMessage message = new TextMessage(payload);
            List<String> toRemove = new java.util.ArrayList<>();

            for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                WebSocketSession session = entry.getValue();
                if (session.isOpen()) {
                    try {
                        synchronized (session) {
                            session.sendMessage(message);
                        }
                    } catch (IOException e) {
                        toRemove.add(entry.getKey());
                    }
                } else {
                    toRemove.add(entry.getKey());
                }
            }

            for (String sessionId : toRemove) {
                try {
                    handleLeaveRoomInternal(sessionId);
                } catch (IOException e) {
                    // 무시
                }
            }
        } catch (Exception e) {
            // objectMapper 등 예외 발생 시
        }
    }

    private void handleCreateRoom(WebSocketSession session) throws IOException {
        String roomCode;
        int attempts = 0;
        do {
            roomCode = String.format("%04d", random.nextInt(10000));
            attempts++;
            if (attempts > 100) {
                sendMessage(session, Map.of("type", "ERROR", "message", "방 생성에 실패했습니다. (방 번호 부족)"));
                return;
            }
        } while (roomRepository.findByRoomCodeAndActiveTrue(roomCode).isPresent());

        VoteRoom room = new VoteRoom();
        room.setRoomCode(roomCode);
        room.setActive(true);
        room = roomRepository.save(room);

        VoteUser user = new VoteUser();
        user.setRoom(room);
        user.setNickname(generateRandomNickname());
        user.setSessionId(session.getId());
        user.setJoinOrder(1);
        user.setHost(true);
        user.setConnected(true);
        user.setLastPingTime(java.time.LocalDateTime.now());
        userRepository.save(user);

        room.setCurrentTurnUserId(user.getId());
        roomRepository.save(room);

        sendMessage(session, Map.of(
                "type", "ROOM_CREATED",
                "roomId", room.getId(),
                "myUserId", user.getId(),
                "roomCode", roomCode));
        broadcastRoomInfo(room);
        broadcastGlobalRoomCount();
    }

    private void handleJoinRoom(WebSocketSession session, Map<String, Object> data) throws IOException {
        String roomCode = (String) data.get("roomCode");

        Optional<VoteRoom> roomOpt = roomRepository.findByRoomCodeAndActiveTrue(roomCode);
        if (roomOpt.isPresent()) {
            VoteRoom room = roomOpt.get();

            List<VoteUser> existingUsers = userRepository.findByRoomOrderByJoinOrderAsc(room);
            int nextJoinOrder = (existingUsers.isEmpty() ? 0
                    : existingUsers.get(existingUsers.size() - 1).getJoinOrder()) + 1;

            VoteUser user = new VoteUser();
            user.setRoom(room);
            user.setNickname(generateRandomNickname());
            user.setSessionId(session.getId());
            user.setJoinOrder(nextJoinOrder); // Always last
            user.setConnected(true);
            user.setLastPingTime(java.time.LocalDateTime.now());

            if (existingUsers.isEmpty()) {
                user.setHost(true);
                room.setCurrentTurnUserId(user.getId());
            }

            userRepository.save(user);
            if (room.getCurrentTurnUserId() == null) {
                room.setCurrentTurnUserId(user.getId());
                roomRepository.save(room);
            }

            sendMessage(session, Map.of(
                    "type", "JOIN_SUCCESS",
                    "roomId", room.getId(),
                    "myUserId", user.getId(),
                    "roomCode", roomCode));
            broadcastRoomInfo(room);
        } else {
            sendMessage(session, Map.of("type", "ERROR", "message", "방을 찾을 수 없습니다."));
        }
    }

    @Transactional
    public void handleStartVote(WebSocketSession session) throws IOException {
        Optional<VoteUser> userOpt = userRepository.findBySessionId(session.getId());
        if (userOpt.isPresent()) {
            VoteUser user = userOpt.get();
            VoteRoom room = user.getRoom();

            if (user.getId().equals(room.getCurrentTurnUserId())) {
                recordRepository.deleteByRoom(room);
                recordRepository.flush();
                broadcastAllInRoom(room, Map.of("type", "VOTE_STARTED", "duration", 10));
            } else {
                sendMessage(session, Map.of("type", "ERROR", "message", "본인 차례가 아닙니다."));
            }
        }
    }

    private void handleSubmitVote(WebSocketSession session, Map<String, Object> data) throws IOException {
        String voteValue = (String) data.get("value");
        Optional<VoteUser> userOpt = userRepository.findBySessionId(session.getId());
        if (userOpt.isPresent()) {
            VoteUser user = userOpt.get();
            VoteRoom room = user.getRoom();

            VoteRecord record = new VoteRecord();
            record.setRoom(room);
            record.setVoteValue(voteValue);
            recordRepository.save(record);

            List<VoteUser> users = userRepository.findByRoomOrderByJoinOrderAsc(room);
            long totalInRoom = users.stream().filter(this::isUserOnline).count();
            long votesCount = recordRepository.findByRoom(room).size();

            broadcastAllInRoom(room, Map.of(
                    "type", "VOTE_SUBMITTED",
                    "currentVotes", votesCount,
                    "totalUsers", totalInRoom));
        }
    }

    private void handleFinishVote(WebSocketSession session) throws IOException {
        Optional<VoteUser> userOpt = userRepository.findBySessionId(session.getId());
        if (userOpt.isPresent()) {
            VoteUser user = userOpt.get();
            VoteRoom room = user.getRoom();

            List<VoteRecord> results = recordRepository.findByRoom(room);
            Map<String, Long> summary = results.stream()
                    .collect(Collectors.groupingBy(VoteRecord::getVoteValue, Collectors.counting()));

            List<VoteUser> users = userRepository.findByRoomOrderByJoinOrderAsc(room);
            long totalParticipants = users.stream().filter(this::isUserOnline).count();
            long totalVotes = results.size();

            broadcastAllInRoom(room, Map.of(
                    "type", "VOTE_FINISHED",
                    "results", summary,
                    "totalVotes", totalVotes,
                    "totalParticipants", totalParticipants));

            advanceTurn(room);
        }
    }

    private void handleSkipTurn(WebSocketSession session) throws IOException {
        Optional<VoteUser> userOpt = userRepository.findBySessionId(session.getId());
        if (userOpt.isPresent()) {
            VoteUser user = userOpt.get();
            VoteRoom room = user.getRoom();
            // 요청자가 본인 차례이거나, 해당 방의 방장(Host)인 경우 차례 넘기기 허용
            if (user.getId().equals(room.getCurrentTurnUserId()) || user.isHost()) {
                advanceTurn(room);
            } else {
                sendMessage(session, Map.of("type", "ERROR", "message", "권한이 없습니다."));
            }
        }
    }

    @Transactional
    public void advanceTurn(VoteRoom room) throws IOException {
        // 모든 사용자 정보를 가져옴 (연결 여부 무관)
        List<VoteUser> users = userRepository.findByRoomOrderByJoinOrderAsc(room);
        if (users.isEmpty())
            return;

        Long currentId = room.getCurrentTurnUserId();
        int currentIndex = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(currentId)) {
                currentIndex = i;
                break;
            }
        }

        // 다음 살아있는(Connected) 사용자를 찾거나 죽은 사용자 정리
        VoteUser nextUser = null;
        int checkedCount = 0;
        List<String> sessionsToRemove = new ArrayList<>();

        while (checkedCount < users.size()) {
            currentIndex = (currentIndex + 1) % users.size();
            VoteUser candidate = users.get(currentIndex);

            if (isUserOnline(candidate)) {
                if (!candidate.isConnected()) {
                    candidate.setConnected(true);
                    userRepository.save(candidate);
                }
                nextUser = candidate;
                break;
            } else {
                // 연결이 끊긴 사용자는 목록에 추가하여 나중에 제거
                sessionsToRemove.add(candidate.getSessionId());
            }
            checkedCount++;
        }

        // 연결 끊긴 사용자들 실제 제거
        for (String sessionId : sessionsToRemove) {
            handleLeaveRoomInternal(sessionId);
        }

        if (nextUser != null) {
            room.setCurrentTurnUserId(nextUser.getId());
            roomRepository.save(room);
            broadcastRoomInfo(room);
        } else {
            // 아무도 연결되어 있지 않음
            room.setCurrentTurnUserId(null);
            roomRepository.save(room);
            broadcastRoomInfo(room);
        }
    }

    @Transactional
    public void handleLeaveRoom(WebSocketSession session) throws IOException {
        handleLeaveRoomInternal(session.getId());
    }

    @Transactional
    private void handleLeaveRoomInternal(String sessionId) throws IOException {
        Optional<VoteUser> userOpt = userRepository.findBySessionId(sessionId);
        if (userOpt.isPresent()) {
            VoteUser user = userOpt.get();
            VoteRoom room = user.getRoom();
            boolean wasTurn = user.getId().equals(room.getCurrentTurnUserId());
            boolean wasHost = user.isHost();

            userRepository.delete(user);
            userRepository.flush();

            List<VoteUser> remainingUsers = userRepository.findByRoomOrderByJoinOrderAsc(room);
            if (remainingUsers.isEmpty()) {
                room.setActive(false);
                recordRepository.deleteByRoom(room);
                recordRepository.flush();
                roomRepository.delete(room);
                roomRepository.flush();
                broadcastGlobalRoomCount();
            } else {
                if (wasHost) {
                    VoteUser nextHost = remainingUsers.get(0);
                    nextHost.setHost(true);
                    userRepository.save(nextHost);
                }

                if (wasTurn) {
                    // 나간 사람이 턴이었다면 다음 사람 찾기
                    advanceTurn(room);
                } else {
                    broadcastRoomInfo(room);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        handleLeaveRoomInternal(session.getId());
        sessions.remove(session.getId());
    }

    private void broadcastRoomInfo(VoteRoom room) throws IOException {
        List<VoteUser> users = userRepository.findByRoomOrderByJoinOrderAsc(room);
        List<Map<String, Object>> userInfo = users.stream().map(u -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", u.getId());
            map.put("nickname", u.getNickname());
            map.put("isHost", u.isHost());
            boolean online = isUserOnline(u);
            map.put("connected", online);

            // DB 필드와 동기화 (선택 사항이나 정화 일관성을 위해 유지)
            if (u.isConnected() != online) {
                u.setConnected(online);
                userRepository.save(u);
            }

            map.put("joinOrder", u.getJoinOrder());
            map.put("isCurrentTurn", u.getId().equals(room.getCurrentTurnUserId()));
            return map;
        }).collect(Collectors.toList());

        broadcastAllInRoom(room, Map.of(
                "type", "ROOM_INFO",
                "roomCode", room.getRoomCode(),
                "users", userInfo,
                "currentTurnUserId", room.getCurrentTurnUserId() != null ? room.getCurrentTurnUserId() : 0));
    }

    private void broadcastAllInRoom(VoteRoom room, Map<String, Object> data) throws IOException {
        String payload = objectMapper.writeValueAsString(data);
        List<VoteUser> users = userRepository.findByRoomOrderByJoinOrderAsc(room);
        List<String> sessionsToRemove = new java.util.ArrayList<>();

        for (VoteUser user : users) {
            WebSocketSession session = sessions.get(user.getSessionId());
            if (session != null && session.isOpen()) {
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(payload));
                    }
                } catch (IOException e) {
                    sessionsToRemove.add(user.getSessionId());
                }
            } else {
                sessionsToRemove.add(user.getSessionId());
            }
        }

        // 전송 실패한 세션들을 모아서 한꺼번에 처리 (무한 재귀 방지)
        for (String sessionId : sessionsToRemove) {
            handleLeaveRoomInternal(sessionId);
        }
    }

    private String generateRandomNickname() {
        char c1 = (char) ('A' + random.nextInt(26));
        char c2 = (char) ('A' + random.nextInt(26));
        int n = random.nextInt(100);
        return String.format("%c%c%02d", c1, c2, n);
    }

    private void sendMessage(WebSocketSession session, Map<String, Object> data) {
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(data)));
            }
        } catch (IOException e) {
            try {
                handleLeaveRoomInternal(session.getId());
            } catch (IOException ex) {
                // 로깅 생략 또는 기본 처리
            }
        }
    }

    private boolean isUserOnline(VoteUser user) {
        if (user == null || user.getSessionId() == null)
            return false;
        WebSocketSession session = sessions.get(user.getSessionId());
        return session != null && session.isOpen();
    }
}
