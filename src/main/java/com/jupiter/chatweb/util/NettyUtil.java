package com.jupiter.chatweb.util;

import com.jupiter.chatweb.chat.message.*;
import com.jupiter.chatweb.chat.protocol.MessageCodecSharable;
import com.jupiter.chatweb.chat.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NettyUtil {
    public  static void connect(String username){
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        AtomicBoolean EXIT = new AtomicBoolean(false);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(MESSAGE_CODEC);


                    //  TODO:NOTICE   放在登录 消息解码器 之后 判断有没有数据（客户端 写空闲）
                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 8s 内如果没有发数据，会触发一个 IdleState#WRITE_IDLE 事件
                    //客户端事件要比服务器短 一般都是服务器的1/2
//                    第一个参数读 第二个参数写
                    ch.pipeline().addLast(new IdleStateHandler
                            (0,
                                    10, 0));
//                     ChannelDuplexHandler 可以同时作为入站和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // TODO:NOTICE 触发了写空闲事件 发送心跳包
                            if (event.state() == IdleState.WRITER_IDLE) {
                                log.debug("已经 10s 没有写数据了 发送心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });



                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        // 接收响应消息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                            if ((msg instanceof LoginResponseMessage)) {

                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {

                                }

                            }

//                          TODO:NOTICE   收到的信息
                            log.info("来信息啦: {}", msg);
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(() -> {
                            }, "system in").start();
                        }

                        //                         在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已经断开，按任意键退出..");
                            EXIT.set(true);
                        }

                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                            EXIT.set(true);
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8091).sync().channel();
            channel.closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
