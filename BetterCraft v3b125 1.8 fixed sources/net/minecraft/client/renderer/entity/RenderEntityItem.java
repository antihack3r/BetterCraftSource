/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import java.util.Random;
import me.nzxtercode.bettercraft.client.gui.section.GuiUISettings;
import me.nzxtercode.bettercraft.client.utils.ItemPhysicUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderEntityItem
extends Render<EntityItem> {
    private final RenderItem itemRenderer;
    private Random field_177079_e = new Random();

    public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
        super(renderManagerIn);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15f;
        this.shadowOpaque = 0.75f;
    }

    private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
        ItemStack itemstack = itemIn.getEntityItem();
        Item item = itemstack.getItem();
        if (item == null) {
            return 0;
        }
        boolean flag = p_177077_9_.isGui3d();
        int i2 = this.func_177078_a(itemstack);
        float f2 = 0.25f;
        float f1 = MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0f + itemIn.hoverStart) * 0.1f + 0.1f;
        float f22 = p_177077_9_.getItemCameraTransforms().getTransform((ItemCameraTransforms.TransformType)ItemCameraTransforms.TransformType.GROUND).scale.y;
        GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.25f * f22, (float)p_177077_6_);
        if (flag || this.renderManager.options != null) {
            float f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0f + itemIn.hoverStart) * 57.295776f;
            GlStateManager.rotate(f3, 0.0f, 1.0f, 0.0f);
        }
        if (!flag) {
            float f6 = -0.0f * (float)(i2 - 1) * 0.5f;
            float f4 = -0.0f * (float)(i2 - 1) * 0.5f;
            float f5 = -0.046875f * (float)(i2 - 1) * 0.5f;
            GlStateManager.translate(f6, f4, f5);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        return i2;
    }

    private int func_177078_a(ItemStack stack) {
        int i2 = 1;
        if (stack.stackSize > 48) {
            i2 = 5;
        } else if (stack.stackSize > 32) {
            i2 = 4;
        } else if (stack.stackSize > 16) {
            i2 = 3;
        } else if (stack.stackSize > 1) {
            i2 = 2;
        }
        return i2;
    }

    @Override
    public void doRender(EntityItem entity, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        if (GuiUISettings.enabledItems[0]) {
            ItemPhysicUtils.doRender(entity, x2, y2, z2, entityYaw, partialTicks);
        } else {
            ItemStack itemstack = entity.getEntityItem();
            this.field_177079_e.setSeed(187L);
            boolean flag = false;
            if (this.bindEntityTexture(entity)) {
                this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
                flag = true;
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            IBakedModel ibakedmodel = this.itemRenderer.getItemModelMesher().getItemModel(itemstack);
            int i2 = this.func_177077_a(entity, x2, y2, z2, partialTicks, ibakedmodel);
            int j2 = 0;
            while (j2 < i2) {
                if (ibakedmodel.isGui3d()) {
                    GlStateManager.pushMatrix();
                    if (j2 > 0) {
                        float f2 = (this.field_177079_e.nextFloat() * 2.0f - 1.0f) * 0.15f;
                        float f1 = (this.field_177079_e.nextFloat() * 2.0f - 1.0f) * 0.15f;
                        float f22 = (this.field_177079_e.nextFloat() * 2.0f - 1.0f) * 0.15f;
                        GlStateManager.translate(f2, f1, f22);
                    }
                    GlStateManager.scale(0.5f, 0.5f, 0.5f);
                    ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                    this.itemRenderer.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                    this.itemRenderer.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                    float f3 = ibakedmodel.getItemCameraTransforms().ground.scale.x;
                    float f4 = ibakedmodel.getItemCameraTransforms().ground.scale.y;
                    float f5 = ibakedmodel.getItemCameraTransforms().ground.scale.z;
                    GlStateManager.translate(0.0f * f3, 0.0f * f4, 0.046875f * f5);
                }
                ++j2;
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            this.bindEntityTexture(entity);
            if (flag) {
                this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
            }
        }
        super.doRender(entity, x2, y2, z2, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityItem entity) {
        return TextureMap.locationBlocksTexture;
    }
}

