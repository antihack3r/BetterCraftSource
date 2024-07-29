/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

public final class Equalizer {
    public static final float BAND_NOT_PRESENT = Float.NEGATIVE_INFINITY;
    public static final Equalizer PASS_THRU_EQ = new Equalizer();
    private static final int BANDS = 32;
    private final float[] settings = new float[32];

    public Equalizer() {
    }

    public Equalizer(float[] settings) {
        this.setFrom(settings);
    }

    public Equalizer(EQFunction eq2) {
        this.setFrom(eq2);
    }

    public void setFrom(float[] eq2) {
        this.reset();
        int max = eq2.length > 32 ? 32 : eq2.length;
        int i2 = 0;
        while (i2 < max) {
            this.settings[i2] = this.limit(eq2[i2]);
            ++i2;
        }
    }

    public void setFrom(EQFunction eq2) {
        this.reset();
        int max = 32;
        int i2 = 0;
        while (i2 < max) {
            this.settings[i2] = this.limit(eq2.getBand(i2));
            ++i2;
        }
    }

    public void setFrom(Equalizer eq2) {
        if (eq2 != this) {
            this.setFrom(eq2.settings);
        }
    }

    public void reset() {
        int i2 = 0;
        while (i2 < 32) {
            this.settings[i2] = 0.0f;
            ++i2;
        }
    }

    public int getBandCount() {
        return this.settings.length;
    }

    public float setBand(int band, float neweq) {
        float eq2 = 0.0f;
        if (band >= 0 && band < 32) {
            eq2 = this.settings[band];
            this.settings[band] = this.limit(neweq);
        }
        return eq2;
    }

    public float getBand(int band) {
        float eq2 = 0.0f;
        if (band >= 0 && band < 32) {
            eq2 = this.settings[band];
        }
        return eq2;
    }

    private float limit(float eq2) {
        if (eq2 == Float.NEGATIVE_INFINITY) {
            return eq2;
        }
        if (eq2 > 1.0f) {
            return 1.0f;
        }
        if (eq2 < -1.0f) {
            return -1.0f;
        }
        return eq2;
    }

    float[] getBandFactors() {
        float[] factors = new float[32];
        int i2 = 0;
        int maxCount = 32;
        while (i2 < maxCount) {
            factors[i2] = this.getBandFactor(this.settings[i2]);
            ++i2;
        }
        return factors;
    }

    float getBandFactor(float eq2) {
        if (eq2 == Float.NEGATIVE_INFINITY) {
            return 0.0f;
        }
        float f2 = (float)Math.pow(2.0, eq2);
        return f2;
    }

    public static abstract class EQFunction {
        public float getBand(int band) {
            return 0.0f;
        }
    }
}

