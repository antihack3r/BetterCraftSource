// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Level;

public enum EventRoute
{
    ENQUEUE {
        @Override
        public void logMessage(final AsyncLogger asyncLogger, final String fqcn, final Level level, final Marker marker, final Message message, final Throwable thrown) {
        }
        
        @Override
        public void logMessage(final AsyncLoggerConfig asyncLoggerConfig, final LogEvent event) {
            asyncLoggerConfig.callAppendersInBackgroundThread(event);
        }
        
        @Override
        public void logMessage(final AsyncAppender asyncAppender, final LogEvent logEvent) {
            asyncAppender.logMessageInBackgroundThread(logEvent);
        }
    }, 
    SYNCHRONOUS {
        @Override
        public void logMessage(final AsyncLogger asyncLogger, final String fqcn, final Level level, final Marker marker, final Message message, final Throwable thrown) {
        }
        
        @Override
        public void logMessage(final AsyncLoggerConfig asyncLoggerConfig, final LogEvent event) {
            asyncLoggerConfig.callAppendersInCurrentThread(event);
        }
        
        @Override
        public void logMessage(final AsyncAppender asyncAppender, final LogEvent logEvent) {
            asyncAppender.logMessageInCurrentThread(logEvent);
        }
    }, 
    DISCARD {
        @Override
        public void logMessage(final AsyncLogger asyncLogger, final String fqcn, final Level level, final Marker marker, final Message message, final Throwable thrown) {
        }
        
        @Override
        public void logMessage(final AsyncLoggerConfig asyncLoggerConfig, final LogEvent event) {
        }
        
        @Override
        public void logMessage(final AsyncAppender asyncAppender, final LogEvent coreEvent) {
        }
    };
    
    public abstract void logMessage(final AsyncLogger p0, final String p1, final Level p2, final Marker p3, final Message p4, final Throwable p5);
    
    public abstract void logMessage(final AsyncLoggerConfig p0, final LogEvent p1);
    
    public abstract void logMessage(final AsyncAppender p0, final LogEvent p1);
}
