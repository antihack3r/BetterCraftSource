// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.bukkit.platform;

import io.netty.channel.ChannelFuture;
import java.util.List;
import org.bukkit.plugin.PluginDescriptionFile;
import io.netty.channel.ChannelHandler;
import com.viaversion.viaversion.bukkit.handlers.BukkitChannelInitializer;
import com.viaversion.viaversion.platform.WrappedChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import com.viaversion.viaversion.util.ReflectionUtil;
import com.viaversion.viaversion.bukkit.util.NMSUtil;
import java.lang.reflect.Method;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import com.viaversion.viaversion.platform.LegacyViaInjector;

public class BukkitViaInjector extends LegacyViaInjector
{
    private static final boolean HAS_WORLD_VERSION_PROTOCOL_VERSION;
    
    @Override
    public void inject() throws ReflectiveOperationException {
        if (PaperViaInjector.PAPER_INJECTION_METHOD) {
            PaperViaInjector.setPaperChannelInitializeListener();
            return;
        }
        super.inject();
    }
    
    @Override
    public void uninject() throws ReflectiveOperationException {
        if (PaperViaInjector.PAPER_INJECTION_METHOD) {
            PaperViaInjector.removePaperChannelInitializeListener();
            return;
        }
        super.uninject();
    }
    
    @Override
    public int getServerProtocolVersion() throws ReflectiveOperationException {
        if (PaperViaInjector.PAPER_PROTOCOL_METHOD) {
            return Bukkit.getUnsafe().getProtocolVersion();
        }
        return BukkitViaInjector.HAS_WORLD_VERSION_PROTOCOL_VERSION ? this.cursedProtocolDetection() : this.veryCursedProtocolDetection();
    }
    
    private int cursedProtocolDetection() throws ReflectiveOperationException {
        final Class<?> sharedConstantsClass = Class.forName("net.minecraft.SharedConstants");
        final Class<?> worldVersionClass = Class.forName("net.minecraft.WorldVersion");
        Method getWorldVersionMethod = null;
        for (final Method method : sharedConstantsClass.getDeclaredMethods()) {
            if (method.getReturnType() == worldVersionClass && method.getParameterTypes().length == 0) {
                getWorldVersionMethod = method;
                break;
            }
        }
        Preconditions.checkNotNull(getWorldVersionMethod, (Object)"Failed to get world version method");
        final Object worldVersion = getWorldVersionMethod.invoke(null, new Object[0]);
        for (final Method method2 : worldVersionClass.getDeclaredMethods()) {
            if (method2.getReturnType() == Integer.TYPE && method2.getParameterTypes().length == 0) {
                return (int)method2.invoke(worldVersion, new Object[0]);
            }
        }
        throw new IllegalAccessException("Failed to find protocol version method in WorldVersion");
    }
    
    private int veryCursedProtocolDetection() throws ReflectiveOperationException {
        final Class<?> serverClazz = NMSUtil.nms("MinecraftServer", "net.minecraft.server.MinecraftServer");
        final Object server = ReflectionUtil.invokeStatic(serverClazz, "getServer");
        Preconditions.checkNotNull(server, (Object)"Failed to get server instance");
        final Class<?> pingClazz = NMSUtil.nms("ServerPing", "net.minecraft.network.protocol.status.ServerPing");
        Object ping = null;
        for (final Field field : serverClazz.getDeclaredFields()) {
            if (field.getType() == pingClazz) {
                field.setAccessible(true);
                ping = field.get(server);
                break;
            }
        }
        Preconditions.checkNotNull(ping, (Object)"Failed to get server ping");
        final Class<?> serverDataClass = NMSUtil.nms("ServerPing$ServerData", "net.minecraft.network.protocol.status.ServerPing$ServerData");
        Object serverData = null;
        for (final Field field2 : pingClazz.getDeclaredFields()) {
            if (field2.getType() == serverDataClass) {
                field2.setAccessible(true);
                serverData = field2.get(ping);
                break;
            }
        }
        Preconditions.checkNotNull(serverData, (Object)"Failed to get server data");
        for (final Field field2 : serverDataClass.getDeclaredFields()) {
            if (field2.getType() == Integer.TYPE) {
                field2.setAccessible(true);
                final int protocolVersion = (int)field2.get(serverData);
                if (protocolVersion != -1) {
                    return protocolVersion;
                }
            }
        }
        throw new RuntimeException("Failed to get server");
    }
    
    @Override
    protected Object getServerConnection() throws ReflectiveOperationException {
        final Class<?> serverClass = NMSUtil.nms("MinecraftServer", "net.minecraft.server.MinecraftServer");
        final Class<?> connectionClass = NMSUtil.nms("ServerConnection", "net.minecraft.server.network.ServerConnection");
        final Object server = ReflectionUtil.invokeStatic(serverClass, "getServer");
        for (final Method method : serverClass.getDeclaredMethods()) {
            if (method.getReturnType() == connectionClass) {
                if (method.getParameterTypes().length == 0) {
                    final Object connection = method.invoke(server, new Object[0]);
                    if (connection != null) {
                        return connection;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    protected WrappedChannelInitializer createChannelInitializer(final ChannelInitializer<Channel> oldInitializer) {
        return new BukkitChannelInitializer(oldInitializer);
    }
    
    @Override
    protected void blame(final ChannelHandler bootstrapAcceptor) throws ReflectiveOperationException {
        final ClassLoader classLoader = bootstrapAcceptor.getClass().getClassLoader();
        if (classLoader.getClass().getName().equals("org.bukkit.plugin.java.PluginClassLoader")) {
            final PluginDescriptionFile description = ReflectionUtil.get(classLoader, "description", PluginDescriptionFile.class);
            throw new RuntimeException("Unable to inject, due to " + bootstrapAcceptor.getClass().getName() + ", try without the plugin " + description.getName() + "?");
        }
        throw new RuntimeException("Unable to find core component 'childHandler', please check your plugins. issue: " + bootstrapAcceptor.getClass().getName());
    }
    
    @Override
    public boolean lateProtocolVersionSetting() {
        return !PaperViaInjector.PAPER_PROTOCOL_METHOD && !BukkitViaInjector.HAS_WORLD_VERSION_PROTOCOL_VERSION;
    }
    
    public boolean isBinded() {
        if (PaperViaInjector.PAPER_INJECTION_METHOD) {
            return true;
        }
        try {
            final Object connection = this.getServerConnection();
            if (connection == null) {
                return false;
            }
            for (final Field field : connection.getClass().getDeclaredFields()) {
                if (List.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    final List<?> value = (List<?>)field.get(connection);
                    synchronized (value) {
                        if (!value.isEmpty() && value.get(0) instanceof ChannelFuture) {
                            return true;
                        }
                    }
                }
            }
        }
        catch (final ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    static {
        HAS_WORLD_VERSION_PROTOCOL_VERSION = (PaperViaInjector.hasClass("net.minecraft.SharedConstants") && PaperViaInjector.hasClass("net.minecraft.WorldVersion") && !PaperViaInjector.hasClass("com.mojang.bridge.game.GameVersion"));
    }
}
