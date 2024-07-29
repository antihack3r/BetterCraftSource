/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.event;

public class KeyValuePair {
    public final String key;
    public final Object value;

    public KeyValuePair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.key) + "=\"" + String.valueOf(this.value) + "\"";
    }
}

