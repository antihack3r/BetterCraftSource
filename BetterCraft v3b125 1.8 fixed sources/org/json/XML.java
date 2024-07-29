/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XMLTokener;

public class XML {
    public static final Character AMP = Character.valueOf('&');
    public static final Character APOS = Character.valueOf('\'');
    public static final Character BANG = Character.valueOf('!');
    public static final Character EQ = Character.valueOf('=');
    public static final Character GT = Character.valueOf('>');
    public static final Character LT = Character.valueOf('<');
    public static final Character QUEST = Character.valueOf('?');
    public static final Character QUOT = Character.valueOf('\"');
    public static final Character SLASH = Character.valueOf('/');

    private static Iterable<Integer> codePointIterator(final String string) {
        return new Iterable<Integer>(){

            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>(){
                    private int nextIndex = 0;
                    private int length;
                    {
                        this.length = string.length();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.nextIndex < this.length;
                    }

                    @Override
                    public Integer next() {
                        int result = string.codePointAt(this.nextIndex);
                        this.nextIndex += Character.charCount(result);
                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static String escape(String string) {
        StringBuilder sb2 = new StringBuilder(string.length());
        block7: for (int cp2 : XML.codePointIterator(string)) {
            switch (cp2) {
                case 38: {
                    sb2.append("&amp;");
                    continue block7;
                }
                case 60: {
                    sb2.append("&lt;");
                    continue block7;
                }
                case 62: {
                    sb2.append("&gt;");
                    continue block7;
                }
                case 34: {
                    sb2.append("&quot;");
                    continue block7;
                }
                case 39: {
                    sb2.append("&apos;");
                    continue block7;
                }
            }
            if (XML.mustEscape(cp2)) {
                sb2.append("&#x");
                sb2.append(Integer.toHexString(cp2));
                sb2.append(';');
                continue;
            }
            sb2.appendCodePoint(cp2);
        }
        return sb2.toString();
    }

    private static boolean mustEscape(int cp2) {
        return Character.isISOControl(cp2) && cp2 != 9 && cp2 != 10 && cp2 != 13 || (cp2 < 32 || cp2 > 55295) && (cp2 < 57344 || cp2 > 65533) && (cp2 < 65536 || cp2 > 0x10FFFF);
    }

    public static String unescape(String string) {
        StringBuilder sb2 = new StringBuilder(string.length());
        int length = string.length();
        for (int i2 = 0; i2 < length; ++i2) {
            char c2 = string.charAt(i2);
            if (c2 == '&') {
                int semic = string.indexOf(59, i2);
                if (semic > i2) {
                    String entity = string.substring(i2 + 1, semic);
                    sb2.append(XMLTokener.unescapeEntity(entity));
                    i2 += entity.length() + 1;
                    continue;
                }
                sb2.append(c2);
                continue;
            }
            sb2.append(c2);
        }
        return sb2.toString();
    }

    public static void noSpace(String string) throws JSONException {
        int length = string.length();
        if (length == 0) {
            throw new JSONException("Empty string.");
        }
        for (int i2 = 0; i2 < length; ++i2) {
            if (!Character.isWhitespace(string.charAt(i2))) continue;
            throw new JSONException("'" + string + "' contains a space character.");
        }
    }

    private static boolean parse(XMLTokener x2, JSONObject context, String name, boolean keepStrings) throws JSONException {
        String string;
        JSONObject jsonobject = null;
        Object token = x2.nextToken();
        if (token == BANG) {
            char c2 = x2.next();
            if (c2 == '-') {
                if (x2.next() == '-') {
                    x2.skipPast("-->");
                    return false;
                }
                x2.back();
            } else if (c2 == '[') {
                token = x2.nextToken();
                if ("CDATA".equals(token) && x2.next() == '[') {
                    String string2 = x2.nextCDATA();
                    if (string2.length() > 0) {
                        context.accumulate("content", string2);
                    }
                    return false;
                }
                throw x2.syntaxError("Expected 'CDATA['");
            }
            int i2 = 1;
            do {
                if ((token = x2.nextMeta()) == null) {
                    throw x2.syntaxError("Missing '>' after '<!'.");
                }
                if (token == LT) {
                    ++i2;
                    continue;
                }
                if (token != GT) continue;
                --i2;
            } while (i2 > 0);
            return false;
        }
        if (token == QUEST) {
            x2.skipPast("?>");
            return false;
        }
        if (token == SLASH) {
            token = x2.nextToken();
            if (name == null) {
                throw x2.syntaxError("Mismatched close tag " + token);
            }
            if (!token.equals(name)) {
                throw x2.syntaxError("Mismatched " + name + " and " + token);
            }
            if (x2.nextToken() != GT) {
                throw x2.syntaxError("Misshaped close tag");
            }
            return true;
        }
        if (token instanceof Character) {
            throw x2.syntaxError("Misshaped tag");
        }
        String tagName = (String)token;
        token = null;
        jsonobject = new JSONObject();
        while (true) {
            if (token == null) {
                token = x2.nextToken();
            }
            if (!(token instanceof String)) break;
            string = (String)token;
            token = x2.nextToken();
            if (token == EQ) {
                token = x2.nextToken();
                if (!(token instanceof String)) {
                    throw x2.syntaxError("Missing value");
                }
                jsonobject.accumulate(string, keepStrings ? (String)token : XML.stringToValue((String)token));
                token = null;
                continue;
            }
            jsonobject.accumulate(string, "");
        }
        if (token == SLASH) {
            if (x2.nextToken() != GT) {
                throw x2.syntaxError("Misshaped tag");
            }
            if (jsonobject.length() > 0) {
                context.accumulate(tagName, jsonobject);
            } else {
                context.accumulate(tagName, "");
            }
            return false;
        }
        if (token == GT) {
            while (true) {
                if ((token = x2.nextContent()) == null) {
                    if (tagName != null) {
                        throw x2.syntaxError("Unclosed tag " + tagName);
                    }
                    return false;
                }
                if (token instanceof String) {
                    string = (String)token;
                    if (string.length() <= 0) continue;
                    jsonobject.accumulate("content", keepStrings ? string : XML.stringToValue(string));
                    continue;
                }
                if (token == LT && XML.parse(x2, jsonobject, tagName, keepStrings)) break;
            }
            if (jsonobject.length() == 0) {
                context.accumulate(tagName, "");
            } else if (jsonobject.length() == 1 && jsonobject.opt("content") != null) {
                context.accumulate(tagName, jsonobject.opt("content"));
            } else {
                context.accumulate(tagName, jsonobject);
            }
            return false;
        }
        throw x2.syntaxError("Misshaped tag");
    }

    public static Object stringToValue(String string) {
        return JSONObject.stringToValue(string);
    }

    public static JSONObject toJSONObject(String string) throws JSONException {
        return XML.toJSONObject(string, false);
    }

    public static JSONObject toJSONObject(String string, boolean keepStrings) throws JSONException {
        JSONObject jo = new JSONObject();
        XMLTokener x2 = new XMLTokener(string);
        while (x2.more() && x2.skipPast("<")) {
            XML.parse(x2, jo, null, keepStrings);
        }
        return jo;
    }

    public static String toString(Object object) throws JSONException {
        return XML.toString(object, null);
    }

    public static String toString(Object object, String tagName) throws JSONException {
        String string;
        StringBuilder sb2 = new StringBuilder();
        if (object instanceof JSONObject) {
            if (tagName != null) {
                sb2.append('<');
                sb2.append(tagName);
                sb2.append('>');
            }
            JSONObject jo = (JSONObject)object;
            for (Map.Entry<String, Object> entry : jo.entrySet()) {
                JSONArray ja2;
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value == null) {
                    value = "";
                } else if (value.getClass().isArray()) {
                    value = new JSONArray(value);
                }
                if ("content".equals(key)) {
                    if (value instanceof JSONArray) {
                        ja2 = (JSONArray)value;
                        int i2 = 0;
                        for (Object val : ja2) {
                            if (i2 > 0) {
                                sb2.append('\n');
                            }
                            sb2.append(XML.escape(val.toString()));
                            ++i2;
                        }
                        continue;
                    }
                    sb2.append(XML.escape(value.toString()));
                    continue;
                }
                if (value instanceof JSONArray) {
                    ja2 = (JSONArray)value;
                    for (Object val : ja2) {
                        if (val instanceof JSONArray) {
                            sb2.append('<');
                            sb2.append(key);
                            sb2.append('>');
                            sb2.append(XML.toString(val));
                            sb2.append("</");
                            sb2.append(key);
                            sb2.append('>');
                            continue;
                        }
                        sb2.append(XML.toString(val, key));
                    }
                    continue;
                }
                if ("".equals(value)) {
                    sb2.append('<');
                    sb2.append(key);
                    sb2.append("/>");
                    continue;
                }
                sb2.append(XML.toString(value, key));
            }
            if (tagName != null) {
                sb2.append("</");
                sb2.append(tagName);
                sb2.append('>');
            }
            return sb2.toString();
        }
        if (object != null && (object instanceof JSONArray || object.getClass().isArray())) {
            JSONArray ja3 = object.getClass().isArray() ? new JSONArray(object) : (JSONArray)object;
            for (Object val : ja3) {
                sb2.append(XML.toString(val, tagName == null ? "array" : tagName));
            }
            return sb2.toString();
        }
        String string2 = string = object == null ? "null" : XML.escape(object.toString());
        return tagName == null ? "\"" + string + "\"" : (string.length() == 0 ? "<" + tagName + "/>" : "<" + tagName + ">" + string + "</" + tagName + ">");
    }
}

