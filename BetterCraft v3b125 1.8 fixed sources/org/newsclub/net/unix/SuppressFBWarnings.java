/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.CLASS)
@interface SuppressFBWarnings {
    public String[] value() default {};
}

