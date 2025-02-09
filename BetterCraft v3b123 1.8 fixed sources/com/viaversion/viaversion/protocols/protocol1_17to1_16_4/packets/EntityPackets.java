// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import java.util.Iterator;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_17;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.rewriter.EntityRewriter;

public final class EntityPackets extends EntityRewriter<ClientboundPackets1_16_2, Protocol1_17To1_16_4>
{
    public EntityPackets(final Protocol1_17To1_16_4 protocol) {
        super(protocol);
        this.mapTypes(Entity1_16_2Types.values(), Entity1_17Types.class);
    }
    
    public void registerPackets() {
        ((EntityRewriter<ClientboundPackets1_16_2, T>)this).registerTrackerWithData(ClientboundPackets1_16_2.SPAWN_ENTITY, Entity1_17Types.FALLING_BLOCK);
        ((EntityRewriter<ClientboundPackets1_16_2, T>)this).registerTracker(ClientboundPackets1_16_2.SPAWN_MOB);
        ((EntityRewriter<ClientboundPackets1_16_2, T>)this).registerTracker(ClientboundPackets1_16_2.SPAWN_PLAYER, Entity1_17Types.PLAYER);
        ((EntityRewriter<ClientboundPackets1_16_2, T>)this).registerMetadataRewriter(ClientboundPackets1_16_2.ENTITY_METADATA, Types1_16.METADATA_LIST, Types1_17.METADATA_LIST);
        this.protocol.registerClientbound(ClientboundPackets1_16_2.DESTROY_ENTITIES, null, wrapper -> {
            final int[] entityIds = wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
            wrapper.cancel();
            final EntityTracker entityTracker = wrapper.user().getEntityTracker(Protocol1_17To1_16_4.class);
            final int[] array;
            int i = 0;
            for (int length = array.length; i < length; ++i) {
                final int entityId = array[i];
                entityTracker.removeEntity(entityId);
                final PacketWrapper newPacket = wrapper.create(ClientboundPackets1_17.REMOVE_ENTITY);
                newPacket.write(Type.VAR_INT, entityId);
                newPacket.send(Protocol1_17To1_16_4.class);
            }
            return;
        });
        ((AbstractProtocol<ClientboundPackets1_16_2, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_16_2.JOIN_GAME, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map(Type.STRING_ARRAY);
                this.map(Type.NBT);
                this.map(Type.NBT);
                this.handler(wrapper -> {
                    final CompoundTag dimensionRegistry = wrapper.get(Type.NBT, 0).get("minecraft:dimension_type");
                    final ListTag dimensions = dimensionRegistry.get("value");
                    dimensions.iterator();
                    final Iterator iterator;
                    while (iterator.hasNext()) {
                        final Tag dimension = iterator.next();
                        final CompoundTag dimensionCompound = ((CompoundTag)dimension).get("element");
                        addNewDimensionData(dimensionCompound);
                    }
                    final CompoundTag currentDimensionTag = wrapper.get(Type.NBT, 1);
                    addNewDimensionData(currentDimensionTag);
                    return;
                });
                this.handler(EntityPackets.this.playerTrackerHandler());
            }
        });
        ((AbstractProtocol<ClientboundPackets1_16_2, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_16_2.RESPAWN, wrapper -> {
            final CompoundTag dimensionData = wrapper.passthrough(Type.NBT);
            addNewDimensionData(dimensionData);
            return;
        });
        ((AbstractProtocol<ClientboundPackets1_16_2, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_16_2.ENTITY_PROPERTIES, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> wrapper.write((Type<Object>)Type.VAR_INT, wrapper.read((Type<T>)Type.INT)));
            }
        });
        ((AbstractProtocol<ClientboundPackets1_16_2, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_16_2.PLAYER_POSITION, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.DOUBLE);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> wrapper.write(Type.BOOLEAN, false));
            }
        });
        this.protocol.registerClientbound(ClientboundPackets1_16_2.COMBAT_EVENT, null, wrapper -> {
            final int type = wrapper.read((Type<Integer>)Type.VAR_INT);
            ClientboundPacketType packetType = null;
            switch (type) {
                case 0: {
                    packetType = ClientboundPackets1_17.COMBAT_ENTER;
                    break;
                }
                case 1: {
                    packetType = ClientboundPackets1_17.COMBAT_END;
                    break;
                }
                case 2: {
                    packetType = ClientboundPackets1_17.COMBAT_KILL;
                    break;
                }
                default: {
                    new IllegalArgumentException("Invalid combat type received: " + type);
                    throw;
                }
            }
            wrapper.setPacketType(packetType);
            return;
        });
        ((AbstractProtocol<ClientboundPackets1_16_2, CM, SM, SU>)this.protocol).cancelClientbound(ClientboundPackets1_16_2.ENTITY_MOVEMENT);
    }
    
    @Override
    protected void registerRewrites() {
        this.filter().handler((event, meta) -> {
            meta.setMetaType(Types1_17.META_TYPES.byId(meta.metaType().typeId()));
            if (meta.metaType() == Types1_17.META_TYPES.poseType) {
                final int pose = meta.value();
                if (pose > 5) {
                    meta.setValue(pose + 1);
                }
            }
            return;
        });
        this.registerMetaTypeHandler(Types1_17.META_TYPES.itemType, Types1_17.META_TYPES.blockStateType, null, Types1_17.META_TYPES.particleType);
        this.filter().filterFamily(Entity1_17Types.ENTITY).addIndex(7);
        this.filter().filterFamily(Entity1_17Types.MINECART_ABSTRACT).index(11).handler((event, meta) -> {
            final int data = (int)meta.getValue();
            meta.setValue(((Protocol1_17To1_16_4)this.protocol).getMappingData().getNewBlockStateId(data));
            return;
        });
        this.filter().type(Entity1_17Types.SHULKER).removeIndex(17);
    }
    
    @Override
    public EntityType typeFromId(final int type) {
        return Entity1_17Types.getTypeFromId(type);
    }
    
    private static void addNewDimensionData(final CompoundTag tag) {
        tag.put("min_y", new IntTag(0));
        tag.put("height", new IntTag(256));
    }
}
