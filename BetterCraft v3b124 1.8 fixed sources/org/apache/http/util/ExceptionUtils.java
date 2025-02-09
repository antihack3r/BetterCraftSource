/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.util;

import java.lang.reflect.Method;

@Deprecated
public final class ExceptionUtils {
    private static final Method INIT_CAUSE_METHOD = ExceptionUtils.getInitCauseMethod();

    private static Method getInitCauseMethod() {
        try {
            Class[] paramsClasses = new Class[]{Throwable.class};
            return Throwable.class.getMethod("initCause", paramsClasses);
        }
        catch (NoSuchMethodException e2) {
            return null;
        }
    }

    public static void initCause(Throwable throwable, Throwable cause) {
        if (INIT_CAUSE_METHOD != null) {
            try {
                INIT_CAUSE_METHOD.invoke((Object)throwable, cause);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private ExceptionUtils() {
    }
}

