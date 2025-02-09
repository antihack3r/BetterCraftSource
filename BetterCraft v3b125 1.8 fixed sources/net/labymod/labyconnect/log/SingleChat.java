/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.labyconnect.log.MessageChatComponent;
import net.labymod.labyconnect.packets.PacketMessage;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.UserStatus;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;

public class SingleChat {
    private static final ResourceLocation POP_SOUND = new ResourceLocation(Source.ABOUT_MC_VERSION.startsWith("1.8") ? "random.pop" : "entity.chicken.egg");
    private final int id;
    private ChatUser chatPartner;
    private List<MessageChatComponent> messages = Collections.synchronizedList(new ArrayList());

    public SingleChat(int id2, ChatUser friend, List<MessageChatComponent> messages) {
        this.id = id2;
        this.chatPartner = friend;
        this.messages = messages;
    }

    public void addMessage(MessageChatComponent message) {
        this.messages.add(message);
        this.chatPartner.setLastInteraction(System.currentTimeMillis());
        LabyConnect chatClient = LabyMod.getInstance().getLabyConnect();
        UserStatus userStatus = chatClient.getClientProfile().getUserStatus();
        boolean isClientSender = message.getSender().equalsIgnoreCase(LabyMod.getInstance().getPlayerName());
        boolean playSounds = LabyMod.getSettings().alertPlaySounds;
        if (isClientSender) {
            if (playSounds) {
                LabyModCore.getMinecraft().playSound(POP_SOUND, 1.5f);
            }
            if (this.chatPartner.isParty()) {
                LabyMod.getInstance().getLabyPlay().getPartySystem().sendChatMessage(message.getMessage());
            } else {
                ChatUser clientUser = chatClient.getClientProfile().buildClientUser();
                PacketMessage packet = new PacketMessage(clientUser, this.chatPartner, message.getMessage(), 0L, 0.0, System.currentTimeMillis());
                chatClient.getClientConnection().sendPacket(packet);
            }
        } else if (playSounds && userStatus != UserStatus.BUSY) {
            LabyModCore.getMinecraft().playSound(POP_SOUND, 2.5f);
            if (!(Display.isActive() && Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiFriendsLayout && GuiFriendsLayout.selectedUser != null && GuiFriendsLayout.selectedUser.equals(this.chatPartner))) {
                this.chatPartner.increaseUnreadMessages();
            }
        }
    }

    public SingleChat apply(ChatUser chatUser) {
        this.chatPartner = chatUser;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public ChatUser getChatPartner() {
        return this.chatPartner;
    }

    public List<MessageChatComponent> getMessages() {
        return this.messages;
    }

    public void setChatPartner(ChatUser chatPartner) {
        this.chatPartner = chatPartner;
    }

    public void setMessages(List<MessageChatComponent> messages) {
        this.messages = messages;
    }
}

