/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import javax.xml.bind.DatatypeConverter;

public class Base64Manager {
    public static String encode(String string) {
        String encoded = DatatypeConverter.printBase64Binary(string.getBytes());
        return encoded;
    }

    public static String decode(String base64String) {
        String decoded = new String(DatatypeConverter.parseBase64Binary(base64String));
        return decoded;
    }
}

