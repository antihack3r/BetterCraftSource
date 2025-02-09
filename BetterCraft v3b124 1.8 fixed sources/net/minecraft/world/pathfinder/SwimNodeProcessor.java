/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;

public class SwimNodeProcessor
extends NodeProcessor {
    @Override
    public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn) {
        super.initProcessor(iblockaccessIn, entityIn);
    }

    @Override
    public void postProcess() {
        super.postProcess();
    }

    @Override
    public PathPoint getPathPointTo(Entity entityIn) {
        return this.openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5), MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
    }

    @Override
    public PathPoint getPathPointToCoords(Entity entityIn, double x2, double y2, double target) {
        return this.openPoint(MathHelper.floor_double(x2 - (double)(entityIn.width / 2.0f)), MathHelper.floor_double(y2 + 0.5), MathHelper.floor_double(target - (double)(entityIn.width / 2.0f)));
    }

    @Override
    public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int i2 = 0;
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n2 = enumFacingArray.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumFacing enumfacing = enumFacingArray[n3];
            PathPoint pathpoint = this.getSafePoint(entityIn, currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());
            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i2++] = pathpoint;
            }
            ++n3;
        }
        return i2;
    }

    private PathPoint getSafePoint(Entity entityIn, int x2, int y2, int z2) {
        int i2 = this.func_176186_b(entityIn, x2, y2, z2);
        return i2 == -1 ? this.openPoint(x2, y2, z2) : null;
    }

    private int func_176186_b(Entity entityIn, int x2, int y2, int z2) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int i2 = x2;
        while (i2 < x2 + this.entitySizeX) {
            int j2 = y2;
            while (j2 < y2 + this.entitySizeY) {
                int k2 = z2;
                while (k2 < z2 + this.entitySizeZ) {
                    Block block = this.blockaccess.getBlockState(blockpos$mutableblockpos.set(i2, j2, k2)).getBlock();
                    if (block.getMaterial() != Material.water) {
                        return 0;
                    }
                    ++k2;
                }
                ++j2;
            }
            ++i2;
        }
        return -1;
    }
}

