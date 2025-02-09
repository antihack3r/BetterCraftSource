/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.StatisticMappings;
import java.util.HashMap;
import java.util.Map;

public class BackwardsMappings
extends com.viaversion.viabackwards.api.data.BackwardsMappings {
    private final Int2ObjectMap<String> statisticMappings = new Int2ObjectOpenHashMap<String>();
    private final Map<String, String> translateMappings = new HashMap<String, String>();

    public BackwardsMappings() {
        super("1.13", "1.12", Protocol1_13To1_12_2.class);
    }

    @Override
    public void loadExtras(CompoundTag data) {
        super.loadExtras(data);
        for (Map.Entry<String, Integer> entry : StatisticMappings.CUSTOM_STATS.entrySet()) {
            this.statisticMappings.put((int)entry.getValue(), entry.getKey());
        }
        for (Map.Entry<String, Object> entry : Protocol1_13To1_12_2.MAPPINGS.getTranslateMapping().entrySet()) {
            this.translateMappings.put((String)entry.getValue(), entry.getKey());
        }
    }

    @Override
    public int getNewBlockStateId(int id2) {
        if (id2 >= 5635 && id2 <= 5650) {
            id2 = id2 < 5639 ? (id2 += 4) : (id2 < 5643 ? (id2 -= 4) : (id2 < 5647 ? (id2 += 4) : (id2 -= 4)));
        }
        int mappedId = super.getNewBlockStateId(id2);
        switch (mappedId) {
            case 1595: 
            case 1596: 
            case 1597: {
                return 1584;
            }
            case 1611: 
            case 1612: 
            case 1613: {
                return 1600;
            }
        }
        return mappedId;
    }

    @Override
    protected int checkValidity(int id2, int mappedId, String type) {
        return mappedId;
    }

    public Int2ObjectMap<String> getStatisticMappings() {
        return this.statisticMappings;
    }

    public Map<String, String> getTranslateMappings() {
        return this.translateMappings;
    }
}

