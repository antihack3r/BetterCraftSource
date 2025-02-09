/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.message;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.logging.log4j.message.MultiformatMessage;
import org.apache.logging.log4j.util.EnglishEnums;

public class MapMessage
implements MultiformatMessage {
    private static final long serialVersionUID = -5031471831131487120L;
    private final SortedMap<String, String> data;

    public MapMessage() {
        this.data = new TreeMap<String, String>();
    }

    public MapMessage(Map<String, String> map) {
        this.data = map instanceof SortedMap ? (SortedMap<Object, Object>)map : new TreeMap<String, String>(map);
    }

    @Override
    public String[] getFormats() {
        String[] formats = new String[MapFormat.values().length];
        int i2 = 0;
        for (MapFormat format : MapFormat.values()) {
            formats[i2++] = format.name();
        }
        return formats;
    }

    @Override
    public Object[] getParameters() {
        return this.data.values().toArray();
    }

    @Override
    public String getFormat() {
        return "";
    }

    public Map<String, String> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    public void clear() {
        this.data.clear();
    }

    public void put(String key, String value) {
        if (value == null) {
            throw new IllegalArgumentException("No value provided for key " + key);
        }
        this.validate(key, value);
        this.data.put(key, value);
    }

    protected void validate(String key, String value) {
    }

    public void putAll(Map<String, String> map) {
        this.data.putAll(map);
    }

    public String get(String key) {
        return (String)this.data.get(key);
    }

    public String remove(String key) {
        return (String)this.data.remove(key);
    }

    public String asString() {
        return this.asString((MapFormat)null);
    }

    public String asString(String format) {
        try {
            return this.asString(EnglishEnums.valueOf(MapFormat.class, format));
        }
        catch (IllegalArgumentException ex2) {
            return this.asString();
        }
    }

    private String asString(MapFormat format) {
        StringBuilder sb2 = new StringBuilder();
        if (format == null) {
            this.appendMap(sb2);
        } else {
            switch (format) {
                case XML: {
                    this.asXML(sb2);
                    break;
                }
                case JSON: {
                    this.asJSON(sb2);
                    break;
                }
                case JAVA: {
                    this.asJava(sb2);
                    break;
                }
                default: {
                    this.appendMap(sb2);
                }
            }
        }
        return sb2.toString();
    }

    public void asXML(StringBuilder sb2) {
        sb2.append("<Map>\n");
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            sb2.append("  <Entry key=\"").append(entry.getKey()).append("\">").append(entry.getValue()).append("</Entry>\n");
        }
        sb2.append("</Map>");
    }

    @Override
    public String getFormattedMessage() {
        return this.asString();
    }

    @Override
    public String getFormattedMessage(String[] formats) {
        if (formats == null || formats.length == 0) {
            return this.asString();
        }
        for (String format : formats) {
            for (MapFormat mapFormat : MapFormat.values()) {
                if (!mapFormat.name().equalsIgnoreCase(format)) continue;
                return this.asString(mapFormat);
            }
        }
        return this.asString();
    }

    protected void appendMap(StringBuilder sb2) {
        boolean first = true;
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            if (!first) {
                sb2.append(" ");
            }
            first = false;
            sb2.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
        }
    }

    protected void asJSON(StringBuilder sb2) {
        boolean first = true;
        sb2.append("{");
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            if (!first) {
                sb2.append(", ");
            }
            first = false;
            sb2.append("\"").append(entry.getKey()).append("\":");
            sb2.append("\"").append(entry.getValue()).append("\"");
        }
        sb2.append("}");
    }

    protected void asJava(StringBuilder sb2) {
        boolean first = true;
        sb2.append("{");
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            if (!first) {
                sb2.append(", ");
            }
            first = false;
            sb2.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
        }
        sb2.append("}");
    }

    public MapMessage newInstance(Map<String, String> map) {
        return new MapMessage(map);
    }

    public String toString() {
        return this.asString();
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        MapMessage that = (MapMessage)o2;
        return this.data.equals(that.data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }

    public static enum MapFormat {
        XML,
        JSON,
        JAVA;

    }
}

