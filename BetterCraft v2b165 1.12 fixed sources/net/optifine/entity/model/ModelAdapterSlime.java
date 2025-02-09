// 
// Decompiled by Procyon v0.6.0
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.client.Minecraft;
import optifine.Reflector;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.monster.EntitySlime;

public class ModelAdapterSlime extends ModelAdapter
{
    public ModelAdapterSlime() {
        super(EntitySlime.class, "slime", 0.25f);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelSlime(16);
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelSlime)) {
            return null;
        }
        final ModelSlime modelslime = (ModelSlime)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(modelslime, Reflector.ModelSlime_ModelRenderers, 0);
        }
        if (modelPart.equals("left_eye")) {
            return (ModelRenderer)Reflector.getFieldValue(modelslime, Reflector.ModelSlime_ModelRenderers, 1);
        }
        if (modelPart.equals("right_eye")) {
            return (ModelRenderer)Reflector.getFieldValue(modelslime, Reflector.ModelSlime_ModelRenderers, 2);
        }
        return modelPart.equals("mouth") ? ((ModelRenderer)Reflector.getFieldValue(modelslime, Reflector.ModelSlime_ModelRenderers, 3)) : null;
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderSlime renderslime = new RenderSlime(rendermanager);
        renderslime.mainModel = modelBase;
        renderslime.shadowSize = shadowSize;
        return renderslime;
    }
}
