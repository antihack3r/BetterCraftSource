/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer;

import com.google.common.base.Preconditions;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.ISyntheticClassInfo;

public abstract class SyntheticClassInfo
implements ISyntheticClassInfo {
    protected final IMixinInfo mixin;
    protected final String name;

    protected SyntheticClassInfo(IMixinInfo mixin, String name) {
        Preconditions.checkNotNull(mixin, "parent");
        Preconditions.checkNotNull(name, "name");
        this.mixin = mixin;
        this.name = name.replace('.', '/');
    }

    @Override
    public final IMixinInfo getMixin() {
        return this.mixin;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getClassName() {
        return this.name.replace('/', '.');
    }
}

