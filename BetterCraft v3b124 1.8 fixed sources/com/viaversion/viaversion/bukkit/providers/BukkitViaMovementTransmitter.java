/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package com.viaversion.viaversion.bukkit.providers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.bukkit.util.NMSUtil;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.MovementTracker;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitViaMovementTransmitter
extends MovementTransmitterProvider {
    private static boolean USE_NMS = true;
    private Object idlePacket;
    private Object idlePacket2;
    private Method getHandle;
    private Field connection;
    private Method handleFlying;

    public BukkitViaMovementTransmitter() {
        Class<?> idlePacketClass;
        USE_NMS = Via.getConfig().isNMSPlayerTicking();
        try {
            idlePacketClass = NMSUtil.nms("PacketPlayInFlying");
        }
        catch (ClassNotFoundException e2) {
            return;
        }
        try {
            this.idlePacket = idlePacketClass.newInstance();
            this.idlePacket2 = idlePacketClass.newInstance();
            Field flying = idlePacketClass.getDeclaredField("f");
            flying.setAccessible(true);
            flying.set(this.idlePacket2, true);
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchFieldException e3) {
            throw new RuntimeException("Couldn't make player idle packet, help!", e3);
        }
        if (USE_NMS) {
            try {
                this.getHandle = NMSUtil.obc("entity.CraftPlayer").getDeclaredMethod("getHandle", new Class[0]);
            }
            catch (ClassNotFoundException | NoSuchMethodException e4) {
                throw new RuntimeException("Couldn't find CraftPlayer", e4);
            }
            try {
                this.connection = NMSUtil.nms("EntityPlayer").getDeclaredField("playerConnection");
            }
            catch (ClassNotFoundException | NoSuchFieldException e5) {
                throw new RuntimeException("Couldn't find Player Connection", e5);
            }
            try {
                this.handleFlying = NMSUtil.nms("PlayerConnection").getDeclaredMethod("a", idlePacketClass);
            }
            catch (ClassNotFoundException | NoSuchMethodException e6) {
                throw new RuntimeException("Couldn't find CraftPlayer", e6);
            }
        }
    }

    public Object getFlyingPacket() {
        if (this.idlePacket == null) {
            throw new NullPointerException("Could not locate flying packet");
        }
        return this.idlePacket;
    }

    public Object getGroundPacket() {
        if (this.idlePacket == null) {
            throw new NullPointerException("Could not locate flying packet");
        }
        return this.idlePacket2;
    }

    @Override
    public void sendPlayer(UserConnection info) {
        if (USE_NMS) {
            Player player = Bukkit.getPlayer((UUID)info.getProtocolInfo().getUuid());
            if (player != null) {
                try {
                    Object entityPlayer = this.getHandle.invoke((Object)player, new Object[0]);
                    Object pc2 = this.connection.get(entityPlayer);
                    if (pc2 != null) {
                        this.handleFlying.invoke(pc2, info.get(MovementTracker.class).isGround() ? this.idlePacket2 : this.idlePacket);
                        info.get(MovementTracker.class).incrementIdlePacket();
                    }
                }
                catch (IllegalAccessException | InvocationTargetException e2) {
                    e2.printStackTrace();
                }
            }
        } else {
            ChannelHandlerContext context = PipelineUtil.getContextBefore("decoder", info.getChannel().pipeline());
            if (context != null) {
                if (info.get(MovementTracker.class).isGround()) {
                    context.fireChannelRead(this.getGroundPacket());
                } else {
                    context.fireChannelRead(this.getFlyingPacket());
                }
                info.get(MovementTracker.class).incrementIdlePacket();
            }
        }
    }
}

