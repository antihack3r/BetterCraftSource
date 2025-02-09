/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.bungee.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BungeePipelineUtil {
    private static final Method DECODE_METHOD;
    private static final Method ENCODE_METHOD;

    public static List<Object> callDecode(MessageToMessageDecoder decoder, ChannelHandlerContext ctx, ByteBuf input) throws InvocationTargetException {
        ArrayList<Object> output = new ArrayList<Object>();
        try {
            DECODE_METHOD.invoke((Object)decoder, ctx, input, output);
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return output;
    }

    public static ByteBuf callEncode(MessageToByteEncoder encoder, ChannelHandlerContext ctx, ByteBuf input) throws InvocationTargetException {
        ByteBuf output = ctx.alloc().buffer();
        try {
            ENCODE_METHOD.invoke((Object)encoder, ctx, input, output);
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return output;
    }

    public static ByteBuf decompress(ChannelHandlerContext ctx, ByteBuf bytebuf) {
        try {
            return (ByteBuf)BungeePipelineUtil.callDecode((MessageToMessageDecoder)ctx.pipeline().get("decompress"), ctx.pipeline().context("decompress"), bytebuf).get(0);
        }
        catch (InvocationTargetException e2) {
            e2.printStackTrace();
            return ctx.alloc().buffer();
        }
    }

    public static ByteBuf compress(ChannelHandlerContext ctx, ByteBuf bytebuf) {
        try {
            return BungeePipelineUtil.callEncode((MessageToByteEncoder)ctx.pipeline().get("compress"), ctx.pipeline().context("compress"), bytebuf);
        }
        catch (InvocationTargetException e2) {
            e2.printStackTrace();
            return ctx.alloc().buffer();
        }
    }

    static {
        try {
            DECODE_METHOD = MessageToMessageDecoder.class.getDeclaredMethod("decode", ChannelHandlerContext.class, Object.class, List.class);
            DECODE_METHOD.setAccessible(true);
            ENCODE_METHOD = MessageToByteEncoder.class.getDeclaredMethod("encode", ChannelHandlerContext.class, Object.class, ByteBuf.class);
            ENCODE_METHOD.setAccessible(true);
        }
        catch (NoSuchMethodException e2) {
            throw new RuntimeException(e2);
        }
    }
}

