package com.joyopi.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParrotHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> roles = new ConcurrentHashMap<>(); // sessionId -> role (GENERAL, PARROT)

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        roles.put(session.getId(), "GENERAL"); // Default role
        broadcastUserList();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        @SuppressWarnings("unchecked")
        Map<String, Object> data = objectMapper.readValue(payload, Map.class);
        String type = (String) data.get("type");

        switch (type) {
            case "JOIN":
                String role = (String) data.get("role");
                roles.put(session.getId(), role);
                System.out.println("User joined: " + session.getId() + " as " + role);
                broadcastUserList();
                break;
            case "TRIGGER_SOUND":
                System.out.println("Trigger sound request from: " + session.getId());
                broadcastToRole("PARROT", data);
                break;
            case "STOP_SOUND":
                System.out.println("Stop sound request from: " + session.getId());
                broadcastToRole("PARROT", data);
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        roles.remove(session.getId());
        broadcastUserList();
    }

    private void broadcastUserList() throws IOException {
        List<Map<String, String>> userList = sessions.keySet().stream()
                .map(id -> Map.of("id", id, "role", roles.get(id)))
                .collect(Collectors.toList());

        String payload = objectMapper.writeValueAsString(Map.of(
                "type", "USER_LIST",
                "users", userList));
        broadcastAll(payload);
    }

    private void broadcastToRole(String targetRole, Map<String, Object> data) throws IOException {
        String payload = objectMapper.writeValueAsString(data);
        for (String sessionId : sessions.keySet()) {
            String role = roles.get(sessionId);
            if (targetRole.equals(role)) {
                WebSocketSession session = sessions.get(sessionId);
                safeSendMessage(session, payload);
            }
        }
    }

    private void broadcastAll(String payload) {
        for (WebSocketSession session : sessions.values()) {
            safeSendMessage(session, payload);
        }
    }

    private void safeSendMessage(WebSocketSession session, String payload) {
        if (session == null)
            return;

        synchronized (session) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(payload));
                } catch (IOException | IllegalStateException e) {
                    // 세션이 이미 닫혔거나 전송 실패 시 조용히 세션 제거
                    removeSession(session);
                }
            } else {
                removeSession(session);
            }
        }
    }

    private void removeSession(WebSocketSession session) {
        String sessionId = session.getId();
        if (sessions.containsKey(sessionId)) {
            sessions.remove(sessionId);
            roles.remove(sessionId);
            System.out.println("Session removed: " + sessionId);
            try {
                broadcastUserList();
            } catch (IOException e) {
                // 브로드캐스트 실패 시 무시
            }
        }
    }
}
