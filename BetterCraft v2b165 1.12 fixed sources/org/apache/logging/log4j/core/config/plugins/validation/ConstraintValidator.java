// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.config.plugins.validation;

import java.lang.annotation.Annotation;

public interface ConstraintValidator<A extends Annotation>
{
    void initialize(final A p0);
    
    boolean isValid(final String p0, final Object p1);
}
