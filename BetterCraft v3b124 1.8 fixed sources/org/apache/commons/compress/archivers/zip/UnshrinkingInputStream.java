/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.z._internal_.InternalLZWInputStream;

class UnshrinkingInputStream
extends InternalLZWInputStream {
    private static final int MAX_CODE_SIZE = 13;
    private static final int MAX_TABLE_SIZE = 8192;
    private final boolean[] isUsed;

    public UnshrinkingInputStream(InputStream inputStream) throws IOException {
        super(inputStream);
        this.setClearCode(this.codeSize);
        this.initializeTables(13);
        this.isUsed = new boolean[this.prefixes.length];
        for (int i2 = 0; i2 < 256; ++i2) {
            this.isUsed[i2] = true;
        }
        this.tableSize = this.clearCode + 1;
    }

    protected int addEntry(int previousCode, byte character) throws IOException {
        while (this.tableSize < 8192 && this.isUsed[this.tableSize]) {
            ++this.tableSize;
        }
        int idx = this.addEntry(previousCode, character, 8192);
        if (idx >= 0) {
            this.isUsed[idx] = true;
        }
        return idx;
    }

    private void partialClear() {
        int i2;
        boolean[] isParent = new boolean[8192];
        for (i2 = 0; i2 < this.isUsed.length; ++i2) {
            if (!this.isUsed[i2] || this.prefixes[i2] == -1) continue;
            isParent[this.prefixes[i2]] = true;
        }
        for (i2 = this.clearCode + 1; i2 < isParent.length; ++i2) {
            if (isParent[i2]) continue;
            this.isUsed[i2] = false;
            this.prefixes[i2] = -1;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected int decompressNextSymbol() throws IOException {
        int code = this.readNextCode();
        if (code < 0) {
            return -1;
        }
        if (code == this.clearCode) {
            int subCode = this.readNextCode();
            if (subCode < 0) {
                throw new IOException("Unexpected EOF;");
            }
            if (subCode == 1) {
                if (this.codeSize >= 13) throw new IOException("Attempt to increase code size beyond maximum");
                ++this.codeSize;
                return 0;
            } else {
                if (subCode != 2) throw new IOException("Invalid clear code subcode " + subCode);
                this.partialClear();
                this.tableSize = this.clearCode + 1;
            }
            return 0;
        }
        boolean addedUnfinishedEntry = false;
        int effectiveCode = code;
        if (this.isUsed[code]) return this.expandCodeToOutputStack(effectiveCode, addedUnfinishedEntry);
        effectiveCode = this.addRepeatOfPreviousCode();
        addedUnfinishedEntry = true;
        return this.expandCodeToOutputStack(effectiveCode, addedUnfinishedEntry);
    }
}

