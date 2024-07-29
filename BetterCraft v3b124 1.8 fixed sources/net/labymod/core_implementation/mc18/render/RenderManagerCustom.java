/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.render;

import java.lang.reflect.Field;
import java.util.Map;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.utils.ReflectionHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;

public class RenderManagerCustom
extends RenderManager {
    private double renderPosX;
    private double renderPosY;
    private double renderPosZ;
    private boolean debugBoundingBox;

    public RenderManagerCustom(TextureManager renderEngineIn, RenderItem itemRendererIn) {
        super(renderEngineIn, itemRendererIn);
        Field renderManagerRenderField = ReflectionHelper.findField(Render.class, LabyModCore.getMappingAdapter().getRenderManagerRenderMappings());
        try {
            Map map = (Map)ReflectionHelper.findField(RenderManager.class, LabyModCore.getMappingAdapter().getEntityRenderMapMappings()).get(this);
            for (Map.Entry entityRenderEntry : map.entrySet()) {
                renderManagerRenderField.set(entityRenderEntry.getValue(), this);
            }
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        try {
            Map map1 = (Map)ReflectionHelper.findField(RenderManager.class, LabyModCore.getMappingAdapter().getSkinMapMappings()).get(this);
            for (Map.Entry skinEntry : map1.entrySet()) {
                renderManagerRenderField.set(skinEntry.getValue(), this);
            }
        }
        catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
    }

    @Override
    public boolean isDebugBoundingBox() {
        return this.debugBoundingBox;
    }

    @Override
    public void setDebugBoundingBox(boolean debugBoundingBoxIn) {
        this.debugBoundingBox = debugBoundingBoxIn;
    }

    @Override
    public boolean renderEntityWithPosYaw(Entity entityIn, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        return this.doRenderEntity(entityIn, x2, y2, z2, entityYaw, partialTicks, false);
    }

    @Override
    public boolean renderEntitySimple(Entity entityIn, float partialTicks) {
        return this.renderEntityStatic(entityIn, partialTicks, false);
    }

    @Override
    public boolean renderEntityStatic(Entity entity, float partialTicks, boolean p_147936_3_) {
        if (entity.ticksExisted == 0) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
        }
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        float f2 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        int i2 = entity.getBrightnessForRender(partialTicks);
        if (entity.isBurning()) {
            i2 = 0xF000F0;
        }
        int j2 = i2 % 65536;
        int k2 = i2 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j2 / 1.0f, (float)k2 / 1.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        return this.doRenderEntity(entity, d0 - this.renderPosX, d2 - this.renderPosY, d3 - this.renderPosZ, f2, partialTicks, p_147936_3_);
    }

    @Override
    public void setRenderPosition(double renderPosXIn, double renderPosYIn, double renderPosZIn) {
        this.renderPosX = renderPosXIn;
        this.renderPosY = renderPosYIn;
        this.renderPosZ = renderPosZIn;
    }

    /*
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean doRenderEntity(Entity entity, double x2, double y2, double z2, float entityYaw, float partialTicks, boolean p_147939_10_) {
        block7: {
            try {
                super.doRenderEntity(entity, x2, y2, z2, entityYaw, partialTicks, p_147939_10_);
            }
            catch (ClassCastException e2) {
                e2.printStackTrace();
            }
            LabyMod.getInstance().getEventManager().callRenderEntity(entity, x2, y2, z2, partialTicks);
            if (!this.debugBoundingBox) return true;
            Render render = null;
            render = this.getEntityRenderObject(entity);
            if (render == null || this.renderEngine == null) return this.renderEngine == null;
            if (!entity.isInvisible() && !p_147939_10_) break block7;
            return true;
            {
                catch (Throwable throwable2) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable2, "Rendering entity in world");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
                    entity.addEntityCrashInfo(crashreportcategory);
                    CrashReportCategory crashreportcategory2 = crashreport.makeCategory("Renderer details");
                    crashreportcategory2.addCrashSection("Assigned renderer", render);
                    crashreportcategory2.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(x2, y2, z2));
                    crashreportcategory2.addCrashSection("Rotation", Float.valueOf(entityYaw));
                    crashreportcategory2.addCrashSection("Delta", Float.valueOf(partialTicks));
                    throw new ReportedException(crashreport);
                }
            }
        }
        try {
            this.renderDebugBoundingBox(entity, x2, y2, z2, entityYaw, partialTicks);
            return true;
        }
        catch (Throwable throwable) {
            throw new ReportedException(CrashReport.makeCrashReport(throwable, "Rendering entity hitbox in world"));
        }
    }

    private void renderDebugBoundingBox(Entity entityIn, double p_85094_2_, double p_85094_4_, double p_85094_6_, float p_85094_8_, float p_85094_9_) {
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        float f2 = entityIn.width / 2.0f;
        AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb2 = new AxisAlignedBB(axisalignedbb.minX - entityIn.posX + p_85094_2_, axisalignedbb.minY - entityIn.posY + p_85094_4_, axisalignedbb.minZ - entityIn.posZ + p_85094_6_, axisalignedbb.maxX - entityIn.posX + p_85094_2_, axisalignedbb.maxY - entityIn.posY + p_85094_4_, axisalignedbb.maxZ - entityIn.posZ + p_85094_6_);
        RenderGlobal.drawOutlinedBoundingBox(axisalignedbb2, 255, 255, 255, 255);
        if (entityIn instanceof EntityLivingBase && !LabyMod.getSettings().oldHitbox) {
            RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(p_85094_2_ - (double)f2, p_85094_4_ + (double)entityIn.getEyeHeight() - (double)0.01f, p_85094_6_ - (double)f2, p_85094_2_ + (double)f2, p_85094_4_ + (double)entityIn.getEyeHeight() + (double)0.01f, p_85094_6_ + (double)f2), 255, 0, 0, 255);
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        if (!LabyMod.getSettings().oldHitbox) {
            Vec3 vec3 = entityIn.getLook(p_85094_9_);
            worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(p_85094_2_, p_85094_4_ + (double)entityIn.getEyeHeight(), p_85094_6_).color(0, 0, 255, 255).endVertex();
            worldrenderer.pos(p_85094_2_ + vec3.xCoord * 2.0, p_85094_4_ + (double)entityIn.getEyeHeight() + vec3.yCoord * 2.0, p_85094_6_ + vec3.zCoord * 2.0).color(0, 0, 255, 255).endVertex();
            tessellator.draw();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
}

