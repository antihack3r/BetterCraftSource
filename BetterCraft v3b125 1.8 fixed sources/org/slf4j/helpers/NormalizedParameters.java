/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.helpers;

import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.MessageFormatter;

public class NormalizedParameters {
    final String message;
    final Object[] arguments;
    final Throwable throwable;

    public NormalizedParameters(String message, Object[] arguments, Throwable throwable) {
        this.message = message;
        this.arguments = arguments;
        this.throwable = throwable;
    }

    public NormalizedParameters(String message, Object[] arguments) {
        this(message, arguments, null);
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public static Throwable getThrowableCandidate(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return null;
        }
        Object lastEntry = argArray[argArray.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable)lastEntry;
        }
        return null;
    }

    public static Object[] trimmedCopy(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
        int trimmedLen = argArray.length - 1;
        Object[] trimmed = new Object[trimmedLen];
        if (trimmedLen > 0) {
            System.arraycopy(argArray, 0, trimmed, 0, trimmedLen);
        }
        return trimmed;
    }

    public static NormalizedParameters normalize(String msg, Object[] arguments, Throwable t2) {
        if (t2 != null) {
            return new NormalizedParameters(msg, arguments, t2);
        }
        if (arguments == null || arguments.length == 0) {
            return new NormalizedParameters(msg, arguments, t2);
        }
        Throwable throwableCandidate = NormalizedParameters.getThrowableCandidate(arguments);
        if (throwableCandidate != null) {
            Object[] trimmedArguments = MessageFormatter.trimmedCopy(arguments);
            return new NormalizedParameters(msg, trimmedArguments, throwableCandidate);
        }
        return new NormalizedParameters(msg, arguments);
    }

    public static NormalizedParameters normalize(LoggingEvent event) {
        return NormalizedParameters.normalize(event.getMessage(), event.getArgumentArray(), event.getThrowable());
    }
}

