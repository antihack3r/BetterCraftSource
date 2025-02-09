/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_9_1_2to1_9_3_4.chunks;

import com.viaversion.viabackwards.protocol.protocol1_9_1_2to1_9_3_4.Protocol1_9_1_2To1_9_3_4;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockEntity {
    private static final Map<String, Integer> types = new HashMap<String, Integer>();

    public static void handle(List<CompoundTag> tags, UserConnection connection) {
        for (CompoundTag tag : tags) {
            try {
                if (!tag.contains("id")) {
                    throw new Exception("NBT tag not handled because the id key is missing");
                }
                String id2 = (String)((Tag)tag.get("id")).getValue();
                if (!types.containsKey(id2)) {
                    throw new Exception("Not handled id: " + id2);
                }
                int newId = types.get(id2);
                if (newId == -1) continue;
                int x2 = ((NumberTag)tag.get("x")).asInt();
                int y2 = ((NumberTag)tag.get("y")).asInt();
                int z2 = ((NumberTag)tag.get("z")).asInt();
                Position pos = new Position(x2, (short)y2, z2);
                BlockEntity.updateBlockEntity(pos, (short)newId, tag, connection);
            }
            catch (Exception e2) {
                if (!Via.getManager().isDebug()) continue;
                Via.getPlatform().getLogger().warning("Block Entity: " + e2.getMessage() + ": " + tag);
            }
        }
    }

    private static void updateBlockEntity(Position pos, short id2, CompoundTag tag, UserConnection connection) throws Exception {
        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9_3.BLOCK_ENTITY_DATA, null, connection);
        wrapper.write(Type.POSITION1_8, pos);
        wrapper.write(Type.UNSIGNED_BYTE, id2);
        wrapper.write(Type.NAMED_COMPOUND_TAG, tag);
        wrapper.scheduleSend(Protocol1_9_1_2To1_9_3_4.class, false);
    }

    static {
        types.put("MobSpawner", 1);
        types.put("Control", 2);
        types.put("Beacon", 3);
        types.put("Skull", 4);
        types.put("FlowerPot", 5);
        types.put("Banner", 6);
        types.put("UNKNOWN", 7);
        types.put("EndGateway", 8);
        types.put("Sign", 9);
    }
}

