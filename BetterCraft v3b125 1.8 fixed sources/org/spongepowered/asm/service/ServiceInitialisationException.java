/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.service;

public class ServiceInitialisationException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceInitialisationException() {
    }

    public ServiceInitialisationException(String message) {
        super(message);
    }

    public ServiceInitialisationException(Throwable cause) {
        super(cause);
    }

    public ServiceInitialisationException(String message, Throwable cause) {
        super(message, cause);
    }
}

