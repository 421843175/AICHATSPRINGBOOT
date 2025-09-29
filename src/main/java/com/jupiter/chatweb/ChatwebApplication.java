package com.jupiter.chatweb;

import com.jupiter.chatweb.chat.server.ChatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatwebApplication implements CommandLineRunner {

    @Autowired
    private ChatServer chatServer; // 注入ChatServer实例
    public static void main(String[] args) {
        System.out.println("系统后端服务成功运行……");
        SpringApplication.run(ChatwebApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("聊天系统服务端成功运行……");
        chatServer.startNettyServer();
    }

}
