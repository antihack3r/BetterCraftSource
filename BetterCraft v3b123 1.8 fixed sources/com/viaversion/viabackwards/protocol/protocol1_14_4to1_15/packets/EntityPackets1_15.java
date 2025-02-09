// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.data.EntityTypeMapping;
import java.util.ArrayList;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.data.ImmediateRespawn;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.Protocol1_14_4To1_15;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viabackwards.api.rewriters.EntityRewriter;

public class EntityPackets1_15 extends EntityRewriter<ClientboundPackets1_15, Protocol1_14_4To1_15>
{
    public EntityPackets1_15(final Protocol1_14_4To1_15 protocol) {
        super(protocol);
    }
    
    @Override
    protected void registerPackets() {
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.UPDATE_HEALTH, wrapper -> {
            final float health = wrapper.passthrough((Type<Float>)Type.FLOAT);
            if (health > 0.0f) {
                return;
            }
            else if (!wrapper.user().get(ImmediateRespawn.class).isImmediateRespawn()) {
                return;
            }
            else {
                final PacketWrapper statusPacket = wrapper.create(ServerboundPackets1_14.CLIENT_STATUS);
                statusPacket.write(Type.VAR_INT, 0);
                statusPacket.sendToServer(Protocol1_14_4To1_15.class);
                return;
            }
        });
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.GAME_EVENT, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.FLOAT);
                this.handler(wrapper -> {
                    if (wrapper.get((Type<Short>)Type.UNSIGNED_BYTE, 0) == 11) {
                        wrapper.user().get(ImmediateRespawn.class).setImmediateRespawn(wrapper.get((Type<Float>)Type.FLOAT, 0) == 1.0f);
                    }
                });
            }
        });
        ((EntityRewriter<ClientboundPackets1_15, T>)this).registerTrackerWithData(ClientboundPackets1_15.SPAWN_ENTITY, Entity1_15Types.FALLING_BLOCK);
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.SPAWN_MOB, new PacketHandlers() {
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
                this.handler(wrapper -> wrapper.write((Type<ArrayList>)Types1_14.METADATA_LIST, new ArrayList()));
                this.handler(wrapper -> {
                    final int type = wrapper.get((Type<Integer>)Type.VAR_INT, 1);
                    final EntityType entityType = Entity1_15Types.getTypeFromId(type);
                    EntityPackets1_15.this.tracker(wrapper.user()).addEntity(wrapper.get((Type<Integer>)Type.VAR_INT, 0), entityType);
                    wrapper.set(Type.VAR_INT, 1, EntityTypeMapping.getOldEntityId(type));
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.RESPAWN, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map(Type.LONG, Type.NOTHING);
            }
        });
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.JOIN_GAME, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.INT);
                this.map(Type.LONG, Type.NOTHING);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.BOOLEAN);
                this.handler(EntityRewriterBase.this.getTrackerHandler(Entity1_15Types.PLAYER, Type.INT));
                this.handler(wrapper -> {
                    final boolean immediateRespawn = !wrapper.read((Type<Boolean>)Type.BOOLEAN);
                    wrapper.user().get(ImmediateRespawn.class).setImmediateRespawn(immediateRespawn);
                });
            }
        });
        ((com.viaversion.viaversion.rewriter.EntityRewriter<ClientboundPackets1_15, T>)this).registerTracker(ClientboundPackets1_15.SPAWN_EXPERIENCE_ORB, Entity1_15Types.EXPERIENCE_ORB);
        ((com.viaversion.viaversion.rewriter.EntityRewriter<ClientboundPackets1_15, T>)this).registerTracker(ClientboundPackets1_15.SPAWN_GLOBAL_ENTITY, Entity1_15Types.LIGHTNING_BOLT);
        ((com.viaversion.viaversion.rewriter.EntityRewriter<ClientboundPackets1_15, T>)this).registerTracker(ClientboundPackets1_15.SPAWN_PAINTING, Entity1_15Types.PAINTING);
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.SPAWN_PLAYER, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.UUID);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.handler(wrapper -> wrapper.write((Type<ArrayList>)Types1_14.METADATA_LIST, new ArrayList()));
                this.handler(EntityRewriterBase.this.getTrackerHandler(Entity1_15Types.PLAYER, Type.VAR_INT));
            }
        });
        ((com.viaversion.viaversion.rewriter.EntityRewriter<ClientboundPackets1_15, T>)this).registerRemoveEntities(ClientboundPackets1_15.DESTROY_ENTITIES);
        ((com.viaversion.viaversion.rewriter.EntityRewriter<ClientboundPackets1_15, T>)this).registerMetadataRewriter(ClientboundPackets1_15.ENTITY_METADATA, Types1_14.METADATA_LIST);
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_15.ENTITY_PROPERTIES, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.INT);
                this.handler(wrapper -> {
                    final int entityId = wrapper.get((Type<Integer>)Type.VAR_INT, 0);
                    final EntityType entityType = EntityPackets1_15.this.tracker(wrapper.user()).entityType(entityId);
                    if (entityType == Entity1_15Types.BEE) {
                        int newSize;
                        final int size = newSize = wrapper.get((Type<Integer>)Type.INT, 0);
                        for (int i = 0; i < size; ++i) {
                            final String key = wrapper.read(Type.STRING);
                            if (key.equals("generic.flyingSpeed")) {
                                --newSize;
                                wrapper.read((Type<Object>)Type.DOUBLE);
                                for (int modSize = wrapper.read((Type<Integer>)Type.VAR_INT), j = 0; j < modSize; ++j) {
                                    wrapper.read(Type.UUID);
                                    wrapper.read((Type<Object>)Type.DOUBLE);
                                    wrapper.read((Type<Object>)Type.BYTE);
                                }
                            }
                            else {
                                wrapper.write(Type.STRING, key);
                                wrapper.passthrough((Type<Object>)Type.DOUBLE);
                                for (int modSize2 = wrapper.passthrough((Type<Integer>)Type.VAR_INT), k = 0; k < modSize2; ++k) {
                                    wrapper.passthrough(Type.UUID);
                                    wrapper.passthrough((Type<Object>)Type.DOUBLE);
                                    wrapper.passthrough((Type<Object>)Type.BYTE);
                                }
                            }
                        }
                        if (newSize != size) {
                            wrapper.set(Type.INT, 0, newSize);
                        }
                    }
                });
            }
        });
    }
    
    @Override
    protected void registerRewrites() {
        this.registerMetaTypeHandler(Types1_14.META_TYPES.itemType, Types1_14.META_TYPES.blockStateType, null, Types1_14.META_TYPES.particleType, Types1_14.META_TYPES.componentType, Types1_14.META_TYPES.optionalComponentType);
        this.filter().filterFamily(Entity1_15Types.LIVINGENTITY).removeIndex(12);
        this.filter().type(Entity1_15Types.BEE).cancel(15);
        this.filter().type(Entity1_15Types.BEE).cancel(16);
        this.mapEntityTypeWithData(Entity1_15Types.BEE, Entity1_15Types.PUFFERFISH).jsonName().spawnMetadata(storage -> {
            storage.add(new Metadata(14, Types1_14.META_TYPES.booleanType, false));
            storage.add(new Metadata(15, Types1_14.META_TYPES.varIntType, 2));
            return;
        });
        this.filter().type(Entity1_15Types.ENDERMAN).cancel(16);
        this.filter().type(Entity1_15Types.TRIDENT).cancel(10);
        this.filter().type(Entity1_15Types.WOLF).addIndex(17);
        this.filter().type(Entity1_15Types.WOLF).index(8).handler((event, meta) -> event.createExtraMeta(new Metadata(17, Types1_14.META_TYPES.floatType, event.meta().value())));
    }
    
    @Override
    public EntityType typeFromId(final int typeId) {
        return Entity1_15Types.getTypeFromId(typeId);
    }
    
    @Override
    public int newEntityId(final int newId) {
        return EntityTypeMapping.getOldEntityId(newId);
    }
}
