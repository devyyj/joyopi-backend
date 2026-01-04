package com.joyopi.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyopi.backend.model.VoteRoom;
import com.joyopi.backend.model.VoteUser;
import com.joyopi.backend.repository.VoteRecordRepository;
import com.joyopi.backend.repository.VoteRoomRepository;
import com.joyopi.backend.repository.VoteUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VoteHandlerTest {

    private VoteHandler voteHandler;
    private VoteRoomRepository roomRepository;
    private VoteUserRepository userRepository;
    private VoteRecordRepository recordRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        roomRepository = mock(VoteRoomRepository.class);
        userRepository = mock(VoteUserRepository.class);
        recordRepository = mock(VoteRecordRepository.class);
        voteHandler = new VoteHandler(roomRepository, userRepository, recordRepository);
    }

    @Test
    void testHandleCreateRoom() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");

        // Mock room saving
        when(roomRepository.save(any(VoteRoom.class))).thenAnswer(i -> {
            VoteRoom r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        // Mock user saving
        when(userRepository.save(any(VoteUser.class))).thenAnswer(i -> {
            VoteUser u = i.getArgument(0);
            if (u.getId() == null)
                u.setId(1L);
            return u;
        });

        // Mock repository methods used in broadcast
        when(userRepository.findByRoomOrderByJoinOrderAsc(any())).thenReturn(new java.util.ArrayList<>());

        // Trigger action
        voteHandler.afterConnectionEstablished(session);
        TextMessage message = new TextMessage("{\"type\":\"CREATE_ROOM\"}");
        voteHandler.handleTextMessage(session, message);

        // Verify result
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());

        boolean foundCreated = false;
        for (TextMessage sent : captor.getAllValues()) {
            Map data = objectMapper.readValue(sent.getPayload(), Map.class);
            if ("ROOM_CREATED".equals(data.get("type"))) {
                foundCreated = true;
                assertNotNull(data.get("roomCode"));
                break;
            }
        }
        assertTrue(foundCreated, "Should receive ROOM_CREATED message");
    }

    @Test
    void testHandleJoinRoom() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session2");
        when(session.isOpen()).thenReturn(true);

        VoteRoom room = new VoteRoom();
        room.setId(1L);
        room.setRoomCode("1234");
        room.setActive(true);

        when(roomRepository.findByRoomCodeAndActiveTrue("1234")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(VoteRoom.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(VoteUser.class))).thenAnswer(i -> {
            VoteUser u = i.getArgument(0);
            if (u.getId() == null)
                u.setId(1L);
            return u;
        });
        when(userRepository.findByRoomOrderByJoinOrderAsc(any())).thenAnswer(i -> {
            VoteUser u = new VoteUser();
            u.setId(1L);
            u.setNickname("TestUser");
            u.setSessionId("session2"); // 필수 설정 (ConcurrentHashMap null key 방지)
            return java.util.List.of(u);
        });

        voteHandler.afterConnectionEstablished(session);
        TextMessage message = new TextMessage("{\"type\":\"JOIN_ROOM\", \"roomCode\":\"1234\"}");
        voteHandler.handleTextMessage(session, message);

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());

        boolean joinSuccess = false;
        for (TextMessage sent : captor.getAllValues()) {
            Map data = objectMapper.readValue(sent.getPayload(), Map.class);
            if ("JOIN_SUCCESS".equals(data.get("type"))) {
                joinSuccess = true;
                assertEquals("1234", data.get("roomCode"));
            }
        }
        assertTrue(joinSuccess, "Should receive JOIN_SUCCESS message");
    }

    @Test
    void testAdvanceTurnSkipsDisconnectedUser() throws Exception {
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session1.getId()).thenReturn("s1");
        when(session2.getId()).thenReturn("s2");
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(false); // Disconnected

        VoteRoom room = new VoteRoom();
        room.setId(1L);
        room.setRoomCode("8888");
        room.setActive(true);

        VoteUser u1 = new VoteUser();
        u1.setId(101L);
        u1.setSessionId("s1");
        u1.setNickname("User1");
        u1.setConnected(true);
        VoteUser u2 = new VoteUser();
        u2.setId(102L);
        u2.setSessionId("s2");
        u2.setNickname("User2");
        u2.setConnected(true);
        room.setCurrentTurnUserId(101L);

        List<VoteUser> users = java.util.List.of(u1, u2);
        when(userRepository.findByRoomOrderByJoinOrderAsc(room)).thenReturn(users);

        voteHandler.afterConnectionEstablished(session1);
        // session2 is NOT put into sessions map or its isOpen returns false

        // Trigger advanceTurn
        voteHandler.advanceTurn(room);

        // Turn should go back to 101 because 102 is disconnected
        assertEquals(101L, room.getCurrentTurnUserId());
    }
}
