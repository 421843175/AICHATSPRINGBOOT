package com.jupiter.chatweb.chat.server.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jupiter.chatweb.entity.FriendshipsEntity;
import com.jupiter.chatweb.entity.MessagesEntity;
import com.jupiter.chatweb.entity.UserEntity;
import com.jupiter.chatweb.mapper.FriendshipsDao;
import com.jupiter.chatweb.mapper.MessagesDao;
import com.jupiter.chatweb.mapper.UserMapper;
import com.jupiter.chatweb.util.AjaxResult;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChannelService {
    private final Map<String, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();
    public static final AttributeKey<String> USERNAME_KEY = AttributeKey.valueOf("username");

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessagesDao messagesMapper;

    @Autowired
    private FriendshipsDao friendshipsMapper;



    // 绑定用户与通道
    public void bindUser(String username, Channel channel) {
        System.out.println("绑定用户通道……");
        channel.attr(USERNAME_KEY).set(username);
        USER_CHANNEL_MAP.put(username, channel);
        // 存储到Redis，设置60*24分钟过期
        redisTemplate.opsForValue().set(
                "channel:"+username,
                channel.id().asLongText(),
                Duration.ofMinutes(60*24)
        );


    }

    // 解绑用户
    public void unbindUser(Channel channel) {
        String username = channel.attr(USERNAME_KEY).get();
        if (username != null) {
            USER_CHANNEL_MAP.remove(username);
            redisTemplate.delete("channel:"+username); // 移除Redis中的记录
        }
    }

    // 获取用户通道
    public Channel getChannel(String username) {
        return USER_CHANNEL_MAP.get(username);
    }

    // 可选：从Redis获取通道ID（用于跨服务查询等场景）
    public String getChannelId(String username) {
        return redisTemplate.opsForValue().get("channel:"+username);
    }


    public void persistMessage
            (String sender, String receiver, String content, boolean isOnline) {
        try {
            // 获取用户ID
            Integer senderId = getUserId(sender);
            Integer receiverId = getUserId(receiver);

            if (senderId == null || receiverId == null) {
                log.error("用户不存在 sender:{} receiver:{}", sender, receiver);
                return;
            }

            // 保存消息记录
            MessagesEntity message = new MessagesEntity();
            message.setSenderUsername(sender);
            message.setReceiverUsername(receiver);
            message.setContent(content);
            message.setSendTime(new Date());
            message.setIsSend(1);
            message.setDeleteStatus(0);
            message.setIs_active(isOnline ? 1 : 0); // 标记在线状态

            messagesMapper.insert(message);

            int updated = friendshipsMapper.updateLastMessage(sender, receiver, content);
            if(updated == 0) {
                log.error("好友关系不存在: {} -> {}", sender, receiver);
            }

        } catch (Exception e) {
            log.error("消息持久化失败", e);
            throw new RuntimeException("消息保存失败", e);
        }
    }


    public void updateUnreadCount(String sender, String receiver) {
        try {
            int updated = friendshipsMapper.updateUnreadCount(sender, receiver);
            if(updated == 0) {
                log.warn("好友关系不存在: {} -> {}", sender, receiver);
            }
        } catch (Exception e) {
            log.error("未读数更新失败", e);
            throw new RuntimeException("未读数更新失败", e);
        }
    }

    private Integer getUserId(String username) {
        UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .select("id")
                .eq("username", username));
        return user != null ? user.getId() : null;
    }

//    public void noActiveMessage(String sender, String receiver, String content) {
//        try {
//            // 1. 保存消息记录
//            UserEntity senderUser = userMapper.selectOne(new QueryWrapper<UserEntity>()
//                    .eq("username", sender));
//            UserEntity receiverUser = userMapper.selectOne(new QueryWrapper<UserEntity>()
//                    .eq("username", receiver));
//
//            if (senderUser == null || receiverUser == null) {
//                log.error("用户不存在 sender:{} receiver:{}", sender, receiver);
//                return;
//            }
//
//            MessagesEntity message = new MessagesEntity();
//            message.setSenderUsername(sender);
//            message.setReceiverUsername(receiver);
//            message.setContent(content);
//            message.setSendTime(new Date());
//            message.setIsSend(1);
//            message.setDeleteStatus(0);
//            //is_active 不在线 说明是未读的消息
//            message.setIs_active(0);
//
//            messagesMapper.insert(message);
//
//            // 2. 更新好友未读数
//
//            int updated = friendshipsMapper.updateUnreadCount(sender, receiver);
//            if(updated == 0) {
//                throw new RuntimeException("好友关系不存在: "+sender+" -> "+receiver);
//            }
//
//        } catch (Exception e) {
//            log.error("离线消息处理失败", e);
//        }
//
//
//    }

//    public AjaxResult<JSONArray> getChatHistory(String sender,String receiver){
//
//    }
}