/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.serverpinger;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.labymod.core.ServerPingerData;
import net.labymod.utils.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public class ServerPinger {
    private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
    private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(final Consumer<ServerPingerData> doneCallback, final ServerPingerData server) throws Throwable {
        ServerAddress serveraddress = ServerAddress.fromString(server.getIpAddress());
        NetworkManager networkmanager = null;
        try {
            networkmanager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
        }
        catch (UnknownHostException e2) {
            doneCallback.accept(null);
        }
        this.pingDestinations.add(networkmanager);
        final NetworkManager finalNetworkManager = networkmanager;
        networkmanager.setNetHandler(new INetHandlerStatusClient(){
            private boolean successful;
            private boolean receivedStatus;
            private long pinged;

            @Override
            public void handleServerInfo(S00PacketServerInfo packetIn) {
                if (this.receivedStatus) {
                    finalNetworkManager.closeChannel(new ChatComponentText("Received unrequested status"));
                } else {
                    String s2;
                    this.receivedStatus = true;
                    ServerStatusResponse serverstatusresponse = packetIn.getResponse();
                    server.setPingToServer(System.currentTimeMillis() - server.getTimePinged());
                    if (serverstatusresponse.getServerDescription() != null) {
                        server.setMotd(serverstatusresponse.getServerDescription().getFormattedText());
                    } else {
                        server.setMotd("");
                    }
                    if (serverstatusresponse.getPlayerCountData() != null) {
                        server.setCurrentPlayers(serverstatusresponse.getPlayerCountData().getOnlinePlayerCount());
                        server.setMaxPlayers(serverstatusresponse.getPlayerCountData().getMaxPlayers());
                        if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayerCountData().getPlayers())) {
                            StringBuilder stringbuilder = new StringBuilder();
                            GameProfile[] gameProfileArray = serverstatusresponse.getPlayerCountData().getPlayers();
                            int n2 = gameProfileArray.length;
                            int n3 = 0;
                            while (n3 < n2) {
                                GameProfile gameprofile = gameProfileArray[n3];
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }
                                stringbuilder.append(gameprofile.getName());
                                ++n3;
                            }
                            if (serverstatusresponse.getPlayerCountData().getPlayers().length < serverstatusresponse.getPlayerCountData().getOnlinePlayerCount()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }
                                stringbuilder.append("... and ").append(serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() - serverstatusresponse.getPlayerCountData().getPlayers().length).append(" more ...");
                            }
                            server.setPlayerList(stringbuilder.toString());
                        }
                    }
                    if (serverstatusresponse.getFavicon() != null && (s2 = serverstatusresponse.getFavicon()).startsWith("data:image/png;base64,")) {
                        server.setBase64EncodedIconData(s2.substring("data:image/png;base64,".length()));
                    }
                    if (serverstatusresponse.getProtocolVersionInfo() != null) {
                        server.setGameVersion(serverstatusresponse.getProtocolVersionInfo().getName());
                        server.setVersion(serverstatusresponse.getProtocolVersionInfo().getProtocol());
                    } else {
                        server.setGameVersion("?");
                    }
                    this.successful = true;
                    this.pinged = Minecraft.getSystemTime();
                    finalNetworkManager.sendPacket(new C01PacketPing(this.pinged));
                }
            }

            @Override
            public void handlePong(S01PacketPong packetIn) {
                long i2 = this.pinged;
                long j2 = Minecraft.getSystemTime();
                server.setPingToServer(j2 - i2);
                doneCallback.accept(server);
                finalNetworkManager.closeChannel(new ChatComponentText("Finished"));
            }

            @Override
            public void onDisconnect(IChatComponent reason) {
                if (!this.successful) {
                    try {
                        ServerPinger.this.tryCompatibilityPing(doneCallback, server);
                    }
                    catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }
        });
        try {
            networkmanager.sendPacket(new C00Handshake(47, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS));
            networkmanager.sendPacket(new C00PacketServerQuery());
        }
        catch (Throwable throwable) {
            doneCallback.accept(null);
            throwable.printStackTrace();
        }
    }

    private void tryCompatibilityPing(final Consumer<ServerPingerData> doneCallback, final ServerPingerData server) {
        final ServerAddress serveraddress = ServerAddress.fromString(server.getIpAddress());
        if (serveraddress == null) {
            return;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue())).handler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel p_initChannel_1_) throws Exception {
                try {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                p_initChannel_1_.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>(){

                    @Override
                    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
                        super.channelActive(p_channelActive_1_);
                        ByteBuf bytebuf = Unpooled.buffer();
                        try {
                            bytebuf.writeByte(254);
                            bytebuf.writeByte(1);
                            bytebuf.writeByte(250);
                            char[] achar = "MC|PingHost".toCharArray();
                            bytebuf.writeShort(achar.length);
                            char[] cArray = achar;
                            int n2 = achar.length;
                            int n3 = 0;
                            while (n3 < n2) {
                                char c0 = cArray[n3];
                                bytebuf.writeChar(c0);
                                ++n3;
                            }
                            bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
                            bytebuf.writeByte(127);
                            achar = serveraddress.getIP().toCharArray();
                            bytebuf.writeShort(achar.length);
                            cArray = achar;
                            n2 = achar.length;
                            n3 = 0;
                            while (n3 < n2) {
                                char c2 = cArray[n3];
                                bytebuf.writeChar(c2);
                                ++n3;
                            }
                            bytebuf.writeInt(serveraddress.getPort());
                            p_channelActive_1_.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        }
                        finally {
                            bytebuf.release();
                        }
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, ByteBuf p_channelRead0_2_) throws Exception {
                        short short1 = p_channelRead0_2_.readUnsignedByte();
                        boolean success = false;
                        if (short1 == 255) {
                            String s2 = new String(p_channelRead0_2_.readBytes(p_channelRead0_2_.readShort() * 2).array(), Charsets.UTF_16BE);
                            String[] astring = Iterables.toArray(PING_RESPONSE_SPLITTER.split(s2), String.class);
                            if ("\u00a71".equals(astring[0])) {
                                String s22 = astring[3];
                                int j2 = MathHelper.parseIntWithDefault(astring[4], -1);
                                int k2 = MathHelper.parseIntWithDefault(astring[5], -1);
                                server.setMotd(s22);
                                server.setCurrentPlayers(j2);
                                server.setMaxPlayers(k2);
                                success = true;
                                doneCallback.accept(server);
                            }
                        }
                        if (!success) {
                            doneCallback.accept(null);
                        }
                        p_channelRead0_1_.close();
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception {
                        p_exceptionCaught_1_.close();
                        doneCallback.accept(null);
                    }
                });
            }
        })).channel(NioSocketChannel.class)).connect(serveraddress.getIP(), serveraddress.getPort());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pingPendingNetworks() {
        List<NetworkManager> list = this.pingDestinations;
        synchronized (list) {
            Iterator<NetworkManager> iterator = this.pingDestinations.iterator();
            while (iterator.hasNext()) {
                NetworkManager networkmanager = iterator.next();
                try {
                    if (networkmanager == null) continue;
                    if (networkmanager.isChannelOpen()) {
                        networkmanager.processReceivedPackets();
                        continue;
                    }
                    iterator.remove();
                    networkmanager.checkDisconnected();
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearPendingNetworks() {
        List<NetworkManager> list = this.pingDestinations;
        synchronized (list) {
            Iterator<NetworkManager> iterator = this.pingDestinations.iterator();
            while (iterator.hasNext()) {
                NetworkManager networkmanager = iterator.next();
                if (networkmanager == null || !networkmanager.isChannelOpen()) continue;
                iterator.remove();
                networkmanager.closeChannel(new ChatComponentText("Cancelled"));
            }
        }
    }
}

