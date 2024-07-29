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
import net.labymod.ingamechat.tools.playermenu.PlayerMenu;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;

public class GuiChatPlayerMenu
extends GuiChatCustom {
    private final int windowWidth = 148;
    private final int windowHeight = 194;
    private Scrollbar scrollbar = new Scrollbar(10);
    private PlayerMenu.PlayerMenuEntry openPlayerMenuEntry = null;
    private PlayerMenu.PlayerMenuEntry hoverPlayerMenuEntry = null;
    private boolean hoverSendInstantlyCheckbox = false;
    private boolean hoverAddButton = false;
    private boolean hoverCancelButton = false;
    private boolean hoverSaveButton = false;
    private boolean hoverDeleteButton = false;
    private ModTextField fieldDisplayName;
    private ModTextField fieldCommand;
    private boolean canHighlightRed = false;

    public GuiChatPlayerMenu(String defaultText) {
        super(defaultText);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.fieldDisplayName = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 0, 10);
        this.fieldCommand = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 0, 10);
        this.fieldDisplayName.setEnableBackgroundDrawing(false);
        this.fieldCommand.setEnableBackgroundDrawing(false);
        this.fieldDisplayName.setMaxStringLength(120);
        this.fieldCommand.setMaxStringLength(120);
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
        draw.drawString(LanguageManager.translate("ingame_chat_tab_playermenu"), x, y - 10);
        this.hoverPlayerMenuEntry = null;
        this.hoverDeleteButton = false;
        playerMenuEntries = LabyMod.getInstance().getChatToolManager().getPlayerMenu();
        this.scrollbar.setPosition(draw.getWidth() - 2 - 1, y + 2, draw.getWidth() - 2, draw.getHeight() - 16 - 2);
        this.scrollbar.update(playerMenuEntries.size());
        listPadding = 2;
        listX = x + 2;
        listY = (double)(y + 2) + this.scrollbar.getScrollY();
        for (PlayerMenu.PlayerMenuEntry playerMenuComponent : playerMenuEntries) {
            block8: {
                if (!(listY >= (double)y) || !(listY < (double)(draw.getHeight() - 16 - 2))) break block8;
                drawUtils = draw;
                left = listX - 2;
                top = listY;
                n6 = listX - 2;
                this.getClass();
                hover = drawUtils.drawRect(mouseX, mouseY, left, top, n6 + 148, listY + 10.0, 0, ModColor.toRGB(100, 200, 200, 100));
                displayName = playerMenuComponent.getDisplayName();
                draw.drawString(displayName, listX, listY + 1.0);
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
                    this.hoverPlayerMenuEntry = playerMenuComponent;
                    this.hoverDeleteButton = hoverDeleteButton;
                }
            }
            listY += 10.0;
        }
        this.scrollbar.draw();
        if (this.openPlayerMenuEntry == null) {
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
            draw.drawString(String.valueOf(LanguageManager.translate("chat_playermenu_displayname")) + ":", x - 130 + 2 - 2, editorYIndex);
            this.fieldDisplayName.width = 126;
            this.fieldDisplayName.xPosition = x - 130 + 2 - 2;
            this.fieldDisplayName.yPosition = (editorYIndex += 10) + 1;
            fieldplayerMenuColor = this.canHighlightRed != false && this.fieldDisplayName.getText().isEmpty() != false ? ModColor.toRGB(200, 100, 100, 200) : 0x7FFFFFFF;
            DrawUtils.drawRect(x - 130 + 2 - 2, editorYIndex, x - 2 - 2, editorYIndex + 10, fieldplayerMenuColor);
            this.fieldDisplayName.drawTextBox();
            draw.drawString(String.valueOf(LanguageManager.translate("chat_playermenu_command")) + ":", x - 130 + 2 - 2, editorYIndex += 15);
            this.fieldCommand.width = 126;
            this.fieldCommand.xPosition = x - 130 + 2 - 2;
            this.fieldCommand.yPosition = (editorYIndex += 10) + 1;
            fieldReplacementColor = this.canHighlightRed != false && this.fieldCommand.getText().isEmpty() != false ? ModColor.toRGB(200, 100, 100, 200) : 0x7FFFFFFF;
            DrawUtils.drawRect(x - 130 + 2 - 2, editorYIndex, x - 2 - 2, editorYIndex + 10, fieldReplacementColor);
            this.fieldCommand.drawTextBox();
            editorYIndex += 13;
            if (mouseX > this.fieldCommand.xPosition && mouseX < this.fieldCommand.xPosition + this.fieldCommand.width && mouseY > this.fieldCommand.yPosition && mouseY < this.fieldCommand.yPosition + this.fieldCommand.height) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, new String[]{LanguageManager.translate("chat_playermenu_tooltip")});
            }
            this.hoverSendInstantlyCheckbox = this.drawElementCheckBox("sendinstantly", this.openPlayerMenuEntry.isSendInstantly(), x - 130 + 2 - 2, editorYIndex + 1, mouseX, mouseY);
            editorYIndex += 15;
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
            this.openPlayerMenuEntry = new PlayerMenu.PlayerMenuEntry("", "", true);
            this.canHighlightRed = false;
            this.fieldDisplayName.setText("");
            this.fieldCommand.setText("");
        }
        if (this.hoverPlayerMenuEntry != null && mouseButton == 0) {
            if (this.hoverDeleteButton) {
                List<PlayerMenu.PlayerMenuEntry> entries = LabyMod.getInstance().getChatToolManager().getPlayerMenu();
                entries.remove(this.hoverPlayerMenuEntry);
                if (this.hoverPlayerMenuEntry == this.openPlayerMenuEntry) {
                    this.openPlayerMenuEntry = null;
                }
                LabyMod.getInstance().getChatToolManager().saveTools();
            } else {
                this.openPlayerMenuEntry = this.hoverPlayerMenuEntry;
                this.canHighlightRed = false;
                this.fieldDisplayName.setText(this.openPlayerMenuEntry.getDisplayName());
                this.fieldCommand.setText(this.openPlayerMenuEntry.getCommand());
            }
        }
        if (this.hoverCancelButton && mouseButton == 0) {
            this.openPlayerMenuEntry = null;
        }
        if (this.hoverSaveButton && mouseButton == 0) {
            String playerMenu = this.fieldDisplayName.getText();
            String replacement = this.fieldCommand.getText();
            if (playerMenu.isEmpty() || replacement.isEmpty()) {
                this.canHighlightRed = true;
                return;
            }
            this.canHighlightRed = false;
            this.openPlayerMenuEntry.setDisplayName(this.fieldDisplayName.getText());
            this.openPlayerMenuEntry.setCommand(this.fieldCommand.getText());
            List<PlayerMenu.PlayerMenuEntry> entries2 = LabyMod.getInstance().getChatToolManager().getPlayerMenu();
            if (!entries2.contains(this.openPlayerMenuEntry)) {
                entries2.add(this.openPlayerMenuEntry);
            }
            this.openPlayerMenuEntry = null;
            LabyMod.getInstance().getChatToolManager().saveTools();
        }
        if (this.openPlayerMenuEntry != null) {
            this.fieldDisplayName.mouseClicked(mouseX, mouseY, mouseButton);
            this.fieldCommand.mouseClicked(mouseX, mouseY, mouseButton);
            if (this.hoverSendInstantlyCheckbox) {
                this.openPlayerMenuEntry.setSendInstantly(!this.openPlayerMenuEntry.isSendInstantly());
            }
        }
    }

    private boolean drawElementCheckBox(String text, boolean check, int x2, int y2, int mouseX, int mouseY) {
        String displayText = String.valueOf(LanguageManager.translate("chat_playermenu_" + text)) + ":";
        LabyMod.getInstance().getDrawUtils().drawString(displayText, x2 + 2, y2 + 2, 0.7);
        boolean hover = mouseX > x2 + 3 && mouseX < x2 + 12 && mouseY > (y2 += 10) + 1 && mouseY < y2 + 10;
        GuiChatPlayerMenu.drawRect(x2 + 3, y2 + 1, x2 + 12, y2 + 10, hover ? 2147483547 : Integer.MAX_VALUE);
        if (!check) {
            return hover;
        }
        GuiChatPlayerMenu.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(ModColor.cl("a")) + "\u2714", x2 + 8, y2 + 1, Integer.MAX_VALUE);
        return hover;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.openPlayerMenuEntry != null) {
            this.fieldDisplayName.textboxKeyTyped(typedChar, keyCode);
            this.fieldCommand.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 15) {
                if (this.fieldDisplayName.isFocused()) {
                    this.fieldDisplayName.setFocused(false);
                    this.fieldCommand.setFocused(true);
                } else {
                    this.fieldDisplayName.setFocused(true);
                    this.fieldCommand.setFocused(false);
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
        if (this.openPlayerMenuEntry != null) {
            this.fieldDisplayName.updateCursorCounter();
            this.fieldCommand.updateCursorCounter();
        }
    }
}

