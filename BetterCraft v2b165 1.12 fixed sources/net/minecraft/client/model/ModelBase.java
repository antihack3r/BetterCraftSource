// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.List;

public abstract class ModelBase
{
    public float swingProgress;
    public boolean isRiding;
    public boolean isChild;
    public List<ModelRenderer> boxList;
    private final Map<String, TextureOffset> modelTextureMap;
    public int textureWidth;
    public int textureHeight;
    
    public ModelBase() {
        this.isChild = true;
        this.boxList = (List<ModelRenderer>)Lists.newArrayList();
        this.modelTextureMap = (Map<String, TextureOffset>)Maps.newHashMap();
        this.textureWidth = 64;
        this.textureHeight = 32;
    }
    
    public void render(final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
    }
    
    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
    }
    
    public void setLivingAnimations(final EntityLivingBase entitylivingbaseIn, final float p_78086_2_, final float p_78086_3_, final float partialTickTime) {
    }
    
    public ModelRenderer getRandomModelBox(final Random rand) {
        return this.boxList.get(rand.nextInt(this.boxList.size()));
    }
    
    protected void setTextureOffset(final String partName, final int x, final int y) {
        this.modelTextureMap.put(partName, new TextureOffset(x, y));
    }
    
    public TextureOffset getTextureOffset(final String partName) {
        return this.modelTextureMap.get(partName);
    }
    
    public static void copyModelAngles(final ModelRenderer source, final ModelRenderer dest) {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }
    
    public void setModelAttributes(final ModelBase model) {
        this.swingProgress = model.swingProgress;
        this.isRiding = model.isRiding;
        this.isChild = model.isChild;
    }
}
