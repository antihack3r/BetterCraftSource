/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.util.internal.InternalThreadLocalMap;

final class CookieEncoderUtil {
    static StringBuilder stringBuilder() {
        return InternalThreadLocalMap.get().stringBuilder();
    }

    static String stripTrailingSeparator(StringBuilder buf) {
        if (buf.length() > 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    static void add(StringBuilder sb2, String name, String val) {
        if (val == null) {
            CookieEncoderUtil.addQuoted(sb2, name, "");
            return;
        }
        for (int i2 = 0; i2 < val.length(); ++i2) {
            char c2 = val.charAt(i2);
            switch (c2) {
                case '\t': 
                case ' ': 
                case '\"': 
                case '(': 
                case ')': 
                case ',': 
                case '/': 
                case ':': 
                case ';': 
                case '<': 
                case '=': 
                case '>': 
                case '?': 
                case '@': 
                case '[': 
                case '\\': 
                case ']': 
                case '{': 
                case '}': {
                    CookieEncoderUtil.addQuoted(sb2, name, val);
                    return;
                }
            }
        }
        CookieEncoderUtil.addUnquoted(sb2, name, val);
    }

    static void addUnquoted(StringBuilder sb2, String name, String val) {
        sb2.append(name);
        sb2.append('=');
        sb2.append(val);
        sb2.append(';');
        sb2.append(' ');
    }

    static void addQuoted(StringBuilder sb2, String name, String val) {
        if (val == null) {
            val = "";
        }
        sb2.append(name);
        sb2.append('=');
        sb2.append('\"');
        sb2.append(val.replace("\\", "\\\\").replace("\"", "\\\""));
        sb2.append('\"');
        sb2.append(';');
        sb2.append(' ');
    }

    static void add(StringBuilder sb2, String name, long val) {
        sb2.append(name);
        sb2.append('=');
        sb2.append(val);
        sb2.append(';');
        sb2.append(' ');
    }

    private CookieEncoderUtil() {
    }
}

