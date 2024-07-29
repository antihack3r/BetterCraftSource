/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.optifine.entity.model.CustomModelRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.entity.model.anim.IModelResolver;
import net.optifine.entity.model.anim.IRenderResolver;
import net.optifine.entity.model.anim.ModelVariableFloat;
import net.optifine.entity.model.anim.ModelVariableType;
import net.optifine.entity.model.anim.RenderResolverEntity;
import net.optifine.entity.model.anim.RenderResolverTileEntity;
import net.optifine.expr.IExpression;

public class ModelResolver
implements IModelResolver {
    private ModelAdapter modelAdapter;
    private ModelBase model;
    private CustomModelRenderer[] customModelRenderers;
    private ModelRenderer thisModelRenderer;
    private ModelRenderer partModelRenderer;
    private IRenderResolver renderResolver;

    public ModelResolver(ModelAdapter modelAdapter, ModelBase model, CustomModelRenderer[] customModelRenderers) {
        this.modelAdapter = modelAdapter;
        this.model = model;
        this.customModelRenderers = customModelRenderers;
        Class oclass = modelAdapter.getEntityClass();
        this.renderResolver = TileEntity.class.isAssignableFrom(oclass) ? new RenderResolverTileEntity() : new RenderResolverEntity();
    }

    @Override
    public IExpression getExpression(String name) {
        ModelVariableFloat iexpression = this.getModelVariable(name);
        if (iexpression != null) {
            return iexpression;
        }
        IExpression iexpression1 = this.renderResolver.getParameter(name);
        return iexpression1 != null ? iexpression1 : null;
    }

    @Override
    public ModelRenderer getModelRenderer(String name) {
        if (name == null) {
            return null;
        }
        if (name.indexOf(":") >= 0) {
            String[] astring = Config.tokenize(name, ":");
            ModelRenderer modelrenderer3 = this.getModelRenderer(astring[0]);
            int j2 = 1;
            while (j2 < astring.length) {
                String s2 = astring[j2];
                ModelRenderer modelrenderer4 = modelrenderer3.getChildDeep(s2);
                if (modelrenderer4 == null) {
                    return null;
                }
                modelrenderer3 = modelrenderer4;
                ++j2;
            }
            return modelrenderer3;
        }
        if (this.thisModelRenderer != null && name.equals("this")) {
            return this.thisModelRenderer;
        }
        if (this.partModelRenderer != null && name.equals("part")) {
            return this.partModelRenderer;
        }
        ModelRenderer modelrenderer = this.modelAdapter.getModelRenderer(this.model, name);
        if (modelrenderer != null) {
            return modelrenderer;
        }
        int i2 = 0;
        while (i2 < this.customModelRenderers.length) {
            CustomModelRenderer custommodelrenderer = this.customModelRenderers[i2];
            ModelRenderer modelrenderer1 = custommodelrenderer.getModelRenderer();
            if (name.equals(modelrenderer1.getId())) {
                return modelrenderer1;
            }
            ModelRenderer modelrenderer2 = modelrenderer1.getChildDeep(name);
            if (modelrenderer2 != null) {
                return modelrenderer2;
            }
            ++i2;
        }
        return null;
    }

    @Override
    public ModelVariableFloat getModelVariable(String name) {
        String[] astring = Config.tokenize(name, ".");
        if (astring.length != 2) {
            return null;
        }
        String s2 = astring[0];
        String s1 = astring[1];
        ModelRenderer modelrenderer = this.getModelRenderer(s2);
        if (modelrenderer == null) {
            return null;
        }
        ModelVariableType modelvariabletype = ModelVariableType.parse(s1);
        return modelvariabletype == null ? null : new ModelVariableFloat(name, modelrenderer, modelvariabletype);
    }

    public void setPartModelRenderer(ModelRenderer partModelRenderer) {
        this.partModelRenderer = partModelRenderer;
    }

    public void setThisModelRenderer(ModelRenderer thisModelRenderer) {
        this.thisModelRenderer = thisModelRenderer;
    }
}

