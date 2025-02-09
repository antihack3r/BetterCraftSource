// 
// Decompiled by Procyon v0.6.0
// 

package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface At {
    String id() default "";
    
    String value();
    
    String slice() default "";
    
    Shift shift() default Shift.NONE;
    
    int by() default 0;
    
    String[] args() default {};
    
    String target() default "";
    
    Desc desc() default @Desc("");
    
    int ordinal() default -1;
    
    int opcode() default -1;
    
    boolean remap() default true;
    
    public enum Shift
    {
        NONE, 
        BEFORE, 
        AFTER, 
        BY;
    }
}
