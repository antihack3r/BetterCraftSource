// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.monster.EntityZombieVillager;

public class RenderZombieVillager extends RenderBiped<EntityZombieVillager>
{
    private static final ResourceLocation ZOMBIE_VILLAGER_TEXTURES;
    private static final ResourceLocation ZOMBIE_VILLAGER_FARMER_LOCATION;
    private static final ResourceLocation ZOMBIE_VILLAGER_LIBRARIAN_LOC;
    private static final ResourceLocation ZOMBIE_VILLAGER_PRIEST_LOCATION;
    private static final ResourceLocation ZOMBIE_VILLAGER_SMITH_LOCATION;
    private static final ResourceLocation ZOMBIE_VILLAGER_BUTCHER_LOCATION;
    
    static {
        ZOMBIE_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");
        ZOMBIE_VILLAGER_FARMER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_farmer.png");
        ZOMBIE_VILLAGER_LIBRARIAN_LOC = new ResourceLocation("textures/entity/zombie_villager/zombie_librarian.png");
        ZOMBIE_VILLAGER_PRIEST_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_priest.png");
        ZOMBIE_VILLAGER_SMITH_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_smith.png");
        ZOMBIE_VILLAGER_BUTCHER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_butcher.png");
    }
    
    public RenderZombieVillager(final RenderManager p_i47186_1_) {
        super(p_i47186_1_, new ModelZombieVillager(), 0.5f);
        ((RenderLivingBase<EntityLivingBase>)this).addLayer(new LayerVillagerArmor(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityZombieVillager entity) {
        switch (entity.func_190736_dl()) {
            case 0: {
                return RenderZombieVillager.ZOMBIE_VILLAGER_FARMER_LOCATION;
            }
            case 1: {
                return RenderZombieVillager.ZOMBIE_VILLAGER_LIBRARIAN_LOC;
            }
            case 2: {
                return RenderZombieVillager.ZOMBIE_VILLAGER_PRIEST_LOCATION;
            }
            case 3: {
                return RenderZombieVillager.ZOMBIE_VILLAGER_SMITH_LOCATION;
            }
            case 4: {
                return RenderZombieVillager.ZOMBIE_VILLAGER_BUTCHER_LOCATION;
            }
            default: {
                return RenderZombieVillager.ZOMBIE_VILLAGER_TEXTURES;
            }
        }
    }
    
    @Override
    protected void rotateCorpse(final EntityZombieVillager entityLiving, final float p_77043_2_, float p_77043_3_, final float partialTicks) {
        if (entityLiving.isConverting()) {
            p_77043_3_ += (float)(Math.cos(entityLiving.ticksExisted * 3.25) * 3.141592653589793 * 0.25);
        }
        super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
    }
}
