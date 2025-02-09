/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers;

import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers.BackwardsBlockEntityProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class PistonHandler
implements BackwardsBlockEntityProvider.BackwardsBlockEntityHandler {
    private final Map<String, Integer> pistonIds = new HashMap<String, Integer>();

    public PistonHandler() {
        if (Via.getConfig().isServersideBlockConnections()) {
            Object2IntMap<String> keyToId = ConnectionData.getKeyToId();
            for (Map.Entry entry : keyToId.entrySet()) {
                if (!((String)entry.getKey()).contains("piston")) continue;
                this.addEntries((String)entry.getKey(), (Integer)entry.getValue());
            }
        } else {
            ListTag blockStates = (ListTag)MappingDataLoader.loadNBT("blockstates-1.13.nbt").get("blockstates");
            for (int id2 = 0; id2 < blockStates.size(); ++id2) {
                StringTag state = (StringTag)blockStates.get(id2);
                String key = state.getValue();
                if (!key.contains("piston")) continue;
                this.addEntries(key, id2);
            }
        }
    }

    private void addEntries(String data, int id2) {
        id2 = Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(id2);
        this.pistonIds.put(data, id2);
        String substring = data.substring(10);
        if (!substring.startsWith("piston") && !substring.startsWith("sticky_piston")) {
            return;
        }
        String[] split = data.substring(0, data.length() - 1).split("\\[");
        String[] properties = split[1].split(",");
        data = split[0] + "[" + properties[1] + "," + properties[0] + "]";
        this.pistonIds.put(data, id2);
    }

    @Override
    public CompoundTag transform(UserConnection user, int blockId, CompoundTag tag) {
        CompoundTag blockState = (CompoundTag)tag.get("blockState");
        if (blockState == null) {
            return tag;
        }
        String dataFromTag = this.getDataFromTag(blockState);
        if (dataFromTag == null) {
            return tag;
        }
        Integer id2 = this.pistonIds.get(dataFromTag);
        if (id2 == null) {
            return tag;
        }
        tag.put("blockId", new IntTag(id2 >> 4));
        tag.put("blockData", new IntTag((int)(id2 & 0xF)));
        return tag;
    }

    private String getDataFromTag(CompoundTag tag) {
        StringTag name = (StringTag)tag.get("Name");
        if (name == null) {
            return null;
        }
        CompoundTag properties = (CompoundTag)tag.get("Properties");
        if (properties == null) {
            return name.getValue();
        }
        StringJoiner joiner = new StringJoiner(",", name.getValue() + "[", "]");
        for (Map.Entry<String, Tag> entry : properties) {
            if (!(entry.getValue() instanceof StringTag)) continue;
            joiner.add(entry.getKey() + "=" + ((StringTag)entry.getValue()).getValue());
        }
        return joiner.toString();
    }
}

