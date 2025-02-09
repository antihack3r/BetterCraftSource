/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client;

import java.io.IOException;
import org.apache.http.annotation.Immutable;

@Immutable
public class ClientProtocolException
extends IOException {
    private static final long serialVersionUID = -5596590843227115865L;

    public ClientProtocolException() {
    }

    public ClientProtocolException(String s2) {
        super(s2);
    }

    public ClientProtocolException(Throwable cause) {
        this.initCause(cause);
    }

    public ClientProtocolException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}

