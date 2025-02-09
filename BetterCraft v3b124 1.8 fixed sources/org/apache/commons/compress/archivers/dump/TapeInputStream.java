/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.dump;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;
import org.apache.commons.compress.archivers.dump.DumpArchiveException;
import org.apache.commons.compress.archivers.dump.DumpArchiveUtil;
import org.apache.commons.compress.archivers.dump.ShortFileException;
import org.apache.commons.compress.archivers.dump.UnsupportedCompressionAlgorithmException;
import org.apache.commons.compress.utils.IOUtils;

class TapeInputStream
extends FilterInputStream {
    private byte[] blockBuffer = new byte[1024];
    private int currBlkIdx = -1;
    private int blockSize = 1024;
    private static final int recordSize = 1024;
    private int readOffset = 1024;
    private boolean isCompressed = false;
    private long bytesRead = 0L;

    public TapeInputStream(InputStream in2) {
        super(in2);
    }

    public void resetBlockSize(int recsPerBlock, boolean isCompressed) throws IOException {
        this.isCompressed = isCompressed;
        this.blockSize = 1024 * recsPerBlock;
        byte[] oldBuffer = this.blockBuffer;
        this.blockBuffer = new byte[this.blockSize];
        System.arraycopy(oldBuffer, 0, this.blockBuffer, 0, 1024);
        this.readFully(this.blockBuffer, 1024, this.blockSize - 1024);
        this.currBlkIdx = 0;
        this.readOffset = 1024;
    }

    public int available() throws IOException {
        if (this.readOffset < this.blockSize) {
            return this.blockSize - this.readOffset;
        }
        return this.in.available();
    }

    public int read() throws IOException {
        throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        if (len % 1024 != 0) {
            throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
        }
        int bytes = 0;
        while (bytes < len) {
            if (this.readOffset == this.blockSize && !this.readBlock(true)) {
                return -1;
            }
            int n2 = 0;
            n2 = this.readOffset + (len - bytes) <= this.blockSize ? len - bytes : this.blockSize - this.readOffset;
            System.arraycopy(this.blockBuffer, this.readOffset, b2, off, n2);
            this.readOffset += n2;
            bytes += n2;
            off += n2;
        }
        return bytes;
    }

    public long skip(long len) throws IOException {
        long bytes;
        long n2;
        if (len % 1024L != 0L) {
            throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
        }
        for (bytes = 0L; bytes < len; bytes += n2) {
            if (this.readOffset == this.blockSize && !this.readBlock(len - bytes < (long)this.blockSize)) {
                return -1L;
            }
            n2 = 0L;
            n2 = (long)this.readOffset + (len - bytes) <= (long)this.blockSize ? len - bytes : (long)(this.blockSize - this.readOffset);
            this.readOffset = (int)((long)this.readOffset + n2);
        }
        return bytes;
    }

    public void close() throws IOException {
        if (this.in != null && this.in != System.in) {
            this.in.close();
        }
    }

    public byte[] peek() throws IOException {
        if (this.readOffset == this.blockSize && !this.readBlock(true)) {
            return null;
        }
        byte[] b2 = new byte[1024];
        System.arraycopy(this.blockBuffer, this.readOffset, b2, 0, b2.length);
        return b2;
    }

    public byte[] readRecord() throws IOException {
        byte[] result = new byte[1024];
        if (-1 == this.read(result, 0, result.length)) {
            throw new ShortFileException();
        }
        return result;
    }

    private boolean readBlock(boolean decompress) throws IOException {
        boolean success = true;
        if (this.in == null) {
            throw new IOException("input buffer is closed");
        }
        if (!this.isCompressed || this.currBlkIdx == -1) {
            success = this.readFully(this.blockBuffer, 0, this.blockSize);
            this.bytesRead += (long)this.blockSize;
        } else {
            boolean compressed;
            if (!this.readFully(this.blockBuffer, 0, 4)) {
                return false;
            }
            this.bytesRead += 4L;
            int h2 = DumpArchiveUtil.convert32(this.blockBuffer, 0);
            boolean bl2 = compressed = (h2 & 1) == 1;
            if (!compressed) {
                success = this.readFully(this.blockBuffer, 0, this.blockSize);
                this.bytesRead += (long)this.blockSize;
            } else {
                int flags = h2 >> 1 & 7;
                int length = h2 >> 4 & 0xFFFFFFF;
                byte[] compBuffer = new byte[length];
                success = this.readFully(compBuffer, 0, length);
                this.bytesRead += (long)length;
                if (!decompress) {
                    Arrays.fill(this.blockBuffer, (byte)0);
                } else {
                    switch (DumpArchiveConstants.COMPRESSION_TYPE.find(flags & 3)) {
                        case ZLIB: {
                            try {
                                Inflater inflator = new Inflater();
                                inflator.setInput(compBuffer, 0, compBuffer.length);
                                length = inflator.inflate(this.blockBuffer);
                                if (length != this.blockSize) {
                                    throw new ShortFileException();
                                }
                                inflator.end();
                                break;
                            }
                            catch (DataFormatException e2) {
                                throw new DumpArchiveException("bad data", e2);
                            }
                        }
                        case BZLIB: {
                            throw new UnsupportedCompressionAlgorithmException("BZLIB2");
                        }
                        case LZO: {
                            throw new UnsupportedCompressionAlgorithmException("LZO");
                        }
                        default: {
                            throw new UnsupportedCompressionAlgorithmException();
                        }
                    }
                }
            }
        }
        ++this.currBlkIdx;
        this.readOffset = 0;
        return success;
    }

    private boolean readFully(byte[] b2, int off, int len) throws IOException {
        int count = IOUtils.readFully(this.in, b2, off, len);
        if (count < len) {
            throw new ShortFileException();
        }
        return true;
    }

    public long getBytesRead() {
        return this.bytesRead;
    }
}

