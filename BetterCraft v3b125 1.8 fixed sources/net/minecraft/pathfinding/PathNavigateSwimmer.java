/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer
extends PathNavigate {
    public PathNavigateSwimmer(EntityLiving entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    @Override
    protected PathFinder getPathFinder() {
        return new PathFinder(new SwimNodeProcessor());
    }

    @Override
    protected boolean canNavigate() {
        return this.isInLiquid();
    }

    @Override
    protected Vec3 getEntityPosition() {
        return new Vec3(this.theEntity.posX, this.theEntity.posY + (double)this.theEntity.height * 0.5, this.theEntity.posZ);
    }

    @Override
    protected void pathFollow() {
        Vec3 vec3 = this.getEntityPosition();
        float f2 = this.theEntity.width * this.theEntity.width;
        int i2 = 6;
        if (vec3.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < (double)f2) {
            this.currentPath.incrementPathIndex();
        }
        int j2 = Math.min(this.currentPath.getCurrentPathIndex() + i2, this.currentPath.getCurrentPathLength() - 1);
        while (j2 > this.currentPath.getCurrentPathIndex()) {
            Vec3 vec31 = this.currentPath.getVectorFromIndex(this.theEntity, j2);
            if (vec31.squareDistanceTo(vec3) <= 36.0 && this.isDirectPathBetweenPoints(vec3, vec31, 0, 0, 0)) {
                this.currentPath.setCurrentPathIndex(j2);
                break;
            }
            --j2;
        }
        this.checkForStuck(vec3);
    }

    @Override
    protected void removeSunnyPath() {
        super.removeSunnyPath();
    }

    @Override
    protected boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(posVec31, new Vec3(posVec32.xCoord, posVec32.yCoord + (double)this.theEntity.height * 0.5, posVec32.zCoord), false, true, false);
        return movingobjectposition == null || movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
    }
}

