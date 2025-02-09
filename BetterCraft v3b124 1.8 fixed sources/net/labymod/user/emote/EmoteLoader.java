/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote;

import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.labymod.user.emote.EmoteRegistry;
import net.labymod.user.emote.keys.provider.KeyFrameStorage;
import net.labymod.utils.GZIPCompression;

public class EmoteLoader
extends Thread {
    private static final Gson GSON = new Gson();
    private EmoteRegistry emoteRegistry;

    public EmoteLoader(EmoteRegistry emoteRegistry) {
        this.emoteRegistry = emoteRegistry;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL("http://dl.labymod.net/emotes/emotedata").openConnection();
            connection.setRequestProperty("User-Agent", Source.getUserAgent());
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(2000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode / 100 == 2) {
                int count;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                BufferedInputStream bis2 = new BufferedInputStream(connection.getInputStream());
                byte[] data = new byte[1024];
                while ((count = bis2.read(data, 0, 1024)) != -1) {
                    dos.write(data, 0, count);
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                DataInputStream dis = new DataInputStream(bais);
                byte[] compressed = new byte[dis.readInt()];
                dis.read(compressed);
                byte[] decompressed = GZIPCompression.decompress(compressed);
                this.decompile(decompressed);
            } else {
                Debug.log(Debug.EnumDebugMode.EMOTE, "Emotedata response code is " + responseCode);
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void decompile(byte[] decompressed) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(decompressed);
        DataInputStream dis = new DataInputStream(bais);
        int count = dis.readInt();
        this.emoteRegistry.emoteSources.clear();
        int i2 = 0;
        while (i2 < count) {
            short id2 = dis.readShort();
            byte[] nameInBytes = new byte[dis.readInt()];
            dis.read(nameInBytes);
            String name = new String(nameInBytes);
            byte[] jsonInBytes = new byte[dis.readInt()];
            dis.read(jsonInBytes);
            String json = new String(jsonInBytes);
            try {
                KeyFrameStorage storage = GSON.fromJson(json, KeyFrameStorage.class);
                storage.setName(name);
                storage.setId(id2);
                this.emoteRegistry.emoteSources.put(storage.getId(), storage);
            }
            catch (Exception error) {
                Debug.log(Debug.EnumDebugMode.EMOTE, "Invalid emote data: " + json);
            }
            ++i2;
        }
        Debug.log(Debug.EnumDebugMode.EMOTE, "Loaded " + this.emoteRegistry.emoteSources.size() + " emotes!");
    }
}

