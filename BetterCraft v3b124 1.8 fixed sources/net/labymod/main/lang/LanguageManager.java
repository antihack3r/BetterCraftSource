/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.lang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import net.labymod.main.lang.Language;
import net.labymod.support.util.Debug;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;

public class LanguageManager {
    private static Map<String, Language> lang = new ConcurrentHashMap<String, Language>();
    private static Language language;
    private static Language defaultLanguage;
    public static String lastLocaleCode;

    static {
        lastLocaleCode = "";
    }

    public static void updateLang() {
        String mcLanguage = "en_US";
        String defaultLanguageCode = "en_US";
        if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().getLanguageManager() != null && Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage() != null && Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode() != null) {
            mcLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        }
        Language targetLanguage = null;
        for (Language lang : LanguageManager.lang.values()) {
            if (!mcLanguage.equals(lang.getName())) continue;
            targetLanguage = lang;
        }
        if (targetLanguage == null) {
            Debug.log(Debug.EnumDebugMode.LANGUAGE, String.valueOf(mcLanguage) + " is not loaded! Trying to load it..");
            targetLanguage = LanguageManager.load(mcLanguage);
        } else {
            Debug.log(Debug.EnumDebugMode.LANGUAGE, "Detected Minecraft language: " + mcLanguage);
        }
        if (defaultLanguage == null) {
            defaultLanguage = LanguageManager.load(defaultLanguageCode);
        }
        if (targetLanguage == null) {
            Debug.log(Debug.EnumDebugMode.LANGUAGE, String.valueOf(mcLanguage) + " doesn't exists, using default language instead.");
            targetLanguage = defaultLanguage;
        }
        if (targetLanguage == null) {
            Debug.log(Debug.EnumDebugMode.LANGUAGE, "Using no language!");
        } else {
            Debug.log(Debug.EnumDebugMode.LANGUAGE, "Using language " + targetLanguage.getName() + " now.");
        }
        language = targetLanguage;
        lastLocaleCode = mcLanguage;
    }

    public static Language load(String name) {
        Properties prop;
        block6: {
            prop = new Properties();
            if (!name.contains("_")) break block6;
            String[] sp2 = name.split("_");
            String[] files = new String[]{sp2[1], name, sp2[0]};
            boolean found = false;
            String[] stringArray = files;
            int n2 = files.length;
            int n3 = 0;
            while (n3 < n2) {
                String fileName = stringArray[n3];
                InputStream stream = LanguageManager.class.getResourceAsStream("/assets/minecraft/labymod/lang/" + fileName.toUpperCase() + ".properties");
                if (stream != null) {
                    InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
                    prop.load(reader);
                    reader.close();
                    found = true;
                    break;
                }
                ++n3;
            }
            if (found) break block6;
            Debug.log(Debug.EnumDebugMode.LANGUAGE, "Cannot find following language: " + name + " (" + sp2[0] + ", " + sp2[1] + ")");
            return null;
        }
        try {
            Language lang = new Language(name);
            for (Map.Entry<Object, Object> s2 : prop.entrySet()) {
                lang.translations.put(s2.getKey().toString(), ModColor.createColors(s2.getValue().toString()));
            }
            LanguageManager.lang.put(name, lang);
            return lang;
        }
        catch (Exception error) {
            error.printStackTrace();
            Debug.log(Debug.EnumDebugMode.LANGUAGE, "Couldn't load language file " + name + " " + error.getMessage());
            return null;
        }
    }

    public static String translateString(String key, boolean format, Object ... args) {
        if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().getLanguageManager() != null && Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage() != null && Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode() != null && !lastLocaleCode.equals(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())) {
            LanguageManager.updateLang();
        }
        if (key == null || language == null) {
            return key;
        }
        String trans = language.get(key);
        if (trans == null && (defaultLanguage == null || (trans = defaultLanguage.get(key)) == null)) {
            return key;
        }
        if (format && args != null) {
            try {
                trans = String.format(trans, args);
            }
            catch (Exception error) {
                error.printStackTrace();
            }
        }
        return trans;
    }

    public static String translateOrReturnKey(String key, Object ... args) {
        String returned = LanguageManager.translate(key, args);
        return returned.contains("N/A") ? key : returned;
    }

    public static String translate(String key) {
        return LanguageManager.translateString(key, false, new Object[0]);
    }

    public static String translate(String key, Object ... args) {
        return LanguageManager.translateString(key, true, args);
    }

    public static Language getLanguage() {
        return language;
    }
}

