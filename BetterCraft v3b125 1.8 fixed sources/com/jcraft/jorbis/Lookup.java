/*
 * Decompiled with CFR 0.152.
 */
package com.jcraft.jorbis;

class Lookup {
    static final int COS_LOOKUP_SZ = 128;
    static final float[] COS_LOOKUP = new float[]{1.0f, 0.9996988f, 0.99879545f, 0.99729043f, 0.9951847f, 0.99247956f, 0.9891765f, 0.98527765f, 0.98078525f, 0.9757021f, 0.97003126f, 0.96377605f, 0.95694035f, 0.94952816f, 0.94154406f, 0.9329928f, 0.9238795f, 0.9142098f, 0.9039893f, 0.8932243f, 0.8819213f, 0.87008697f, 0.8577286f, 0.8448536f, 0.8314696f, 0.8175848f, 0.8032075f, 0.7883464f, 0.77301043f, 0.7572088f, 0.7409511f, 0.7242471f, 0.70710677f, 0.68954057f, 0.671559f, 0.65317285f, 0.6343933f, 0.6152316f, 0.5956993f, 0.57580817f, 0.55557024f, 0.53499764f, 0.51410276f, 0.4928982f, 0.47139674f, 0.44961134f, 0.42755508f, 0.4052413f, 0.38268343f, 0.35989505f, 0.33688986f, 0.31368175f, 0.29028466f, 0.26671275f, 0.24298018f, 0.21910124f, 0.19509032f, 0.17096189f, 0.14673047f, 0.12241068f, 0.09801714f, 0.07356457f, 0.049067676f, 0.024541229f, 0.0f, -0.024541229f, -0.049067676f, -0.07356457f, -0.09801714f, -0.12241068f, -0.14673047f, -0.17096189f, -0.19509032f, -0.21910124f, -0.24298018f, -0.26671275f, -0.29028466f, -0.31368175f, -0.33688986f, -0.35989505f, -0.38268343f, -0.4052413f, -0.42755508f, -0.44961134f, -0.47139674f, -0.4928982f, -0.51410276f, -0.53499764f, -0.55557024f, -0.57580817f, -0.5956993f, -0.6152316f, -0.6343933f, -0.65317285f, -0.671559f, -0.68954057f, -0.70710677f, -0.7242471f, -0.7409511f, -0.7572088f, -0.77301043f, -0.7883464f, -0.8032075f, -0.8175848f, -0.8314696f, -0.8448536f, -0.8577286f, -0.87008697f, -0.8819213f, -0.8932243f, -0.9039893f, -0.9142098f, -0.9238795f, -0.9329928f, -0.94154406f, -0.94952816f, -0.95694035f, -0.96377605f, -0.97003126f, -0.9757021f, -0.98078525f, -0.98527765f, -0.9891765f, -0.99247956f, -0.9951847f, -0.99729043f, -0.99879545f, -0.9996988f, -1.0f};
    static final int INVSQ_LOOKUP_SZ = 32;
    static final float[] INVSQ_LOOKUP = new float[]{1.4142135f, 1.3926213f, 1.3719887f, 1.3522468f, 1.3333334f, 1.3151919f, 1.2977713f, 1.2810252f, 1.264911f, 1.2493901f, 1.2344269f, 1.2199886f, 1.2060454f, 1.1925696f, 1.1795356f, 1.16692f, 1.1547005f, 1.1428572f, 1.1313709f, 1.1202241f, 1.1094004f, 1.0988845f, 1.0886621f, 1.0787197f, 1.069045f, 1.0596259f, 1.0504515f, 1.0415113f, 1.0327955f, 1.0242951f, 1.016001f, 1.0079052f, 1.0f};
    static final int INVSQ2EXP_LOOKUP_MIN = -32;
    static final int INVSQ2EXP_LOOKUP_MAX = 32;
    static final float[] INVSQ2EXP_LOOKUP = new float[]{65536.0f, 46340.95f, 32768.0f, 23170.475f, 16384.0f, 11585.237f, 8192.0f, 5792.6187f, 4096.0f, 2896.3093f, 2048.0f, 1448.1547f, 1024.0f, 724.07733f, 512.0f, 362.03867f, 256.0f, 181.01933f, 128.0f, 90.50967f, 64.0f, 45.254833f, 32.0f, 22.627417f, 16.0f, 11.313708f, 8.0f, 5.656854f, 4.0f, 2.828427f, 2.0f, 1.4142135f, 1.0f, 0.70710677f, 0.5f, 0.35355338f, 0.25f, 0.17677669f, 0.125f, 0.088388346f, 0.0625f, 0.044194173f, 0.03125f, 0.022097087f, 0.015625f, 0.011048543f, 0.0078125f, 0.0055242716f, 0.00390625f, 0.0027621358f, 0.001953125f, 0.0013810679f, 9.765625E-4f, 6.9053395E-4f, 4.8828125E-4f, 3.4526698E-4f, 2.4414062E-4f, 1.7263349E-4f, 1.2207031E-4f, 8.6316744E-5f, 6.1035156E-5f, 4.3158372E-5f, 3.0517578E-5f, 2.1579186E-5f, 1.5258789E-5f};
    static final int FROMdB_LOOKUP_SZ = 35;
    static final int FROMdB2_LOOKUP_SZ = 32;
    static final int FROMdB_SHIFT = 5;
    static final int FROMdB2_SHIFT = 3;
    static final int FROMdB2_MASK = 31;
    static final float[] FROMdB_LOOKUP = new float[]{1.0f, 0.63095737f, 0.39810717f, 0.25118864f, 0.15848932f, 0.1f, 0.06309573f, 0.039810717f, 0.025118865f, 0.015848933f, 0.01f, 0.0063095735f, 0.0039810715f, 0.0025118864f, 0.0015848932f, 0.001f, 6.3095737E-4f, 3.9810716E-4f, 2.5118864E-4f, 1.5848932E-4f, 1.0E-4f, 6.309574E-5f, 3.981072E-5f, 2.5118865E-5f, 1.5848931E-5f, 1.0E-5f, 6.3095736E-6f, 3.9810716E-6f, 2.5118864E-6f, 1.5848932E-6f, 1.0E-6f, 6.3095735E-7f, 3.9810718E-7f, 2.5118865E-7f, 1.5848931E-7f};
    static final float[] FROMdB2_LOOKUP = new float[]{0.9928303f, 0.9786446f, 0.9646616f, 0.95087844f, 0.9372922f, 0.92390007f, 0.9106993f, 0.89768714f, 0.8848609f, 0.8722179f, 0.8597556f, 0.8474713f, 0.83536255f, 0.8234268f, 0.8116616f, 0.8000645f, 0.7886331f, 0.777365f, 0.76625794f, 0.7553096f, 0.7445176f, 0.7338799f, 0.72339416f, 0.71305823f, 0.70287f, 0.6928273f, 0.68292814f, 0.6731704f, 0.66355205f, 0.65407115f, 0.64472574f, 0.63551384f};

    Lookup() {
    }

    static float coslook(float a2) {
        double d2 = (double)a2 * 40.74366592;
        int i2 = (int)d2;
        return COS_LOOKUP[i2] + (float)(d2 - (double)i2) * (COS_LOOKUP[i2 + 1] - COS_LOOKUP[i2]);
    }

    static float invsqlook(float a2) {
        double d2 = a2 * 64.0f - 32.0f;
        int i2 = (int)d2;
        return INVSQ_LOOKUP[i2] + (float)(d2 - (double)i2) * (INVSQ_LOOKUP[i2 + 1] - INVSQ_LOOKUP[i2]);
    }

    static float invsq2explook(int a2) {
        return INVSQ2EXP_LOOKUP[a2 - -32];
    }

    static float fromdBlook(float a2) {
        int i2 = (int)(a2 * -8.0f);
        return i2 < 0 ? 1.0f : (i2 >= 1120 ? 0.0f : FROMdB_LOOKUP[i2 >>> 5] * FROMdB2_LOOKUP[i2 & 0x1F]);
    }
}

