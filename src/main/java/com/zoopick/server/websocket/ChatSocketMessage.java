package com.zoopick.server.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatSocketMessage(Type type, @JsonProperty("room_id") long roomId, String content) {
    public enum Type {
        JOIN,
        MESSAGE
    }
}
