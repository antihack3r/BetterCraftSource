/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.lang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Language {
    public Map<String, String> translations = new ConcurrentHashMap<String, String>();
    private String name;

    public Language(String name) {
        this.name = name;
    }

    public void add(String key, String translation) {
        this.translations.put(key.toLowerCase(), translation);
    }

    public String get(String key) {
        return this.translations.get(key.toLowerCase());
    }

    public Map<String, String> getTranslations() {
        return this.translations;
    }

    public String getName() {
        return this.name;
    }
}

