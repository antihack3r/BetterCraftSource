/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.net.URI;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Utils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Logger logger = LogManager.getLogger();

    Utils() {
    }

    public static void drawGuiInfoBox(String text, int guiWidth, int guiHeight, int bottomPadding) {
        Utils.drawGuiInfoBox(text, 300, 100, guiWidth, guiHeight, bottomPadding);
    }

    public static void drawGuiInfoBox(String text, int infoBoxWidth, int infoBoxHeight, int guiWidth, int guiHeight, int bottomPadding) {
        if (text == null) {
            return;
        }
        int infoX = guiWidth / 2 - infoBoxWidth / 2;
        int infoY = guiHeight - bottomPadding - infoBoxHeight;
        int y2 = infoY + 5;
        GuiScreen.drawRect(infoX, infoY, infoX + infoBoxWidth, infoY + infoBoxHeight, 0x7F000000);
        List<String> lines = Utils.wordWrap(text, infoBoxWidth - 10);
        for (String s2 : lines) {
            Utils.mc.fontRendererObj.drawString(s2, infoX + 5, y2, 0xFFFFFF);
            y2 += Utils.mc.fontRendererObj.FONT_HEIGHT;
        }
    }

    public static List<String> wordWrap(String s2, int width) {
        s2 = s2.replace("\r", "");
        List<String> lines = Utils.mc.fontRendererObj.listFormattedStringToWidth(s2, width);
        return lines;
    }

    public static void drawListBackground(int topMargin, int bottomMargin, int top, int left, int bottom, int right) {
        Utils.drawDarkBackground(top, left, bottom, right);
        Utils.drawBorder(topMargin, bottomMargin, top, left, bottom, right);
    }

    public static void drawDarkBackground(int top, int left, int bottom, int right) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator t2 = Tessellator.getInstance();
        WorldRenderer wr2 = t2.getWorldRenderer();
        mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float textureSize = 32.0f;
        wr2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        wr2.pos(0.0, bottom, 0.0).tex(0.0f / textureSize, (float)bottom / textureSize).color(32, 32, 32, 255).endVertex();
        wr2.pos(right, bottom, 0.0).tex((float)right / textureSize, (float)bottom / textureSize).color(32, 32, 32, 255).endVertex();
        wr2.pos(right, top, 0.0).tex((float)right / textureSize, (float)top / textureSize).color(32, 32, 32, 255).endVertex();
        wr2.pos(left, top, 0.0).tex((float)left / textureSize, (float)top / textureSize).color(32, 32, 32, 255).endVertex();
        t2.draw();
    }

    public static void drawBorder(int topMargin, int bottomMargin, int top, int left, int bottom, int right) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        int padding = 4;
        mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float textureSize = 32.0f;
        Tessellator t2 = Tessellator.getInstance();
        WorldRenderer wr2 = t2.getWorldRenderer();
        int upperBoxEnd = top + topMargin;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        wr2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        wr2.pos(left, upperBoxEnd, 0.0).tex(0.0, (float)upperBoxEnd / textureSize).color(64, 64, 64, 255).endVertex();
        wr2.pos(right, upperBoxEnd, 0.0).tex((float)right / textureSize, (float)upperBoxEnd / textureSize).color(64, 64, 64, 255).endVertex();
        wr2.pos(right, top, 0.0).tex((float)right / textureSize, (float)top / textureSize).color(64, 64, 64, 255).endVertex();
        wr2.pos(left, top, 0.0).tex(0.0, (float)top / textureSize).color(64, 64, 64, 255).endVertex();
        t2.draw();
        int lowerBoxStart = bottom - bottomMargin;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        wr2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        wr2.pos(left, bottom, 0.0).tex(0.0, (float)bottom / textureSize).color(64, 64, 64, 255).endVertex();
        wr2.pos(right, bottom, 0.0).tex((float)right / textureSize, (float)bottom / textureSize).color(64, 64, 64, 255).endVertex();
        wr2.pos(right, lowerBoxStart, 0.0).tex((float)right / textureSize, (float)lowerBoxStart / textureSize).color(64, 64, 64, 255).endVertex();
        wr2.pos(left, lowerBoxStart, 0.0).tex(0.0, (float)lowerBoxStart / textureSize).color(64, 64, 64, 255).endVertex();
        t2.draw();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture2D();
        wr2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        wr2.pos(left, upperBoxEnd + padding, 0.0).tex(0.0, 1.0).color(0, 0, 0, 0).endVertex();
        wr2.pos(right, upperBoxEnd + padding, 0.0).tex(1.0, 1.0).color(0, 0, 0, 0).endVertex();
        wr2.pos(right, upperBoxEnd, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
        wr2.pos(left, upperBoxEnd, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
        t2.draw();
        wr2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        wr2.pos(left, lowerBoxStart, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
        wr2.pos(right, lowerBoxStart, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
        wr2.pos(right, lowerBoxStart - padding, 0.0).tex(1.0, 0.0).color(0, 0, 0, 0).endVertex();
        wr2.pos(left, lowerBoxStart - padding, 0.0).tex(0.0, 0.0).color(0, 0, 0, 0).endVertex();
        t2.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    public static boolean isMouseOverTextBox(int mouseX, int mouseY, GuiTextField textBox) {
        int scaledX = mouseX - textBox.xPosition;
        int scaledY = mouseY - textBox.yPosition;
        int height = 20;
        return scaledX >= 0 && scaledX < textBox.getWidth() && scaledY >= 0 && scaledY < 20;
    }

    public static void openLink(String path) {
        try {
            Class<?> desktopClass = Class.forName("java.awt.Desktop");
            Object desktop = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            desktopClass.getMethod("browse", URI.class).invoke(desktop, new URI(path));
        }
        catch (Throwable e2) {
            logger.error("Couldn't open link", e2);
        }
    }

    public static void drawStringWithShadow(String s2, int x2, int y2, int color) {
        Utils.mc.fontRendererObj.drawStringWithShadow(s2, x2, y2, color);
    }
}

