// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_20to1_19_4.packets;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import java.util.Iterator;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.FloatTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.types.version.Types1_20;
import com.viaversion.viaversion.api.type.types.version.Types1_19_4;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_19_4Types;
import com.viaversion.viaversion.protocols.protocol1_20to1_19_4.Protocol1_20To1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.rewriter.EntityRewriter;

public final class EntityPackets extends EntityRewriter<ClientboundPackets1_19_4, Protocol1_20To1_19_4>
{
    public EntityPackets(final Protocol1_20To1_19_4 protocol) {
        super(protocol);
    }
    
    public void registerPackets() {
        ((EntityRewriter<ClientboundPackets1_19_4, T>)this).registerTrackerWithData1_19(ClientboundPackets1_19_4.SPAWN_ENTITY, Entity1_19_4Types.FALLING_BLOCK);
        ((EntityRewriter<ClientboundPackets1_19_4, T>)this).registerMetadataRewriter(ClientboundPackets1_19_4.ENTITY_METADATA, Types1_19_4.METADATA_LIST, Types1_20.METADATA_LIST);
        ((EntityRewriter<ClientboundPackets1_19_4, T>)this).registerRemoveEntities(ClientboundPackets1_19_4.REMOVE_ENTITIES);
        ((AbstractProtocol<ClientboundPackets1_19_4, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_19_4.JOIN_GAME, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map(Type.STRING_ARRAY);
                this.map(Type.NBT);
                this.map(Type.STRING);
                this.map(Type.STRING);
                this.map((Type<Object>)Type.LONG);
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map(Type.OPTIONAL_GLOBAL_POSITION);
                this.create(Type.VAR_INT, 0);
                this.handler(EntityPackets.this.dimensionDataHandler());
                this.handler(EntityPackets.this.biomeSizeTracker());
                this.handler(EntityPackets.this.worldDataTrackerHandlerByKey());
                this.handler(wrapper -> {
                    final CompoundTag registry = wrapper.get(Type.NBT, 0);
                    final CompoundTag damageTypeRegistry = registry.get("minecraft:damage_type");
                    final ListTag damageTypes = damageTypeRegistry.get("value");
                    int highestId = -1;
                    damageTypes.iterator();
                    final Iterator iterator;
                    while (iterator.hasNext()) {
                        final Tag damageType = iterator.next();
                        final IntTag id = ((CompoundTag)damageType).get("id");
                        highestId = Math.max(highestId, id.asInt());
                    }
                    final CompoundTag outsideBorderReason = new CompoundTag();
                    final CompoundTag outsideBorderElement = new CompoundTag();
                    outsideBorderElement.put("scaling", new StringTag("always"));
                    outsideBorderElement.put("exhaustion", new FloatTag(0.0f));
                    outsideBorderElement.put("message_id", new StringTag("badRespawnPoint"));
                    outsideBorderReason.put("id", new IntTag(highestId + 1));
                    outsideBorderReason.put("name", new StringTag("minecraft:outside_border"));
                    outsideBorderReason.put("element", outsideBorderElement);
                    damageTypes.add(outsideBorderReason);
                    final CompoundTag genericKillReason = new CompoundTag();
                    final CompoundTag genericKillElement = new CompoundTag();
                    genericKillElement.put("scaling", new StringTag("always"));
                    genericKillElement.put("exhaustion", new FloatTag(0.0f));
                    genericKillElement.put("message_id", new StringTag("badRespawnPoint"));
                    genericKillReason.put("id", new IntTag(highestId + 2));
                    genericKillReason.put("name", new StringTag("minecraft:generic_kill"));
                    genericKillReason.put("element", genericKillElement);
                    damageTypes.add(genericKillReason);
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_19_4, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_19_4.RESPAWN, new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.map(Type.STRING);
                this.map((Type<Object>)Type.LONG);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.BYTE);
                this.map(Type.OPTIONAL_GLOBAL_POSITION);
                this.create(Type.VAR_INT, 0);
                this.handler(EntityPackets.this.worldDataTrackerHandlerByKey());
            }
        });
    }
    
    @Override
    protected void registerRewrites() {
        this.filter().handler((event, meta) -> meta.setMetaType(Types1_20.META_TYPES.byId(meta.metaType().typeId())));
        this.registerMetaTypeHandler(Types1_20.META_TYPES.itemType, Types1_20.META_TYPES.blockStateType, Types1_20.META_TYPES.optionalBlockStateType, Types1_20.META_TYPES.particleType);
        this.filter().filterFamily(Entity1_19_4Types.MINECART_ABSTRACT).index(11).handler((event, meta) -> {
            final int blockState = meta.value();
            meta.setValue(((Protocol1_20To1_19_4)this.protocol).getMappingData().getNewBlockStateId(blockState));
        });
    }
    
    @Override
    public EntityType typeFromId(final int type) {
        return Entity1_19_4Types.getTypeFromId(type);
    }
}
