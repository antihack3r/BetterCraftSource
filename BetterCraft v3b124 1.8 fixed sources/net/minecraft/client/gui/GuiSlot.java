/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiResourcePackList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot {
    protected final Minecraft mc;
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected final int slotHeight;
    private int scrollUpButtonID;
    private int scrollDownButtonID;
    protected int mouseX;
    protected int mouseY;
    protected boolean field_148163_i = true;
    protected int initialClickY = -2;
    protected float scrollMultiplier;
    protected float amountScrolled;
    protected int selectedElement = -1;
    protected long lastClicked;
    protected boolean field_178041_q = true;
    protected boolean showSelectionBox = true;
    protected boolean hasListHeader;
    protected int headerPadding;
    private boolean enabled = true;

    public GuiSlot(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
        this.mc = mcIn;
        this.width = width;
        this.height = height;
        this.top = topIn;
        this.bottom = bottomIn;
        this.slotHeight = slotHeightIn;
        this.left = 0;
        this.right = width;
    }

    public void setDimensions(int widthIn, int heightIn, int topIn, int bottomIn) {
        this.width = widthIn;
        this.height = heightIn;
        this.top = topIn;
        this.bottom = bottomIn;
        this.left = 0;
        this.right = widthIn;
    }

    public void setShowSelectionBox(boolean showSelectionBoxIn) {
        this.showSelectionBox = showSelectionBoxIn;
    }

    protected void setHasListHeader(boolean hasListHeaderIn, int headerPaddingIn) {
        this.hasListHeader = hasListHeaderIn;
        this.headerPadding = headerPaddingIn;
        if (!hasListHeaderIn) {
            this.headerPadding = 0;
        }
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int var1, boolean var2, int var3, int var4);

    protected abstract boolean isSelected(int var1);

    protected int getContentHeight() {
        return this.getSize() * this.slotHeight + this.headerPadding;
    }

    protected abstract void drawBackground();

    protected void func_178040_a(int p_178040_1_, int p_178040_2_, int p_178040_3_) {
    }

    protected abstract void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6);

    protected void drawListHeader(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_) {
    }

    protected void func_148132_a(int p_148132_1_, int p_148132_2_) {
    }

    protected void func_148142_b(int p_148142_1_, int p_148142_2_) {
    }

    public int getSlotIndexFromScreenCoords(int p_148124_1_, int p_148124_2_) {
        int i2 = this.left + this.width / 2 - this.getListWidth() / 2;
        int j2 = this.left + this.width / 2 + this.getListWidth() / 2;
        int k2 = p_148124_2_ - this.top - this.headerPadding + (int)this.amountScrolled - 4;
        int l2 = k2 / this.slotHeight;
        return p_148124_1_ < this.getScrollBarX() && p_148124_1_ >= i2 && p_148124_1_ <= j2 && l2 >= 0 && k2 >= 0 && l2 < this.getSize() ? l2 : -1;
    }

    public void registerScrollButtons(int scrollUpButtonIDIn, int scrollDownButtonIDIn) {
        this.scrollUpButtonID = scrollUpButtonIDIn;
        this.scrollDownButtonID = scrollDownButtonIDIn;
    }

    protected void bindAmountScrolled() {
        this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0f, this.func_148135_f());
    }

    public int func_148135_f() {
        return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
    }

    public int getAmountScrolled() {
        return (int)this.amountScrolled;
    }

    public boolean isMouseYWithinSlotBounds(int p_148141_1_) {
        return p_148141_1_ >= this.top && p_148141_1_ <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
    }

    public void scrollBy(int amount) {
        this.amountScrolled += (float)amount;
        this.bindAmountScrolled();
        this.initialClickY = -2;
    }

    public void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == this.scrollUpButtonID) {
                this.amountScrolled -= (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2;
                this.bindAmountScrolled();
            } else if (button.id == this.scrollDownButtonID) {
                this.amountScrolled += (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2;
                this.bindAmountScrolled();
            }
        }
    }

    public void drawScreen(int mouseXIn, int mouseYIn, float p_148128_3_) {
        if (this.field_178041_q) {
            this.mouseX = mouseXIn;
            this.mouseY = mouseYIn;
            this.drawBackground();
            int i2 = this.getScrollBarX();
            int j2 = i2 + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int k2 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int l2 = this.top + 4 - (int)this.amountScrolled;
            if (this.hasListHeader) {
                this.drawListHeader(k2, l2, tessellator);
            }
            GuiSlot.startScissor(0.0f, this.top, this.width, this.bottom);
            this.drawSelectionBox(k2, l2, mouseXIn, mouseYIn);
            GuiSlot.stopScissor();
            GlStateManager.disableDepth();
            int i1 = 1;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            int rainbow = ColorUtils.rainbowEffect();
            float r2 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
            float g2 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
            float b2 = (float)(rainbow & 0xFF) / 255.0f;
            float a2 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldrenderer.pos(this.left, this.top + i1, 0.0).tex(0.0, 1.0).color(255, 255, 255, 255).endVertex();
            worldrenderer.pos(this.right, this.top + i1, 0.0).tex(1.0, 1.0).color(255, 255, 255, 255).endVertex();
            worldrenderer.pos(this.right, this.top, 0.0).tex(1.0, 0.0).color(r2, g2, b2, a2).endVertex();
            worldrenderer.pos(this.left, this.top, 0.0).tex(0.0, 0.0).color(r2, g2, b2, a2).endVertex();
            tessellator.draw();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldrenderer.pos(this.left, this.bottom, 0.0).tex(0.0, 1.0).color(r2, g2, b2, a2).endVertex();
            worldrenderer.pos(this.right, this.bottom, 0.0).tex(1.0, 1.0).color(r2, g2, b2, a2).endVertex();
            worldrenderer.pos(this.right, this.bottom - i1, 0.0).tex(1.0, 0.0).color(255, 255, 255, 255).endVertex();
            worldrenderer.pos(this.left, this.bottom - i1, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
            tessellator.draw();
            int j1 = this.func_148135_f();
            if (j1 > 0) {
                int k1 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                int l1 = (int)this.amountScrolled * (this.bottom - this.top - (k1 = MathHelper.clamp_int(k1, 32, this.bottom - this.top - 8))) / j1 + this.top;
                if (l1 < this.top) {
                    l1 = this.top;
                }
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                worldrenderer.pos(i2, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j2, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j2, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(i2, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                worldrenderer.pos(i2, l1 + k1, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j2, l1 + k1, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j2, l1, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(i2, l1, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
                tessellator.draw();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                worldrenderer.pos(i2, l1 + k1 - 1, 0.0).tex(0.0, 1.0).color(192, 192, 192, 255).endVertex();
                worldrenderer.pos(j2 - 1, l1 + k1 - 1, 0.0).tex(1.0, 1.0).color(192, 192, 192, 255).endVertex();
                worldrenderer.pos(j2 - 1, l1, 0.0).tex(1.0, 0.0).color(192, 192, 192, 255).endVertex();
                worldrenderer.pos(i2, l1, 0.0).tex(0.0, 0.0).color(192, 192, 192, 255).endVertex();
                tessellator.draw();
            }
            this.func_148142_b(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public static void startScissor(float startX, float startY, float endX, float endY) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        float width = endX - startX;
        float height = endY - startY;
        assert (Minecraft.getMinecraft().currentScreen != null);
        GuiScreen cfr_ignored_0 = Minecraft.getMinecraft().currentScreen;
        float bottomY = (float)GuiScreen.height - endY;
        float factor = scaledResolution.getScaleFactor();
        float scissorX = startX * factor;
        float scissorY = bottomY * factor;
        float scissorWidth = width * factor;
        float scissorHeight = height * factor;
        GL11.glScissor((int)scissorX, (int)scissorY, (int)scissorWidth, (int)scissorHeight);
        GL11.glEnable(3089);
    }

    public static void stopScissor() {
        GL11.glDisable(3089);
    }

    public void handleMouseInput() {
        if (this.isMouseYWithinSlotBounds(this.mouseY)) {
            int i2;
            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
                int i3 = (this.width - this.getListWidth()) / 2;
                int j2 = (this.width + this.getListWidth()) / 2;
                int k2 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                int l2 = k2 / this.slotHeight;
                if (l2 < this.getSize() && this.mouseX >= i3 && this.mouseX <= j2 && l2 >= 0 && k2 >= 0) {
                    this.elementClicked(l2, false, this.mouseX, this.mouseY);
                    this.selectedElement = l2;
                } else if (this.mouseX >= i3 && this.mouseX <= j2 && k2 < 0) {
                    this.func_148132_a(this.mouseX - i3, this.mouseY - this.top + (int)this.amountScrolled - 4);
                }
            }
            if (Mouse.isButtonDown(0) && this.getEnabled()) {
                if (this.initialClickY != -1) {
                    if (this.initialClickY >= 0) {
                        this.amountScrolled -= (float)(this.mouseY - this.initialClickY) * this.scrollMultiplier;
                        this.initialClickY = this.mouseY;
                    }
                } else {
                    boolean flag1 = true;
                    if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
                        int j2 = (this.width - this.getListWidth()) / 2;
                        int k2 = (this.width + this.getListWidth()) / 2;
                        int l2 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                        int i1 = l2 / this.slotHeight;
                        if (i1 < this.getSize() && this.mouseX >= j2 && this.mouseX <= k2 && i1 >= 0 && l2 >= 0) {
                            boolean flag = i1 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                            this.elementClicked(i1, flag, this.mouseX, this.mouseY);
                            this.selectedElement = i1;
                            this.lastClicked = Minecraft.getSystemTime();
                        } else if (this.mouseX >= j2 && this.mouseX <= k2 && l2 < 0) {
                            this.func_148132_a(this.mouseX - j2, this.mouseY - this.top + (int)this.amountScrolled - 4);
                            flag1 = false;
                        }
                        int i3 = this.getScrollBarX();
                        int j1 = i3 + 6;
                        if (this.mouseX >= i3 && this.mouseX <= j1) {
                            this.scrollMultiplier = -1.0f;
                            int k1 = this.func_148135_f();
                            if (k1 < 1) {
                                k1 = 1;
                            }
                            int l1 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
                            l1 = MathHelper.clamp_int(l1, 32, this.bottom - this.top - 8);
                            this.scrollMultiplier /= (float)(this.bottom - this.top - l1) / (float)k1;
                        } else {
                            this.scrollMultiplier = 1.0f;
                        }
                        this.initialClickY = flag1 ? this.mouseY : -2;
                    } else {
                        this.initialClickY = -2;
                    }
                }
            } else {
                this.initialClickY = -1;
            }
            if ((i2 = Mouse.getEventDWheel()) != 0) {
                if (i2 > 0) {
                    i2 = -1;
                } else if (i2 < 0) {
                    i2 = 1;
                }
                this.amountScrolled += (float)(i2 * this.slotHeight / 2);
            }
        }
    }

    public void setEnabled(boolean enabledIn) {
        this.enabled = enabledIn;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public int getListWidth() {
        return 220;
    }

    protected void drawSelectionBox(int p_148120_1_, int p_148120_2_, int mouseXIn, int mouseYIn) {
        int i2 = this.getSize();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int j2 = 0;
        while (j2 < i2) {
            int k2 = p_148120_2_ + j2 * this.slotHeight + this.headerPadding;
            int l2 = this.slotHeight - 4;
            if (k2 > this.bottom || k2 + l2 < this.top) {
                this.func_178040_a(j2, p_148120_1_, k2);
            }
            if (this.showSelectionBox && this.isSelected(j2)) {
                int i1 = this.left + (this.width / 2 - this.getListWidth() / 2);
                int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableTexture2D();
                int rainbow = ColorUtils.rainbowEffect();
                float r2 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
                float g2 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
                float b2 = (float)(rainbow & 0xFF) / 255.0f;
                float a2 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                worldrenderer.pos(i1, k2 + l2 + 2, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j1, k2 + l2 + 2, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j1, k2 - 2, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(i1, k2 - 2, 0.0).tex(0.0, 0.0).color(r2, g2, b2, a2).endVertex();
                worldrenderer.pos(i1 + 1, k2 + l2 + 1, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j1 - 1, k2 + l2 + 1, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j1 - 1, k2 - 1, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(i1 + 1, k2 - 1, 0.0).tex(0.0, 0.0).color(80, 80, 80, 255).endVertex();
                tessellator.draw();
                GlStateManager.enableTexture2D();
            }
            if (!(this instanceof GuiResourcePackList) || k2 >= this.top - this.slotHeight && k2 <= this.bottom) {
                this.drawSlot(j2, p_148120_1_, k2, l2, mouseXIn, mouseYIn);
            }
            ++j2;
        }
    }

    protected int getScrollBarX() {
        return this.width / 2 + 124;
    }

    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f2 = 32.0f;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(this.left, endY, 0.0).tex(0.0, (float)endY / 32.0f).color(64, 64, 64, endAlpha).endVertex();
        worldrenderer.pos(this.left + this.width, endY, 0.0).tex((float)this.width / 32.0f, (float)endY / 32.0f).color(64, 64, 64, endAlpha).endVertex();
        worldrenderer.pos(this.left + this.width, startY, 0.0).tex((float)this.width / 32.0f, (float)startY / 32.0f).color(64, 64, 64, startAlpha).endVertex();
        worldrenderer.pos(this.left, startY, 0.0).tex(0.0, (float)startY / 32.0f).color(64, 64, 64, startAlpha).endVertex();
        tessellator.draw();
    }

    public void setSlotXBoundsFromLeft(int leftIn) {
        this.left = leftIn;
        this.right = leftIn + this.width;
    }

    public int getSlotHeight() {
        return this.slotHeight;
    }

    protected void drawContainerBackground(Tessellator p_drawContainerBackground_1_) {
        WorldRenderer worldrenderer = p_drawContainerBackground_1_.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f2 = 32.0f;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(this.left, this.bottom, 0.0).tex((float)this.left / f2, (float)(this.bottom + (int)this.amountScrolled) / f2).color(32, 32, 32, 255).endVertex();
        worldrenderer.pos(this.right, this.bottom, 0.0).tex((float)this.right / f2, (float)(this.bottom + (int)this.amountScrolled) / f2).color(32, 32, 32, 255).endVertex();
        worldrenderer.pos(this.right, this.top, 0.0).tex((float)this.right / f2, (float)(this.top + (int)this.amountScrolled) / f2).color(32, 32, 32, 255).endVertex();
        worldrenderer.pos(this.left, this.top, 0.0).tex((float)this.left / f2, (float)(this.top + (int)this.amountScrolled) / f2).color(32, 32, 32, 255).endVertex();
        p_drawContainerBackground_1_.draw();
    }
}

