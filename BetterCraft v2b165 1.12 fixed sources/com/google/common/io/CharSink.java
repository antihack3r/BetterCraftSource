// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public abstract class CharSink
{
    protected CharSink() {
    }
    
    public abstract Writer openStream() throws IOException;
    
    public Writer openBufferedStream() throws IOException {
        final Writer writer = this.openStream();
        return (writer instanceof BufferedWriter) ? writer : new BufferedWriter(writer);
    }
    
    public void write(final CharSequence charSequence) throws IOException {
        Preconditions.checkNotNull(charSequence);
        final Closer closer = Closer.create();
        try {
            final Writer out = closer.register(this.openStream());
            out.append(charSequence);
            out.flush();
        }
        catch (final Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }
    
    public void writeLines(final Iterable<? extends CharSequence> lines) throws IOException {
        this.writeLines(lines, System.getProperty("line.separator"));
    }
    
    public void writeLines(final Iterable<? extends CharSequence> lines, final String lineSeparator) throws IOException {
        Preconditions.checkNotNull(lines);
        Preconditions.checkNotNull(lineSeparator);
        final Closer closer = Closer.create();
        try {
            final Writer out = closer.register(this.openBufferedStream());
            for (final CharSequence line : lines) {
                out.append(line).append((CharSequence)lineSeparator);
            }
            out.flush();
        }
        catch (final Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }
    
    @CanIgnoreReturnValue
    public long writeFrom(final Readable readable) throws IOException {
        Preconditions.checkNotNull(readable);
        final Closer closer = Closer.create();
        try {
            final Writer out = closer.register(this.openStream());
            final long written = CharStreams.copy(readable, out);
            out.flush();
            return written;
        }
        catch (final Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }
}
