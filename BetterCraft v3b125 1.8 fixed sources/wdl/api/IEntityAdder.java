/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import java.util.List;
import wdl.api.IWDLMod;

public interface IEntityAdder
extends IWDLMod {
    public List<String> getModEntities();

    public int getDefaultEntityTrackDistance(String var1);

    public String getEntityCategory(String var1);
}

