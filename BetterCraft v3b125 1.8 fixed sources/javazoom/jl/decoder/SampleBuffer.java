/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import javazoom.jl.decoder.Obuffer;

public class SampleBuffer
extends Obuffer {
    private short[] buffer = new short[2304];
    private int[] bufferp = new int[2];
    private int channels;
    private int frequency;

    public SampleBuffer(int sample_frequency, int number_of_channels) {
        this.channels = number_of_channels;
        this.frequency = sample_frequency;
        int i2 = 0;
        while (i2 < number_of_channels) {
            this.bufferp[i2] = (short)i2;
            ++i2;
        }
    }

    public int getChannelCount() {
        return this.channels;
    }

    public int getSampleFrequency() {
        return this.frequency;
    }

    public short[] getBuffer() {
        return this.buffer;
    }

    public int getBufferLength() {
        return this.bufferp[0];
    }

    @Override
    public void append(int channel, short value) {
        this.buffer[this.bufferp[channel]] = value;
        int n2 = channel;
        this.bufferp[n2] = this.bufferp[n2] + this.channels;
    }

    @Override
    public void appendSamples(int channel, float[] f2) {
        int pos = this.bufferp[channel];
        int i2 = 0;
        while (i2 < 32) {
            short s2;
            float fs2;
            fs2 = (fs2 = f2[i2++]) > 32767.0f ? 32767.0f : (fs2 < -32767.0f ? -32767.0f : fs2);
            this.buffer[pos] = s2 = (short)fs2;
            pos += this.channels;
        }
        this.bufferp[channel] = pos;
    }

    @Override
    public void write_buffer(int val) {
    }

    @Override
    public void close() {
    }

    @Override
    public void clear_buffer() {
        int i2 = 0;
        while (i2 < this.channels) {
            this.bufferp[i2] = (short)i2;
            ++i2;
        }
    }

    @Override
    public void set_stop_flag() {
    }
}

