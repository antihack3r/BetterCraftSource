/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.conn;

import java.io.IOException;

public interface ConnectionReleaseTrigger {
    public void releaseConnection() throws IOException;

    public void abortConnection() throws IOException;
}

