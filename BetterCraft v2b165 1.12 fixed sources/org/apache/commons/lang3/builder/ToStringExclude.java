// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.commons.lang3.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ToStringExclude {
}
