/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tabs;

import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.ingamechat.namehistory.NameHistory;
import net.labymod.ingamechat.namehistory.NameHistoryUtil;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.labymod.utils.UUIDFetcher;
import net.minecraft.client.gui.FontRenderer;

public class GuiChatNameHistory
extends GuiChatCustom {
    private final int windowWidth = 160;
    private final int windowHeight = 16;
    private final int entryHeight = 11;
    private final int maxEntries = 10;
    private Scrollbar scrollbar;
    private ModTextField fieldSearch;
    private boolean hoverSearchButton;
    private NameHistory nameHistory;
    private boolean lastSearchNotFound;
    private String forceNameSearch;

    public GuiChatNameHistory(String defaultText) {
        super(defaultText);
        this.getClass();
        this.scrollbar = new Scrollbar(11);
        this.hoverSearchButton = false;
        this.lastSearchNotFound = false;
        this.forceNameSearch = null;
    }

    public GuiChatNameHistory(String defaultText, String username) {
        super(defaultText);
        this.getClass();
        this.scrollbar = new Scrollbar(11);
        this.hoverSearchButton = false;
        this.lastSearchNotFound = false;
        this.forceNameSearch = null;
        this.forceNameSearch = username;
    }

    @Override
    public void initGui() {
        super.initGui();
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        double buttonWidth = (double)draw.getStringWidth(LanguageManager.translate("button_search")) * 0.7 + 4.0;
        boolean componentId = false;
        FontRenderer fontRenderer = LabyModCore.getMinecraft().getFontRenderer();
        boolean x2 = false;
        boolean y2 = false;
        int par5Width = (int)((double)this.windowWidth - buttonWidth);
        this.getClass();
        this.fieldSearch = new ModTextField(0, fontRenderer, 0, 0, par5Width, 16);
        this.fieldSearch.setEnableBackgroundDrawing(false);
        this.fieldSearch.setMaxStringLength(16);
        this.fieldSearch.setFocused(true);
        Scrollbar scrollbar = this.scrollbar;
        this.getClass();
        scrollbar.setSpeed(11);
        this.scrollbar.init();
        if (this.forceNameSearch != null) {
            this.fieldSearch.setText(this.forceNameSearch);
            this.fieldSearch.setFocused(true);
            this.fieldSearch.setCursorPositionEnd();
            this.doNameSearch(this.forceNameSearch);
            this.forceNameSearch = null;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int n2 = width - 2;
        this.getClass();
        int x2 = n2 - 160;
        int n22 = height - 24;
        this.getClass();
        double y2 = n22 - 24;
        DrawUtils drawUtils = draw;
        double left = x2;
        double top = y2 -= (double)this.pushSearchBarUp();
        int n3 = x2;
        this.getClass();
        double right = n3 + 160;
        double n4 = y2;
        this.getClass();
        DrawUtils.drawRect(left, top, right, n4 + 16.0, Integer.MIN_VALUE);
        boolean paddingField = true;
        int fieldColor = this.fieldSearch.isFocused() ? Integer.MIN_VALUE : ModColor.toRGB(12, 12, 12, 180);
        DrawUtils drawUtils2 = draw;
        double left2 = x2 + 1;
        double top2 = y2 + 1.0;
        double right2 = x2 + this.fieldSearch.getWidth() - 1;
        double n5 = y2;
        this.getClass();
        DrawUtils.drawRect(left2, top2, right2, n5 + 16.0 - 1.0, fieldColor);
        this.fieldSearch.xPosition = x2 + 3;
        this.fieldSearch.yPosition = (int)(y2 + 4.0);
        this.fieldSearch.drawTextBox();
        int searchX = x2 + this.fieldSearch.getWidth();
        double searchY = y2;
        int searchWidth = width - 2 - searchX;
        boolean bl2 = this.hoverSearchButton = mouseX > searchX && searchX < searchX + searchWidth && (double)mouseY > searchY && (double)mouseY < searchY + 16.0;
        int buttonColor = this.fieldSearch.getText().isEmpty() ? ModColor.toRGB(100, 100, 100, 200) : (this.hoverSearchButton ? ModColor.toRGB(100, 200, 100, 200) : Integer.MAX_VALUE);
        boolean paddingButton = true;
        DrawUtils drawUtils3 = draw;
        double left3 = searchX + 1;
        double top3 = searchY + 1.0;
        double right3 = searchX + searchWidth - 1;
        double n6 = searchY;
        this.getClass();
        DrawUtils.drawRect(left3, top3, right3, n6 + 16.0 - 1.0, buttonColor);
        draw.drawCenteredString(LanguageManager.translate("button_search"), (double)searchX + (double)searchWidth / 2.0, searchY + 5.0, 0.7);
        double topY = y2;
        if (this.nameHistory == null && this.lastSearchNotFound) {
            double n7 = y2;
            this.getClass();
            y2 = n7 + 16.0;
            DrawUtils drawUtils4 = draw;
            double left4 = x2;
            double top4 = y2 + 1.0;
            int n8 = x2;
            this.getClass();
            DrawUtils.drawRect(left4, top4, (double)(n8 + 160), y2 + 14.0, Integer.MIN_VALUE);
            draw.drawString(String.valueOf(ModColor.cl('c')) + LanguageManager.translate("search_not_found"), x2 + 3, y2 + 4.0);
        } else if (this.nameHistory != null && this.nameHistory.getChanges() != null) {
            double n9 = y2;
            this.getClass();
            y2 = n9 + 16.0;
            y2 += this.scrollbar.getScrollY();
            int index = 0;
            UUIDFetcher[] uUIDFetcherArray = this.nameHistory.getChanges();
            int n7 = uUIDFetcherArray.length;
            int n8 = 0;
            while (n8 < n7) {
                UUIDFetcher fetchedName = uUIDFetcherArray[n8];
                double n10 = y2;
                double n11 = topY;
                this.getClass();
                if (n10 >= n11 + 16.0 && y2 < (double)(height - 24) && this.nameHistory != null && this.nameHistory.getChanges() != null && fetchedName != null) {
                    boolean latestName = index == 0;
                    boolean originalName = index == this.nameHistory.getChanges().length - 1;
                    DrawUtils drawUtils5 = draw;
                    double left5 = x2;
                    double top5 = y2 + 1.0;
                    int n12 = x2;
                    this.getClass();
                    double right4 = n12 + 160;
                    double n13 = y2;
                    this.getClass();
                    DrawUtils.drawRect(left5, top5, right4, n13 + 11.0, Integer.MIN_VALUE);
                    String nameColor = ModColor.cl((char)(latestName ? 97 : (originalName ? 98 : 55)));
                    draw.drawString(String.valueOf(nameColor) + fetchedName.name, x2 + 2, y2 + (double)(latestName || originalName ? 2 : 3), latestName || originalName ? 1.0 : 0.7);
                    String timeDiff = fetchedName.changedToAt == 0L ? LanguageManager.translate("original_name") : ModUtils.getTimeDiff(fetchedName.changedToAt);
                    DrawUtils drawUtils6 = draw;
                    String string = String.valueOf(ModColor.cl('e')) + timeDiff;
                    int n14 = x2;
                    this.getClass();
                    drawUtils6.drawRightString(string, n14 + 160 - 4, y2 + 4.0, 0.6);
                    if (this.nameHistory != null && this.nameHistory.getChanges() != null && this.nameHistory.getChanges().length > 1) {
                        int n18;
                        int n16;
                        int color = originalName ? ModColor.toRGB(100, 200, 200, 250) : (latestName ? ModColor.toRGB(100, 200, 100, 250) : ModColor.toRGB(180, 180, 180, 100));
                        DrawUtils drawUtils7 = draw;
                        double left6 = x2 - 3;
                        double n15 = y2;
                        if (latestName) {
                            this.getClass();
                            n16 = 5;
                        } else {
                            n16 = 0;
                        }
                        double top6 = n15 + (double)n16;
                        double right5 = x2 - 3 + 1;
                        double n17 = y2;
                        if (originalName) {
                            this.getClass();
                            n18 = 6;
                        } else {
                            n18 = this.entryHeight;
                        }
                        DrawUtils.drawRect(left6, top6, right5, n17 + (double)n18, color);
                        DrawUtils drawUtils8 = draw;
                        double left7 = x2 - 3 + 1;
                        double n19 = y2;
                        this.getClass();
                        double top7 = n19 + 5.0;
                        double right6 = x2;
                        double n20 = y2;
                        this.getClass();
                        DrawUtils.drawRect(left7, top7, right6, n20 + 5.0 + 1.0, color);
                    }
                }
                double n21 = y2;
                this.getClass();
                y2 = n21 + 11.0;
                ++index;
                ++n8;
            }
        }
        Scrollbar scrollbar = this.scrollbar;
        int n222 = x2;
        this.getClass();
        double left8 = n222 + 160 - 3;
        double n23 = topY;
        this.getClass();
        double top8 = n23 + 16.0;
        int n24 = x2;
        this.getClass();
        scrollbar.setPosition(left8, top8, (double)(n24 + 160), (double)(height - 24));
        this.scrollbar.draw();
        draw.drawString(LanguageManager.translate("ingame_chat_tab_namehistory"), x2, topY - 10.0);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        n = GuiChatNameHistory.width - 2;
        this.getClass();
        x = n - 160;
        n2 = GuiChatNameHistory.height - 24;
        this.getClass();
        y = n2 - 24;
        this.fieldSearch.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX <= x || mouseX >= x + this.fieldSearch.getWidth() || mouseY <= (y -= this.pushSearchBarUp())) ** GOTO lbl-1000
        n3 = y;
        this.getClass();
        if (mouseY < n3 + 16) {
            this.fieldSearch.setFocused(true);
        } else lbl-1000:
        // 2 sources

        {
            this.fieldSearch.setFocused(false);
        }
        if (this.hoverSearchButton && !this.fieldSearch.getText().isEmpty()) {
            this.doNameSearch(this.fieldSearch.getText());
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.fieldSearch.isFocused()) {
            this.fieldSearch.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 28) {
                this.doNameSearch(this.fieldSearch.getText());
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
        Scrollbar scrollbar = this.scrollbar;
        double scrollY = this.scrollbar.getScrollY();
        this.getClass();
        int n2 = (int)(scrollY / 11.0);
        this.getClass();
        scrollbar.setScrollY(n2 * 11);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }

    @Override
    public void updateScreen() {
        if (this.fieldSearch.isFocused()) {
            this.fieldSearch.updateCursorCounter();
        } else {
            super.updateScreen();
        }
    }

    private int pushSearchBarUp() {
        if (this.nameHistory != null) {
            int n2;
            int length = this.nameHistory.getChanges().length;
            this.getClass();
            if (length > 10) {
                int n3 = this.maxEntries;
                this.getClass();
                n2 = 110;
            } else {
                int length2 = this.nameHistory.getChanges().length;
                this.getClass();
                n2 = length2 * 11;
            }
            return n2;
        }
        if (this.lastSearchNotFound) {
            return 14;
        }
        return 0;
    }

    private void doNameSearch(String username) {
        NameHistoryUtil.getNameHistory(username, new Consumer<NameHistory>(){

            @Override
            public void accept(NameHistory nameHistory) {
                GuiChatNameHistory.this.nameHistory = nameHistory;
                GuiChatNameHistory.this.lastSearchNotFound = nameHistory == null;
                if (nameHistory == null) {
                    GuiChatNameHistory.this.scrollbar.update(0);
                } else {
                    GuiChatNameHistory.this.scrollbar.update(nameHistory.getChanges().length);
                    GuiChatNameHistory.this.fieldSearch.setFocused(false);
                }
            }
        });
    }
}

