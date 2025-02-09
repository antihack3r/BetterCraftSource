// 
// Decompiled by Procyon v0.6.0
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import optifine.Config;
import optifine.Reflector;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityMinecart;

public class ModelAdapterMinecart extends ModelAdapter
{
    public ModelAdapterMinecart() {
        super(EntityMinecart.class, "minecart", 0.5f);
    }
    
    protected ModelAdapterMinecart(final Class entityClass, final String name, final float shadow) {
        super(entityClass, name, shadow);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelMinecart();
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelMinecart)) {
            return null;
        }
        final ModelMinecart modelminecart = (ModelMinecart)model;
        if (modelPart.equals("bottom")) {
            return modelminecart.sideModels[0];
        }
        if (modelPart.equals("back")) {
            return modelminecart.sideModels[1];
        }
        if (modelPart.equals("front")) {
            return modelminecart.sideModels[2];
        }
        if (modelPart.equals("right")) {
            return modelminecart.sideModels[3];
        }
        if (modelPart.equals("left")) {
            return modelminecart.sideModels[4];
        }
        return modelPart.equals("dirt") ? modelminecart.sideModels[5] : null;
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderMinecart renderminecart = new RenderMinecart(rendermanager);
        if (!Reflector.RenderMinecart_modelMinecart.exists()) {
            Config.warn("Field not found: RenderMinecart.modelMinecart");
            return null;
        }
        Reflector.setFieldValue(renderminecart, Reflector.RenderMinecart_modelMinecart, modelBase);
        renderminecart.shadowSize = shadowSize;
        return renderminecart;
    }
}
