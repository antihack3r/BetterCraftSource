/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.util.Map;
import org.json.Cookie;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CookieList {
    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONTokener x2 = new JSONTokener(string);
        while (x2.more()) {
            String name = Cookie.unescape(x2.nextTo('='));
            x2.next('=');
            jo.put(name, Cookie.unescape(x2.nextTo(';')));
            x2.next();
        }
        return jo;
    }

    public static String toString(JSONObject jo) throws JSONException {
        boolean b2 = false;
        StringBuilder sb2 = new StringBuilder();
        for (Map.Entry<String, Object> entry : jo.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (JSONObject.NULL.equals(value)) continue;
            if (b2) {
                sb2.append(';');
            }
            sb2.append(Cookie.escape(key));
            sb2.append("=");
            sb2.append(Cookie.escape(value.toString()));
            b2 = true;
        }
        return sb2.toString();
    }
}

