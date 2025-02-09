/*
 * Decompiled with CFR 0.152.
 */
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.CodeBook;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.FuncFloor;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.InfoMode;
import com.jcraft.jorbis.Util;

class Floor1
extends FuncFloor {
    static final int floor1_rangedb = 140;
    static final int VIF_POSIT = 63;
    private static float[] FLOOR_fromdB_LOOKUP = new float[]{1.0649863E-7f, 1.1341951E-7f, 1.2079015E-7f, 1.2863978E-7f, 1.369995E-7f, 1.459025E-7f, 1.5538409E-7f, 1.6548181E-7f, 1.7623574E-7f, 1.8768856E-7f, 1.998856E-7f, 2.128753E-7f, 2.2670913E-7f, 2.4144197E-7f, 2.5713223E-7f, 2.7384212E-7f, 2.9163792E-7f, 3.1059022E-7f, 3.307741E-7f, 3.5226967E-7f, 3.7516213E-7f, 3.995423E-7f, 4.255068E-7f, 4.5315863E-7f, 4.8260745E-7f, 5.1397E-7f, 5.4737063E-7f, 5.829419E-7f, 6.208247E-7f, 6.611694E-7f, 7.041359E-7f, 7.4989464E-7f, 7.98627E-7f, 8.505263E-7f, 9.057983E-7f, 9.646621E-7f, 1.0273513E-6f, 1.0941144E-6f, 1.1652161E-6f, 1.2409384E-6f, 1.3215816E-6f, 1.4074654E-6f, 1.4989305E-6f, 1.5963394E-6f, 1.7000785E-6f, 1.8105592E-6f, 1.9282195E-6f, 2.053526E-6f, 2.1869757E-6f, 2.3290977E-6f, 2.4804558E-6f, 2.6416496E-6f, 2.813319E-6f, 2.9961443E-6f, 3.1908505E-6f, 3.39821E-6f, 3.619045E-6f, 3.8542307E-6f, 4.1047006E-6f, 4.371447E-6f, 4.6555283E-6f, 4.958071E-6f, 5.280274E-6f, 5.623416E-6f, 5.988857E-6f, 6.3780467E-6f, 6.7925284E-6f, 7.2339453E-6f, 7.704048E-6f, 8.2047E-6f, 8.737888E-6f, 9.305725E-6f, 9.910464E-6f, 1.0554501E-5f, 1.1240392E-5f, 1.1970856E-5f, 1.2748789E-5f, 1.3577278E-5f, 1.4459606E-5f, 1.5399271E-5f, 1.6400005E-5f, 1.7465769E-5f, 1.8600793E-5f, 1.9809577E-5f, 2.1096914E-5f, 2.2467912E-5f, 2.3928002E-5f, 2.5482977E-5f, 2.7139005E-5f, 2.890265E-5f, 3.078091E-5f, 3.2781227E-5f, 3.4911533E-5f, 3.718028E-5f, 3.9596467E-5f, 4.2169668E-5f, 4.491009E-5f, 4.7828602E-5f, 5.0936775E-5f, 5.424693E-5f, 5.7772202E-5f, 6.152657E-5f, 6.552491E-5f, 6.9783084E-5f, 7.4317984E-5f, 7.914758E-5f, 8.429104E-5f, 8.976875E-5f, 9.560242E-5f, 1.0181521E-4f, 1.0843174E-4f, 1.1547824E-4f, 1.2298267E-4f, 1.3097477E-4f, 1.3948625E-4f, 1.4855085E-4f, 1.5820454E-4f, 1.6848555E-4f, 1.7943469E-4f, 1.9109536E-4f, 2.0351382E-4f, 2.167393E-4f, 2.3082423E-4f, 2.4582449E-4f, 2.6179955E-4f, 2.7881275E-4f, 2.9693157E-4f, 3.1622787E-4f, 3.3677815E-4f, 3.5866388E-4f, 3.8197188E-4f, 4.0679457E-4f, 4.3323037E-4f, 4.613841E-4f, 4.913675E-4f, 5.2329927E-4f, 5.573062E-4f, 5.935231E-4f, 6.320936E-4f, 6.731706E-4f, 7.16917E-4f, 7.635063E-4f, 8.1312325E-4f, 8.6596457E-4f, 9.2223985E-4f, 9.821722E-4f, 0.0010459992f, 0.0011139743f, 0.0011863665f, 0.0012634633f, 0.0013455702f, 0.0014330129f, 0.0015261382f, 0.0016253153f, 0.0017309374f, 0.0018434235f, 0.0019632196f, 0.0020908006f, 0.0022266726f, 0.0023713743f, 0.0025254795f, 0.0026895993f, 0.0028643848f, 0.0030505287f, 0.003248769f, 0.0034598925f, 0.0036847359f, 0.0039241905f, 0.0041792067f, 0.004450795f, 0.004740033f, 0.005048067f, 0.0053761187f, 0.005725489f, 0.0060975635f, 0.0064938175f, 0.0069158226f, 0.0073652514f, 0.007843887f, 0.008353627f, 0.008896492f, 0.009474637f, 0.010090352f, 0.01074608f, 0.011444421f, 0.012188144f, 0.012980198f, 0.013823725f, 0.014722068f, 0.015678791f, 0.016697686f, 0.017782796f, 0.018938422f, 0.020169148f, 0.021479854f, 0.022875736f, 0.02436233f, 0.025945531f, 0.027631618f, 0.029427277f, 0.031339627f, 0.03337625f, 0.035545226f, 0.037855156f, 0.0403152f, 0.042935107f, 0.045725275f, 0.048696756f, 0.05186135f, 0.05523159f, 0.05882085f, 0.062643364f, 0.06671428f, 0.07104975f, 0.075666964f, 0.08058423f, 0.08582105f, 0.09139818f, 0.097337745f, 0.1036633f, 0.11039993f, 0.11757434f, 0.12521498f, 0.13335215f, 0.14201812f, 0.15124726f, 0.16107617f, 0.1715438f, 0.18269168f, 0.19456401f, 0.20720787f, 0.22067343f, 0.23501402f, 0.25028655f, 0.26655158f, 0.28387362f, 0.3023213f, 0.32196787f, 0.34289113f, 0.36517414f, 0.3889052f, 0.41417846f, 0.44109413f, 0.4697589f, 0.50028646f, 0.53279793f, 0.5674221f, 0.6042964f, 0.64356697f, 0.6853896f, 0.72993004f, 0.777365f, 0.8278826f, 0.88168305f, 0.9389798f, 1.0f};

    Floor1() {
    }

    void pack(Object i2, Buffer opb) {
        int k2;
        int j2;
        InfoFloor1 info = (InfoFloor1)i2;
        int count = 0;
        int maxposit = info.postlist[1];
        int maxclass = -1;
        opb.write(info.partitions, 5);
        for (j2 = 0; j2 < info.partitions; ++j2) {
            opb.write(info.partitionclass[j2], 4);
            if (maxclass >= info.partitionclass[j2]) continue;
            maxclass = info.partitionclass[j2];
        }
        for (j2 = 0; j2 < maxclass + 1; ++j2) {
            opb.write(info.class_dim[j2] - 1, 3);
            opb.write(info.class_subs[j2], 2);
            if (info.class_subs[j2] != 0) {
                opb.write(info.class_book[j2], 8);
            }
            for (k2 = 0; k2 < 1 << info.class_subs[j2]; ++k2) {
                opb.write(info.class_subbook[j2][k2] + 1, 8);
            }
        }
        opb.write(info.mult - 1, 2);
        opb.write(Util.ilog2(maxposit), 4);
        int rangebits = Util.ilog2(maxposit);
        k2 = 0;
        for (j2 = 0; j2 < info.partitions; ++j2) {
            count += info.class_dim[info.partitionclass[j2]];
            while (k2 < count) {
                opb.write(info.postlist[k2 + 2], rangebits);
                ++k2;
            }
        }
    }

    Object unpack(Info vi2, Buffer opb) {
        int k2;
        int j2;
        int count = 0;
        int maxclass = -1;
        InfoFloor1 info = new InfoFloor1();
        info.partitions = opb.read(5);
        for (j2 = 0; j2 < info.partitions; ++j2) {
            info.partitionclass[j2] = opb.read(4);
            if (maxclass >= info.partitionclass[j2]) continue;
            maxclass = info.partitionclass[j2];
        }
        for (j2 = 0; j2 < maxclass + 1; ++j2) {
            info.class_dim[j2] = opb.read(3) + 1;
            info.class_subs[j2] = opb.read(2);
            if (info.class_subs[j2] < 0) {
                info.free();
                return null;
            }
            if (info.class_subs[j2] != 0) {
                info.class_book[j2] = opb.read(8);
            }
            if (info.class_book[j2] < 0 || info.class_book[j2] >= vi2.books) {
                info.free();
                return null;
            }
            for (k2 = 0; k2 < 1 << info.class_subs[j2]; ++k2) {
                info.class_subbook[j2][k2] = opb.read(8) - 1;
                if (info.class_subbook[j2][k2] >= -1 && info.class_subbook[j2][k2] < vi2.books) continue;
                info.free();
                return null;
            }
        }
        info.mult = opb.read(2) + 1;
        int rangebits = opb.read(4);
        k2 = 0;
        for (j2 = 0; j2 < info.partitions; ++j2) {
            count += info.class_dim[info.partitionclass[j2]];
            while (k2 < count) {
                info.postlist[k2 + 2] = opb.read(rangebits);
                int t2 = info.postlist[k2 + 2];
                if (t2 < 0 || t2 >= 1 << rangebits) {
                    info.free();
                    return null;
                }
                ++k2;
            }
        }
        info.postlist[0] = 0;
        info.postlist[1] = 1 << rangebits;
        return info;
    }

    Object look(DspState vd2, InfoMode mi, Object i2) {
        int j2;
        int j3;
        int _n = 0;
        int[] sortpointer = new int[65];
        InfoFloor1 info = (InfoFloor1)i2;
        LookFloor1 look = new LookFloor1();
        look.vi = info;
        look.n = info.postlist[1];
        for (j3 = 0; j3 < info.partitions; ++j3) {
            _n += info.class_dim[info.partitionclass[j3]];
        }
        look.posts = _n += 2;
        for (j3 = 0; j3 < _n; ++j3) {
            sortpointer[j3] = j3;
        }
        for (j2 = 0; j2 < _n - 1; ++j2) {
            for (int k2 = j2; k2 < _n; ++k2) {
                if (info.postlist[sortpointer[j2]] <= info.postlist[sortpointer[k2]]) continue;
                int foo = sortpointer[k2];
                sortpointer[k2] = sortpointer[j2];
                sortpointer[j2] = foo;
            }
        }
        for (j2 = 0; j2 < _n; ++j2) {
            look.forward_index[j2] = sortpointer[j2];
        }
        for (j2 = 0; j2 < _n; ++j2) {
            look.reverse_index[look.forward_index[j2]] = j2;
        }
        for (j2 = 0; j2 < _n; ++j2) {
            look.sorted_index[j2] = info.postlist[look.forward_index[j2]];
        }
        switch (info.mult) {
            case 1: {
                look.quant_q = 256;
                break;
            }
            case 2: {
                look.quant_q = 128;
                break;
            }
            case 3: {
                look.quant_q = 86;
                break;
            }
            case 4: {
                look.quant_q = 64;
                break;
            }
            default: {
                look.quant_q = -1;
            }
        }
        for (j2 = 0; j2 < _n - 2; ++j2) {
            int lo2 = 0;
            int hi2 = 1;
            int lx2 = 0;
            int hx2 = look.n;
            int currentx = info.postlist[j2 + 2];
            for (int k3 = 0; k3 < j2 + 2; ++k3) {
                int x2 = info.postlist[k3];
                if (x2 > lx2 && x2 < currentx) {
                    lo2 = k3;
                    lx2 = x2;
                }
                if (x2 >= hx2 || x2 <= currentx) continue;
                hi2 = k3;
                hx2 = x2;
            }
            look.loneighbor[j2] = lo2;
            look.hineighbor[j2] = hi2;
        }
        return look;
    }

    void free_info(Object i2) {
    }

    void free_look(Object i2) {
    }

    void free_state(Object vs2) {
    }

    int forward(Block vb2, Object i2, float[] in2, float[] out, Object vs2) {
        return 0;
    }

    Object inverse1(Block vb2, Object ii2, Object memo) {
        LookFloor1 look = (LookFloor1)ii2;
        InfoFloor1 info = look.vi;
        CodeBook[] books = vb2.vd.fullbooks;
        if (vb2.opb.read(1) == 1) {
            int i2;
            int[] fit_value = null;
            if (memo instanceof int[]) {
                fit_value = (int[])memo;
            }
            if (fit_value == null || fit_value.length < look.posts) {
                fit_value = new int[look.posts];
            } else {
                for (i2 = 0; i2 < fit_value.length; ++i2) {
                    fit_value[i2] = 0;
                }
            }
            fit_value[0] = vb2.opb.read(Util.ilog(look.quant_q - 1));
            fit_value[1] = vb2.opb.read(Util.ilog(look.quant_q - 1));
            int j2 = 2;
            for (i2 = 0; i2 < info.partitions; ++i2) {
                int clss = info.partitionclass[i2];
                int cdim = info.class_dim[clss];
                int csubbits = info.class_subs[clss];
                int csub = 1 << csubbits;
                int cval = 0;
                if (csubbits != 0 && (cval = books[info.class_book[clss]].decode(vb2.opb)) == -1) {
                    return null;
                }
                for (int k2 = 0; k2 < cdim; ++k2) {
                    int book = info.class_subbook[clss][cval & csub - 1];
                    cval >>>= csubbits;
                    if (book >= 0) {
                        fit_value[j2 + k2] = books[book].decode(vb2.opb);
                        if (fit_value[j2 + k2] != -1) continue;
                        return null;
                    }
                    fit_value[j2 + k2] = 0;
                }
                j2 += cdim;
            }
            for (i2 = 2; i2 < look.posts; ++i2) {
                int loroom;
                int predicted = Floor1.render_point(info.postlist[look.loneighbor[i2 - 2]], info.postlist[look.hineighbor[i2 - 2]], fit_value[look.loneighbor[i2 - 2]], fit_value[look.hineighbor[i2 - 2]], info.postlist[i2]);
                int hiroom = look.quant_q - predicted;
                int room = (hiroom < (loroom = predicted) ? hiroom : loroom) << 1;
                int val = fit_value[i2];
                if (val != 0) {
                    val = val >= room ? (hiroom > loroom ? (val -= loroom) : -1 - (val - hiroom)) : ((val & 1) != 0 ? -(val + 1 >>> 1) : (val >>= 1));
                    fit_value[i2] = val + predicted;
                    int n2 = look.loneighbor[i2 - 2];
                    fit_value[n2] = fit_value[n2] & Short.MAX_VALUE;
                    int n3 = look.hineighbor[i2 - 2];
                    fit_value[n3] = fit_value[n3] & Short.MAX_VALUE;
                    continue;
                }
                fit_value[i2] = predicted | 0x8000;
            }
            return fit_value;
        }
        return null;
    }

    private static int render_point(int x0, int x1, int y0, int y1, int x2) {
        int dy2 = (y1 &= Short.MAX_VALUE) - (y0 &= Short.MAX_VALUE);
        int adx2 = x1 - x0;
        int ady2 = Math.abs(dy2);
        int err = ady2 * (x2 - x0);
        int off = err / adx2;
        if (dy2 < 0) {
            return y0 - off;
        }
        return y0 + off;
    }

    int inverse2(Block vb2, Object i2, Object memo, float[] out) {
        LookFloor1 look = (LookFloor1)i2;
        InfoFloor1 info = look.vi;
        int n2 = vb2.vd.vi.blocksizes[vb2.mode] / 2;
        if (memo != null) {
            int j2;
            int[] fit_value = (int[])memo;
            int hx2 = 0;
            int lx2 = 0;
            int ly2 = fit_value[0] * info.mult;
            for (j2 = 1; j2 < look.posts; ++j2) {
                int current = look.forward_index[j2];
                int hy2 = fit_value[current] & Short.MAX_VALUE;
                if (hy2 != fit_value[current]) continue;
                hx2 = info.postlist[current];
                Floor1.render_line(lx2, hx2, ly2, hy2 *= info.mult, out);
                lx2 = hx2;
                ly2 = hy2;
            }
            for (j2 = hx2; j2 < n2; ++j2) {
                int n3 = j2;
                out[n3] = out[n3] * out[j2 - 1];
            }
            return 1;
        }
        for (int j3 = 0; j3 < n2; ++j3) {
            out[j3] = 0.0f;
        }
        return 0;
    }

    private static void render_line(int x0, int x1, int y0, int y1, float[] d2) {
        int dy2 = y1 - y0;
        int adx2 = x1 - x0;
        int ady2 = Math.abs(dy2);
        int base = dy2 / adx2;
        int sy2 = dy2 < 0 ? base - 1 : base + 1;
        int x2 = x0;
        int y2 = y0;
        int err = 0;
        ady2 -= Math.abs(base * adx2);
        int n2 = x2;
        d2[n2] = d2[n2] * FLOOR_fromdB_LOOKUP[y2];
        while (++x2 < x1) {
            if ((err += ady2) >= adx2) {
                err -= adx2;
                y2 += sy2;
            } else {
                y2 += base;
            }
            int n3 = x2;
            d2[n3] = d2[n3] * FLOOR_fromdB_LOOKUP[y2];
        }
    }

    class EchstateFloor1 {
        int[] codewords;
        float[] curve;
        long frameno;
        long codes;

        EchstateFloor1() {
        }
    }

    class Lsfit_acc {
        long x0;
        long x1;
        long xa;
        long ya;
        long x2a;
        long y2a;
        long xya;
        long n;
        long an;
        long un;
        long edgey0;
        long edgey1;

        Lsfit_acc() {
        }
    }

    class LookFloor1 {
        static final int VIF_POSIT = 63;
        int[] sorted_index = new int[65];
        int[] forward_index = new int[65];
        int[] reverse_index = new int[65];
        int[] hineighbor = new int[63];
        int[] loneighbor = new int[63];
        int posts;
        int n;
        int quant_q;
        InfoFloor1 vi;
        int phrasebits;
        int postbits;
        int frames;

        LookFloor1() {
        }

        void free() {
            this.sorted_index = null;
            this.forward_index = null;
            this.reverse_index = null;
            this.hineighbor = null;
            this.loneighbor = null;
        }
    }

    class InfoFloor1 {
        static final int VIF_POSIT = 63;
        static final int VIF_CLASS = 16;
        static final int VIF_PARTS = 31;
        int partitions;
        int[] partitionclass = new int[31];
        int[] class_dim = new int[16];
        int[] class_subs = new int[16];
        int[] class_book = new int[16];
        int[][] class_subbook = new int[16][];
        int mult;
        int[] postlist = new int[65];
        float maxover;
        float maxunder;
        float maxerr;
        int twofitminsize;
        int twofitminused;
        int twofitweight;
        float twofitatten;
        int unusedminsize;
        int unusedmin_n;
        int n;

        InfoFloor1() {
            for (int i2 = 0; i2 < this.class_subbook.length; ++i2) {
                this.class_subbook[i2] = new int[8];
            }
        }

        void free() {
            this.partitionclass = null;
            this.class_dim = null;
            this.class_subs = null;
            this.class_book = null;
            this.class_subbook = null;
            this.postlist = null;
        }

        Object copy_info() {
            InfoFloor1 info = this;
            InfoFloor1 ret = new InfoFloor1();
            ret.partitions = info.partitions;
            System.arraycopy(info.partitionclass, 0, ret.partitionclass, 0, 31);
            System.arraycopy(info.class_dim, 0, ret.class_dim, 0, 16);
            System.arraycopy(info.class_subs, 0, ret.class_subs, 0, 16);
            System.arraycopy(info.class_book, 0, ret.class_book, 0, 16);
            for (int j2 = 0; j2 < 16; ++j2) {
                System.arraycopy(info.class_subbook[j2], 0, ret.class_subbook[j2], 0, 8);
            }
            ret.mult = info.mult;
            System.arraycopy(info.postlist, 0, ret.postlist, 0, 65);
            ret.maxover = info.maxover;
            ret.maxunder = info.maxunder;
            ret.maxerr = info.maxerr;
            ret.twofitminsize = info.twofitminsize;
            ret.twofitminused = info.twofitminused;
            ret.twofitweight = info.twofitweight;
            ret.twofitatten = info.twofitatten;
            ret.unusedminsize = info.unusedminsize;
            ret.unusedmin_n = info.unusedmin_n;
            ret.n = info.n;
            return ret;
        }
    }
}

