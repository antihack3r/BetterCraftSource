/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api;

import com.google.gson.JsonElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.labymod.api.events.MessageModifyChatEvent;
import net.labymod.api.events.MessageReceiveEvent;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.api.events.PluginMessageEvent;
import net.labymod.api.events.RenderEntityEvent;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.api.events.TabListEvent;
import net.labymod.api.events.UserMenuActionEvent;
import net.labymod.core.ChatComponent;
import net.labymod.labyconnect.packets.PacketAddonDevelopment;
import net.labymod.labyconnect.packets.PacketAddonMessage;
import net.labymod.user.User;
import net.labymod.user.util.UserActionEntry;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class EventManager {
    private Set<MessageModifyChatEvent> messageModifyChat = new HashSet<MessageModifyChatEvent>();
    private Set<MessageReceiveEvent> messageReceive = new HashSet<MessageReceiveEvent>();
    private Set<MessageSendEvent> messageSend = new HashSet<MessageSendEvent>();
    private Set<TabListEvent> tabList = new HashSet<TabListEvent>();
    private Set<PluginMessageEvent> pluginMessage = new HashSet<PluginMessageEvent>();
    private Set<ServerMessageEvent> serverMessage = new HashSet<ServerMessageEvent>();
    private Set<RenderEntityEvent> renderEntity = new HashSet<RenderEntityEvent>();
    private Set<Consumer<ServerData>> joinServer = new HashSet<Consumer<ServerData>>();
    private Set<Consumer<ServerData>> quitServer = new HashSet<Consumer<ServerData>>();
    private Set<Consumer<Entity>> attackEntity = new HashSet<Consumer<Entity>>();
    private Set<Consumer<PacketAddonMessage>> addonMessage = new HashSet<Consumer<PacketAddonMessage>>();
    private Set<Consumer<Object>> incomingPackets = new HashSet<Consumer<Object>>();
    private Set<Consumer<PacketAddonDevelopment>> addonDevelopmentPackets = new HashSet<Consumer<PacketAddonDevelopment>>();
    private Set<UserMenuActionEvent> createUserMenuActions = new HashSet<UserMenuActionEvent>();

    public void callAllHeader(ChatComponent tabListHeader) {
        if (tabListHeader == null || tabListHeader == null) {
            return;
        }
        for (TabListEvent tabListUpdateListener : this.tabList) {
            tabListUpdateListener.onUpdate(TabListEvent.Type.HEADER, tabListHeader.getFormattedText(), tabListHeader.getUnformattedText());
        }
    }

    public void callAllFooter(ChatComponent tabListFooter) {
        if (tabListFooter == null || tabListFooter == null) {
            return;
        }
        for (TabListEvent tabListUpdateListener : this.tabList) {
            tabListUpdateListener.onUpdate(TabListEvent.Type.FOOTER, tabListFooter.getFormattedText(), tabListFooter.getUnformattedText());
        }
    }

    public void callAllPluginMessage(String channelName, PacketBuffer packetBuffer) {
        for (PluginMessageEvent tabListUpdateListener : this.pluginMessage) {
            tabListUpdateListener.receiveMessage(channelName, packetBuffer);
        }
    }

    public void callRenderEntity(Entity entity, double x2, double y2, double z2, float partialTicks) {
        for (RenderEntityEvent renderEntityEvent : this.renderEntity) {
            renderEntityEvent.onRender(entity, x2, y2, z2, partialTicks);
        }
    }

    public void callJoinServer(ServerData serverData) {
        for (Consumer<ServerData> consumer : this.joinServer) {
            consumer.accept(serverData);
        }
    }

    public void callQuitServer(ServerData lastServerData) {
        for (Consumer<ServerData> consumer : this.quitServer) {
            consumer.accept(lastServerData);
        }
    }

    public void callAttackEntity(Entity entity) {
        for (Consumer<Entity> consumer : this.attackEntity) {
            consumer.accept(entity);
        }
    }

    public void callServerMessage(String messageKey, JsonElement serverMessage) {
        for (ServerMessageEvent serverMessageListener : this.serverMessage) {
            try {
                serverMessageListener.onServerMessage(messageKey, serverMessage);
            }
            catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    public void callAddonMessage(PacketAddonMessage packet) {
        for (Consumer<PacketAddonMessage> consumer : this.addonMessage) {
            consumer.accept(packet);
        }
    }

    public void callincomingPacket(Object packet) {
        for (Consumer<Object> consumer : this.incomingPackets) {
            consumer.accept(packet);
        }
    }

    public void callAddonDevelopmentPacket(PacketAddonDevelopment packet) {
        for (Consumer<PacketAddonDevelopment> consumer : this.addonDevelopmentPackets) {
            consumer.accept(packet);
        }
    }

    public void callCreateUserMenuActions(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo, List<UserActionEntry> entries) {
        for (UserMenuActionEvent event : this.createUserMenuActions) {
            try {
                event.createActions(user, entityPlayer, networkPlayerInfo, entries);
            }
            catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    public void register(TabListEvent listener) {
        this.tabList.add(listener);
    }

    public void register(MessageModifyChatEvent listener) {
        this.messageModifyChat.add(listener);
    }

    public void register(MessageReceiveEvent listener) {
        this.messageReceive.add(listener);
    }

    public void register(MessageSendEvent listener) {
        this.messageSend.add(listener);
    }

    public void register(PluginMessageEvent listener) {
        this.pluginMessage.add(listener);
    }

    public void register(RenderEntityEvent listener) {
        this.renderEntity.add(listener);
    }

    public void register(ServerMessageEvent listener) {
        this.serverMessage.add(listener);
    }

    public void registerOnJoin(Consumer<ServerData> listener) {
        this.joinServer.add(listener);
    }

    public void registerOnQuit(Consumer<ServerData> listener) {
        this.quitServer.add(listener);
    }

    public void registerOnAttack(Consumer<Entity> listener) {
        this.attackEntity.add(listener);
    }

    public void registerAddonMessage(Consumer<PacketAddonMessage> listener) {
        this.addonMessage.add(listener);
    }

    public void registerOnIncomingPacket(Consumer<Object> listener) {
        this.incomingPackets.add(listener);
    }

    public void registerOnAddonDevelopmentPacket(Consumer<PacketAddonDevelopment> listener) {
        this.addonDevelopmentPackets.add(listener);
    }

    public void register(UserMenuActionEvent listener) {
        this.createUserMenuActions.add(listener);
    }

    public Set<MessageModifyChatEvent> getMessageModifyChat() {
        return this.messageModifyChat;
    }

    public Set<MessageReceiveEvent> getMessageReceive() {
        return this.messageReceive;
    }

    public Set<MessageSendEvent> getMessageSend() {
        return this.messageSend;
    }
}

