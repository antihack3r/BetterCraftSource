// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.net.ssl;

public class StoreConfigurationException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public StoreConfigurationException(final Exception e) {
        super(e);
    }
    
    public StoreConfigurationException(final String message) {
        super(message);
    }
}
