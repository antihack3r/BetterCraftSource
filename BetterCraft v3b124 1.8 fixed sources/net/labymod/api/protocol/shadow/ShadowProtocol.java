/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.protocol.shadow;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.util.concurrent.atomic.AtomicInteger;
import net.labymod.api.EventManager;
import net.labymod.api.events.PluginMessageEvent;
import net.labymod.api.protocol.shadow.ShadowTransformerIn;
import net.labymod.api.protocol.shadow.ShadowTransformerOut;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;
import net.minecraft.network.PacketBuffer;

public class ShadowProtocol
implements Consumer<ServerData>,
PluginMessageEvent {
    public static final String PM_CHANNEL = "SHADOW";
    public static final int SHADOW_VERSION = 1;
    private boolean shadowSupported = false;
    private AtomicInteger packetCounter = new AtomicInteger(0);

    public ShadowProtocol() {
        EventManager eventManager = LabyMod.getInstance().getEventManager();
        eventManager.registerOnQuit(this);
        eventManager.register(this);
    }

    @Override
    public void accept(ServerData accepted) {
        this.shadowSupported = false;
    }

    @Override
    public void receiveMessage(String channelName, PacketBuffer packetBuffer) {
        if (channelName.equals(PM_CHANNEL)) {
            int packetId = packetBuffer.readInt();
            if (packetId == 0) {
                this.packetCounter.set(0);
            }
            if (packetId == 1) {
                try {
                    Channel channel = LabyMod.getInstance().getNettyChannel();
                    ChannelPipeline pipeline = channel.pipeline();
                    if (pipeline.context("labytransformerin") == null) {
                        pipeline.addAfter("decoder", "labytransformerin", new ShadowTransformerIn(this));
                        pipeline.addAfter("encoder", "labytransformerout", new ShadowTransformerOut(this));
                    }
                    this.shadowSupported = true;
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (packetId == 2) {
                this.shadowSupported = false;
            }
        }
    }

    public void increaseCounter() {
        this.packetCounter.incrementAndGet();
    }

    public boolean isShadowSupported() {
        return this.shadowSupported;
    }

    public AtomicInteger getPacketCounter() {
        return this.packetCounter;
    }
}

