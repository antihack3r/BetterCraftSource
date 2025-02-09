// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public final class Throwables
{
    private Throwables() {
    }
    
    public static Throwable getRootCause(final Throwable throwable) {
        Throwable root;
        Throwable cause;
        for (root = throwable; (cause = root.getCause()) != null; root = cause) {}
        return root;
    }
    
    public static List<String> toStringList(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
        }
        catch (final RuntimeException ex2) {}
        pw.flush();
        final List<String> lines = new ArrayList<String>();
        final LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
        }
        catch (final IOException ex) {
            if (ex instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            lines.add(ex.toString());
        }
        finally {
            Closer.closeSilently(reader);
        }
        return lines;
    }
    
    public static void rethrow(final Throwable t) {
        rethrow0(t);
    }
    
    private static <T extends Throwable> void rethrow0(final Throwable t) throws T, Throwable {
        throw t;
    }
}
