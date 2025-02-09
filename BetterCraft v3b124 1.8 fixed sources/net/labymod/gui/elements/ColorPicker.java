/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import java.awt.Color;
import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.settings.LabyModModuleEditorGui;
import net.labymod.settings.PreviewRenderer;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class ColorPicker
extends Gui {
    private static final int[] ADVANCED_COLORS = new int[]{-4842468, -7795121, -11922292, -13624430, -15064194, -15841375, -16754788, -16687004, -16757697, -14918112, -13407970, -8292586, -753898, -37120, -1683200, -4246004, -12704222, -14606047, -14208456};
    private String title;
    private int x;
    private int y;
    private int width;
    private int height;
    private Color selectedColor;
    private Color colorForPreview;
    private boolean openedSelector;
    private boolean hoverSlider = false;
    private boolean hasAdvanced = false;
    private boolean hoverAdvancedButton = false;
    private boolean hoverDefaultButton;
    private boolean hasDefault = false;
    private boolean isDefault = true;
    private DefaultColorCallback defaultColor;
    private Consumer<Color> updateListener;

    public ColorPicker(String title, Color selectedColor, DefaultColorCallback defaultColorCallback, int x2, int y2, int width, int height) {
        this.title = title;
        this.x = x2;
        this.y = y2;
        this.width = width;
        this.height = height;
        this.selectedColor = selectedColor;
        this.colorForPreview = selectedColor;
        this.defaultColor = defaultColorCallback;
    }

    public void onGuiClosed() {
        if (this.colorForPreview != this.selectedColor && this.updateListener != null) {
            this.updateListener.accept(this.selectedColor);
        }
    }

    public void drawColorPicker(int mouseX, int mouseY) {
        LabyMod.getInstance().getDrawUtils().drawCenteredString(this.title, this.x + this.width / 2, this.y - 5, 0.5);
        ColorPicker.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.openedSelector ? -1 : Integer.MAX_VALUE);
        int bgColor = this.colorForPreview == null ? (this.defaultColor == null ? (this.openedSelector ? Integer.MIN_VALUE : Integer.MAX_VALUE) : this.defaultColor.getDefaultColor().getRGB()) : this.colorForPreview.getRGB();
        ColorPicker.drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, bgColor);
        if (this.hasDefault && this.selectedColor == null && this.isDefault) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_HOVER_DEFAULT);
            Color color = new Color(bgColor);
            int luma = (int)(0.2126f * (float)color.getRed() + 0.7152f * (float)color.getGreen() + 0.0722f * (float)color.getBlue());
            luma = 255 - luma;
            if (luma < 80) {
                GlStateManager.color((float)luma / 255.0f, (float)luma / 255.0f, (float)luma / 255.0f, 1.0f);
                LabyMod.getInstance().getDrawUtils().drawTexture(this.x + 2, this.y + 2, 256.0, 256.0, this.width - 4, this.height - 4, 1.1f);
            } else {
                LabyMod.getInstance().getDrawUtils().drawTexture(this.x + 2, this.y + 2, 256.0, 256.0, this.width - 4, this.height - 4);
            }
            if (this.isMouseOver(mouseX, mouseY)) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 200L, "Default");
            }
        }
        if (this.openedSelector) {
            this.drawColorsAndButtons(mouseX, mouseY);
        }
    }

    private Color getContrastColor(int r2, int g2, int b2) {
        double y2 = (299 * r2 + 587 * g2 + 114 * b2) / 1000;
        return y2 >= 128.0 ? Color.black : Color.white;
    }

    private void drawColorsAndButtons(int mouseX, int mouseY) {
        int widthPerColor = 13;
        int sliderHeight = 13;
        int sliderWidth = 0;
        ModColor[] modColorArray = ModColor.values();
        int n2 = modColorArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ModColor color = modColorArray[n3];
            if (color.getColor() != null) {
                sliderWidth += 13;
            }
            ++n3;
        }
        int sliderX = this.x - sliderWidth / 2 + this.width / 2;
        int sliderY = this.y + this.height + 4;
        if (this.hasAdvanced) {
            sliderX -= 20;
        }
        int minX = this.hasDefault && this.selectedColor != null ? 20 : 5;
        int maxX = LabyMod.getInstance().getDrawUtils().getWidth() - 5;
        int maxY = LabyMod.getInstance().getDrawUtils().getHeight() - 5;
        if (sliderX + sliderWidth > maxX) {
            sliderX -= sliderX + sliderWidth - maxX;
        }
        if (sliderX < minX) {
            sliderX = minX;
        }
        if (sliderY > maxY) {
            sliderY = maxY - 13 - this.height;
        } else {
            ColorPicker.drawRect(this.x + this.width / 2 - 1, sliderY - 3, this.x + this.width / 2 + 1, sliderY - 2, Integer.MAX_VALUE);
            ColorPicker.drawRect(this.x + this.width / 2 - 2, sliderY - 2, this.x + this.width / 2 + 2, sliderY - 1, Integer.MAX_VALUE);
        }
        if (!(Minecraft.getMinecraft().currentScreen instanceof AdvancedColorSelectorGui)) {
            this.drawSlider(mouseX, mouseY, sliderX, sliderY, sliderWidth, 13, 13);
            this.drawButtons(mouseX, mouseY, sliderX, sliderY, sliderWidth, 13, 13);
        }
        this.hoverAdvancedButton = mouseX > sliderX + sliderWidth + 3 - 1 && mouseX < sliderX + sliderWidth + 3 + 13 + 1 && mouseY > sliderY - 1 && mouseY < sliderY + 13 + 1;
        this.hoverSlider = mouseX > sliderX && mouseX < sliderX + sliderWidth + 13 && mouseY > sliderY && mouseY < sliderY + 13;
        this.hoverDefaultButton = mouseX > sliderX - 3 - 13 - 1 && mouseX < sliderX - 3 + 1 && mouseY > sliderY - 1 && mouseY < sliderY + 13 + 1;
    }

    private void drawSlider(int mouseX, int mouseY, int sliderX, int sliderY, int sliderWidth, int sliderHeight, int widthPerColor) {
        ColorPicker.drawRect(sliderX - 1, sliderY - 1, sliderX + sliderWidth + 1, sliderY + sliderHeight + 1, Integer.MAX_VALUE);
        int pos = 0;
        int hoverPos = -1;
        int selectedPos = -1;
        ModColor hoverColorType = null;
        ModColor selectedColorType = null;
        ModColor[] modColorArray = ModColor.values();
        int n2 = modColorArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ModColor color = modColorArray[n3];
            if (color.getColor() != null) {
                boolean hoverColor;
                ColorPicker.drawRect(sliderX + pos, sliderY, sliderX + pos + widthPerColor, sliderY + sliderHeight, color.getColor().getRGB());
                boolean bl2 = hoverColor = mouseX > sliderX + pos && mouseX < sliderX + pos + widthPerColor + 1 && mouseY > sliderY && mouseY < sliderY + sliderHeight;
                if (hoverPos == -1 && hoverColorType == null && hoverColor) {
                    hoverPos = pos;
                    hoverColorType = color;
                }
                if (color.getColor() == this.selectedColor) {
                    selectedPos = pos;
                    selectedColorType = color;
                }
                pos += widthPerColor;
            }
            ++n3;
        }
        if (hoverColorType != null) {
            ColorPicker.drawRect(sliderX + hoverPos - 1, sliderY - 1, sliderX + hoverPos + widthPerColor + 1, sliderY + sliderHeight + 1, hoverColorType.getColor().getRGB());
            this.colorForPreview = hoverColorType.getColor();
            if (this.updateListener != null) {
                this.updateListener.accept(this.colorForPreview);
            }
        } else {
            this.colorForPreview = this.selectedColor;
            if (this.updateListener != null) {
                this.updateListener.accept(this.selectedColor);
            }
        }
        if (selectedColorType != null) {
            ColorPicker.drawRect(sliderX + selectedPos - 1, sliderY - 1, sliderX + selectedPos + widthPerColor + 1, sliderY + sliderHeight + 1, -1);
            ColorPicker.drawRect(sliderX + selectedPos, sliderY, sliderX + selectedPos + widthPerColor, sliderY + sliderHeight, selectedColorType.getColor().getRGB());
        }
    }

    private void drawAdvanced(int mouseX, int mouseY, int advancedX, int advancedY, int advancedWidth) {
        double heightPerColor;
        int alphaCount = 12;
        double widthPerColor = heightPerColor = (double)advancedWidth / (double)ADVANCED_COLORS.length;
        ColorPicker.drawRect(advancedX - 1, advancedY - 1, (int)((double)advancedX + widthPerColor * (double)ADVANCED_COLORS.length + 1.0), (int)((double)advancedY + heightPerColor * 12.0 + 1.0), Integer.MAX_VALUE);
        double hoverPosX = -1.0;
        double hoverPosY = -1.0;
        Color hoveredColorType = null;
        double selectedPosX = -1.0;
        double selectedPosY = -1.0;
        Color selectedColorType = null;
        double posX = 0.0;
        int[] nArray = ADVANCED_COLORS;
        int n2 = ADVANCED_COLORS.length;
        int n3 = 0;
        while (n3 < n2) {
            int color = nArray[n3];
            int posY = 0;
            while (posY < 12) {
                boolean hoverColor;
                int rgb = ModColor.changeBrightness(new Color(color), 0.07f * (float)posY).getRGB();
                ColorPicker.drawRect((int)((double)advancedX + posX), (int)((double)advancedY + (double)posY * heightPerColor), (int)((double)advancedX + posX + widthPerColor), (int)((double)advancedY + (double)posY * heightPerColor + heightPerColor), rgb);
                boolean bl2 = hoverColor = (double)mouseX > (double)advancedX + posX && (double)mouseX < (double)advancedX + posX + widthPerColor + 1.0 && (double)mouseY > (double)advancedY + (double)posY * heightPerColor && (double)mouseY < (double)advancedY + (double)posY * heightPerColor + heightPerColor + 1.0;
                if (hoverColor) {
                    hoverPosX = posX;
                    hoverPosY = posY;
                    hoveredColorType = new Color(rgb);
                }
                if (this.selectedColor != null && rgb == this.selectedColor.getRGB()) {
                    selectedPosX = posX;
                    selectedPosY = posY;
                    selectedColorType = this.selectedColor;
                }
                ++posY;
            }
            posX += widthPerColor;
            ++n3;
        }
        if (hoveredColorType != null) {
            ColorPicker.drawRect((int)((double)advancedX + hoverPosX - 1.0), (int)((double)advancedY + hoverPosY * heightPerColor - 1.0), (int)((double)advancedX + hoverPosX + widthPerColor + 1.0), (int)((double)advancedY + hoverPosY * heightPerColor + heightPerColor + 1.0), hoveredColorType.getRGB());
            this.colorForPreview = hoveredColorType;
            if (this.updateListener != null) {
                this.updateListener.accept(this.colorForPreview);
            }
        } else {
            this.colorForPreview = this.selectedColor;
            if (this.updateListener != null) {
                this.updateListener.accept(this.selectedColor);
            }
        }
        if (selectedColorType != null) {
            ColorPicker.drawRect((int)((double)advancedX + selectedPosX - 1.0), (int)((double)advancedY + selectedPosY * heightPerColor - 1.0), (int)((double)advancedX + selectedPosX + widthPerColor + 1.0), (int)((double)advancedY + selectedPosY * heightPerColor + heightPerColor + 1.0), -1);
            ColorPicker.drawRect((int)((double)advancedX + selectedPosX), (int)((double)advancedY + selectedPosY * heightPerColor), (int)((double)advancedX + selectedPosX + widthPerColor), (int)((double)advancedY + selectedPosY * heightPerColor + heightPerColor), selectedColorType.getRGB());
        }
    }

    private void drawButtons(int mouseX, int mouseY, int sliderX, int sliderY, int sliderWidth, int sliderHeight, int widthPerColor) {
        if (this.hasDefault && this.selectedColor != null) {
            ColorPicker.drawRect(sliderX - 3 - widthPerColor - 1, sliderY - 1, sliderX - 3 + 1, sliderY + sliderHeight + 1, this.hoverDefaultButton ? -1 : Integer.MAX_VALUE);
            ColorPicker.drawRect(sliderX - 3 - widthPerColor, sliderY, sliderX - 3, sliderY + sliderHeight, Integer.MIN_VALUE);
            LabyMod.getInstance().getDrawUtils().fontRenderer.drawString("D", sliderX - 3 - widthPerColor / 2 - 3, sliderY + sliderHeight / 2 - 3, -1, false);
            if (this.hoverDefaultButton) {
                this.updateListener.accept(null);
                this.colorForPreview = this.defaultColor.getDefaultColor();
            }
        }
        if (this.hasAdvanced) {
            ColorPicker.drawRect(sliderX + sliderWidth + 3 - 1, sliderY - 1, sliderX + sliderWidth + 3 + widthPerColor + 1, sliderY + sliderHeight + 1, this.hoverAdvancedButton ? -1 : Integer.MAX_VALUE);
            ColorPicker.drawRect(sliderX + sliderWidth + 3, sliderY, sliderX + sliderWidth + 3 + widthPerColor, sliderY + sliderHeight, -1);
            int iconX = sliderX + sliderWidth + 3;
            int iconY = sliderY;
            double pxlX = iconX;
            int[] nArray = ADVANCED_COLORS;
            int n2 = ADVANCED_COLORS.length;
            int n3 = 0;
            while (n3 < n2) {
                int color = nArray[n3];
                int pxlY = iconY;
                int i2 = 0;
                while (i2 < 13) {
                    Color theColor = new Color(color + i2 * 2000);
                    int rgb = ModColor.toRGB(theColor.getRed(), theColor.getGreen(), theColor.getBlue(), 255 - i2 * 18);
                    ColorPicker.drawRect((int)pxlX, pxlY, (int)pxlX + 1, pxlY + 1, rgb);
                    ++pxlY;
                    ++i2;
                }
                pxlX += 0.7;
                ++n3;
            }
            if (this.hoverAdvancedButton) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "More colors");
            }
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.openedSelector = !this.openedSelector;
            return true;
        }
        if (this.openedSelector) {
            if (this.hoverSlider) {
                this.selectedColor = this.colorForPreview;
                if (this.updateListener != null) {
                    this.updateListener.accept(this.selectedColor);
                }
            }
            if (this.hasDefault && this.selectedColor != null && this.hoverDefaultButton) {
                this.selectedColor = null;
                this.colorForPreview = this.defaultColor.getDefaultColor();
                if (this.updateListener != null) {
                    this.updateListener.accept(this.selectedColor);
                }
                return true;
            }
            if (this.hasAdvanced && this.hoverAdvancedButton) {
                Minecraft.getMinecraft().displayGuiScreen(new AdvancedColorSelectorGui(this, Minecraft.getMinecraft().currentScreen, new Consumer<GuiScreen>(){

                    @Override
                    public void accept(GuiScreen lastScreen) {
                        Minecraft.getMinecraft().displayGuiScreen(lastScreen);
                    }
                }));
                return true;
            }
        }
        boolean flag = this.openedSelector;
        this.openedSelector = false;
        return flag ^ this.openedSelector;
    }

    public boolean mouseDragging(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        this.colorForPreview = selectedColor;
    }

    public boolean isHoverAdvancedButton() {
        return this.hoverAdvancedButton;
    }

    public boolean isHoverDefaultButton() {
        return this.hoverDefaultButton;
    }

    public boolean isHoverSlider() {
        return this.hoverSlider;
    }

    public Color getSelectedColor() {
        return this.selectedColor;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x2) {
        this.x = x2;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y2) {
        this.y = y2;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
    }

    public String getTitle() {
        return this.title;
    }

    public Color getColorForPreview() {
        return this.colorForPreview;
    }

    public void setHasDefault(boolean hasDefault) {
        this.hasDefault = hasDefault;
    }

    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }

    public void setHasAdvanced(boolean hasAdvanced) {
        this.hasAdvanced = hasAdvanced;
    }

    public void setUpdateListener(Consumer<Color> updateListener) {
        this.updateListener = updateListener;
    }

    public class AdvancedColorSelectorGui
    extends GuiScreen {
        private GuiScreen backgroundScreen;
        private Consumer<GuiScreen> callback;
        private ColorPicker colorPicker;
        private GuiTextField fieldHexColor;
        private Color lastColor = null;
        private boolean validHex = true;

        public AdvancedColorSelectorGui(ColorPicker colorPicker2, GuiScreen backgroundScreen, Consumer<GuiScreen> callback) {
            this.backgroundScreen = backgroundScreen;
            this.callback = callback;
            this.colorPicker = colorPicker2;
        }

        @Override
        public void initGui() {
            super.initGui();
            GuiScreen.width = width;
            GuiScreen.height = height;
            if (this.backgroundScreen instanceof LabyModModuleEditorGui) {
                PreviewRenderer.getInstance().init(AdvancedColorSelectorGui.class);
            }
            this.fieldHexColor = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 70, height / 4 + 115, 100, 16);
            this.fieldHexColor.setMaxStringLength(7);
            this.lastColor = null;
            this.buttonList.add(new GuiButton(1, width / 2 + 40, height / 4 + 113, 60, 20, "Done"));
        }

        @Override
        public void onGuiClosed() {
            this.backgroundScreen.onGuiClosed();
            super.onGuiClosed();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.backgroundScreen.drawScreen(mouseX, mouseY, partialTicks);
            AdvancedColorSelectorGui.drawRect(0, 0, width, height, Integer.MIN_VALUE);
            this.colorPicker.drawColorPicker(mouseX, mouseY);
            AdvancedColorSelectorGui.drawRect(width / 2 - 105, height / 4 - 25, width / 2 + 105, height / 4 + 140, Integer.MIN_VALUE);
            this.colorPicker.drawAdvanced(mouseX, mouseY, AdvancedColorSelectorGui.width / 2 - 100, AdvancedColorSelectorGui.height / 4 - 20, 200);
            AdvancedColorSelectorGui.drawRect(LabyModCore.getMinecraft().getXPosition(this.fieldHexColor) - 2, LabyModCore.getMinecraft().getYPosition(this.fieldHexColor) - 2, LabyModCore.getMinecraft().getXPosition(this.fieldHexColor) + 100 + 2, LabyModCore.getMinecraft().getYPosition(this.fieldHexColor) + 16 + 2, this.validHex ? ModColor.toRGB(85, 255, 85, 100) : ModColor.toRGB(255, 85, 85, 100));
            this.fieldHexColor.drawTextBox();
            if (this.colorPicker.colorForPreview == null) {
                this.colorPicker.colorForPreview = this.colorPicker.defaultColor.getDefaultColor();
            }
            AdvancedColorSelectorGui.drawRect(width / 2 - 100, height / 4 + 113, width / 2 - 100 + 20, height / 4 + 113 + 20, Integer.MAX_VALUE);
            AdvancedColorSelectorGui.drawRect(width / 2 - 100 + 1, height / 4 + 113 + 1, width / 2 - 100 + 20 - 1, height / 4 + 113 + 20 - 1, this.colorPicker.colorForPreview.getRGB());
            if (this.lastColor == null || !this.lastColor.equals(this.colorPicker.colorForPreview)) {
                this.lastColor = this.colorPicker.colorForPreview;
                String hex = String.format("#%02x%02x%02x", this.lastColor.getRed(), this.lastColor.getGreen(), this.lastColor.getBlue());
                this.fieldHexColor.setText(hex);
                this.validHex = true;
            }
            AdvancedColorSelectorGui.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), "Advanced colors", width / 2, height / 4 - 35, -1);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.colorPicker.selectedColor = this.colorPicker.colorForPreview;
            if (this.colorPicker.updateListener != null) {
                this.colorPicker.updateListener.accept(this.colorPicker.selectedColor);
            }
            this.fieldHexColor.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
            }
            if (this.fieldHexColor.textboxKeyTyped(typedChar, keyCode)) {
                String hex = this.fieldHexColor.getText();
                if (hex.length() == 7) {
                    try {
                        this.colorPicker.selectedColor = new Color(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16), Integer.valueOf(hex.substring(5, 7), 16));
                        this.colorPicker.colorForPreview = this.colorPicker.selectedColor;
                        this.validHex = true;
                    }
                    catch (Exception error) {
                        this.validHex = false;
                    }
                } else {
                    this.validHex = false;
                }
            }
        }

        @Override
        public void updateScreen() {
            this.backgroundScreen.updateScreen();
            this.fieldHexColor.updateCursorCounter();
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            super.actionPerformed(button);
            if (button.id == 1) {
                Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
            }
        }

        public GuiScreen getBackgroundScreen() {
            return this.backgroundScreen;
        }
    }

    public static interface DefaultColorCallback {
        public Color getDefaultColor();
    }
}

