/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import net.minecraft.world.World;
import net.optifine.LightMap;

public class LightMapPack {
    private LightMap lightMap;
    private LightMap lightMapRain;
    private LightMap lightMapThunder;
    private int[] colorBuffer1 = new int[0];
    private int[] colorBuffer2 = new int[0];

    public LightMapPack(LightMap lightMap, LightMap lightMapRain, LightMap lightMapThunder) {
        if (lightMapRain != null || lightMapThunder != null) {
            if (lightMapRain == null) {
                lightMapRain = lightMap;
            }
            if (lightMapThunder == null) {
                lightMapThunder = lightMapRain;
            }
        }
        this.lightMap = lightMap;
        this.lightMapRain = lightMapRain;
        this.lightMapThunder = lightMapThunder;
    }

    public boolean updateLightmap(World world, float torchFlickerX, int[] lmColors, boolean nightvision, float partialTicks) {
        if (this.lightMapRain == null && this.lightMapThunder == null) {
            return this.lightMap.updateLightmap(world, torchFlickerX, lmColors, nightvision);
        }
        int i2 = world.provider.getDimensionId();
        if (i2 != 1 && i2 != -1) {
            boolean flag1;
            float f2 = world.getRainStrength(partialTicks);
            float f1 = world.getThunderStrength(partialTicks);
            float f22 = 1.0E-4f;
            boolean flag = f2 > f22;
            boolean bl2 = flag1 = f1 > f22;
            if (!flag && !flag1) {
                return this.lightMap.updateLightmap(world, torchFlickerX, lmColors, nightvision);
            }
            if (f2 > 0.0f) {
                f1 /= f2;
            }
            float f3 = 1.0f - f2;
            float f4 = f2 - f1;
            if (this.colorBuffer1.length != lmColors.length) {
                this.colorBuffer1 = new int[lmColors.length];
                this.colorBuffer2 = new int[lmColors.length];
            }
            int j2 = 0;
            int[][] aint = new int[][]{lmColors, this.colorBuffer1, this.colorBuffer2};
            float[] afloat = new float[3];
            if (f3 > f22 && this.lightMap.updateLightmap(world, torchFlickerX, aint[j2], nightvision)) {
                afloat[j2] = f3;
                ++j2;
            }
            if (f4 > f22 && this.lightMapRain != null && this.lightMapRain.updateLightmap(world, torchFlickerX, aint[j2], nightvision)) {
                afloat[j2] = f4;
                ++j2;
            }
            if (f1 > f22 && this.lightMapThunder != null && this.lightMapThunder.updateLightmap(world, torchFlickerX, aint[j2], nightvision)) {
                afloat[j2] = f1;
                ++j2;
            }
            return j2 == 2 ? this.blend(aint[0], afloat[0], aint[1], afloat[1]) : (j2 == 3 ? this.blend(aint[0], afloat[0], aint[1], afloat[1], aint[2], afloat[2]) : true);
        }
        return this.lightMap.updateLightmap(world, torchFlickerX, lmColors, nightvision);
    }

    private boolean blend(int[] cols0, float br0, int[] cols1, float br1) {
        if (cols1.length != cols0.length) {
            return false;
        }
        int i2 = 0;
        while (i2 < cols0.length) {
            int j2 = cols0[i2];
            int k2 = j2 >> 16 & 0xFF;
            int l2 = j2 >> 8 & 0xFF;
            int i1 = j2 & 0xFF;
            int j1 = cols1[i2];
            int k1 = j1 >> 16 & 0xFF;
            int l1 = j1 >> 8 & 0xFF;
            int i22 = j1 & 0xFF;
            int j22 = (int)((float)k2 * br0 + (float)k1 * br1);
            int k22 = (int)((float)l2 * br0 + (float)l1 * br1);
            int l22 = (int)((float)i1 * br0 + (float)i22 * br1);
            cols0[i2] = 0xFF000000 | j22 << 16 | k22 << 8 | l22;
            ++i2;
        }
        return true;
    }

    private boolean blend(int[] cols0, float br0, int[] cols1, float br1, int[] cols2, float br2) {
        if (cols1.length == cols0.length && cols2.length == cols0.length) {
            int i2 = 0;
            while (i2 < cols0.length) {
                int j2 = cols0[i2];
                int k2 = j2 >> 16 & 0xFF;
                int l2 = j2 >> 8 & 0xFF;
                int i1 = j2 & 0xFF;
                int j1 = cols1[i2];
                int k1 = j1 >> 16 & 0xFF;
                int l1 = j1 >> 8 & 0xFF;
                int i22 = j1 & 0xFF;
                int j22 = cols2[i2];
                int k22 = j22 >> 16 & 0xFF;
                int l22 = j22 >> 8 & 0xFF;
                int i3 = j22 & 0xFF;
                int j3 = (int)((float)k2 * br0 + (float)k1 * br1 + (float)k22 * br2);
                int k3 = (int)((float)l2 * br0 + (float)l1 * br1 + (float)l22 * br2);
                int l3 = (int)((float)i1 * br0 + (float)i22 * br1 + (float)i3 * br2);
                cols0[i2] = 0xFF000000 | j3 << 16 | k3 << 8 | l3;
                ++i2;
            }
            return true;
        }
        return false;
    }
}

