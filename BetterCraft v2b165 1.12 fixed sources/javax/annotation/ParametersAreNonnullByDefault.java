// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;

@Documented
@Nonnull
@TypeQualifierDefault({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersAreNonnullByDefault {
}
