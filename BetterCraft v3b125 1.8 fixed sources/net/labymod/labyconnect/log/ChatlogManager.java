/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.log;

import com.mojang.authlib.GameProfile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.labymod.labyconnect.log.MessageChatComponent;
import net.labymod.labyconnect.log.SingleChat;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.UserStatus;
import net.labymod.support.util.Debug;

public class ChatlogManager {
    private final int MAX_LOG_MESSAGE_COUNT = 1000;
    private List<SingleChat> chats = new ArrayList<SingleChat>();

    public SingleChat getChat(ChatUser user) {
        for (SingleChat chat : this.chats) {
            if (!chat.getChatPartner().equals(user)) continue;
            return chat.apply(user);
        }
        SingleChat singleChat = new SingleChat(this.chats.size(), user, new ArrayList<MessageChatComponent>());
        this.chats.add(singleChat);
        return singleChat;
    }

    public void loadChatlogs(UUID accountUUID) {
        this.chats.clear();
        File chatlogFile = new File(String.format("LabyMod/chatlog/%s.log", accountUUID.toString()));
        if (!chatlogFile.exists()) {
            return;
        }
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(chatlogFile));
            int total = dis.readInt();
            int i2 = 0;
            while (i2 < total) {
                dis.readInt();
                String name = this.readString(dis);
                UUID uuid = new UUID(dis.readLong(), dis.readLong());
                ArrayList<MessageChatComponent> messageArray = new ArrayList<MessageChatComponent>();
                int totalMessages = dis.readInt();
                if (totalMessages < 1000) {
                    int b2 = 0;
                    while (b2 < totalMessages) {
                        String sender = this.readString(dis);
                        long time = dis.readLong();
                        String message = this.readString(dis);
                        messageArray.add(new MessageChatComponent(sender, time, message));
                        ++b2;
                    }
                }
                GameProfile dummyGameProfile = new GameProfile(uuid, name);
                SingleChat dummySingleChat = this.getChat(new ChatUser(dummyGameProfile, UserStatus.OFFLINE, "", null, 0, System.currentTimeMillis(), 0L, "", 0L, 0L, 0, false));
                dummySingleChat.getMessages().addAll(messageArray);
                ++i2;
            }
            dis.close();
        }
        catch (Exception e2) {
            chatlogFile.delete();
            e2.printStackTrace();
        }
        Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "Loaded " + this.chats.size() + " chats!");
    }

    public void saveChatlogs(UUID accountUUID) {
        Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "Save chat log file for " + accountUUID.toString());
        File chatlogFile = new File(String.format("LabyMod/chatlog/%s.log", accountUUID.toString()));
        if (!chatlogFile.getParentFile().exists()) {
            chatlogFile.getParentFile().mkdirs();
        }
        if (!chatlogFile.exists()) {
            Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "Create new log file for " + accountUUID.toString());
            try {
                chatlogFile.createNewFile();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            ArrayList<SingleChat> chatTemp = new ArrayList<SingleChat>(this.chats);
            Iterator iterator = chatTemp.iterator();
            while (iterator.hasNext()) {
                if (!((SingleChat)iterator.next()).getChatPartner().isParty()) continue;
                iterator.remove();
            }
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(chatlogFile));
            dos.writeInt(chatTemp.size());
            for (SingleChat chat : chatTemp) {
                int count;
                dos.writeInt(chat.getId());
                GameProfile profile = chat.getChatPartner().getGameProfile();
                this.writeString(dos, profile.getName());
                dos.writeLong(profile.getId().getMostSignificantBits());
                dos.writeLong(profile.getId().getLeastSignificantBits());
                List<MessageChatComponent> messageArray = chat.getMessages();
                int size = count = messageArray.size();
                boolean flag = size > 300;
                dos.writeInt(flag ? 300 : size);
                int b2 = 0;
                while (b2 < size) {
                    if (flag && count > 300) {
                        --count;
                    } else {
                        MessageChatComponent component = messageArray.get(b2);
                        this.writeString(dos, component.getSender());
                        dos.writeLong(component.getSentTime());
                        this.writeString(dos, component.getMessage());
                    }
                    ++b2;
                }
            }
            dos.flush();
            dos.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "Saved " + this.chats.size() + " chats!");
    }

    private void writeString(DataOutputStream dos, String string) {
        try {
            byte[] bytes = string.getBytes(Charset.forName("UTF8"));
            dos.writeInt(bytes.length);
            byte[] byArray = bytes;
            int n2 = bytes.length;
            int n3 = 0;
            while (n3 < n2) {
                byte b2 = byArray[n3];
                dos.writeByte(b2);
                ++n3;
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private String readString(DataInputStream dis) {
        try {
            int length = dis.readInt();
            byte[] bytes = new byte[length];
            int i2 = 0;
            while (i2 < length) {
                bytes[i2] = dis.readByte();
                ++i2;
            }
            return new String(bytes, Charset.forName("UTF8"));
        }
        catch (Exception ex2) {
            return "";
        }
    }

    public int getMAX_LOG_MESSAGE_COUNT() {
        return this.MAX_LOG_MESSAGE_COUNT;
    }

    public List<SingleChat> getChats() {
        return this.chats;
    }
}

