/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.optifine.DynamicLight;

public class DynamicLightsMap {
    private Map<Integer, DynamicLight> map = new HashMap<Integer, DynamicLight>();
    private List<DynamicLight> list = new ArrayList<DynamicLight>();
    private boolean dirty = false;

    public DynamicLight put(int id2, DynamicLight dynamicLight) {
        DynamicLight dynamiclight = this.map.put(id2, dynamicLight);
        this.setDirty();
        return dynamiclight;
    }

    public DynamicLight get(int id2) {
        return this.map.get(id2);
    }

    public int size() {
        return this.map.size();
    }

    public DynamicLight remove(int id2) {
        DynamicLight dynamiclight = this.map.remove(id2);
        if (dynamiclight != null) {
            this.setDirty();
        }
        return dynamiclight;
    }

    public void clear() {
        this.map.clear();
        this.list.clear();
        this.setDirty();
    }

    private void setDirty() {
        this.dirty = true;
    }

    public List<DynamicLight> valueList() {
        if (this.dirty) {
            this.list.clear();
            this.list.addAll(this.map.values());
            this.dirty = false;
        }
        return this.list;
    }
}

