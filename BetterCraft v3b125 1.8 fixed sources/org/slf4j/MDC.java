/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j;

import java.io.Closeable;
import java.util.Deque;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.helpers.Util;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class MDC {
    static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
    private static final String MDC_APAPTER_CANNOT_BE_NULL_MESSAGE = "MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA";
    static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
    static MDCAdapter mdcAdapter;

    private MDC() {
    }

    public static void put(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        mdcAdapter.put(key, val);
    }

    public static MDCCloseable putCloseable(String key, String val) throws IllegalArgumentException {
        MDC.put(key, val);
        return new MDCCloseable(key);
    }

    public static String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        return mdcAdapter.get(key);
    }

    public static void remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        mdcAdapter.remove(key);
    }

    public static void clear() {
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        mdcAdapter.clear();
    }

    public static Map<String, String> getCopyOfContextMap() {
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        return mdcAdapter.getCopyOfContextMap();
    }

    public static void setContextMap(Map<String, String> contextMap) {
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        mdcAdapter.setContextMap(contextMap);
    }

    public static MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    public static void pushByKey(String key, String value) {
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        mdcAdapter.pushByKey(key, value);
    }

    public static String popByKey(String key) {
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        return mdcAdapter.popByKey(key);
    }

    public Deque<String> getCopyOfDequeByKey(String key) {
        if (mdcAdapter == null) {
            throw new IllegalStateException(MDC_APAPTER_CANNOT_BE_NULL_MESSAGE);
        }
        return mdcAdapter.getCopyOfDequeByKey(key);
    }

    static {
        SLF4JServiceProvider provider = LoggerFactory.getProvider();
        if (provider != null) {
            mdcAdapter = provider.getMDCAdapter();
        } else {
            Util.report("Failed to find provider.");
            Util.report("Defaulting to no-operation MDCAdapter implementation.");
            mdcAdapter = new NOPMDCAdapter();
        }
    }

    public static class MDCCloseable
    implements Closeable {
        private final String key;

        private MDCCloseable(String key) {
            this.key = key;
        }

        @Override
        public void close() {
            MDC.remove(this.key);
        }
    }
}

