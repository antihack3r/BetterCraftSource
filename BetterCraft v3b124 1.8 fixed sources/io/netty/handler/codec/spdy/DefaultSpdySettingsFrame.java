/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdySettingsFrame;
import io.netty.util.internal.StringUtil;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DefaultSpdySettingsFrame
implements SpdySettingsFrame {
    private boolean clear;
    private final Map<Integer, Setting> settingsMap = new TreeMap<Integer, Setting>();

    @Override
    public Set<Integer> ids() {
        return this.settingsMap.keySet();
    }

    @Override
    public boolean isSet(int id2) {
        Integer key = id2;
        return this.settingsMap.containsKey(key);
    }

    @Override
    public int getValue(int id2) {
        Integer key = id2;
        if (this.settingsMap.containsKey(key)) {
            return this.settingsMap.get(key).getValue();
        }
        return -1;
    }

    @Override
    public SpdySettingsFrame setValue(int id2, int value) {
        return this.setValue(id2, value, false, false);
    }

    @Override
    public SpdySettingsFrame setValue(int id2, int value, boolean persistValue, boolean persisted) {
        if (id2 < 0 || id2 > 0xFFFFFF) {
            throw new IllegalArgumentException("Setting ID is not valid: " + id2);
        }
        Integer key = id2;
        if (this.settingsMap.containsKey(key)) {
            Setting setting = this.settingsMap.get(key);
            setting.setValue(value);
            setting.setPersist(persistValue);
            setting.setPersisted(persisted);
        } else {
            this.settingsMap.put(key, new Setting(value, persistValue, persisted));
        }
        return this;
    }

    @Override
    public SpdySettingsFrame removeValue(int id2) {
        Integer key = id2;
        if (this.settingsMap.containsKey(key)) {
            this.settingsMap.remove(key);
        }
        return this;
    }

    @Override
    public boolean isPersistValue(int id2) {
        Integer key = id2;
        if (this.settingsMap.containsKey(key)) {
            return this.settingsMap.get(key).isPersist();
        }
        return false;
    }

    @Override
    public SpdySettingsFrame setPersistValue(int id2, boolean persistValue) {
        Integer key = id2;
        if (this.settingsMap.containsKey(key)) {
            this.settingsMap.get(key).setPersist(persistValue);
        }
        return this;
    }

    @Override
    public boolean isPersisted(int id2) {
        Integer key = id2;
        if (this.settingsMap.containsKey(key)) {
            return this.settingsMap.get(key).isPersisted();
        }
        return false;
    }

    @Override
    public SpdySettingsFrame setPersisted(int id2, boolean persisted) {
        Integer key = id2;
        if (this.settingsMap.containsKey(key)) {
            this.settingsMap.get(key).setPersisted(persisted);
        }
        return this;
    }

    @Override
    public boolean clearPreviouslyPersistedSettings() {
        return this.clear;
    }

    @Override
    public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean clear) {
        this.clear = clear;
        return this;
    }

    private Set<Map.Entry<Integer, Setting>> getSettings() {
        return this.settingsMap.entrySet();
    }

    private void appendSettings(StringBuilder buf) {
        for (Map.Entry<Integer, Setting> e2 : this.getSettings()) {
            Setting setting = e2.getValue();
            buf.append("--> ");
            buf.append(e2.getKey());
            buf.append(':');
            buf.append(setting.getValue());
            buf.append(" (persist value: ");
            buf.append(setting.isPersist());
            buf.append("; persisted: ");
            buf.append(setting.isPersisted());
            buf.append(')');
            buf.append(StringUtil.NEWLINE);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(StringUtil.simpleClassName(this));
        buf.append(StringUtil.NEWLINE);
        this.appendSettings(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    private static final class Setting {
        private int value;
        private boolean persist;
        private boolean persisted;

        Setting(int value, boolean persist, boolean persisted) {
            this.value = value;
            this.persist = persist;
            this.persisted = persisted;
        }

        int getValue() {
            return this.value;
        }

        void setValue(int value) {
            this.value = value;
        }

        boolean isPersist() {
            return this.persist;
        }

        void setPersist(boolean persist) {
            this.persist = persist;
        }

        boolean isPersisted() {
            return this.persisted;
        }

        void setPersisted(boolean persisted) {
            this.persisted = persisted;
        }
    }
}

