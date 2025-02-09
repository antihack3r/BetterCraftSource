/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import javazoom.jl.decoder.BitstreamErrors;
import javazoom.jl.decoder.JavaLayerException;

public class BitstreamException
extends JavaLayerException
implements BitstreamErrors {
    private int errorcode = 256;

    public BitstreamException(String msg, Throwable t2) {
        super(msg, t2);
    }

    public BitstreamException(int errorcode, Throwable t2) {
        this(BitstreamException.getErrorString(errorcode), t2);
        this.errorcode = errorcode;
    }

    public int getErrorCode() {
        return this.errorcode;
    }

    public static String getErrorString(int errorcode) {
        return "Bitstream errorcode " + Integer.toHexString(errorcode);
    }
}

