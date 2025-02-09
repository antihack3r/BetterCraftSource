/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.VerifyException;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class Verify {
    public static void verify(boolean expression) {
        if (!expression) {
            throw new VerifyException();
        }
    }

    public static void verify(boolean expression, @Nullable String errorMessageTemplate, Object ... errorMessageArgs) {
        if (!expression) {
            throw new VerifyException(Preconditions.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static <T> T verifyNotNull(@Nullable T reference) {
        return Verify.verifyNotNull(reference, "expected a non-null reference", new Object[0]);
    }

    public static <T> T verifyNotNull(@Nullable T reference, @Nullable String errorMessageTemplate, Object ... errorMessageArgs) {
        Verify.verify(reference != null, errorMessageTemplate, errorMessageArgs);
        return reference;
    }

    private Verify() {
    }
}

