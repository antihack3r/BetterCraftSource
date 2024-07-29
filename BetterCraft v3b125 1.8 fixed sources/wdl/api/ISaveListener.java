/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import java.io.File;
import wdl.api.IWDLMod;

public interface ISaveListener
extends IWDLMod {
    public void afterChunksSaved(File var1);
}

