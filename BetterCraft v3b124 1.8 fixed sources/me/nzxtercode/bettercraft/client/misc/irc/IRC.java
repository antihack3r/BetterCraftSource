/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.irc;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

public class IRC
extends PircBot {
    private static IRC INSTANCE = new IRC();
    public String prefix;
    public String server;
    public int port;
    public ConnectionStatus status;
    public String currentChannel;
    public String sender;
    private List<Consumer<Map.Entry<String, String>>> messageListeners;
    private List<Map.Entry<String, String>> messageList;
    private int unreadMessages;

    public IRC() {
        BetterCraft.getInstance();
        this.prefix = String.valueOf(BetterCraft.clientPrefix) + "\u00a78[\u00a7fIRC\u00a78] \u00a77";
        this.server = "irc.libera.chat";
        this.port = 6697;
        this.sender = "\u00a77%sender% \u00a78> \u00a7r";
        this.messageListeners = Lists.newArrayList();
        this.messageList = Lists.newArrayList();
        this.unreadMessages = 0;
    }

    public static final IRC getInstance() {
        return INSTANCE;
    }

    public final boolean isUserConnected(String nick) {
        return Arrays.asList(IRC.getInstance().getUsers(IRC.getInstance().currentChannel)).stream().map(user -> user.getNick()).collect(Collectors.toList()).contains(nick);
    }

    public List<Consumer<Map.Entry<String, String>>> getMessageListeners() {
        return this.messageListeners;
    }

    public List<Map.Entry<String, String>> getMessageList() {
        return this.messageList;
    }

    public int getUnreadMessages() {
        return this.unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public void init() {
        StringBuilder stringBuilder = new StringBuilder("#");
        BetterCraft.getInstance();
        this.connectBot(this.server, stringBuilder.append(BetterCraft.clientName).toString());
        this.messageListeners.clear();
        this.messageListeners.add(messageEntry -> {
            this.messageList.add((Map.Entry<String, String>)messageEntry);
            ++this.unreadMessages;
        });
    }

    public void connectBot(final String server, final String channel) {
        this.currentChannel = channel;
        new Thread(new Runnable(){

            @Override
            public void run() {
                IRC.this.status = ConnectionStatus.Connecting;
                try {
                    IRC.this.setLogin("~" + String.valueOf(Minecraft.getMinecraft().getSession().getProfile().getId()));
                    IRC.this.setName(String.valueOf(Minecraft.getMinecraft().getSession().getUsername()));
                    BetterCraft.getInstance();
                    StringBuilder stringBuilder = new StringBuilder(String.valueOf(BetterCraft.clientName)).append(" ");
                    BetterCraft.getInstance();
                    StringBuilder stringBuilder2 = stringBuilder.append(BetterCraft.clientVersion).append(" by ");
                    BetterCraft.getInstance();
                    IRC.this.setVersion(stringBuilder2.append(BetterCraft.clientAuthor).toString());
                    IRC.this.connect(server);
                    IRC.this.joinChannel(channel);
                    CosmeticInstance.sendCosmetics();
                    IRC.this.status = ConnectionStatus.Online;
                    System.out.println("Online IRC");
                }
                catch (NickAlreadyInUseException var2) {
                    IRC.this.status = ConnectionStatus.Offline;
                    System.out.println("Offline Nick");
                }
                catch (IOException | IrcException var3) {
                    IRC.this.status = ConnectionStatus.Offline;
                    System.out.println("Offline IRC");
                }
            }
        }).start();
    }

    public void disconnectBot() {
        new Thread(new Runnable(){

            @Override
            public void run() {
                IRC.this.disconnect();
            }
        }).start();
    }

    @Override
    protected void onDisconnect() {
        this.status = ConnectionStatus.Offline;
        super.onDisconnect();
    }

    public void sendChatWithoutPrefix(String msg) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(msg.replace("\u00a7", "\u00a7")));
    }

    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        CosmeticInstance.sendCosmetics();
    }

    public void sendClientMessage(String inputMessage) {
        if (this.isConnected()) {
            this.messageListeners.forEach(listener -> listener.accept(new AbstractMap.SimpleEntry<String, String>(this.getName(), inputMessage)));
            String message = StringUtils.stripControlCodes(inputMessage);
            this.sendMessage(this.currentChannel, message);
        }
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (message.startsWith("$Cosmetics")) {
            try {
                JsonArray array = new JsonParser().parse(message.replace("$Cosmetics ", "")).getAsJsonArray();
                if (CosmeticInstance.USER_COSMETICS.containsKey(sender)) {
                    CosmeticInstance.USER_COSMETICS.remove(sender);
                }
                CosmeticInstance.USER_COSMETICS.put(sender, StreamSupport.stream(array.spliterator(), false).map(element -> element.getAsInt()).map(id2 -> CosmeticInstance.getCosmetics().stream().filter(cosmetic -> Objects.equals(cosmetic.getId(), id2)).findFirst().get()).collect(Collectors.toList()));
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            this.messageListeners.forEach(listener -> listener.accept(new AbstractMap.SimpleEntry<String, String>(sender, message)));
        }
    }

    public static enum ConnectionStatus {
        Connecting("Connecting"),
        Online("Online"),
        Offline("Offline");

        String s;

        private ConnectionStatus(String s2) {
            this.s = s2;
        }

        public String getText() {
            return this.s;
        }
    }
}

