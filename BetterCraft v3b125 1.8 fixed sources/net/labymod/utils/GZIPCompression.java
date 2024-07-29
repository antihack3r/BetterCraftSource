/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompression {
    public static byte[] compress(byte[] input) {
        try {
            ByteArrayOutputStream bos2 = new ByteArrayOutputStream(input.length);
            GZIPOutputStream gzip = new GZIPOutputStream(bos2);
            gzip.write(input);
            gzip.close();
            byte[] compressed = bos2.toByteArray();
            bos2.close();
            return compressed;
        }
        catch (IOException e2) {
            e2.printStackTrace();
            return input;
        }
    }

    public static byte[] decompress(byte[] input) {
        try {
            int len;
            byte[] buffer = new byte[1024];
            ByteArrayInputStream bis2 = new ByteArrayInputStream(input);
            GZIPInputStream gis = new GZIPInputStream(bis2);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((len = gis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            gis.close();
            out.close();
            return out.toByteArray();
        }
        catch (IOException e2) {
            e2.printStackTrace();
            return input;
        }
    }
}

