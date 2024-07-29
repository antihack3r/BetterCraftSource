/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.nbt.SaveStates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSaveSlotButton
extends Gui {
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/widgets.png");
    private static final int X_SIZE = 14;
    private static final int HEIGHT = 20;
    private static final int MAX_WIDTH = 150;
    private static final int MIN_WIDTH = 82;
    private static final int GAP = 3;
    private final Minecraft mc;
    public final SaveStates.SaveState save;
    private final int rightX;
    private int x;
    private int y;
    private int width;
    private String text;
    private boolean xVisible;
    private int tickCount;

    public GuiSaveSlotButton(SaveStates.SaveState save, int rightX, int y2) {
        this.save = save;
        this.rightX = rightX;
        this.y = y2;
        this.mc = Minecraft.getMinecraft();
        this.xVisible = !save.tag.hasNoTags();
        this.text = String.valueOf(save.tag.hasNoTags() ? "Save " : "Load ") + save.name;
        this.tickCount = -1;
        this.updatePosition();
    }

    public void draw(int mx2, int my2) {
        int textColor = this.inBounds(mx2, my2) ? 0xFFFFA0 : 0xFFFFFF;
        this.renderVanillaButton(this.x, this.y, 0, 66, this.width, 20);
        GuiSaveSlotButton.drawCenteredString(this.mc.fontRendererObj, this.text, this.x + this.width / 2, this.y + 6, textColor);
        if (this.tickCount != -1 && this.tickCount / 6 % 2 == 0) {
            this.mc.fontRendererObj.drawStringWithShadow("_", this.x + (this.width + this.mc.fontRendererObj.getStringWidth(this.text)) / 2 + 1, this.y + 6, 0xFFFFFF);
        }
        if (this.xVisible) {
            textColor = this.inBoundsOfX(mx2, my2) ? 0xFFFFA0 : 0xFFFFFF;
            this.renderVanillaButton(this.leftBoundOfX(), this.topBoundOfX(), 0, 66, 14, 14);
            GuiSaveSlotButton.drawCenteredString(this.mc.fontRendererObj, "x", this.x - 3 - 7, this.y + 6, textColor);
        }
    }

    private void renderVanillaButton(int x2, int y2, int u2, int v2, int width, int height) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(x2, y2, u2, v2, width / 2, height / 2);
        this.drawTexturedModalRect(x2 + width / 2, y2, u2 + 200 - width / 2, v2, width / 2, height / 2);
        this.drawTexturedModalRect(x2, y2 + height / 2, u2, v2 + 20 - height / 2, width / 2, height / 2);
        this.drawTexturedModalRect(x2 + width / 2, y2 + height / 2, u2 + 200 - width / 2, v2 + 20 - height / 2, width / 2, height / 2);
    }

    private int leftBoundOfX() {
        return this.x - 14 - 3;
    }

    private int topBoundOfX() {
        return this.y + 3;
    }

    public boolean inBoundsOfX(int mx2, int my2) {
        int buttonX = this.leftBoundOfX();
        int buttonY = this.topBoundOfX();
        return this.xVisible && mx2 >= buttonX && my2 >= buttonY && mx2 < buttonX + 14 && my2 < buttonY + 14;
    }

    public boolean inBounds(int mx2, int my2) {
        return mx2 >= this.x && my2 >= this.y && mx2 < this.x + this.width && my2 < this.y + 20;
    }

    private void updatePosition() {
        this.width = this.mc.fontRendererObj.getStringWidth(this.text) + 24;
        if (this.width % 2 == 1) {
            ++this.width;
        }
        this.width = MathHelper.clamp_int(this.width, 82, 150);
        this.x = this.rightX - this.width;
    }

    public void reset() {
        this.xVisible = false;
        this.save.tag = new NBTTagCompound();
        this.text = "Save " + this.save.name;
        this.updatePosition();
    }

    public void saved() {
        this.xVisible = true;
        this.text = "Load " + this.save.name;
        this.updatePosition();
    }

    public void keyTyped(char c2, int key) {
        if (key == 14) {
            this.backSpace();
        }
        if (Character.isDigit(c2) || Character.isLetter(c2)) {
            this.save.name = String.valueOf(this.save.name) + c2;
            this.text = String.valueOf(this.save.tag.hasNoTags() ? "Save " : "Load ") + this.save.name;
            this.updatePosition();
        }
    }

    public void backSpace() {
        if (this.save.name.length() > 0) {
            this.save.name = this.save.name.substring(0, this.save.name.length() - 1);
            this.text = String.valueOf(this.save.tag.hasNoTags() ? "Save " : "Load ") + this.save.name;
            this.updatePosition();
        }
    }

    public void startEditing() {
        this.tickCount = 0;
    }

    public void stopEditing() {
        this.tickCount = -1;
    }

    public void update() {
        ++this.tickCount;
    }
}

