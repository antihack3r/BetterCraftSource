/*
 * Decompiled with CFR 0.152.
 */
package wdl.update;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class ClassHasher {
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int j2 = 0;
        while (j2 < bytes.length) {
            int v2 = bytes[j2] & 0xFF;
            hexChars[j2 * 2] = hexArray[v2 >>> 4];
            hexChars[j2 * 2 + 1] = hexArray[v2 & 0xF];
            ++j2;
        }
        return new String(hexChars);
    }

    public static String hash(String relativeTo, String file) throws ClassNotFoundException, FileNotFoundException, Exception {
        Class<?> clazz = Class.forName(relativeTo);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream stream = null;){
            stream = clazz.getResourceAsStream(file);
            if (stream == null) {
                throw new FileNotFoundException(String.valueOf(file) + " relative to " + relativeTo);
            }
            try (DigestInputStream digestStream = null;){
                digestStream = new DigestInputStream(stream, digest);
                while (digestStream.read() != -1) {
                }
            }
        }
        return ClassHasher.bytesToHex(digest.digest());
    }
}

