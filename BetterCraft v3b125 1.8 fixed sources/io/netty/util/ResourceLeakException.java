/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import java.util.Arrays;

@Deprecated
public class ResourceLeakException
extends RuntimeException {
    private static final long serialVersionUID = 7186453858343358280L;
    private final StackTraceElement[] cachedStackTrace = this.getStackTrace();

    public ResourceLeakException() {
    }

    public ResourceLeakException(String message) {
        super(message);
    }

    public ResourceLeakException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceLeakException(Throwable cause) {
        super(cause);
    }

    public int hashCode() {
        StackTraceElement[] trace = this.cachedStackTrace;
        int hashCode = 0;
        for (StackTraceElement e2 : trace) {
            hashCode = hashCode * 31 + e2.hashCode();
        }
        return hashCode;
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof ResourceLeakException)) {
            return false;
        }
        if (o2 == this) {
            return true;
        }
        return Arrays.equals(this.cachedStackTrace, ((ResourceLeakException)o2).cachedStackTrace);
    }
}

