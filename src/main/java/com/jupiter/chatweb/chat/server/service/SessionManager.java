package com.jupiter.chatweb.chat.server.service;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// SessionManager.java
@Component
public class SessionManager {
    private final Map<String, Channel> sessionMap = new ConcurrentHashMap<>();
    private final Map<Channel, String> channelSessionMap = new ConcurrentHashMap<>();

    public String createSession(Channel channel) {
        String sessionId = UUID.randomUUID().toString();
        sessionMap.put(sessionId, channel);
        channelSessionMap.put(channel, sessionId);
        return sessionId;
    }

    public Channel getChannelBySession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public void removeSession(Channel channel) {
        String sessionId = channelSessionMap.get(channel);
        if (sessionId != null) {
            sessionMap.remove(sessionId);
            channelSessionMap.remove(channel);
        }
    }
}