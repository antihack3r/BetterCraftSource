/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.HashMap;
import java.util.Map;
import net.labymod.labyconnect.packets.EnumConnectionState;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketActionPlay;
import net.labymod.labyconnect.packets.PacketActionPlayResponse;
import net.labymod.labyconnect.packets.PacketActionRequest;
import net.labymod.labyconnect.packets.PacketActionRequestResponse;
import net.labymod.labyconnect.packets.PacketAddonDevelopment;
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
import net.labymod.labyconnect.packets.PacketLoginStart;
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
import net.labymod.support.util.Debug;

public class Protocol {
    private static final Protocol INSTANCE = new Protocol();
    private Map<Class<? extends Packet>, EnumConnectionState> protocol = new HashMap<Class<? extends Packet>, EnumConnectionState>();
    private Map<Integer, Class<? extends Packet>> packets = new HashMap<Integer, Class<? extends Packet>>();

    public static Protocol getProtocol() {
        return INSTANCE;
    }

    public Protocol() {
        this.register(0, PacketHelloPing.class, EnumConnectionState.HELLO);
        this.register(1, PacketHelloPong.class, EnumConnectionState.HELLO);
        this.register(2, PacketLoginStart.class, EnumConnectionState.LOGIN);
        this.register(3, PacketLoginData.class, EnumConnectionState.LOGIN);
        this.register(4, PacketLoginFriend.class, EnumConnectionState.LOGIN);
        this.register(5, PacketLoginRequest.class, EnumConnectionState.LOGIN);
        this.register(6, PacketLoginOptions.class, EnumConnectionState.LOGIN);
        this.register(7, PacketLoginComplete.class, EnumConnectionState.LOGIN);
        this.register(8, PacketLoginTime.class, EnumConnectionState.LOGIN);
        this.register(9, PacketLoginVersion.class, EnumConnectionState.LOGIN);
        this.register(10, PacketEncryptionRequest.class, EnumConnectionState.LOGIN);
        this.register(11, PacketEncryptionResponse.class, EnumConnectionState.LOGIN);
        this.register(14, PacketPlayPlayerOnline.class, EnumConnectionState.PLAY);
        this.register(16, PacketPlayRequestAddFriend.class, EnumConnectionState.PLAY);
        this.register(17, PacketPlayRequestAddFriendResponse.class, EnumConnectionState.PLAY);
        this.register(18, PacketPlayRequestRemove.class, EnumConnectionState.PLAY);
        this.register(19, PacketPlayDenyFriendRequest.class, EnumConnectionState.PLAY);
        this.register(20, PacketPlayFriendRemove.class, EnumConnectionState.PLAY);
        this.register(21, PacketPlayChangeOptions.class, EnumConnectionState.PLAY);
        this.register(22, PacketPlayServerStatus.class, EnumConnectionState.PLAY);
        this.register(23, PacketPlayFriendStatus.class, EnumConnectionState.PLAY);
        this.register(24, PacketPlayFriendPlayingOn.class, EnumConnectionState.PLAY);
        this.register(25, PacketPlayTyping.class, EnumConnectionState.PLAY);
        this.register(26, PacketMojangStatus.class, EnumConnectionState.PLAY);
        this.register(27, PacketActionPlay.class, EnumConnectionState.PLAY);
        this.register(28, PacketActionPlayResponse.class, EnumConnectionState.PLAY);
        this.register(29, PacketActionRequest.class, EnumConnectionState.PLAY);
        this.register(30, PacketActionRequestResponse.class, EnumConnectionState.PLAY);
        this.register(31, PacketUpdateCosmetics.class, EnumConnectionState.PLAY);
        this.register(32, PacketAddonMessage.class, EnumConnectionState.PLAY);
        this.register(33, PacketUserBadge.class, EnumConnectionState.PLAY);
        this.register(34, PacketAddonDevelopment.class, EnumConnectionState.PLAY);
        this.register(60, PacketDisconnect.class, EnumConnectionState.ALL);
        this.register(61, PacketKick.class, EnumConnectionState.ALL);
        this.register(62, PacketPing.class, EnumConnectionState.ALL);
        this.register(63, PacketPong.class, EnumConnectionState.ALL);
        this.register(64, PacketServerMessage.class, EnumConnectionState.ALL);
        this.register(65, PacketMessage.class, EnumConnectionState.ALL);
        this.register(66, PacketBanned.class, EnumConnectionState.ALL);
        this.register(67, PacketChatVisibilityChange.class, EnumConnectionState.ALL);
        this.register(68, PacketPlayServerStatusUpdate.class, EnumConnectionState.PLAY);
    }

    public Map<Integer, Class<? extends Packet>> getPackets() {
        return this.packets;
    }

    private final void register(int id2, Class<? extends Packet> clazz, EnumConnectionState state) {
        try {
            clazz.newInstance();
            this.packets.put(id2, clazz);
            this.protocol.put(clazz, state);
        }
        catch (Exception e2) {
            Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "Class " + clazz.getSimpleName() + " does not contain a default Constructor, this might break the game :/");
        }
    }

    public Packet getPacket(int id2) throws IllegalAccessException, InstantiationException {
        if (!this.packets.containsKey(id2)) {
            throw new RuntimeException("Packet with id " + id2 + " is not registered.");
        }
        return this.packets.get(id2).newInstance();
    }

    public int getPacketId(Packet packet) {
        for (Map.Entry<Integer, Class<? extends Packet>> entry : this.packets.entrySet()) {
            Class<? extends Packet> clazz = entry.getValue();
            if (!clazz.isInstance(packet)) continue;
            return entry.getKey();
        }
        throw new RuntimeException("Packet " + packet + " is not registered.");
    }
}

