/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.util;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class ProviderUtil {
    private static final String PROVIDER_RESOURCE = "META-INF/log4j-provider.properties";
    private static final String API_VERSION = "Log4jAPIVersion";
    private static final String[] COMPATIBLE_API_VERSIONS = new String[]{"2.0.0"};
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final List<Provider> PROVIDERS = new ArrayList<Provider>();

    private ProviderUtil() {
    }

    public static Iterator<Provider> getProviders() {
        return PROVIDERS.iterator();
    }

    public static boolean hasProviders() {
        return PROVIDERS.size() > 0;
    }

    public static ClassLoader findClassLoader() {
        ClassLoader cl2 = System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
        if (cl2 == null) {
            cl2 = ProviderUtil.class.getClassLoader();
        }
        return cl2;
    }

    private static boolean validVersion(String version) {
        for (String v2 : COMPATIBLE_API_VERSIONS) {
            if (!version.startsWith(v2)) continue;
            return true;
        }
        return false;
    }

    static {
        ClassLoader cl2 = ProviderUtil.findClassLoader();
        Enumeration<URL> enumResources = null;
        try {
            enumResources = cl2.getResources(PROVIDER_RESOURCE);
        }
        catch (IOException e2) {
            LOGGER.fatal("Unable to locate META-INF/log4j-provider.properties", (Throwable)e2);
        }
        if (enumResources != null) {
            while (enumResources.hasMoreElements()) {
                URL url = enumResources.nextElement();
                try {
                    Properties props = PropertiesUtil.loadClose(url.openStream(), url);
                    if (!ProviderUtil.validVersion(props.getProperty(API_VERSION))) continue;
                    PROVIDERS.add(new Provider(props, url));
                }
                catch (IOException ioe) {
                    LOGGER.error("Unable to open " + url.toString(), (Throwable)ioe);
                }
            }
        }
    }
}

