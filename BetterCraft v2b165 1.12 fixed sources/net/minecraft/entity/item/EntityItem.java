// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import org.apache.logging.log4j.LogManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.Entity;

public class EntityItem extends Entity
{
    private static final Logger LOGGER;
    public static final DataParameter<ItemStack> ITEM;
    private int age;
    private int delayBeforeCanPickup;
    private int health;
    private String thrower;
    private String owner;
    public float hoverStart;
    
    static {
        LOGGER = LogManager.getLogger();
        ITEM = EntityDataManager.createKey(EntityItem.class, DataSerializers.OPTIONAL_ITEM_STACK);
    }
    
    public EntityItem(final World worldIn, final double x, final double y, final double z) {
        super(worldIn);
        this.health = 5;
        this.hoverStart = (float)(Math.random() * 3.141592653589793 * 2.0);
        this.setSize(0.25f, 0.25f);
        this.setPosition(x, y, z);
        this.rotationYaw = (float)(Math.random() * 360.0);
        this.motionX = (float)(Math.random() * 0.20000000298023224 - 0.10000000149011612);
        this.motionY = 0.20000000298023224;
        this.motionZ = (float)(Math.random() * 0.20000000298023224 - 0.10000000149011612);
    }
    
    public EntityItem(final World worldIn, final double x, final double y, final double z, final ItemStack stack) {
        this(worldIn, x, y, z);
        this.setEntityItemStack(stack);
    }
    
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public EntityItem(final World worldIn) {
        super(worldIn);
        this.health = 5;
        this.hoverStart = (float)(Math.random() * 3.141592653589793 * 2.0);
        this.setSize(0.25f, 0.25f);
        this.setEntityItemStack(ItemStack.field_190927_a);
    }
    
    @Override
    protected void entityInit() {
        this.getDataManager().register(EntityItem.ITEM, ItemStack.field_190927_a);
    }
    
    @Override
    public void onUpdate() {
        if (this.getEntityItem().func_190926_b()) {
            this.setDead();
        }
        else {
            super.onUpdate();
            if (this.delayBeforeCanPickup > 0 && this.delayBeforeCanPickup != 32767) {
                --this.delayBeforeCanPickup;
            }
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            final double d0 = this.motionX;
            final double d2 = this.motionY;
            final double d3 = this.motionZ;
            if (!this.hasNoGravity()) {
                this.motionY -= 0.03999999910593033;
            }
            if (this.world.isRemote) {
                this.noClip = false;
            }
            else {
                this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0, this.posZ);
            }
            this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            final boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
            if (flag || this.ticksExisted % 25 == 0) {
                if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
                    this.motionY = 0.20000000298023224;
                    this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f;
                    this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f;
                    this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + this.rand.nextFloat() * 0.4f);
                }
                if (!this.world.isRemote) {
                    this.searchForOtherItemsNearby();
                }
            }
            float f = 0.98f;
            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.98f;
            }
            this.motionX *= f;
            this.motionY *= 0.9800000190734863;
            this.motionZ *= f;
            if (this.onGround) {
                this.motionY *= -0.5;
            }
            if (this.age != -32768) {
                ++this.age;
            }
            this.handleWaterMovement();
            if (!this.world.isRemote) {
                final double d4 = this.motionX - d0;
                final double d5 = this.motionY - d2;
                final double d6 = this.motionZ - d3;
                final double d7 = d4 * d4 + d5 * d5 + d6 * d6;
                if (d7 > 0.01) {
                    this.isAirBorne = true;
                }
            }
            if (!this.world.isRemote && this.age >= 6000) {
                this.setDead();
            }
        }
    }
    
    private void searchForOtherItemsNearby() {
        for (final EntityItem entityitem : this.world.getEntitiesWithinAABB((Class<? extends EntityItem>)EntityItem.class, this.getEntityBoundingBox().expand(0.5, 0.0, 0.5))) {
            this.combineItems(entityitem);
        }
    }
    
    private boolean combineItems(final EntityItem other) {
        if (other == this) {
            return false;
        }
        if (!other.isEntityAlive() || !this.isEntityAlive()) {
            return false;
        }
        final ItemStack itemstack = this.getEntityItem();
        final ItemStack itemstack2 = other.getEntityItem();
        if (this.delayBeforeCanPickup == 32767 || other.delayBeforeCanPickup == 32767) {
            return false;
        }
        if (this.age == -32768 || other.age == -32768) {
            return false;
        }
        if (itemstack2.getItem() != itemstack.getItem()) {
            return false;
        }
        if (itemstack2.hasTagCompound() ^ itemstack.hasTagCompound()) {
            return false;
        }
        if (itemstack2.hasTagCompound() && !itemstack2.getTagCompound().equals(itemstack.getTagCompound())) {
            return false;
        }
        if (itemstack2.getItem() == null) {
            return false;
        }
        if (itemstack2.getItem().getHasSubtypes() && itemstack2.getMetadata() != itemstack.getMetadata()) {
            return false;
        }
        if (itemstack2.func_190916_E() < itemstack.func_190916_E()) {
            return other.combineItems(this);
        }
        if (itemstack2.func_190916_E() + itemstack.func_190916_E() > itemstack2.getMaxStackSize()) {
            return false;
        }
        itemstack2.func_190917_f(itemstack.func_190916_E());
        other.delayBeforeCanPickup = Math.max(other.delayBeforeCanPickup, this.delayBeforeCanPickup);
        other.age = Math.min(other.age, this.age);
        other.setEntityItemStack(itemstack2);
        this.setDead();
        return true;
    }
    
    public void setAgeToCreativeDespawnTime() {
        this.age = 4800;
    }
    
    @Override
    public boolean handleWaterMovement() {
        if (this.world.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this)) {
            if (!this.inWater && !this.firstUpdate) {
                this.resetHeight();
            }
            this.inWater = true;
        }
        else {
            this.inWater = false;
        }
        return this.inWater;
    }
    
    @Override
    protected void dealFireDamage(final int amount) {
        this.attackEntityFrom(DamageSource.inFire, (float)amount);
    }
    
    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (!this.getEntityItem().func_190926_b() && this.getEntityItem().getItem() == Items.NETHER_STAR && source.isExplosion()) {
            return false;
        }
        this.setBeenAttacked();
        this.health -= (int)amount;
        if (this.health <= 0) {
            this.setDead();
        }
        return false;
    }
    
    public static void registerFixesItem(final DataFixer fixer) {
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityItem.class, new String[] { "Item" }));
    }
    
    public void writeEntityToNBT(final NBTTagCompound compound) {
        compound.setShort("Health", (short)this.health);
        compound.setShort("Age", (short)this.age);
        compound.setShort("PickupDelay", (short)this.delayBeforeCanPickup);
        if (this.getThrower() != null) {
            compound.setString("Thrower", this.thrower);
        }
        if (this.getOwner() != null) {
            compound.setString("Owner", this.owner);
        }
        if (!this.getEntityItem().func_190926_b()) {
            compound.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
        }
    }
    
    public void readEntityFromNBT(final NBTTagCompound compound) {
        this.health = compound.getShort("Health");
        this.age = compound.getShort("Age");
        if (compound.hasKey("PickupDelay")) {
            this.delayBeforeCanPickup = compound.getShort("PickupDelay");
        }
        if (compound.hasKey("Owner")) {
            this.owner = compound.getString("Owner");
        }
        if (compound.hasKey("Thrower")) {
            this.thrower = compound.getString("Thrower");
        }
        final NBTTagCompound nbttagcompound = compound.getCompoundTag("Item");
        this.setEntityItemStack(new ItemStack(nbttagcompound));
        if (this.getEntityItem().func_190926_b()) {
            this.setDead();
        }
    }
    
    @Override
    public void onCollideWithPlayer(final EntityPlayer entityIn) {
        if (!this.world.isRemote) {
            final ItemStack itemstack = this.getEntityItem();
            final Item item = itemstack.getItem();
            final int i = itemstack.func_190916_E();
            if (this.delayBeforeCanPickup == 0 && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(entityIn.getName())) && entityIn.inventory.addItemStackToInventory(itemstack)) {
                entityIn.onItemPickup(this, i);
                if (itemstack.func_190926_b()) {
                    this.setDead();
                    itemstack.func_190920_e(i);
                }
                entityIn.addStat(StatList.getObjectsPickedUpStats(item), i);
            }
        }
    }
    
    @Override
    public String getName() {
        return this.hasCustomName() ? this.getCustomNameTag() : I18n.translateToLocal("item." + this.getEntityItem().getUnlocalizedName());
    }
    
    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }
    
    @Nullable
    @Override
    public Entity changeDimension(final int dimensionIn) {
        final Entity entity = super.changeDimension(dimensionIn);
        if (!this.world.isRemote && entity instanceof EntityItem) {
            ((EntityItem)entity).searchForOtherItemsNearby();
        }
        return entity;
    }
    
    public ItemStack getEntityItem() {
        return this.getDataManager().get(EntityItem.ITEM);
    }
    
    public void setEntityItemStack(final ItemStack stack) {
        this.getDataManager().set(EntityItem.ITEM, stack);
        this.getDataManager().setDirty(EntityItem.ITEM);
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public String getThrower() {
        return this.thrower;
    }
    
    public void setThrower(final String thrower) {
        this.thrower = thrower;
    }
    
    public int getAge() {
        return this.age;
    }
    
    public void setDefaultPickupDelay() {
        this.delayBeforeCanPickup = 10;
    }
    
    public void setNoPickupDelay() {
        this.delayBeforeCanPickup = 0;
    }
    
    public void setInfinitePickupDelay() {
        this.delayBeforeCanPickup = 32767;
    }
    
    public void setPickupDelay(final int ticks) {
        this.delayBeforeCanPickup = ticks;
    }
    
    public boolean cannotPickup() {
        return this.delayBeforeCanPickup > 0;
    }
    
    public void setNoDespawn() {
        this.age = -6000;
    }
    
    public void makeFakeItem() {
        this.setInfinitePickupDelay();
        this.age = 5999;
    }
}
