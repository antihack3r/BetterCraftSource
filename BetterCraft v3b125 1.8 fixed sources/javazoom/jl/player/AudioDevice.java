/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.player;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;

public interface AudioDevice {
    public void open(Decoder var1) throws JavaLayerException;

    public boolean isOpen();

    public void write(short[] var1, int var2, int var3) throws JavaLayerException;

    public void close();

    public void flush();

    public int getPosition();
}

