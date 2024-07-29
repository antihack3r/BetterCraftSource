/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tabs;

import java.io.IOException;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.ingamechat.tools.shortcuts.Shortcuts;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;

public class GuiChatShortcuts
extends GuiChatCustom {
    private final int windowWidth = 148;
    private final int windowHeight = 194;
    private Scrollbar scrollbar = new Scrollbar(10);
    private Shortcuts.Shortcut openShortcutComponent = null;
    private Shortcuts.Shortcut hoverShortcutComponent = null;
    private boolean hoverAddButton = false;
    private boolean hoverCancelButton = false;
    private boolean hoverSaveButton = false;
    private boolean hoverDeleteButton = false;
    private ModTextField fieldShortcut;
    private ModTextField fieldReplacement;
    private boolean canHighlightRed = false;

    public GuiChatShortcuts(String defaultText) {
        super(defaultText);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.fieldShortcut = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 0, 10);
        this.fieldReplacement = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 0, 10);
        this.fieldShortcut.setEnableBackgroundDrawing(false);
        this.fieldReplacement.setEnableBackgroundDrawing(false);
        this.fieldShortcut.setMaxStringLength(120);
        this.fieldReplacement.setMaxStringLength(120);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        draw = LabyMod.getInstance().getDrawUtils();
        margin = 2;
        chatHeight = 16;
        width = draw.getWidth();
        this.getClass();
        x = width - 148 - 2;
        height = draw.getHeight();
        this.getClass();
        y = height - 194 - 16;
        n = x;
        n2 = y;
        n3 = x;
        this.getClass();
        n4 = n3 + 148;
        n5 = y;
        this.getClass();
        DrawUtils.drawRect(n, n2, n4, n5 + 194, -2147483648);
        draw.drawString(LanguageManager.translate("ingame_chat_tab_shortcut"), x, y - 10);
        this.hoverShortcutComponent = null;
        this.hoverDeleteButton = false;
        shortcuts = LabyMod.getInstance().getChatToolManager().getShortcuts();
        this.scrollbar.setPosition(draw.getWidth() - 2 - 1, y + 2, draw.getWidth() - 2, draw.getHeight() - 16 - 2);
        this.scrollbar.update(shortcuts.size());
        listPadding = 2;
        listX = x + 2;
        listY = (double)(y + 2) + this.scrollbar.getScrollY();
        for (Shortcuts.Shortcut shortcutComponent : shortcuts) {
            block8: {
                if (!(listY >= (double)y) || !(listY < (double)(draw.getHeight() - 16 - 2))) break block8;
                drawUtils = draw;
                left = listX - 2;
                top = listY;
                n6 = listX - 2;
                this.getClass();
                hover = drawUtils.drawRect(mouseX, mouseY, left, top, n6 + 148, listY + 10.0, 0, ModColor.toRGB(100, 200, 200, 100));
                shortcut = shortcutComponent.getShortcut();
                draw.drawString(shortcut, listX, listY + 1.0);
                b = false;
                if (!hover) ** GOTO lbl-1000
                n7 = listX - 2;
                this.getClass();
                if (mouseX > n7 + 148 - 10) {
                    b = true;
                } else lbl-1000:
                // 2 sources

                {
                    b = false;
                }
                hoverDeleteButton = b;
                if (hover) {
                    drawUtils2 = draw;
                    string = String.valueOf(ModColor.cl(hoverDeleteButton != false ? 'c' : '4')) + "\u2718";
                    n8 = listX - 2;
                    this.getClass();
                    drawUtils2.drawRightString(string, n8 + 148, listY + 1.0);
                }
                if (hover) {
                    this.hoverShortcutComponent = shortcutComponent;
                    this.hoverDeleteButton = hoverDeleteButton;
                }
            }
            listY += 10.0;
        }
        this.scrollbar.draw();
        if (this.openShortcutComponent == null) {
            buttonSize = 14;
            buttonMargin = 2;
            this.hoverAddButton = draw.drawRect(mouseX, mouseY, String.valueOf(this.hoverAddButton != false ? ModColor.cl('a') : "") + "+", x - 14 - 2, y, x - 2, y + 14, -2147483648, 0x7FFFFFFF);
            this.hoverSaveButton = false;
            this.hoverCancelButton = false;
        } else {
            editorWidth = 130;
            editorMargin = 2;
            editorPadding = 2;
            editorYIndex = y + 2;
            n9 = x - 130 - 2;
            n10 = y;
            n11 = x - 2;
            n12 = y;
            this.getClass();
            DrawUtils.drawRect(n9, n10, n11, n12 + 194, -2147483648);
            draw.drawString(String.valueOf(LanguageManager.translate("chat_shortcut_shortcut")) + ":", x - 130 + 2 - 2, editorYIndex);
            this.fieldShortcut.width = 126;
            this.fieldShortcut.xPosition = x - 130 + 2 - 2;
            this.fieldShortcut.yPosition = (editorYIndex += 10) + 1;
            fieldShortcutColor = this.canHighlightRed != false && this.fieldShortcut.getText().isEmpty() != false ? ModColor.toRGB(200, 100, 100, 200) : 0x7FFFFFFF;
            DrawUtils.drawRect(x - 130 + 2 - 2, editorYIndex, x - 2 - 2, editorYIndex + 10, fieldShortcutColor);
            this.fieldShortcut.drawTextBox();
            draw.drawString(String.valueOf(LanguageManager.translate("chat_shortcut_replacement")) + ":", x - 130 + 2 - 2, editorYIndex += 15);
            this.fieldReplacement.width = 126;
            this.fieldReplacement.xPosition = x - 130 + 2 - 2;
            this.fieldReplacement.yPosition = (editorYIndex += 10) + 1;
            fieldReplacementColor = this.canHighlightRed != false && this.fieldReplacement.getText().isEmpty() != false ? ModColor.toRGB(200, 100, 100, 200) : 0x7FFFFFFF;
            DrawUtils.drawRect(x - 130 + 2 - 2, editorYIndex, x - 2 - 2, editorYIndex + 10, fieldReplacementColor);
            this.fieldReplacement.drawTextBox();
            editorYIndex += 13;
            if (mouseX > this.fieldReplacement.xPosition && mouseX < this.fieldReplacement.xPosition + this.fieldReplacement.width && mouseY > this.fieldReplacement.yPosition && mouseY < this.fieldReplacement.yPosition + this.fieldReplacement.height) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, new String[]{"%s = Username"});
            }
            buttonHeight = 12;
            drawUtils3 = draw;
            translate = LanguageManager.translate("button_cancel");
            left2 = x - 130 + 2 - 2;
            n13 = y;
            this.getClass();
            top2 = n13 + 194 - 2 - 12;
            right = x - 130 + 2 - 2 + 65 - 2;
            n14 = y;
            this.getClass();
            this.hoverCancelButton = drawUtils3.drawRect(mouseX, mouseY, translate, left2, top2, right, n14 + 194 - 2, 0x7FFFFFFF, ModColor.toRGB(200, 100, 100, 200));
            drawUtils4 = draw;
            translate2 = LanguageManager.translate("button_save");
            left3 = x - 130 + 2 - 2 + 65 + 2;
            n15 = y;
            this.getClass();
            top3 = n15 + 194 - 2 - 12;
            right2 = x - 2 - 2;
            n16 = y;
            this.getClass();
            this.hoverSaveButton = drawUtils4.drawRect(mouseX, mouseY, translate2, left3, top3, right2, n16 + 194 - 2, 0x7FFFFFFF, ModColor.toRGB(100, 200, 100, 200));
            this.hoverAddButton = false;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.hoverAddButton && mouseButton == 0) {
            this.openShortcutComponent = new Shortcuts.Shortcut("", "");
            this.canHighlightRed = false;
            this.fieldShortcut.setText("");
            this.fieldReplacement.setText("");
        }
        if (this.hoverShortcutComponent != null && mouseButton == 0) {
            if (this.hoverDeleteButton) {
                List<Shortcuts.Shortcut> shortcuts = LabyMod.getInstance().getChatToolManager().getShortcuts();
                shortcuts.remove(this.hoverShortcutComponent);
                if (this.hoverShortcutComponent == this.openShortcutComponent) {
                    this.openShortcutComponent = null;
                }
                LabyMod.getInstance().getChatToolManager().saveTools();
            } else {
                this.openShortcutComponent = this.hoverShortcutComponent;
                this.canHighlightRed = false;
                this.fieldShortcut.setText(this.openShortcutComponent.getShortcut());
                this.fieldReplacement.setText(this.openShortcutComponent.getReplacement());
            }
        }
        if (this.hoverCancelButton && mouseButton == 0) {
            this.openShortcutComponent = null;
        }
        if (this.hoverSaveButton && mouseButton == 0) {
            String shortcut = this.fieldShortcut.getText();
            String replacement = this.fieldReplacement.getText();
            if (shortcut.isEmpty() || replacement.isEmpty()) {
                this.canHighlightRed = true;
                return;
            }
            this.canHighlightRed = false;
            this.openShortcutComponent.setShortcut(this.fieldShortcut.getText());
            this.openShortcutComponent.setReplacement(this.fieldReplacement.getText());
            List<Shortcuts.Shortcut> shortcuts2 = LabyMod.getInstance().getChatToolManager().getShortcuts();
            if (!shortcuts2.contains(this.openShortcutComponent)) {
                shortcuts2.add(this.openShortcutComponent);
            }
            this.openShortcutComponent = null;
            LabyMod.getInstance().getChatToolManager().saveTools();
        }
        if (this.openShortcutComponent != null) {
            this.fieldShortcut.mouseClicked(mouseX, mouseY, mouseButton);
            this.fieldReplacement.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.openShortcutComponent != null) {
            this.fieldShortcut.textboxKeyTyped(typedChar, keyCode);
            this.fieldReplacement.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 15) {
                if (this.fieldShortcut.isFocused()) {
                    this.fieldShortcut.setFocused(false);
                    this.fieldReplacement.setFocused(true);
                } else {
                    this.fieldShortcut.setFocused(true);
                    this.fieldReplacement.setFocused(false);
                }
            }
            if (keyCode == 1) {
                super.keyTyped(typedChar, keyCode);
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }

    @Override
    public void updateScreen() {
        if (this.openShortcutComponent != null) {
            this.fieldShortcut.updateCursorCounter();
            this.fieldReplacement.updateCursorCounter();
        }
    }
}

