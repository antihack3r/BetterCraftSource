// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.entity.monster;

import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import javax.annotation.Nullable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

public class EntityStray extends AbstractSkeleton
{
    public EntityStray(final World p_i47281_1_) {
        super(p_i47281_1_);
    }
    
    public static void func_190728_b(final DataFixer p_190728_0_) {
        EntityLiving.registerFixesMob(p_190728_0_, EntityStray.class);
    }
    
    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && this.world.canSeeSky(new BlockPos(this));
    }
    
    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_STRAY;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_STRAY_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource p_184601_1_) {
        return SoundEvents.ENTITY_STRAY_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRAY_DEATH;
    }
    
    @Override
    SoundEvent func_190727_o() {
        return SoundEvents.ENTITY_STRAY_STEP;
    }
    
    @Override
    protected EntityArrow func_190726_a(final float p_190726_1_) {
        final EntityArrow entityarrow = super.func_190726_a(p_190726_1_);
        if (entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow)entityarrow).addEffect(new PotionEffect(MobEffects.SLOWNESS, 600));
        }
        return entityarrow;
    }
}
