/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;

public class WorldProviderHell
extends WorldProvider {
    @Override
    public void registerWorldChunkManager() {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 0.0f);
        this.isHellWorld = true;
        this.hasNoSky = true;
        this.dimensionId = -1;
    }

    @Override
    public Vec3 getFogColor(float p_76562_1_, float p_76562_2_) {
        return new Vec3(0.2f, 0.03f, 0.03f);
    }

    @Override
    protected void generateLightBrightnessTable() {
        float f2 = 0.1f;
        int i2 = 0;
        while (i2 <= 15) {
            float f1 = 1.0f - (float)i2 / 15.0f;
            this.lightBrightnessTable[i2] = (1.0f - f1) / (f1 * 3.0f + 1.0f) * (1.0f - f2) + f2;
            ++i2;
        }
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new ChunkProviderHell(this.worldObj, this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.worldObj.getSeed());
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public boolean canCoordinateBeSpawn(int x2, int z2) {
        return false;
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.5f;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean doesXZShowFog(int x2, int z2) {
        return true;
    }

    @Override
    public String getDimensionName() {
        return "Nether";
    }

    @Override
    public String getInternalNameSuffix() {
        return "_nether";
    }

    @Override
    public WorldBorder getWorldBorder() {
        return new WorldBorder(){

            @Override
            public double getCenterX() {
                return super.getCenterX() / 8.0;
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / 8.0;
            }
        };
    }
}

