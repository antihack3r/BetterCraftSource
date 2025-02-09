/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyPingFrame;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyPingFrame
implements SpdyPingFrame {
    private int id;

    public DefaultSpdyPingFrame(int id2) {
        this.setId(id2);
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public SpdyPingFrame setId(int id2) {
        this.id = id2;
        return this;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(StringUtil.simpleClassName(this));
        buf.append(StringUtil.NEWLINE);
        buf.append("--> ID = ");
        buf.append(this.id());
        return buf.toString();
    }
}

