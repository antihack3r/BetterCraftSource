// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.storage.BiomeStorage;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.rewriter.TagRewriter;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.data.CommandRewriter1_16_2;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.packets.BlockItemPackets1_16_2;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.packets.EntityPackets1_16_2;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viabackwards.api.BackwardsProtocol;

public class Protocol1_16_1To1_16_2 extends BackwardsProtocol<ClientboundPackets1_16_2, ClientboundPackets1_16, ServerboundPackets1_16_2, ServerboundPackets1_16>
{
    public static final BackwardsMappings MAPPINGS;
    private final EntityPackets1_16_2 entityRewriter;
    private final BlockItemPackets1_16_2 blockItemPackets;
    private final TranslatableRewriter<ClientboundPackets1_16_2> translatableRewriter;
    
    public Protocol1_16_1To1_16_2() {
        super(ClientboundPackets1_16_2.class, ClientboundPackets1_16.class, ServerboundPackets1_16_2.class, ServerboundPackets1_16.class);
        this.entityRewriter = new EntityPackets1_16_2(this);
        this.blockItemPackets = new BlockItemPackets1_16_2(this);
        this.translatableRewriter = new TranslatableRewriter<ClientboundPackets1_16_2>(this);
    }
    
    @Override
    protected void registerPackets() {
        super.registerPackets();
        this.translatableRewriter.registerBossBar(ClientboundPackets1_16_2.BOSSBAR);
        this.translatableRewriter.registerCombatEvent(ClientboundPackets1_16_2.COMBAT_EVENT);
        this.translatableRewriter.registerDisconnect(ClientboundPackets1_16_2.DISCONNECT);
        this.translatableRewriter.registerTabList(ClientboundPackets1_16_2.TAB_LIST);
        this.translatableRewriter.registerTitle(ClientboundPackets1_16_2.TITLE);
        this.translatableRewriter.registerOpenWindow(ClientboundPackets1_16_2.OPEN_WINDOW);
        this.translatableRewriter.registerPing();
        new CommandRewriter1_16_2(this).registerDeclareCommands(ClientboundPackets1_16_2.DECLARE_COMMANDS);
        final SoundRewriter<ClientboundPackets1_16_2> soundRewriter = new SoundRewriter<ClientboundPackets1_16_2>(this);
        soundRewriter.registerSound(ClientboundPackets1_16_2.SOUND);
        soundRewriter.registerSound(ClientboundPackets1_16_2.ENTITY_SOUND);
        soundRewriter.registerNamedSound(ClientboundPackets1_16_2.NAMED_SOUND);
        soundRewriter.registerStopSound(ClientboundPackets1_16_2.STOP_SOUND);
        ((AbstractProtocol<ClientboundPackets1_16_2, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_16_2.CHAT_MESSAGE, wrapper -> {
            final JsonElement message = wrapper.passthrough(Type.COMPONENT);
            this.translatableRewriter.processText(message);
            final byte position = wrapper.passthrough((Type<Byte>)Type.BYTE);
            if (position == 2) {
                wrapper.clearPacket();
                wrapper.setPacketType(ClientboundPackets1_16.TITLE);
                wrapper.write(Type.VAR_INT, 2);
                wrapper.write(Type.COMPONENT, message);
            }
            return;
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_16>)this).registerServerbound(ServerboundPackets1_16.RECIPE_BOOK_DATA, wrapper -> {
            final int type = wrapper.read((Type<Integer>)Type.VAR_INT);
            if (type == 0) {
                wrapper.passthrough(Type.STRING);
                wrapper.setPacketType(ServerboundPackets1_16_2.SEEN_RECIPE);
            }
            else {
                wrapper.cancel();
                for (int i = 0; i < 3; ++i) {
                    sendSeenRecipePacket(i, wrapper);
                }
            }
            return;
        });
        new TagRewriter<ClientboundPackets1_16_2>(this).register(ClientboundPackets1_16_2.TAGS, RegistryType.ENTITY);
        new StatisticsRewriter<ClientboundPackets1_16_2>(this).register(ClientboundPackets1_16_2.STATISTICS);
    }
    
    private static void sendSeenRecipePacket(final int recipeType, final PacketWrapper wrapper) throws Exception {
        final boolean open = wrapper.read((Type<Boolean>)Type.BOOLEAN);
        final boolean filter = wrapper.read((Type<Boolean>)Type.BOOLEAN);
        final PacketWrapper newPacket = wrapper.create(ServerboundPackets1_16_2.RECIPE_BOOK_DATA);
        newPacket.write(Type.VAR_INT, recipeType);
        newPacket.write(Type.BOOLEAN, open);
        newPacket.write(Type.BOOLEAN, filter);
        newPacket.sendToServer(Protocol1_16_1To1_16_2.class);
    }
    
    @Override
    public void init(final UserConnection user) {
        user.put(new BiomeStorage());
        user.addEntityTracker(this.getClass(), new EntityTrackerBase(user, Entity1_16_2Types.PLAYER));
    }
    
    @Override
    public TranslatableRewriter<ClientboundPackets1_16_2> getTranslatableRewriter() {
        return this.translatableRewriter;
    }
    
    @Override
    public BackwardsMappings getMappingData() {
        return Protocol1_16_1To1_16_2.MAPPINGS;
    }
    
    @Override
    public EntityPackets1_16_2 getEntityRewriter() {
        return this.entityRewriter;
    }
    
    @Override
    public BlockItemPackets1_16_2 getItemRewriter() {
        return this.blockItemPackets;
    }
    
    static {
        MAPPINGS = new BackwardsMappings("1.16.2", "1.16", Protocol1_16_2To1_16_1.class);
    }
}
