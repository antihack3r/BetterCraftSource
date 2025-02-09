/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.protocol.shadow;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.beans.ConstructorProperties;
import net.labymod.api.protocol.shadow.ShadowProtocol;

public class ShadowTransformerIn
extends ChannelInboundHandlerAdapter {
    private ShadowProtocol shadow;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!this.shadow.isShadowSupported()) {
            super.channelRead(ctx, msg);
            return;
        }
        this.shadow.increaseCounter();
        super.channelRead(ctx, msg);
    }

    @ConstructorProperties(value={"shadow"})
    public ShadowTransformerIn(ShadowProtocol shadow) {
        this.shadow = shadow;
    }
}

