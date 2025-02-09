// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.PlayerMovementMapper;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MainHandProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CompressionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.ClientChunks;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.chat.GameMode;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.chat.ChatRewriter;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;

public class PlayerPackets
{
    public static void register(final Protocol1_9To1_8 protocol) {
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.CHAT_MESSAGE, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map((Type<Object>)Type.BYTE);
                this.handler(wrapper -> {
                    try {
                        final JsonObject obj = wrapper.get((Type<JsonObject>)Type.COMPONENT, 0);
                        ChatRewriter.toClient(obj, wrapper.user());
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.TAB_LIST, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.DISCONNECT, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.TITLE, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> {
                    final int action = wrapper.get((Type<Integer>)Type.VAR_INT, 0);
                    if (action == 0 || action == 1) {
                        Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING));
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.PLAYER_POSITION, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.BYTE);
                this.handler(wrapper -> wrapper.write(Type.VAR_INT, 0));
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.TEAMS, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.map((Type<Object>)Type.BYTE);
                this.handler(wrapper -> {
                    final byte mode = wrapper.get((Type<Byte>)Type.BYTE, 0);
                    if (mode == 0 || mode == 2) {
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough((Type<Object>)Type.BYTE);
                        wrapper.passthrough(Type.STRING);
                        wrapper.write(Type.STRING, Via.getConfig().isPreventCollision() ? "never" : "");
                        wrapper.passthrough((Type<Object>)Type.BYTE);
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        final String[] players = wrapper.passthrough(Type.STRING_ARRAY);
                        final EntityTracker1_9 entityTracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        final String myName = wrapper.user().getProtocolInfo().getUsername();
                        final String teamName = wrapper.get(Type.STRING, 0);
                        final String[] array;
                        int i = 0;
                        for (int length = array.length; i < length; ++i) {
                            final String player = array[i];
                            if (entityTracker.isAutoTeam() && player.equalsIgnoreCase(myName)) {
                                if (mode == 4) {
                                    wrapper.send(Protocol1_9To1_8.class);
                                    wrapper.cancel();
                                    entityTracker.sendTeamPacket(true, true);
                                    entityTracker.setCurrentTeam("viaversion");
                                }
                                else {
                                    entityTracker.sendTeamPacket(false, true);
                                    entityTracker.setCurrentTeam(teamName);
                                }
                            }
                        }
                    }
                    if (mode == 1) {
                        final EntityTracker1_9 entityTracker2 = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        final String teamName2 = wrapper.get(Type.STRING, 0);
                        if (entityTracker2.isAutoTeam() && teamName2.equals(entityTracker2.getCurrentTeam())) {
                            wrapper.send(Protocol1_9To1_8.class);
                            wrapper.cancel();
                            entityTracker2.sendTeamPacket(true, true);
                            entityTracker2.setCurrentTeam("viaversion");
                        }
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.JOIN_GAME, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.handler(wrapper -> {
                    final int entityId = wrapper.get((Type<Integer>)Type.INT, 0);
                    final EntityTracker1_9 tracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityId, Entity1_10Types.EntityType.PLAYER);
                    tracker.setClientEntityId(entityId);
                    return;
                });
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.map((Type<Object>)Type.BOOLEAN);
                this.handler(wrapper -> {
                    final EntityTracker1_9 tracker2 = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker2.setGameMode(GameMode.getById(wrapper.get((Type<Short>)Type.UNSIGNED_BYTE, 0)));
                    return;
                });
                this.handler(wrapper -> {
                    final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    final int dimensionId = wrapper.get((Type<Byte>)Type.BYTE, 0);
                    clientWorld.setEnvironment(dimensionId);
                    return;
                });
                this.handler(wrapper -> {
                    final CommandBlockProvider provider = Via.getManager().getProviders().get(CommandBlockProvider.class);
                    provider.sendPermission(wrapper.user());
                    return;
                });
                this.handler(wrapper -> {
                    final EntityTracker1_9 entityTracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (Via.getConfig().isAutoTeam()) {
                        entityTracker.setAutoTeam(true);
                        wrapper.send(Protocol1_9To1_8.class);
                        wrapper.cancel();
                        entityTracker.sendTeamPacket(true, true);
                        entityTracker.setCurrentTeam("viaversion");
                    }
                    else {
                        entityTracker.setAutoTeam(false);
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.PLAYER_INFO, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> {
                    final int action = wrapper.get((Type<Integer>)Type.VAR_INT, 0);
                    for (int count = wrapper.get((Type<Integer>)Type.VAR_INT, 1), i = 0; i < count; ++i) {
                        wrapper.passthrough(Type.UUID);
                        if (action == 0) {
                            wrapper.passthrough(Type.STRING);
                            for (int properties = wrapper.passthrough((Type<Integer>)Type.VAR_INT), j = 0; j < properties; ++j) {
                                wrapper.passthrough(Type.STRING);
                                wrapper.passthrough(Type.STRING);
                                wrapper.passthrough(Type.OPTIONAL_STRING);
                            }
                            wrapper.passthrough((Type<Object>)Type.VAR_INT);
                            wrapper.passthrough((Type<Object>)Type.VAR_INT);
                            final String displayName = wrapper.read(Type.OPTIONAL_STRING);
                            wrapper.write(Type.OPTIONAL_COMPONENT, (displayName != null) ? Protocol1_9To1_8.FIX_JSON.transform(wrapper, displayName) : null);
                        }
                        else if (action == 1 || action == 2) {
                            wrapper.passthrough((Type<Object>)Type.VAR_INT);
                        }
                        else if (action == 3) {
                            final String displayName2 = wrapper.read(Type.OPTIONAL_STRING);
                            wrapper.write(Type.OPTIONAL_COMPONENT, (displayName2 != null) ? Protocol1_9To1_8.FIX_JSON.transform(wrapper, displayName2) : null);
                        }
                        else if (action == 4) {}
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.PLUGIN_MESSAGE, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    final String name = wrapper.get(Type.STRING, 0);
                    if (name.equalsIgnoreCase("MC|BOpen")) {
                        wrapper.read(Type.REMAINING_BYTES);
                        wrapper.write(Type.VAR_INT, 0);
                    }
                    if (name.equalsIgnoreCase("MC|TrList")) {
                        wrapper.passthrough((Type<Object>)Type.INT);
                        final Short size = wrapper.passthrough((Type<Short>)Type.UNSIGNED_BYTE);
                        for (int i = 0; i < size; ++i) {
                            final Item item1 = wrapper.passthrough(Type.ITEM);
                            ItemRewriter.toClient(item1);
                            final Item item2 = wrapper.passthrough(Type.ITEM);
                            ItemRewriter.toClient(item2);
                            final boolean present = wrapper.passthrough((Type<Boolean>)Type.BOOLEAN);
                            if (present) {
                                final Item item3 = wrapper.passthrough(Type.ITEM);
                                ItemRewriter.toClient(item3);
                            }
                            wrapper.passthrough((Type<Object>)Type.BOOLEAN);
                            wrapper.passthrough((Type<Object>)Type.INT);
                            wrapper.passthrough((Type<Object>)Type.INT);
                        }
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.RESPAWN, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    final int dimensionId = wrapper.get((Type<Integer>)Type.INT, 0);
                    clientWorld.setEnvironment(dimensionId);
                    return;
                });
                this.handler(wrapper -> {
                    wrapper.user().get(ClientChunks.class).getLoadedChunks().clear();
                    final int gamemode = wrapper.get((Type<Short>)Type.UNSIGNED_BYTE, 0);
                    final EntityTracker1_9 tracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.setGameMode(GameMode.getById(gamemode));
                    return;
                });
                this.handler(wrapper -> {
                    final CommandBlockProvider provider = Via.getManager().getProviders().get(CommandBlockProvider.class);
                    provider.sendPermission(wrapper.user());
                    provider.unloadChunks(wrapper.user());
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_8, CM, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.GAME_EVENT, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.FLOAT);
                this.handler(wrapper -> {
                    final short reason = wrapper.get((Type<Short>)Type.UNSIGNED_BYTE, 0);
                    if (reason == 3) {
                        final int gamemode = wrapper.get((Type<Float>)Type.FLOAT, 0).intValue();
                        final EntityTracker1_9 tracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        tracker.setGameMode(GameMode.getById(gamemode));
                    }
                    else if (reason == 4) {
                        wrapper.set(Type.FLOAT, 0, 1.0f);
                    }
                });
            }
        });
        ((Protocol<ClientboundPackets1_8, ClientboundPackets1_9, SM, SU>)protocol).registerClientbound(ClientboundPackets1_8.SET_COMPRESSION, null, wrapper -> {
            wrapper.cancel();
            final CompressionProvider provider = Via.getManager().getProviders().get(CompressionProvider.class);
            provider.handlePlayCompression(wrapper.user(), wrapper.read((Type<Integer>)Type.VAR_INT));
            return;
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.TAB_COMPLETE, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.map(Type.BOOLEAN, Type.NOTHING);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.CLIENT_SETTINGS, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.map((Type<Object>)Type.BYTE);
                this.map(Type.VAR_INT, Type.BYTE);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.handler(wrapper -> {
                    final int hand = wrapper.read((Type<Integer>)Type.VAR_INT);
                    if (Via.getConfig().isLeftHandedHandling() && hand == 0) {
                        wrapper.set(Type.UNSIGNED_BYTE, 0, (short)(wrapper.get((Type<Short>)Type.UNSIGNED_BYTE, 0) | 0x80));
                    }
                    wrapper.sendToServer(Protocol1_9To1_8.class);
                    wrapper.cancel();
                    Via.getManager().getProviders().get(MainHandProvider.class).setMainHand(wrapper.user(), hand);
                });
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.ANIMATION, new PacketHandlers() {
            public void register() {
                this.map(Type.VAR_INT, Type.NOTHING);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).cancelServerbound(ServerboundPackets1_9.TELEPORT_CONFIRM);
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).cancelServerbound(ServerboundPackets1_9.VEHICLE_MOVE);
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).cancelServerbound(ServerboundPackets1_9.STEER_BOAT);
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.PLUGIN_MESSAGE, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    final String name = wrapper.get(Type.STRING, 0);
                    if (name.equalsIgnoreCase("MC|BSign")) {
                        final Item item = wrapper.passthrough(Type.ITEM);
                        if (item != null) {
                            item.setIdentifier(387);
                            ItemRewriter.rewriteBookToServer(item);
                        }
                    }
                    if (name.equalsIgnoreCase("MC|AutoCmd")) {
                        wrapper.set(Type.STRING, 0, "MC|AdvCdm");
                        wrapper.write(Type.BYTE, (Byte)0);
                        wrapper.passthrough((Type<Object>)Type.INT);
                        wrapper.passthrough((Type<Object>)Type.INT);
                        wrapper.passthrough((Type<Object>)Type.INT);
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough((Type<Object>)Type.BOOLEAN);
                        wrapper.clearInputBuffer();
                    }
                    if (name.equalsIgnoreCase("MC|AdvCmd")) {
                        wrapper.set(Type.STRING, 0, "MC|AdvCdm");
                    }
                });
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.CLIENT_STATUS, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> {
                    final int action = wrapper.get((Type<Integer>)Type.VAR_INT, 0);
                    if (action == 2) {
                        final EntityTracker1_9 tracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        if (tracker.isBlocking()) {
                            if (!Via.getConfig().isShowShieldWhenSwordInHand()) {
                                tracker.setSecondHand(null);
                            }
                            tracker.setBlocking(false);
                        }
                    }
                });
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.PLAYER_POSITION, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.BOOLEAN);
                this.handler(new PlayerMovementMapper());
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.PLAYER_POSITION_AND_ROTATION, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.BOOLEAN);
                this.handler(new PlayerMovementMapper());
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.PLAYER_ROTATION, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.BOOLEAN);
                this.handler(new PlayerMovementMapper());
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_9>)protocol).registerServerbound(ServerboundPackets1_9.PLAYER_MOVEMENT, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.BOOLEAN);
                this.handler(new PlayerMovementMapper());
            }
        });
    }
}
