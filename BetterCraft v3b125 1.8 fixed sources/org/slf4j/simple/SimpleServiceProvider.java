/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.simple;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.simple.SimpleLoggerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class SimpleServiceProvider
implements SLF4JServiceProvider {
    public static String REQUESTED_API_VERSION = "2.0.99";
    private ILoggerFactory loggerFactory;
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;

    @Override
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return this.mdcAdapter;
    }

    @Override
    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }

    @Override
    public void initialize() {
        this.loggerFactory = new SimpleLoggerFactory();
        this.markerFactory = new BasicMarkerFactory();
        this.mdcAdapter = new NOPMDCAdapter();
    }
}

