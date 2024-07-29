/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import javazoom.jl.decoder.BitstreamErrors;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Crc16;
import javazoom.jl.decoder.Header;

public final class Bitstream
implements BitstreamErrors {
    static byte INITIAL_SYNC = 0;
    static byte STRICT_SYNC = 1;
    private static final int BUFFER_INT_SIZE = 433;
    private final int[] framebuffer = new int[433];
    private int framesize;
    private byte[] frame_bytes = new byte[1732];
    private int wordpointer;
    private int bitindex;
    private int syncword;
    private int header_pos = 0;
    private boolean single_ch_mode;
    private final int[] bitmask;
    private final PushbackInputStream source;
    private final Header header;
    private final byte[] syncbuf;
    private Crc16[] crc;
    private byte[] rawid3v2;
    private boolean firstframe;

    public Bitstream(InputStream in2) {
        int[] nArray = new int[18];
        nArray[1] = 1;
        nArray[2] = 3;
        nArray[3] = 7;
        nArray[4] = 15;
        nArray[5] = 31;
        nArray[6] = 63;
        nArray[7] = 127;
        nArray[8] = 255;
        nArray[9] = 511;
        nArray[10] = 1023;
        nArray[11] = 2047;
        nArray[12] = 4095;
        nArray[13] = 8191;
        nArray[14] = 16383;
        nArray[15] = Short.MAX_VALUE;
        nArray[16] = 65535;
        nArray[17] = 131071;
        this.bitmask = nArray;
        this.header = new Header();
        this.syncbuf = new byte[4];
        this.crc = new Crc16[1];
        this.rawid3v2 = null;
        this.firstframe = true;
        if (in2 == null) {
            throw new NullPointerException("in");
        }
        in2 = new BufferedInputStream(in2);
        this.loadID3v2(in2);
        this.firstframe = true;
        this.source = new PushbackInputStream(in2, 1732);
        this.closeFrame();
    }

    public int header_pos() {
        return this.header_pos;
    }

    private void loadID3v2(InputStream in2) {
        int size;
        block14: {
            size = -1;
            try {
                try {
                    in2.mark(10);
                    this.header_pos = size = this.readID3v2Header(in2);
                }
                catch (IOException iOException) {
                    try {
                        in2.reset();
                    }
                    catch (IOException iOException2) {}
                    break block14;
                }
            }
            catch (Throwable throwable) {
                try {
                    in2.reset();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                throw throwable;
            }
            try {
                in2.reset();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        try {
            if (size > 0) {
                this.rawid3v2 = new byte[size];
                in2.read(this.rawid3v2, 0, this.rawid3v2.length);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private int readID3v2Header(InputStream in2) throws IOException {
        byte[] id3header = new byte[4];
        int size = -10;
        in2.read(id3header, 0, 3);
        if (id3header[0] == 73 && id3header[1] == 68 && id3header[2] == 51) {
            in2.read(id3header, 0, 3);
            byte majorVersion = id3header[0];
            byte revision = id3header[1];
            in2.read(id3header, 0, 4);
            size = (id3header[0] << 21) + (id3header[1] << 14) + (id3header[2] << 7) + id3header[3];
        }
        return size + 10;
    }

    public InputStream getRawID3v2() {
        if (this.rawid3v2 == null) {
            return null;
        }
        ByteArrayInputStream bain = new ByteArrayInputStream(this.rawid3v2);
        return bain;
    }

    public void close() throws BitstreamException {
        try {
            this.source.close();
        }
        catch (IOException ex2) {
            throw this.newBitstreamException(258, ex2);
        }
    }

    public Header readFrame() throws BitstreamException {
        Header result;
        block7: {
            result = null;
            try {
                result = this.readNextFrame();
                if (this.firstframe) {
                    result.parseVBR(this.frame_bytes);
                    this.firstframe = false;
                }
            }
            catch (BitstreamException ex2) {
                if (ex2.getErrorCode() == 261) {
                    try {
                        this.closeFrame();
                        result = this.readNextFrame();
                    }
                    catch (BitstreamException e2) {
                        if (e2.getErrorCode() != 260) {
                            throw this.newBitstreamException(e2.getErrorCode(), e2);
                        }
                        break block7;
                    }
                }
                if (ex2.getErrorCode() == 260) break block7;
                throw this.newBitstreamException(ex2.getErrorCode(), ex2);
            }
        }
        return result;
    }

    private Header readNextFrame() throws BitstreamException {
        if (this.framesize == -1) {
            this.nextFrame();
        }
        return this.header;
    }

    private void nextFrame() throws BitstreamException {
        this.header.read_header(this, this.crc);
    }

    public void unreadFrame() throws BitstreamException {
        if (this.wordpointer == -1 && this.bitindex == -1 && this.framesize > 0) {
            try {
                this.source.unread(this.frame_bytes, 0, this.framesize);
            }
            catch (IOException ex2) {
                throw this.newBitstreamException(258);
            }
        }
    }

    public void closeFrame() {
        this.framesize = -1;
        this.wordpointer = -1;
        this.bitindex = -1;
    }

    public boolean isSyncCurrentPosition(int syncmode) throws BitstreamException {
        int read = this.readBytes(this.syncbuf, 0, 4);
        int headerstring = this.syncbuf[0] << 24 & 0xFF000000 | this.syncbuf[1] << 16 & 0xFF0000 | this.syncbuf[2] << 8 & 0xFF00 | this.syncbuf[3] << 0 & 0xFF;
        try {
            this.source.unread(this.syncbuf, 0, read);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        boolean sync = false;
        switch (read) {
            case 0: {
                sync = true;
                break;
            }
            case 4: {
                sync = this.isSyncMark(headerstring, syncmode, this.syncword);
            }
        }
        return sync;
    }

    public int readBits(int n2) {
        return this.get_bits(n2);
    }

    public int readCheckedBits(int n2) {
        return this.get_bits(n2);
    }

    protected BitstreamException newBitstreamException(int errorcode) {
        return new BitstreamException(errorcode, null);
    }

    protected BitstreamException newBitstreamException(int errorcode, Throwable throwable) {
        return new BitstreamException(errorcode, throwable);
    }

    int syncHeader(byte syncmode) throws BitstreamException {
        boolean sync;
        int bytesRead = this.readBytes(this.syncbuf, 0, 3);
        if (bytesRead != 3) {
            throw this.newBitstreamException(260, null);
        }
        int headerstring = this.syncbuf[0] << 16 & 0xFF0000 | this.syncbuf[1] << 8 & 0xFF00 | this.syncbuf[2] << 0 & 0xFF;
        do {
            headerstring <<= 8;
            if (this.readBytes(this.syncbuf, 3, 1) == 1) continue;
            throw this.newBitstreamException(260, null);
        } while (!(sync = this.isSyncMark(headerstring |= this.syncbuf[3] & 0xFF, syncmode, this.syncword)));
        return headerstring;
    }

    public boolean isSyncMark(int headerstring, int syncmode, int word) {
        boolean sync = false;
        if (syncmode == INITIAL_SYNC) {
            sync = (headerstring & 0xFFE00000) == -2097152;
        } else {
            boolean bl2 = (headerstring & 0xFFF80C00) == word && (headerstring & 0xC0) == 192 == this.single_ch_mode ? true : (sync = false);
        }
        if (sync) {
            boolean bl3 = sync = (headerstring >>> 10 & 3) != 3;
        }
        if (sync) {
            boolean bl4 = sync = (headerstring >>> 17 & 3) != 0;
        }
        if (sync) {
            sync = (headerstring >>> 19 & 3) != 1;
        }
        return sync;
    }

    int read_frame_data(int bytesize) throws BitstreamException {
        int numread = 0;
        numread = this.readFully(this.frame_bytes, 0, bytesize);
        this.framesize = bytesize;
        this.wordpointer = -1;
        this.bitindex = -1;
        return numread;
    }

    void parse_frame() throws BitstreamException {
        int b2 = 0;
        byte[] byteread = this.frame_bytes;
        int bytesize = this.framesize;
        int k2 = 0;
        while (k2 < bytesize) {
            boolean convert = false;
            byte b0 = 0;
            byte b1 = 0;
            byte b22 = 0;
            byte b3 = 0;
            b0 = byteread[k2];
            if (k2 + 1 < bytesize) {
                b1 = byteread[k2 + 1];
            }
            if (k2 + 2 < bytesize) {
                b22 = byteread[k2 + 2];
            }
            if (k2 + 3 < bytesize) {
                b3 = byteread[k2 + 3];
            }
            this.framebuffer[b2++] = b0 << 24 & 0xFF000000 | b1 << 16 & 0xFF0000 | b22 << 8 & 0xFF00 | b3 & 0xFF;
            k2 += 4;
        }
        this.wordpointer = 0;
        this.bitindex = 0;
    }

    public int get_bits(int number_of_bits) {
        int returnvalue = 0;
        int sum = this.bitindex + number_of_bits;
        if (this.wordpointer < 0) {
            this.wordpointer = 0;
        }
        if (sum <= 32) {
            returnvalue = this.framebuffer[this.wordpointer] >>> 32 - sum & this.bitmask[number_of_bits];
            if ((this.bitindex += number_of_bits) == 32) {
                this.bitindex = 0;
                ++this.wordpointer;
            }
            return returnvalue;
        }
        int Right = this.framebuffer[this.wordpointer] & 0xFFFF;
        ++this.wordpointer;
        int Left = this.framebuffer[this.wordpointer] & 0xFFFF0000;
        returnvalue = Right << 16 & 0xFFFF0000 | Left >>> 16 & 0xFFFF;
        returnvalue >>>= 48 - sum;
        this.bitindex = sum - 32;
        return returnvalue &= this.bitmask[number_of_bits];
    }

    void set_syncword(int syncword0) {
        this.syncword = syncword0 & 0xFFFFFF3F;
        this.single_ch_mode = (syncword0 & 0xC0) == 192;
    }

    private int readFully(byte[] b2, int offs, int len) throws BitstreamException {
        int nRead = 0;
        try {
            while (len > 0) {
                int bytesread = this.source.read(b2, offs, len);
                if (bytesread == -1) {
                    while (len-- > 0) {
                        b2[offs++] = 0;
                    }
                    break;
                }
                nRead += bytesread;
                offs += bytesread;
                len -= bytesread;
            }
        }
        catch (IOException ex2) {
            throw this.newBitstreamException(258, ex2);
        }
        return nRead;
    }

    private int readBytes(byte[] b2, int offs, int len) throws BitstreamException {
        int totalBytesRead = 0;
        try {
            while (len > 0) {
                int bytesread = this.source.read(b2, offs, len);
                if (bytesread != -1) {
                    totalBytesRead += bytesread;
                    offs += bytesread;
                    len -= bytesread;
                    continue;
                }
                break;
            }
        }
        catch (IOException ex2) {
            throw this.newBitstreamException(258, ex2);
        }
        return totalBytesRead;
    }
}

