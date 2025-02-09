/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.dump;

import org.apache.commons.compress.archivers.dump.DumpArchiveException;

public class InvalidFormatException
extends DumpArchiveException {
    private static final long serialVersionUID = 1L;
    protected long offset;

    public InvalidFormatException() {
        super("there was an error decoding a tape segment");
    }

    public InvalidFormatException(long offset) {
        super("there was an error decoding a tape segment header at offset " + offset + ".");
        this.offset = offset;
    }

    public long getOffset() {
        return this.offset;
    }
}

