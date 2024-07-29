/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.SVertexBuilder;
import org.lwjgl.opengl.GL11;

public class WorldVertexBufferUploader {
    public void draw(WorldRenderer p_181679_1_) {
        if (p_181679_1_.getVertexCount() > 0) {
            if (p_181679_1_.getDrawMode() == 7 && Config.isQuadsToTriangles()) {
                p_181679_1_.quadsToTriangles();
            }
            VertexFormat vertexformat = p_181679_1_.getVertexFormat();
            int i2 = vertexformat.getNextOffset();
            ByteBuffer bytebuffer = p_181679_1_.getByteBuffer();
            List<VertexFormatElement> list = vertexformat.getElements();
            boolean flag = Reflector.ForgeVertexFormatElementEnumUseage_preDraw.exists();
            boolean flag1 = Reflector.ForgeVertexFormatElementEnumUseage_postDraw.exists();
            int j2 = 0;
            while (j2 < list.size()) {
                VertexFormatElement vertexformatelement = list.get(j2);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
                if (flag) {
                    Reflector.callVoid((Object)vertexformatelement$enumusage, Reflector.ForgeVertexFormatElementEnumUseage_preDraw, vertexformat, j2, i2, bytebuffer);
                } else {
                    int k2 = vertexformatelement.getType().getGlConstant();
                    int l2 = vertexformatelement.getIndex();
                    bytebuffer.position(vertexformat.getOffset(j2));
                    switch (vertexformatelement$enumusage) {
                        case POSITION: {
                            GL11.glVertexPointer(vertexformatelement.getElementCount(), k2, i2, bytebuffer);
                            GL11.glEnableClientState(32884);
                            break;
                        }
                        case UV: {
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + l2);
                            GL11.glTexCoordPointer(vertexformatelement.getElementCount(), k2, i2, bytebuffer);
                            GL11.glEnableClientState(32888);
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                            break;
                        }
                        case COLOR: {
                            GL11.glColorPointer(vertexformatelement.getElementCount(), k2, i2, bytebuffer);
                            GL11.glEnableClientState(32886);
                            break;
                        }
                        case NORMAL: {
                            GL11.glNormalPointer(k2, i2, bytebuffer);
                            GL11.glEnableClientState(32885);
                        }
                    }
                }
                ++j2;
            }
            if (p_181679_1_.isMultiTexture()) {
                p_181679_1_.drawMultiTexture();
            } else if (Config.isShaders()) {
                SVertexBuilder.drawArrays(p_181679_1_.getDrawMode(), 0, p_181679_1_.getVertexCount(), p_181679_1_);
            } else {
                GL11.glDrawArrays(p_181679_1_.getDrawMode(), 0, p_181679_1_.getVertexCount());
            }
            int j1 = 0;
            int k1 = list.size();
            while (j1 < k1) {
                VertexFormatElement vertexformatelement1 = list.get(j1);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();
                if (flag1) {
                    Reflector.callVoid((Object)vertexformatelement$enumusage1, Reflector.ForgeVertexFormatElementEnumUseage_postDraw, vertexformat, j1, i2, bytebuffer);
                } else {
                    int i1 = vertexformatelement1.getIndex();
                    switch (vertexformatelement$enumusage1) {
                        case POSITION: {
                            GL11.glDisableClientState(32884);
                            break;
                        }
                        case UV: {
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i1);
                            GL11.glDisableClientState(32888);
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                            break;
                        }
                        case COLOR: {
                            GL11.glDisableClientState(32886);
                            GlStateManager.resetColor();
                            break;
                        }
                        case NORMAL: {
                            GL11.glDisableClientState(32885);
                        }
                    }
                }
                ++j1;
            }
        }
        p_181679_1_.reset();
    }
}

