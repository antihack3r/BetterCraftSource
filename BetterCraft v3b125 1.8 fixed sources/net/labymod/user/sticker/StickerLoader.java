/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.sticker;

import com.google.gson.Gson;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.labymod.user.sticker.StickerRegistry;
import net.labymod.user.sticker.data.StickerData;

public class StickerLoader
extends Thread {
    private static final Gson GSON = new Gson();
    private StickerRegistry stickerRegistry;

    public StickerLoader(StickerRegistry stickerRegistry) {
        this.stickerRegistry = stickerRegistry;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL("http://dl.labymod.net/stickers.json").openConnection();
            connection.setRequestProperty("User-Agent", Source.getUserAgent());
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(2000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode / 100 == 2) {
                StickerData stickerData;
                String jsonString = "";
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    jsonString = String.valueOf(jsonString) + scanner.nextLine();
                }
                scanner.close();
                this.stickerRegistry.stickerData = stickerData = GSON.fromJson(jsonString, StickerData.class);
            } else {
                Debug.log(Debug.EnumDebugMode.EMOTE, "Stickerdata response code is " + responseCode);
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }
}

