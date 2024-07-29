/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import java.util.Base64;

public class Base64Manager {
    public static String encode(String string) {
        String encoded = Base64.getEncoder().encodeToString(string.getBytes());
        return encoded;
    }

    public static String decode(String base64String) {
        String decoded = new String(Base64.getDecoder().decode(base64String));
        return decoded;
    }
}

