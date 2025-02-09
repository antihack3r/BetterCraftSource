// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.Reader;
import java.util.PropertyResourceBundle;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

public final class UTF8ResourceBundleControl extends ResourceBundle.Control
{
    private static final UTF8ResourceBundleControl INSTANCE;
    
    public static ResourceBundle.Control get() {
        return UTF8ResourceBundleControl.INSTANCE;
    }
    
    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        if (format.equals("java.properties")) {
            final String bundle = this.toBundleName(baseName, locale);
            final String resource = this.toResourceName(bundle, "properties");
            InputStream is = null;
            if (reload) {
                final URL url = loader.getResource(resource);
                if (url != null) {
                    final URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        is = connection.getInputStream();
                    }
                }
            }
            else {
                is = loader.getResourceAsStream(resource);
            }
            if (is != null) {
                try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    return new PropertyResourceBundle(isr);
                }
            }
            return null;
        }
        return super.newBundle(baseName, locale, format, loader, reload);
    }
    
    static {
        INSTANCE = new UTF8ResourceBundleControl();
    }
}
