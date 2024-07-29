/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tabs;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.ModColor;
import org.lwjgl.input.Mouse;

public class GuiChatSymbols
extends GuiChatCustom {
    private static final char[] SYMBOLS = GuiChatSymbols.loadSymbols();
    private Scrollbar scrollbar = new Scrollbar(15);
    private HashMap<String, Long> pressedAnimation = new HashMap();
    private boolean canScroll;

    public GuiChatSymbols(String defaultText) {
        super(defaultText);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.scrollbar.setPosition(width - 8, height - 145, width - 3, height - 20);
        this.scrollbar.update(SYMBOLS.length / 9);
        this.scrollbar.setSpeed(10);
        this.scrollbar.setEntryHeight(10.0);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (this.canScroll) {
            this.scrollbar.mouseInput();
            int i2 = Mouse.getEventDWheel();
            if (i2 != 0) {
                this.mc.ingameGUI.getChatGUI().resetScroll();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.scrollbar.calc();
        GuiChatSymbols.drawRect(width - 100, height - 150, width - 2, height - 16, Integer.MIN_VALUE);
        GuiChatSymbols.drawRect(width - 6, height - 145, width - 5, height - 20, Integer.MIN_VALUE);
        GuiChatSymbols.drawRect(width - 7, (int)this.scrollbar.getTop(), width - 4, (int)(this.scrollbar.getTop() + this.scrollbar.getBarLength()), Integer.MAX_VALUE);
        this.canScroll = mouseX > width - 100 && mouseX < width - 2 && mouseY > height - 150 && mouseY < height - 16;
        int row = 0;
        int column = 0;
        Object[] objectArray = SYMBOLS;
        int n2 = SYMBOLS.length;
        int n3 = 0;
        while (n3 < n2) {
            char symbol = objectArray[n3];
            if ((double)(column * 10) + this.scrollbar.getScrollY() > -5.0 && (double)(column * 10) + this.scrollbar.getScrollY() < 125.0) {
                boolean hoverSymbol = mouseX > width - 93 + row * 10 - 5 && mouseX < width - 93 + row * 10 + 6 && (double)mouseY > (double)(height - 147 + column * 10) + this.scrollbar.getScrollY() - 5.0 && (double)mouseY < (double)(height - 147 + column * 10) + this.scrollbar.getScrollY() + 6.0;
                GuiChatSymbols.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(symbol), width - 93 + row * 10, (int)((double)(height - 147 + column * 10) + this.scrollbar.getScrollY()), hoverSymbol ? -24000 : -1);
            }
            if (++row > 8) {
                row = 0;
                ++column;
            }
            ++n3;
        }
        row = 0;
        column = 0;
        objectArray = ModColor.COLOR_CODES;
        n2 = ModColor.COLOR_CODES.length;
        n3 = 0;
        while (n3 < n2) {
            boolean hoverCode;
            char code = objectArray[n3];
            Long longPressed = this.pressedAnimation.get(code);
            int pressedCode = (int)(longPressed == null ? 0L : longPressed - System.currentTimeMillis());
            boolean bl2 = hoverCode = mouseX > width - 111 - column * 11 && mouseX < width - 111 + 10 - column * 11 && mouseY > height - 150 + row * 11 && mouseY < height - 150 + 10 + row * 11;
            GuiChatSymbols.drawRect(width - 111 - column * 11, height - 150 + row * 11, width - 111 + 10 - column * 11, height - 150 + 10 + row * 11, hoverCode || pressedCode != 0 ? ModColor.toRGB(132, 132, 132, pressedCode == 0 ? 130 : pressedCode / 4) : Integer.MIN_VALUE);
            GuiChatSymbols.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(ModColor.cl((String)code)) + (String)code, width - 111 - column * 11 + 5, height - 150 + row * 11 + 1, Integer.MAX_VALUE);
            if (pressedCode < 0) {
                this.pressedAnimation.remove(code);
            }
            if (++row > 11 || code.equals("9") || code.equals("f")) {
                row = 0;
                ++column;
            }
            ++n3;
        }
        boolean contains = false;
        String[] stringArray = ModColor.COLOR_CODES;
        int n4 = ModColor.COLOR_CODES.length;
        n2 = 0;
        while (n2 < n4) {
            String code2 = stringArray[n2];
            if (this.inputField.getText().contains("&" + code2)) {
                contains = true;
                break;
            }
            ++n2;
        }
        if (contains) {
            GuiChatSymbols.drawRect(2, height - 16 - 11, width - 101, height - 15, Integer.MIN_VALUE);
            String string = this.inputField.getText().replace("&", ModColor.getCharAsString());
            while (string.contains("  ")) {
                string = string.replace("  ", " ");
            }
            this.drawString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(ModColor.cl("r")) + string, 4, height - 16 - 9, Integer.MAX_VALUE);
        }
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), LanguageManager.translate("ingame_chat_tab_symbols"), width - 100, height - 160, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        int row = 0;
        int column = 0;
        Object[] objectArray = SYMBOLS;
        int n2 = SYMBOLS.length;
        int n3 = 0;
        while (n3 < n2) {
            char symbol = objectArray[n3];
            if ((double)(column * 10) + this.scrollbar.getScrollY() > -5.0 && (double)(column * 10) + this.scrollbar.getScrollY() < 125.0) {
                boolean hoverSymbol;
                boolean bl2 = hoverSymbol = mouseX > width - 93 + row * 10 - 5 && mouseX < width - 93 + row * 10 + 6 && (double)mouseY > (double)(height - 147 + column * 10) + this.scrollbar.getScrollY() - 5.0 && (double)mouseY < (double)(height - 147 + column * 10) + this.scrollbar.getScrollY() + 6.0;
                if (hoverSymbol) {
                    this.inputField.textboxKeyTyped(symbol, 0);
                    LabyModCore.getMinecraft().playSound(SettingsElement.BUTTON_PRESS_SOUND, 2.0f);
                    break;
                }
            }
            if (++row > 8) {
                row = 0;
                ++column;
            }
            ++n3;
        }
        row = 0;
        column = 0;
        objectArray = ModColor.COLOR_CODES;
        n2 = ModColor.COLOR_CODES.length;
        n3 = 0;
        while (n3 < n2) {
            char code = objectArray[n3];
            if (mouseX > width - 111 - column * 11 && mouseX < width - 111 + 10 - column * 11 && mouseY > height - 150 + row * 11 && mouseY < height - 150 + 10 + row * 11) {
                this.inputField.textboxKeyTyped("&".charAt(0), 0);
                this.inputField.textboxKeyTyped(code.charAt(0), 0);
                LabyModCore.getMinecraft().playSound(SettingsElement.BUTTON_PRESS_SOUND, 2.0f);
            }
            if (++row > 11 || code.equals("9") || code.equals("f")) {
                row = 0;
                ++column;
            }
            ++n3;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        String currentText = this.inputField.getText();
        int curser = this.inputField.getCursorPosition();
        if (curser > 1 && currentText.length() > 0) {
            String curserText = String.valueOf(typedChar);
            boolean isColorCode = false;
            String[] stringArray = ModColor.COLOR_CODES;
            int n2 = ModColor.COLOR_CODES.length;
            int n3 = 0;
            while (n3 < n2) {
                String code = stringArray[n3];
                if (code.equals(curserText)) {
                    isColorCode = true;
                    break;
                }
                ++n3;
            }
            if (curser > 1 && isColorCode && String.valueOf(currentText.charAt(curser - 2)).equals("&")) {
                this.pressedAnimation.put(curserText, System.currentTimeMillis() + 1000L);
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
        super.mouseReleased(mouseX, mouseY, state);
    }

    private static char[] loadSymbols() {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/minecraft/labymod/data/symbols.txt");
        DataInputStream dis = new DataInputStream(inputStream);
        String string = "";
        Scanner scanner = new Scanner((InputStream)dis, "UTF-8");
        while (scanner.hasNext()) {
            string = String.valueOf(string) + scanner.next();
        }
        scanner.close();
        return string.toCharArray();
    }
}

