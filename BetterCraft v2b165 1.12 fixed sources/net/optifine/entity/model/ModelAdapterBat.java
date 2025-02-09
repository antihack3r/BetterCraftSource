// 
// Decompiled by Procyon v0.6.0
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.Minecraft;
import optifine.Reflector;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityBat;

public class ModelAdapterBat extends ModelAdapter
{
    public ModelAdapterBat() {
        super(EntityBat.class, "bat", 0.25f);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelBat();
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelBat)) {
            return null;
        }
        final ModelBat modelbat = (ModelBat)model;
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 0);
        }
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 1);
        }
        if (modelPart.equals("right_wing")) {
            return (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 2);
        }
        if (modelPart.equals("left_wing")) {
            return (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 3);
        }
        if (modelPart.equals("outer_right_wing")) {
            return (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 4);
        }
        return modelPart.equals("outer_left_wing") ? ((ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 5)) : null;
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderBat renderbat = new RenderBat(rendermanager);
        renderbat.mainModel = modelBase;
        renderbat.shadowSize = shadowSize;
        return renderbat;
    }
}
