// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "ThreadPatternConverter", category = "Converter")
@ConverterKeys({ "t", "thread" })
public final class ThreadPatternConverter extends LogEventPatternConverter
{
    private static final ThreadPatternConverter INSTANCE;
    
    private ThreadPatternConverter() {
        super("Thread", "thread");
    }
    
    public static ThreadPatternConverter newInstance(final String[] options) {
        return ThreadPatternConverter.INSTANCE;
    }
    
    @Override
    public void format(final LogEvent event, final StringBuilder toAppendTo) {
        toAppendTo.append(event.getThreadName());
    }
    
    static {
        INSTANCE = new ThreadPatternConverter();
    }
}
