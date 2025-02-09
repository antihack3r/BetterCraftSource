/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.entities;

import java.nio.ByteBuffer;
import org.json.JSONObject;

public class Packet {
    private final OpCode op;
    private final JSONObject data;

    public Packet(OpCode op2, JSONObject data) {
        this.op = op2;
        this.data = data;
    }

    public byte[] toBytes() {
        byte[] d2 = this.data.toString().getBytes();
        ByteBuffer packet = ByteBuffer.allocate(d2.length + 8);
        packet.putInt(Integer.reverseBytes(this.op.ordinal()));
        packet.putInt(Integer.reverseBytes(d2.length));
        packet.put(d2);
        return packet.array();
    }

    public OpCode getOp() {
        return this.op;
    }

    public JSONObject getJson() {
        return this.data;
    }

    public String toString() {
        return "Pkt:" + (Object)((Object)this.getOp()) + this.getJson().toString();
    }

    public static enum OpCode {
        HANDSHAKE,
        FRAME,
        CLOSE,
        PING,
        PONG;

    }
}

