/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.pattern;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.helpers.Constants;
import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name="ThrowablePatternConverter", category="Converter")
@ConverterKeys(value={"ex", "throwable", "exception"})
public class ThrowablePatternConverter
extends LogEventPatternConverter {
    private String rawOption;
    protected final ThrowableFormatOptions options;

    protected ThrowablePatternConverter(String name, String style, String[] options) {
        super(name, style);
        this.options = ThrowableFormatOptions.newInstance(options);
        if (options != null && options.length > 0) {
            this.rawOption = options[0];
        }
    }

    public static ThrowablePatternConverter newInstance(String[] options) {
        return new ThrowablePatternConverter("Throwable", "throwable", options);
    }

    @Override
    public void format(LogEvent event, StringBuilder buffer) {
        Throwable t2 = event.getThrown();
        if (this.isSubShortOption()) {
            this.formatSubShortOption(t2, buffer);
        } else if (t2 != null && this.options.anyLines()) {
            this.formatOption(t2, buffer);
        }
    }

    private boolean isSubShortOption() {
        return "short.message".equalsIgnoreCase(this.rawOption) || "short.localizedMessage".equalsIgnoreCase(this.rawOption) || "short.fileName".equalsIgnoreCase(this.rawOption) || "short.lineNumber".equalsIgnoreCase(this.rawOption) || "short.methodName".equalsIgnoreCase(this.rawOption) || "short.className".equalsIgnoreCase(this.rawOption);
    }

    private void formatSubShortOption(Throwable t2, StringBuilder buffer) {
        StackTraceElement[] trace;
        StackTraceElement throwingMethod = null;
        if (t2 != null && (trace = t2.getStackTrace()) != null && trace.length > 0) {
            throwingMethod = trace[0];
        }
        if (t2 != null && throwingMethod != null) {
            String toAppend = "";
            if ("short.className".equalsIgnoreCase(this.rawOption)) {
                toAppend = throwingMethod.getClassName();
            } else if ("short.methodName".equalsIgnoreCase(this.rawOption)) {
                toAppend = throwingMethod.getMethodName();
            } else if ("short.lineNumber".equalsIgnoreCase(this.rawOption)) {
                toAppend = String.valueOf(throwingMethod.getLineNumber());
            } else if ("short.message".equalsIgnoreCase(this.rawOption)) {
                toAppend = t2.getMessage();
            } else if ("short.localizedMessage".equalsIgnoreCase(this.rawOption)) {
                toAppend = t2.getLocalizedMessage();
            } else if ("short.fileName".equalsIgnoreCase(this.rawOption)) {
                toAppend = throwingMethod.getFileName();
            }
            int len = buffer.length();
            if (len > 0 && !Character.isWhitespace(buffer.charAt(len - 1))) {
                buffer.append(" ");
            }
            buffer.append(toAppend);
        }
    }

    private void formatOption(Throwable throwable, StringBuilder buffer) {
        StringWriter w2 = new StringWriter();
        throwable.printStackTrace(new PrintWriter(w2));
        int len = buffer.length();
        if (len > 0 && !Character.isWhitespace(buffer.charAt(len - 1))) {
            buffer.append(' ');
        }
        if (!this.options.allLines() || !Constants.LINE_SEP.equals(this.options.getSeparator())) {
            StringBuilder sb2 = new StringBuilder();
            String[] array = w2.toString().split(Constants.LINE_SEP);
            int limit = this.options.minLines(array.length) - 1;
            for (int i2 = 0; i2 <= limit; ++i2) {
                sb2.append(array[i2]);
                if (i2 >= limit) continue;
                sb2.append(this.options.getSeparator());
            }
            buffer.append(sb2.toString());
        } else {
            buffer.append(w2.toString());
        }
    }

    @Override
    public boolean handlesThrowable() {
        return true;
    }
}

