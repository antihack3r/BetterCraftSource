/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PipelineUtil {
    private static final Method DECODE_METHOD;
    private static final Method ENCODE_METHOD;
    private static final Method MTM_DECODE;

    public static List<Object> callDecode(ByteToMessageDecoder decoder, ChannelHandlerContext ctx, Object input) throws InvocationTargetException {
        ArrayList<Object> output = new ArrayList<Object>();
        try {
            DECODE_METHOD.invoke((Object)decoder, ctx, input, output);
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return output;
    }

    public static void callEncode(MessageToByteEncoder encoder, ChannelHandlerContext ctx, Object msg, ByteBuf output) throws InvocationTargetException {
        try {
            ENCODE_METHOD.invoke((Object)encoder, ctx, msg, output);
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    public static List<Object> callDecode(MessageToMessageDecoder decoder, ChannelHandlerContext ctx, Object msg) throws InvocationTargetException {
        ArrayList<Object> output = new ArrayList<Object>();
        try {
            MTM_DECODE.invoke((Object)decoder, ctx, msg, output);
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return output;
    }

    public static boolean containsCause(Throwable t2, Class<?> c2) {
        while (t2 != null) {
            if (c2.isAssignableFrom(t2.getClass())) {
                return true;
            }
            t2 = t2.getCause();
        }
        return false;
    }

    public static <T> @Nullable T getCause(Throwable t2, Class<T> c2) {
        while (t2 != null) {
            if (c2.isAssignableFrom(t2.getClass())) {
                return (T)t2;
            }
            t2 = t2.getCause();
        }
        return null;
    }

    public static ChannelHandlerContext getContextBefore(String name, ChannelPipeline pipeline) {
        boolean mark = false;
        for (String s2 : pipeline.names()) {
            if (mark) {
                return pipeline.context(pipeline.get(s2));
            }
            if (!s2.equalsIgnoreCase(name)) continue;
            mark = true;
        }
        return null;
    }

    public static ChannelHandlerContext getPreviousContext(String name, ChannelPipeline pipeline) {
        String previous = null;
        for (String entry : pipeline.toMap().keySet()) {
            if (entry.equals(name)) {
                return pipeline.context(previous);
            }
            previous = entry;
        }
        return null;
    }

    static {
        try {
            DECODE_METHOD = ByteToMessageDecoder.class.getDeclaredMethod("decode", ChannelHandlerContext.class, ByteBuf.class, List.class);
            DECODE_METHOD.setAccessible(true);
            ENCODE_METHOD = MessageToByteEncoder.class.getDeclaredMethod("encode", ChannelHandlerContext.class, Object.class, ByteBuf.class);
            ENCODE_METHOD.setAccessible(true);
            MTM_DECODE = MessageToMessageDecoder.class.getDeclaredMethod("decode", ChannelHandlerContext.class, Object.class, List.class);
            MTM_DECODE.setAccessible(true);
        }
        catch (NoSuchMethodException e2) {
            throw new RuntimeException(e2);
        }
    }
}

