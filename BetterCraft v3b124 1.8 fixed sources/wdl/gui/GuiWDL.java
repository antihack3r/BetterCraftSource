/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import wdl.WDL;
import wdl.WDLPluginChannels;
import wdl.gui.GuiWDLAbout;
import wdl.gui.GuiWDLBackup;
import wdl.gui.GuiWDLEntities;
import wdl.gui.GuiWDLGenerator;
import wdl.gui.GuiWDLMessages;
import wdl.gui.GuiWDLMultiworld;
import wdl.gui.GuiWDLMultiworldSelect;
import wdl.gui.GuiWDLPermissions;
import wdl.gui.GuiWDLPlayer;
import wdl.gui.GuiWDLUpdates;
import wdl.gui.GuiWDLWorld;
import wdl.gui.Utils;
import wdl.update.WDLUpdateChecker;

public class GuiWDL
extends GuiScreen {
    private String displayedTooltip = null;
    private String title = "";
    private GuiScreen parent;
    private GuiTextField worldname;
    private GuiWDLButtonList list;

    public GuiWDL(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        if (WDL.isMultiworld && WDL.worldName.isEmpty()) {
            this.mc.displayGuiScreen(new GuiWDLMultiworldSelect(I18n.format("wdl.gui.multiworldSelect.title.changeOptions", new Object[0]), new GuiWDLMultiworldSelect.WorldSelectionCallback(){

                @Override
                public void onWorldSelected(String selectedWorld) {
                    WDL.worldName = selectedWorld;
                    WDL.isMultiworld = true;
                    WDL.propsFound = true;
                    WDL.worldProps = WDL.loadWorldProps(selectedWorld);
                    GuiWDL.this.mc.displayGuiScreen(GuiWDL.this);
                }

                @Override
                public void onCancel() {
                    GuiWDL.this.mc.displayGuiScreen(null);
                }
            }));
            return;
        }
        if (!WDL.propsFound) {
            this.mc.displayGuiScreen(new GuiWDLMultiworld(new GuiWDLMultiworld.MultiworldCallback(){

                @Override
                public void onSelect(boolean enableMutliworld) {
                    WDL.isMultiworld = enableMutliworld;
                    if (WDL.isMultiworld) {
                        GuiWDL.this.mc.displayGuiScreen(new GuiWDLMultiworldSelect(I18n.format("wdl.gui.multiworldSelect.title.changeOptions", new Object[0]), new GuiWDLMultiworldSelect.WorldSelectionCallback(){

                            @Override
                            public void onWorldSelected(String selectedWorld) {
                                WDL.worldName = selectedWorld;
                                WDL.isMultiworld = true;
                                WDL.propsFound = true;
                                WDL.worldProps = WDL.loadWorldProps(selectedWorld);
                                GuiWDL.this.mc.displayGuiScreen(GuiWDL.this);
                            }

                            @Override
                            public void onCancel() {
                                GuiWDL.this.mc.displayGuiScreen(null);
                            }
                        }));
                    } else {
                        WDL.baseProps.setProperty("LinkedWorlds", "");
                        WDL.saveProps();
                        WDL.propsFound = true;
                        GuiWDL.this.mc.displayGuiScreen(GuiWDL.this);
                    }
                }

                @Override
                public void onCancel() {
                    GuiWDL.this.mc.displayGuiScreen(null);
                }
            }));
            return;
        }
        this.buttonList.clear();
        this.title = I18n.format("wdl.gui.wdl.title", WDL.baseFolderName.replace('@', ':'));
        if (WDL.baseProps.getProperty("ServerName").isEmpty()) {
            WDL.baseProps.setProperty("ServerName", WDL.getServerName());
        }
        this.worldname = new GuiTextField(42, this.fontRendererObj, width / 2 - 155, 19, 150, 18);
        this.worldname.setText(WDL.baseProps.getProperty("ServerName"));
        this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
        this.list = new GuiWDLButtonList();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) {
            return;
        }
        if (button.id == 100) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void onGuiClosed() {
        if (this.worldname != null) {
            WDL.baseProps.setProperty("ServerName", this.worldname.getText());
            WDL.saveProps();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.list.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.worldname.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.list.mouseReleased(mouseX, mouseY, state)) {
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.worldname.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        this.worldname.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.displayedTooltip = null;
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        GuiWDL.drawCenteredString(this.fontRendererObj, this.title, width / 2, 8, 0xFFFFFF);
        String name = I18n.format("wdl.gui.wdl.worldname", new Object[0]);
        this.drawString(this.fontRendererObj, name, this.worldname.xPosition - this.fontRendererObj.getStringWidth(String.valueOf(name) + " "), 26, 0xFFFFFF);
        this.worldname.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        Utils.drawGuiInfoBox(this.displayedTooltip, width, height, 48);
    }

    private class GuiWDLButtonList
    extends GuiListExtended {
        private List<GuiListExtended.IGuiListEntry> entries;

        public GuiWDLButtonList() {
            super(GuiWDL.this.mc, width, height, 39, height - 32, 20);
            this.entries = new ArrayList<GuiListExtended.IGuiListEntry>(){
                {
                    this.add(new ButtonEntry("worldOverrides", new GuiWDLWorld(GuiWDL.this), true));
                    this.add(new ButtonEntry("generatorOverrides", new GuiWDLGenerator(GuiWDL.this), true));
                    this.add(new ButtonEntry("playerOverrides", new GuiWDLPlayer(GuiWDL.this), true));
                    this.add(new ButtonEntry("entityOptions", new GuiWDLEntities(GuiWDL.this), true));
                    this.add(new ButtonEntry("backupOptions", new GuiWDLBackup(GuiWDL.this), true));
                    this.add(new ButtonEntry("messageOptions", new GuiWDLMessages(GuiWDL.this), false));
                    this.add(new ButtonEntry("permissionsInfo", new GuiWDLPermissions(GuiWDL.this), false));
                    this.add(new ButtonEntry("about", new GuiWDLAbout(GuiWDL.this), false));
                    if (WDLUpdateChecker.hasNewVersion()) {
                        this.add(0, new ButtonEntry("updates.hasNew", new GuiWDLUpdates(GuiWDL.this), false));
                    } else {
                        this.add(new ButtonEntry("updates", new GuiWDLUpdates(GuiWDL.this), false));
                    }
                }
            };
        }

        @Override
        public GuiListExtended.IGuiListEntry getListEntry(int index) {
            return this.entries.get(index);
        }

        @Override
        protected int getSize() {
            return this.entries.size();
        }

        private class ButtonEntry
        implements GuiListExtended.IGuiListEntry {
            private final GuiButton button;
            private final GuiScreen toOpen;
            private final String tooltip;

            public ButtonEntry(String key, GuiScreen toOpen, boolean needsPerms) {
                this.button = new GuiButton(0, 0, 0, I18n.format("wdl.gui.wdl." + key + ".name", new Object[0]));
                this.toOpen = toOpen;
                if (needsPerms) {
                    this.button.enabled = WDLPluginChannels.canDownloadAtAll();
                }
                this.tooltip = I18n.format("wdl.gui.wdl." + key + ".description", new Object[0]);
            }

            @Override
            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }

            @Override
            public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                this.button.xPosition = width / 2 - 100;
                this.button.yPosition = y2;
                this.button.drawButton(GuiWDLButtonList.this.mc, mouseX, mouseY);
                if (this.button.isMouseOver()) {
                    GuiWDL.this.displayedTooltip = this.tooltip;
                }
            }

            @Override
            public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                if (this.button.mousePressed(GuiWDLButtonList.this.mc, x2, y2)) {
                    GuiWDLButtonList.this.mc.displayGuiScreen(this.toOpen);
                    this.button.playPressSound(GuiWDLButtonList.this.mc.getSoundHandler());
                    return true;
                }
                return false;
            }

            @Override
            public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
            }
        }
    }
}

