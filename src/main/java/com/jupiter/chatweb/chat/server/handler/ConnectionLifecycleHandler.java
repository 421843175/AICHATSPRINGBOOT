package com.jupiter.chatweb.chat.server.handler;

import com.jupiter.chatweb.chat.server.service.ChannelService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ConnectionLifecycleHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private ChannelService channelService;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // 连接断开时解绑用户
        channelService.unbindUser(ctx.channel());
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 发生异常时解绑用户
        channelService.unbindUser(ctx.channel());
        ctx.close();
    }
}