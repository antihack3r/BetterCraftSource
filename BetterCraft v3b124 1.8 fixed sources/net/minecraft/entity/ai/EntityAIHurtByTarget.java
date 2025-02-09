/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget
extends EntityAITarget {
    private boolean entityCallsForHelp;
    private int revengeTimerOld;
    private final Class[] targetClasses;

    public EntityAIHurtByTarget(EntityCreature creatureIn, boolean entityCallsForHelpIn, Class ... targetClassesIn) {
        super(creatureIn, false);
        this.entityCallsForHelp = entityCallsForHelpIn;
        this.targetClasses = targetClassesIn;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        int i2 = this.taskOwner.getRevengeTimer();
        return i2 != this.revengeTimerOld && this.isSuitableTarget(this.taskOwner.getAITarget(), false);
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
        this.revengeTimerOld = this.taskOwner.getRevengeTimer();
        if (this.entityCallsForHelp) {
            double d0 = this.getTargetDistance();
            for (EntityCreature entitycreature : this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), new AxisAlignedBB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0, this.taskOwner.posY + 1.0, this.taskOwner.posZ + 1.0).expand(d0, 10.0, d0))) {
                if (this.taskOwner == entitycreature || entitycreature.getAttackTarget() != null || entitycreature.isOnSameTeam(this.taskOwner.getAITarget())) continue;
                boolean flag = false;
                Class[] classArray = this.targetClasses;
                int n2 = this.targetClasses.length;
                int n3 = 0;
                while (n3 < n2) {
                    Class oclass = classArray[n3];
                    if (entitycreature.getClass() == oclass) {
                        flag = true;
                        break;
                    }
                    ++n3;
                }
                if (flag) continue;
                this.setEntityAttackTarget(entitycreature, this.taskOwner.getAITarget());
            }
        }
        super.startExecuting();
    }

    protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
        creatureIn.setAttackTarget(entityLivingBaseIn);
    }
}

