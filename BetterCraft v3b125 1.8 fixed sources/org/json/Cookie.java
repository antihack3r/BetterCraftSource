/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Cookie {
    public static String escape(String string) {
        String s2 = string.trim();
        int length = s2.length();
        StringBuilder sb2 = new StringBuilder(length);
        for (int i2 = 0; i2 < length; ++i2) {
            char c2 = s2.charAt(i2);
            if (c2 < ' ' || c2 == '+' || c2 == '%' || c2 == '=' || c2 == ';') {
                sb2.append('%');
                sb2.append(Character.forDigit((char)(c2 >>> 4 & 0xF), 16));
                sb2.append(Character.forDigit((char)(c2 & 0xF), 16));
                continue;
            }
            sb2.append(c2);
        }
        return sb2.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONTokener x2 = new JSONTokener(string);
        jo.put("name", x2.nextTo('='));
        x2.next('=');
        jo.put("value", x2.nextTo(';'));
        x2.next();
        while (x2.more()) {
            Object value;
            String name = Cookie.unescape(x2.nextTo("=;"));
            if (x2.next() != '=') {
                if (!name.equals("secure")) throw x2.syntaxError("Missing '=' in cookie parameter.");
                value = Boolean.TRUE;
            } else {
                value = Cookie.unescape(x2.nextTo(';'));
                x2.next();
            }
            jo.put(name, value);
        }
        return jo;
    }

    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(Cookie.escape(jo.getString("name")));
        sb2.append("=");
        sb2.append(Cookie.escape(jo.getString("value")));
        if (jo.has("expires")) {
            sb2.append(";expires=");
            sb2.append(jo.getString("expires"));
        }
        if (jo.has("domain")) {
            sb2.append(";domain=");
            sb2.append(Cookie.escape(jo.getString("domain")));
        }
        if (jo.has("path")) {
            sb2.append(";path=");
            sb2.append(Cookie.escape(jo.getString("path")));
        }
        if (jo.optBoolean("secure")) {
            sb2.append(";secure");
        }
        return sb2.toString();
    }

    public static String unescape(String string) {
        int length = string.length();
        StringBuilder sb2 = new StringBuilder(length);
        for (int i2 = 0; i2 < length; ++i2) {
            char c2 = string.charAt(i2);
            if (c2 == '+') {
                c2 = ' ';
            } else if (c2 == '%' && i2 + 2 < length) {
                int d2 = JSONTokener.dehexchar(string.charAt(i2 + 1));
                int e2 = JSONTokener.dehexchar(string.charAt(i2 + 2));
                if (d2 >= 0 && e2 >= 0) {
                    c2 = (char)(d2 * 16 + e2);
                    i2 += 2;
                }
            }
            sb2.append(c2);
        }
        return sb2.toString();
    }
}

