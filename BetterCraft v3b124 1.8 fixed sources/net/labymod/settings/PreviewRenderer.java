/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import net.labymod.core.LabyModCore;
import net.labymod.core.WorldRendererAdapter;
import net.labymod.gui.elements.CustomGuiButton;
import net.labymod.gui.elements.Tabs;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.Source;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.LabyModModuleEditorGui;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;
import net.labymod.utils.ReflectionHelper;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PreviewRenderer
extends Gui {
    private static final boolean MC18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ItemStack previewItemStackSword = Material.IRON_SWORD.createItemStack();
    private static final ItemStack previewItemStackFish = Material.RAW_FISH.createItemStack(1, 3);
    private static final CustomGuiButton dummyButton = new CustomGuiButton(0, 0, 0, null);
    private static PreviewRenderer instance;
    private final Framebuffer framebuffer;
    private Class<?> bindedClass;
    private boolean created;
    private boolean fastRenderConflict;

    public PreviewRenderer() {
        this.framebuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);
        this.created = false;
        instance = this;
    }

    public static PreviewRenderer getInstance() {
        if (instance == null) {
            instance = new PreviewRenderer();
        }
        return instance;
    }

    public void init(Class<?> theClass) {
        this.bindedClass = theClass;
        if (!this.fastRenderConflict) {
            this.handleOFFastRender();
        }
        int width = Minecraft.getMinecraft().displayWidth;
        int height = Minecraft.getMinecraft().displayHeight;
        if (this.framebuffer.framebufferWidth != width || this.framebuffer.framebufferHeight != height && !this.fastRenderConflict) {
            this.framebuffer.createFramebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            this.created = false;
        }
    }

    public void createFrame() {
        if (this.framebuffer == null || Minecraft.getMinecraft().currentScreen == null || !Minecraft.getMinecraft().currentScreen.getClass().equals(this.bindedClass)) {
            return;
        }
        if (!this.fastRenderConflict) {
            this.framebuffer.bindFramebuffer(true);
        }
    }

    private void setFirstFrameInBuffer() {
        if (this.created) {
            return;
        }
        this.created = true;
        GlStateManager.pushMatrix();
        this.framebuffer.bindFramebuffer(true);
        this.renderImage(true, 0, 0, LabyMod.getInstance().getDrawUtils().getScaledResolution().getScaledWidth(), LabyMod.getInstance().getDrawUtils().getScaledResolution().getScaledHeight());
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        GlStateManager.popMatrix();
    }

    public void kill() {
        if (!this.fastRenderConflict && this.framebuffer != null) {
            this.framebuffer.unbindFramebuffer();
        }
    }

    private void renderImage(boolean forceDefaultImage, int left, int top, int right, int bottom) {
        int minWidth = Math.min(left, right);
        int minHeight = Math.min(top, bottom);
        int maxWidth = Math.max(left, right);
        int maxHeight = Math.max(top, bottom);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter vertexBuffer = LabyModCore.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        float[] color = new float[]{0.0f, 0.0f, 0.0f, 0.5f};
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(minWidth - 1, maxHeight + 1, -90.0).color(color[0], color[1], color[2], color[3]).endVertex();
        vertexBuffer.pos(maxWidth + 1, maxHeight + 1, -90.0).color(color[0], color[1], color[2], color[3]).endVertex();
        vertexBuffer.pos(maxWidth + 1, minHeight - 1, -90.0).color(color[0], color[1], color[2], color[3]).endVertex();
        vertexBuffer.pos(minWidth - 1, minHeight - 1, -90.0).color(color[0], color[1], color[2], color[3]).endVertex();
        tessellator.draw();
        if (LabyMod.getInstance().isInGame() && !forceDefaultImage) {
            this.framebuffer.bindFramebufferTexture();
            GlStateManager.enableTexture2D();
            vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            vertexBuffer.pos(minWidth, maxHeight, -90.0).tex(0.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            vertexBuffer.pos(maxWidth, maxHeight, -90.0).tex(1.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            vertexBuffer.pos(maxWidth, minHeight, -90.0).tex(1.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            vertexBuffer.pos(minWidth, minHeight, -90.0).tex(0.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            tessellator.draw();
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.SETTINGS_MODULE_EDITOR_BG);
            GlStateManager.enableTexture2D();
            LabyMod.getInstance().getDrawUtils().drawTexture(left, top, 0.0, 0.0, 255.0, 255.0, right - left, bottom - top);
        }
    }

    private void renderHotbar(int left, int top, int right, int bottom, float partialTicks, int mouseX, int mouseY) {
        EntityPlayer entityplayer = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
        if (!(entityplayer instanceof EntityPlayer)) {
            entityplayer = null;
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(widgetsTexPath);
        int i2 = (right - left) / 2;
        float f2 = this.zLevel;
        this.zLevel = -90.0f;
        this.drawTexturedModalRect(left + i2 - 91, bottom - 22, 0, 0, 182, 22);
        this.drawTexturedModalRect(left + i2 - 91 - 1 + (entityplayer == null ? 0 : entityplayer.inventory.currentItem) * 20, bottom - 22 - 1, 0, 22, 24, 22);
        this.zLevel = f2;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        int j2 = 0;
        while (j2 < 9) {
            int k2 = (right - left) / 2 - 90 + j2 * 20 + 2;
            int l2 = bottom - 16 - 3;
            this.renderHotbarItem(j2, left + k2, l2, partialTicks, entityplayer, mouseX, mouseY);
            ++j2;
        }
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer entity, int mouseX, int mouseY) {
        ItemStack itemstack = null;
        itemstack = entity == null ? (index == 0 ? previewItemStackSword : (index == 8 ? previewItemStackFish : null)) : LabyModCore.getMinecraft().getItem(entity.inventory, index);
        if (itemstack != null) {
            float f2 = (float)LabyModCore.getMinecraft().getAnimationsToGo(itemstack) - partialTicks;
            if (f2 > 0.0f) {
                GlStateManager.pushMatrix();
                float f22 = 1.0f + f2 / 5.0f;
                GlStateManager.translate(xPos + 8, yPos + 12, 0.0f);
                GlStateManager.scale(1.0f / f22, (f22 + 1.0f) / 2.0f, 1.0f);
                GlStateManager.translate(-(xPos + 8), -(yPos + 12), 0.0f);
            }
            GlStateManager.enableDepth();
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemstack, xPos, yPos);
            GlStateManager.disableDepth();
            if (f2 > 0.0f) {
                GlStateManager.popMatrix();
            }
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(LabyModCore.getMinecraft().getFontRenderer(), itemstack, xPos, yPos);
            if (index == 8 && entity == null && mouseX > xPos && mouseX < xPos + 16 && mouseY > yPos && mouseY < yPos + 16) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "Puffi");
            }
        }
    }

    private void renderPlayerStatsNew(int left, int top, int right, int bottom) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getIcons());
        GlStateManager.enableAlpha();
        float healthUpdateCounter = 0.0f;
        int lastPlayerHealth = 0;
        float lastSystemTime = 0.0f;
        float playerHealth = 0.0f;
        float updateCounter = 0.0f;
        int i2 = 20;
        boolean flag = healthUpdateCounter > 0.0f && (healthUpdateCounter - 0.0f) / 3.0f % 2.0f == 1.0f;
        lastSystemTime = Minecraft.getSystemTime();
        healthUpdateCounter = 10.0f;
        if ((float)Minecraft.getSystemTime() - lastSystemTime > 1000.0f) {
            playerHealth = 20.0f;
            lastPlayerHealth = 20;
            lastSystemTime = Minecraft.getSystemTime();
        }
        playerHealth = 20.0f;
        int j2 = lastPlayerHealth;
        boolean flag2 = false;
        int k2 = 20;
        int l2 = 20;
        float saturation = 0.0f;
        int i22 = left + (right - left) / 2 - 91;
        int j22 = left + (right - left) / 2 + 91;
        int k22 = top + bottom - top - 39;
        float f2 = 20.0f;
        float f22 = 0.0f;
        int l22 = LabyModCore.getMath().ceiling_float_int(1.0f);
        int i3 = Math.max(10 - (l22 - 2), 3);
        int j3 = k22 - (l22 - 1) * i3 - 10;
        float f3 = 0.0f;
        int k3 = 20;
        int l3 = -1;
        int i4 = 0;
        while (i4 < 10) {
            int j4 = i22 + i4 * 8;
            if (i4 * 2 + 1 < 20) {
                this.drawTexturedModalRect(j4, j3, 34, 9, 9, 9);
            }
            if (i4 * 2 + 1 == 20) {
                this.drawTexturedModalRect(j4, j3, 25, 9, 9, 9);
            }
            if (i4 * 2 + 1 > 20) {
                this.drawTexturedModalRect(j4, j3, 16, 9, 9, 9);
            }
            ++i4;
        }
        int i5 = LabyModCore.getMath().ceiling_float_int(10.0f) - 1;
        while (i5 >= 0) {
            int j5 = 16;
            int k4 = 0;
            if (flag) {
                k4 = 1;
            }
            int l4 = LabyModCore.getMath().ceiling_float_int((float)(i5 + 1) / 10.0f) - 1;
            int i6 = i22 + i5 % 10 * 8;
            int j6 = k22 - l4 * i3;
            if (i5 == -1) {
                j6 -= 2;
            }
            boolean k5 = false;
            this.drawTexturedModalRect(i6, j6, 16 + k4 * 9, 0, 9, 9);
            if (flag) {
                if (i5 * 2 + 1 < j2) {
                    this.drawTexturedModalRect(i6, j6, 70, 0, 9, 9);
                }
                if (i5 * 2 + 1 == j2) {
                    this.drawTexturedModalRect(i6, j6, 79, 0, 9, 9);
                }
            }
            if (f3 > 0.0f) {
                this.drawTexturedModalRect(i6, j6, 160, 0, 9, 9);
                f3 -= 2.0f;
            } else {
                if (i5 * 2 + 1 < 20) {
                    this.drawTexturedModalRect(i6, j6, 52, 0, 9, 9);
                }
                if (i5 * 2 + 1 == 20) {
                    this.drawTexturedModalRect(i6, j6, 61, 0, 9, 9);
                }
            }
            --i5;
        }
        int k6 = 0;
        while (k6 < 10) {
            int i7 = k22;
            int l5 = 16;
            int j7 = 0;
            int i8 = j22 - k6 * 8 - 9;
            this.drawTexturedModalRect(i8, i7, 16 + j7 * 9, 27, 9, 9);
            if (k6 * 2 + 1 < 20) {
                this.drawTexturedModalRect(i8, i7, 52, 27, 9, 9);
            }
            if (k6 * 2 + 1 == 20) {
                this.drawTexturedModalRect(i8, i7, 61, 27, 9, 9);
            }
            ++k6;
        }
        int l6 = 300;
        int k7 = LabyModCore.getMath().ceiling_double_int(9.933333333333334);
        int i9 = LabyModCore.getMath().ceiling_double_int(10.0) - k7;
        int l7 = 0;
        while (l7 < k7 + i9) {
            if (l7 < k7) {
                this.drawTexturedModalRect(j22 - l7 * 8 - 9, j3, 16, 18, 9, 9);
            } else {
                this.drawTexturedModalRect(j22 - l7 * 8 - 9, j3, 25, 18, 9, 9);
            }
            ++l7;
        }
    }

    public void renderExpBar(int left, int top, int right, int bottom) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getIcons());
        boolean i2 = true;
        float experience = 0.7f;
        int experienceLevel = 42;
        int j2 = 182;
        int k2 = 128;
        int l2 = bottom - 32 + 3;
        this.drawTexturedModalRect(left + (right - left) / 2 - 91, l2, 0, 64, 182, 5);
        this.drawTexturedModalRect(left + (right - left) / 2 - 91, l2, 0, 69, 128, 5);
        int k22 = 8453920;
        String s2 = "42";
        int l22 = left + (right - left - LabyModCore.getMinecraft().getFontRenderer().getStringWidth("42")) / 2;
        int i22 = bottom - 31 - 4;
        LabyModCore.getMinecraft().getFontRenderer().drawString("42", l22 + 1, i22, 0);
        LabyModCore.getMinecraft().getFontRenderer().drawString("42", l22 - 1, i22, 0);
        LabyModCore.getMinecraft().getFontRenderer().drawString("42", l22, i22 + 1, 0);
        LabyModCore.getMinecraft().getFontRenderer().drawString("42", l22, i22 - 1, 0);
        LabyModCore.getMinecraft().getFontRenderer().drawString("42", l22, i22, 8453920);
    }

    public void renderEscapeScreen(int left, int top, int right, int bottom, int mouseX, int mouseY) {
        int width = right - left;
        int height = bottom - top;
        int i2 = -16;
        Minecraft mc2 = Minecraft.getMinecraft();
        String disconnectString = I18n.format(mc2.isIntegratedServerRunning() ? "menu.disconnect" : "menu.returnToMenu", new Object[0]);
        boolean shareToLanEnabled = mc2.isSingleplayer() && !mc2.getIntegratedServer().getPublic();
        this.renderDummyButton(disconnectString, left + width / 2 - 100, top + height / 4 + 120 + -16, 200, true, mouseX, mouseY);
        this.renderDummyButton(I18n.format("menu.returnToGame", new Object[0]), left + width / 2 - 100, top + height / 4 + 24 + -16, 200, true, mouseX, mouseY);
        this.renderDummyButton(I18n.format("menu.options", new Object[0]), left + width / 2 - 100, top + height / 4 + 96 + -16, 98, true, mouseX, mouseY);
        this.renderDummyButton(I18n.format("menu.shareToLan", new Object[0]), left + width / 2 + 2, top + height / 4 + 96 + -16, 98, shareToLanEnabled, mouseX, mouseY);
        this.renderDummyButton(MC18 ? I18n.format("gui.achievements", new Object[0]) : "Achievements", left + width / 2 - 100, top + height / 4 + 48 + -16, 98, true, mouseX, mouseY);
        this.renderDummyButton(I18n.format("gui.stats", new Object[0]), left + width / 2 + 2, top + height / 4 + 48 + -16, 98, true, mouseX, mouseY);
        PreviewRenderer.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), I18n.format("menu.game", new Object[0]), left + width / 2, 40 + top, 0xFFFFFF);
        Map<String, Class<? extends GuiScreen>[]> tabs = Tabs.getGuiMap();
        if (tabs.isEmpty()) {
            Tabs.initGuiScreen(new ArrayList<GuiButton>(), null);
        }
        int tabX = 0;
        for (String buttonName : tabs.keySet()) {
            buttonName = LanguageManager.translate(buttonName);
            int nameWidth = LabyMod.getInstance().getDrawUtils().getStringWidth(buttonName);
            this.renderDummyButton(buttonName, left + tabX + 5, top + 5, 10 + nameWidth, true, mouseX, mouseY);
            tabX += nameWidth + 12;
        }
    }

    public void renderDummyButton(String displayText, int x2, int y2, int width, boolean enabled, int mouseX, int mouseY) {
        PreviewRenderer.dummyButton.displayString = displayText;
        dummyButton.setPosition(x2, y2, x2 + width, y2 + 20);
        dummyButton.setEnabled(enabled);
        LabyModCore.getMinecraft().drawButton(dummyButton, mouseX, mouseY);
    }

    private void handleOFFastRender() {
        try {
            Field fieldOfFastRender = ReflectionHelper.findField(GameSettings.class, "ofFastRender");
            try {
                this.fastRenderConflict = fieldOfFastRender.getBoolean(Minecraft.getMinecraft().gameSettings);
                if (this.fastRenderConflict) {
                    Minecraft.getMinecraft().displayGuiScreen(new FastRenderConflict());
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static class FastRenderConflict
    extends GuiScreen {
        private final GuiScreen lastScreen;
        private BooleanElement ofFastRenderElement;
        private GuiButton buttonRestartGame;

        public FastRenderConflict() {
            this.lastScreen = Minecraft.getMinecraft().currentScreen;
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            int divY = height / 2 - 40 + 50;
            this.buttonList.add(new GuiButton(0, width / 2 - 100, divY + 30, 90, 20, LanguageManager.translate("button_cancel")));
            this.buttonRestartGame = new GuiButton(1, width / 2 + 10, divY + 30, 90, 20, LanguageManager.translate("button_restart"));
            this.buttonList.add(this.buttonRestartGame);
            String buttonString = String.valueOf(LanguageManager.translate("button_continue_anyway")) + " >>";
            this.buttonList.add(new GuiButton(2, width - LabyMod.getInstance().getDrawUtils().getStringWidth(buttonString) - 10, height - 25, 120, 20, buttonString));
            super.initGui();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawBackground(0);
            DrawUtils draw = LabyMod.getInstance().getDrawUtils();
            if (this.ofFastRenderElement == null) {
                draw.drawCenteredString(String.valueOf(ModColor.cl("c")) + LanguageManager.translate("optifine_fast_render_manual_title"), width / 2, height / 2 - 50);
                draw.drawCenteredString(LanguageManager.translate("optifine_fast_render_manual_explanation"), width / 2, height / 2 - 50 + 10);
                draw.drawCenteredString(LanguageManager.translate("optifine_fast_render_manual_path"), width / 2, height / 2 - 50 + 30);
            } else {
                int divY = height / 2 - 40;
                draw.drawCenteredString(String.valueOf(ModColor.cl("c")) + LanguageManager.translate("optifine_fast_render_auto_tile"), width / 2, divY);
                draw.drawCenteredString(LanguageManager.translate("optifine_fast_render_auto_explanation"), width / 2, divY + 10);
                draw.drawCenteredString(LanguageManager.translate("optifine_fast_render_auto_request"), width / 2, divY + 30);
                int centerPosX = width / 2;
                int centerPosY = divY + 55;
                int elementWidth = 200;
                int elementHeight = 22;
                this.ofFastRenderElement.draw(centerPosX - 100, centerPosY - 11, centerPosX + 100, centerPosY + 11, mouseX, mouseY);
            }
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            if (this.ofFastRenderElement != null) {
                this.ofFastRenderElement.mouseClicked(mouseX, mouseY, mouseButton);
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            super.actionPerformed(button);
            switch (button.id) {
                case 0: {
                    if (this.lastScreen instanceof LabyModModuleEditorGui) {
                        LabyModModuleEditorGui lastScreen = (LabyModModuleEditorGui)this.lastScreen;
                        Minecraft.getMinecraft().displayGuiScreen(lastScreen.getLastScreen());
                        break;
                    }
                    Minecraft.getMinecraft().displayGuiScreen(LabyMod.getInstance().isInGame() ? null : new GuiMainMenu());
                    break;
                }
                case 1: {
                    Minecraft.getMinecraft().shutdown();
                    break;
                }
                case 2: {
                    Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
                }
            }
        }
    }
}

