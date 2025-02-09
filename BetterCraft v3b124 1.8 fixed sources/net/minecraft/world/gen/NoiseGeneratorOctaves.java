/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorImproved;

public class NoiseGeneratorOctaves
extends NoiseGenerator {
    private NoiseGeneratorImproved[] generatorCollection;
    private int octaves;

    public NoiseGeneratorOctaves(Random seed, int octavesIn) {
        this.octaves = octavesIn;
        this.generatorCollection = new NoiseGeneratorImproved[octavesIn];
        int i2 = 0;
        while (i2 < octavesIn) {
            this.generatorCollection[i2] = new NoiseGeneratorImproved(seed);
            ++i2;
        }
    }

    public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        if (noiseArray == null) {
            noiseArray = new double[xSize * ySize * zSize];
        } else {
            int i2 = 0;
            while (i2 < noiseArray.length) {
                noiseArray[i2] = 0.0;
                ++i2;
            }
        }
        double d3 = 1.0;
        int j2 = 0;
        while (j2 < this.octaves) {
            double d0 = (double)xOffset * d3 * xScale;
            double d1 = (double)yOffset * d3 * yScale;
            double d2 = (double)zOffset * d3 * zScale;
            long k2 = MathHelper.floor_double_long(d0);
            long l2 = MathHelper.floor_double_long(d2);
            d0 -= (double)k2;
            d2 -= (double)l2;
            this.generatorCollection[j2].populateNoiseArray(noiseArray, d0 += (double)(k2 %= 0x1000000L), d1, d2 += (double)(l2 %= 0x1000000L), xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3);
            d3 /= 2.0;
            ++j2;
        }
        return noiseArray;
    }

    public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int zOffset, int xSize, int zSize, double xScale, double zScale, double p_76305_10_) {
        return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0, zScale);
    }
}

