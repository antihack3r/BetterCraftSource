/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import wdl.api.IWDLMod;

public interface ISpecialEntityHandler
extends IWDLMod {
    public Multimap<String, String> getSpecialEntities();

    public String getSpecialEntityName(Entity var1);

    public String getSpecialEntityCategory(String var1);

    public int getSpecialEntityTrackDistance(String var1);
}

