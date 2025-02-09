// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.LoggingException;

public class AppenderLoggingException extends LoggingException
{
    private static final long serialVersionUID = 6545990597472958303L;
    
    public AppenderLoggingException(final String message) {
        super(message);
    }
    
    public AppenderLoggingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public AppenderLoggingException(final Throwable cause) {
        super(cause);
    }
}
