/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import java.io.IOException;
import javazoom.jl.decoder.JavaLayerUtils;
import javazoom.jl.decoder.Obuffer;

final class SynthesisFilter {
    private float[] v1;
    private float[] v2;
    private float[] actual_v;
    private int actual_write_pos;
    private float[] samples;
    private int channel;
    private float scalefactor;
    private float[] eq;
    private float[] _tmpOut = new float[32];
    private static final double MY_PI = Math.PI;
    private static final float cos1_64 = (float)(1.0 / (2.0 * Math.cos(0.04908738521234052)));
    private static final float cos3_64 = (float)(1.0 / (2.0 * Math.cos(0.14726215563702155)));
    private static final float cos5_64 = (float)(1.0 / (2.0 * Math.cos(0.2454369260617026)));
    private static final float cos7_64 = (float)(1.0 / (2.0 * Math.cos(0.3436116964863836)));
    private static final float cos9_64 = (float)(1.0 / (2.0 * Math.cos(0.44178646691106466)));
    private static final float cos11_64 = (float)(1.0 / (2.0 * Math.cos(0.5399612373357456)));
    private static final float cos13_64 = (float)(1.0 / (2.0 * Math.cos(0.6381360077604268)));
    private static final float cos15_64 = (float)(1.0 / (2.0 * Math.cos(0.7363107781851077)));
    private static final float cos17_64 = (float)(1.0 / (2.0 * Math.cos(0.8344855486097889)));
    private static final float cos19_64 = (float)(1.0 / (2.0 * Math.cos(0.9326603190344698)));
    private static final float cos21_64 = (float)(1.0 / (2.0 * Math.cos(1.030835089459151)));
    private static final float cos23_64 = (float)(1.0 / (2.0 * Math.cos(1.1290098598838318)));
    private static final float cos25_64 = (float)(1.0 / (2.0 * Math.cos(1.227184630308513)));
    private static final float cos27_64 = (float)(1.0 / (2.0 * Math.cos(1.325359400733194)));
    private static final float cos29_64 = (float)(1.0 / (2.0 * Math.cos(1.423534171157875)));
    private static final float cos31_64 = (float)(1.0 / (2.0 * Math.cos(1.521708941582556)));
    private static final float cos1_32 = (float)(1.0 / (2.0 * Math.cos(0.09817477042468103)));
    private static final float cos3_32 = (float)(1.0 / (2.0 * Math.cos(0.2945243112740431)));
    private static final float cos5_32 = (float)(1.0 / (2.0 * Math.cos(0.4908738521234052)));
    private static final float cos7_32 = (float)(1.0 / (2.0 * Math.cos(0.6872233929727672)));
    private static final float cos9_32 = (float)(1.0 / (2.0 * Math.cos(0.8835729338221293)));
    private static final float cos11_32 = (float)(1.0 / (2.0 * Math.cos(1.0799224746714913)));
    private static final float cos13_32 = (float)(1.0 / (2.0 * Math.cos(1.2762720155208536)));
    private static final float cos15_32 = (float)(1.0 / (2.0 * Math.cos(1.4726215563702154)));
    private static final float cos1_16 = (float)(1.0 / (2.0 * Math.cos(0.19634954084936207)));
    private static final float cos3_16 = (float)(1.0 / (2.0 * Math.cos(0.5890486225480862)));
    private static final float cos5_16 = (float)(1.0 / (2.0 * Math.cos(0.9817477042468103)));
    private static final float cos7_16 = (float)(1.0 / (2.0 * Math.cos(1.3744467859455345)));
    private static final float cos1_8 = (float)(1.0 / (2.0 * Math.cos(0.39269908169872414)));
    private static final float cos3_8 = (float)(1.0 / (2.0 * Math.cos(1.1780972450961724)));
    private static final float cos1_4 = (float)(1.0 / (2.0 * Math.cos(0.7853981633974483)));
    private static float[] d = null;
    private static float[][] d16 = null;

    public SynthesisFilter(int channelnumber, float factor, float[] eq0) {
        if (d == null) {
            d = SynthesisFilter.load_d();
            d16 = SynthesisFilter.splitArray(d, 16);
        }
        this.v1 = new float[512];
        this.v2 = new float[512];
        this.samples = new float[32];
        this.channel = channelnumber;
        this.scalefactor = factor;
        this.setEQ(this.eq);
        this.reset();
    }

    public void setEQ(float[] eq0) {
        this.eq = eq0;
        if (this.eq == null) {
            this.eq = new float[32];
            int i2 = 0;
            while (i2 < 32) {
                this.eq[i2] = 1.0f;
                ++i2;
            }
        }
        if (this.eq.length < 32) {
            throw new IllegalArgumentException("eq0");
        }
    }

    public void reset() {
        int p2 = 0;
        while (p2 < 512) {
            this.v2[p2] = 0.0f;
            this.v1[p2] = 0.0f;
            ++p2;
        }
        int p22 = 0;
        while (p22 < 32) {
            this.samples[p22] = 0.0f;
            ++p22;
        }
        this.actual_v = this.v1;
        this.actual_write_pos = 15;
    }

    public void input_sample(float sample, int subbandnumber) {
        this.samples[subbandnumber] = this.eq[subbandnumber] * sample;
    }

    public void input_samples(float[] s2) {
        int i2 = 31;
        while (i2 >= 0) {
            this.samples[i2] = s2[i2] * this.eq[i2];
            --i2;
        }
    }

    private void compute_new_v() {
        float new_v31 = 0.0f;
        float new_v30 = 0.0f;
        float new_v29 = 0.0f;
        float new_v28 = 0.0f;
        float new_v27 = 0.0f;
        float new_v26 = 0.0f;
        float new_v25 = 0.0f;
        float new_v24 = 0.0f;
        float new_v23 = 0.0f;
        float new_v22 = 0.0f;
        float new_v21 = 0.0f;
        float new_v20 = 0.0f;
        float new_v19 = 0.0f;
        float new_v18 = 0.0f;
        float new_v17 = 0.0f;
        float new_v16 = 0.0f;
        float new_v15 = 0.0f;
        float new_v14 = 0.0f;
        float new_v13 = 0.0f;
        float new_v12 = 0.0f;
        float new_v11 = 0.0f;
        float new_v10 = 0.0f;
        float new_v9 = 0.0f;
        float new_v8 = 0.0f;
        float new_v7 = 0.0f;
        float new_v6 = 0.0f;
        float new_v5 = 0.0f;
        float new_v4 = 0.0f;
        float new_v3 = 0.0f;
        float new_v2 = 0.0f;
        float new_v1 = 0.0f;
        float new_v0 = 0.0f;
        float[] s2 = this.samples;
        float s0 = s2[0];
        float s1 = s2[1];
        float s22 = s2[2];
        float s3 = s2[3];
        float s4 = s2[4];
        float s5 = s2[5];
        float s6 = s2[6];
        float s7 = s2[7];
        float s8 = s2[8];
        float s9 = s2[9];
        float s10 = s2[10];
        float s11 = s2[11];
        float s12 = s2[12];
        float s13 = s2[13];
        float s14 = s2[14];
        float s15 = s2[15];
        float s16 = s2[16];
        float s17 = s2[17];
        float s18 = s2[18];
        float s19 = s2[19];
        float s20 = s2[20];
        float s21 = s2[21];
        float s222 = s2[22];
        float s23 = s2[23];
        float s24 = s2[24];
        float s25 = s2[25];
        float s26 = s2[26];
        float s27 = s2[27];
        float s28 = s2[28];
        float s29 = s2[29];
        float s30 = s2[30];
        float s31 = s2[31];
        float p0 = s0 + s31;
        float p1 = s1 + s30;
        float p2 = s22 + s29;
        float p3 = s3 + s28;
        float p4 = s4 + s27;
        float p5 = s5 + s26;
        float p6 = s6 + s25;
        float p7 = s7 + s24;
        float p8 = s8 + s23;
        float p9 = s9 + s222;
        float p10 = s10 + s21;
        float p11 = s11 + s20;
        float p12 = s12 + s19;
        float p13 = s13 + s18;
        float p14 = s14 + s17;
        float p15 = s15 + s16;
        float pp0 = p0 + p15;
        float pp1 = p1 + p14;
        float pp2 = p2 + p13;
        float pp3 = p3 + p12;
        float pp4 = p4 + p11;
        float pp5 = p5 + p10;
        float pp6 = p6 + p9;
        float pp7 = p7 + p8;
        float pp8 = (p0 - p15) * cos1_32;
        float pp9 = (p1 - p14) * cos3_32;
        float pp10 = (p2 - p13) * cos5_32;
        float pp11 = (p3 - p12) * cos7_32;
        float pp12 = (p4 - p11) * cos9_32;
        float pp13 = (p5 - p10) * cos11_32;
        float pp14 = (p6 - p9) * cos13_32;
        float pp15 = (p7 - p8) * cos15_32;
        p0 = pp0 + pp7;
        p1 = pp1 + pp6;
        p2 = pp2 + pp5;
        p3 = pp3 + pp4;
        p4 = (pp0 - pp7) * cos1_16;
        p5 = (pp1 - pp6) * cos3_16;
        p6 = (pp2 - pp5) * cos5_16;
        p7 = (pp3 - pp4) * cos7_16;
        p8 = pp8 + pp15;
        p9 = pp9 + pp14;
        p10 = pp10 + pp13;
        p11 = pp11 + pp12;
        p12 = (pp8 - pp15) * cos1_16;
        p13 = (pp9 - pp14) * cos3_16;
        p14 = (pp10 - pp13) * cos5_16;
        p15 = (pp11 - pp12) * cos7_16;
        pp0 = p0 + p3;
        pp1 = p1 + p2;
        pp2 = (p0 - p3) * cos1_8;
        pp3 = (p1 - p2) * cos3_8;
        pp4 = p4 + p7;
        pp5 = p5 + p6;
        pp6 = (p4 - p7) * cos1_8;
        pp7 = (p5 - p6) * cos3_8;
        pp8 = p8 + p11;
        pp9 = p9 + p10;
        pp10 = (p8 - p11) * cos1_8;
        pp11 = (p9 - p10) * cos3_8;
        pp12 = p12 + p15;
        pp13 = p13 + p14;
        pp14 = (p12 - p15) * cos1_8;
        pp15 = (p13 - p14) * cos3_8;
        p0 = pp0 + pp1;
        p1 = (pp0 - pp1) * cos1_4;
        p2 = pp2 + pp3;
        p3 = (pp2 - pp3) * cos1_4;
        p4 = pp4 + pp5;
        p5 = (pp4 - pp5) * cos1_4;
        p6 = pp6 + pp7;
        p7 = (pp6 - pp7) * cos1_4;
        p8 = pp8 + pp9;
        p9 = (pp8 - pp9) * cos1_4;
        p10 = pp10 + pp11;
        p11 = (pp10 - pp11) * cos1_4;
        p12 = pp12 + pp13;
        p13 = (pp12 - pp13) * cos1_4;
        p14 = pp14 + pp15;
        p15 = (pp14 - pp15) * cos1_4;
        new_v12 = p7;
        new_v4 = new_v12 + p5;
        new_v19 = -new_v4 - p6;
        new_v27 = -p6 - p7 - p4;
        new_v14 = p15;
        new_v10 = new_v14 + p11;
        new_v6 = new_v10 + p13;
        new_v2 = p15 + p13 + p9;
        new_v17 = -new_v2 - p14;
        float tmp1 = -p14 - p15 - p10 - p11;
        new_v21 = tmp1 - p13;
        new_v29 = -p14 - p15 - p12 - p8;
        new_v25 = tmp1 - p12;
        new_v31 = -p0;
        new_v0 = p1;
        new_v8 = p3;
        new_v23 = -new_v8 - p2;
        p0 = (s0 - s31) * cos1_64;
        p1 = (s1 - s30) * cos3_64;
        p2 = (s22 - s29) * cos5_64;
        p3 = (s3 - s28) * cos7_64;
        p4 = (s4 - s27) * cos9_64;
        p5 = (s5 - s26) * cos11_64;
        p6 = (s6 - s25) * cos13_64;
        p7 = (s7 - s24) * cos15_64;
        p8 = (s8 - s23) * cos17_64;
        p9 = (s9 - s222) * cos19_64;
        p10 = (s10 - s21) * cos21_64;
        p11 = (s11 - s20) * cos23_64;
        p12 = (s12 - s19) * cos25_64;
        p13 = (s13 - s18) * cos27_64;
        p14 = (s14 - s17) * cos29_64;
        p15 = (s15 - s16) * cos31_64;
        pp0 = p0 + p15;
        pp1 = p1 + p14;
        pp2 = p2 + p13;
        pp3 = p3 + p12;
        pp4 = p4 + p11;
        pp5 = p5 + p10;
        pp6 = p6 + p9;
        pp7 = p7 + p8;
        pp8 = (p0 - p15) * cos1_32;
        pp9 = (p1 - p14) * cos3_32;
        pp10 = (p2 - p13) * cos5_32;
        pp11 = (p3 - p12) * cos7_32;
        pp12 = (p4 - p11) * cos9_32;
        pp13 = (p5 - p10) * cos11_32;
        pp14 = (p6 - p9) * cos13_32;
        pp15 = (p7 - p8) * cos15_32;
        p0 = pp0 + pp7;
        p1 = pp1 + pp6;
        p2 = pp2 + pp5;
        p3 = pp3 + pp4;
        p4 = (pp0 - pp7) * cos1_16;
        p5 = (pp1 - pp6) * cos3_16;
        p6 = (pp2 - pp5) * cos5_16;
        p7 = (pp3 - pp4) * cos7_16;
        p8 = pp8 + pp15;
        p9 = pp9 + pp14;
        p10 = pp10 + pp13;
        p11 = pp11 + pp12;
        p12 = (pp8 - pp15) * cos1_16;
        p13 = (pp9 - pp14) * cos3_16;
        p14 = (pp10 - pp13) * cos5_16;
        p15 = (pp11 - pp12) * cos7_16;
        pp0 = p0 + p3;
        pp1 = p1 + p2;
        pp2 = (p0 - p3) * cos1_8;
        pp3 = (p1 - p2) * cos3_8;
        pp4 = p4 + p7;
        pp5 = p5 + p6;
        pp6 = (p4 - p7) * cos1_8;
        pp7 = (p5 - p6) * cos3_8;
        pp8 = p8 + p11;
        pp9 = p9 + p10;
        pp10 = (p8 - p11) * cos1_8;
        pp11 = (p9 - p10) * cos3_8;
        pp12 = p12 + p15;
        pp13 = p13 + p14;
        pp14 = (p12 - p15) * cos1_8;
        pp15 = (p13 - p14) * cos3_8;
        p0 = pp0 + pp1;
        p1 = (pp0 - pp1) * cos1_4;
        p2 = pp2 + pp3;
        p3 = (pp2 - pp3) * cos1_4;
        p4 = pp4 + pp5;
        p5 = (pp4 - pp5) * cos1_4;
        p6 = pp6 + pp7;
        p7 = (pp6 - pp7) * cos1_4;
        p8 = pp8 + pp9;
        p9 = (pp8 - pp9) * cos1_4;
        p10 = pp10 + pp11;
        p11 = (pp10 - pp11) * cos1_4;
        p12 = pp12 + pp13;
        p13 = (pp12 - pp13) * cos1_4;
        p14 = pp14 + pp15;
        new_v15 = p15 = (pp14 - pp15) * cos1_4;
        new_v13 = new_v15 + p7;
        new_v11 = new_v13 + p11;
        new_v5 = new_v11 + p5 + p13;
        new_v9 = p15 + p11 + p3;
        new_v7 = new_v9 + p13;
        tmp1 = p13 + p15 + p9;
        new_v1 = tmp1 + p1;
        new_v16 = -new_v1 - p14;
        new_v3 = tmp1 + p5 + p7;
        new_v18 = -new_v3 - p6 - p14;
        tmp1 = -p10 - p11 - p14 - p15;
        new_v22 = tmp1 - p13 - p2 - p3;
        new_v20 = tmp1 - p13 - p5 - p6 - p7;
        new_v24 = tmp1 - p12 - p2 - p3;
        float tmp2 = p4 + p6 + p7;
        new_v26 = tmp1 - p12 - tmp2;
        tmp1 = -p8 - p12 - p14 - p15;
        new_v30 = tmp1 - p0;
        new_v28 = tmp1 - tmp2;
        float[] dest = this.actual_v;
        int pos = this.actual_write_pos;
        dest[0 + pos] = new_v0;
        dest[16 + pos] = new_v1;
        dest[32 + pos] = new_v2;
        dest[48 + pos] = new_v3;
        dest[64 + pos] = new_v4;
        dest[80 + pos] = new_v5;
        dest[96 + pos] = new_v6;
        dest[112 + pos] = new_v7;
        dest[128 + pos] = new_v8;
        dest[144 + pos] = new_v9;
        dest[160 + pos] = new_v10;
        dest[176 + pos] = new_v11;
        dest[192 + pos] = new_v12;
        dest[208 + pos] = new_v13;
        dest[224 + pos] = new_v14;
        dest[240 + pos] = new_v15;
        dest[256 + pos] = 0.0f;
        dest[272 + pos] = -new_v15;
        dest[288 + pos] = -new_v14;
        dest[304 + pos] = -new_v13;
        dest[320 + pos] = -new_v12;
        dest[336 + pos] = -new_v11;
        dest[352 + pos] = -new_v10;
        dest[368 + pos] = -new_v9;
        dest[384 + pos] = -new_v8;
        dest[400 + pos] = -new_v7;
        dest[416 + pos] = -new_v6;
        dest[432 + pos] = -new_v5;
        dest[448 + pos] = -new_v4;
        dest[464 + pos] = -new_v3;
        dest[480 + pos] = -new_v2;
        dest[496 + pos] = -new_v1;
        dest = this.actual_v == this.v1 ? this.v2 : this.v1;
        dest[0 + pos] = -new_v0;
        dest[16 + pos] = new_v16;
        dest[32 + pos] = new_v17;
        dest[48 + pos] = new_v18;
        dest[64 + pos] = new_v19;
        dest[80 + pos] = new_v20;
        dest[96 + pos] = new_v21;
        dest[112 + pos] = new_v22;
        dest[128 + pos] = new_v23;
        dest[144 + pos] = new_v24;
        dest[160 + pos] = new_v25;
        dest[176 + pos] = new_v26;
        dest[192 + pos] = new_v27;
        dest[208 + pos] = new_v28;
        dest[224 + pos] = new_v29;
        dest[240 + pos] = new_v30;
        dest[256 + pos] = new_v31;
        dest[272 + pos] = new_v30;
        dest[288 + pos] = new_v29;
        dest[304 + pos] = new_v28;
        dest[320 + pos] = new_v27;
        dest[336 + pos] = new_v26;
        dest[352 + pos] = new_v25;
        dest[368 + pos] = new_v24;
        dest[384 + pos] = new_v23;
        dest[400 + pos] = new_v22;
        dest[416 + pos] = new_v21;
        dest[432 + pos] = new_v20;
        dest[448 + pos] = new_v19;
        dest[464 + pos] = new_v18;
        dest[480 + pos] = new_v17;
        dest[496 + pos] = new_v16;
    }

    private void compute_new_v_old() {
        float[] new_v = new float[32];
        float[] p2 = new float[16];
        float[] pp2 = new float[16];
        int i2 = 31;
        while (i2 >= 0) {
            new_v[i2] = 0.0f;
            --i2;
        }
        float[] x1 = this.samples;
        p2[0] = x1[0] + x1[31];
        p2[1] = x1[1] + x1[30];
        p2[2] = x1[2] + x1[29];
        p2[3] = x1[3] + x1[28];
        p2[4] = x1[4] + x1[27];
        p2[5] = x1[5] + x1[26];
        p2[6] = x1[6] + x1[25];
        p2[7] = x1[7] + x1[24];
        p2[8] = x1[8] + x1[23];
        p2[9] = x1[9] + x1[22];
        p2[10] = x1[10] + x1[21];
        p2[11] = x1[11] + x1[20];
        p2[12] = x1[12] + x1[19];
        p2[13] = x1[13] + x1[18];
        p2[14] = x1[14] + x1[17];
        p2[15] = x1[15] + x1[16];
        pp2[0] = p2[0] + p2[15];
        pp2[1] = p2[1] + p2[14];
        pp2[2] = p2[2] + p2[13];
        pp2[3] = p2[3] + p2[12];
        pp2[4] = p2[4] + p2[11];
        pp2[5] = p2[5] + p2[10];
        pp2[6] = p2[6] + p2[9];
        pp2[7] = p2[7] + p2[8];
        pp2[8] = (p2[0] - p2[15]) * cos1_32;
        pp2[9] = (p2[1] - p2[14]) * cos3_32;
        pp2[10] = (p2[2] - p2[13]) * cos5_32;
        pp2[11] = (p2[3] - p2[12]) * cos7_32;
        pp2[12] = (p2[4] - p2[11]) * cos9_32;
        pp2[13] = (p2[5] - p2[10]) * cos11_32;
        pp2[14] = (p2[6] - p2[9]) * cos13_32;
        pp2[15] = (p2[7] - p2[8]) * cos15_32;
        p2[0] = pp2[0] + pp2[7];
        p2[1] = pp2[1] + pp2[6];
        p2[2] = pp2[2] + pp2[5];
        p2[3] = pp2[3] + pp2[4];
        p2[4] = (pp2[0] - pp2[7]) * cos1_16;
        p2[5] = (pp2[1] - pp2[6]) * cos3_16;
        p2[6] = (pp2[2] - pp2[5]) * cos5_16;
        p2[7] = (pp2[3] - pp2[4]) * cos7_16;
        p2[8] = pp2[8] + pp2[15];
        p2[9] = pp2[9] + pp2[14];
        p2[10] = pp2[10] + pp2[13];
        p2[11] = pp2[11] + pp2[12];
        p2[12] = (pp2[8] - pp2[15]) * cos1_16;
        p2[13] = (pp2[9] - pp2[14]) * cos3_16;
        p2[14] = (pp2[10] - pp2[13]) * cos5_16;
        p2[15] = (pp2[11] - pp2[12]) * cos7_16;
        pp2[0] = p2[0] + p2[3];
        pp2[1] = p2[1] + p2[2];
        pp2[2] = (p2[0] - p2[3]) * cos1_8;
        pp2[3] = (p2[1] - p2[2]) * cos3_8;
        pp2[4] = p2[4] + p2[7];
        pp2[5] = p2[5] + p2[6];
        pp2[6] = (p2[4] - p2[7]) * cos1_8;
        pp2[7] = (p2[5] - p2[6]) * cos3_8;
        pp2[8] = p2[8] + p2[11];
        pp2[9] = p2[9] + p2[10];
        pp2[10] = (p2[8] - p2[11]) * cos1_8;
        pp2[11] = (p2[9] - p2[10]) * cos3_8;
        pp2[12] = p2[12] + p2[15];
        pp2[13] = p2[13] + p2[14];
        pp2[14] = (p2[12] - p2[15]) * cos1_8;
        pp2[15] = (p2[13] - p2[14]) * cos3_8;
        p2[0] = pp2[0] + pp2[1];
        p2[1] = (pp2[0] - pp2[1]) * cos1_4;
        p2[2] = pp2[2] + pp2[3];
        p2[3] = (pp2[2] - pp2[3]) * cos1_4;
        p2[4] = pp2[4] + pp2[5];
        p2[5] = (pp2[4] - pp2[5]) * cos1_4;
        p2[6] = pp2[6] + pp2[7];
        p2[7] = (pp2[6] - pp2[7]) * cos1_4;
        p2[8] = pp2[8] + pp2[9];
        p2[9] = (pp2[8] - pp2[9]) * cos1_4;
        p2[10] = pp2[10] + pp2[11];
        p2[11] = (pp2[10] - pp2[11]) * cos1_4;
        p2[12] = pp2[12] + pp2[13];
        p2[13] = (pp2[12] - pp2[13]) * cos1_4;
        p2[14] = pp2[14] + pp2[15];
        p2[15] = (pp2[14] - pp2[15]) * cos1_4;
        new_v[12] = p2[7];
        new_v[4] = new_v[12] + p2[5];
        new_v[19] = -new_v[4] - p2[6];
        new_v[27] = -p2[6] - p2[7] - p2[4];
        new_v[14] = p2[15];
        new_v[10] = new_v[14] + p2[11];
        new_v[6] = new_v[10] + p2[13];
        new_v[2] = p2[15] + p2[13] + p2[9];
        new_v[17] = -new_v[2] - p2[14];
        float tmp1 = -p2[14] - p2[15] - p2[10] - p2[11];
        new_v[21] = tmp1 - p2[13];
        new_v[29] = -p2[14] - p2[15] - p2[12] - p2[8];
        new_v[25] = tmp1 - p2[12];
        new_v[31] = -p2[0];
        new_v[0] = p2[1];
        new_v[8] = p2[3];
        new_v[23] = -new_v[8] - p2[2];
        p2[0] = (x1[0] - x1[31]) * cos1_64;
        p2[1] = (x1[1] - x1[30]) * cos3_64;
        p2[2] = (x1[2] - x1[29]) * cos5_64;
        p2[3] = (x1[3] - x1[28]) * cos7_64;
        p2[4] = (x1[4] - x1[27]) * cos9_64;
        p2[5] = (x1[5] - x1[26]) * cos11_64;
        p2[6] = (x1[6] - x1[25]) * cos13_64;
        p2[7] = (x1[7] - x1[24]) * cos15_64;
        p2[8] = (x1[8] - x1[23]) * cos17_64;
        p2[9] = (x1[9] - x1[22]) * cos19_64;
        p2[10] = (x1[10] - x1[21]) * cos21_64;
        p2[11] = (x1[11] - x1[20]) * cos23_64;
        p2[12] = (x1[12] - x1[19]) * cos25_64;
        p2[13] = (x1[13] - x1[18]) * cos27_64;
        p2[14] = (x1[14] - x1[17]) * cos29_64;
        p2[15] = (x1[15] - x1[16]) * cos31_64;
        pp2[0] = p2[0] + p2[15];
        pp2[1] = p2[1] + p2[14];
        pp2[2] = p2[2] + p2[13];
        pp2[3] = p2[3] + p2[12];
        pp2[4] = p2[4] + p2[11];
        pp2[5] = p2[5] + p2[10];
        pp2[6] = p2[6] + p2[9];
        pp2[7] = p2[7] + p2[8];
        pp2[8] = (p2[0] - p2[15]) * cos1_32;
        pp2[9] = (p2[1] - p2[14]) * cos3_32;
        pp2[10] = (p2[2] - p2[13]) * cos5_32;
        pp2[11] = (p2[3] - p2[12]) * cos7_32;
        pp2[12] = (p2[4] - p2[11]) * cos9_32;
        pp2[13] = (p2[5] - p2[10]) * cos11_32;
        pp2[14] = (p2[6] - p2[9]) * cos13_32;
        pp2[15] = (p2[7] - p2[8]) * cos15_32;
        p2[0] = pp2[0] + pp2[7];
        p2[1] = pp2[1] + pp2[6];
        p2[2] = pp2[2] + pp2[5];
        p2[3] = pp2[3] + pp2[4];
        p2[4] = (pp2[0] - pp2[7]) * cos1_16;
        p2[5] = (pp2[1] - pp2[6]) * cos3_16;
        p2[6] = (pp2[2] - pp2[5]) * cos5_16;
        p2[7] = (pp2[3] - pp2[4]) * cos7_16;
        p2[8] = pp2[8] + pp2[15];
        p2[9] = pp2[9] + pp2[14];
        p2[10] = pp2[10] + pp2[13];
        p2[11] = pp2[11] + pp2[12];
        p2[12] = (pp2[8] - pp2[15]) * cos1_16;
        p2[13] = (pp2[9] - pp2[14]) * cos3_16;
        p2[14] = (pp2[10] - pp2[13]) * cos5_16;
        p2[15] = (pp2[11] - pp2[12]) * cos7_16;
        pp2[0] = p2[0] + p2[3];
        pp2[1] = p2[1] + p2[2];
        pp2[2] = (p2[0] - p2[3]) * cos1_8;
        pp2[3] = (p2[1] - p2[2]) * cos3_8;
        pp2[4] = p2[4] + p2[7];
        pp2[5] = p2[5] + p2[6];
        pp2[6] = (p2[4] - p2[7]) * cos1_8;
        pp2[7] = (p2[5] - p2[6]) * cos3_8;
        pp2[8] = p2[8] + p2[11];
        pp2[9] = p2[9] + p2[10];
        pp2[10] = (p2[8] - p2[11]) * cos1_8;
        pp2[11] = (p2[9] - p2[10]) * cos3_8;
        pp2[12] = p2[12] + p2[15];
        pp2[13] = p2[13] + p2[14];
        pp2[14] = (p2[12] - p2[15]) * cos1_8;
        pp2[15] = (p2[13] - p2[14]) * cos3_8;
        p2[0] = pp2[0] + pp2[1];
        p2[1] = (pp2[0] - pp2[1]) * cos1_4;
        p2[2] = pp2[2] + pp2[3];
        p2[3] = (pp2[2] - pp2[3]) * cos1_4;
        p2[4] = pp2[4] + pp2[5];
        p2[5] = (pp2[4] - pp2[5]) * cos1_4;
        p2[6] = pp2[6] + pp2[7];
        p2[7] = (pp2[6] - pp2[7]) * cos1_4;
        p2[8] = pp2[8] + pp2[9];
        p2[9] = (pp2[8] - pp2[9]) * cos1_4;
        p2[10] = pp2[10] + pp2[11];
        p2[11] = (pp2[10] - pp2[11]) * cos1_4;
        p2[12] = pp2[12] + pp2[13];
        p2[13] = (pp2[12] - pp2[13]) * cos1_4;
        p2[14] = pp2[14] + pp2[15];
        p2[15] = (pp2[14] - pp2[15]) * cos1_4;
        new_v[15] = p2[15];
        new_v[13] = new_v[15] + p2[7];
        new_v[11] = new_v[13] + p2[11];
        new_v[5] = new_v[11] + p2[5] + p2[13];
        new_v[9] = p2[15] + p2[11] + p2[3];
        new_v[7] = new_v[9] + p2[13];
        tmp1 = p2[13] + p2[15] + p2[9];
        new_v[1] = tmp1 + p2[1];
        new_v[16] = -new_v[1] - p2[14];
        new_v[3] = tmp1 + p2[5] + p2[7];
        new_v[18] = -new_v[3] - p2[6] - p2[14];
        tmp1 = -p2[10] - p2[11] - p2[14] - p2[15];
        new_v[22] = tmp1 - p2[13] - p2[2] - p2[3];
        new_v[20] = tmp1 - p2[13] - p2[5] - p2[6] - p2[7];
        new_v[24] = tmp1 - p2[12] - p2[2] - p2[3];
        float tmp2 = p2[4] + p2[6] + p2[7];
        new_v[26] = tmp1 - p2[12] - tmp2;
        tmp1 = -p2[8] - p2[12] - p2[14] - p2[15];
        new_v[30] = tmp1 - p2[0];
        new_v[28] = tmp1 - tmp2;
        x1 = new_v;
        float[] dest = this.actual_v;
        dest[0 + this.actual_write_pos] = x1[0];
        dest[16 + this.actual_write_pos] = x1[1];
        dest[32 + this.actual_write_pos] = x1[2];
        dest[48 + this.actual_write_pos] = x1[3];
        dest[64 + this.actual_write_pos] = x1[4];
        dest[80 + this.actual_write_pos] = x1[5];
        dest[96 + this.actual_write_pos] = x1[6];
        dest[112 + this.actual_write_pos] = x1[7];
        dest[128 + this.actual_write_pos] = x1[8];
        dest[144 + this.actual_write_pos] = x1[9];
        dest[160 + this.actual_write_pos] = x1[10];
        dest[176 + this.actual_write_pos] = x1[11];
        dest[192 + this.actual_write_pos] = x1[12];
        dest[208 + this.actual_write_pos] = x1[13];
        dest[224 + this.actual_write_pos] = x1[14];
        dest[240 + this.actual_write_pos] = x1[15];
        dest[256 + this.actual_write_pos] = 0.0f;
        dest[272 + this.actual_write_pos] = -x1[15];
        dest[288 + this.actual_write_pos] = -x1[14];
        dest[304 + this.actual_write_pos] = -x1[13];
        dest[320 + this.actual_write_pos] = -x1[12];
        dest[336 + this.actual_write_pos] = -x1[11];
        dest[352 + this.actual_write_pos] = -x1[10];
        dest[368 + this.actual_write_pos] = -x1[9];
        dest[384 + this.actual_write_pos] = -x1[8];
        dest[400 + this.actual_write_pos] = -x1[7];
        dest[416 + this.actual_write_pos] = -x1[6];
        dest[432 + this.actual_write_pos] = -x1[5];
        dest[448 + this.actual_write_pos] = -x1[4];
        dest[464 + this.actual_write_pos] = -x1[3];
        dest[480 + this.actual_write_pos] = -x1[2];
        dest[496 + this.actual_write_pos] = -x1[1];
    }

    private void compute_pcm_samples0(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[0 + dvp] * dp2[0] + vp2[15 + dvp] * dp2[1] + vp2[14 + dvp] * dp2[2] + vp2[13 + dvp] * dp2[3] + vp2[12 + dvp] * dp2[4] + vp2[11 + dvp] * dp2[5] + vp2[10 + dvp] * dp2[6] + vp2[9 + dvp] * dp2[7] + vp2[8 + dvp] * dp2[8] + vp2[7 + dvp] * dp2[9] + vp2[6 + dvp] * dp2[10] + vp2[5 + dvp] * dp2[11] + vp2[4 + dvp] * dp2[12] + vp2[3 + dvp] * dp2[13] + vp2[2 + dvp] * dp2[14] + vp2[1 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples1(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[1 + dvp] * dp2[0] + vp2[0 + dvp] * dp2[1] + vp2[15 + dvp] * dp2[2] + vp2[14 + dvp] * dp2[3] + vp2[13 + dvp] * dp2[4] + vp2[12 + dvp] * dp2[5] + vp2[11 + dvp] * dp2[6] + vp2[10 + dvp] * dp2[7] + vp2[9 + dvp] * dp2[8] + vp2[8 + dvp] * dp2[9] + vp2[7 + dvp] * dp2[10] + vp2[6 + dvp] * dp2[11] + vp2[5 + dvp] * dp2[12] + vp2[4 + dvp] * dp2[13] + vp2[3 + dvp] * dp2[14] + vp2[2 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples2(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[2 + dvp] * dp2[0] + vp2[1 + dvp] * dp2[1] + vp2[0 + dvp] * dp2[2] + vp2[15 + dvp] * dp2[3] + vp2[14 + dvp] * dp2[4] + vp2[13 + dvp] * dp2[5] + vp2[12 + dvp] * dp2[6] + vp2[11 + dvp] * dp2[7] + vp2[10 + dvp] * dp2[8] + vp2[9 + dvp] * dp2[9] + vp2[8 + dvp] * dp2[10] + vp2[7 + dvp] * dp2[11] + vp2[6 + dvp] * dp2[12] + vp2[5 + dvp] * dp2[13] + vp2[4 + dvp] * dp2[14] + vp2[3 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples3(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        boolean idx = false;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[3 + dvp] * dp2[0] + vp2[2 + dvp] * dp2[1] + vp2[1 + dvp] * dp2[2] + vp2[0 + dvp] * dp2[3] + vp2[15 + dvp] * dp2[4] + vp2[14 + dvp] * dp2[5] + vp2[13 + dvp] * dp2[6] + vp2[12 + dvp] * dp2[7] + vp2[11 + dvp] * dp2[8] + vp2[10 + dvp] * dp2[9] + vp2[9 + dvp] * dp2[10] + vp2[8 + dvp] * dp2[11] + vp2[7 + dvp] * dp2[12] + vp2[6 + dvp] * dp2[13] + vp2[5 + dvp] * dp2[14] + vp2[4 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples4(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[4 + dvp] * dp2[0] + vp2[3 + dvp] * dp2[1] + vp2[2 + dvp] * dp2[2] + vp2[1 + dvp] * dp2[3] + vp2[0 + dvp] * dp2[4] + vp2[15 + dvp] * dp2[5] + vp2[14 + dvp] * dp2[6] + vp2[13 + dvp] * dp2[7] + vp2[12 + dvp] * dp2[8] + vp2[11 + dvp] * dp2[9] + vp2[10 + dvp] * dp2[10] + vp2[9 + dvp] * dp2[11] + vp2[8 + dvp] * dp2[12] + vp2[7 + dvp] * dp2[13] + vp2[6 + dvp] * dp2[14] + vp2[5 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples5(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[5 + dvp] * dp2[0] + vp2[4 + dvp] * dp2[1] + vp2[3 + dvp] * dp2[2] + vp2[2 + dvp] * dp2[3] + vp2[1 + dvp] * dp2[4] + vp2[0 + dvp] * dp2[5] + vp2[15 + dvp] * dp2[6] + vp2[14 + dvp] * dp2[7] + vp2[13 + dvp] * dp2[8] + vp2[12 + dvp] * dp2[9] + vp2[11 + dvp] * dp2[10] + vp2[10 + dvp] * dp2[11] + vp2[9 + dvp] * dp2[12] + vp2[8 + dvp] * dp2[13] + vp2[7 + dvp] * dp2[14] + vp2[6 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples6(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[6 + dvp] * dp2[0] + vp2[5 + dvp] * dp2[1] + vp2[4 + dvp] * dp2[2] + vp2[3 + dvp] * dp2[3] + vp2[2 + dvp] * dp2[4] + vp2[1 + dvp] * dp2[5] + vp2[0 + dvp] * dp2[6] + vp2[15 + dvp] * dp2[7] + vp2[14 + dvp] * dp2[8] + vp2[13 + dvp] * dp2[9] + vp2[12 + dvp] * dp2[10] + vp2[11 + dvp] * dp2[11] + vp2[10 + dvp] * dp2[12] + vp2[9 + dvp] * dp2[13] + vp2[8 + dvp] * dp2[14] + vp2[7 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples7(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[7 + dvp] * dp2[0] + vp2[6 + dvp] * dp2[1] + vp2[5 + dvp] * dp2[2] + vp2[4 + dvp] * dp2[3] + vp2[3 + dvp] * dp2[4] + vp2[2 + dvp] * dp2[5] + vp2[1 + dvp] * dp2[6] + vp2[0 + dvp] * dp2[7] + vp2[15 + dvp] * dp2[8] + vp2[14 + dvp] * dp2[9] + vp2[13 + dvp] * dp2[10] + vp2[12 + dvp] * dp2[11] + vp2[11 + dvp] * dp2[12] + vp2[10 + dvp] * dp2[13] + vp2[9 + dvp] * dp2[14] + vp2[8 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples8(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[8 + dvp] * dp2[0] + vp2[7 + dvp] * dp2[1] + vp2[6 + dvp] * dp2[2] + vp2[5 + dvp] * dp2[3] + vp2[4 + dvp] * dp2[4] + vp2[3 + dvp] * dp2[5] + vp2[2 + dvp] * dp2[6] + vp2[1 + dvp] * dp2[7] + vp2[0 + dvp] * dp2[8] + vp2[15 + dvp] * dp2[9] + vp2[14 + dvp] * dp2[10] + vp2[13 + dvp] * dp2[11] + vp2[12 + dvp] * dp2[12] + vp2[11 + dvp] * dp2[13] + vp2[10 + dvp] * dp2[14] + vp2[9 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples9(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[9 + dvp] * dp2[0] + vp2[8 + dvp] * dp2[1] + vp2[7 + dvp] * dp2[2] + vp2[6 + dvp] * dp2[3] + vp2[5 + dvp] * dp2[4] + vp2[4 + dvp] * dp2[5] + vp2[3 + dvp] * dp2[6] + vp2[2 + dvp] * dp2[7] + vp2[1 + dvp] * dp2[8] + vp2[0 + dvp] * dp2[9] + vp2[15 + dvp] * dp2[10] + vp2[14 + dvp] * dp2[11] + vp2[13 + dvp] * dp2[12] + vp2[12 + dvp] * dp2[13] + vp2[11 + dvp] * dp2[14] + vp2[10 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples10(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[10 + dvp] * dp2[0] + vp2[9 + dvp] * dp2[1] + vp2[8 + dvp] * dp2[2] + vp2[7 + dvp] * dp2[3] + vp2[6 + dvp] * dp2[4] + vp2[5 + dvp] * dp2[5] + vp2[4 + dvp] * dp2[6] + vp2[3 + dvp] * dp2[7] + vp2[2 + dvp] * dp2[8] + vp2[1 + dvp] * dp2[9] + vp2[0 + dvp] * dp2[10] + vp2[15 + dvp] * dp2[11] + vp2[14 + dvp] * dp2[12] + vp2[13 + dvp] * dp2[13] + vp2[12 + dvp] * dp2[14] + vp2[11 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples11(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[11 + dvp] * dp2[0] + vp2[10 + dvp] * dp2[1] + vp2[9 + dvp] * dp2[2] + vp2[8 + dvp] * dp2[3] + vp2[7 + dvp] * dp2[4] + vp2[6 + dvp] * dp2[5] + vp2[5 + dvp] * dp2[6] + vp2[4 + dvp] * dp2[7] + vp2[3 + dvp] * dp2[8] + vp2[2 + dvp] * dp2[9] + vp2[1 + dvp] * dp2[10] + vp2[0 + dvp] * dp2[11] + vp2[15 + dvp] * dp2[12] + vp2[14 + dvp] * dp2[13] + vp2[13 + dvp] * dp2[14] + vp2[12 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples12(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[12 + dvp] * dp2[0] + vp2[11 + dvp] * dp2[1] + vp2[10 + dvp] * dp2[2] + vp2[9 + dvp] * dp2[3] + vp2[8 + dvp] * dp2[4] + vp2[7 + dvp] * dp2[5] + vp2[6 + dvp] * dp2[6] + vp2[5 + dvp] * dp2[7] + vp2[4 + dvp] * dp2[8] + vp2[3 + dvp] * dp2[9] + vp2[2 + dvp] * dp2[10] + vp2[1 + dvp] * dp2[11] + vp2[0 + dvp] * dp2[12] + vp2[15 + dvp] * dp2[13] + vp2[14 + dvp] * dp2[14] + vp2[13 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples13(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[13 + dvp] * dp2[0] + vp2[12 + dvp] * dp2[1] + vp2[11 + dvp] * dp2[2] + vp2[10 + dvp] * dp2[3] + vp2[9 + dvp] * dp2[4] + vp2[8 + dvp] * dp2[5] + vp2[7 + dvp] * dp2[6] + vp2[6 + dvp] * dp2[7] + vp2[5 + dvp] * dp2[8] + vp2[4 + dvp] * dp2[9] + vp2[3 + dvp] * dp2[10] + vp2[2 + dvp] * dp2[11] + vp2[1 + dvp] * dp2[12] + vp2[0 + dvp] * dp2[13] + vp2[15 + dvp] * dp2[14] + vp2[14 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples14(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[14 + dvp] * dp2[0] + vp2[13 + dvp] * dp2[1] + vp2[12 + dvp] * dp2[2] + vp2[11 + dvp] * dp2[3] + vp2[10 + dvp] * dp2[4] + vp2[9 + dvp] * dp2[5] + vp2[8 + dvp] * dp2[6] + vp2[7 + dvp] * dp2[7] + vp2[6 + dvp] * dp2[8] + vp2[5 + dvp] * dp2[9] + vp2[4 + dvp] * dp2[10] + vp2[3 + dvp] * dp2[11] + vp2[2 + dvp] * dp2[12] + vp2[1 + dvp] * dp2[13] + vp2[0 + dvp] * dp2[14] + vp2[15 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples15(Obuffer buffer) {
        float[] vp2 = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        int i2 = 0;
        while (i2 < 32) {
            float pcm_sample;
            float[] dp2 = d16[i2];
            tmpOut[i2] = pcm_sample = (vp2[15 + dvp] * dp2[0] + vp2[14 + dvp] * dp2[1] + vp2[13 + dvp] * dp2[2] + vp2[12 + dvp] * dp2[3] + vp2[11 + dvp] * dp2[4] + vp2[10 + dvp] * dp2[5] + vp2[9 + dvp] * dp2[6] + vp2[8 + dvp] * dp2[7] + vp2[7 + dvp] * dp2[8] + vp2[6 + dvp] * dp2[9] + vp2[5 + dvp] * dp2[10] + vp2[4 + dvp] * dp2[11] + vp2[3 + dvp] * dp2[12] + vp2[2 + dvp] * dp2[13] + vp2[1 + dvp] * dp2[14] + vp2[0 + dvp] * dp2[15]) * this.scalefactor;
            dvp += 16;
            ++i2;
        }
    }

    private void compute_pcm_samples(Obuffer buffer) {
        switch (this.actual_write_pos) {
            case 0: {
                this.compute_pcm_samples0(buffer);
                break;
            }
            case 1: {
                this.compute_pcm_samples1(buffer);
                break;
            }
            case 2: {
                this.compute_pcm_samples2(buffer);
                break;
            }
            case 3: {
                this.compute_pcm_samples3(buffer);
                break;
            }
            case 4: {
                this.compute_pcm_samples4(buffer);
                break;
            }
            case 5: {
                this.compute_pcm_samples5(buffer);
                break;
            }
            case 6: {
                this.compute_pcm_samples6(buffer);
                break;
            }
            case 7: {
                this.compute_pcm_samples7(buffer);
                break;
            }
            case 8: {
                this.compute_pcm_samples8(buffer);
                break;
            }
            case 9: {
                this.compute_pcm_samples9(buffer);
                break;
            }
            case 10: {
                this.compute_pcm_samples10(buffer);
                break;
            }
            case 11: {
                this.compute_pcm_samples11(buffer);
                break;
            }
            case 12: {
                this.compute_pcm_samples12(buffer);
                break;
            }
            case 13: {
                this.compute_pcm_samples13(buffer);
                break;
            }
            case 14: {
                this.compute_pcm_samples14(buffer);
                break;
            }
            case 15: {
                this.compute_pcm_samples15(buffer);
            }
        }
        if (buffer != null) {
            buffer.appendSamples(this.channel, this._tmpOut);
        }
    }

    public void calculate_pcm_samples(Obuffer buffer) {
        this.compute_new_v();
        this.compute_pcm_samples(buffer);
        this.actual_write_pos = this.actual_write_pos + 1 & 0xF;
        this.actual_v = this.actual_v == this.v1 ? this.v2 : this.v1;
        int p2 = 0;
        while (p2 < 32) {
            this.samples[p2] = 0.0f;
            ++p2;
        }
    }

    private static float[] load_d() {
        try {
            Class<Float> elemType = Float.TYPE;
            Object o2 = JavaLayerUtils.deserializeArrayResource("sfd.ser", elemType, 512);
            return (float[])o2;
        }
        catch (IOException ex2) {
            throw new ExceptionInInitializerError(ex2);
        }
    }

    private static float[][] splitArray(float[] array, int blockSize) {
        int size = array.length / blockSize;
        float[][] split = new float[size][];
        int i2 = 0;
        while (i2 < size) {
            split[i2] = SynthesisFilter.subArray(array, i2 * blockSize, blockSize);
            ++i2;
        }
        return split;
    }

    private static float[] subArray(float[] array, int offs, int len) {
        if (offs + len > array.length) {
            len = array.length - offs;
        }
        if (len < 0) {
            len = 0;
        }
        float[] subarray = new float[len];
        int i2 = 0;
        while (i2 < len) {
            subarray[i2] = array[offs + i2];
            ++i2;
        }
        return subarray;
    }
}

