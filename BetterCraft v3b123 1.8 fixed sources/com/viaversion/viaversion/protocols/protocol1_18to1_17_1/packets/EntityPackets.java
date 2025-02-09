// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_18to1_17_1.packets;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.storage.ChunkLightStorage;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.types.version.Types1_18;
import com.viaversion.viaversion.api.type.types.version.Types1_17;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.Protocol1_18To1_17_1;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.rewriter.EntityRewriter;

public final class EntityPackets extends EntityRewriter<ClientboundPackets1_17_1, Protocol1_18To1_17_1>
{
    public EntityPackets(final Protocol1_18To1_17_1 protocol) {
        super(protocol);
    }
    
    public void registerPackets() {
        ((EntityRewriter<ClientboundPackets1_17_1, T>)this).registerMetadataRewriter(ClientboundPackets1_17_1.ENTITY_METADATA, Types1_17.METADATA_LIST, Types1_18.METADATA_LIST);
        ((AbstractProtocol<ClientboundPackets1_17_1, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_17_1.JOIN_GAME, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.BOOLEAN);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.BYTE);
                this.map(Type.STRING_ARRAY);
                this.map(Type.NBT);
                this.map(Type.NBT);
                this.map(Type.STRING);
                this.map((Type<Object>)Type.LONG);
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> {
                    final int chunkRadius = wrapper.passthrough((Type<Integer>)Type.VAR_INT);
                    wrapper.write(Type.VAR_INT, chunkRadius);
                    return;
                });
                this.handler(EntityPackets.this.worldDataTrackerHandler(1));
                this.handler(EntityPackets.this.biomeSizeTracker());
            }
        });
        ((AbstractProtocol<ClientboundPackets1_17_1, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_17_1.RESPAWN, new PacketHandlers() {
            public void register() {
                this.map(Type.NBT);
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    final String world = wrapper.get(Type.STRING, 0);
                    final EntityTracker tracker = EntityPackets.this.tracker(wrapper.user());
                    if (!world.equals(tracker.currentWorld())) {
                        wrapper.user().get(ChunkLightStorage.class).clear();
                    }
                    return;
                });
                this.handler(EntityPackets.this.worldDataTrackerHandler(0));
            }
        });
    }
    
    @Override
    protected void registerRewrites() {
        this.filter().handler((event, meta) -> {
            meta.setMetaType(Types1_18.META_TYPES.byId(meta.metaType().typeId()));
            if (meta.metaType() == Types1_18.META_TYPES.particleType) {
                final Particle particle = (Particle)meta.getValue();
                if (particle.getId() == 2) {
                    particle.setId(3);
                    particle.getArguments().add(new Particle.ParticleData(Type.VAR_INT, 7754));
                }
                else if (particle.getId() == 3) {
                    particle.getArguments().add(new Particle.ParticleData(Type.VAR_INT, 7786));
                }
                else {
                    this.rewriteParticle(particle);
                }
            }
            return;
        });
        this.registerMetaTypeHandler(Types1_18.META_TYPES.itemType, null, null, null);
    }
    
    @Override
    public EntityType typeFromId(final int type) {
        return Entity1_17Types.getTypeFromId(type);
    }
}
