/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.protocol.shadow;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.beans.ConstructorProperties;
import net.labymod.api.protocol.shadow.ShadowProtocol;
import net.labymod.core.LabyModCore;

public class ShadowTransformerOut
extends ChannelOutboundHandlerAdapter {
    private ShadowProtocol shadow;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!this.shadow.isShadowSupported()) {
            super.write(ctx, msg, promise);
            return;
        }
        if (LabyModCore.getCoreAdapter().getProtocolAdapter().handleOutgoingPacket(msg, this.shadow)) {
            return;
        }
        super.write(ctx, msg, promise);
    }

    @ConstructorProperties(value={"shadow"})
    public ShadowTransformerOut(ShadowProtocol shadow) {
        this.shadow = shadow;
    }
}

