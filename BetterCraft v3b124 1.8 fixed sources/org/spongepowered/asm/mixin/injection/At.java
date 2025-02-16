/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.spongepowered.asm.mixin.injection.Desc;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface At {
    public String id() default "";

    public String value();

    public String slice() default "";

    public Shift shift() default Shift.NONE;

    public int by() default 0;

    public String[] args() default {};

    public String target() default "";

    public Desc desc() default @Desc(value="");

    public int ordinal() default -1;

    public int opcode() default -1;

    public boolean remap() default true;

    public static enum Shift {
        NONE,
        BEFORE,
        AFTER,
        BY;

    }
}

