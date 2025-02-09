/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.simple;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.AccessController;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.slf4j.helpers.Util;
import org.slf4j.simple.OutputChoice;

public class SimpleLoggerConfiguration {
    private static final String CONFIGURATION_FILE = "simplelogger.properties";
    static int DEFAULT_LOG_LEVEL_DEFAULT = 20;
    int defaultLogLevel = DEFAULT_LOG_LEVEL_DEFAULT;
    private static final boolean SHOW_DATE_TIME_DEFAULT = false;
    boolean showDateTime = false;
    private static final String DATE_TIME_FORMAT_STR_DEFAULT;
    private static String dateTimeFormatStr;
    DateFormat dateFormatter = null;
    private static final boolean SHOW_THREAD_NAME_DEFAULT = true;
    boolean showThreadName = true;
    private static final boolean SHOW_THREAD_ID_DEFAULT = false;
    boolean showThreadId = false;
    static final boolean SHOW_LOG_NAME_DEFAULT = true;
    boolean showLogName = true;
    private static final boolean SHOW_SHORT_LOG_NAME_DEFAULT = false;
    boolean showShortLogName = false;
    private static final boolean LEVEL_IN_BRACKETS_DEFAULT = false;
    boolean levelInBrackets = false;
    private static final String LOG_FILE_DEFAULT = "System.err";
    private String logFile = "System.err";
    OutputChoice outputChoice = null;
    private static final boolean CACHE_OUTPUT_STREAM_DEFAULT = false;
    private boolean cacheOutputStream = false;
    private static final String WARN_LEVELS_STRING_DEFAULT = "WARN";
    String warnLevelString = "WARN";
    private final Properties properties = new Properties();

    void init() {
        this.loadProperties();
        String defaultLogLevelString = this.getStringProperty("org.slf4j.simpleLogger.defaultLogLevel", null);
        if (defaultLogLevelString != null) {
            this.defaultLogLevel = SimpleLoggerConfiguration.stringToLevel(defaultLogLevelString);
        }
        this.showLogName = this.getBooleanProperty("org.slf4j.simpleLogger.showLogName", true);
        this.showShortLogName = this.getBooleanProperty("org.slf4j.simpleLogger.showShortLogName", false);
        this.showDateTime = this.getBooleanProperty("org.slf4j.simpleLogger.showDateTime", false);
        this.showThreadName = this.getBooleanProperty("org.slf4j.simpleLogger.showThreadName", true);
        this.showThreadId = this.getBooleanProperty("org.slf4j.simpleLogger.showThreadId", false);
        dateTimeFormatStr = this.getStringProperty("org.slf4j.simpleLogger.dateTimeFormat", DATE_TIME_FORMAT_STR_DEFAULT);
        this.levelInBrackets = this.getBooleanProperty("org.slf4j.simpleLogger.levelInBrackets", false);
        this.warnLevelString = this.getStringProperty("org.slf4j.simpleLogger.warnLevelString", WARN_LEVELS_STRING_DEFAULT);
        this.logFile = this.getStringProperty("org.slf4j.simpleLogger.logFile", this.logFile);
        this.cacheOutputStream = this.getBooleanProperty("org.slf4j.simpleLogger.cacheOutputStream", false);
        this.outputChoice = SimpleLoggerConfiguration.computeOutputChoice(this.logFile, this.cacheOutputStream);
        if (dateTimeFormatStr != null) {
            try {
                this.dateFormatter = new SimpleDateFormat(dateTimeFormatStr);
            }
            catch (IllegalArgumentException e2) {
                Util.report("Bad date format in simplelogger.properties; will output relative time", e2);
            }
        }
    }

    private void loadProperties() {
        InputStream in2 = AccessController.doPrivileged(() -> {
            ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
            if (threadCL != null) {
                return threadCL.getResourceAsStream(CONFIGURATION_FILE);
            }
            return ClassLoader.getSystemResourceAsStream(CONFIGURATION_FILE);
        });
        if (null != in2) {
            try {
                this.properties.load(in2);
            }
            catch (IOException iOException) {
            }
            finally {
                try {
                    in2.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    String getStringProperty(String name, String defaultValue) {
        String prop = this.getStringProperty(name);
        return prop == null ? defaultValue : prop;
    }

    boolean getBooleanProperty(String name, boolean defaultValue) {
        String prop = this.getStringProperty(name);
        return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
    }

    String getStringProperty(String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return prop == null ? this.properties.getProperty(name) : prop;
    }

    static int stringToLevel(String levelStr) {
        if ("trace".equalsIgnoreCase(levelStr)) {
            return 0;
        }
        if ("debug".equalsIgnoreCase(levelStr)) {
            return 10;
        }
        if ("info".equalsIgnoreCase(levelStr)) {
            return 20;
        }
        if ("warn".equalsIgnoreCase(levelStr)) {
            return 30;
        }
        if ("error".equalsIgnoreCase(levelStr)) {
            return 40;
        }
        if ("off".equalsIgnoreCase(levelStr)) {
            return 50;
        }
        return 20;
    }

    private static OutputChoice computeOutputChoice(String logFile, boolean cacheOutputStream) {
        if (LOG_FILE_DEFAULT.equalsIgnoreCase(logFile)) {
            if (cacheOutputStream) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_ERR);
            }
            return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
        }
        if ("System.out".equalsIgnoreCase(logFile)) {
            if (cacheOutputStream) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_OUT);
            }
            return new OutputChoice(OutputChoice.OutputChoiceType.SYS_OUT);
        }
        try {
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream printStream = new PrintStream(fos);
            return new OutputChoice(printStream);
        }
        catch (FileNotFoundException e2) {
            Util.report("Could not open [" + logFile + "]. Defaulting to System.err", e2);
            return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
        }
    }

    static {
        dateTimeFormatStr = DATE_TIME_FORMAT_STR_DEFAULT = null;
    }
}

