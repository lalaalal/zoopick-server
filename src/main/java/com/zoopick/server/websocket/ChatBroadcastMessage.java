package com.zoopick.server.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatBroadcastMessage(@JsonProperty("sender_nickname") String senderNickname, String message) {

}
