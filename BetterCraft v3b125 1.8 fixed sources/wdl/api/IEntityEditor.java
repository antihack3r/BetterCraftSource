/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.entity.Entity;
import wdl.api.IWDLMod;

public interface IEntityEditor
extends IWDLMod {
    public boolean shouldEdit(Entity var1);

    public void editEntity(Entity var1);
}

