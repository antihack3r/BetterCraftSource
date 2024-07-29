/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Proxy;
import java.nio.channels.UnresolvedAddressException;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.labyconnect.ClientChannelInitializer;
import net.labymod.labyconnect.GameIconHelper;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyconnect.gui.GuiFriendsAddFriend;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.log.MessageChatComponent;
import net.labymod.labyconnect.log.SingleChat;
import net.labymod.labyconnect.packets.CryptManager;
import net.labymod.labyconnect.packets.EnumConnectionState;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketAddonMessage;
import net.labymod.labyconnect.packets.PacketBanned;
import net.labymod.labyconnect.packets.PacketChatVisibilityChange;
import net.labymod.labyconnect.packets.PacketDisconnect;
import net.labymod.labyconnect.packets.PacketEncryptionRequest;
import net.labymod.labyconnect.packets.PacketEncryptionResponse;
import net.labymod.labyconnect.packets.PacketHelloPing;
import net.labymod.labyconnect.packets.PacketHelloPong;
import net.labymod.labyconnect.packets.PacketKick;
import net.labymod.labyconnect.packets.PacketLoginComplete;
import net.labymod.labyconnect.packets.PacketLoginData;
import net.labymod.labyconnect.packets.PacketLoginFriend;
import net.labymod.labyconnect.packets.PacketLoginOptions;
import net.labymod.labyconnect.packets.PacketLoginRequest;
import net.labymod.labyconnect.packets.PacketLoginTime;
import net.labymod.labyconnect.packets.PacketLoginVersion;
import net.labymod.labyconnect.packets.PacketMessage;
import net.labymod.labyconnect.packets.PacketMojangStatus;
import net.labymod.labyconnect.packets.PacketPing;
import net.labymod.labyconnect.packets.PacketPlayChangeOptions;
import net.labymod.labyconnect.packets.PacketPlayDenyFriendRequest;
import net.labymod.labyconnect.packets.PacketPlayFriendPlayingOn;
import net.labymod.labyconnect.packets.PacketPlayFriendRemove;
import net.labymod.labyconnect.packets.PacketPlayFriendStatus;
import net.labymod.labyconnect.packets.PacketPlayPlayerOnline;
import net.labymod.labyconnect.packets.PacketPlayRequestAddFriend;
import net.labymod.labyconnect.packets.PacketPlayRequestAddFriendResponse;
import net.labymod.labyconnect.packets.PacketPlayRequestRemove;
import net.labymod.labyconnect.packets.PacketPlayServerStatus;
import net.labymod.labyconnect.packets.PacketPlayServerStatusUpdate;
import net.labymod.labyconnect.packets.PacketPlayTyping;
import net.labymod.labyconnect.packets.PacketPong;
import net.labymod.labyconnect.packets.PacketServerMessage;
import net.labymod.labyconnect.packets.PacketUpdateCosmetics;
import net.labymod.labyconnect.packets.PacketUserBadge;
import net.labymod.labyconnect.user.ChatRequest;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.main.Updater;
import net.labymod.main.lang.LanguageManager;
import net.labymod.support.util.Debug;
import net.labymod.user.FamiliarManager;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.user.group.LabyGroup;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;

@ChannelHandler.Sharable
public class ClientConnection
extends PacketHandler {
    private NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Chat#%d").build());
    private ExecutorService executorService = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder().setNameFormat("Helper#%d").build());
    private NioSocketChannel nioSocketChannel;
    private Bootstrap bootstrap;
    private EnumConnectionState currentConnectionState = EnumConnectionState.OFFLINE;
    private LabyConnect chatClient;
    private String lastKickMessage = "Unknown";
    private String customIp = null;
    private int customPort = -1;
    private boolean pinAvailable = false;
    private Consumer<String> pinResponseConsumer = null;

    public ClientConnection(LabyConnect chatClient) {
        this.chatClient = chatClient;
        if (Debug.isActive()) {
            LabyMod.getInstance().getEventManager().register(new MessageSendEvent(){

                @Override
                public boolean onSend(String message) {
                    if (!message.toLowerCase().startsWith("/labymodchatdebug")) {
                        return false;
                    }
                    if (!message.toLowerCase().contains(" ")) {
                        LabyMod.getInstance().getLabyModAPI().displayMessageInChat(String.valueOf(ModColor.cl("c")) + "/labymodchatdebug ip:port");
                        return false;
                    }
                    try {
                        String ip2 = message.toLowerCase().split(" ")[1];
                        int port = 25565;
                        if (ip2.contains(":")) {
                            String[] split = ip2.split(":");
                            ip2 = split[0];
                            port = Integer.parseInt(split[1]);
                        }
                        LabyMod.getInstance().getLabyModAPI().displayMessageInChat(String.valueOf(ModColor.cl("a")) + "Connecting to " + ModColor.cl("f") + ip2 + ModColor.cl("a") + " on port " + ModColor.cl("f") + port);
                        ClientConnection.this.customIp = ip2;
                        ClientConnection.this.customPort = port;
                        ClientConnection.this.connect();
                    }
                    catch (Exception error) {
                        LabyMod.getInstance().getLabyModAPI().displayMessageInChat(String.valueOf(ModColor.cl("c")) + "Error: " + error.getMessage());
                    }
                    return true;
                }
            });
        }
    }

    public void connect() {
        String defaultIp = "mod.labymod.net";
        int defaultPort = 30336;
        String customPort = System.getProperty("customChatPort");
        if (customPort != null) {
            defaultPort = Integer.parseInt(customPort);
        }
        if (this.customIp != null) {
            defaultIp = this.customIp;
        }
        if (this.customPort != -1) {
            defaultPort = this.customPort;
        }
        this.connect(defaultIp, defaultPort);
    }

    public void connect(final String ip2, final int port) {
        if (this.nioSocketChannel != null && this.nioSocketChannel.isOpen()) {
            this.nioSocketChannel.close();
            this.nioSocketChannel = null;
        }
        this.chatClient.setForcedLogout(false);
        this.chatClient.getChatlogManager().loadChatlogs(LabyMod.getInstance().getPlayerUUID());
        this.updateConnectionState(EnumConnectionState.HELLO);
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.nioEventLoopGroup);
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.handler(new ClientChannelInitializer(this));
        this.executorService.execute(new Runnable(){

            @Override
            public void run() {
                block3: {
                    try {
                        Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "Connecting to " + ip2 + ":" + port);
                        ClientConnection.this.bootstrap.connect(ip2, port).syncUninterruptibly();
                        ClientConnection.this.sendPacket(new PacketHelloPing(System.currentTimeMillis()));
                    }
                    catch (UnresolvedAddressException error) {
                        ClientConnection.this.updateConnectionState(EnumConnectionState.OFFLINE);
                        ClientConnection.this.lastKickMessage = error.getMessage() == null ? "Unknown error" : error.getMessage();
                        Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "UnresolvedAddressException: " + error.getMessage());
                        error.printStackTrace();
                    }
                    catch (Throwable throwable) {
                        ClientConnection.this.updateConnectionState(EnumConnectionState.OFFLINE);
                        ClientConnection.this.lastKickMessage = throwable.getMessage() == null ? "Unknown error" : throwable.getMessage();
                        Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "Throwable: " + throwable.getMessage());
                        throwable.printStackTrace();
                        if (!ClientConnection.this.lastKickMessage.contains("no further information") && throwable.getMessage() != null) break block3;
                        ClientConnection.this.lastKickMessage = LanguageManager.translate("chat_not_reachable");
                    }
                }
            }
        });
    }

    public void disconnect(final boolean kicked) {
        if (this.currentConnectionState == EnumConnectionState.OFFLINE) {
            return;
        }
        this.updateConnectionState(EnumConnectionState.OFFLINE);
        LabyMod.getInstance().getUserManager().getFamiliarManager().clear();
        this.executorService.execute(new Runnable(){

            @Override
            public void run() {
                ClientConnection.this.chatClient.getChatlogManager().saveChatlogs(LabyMod.getInstance().getPlayerUUID());
                if (ClientConnection.this.nioSocketChannel != null && !kicked) {
                    ClientConnection.this.nioSocketChannel.writeAndFlush(new PacketDisconnect("Logout")).addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture arg0) throws Exception {
                            if (ClientConnection.this.nioSocketChannel != null) {
                                ClientConnection.this.nioSocketChannel.close();
                            }
                        }
                    });
                }
            }
        });
    }

    public void updateConnectionState(EnumConnectionState connectionState) {
        this.currentConnectionState = connectionState;
    }

    public void requestPin(Consumer<String> consumer) {
        this.pinResponseConsumer = consumer;
        this.sendPacket(new PacketAddonMessage("auth_pin", new JsonObject().toString()));
    }

    @Override
    public void handle(PacketLoginData packet) {
    }

    @Override
    public void handle(PacketHelloPing packet) {
    }

    @Override
    public void handle(PacketHelloPong packet) {
        this.updateConnectionState(EnumConnectionState.LOGIN);
        this.sendPacket(new PacketLoginData(LabyMod.getInstance().getPlayerUUID(), LabyMod.getInstance().getPlayerName(), LabyMod.getSettings().motd));
        this.sendPacket(new PacketLoginOptions(LabyMod.getSettings().showConnectedIp, this.chatClient.getClientProfile().getUserStatus(), this.chatClient.getClientProfile().getTimeZone()));
        this.sendPacket(new PacketLoginVersion(23, String.valueOf(Source.ABOUT_MC_VERSION) + "_" + "3.6.6"));
        JsonArray addons = new JsonArray();
        for (LabyModAddon addonInfo : AddonLoader.getAddons()) {
            if (addonInfo.about == null || addonInfo.about.uuid == null) continue;
            JsonObject entry = new JsonObject();
            entry.addProperty("uuid", addonInfo.about.uuid.toString());
            entry.addProperty("name", addonInfo.about.name);
            addons.add(entry);
        }
        JsonObject obj = new JsonObject();
        obj.add("addons", addons);
        this.sendPacket(new PacketAddonMessage("labymod_addons", obj.toString()));
        this.chatClient.getFriends().clear();
        this.chatClient.getRequests().clear();
    }

    @Override
    public void handle(PacketPlayPlayerOnline packet) {
        ChatUser chatUser = this.chatClient.getChatUserByUUID(packet.getPlayer().getGameProfile().getId());
        chatUser.setStatus(packet.getPlayer().getStatus());
        chatUser.setStatusMessage(packet.getPlayer().getStatusMessage());
        if (LabyMod.getSettings().alertsOnlineStatus) {
            if (packet.getPlayer().isOnline()) {
                LabyMod.getInstance().notifyMessageProfile(packet.getPlayer().getGameProfile(), String.valueOf(ModColor.cl("a")) + LanguageManager.translate("chat_user_now_online"));
            } else {
                LabyMod.getInstance().notifyMessageProfile(packet.getPlayer().getGameProfile(), String.valueOf(ModColor.cl("c")) + LanguageManager.translate("chat_user_now_offline"));
            }
        }
        this.chatClient.sortFriendList(LabyMod.getSettings().friendSortType);
    }

    @Override
    public void handle(PacketLoginComplete packet) {
        this.updateConnectionState(EnumConnectionState.PLAY);
        this.pinAvailable = !packet.getDashboardPin().isEmpty();
        LabyMod.getInstance().getUserManager().getFamiliarManager().refresh();
    }

    @Override
    public void handle(PacketChatVisibilityChange packet) {
    }

    @Override
    public void handle(PacketKick packet) {
        this.disconnect(true);
        this.lastKickMessage = packet.getReason() == null ? LanguageManager.translate("chat_unknown_kick_reason") : packet.getReason();
        LabyMod.getInstance().notifyMessageRaw(LanguageManager.translate("chat_disconnected_title"), this.lastKickMessage);
    }

    @Override
    public void handle(PacketDisconnect packet) {
        this.disconnect(true);
        this.lastKickMessage = packet.getReason() == null ? LanguageManager.translate("chat_unknown_disconnect_reason") : packet.getReason();
        LabyMod.getInstance().notifyMessageRaw(LanguageManager.translate("chat_disconnected_title"), this.lastKickMessage);
        Executors.newSingleThreadScheduledExecutor().schedule(() -> this.connect(), 5L, TimeUnit.SECONDS);
    }

    @Override
    public void handle(PacketPlayRequestAddFriend packet) {
    }

    @Override
    public void handle(PacketLoginFriend packet) {
        this.chatClient.getFriends().addAll(packet.getFriends());
        this.chatClient.sortFriendList(LabyMod.getSettings().friendSortType);
    }

    @Override
    public void handle(PacketLoginRequest packet) {
        if (LabyMod.getSettings().ignoreRequests) {
            for (ChatRequest chatRequest : packet.getRequests()) {
                this.sendPacket(new PacketPlayDenyFriendRequest(chatRequest));
            }
        } else {
            this.chatClient.getRequests().addAll(packet.getRequests());
            for (ChatRequest chatRequest : this.chatClient.getRequests()) {
                LabyMod.getInstance().notifyMessageProfile(chatRequest.getGameProfile(), String.valueOf(ModColor.cl("f")) + LanguageManager.translate("chat_user_friend_request"));
            }
        }
        if (Minecraft.getMinecraft().currentScreen instanceof GuiFriendsLayout) {
            ((GuiFriendsLayout)Minecraft.getMinecraft().currentScreen).getChatElementMyProfile().updateButtons();
        }
    }

    @Override
    public void handle(PacketBanned packet) {
        this.disconnect(true);
        Minecraft.getMinecraft().shutdown();
        this.lastKickMessage = packet.getReason() == null ? LanguageManager.translate("chat_unknown_ban_reason") : packet.getReason();
        LabyMod.getInstance().notifyMessageRaw(LanguageManager.translate("chat_disconnected_title"), this.lastKickMessage);
    }

    @Override
    public void handle(PacketPing packet) {
        this.sendPacket(new PacketPong());
    }

    @Override
    public void handle(PacketPong packet) {
    }

    @Override
    public void handle(PacketServerMessage packet) {
        LabyMod.getInstance().notifyMessageRaw(LanguageManager.translate("chat_server_message_title"), packet.getMessage());
    }

    @Override
    public void handle(final PacketMessage packet) {
        ChatUser chatUser = this.chatClient.getChatUserByUUID(packet.getSender().getGameProfile().getId());
        if (chatUser != null) {
            chatUser.setLastTyping(0L);
            final SingleChat singleChat = this.chatClient.getChatlogManager().getChat(chatUser);
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    singleChat.addMessage(new MessageChatComponent(packet.getSender().getGameProfile().getName(), System.currentTimeMillis(), packet.getMessage()));
                }
            });
            LabyMod.getInstance().notifyMessageProfile(packet.getSender().getGameProfile(), packet.getMessage());
        }
    }

    @Override
    public void handle(PacketPlayTyping packet) {
        ChatUser chatUser = this.chatClient.getChatUserByUUID(packet.getPlayer().getGameProfile().getId());
        if (chatUser != null) {
            chatUser.setLastTyping(System.currentTimeMillis());
        }
    }

    @Override
    public void handle(PacketPlayRequestAddFriendResponse packet) {
        GuiFriendsAddFriend.response = packet.isRequestSent() ? "true" : packet.getReason();
    }

    @Override
    public void handle(PacketPlayRequestRemove packet) {
        Iterator<ChatRequest> iterator = this.chatClient.getRequests().iterator();
        while (iterator.hasNext()) {
            ChatRequest next = iterator.next();
            if (!next.getGameProfile().getName().equalsIgnoreCase(packet.getPlayerName())) continue;
            iterator.remove();
        }
    }

    @Override
    public void handle(PacketPlayDenyFriendRequest packet) {
    }

    @Override
    public void handle(PacketPlayFriendRemove packet) {
        Iterator<ChatUser> iterator = this.chatClient.getFriends().iterator();
        while (iterator.hasNext()) {
            ChatUser next = iterator.next();
            if (!next.equals(packet.getToRemove())) continue;
            iterator.remove();
            if (GuiFriendsLayout.selectedUser == null || !GuiFriendsLayout.selectedUser.equals(next)) continue;
            GuiFriendsLayout.selectedUser = null;
        }
        this.chatClient.sortFriendList(LabyMod.getSettings().friendSortType);
        GameIconHelper.updateIcon(true, false);
    }

    @Override
    public void handle(PacketLoginOptions packet) {
    }

    @Override
    public void handle(PacketPlayServerStatus packet) {
    }

    @Override
    public void handle(PacketPlayServerStatusUpdate packet) {
    }

    @Override
    public void handle(PacketPlayFriendStatus packet) {
        ChatUser chatUser = this.chatClient.getChatUser(packet.getPlayer());
        chatUser.setCurrentServerInfo(packet.getPlayerInfo());
    }

    @Override
    public void handle(PacketPlayFriendPlayingOn packet) {
        if (!LabyMod.getSettings().alertsPlayingOn) {
            return;
        }
        if (packet.getGameModeName() == null || packet.getGameModeName().isEmpty()) {
            return;
        }
        String message = null;
        message = packet.getGameModeName().contains(".") ? LanguageManager.translate("chat_user_now_playing_on", packet.getGameModeName()) : LanguageManager.translate("chat_user_now_playing", packet.getGameModeName());
        LabyMod.getInstance().notifyMessageProfile(packet.getPlayer().getGameProfile(), message);
    }

    @Override
    public void handle(PacketPlayChangeOptions packet) {
    }

    @Override
    public void handle(PacketLoginTime packet) {
        this.chatClient.getClientProfile().setFirstJoined(packet.getDateJoined());
    }

    @Override
    public void handle(PacketLoginVersion packet) {
    }

    @Override
    public void handle(PacketEncryptionRequest encryptionRequest) {
        SecretKey secretKey = CryptManager.createNewSharedKey();
        PublicKey publicKey = CryptManager.decodePublicKey(encryptionRequest.getPublicKey());
        String serverId = encryptionRequest.getServerId();
        String token = "";
        String hash = new BigInteger(CryptManager.getServerIdHash(serverId, publicKey, secretKey)).toString(16);
        UUID uuid = Minecraft.getMinecraft().getSession().getProfile().getId();
        if (uuid == null) {
            this.lastKickMessage = LanguageManager.translate("chat_invalid_session");
            Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, this.lastKickMessage);
            this.disconnect(false);
            return;
        }
        try {
            MinecraftSessionService minecraftSessionService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString()).createMinecraftSessionService();
            minecraftSessionService.joinServer(Minecraft.getMinecraft().getSession().getProfile(), Minecraft.getMinecraft().getSession().getToken(), hash);
            this.sendPacket(new PacketEncryptionResponse(secretKey, publicKey, encryptionRequest.getVerifyToken()));
            return;
        }
        catch (AuthenticationUnavailableException e1) {
            this.lastKickMessage = LanguageManager.translate("chat_authentication_unavaileable");
        }
        catch (InvalidCredentialsException e2) {
            this.lastKickMessage = LanguageManager.translate("chat_invalid_session");
        }
        catch (AuthenticationException e3) {
            this.lastKickMessage = LanguageManager.translate("chat_login_failed");
        }
        Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, this.lastKickMessage);
        this.disconnect(false);
    }

    @Override
    public void handle(PacketEncryptionResponse packet) {
    }

    @Override
    public void handle(PacketMojangStatus packet) {
    }

    @Override
    public void handle(PacketUpdateCosmetics packet) {
        UUID uuid = LabyMod.getInstance().getPlayerUUID();
        if (uuid == null) {
            return;
        }
        String json = packet.getJson();
        UserManager userManager = LabyMod.getInstance().getUserManager();
        if (json == null) {
            userManager.removeCheckedUser(uuid);
            userManager.getUser(uuid).unloadCosmeticTextures();
            return;
        }
        LabyMod.getInstance().getUserManager().broadcastBitUpdate(true);
        userManager.updateUsersJson(uuid, json, new Consumer<Boolean>(){

            @Override
            public void accept(Boolean accepted) {
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                LabyMod.getInstance().getUserManager().broadcastBitUpdate(false);
            }
        });
    }

    @Override
    public void handle(PacketUserBadge packetUserStatus) {
        byte[] ranks;
        FamiliarManager familiarManager = LabyMod.getInstance().getUserManager().getFamiliarManager();
        UserManager userManager = LabyMod.getInstance().getUserManager();
        UUID[] uuids = packetUserStatus.getUuids();
        boolean validRanks = uuids.length == (ranks = packetUserStatus.getRanks()).length;
        int i2 = 0;
        while (i2 < packetUserStatus.getUuids().length) {
            byte rank;
            UUID uuid = uuids[i2];
            familiarManager.newFamiliarUser(uuid);
            if (validRanks && (rank = ranks[i2]) > 0) {
                try {
                    User user = userManager.getUser(uuid);
                    LabyGroup group = userManager.getGroupManager().getGroupById(ranks[i2]);
                    user.setGroup(group);
                    System.out.println("s");
                }
                catch (Exception error) {
                    Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "Error on updating user rank of " + uuid.toString() + ": " + ranks[i2]);
                }
            }
            ++i2;
        }
    }

    @Override
    public void handle(PacketAddonMessage packet) {
        JsonObject jsonObject;
        LabyMod.getInstance().getEventManager().callAddonMessage(packet);
        String key = packet.getKey();
        if (key.equals("UPDATE")) {
            Updater updater = LabyMod.getInstance().getUpdater();
            try {
                updater.downloadUpdaterFile();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            updater.addUpdaterHook();
        }
        if (key.equals("auth_pin") && (jsonObject = (JsonObject)new JsonParser().parse(packet.getJson())).has("pin")) {
            String pin = jsonObject.get("pin").getAsString();
            if (this.pinResponseConsumer != null) {
                this.pinResponseConsumer.accept(pin);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.disconnect(false);
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public void sendPacket(final Packet packet) {
        if (this.nioSocketChannel == null) {
            return;
        }
        if (!this.nioSocketChannel.isOpen() || !this.nioSocketChannel.isWritable() || this.currentConnectionState == EnumConnectionState.OFFLINE) {
            return;
        }
        if (this.nioSocketChannel.eventLoop().inEventLoop()) {
            this.nioSocketChannel.writeAndFlush(packet).addListeners(new GenericFutureListener[]{ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE});
        } else {
            this.nioSocketChannel.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    ClientConnection.this.nioSocketChannel.writeAndFlush(packet).addListeners(new GenericFutureListener[]{ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE});
                }
            });
        }
    }

    public void setNioSocketChannel(NioSocketChannel nioSocketChannel) {
        this.nioSocketChannel = nioSocketChannel;
    }

    public EnumConnectionState getCurrentConnectionState() {
        return this.currentConnectionState;
    }

    public String getLastKickMessage() {
        return this.lastKickMessage;
    }

    public String getCustomIp() {
        return this.customIp;
    }

    public boolean isPinAvailable() {
        return this.pinAvailable;
    }
}

