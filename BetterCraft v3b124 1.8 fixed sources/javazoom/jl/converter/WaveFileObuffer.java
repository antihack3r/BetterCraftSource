/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.converter;

import javazoom.jl.converter.WaveFile;
import javazoom.jl.decoder.Obuffer;

public class WaveFileObuffer
extends Obuffer {
    private short[] buffer;
    private short[] bufferp;
    private int channels;
    private WaveFile outWave;
    short[] myBuffer = new short[2];

    public WaveFileObuffer(int number_of_channels, int freq, String FileName) {
        if (FileName == null) {
            throw new NullPointerException("FileName");
        }
        this.buffer = new short[2304];
        this.bufferp = new short[2];
        this.channels = number_of_channels;
        int i2 = 0;
        while (i2 < number_of_channels) {
            this.bufferp[i2] = (short)i2;
            ++i2;
        }
        this.outWave = new WaveFile();
        int rc2 = this.outWave.OpenForWrite(FileName, freq, (short)16, (short)this.channels);
    }

    @Override
    public void append(int channel, short value) {
        this.buffer[this.bufferp[channel]] = value;
        int n2 = channel;
        this.bufferp[n2] = (short)(this.bufferp[n2] + this.channels);
    }

    @Override
    public void write_buffer(int val) {
        boolean k2 = false;
        int rc2 = 0;
        rc2 = this.outWave.WriteData(this.buffer, this.bufferp[0]);
        int i2 = 0;
        while (i2 < this.channels) {
            this.bufferp[i2] = (short)i2;
            ++i2;
        }
    }

    @Override
    public void close() {
        this.outWave.Close();
    }

    @Override
    public void clear_buffer() {
    }

    @Override
    public void set_stop_flag() {
    }
}

