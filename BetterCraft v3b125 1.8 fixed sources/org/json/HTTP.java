/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.util.Locale;
import java.util.Map;
import org.json.HTTPTokener;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTP {
    public static final String CRLF = "\r\n";

    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jo = new JSONObject();
        HTTPTokener x2 = new HTTPTokener(string);
        String token = x2.nextToken();
        if (token.toUpperCase(Locale.ROOT).startsWith("HTTP")) {
            jo.put("HTTP-Version", token);
            jo.put("Status-Code", x2.nextToken());
            jo.put("Reason-Phrase", x2.nextTo('\u0000'));
            x2.next();
        } else {
            jo.put("Method", token);
            jo.put("Request-URI", x2.nextToken());
            jo.put("HTTP-Version", x2.nextToken());
        }
        while (x2.more()) {
            String name = x2.nextTo(':');
            x2.next(':');
            jo.put(name, x2.nextTo('\u0000'));
            x2.next();
        }
        return jo;
    }

    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb2 = new StringBuilder();
        if (jo.has("Status-Code") && jo.has("Reason-Phrase")) {
            sb2.append(jo.getString("HTTP-Version"));
            sb2.append(' ');
            sb2.append(jo.getString("Status-Code"));
            sb2.append(' ');
            sb2.append(jo.getString("Reason-Phrase"));
        } else if (jo.has("Method") && jo.has("Request-URI")) {
            sb2.append(jo.getString("Method"));
            sb2.append(' ');
            sb2.append('\"');
            sb2.append(jo.getString("Request-URI"));
            sb2.append('\"');
            sb2.append(' ');
            sb2.append(jo.getString("HTTP-Version"));
        } else {
            throw new JSONException("Not enough material for an HTTP header.");
        }
        sb2.append(CRLF);
        for (Map.Entry<String, Object> entry : jo.entrySet()) {
            String key = entry.getKey();
            if ("HTTP-Version".equals(key) || "Status-Code".equals(key) || "Reason-Phrase".equals(key) || "Method".equals(key) || "Request-URI".equals(key) || JSONObject.NULL.equals(entry.getValue())) continue;
            sb2.append(key);
            sb2.append(": ");
            sb2.append(jo.optString(key));
            sb2.append(CRLF);
        }
        sb2.append(CRLF);
        return sb2.toString();
    }
}

