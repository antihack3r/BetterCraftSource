/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public class MapGenCavesHell
extends MapGenBase {
    protected void func_180705_a(long p_180705_1_, int p_180705_3_, int p_180705_4_, ChunkPrimer p_180705_5_, double p_180705_6_, double p_180705_8_, double p_180705_10_) {
        this.func_180704_a(p_180705_1_, p_180705_3_, p_180705_4_, p_180705_5_, p_180705_6_, p_180705_8_, p_180705_10_, 1.0f + this.rand.nextFloat() * 6.0f, 0.0f, 0.0f, -1, -1, 0.5);
    }

    protected void func_180704_a(long p_180704_1_, int p_180704_3_, int p_180704_4_, ChunkPrimer p_180704_5_, double p_180704_6_, double p_180704_8_, double p_180704_10_, float p_180704_12_, float p_180704_13_, float p_180704_14_, int p_180704_15_, int p_180704_16_, double p_180704_17_) {
        double d0 = p_180704_3_ * 16 + 8;
        double d1 = p_180704_4_ * 16 + 8;
        float f2 = 0.0f;
        float f1 = 0.0f;
        Random random = new Random(p_180704_1_);
        if (p_180704_16_ <= 0) {
            int i2 = this.range * 16 - 16;
            p_180704_16_ = i2 - random.nextInt(i2 / 4);
        }
        boolean flag1 = false;
        if (p_180704_15_ == -1) {
            p_180704_15_ = p_180704_16_ / 2;
            flag1 = true;
        }
        int j2 = random.nextInt(p_180704_16_ / 2) + p_180704_16_ / 4;
        boolean flag = random.nextInt(6) == 0;
        while (p_180704_15_ < p_180704_16_) {
            double d2 = 1.5 + (double)(MathHelper.sin((float)p_180704_15_ * (float)Math.PI / (float)p_180704_16_) * p_180704_12_ * 1.0f);
            double d3 = d2 * p_180704_17_;
            float f22 = MathHelper.cos(p_180704_14_);
            float f3 = MathHelper.sin(p_180704_14_);
            p_180704_6_ += (double)(MathHelper.cos(p_180704_13_) * f22);
            p_180704_8_ += (double)f3;
            p_180704_10_ += (double)(MathHelper.sin(p_180704_13_) * f22);
            p_180704_14_ = flag ? (p_180704_14_ *= 0.92f) : (p_180704_14_ *= 0.7f);
            p_180704_14_ += f1 * 0.1f;
            p_180704_13_ += f2 * 0.1f;
            f1 *= 0.9f;
            f2 *= 0.75f;
            f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f2 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (!flag1 && p_180704_15_ == j2 && p_180704_12_ > 1.0f) {
                this.func_180704_a(random.nextLong(), p_180704_3_, p_180704_4_, p_180704_5_, p_180704_6_, p_180704_8_, p_180704_10_, random.nextFloat() * 0.5f + 0.5f, p_180704_13_ - 1.5707964f, p_180704_14_ / 3.0f, p_180704_15_, p_180704_16_, 1.0);
                this.func_180704_a(random.nextLong(), p_180704_3_, p_180704_4_, p_180704_5_, p_180704_6_, p_180704_8_, p_180704_10_, random.nextFloat() * 0.5f + 0.5f, p_180704_13_ + 1.5707964f, p_180704_14_ / 3.0f, p_180704_15_, p_180704_16_, 1.0);
                return;
            }
            if (flag1 || random.nextInt(4) != 0) {
                double d4 = p_180704_6_ - d0;
                double d5 = p_180704_10_ - d1;
                double d6 = p_180704_16_ - p_180704_15_;
                double d7 = p_180704_12_ + 2.0f + 16.0f;
                if (d4 * d4 + d5 * d5 - d6 * d6 > d7 * d7) {
                    return;
                }
                if (p_180704_6_ >= d0 - 16.0 - d2 * 2.0 && p_180704_10_ >= d1 - 16.0 - d2 * 2.0 && p_180704_6_ <= d0 + 16.0 + d2 * 2.0 && p_180704_10_ <= d1 + 16.0 + d2 * 2.0) {
                    int j22 = MathHelper.floor_double(p_180704_6_ - d2) - p_180704_3_ * 16 - 1;
                    int k2 = MathHelper.floor_double(p_180704_6_ + d2) - p_180704_3_ * 16 + 1;
                    int k22 = MathHelper.floor_double(p_180704_8_ - d3) - 1;
                    int l2 = MathHelper.floor_double(p_180704_8_ + d3) + 1;
                    int l22 = MathHelper.floor_double(p_180704_10_ - d2) - p_180704_4_ * 16 - 1;
                    int i1 = MathHelper.floor_double(p_180704_10_ + d2) - p_180704_4_ * 16 + 1;
                    if (j22 < 0) {
                        j22 = 0;
                    }
                    if (k2 > 16) {
                        k2 = 16;
                    }
                    if (k22 < 1) {
                        k22 = 1;
                    }
                    if (l2 > 120) {
                        l2 = 120;
                    }
                    if (l22 < 0) {
                        l22 = 0;
                    }
                    if (i1 > 16) {
                        i1 = 16;
                    }
                    boolean flag2 = false;
                    int j1 = j22;
                    while (!flag2 && j1 < k2) {
                        int k1 = l22;
                        while (!flag2 && k1 < i1) {
                            int l1 = l2 + 1;
                            while (!flag2 && l1 >= k22 - 1) {
                                if (l1 >= 0 && l1 < 128) {
                                    IBlockState iblockstate = p_180704_5_.getBlockState(j1, l1, k1);
                                    if (iblockstate.getBlock() == Blocks.flowing_lava || iblockstate.getBlock() == Blocks.lava) {
                                        flag2 = true;
                                    }
                                    if (l1 != k22 - 1 && j1 != j22 && j1 != k2 - 1 && k1 != l22 && k1 != i1 - 1) {
                                        l1 = k22;
                                    }
                                }
                                --l1;
                            }
                            ++k1;
                        }
                        ++j1;
                    }
                    if (!flag2) {
                        int i3 = j22;
                        while (i3 < k2) {
                            double d10 = ((double)(i3 + p_180704_3_ * 16) + 0.5 - p_180704_6_) / d2;
                            int j3 = l22;
                            while (j3 < i1) {
                                double d8 = ((double)(j3 + p_180704_4_ * 16) + 0.5 - p_180704_10_) / d2;
                                int i2 = l2;
                                while (i2 > k22) {
                                    IBlockState iblockstate1;
                                    double d9 = ((double)(i2 - 1) + 0.5 - p_180704_8_) / d3;
                                    if (d9 > -0.7 && d10 * d10 + d9 * d9 + d8 * d8 < 1.0 && ((iblockstate1 = p_180704_5_.getBlockState(i3, i2, j3)).getBlock() == Blocks.netherrack || iblockstate1.getBlock() == Blocks.dirt || iblockstate1.getBlock() == Blocks.grass)) {
                                        p_180704_5_.setBlockState(i3, i2, j3, Blocks.air.getDefaultState());
                                    }
                                    --i2;
                                }
                                ++j3;
                            }
                            ++i3;
                        }
                        if (flag1) break;
                    }
                }
            }
            ++p_180704_15_;
        }
    }

    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {
        int i2 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);
        if (this.rand.nextInt(5) != 0) {
            i2 = 0;
        }
        int j2 = 0;
        while (j2 < i2) {
            double d0 = chunkX * 16 + this.rand.nextInt(16);
            double d1 = this.rand.nextInt(128);
            double d2 = chunkZ * 16 + this.rand.nextInt(16);
            int k2 = 1;
            if (this.rand.nextInt(4) == 0) {
                this.func_180705_a(this.rand.nextLong(), p_180701_4_, p_180701_5_, chunkPrimerIn, d0, d1, d2);
                k2 += this.rand.nextInt(4);
            }
            int l2 = 0;
            while (l2 < k2) {
                float f2 = this.rand.nextFloat() * (float)Math.PI * 2.0f;
                float f1 = (this.rand.nextFloat() - 0.5f) * 2.0f / 8.0f;
                float f22 = this.rand.nextFloat() * 2.0f + this.rand.nextFloat();
                this.func_180704_a(this.rand.nextLong(), p_180701_4_, p_180701_5_, chunkPrimerIn, d0, d1, d2, f22 * 2.0f, f2, f1, 0, 0, 0.5);
                ++l2;
            }
            ++j2;
        }
    }
}

