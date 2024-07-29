/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.optifine.BlockPosM;

public class ClearWater {
    public static void updateWaterOpacity(GameSettings settings, World world) {
        Entity entity;
        IChunkProvider ichunkprovider;
        if (settings != null) {
            int i2 = 3;
            if (settings.ofClearWater) {
                i2 = 1;
            }
            BlockAir.setLightOpacity(Blocks.water, i2);
            BlockAir.setLightOpacity(Blocks.flowing_water, i2);
        }
        if (world != null && (ichunkprovider = world.getChunkProvider()) != null && (entity = Config.getMinecraft().getRenderViewEntity()) != null) {
            int j2 = (int)entity.posX / 16;
            int k2 = (int)entity.posZ / 16;
            int l2 = j2 - 512;
            int i1 = j2 + 512;
            int j1 = k2 - 512;
            int k1 = k2 + 512;
            int l1 = 0;
            int i2 = l2;
            while (i2 < i1) {
                int j22 = j1;
                while (j22 < k1) {
                    Chunk chunk;
                    if (ichunkprovider.chunkExists(i2, j22) && (chunk = ichunkprovider.provideChunk(i2, j22)) != null && !(chunk instanceof EmptyChunk)) {
                        int k22 = i2 << 4;
                        int l22 = j22 << 4;
                        int i3 = k22 + 16;
                        int j3 = l22 + 16;
                        BlockPosM blockposm = new BlockPosM(0, 0, 0);
                        BlockPosM blockposm1 = new BlockPosM(0, 0, 0);
                        int k3 = k22;
                        while (k3 < i3) {
                            int l3 = l22;
                            while (l3 < j3) {
                                blockposm.setXyz(k3, 0, l3);
                                BlockPos blockpos = world.getPrecipitationHeight(blockposm);
                                int i4 = 0;
                                while (i4 < blockpos.getY()) {
                                    blockposm1.setXyz(k3, i4, l3);
                                    IBlockState iblockstate = world.getBlockState(blockposm1);
                                    if (iblockstate.getBlock().getMaterial() == Material.water) {
                                        world.markBlocksDirtyVertical(k3, l3, blockposm1.getY(), blockpos.getY());
                                        ++l1;
                                        break;
                                    }
                                    ++i4;
                                }
                                ++l3;
                            }
                            ++k3;
                        }
                    }
                    ++j22;
                }
                ++i2;
            }
            if (l1 > 0) {
                String s2 = "server";
                if (Config.isMinecraftThread()) {
                    s2 = "client";
                }
                Config.dbg("ClearWater (" + s2 + ") relighted " + l1 + " chunks");
            }
        }
    }
}

