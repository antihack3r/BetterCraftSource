/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import javazoom.jl.decoder.BitReserve;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.FrameDecoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.Obuffer;
import javazoom.jl.decoder.SynthesisFilter;
import javazoom.jl.decoder.huffcodetab;

final class LayerIIIDecoder
implements FrameDecoder {
    final double d43 = 1.3333333333333333;
    public int[] scalefac_buffer;
    private int CheckSumHuff = 0;
    private int[] is_1d;
    private float[][][] ro;
    private float[][][] lr;
    private float[] out_1d;
    private float[][] prevblck;
    private float[][] k;
    private int[] nonzero;
    private Bitstream stream;
    private Header header;
    private SynthesisFilter filter1;
    private SynthesisFilter filter2;
    private Obuffer buffer;
    private int which_channels;
    private BitReserve br;
    private III_side_info_t si;
    private temporaire2[] III_scalefac_t;
    private temporaire2[] scalefac;
    private int max_gr;
    private int frame_start;
    private int part2_start;
    private int channels;
    private int first_channel;
    private int last_channel;
    private int sfreq;
    private float[] samples1 = new float[32];
    private float[] samples2 = new float[32];
    private final int[] new_slen = new int[4];
    int[] x = new int[1];
    int[] y = new int[1];
    int[] v = new int[1];
    int[] w = new int[1];
    int[] is_pos = new int[576];
    float[] is_ratio = new float[576];
    float[] tsOutCopy = new float[18];
    float[] rawout = new float[36];
    private int counter = 0;
    private static final int SSLIMIT = 18;
    private static final int SBLIMIT = 32;
    private static final int[][] slen;
    public static final int[] pretab;
    private SBI[] sfBandIndex;
    public static final float[] two_to_negative_half_pow;
    public static final float[] t_43;
    public static final float[][] io;
    public static final float[] TAN12;
    private static int[][] reorder_table;
    private static final float[] cs;
    private static final float[] ca;
    public static final float[][] win;
    public Sftable sftable;
    public static final int[][][] nr_of_sfb_block;

    static {
        int[][] nArrayArray = new int[2][];
        int[] nArray = new int[16];
        nArray[4] = 3;
        nArray[5] = 1;
        nArray[6] = 1;
        nArray[7] = 1;
        nArray[8] = 2;
        nArray[9] = 2;
        nArray[10] = 2;
        nArray[11] = 3;
        nArray[12] = 3;
        nArray[13] = 3;
        nArray[14] = 4;
        nArray[15] = 4;
        nArrayArray[0] = nArray;
        int[] nArray2 = new int[16];
        nArray2[1] = 1;
        nArray2[2] = 2;
        nArray2[3] = 3;
        nArray2[5] = 1;
        nArray2[6] = 2;
        nArray2[7] = 3;
        nArray2[8] = 1;
        nArray2[9] = 2;
        nArray2[10] = 3;
        nArray2[11] = 1;
        nArray2[12] = 2;
        nArray2[13] = 3;
        nArray2[14] = 2;
        nArray2[15] = 3;
        nArrayArray[1] = nArray2;
        slen = nArrayArray;
        int[] nArray3 = new int[22];
        nArray3[11] = 1;
        nArray3[12] = 1;
        nArray3[13] = 1;
        nArray3[14] = 1;
        nArray3[15] = 2;
        nArray3[16] = 2;
        nArray3[17] = 3;
        nArray3[18] = 3;
        nArray3[19] = 3;
        nArray3[20] = 2;
        pretab = nArray3;
        two_to_negative_half_pow = new float[]{1.0f, 0.70710677f, 0.5f, 0.35355338f, 0.25f, 0.17677669f, 0.125f, 0.088388346f, 0.0625f, 0.044194173f, 0.03125f, 0.022097087f, 0.015625f, 0.011048543f, 0.0078125f, 0.0055242716f, 0.00390625f, 0.0027621358f, 0.001953125f, 0.0013810679f, 9.765625E-4f, 6.9053395E-4f, 4.8828125E-4f, 3.4526698E-4f, 2.4414062E-4f, 1.7263349E-4f, 1.2207031E-4f, 8.6316744E-5f, 6.1035156E-5f, 4.3158372E-5f, 3.0517578E-5f, 2.1579186E-5f, 1.5258789E-5f, 1.0789593E-5f, 7.6293945E-6f, 5.3947965E-6f, 3.8146973E-6f, 2.6973983E-6f, 1.9073486E-6f, 1.3486991E-6f, 9.536743E-7f, 6.7434956E-7f, 4.7683716E-7f, 3.3717478E-7f, 2.3841858E-7f, 1.6858739E-7f, 1.1920929E-7f, 8.4293696E-8f, 5.9604645E-8f, 4.2146848E-8f, 2.9802322E-8f, 2.1073424E-8f, 1.4901161E-8f, 1.0536712E-8f, 7.450581E-9f, 5.268356E-9f, 3.7252903E-9f, 2.634178E-9f, 1.8626451E-9f, 1.317089E-9f, 9.313226E-10f, 6.585445E-10f, 4.656613E-10f, 3.2927225E-10f};
        t_43 = LayerIIIDecoder.create_t_43();
        io = new float[][]{{1.0f, 0.8408964f, 0.70710677f, 0.59460354f, 0.5f, 0.4204482f, 0.35355338f, 0.29730177f, 0.25f, 0.2102241f, 0.17677669f, 0.14865088f, 0.125f, 0.10511205f, 0.088388346f, 0.07432544f, 0.0625f, 0.052556027f, 0.044194173f, 0.03716272f, 0.03125f, 0.026278013f, 0.022097087f, 0.01858136f, 0.015625f, 0.013139007f, 0.011048543f, 0.00929068f, 0.0078125f, 0.0065695033f, 0.0055242716f, 0.00464534f}, {1.0f, 0.70710677f, 0.5f, 0.35355338f, 0.25f, 0.17677669f, 0.125f, 0.088388346f, 0.0625f, 0.044194173f, 0.03125f, 0.022097087f, 0.015625f, 0.011048543f, 0.0078125f, 0.0055242716f, 0.00390625f, 0.0027621358f, 0.001953125f, 0.0013810679f, 9.765625E-4f, 6.9053395E-4f, 4.8828125E-4f, 3.4526698E-4f, 2.4414062E-4f, 1.7263349E-4f, 1.2207031E-4f, 8.6316744E-5f, 6.1035156E-5f, 4.3158372E-5f, 3.0517578E-5f, 2.1579186E-5f}};
        TAN12 = new float[]{0.0f, 0.2679492f, 0.57735026f, 1.0f, 1.7320508f, 3.732051f, 1.0E11f, -3.732051f, -1.7320508f, -1.0f, -0.57735026f, -0.2679492f, 0.0f, 0.2679492f, 0.57735026f, 1.0f};
        cs = new float[]{0.8574929f, 0.881742f, 0.94962865f, 0.9833146f, 0.9955178f, 0.9991606f, 0.9998992f, 0.99999315f};
        ca = new float[]{-0.51449573f, -0.47173196f, -0.31337744f, -0.1819132f, -0.09457419f, -0.040965583f, -0.014198569f, -0.0036999746f};
        win = new float[][]{{-0.016141215f, -0.05360318f, -0.100707136f, -0.16280818f, -0.5f, -0.38388735f, -0.6206114f, -1.1659756f, -3.8720753f, -4.225629f, -1.519529f, -0.97416484f, -0.73744076f, -1.2071068f, -0.5163616f, -0.45426053f, -0.40715656f, -0.3696946f, -0.3387627f, -0.31242222f, -0.28939587f, -0.26880082f, -0.5f, -0.23251417f, -0.21596715f, -0.20004979f, -0.18449493f, -0.16905846f, -0.15350361f, -0.13758625f, -0.12103922f, -0.20710678f, -0.084752575f, -0.06415752f, -0.041131172f, -0.014790705f}, {-0.016141215f, -0.05360318f, -0.100707136f, -0.16280818f, -0.5f, -0.38388735f, -0.6206114f, -1.1659756f, -3.8720753f, -4.225629f, -1.519529f, -0.97416484f, -0.73744076f, -1.2071068f, -0.5163616f, -0.45426053f, -0.40715656f, -0.3696946f, -0.33908543f, -0.3151181f, -0.29642227f, -0.28184548f, -0.5411961f, -0.2621323f, -0.25387916f, -0.2329629f, -0.19852729f, -0.15233535f, -0.0964964f, -0.03342383f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {-0.0483008f, -0.15715657f, -0.28325045f, -0.42953748f, -1.2071068f, -0.8242648f, -1.1451749f, -1.769529f, -4.5470223f, -3.489053f, -0.7329629f, -0.15076515f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.15076514f, -0.7329629f, -3.489053f, -4.5470223f, -1.769529f, -1.1451749f, -0.8313774f, -1.306563f, -0.54142016f, -0.46528974f, -0.4106699f, -0.3700468f, -0.3387627f, -0.31242222f, -0.28939587f, -0.26880082f, -0.5f, -0.23251417f, -0.21596715f, -0.20004979f, -0.18449493f, -0.16905846f, -0.15350361f, -0.13758625f, -0.12103922f, -0.20710678f, -0.084752575f, -0.06415752f, -0.041131172f, -0.014790705f}};
        int[][][] nArrayArray2 = new int[6][][];
        nArrayArray2[0] = new int[][]{{6, 5, 5, 5}, {9, 9, 9, 9}, {6, 9, 9, 9}};
        nArrayArray2[1] = new int[][]{{6, 5, 7, 3}, {9, 9, 12, 6}, {6, 9, 12, 6}};
        int[][] nArrayArray3 = new int[3][];
        int[] nArray4 = new int[4];
        nArray4[0] = 11;
        nArray4[1] = 10;
        nArrayArray3[0] = nArray4;
        int[] nArray5 = new int[4];
        nArray5[0] = 18;
        nArray5[1] = 18;
        nArrayArray3[1] = nArray5;
        int[] nArray6 = new int[4];
        nArray6[0] = 15;
        nArray6[1] = 18;
        nArrayArray3[2] = nArray6;
        nArrayArray2[2] = nArrayArray3;
        int[][] nArrayArray4 = new int[3][];
        int[] nArray7 = new int[4];
        nArray7[0] = 7;
        nArray7[1] = 7;
        nArray7[2] = 7;
        nArrayArray4[0] = nArray7;
        int[] nArray8 = new int[4];
        nArray8[0] = 12;
        nArray8[1] = 12;
        nArray8[2] = 12;
        nArrayArray4[1] = nArray8;
        int[] nArray9 = new int[4];
        nArray9[0] = 6;
        nArray9[1] = 15;
        nArray9[2] = 12;
        nArrayArray4[2] = nArray9;
        nArrayArray2[3] = nArrayArray4;
        nArrayArray2[4] = new int[][]{{6, 6, 6, 3}, {12, 9, 9, 6}, {6, 12, 9, 6}};
        int[][] nArrayArray5 = new int[3][];
        int[] nArray10 = new int[4];
        nArray10[0] = 8;
        nArray10[1] = 8;
        nArray10[2] = 5;
        nArrayArray5[0] = nArray10;
        int[] nArray11 = new int[4];
        nArray11[0] = 15;
        nArray11[1] = 12;
        nArray11[2] = 9;
        nArrayArray5[1] = nArray11;
        int[] nArray12 = new int[4];
        nArray12[0] = 6;
        nArray12[1] = 18;
        nArray12[2] = 9;
        nArrayArray5[2] = nArray12;
        nArrayArray2[5] = nArrayArray5;
        nr_of_sfb_block = nArrayArray2;
    }

    public LayerIIIDecoder(Bitstream stream0, Header header0, SynthesisFilter filtera, SynthesisFilter filterb, Obuffer buffer0, int which_ch0) {
        huffcodetab.inithuff();
        this.is_1d = new int[580];
        this.ro = new float[2][32][18];
        this.lr = new float[2][32][18];
        this.out_1d = new float[576];
        this.prevblck = new float[2][576];
        this.k = new float[2][576];
        this.nonzero = new int[2];
        this.III_scalefac_t = new temporaire2[2];
        this.III_scalefac_t[0] = new temporaire2();
        this.III_scalefac_t[1] = new temporaire2();
        this.scalefac = this.III_scalefac_t;
        this.sfBandIndex = new SBI[9];
        int[] nArray = new int[23];
        nArray[1] = 6;
        nArray[2] = 12;
        nArray[3] = 18;
        nArray[4] = 24;
        nArray[5] = 30;
        nArray[6] = 36;
        nArray[7] = 44;
        nArray[8] = 54;
        nArray[9] = 66;
        nArray[10] = 80;
        nArray[11] = 96;
        nArray[12] = 116;
        nArray[13] = 140;
        nArray[14] = 168;
        nArray[15] = 200;
        nArray[16] = 238;
        nArray[17] = 284;
        nArray[18] = 336;
        nArray[19] = 396;
        nArray[20] = 464;
        nArray[21] = 522;
        nArray[22] = 576;
        int[] l0 = nArray;
        int[] nArray2 = new int[14];
        nArray2[1] = 4;
        nArray2[2] = 8;
        nArray2[3] = 12;
        nArray2[4] = 18;
        nArray2[5] = 24;
        nArray2[6] = 32;
        nArray2[7] = 42;
        nArray2[8] = 56;
        nArray2[9] = 74;
        nArray2[10] = 100;
        nArray2[11] = 132;
        nArray2[12] = 174;
        nArray2[13] = 192;
        int[] s0 = nArray2;
        int[] nArray3 = new int[23];
        nArray3[1] = 6;
        nArray3[2] = 12;
        nArray3[3] = 18;
        nArray3[4] = 24;
        nArray3[5] = 30;
        nArray3[6] = 36;
        nArray3[7] = 44;
        nArray3[8] = 54;
        nArray3[9] = 66;
        nArray3[10] = 80;
        nArray3[11] = 96;
        nArray3[12] = 114;
        nArray3[13] = 136;
        nArray3[14] = 162;
        nArray3[15] = 194;
        nArray3[16] = 232;
        nArray3[17] = 278;
        nArray3[18] = 330;
        nArray3[19] = 394;
        nArray3[20] = 464;
        nArray3[21] = 540;
        nArray3[22] = 576;
        int[] l1 = nArray3;
        int[] nArray4 = new int[14];
        nArray4[1] = 4;
        nArray4[2] = 8;
        nArray4[3] = 12;
        nArray4[4] = 18;
        nArray4[5] = 26;
        nArray4[6] = 36;
        nArray4[7] = 48;
        nArray4[8] = 62;
        nArray4[9] = 80;
        nArray4[10] = 104;
        nArray4[11] = 136;
        nArray4[12] = 180;
        nArray4[13] = 192;
        int[] s1 = nArray4;
        int[] nArray5 = new int[23];
        nArray5[1] = 6;
        nArray5[2] = 12;
        nArray5[3] = 18;
        nArray5[4] = 24;
        nArray5[5] = 30;
        nArray5[6] = 36;
        nArray5[7] = 44;
        nArray5[8] = 54;
        nArray5[9] = 66;
        nArray5[10] = 80;
        nArray5[11] = 96;
        nArray5[12] = 116;
        nArray5[13] = 140;
        nArray5[14] = 168;
        nArray5[15] = 200;
        nArray5[16] = 238;
        nArray5[17] = 284;
        nArray5[18] = 336;
        nArray5[19] = 396;
        nArray5[20] = 464;
        nArray5[21] = 522;
        nArray5[22] = 576;
        int[] l2 = nArray5;
        int[] nArray6 = new int[14];
        nArray6[1] = 4;
        nArray6[2] = 8;
        nArray6[3] = 12;
        nArray6[4] = 18;
        nArray6[5] = 26;
        nArray6[6] = 36;
        nArray6[7] = 48;
        nArray6[8] = 62;
        nArray6[9] = 80;
        nArray6[10] = 104;
        nArray6[11] = 134;
        nArray6[12] = 174;
        nArray6[13] = 192;
        int[] s2 = nArray6;
        int[] nArray7 = new int[23];
        nArray7[1] = 4;
        nArray7[2] = 8;
        nArray7[3] = 12;
        nArray7[4] = 16;
        nArray7[5] = 20;
        nArray7[6] = 24;
        nArray7[7] = 30;
        nArray7[8] = 36;
        nArray7[9] = 44;
        nArray7[10] = 52;
        nArray7[11] = 62;
        nArray7[12] = 74;
        nArray7[13] = 90;
        nArray7[14] = 110;
        nArray7[15] = 134;
        nArray7[16] = 162;
        nArray7[17] = 196;
        nArray7[18] = 238;
        nArray7[19] = 288;
        nArray7[20] = 342;
        nArray7[21] = 418;
        nArray7[22] = 576;
        int[] l3 = nArray7;
        int[] nArray8 = new int[14];
        nArray8[1] = 4;
        nArray8[2] = 8;
        nArray8[3] = 12;
        nArray8[4] = 16;
        nArray8[5] = 22;
        nArray8[6] = 30;
        nArray8[7] = 40;
        nArray8[8] = 52;
        nArray8[9] = 66;
        nArray8[10] = 84;
        nArray8[11] = 106;
        nArray8[12] = 136;
        nArray8[13] = 192;
        int[] s3 = nArray8;
        int[] nArray9 = new int[23];
        nArray9[1] = 4;
        nArray9[2] = 8;
        nArray9[3] = 12;
        nArray9[4] = 16;
        nArray9[5] = 20;
        nArray9[6] = 24;
        nArray9[7] = 30;
        nArray9[8] = 36;
        nArray9[9] = 42;
        nArray9[10] = 50;
        nArray9[11] = 60;
        nArray9[12] = 72;
        nArray9[13] = 88;
        nArray9[14] = 106;
        nArray9[15] = 128;
        nArray9[16] = 156;
        nArray9[17] = 190;
        nArray9[18] = 230;
        nArray9[19] = 276;
        nArray9[20] = 330;
        nArray9[21] = 384;
        nArray9[22] = 576;
        int[] l4 = nArray9;
        int[] nArray10 = new int[14];
        nArray10[1] = 4;
        nArray10[2] = 8;
        nArray10[3] = 12;
        nArray10[4] = 16;
        nArray10[5] = 22;
        nArray10[6] = 28;
        nArray10[7] = 38;
        nArray10[8] = 50;
        nArray10[9] = 64;
        nArray10[10] = 80;
        nArray10[11] = 100;
        nArray10[12] = 126;
        nArray10[13] = 192;
        int[] s4 = nArray10;
        int[] nArray11 = new int[23];
        nArray11[1] = 4;
        nArray11[2] = 8;
        nArray11[3] = 12;
        nArray11[4] = 16;
        nArray11[5] = 20;
        nArray11[6] = 24;
        nArray11[7] = 30;
        nArray11[8] = 36;
        nArray11[9] = 44;
        nArray11[10] = 54;
        nArray11[11] = 66;
        nArray11[12] = 82;
        nArray11[13] = 102;
        nArray11[14] = 126;
        nArray11[15] = 156;
        nArray11[16] = 194;
        nArray11[17] = 240;
        nArray11[18] = 296;
        nArray11[19] = 364;
        nArray11[20] = 448;
        nArray11[21] = 550;
        nArray11[22] = 576;
        int[] l5 = nArray11;
        int[] nArray12 = new int[14];
        nArray12[1] = 4;
        nArray12[2] = 8;
        nArray12[3] = 12;
        nArray12[4] = 16;
        nArray12[5] = 22;
        nArray12[6] = 30;
        nArray12[7] = 42;
        nArray12[8] = 58;
        nArray12[9] = 78;
        nArray12[10] = 104;
        nArray12[11] = 138;
        nArray12[12] = 180;
        nArray12[13] = 192;
        int[] s5 = nArray12;
        int[] nArray13 = new int[23];
        nArray13[1] = 6;
        nArray13[2] = 12;
        nArray13[3] = 18;
        nArray13[4] = 24;
        nArray13[5] = 30;
        nArray13[6] = 36;
        nArray13[7] = 44;
        nArray13[8] = 54;
        nArray13[9] = 66;
        nArray13[10] = 80;
        nArray13[11] = 96;
        nArray13[12] = 116;
        nArray13[13] = 140;
        nArray13[14] = 168;
        nArray13[15] = 200;
        nArray13[16] = 238;
        nArray13[17] = 284;
        nArray13[18] = 336;
        nArray13[19] = 396;
        nArray13[20] = 464;
        nArray13[21] = 522;
        nArray13[22] = 576;
        int[] l6 = nArray13;
        int[] nArray14 = new int[14];
        nArray14[1] = 4;
        nArray14[2] = 8;
        nArray14[3] = 12;
        nArray14[4] = 18;
        nArray14[5] = 26;
        nArray14[6] = 36;
        nArray14[7] = 48;
        nArray14[8] = 62;
        nArray14[9] = 80;
        nArray14[10] = 104;
        nArray14[11] = 134;
        nArray14[12] = 174;
        nArray14[13] = 192;
        int[] s6 = nArray14;
        int[] nArray15 = new int[23];
        nArray15[1] = 6;
        nArray15[2] = 12;
        nArray15[3] = 18;
        nArray15[4] = 24;
        nArray15[5] = 30;
        nArray15[6] = 36;
        nArray15[7] = 44;
        nArray15[8] = 54;
        nArray15[9] = 66;
        nArray15[10] = 80;
        nArray15[11] = 96;
        nArray15[12] = 116;
        nArray15[13] = 140;
        nArray15[14] = 168;
        nArray15[15] = 200;
        nArray15[16] = 238;
        nArray15[17] = 284;
        nArray15[18] = 336;
        nArray15[19] = 396;
        nArray15[20] = 464;
        nArray15[21] = 522;
        nArray15[22] = 576;
        int[] l7 = nArray15;
        int[] nArray16 = new int[14];
        nArray16[1] = 4;
        nArray16[2] = 8;
        nArray16[3] = 12;
        nArray16[4] = 18;
        nArray16[5] = 26;
        nArray16[6] = 36;
        nArray16[7] = 48;
        nArray16[8] = 62;
        nArray16[9] = 80;
        nArray16[10] = 104;
        nArray16[11] = 134;
        nArray16[12] = 174;
        nArray16[13] = 192;
        int[] s7 = nArray16;
        int[] nArray17 = new int[23];
        nArray17[1] = 12;
        nArray17[2] = 24;
        nArray17[3] = 36;
        nArray17[4] = 48;
        nArray17[5] = 60;
        nArray17[6] = 72;
        nArray17[7] = 88;
        nArray17[8] = 108;
        nArray17[9] = 132;
        nArray17[10] = 160;
        nArray17[11] = 192;
        nArray17[12] = 232;
        nArray17[13] = 280;
        nArray17[14] = 336;
        nArray17[15] = 400;
        nArray17[16] = 476;
        nArray17[17] = 566;
        nArray17[18] = 568;
        nArray17[19] = 570;
        nArray17[20] = 572;
        nArray17[21] = 574;
        nArray17[22] = 576;
        int[] l8 = nArray17;
        int[] nArray18 = new int[14];
        nArray18[1] = 8;
        nArray18[2] = 16;
        nArray18[3] = 24;
        nArray18[4] = 36;
        nArray18[5] = 52;
        nArray18[6] = 72;
        nArray18[7] = 96;
        nArray18[8] = 124;
        nArray18[9] = 160;
        nArray18[10] = 162;
        nArray18[11] = 164;
        nArray18[12] = 166;
        nArray18[13] = 192;
        int[] s8 = nArray18;
        this.sfBandIndex[0] = new SBI(l0, s0);
        this.sfBandIndex[1] = new SBI(l1, s1);
        this.sfBandIndex[2] = new SBI(l2, s2);
        this.sfBandIndex[3] = new SBI(l3, s3);
        this.sfBandIndex[4] = new SBI(l4, s4);
        this.sfBandIndex[5] = new SBI(l5, s5);
        this.sfBandIndex[6] = new SBI(l6, s6);
        this.sfBandIndex[7] = new SBI(l7, s7);
        this.sfBandIndex[8] = new SBI(l8, s8);
        if (reorder_table == null) {
            reorder_table = new int[9][];
            int i2 = 0;
            while (i2 < 9) {
                LayerIIIDecoder.reorder_table[i2] = LayerIIIDecoder.reorder(this.sfBandIndex[i2].s);
                ++i2;
            }
        }
        int[] nArray19 = new int[5];
        nArray19[1] = 6;
        nArray19[2] = 11;
        nArray19[3] = 16;
        nArray19[4] = 21;
        int[] ll0 = nArray19;
        int[] nArray20 = new int[3];
        nArray20[1] = 6;
        nArray20[2] = 12;
        int[] ss0 = nArray20;
        this.sftable = new Sftable(ll0, ss0);
        this.scalefac_buffer = new int[54];
        this.stream = stream0;
        this.header = header0;
        this.filter1 = filtera;
        this.filter2 = filterb;
        this.buffer = buffer0;
        this.which_channels = which_ch0;
        this.frame_start = 0;
        this.channels = this.header.mode() == 3 ? 1 : 2;
        this.max_gr = this.header.version() == 1 ? 2 : 1;
        this.sfreq = this.header.sample_frequency() + (this.header.version() == 1 ? 3 : (this.header.version() == 2 ? 6 : 0));
        if (this.channels == 2) {
            switch (this.which_channels) {
                case 1: 
                case 3: {
                    this.last_channel = 0;
                    this.first_channel = 0;
                    break;
                }
                case 2: {
                    this.last_channel = 1;
                    this.first_channel = 1;
                    break;
                }
                default: {
                    this.first_channel = 0;
                    this.last_channel = 1;
                    break;
                }
            }
        } else {
            this.last_channel = 0;
            this.first_channel = 0;
        }
        int ch = 0;
        while (ch < 2) {
            int j2 = 0;
            while (j2 < 576) {
                this.prevblck[ch][j2] = 0.0f;
                ++j2;
            }
            ++ch;
        }
        this.nonzero[1] = 576;
        this.nonzero[0] = 576;
        this.br = new BitReserve();
        this.si = new III_side_info_t();
    }

    public void seek_notify() {
        this.frame_start = 0;
        int ch = 0;
        while (ch < 2) {
            int j2 = 0;
            while (j2 < 576) {
                this.prevblck[ch][j2] = 0.0f;
                ++j2;
            }
            ++ch;
        }
        this.br = new BitReserve();
    }

    @Override
    public void decodeFrame() {
        this.decode();
    }

    public void decode() {
        int nSlots = this.header.slots();
        this.get_side_info();
        int i2 = 0;
        while (i2 < nSlots) {
            this.br.hputbuf(this.stream.get_bits(8));
            ++i2;
        }
        int main_data_end = this.br.hsstell() >>> 3;
        int flush_main = this.br.hsstell() & 7;
        if (flush_main != 0) {
            this.br.hgetbits(8 - flush_main);
            ++main_data_end;
        }
        int bytes_to_discard = this.frame_start - main_data_end - this.si.main_data_begin;
        this.frame_start += nSlots;
        if (bytes_to_discard < 0) {
            return;
        }
        if (main_data_end > 4096) {
            this.frame_start -= 4096;
            this.br.rewindNbytes(4096);
        }
        while (bytes_to_discard > 0) {
            this.br.hgetbits(8);
            --bytes_to_discard;
        }
        int gr2 = 0;
        while (gr2 < this.max_gr) {
            int ch = 0;
            while (ch < this.channels) {
                this.part2_start = this.br.hsstell();
                if (this.header.version() == 1) {
                    this.get_scale_factors(ch, gr2);
                } else {
                    this.get_LSF_scale_factors(ch, gr2);
                }
                this.huffman_decode(ch, gr2);
                this.dequantize_sample(this.ro[ch], ch, gr2);
                ++ch;
            }
            this.stereo(gr2);
            if (this.which_channels == 3 && this.channels > 1) {
                this.do_downmix();
            }
            ch = this.first_channel;
            while (ch <= this.last_channel) {
                int sb2;
                int ss2;
                this.reorder(this.lr[ch], ch, gr2);
                this.antialias(ch, gr2);
                this.hybrid(ch, gr2);
                int sb18 = 18;
                while (sb18 < 576) {
                    ss2 = 1;
                    while (ss2 < 18) {
                        this.out_1d[sb18 + ss2] = -this.out_1d[sb18 + ss2];
                        ss2 += 2;
                    }
                    sb18 += 36;
                }
                if (ch == 0 || this.which_channels == 2) {
                    ss2 = 0;
                    while (ss2 < 18) {
                        sb2 = 0;
                        sb18 = 0;
                        while (sb18 < 576) {
                            this.samples1[sb2] = this.out_1d[sb18 + ss2];
                            ++sb2;
                            sb18 += 18;
                        }
                        this.filter1.input_samples(this.samples1);
                        this.filter1.calculate_pcm_samples(this.buffer);
                        ++ss2;
                    }
                } else {
                    ss2 = 0;
                    while (ss2 < 18) {
                        sb2 = 0;
                        sb18 = 0;
                        while (sb18 < 576) {
                            this.samples2[sb2] = this.out_1d[sb18 + ss2];
                            ++sb2;
                            sb18 += 18;
                        }
                        this.filter2.input_samples(this.samples2);
                        this.filter2.calculate_pcm_samples(this.buffer);
                        ++ss2;
                    }
                }
                ++ch;
            }
            ++gr2;
        }
        ++this.counter;
        this.buffer.write_buffer(1);
    }

    private boolean get_side_info() {
        if (this.header.version() == 1) {
            this.si.main_data_begin = this.stream.get_bits(9);
            this.si.private_bits = this.channels == 1 ? this.stream.get_bits(5) : this.stream.get_bits(3);
            int ch = 0;
            while (ch < this.channels) {
                this.si.ch[ch].scfsi[0] = this.stream.get_bits(1);
                this.si.ch[ch].scfsi[1] = this.stream.get_bits(1);
                this.si.ch[ch].scfsi[2] = this.stream.get_bits(1);
                this.si.ch[ch].scfsi[3] = this.stream.get_bits(1);
                ++ch;
            }
            int gr2 = 0;
            while (gr2 < 2) {
                ch = 0;
                while (ch < this.channels) {
                    this.si.ch[ch].gr[gr2].part2_3_length = this.stream.get_bits(12);
                    this.si.ch[ch].gr[gr2].big_values = this.stream.get_bits(9);
                    this.si.ch[ch].gr[gr2].global_gain = this.stream.get_bits(8);
                    this.si.ch[ch].gr[gr2].scalefac_compress = this.stream.get_bits(4);
                    this.si.ch[ch].gr[gr2].window_switching_flag = this.stream.get_bits(1);
                    if (this.si.ch[ch].gr[gr2].window_switching_flag != 0) {
                        this.si.ch[ch].gr[gr2].block_type = this.stream.get_bits(2);
                        this.si.ch[ch].gr[gr2].mixed_block_flag = this.stream.get_bits(1);
                        this.si.ch[ch].gr[gr2].table_select[0] = this.stream.get_bits(5);
                        this.si.ch[ch].gr[gr2].table_select[1] = this.stream.get_bits(5);
                        this.si.ch[ch].gr[gr2].subblock_gain[0] = this.stream.get_bits(3);
                        this.si.ch[ch].gr[gr2].subblock_gain[1] = this.stream.get_bits(3);
                        this.si.ch[ch].gr[gr2].subblock_gain[2] = this.stream.get_bits(3);
                        if (this.si.ch[ch].gr[gr2].block_type == 0) {
                            return false;
                        }
                        this.si.ch[ch].gr[gr2].region0_count = this.si.ch[ch].gr[gr2].block_type == 2 && this.si.ch[ch].gr[gr2].mixed_block_flag == 0 ? 8 : 7;
                        this.si.ch[ch].gr[gr2].region1_count = 20 - this.si.ch[ch].gr[gr2].region0_count;
                    } else {
                        this.si.ch[ch].gr[gr2].table_select[0] = this.stream.get_bits(5);
                        this.si.ch[ch].gr[gr2].table_select[1] = this.stream.get_bits(5);
                        this.si.ch[ch].gr[gr2].table_select[2] = this.stream.get_bits(5);
                        this.si.ch[ch].gr[gr2].region0_count = this.stream.get_bits(4);
                        this.si.ch[ch].gr[gr2].region1_count = this.stream.get_bits(3);
                        this.si.ch[ch].gr[gr2].block_type = 0;
                    }
                    this.si.ch[ch].gr[gr2].preflag = this.stream.get_bits(1);
                    this.si.ch[ch].gr[gr2].scalefac_scale = this.stream.get_bits(1);
                    this.si.ch[ch].gr[gr2].count1table_select = this.stream.get_bits(1);
                    ++ch;
                }
                ++gr2;
            }
        } else {
            this.si.main_data_begin = this.stream.get_bits(8);
            this.si.private_bits = this.channels == 1 ? this.stream.get_bits(1) : this.stream.get_bits(2);
            int ch = 0;
            while (ch < this.channels) {
                this.si.ch[ch].gr[0].part2_3_length = this.stream.get_bits(12);
                this.si.ch[ch].gr[0].big_values = this.stream.get_bits(9);
                this.si.ch[ch].gr[0].global_gain = this.stream.get_bits(8);
                this.si.ch[ch].gr[0].scalefac_compress = this.stream.get_bits(9);
                this.si.ch[ch].gr[0].window_switching_flag = this.stream.get_bits(1);
                if (this.si.ch[ch].gr[0].window_switching_flag != 0) {
                    this.si.ch[ch].gr[0].block_type = this.stream.get_bits(2);
                    this.si.ch[ch].gr[0].mixed_block_flag = this.stream.get_bits(1);
                    this.si.ch[ch].gr[0].table_select[0] = this.stream.get_bits(5);
                    this.si.ch[ch].gr[0].table_select[1] = this.stream.get_bits(5);
                    this.si.ch[ch].gr[0].subblock_gain[0] = this.stream.get_bits(3);
                    this.si.ch[ch].gr[0].subblock_gain[1] = this.stream.get_bits(3);
                    this.si.ch[ch].gr[0].subblock_gain[2] = this.stream.get_bits(3);
                    if (this.si.ch[ch].gr[0].block_type == 0) {
                        return false;
                    }
                    if (this.si.ch[ch].gr[0].block_type == 2 && this.si.ch[ch].gr[0].mixed_block_flag == 0) {
                        this.si.ch[ch].gr[0].region0_count = 8;
                    } else {
                        this.si.ch[ch].gr[0].region0_count = 7;
                        this.si.ch[ch].gr[0].region1_count = 20 - this.si.ch[ch].gr[0].region0_count;
                    }
                } else {
                    this.si.ch[ch].gr[0].table_select[0] = this.stream.get_bits(5);
                    this.si.ch[ch].gr[0].table_select[1] = this.stream.get_bits(5);
                    this.si.ch[ch].gr[0].table_select[2] = this.stream.get_bits(5);
                    this.si.ch[ch].gr[0].region0_count = this.stream.get_bits(4);
                    this.si.ch[ch].gr[0].region1_count = this.stream.get_bits(3);
                    this.si.ch[ch].gr[0].block_type = 0;
                }
                this.si.ch[ch].gr[0].scalefac_scale = this.stream.get_bits(1);
                this.si.ch[ch].gr[0].count1table_select = this.stream.get_bits(1);
                ++ch;
            }
        }
        return true;
    }

    private void get_scale_factors(int ch, int gr2) {
        gr_info_s gr_info = this.si.ch[ch].gr[gr2];
        int scale_comp = gr_info.scalefac_compress;
        int length0 = slen[0][scale_comp];
        int length1 = slen[1][scale_comp];
        if (gr_info.window_switching_flag != 0 && gr_info.block_type == 2) {
            if (gr_info.mixed_block_flag != 0) {
                int window;
                int sfb = 0;
                while (sfb < 8) {
                    this.scalefac[ch].l[sfb] = this.br.hgetbits(slen[0][gr_info.scalefac_compress]);
                    ++sfb;
                }
                sfb = 3;
                while (sfb < 6) {
                    window = 0;
                    while (window < 3) {
                        this.scalefac[ch].s[window][sfb] = this.br.hgetbits(slen[0][gr_info.scalefac_compress]);
                        ++window;
                    }
                    ++sfb;
                }
                sfb = 6;
                while (sfb < 12) {
                    window = 0;
                    while (window < 3) {
                        this.scalefac[ch].s[window][sfb] = this.br.hgetbits(slen[1][gr_info.scalefac_compress]);
                        ++window;
                    }
                    ++sfb;
                }
                sfb = 12;
                window = 0;
                while (window < 3) {
                    this.scalefac[ch].s[window][sfb] = 0;
                    ++window;
                }
            } else {
                this.scalefac[ch].s[0][0] = this.br.hgetbits(length0);
                this.scalefac[ch].s[1][0] = this.br.hgetbits(length0);
                this.scalefac[ch].s[2][0] = this.br.hgetbits(length0);
                this.scalefac[ch].s[0][1] = this.br.hgetbits(length0);
                this.scalefac[ch].s[1][1] = this.br.hgetbits(length0);
                this.scalefac[ch].s[2][1] = this.br.hgetbits(length0);
                this.scalefac[ch].s[0][2] = this.br.hgetbits(length0);
                this.scalefac[ch].s[1][2] = this.br.hgetbits(length0);
                this.scalefac[ch].s[2][2] = this.br.hgetbits(length0);
                this.scalefac[ch].s[0][3] = this.br.hgetbits(length0);
                this.scalefac[ch].s[1][3] = this.br.hgetbits(length0);
                this.scalefac[ch].s[2][3] = this.br.hgetbits(length0);
                this.scalefac[ch].s[0][4] = this.br.hgetbits(length0);
                this.scalefac[ch].s[1][4] = this.br.hgetbits(length0);
                this.scalefac[ch].s[2][4] = this.br.hgetbits(length0);
                this.scalefac[ch].s[0][5] = this.br.hgetbits(length0);
                this.scalefac[ch].s[1][5] = this.br.hgetbits(length0);
                this.scalefac[ch].s[2][5] = this.br.hgetbits(length0);
                this.scalefac[ch].s[0][6] = this.br.hgetbits(length1);
                this.scalefac[ch].s[1][6] = this.br.hgetbits(length1);
                this.scalefac[ch].s[2][6] = this.br.hgetbits(length1);
                this.scalefac[ch].s[0][7] = this.br.hgetbits(length1);
                this.scalefac[ch].s[1][7] = this.br.hgetbits(length1);
                this.scalefac[ch].s[2][7] = this.br.hgetbits(length1);
                this.scalefac[ch].s[0][8] = this.br.hgetbits(length1);
                this.scalefac[ch].s[1][8] = this.br.hgetbits(length1);
                this.scalefac[ch].s[2][8] = this.br.hgetbits(length1);
                this.scalefac[ch].s[0][9] = this.br.hgetbits(length1);
                this.scalefac[ch].s[1][9] = this.br.hgetbits(length1);
                this.scalefac[ch].s[2][9] = this.br.hgetbits(length1);
                this.scalefac[ch].s[0][10] = this.br.hgetbits(length1);
                this.scalefac[ch].s[1][10] = this.br.hgetbits(length1);
                this.scalefac[ch].s[2][10] = this.br.hgetbits(length1);
                this.scalefac[ch].s[0][11] = this.br.hgetbits(length1);
                this.scalefac[ch].s[1][11] = this.br.hgetbits(length1);
                this.scalefac[ch].s[2][11] = this.br.hgetbits(length1);
                this.scalefac[ch].s[0][12] = 0;
                this.scalefac[ch].s[1][12] = 0;
                this.scalefac[ch].s[2][12] = 0;
            }
        } else {
            if (this.si.ch[ch].scfsi[0] == 0 || gr2 == 0) {
                this.scalefac[ch].l[0] = this.br.hgetbits(length0);
                this.scalefac[ch].l[1] = this.br.hgetbits(length0);
                this.scalefac[ch].l[2] = this.br.hgetbits(length0);
                this.scalefac[ch].l[3] = this.br.hgetbits(length0);
                this.scalefac[ch].l[4] = this.br.hgetbits(length0);
                this.scalefac[ch].l[5] = this.br.hgetbits(length0);
            }
            if (this.si.ch[ch].scfsi[1] == 0 || gr2 == 0) {
                this.scalefac[ch].l[6] = this.br.hgetbits(length0);
                this.scalefac[ch].l[7] = this.br.hgetbits(length0);
                this.scalefac[ch].l[8] = this.br.hgetbits(length0);
                this.scalefac[ch].l[9] = this.br.hgetbits(length0);
                this.scalefac[ch].l[10] = this.br.hgetbits(length0);
            }
            if (this.si.ch[ch].scfsi[2] == 0 || gr2 == 0) {
                this.scalefac[ch].l[11] = this.br.hgetbits(length1);
                this.scalefac[ch].l[12] = this.br.hgetbits(length1);
                this.scalefac[ch].l[13] = this.br.hgetbits(length1);
                this.scalefac[ch].l[14] = this.br.hgetbits(length1);
                this.scalefac[ch].l[15] = this.br.hgetbits(length1);
            }
            if (this.si.ch[ch].scfsi[3] == 0 || gr2 == 0) {
                this.scalefac[ch].l[16] = this.br.hgetbits(length1);
                this.scalefac[ch].l[17] = this.br.hgetbits(length1);
                this.scalefac[ch].l[18] = this.br.hgetbits(length1);
                this.scalefac[ch].l[19] = this.br.hgetbits(length1);
                this.scalefac[ch].l[20] = this.br.hgetbits(length1);
            }
            this.scalefac[ch].l[21] = 0;
            this.scalefac[ch].l[22] = 0;
        }
    }

    private void get_LSF_scale_data(int ch, int gr2) {
        int mode_ext = this.header.mode_extension();
        int blocknumber = 0;
        gr_info_s gr_info = this.si.ch[ch].gr[gr2];
        int scalefac_comp = gr_info.scalefac_compress;
        int blocktypenumber = gr_info.block_type == 2 ? (gr_info.mixed_block_flag == 0 ? 1 : (gr_info.mixed_block_flag == 1 ? 2 : 0)) : 0;
        if (mode_ext != 1 && mode_ext != 3 || ch != 1) {
            if (scalefac_comp < 400) {
                this.new_slen[0] = (scalefac_comp >>> 4) / 5;
                this.new_slen[1] = (scalefac_comp >>> 4) % 5;
                this.new_slen[2] = (scalefac_comp & 0xF) >>> 2;
                this.new_slen[3] = scalefac_comp & 3;
                this.si.ch[ch].gr[gr2].preflag = 0;
                blocknumber = 0;
            } else if (scalefac_comp < 500) {
                this.new_slen[0] = (scalefac_comp - 400 >>> 2) / 5;
                this.new_slen[1] = (scalefac_comp - 400 >>> 2) % 5;
                this.new_slen[2] = scalefac_comp - 400 & 3;
                this.new_slen[3] = 0;
                this.si.ch[ch].gr[gr2].preflag = 0;
                blocknumber = 1;
            } else if (scalefac_comp < 512) {
                this.new_slen[0] = (scalefac_comp - 500) / 3;
                this.new_slen[1] = (scalefac_comp - 500) % 3;
                this.new_slen[2] = 0;
                this.new_slen[3] = 0;
                this.si.ch[ch].gr[gr2].preflag = 1;
                blocknumber = 2;
            }
        }
        if ((mode_ext == 1 || mode_ext == 3) && ch == 1) {
            int int_scalefac_comp = scalefac_comp >>> 1;
            if (int_scalefac_comp < 180) {
                this.new_slen[0] = int_scalefac_comp / 36;
                this.new_slen[1] = int_scalefac_comp % 36 / 6;
                this.new_slen[2] = int_scalefac_comp % 36 % 6;
                this.new_slen[3] = 0;
                this.si.ch[ch].gr[gr2].preflag = 0;
                blocknumber = 3;
            } else if (int_scalefac_comp < 244) {
                this.new_slen[0] = (int_scalefac_comp - 180 & 0x3F) >>> 4;
                this.new_slen[1] = (int_scalefac_comp - 180 & 0xF) >>> 2;
                this.new_slen[2] = int_scalefac_comp - 180 & 3;
                this.new_slen[3] = 0;
                this.si.ch[ch].gr[gr2].preflag = 0;
                blocknumber = 4;
            } else if (int_scalefac_comp < 255) {
                this.new_slen[0] = (int_scalefac_comp - 244) / 3;
                this.new_slen[1] = (int_scalefac_comp - 244) % 3;
                this.new_slen[2] = 0;
                this.new_slen[3] = 0;
                this.si.ch[ch].gr[gr2].preflag = 0;
                blocknumber = 5;
            }
        }
        int x2 = 0;
        while (x2 < 45) {
            this.scalefac_buffer[x2] = 0;
            ++x2;
        }
        int m2 = 0;
        int i2 = 0;
        while (i2 < 4) {
            int j2 = 0;
            while (j2 < nr_of_sfb_block[blocknumber][blocktypenumber][i2]) {
                this.scalefac_buffer[m2] = this.new_slen[i2] == 0 ? 0 : this.br.hgetbits(this.new_slen[i2]);
                ++m2;
                ++j2;
            }
            ++i2;
        }
    }

    private void get_LSF_scale_factors(int ch, int gr2) {
        int m2 = 0;
        gr_info_s gr_info = this.si.ch[ch].gr[gr2];
        this.get_LSF_scale_data(ch, gr2);
        if (gr_info.window_switching_flag != 0 && gr_info.block_type == 2) {
            if (gr_info.mixed_block_flag != 0) {
                int window;
                int sfb = 0;
                while (sfb < 8) {
                    this.scalefac[ch].l[sfb] = this.scalefac_buffer[m2];
                    ++m2;
                    ++sfb;
                }
                sfb = 3;
                while (sfb < 12) {
                    window = 0;
                    while (window < 3) {
                        this.scalefac[ch].s[window][sfb] = this.scalefac_buffer[m2];
                        ++m2;
                        ++window;
                    }
                    ++sfb;
                }
                window = 0;
                while (window < 3) {
                    this.scalefac[ch].s[window][12] = 0;
                    ++window;
                }
            } else {
                int window;
                int sfb = 0;
                while (sfb < 12) {
                    window = 0;
                    while (window < 3) {
                        this.scalefac[ch].s[window][sfb] = this.scalefac_buffer[m2];
                        ++m2;
                        ++window;
                    }
                    ++sfb;
                }
                window = 0;
                while (window < 3) {
                    this.scalefac[ch].s[window][12] = 0;
                    ++window;
                }
            }
        } else {
            int sfb = 0;
            while (sfb < 21) {
                this.scalefac[ch].l[sfb] = this.scalefac_buffer[m2];
                ++m2;
                ++sfb;
            }
            this.scalefac[ch].l[21] = 0;
            this.scalefac[ch].l[22] = 0;
        }
    }

    private void huffman_decode(int ch, int gr2) {
        huffcodetab h2;
        int region2Start;
        int region1Start;
        this.x[0] = 0;
        this.y[0] = 0;
        this.v[0] = 0;
        this.w[0] = 0;
        int part2_3_end = this.part2_start + this.si.ch[ch].gr[gr2].part2_3_length;
        if (this.si.ch[ch].gr[gr2].window_switching_flag != 0 && this.si.ch[ch].gr[gr2].block_type == 2) {
            region1Start = this.sfreq == 8 ? 72 : 36;
            region2Start = 576;
        } else {
            int buf = this.si.ch[ch].gr[gr2].region0_count + 1;
            int buf1 = buf + this.si.ch[ch].gr[gr2].region1_count + 1;
            if (buf1 > this.sfBandIndex[this.sfreq].l.length - 1) {
                buf1 = this.sfBandIndex[this.sfreq].l.length - 1;
            }
            region1Start = this.sfBandIndex[this.sfreq].l[buf];
            region2Start = this.sfBandIndex[this.sfreq].l[buf1];
        }
        int index = 0;
        int i2 = 0;
        while (i2 < this.si.ch[ch].gr[gr2].big_values << 1) {
            h2 = i2 < region1Start ? huffcodetab.ht[this.si.ch[ch].gr[gr2].table_select[0]] : (i2 < region2Start ? huffcodetab.ht[this.si.ch[ch].gr[gr2].table_select[1]] : huffcodetab.ht[this.si.ch[ch].gr[gr2].table_select[2]]);
            huffcodetab.huffman_decoder(h2, this.x, this.y, this.v, this.w, this.br);
            this.is_1d[index++] = this.x[0];
            this.is_1d[index++] = this.y[0];
            this.CheckSumHuff = this.CheckSumHuff + this.x[0] + this.y[0];
            i2 += 2;
        }
        h2 = huffcodetab.ht[this.si.ch[ch].gr[gr2].count1table_select + 32];
        int num_bits = this.br.hsstell();
        while (num_bits < part2_3_end && index < 576) {
            huffcodetab.huffman_decoder(h2, this.x, this.y, this.v, this.w, this.br);
            this.is_1d[index++] = this.v[0];
            this.is_1d[index++] = this.w[0];
            this.is_1d[index++] = this.x[0];
            this.is_1d[index++] = this.y[0];
            this.CheckSumHuff = this.CheckSumHuff + this.v[0] + this.w[0] + this.x[0] + this.y[0];
            num_bits = this.br.hsstell();
        }
        if (num_bits > part2_3_end) {
            this.br.rewindNbits(num_bits - part2_3_end);
            index -= 4;
        }
        if ((num_bits = this.br.hsstell()) < part2_3_end) {
            this.br.hgetbits(part2_3_end - num_bits);
        }
        this.nonzero[ch] = index < 576 ? index : 576;
        if (index < 0) {
            index = 0;
        }
        while (index < 576) {
            this.is_1d[index] = 0;
            ++index;
        }
    }

    private void i_stereo_k_values(int is_pos, int io_type, int i2) {
        if (is_pos == 0) {
            this.k[0][i2] = 1.0f;
            this.k[1][i2] = 1.0f;
        } else if ((is_pos & 1) != 0) {
            this.k[0][i2] = io[io_type][is_pos + 1 >>> 1];
            this.k[1][i2] = 1.0f;
        } else {
            this.k[0][i2] = 1.0f;
            this.k[1][i2] = io[io_type][is_pos >>> 1];
        }
    }

    private void dequantize_sample(float[][] xr2, int ch, int gr2) {
        int quotien;
        int reste;
        int next_cb_boundary;
        gr_info_s gr_info = this.si.ch[ch].gr[gr2];
        int cb2 = 0;
        int cb_begin = 0;
        int cb_width = 0;
        int index = 0;
        float[][] xr_1d = xr2;
        if (gr_info.window_switching_flag != 0 && gr_info.block_type == 2) {
            if (gr_info.mixed_block_flag != 0) {
                next_cb_boundary = this.sfBandIndex[this.sfreq].l[1];
            } else {
                cb_width = this.sfBandIndex[this.sfreq].s[1];
                next_cb_boundary = (cb_width << 2) - cb_width;
                cb_begin = 0;
            }
        } else {
            next_cb_boundary = this.sfBandIndex[this.sfreq].l[1];
        }
        float g_gain = (float)Math.pow(2.0, 0.25 * ((double)gr_info.global_gain - 210.0));
        int j2 = 0;
        while (j2 < this.nonzero[ch]) {
            int abv2;
            reste = j2 % 18;
            quotien = (j2 - reste) / 18;
            xr_1d[quotien][reste] = this.is_1d[j2] == 0 ? 0.0f : ((abv2 = this.is_1d[j2]) < t_43.length ? (this.is_1d[j2] > 0 ? g_gain * t_43[abv2] : (-abv2 < t_43.length ? -g_gain * t_43[-abv2] : -g_gain * (float)Math.pow(-abv2, 1.3333333333333333))) : (this.is_1d[j2] > 0 ? g_gain * (float)Math.pow(abv2, 1.3333333333333333) : -g_gain * (float)Math.pow(-abv2, 1.3333333333333333)));
            ++j2;
        }
        j2 = 0;
        while (j2 < this.nonzero[ch]) {
            int idx;
            reste = j2 % 18;
            quotien = (j2 - reste) / 18;
            if (index == next_cb_boundary) {
                if (gr_info.window_switching_flag != 0 && gr_info.block_type == 2) {
                    if (gr_info.mixed_block_flag != 0) {
                        if (index == this.sfBandIndex[this.sfreq].l[8]) {
                            next_cb_boundary = this.sfBandIndex[this.sfreq].s[4];
                            next_cb_boundary = (next_cb_boundary << 2) - next_cb_boundary;
                            cb2 = 3;
                            cb_width = this.sfBandIndex[this.sfreq].s[4] - this.sfBandIndex[this.sfreq].s[3];
                            cb_begin = this.sfBandIndex[this.sfreq].s[3];
                            cb_begin = (cb_begin << 2) - cb_begin;
                        } else if (index < this.sfBandIndex[this.sfreq].l[8]) {
                            next_cb_boundary = this.sfBandIndex[this.sfreq].l[++cb2 + 1];
                        } else {
                            next_cb_boundary = this.sfBandIndex[this.sfreq].s[++cb2 + 1];
                            next_cb_boundary = (next_cb_boundary << 2) - next_cb_boundary;
                            cb_begin = this.sfBandIndex[this.sfreq].s[cb2];
                            cb_width = this.sfBandIndex[this.sfreq].s[cb2 + 1] - cb_begin;
                            cb_begin = (cb_begin << 2) - cb_begin;
                        }
                    } else {
                        next_cb_boundary = this.sfBandIndex[this.sfreq].s[++cb2 + 1];
                        next_cb_boundary = (next_cb_boundary << 2) - next_cb_boundary;
                        cb_begin = this.sfBandIndex[this.sfreq].s[cb2];
                        cb_width = this.sfBandIndex[this.sfreq].s[cb2 + 1] - cb_begin;
                        cb_begin = (cb_begin << 2) - cb_begin;
                    }
                } else {
                    next_cb_boundary = this.sfBandIndex[this.sfreq].l[++cb2 + 1];
                }
            }
            if (gr_info.window_switching_flag != 0 && (gr_info.block_type == 2 && gr_info.mixed_block_flag == 0 || gr_info.block_type == 2 && gr_info.mixed_block_flag != 0 && j2 >= 36)) {
                int t_index = (index - cb_begin) / cb_width;
                idx = this.scalefac[ch].s[t_index][cb2] << gr_info.scalefac_scale;
                float[] fArray = xr_1d[quotien];
                int n2 = reste;
                fArray[n2] = fArray[n2] * two_to_negative_half_pow[idx += gr_info.subblock_gain[t_index] << 2];
            } else {
                idx = this.scalefac[ch].l[cb2];
                if (gr_info.preflag != 0) {
                    idx += pretab[cb2];
                }
                float[] fArray = xr_1d[quotien];
                int n3 = reste;
                fArray[n3] = fArray[n3] * two_to_negative_half_pow[idx <<= gr_info.scalefac_scale];
            }
            ++index;
            ++j2;
        }
        j2 = this.nonzero[ch];
        while (j2 < 576) {
            reste = j2 % 18;
            quotien = (j2 - reste) / 18;
            if (reste < 0) {
                reste = 0;
            }
            if (quotien < 0) {
                quotien = 0;
            }
            xr_1d[quotien][reste] = 0.0f;
            ++j2;
        }
    }

    private void reorder(float[][] xr2, int ch, int gr2) {
        gr_info_s gr_info = this.si.ch[ch].gr[gr2];
        float[][] xr_1d = xr2;
        if (gr_info.window_switching_flag != 0 && gr_info.block_type == 2) {
            int index = 0;
            while (index < 576) {
                this.out_1d[index] = 0.0f;
                ++index;
            }
            if (gr_info.mixed_block_flag != 0) {
                index = 0;
                while (index < 36) {
                    int reste = index % 18;
                    int quotien = (index - reste) / 18;
                    this.out_1d[index] = xr_1d[quotien][reste];
                    ++index;
                }
                int sfb = 3;
                while (sfb < 13) {
                    int sfb_start = this.sfBandIndex[this.sfreq].s[sfb];
                    int sfb_lines = this.sfBandIndex[this.sfreq].s[sfb + 1] - sfb_start;
                    int sfb_start3 = (sfb_start << 2) - sfb_start;
                    int freq = 0;
                    int freq3 = 0;
                    while (freq < sfb_lines) {
                        int src_line = sfb_start3 + freq;
                        int des_line = sfb_start3 + freq3;
                        int reste = src_line % 18;
                        int quotien = (src_line - reste) / 18;
                        this.out_1d[des_line] = xr_1d[quotien][reste];
                        reste = (src_line += sfb_lines) % 18;
                        quotien = (src_line - reste) / 18;
                        this.out_1d[++des_line] = xr_1d[quotien][reste];
                        reste = (src_line += sfb_lines) % 18;
                        quotien = (src_line - reste) / 18;
                        this.out_1d[++des_line] = xr_1d[quotien][reste];
                        ++freq;
                        freq3 += 3;
                    }
                    ++sfb;
                }
            } else {
                index = 0;
                while (index < 576) {
                    int j2 = reorder_table[this.sfreq][index];
                    int reste = j2 % 18;
                    int quotien = (j2 - reste) / 18;
                    this.out_1d[index] = xr_1d[quotien][reste];
                    ++index;
                }
            }
        } else {
            int index = 0;
            while (index < 576) {
                int reste = index % 18;
                int quotien = (index - reste) / 18;
                this.out_1d[index] = xr_1d[quotien][reste];
                ++index;
            }
        }
    }

    private void stereo(int gr2) {
        if (this.channels == 1) {
            int sb2 = 0;
            while (sb2 < 32) {
                int ss2 = 0;
                while (ss2 < 18) {
                    this.lr[0][sb2][ss2] = this.ro[0][sb2][ss2];
                    this.lr[0][sb2][ss2 + 1] = this.ro[0][sb2][ss2 + 1];
                    this.lr[0][sb2][ss2 + 2] = this.ro[0][sb2][ss2 + 2];
                    ss2 += 3;
                }
                ++sb2;
            }
        } else {
            int ss3;
            int sb3;
            gr_info_s gr_info = this.si.ch[0].gr[gr2];
            int mode_ext = this.header.mode_extension();
            boolean ms_stereo = this.header.mode() == 1 && (mode_ext & 2) != 0;
            boolean i_stereo = this.header.mode() == 1 && (mode_ext & 1) != 0;
            boolean lsf = this.header.version() == 0 || this.header.version() == 2;
            int io_type = gr_info.scalefac_compress & 1;
            int i2 = 0;
            while (i2 < 576) {
                this.is_pos[i2] = 7;
                this.is_ratio[i2] = 0.0f;
                ++i2;
            }
            if (i_stereo) {
                int sfb;
                if (gr_info.window_switching_flag != 0 && gr_info.block_type == 2) {
                    int temp;
                    int lines;
                    if (gr_info.mixed_block_flag != 0) {
                        int max_sfb = 0;
                        int j2 = 0;
                        while (j2 < 3) {
                            int sfbcnt = 2;
                            sfb = 12;
                            while (sfb >= 3) {
                                i2 = this.sfBandIndex[this.sfreq].s[sfb];
                                lines = this.sfBandIndex[this.sfreq].s[sfb + 1] - i2;
                                i2 = (i2 << 2) - i2 + (j2 + 1) * lines - 1;
                                while (lines > 0) {
                                    if (this.ro[1][i2 / 18][i2 % 18] != 0.0f) {
                                        sfbcnt = sfb;
                                        sfb = -10;
                                        lines = -10;
                                    }
                                    --lines;
                                    --i2;
                                }
                                --sfb;
                            }
                            sfb = sfbcnt + 1;
                            if (sfb > max_sfb) {
                                max_sfb = sfb;
                            }
                            while (sfb < 12) {
                                temp = this.sfBandIndex[this.sfreq].s[sfb];
                                sb3 = this.sfBandIndex[this.sfreq].s[sfb + 1] - temp;
                                i2 = (temp << 2) - temp + j2 * sb3;
                                while (sb3 > 0) {
                                    this.is_pos[i2] = this.scalefac[1].s[j2][sfb];
                                    if (this.is_pos[i2] != 7) {
                                        if (lsf) {
                                            this.i_stereo_k_values(this.is_pos[i2], io_type, i2);
                                        } else {
                                            this.is_ratio[i2] = TAN12[this.is_pos[i2]];
                                        }
                                    }
                                    ++i2;
                                    --sb3;
                                }
                                ++sfb;
                            }
                            sfb = this.sfBandIndex[this.sfreq].s[10];
                            sb3 = this.sfBandIndex[this.sfreq].s[11] - sfb;
                            sfb = (sfb << 2) - sfb + j2 * sb3;
                            temp = this.sfBandIndex[this.sfreq].s[11];
                            sb3 = this.sfBandIndex[this.sfreq].s[12] - temp;
                            i2 = (temp << 2) - temp + j2 * sb3;
                            while (sb3 > 0) {
                                this.is_pos[i2] = this.is_pos[sfb];
                                if (lsf) {
                                    this.k[0][i2] = this.k[0][sfb];
                                    this.k[1][i2] = this.k[1][sfb];
                                } else {
                                    this.is_ratio[i2] = this.is_ratio[sfb];
                                }
                                ++i2;
                                --sb3;
                            }
                            ++j2;
                        }
                        if (max_sfb <= 3) {
                            i2 = 2;
                            ss3 = 17;
                            sb3 = -1;
                            while (i2 >= 0) {
                                if (this.ro[1][i2][ss3] != 0.0f) {
                                    sb3 = (i2 << 4) + (i2 << 1) + ss3;
                                    i2 = -1;
                                    continue;
                                }
                                if (--ss3 >= 0) continue;
                                --i2;
                                ss3 = 17;
                            }
                            i2 = 0;
                            while (this.sfBandIndex[this.sfreq].l[i2] <= sb3) {
                                ++i2;
                            }
                            sfb = i2;
                            i2 = this.sfBandIndex[this.sfreq].l[i2];
                            while (sfb < 8) {
                                sb3 = this.sfBandIndex[this.sfreq].l[sfb + 1] - this.sfBandIndex[this.sfreq].l[sfb];
                                while (sb3 > 0) {
                                    this.is_pos[i2] = this.scalefac[1].l[sfb];
                                    if (this.is_pos[i2] != 7) {
                                        if (lsf) {
                                            this.i_stereo_k_values(this.is_pos[i2], io_type, i2);
                                        } else {
                                            this.is_ratio[i2] = TAN12[this.is_pos[i2]];
                                        }
                                    }
                                    ++i2;
                                    --sb3;
                                }
                                ++sfb;
                            }
                        }
                    } else {
                        int j3 = 0;
                        while (j3 < 3) {
                            int sfbcnt = -1;
                            sfb = 12;
                            while (sfb >= 0) {
                                temp = this.sfBandIndex[this.sfreq].s[sfb];
                                lines = this.sfBandIndex[this.sfreq].s[sfb + 1] - temp;
                                i2 = (temp << 2) - temp + (j3 + 1) * lines - 1;
                                while (lines > 0) {
                                    if (this.ro[1][i2 / 18][i2 % 18] != 0.0f) {
                                        sfbcnt = sfb;
                                        sfb = -10;
                                        lines = -10;
                                    }
                                    --lines;
                                    --i2;
                                }
                                --sfb;
                            }
                            sfb = sfbcnt + 1;
                            while (sfb < 12) {
                                temp = this.sfBandIndex[this.sfreq].s[sfb];
                                sb3 = this.sfBandIndex[this.sfreq].s[sfb + 1] - temp;
                                i2 = (temp << 2) - temp + j3 * sb3;
                                while (sb3 > 0) {
                                    this.is_pos[i2] = this.scalefac[1].s[j3][sfb];
                                    if (this.is_pos[i2] != 7) {
                                        if (lsf) {
                                            this.i_stereo_k_values(this.is_pos[i2], io_type, i2);
                                        } else {
                                            this.is_ratio[i2] = TAN12[this.is_pos[i2]];
                                        }
                                    }
                                    ++i2;
                                    --sb3;
                                }
                                ++sfb;
                            }
                            temp = this.sfBandIndex[this.sfreq].s[10];
                            int temp2 = this.sfBandIndex[this.sfreq].s[11];
                            sb3 = temp2 - temp;
                            sfb = (temp << 2) - temp + j3 * sb3;
                            sb3 = this.sfBandIndex[this.sfreq].s[12] - temp2;
                            i2 = (temp2 << 2) - temp2 + j3 * sb3;
                            while (sb3 > 0) {
                                this.is_pos[i2] = this.is_pos[sfb];
                                if (lsf) {
                                    this.k[0][i2] = this.k[0][sfb];
                                    this.k[1][i2] = this.k[1][sfb];
                                } else {
                                    this.is_ratio[i2] = this.is_ratio[sfb];
                                }
                                ++i2;
                                --sb3;
                            }
                            ++j3;
                        }
                    }
                } else {
                    i2 = 31;
                    ss3 = 17;
                    sb3 = 0;
                    while (i2 >= 0) {
                        if (this.ro[1][i2][ss3] != 0.0f) {
                            sb3 = (i2 << 4) + (i2 << 1) + ss3;
                            i2 = -1;
                            continue;
                        }
                        if (--ss3 >= 0) continue;
                        --i2;
                        ss3 = 17;
                    }
                    i2 = 0;
                    while (this.sfBandIndex[this.sfreq].l[i2] <= sb3) {
                        ++i2;
                    }
                    sfb = i2;
                    i2 = this.sfBandIndex[this.sfreq].l[i2];
                    while (sfb < 21) {
                        sb3 = this.sfBandIndex[this.sfreq].l[sfb + 1] - this.sfBandIndex[this.sfreq].l[sfb];
                        while (sb3 > 0) {
                            this.is_pos[i2] = this.scalefac[1].l[sfb];
                            if (this.is_pos[i2] != 7) {
                                if (lsf) {
                                    this.i_stereo_k_values(this.is_pos[i2], io_type, i2);
                                } else {
                                    this.is_ratio[i2] = TAN12[this.is_pos[i2]];
                                }
                            }
                            ++i2;
                            --sb3;
                        }
                        ++sfb;
                    }
                    sfb = this.sfBandIndex[this.sfreq].l[20];
                    sb3 = 576 - this.sfBandIndex[this.sfreq].l[21];
                    while (sb3 > 0 && i2 < 576) {
                        this.is_pos[i2] = this.is_pos[sfb];
                        if (lsf) {
                            this.k[0][i2] = this.k[0][sfb];
                            this.k[1][i2] = this.k[1][sfb];
                        } else {
                            this.is_ratio[i2] = this.is_ratio[sfb];
                        }
                        ++i2;
                        --sb3;
                    }
                }
            }
            i2 = 0;
            sb3 = 0;
            while (sb3 < 32) {
                ss3 = 0;
                while (ss3 < 18) {
                    if (this.is_pos[i2] == 7) {
                        if (ms_stereo) {
                            this.lr[0][sb3][ss3] = (this.ro[0][sb3][ss3] + this.ro[1][sb3][ss3]) * 0.70710677f;
                            this.lr[1][sb3][ss3] = (this.ro[0][sb3][ss3] - this.ro[1][sb3][ss3]) * 0.70710677f;
                        } else {
                            this.lr[0][sb3][ss3] = this.ro[0][sb3][ss3];
                            this.lr[1][sb3][ss3] = this.ro[1][sb3][ss3];
                        }
                    } else if (i_stereo) {
                        if (lsf) {
                            this.lr[0][sb3][ss3] = this.ro[0][sb3][ss3] * this.k[0][i2];
                            this.lr[1][sb3][ss3] = this.ro[0][sb3][ss3] * this.k[1][i2];
                        } else {
                            this.lr[1][sb3][ss3] = this.ro[0][sb3][ss3] / (1.0f + this.is_ratio[i2]);
                            this.lr[0][sb3][ss3] = this.lr[1][sb3][ss3] * this.is_ratio[i2];
                        }
                    }
                    ++i2;
                    ++ss3;
                }
                ++sb3;
            }
        }
    }

    private void antialias(int ch, int gr2) {
        gr_info_s gr_info = this.si.ch[ch].gr[gr2];
        if (gr_info.window_switching_flag != 0 && gr_info.block_type == 2 && gr_info.mixed_block_flag == 0) {
            return;
        }
        int sb18lim = gr_info.window_switching_flag != 0 && gr_info.mixed_block_flag != 0 && gr_info.block_type == 2 ? 18 : 558;
        int sb18 = 0;
        while (sb18 < sb18lim) {
            int ss2 = 0;
            while (ss2 < 8) {
                int src_idx1 = sb18 + 17 - ss2;
                int src_idx2 = sb18 + 18 + ss2;
                float bu2 = this.out_1d[src_idx1];
                float bd2 = this.out_1d[src_idx2];
                this.out_1d[src_idx1] = bu2 * cs[ss2] - bd2 * ca[ss2];
                this.out_1d[src_idx2] = bd2 * cs[ss2] + bu2 * ca[ss2];
                ++ss2;
            }
            sb18 += 18;
        }
    }

    private void hybrid(int ch, int gr2) {
        gr_info_s gr_info = this.si.ch[ch].gr[gr2];
        int sb18 = 0;
        while (sb18 < 576) {
            int bt2 = gr_info.window_switching_flag != 0 && gr_info.mixed_block_flag != 0 && sb18 < 36 ? 0 : gr_info.block_type;
            float[] tsOut = this.out_1d;
            int cc2 = 0;
            while (cc2 < 18) {
                this.tsOutCopy[cc2] = tsOut[cc2 + sb18];
                ++cc2;
            }
            this.inv_mdct(this.tsOutCopy, this.rawout, bt2);
            cc2 = 0;
            while (cc2 < 18) {
                tsOut[cc2 + sb18] = this.tsOutCopy[cc2];
                ++cc2;
            }
            float[][] prvblk = this.prevblck;
            tsOut[0 + sb18] = this.rawout[0] + prvblk[ch][sb18 + 0];
            prvblk[ch][sb18 + 0] = this.rawout[18];
            tsOut[1 + sb18] = this.rawout[1] + prvblk[ch][sb18 + 1];
            prvblk[ch][sb18 + 1] = this.rawout[19];
            tsOut[2 + sb18] = this.rawout[2] + prvblk[ch][sb18 + 2];
            prvblk[ch][sb18 + 2] = this.rawout[20];
            tsOut[3 + sb18] = this.rawout[3] + prvblk[ch][sb18 + 3];
            prvblk[ch][sb18 + 3] = this.rawout[21];
            tsOut[4 + sb18] = this.rawout[4] + prvblk[ch][sb18 + 4];
            prvblk[ch][sb18 + 4] = this.rawout[22];
            tsOut[5 + sb18] = this.rawout[5] + prvblk[ch][sb18 + 5];
            prvblk[ch][sb18 + 5] = this.rawout[23];
            tsOut[6 + sb18] = this.rawout[6] + prvblk[ch][sb18 + 6];
            prvblk[ch][sb18 + 6] = this.rawout[24];
            tsOut[7 + sb18] = this.rawout[7] + prvblk[ch][sb18 + 7];
            prvblk[ch][sb18 + 7] = this.rawout[25];
            tsOut[8 + sb18] = this.rawout[8] + prvblk[ch][sb18 + 8];
            prvblk[ch][sb18 + 8] = this.rawout[26];
            tsOut[9 + sb18] = this.rawout[9] + prvblk[ch][sb18 + 9];
            prvblk[ch][sb18 + 9] = this.rawout[27];
            tsOut[10 + sb18] = this.rawout[10] + prvblk[ch][sb18 + 10];
            prvblk[ch][sb18 + 10] = this.rawout[28];
            tsOut[11 + sb18] = this.rawout[11] + prvblk[ch][sb18 + 11];
            prvblk[ch][sb18 + 11] = this.rawout[29];
            tsOut[12 + sb18] = this.rawout[12] + prvblk[ch][sb18 + 12];
            prvblk[ch][sb18 + 12] = this.rawout[30];
            tsOut[13 + sb18] = this.rawout[13] + prvblk[ch][sb18 + 13];
            prvblk[ch][sb18 + 13] = this.rawout[31];
            tsOut[14 + sb18] = this.rawout[14] + prvblk[ch][sb18 + 14];
            prvblk[ch][sb18 + 14] = this.rawout[32];
            tsOut[15 + sb18] = this.rawout[15] + prvblk[ch][sb18 + 15];
            prvblk[ch][sb18 + 15] = this.rawout[33];
            tsOut[16 + sb18] = this.rawout[16] + prvblk[ch][sb18 + 16];
            prvblk[ch][sb18 + 16] = this.rawout[34];
            tsOut[17 + sb18] = this.rawout[17] + prvblk[ch][sb18 + 17];
            prvblk[ch][sb18 + 17] = this.rawout[35];
            sb18 += 18;
        }
    }

    private void do_downmix() {
        int sb2 = 0;
        while (sb2 < 18) {
            int ss2 = 0;
            while (ss2 < 18) {
                this.lr[0][sb2][ss2] = (this.lr[0][sb2][ss2] + this.lr[1][sb2][ss2]) * 0.5f;
                this.lr[0][sb2][ss2 + 1] = (this.lr[0][sb2][ss2 + 1] + this.lr[1][sb2][ss2 + 1]) * 0.5f;
                this.lr[0][sb2][ss2 + 2] = (this.lr[0][sb2][ss2 + 2] + this.lr[1][sb2][ss2 + 2]) * 0.5f;
                ss2 += 3;
            }
            ++sb2;
        }
    }

    public void inv_mdct(float[] in2, float[] out, int block_type) {
        float tmpf_17 = 0.0f;
        float tmpf_16 = 0.0f;
        float tmpf_15 = 0.0f;
        float tmpf_14 = 0.0f;
        float tmpf_13 = 0.0f;
        float tmpf_12 = 0.0f;
        float tmpf_11 = 0.0f;
        float tmpf_10 = 0.0f;
        float tmpf_9 = 0.0f;
        float tmpf_8 = 0.0f;
        float tmpf_7 = 0.0f;
        float tmpf_6 = 0.0f;
        float tmpf_5 = 0.0f;
        float tmpf_4 = 0.0f;
        float tmpf_3 = 0.0f;
        float tmpf_2 = 0.0f;
        float tmpf_1 = 0.0f;
        float tmpf_0 = 0.0f;
        if (block_type == 2) {
            out[0] = 0.0f;
            out[1] = 0.0f;
            out[2] = 0.0f;
            out[3] = 0.0f;
            out[4] = 0.0f;
            out[5] = 0.0f;
            out[6] = 0.0f;
            out[7] = 0.0f;
            out[8] = 0.0f;
            out[9] = 0.0f;
            out[10] = 0.0f;
            out[11] = 0.0f;
            out[12] = 0.0f;
            out[13] = 0.0f;
            out[14] = 0.0f;
            out[15] = 0.0f;
            out[16] = 0.0f;
            out[17] = 0.0f;
            out[18] = 0.0f;
            out[19] = 0.0f;
            out[20] = 0.0f;
            out[21] = 0.0f;
            out[22] = 0.0f;
            out[23] = 0.0f;
            out[24] = 0.0f;
            out[25] = 0.0f;
            out[26] = 0.0f;
            out[27] = 0.0f;
            out[28] = 0.0f;
            out[29] = 0.0f;
            out[30] = 0.0f;
            out[31] = 0.0f;
            out[32] = 0.0f;
            out[33] = 0.0f;
            out[34] = 0.0f;
            out[35] = 0.0f;
            int six_i = 0;
            int i2 = 0;
            while (i2 < 3) {
                int n2 = 15 + i2;
                in2[n2] = in2[n2] + in2[12 + i2];
                int n3 = 12 + i2;
                in2[n3] = in2[n3] + in2[9 + i2];
                int n4 = 9 + i2;
                in2[n4] = in2[n4] + in2[6 + i2];
                int n5 = 6 + i2;
                in2[n5] = in2[n5] + in2[3 + i2];
                int n6 = 3 + i2;
                in2[n6] = in2[n6] + in2[0 + i2];
                int n7 = 15 + i2;
                in2[n7] = in2[n7] + in2[9 + i2];
                int n8 = 9 + i2;
                in2[n8] = in2[n8] + in2[3 + i2];
                float pp2 = in2[12 + i2] * 0.5f;
                float pp1 = in2[6 + i2] * 0.8660254f;
                float sum = in2[0 + i2] + pp2;
                tmpf_1 = in2[0 + i2] - in2[12 + i2];
                tmpf_0 = sum + pp1;
                tmpf_2 = sum - pp1;
                pp2 = in2[15 + i2] * 0.5f;
                pp1 = in2[9 + i2] * 0.8660254f;
                sum = in2[3 + i2] + pp2;
                tmpf_4 = in2[3 + i2] - in2[15 + i2];
                tmpf_5 = sum + pp1;
                tmpf_3 = sum - pp1;
                tmpf_3 *= 1.9318516f;
                float save = tmpf_0;
                tmpf_0 += (tmpf_5 *= 0.5176381f);
                tmpf_5 = save - tmpf_5;
                save = tmpf_1;
                tmpf_1 += (tmpf_4 *= 0.70710677f);
                tmpf_4 = save - tmpf_4;
                save = tmpf_2;
                tmpf_2 += tmpf_3;
                tmpf_3 = save - tmpf_3;
                tmpf_0 *= 0.5043145f;
                tmpf_1 *= 0.5411961f;
                tmpf_2 *= 0.6302362f;
                tmpf_3 *= 0.8213398f;
                tmpf_4 *= 1.306563f;
                tmpf_5 *= 3.830649f;
                tmpf_8 = -tmpf_0 * 0.7933533f;
                tmpf_9 = -tmpf_0 * 0.6087614f;
                tmpf_7 = -tmpf_1 * 0.9238795f;
                tmpf_10 = -tmpf_1 * 0.38268343f;
                tmpf_6 = -tmpf_2 * 0.9914449f;
                tmpf_11 = -tmpf_2 * 0.13052619f;
                tmpf_0 = tmpf_3;
                tmpf_1 = tmpf_4 * 0.38268343f;
                tmpf_2 = tmpf_5 * 0.6087614f;
                tmpf_3 = -tmpf_5 * 0.7933533f;
                tmpf_4 = -tmpf_4 * 0.9238795f;
                tmpf_5 = -tmpf_0 * 0.9914449f;
                int n9 = six_i + 6;
                out[n9] = out[n9] + (tmpf_0 *= 0.13052619f);
                int n10 = six_i + 7;
                out[n10] = out[n10] + tmpf_1;
                int n11 = six_i + 8;
                out[n11] = out[n11] + tmpf_2;
                int n12 = six_i + 9;
                out[n12] = out[n12] + tmpf_3;
                int n13 = six_i + 10;
                out[n13] = out[n13] + tmpf_4;
                int n14 = six_i + 11;
                out[n14] = out[n14] + tmpf_5;
                int n15 = six_i + 12;
                out[n15] = out[n15] + tmpf_6;
                int n16 = six_i + 13;
                out[n16] = out[n16] + tmpf_7;
                int n17 = six_i + 14;
                out[n17] = out[n17] + tmpf_8;
                int n18 = six_i + 15;
                out[n18] = out[n18] + tmpf_9;
                int n19 = six_i + 16;
                out[n19] = out[n19] + tmpf_10;
                int n20 = six_i + 17;
                out[n20] = out[n20] + tmpf_11;
                six_i += 6;
                ++i2;
            }
        } else {
            in2[17] = in2[17] + in2[16];
            in2[16] = in2[16] + in2[15];
            in2[15] = in2[15] + in2[14];
            in2[14] = in2[14] + in2[13];
            in2[13] = in2[13] + in2[12];
            in2[12] = in2[12] + in2[11];
            in2[11] = in2[11] + in2[10];
            in2[10] = in2[10] + in2[9];
            in2[9] = in2[9] + in2[8];
            in2[8] = in2[8] + in2[7];
            in2[7] = in2[7] + in2[6];
            in2[6] = in2[6] + in2[5];
            in2[5] = in2[5] + in2[4];
            in2[4] = in2[4] + in2[3];
            in2[3] = in2[3] + in2[2];
            in2[2] = in2[2] + in2[1];
            in2[1] = in2[1] + in2[0];
            in2[17] = in2[17] + in2[15];
            in2[15] = in2[15] + in2[13];
            in2[13] = in2[13] + in2[11];
            in2[11] = in2[11] + in2[9];
            in2[9] = in2[9] + in2[7];
            in2[7] = in2[7] + in2[5];
            in2[5] = in2[5] + in2[3];
            in2[3] = in2[3] + in2[1];
            float i00 = in2[0] + in2[0];
            float iip12 = i00 + in2[12];
            float tmp0 = iip12 + in2[4] * 1.8793852f + in2[8] * 1.5320889f + in2[16] * 0.34729636f;
            float tmp1 = i00 + in2[4] - in2[8] - in2[12] - in2[12] - in2[16];
            float tmp2 = iip12 - in2[4] * 0.34729636f - in2[8] * 1.8793852f + in2[16] * 1.5320889f;
            float tmp3 = iip12 - in2[4] * 1.5320889f + in2[8] * 0.34729636f - in2[16] * 1.8793852f;
            float tmp4 = in2[0] - in2[4] + in2[8] - in2[12] + in2[16];
            float i66_ = in2[6] * 1.7320508f;
            float tmp0_ = in2[2] * 1.9696155f + i66_ + in2[10] * 1.2855753f + in2[14] * 0.6840403f;
            float tmp1_ = (in2[2] - in2[10] - in2[14]) * 1.7320508f;
            float tmp2_ = in2[2] * 1.2855753f - i66_ - in2[10] * 0.6840403f + in2[14] * 1.9696155f;
            float tmp3_ = in2[2] * 0.6840403f - i66_ + in2[10] * 1.9696155f - in2[14] * 1.2855753f;
            float i0 = in2[1] + in2[1];
            float i0p12 = i0 + in2[13];
            float tmp0o = i0p12 + in2[5] * 1.8793852f + in2[9] * 1.5320889f + in2[17] * 0.34729636f;
            float tmp1o = i0 + in2[5] - in2[9] - in2[13] - in2[13] - in2[17];
            float tmp2o = i0p12 - in2[5] * 0.34729636f - in2[9] * 1.8793852f + in2[17] * 1.5320889f;
            float tmp3o = i0p12 - in2[5] * 1.5320889f + in2[9] * 0.34729636f - in2[17] * 1.8793852f;
            float tmp4o = (in2[1] - in2[5] + in2[9] - in2[13] + in2[17]) * 0.70710677f;
            float i6_ = in2[7] * 1.7320508f;
            float tmp0_o = in2[3] * 1.9696155f + i6_ + in2[11] * 1.2855753f + in2[15] * 0.6840403f;
            float tmp1_o = (in2[3] - in2[11] - in2[15]) * 1.7320508f;
            float tmp2_o = in2[3] * 1.2855753f - i6_ - in2[11] * 0.6840403f + in2[15] * 1.9696155f;
            float tmp3_o = in2[3] * 0.6840403f - i6_ + in2[11] * 1.9696155f - in2[15] * 1.2855753f;
            float e2 = tmp0 + tmp0_;
            float o2 = (tmp0o + tmp0_o) * 0.5019099f;
            tmpf_0 = e2 + o2;
            tmpf_17 = e2 - o2;
            e2 = tmp1 + tmp1_;
            o2 = (tmp1o + tmp1_o) * 0.5176381f;
            tmpf_1 = e2 + o2;
            tmpf_16 = e2 - o2;
            e2 = tmp2 + tmp2_;
            o2 = (tmp2o + tmp2_o) * 0.55168897f;
            tmpf_2 = e2 + o2;
            tmpf_15 = e2 - o2;
            e2 = tmp3 + tmp3_;
            o2 = (tmp3o + tmp3_o) * 0.61038727f;
            tmpf_3 = e2 + o2;
            tmpf_14 = e2 - o2;
            tmpf_4 = tmp4 + tmp4o;
            tmpf_13 = tmp4 - tmp4o;
            e2 = tmp3 - tmp3_;
            o2 = (tmp3o - tmp3_o) * 0.8717234f;
            tmpf_5 = e2 + o2;
            tmpf_12 = e2 - o2;
            e2 = tmp2 - tmp2_;
            o2 = (tmp2o - tmp2_o) * 1.1831008f;
            tmpf_6 = e2 + o2;
            tmpf_11 = e2 - o2;
            e2 = tmp1 - tmp1_;
            o2 = (tmp1o - tmp1_o) * 1.9318516f;
            tmpf_7 = e2 + o2;
            tmpf_10 = e2 - o2;
            e2 = tmp0 - tmp0_;
            o2 = (tmp0o - tmp0_o) * 5.7368565f;
            tmpf_8 = e2 + o2;
            tmpf_9 = e2 - o2;
            float[] win_bt = win[block_type];
            out[0] = -tmpf_9 * win_bt[0];
            out[1] = -tmpf_10 * win_bt[1];
            out[2] = -tmpf_11 * win_bt[2];
            out[3] = -tmpf_12 * win_bt[3];
            out[4] = -tmpf_13 * win_bt[4];
            out[5] = -tmpf_14 * win_bt[5];
            out[6] = -tmpf_15 * win_bt[6];
            out[7] = -tmpf_16 * win_bt[7];
            out[8] = -tmpf_17 * win_bt[8];
            out[9] = tmpf_17 * win_bt[9];
            out[10] = tmpf_16 * win_bt[10];
            out[11] = tmpf_15 * win_bt[11];
            out[12] = tmpf_14 * win_bt[12];
            out[13] = tmpf_13 * win_bt[13];
            out[14] = tmpf_12 * win_bt[14];
            out[15] = tmpf_11 * win_bt[15];
            out[16] = tmpf_10 * win_bt[16];
            out[17] = tmpf_9 * win_bt[17];
            out[18] = tmpf_8 * win_bt[18];
            out[19] = tmpf_7 * win_bt[19];
            out[20] = tmpf_6 * win_bt[20];
            out[21] = tmpf_5 * win_bt[21];
            out[22] = tmpf_4 * win_bt[22];
            out[23] = tmpf_3 * win_bt[23];
            out[24] = tmpf_2 * win_bt[24];
            out[25] = tmpf_1 * win_bt[25];
            out[26] = tmpf_0 * win_bt[26];
            out[27] = tmpf_0 * win_bt[27];
            out[28] = tmpf_1 * win_bt[28];
            out[29] = tmpf_2 * win_bt[29];
            out[30] = tmpf_3 * win_bt[30];
            out[31] = tmpf_4 * win_bt[31];
            out[32] = tmpf_5 * win_bt[32];
            out[33] = tmpf_6 * win_bt[33];
            out[34] = tmpf_7 * win_bt[34];
            out[35] = tmpf_8 * win_bt[35];
        }
    }

    private static float[] create_t_43() {
        float[] t43 = new float[8192];
        double d43 = 1.3333333333333333;
        int i2 = 0;
        while (i2 < 8192) {
            t43[i2] = (float)Math.pow(i2, 1.3333333333333333);
            ++i2;
        }
        return t43;
    }

    static int[] reorder(int[] scalefac_band) {
        int j2 = 0;
        int[] ix2 = new int[576];
        int sfb = 0;
        while (sfb < 13) {
            int start = scalefac_band[sfb];
            int end = scalefac_band[sfb + 1];
            int window = 0;
            while (window < 3) {
                int i2 = start;
                while (i2 < end) {
                    ix2[3 * i2 + window] = j2++;
                    ++i2;
                }
                ++window;
            }
            ++sfb;
        }
        return ix2;
    }

    static class III_side_info_t {
        public int main_data_begin = 0;
        public int private_bits = 0;
        public temporaire[] ch = new temporaire[2];

        public III_side_info_t() {
            this.ch[0] = new temporaire();
            this.ch[1] = new temporaire();
        }
    }

    static class SBI {
        public int[] l;
        public int[] s;

        public SBI() {
            this.l = new int[23];
            this.s = new int[14];
        }

        public SBI(int[] thel, int[] thes) {
            this.l = thel;
            this.s = thes;
        }
    }

    class Sftable {
        public int[] l;
        public int[] s;

        public Sftable() {
            this.l = new int[5];
            this.s = new int[3];
        }

        public Sftable(int[] thel, int[] thes) {
            this.l = thel;
            this.s = thes;
        }
    }

    static class gr_info_s {
        public int part2_3_length = 0;
        public int big_values = 0;
        public int global_gain = 0;
        public int scalefac_compress = 0;
        public int window_switching_flag = 0;
        public int block_type = 0;
        public int mixed_block_flag = 0;
        public int[] table_select = new int[3];
        public int[] subblock_gain = new int[3];
        public int region0_count = 0;
        public int region1_count = 0;
        public int preflag = 0;
        public int scalefac_scale = 0;
        public int count1table_select = 0;
    }

    static class temporaire {
        public int[] scfsi = new int[4];
        public gr_info_s[] gr = new gr_info_s[2];

        public temporaire() {
            this.gr[0] = new gr_info_s();
            this.gr[1] = new gr_info_s();
        }
    }

    static class temporaire2 {
        public int[] l = new int[23];
        public int[][] s = new int[3][13];
    }
}

