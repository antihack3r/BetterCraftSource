// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.pattern;

public interface PatternConverter
{
    public static final String CATEGORY = "Converter";
    
    void format(final Object p0, final StringBuilder p1);
    
    String getName();
    
    String getStyleClass(final Object p0);
}
