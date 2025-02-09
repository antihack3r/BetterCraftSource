/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class CharsetUtils {
    public static Charset lookup(String name) {
        if (name == null) {
            return null;
        }
        try {
            return Charset.forName(name);
        }
        catch (UnsupportedCharsetException ex2) {
            return null;
        }
    }

    public static Charset get(String name) throws UnsupportedEncodingException {
        if (name == null) {
            return null;
        }
        try {
            return Charset.forName(name);
        }
        catch (UnsupportedCharsetException ex2) {
            throw new UnsupportedEncodingException(name);
        }
    }
}

