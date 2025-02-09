/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.status;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;

public class StatusData
implements Serializable {
    private static final long serialVersionUID = -4341916115118014017L;
    private final long timestamp = System.currentTimeMillis();
    private final StackTraceElement caller;
    private final Level level;
    private final Message msg;
    private final Throwable throwable;

    public StatusData(StackTraceElement caller, Level level, Message msg, Throwable t2) {
        this.caller = caller;
        this.level = level;
        this.msg = msg;
        this.throwable = t2;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public StackTraceElement getStackTraceElement() {
        return this.caller;
    }

    public Level getLevel() {
        return this.level;
    }

    public Message getMessage() {
        return this.msg;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public String getFormattedStatus() {
        StringBuilder sb2 = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        sb2.append(format.format(new Date(this.timestamp)));
        sb2.append(" ");
        sb2.append(this.level.toString());
        sb2.append(" ");
        sb2.append(this.msg.getFormattedMessage());
        Object[] params = this.msg.getParameters();
        Throwable t2 = this.throwable == null && params != null && params[params.length - 1] instanceof Throwable ? (Throwable)params[params.length - 1] : this.throwable;
        if (t2 != null) {
            sb2.append(" ");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            t2.printStackTrace(new PrintStream(baos));
            sb2.append(baos.toString());
        }
        return sb2.toString();
    }
}

