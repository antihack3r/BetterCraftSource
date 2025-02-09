// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import java.util.List;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.ParticleMapping;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_12;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viabackwards.api.entities.storage.EntityPositionHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.BackwardsBlockStorage;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.PaintingMapping;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.EntityTypeMapping;
import com.viaversion.viaversion.api.type.types.version.Types1_12;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import java.util.Optional;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.PlayerPositionStorage1_13;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;

public class EntityPackets1_13 extends LegacyEntityRewriter<ClientboundPackets1_13, Protocol1_12_2To1_13>
{
    public EntityPackets1_13(final Protocol1_12_2To1_13 protocol) {
        super(protocol);
    }
    
    @Override
    protected void registerPackets() {
        ((AbstractProtocol<ClientboundPackets1_13, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_13.PLAYER_POSITION, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.BYTE);
                this.handler(wrapper -> {
                    if (!(!ViaBackwards.getConfig().isFix1_13FacePlayer())) {
                        final PlayerPositionStorage1_13 playerStorage = wrapper.user().get(PlayerPositionStorage1_13.class);
                        final byte bitField = wrapper.get((Type<Byte>)Type.BYTE, 0);
                        playerStorage.setX(toSet(bitField, 0, playerStorage.getX(), wrapper.get((Type<Double>)Type.DOUBLE, 0)));
                        playerStorage.setY(toSet(bitField, 1, playerStorage.getY(), wrapper.get((Type<Double>)Type.DOUBLE, 1)));
                        playerStorage.setZ(toSet(bitField, 2, playerStorage.getZ(), wrapper.get((Type<Double>)Type.DOUBLE, 2)));
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_13, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_13.SPAWN_ENTITY, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.UUID);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.INT);
                this.handler(LegacyEntityRewriter.this.getObjectTrackerHandler());
                this.handler(wrapper -> {
                    final Optional<Entity1_13Types.ObjectType> optionalType = Entity1_13Types.ObjectType.findById(wrapper.get((Type<Byte>)Type.BYTE, 0));
                    if (!(!optionalType.isPresent())) {
                        final Entity1_13Types.ObjectType type = optionalType.get();
                        if (type == Entity1_13Types.ObjectType.FALLING_BLOCK) {
                            final int blockState = wrapper.get((Type<Integer>)Type.INT, 0);
                            final int combined = Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(blockState);
                            final int combined2 = (combined >> 4 & 0xFFF) | (combined & 0xF) << 12;
                            wrapper.set(Type.INT, 0, combined2);
                        }
                        else if (type == Entity1_13Types.ObjectType.ITEM_FRAME) {
                            int data = wrapper.get((Type<Integer>)Type.INT, 0);
                            switch (data) {
                                case 3: {
                                    data = 0;
                                    break;
                                }
                                case 4: {
                                    data = 1;
                                    break;
                                }
                                case 5: {
                                    data = 3;
                                    break;
                                }
                            }
                            wrapper.set(Type.INT, 0, data);
                        }
                        else if (type == Entity1_13Types.ObjectType.TRIDENT) {
                            wrapper.set(Type.BYTE, 0, (byte)Entity1_13Types.ObjectType.TIPPED_ARROW.getId());
                        }
                    }
                });
            }
        });
        ((EntityRewriter<ClientboundPackets1_13, T>)this).registerTracker(ClientboundPackets1_13.SPAWN_EXPERIENCE_ORB, Entity1_13Types.EntityType.EXPERIENCE_ORB);
        ((EntityRewriter<ClientboundPackets1_13, T>)this).registerTracker(ClientboundPackets1_13.SPAWN_GLOBAL_ENTITY, Entity1_13Types.EntityType.LIGHTNING_BOLT);
        ((AbstractProtocol<ClientboundPackets1_13, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_13.SPAWN_MOB, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.UUID);
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.SHORT);
                this.map((Type<Object>)Type.SHORT);
                this.map((Type<Object>)Type.SHORT);
                this.map(Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
                this.handler(wrapper -> {
                    final int type = wrapper.get((Type<Integer>)Type.VAR_INT, 1);
                    final EntityType entityType = Entity1_13Types.getTypeFromId(type, false);
                    EntityPackets1_13.this.tracker(wrapper.user()).addEntity(wrapper.get((Type<Integer>)Type.VAR_INT, 0), entityType);
                    final int oldId = EntityTypeMapping.getOldId(type);
                    if (oldId == -1) {
                        if (!EntityRewriterBase.this.hasData(entityType)) {
                            ViaBackwards.getPlatform().getLogger().warning("Could not find 1.12 entity type for 1.13 entity type " + type + "/" + entityType);
                        }
                    }
                    else {
                        wrapper.set(Type.VAR_INT, 1, oldId);
                    }
                    return;
                });
                this.handler(LegacyEntityRewriter.this.getMobSpawnRewriter(Types1_12.METADATA_LIST));
            }
        });
        ((AbstractProtocol<ClientboundPackets1_13, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_13.SPAWN_PLAYER, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.UUID);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map(Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
                this.handler(LegacyEntityRewriter.this.getTrackerAndMetaHandler(Types1_12.METADATA_LIST, Entity1_13Types.EntityType.PLAYER));
            }
        });
        ((AbstractProtocol<ClientboundPackets1_13, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_13.SPAWN_PAINTING, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.UUID);
                this.handler(EntityRewriterBase.this.getTrackerHandler(Entity1_13Types.EntityType.PAINTING, Type.VAR_INT));
                this.handler(wrapper -> {
                    final int motive = wrapper.read((Type<Integer>)Type.VAR_INT);
                    final String title = PaintingMapping.getStringId(motive);
                    wrapper.write(Type.STRING, title);
                });
            }
        });
        ((LegacyEntityRewriter<ClientboundPackets1_13, T>)this).registerJoinGame(ClientboundPackets1_13.JOIN_GAME, Entity1_13Types.EntityType.PLAYER);
        ((AbstractProtocol<ClientboundPackets1_13, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_13.RESPAWN, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.handler(EntityRewriterBase.this.getDimensionHandler(0));
                this.handler(wrapper -> wrapper.user().get(BackwardsBlockStorage.class).clear());
            }
        });
        ((EntityRewriter<ClientboundPackets1_13, T>)this).registerRemoveEntities(ClientboundPackets1_13.DESTROY_ENTITIES);
        ((LegacyEntityRewriter<ClientboundPackets1_13, T>)this).registerMetadataRewriter(ClientboundPackets1_13.ENTITY_METADATA, Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
        this.protocol.registerClientbound(ClientboundPackets1_13.FACE_PLAYER, null, wrapper -> {
            wrapper.cancel();
            if (!ViaBackwards.getConfig().isFix1_13FacePlayer()) {
                return;
            }
            else {
                final int anchor = wrapper.read((Type<Integer>)Type.VAR_INT);
                final double x = wrapper.read((Type<Double>)Type.DOUBLE);
                final double y = wrapper.read((Type<Double>)Type.DOUBLE);
                final double z = wrapper.read((Type<Double>)Type.DOUBLE);
                final PlayerPositionStorage1_13 positionStorage = wrapper.user().get(PlayerPositionStorage1_13.class);
                final PacketWrapper positionAndLook = wrapper.create(ClientboundPackets1_12_1.PLAYER_POSITION);
                positionAndLook.write(Type.DOUBLE, 0.0);
                positionAndLook.write(Type.DOUBLE, 0.0);
                positionAndLook.write(Type.DOUBLE, 0.0);
                EntityPositionHandler.writeFacingDegrees(positionAndLook, positionStorage.getX(), (anchor == 1) ? (positionStorage.getY() + 1.62) : positionStorage.getY(), positionStorage.getZ(), x, y, z);
                positionAndLook.write(Type.BYTE, (Byte)7);
                positionAndLook.write(Type.VAR_INT, -1);
                positionAndLook.send(Protocol1_12_2To1_13.class);
                return;
            }
        });
        if (ViaBackwards.getConfig().isFix1_13FacePlayer()) {
            final PacketHandlers movementRemapper = new PacketHandlers() {
                public void register() {
                    this.map((Type<Object>)Type.DOUBLE);
                    this.map((Type<Object>)Type.DOUBLE);
                    this.map((Type<Object>)Type.DOUBLE);
                    this.handler(wrapper -> wrapper.user().get(PlayerPositionStorage1_13.class).setCoordinates(wrapper, false));
                }
            };
            ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_12_1>)this.protocol).registerServerbound(ServerboundPackets1_12_1.PLAYER_POSITION, movementRemapper);
            ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_12_1>)this.protocol).registerServerbound(ServerboundPackets1_12_1.PLAYER_POSITION_AND_ROTATION, movementRemapper);
            ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_12_1>)this.protocol).registerServerbound(ServerboundPackets1_12_1.VEHICLE_MOVE, movementRemapper);
        }
    }
    
    @Override
    protected void registerRewrites() {
        this.mapEntityTypeWithData(Entity1_13Types.EntityType.DROWNED, Entity1_13Types.EntityType.ZOMBIE_VILLAGER).plainName();
        this.mapEntityTypeWithData(Entity1_13Types.EntityType.COD, Entity1_13Types.EntityType.SQUID).plainName();
        this.mapEntityTypeWithData(Entity1_13Types.EntityType.SALMON, Entity1_13Types.EntityType.SQUID).plainName();
        this.mapEntityTypeWithData(Entity1_13Types.EntityType.PUFFERFISH, Entity1_13Types.EntityType.SQUID).plainName();
        this.mapEntityTypeWithData(Entity1_13Types.EntityType.TROPICAL_FISH, Entity1_13Types.EntityType.SQUID).plainName();
        this.mapEntityTypeWithData(Entity1_13Types.EntityType.PHANTOM, Entity1_13Types.EntityType.PARROT).plainName().spawnMetadata(storage -> storage.add(new Metadata(15, MetaType1_12.VarInt, 3)));
        this.mapEntityTypeWithData(Entity1_13Types.EntityType.DOLPHIN, Entity1_13Types.EntityType.SQUID).plainName();
        this.mapEntityTypeWithData(Entity1_13Types.EntityType.TURTLE, Entity1_13Types.EntityType.OCELOT).plainName();
        this.filter().handler((event, meta) -> {
            final int typeId = meta.metaType().typeId();
            if (typeId == 4) {
                final JsonElement element = meta.value();
                ((Protocol1_12_2To1_13)this.protocol).translatableRewriter().processText(element);
                meta.setMetaType(MetaType1_12.Chat);
            }
            else if (typeId == 5) {
                final JsonElement element2 = meta.value();
                meta.setTypeAndValue(MetaType1_12.String, ((Protocol1_12_2To1_13)this.protocol).jsonToLegacy(element2));
            }
            else if (typeId == 6) {
                final Item item = (Item)meta.getValue();
                meta.setTypeAndValue(MetaType1_12.Slot, ((Protocol1_12_2To1_13)this.protocol).getItemRewriter().handleItemToClient(item));
            }
            else if (typeId == 15) {
                event.cancel();
            }
            else {
                meta.setMetaType(MetaType1_12.byId((typeId > 5) ? (typeId - 1) : typeId));
            }
            return;
        });
        this.filter().filterFamily(Entity1_13Types.EntityType.ZOMBIE).removeIndex(15);
        this.filter().type(Entity1_13Types.EntityType.TURTLE).cancel(13);
        this.filter().type(Entity1_13Types.EntityType.TURTLE).cancel(14);
        this.filter().type(Entity1_13Types.EntityType.TURTLE).cancel(15);
        this.filter().type(Entity1_13Types.EntityType.TURTLE).cancel(16);
        this.filter().type(Entity1_13Types.EntityType.TURTLE).cancel(17);
        this.filter().type(Entity1_13Types.EntityType.TURTLE).cancel(18);
        this.filter().filterFamily(Entity1_13Types.EntityType.ABSTRACT_FISHES).cancel(12);
        this.filter().filterFamily(Entity1_13Types.EntityType.ABSTRACT_FISHES).cancel(13);
        this.filter().type(Entity1_13Types.EntityType.PHANTOM).cancel(12);
        this.filter().type(Entity1_13Types.EntityType.BOAT).cancel(12);
        this.filter().type(Entity1_13Types.EntityType.TRIDENT).cancel(7);
        this.filter().type(Entity1_13Types.EntityType.WOLF).index(17).handler((event, meta) -> meta.setValue(15 - (int)meta.getValue()));
        this.filter().type(Entity1_13Types.EntityType.AREA_EFFECT_CLOUD).index(9).handler((event, meta) -> {
            final Particle particle = (Particle)meta.getValue();
            final ParticleMapping.ParticleData data = ParticleMapping.getMapping(particle.getId());
            int firstArg = 0;
            int secondArg = 0;
            final int[] particleArgs = data.rewriteMeta((Protocol1_12_2To1_13)this.protocol, particle.getArguments());
            if (particleArgs != null && particleArgs.length != 0) {
                if (data.getHandler().isBlockHandler() && particleArgs[0] == 0) {
                    particleArgs[0] = 102;
                }
                firstArg = particleArgs[0];
                secondArg = ((particleArgs.length == 2) ? particleArgs[1] : 0);
            }
            event.createExtraMeta(new Metadata(9, MetaType1_12.VarInt, data.getHistoryId()));
            event.createExtraMeta(new Metadata(10, MetaType1_12.VarInt, firstArg));
            event.createExtraMeta(new Metadata(11, MetaType1_12.VarInt, secondArg));
            event.cancel();
        });
    }
    
    @Override
    public EntityType typeFromId(final int typeId) {
        return Entity1_13Types.getTypeFromId(typeId, false);
    }
    
    @Override
    protected EntityType getObjectTypeFromId(final int typeId) {
        return Entity1_13Types.getTypeFromId(typeId, true);
    }
    
    @Override
    public int newEntityId(final int newId) {
        return EntityTypeMapping.getOldId(newId);
    }
    
    private static double toSet(final int field, final int bitIndex, final double origin, final double packetValue) {
        return ((field & 1 << bitIndex) != 0x0) ? (origin + packetValue) : packetValue;
    }
}
