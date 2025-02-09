/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addon.online;

import java.io.File;

public interface CallbackAddonDownloadProcess {
    public void progress(double var1);

    public void success(File var1);

    public void failed(String var1);
}

