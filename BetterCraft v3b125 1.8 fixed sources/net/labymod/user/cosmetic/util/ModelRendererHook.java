/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.util;

import net.labymod.utils.Consumer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelRendererHook
extends ModelRenderer {
    private Consumer<ModelRendererHook> hook;
    private float scale;

    public ModelRendererHook(ModelBase model) {
        super(model);
    }

    public ModelRendererHook(ModelBase model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }

    @Override
    public void render(float scale) {
        this.scale = scale;
        this.hook.accept(this);
    }

    public void renderSuper() {
        super.render(this.scale);
    }

    public void setHook(Consumer<ModelRendererHook> hook) {
        this.hook = hook;
    }

    public float getScale() {
        return this.scale;
    }
}

