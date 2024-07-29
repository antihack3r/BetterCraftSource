/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.awt.Color;
import net.labymod.core.LabyModCore;
import net.labymod.core.WorldRendererAdapter;
import net.labymod.main.LabyMod;
import net.labymod.utils.ModColor;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;

public class ModTextField
extends Gui {
    private final int id;
    private final FontRenderer fontRendererInstance;
    public int xPosition;
    public int yPosition;
    public int width;
    public int height;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private boolean isEnabled = true;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int enabledColor = 0xE0E0E0;
    private int disabledColor = 0x707070;
    private boolean visible = true;
    private Predicate<String> field_175209_y = Predicates.alwaysTrue();
    private boolean blackBox = true;
    private boolean modPasswordBox = false;
    private String modBlacklistWord = "";
    private boolean colorBarEnabled = false;
    private ModColor hoveredModColor = null;
    private String colorAtCursor = null;
    private String placeHolder;
    private boolean backgroundColor = false;

    public ModTextField(int componentId, FontRenderer fontrenderer, int x2, int y2, int par5Width, int par6Height) {
        this.id = componentId;
        this.fontRendererInstance = fontrenderer;
        this.xPosition = x2;
        this.yPosition = y2;
        this.width = par5Width;
        this.height = par6Height;
    }

    public void setBackgroundColor(boolean backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isBackgroundColor() {
        return this.backgroundColor;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String p_146180_1_) {
        if (this.field_175209_y.apply(p_146180_1_)) {
            this.text = p_146180_1_.length() > this.maxStringLength ? p_146180_1_.substring(0, this.maxStringLength) : p_146180_1_;
            this.setCursorPosition(0);
        }
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public String getPlaceHolder() {
        return this.placeHolder;
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i2 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i2, j2);
    }

    public void func_175205_a(Predicate<String> p_175205_1_) {
        this.field_175209_y = p_175205_1_;
    }

    public void writeText(String p_146191_1_) {
        String s2 = "";
        String s22 = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
        int i2 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k2 = this.maxStringLength - this.text.length() - (i2 - j2);
        int l2 = 0;
        if (this.text.length() > 0) {
            s2 = String.valueOf(s2) + this.text.substring(0, i2);
        }
        if (k2 < s22.length()) {
            s2 = String.valueOf(s2) + s22.substring(0, k2);
            l2 = k2;
        } else {
            s2 = String.valueOf(s2) + s22;
            l2 = s22.length();
        }
        if (this.text.length() > 0 && j2 < this.text.length()) {
            s2 = String.valueOf(s2) + this.text.substring(j2);
        }
        if (this.field_175209_y.apply(s2)) {
            this.text = s2;
            this.moveCursorBy(i2 - this.selectionEnd + l2);
        }
    }

    public void deleteWords(int p_146177_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int p_146175_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = p_146175_1_ < 0;
                int i2 = flag ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
                int j2 = flag ? this.cursorPosition : this.cursorPosition + p_146175_1_;
                String s2 = "";
                if (i2 >= 0) {
                    s2 = this.text.substring(0, i2);
                }
                if (j2 < this.text.length()) {
                    s2 = String.valueOf(s2) + this.text.substring(j2);
                }
                if (this.field_175209_y.apply(s2)) {
                    this.text = s2;
                    if (flag) {
                        this.moveCursorBy(p_146175_1_);
                    }
                }
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public int getNthWordFromCursor(int p_146187_1_) {
        return this.getNthWordFromPos(p_146187_1_, this.getCursorPosition());
    }

    public int getNthWordFromPos(int p_146183_1_, int p_146183_2_) {
        return this.func_146197_a(p_146183_1_, p_146183_2_, true);
    }

    /*
     * Unable to fully structure code
     */
    public int func_146197_a(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
        i = p_146197_2_;
        flag = p_146197_1_ < 0;
        j = Math.abs(p_146197_1_);
        k = 0;
        while (k < j) {
            block4: {
                if (flag) ** GOTO lbl15
                l = this.text.length();
                if ((i = this.text.indexOf(32, i)) != -1) ** GOTO lbl12
                i = l;
                break block4;
lbl-1000:
                // 1 sources

                {
                    ++i;
lbl12:
                    // 2 sources

                    ** while (p_146197_3_ && i < l && this.text.charAt((int)i) == ' ')
                }
lbl13:
                // 1 sources

                break block4;
lbl-1000:
                // 1 sources

                {
                    --i;
lbl15:
                    // 2 sources

                    ** while (p_146197_3_ && i > 0 && this.text.charAt((int)(i - 1)) == ' ')
                }
lbl16:
                // 2 sources

                while (i > 0 && this.text.charAt(i - 1) != ' ') {
                    --i;
                }
            }
            ++k;
        }
        return i;
    }

    public void moveCursorBy(int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    public void setCursorPosition(int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        int i2 = this.text.length();
        this.cursorPosition = LabyModCore.getMath().clamp_int(this.cursorPosition, 0, i2);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
        if (!this.isFocused) {
            return false;
        }
        if (GuiScreen.isKeyComboCtrlA(p_146201_2_)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        if (GuiScreen.isKeyComboCtrlC(p_146201_2_)) {
            if (!this.isPasswordBox()) {
                GuiScreen.setClipboardString(this.getSelectedText());
            }
            return true;
        }
        if (GuiScreen.isKeyComboCtrlV(p_146201_2_)) {
            if (this.isEnabled) {
                this.writeText(GuiScreen.getClipboardString());
            }
            return true;
        }
        if (GuiScreen.isKeyComboCtrlX(p_146201_2_)) {
            if (!this.isPasswordBox()) {
                GuiScreen.setClipboardString(this.getSelectedText());
            }
            if (this.isEnabled) {
                this.writeText("");
            }
            return true;
        }
        switch (p_146201_2_) {
            case 14: {
                if (GuiScreen.isCtrlKeyDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(-1);
                    }
                } else if (this.isEnabled) {
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
                    if (this.isEnabled) {
                        this.deleteWords(1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(1);
                }
                return true;
            }
        }
        if (ChatAllowedCharacters.isAllowedCharacter(p_146201_1_)) {
            if (this.isEnabled) {
                this.writeText(Character.toString(p_146201_1_));
            }
            return true;
        }
        return false;
    }

    public boolean mouseClicked(int p_146192_1_, int p_146192_2_, int p_146192_3_) {
        boolean flag;
        boolean bl2 = flag = p_146192_1_ >= this.xPosition && p_146192_1_ < this.xPosition + this.width && p_146192_2_ >= this.yPosition && p_146192_2_ < this.yPosition + this.height;
        if (this.colorBarEnabled && this.hoveredModColor != null) {
            this.writeText("&" + this.hoveredModColor.getColorChar());
            return true;
        }
        if (this.canLoseFocus) {
            this.setFocused(flag);
        }
        if (this.isFocused && flag && p_146192_3_ == 0) {
            int i2 = p_146192_1_ - this.xPosition;
            if (this.enableBackgroundDrawing) {
                i2 -= 4;
            }
            String s2 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(s2, i2).length() + this.lineScrollOffset);
        }
        return this.isFocused;
    }

    public void drawTextBox() {
        if (this.getVisible()) {
            if (!this.getBlacklistWord().isEmpty() && this.getText().contains(this.getBlacklistWord())) {
                this.setText(this.getText().replace(this.getBlacklistWord(), ""));
            }
            if (this.getEnableBackgroundDrawing()) {
                ModTextField.drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
                ModTextField.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
            }
            int i2 = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j2 = this.cursorPosition - this.lineScrollOffset;
            int k2 = this.selectionEnd - this.lineScrollOffset;
            String theText = this.getText().substring(this.lineScrollOffset);
            if (this.isPasswordBox()) {
                theText = theText.replaceAll(".", "*");
            }
            String s2 = this.fontRendererInstance.trimStringToWidth(theText, this.getWidth());
            boolean flag = j2 >= 0 && j2 <= s2.length();
            boolean flag2 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l2 = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int i22 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int j22 = l2;
            this.colorAtCursor = null;
            if (k2 > s2.length()) {
                k2 = s2.length();
            }
            if (s2.length() > 0) {
                String s22 = flag ? s2.substring(0, j2) : s2;
                j22 += this.fontRendererInstance.getStringWidth(this.visualColorForText(s22, true));
            }
            boolean placeHolder = this.placeHolder != null && this.getText().isEmpty() && !this.isFocused;
            boolean flag3 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k22 = j22;
            if (!flag) {
                k22 = j2 > 0 ? l2 + this.width : l2;
            } else if (flag3) {
                k22 = j22 - 1;
                --j22;
            }
            if (s2.length() > 0 && flag && j2 < s2.length()) {
                j22 += this.fontRendererInstance.getStringWidth(this.visualColorForText(s2.substring(j2), false));
            }
            this.fontRendererInstance.drawStringWithShadow(this.visualColorForText(s2, false), l2, i22, i2);
            if (flag2 && !placeHolder) {
                if (flag3) {
                    Gui.drawRect(k22, i22 - 1, k22 + 1, i22 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                } else {
                    this.fontRendererInstance.drawStringWithShadow("_", k22, i22, i2);
                }
            }
            if (k2 != j2) {
                int l22 = l2 + this.fontRendererInstance.getStringWidth(s2.substring(0, k2));
                this.drawCursorVertical(k22, i22 - 1, l22 - 1, i22 + 1 + this.fontRendererInstance.FONT_HEIGHT);
            }
            if (placeHolder) {
                this.drawString(this.fontRendererInstance, this.placeHolder, k22, i22, Color.LIGHT_GRAY.getRGB());
            }
        }
    }

    public void drawColorBar(int mouseX, int mouseY) {
        if (this.colorBarEnabled) {
            this.hoveredModColor = null;
            int ll2 = 9;
            int pX = this.xPosition + this.width / 2 - (ModColor.values().length * 9 - 9) / 2;
            int pY = this.yPosition + this.height + 5;
            ModColor[] modColorArray = ModColor.values();
            int n2 = modColorArray.length;
            int n3 = 0;
            while (n3 < n2) {
                boolean hovered;
                ModColor color = modColorArray[n3];
                boolean bl2 = hovered = mouseX > pX - 4 && mouseX < pX + 4 && mouseY > pY - 1 && mouseY < pY + 9;
                if (hovered) {
                    this.hoveredModColor = color;
                }
                if (this.colorAtCursor != null && this.colorAtCursor.equals("" + color.getColorChar())) {
                    hovered = true;
                }
                ModTextField.drawRect(pX - 4, pY - 1, pX + 4, pY + 9, hovered ? Integer.MAX_VALUE : ModColor.toRGB(120, 120, 120, 120));
                LabyMod.getInstance().getDrawUtils().drawCenteredString(String.valueOf(color.toString()) + color.getColorChar(), pX, pY);
                pX += 9;
                ++n3;
            }
        }
    }

    private String visualColorForText(String text, boolean saveCursorColor) {
        String coloredString = "";
        boolean foundColor = false;
        int i2 = 0;
        while (i2 < text.length()) {
            char c2 = text.charAt(i2);
            if (c2 == '&' && i2 != text.length() - 1) {
                if (foundColor) {
                    coloredString = String.valueOf(coloredString) + "&";
                }
                foundColor = true;
            } else {
                if (foundColor) {
                    foundColor = false;
                    coloredString = String.valueOf(coloredString) + "\u00c2\u00a7" + c2 + '&';
                    if (saveCursorColor) {
                        this.colorAtCursor = "" + c2;
                    }
                }
                coloredString = String.valueOf(coloredString) + c2;
            }
            ++i2;
        }
        return coloredString;
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
        if (p_146188_3_ > this.xPosition + this.width) {
            p_146188_3_ = this.xPosition + this.width;
        }
        if (p_146188_1_ > this.xPosition + this.width) {
            p_146188_1_ = this.xPosition + this.width;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
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

    public void setMaxStringLength(int p_146203_1_) {
        this.maxStringLength = p_146203_1_;
        if (this.text.length() > p_146203_1_) {
            this.text = this.text.substring(0, p_146203_1_);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public void setBlacklistWord(String modBlacklistWords) {
        this.modBlacklistWord = modBlacklistWords;
    }

    public String getBlacklistWord() {
        return this.modBlacklistWord;
    }

    public void setPasswordBox(boolean modPasswordBox) {
        this.modPasswordBox = modPasswordBox;
    }

    public boolean isPasswordBox() {
        return this.modPasswordBox;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean p_146185_1_) {
        this.enableBackgroundDrawing = p_146185_1_;
    }

    public boolean isBlackBox() {
        return this.blackBox;
    }

    public void setBlackBox(boolean blackBox) {
        this.blackBox = blackBox;
    }

    public void setTextColor(int p_146193_1_) {
        this.enabledColor = p_146193_1_;
    }

    public void setDisabledTextColour(int p_146204_1_) {
        this.disabledColor = p_146204_1_;
    }

    public void setFocused(boolean p_146195_1_) {
        if (p_146195_1_ && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = p_146195_1_;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setEnabled(boolean p_146184_1_) {
        this.isEnabled = p_146184_1_;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void setSelectionPos(int p_146199_1_) {
        int i2 = this.text.length();
        if (p_146199_1_ > i2) {
            p_146199_1_ = i2;
        }
        if (p_146199_1_ < 0) {
            p_146199_1_ = 0;
        }
        this.selectionEnd = p_146199_1_;
        if (this.fontRendererInstance != null) {
            if (this.lineScrollOffset > i2) {
                this.lineScrollOffset = i2;
            }
            int j2 = this.getWidth();
            String s2 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), j2);
            int k2 = s2.length() + this.lineScrollOffset;
            if (p_146199_1_ == this.lineScrollOffset) {
                this.lineScrollOffset -= this.fontRendererInstance.trimStringToWidth(this.text, j2, true).length();
            }
            if (p_146199_1_ > k2) {
                this.lineScrollOffset += p_146199_1_ - k2;
            } else if (p_146199_1_ <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - p_146199_1_;
            }
            this.lineScrollOffset = LabyModCore.getMath().clamp_int(this.lineScrollOffset, 0, i2);
        }
    }

    public void setCanLoseFocus(boolean p_146205_1_) {
        this.canLoseFocus = p_146205_1_;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean p_146189_1_) {
        this.visible = p_146189_1_;
    }

    public boolean isColorBarEnabled() {
        return this.colorBarEnabled;
    }

    public void setColorBarEnabled(boolean colorBar) {
        this.colorBarEnabled = colorBar;
    }
}

