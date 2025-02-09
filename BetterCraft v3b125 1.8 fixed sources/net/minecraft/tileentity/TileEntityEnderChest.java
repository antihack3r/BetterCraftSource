/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityEnderChest
extends TileEntity
implements ITickable {
    public float lidAngle;
    public float prevLidAngle;
    public int numPlayersUsing;
    private int ticksSinceSync;

    @Override
    public void update() {
        if (++this.ticksSinceSync % 20 * 4 == 0) {
            this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
        }
        this.prevLidAngle = this.lidAngle;
        int i2 = this.pos.getX();
        int j2 = this.pos.getY();
        int k2 = this.pos.getZ();
        float f2 = 0.1f;
        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0f) {
            double d0 = (double)i2 + 0.5;
            double d1 = (double)k2 + 0.5;
            this.worldObj.playSoundEffect(d0, (double)j2 + 0.5, d1, "random.chestopen", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
        }
        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0f || this.numPlayersUsing > 0 && this.lidAngle < 1.0f) {
            float f1;
            float f22 = this.lidAngle;
            this.lidAngle = this.numPlayersUsing > 0 ? (this.lidAngle += f2) : (this.lidAngle -= f2);
            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f;
            }
            if (this.lidAngle < (f1 = 0.5f) && f22 >= f1) {
                double d3 = (double)i2 + 0.5;
                double d2 = (double)k2 + 0.5;
                this.worldObj.playSoundEffect(d3, (double)j2 + 0.5, d2, "random.chestclosed", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
            }
            if (this.lidAngle < 0.0f) {
                this.lidAngle = 0.0f;
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int id2, int type) {
        if (id2 == 1) {
            this.numPlayersUsing = type;
            return true;
        }
        return super.receiveClientEvent(id2, type);
    }

    @Override
    public void invalidate() {
        this.updateContainingBlockInfo();
        super.invalidate();
    }

    public void openChest() {
        ++this.numPlayersUsing;
        this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
    }

    public void closeChest() {
        --this.numPlayersUsing;
        this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
    }

    public boolean canBeUsed(EntityPlayer p_145971_1_) {
        return this.worldObj.getTileEntity(this.pos) != this ? false : p_145971_1_.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }
}

