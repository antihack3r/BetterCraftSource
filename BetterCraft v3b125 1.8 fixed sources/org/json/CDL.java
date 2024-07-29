/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CDL {
    private static String getValue(JSONTokener x2) throws JSONException {
        char c2;
        while ((c2 = x2.next()) == ' ' || c2 == '\t') {
        }
        switch (c2) {
            case '\u0000': {
                return null;
            }
            case '\"': 
            case '\'': {
                char q2 = c2;
                StringBuffer sb2 = new StringBuffer();
                while (true) {
                    char nextC;
                    if ((c2 = x2.next()) == q2 && (nextC = x2.next()) != '\"') {
                        if (nextC <= '\u0000') break;
                        x2.back();
                        break;
                    }
                    if (c2 == '\u0000' || c2 == '\n' || c2 == '\r') {
                        throw x2.syntaxError("Missing close quote '" + q2 + "'.");
                    }
                    sb2.append(c2);
                }
                return sb2.toString();
            }
            case ',': {
                x2.back();
                return "";
            }
        }
        x2.back();
        return x2.nextTo(',');
    }

    public static JSONArray rowToJSONArray(JSONTokener x2) throws JSONException {
        JSONArray ja2 = new JSONArray();
        block0: while (true) {
            String value = CDL.getValue(x2);
            char c2 = x2.next();
            if (value == null || ja2.length() == 0 && value.length() == 0 && c2 != ',') {
                return null;
            }
            ja2.put(value);
            while (true) {
                if (c2 == ',') continue block0;
                if (c2 != ' ') {
                    if (c2 == '\n' || c2 == '\r' || c2 == '\u0000') {
                        return ja2;
                    }
                    throw x2.syntaxError("Bad character '" + c2 + "' (" + c2 + ").");
                }
                c2 = x2.next();
            }
            break;
        }
    }

    public static JSONObject rowToJSONObject(JSONArray names, JSONTokener x2) throws JSONException {
        JSONArray ja2 = CDL.rowToJSONArray(x2);
        return ja2 != null ? ja2.toJSONObject(names) : null;
    }

    public static String rowToString(JSONArray ja2) {
        StringBuilder sb2 = new StringBuilder();
        for (int i2 = 0; i2 < ja2.length(); ++i2) {
            Object object;
            if (i2 > 0) {
                sb2.append(',');
            }
            if ((object = ja2.opt(i2)) == null) continue;
            String string = object.toString();
            if (string.length() > 0 && (string.indexOf(44) >= 0 || string.indexOf(10) >= 0 || string.indexOf(13) >= 0 || string.indexOf(0) >= 0 || string.charAt(0) == '\"')) {
                sb2.append('\"');
                int length = string.length();
                for (int j2 = 0; j2 < length; ++j2) {
                    char c2 = string.charAt(j2);
                    if (c2 < ' ' || c2 == '\"') continue;
                    sb2.append(c2);
                }
                sb2.append('\"');
                continue;
            }
            sb2.append(string);
        }
        sb2.append('\n');
        return sb2.toString();
    }

    public static JSONArray toJSONArray(String string) throws JSONException {
        return CDL.toJSONArray(new JSONTokener(string));
    }

    public static JSONArray toJSONArray(JSONTokener x2) throws JSONException {
        return CDL.toJSONArray(CDL.rowToJSONArray(x2), x2);
    }

    public static JSONArray toJSONArray(JSONArray names, String string) throws JSONException {
        return CDL.toJSONArray(names, new JSONTokener(string));
    }

    public static JSONArray toJSONArray(JSONArray names, JSONTokener x2) throws JSONException {
        JSONObject jo;
        if (names == null || names.length() == 0) {
            return null;
        }
        JSONArray ja2 = new JSONArray();
        while ((jo = CDL.rowToJSONObject(names, x2)) != null) {
            ja2.put(jo);
        }
        if (ja2.length() == 0) {
            return null;
        }
        return ja2;
    }

    public static String toString(JSONArray ja2) throws JSONException {
        JSONArray names;
        JSONObject jo = ja2.optJSONObject(0);
        if (jo != null && (names = jo.names()) != null) {
            return CDL.rowToString(names) + CDL.toString(names, ja2);
        }
        return null;
    }

    public static String toString(JSONArray names, JSONArray ja2) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        StringBuffer sb2 = new StringBuffer();
        for (int i2 = 0; i2 < ja2.length(); ++i2) {
            JSONObject jo = ja2.optJSONObject(i2);
            if (jo == null) continue;
            sb2.append(CDL.rowToString(jo.toJSONArray(names)));
        }
        return sb2.toString();
    }
}

