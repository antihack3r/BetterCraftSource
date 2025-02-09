/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addon.online;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.online.CallbackAddonDownloadProcess;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.addon.online.info.OnlineAddonInfo;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;

public class AddonDownloader
extends Thread {
    private AddonInfo addonInfo;
    private CallbackAddonDownloadProcess callback;
    private File file;

    public AddonDownloader(OnlineAddonInfo addonInfo, CallbackAddonDownloadProcess callback) {
        this.addonInfo = addonInfo;
        this.callback = callback;
        if (addonInfo.isIncludeInJar()) {
            this.file = new File("LabyMod/ofhandler/", "optifine.jar");
            this.file.getParentFile().mkdir();
        } else {
            this.file = new File(AddonLoader.getAddonsDirectory(), String.valueOf(addonInfo.getName()) + ".jar");
        }
    }

    @Override
    public void run() {
        try {
            Debug.log(Debug.EnumDebugMode.ADDON, "Download addon " + this.addonInfo.getName() + " " + this.addonInfo.getDownloadURL());
            HttpURLConnection connection = (HttpURLConnection)new URL(this.addonInfo.getDownloadURL()).openConnection();
            connection.setRequestProperty("User-Agent", Source.getUserAgent());
            connection.connect();
            DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(this.file);
            int total = connection.getContentLength();
            int current = 0;
            boolean smooth = total < 1000000;
            byte[] buff = new byte[2048];
            int readBytes = 0;
            while ((readBytes = dataInputStream.read(buff, 0, buff.length)) != -1) {
                fileOutputStream.write(buff, 0, readBytes);
                if (total != -1 && (current += readBytes) != total) {
                    this.callback.progress((double)current / (double)total * 100.0);
                }
                if (!smooth) continue;
                Thread.sleep(30L);
            }
            dataInputStream.close();
            fileOutputStream.close();
            this.callback.success(this.file);
            this.callback.progress(100.0);
        }
        catch (Exception e2) {
            this.callback.failed(e2.getMessage());
            e2.printStackTrace();
        }
    }
}

