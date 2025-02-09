/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import javazoom.jl.decoder.DecoderErrors;
import javazoom.jl.decoder.JavaLayerException;

public class DecoderException
extends JavaLayerException
implements DecoderErrors {
    private int errorcode = 512;

    public DecoderException(String msg, Throwable t2) {
        super(msg, t2);
    }

    public DecoderException(int errorcode, Throwable t2) {
        this(DecoderException.getErrorString(errorcode), t2);
        this.errorcode = errorcode;
    }

    public int getErrorCode() {
        return this.errorcode;
    }

    public static String getErrorString(int errorcode) {
        return "Decoder errorcode " + Integer.toHexString(errorcode);
    }
}

