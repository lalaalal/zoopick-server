package com.zoopick.server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoopick.server.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@NullMarked
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final ChatWebSocketBroadcaster chatWebSocketBroadcaster;
    private final ChatRoomService chatRoomService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (session.getAttributes().get(AuthHandshakeInterceptor.USER_ID_ATTRIBUTE) == null)
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized websocket session"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatSocketMessage chatSocketMessage = objectMapper.convertValue(message.getPayload(), ChatSocketMessage.class);
        long userId = WebSocketSessionUtils.getUserId(session);
        if (!chatRoomService.getParticipants(chatSocketMessage.roomId()).contains(userId)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Not permitted to access room"));
            return;
        }

        if (!webSocketSessionManager.getSessionsByRoom(chatSocketMessage.roomId()).contains(session))
            webSocketSessionManager.join(chatSocketMessage.roomId(), session);
        if (chatSocketMessage.type() == ChatSocketMessage.Type.MESSAGE)
            chatWebSocketBroadcaster.broadcast(chatSocketMessage.roomId(), session, chatSocketMessage.content());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        webSocketSessionManager.leave(session);
    }
}
