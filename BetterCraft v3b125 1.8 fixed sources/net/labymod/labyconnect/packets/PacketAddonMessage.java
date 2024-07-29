/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketAddonMessage
extends Packet {
    private String key;
    private byte[] data;

    public PacketAddonMessage(String key, byte[] data) {
        this.key = key;
        this.data = data;
    }

    public PacketAddonMessage(String key, String json) {
        this.key = key;
        this.data = this.toBytes(json);
    }

    public PacketAddonMessage() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.key = buf.readString();
        byte[] data = new byte[buf.readInt()];
        buf.readBytes(data);
        this.data = data;
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.key);
        buf.writeInt(this.data.length);
        buf.writeBytes(this.data);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getJson() {
        StringBuilder outStr;
        block6: {
            outStr = new StringBuilder();
            if (this.data != null && this.data.length != 0) break block6;
            return "";
        }
        try {
            if (this.isCompressed(this.data)) {
                String line;
                GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(this.data));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream)gis, "UTF-8"));
                while ((line = bufferedReader.readLine()) != null) {
                    outStr.append(line);
                }
            } else {
                outStr.append(Arrays.toString(this.data));
            }
            return outStr.toString();
        }
        catch (IOException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    private byte[] toBytes(String in2) {
        byte[] str = in2.getBytes(StandardCharsets.UTF_8);
        try {
            if (str == null || str.length == 0) {
                return new byte[0];
            }
            ByteArrayOutputStream obj = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(obj);
            gzip.write(str);
            gzip.flush();
            gzip.close();
            return obj.toByteArray();
        }
        catch (IOException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    private boolean isCompressed(byte[] compressed) {
        return compressed[0] == 31 && compressed[1] == -117;
    }

    public String getKey() {
        return this.key;
    }

    public byte[] getData() {
        return this.data;
    }
}

