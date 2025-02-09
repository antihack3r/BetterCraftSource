// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.util;

import org.apache.logging.log4j.status.StatusLogger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Properties;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.locks.Lock;
import org.apache.logging.log4j.spi.Provider;
import java.util.Collection;

public final class ProviderUtil
{
    protected static final String PROVIDER_RESOURCE = "META-INF/log4j-provider.properties";
    protected static final Collection<Provider> PROVIDERS;
    protected static final Lock STARTUP_LOCK;
    private static final String API_VERSION = "Log4jAPIVersion";
    private static final String[] COMPATIBLE_API_VERSIONS;
    private static final Logger LOGGER;
    private static volatile ProviderUtil instance;
    
    private ProviderUtil() {
        for (final LoaderUtil.UrlResource resource : LoaderUtil.findUrlResources("META-INF/log4j-provider.properties")) {
            loadProvider(resource.getUrl(), resource.getClassLoader());
        }
    }
    
    protected static void loadProvider(final URL url, final ClassLoader cl) {
        try {
            final Properties props = PropertiesUtil.loadClose(url.openStream(), url);
            if (validVersion(props.getProperty("Log4jAPIVersion"))) {
                final Provider provider = new Provider(props, url, cl);
                ProviderUtil.PROVIDERS.add(provider);
                ProviderUtil.LOGGER.debug("Loaded Provider {}", provider);
            }
        }
        catch (final IOException e) {
            ProviderUtil.LOGGER.error("Unable to open {}", url, e);
        }
    }
    
    @Deprecated
    protected static void loadProviders(final Enumeration<URL> urls, final ClassLoader cl) {
        if (urls != null) {
            while (urls.hasMoreElements()) {
                loadProvider(urls.nextElement(), cl);
            }
        }
    }
    
    public static Iterable<Provider> getProviders() {
        lazyInit();
        return ProviderUtil.PROVIDERS;
    }
    
    public static boolean hasProviders() {
        lazyInit();
        return !ProviderUtil.PROVIDERS.isEmpty();
    }
    
    protected static void lazyInit() {
        if (ProviderUtil.instance == null) {
            try {
                ProviderUtil.STARTUP_LOCK.lockInterruptibly();
                try {
                    if (ProviderUtil.instance == null) {
                        ProviderUtil.instance = new ProviderUtil();
                    }
                }
                finally {
                    ProviderUtil.STARTUP_LOCK.unlock();
                }
            }
            catch (final InterruptedException e) {
                ProviderUtil.LOGGER.fatal("Interrupted before Log4j Providers could be loaded.", e);
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static ClassLoader findClassLoader() {
        return LoaderUtil.getThreadContextClassLoader();
    }
    
    private static boolean validVersion(final String version) {
        for (final String v : ProviderUtil.COMPATIBLE_API_VERSIONS) {
            if (version.startsWith(v)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        PROVIDERS = new HashSet<Provider>();
        STARTUP_LOCK = new ReentrantLock();
        COMPATIBLE_API_VERSIONS = new String[] { "2.0.0", "2.1.0", "2.2.0", "2.3.0", "2.4.0", "2.5.0", "2.6.0" };
        LOGGER = StatusLogger.getLogger();
    }
}
