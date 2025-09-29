package com.jupiter.chatweb.chat.server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jupiter.chatweb.mapper.UserMapper;
import com.jupiter.chatweb.chat.server.service.ChannelService;
import com.jupiter.chatweb.util.JsonUtil;
import com.jupiter.chatweb.util.TokenUtils;
import com.sun.xml.internal.bind.v2.TODO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@ChannelHandler.Sharable
public class MessagesHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
//    TODO:NOTICE 核心消息处理器
    @Autowired
    private ChannelService channelService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws JsonProcessingException {
        Map<String, Object> message = JsonUtil.fromJson(msg.text(), Map.class);
        System.out.println("msg====>"+message);

        String token = (String) message.get("token");
        System.out.println("token="+token);
        // 验证用户有效性
        String username = TokenUtils.getLoginName(token);

        String type =(String) message.get("type");

        if ("AUTH".equals(type)) {
            auth(ctx, username);
        }
        else if("MSG".equals(type)){
            String body = (String) message.get("body");
            String to = (String) message.get("to");
            msg(username,body,to);

        }
        else {
                ctx.close();
            }
        }


    private void msg(String sender,String body,String to){
        Channel Tochannel = channelService.getChannel(to);
        System.out.println(Tochannel);

        // body封装成json
        JSONObject messageData = new JSONObject();
        //消息类型 信息
        messageData.put("type","message");

        messageData.put("sender", sender);
        messageData.put("content", body);
        messageData.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        messageData.put("avatar", "");
        messageData.put("isSelf", false);

        // 使用 FastJSON 序列化
        String jsonMessage = JSON.toJSONString(messageData);

        // 发送消息
        Channel toChannel = channelService.getChannel(to);

        boolean isOnline = (toChannel != null && toChannel.isActive());
        // 无论在线与否都持久化消息
        channelService.persistMessage(sender, to, body, isOnline);


        if (isOnline) {
            toChannel.writeAndFlush(new TextWebSocketFrame(jsonMessage));
        } else {
            //用户不在线 未读消息+1 写到数据库
            System.out.println("用户 " + to + " 不在线");
            // 更新未读计数
            channelService.updateUnreadCount(sender, to);
        }
    }
    private void auth(ChannelHandlerContext ctx,String username) {

        System.out.println("username==="+username+",ctx.channel===>"+ ctx.channel());
        channelService.bindUser(username, ctx.channel());


    }
}

