package com.jupiter.chatweb.chat.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {

    //                协议规定了 12个字节 4个字节length 然后是内容
//                maxFrameLength：最大允许的帧长度（1024）。
//                lengthFieldOffset：长度字段的偏移量（12字节）。
//                lengthFieldLength：长度字段的字节数（4字节）。
//                lengthAdjustment：长度字段的矫正值（0）。
//                initialBytesToStrip：解码后跳过的字节数（0）。
    public ProcotolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
