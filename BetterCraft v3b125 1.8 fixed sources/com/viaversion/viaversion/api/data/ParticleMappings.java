/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.data.FullMappingsBase;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import java.util.List;

public class ParticleMappings
extends FullMappingsBase {
    private final IntList itemParticleIds = new IntArrayList(4);
    private final IntList blockParticleIds = new IntArrayList(4);

    public ParticleMappings(List<String> unmappedIdentifiers, List<String> mappedIdentifiers, Mappings mappings) {
        super(unmappedIdentifiers, mappedIdentifiers, mappings);
        this.addBlockParticle("block");
        this.addBlockParticle("falling_dust");
        this.addBlockParticle("block_marker");
        this.addItemParticle("item");
    }

    public boolean addItemParticle(String identifier) {
        int id2 = this.id(identifier);
        return id2 != -1 && this.itemParticleIds.add(id2);
    }

    public boolean addBlockParticle(String identifier) {
        int id2 = this.id(identifier);
        return id2 != -1 && this.blockParticleIds.add(id2);
    }

    public boolean isBlockParticle(int id2) {
        return this.blockParticleIds.contains(id2);
    }

    public boolean isItemParticle(int id2) {
        return this.itemParticleIds.contains(id2);
    }

    @Deprecated
    public int getBlockId() {
        return this.id("block");
    }

    @Deprecated
    public int getFallingDustId() {
        return this.id("falling_dust");
    }

    @Deprecated
    public int getItemId() {
        return this.id("item");
    }
}

