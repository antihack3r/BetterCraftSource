// 
// Decompiled by Procyon v0.6.0
// 

package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.CLASS)
public @interface Group {
    String name() default "";
    
    int min() default -1;
    
    int max() default -1;
}
