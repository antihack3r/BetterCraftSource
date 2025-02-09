// 
// Decompiled by Procyon v0.6.0
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.monster.EntityIronGolem;

public class ModelAdapterIronGolem extends ModelAdapter
{
    public ModelAdapterIronGolem() {
        super(EntityIronGolem.class, "iron_golem", 0.5f);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelIronGolem();
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelIronGolem)) {
            return null;
        }
        final ModelIronGolem modelirongolem = (ModelIronGolem)model;
        if (modelPart.equals("head")) {
            return modelirongolem.ironGolemHead;
        }
        if (modelPart.equals("body")) {
            return modelirongolem.ironGolemBody;
        }
        if (modelPart.equals("left_arm")) {
            return modelirongolem.ironGolemLeftArm;
        }
        if (modelPart.equals("right_arm")) {
            return modelirongolem.ironGolemRightArm;
        }
        if (modelPart.equals("left_leg")) {
            return modelirongolem.ironGolemLeftLeg;
        }
        return modelPart.equals("right_leg") ? modelirongolem.ironGolemRightLeg : null;
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderIronGolem renderirongolem = new RenderIronGolem(rendermanager);
        renderirongolem.mainModel = modelBase;
        renderirongolem.shadowSize = shadowSize;
        return renderirongolem;
    }
}
