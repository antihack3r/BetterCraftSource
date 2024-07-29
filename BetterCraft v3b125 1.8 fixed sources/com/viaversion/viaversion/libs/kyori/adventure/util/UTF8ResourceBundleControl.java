/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NotNull;

public final class UTF8ResourceBundleControl
extends ResourceBundle.Control {
    private static final UTF8ResourceBundleControl INSTANCE = new UTF8ResourceBundleControl();

    public static @NotNull ResourceBundle.Control get() {
        return INSTANCE;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        if (format.equals("java.properties")) {
            String bundle = this.toBundleName(baseName, locale);
            String resource = this.toResourceName(bundle, "properties");
            InputStream is2 = null;
            if (reload) {
                URLConnection connection;
                URL url = loader.getResource(resource);
                if (url != null && (connection = url.openConnection()) != null) {
                    connection.setUseCaches(false);
                    is2 = connection.getInputStream();
                }
            } else {
                is2 = loader.getResourceAsStream(resource);
            }
            if (is2 != null) {
                try (InputStreamReader isr = new InputStreamReader(is2, StandardCharsets.UTF_8);){
                    PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(isr);
                    return propertyResourceBundle;
                }
            }
            return null;
        }
        return super.newBundle(baseName, locale, format, loader, reload);
    }
}

