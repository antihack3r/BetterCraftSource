/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.util.Key;
import java.util.LinkedHashMap;
import java.util.Map;

public final class WrappedBlockData {
    private final LinkedHashMap<String, String> blockData = new LinkedHashMap();
    private final String minecraftKey;
    private final int savedBlockStateId;

    public static WrappedBlockData fromString(String s2) {
        String[] array = s2.split("\\[");
        String key = array[0];
        WrappedBlockData wrappedBlockdata = new WrappedBlockData(key, ConnectionData.getId(s2));
        if (array.length > 1) {
            String[] data;
            String blockData = array[1];
            blockData = blockData.replace("]", "");
            for (String d2 : data = blockData.split(",")) {
                String[] a2 = d2.split("=");
                wrappedBlockdata.blockData.put(a2[0], a2[1]);
            }
        }
        return wrappedBlockdata;
    }

    private WrappedBlockData(String minecraftKey, int savedBlockStateId) {
        this.minecraftKey = Key.namespaced(minecraftKey);
        this.savedBlockStateId = savedBlockStateId;
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder(this.minecraftKey + "[");
        for (Map.Entry<String, String> entry : this.blockData.entrySet()) {
            sb2.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
        }
        return sb2.substring(0, sb2.length() - 1) + "]";
    }

    public String getMinecraftKey() {
        return this.minecraftKey;
    }

    public int getSavedBlockStateId() {
        return this.savedBlockStateId;
    }

    public int getBlockStateId() {
        return ConnectionData.getId(this.toString());
    }

    public WrappedBlockData set(String data, Object value) {
        if (!this.hasData(data)) {
            throw new UnsupportedOperationException("No blockdata found for " + data + " at " + this.minecraftKey);
        }
        this.blockData.put(data, value.toString());
        return this;
    }

    public String getValue(String data) {
        return this.blockData.get(data);
    }

    public boolean hasData(String key) {
        return this.blockData.containsKey(key);
    }
}

