// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemBow;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.EntityMob;

public class EntityAIAttackRangedBow<T extends EntityMob & IRangedAttackMob> extends EntityAIBase
{
    private final T entity;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime;
    
    public EntityAIAttackRangedBow(final T p_i47515_1_, final double p_i47515_2_, final int p_i47515_4_, final float p_i47515_5_) {
        this.attackTime = -1;
        this.strafingTime = -1;
        this.entity = p_i47515_1_;
        this.moveSpeedAmp = p_i47515_2_;
        this.attackCooldown = p_i47515_4_;
        this.maxAttackDistance = p_i47515_5_ * p_i47515_5_;
        this.setMutexBits(3);
    }
    
    public void setAttackCooldown(final int p_189428_1_) {
        this.attackCooldown = p_189428_1_;
    }
    
    @Override
    public boolean shouldExecute() {
        return this.entity.getAttackTarget() != null && this.isBowInMainhand();
    }
    
    protected boolean isBowInMainhand() {
        return !this.entity.getHeldItemMainhand().func_190926_b() && this.entity.getHeldItemMainhand().getItem() == Items.BOW;
    }
    
    @Override
    public boolean continueExecuting() {
        return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && this.isBowInMainhand();
    }
    
    @Override
    public void startExecuting() {
        super.startExecuting();
        this.entity.setSwingingArms(true);
    }
    
    @Override
    public void resetTask() {
        super.resetTask();
        this.entity.setSwingingArms(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
    }
    
    @Override
    public void updateTask() {
        final EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
        if (entitylivingbase != null) {
            final double d0 = this.entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
            final boolean flag = this.entity.getEntitySenses().canSee(entitylivingbase);
            final boolean flag2 = this.seeTime > 0;
            if (flag != flag2) {
                this.seeTime = 0;
            }
            if (flag) {
                ++this.seeTime;
            }
            else {
                --this.seeTime;
            }
            if (d0 <= this.maxAttackDistance && this.seeTime >= 20) {
                this.entity.getNavigator().clearPathEntity();
                ++this.strafingTime;
            }
            else {
                this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
                this.strafingTime = -1;
            }
            if (this.strafingTime >= 20) {
                if (this.entity.getRNG().nextFloat() < 0.3) {
                    this.strafingClockwise = !this.strafingClockwise;
                }
                if (this.entity.getRNG().nextFloat() < 0.3) {
                    this.strafingBackwards = !this.strafingBackwards;
                }
                this.strafingTime = 0;
            }
            if (this.strafingTime > -1) {
                if (d0 > this.maxAttackDistance * 0.75f) {
                    this.strafingBackwards = false;
                }
                else if (d0 < this.maxAttackDistance * 0.25f) {
                    this.strafingBackwards = true;
                }
                this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5f : 0.5f, this.strafingClockwise ? 0.5f : -0.5f);
                this.entity.faceEntity(entitylivingbase, 30.0f, 30.0f);
            }
            else {
                this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0f, 30.0f);
            }
            if (this.entity.isHandActive()) {
                if (!flag && this.seeTime < -60) {
                    this.entity.resetActiveHand();
                }
                else if (flag) {
                    final int i = this.entity.getItemInUseMaxCount();
                    if (i >= 20) {
                        this.entity.resetActiveHand();
                        this.entity.attackEntityWithRangedAttack(entitylivingbase, ItemBow.getArrowVelocity(i));
                        this.attackTime = this.attackCooldown;
                    }
                }
            }
            else {
                final int attackTime = this.attackTime - 1;
                this.attackTime = attackTime;
                if (attackTime <= 0 && this.seeTime >= -60) {
                    this.entity.setActiveHand(EnumHand.MAIN_HAND);
                }
            }
        }
    }
}
