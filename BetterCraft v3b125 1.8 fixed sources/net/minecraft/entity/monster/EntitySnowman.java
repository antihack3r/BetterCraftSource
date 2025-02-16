/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.monster;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman
extends EntityGolem
implements IRangedAttackMob {
    public EntitySnowman(World worldIn) {
        super(worldIn);
        this.setSize(0.7f, 1.9f);
        ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25, 20, 10.0f));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<Entity>(this, EntityLiving.class, 10, true, false, IMob.mobSelector));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            int i2 = MathHelper.floor_double(this.posX);
            int j2 = MathHelper.floor_double(this.posY);
            int k2 = MathHelper.floor_double(this.posZ);
            if (this.isWet()) {
                this.attackEntityFrom(DamageSource.drown, 1.0f);
            }
            BlockPos blockPos = new BlockPos(i2, 0, k2);
            BlockPos blockPos2 = new BlockPos(i2, j2, k2);
            if (this.worldObj.getBiomeGenForCoords(blockPos).getFloatTemperature(blockPos2) > 1.0f) {
                this.attackEntityFrom(DamageSource.onFire, 1.0f);
            }
            int l2 = 0;
            while (l2 < 4) {
                i2 = MathHelper.floor_double(this.posX + (double)((float)(l2 % 2 * 2 - 1) * 0.25f));
                BlockPos blockpos = new BlockPos(i2, j2 = MathHelper.floor_double(this.posY), k2 = MathHelper.floor_double(this.posZ + (double)((float)(l2 / 2 % 2 * 2 - 1) * 0.25f)));
                if (this.worldObj.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
                    BlockPos blockPos3 = new BlockPos(i2, 0, k2);
                    if (this.worldObj.getBiomeGenForCoords(blockPos3).getFloatTemperature(blockpos) < 0.8f && Blocks.snow_layer.canPlaceBlockAt(this.worldObj, blockpos)) {
                        this.worldObj.setBlockState(blockpos, Blocks.snow_layer.getDefaultState());
                    }
                }
                ++l2;
            }
        }
    }

    @Override
    protected Item getDropItem() {
        return Items.snowball;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int i2 = this.rand.nextInt(16);
        int j2 = 0;
        while (j2 < i2) {
            this.dropItem(Items.snowball, 1);
            ++j2;
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_) {
        EntitySnowball entitysnowball = new EntitySnowball(this.worldObj, this);
        double d0 = target.posY + (double)target.getEyeHeight() - (double)1.1f;
        double d1 = target.posX - this.posX;
        double d2 = d0 - entitysnowball.posY;
        double d3 = target.posZ - this.posZ;
        float f2 = MathHelper.sqrt_double(d1 * d1 + d3 * d3) * 0.2f;
        entitysnowball.setThrowableHeading(d1, d2 + (double)f2, d3, 1.6f, 12.0f);
        this.playSound("random.bow", 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
        this.worldObj.spawnEntityInWorld(entitysnowball);
    }

    @Override
    public float getEyeHeight() {
        return 1.7f;
    }
}

