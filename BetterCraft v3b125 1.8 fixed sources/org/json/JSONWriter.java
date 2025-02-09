/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONWriter {
    private static final int maxdepth = 200;
    private boolean comma = false;
    protected char mode = (char)105;
    private final JSONObject[] stack = new JSONObject[200];
    private int top = 0;
    protected Appendable writer;

    public JSONWriter(Appendable w2) {
        this.writer = w2;
    }

    private JSONWriter append(String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null pointer");
        }
        if (this.mode == 'o' || this.mode == 'a') {
            try {
                if (this.comma && this.mode == 'a') {
                    this.writer.append(',');
                }
                this.writer.append(string);
            }
            catch (IOException e2) {
                throw new JSONException(e2);
            }
            if (this.mode == 'o') {
                this.mode = (char)107;
            }
            this.comma = true;
            return this;
        }
        throw new JSONException("Value out of sequence.");
    }

    public JSONWriter array() throws JSONException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            this.push(null);
            this.append("[");
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced array.");
    }

    private JSONWriter end(char m2, char c2) throws JSONException {
        if (this.mode != m2) {
            throw new JSONException(m2 == 'a' ? "Misplaced endArray." : "Misplaced endObject.");
        }
        this.pop(m2);
        try {
            this.writer.append(c2);
        }
        catch (IOException e2) {
            throw new JSONException(e2);
        }
        this.comma = true;
        return this;
    }

    public JSONWriter endArray() throws JSONException {
        return this.end('a', ']');
    }

    public JSONWriter endObject() throws JSONException {
        return this.end('k', '}');
    }

    public JSONWriter key(String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null key.");
        }
        if (this.mode == 'k') {
            try {
                this.stack[this.top - 1].putOnce(string, Boolean.TRUE);
                if (this.comma) {
                    this.writer.append(',');
                }
                this.writer.append(JSONObject.quote(string));
                this.writer.append(':');
                this.comma = false;
                this.mode = (char)111;
                return this;
            }
            catch (IOException e2) {
                throw new JSONException(e2);
            }
        }
        throw new JSONException("Misplaced key.");
    }

    public JSONWriter object() throws JSONException {
        if (this.mode == 'i') {
            this.mode = (char)111;
        }
        if (this.mode == 'o' || this.mode == 'a') {
            this.append("{");
            this.push(new JSONObject());
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced object.");
    }

    private void pop(char c2) throws JSONException {
        char m2;
        if (this.top <= 0) {
            throw new JSONException("Nesting error.");
        }
        char c3 = m2 = this.stack[this.top - 1] == null ? (char)'a' : 'k';
        if (m2 != c2) {
            throw new JSONException("Nesting error.");
        }
        --this.top;
        this.mode = (char)(this.top == 0 ? 100 : (this.stack[this.top - 1] == null ? 97 : 107));
    }

    private void push(JSONObject jo) throws JSONException {
        if (this.top >= 200) {
            throw new JSONException("Nesting too deep.");
        }
        this.stack[this.top] = jo;
        this.mode = (char)(jo == null ? 97 : 107);
        ++this.top;
    }

    public JSONWriter value(boolean b2) throws JSONException {
        return this.append(b2 ? "true" : "false");
    }

    public JSONWriter value(double d2) throws JSONException {
        return this.value(new Double(d2));
    }

    public JSONWriter value(long l2) throws JSONException {
        return this.append(Long.toString(l2));
    }

    public JSONWriter value(Object object) throws JSONException {
        return this.append(JSONObject.valueToString(object));
    }
}

