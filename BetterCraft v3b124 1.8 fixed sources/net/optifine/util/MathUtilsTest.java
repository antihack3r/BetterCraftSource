/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import net.minecraft.util.MathHelper;
import net.optifine.util.MathUtils;

public class MathUtilsTest {
    public static void main(String[] args) throws Exception {
        OPER[] amathutilstest$oper = OPER.values();
        int i2 = 0;
        while (i2 < amathutilstest$oper.length) {
            OPER mathutilstest$oper = amathutilstest$oper[i2];
            MathUtilsTest.dbg("******** " + (Object)((Object)mathutilstest$oper) + " ***********");
            MathUtilsTest.test(mathutilstest$oper, false);
            ++i2;
        }
    }

    private static void test(OPER oper, boolean fast) {
        double d1;
        double d0;
        MathHelper.fastMath = fast;
        switch (oper) {
            case SIN: 
            case COS: {
                d0 = -MathHelper.PI;
                d1 = MathHelper.PI;
                break;
            }
            case ASIN: 
            case ACOS: {
                d0 = -1.0;
                d1 = 1.0;
                break;
            }
            default: {
                return;
            }
        }
        int i2 = 10;
        int j2 = 0;
        while (j2 <= i2) {
            float f1;
            float f2;
            double d2 = d0 + (double)j2 * (d1 - d0) / (double)i2;
            switch (oper) {
                case SIN: {
                    f2 = (float)Math.sin(d2);
                    f1 = MathHelper.sin((float)d2);
                    break;
                }
                case COS: {
                    f2 = (float)Math.cos(d2);
                    f1 = MathHelper.cos((float)d2);
                    break;
                }
                case ASIN: {
                    f2 = (float)Math.asin(d2);
                    f1 = MathUtils.asin((float)d2);
                    break;
                }
                case ACOS: {
                    f2 = (float)Math.acos(d2);
                    f1 = MathUtils.acos((float)d2);
                    break;
                }
                default: {
                    return;
                }
            }
            MathUtilsTest.dbg(String.format("%.2f, Math: %f, Helper: %f, diff: %f", d2, Float.valueOf(f2), Float.valueOf(f1), Float.valueOf(Math.abs(f2 - f1))));
            ++j2;
        }
    }

    public static void dbg(String str) {
        System.out.println(str);
    }

    private static enum OPER {
        SIN,
        COS,
        ASIN,
        ACOS;

    }
}

