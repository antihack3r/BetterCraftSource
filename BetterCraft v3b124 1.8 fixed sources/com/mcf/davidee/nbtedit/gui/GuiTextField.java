/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.gui.CharacterFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiTextField
extends Gui {
    private final FontRenderer fontRenderer;
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean isFocused = false;
    private boolean isEnabled = true;
    private int field_73816_n = 0;
    private int cursorPosition = 0;
    private int selectionEnd = 0;
    private int enabledColor = 0xE0E0E0;
    private int disabledColor = 0x707070;
    private boolean visible = true;
    private boolean enableBackgroundDrawing = true;
    private boolean allowSection;

    public GuiTextField(FontRenderer par1FontRenderer, int x2, int y2, int w2, int h2, boolean allowSection) {
        this.fontRenderer = par1FontRenderer;
        this.xPos = x2;
        this.yPos = y2;
        this.width = w2;
        this.height = h2;
        this.allowSection = allowSection;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String par1Str) {
        this.text = par1Str.length() > this.maxStringLength ? par1Str.substring(0, this.maxStringLength) : par1Str;
        this.setCursorPositionEnd();
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedtext() {
        int var1 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int var2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(var1, var2);
    }

    public void writeText(String par1Str) {
        int var8;
        String var2 = "";
        String var3 = CharacterFilter.filerAllowedCharacters(par1Str, this.allowSection);
        int var4 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int var5 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int var6 = this.maxStringLength - this.text.length() - (var4 - this.selectionEnd);
        if (this.text.length() > 0) {
            var2 = String.valueOf(var2) + this.text.substring(0, var4);
        }
        if (var6 < var3.length()) {
            var2 = String.valueOf(var2) + var3.substring(0, var6);
            var8 = var6;
        } else {
            var2 = String.valueOf(var2) + var3;
            var8 = var3.length();
        }
        if (this.text.length() > 0 && var5 < this.text.length()) {
            var2 = String.valueOf(var2) + this.text.substring(var5);
        }
        this.text = var2;
        this.moveCursorBy(var4 - this.selectionEnd + var8);
    }

    public void deleteWords(int par1) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(par1) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int par1) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean var2 = par1 < 0;
                int var3 = var2 ? this.cursorPosition + par1 : this.cursorPosition;
                int var4 = var2 ? this.cursorPosition : this.cursorPosition + par1;
                String var5 = "";
                if (var3 >= 0) {
                    var5 = this.text.substring(0, var3);
                }
                if (var4 < this.text.length()) {
                    var5 = String.valueOf(var5) + this.text.substring(var4);
                }
                this.text = var5;
                if (var2) {
                    this.moveCursorBy(par1);
                }
            }
        }
    }

    public int getNthWordFromCursor(int par1) {
        return this.getNthWordFromPos(par1, this.getCursorPosition());
    }

    public int getNthWordFromPos(int par1, int par2) {
        return this.func_73798_a(par1, this.getCursorPosition(), true);
    }

    /*
     * Unable to fully structure code
     */
    public int func_73798_a(int par1, int par2, boolean par3) {
        var4 = par2;
        var5 = par1 < 0;
        var6 = Math.abs(par1);
        var7 = 0;
        while (var7 < var6) {
            block5: {
                block4: {
                    if (!var5) break block4;
                    while (par3 && var4 > 0 && this.text.charAt(var4 - 1) == ' ') {
                        --var4;
                    }
                    while (var4 > 0 && this.text.charAt(var4 - 1) != ' ') {
                        --var4;
                    }
                    break block5;
                }
                var8 = this.text.length();
                if ((var4 = this.text.indexOf(32, var4)) != -1) ** GOTO lbl20
                var4 = var8;
                break block5;
lbl-1000:
                // 1 sources

                {
                    ++var4;
lbl20:
                    // 2 sources

                    ** while (par3 && var4 < var8 && this.text.charAt((int)var4) == ' ')
                }
            }
            ++var7;
        }
        return var4;
    }

    public void moveCursorBy(int par1) {
        this.setCursorPosition(this.selectionEnd + par1);
    }

    public void setCursorPosition(int par1) {
        this.cursorPosition = par1;
        int var2 = this.text.length();
        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }
        if (this.cursorPosition > var2) {
            this.cursorPosition = var2;
        }
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean textboxKeyTyped(char par1, int par2) {
        if (this.isEnabled && this.isFocused) {
            switch (par1) {
                case '\u0001': {
                    this.setCursorPositionEnd();
                    this.setSelectionPos(0);
                    return true;
                }
                case '\u0003': {
                    GuiScreen.setClipboardString(this.getSelectedtext());
                    return true;
                }
                case '\u0016': {
                    this.writeText(GuiScreen.getClipboardString());
                    return true;
                }
                case '\u0018': {
                    GuiScreen.setClipboardString(this.getSelectedtext());
                    this.writeText("");
                    return true;
                }
            }
            switch (par2) {
                case 14: {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(-1);
                    } else {
                        this.deleteFromCursor(-1);
                    }
                    return true;
                }
                case 199: {
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCursorPositionZero();
                    }
                    return true;
                }
                case 203: {
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    } else {
                        this.moveCursorBy(-1);
                    }
                    return true;
                }
                case 205: {
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }
                    return true;
                }
                case 207: {
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(this.text.length());
                    } else {
                        this.setCursorPositionEnd();
                    }
                    return true;
                }
                case 211: {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(1);
                    } else {
                        this.deleteFromCursor(1);
                    }
                    return true;
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(par1)) {
                this.writeText(Character.toString(par1));
                return true;
            }
            return false;
        }
        return false;
    }

    public void mouseClicked(int par1, int par2, int par3) {
        String displayString = this.text.replace('\u00a7', '?');
        boolean var4 = par1 >= this.xPos && par1 < this.xPos + this.width && par2 >= this.yPos && par2 < this.yPos + this.height;
        this.setFocused(this.isEnabled && var4);
        if (this.isFocused && par3 == 0) {
            int var5 = par1 - this.xPos;
            if (this.enableBackgroundDrawing) {
                var5 -= 4;
            }
            String var6 = this.fontRenderer.trimStringToWidth(displayString.substring(this.field_73816_n), this.getWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(var6, var5).length() + this.field_73816_n);
        }
    }

    public void drawTextBox() {
        String textToDisplay = this.text.replace('\u00a7', '?');
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                GuiTextField.drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
                GuiTextField.drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
            }
            int var1 = this.isEnabled ? this.enabledColor : this.disabledColor;
            int var2 = this.cursorPosition - this.field_73816_n;
            int var3 = this.selectionEnd - this.field_73816_n;
            String var4 = this.fontRenderer.trimStringToWidth(textToDisplay.substring(this.field_73816_n), this.getWidth());
            boolean var5 = var2 >= 0 && var2 <= var4.length();
            boolean var6 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && var5;
            int var7 = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
            int var8 = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;
            int var9 = var7;
            if (var3 > var4.length()) {
                var3 = var4.length();
            }
            if (var4.length() > 0) {
                String var10 = var5 ? var4.substring(0, var2) : var4;
                var9 = this.fontRenderer.drawStringWithShadow(var10, var7, var8, var1);
            }
            boolean var13 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int var11 = var9;
            if (!var5) {
                var11 = var2 > 0 ? var7 + this.width : var7;
            } else if (var13) {
                var11 = var9 - 1;
                --var9;
            }
            if (var4.length() > 0 && var5 && var2 < var4.length()) {
                this.fontRenderer.drawStringWithShadow(var4.substring(var2), var9, var8, var1);
            }
            if (var6) {
                if (var13) {
                    Gui.drawRect(var11, var8 - 1, var11 + 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
                } else {
                    this.fontRenderer.drawStringWithShadow("_", var11, var8, var1);
                }
            }
            if (var3 != var2) {
                int var12 = var7 + this.fontRenderer.getStringWidth(var4.substring(0, var3));
                this.drawCursorVertical(var11, var8 - 1, var12 - 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
        }
    }

    private void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
        if (p_146188_1_ < p_146188_3_) {
            int i2 = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = i2;
        }
        if (p_146188_2_ < p_146188_4_) {
            int j2 = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = j2;
        }
        if (p_146188_3_ > this.xPos + this.width) {
            p_146188_3_ = this.xPos + this.width;
        }
        if (p_146188_1_ > this.xPos + this.width) {
            p_146188_1_ = this.xPos + this.width;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_146188_1_, p_146188_4_, 0.0).endVertex();
        worldrenderer.pos(p_146188_3_, p_146188_4_, 0.0).endVertex();
        worldrenderer.pos(p_146188_3_, p_146188_2_, 0.0).endVertex();
        worldrenderer.pos(p_146188_1_, p_146188_2_, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int par1) {
        this.maxStringLength = par1;
        if (this.text.length() > par1) {
            this.text = this.text.substring(0, par1);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean par1) {
        this.enableBackgroundDrawing = par1;
    }

    public void setTextColor(int par1) {
        this.enabledColor = par1;
    }

    public void func_82266_h(int par1) {
        this.disabledColor = par1;
    }

    public void setFocused(boolean par1) {
        if (par1 && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = par1;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void func_82265_c(boolean par1) {
        this.isEnabled = par1;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void setSelectionPos(int par1) {
        String displayString = this.text.replace('\u00a7', '?');
        int var2 = displayString.length();
        if (par1 > var2) {
            par1 = var2;
        }
        if (par1 < 0) {
            par1 = 0;
        }
        this.selectionEnd = par1;
        if (this.fontRenderer != null) {
            if (this.field_73816_n > var2) {
                this.field_73816_n = var2;
            }
            int var3 = this.getWidth();
            String var4 = this.fontRenderer.trimStringToWidth(displayString.substring(this.field_73816_n), var3);
            int var5 = var4.length() + this.field_73816_n;
            if (par1 == this.field_73816_n) {
                this.field_73816_n -= this.fontRenderer.trimStringToWidth(displayString, var3, true).length();
            }
            if (par1 > var5) {
                this.field_73816_n += par1 - var5;
            } else if (par1 <= this.field_73816_n) {
                this.field_73816_n -= this.field_73816_n - par1;
            }
            if (this.field_73816_n < 0) {
                this.field_73816_n = 0;
            }
            if (this.field_73816_n > var2) {
                this.field_73816_n = var2;
            }
        }
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean par1) {
        this.visible = par1;
    }
}

