/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.simple;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.NormalizedParameters;
import org.slf4j.simple.SimpleLoggerConfiguration;

public class SimpleLogger
extends LegacyAbstractLogger {
    private static final long serialVersionUID = -632788891211436180L;
    private static final long START_TIME = System.currentTimeMillis();
    protected static final int LOG_LEVEL_TRACE = 0;
    protected static final int LOG_LEVEL_DEBUG = 10;
    protected static final int LOG_LEVEL_INFO = 20;
    protected static final int LOG_LEVEL_WARN = 30;
    protected static final int LOG_LEVEL_ERROR = 40;
    static char SP = (char)32;
    static final String TID_PREFIX = "tid=";
    protected static final int LOG_LEVEL_OFF = 50;
    private static boolean INITIALIZED = false;
    static final SimpleLoggerConfiguration CONFIG_PARAMS = new SimpleLoggerConfiguration();
    protected int currentLogLevel = 20;
    private transient String shortLogName = null;
    public static final String SYSTEM_PREFIX = "org.slf4j.simpleLogger.";
    public static final String LOG_KEY_PREFIX = "org.slf4j.simpleLogger.log.";
    public static final String CACHE_OUTPUT_STREAM_STRING_KEY = "org.slf4j.simpleLogger.cacheOutputStream";
    public static final String WARN_LEVEL_STRING_KEY = "org.slf4j.simpleLogger.warnLevelString";
    public static final String LEVEL_IN_BRACKETS_KEY = "org.slf4j.simpleLogger.levelInBrackets";
    public static final String LOG_FILE_KEY = "org.slf4j.simpleLogger.logFile";
    public static final String SHOW_SHORT_LOG_NAME_KEY = "org.slf4j.simpleLogger.showShortLogName";
    public static final String SHOW_LOG_NAME_KEY = "org.slf4j.simpleLogger.showLogName";
    public static final String SHOW_THREAD_NAME_KEY = "org.slf4j.simpleLogger.showThreadName";
    public static final String SHOW_THREAD_ID_KEY = "org.slf4j.simpleLogger.showThreadId";
    public static final String DATE_TIME_FORMAT_KEY = "org.slf4j.simpleLogger.dateTimeFormat";
    public static final String SHOW_DATE_TIME_KEY = "org.slf4j.simpleLogger.showDateTime";
    public static final String DEFAULT_LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel";

    static void lazyInit() {
        if (INITIALIZED) {
            return;
        }
        INITIALIZED = true;
        SimpleLogger.init();
    }

    static void init() {
        CONFIG_PARAMS.init();
    }

    SimpleLogger(String name) {
        this.name = name;
        String levelString = this.recursivelyComputeLevelString();
        this.currentLogLevel = levelString != null ? SimpleLoggerConfiguration.stringToLevel(levelString) : SimpleLogger.CONFIG_PARAMS.defaultLogLevel;
    }

    String recursivelyComputeLevelString() {
        String tempName = this.name;
        String levelString = null;
        int indexOfLastDot = tempName.length();
        while (levelString == null && indexOfLastDot > -1) {
            tempName = tempName.substring(0, indexOfLastDot);
            levelString = CONFIG_PARAMS.getStringProperty(LOG_KEY_PREFIX + tempName, null);
            indexOfLastDot = String.valueOf(tempName).lastIndexOf(".");
        }
        return levelString;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void write(StringBuilder buf, Throwable t2) {
        PrintStream targetStream = SimpleLogger.CONFIG_PARAMS.outputChoice.getTargetPrintStream();
        SimpleLoggerConfiguration simpleLoggerConfiguration = CONFIG_PARAMS;
        synchronized (simpleLoggerConfiguration) {
            targetStream.println(buf.toString());
            this.writeThrowable(t2, targetStream);
            targetStream.flush();
        }
    }

    protected void writeThrowable(Throwable t2, PrintStream targetStream) {
        if (t2 != null) {
            t2.printStackTrace(targetStream);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getFormattedDate() {
        String dateText;
        Date now = new Date();
        DateFormat dateFormat = SimpleLogger.CONFIG_PARAMS.dateFormatter;
        synchronized (dateFormat) {
            dateText = SimpleLogger.CONFIG_PARAMS.dateFormatter.format(now);
        }
        return dateText;
    }

    private String computeShortName() {
        return this.name.substring(this.name.lastIndexOf(".") + 1);
    }

    protected boolean isLevelEnabled(int logLevel) {
        return logLevel >= this.currentLogLevel;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.isLevelEnabled(0);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.isLevelEnabled(10);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.isLevelEnabled(20);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.isLevelEnabled(30);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.isLevelEnabled(40);
    }

    @Override
    protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable t2) {
        ArrayList<Marker> markers = null;
        if (marker != null) {
            markers = new ArrayList<Marker>();
            markers.add(marker);
        }
        this.innerHandleNormalizedLoggingCall(level, markers, messagePattern, arguments, t2);
    }

    private void innerHandleNormalizedLoggingCall(Level level, List<Marker> markers, String messagePattern, Object[] arguments, Throwable t2) {
        StringBuilder buf = new StringBuilder(32);
        if (SimpleLogger.CONFIG_PARAMS.showDateTime) {
            if (SimpleLogger.CONFIG_PARAMS.dateFormatter != null) {
                buf.append(this.getFormattedDate());
                buf.append(SP);
            } else {
                buf.append(System.currentTimeMillis() - START_TIME);
                buf.append(SP);
            }
        }
        if (SimpleLogger.CONFIG_PARAMS.showThreadName) {
            buf.append('[');
            buf.append(Thread.currentThread().getName());
            buf.append("] ");
        }
        if (SimpleLogger.CONFIG_PARAMS.showThreadId) {
            buf.append(TID_PREFIX);
            buf.append(Thread.currentThread().getId());
            buf.append(SP);
        }
        if (SimpleLogger.CONFIG_PARAMS.levelInBrackets) {
            buf.append('[');
        }
        String levelStr = level.name();
        buf.append(levelStr);
        if (SimpleLogger.CONFIG_PARAMS.levelInBrackets) {
            buf.append(']');
        }
        buf.append(SP);
        if (SimpleLogger.CONFIG_PARAMS.showShortLogName) {
            if (this.shortLogName == null) {
                this.shortLogName = this.computeShortName();
            }
            buf.append(String.valueOf(this.shortLogName)).append(" - ");
        } else if (SimpleLogger.CONFIG_PARAMS.showLogName) {
            buf.append(String.valueOf(this.name)).append(" - ");
        }
        if (markers != null) {
            buf.append(SP);
            for (Marker marker : markers) {
                buf.append(marker.getName()).append(SP);
            }
        }
        String formattedMessage = MessageFormatter.basicArrayFormat(messagePattern, arguments);
        buf.append(formattedMessage);
        this.write(buf, t2);
    }

    public void log(LoggingEvent event) {
        int levelInt = event.getLevel().toInt();
        if (!this.isLevelEnabled(levelInt)) {
            return;
        }
        NormalizedParameters np2 = NormalizedParameters.normalize(event);
        this.innerHandleNormalizedLoggingCall(event.getLevel(), event.getMarkers(), np2.getMessage(), np2.getArguments(), event.getThrowable());
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return null;
    }
}

