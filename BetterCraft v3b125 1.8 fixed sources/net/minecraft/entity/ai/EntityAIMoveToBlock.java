/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityAIMoveToBlock
extends EntityAIBase {
    private final EntityCreature theEntity;
    private final double movementSpeed;
    protected int runDelay;
    private int timeoutCounter;
    private int field_179490_f;
    protected BlockPos destinationBlock = BlockPos.ORIGIN;
    private boolean isAboveDestination;
    private int searchLength;

    public EntityAIMoveToBlock(EntityCreature creature, double speedIn, int length) {
        this.theEntity = creature;
        this.movementSpeed = speedIn;
        this.searchLength = length;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        if (this.runDelay > 0) {
            --this.runDelay;
            return false;
        }
        this.runDelay = 200 + this.theEntity.getRNG().nextInt(200);
        return this.searchForDestination();
    }

    @Override
    public boolean continueExecuting() {
        return this.timeoutCounter >= -this.field_179490_f && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.theEntity.worldObj, this.destinationBlock);
    }

    @Override
    public void startExecuting() {
        this.theEntity.getNavigator().tryMoveToXYZ((double)this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, (double)this.destinationBlock.getZ() + 0.5, this.movementSpeed);
        this.timeoutCounter = 0;
        this.field_179490_f = this.theEntity.getRNG().nextInt(this.theEntity.getRNG().nextInt(1200) + 1200) + 1200;
    }

    @Override
    public void resetTask() {
    }

    @Override
    public void updateTask() {
        if (this.theEntity.getDistanceSqToCenter(this.destinationBlock.up()) > 1.0) {
            this.isAboveDestination = false;
            ++this.timeoutCounter;
            if (this.timeoutCounter % 40 == 0) {
                this.theEntity.getNavigator().tryMoveToXYZ((double)this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, (double)this.destinationBlock.getZ() + 0.5, this.movementSpeed);
            }
        } else {
            this.isAboveDestination = true;
            --this.timeoutCounter;
        }
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestination;
    }

    private boolean searchForDestination() {
        int i2 = this.searchLength;
        boolean j2 = true;
        BlockPos blockpos = new BlockPos(this.theEntity);
        int k2 = 0;
        while (k2 <= 1) {
            int l2 = 0;
            while (l2 < i2) {
                int i1 = 0;
                while (i1 <= l2) {
                    int j1 = i1 < l2 && i1 > -l2 ? l2 : 0;
                    while (j1 <= l2) {
                        BlockPos blockpos1 = blockpos.add(i1, k2 - 1, j1);
                        if (this.theEntity.isWithinHomeDistanceFromPosition(blockpos1) && this.shouldMoveTo(this.theEntity.worldObj, blockpos1)) {
                            this.destinationBlock = blockpos1;
                            return true;
                        }
                        int n2 = j1 = j1 > 0 ? -j1 : 1 - j1;
                    }
                    int n3 = i1 = i1 > 0 ? -i1 : 1 - i1;
                }
                ++l2;
            }
            int n4 = k2 = k2 > 0 ? -k2 : 1 - k2;
        }
        return false;
    }

    protected abstract boolean shouldMoveTo(World var1, BlockPos var2);
}

