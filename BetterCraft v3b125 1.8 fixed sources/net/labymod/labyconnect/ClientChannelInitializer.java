/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import net.labymod.labyconnect.ClientConnection;
import net.labymod.labyconnect.handling.PacketDecoder;
import net.labymod.labyconnect.handling.PacketEncoder;
import net.labymod.labyconnect.handling.PacketPrepender;
import net.labymod.labyconnect.handling.PacketSplitter;

public class ClientChannelInitializer
extends ChannelInitializer<NioSocketChannel> {
    private ClientConnection clientConnection;

    public ClientChannelInitializer(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        this.clientConnection.setNioSocketChannel(channel);
        channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(120L, TimeUnit.SECONDS)).addLast("splitter", (ChannelHandler)new PacketPrepender()).addLast("decoder", (ChannelHandler)new PacketDecoder()).addLast("prepender", (ChannelHandler)new PacketSplitter()).addLast("encoder", (ChannelHandler)new PacketEncoder()).addLast(this.getClientConnection());
    }

    public ClientConnection getClientConnection() {
        return this.clientConnection;
    }
}

