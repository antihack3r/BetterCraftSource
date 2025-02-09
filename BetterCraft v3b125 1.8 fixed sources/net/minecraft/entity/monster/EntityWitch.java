/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.monster;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWitch
extends EntityMob
implements IRangedAttackMob {
    private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier MODIFIER = new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.25, 0).setSaved(false);
    private static final Item[] witchDrops = new Item[]{Items.glowstone_dust, Items.sugar, Items.redstone, Items.spider_eye, Items.glass_bottle, Items.gunpowder, Items.stick, Items.stick};
    private int witchAttackTimer;

    public EntityWitch(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 1.95f);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0, 60, 10.0f));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>((EntityCreature)this, EntityPlayer.class, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataWatcher().addObject(21, (byte)0);
    }

    @Override
    protected String getLivingSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return null;
    }

    @Override
    protected String getDeathSound() {
        return null;
    }

    public void setAggressive(boolean aggressive) {
        this.getDataWatcher().updateObject(21, (byte)(aggressive ? 1 : 0));
    }

    public boolean getAggressive() {
        return this.getDataWatcher().getWatchableObjectByte(21) == 1;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(26.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote) {
            if (this.getAggressive()) {
                if (this.witchAttackTimer-- <= 0) {
                    List<PotionEffect> list;
                    this.setAggressive(false);
                    ItemStack itemstack = this.getHeldItem();
                    this.setCurrentItemOrArmor(0, null);
                    if (itemstack != null && itemstack.getItem() == Items.potionitem && (list = Items.potionitem.getEffects(itemstack)) != null) {
                        for (PotionEffect potioneffect : list) {
                            this.addPotionEffect(new PotionEffect(potioneffect));
                        }
                    }
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(MODIFIER);
                }
            } else {
                int i2 = -1;
                if (this.rand.nextFloat() < 0.15f && this.isInsideOfMaterial(Material.water) && !this.isPotionActive(Potion.waterBreathing)) {
                    i2 = 8237;
                } else if (this.rand.nextFloat() < 0.15f && this.isBurning() && !this.isPotionActive(Potion.fireResistance)) {
                    i2 = 16307;
                } else if (this.rand.nextFloat() < 0.05f && this.getHealth() < this.getMaxHealth()) {
                    i2 = 16341;
                } else if (this.rand.nextFloat() < 0.25f && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0) {
                    i2 = 16274;
                } else if (this.rand.nextFloat() < 0.25f && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0) {
                    i2 = 16274;
                }
                if (i2 > -1) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Items.potionitem, 1, i2));
                    this.witchAttackTimer = this.getHeldItem().getMaxItemUseDuration();
                    this.setAggressive(true);
                    IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                    iattributeinstance.removeModifier(MODIFIER);
                    iattributeinstance.applyModifier(MODIFIER);
                }
            }
            if (this.rand.nextFloat() < 7.5E-4f) {
                this.worldObj.setEntityState(this, (byte)15);
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public void handleStatusUpdate(byte id2) {
        if (id2 == 15) {
            int i2 = 0;
            while (i2 < this.rand.nextInt(35) + 10) {
                this.worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + this.rand.nextGaussian() * (double)0.13f, this.getEntityBoundingBox().maxY + 0.5 + this.rand.nextGaussian() * (double)0.13f, this.posZ + this.rand.nextGaussian() * (double)0.13f, 0.0, 0.0, 0.0, new int[0]);
                ++i2;
            }
        } else {
            super.handleStatusUpdate(id2);
        }
    }

    @Override
    protected float applyPotionDamageCalculations(DamageSource source, float damage) {
        damage = super.applyPotionDamageCalculations(source, damage);
        if (source.getEntity() == this) {
            damage = 0.0f;
        }
        if (source.isMagicDamage()) {
            damage = (float)((double)damage * 0.15);
        }
        return damage;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int i2 = this.rand.nextInt(3) + 1;
        int j2 = 0;
        while (j2 < i2) {
            int k2 = this.rand.nextInt(3);
            Item item = witchDrops[this.rand.nextInt(witchDrops.length)];
            if (lootingModifier > 0) {
                k2 += this.rand.nextInt(lootingModifier + 1);
            }
            int l2 = 0;
            while (l2 < k2) {
                this.dropItem(item, 1);
                ++l2;
            }
            ++j2;
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_) {
        if (!this.getAggressive()) {
            EntityPotion entitypotion = new EntityPotion(this.worldObj, (EntityLivingBase)this, 32732);
            double d0 = target.posY + (double)target.getEyeHeight() - (double)1.1f;
            entitypotion.rotationPitch -= -20.0f;
            double d1 = target.posX + target.motionX - this.posX;
            double d2 = d0 - this.posY;
            double d3 = target.posZ + target.motionZ - this.posZ;
            float f2 = MathHelper.sqrt_double(d1 * d1 + d3 * d3);
            if (f2 >= 8.0f && !target.isPotionActive(Potion.moveSlowdown)) {
                entitypotion.setPotionDamage(32698);
            } else if (target.getHealth() >= 8.0f && !target.isPotionActive(Potion.poison)) {
                entitypotion.setPotionDamage(32660);
            } else if (f2 <= 3.0f && !target.isPotionActive(Potion.weakness) && this.rand.nextFloat() < 0.25f) {
                entitypotion.setPotionDamage(32696);
            }
            entitypotion.setThrowableHeading(d1, d2 + (double)(f2 * 0.2f), d3, 0.75f, 8.0f);
            this.worldObj.spawnEntityInWorld(entitypotion);
        }
    }

    @Override
    public float getEyeHeight() {
        return 1.62f;
    }
}

