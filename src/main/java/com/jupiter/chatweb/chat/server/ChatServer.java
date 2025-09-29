package com.jupiter.chatweb.chat.server;


import com.jupiter.chatweb.chat.server.handler.MessagesHandler;
import com.jupiter.chatweb.chat.server.handler.ConnectionLifecycleHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ChatServer {
    @Autowired
    MessagesHandler AuthHandler;

    @Autowired
    ConnectionLifecycleHandler connectionLifecycleHandler ;


//    @Autowired
//    MessageHandler messageHandler;

    public  void startNettyServer() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
//        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();






        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(new HttpServerCodec());
                    ch.pipeline().addLast(new ChunkedWriteHandler());
                    ch.pipeline().addLast(new HttpObjectAggregator(65536));
                    ch.pipeline().addLast(new WebSocketServerProtocolHandler("/socket.io"));



                    // 空闲检测
                    ch.pipeline().addLast(new IdleStateHandler(20, 0, 0));
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                            if (evt instanceof IdleStateEvent) {
                                IdleStateEvent event = (IdleStateEvent) evt;
                                if (event.state() == IdleState.READER_IDLE) {
                                    log.debug("已经 20s 没有读到数据了 也没有心跳包");

//                                    TODO:关闭通道
//                                    ctx.channel().close();
                                }
                            } else {
                                try {
                                    super.userEventTriggered(ctx, evt);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    ch.pipeline().addLast(AuthHandler);
                    ch.pipeline().addLast(connectionLifecycleHandler);
//                    ch.pipeline().addLast(messageHandler);

//                    ch.pipeline().addLast(new NettyWebSocketHandler());
//                    ch.pipeline().addLast(new MatchHandler());
//
//                    // 其他业务处理器
//                    ch.pipeline().addLast(CHAT_HANDLER);
                }
            });

            Channel channel = serverBootstrap.bind(8091).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
