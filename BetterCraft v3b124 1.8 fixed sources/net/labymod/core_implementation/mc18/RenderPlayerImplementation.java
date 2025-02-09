/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18;

import net.labymod.core.LabyModCore;
import net.labymod.core.RenderPlayerAdapter;
import net.labymod.core.WorldRendererAdapter;
import net.labymod.core_implementation.mc18.layer.LayerArrowCustom;
import net.labymod.core_implementation.mc18.layer.LayerBipedArmorCustom;
import net.labymod.core_implementation.mc18.layer.LayerHeldItemCustom;
import net.labymod.main.LabyMod;
import net.labymod.mojang.RenderPlayerHook;
import net.labymod.user.User;
import net.labymod.user.cosmetic.layers.LayerCustomCape;
import net.labymod.user.group.EnumGroupDisplayType;
import net.labymod.user.group.LabyGroup;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TagManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.lwjgl.opengl.GL11;

public class RenderPlayerImplementation
implements RenderPlayerAdapter {
    @Override
    public String[] getSkinMapNames() {
        return new String[]{"skinMap", "l", "field_178636_l"};
    }

    @Override
    public LayerRenderer[] getLayerRenderers(RenderPlayer renderPlayer) {
        return new LayerRenderer[]{new LayerBipedArmorCustom(renderPlayer), new LayerHeldItemCustom(renderPlayer), new LayerArrowCustom(renderPlayer), new LayerDeadmau5Head(renderPlayer), new LayerCustomCape(renderPlayer), new LayerCustomHead(renderPlayer.getMainModel().bipedHead)};
    }

    @Override
    public void renderName(RenderPlayerHook.RenderPlayerCustom renderPlayer, AbstractClientPlayer entity, double x2, double y2, double z2) {
        boolean canRender;
        boolean bl2 = canRender = Minecraft.isGuiEnabled() && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) && entity.riddenByEntity == null;
        if (renderPlayer.canRenderTheName(entity) || entity == renderPlayer.getRenderManager().livingPlayer && LabyMod.getSettings().showMyName && canRender) {
            float f2;
            double distance = entity.getDistanceSqToEntity(renderPlayer.getRenderManager().livingPlayer);
            float f3 = f2 = entity.isSneaking() ? 32.0f : 64.0f;
            if (distance < (double)(f2 * f2)) {
                User user = entity instanceof EntityPlayer ? LabyMod.getInstance().getUserManager().getUser(entity.getUniqueID()) : null;
                float maxNameTagHeight = user == null || !LabyMod.getSettings().cosmetics ? 0.0f : user.getMaxNameTagHeight();
                String username = entity.getDisplayName().getFormattedText();
                GlStateManager.alphaFunc(516, 0.1f);
                String tagName = TagManager.getTaggedMessage(username);
                if (tagName != null) {
                    username = tagName;
                }
                float fixedPlayerViewX = renderPlayer.getRenderManager().playerViewX * (float)(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1 : 1);
                y2 += (double)maxNameTagHeight;
                FontRenderer fontrenderer = renderPlayer.getFontRendererFromRenderManager();
                if (entity.isSneaking()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)x2, (float)y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float)z2);
                    GL11.glNormal3f(0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(-renderPlayer.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(fixedPlayerViewX, 1.0f, 0.0f, 0.0f);
                    GlStateManager.scale(-0.02666667f, -0.02666667f, 0.02666667f);
                    GlStateManager.translate(0.0f, 9.374999f, 0.0f);
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.disableTexture2D();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    int i2 = fontrenderer.getStringWidth(username) / 2;
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    worldrenderer.pos(-i2 - 1, -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                    worldrenderer.pos(-i2 - 1, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                    worldrenderer.pos(i2 + 1, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                    worldrenderer.pos(i2 + 1, -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.depthMask(true);
                    fontrenderer.drawString(username, -fontrenderer.getStringWidth(username) / 2, 0, 0x20FFFFFF);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.popMatrix();
                } else {
                    Scoreboard scoreboard;
                    ScoreObjective scoreobjective;
                    LabyGroup labyGroup = user.getGroup();
                    if (user.getSubTitle() != null) {
                        GlStateManager.pushMatrix();
                        double size = user.getSubTitleSize();
                        GlStateManager.translate(0.0, -0.2 + size / 8.0, 0.0);
                        this.renderLivingLabelCustom(renderPlayer, entity, user.getSubTitle(), x2, y2, z2, 64, (float)size);
                        y2 += size / 6.0;
                        GlStateManager.popMatrix();
                    }
                    if (labyGroup != null && labyGroup.getDisplayType() == EnumGroupDisplayType.BESIDE_NAME) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float)x2, (float)y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float)z2);
                        GlStateManager.rotate(-renderPlayer.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                        GlStateManager.rotate(fixedPlayerViewX, 1.0f, 0.0f, 0.0f);
                        GlStateManager.scale(-0.02666667f, -0.02666667f, 0.02666667f);
                        GlStateManager.disableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        double pos = -fontrenderer.getStringWidth(username) / 2 - 2 - 8;
                        labyGroup.renderBadge(pos, -0.5, 8.0, 8.0, false);
                        GlStateManager.enableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.resetColor();
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GlStateManager.popMatrix();
                    }
                    if (distance < 100.0 && (scoreobjective = (scoreboard = entity.getWorldScoreboard()).getObjectiveInDisplaySlot(2)) != null) {
                        Score score = scoreboard.getValueFromObjective(entity.getName(), scoreobjective);
                        this.renderLivingLabelCustom(renderPlayer, entity, String.valueOf(score.getScorePoints()) + " " + scoreobjective.getDisplayName(), x2, y2, z2, 64);
                        y2 += (double)((float)LabyMod.getInstance().getDrawUtils().getFontRenderer().FONT_HEIGHT * 1.15f * 0.02666667f);
                    }
                    this.renderLivingLabelCustom(renderPlayer, entity, username, x2, y2 - (entity.isChild() ? (double)(entity.height / 2.0f) : 0.0), z2, 64);
                    if (tagName != null) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float)x2, (float)y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float)z2);
                        GlStateManager.rotate(-renderPlayer.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                        GlStateManager.rotate(fixedPlayerViewX, 1.0f, 0.0f, 0.0f);
                        GlStateManager.scale(-0.01666667f, -0.01666667f, 0.01666667f);
                        GlStateManager.translate(0.0f, entity.isSneaking() ? 17.0f : 2.0f, 0.0f);
                        GlStateManager.disableLighting();
                        GlStateManager.enableBlend();
                        fontrenderer.drawString("\u270e", 5 + (int)((double)fontrenderer.getStringWidth(username) * 0.8), 0, ModColor.toRGB(255, 255, 0, 255));
                        GlStateManager.disableBlend();
                        GlStateManager.enableLighting();
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        GlStateManager.popMatrix();
                    }
                    if (labyGroup != null && labyGroup.getDisplayType() == EnumGroupDisplayType.ABOVE_HEAD) {
                        GlStateManager.pushMatrix();
                        double size = 0.5;
                        GlStateManager.scale(0.5, 0.5, 0.5);
                        GlStateManager.translate(0.0, 2.0, 0.0);
                        this.renderLivingLabelCustom(renderPlayer, entity, labyGroup.getDisplayTag(), x2 / 0.5, (y2 - (entity.isChild() ? (double)(entity.height / 2.0f) : 0.0) + 0.3) / 0.5, z2 / 0.5, 10);
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }

    protected void renderLivingLabelCustom(RenderPlayerHook.RenderPlayerCustom renderPlayer, Entity entityIn, String str, double x2, double y2, double z2, int maxDistance) {
        this.renderLivingLabelCustom(renderPlayer, entityIn, str, x2, y2, z2, maxDistance, 1.6f);
    }

    protected void renderLivingLabelCustom(RenderPlayerHook.RenderPlayerCustom renderPlayer, Entity entityIn, String str, double x2, double y2, double z2, int maxDistance, float scale) {
        double d0 = entityIn.getDistanceSqToEntity(renderPlayer.getRenderManager().livingPlayer);
        if (d0 <= (double)(maxDistance * maxDistance)) {
            float fixedPlayerViewX = renderPlayer.getRenderManager().playerViewX * (float)(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1 : 1);
            FontRenderer fontrenderer = renderPlayer.getFontRendererFromRenderManager();
            float f1 = 0.016666668f * scale;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x2 + 0.0f, (float)y2 + entityIn.height + 0.5f, (float)z2);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-renderPlayer.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(fixedPlayerViewX, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
            int i2 = 0;
            if (str.equals("deadmau5")) {
                i2 = -10;
            }
            int j2 = fontrenderer.getStringWidth(str) / 2;
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(-j2 - 1, -1 + i2, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldrenderer.pos(-j2 - 1, 8 + i2, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldrenderer.pos(j2 + 1, 8 + i2, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldrenderer.pos(j2 + 1, -1 + i2, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i2, 0x20FFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i2, -1);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public RenderPlayerHook.RenderPlayerCustom getRenderPlayer(RenderManager renderManager, boolean slim) {
        return new RenderPlayerHook.RenderPlayerCustom(renderManager, slim){

            @Override
            public boolean canRenderTheName(AbstractClientPlayer entity) {
                return super.canRenderName(entity);
            }

            @Override
            public void renderLabel(AbstractClientPlayer entityIn, double x2, double y2, double z2, String string, float height, double distance) {
                super.renderOffsetLivingLabel(entityIn, x2, y2, z2, string, height, distance);
            }

            @Override
            public void renderName(AbstractClientPlayer entity, double x2, double y2, double z2) {
                LabyModCore.getRenderPlayerImplementation().renderName(this, entity, x2, y2, z2);
            }
        };
    }
}

