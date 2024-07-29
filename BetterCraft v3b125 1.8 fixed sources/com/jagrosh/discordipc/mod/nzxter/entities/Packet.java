/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities;

import com.google.gson.JsonObject;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class Packet {
    private final OpCode op;
    private final JsonObject data;
    private final String encoding;

    public Packet(OpCode op2, JsonObject data, String encoding) {
        this.op = op2;
        this.data = data;
        this.encoding = encoding;
    }

    @Deprecated
    public Packet(OpCode op2, JsonObject data) {
        this(op2, data, "UTF-8");
    }

    public byte[] toBytes() {
        byte[] d2;
        String s2 = this.data.toString();
        try {
            d2 = s2.getBytes(this.encoding);
        }
        catch (UnsupportedEncodingException e2) {
            d2 = s2.getBytes();
        }
        ByteBuffer packet = ByteBuffer.allocate(d2.length + 8);
        packet.putInt(Integer.reverseBytes(this.op.ordinal()));
        packet.putInt(Integer.reverseBytes(d2.length));
        packet.put(d2);
        return packet.array();
    }

    public OpCode getOp() {
        return this.op;
    }

    public JsonObject getJson() {
        return this.data;
    }

    public String toString() {
        return "Pkt:" + (Object)((Object)this.getOp()) + this.getJson().toString();
    }

    public String toDecodedString() {
        try {
            return "Pkt:" + (Object)((Object)this.getOp()) + new String(this.getJson().toString().getBytes(this.encoding));
        }
        catch (UnsupportedEncodingException e2) {
            return "Pkt:" + (Object)((Object)this.getOp()) + this.getJson().toString();
        }
    }

    public static enum OpCode {
        HANDSHAKE,
        FRAME,
        CLOSE,
        PING,
        PONG;

    }
}

