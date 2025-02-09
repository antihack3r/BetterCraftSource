/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.simple;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public class SimpleLogger
extends AbstractLogger {
    private static final char SPACE = ' ';
    private DateFormat dateFormatter;
    private Level level;
    private final boolean showDateTime;
    private final boolean showContextMap;
    private PrintStream stream;
    private final String logName;

    public SimpleLogger(String name, Level defaultLevel, boolean showLogName, boolean showShortLogName, boolean showDateTime, boolean showContextMap, String dateTimeFormat, MessageFactory messageFactory, PropertiesUtil props, PrintStream stream) {
        super(name, messageFactory);
        int index;
        String lvl = props.getStringProperty("org.apache.logging.log4j.simplelog." + name + ".level");
        this.level = Level.toLevel(lvl, defaultLevel);
        this.logName = showShortLogName ? ((index = name.lastIndexOf(".")) > 0 && index < name.length() ? name.substring(index + 1) : name) : (showLogName ? name : null);
        this.showDateTime = showDateTime;
        this.showContextMap = showContextMap;
        this.stream = stream;
        if (showDateTime) {
            try {
                this.dateFormatter = new SimpleDateFormat(dateTimeFormat);
            }
            catch (IllegalArgumentException e2) {
                this.dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");
            }
        }
    }

    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        if (level != null) {
            this.level = level;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void log(Marker marker, String fqcn, Level level, Message msg, Throwable throwable) {
        Map<String, String> mdc;
        StringBuilder sb2 = new StringBuilder();
        if (this.showDateTime) {
            String dateText;
            Date now = new Date();
            DateFormat dateFormat = this.dateFormatter;
            synchronized (dateFormat) {
                dateText = this.dateFormatter.format(now);
            }
            sb2.append(dateText);
            sb2.append(' ');
        }
        sb2.append(level.toString());
        sb2.append(' ');
        if (this.logName != null && this.logName.length() > 0) {
            sb2.append(this.logName);
            sb2.append(' ');
        }
        sb2.append(msg.getFormattedMessage());
        if (this.showContextMap && (mdc = ThreadContext.getContext()).size() > 0) {
            sb2.append(' ');
            sb2.append(mdc.toString());
            sb2.append(' ');
        }
        Object[] params = msg.getParameters();
        Throwable t2 = throwable == null && params != null && params[params.length - 1] instanceof Throwable ? (Throwable)params[params.length - 1] : throwable;
        if (t2 != null) {
            sb2.append(' ');
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            t2.printStackTrace(new PrintStream(baos));
            sb2.append(baos.toString());
        }
        this.stream.println(sb2.toString());
    }

    @Override
    protected boolean isEnabled(Level level, Marker marker, String msg) {
        return this.level.intLevel() >= level.intLevel();
    }

    @Override
    protected boolean isEnabled(Level level, Marker marker, String msg, Throwable t2) {
        return this.level.intLevel() >= level.intLevel();
    }

    @Override
    protected boolean isEnabled(Level level, Marker marker, String msg, Object ... p1) {
        return this.level.intLevel() >= level.intLevel();
    }

    @Override
    protected boolean isEnabled(Level level, Marker marker, Object msg, Throwable t2) {
        return this.level.intLevel() >= level.intLevel();
    }

    @Override
    protected boolean isEnabled(Level level, Marker marker, Message msg, Throwable t2) {
        return this.level.intLevel() >= level.intLevel();
    }
}

