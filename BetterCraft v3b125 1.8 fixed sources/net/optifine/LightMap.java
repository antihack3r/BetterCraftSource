/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.world.World;
import net.optifine.CustomColormap;

public class LightMap {
    private CustomColormap lightMapRgb = null;
    private float[][] sunRgbs = new float[16][3];
    private float[][] torchRgbs = new float[16][3];

    public LightMap(CustomColormap lightMapRgb) {
        this.lightMapRgb = lightMapRgb;
    }

    public CustomColormap getColormap() {
        return this.lightMapRgb;
    }

    public boolean updateLightmap(World world, float torchFlickerX, int[] lmColors, boolean nightvision) {
        if (this.lightMapRgb == null) {
            return false;
        }
        int i2 = this.lightMapRgb.getHeight();
        if (nightvision && i2 < 64) {
            return false;
        }
        int j2 = this.lightMapRgb.getWidth();
        if (j2 < 16) {
            LightMap.warn("Invalid lightmap width: " + j2);
            this.lightMapRgb = null;
            return false;
        }
        int k2 = 0;
        if (nightvision) {
            k2 = j2 * 16 * 2;
        }
        float f2 = 1.1666666f * (world.getSunBrightness(1.0f) - 0.2f);
        if (world.getLastLightningBolt() > 0) {
            f2 = 1.0f;
        }
        f2 = Config.limitTo1(f2);
        float f1 = f2 * (float)(j2 - 1);
        float f22 = Config.limitTo1(torchFlickerX + 0.5f) * (float)(j2 - 1);
        float f3 = Config.limitTo1(Config.getGameSettings().gammaSetting);
        boolean flag = f3 > 1.0E-4f;
        float[][] afloat = this.lightMapRgb.getColorsRgb();
        this.getLightMapColumn(afloat, f1, k2, j2, this.sunRgbs);
        this.getLightMapColumn(afloat, f22, k2 + 16 * j2, j2, this.torchRgbs);
        float[] afloat1 = new float[3];
        int l2 = 0;
        while (l2 < 16) {
            int i1 = 0;
            while (i1 < 16) {
                int j1 = 0;
                while (j1 < 3) {
                    float f4 = Config.limitTo1(this.sunRgbs[l2][j1] + this.torchRgbs[i1][j1]);
                    if (flag) {
                        float f5 = 1.0f - f4;
                        f5 = 1.0f - f5 * f5 * f5 * f5;
                        f4 = f3 * f5 + (1.0f - f3) * f4;
                    }
                    afloat1[j1] = f4;
                    ++j1;
                }
                int k1 = (int)(afloat1[0] * 255.0f);
                int l1 = (int)(afloat1[1] * 255.0f);
                int i22 = (int)(afloat1[2] * 255.0f);
                lmColors[l2 * 16 + i1] = 0xFF000000 | k1 << 16 | l1 << 8 | i22;
                ++i1;
            }
            ++l2;
        }
        return true;
    }

    private void getLightMapColumn(float[][] origMap, float x2, int offset, int width, float[][] colRgb) {
        int j2;
        int i2 = (int)Math.floor(x2);
        if (i2 == (j2 = (int)Math.ceil(x2))) {
            int i1 = 0;
            while (i1 < 16) {
                float[] afloat3 = origMap[offset + i1 * width + i2];
                float[] afloat4 = colRgb[i1];
                int j1 = 0;
                while (j1 < 3) {
                    afloat4[j1] = afloat3[j1];
                    ++j1;
                }
                ++i1;
            }
        } else {
            float f2 = 1.0f - (x2 - (float)i2);
            float f1 = 1.0f - ((float)j2 - x2);
            int k2 = 0;
            while (k2 < 16) {
                float[] afloat = origMap[offset + k2 * width + i2];
                float[] afloat1 = origMap[offset + k2 * width + j2];
                float[] afloat2 = colRgb[k2];
                int l2 = 0;
                while (l2 < 3) {
                    afloat2[l2] = afloat[l2] * f2 + afloat1[l2] * f1;
                    ++l2;
                }
                ++k2;
            }
        }
    }

    private static void dbg(String str) {
        Config.dbg("CustomColors: " + str);
    }

    private static void warn(String str) {
        Config.warn("CustomColors: " + str);
    }
}

