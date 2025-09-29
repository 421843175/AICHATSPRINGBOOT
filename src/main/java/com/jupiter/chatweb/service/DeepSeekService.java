package com.jupiter.chatweb.service;

import com.jupiter.chatweb.config.DeepSeekConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeepSeekService {
    private final DeepSeekConfig config;
    private final RestTemplate restTemplate;

    public String chatCompletion(List<ChatMessage> messages) {
        ChatRequest request = new ChatRequest(config.getModel(), messages, false);

        try {
            ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                    config.getBaseUrl() + "/v1/chat/completions",
                    new HttpEntity<>(request, buildHeaders()),
                    ChatResponse.class
            );
            System.out.println("AI回复:"+response.getBody().getChoices().get(0).getMessage().getContent());
            return response.getBody().getChoices().get(0).getMessage().getContent();
        } catch (ResourceAccessException e) {
            log.error("连接错误: {}", e.getMessage());
            return "网络连接异常，请检查网络设置";
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
            log.error("API错误: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "服务请求失败，错误码：" + e.getStatusCode();
        } catch (Exception e) {
            log.error("系统错误: {}", e.getMessage());
            return "服务暂时不可用";
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + config.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    // 请求响应对象
    @Data
    @AllArgsConstructor
    private static class ChatRequest {
        private String model;
        private List<ChatMessage> messages;
        private boolean stream;
    }

    @Data
    private static class ChatResponse {
        private List<Choice> choices;

        @Data
        private static class Choice {
            private Message message;
        }

        @Data
        private static class Message {
            private String content;
        }
    }

    @Data
    @AllArgsConstructor
    public static class ChatMessage {
        private String role;
        private String content;
    }
}