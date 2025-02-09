/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.util;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.vector.FBPVector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FBPRenderUtil {
    public static void renderCubeShaded_S(WorldRenderer buf, Vector2f[] uvs, float f5, float f6, float f7, double scale, FBPVector3d rotVec, int j2, int k2, float r2, float g2, float b2, float a2) {
        Tessellator.getInstance().draw();
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        buf.begin(7, FBP.POSITION_TEX_COLOR_LMAP_NORMAL);
        GlStateManager.enableCull();
        RenderHelper.enableStandardItemLighting();
        buf.setTranslation(f5, f6, f7);
        FBPRenderUtil.putCube_S(buf, uvs, scale, rotVec, j2, k2, r2, g2, b2, a2, FBP.cartoonMode);
        buf.setTranslation(0.0, 0.0, 0.0);
        Tessellator.getInstance().draw();
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        buf.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        RenderHelper.disableStandardItemLighting();
    }

    public static void renderCubeShaded_WH(WorldRenderer buf, Vector2f[] uvs, float f5, float f6, float f7, double width, double height, FBPVector3d rotVec, int j2, int k2, float r2, float g2, float b2, float a2) {
        Tessellator.getInstance().draw();
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        buf.begin(7, FBP.POSITION_TEX_COLOR_LMAP_NORMAL);
        GlStateManager.enableCull();
        RenderHelper.enableStandardItemLighting();
        buf.setTranslation(f5, f6, f7);
        FBPRenderUtil.putCube_WH(buf, uvs, width, height, rotVec, j2, k2, r2, g2, b2, a2, FBP.cartoonMode);
        buf.setTranslation(0.0, 0.0, 0.0);
        Tessellator.getInstance().draw();
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        buf.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        RenderHelper.disableStandardItemLighting();
    }

    static void putCube_S(WorldRenderer worldRendererIn, Vector2f[] par, double scale, FBPVector3d rotVec, int j2, int k2, float r2, float g2, float b2, float a2, boolean cartoon) {
        float radsX = (float)Math.toRadians(rotVec.x);
        float radsY = (float)Math.toRadians(rotVec.y);
        float radsZ = (float)Math.toRadians(rotVec.z);
        int i2 = 0;
        while (i2 < FBP.CUBE.length) {
            Vec3 v1 = FBP.CUBE[i2];
            Vec3 v2 = FBP.CUBE[i2 + 1];
            Vec3 v3 = FBP.CUBE[i2 + 2];
            Vec3 v4 = FBP.CUBE[i2 + 3];
            v1 = FBPRenderUtil.rotatef_d(v1, radsX, radsY, radsZ);
            v2 = FBPRenderUtil.rotatef_d(v2, radsX, radsY, radsZ);
            v3 = FBPRenderUtil.rotatef_d(v3, radsX, radsY, radsZ);
            v4 = FBPRenderUtil.rotatef_d(v4, radsX, radsY, radsZ);
            Vec3 normal = FBPRenderUtil.rotatef_d(FBP.CUBE_NORMALS[i2 / 4], radsX, radsY, radsZ);
            if (!cartoon) {
                FBPRenderUtil.addVt_S(worldRendererIn, scale, v1, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_S(worldRendererIn, scale, v2, par[1].x, par[1].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_S(worldRendererIn, scale, v3, par[2].x, par[2].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_S(worldRendererIn, scale, v4, par[3].x, par[3].y, j2, k2, r2, g2, b2, a2, normal);
            } else {
                FBPRenderUtil.addVt_S(worldRendererIn, scale, v1, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_S(worldRendererIn, scale, v2, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_S(worldRendererIn, scale, v3, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_S(worldRendererIn, scale, v4, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
            }
            i2 += 4;
        }
    }

    static void putCube_WH(WorldRenderer worldRendererIn, Vector2f[] par, double width, double height, FBPVector3d rotVec, int j2, int k2, float r2, float g2, float b2, float a2, boolean cartoon) {
        float radsX = (float)Math.toRadians(rotVec.x);
        float radsY = (float)Math.toRadians(rotVec.y);
        float radsZ = (float)Math.toRadians(rotVec.z);
        int i2 = 0;
        while (i2 < FBP.CUBE.length) {
            Vec3 v1 = FBP.CUBE[i2];
            Vec3 v2 = FBP.CUBE[i2 + 1];
            Vec3 v3 = FBP.CUBE[i2 + 2];
            Vec3 v4 = FBP.CUBE[i2 + 3];
            v1 = FBPRenderUtil.rotatef_d(v1, radsX, radsY, radsZ);
            v2 = FBPRenderUtil.rotatef_d(v2, radsX, radsY, radsZ);
            v3 = FBPRenderUtil.rotatef_d(v3, radsX, radsY, radsZ);
            v4 = FBPRenderUtil.rotatef_d(v4, radsX, radsY, radsZ);
            Vec3 normal = FBPRenderUtil.rotatef_d(FBP.CUBE_NORMALS[i2 / 4], radsX, radsY, radsZ);
            if (!cartoon) {
                FBPRenderUtil.addVt_WH(worldRendererIn, width, height, v1, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_WH(worldRendererIn, width, height, v2, par[1].x, par[1].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_WH(worldRendererIn, width, height, v3, par[2].x, par[2].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_WH(worldRendererIn, width, height, v4, par[3].x, par[3].y, j2, k2, r2, g2, b2, a2, normal);
            } else {
                FBPRenderUtil.addVt_WH(worldRendererIn, width, height, v1, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_WH(worldRendererIn, width, height, v2, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_WH(worldRendererIn, width, height, v3, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
                FBPRenderUtil.addVt_WH(worldRendererIn, width, height, v4, par[0].x, par[0].y, j2, k2, r2, g2, b2, a2, normal);
            }
            i2 += 4;
        }
    }

    static void addVt_S(WorldRenderer worldRendererIn, double scale, Vec3 pos, double u2, double v2, int j2, int k2, float r2, float g2, float b2, float a2, Vec3 n2) {
        worldRendererIn.pos(pos.xCoord * scale, pos.yCoord * scale, pos.zCoord * scale).tex(u2, v2).color(r2, g2, b2, a2).lightmap(j2, k2).normal((float)n2.xCoord, (float)n2.yCoord, (float)n2.zCoord).endVertex();
    }

    static void addVt_WH(WorldRenderer worldRendererIn, double width, double height, Vec3 pos, double u2, double v2, int j2, int k2, float r2, float g2, float b2, float a2, Vec3 n2) {
        worldRendererIn.pos(pos.xCoord * width, pos.yCoord * height, pos.zCoord * width).tex(u2, v2).color(r2, g2, b2, a2).lightmap(j2, k2).normal((float)n2.xCoord, (float)n2.yCoord, (float)n2.zCoord).endVertex();
    }

    public static Vec3 rotatef_d(Vec3 vec, float AngleX, float AngleY, float AngleZ) {
        FBPVector3d sin = new FBPVector3d(MathHelper.sin(AngleX), MathHelper.sin(AngleY), MathHelper.sin(AngleZ));
        FBPVector3d cos = new FBPVector3d(MathHelper.cos(AngleX), MathHelper.cos(AngleY), MathHelper.cos(AngleZ));
        vec = new Vec3(vec.xCoord, vec.yCoord * cos.x - vec.zCoord * sin.x, vec.yCoord * sin.x + vec.zCoord * cos.x);
        vec = new Vec3(vec.xCoord * cos.z - vec.yCoord * sin.z, vec.xCoord * sin.z + vec.yCoord * cos.z, vec.zCoord);
        vec = new Vec3(vec.xCoord * cos.y + vec.zCoord * sin.y, vec.yCoord, vec.xCoord * sin.y - vec.zCoord * cos.y);
        return vec;
    }

    public static Vector3f rotatef_f(Vector3f pos, float AngleX, float AngleY, float AngleZ, EnumFacing facing) {
        FBPVector3d sin = new FBPVector3d(MathHelper.sin(AngleX), MathHelper.sin(AngleY), MathHelper.sin(AngleZ));
        FBPVector3d cos = new FBPVector3d(MathHelper.cos(AngleX), MathHelper.cos(AngleY), MathHelper.cos(AngleZ));
        FBPVector3d pos2 = new FBPVector3d(pos.x, pos.y, pos.z);
        if (facing == EnumFacing.EAST) {
            FBPVector3d fbpVector3d = pos2;
            fbpVector3d.x -= 1.0;
        } else if (facing == EnumFacing.WEST) {
            FBPVector3d fbpVector3d2 = pos2;
            fbpVector3d2.x += 1.0;
        } else if (facing == EnumFacing.SOUTH) {
            FBPVector3d fbpVector3d3 = pos2;
            fbpVector3d3.z -= 1.0;
            FBPVector3d fbpVector3d4 = pos2;
            fbpVector3d4.x -= 1.0;
        }
        FBPVector3d pos3 = new FBPVector3d(pos2.x, pos2.y * cos.x - pos2.z * sin.x, pos2.y * sin.x + pos2.z * cos.x);
        pos3 = new FBPVector3d(pos3.x * cos.z - pos3.y * sin.z, pos3.x * sin.z + pos3.y * cos.z, pos3.z);
        pos3 = new FBPVector3d(pos3.x * cos.y + pos3.z * sin.y, pos3.y, pos3.x * sin.y - pos3.z * cos.y);
        if (facing == EnumFacing.EAST) {
            FBPVector3d fbpVector3d5 = pos3;
            fbpVector3d5.x += 1.0;
        } else if (facing == EnumFacing.WEST) {
            FBPVector3d fbpVector3d6 = pos3;
            fbpVector3d6.x -= 1.0;
        } else if (facing == EnumFacing.SOUTH) {
            FBPVector3d fbpVector3d7 = pos3;
            fbpVector3d7.z += 1.0;
            FBPVector3d fbpVector3d8 = pos3;
            fbpVector3d8.x += 1.0;
        }
        return new Vector3f((float)pos3.x, (float)pos3.y, (float)pos3.z);
    }

    public static void markBlockForRender(BlockPos pos) {
        BlockPos bp1 = pos.add(1, 1, 1);
        BlockPos bp2 = pos.add(-1, -1, -1);
        Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate(bp1.getX(), bp1.getY(), bp1.getZ(), bp2.getX(), bp2.getY(), bp2.getZ());
    }
}

