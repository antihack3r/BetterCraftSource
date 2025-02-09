/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.background;

import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.vecmath.Vector2f;
import me.nzxtercode.bettercraft.client.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

public class GuiColorPicker
extends GuiScreen {
    private int glTexture;
    private Vector2f position;
    private final ResourceLocation location = new ResourceLocation("client/gui/colorgradient.png");
    private final AtomicReference<BufferedImage> image = new AtomicReference();
    private final Consumer<Color> consumer;
    private GuiScreen parent;
    private GuiTextField hexTextField;
    private boolean buttonEnabled;

    public GuiColorPicker(GuiScreen parent, Consumer<Color> consumer) {
        this.consumer = consumer;
        this.parent = parent;
        try {
            this.image.set(TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(this.location).getInputStream()));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, width - 107, height - 27, 100, 20, "Back"));
        this.buttonList.add(new GuiButton(1, width / 2 + 45, height / 2 + 10, 50, 10, "Background"));
        this.buttonList.add(new GuiButton(2, width / 2 - 20, height / 2 + 10, 50, 10, "Particel"));
        this.buttonList.add(new GuiButton(3, width / 2 - 90, height / 2 + 10, 50, 10, "Button-String"));
        this.hexTextField = new GuiTextField(0, this.fontRendererObj, width / 2 + 30 + 1, height / 2 - 5, 59, this.mc.fontRendererObj.FONT_HEIGHT);
        this.hexTextField.setEnableBackgroundDrawing(false);
        this.hexTextField.setMaxStringLength(6);
        this.image.set(this.resize(this.image.get(), 100, 100));
        if (Objects.nonNull(this.position)) {
            this.hexTextField.setText(Integer.toHexString(this.getColor((int)this.position.getX(), (int)this.position.getY()).getRGB()).substring(2));
        }
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        }
        if (Objects.nonNull(this.position)) {
            switch (button.id) {
                case 1: {
                    Config.getInstance().editBackground("Background", json -> json.add("color", new JsonPrimitive(this.getColor((int)this.position.getX(), (int)this.position.getY()).getRGB())));
                    break;
                }
                case 2: {
                    Config.getInstance().editColor("Particel", json -> json.add("color", new JsonPrimitive(this.getColor((int)this.position.getX(), (int)this.position.getY()).getRGB())));
                    break;
                }
                case 3: {
                    Config.getInstance().editColor("Buttons", json -> json.add("string", new JsonPrimitive(this.getColor((int)this.position.getX(), (int)this.position.getY()).getRGB())));
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            this.drawDefaultBackground();
            Gui.drawRect(width / 2 - 100, height / 2 - 100, width / 2 + 100, height / 2 + 30, Integer.MIN_VALUE);
            this.drawTextureAt(width / 2 - 100, height / 2 - 100, 100, 100);
            if (Objects.nonNull(this.position)) {
                Color color = this.getColor((int)this.position.getX(), (int)this.position.getY());
                this.mc.fontRendererObj.drawString("RGB", width / 2 + 10, height / 2 - 95, -1);
                this.mc.fontRendererObj.drawString("R:", width / 2 + 10, height / 2 - 85, -1);
                Gui.drawRect(width / 2 + 30, height / 2 - 85, width / 2 + 90, height / 2 - 85 + this.mc.fontRendererObj.FONT_HEIGHT - 1, new Color(color.getRed(), 1, 1).getRGB());
                this.mc.fontRendererObj.drawString("G:", width / 2 + 10, height / 2 - 75, -1);
                Gui.drawRect(width / 2 + 30, height / 2 - 75, width / 2 + 90, height / 2 - 75 + this.mc.fontRendererObj.FONT_HEIGHT - 1, new Color(1, color.getGreen(), 1).getRGB());
                this.mc.fontRendererObj.drawString("B:", width / 2 + 10, height / 2 - 65, -1);
                Gui.drawRect(width / 2 + 30, height / 2 - 65, width / 2 + 90, height / 2 - 65 + this.mc.fontRendererObj.FONT_HEIGHT - 1, new Color(1, 1, color.getBlue()).getRGB());
                float[] hsb = new float[3];
                Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
                this.mc.fontRendererObj.drawString("HSB", width / 2 + 10, height / 2 - 50, -1);
                this.mc.fontRendererObj.drawString("H:", width / 2 + 10, height / 2 - 40, -1);
                Gui.drawRect(width / 2 + 30, height / 2 - 40, width / 2 + 90, height / 2 - 40 + this.mc.fontRendererObj.FONT_HEIGHT - 1, color.getRGB());
                int lol2 = Math.min((int)(-((hsb[2] * 10000.0f - 9999.0f) / 500.0f) * 255.0f), 255);
                this.mc.fontRendererObj.drawString("S:", width / 2 + 10, height / 2 - 30, -1);
                Gui.drawRect(width / 2 + 30, height / 2 - 30, width / 2 + 90, height / 2 - 30 + this.mc.fontRendererObj.FONT_HEIGHT - 1, new Color(lol2, lol2, lol2).getRGB());
                int lol = 255 - Math.min((int)(-((hsb[2] * 10000.0f - 9999.0f) / 500.0f) * 255.0f), 255);
                this.mc.fontRendererObj.drawString("B:", width / 2 + 10, height / 2 - 20, -1);
                Gui.drawRect(width / 2 + 30, height / 2 - 20, width / 2 + 90, height / 2 - 20 + this.mc.fontRendererObj.FONT_HEIGHT - 1, new Color(lol, lol, lol).getRGB());
                this.mc.fontRendererObj.drawString("Hex:", width / 2 + 10, height / 2 - 5, -1);
                Gui.drawRect(width / 2 + 30, height / 2 - 5, width / 2 + 90, height / 2 - 5 + this.mc.fontRendererObj.FONT_HEIGHT - 1, color.getRGB());
                this.hexTextField.drawTextBox();
                this.drawBorder(width / 2 - 100 + (int)this.position.x - 2, height / 2 - 100 + (int)this.position.y - 2, width / 2 - 100 + (int)this.position.x + 2, height / 2 - 100 + (int)this.position.y + 2, Color.WHITE.getRGB());
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.update(mouseX, mouseY);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.update(mouseX, mouseY);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void update(int mouseX, int mouseY) {
        if (mouseX >= width / 2 - 100 && mouseX <= width / 2 - 100 + 100 && mouseY >= height / 2 - 100 && mouseY <= height / 2 - 100 + 100) {
            try {
                Color color = this.getColor(Math.max(Math.min(mouseX - (width / 2 - 100), 99), 1), Math.max(Math.min(mouseY - (height / 2 - 100), 99), 0));
                if (color.getRed() != 0 && color.getGreen() != 0 && color.getBlue() != 0) {
                    this.position = new Vector2f(Math.max(Math.min(mouseX - (width / 2 - 100), 99), 1), Math.max(Math.min(mouseY - (height / 2 - 100), 99), 0));
                    this.hexTextField.setText(Integer.toHexString(color.getRGB()).substring(2));
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public Color getColor(int x2, int y2) {
        return new Color(this.image.get().getRGB(x2, y2));
    }

    public BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, 4);
        BufferedImage dimg = new BufferedImage(newW, newH, 2);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }

    public void drawTextureAt(int x2, int y2, int width, int height) throws Exception {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.location);
        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 0.0f, width, height, width, height);
        GlStateManager.popMatrix();
    }

    public void drawBorder(int left, int top, int right, int bottom, int border) {
        Gui.drawRect(left, top + 1, right, top, border);
        Gui.drawRect(right - 1, top, right, bottom, border);
        Gui.drawRect(left, bottom, right, bottom - 1, border);
        Gui.drawRect(left, top, left + 1, bottom, border);
    }
}

