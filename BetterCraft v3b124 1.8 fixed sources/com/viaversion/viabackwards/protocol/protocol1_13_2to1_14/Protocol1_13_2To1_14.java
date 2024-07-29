/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.data.CommandRewriter1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.BlockItemPackets1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.EntityPackets1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.PlayerPackets1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets.SoundPackets1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.ChunkLightStorage;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.DifficultyStorage;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_14;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;

public class Protocol1_13_2To1_14
extends BackwardsProtocol<ClientboundPackets1_14, ClientboundPackets1_13, ServerboundPackets1_14, ServerboundPackets1_13> {
    public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.14", "1.13.2", Protocol1_14To1_13_2.class);
    private final EntityPackets1_14 entityRewriter = new EntityPackets1_14(this);
    private final BlockItemPackets1_14 blockItemPackets = new BlockItemPackets1_14(this);
    private final TranslatableRewriter<ClientboundPackets1_14> translatableRewriter = new TranslatableRewriter<ClientboundPackets1_14>(this, ComponentRewriter.ReadType.JSON);

    public Protocol1_13_2To1_14() {
        super(ClientboundPackets1_14.class, ClientboundPackets1_13.class, ServerboundPackets1_14.class, ServerboundPackets1_13.class);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();
        this.translatableRewriter.registerBossBar(ClientboundPackets1_14.BOSSBAR);
        this.translatableRewriter.registerComponentPacket(ClientboundPackets1_14.CHAT_MESSAGE);
        this.translatableRewriter.registerCombatEvent(ClientboundPackets1_14.COMBAT_EVENT);
        this.translatableRewriter.registerComponentPacket(ClientboundPackets1_14.DISCONNECT);
        this.translatableRewriter.registerTabList(ClientboundPackets1_14.TAB_LIST);
        this.translatableRewriter.registerTitle(ClientboundPackets1_14.TITLE);
        this.translatableRewriter.registerPing();
        new CommandRewriter1_14(this).registerDeclareCommands(ClientboundPackets1_14.DECLARE_COMMANDS);
        new PlayerPackets1_14(this).register();
        new SoundPackets1_14(this).register();
        new StatisticsRewriter<ClientboundPackets1_14>(this).register(ClientboundPackets1_14.STATISTICS);
        this.cancelClientbound(ClientboundPackets1_14.UPDATE_VIEW_POSITION);
        this.cancelClientbound(ClientboundPackets1_14.UPDATE_VIEW_DISTANCE);
        this.registerClientbound(ClientboundPackets1_14.TAGS, (PacketWrapper wrapper) -> {
            int blockTagsSize = wrapper.passthrough(Type.VAR_INT);
            for (int i2 = 0; i2 < blockTagsSize; ++i2) {
                wrapper.passthrough(Type.STRING);
                int[] blockIds = wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                for (int j2 = 0; j2 < blockIds.length; ++j2) {
                    int blockId;
                    int id2 = blockIds[j2];
                    blockIds[j2] = blockId = MAPPINGS.getNewBlockId(id2);
                }
            }
            int itemTagsSize = wrapper.passthrough(Type.VAR_INT);
            for (int i3 = 0; i3 < itemTagsSize; ++i3) {
                wrapper.passthrough(Type.STRING);
                int[] itemIds = wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                for (int j3 = 0; j3 < itemIds.length; ++j3) {
                    int oldId;
                    int itemId = itemIds[j3];
                    itemIds[j3] = oldId = MAPPINGS.getItemMappings().getNewId(itemId);
                }
            }
            int fluidTagsSize = wrapper.passthrough(Type.VAR_INT);
            for (int i4 = 0; i4 < fluidTagsSize; ++i4) {
                wrapper.passthrough(Type.STRING);
                wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
            }
            int entityTagsSize = wrapper.read(Type.VAR_INT);
            for (int i5 = 0; i5 < entityTagsSize; ++i5) {
                wrapper.read(Type.STRING);
                wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
            }
        });
        this.registerClientbound(ClientboundPackets1_14.UPDATE_LIGHT, null, (PacketWrapper wrapper) -> {
            int x2 = wrapper.read(Type.VAR_INT);
            int z2 = wrapper.read(Type.VAR_INT);
            int skyLightMask = wrapper.read(Type.VAR_INT);
            int blockLightMask = wrapper.read(Type.VAR_INT);
            int emptySkyLightMask = wrapper.read(Type.VAR_INT);
            int emptyBlockLightMask = wrapper.read(Type.VAR_INT);
            byte[][] skyLight = new byte[16][];
            if (Protocol1_13_2To1_14.isSet(skyLightMask, 0)) {
                wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
            }
            for (int i2 = 0; i2 < 16; ++i2) {
                if (Protocol1_13_2To1_14.isSet(skyLightMask, i2 + 1)) {
                    skyLight[i2] = wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                    continue;
                }
                if (!Protocol1_13_2To1_14.isSet(emptySkyLightMask, i2 + 1)) continue;
                skyLight[i2] = ChunkLightStorage.EMPTY_LIGHT;
            }
            if (Protocol1_13_2To1_14.isSet(skyLightMask, 17)) {
                wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
            }
            byte[][] blockLight = new byte[16][];
            if (Protocol1_13_2To1_14.isSet(blockLightMask, 0)) {
                wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
            }
            for (int i3 = 0; i3 < 16; ++i3) {
                if (Protocol1_13_2To1_14.isSet(blockLightMask, i3 + 1)) {
                    blockLight[i3] = wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                    continue;
                }
                if (!Protocol1_13_2To1_14.isSet(emptyBlockLightMask, i3 + 1)) continue;
                blockLight[i3] = ChunkLightStorage.EMPTY_LIGHT;
            }
            if (Protocol1_13_2To1_14.isSet(blockLightMask, 17)) {
                wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
            }
            wrapper.user().get(ChunkLightStorage.class).setStoredLight(skyLight, blockLight, x2, z2);
            wrapper.cancel();
        });
    }

    private static boolean isSet(int mask, int i2) {
        return (mask & 1 << i2) != 0;
    }

    @Override
    public void init(UserConnection user) {
        if (!user.has(ClientWorld.class)) {
            user.put(new ClientWorld());
        }
        user.addEntityTracker(this.getClass(), new EntityTrackerBase(user, EntityTypes1_14.PLAYER));
        if (!user.has(ChunkLightStorage.class)) {
            user.put(new ChunkLightStorage());
        }
        user.put(new DifficultyStorage());
    }

    @Override
    public BackwardsMappings getMappingData() {
        return MAPPINGS;
    }

    public EntityPackets1_14 getEntityRewriter() {
        return this.entityRewriter;
    }

    public BlockItemPackets1_14 getItemRewriter() {
        return this.blockItemPackets;
    }

    @Override
    public TranslatableRewriter<ClientboundPackets1_14> getTranslatableRewriter() {
        return this.translatableRewriter;
    }
}

