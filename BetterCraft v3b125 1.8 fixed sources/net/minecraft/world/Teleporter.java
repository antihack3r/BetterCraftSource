/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;

public class Teleporter {
    private final WorldServer worldServerInstance;
    private final Random random;
    private final LongHashMap<PortalPosition> destinationCoordinateCache = new LongHashMap();
    private final List<Long> destinationCoordinateKeys = Lists.newArrayList();

    public Teleporter(WorldServer worldIn) {
        this.worldServerInstance = worldIn;
        this.random = new Random(worldIn.getSeed());
    }

    public void placeInPortal(Entity entityIn, float rotationYaw) {
        if (this.worldServerInstance.provider.getDimensionId() != 1) {
            if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
                this.makePortal(entityIn);
                this.placeInExistingPortal(entityIn, rotationYaw);
            }
        } else {
            int i2 = MathHelper.floor_double(entityIn.posX);
            int j2 = MathHelper.floor_double(entityIn.posY) - 1;
            int k2 = MathHelper.floor_double(entityIn.posZ);
            int l2 = 1;
            int i1 = 0;
            int j1 = -2;
            while (j1 <= 2) {
                int k1 = -2;
                while (k1 <= 2) {
                    int l1 = -1;
                    while (l1 < 3) {
                        int i22 = i2 + k1 * l2 + j1 * i1;
                        int j22 = j2 + l1;
                        int k22 = k2 + k1 * i1 - j1 * l2;
                        boolean flag = l1 < 0;
                        this.worldServerInstance.setBlockState(new BlockPos(i22, j22, k22), flag ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
                        ++l1;
                    }
                    ++k1;
                }
                ++j1;
            }
            entityIn.setLocationAndAngles(i2, j2, k2, entityIn.rotationYaw, 0.0f);
            entityIn.motionZ = 0.0;
            entityIn.motionY = 0.0;
            entityIn.motionX = 0.0;
        }
    }

    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
        int i2 = 128;
        double d0 = -1.0;
        int j2 = MathHelper.floor_double(entityIn.posX);
        int k2 = MathHelper.floor_double(entityIn.posZ);
        boolean flag = true;
        BlockPos blockpos = BlockPos.ORIGIN;
        long l2 = ChunkCoordIntPair.chunkXZ2Int(j2, k2);
        if (this.destinationCoordinateCache.containsItem(l2)) {
            PortalPosition teleporter$portalposition = this.destinationCoordinateCache.getValueByKey(l2);
            d0 = 0.0;
            blockpos = teleporter$portalposition;
            teleporter$portalposition.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
            flag = false;
        } else {
            BlockPos blockpos3 = new BlockPos(entityIn);
            int i1 = -128;
            while (i1 <= 128) {
                int j1 = -128;
                while (j1 <= 128) {
                    BlockPos blockpos1 = blockpos3.add(i1, this.worldServerInstance.getActualHeight() - 1 - blockpos3.getY(), j1);
                    while (blockpos1.getY() >= 0) {
                        BlockPos blockpos2 = blockpos1.down();
                        if (this.worldServerInstance.getBlockState(blockpos1).getBlock() == Blocks.portal) {
                            while (this.worldServerInstance.getBlockState(blockpos2 = blockpos1.down()).getBlock() == Blocks.portal) {
                                blockpos1 = blockpos2;
                            }
                            double d1 = blockpos1.distanceSq(blockpos3);
                            if (d0 < 0.0 || d1 < d0) {
                                d0 = d1;
                                blockpos = blockpos1;
                            }
                        }
                        blockpos1 = blockpos2;
                    }
                    ++j1;
                }
                ++i1;
            }
        }
        if (d0 >= 0.0) {
            if (flag) {
                this.destinationCoordinateCache.add(l2, new PortalPosition(blockpos, this.worldServerInstance.getTotalWorldTime()));
                this.destinationCoordinateKeys.add(l2);
            }
            double d5 = (double)blockpos.getX() + 0.5;
            double d6 = (double)blockpos.getY() + 0.5;
            double d7 = (double)blockpos.getZ() + 0.5;
            BlockPattern.PatternHelper blockpattern$patternhelper = Blocks.portal.func_181089_f(this.worldServerInstance, blockpos);
            boolean flag1 = blockpattern$patternhelper.getFinger().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
            double d2 = blockpattern$patternhelper.getFinger().getAxis() == EnumFacing.Axis.X ? (double)blockpattern$patternhelper.getPos().getZ() : (double)blockpattern$patternhelper.getPos().getX();
            d6 = (double)(blockpattern$patternhelper.getPos().getY() + 1) - entityIn.func_181014_aG().yCoord * (double)blockpattern$patternhelper.func_181119_e();
            if (flag1) {
                d2 += 1.0;
            }
            if (blockpattern$patternhelper.getFinger().getAxis() == EnumFacing.Axis.X) {
                d7 = d2 + (1.0 - entityIn.func_181014_aG().xCoord) * (double)blockpattern$patternhelper.func_181118_d() * (double)blockpattern$patternhelper.getFinger().rotateY().getAxisDirection().getOffset();
            } else {
                d5 = d2 + (1.0 - entityIn.func_181014_aG().xCoord) * (double)blockpattern$patternhelper.func_181118_d() * (double)blockpattern$patternhelper.getFinger().rotateY().getAxisDirection().getOffset();
            }
            float f2 = 0.0f;
            float f1 = 0.0f;
            float f22 = 0.0f;
            float f3 = 0.0f;
            if (blockpattern$patternhelper.getFinger().getOpposite() == entityIn.getTeleportDirection()) {
                f2 = 1.0f;
                f1 = 1.0f;
            } else if (blockpattern$patternhelper.getFinger().getOpposite() == entityIn.getTeleportDirection().getOpposite()) {
                f2 = -1.0f;
                f1 = -1.0f;
            } else if (blockpattern$patternhelper.getFinger().getOpposite() == entityIn.getTeleportDirection().rotateY()) {
                f22 = 1.0f;
                f3 = -1.0f;
            } else {
                f22 = -1.0f;
                f3 = 1.0f;
            }
            double d3 = entityIn.motionX;
            double d4 = entityIn.motionZ;
            entityIn.motionX = d3 * (double)f2 + d4 * (double)f3;
            entityIn.motionZ = d3 * (double)f22 + d4 * (double)f1;
            entityIn.rotationYaw = rotationYaw - (float)(entityIn.getTeleportDirection().getOpposite().getHorizontalIndex() * 90) + (float)(blockpattern$patternhelper.getFinger().getHorizontalIndex() * 90);
            entityIn.setLocationAndAngles(d5, d6, d7, entityIn.rotationYaw, entityIn.rotationPitch);
            return true;
        }
        return false;
    }

    public boolean makePortal(Entity entityIn) {
        int i2 = 16;
        double d0 = -1.0;
        int j2 = MathHelper.floor_double(entityIn.posX);
        int k2 = MathHelper.floor_double(entityIn.posY);
        int l2 = MathHelper.floor_double(entityIn.posZ);
        int i1 = j2;
        int j1 = k2;
        int k1 = l2;
        int l1 = 0;
        int i22 = this.random.nextInt(4);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int j22 = j2 - i2;
        while (j22 <= j2 + i2) {
            double d1 = (double)j22 + 0.5 - entityIn.posX;
            int l22 = l2 - i2;
            while (l22 <= l2 + i2) {
                double d2 = (double)l22 + 0.5 - entityIn.posZ;
                int j3 = this.worldServerInstance.getActualHeight() - 1;
                while (j3 >= 0) {
                    if (this.worldServerInstance.isAirBlock(blockpos$mutableblockpos.set(j22, j3, l22))) {
                        while (j3 > 0 && this.worldServerInstance.isAirBlock(blockpos$mutableblockpos.set(j22, j3 - 1, l22))) {
                            --j3;
                        }
                        int k3 = i22;
                        block4: while (k3 < i22 + 4) {
                            int l3 = k3 % 2;
                            int i4 = 1 - l3;
                            if (k3 % 4 >= 2) {
                                l3 = -l3;
                                i4 = -i4;
                            }
                            int j4 = 0;
                            while (j4 < 3) {
                                int k4 = 0;
                                while (k4 < 4) {
                                    int l4 = -1;
                                    while (l4 < 4) {
                                        int i5 = j22 + (k4 - 1) * l3 + j4 * i4;
                                        int j5 = j3 + l4;
                                        int k5 = l22 + (k4 - 1) * i4 - j4 * l3;
                                        blockpos$mutableblockpos.set(i5, j5, k5);
                                        if (l4 < 0 && !this.worldServerInstance.getBlockState(blockpos$mutableblockpos).getBlock().getMaterial().isSolid() || l4 >= 0 && !this.worldServerInstance.isAirBlock(blockpos$mutableblockpos)) break block4;
                                        ++l4;
                                    }
                                    ++k4;
                                }
                                ++j4;
                            }
                            double d5 = (double)j3 + 0.5 - entityIn.posY;
                            double d7 = d1 * d1 + d5 * d5 + d2 * d2;
                            if (d0 < 0.0 || d7 < d0) {
                                d0 = d7;
                                i1 = j22;
                                j1 = j3;
                                k1 = l22;
                                l1 = k3 % 4;
                            }
                            ++k3;
                        }
                    }
                    --j3;
                }
                ++l22;
            }
            ++j22;
        }
        if (d0 < 0.0) {
            int l5 = j2 - i2;
            while (l5 <= j2 + i2) {
                double d3 = (double)l5 + 0.5 - entityIn.posX;
                int j6 = l2 - i2;
                while (j6 <= l2 + i2) {
                    double d4 = (double)j6 + 0.5 - entityIn.posZ;
                    int i7 = this.worldServerInstance.getActualHeight() - 1;
                    while (i7 >= 0) {
                        if (this.worldServerInstance.isAirBlock(blockpos$mutableblockpos.set(l5, i7, j6))) {
                            while (i7 > 0 && this.worldServerInstance.isAirBlock(blockpos$mutableblockpos.set(l5, i7 - 1, j6))) {
                                --i7;
                            }
                            int k7 = i22;
                            block12: while (k7 < i22 + 2) {
                                int j8 = k7 % 2;
                                int j9 = 1 - j8;
                                int j10 = 0;
                                while (j10 < 4) {
                                    int j11 = -1;
                                    while (j11 < 4) {
                                        int j12 = l5 + (j10 - 1) * j8;
                                        int i13 = i7 + j11;
                                        int j13 = j6 + (j10 - 1) * j9;
                                        blockpos$mutableblockpos.set(j12, i13, j13);
                                        if (j11 < 0 && !this.worldServerInstance.getBlockState(blockpos$mutableblockpos).getBlock().getMaterial().isSolid() || j11 >= 0 && !this.worldServerInstance.isAirBlock(blockpos$mutableblockpos)) break block12;
                                        ++j11;
                                    }
                                    ++j10;
                                }
                                double d6 = (double)i7 + 0.5 - entityIn.posY;
                                double d8 = d3 * d3 + d6 * d6 + d4 * d4;
                                if (d0 < 0.0 || d8 < d0) {
                                    d0 = d8;
                                    i1 = l5;
                                    j1 = i7;
                                    k1 = j6;
                                    l1 = k7 % 2;
                                }
                                ++k7;
                            }
                        }
                        --i7;
                    }
                    ++j6;
                }
                ++l5;
            }
        }
        int i6 = i1;
        int k22 = j1;
        int k6 = k1;
        int l6 = l1 % 2;
        int i3 = 1 - l6;
        if (l1 % 4 >= 2) {
            l6 = -l6;
            i3 = -i3;
        }
        if (d0 < 0.0) {
            k22 = j1 = MathHelper.clamp_int(j1, 70, this.worldServerInstance.getActualHeight() - 10);
            int j7 = -1;
            while (j7 <= 1) {
                int l7 = 1;
                while (l7 < 3) {
                    int k8 = -1;
                    while (k8 < 3) {
                        int k9 = i6 + (l7 - 1) * l6 + j7 * i3;
                        int k10 = k22 + k8;
                        int k11 = k6 + (l7 - 1) * i3 - j7 * l6;
                        boolean flag = k8 < 0;
                        this.worldServerInstance.setBlockState(new BlockPos(k9, k10, k11), flag ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
                        ++k8;
                    }
                    ++l7;
                }
                ++j7;
            }
        }
        IBlockState iblockstate = Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, l6 != 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
        int i8 = 0;
        while (i8 < 4) {
            int l8 = 0;
            while (l8 < 4) {
                int l9 = -1;
                while (l9 < 4) {
                    int l10 = i6 + (l8 - 1) * l6;
                    int l11 = k22 + l9;
                    int k12 = k6 + (l8 - 1) * i3;
                    boolean flag1 = l8 == 0 || l8 == 3 || l9 == -1 || l9 == 3;
                    this.worldServerInstance.setBlockState(new BlockPos(l10, l11, k12), flag1 ? Blocks.obsidian.getDefaultState() : iblockstate, 2);
                    ++l9;
                }
                ++l8;
            }
            int i9 = 0;
            while (i9 < 4) {
                int i10 = -1;
                while (i10 < 4) {
                    int i11 = i6 + (i9 - 1) * l6;
                    int i12 = k22 + i10;
                    int l12 = k6 + (i9 - 1) * i3;
                    BlockPos blockpos = new BlockPos(i11, i12, l12);
                    this.worldServerInstance.notifyNeighborsOfStateChange(blockpos, this.worldServerInstance.getBlockState(blockpos).getBlock());
                    ++i10;
                }
                ++i9;
            }
            ++i8;
        }
        return true;
    }

    public void removeStalePortalLocations(long worldTime) {
        if (worldTime % 100L == 0L) {
            Iterator<Long> iterator = this.destinationCoordinateKeys.iterator();
            long i2 = worldTime - 300L;
            while (iterator.hasNext()) {
                Long olong = iterator.next();
                PortalPosition teleporter$portalposition = this.destinationCoordinateCache.getValueByKey(olong);
                if (teleporter$portalposition != null && teleporter$portalposition.lastUpdateTime >= i2) continue;
                iterator.remove();
                this.destinationCoordinateCache.remove(olong);
            }
        }
    }

    public class PortalPosition
    extends BlockPos {
        public long lastUpdateTime;

        public PortalPosition(BlockPos pos, long lastUpdate) {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.lastUpdateTime = lastUpdate;
        }
    }
}

