/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

public abstract class Obuffer {
    public static final int OBUFFERSIZE = 2304;
    public static final int MAXCHANNELS = 2;

    public abstract void append(int var1, short var2);

    public void appendSamples(int channel, float[] f2) {
        int i2 = 0;
        while (i2 < 32) {
            short s2 = this.clip(f2[i2++]);
            this.append(channel, s2);
        }
    }

    private final short clip(float sample) {
        return (short)(sample > 32767.0f ? Short.MAX_VALUE : (short)(sample < -32768.0f ? Short.MIN_VALUE : (short)sample));
    }

    public abstract void write_buffer(int var1);

    public abstract void close();

    public abstract void clear_buffer();

    public abstract void set_stop_flag();
}

