/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.dump;

import org.apache.commons.compress.archivers.dump.DumpArchiveException;

public class UnsupportedCompressionAlgorithmException
extends DumpArchiveException {
    private static final long serialVersionUID = 1L;

    public UnsupportedCompressionAlgorithmException() {
        super("this file uses an unsupported compression algorithm.");
    }

    public UnsupportedCompressionAlgorithmException(String alg2) {
        super("this file uses an unsupported compression algorithm: " + alg2 + ".");
    }
}

