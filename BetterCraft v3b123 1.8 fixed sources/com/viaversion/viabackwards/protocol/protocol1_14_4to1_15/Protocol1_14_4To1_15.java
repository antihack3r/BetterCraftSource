// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viabackwards.protocol.protocol1_14_4to1_15;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.data.ImmediateRespawn;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.rewriter.TagRewriter;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets.BlockItemPackets1_15;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets.EntityPackets1_15;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viabackwards.api.BackwardsProtocol;

public class Protocol1_14_4To1_15 extends BackwardsProtocol<ClientboundPackets1_15, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14>
{
    public static final BackwardsMappings MAPPINGS;
    private final EntityPackets1_15 entityRewriter;
    private final BlockItemPackets1_15 blockItemPackets;
    private final TranslatableRewriter<ClientboundPackets1_15> translatableRewriter;
    
    public Protocol1_14_4To1_15() {
        super(ClientboundPackets1_15.class, ClientboundPackets1_14.class, ServerboundPackets1_14.class, ServerboundPackets1_14.class);
        this.entityRewriter = new EntityPackets1_15(this);
        this.blockItemPackets = new BlockItemPackets1_15(this);
        this.translatableRewriter = new TranslatableRewriter<ClientboundPackets1_15>(this);
    }
    
    @Override
    protected void registerPackets() {
        super.registerPackets();
        this.translatableRewriter.registerBossBar(ClientboundPackets1_15.BOSSBAR);
        this.translatableRewriter.registerComponentPacket(ClientboundPackets1_15.CHAT_MESSAGE);
        this.translatableRewriter.registerCombatEvent(ClientboundPackets1_15.COMBAT_EVENT);
        this.translatableRewriter.registerDisconnect(ClientboundPackets1_15.DISCONNECT);
        this.translatableRewriter.registerOpenWindow(ClientboundPackets1_15.OPEN_WINDOW);
        this.translatableRewriter.registerTabList(ClientboundPackets1_15.TAB_LIST);
        this.translatableRewriter.registerTitle(ClientboundPackets1_15.TITLE);
        this.translatableRewriter.registerPing();
        final SoundRewriter<ClientboundPackets1_15> soundRewriter = new SoundRewriter<ClientboundPackets1_15>(this);
        soundRewriter.registerSound(ClientboundPackets1_15.SOUND);
        soundRewriter.registerSound(ClientboundPackets1_15.ENTITY_SOUND);
        soundRewriter.registerNamedSound(ClientboundPackets1_15.NAMED_SOUND);
        soundRewriter.registerStopSound(ClientboundPackets1_15.STOP_SOUND);
        ((AbstractProtocol<ClientboundPackets1_15, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_15.EXPLOSION, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.FLOAT);
                this.map((Type<Object>)Type.FLOAT);
                this.handler(wrapper -> {
                    final PacketWrapper soundPacket = wrapper.create(ClientboundPackets1_14.SOUND);
                    soundPacket.write(Type.VAR_INT, 243);
                    soundPacket.write(Type.VAR_INT, 4);
                    soundPacket.write(Type.INT, this.toEffectCoordinate(wrapper.get((Type<Float>)Type.FLOAT, 0)));
                    soundPacket.write(Type.INT, this.toEffectCoordinate(wrapper.get((Type<Float>)Type.FLOAT, 1)));
                    soundPacket.write(Type.INT, this.toEffectCoordinate(wrapper.get((Type<Float>)Type.FLOAT, 2)));
                    soundPacket.write(Type.FLOAT, 4.0f);
                    soundPacket.write(Type.FLOAT, 1.0f);
                    soundPacket.send(Protocol1_14_4To1_15.class);
                });
            }
            
            private int toEffectCoordinate(final float coordinate) {
                return (int)(coordinate * 8.0f);
            }
        });
        new TagRewriter<ClientboundPackets1_15>(this).register(ClientboundPackets1_15.TAGS, RegistryType.ENTITY);
        new StatisticsRewriter<ClientboundPackets1_15>(this).register(ClientboundPackets1_15.STATISTICS);
    }
    
    @Override
    public void init(final UserConnection user) {
        user.put(new ImmediateRespawn());
        user.addEntityTracker(this.getClass(), new EntityTrackerBase(user, Entity1_15Types.PLAYER));
    }
    
    @Override
    public BackwardsMappings getMappingData() {
        return Protocol1_14_4To1_15.MAPPINGS;
    }
    
    @Override
    public EntityPackets1_15 getEntityRewriter() {
        return this.entityRewriter;
    }
    
    @Override
    public BlockItemPackets1_15 getItemRewriter() {
        return this.blockItemPackets;
    }
    
    @Override
    public TranslatableRewriter<ClientboundPackets1_15> getTranslatableRewriter() {
        return this.translatableRewriter;
    }
    
    static {
        MAPPINGS = new BackwardsMappings("1.15", "1.14", Protocol1_15To1_14_4.class);
    }
}
