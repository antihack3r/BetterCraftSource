// 
// Decompiled by Procyon v0.6.0
// 

package org.lwjgl;

public class LWJGLException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public LWJGLException() {
    }
    
    public LWJGLException(final String msg) {
        super(msg);
    }
    
    public LWJGLException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public LWJGLException(final Throwable cause) {
        super(cause);
    }
}
