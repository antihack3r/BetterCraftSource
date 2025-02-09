/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import java.util.Map;
import net.labymod.gui.GuiTagsAdd;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.gui.elements.Tabs;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TagManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;

public class GuiTags
extends GuiScreen {
    private Scrollbar scrollbar = new Scrollbar(29);
    private String selectedTag = null;
    private String hoveredTag = null;
    private boolean addTagScreen = false;
    private GuiButton buttonEdit;
    private GuiButton buttonRemove;

    @Override
    public void initGui() {
        super.initGui();
        this.scrollbar.init();
        this.scrollbar.setPosition(width / 2 + 102, 44, width / 2 + 106, height - 32 - 3);
        this.scrollbar.setSpeed(10);
        this.buttonRemove = new GuiButton(1, width / 2 - 120, height - 26, 75, 20, LanguageManager.translate("button_remove"));
        this.buttonList.add(this.buttonRemove);
        this.buttonEdit = new GuiButton(2, width / 2 - 37, height - 26, 75, 20, LanguageManager.translate("button_edit"));
        this.buttonList.add(this.buttonEdit);
        this.buttonList.add(new GuiButton(3, width / 2 + 120 - 75, height - 26, 75, 20, LanguageManager.translate("button_add")));
        Tabs.initGuiScreen(this.buttonList, this);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        TagManager.save();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 1: {
                final GuiScreen lastScreen = Minecraft.getMinecraft().currentScreen;
                Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                    @Override
                    public void confirmClicked(boolean result, int id2) {
                        if (result) {
                            TagManager.getConfigManager().getSettings().getTags().remove(GuiTags.this.selectedTag);
                        }
                        Minecraft.getMinecraft().displayGuiScreen(lastScreen);
                    }
                }, LanguageManager.translate("warning_delete"), String.valueOf(ModColor.cl("c")) + this.selectedTag, 1));
                break;
            }
            case 2: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiTagsAdd(this, this.selectedTag));
                break;
            }
            case 3: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiTagsAdd(this, null));
            }
        }
        Tabs.actionPerformedButton(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        LabyMod.getInstance().getDrawUtils().drawAutoDimmedBackground(this.scrollbar.getScrollY());
        if (this.addTagScreen) {
            return;
        }
        this.hoveredTag = null;
        double yPos = 45.0 + this.scrollbar.getScrollY() + 3.0;
        Map<String, String> tags = TagManager.getConfigManager().getSettings().getTags();
        for (String tag : tags.keySet()) {
            this.drawEntry(tag, tags.get(tag), yPos, mouseX, mouseY);
            yPos += 29.0;
        }
        LabyMod.getInstance().getDrawUtils().drawOverlayBackground(0, 41);
        LabyMod.getInstance().getDrawUtils().drawOverlayBackground(height - 32, height);
        LabyMod.getInstance().getDrawUtils().drawGradientShadowTop(41.0, 0.0, width);
        LabyMod.getInstance().getDrawUtils().drawGradientShadowBottom(height - 32, 0.0, width);
        LabyMod.getInstance().getDrawUtils().drawCenteredString(LanguageManager.translate("title_tags"), width / 2, 29.0);
        this.scrollbar.update(tags.size());
        this.scrollbar.draw();
        this.buttonEdit.enabled = this.selectedTag != null;
        this.buttonRemove.enabled = this.selectedTag != null;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawEntry(String key, String value, double y2, int mouseX, int mouseY) {
        boolean hovered;
        int x2 = width / 2 - 100;
        boolean bl2 = hovered = mouseX > x2 && mouseX < x2 + 200 && (double)mouseY > y2 && (double)mouseY < y2 + 24.0 && mouseX > 32 && mouseY < height - 32;
        if (hovered) {
            this.hoveredTag = key;
        }
        int borderColor = this.selectedTag == key ? ModColor.toRGB(240, 240, 240, 240) : Integer.MIN_VALUE;
        int backgroundColor = hovered ? ModColor.toRGB(50, 50, 50, 120) : ModColor.toRGB(30, 30, 30, 120);
        GuiTags.drawRect(x2 - 5, (int)y2 - 4, x2 + 200, (int)y2 + 24, backgroundColor);
        LabyMod.getInstance().getDrawUtils().drawRectBorder(x2 - 5, (int)y2 - 4, x2 + 200, (int)y2 + 24, borderColor, 1.0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int headSize = 20;
        LabyMod.getInstance().getDrawUtils().drawPlayerHead(key, x2, (int)y2, 20);
        LabyMod.getInstance().getDrawUtils().drawString(key, x2 + 20 + 5, y2 + 1.0);
        String tagValue = value.replaceAll("&", "\u00c2\u00a7");
        tagValue = LabyMod.getInstance().getDrawUtils().trimStringToWidth(tagValue, 170);
        LabyMod.getInstance().getDrawUtils().drawString(tagValue, x2 + 20 + 5, y2 + 10.0 + 1.0);
        Tabs.drawParty(mouseX, mouseY, width);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.hoveredTag != null) {
            this.selectedTag = this.hoveredTag;
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }
}

