// 
// Decompiled by Procyon v0.6.0
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderAbstractHorse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityDonkey;

public class ModelAdapterDonkey extends ModelAdapterHorse
{
    public ModelAdapterDonkey() {
        super(EntityDonkey.class, "donkey", 0.75f);
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderAbstractHorse renderabstracthorse = new RenderAbstractHorse(rendermanager);
        renderabstracthorse.mainModel = modelBase;
        renderabstracthorse.shadowSize = shadowSize;
        return renderabstracthorse;
    }
}
