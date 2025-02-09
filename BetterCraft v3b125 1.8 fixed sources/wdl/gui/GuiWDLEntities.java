/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import com.google.common.collect.Multimap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import wdl.EntityUtils;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;
import wdl.gui.GuiSlider;
import wdl.gui.GuiWDLEntityRangePresets;

public class GuiWDLEntities
extends GuiScreen {
    private GuiEntityList entityList;
    private GuiScreen parent;
    private GuiButton rangeModeButton;
    private GuiButton presetsButton;
    private String mode;

    public GuiWDLEntities(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(200, width / 2 - 100, height - 29, "OK"));
        this.rangeModeButton = new GuiButton(100, width / 2 - 155, 18, 150, 20, this.getRangeModeText());
        this.presetsButton = new GuiButton(101, width / 2 + 5, 18, 150, 20, I18n.format("wdl.gui.entities.rangePresets", new Object[0]));
        this.mode = WDL.worldProps.getProperty("Entity.TrackDistanceMode");
        this.presetsButton.enabled = this.shouldEnablePresetsButton();
        this.buttonList.add(this.rangeModeButton);
        this.buttonList.add(this.presetsButton);
        this.entityList = new GuiEntityList();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.entityList.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 100) {
            this.cycleRangeMode();
        }
        if (button.id == 101 && button.enabled) {
            this.mc.displayGuiScreen(new GuiWDLEntityRangePresets(this));
        }
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void onGuiClosed() {
        WDL.saveProps();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.entityList.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.entityList.mouseReleased(mouseX, mouseY, state)) {
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.entityList.drawScreen(mouseX, mouseY, partialTicks);
        GuiWDLEntities.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.entities.title", new Object[0]), width / 2, 8, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void cycleRangeMode() {
        this.mode = this.mode.equals("default") ? (WDLPluginChannels.hasServerEntityRange() ? "server" : "user") : (this.mode.equals("server") ? "user" : "default");
        WDL.worldProps.setProperty("Entity.TrackDistanceMode", this.mode);
        this.rangeModeButton.displayString = this.getRangeModeText();
        this.presetsButton.enabled = this.shouldEnablePresetsButton();
    }

    private String getRangeModeText() {
        String mode = WDL.worldProps.getProperty("Entity.TrackDistanceMode");
        return I18n.format("wdl.gui.entities.trackDistanceMode." + mode, new Object[0]);
    }

    private boolean shouldEnablePresetsButton() {
        return this.mode.equals("user");
    }

    private class GuiEntityList
    extends GuiListExtended {
        private int largestWidth;
        private int totalWidth;
        private List<GuiListExtended.IGuiListEntry> entries;

        public GuiEntityList() {
            super(GuiWDLEntities.this.mc, width, height, 39, height - 32, 20);
            this.entries = new ArrayList<GuiListExtended.IGuiListEntry>(){
                {
                    try {
                        int largestWidthSoFar = 0;
                        Multimap<String, String> entities = EntityUtils.getEntitiesByGroup();
                        ArrayList<String> categories = new ArrayList<String>(entities.keySet());
                        categories.remove("Passive");
                        categories.remove("Hostile");
                        categories.remove("Other");
                        Collections.sort(categories);
                        categories.add(0, "Hostile");
                        categories.add(1, "Passive");
                        categories.add("Other");
                        for (String category : categories) {
                            CategoryEntry categoryEntry = new CategoryEntry(category);
                            this.add(categoryEntry);
                            ArrayList<String> categoryEntities = new ArrayList<String>(entities.get(category));
                            Collections.sort(categoryEntities);
                            for (String entity : categoryEntities) {
                                this.add(new EntityEntry(categoryEntry, entity));
                                int width = GuiWDLEntities.this.fontRendererObj.getStringWidth(entity);
                                if (width <= largestWidthSoFar) continue;
                                largestWidthSoFar = width;
                            }
                        }
                        GuiEntityList.this.largestWidth = largestWidthSoFar;
                        GuiEntityList.this.totalWidth = GuiEntityList.this.largestWidth + 255;
                    }
                    catch (Exception e2) {
                        WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSetUpEntityUI", e2);
                        Minecraft.getMinecraft().displayGuiScreen(null);
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

        @Override
        protected int getScrollBarX() {
            return width / 2 + this.totalWidth / 2 + 10;
        }

        private class CategoryEntry
        implements GuiListExtended.IGuiListEntry {
            private final String group;
            private final int labelWidth;
            private final GuiButton enableGroupButton;
            private boolean groupEnabled;

            public CategoryEntry(String group) {
                this.group = group;
                this.labelWidth = ((GuiEntityList)GuiEntityList.this).mc.fontRendererObj.getStringWidth(group);
                this.groupEnabled = WDL.worldProps.getProperty("EntityGroup." + group + ".Enabled", "true").equals("true");
                this.enableGroupButton = new GuiButton(0, 0, 0, 90, 18, this.getButtonText());
            }

            @Override
            public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                ((GuiEntityList)GuiEntityList.this).mc.fontRendererObj.drawString(this.group, x2 + 55 - this.labelWidth / 2, y2 + slotHeight - ((GuiEntityList)GuiEntityList.this).mc.fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFF);
                this.enableGroupButton.xPosition = x2 + 110;
                this.enableGroupButton.yPosition = y2;
                this.enableGroupButton.displayString = this.getButtonText();
                this.enableGroupButton.drawButton(GuiEntityList.this.mc, mouseX, mouseY);
            }

            @Override
            public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                if (this.enableGroupButton.mousePressed(GuiEntityList.this.mc, x2, y2)) {
                    this.groupEnabled ^= true;
                    this.enableGroupButton.playPressSound(GuiEntityList.this.mc.getSoundHandler());
                    this.enableGroupButton.displayString = this.getButtonText();
                    WDL.worldProps.setProperty("EntityGroup." + this.group + ".Enabled", Boolean.toString(this.groupEnabled));
                    return true;
                }
                return false;
            }

            @Override
            public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
            }

            @Override
            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }

            boolean isGroupEnabled() {
                return this.groupEnabled;
            }

            private String getButtonText() {
                if (this.groupEnabled) {
                    return I18n.format("wdl.gui.entities.group.enabled", new Object[0]);
                }
                return I18n.format("wdl.gui.entities.group.disabled", new Object[0]);
            }
        }

        private class EntityEntry
        implements GuiListExtended.IGuiListEntry {
            private final CategoryEntry category;
            private final String entity;
            private final GuiButton onOffButton;
            private final GuiSlider rangeSlider;
            private boolean entityEnabled;
            private int range;
            private String cachedMode;

            public EntityEntry(CategoryEntry category, String entity) {
                this.category = category;
                this.entity = entity;
                this.entityEnabled = WDL.worldProps.getProperty("Entity." + entity + ".Enabled", "true").equals("true");
                this.range = EntityUtils.getEntityTrackDistance(entity);
                this.onOffButton = new GuiButton(0, 0, 0, 75, 18, this.getButtonText());
                this.onOffButton.enabled = category.isGroupEnabled();
                this.rangeSlider = new GuiSlider(1, 0, 0, 150, 18, "wdl.gui.entities.trackDistance", this.range, 256);
                this.cachedMode = GuiWDLEntities.this.mode;
                this.rangeSlider.enabled = this.cachedMode.equals("user");
            }

            @Override
            public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                int center = width / 2 - GuiEntityList.this.totalWidth / 2 + GuiEntityList.this.largestWidth + 10;
                ((GuiEntityList)GuiEntityList.this).mc.fontRendererObj.drawString(this.entity, center - GuiEntityList.this.largestWidth - 10, y2 + slotHeight / 2 - ((GuiEntityList)GuiEntityList.this).mc.fontRendererObj.FONT_HEIGHT / 2, 0xFFFFFF);
                this.onOffButton.xPosition = center;
                this.onOffButton.yPosition = y2;
                this.onOffButton.enabled = this.category.isGroupEnabled();
                this.onOffButton.displayString = this.getButtonText();
                this.rangeSlider.xPosition = center + 85;
                this.rangeSlider.yPosition = y2;
                if (!this.cachedMode.equals(GuiWDLEntities.this.mode)) {
                    this.cachedMode = GuiWDLEntities.this.mode;
                    this.rangeSlider.enabled = this.cachedMode.equals("user");
                    this.rangeSlider.setValue(EntityUtils.getEntityTrackDistance(this.entity));
                }
                this.onOffButton.drawButton(GuiEntityList.this.mc, mouseX, mouseY);
                this.rangeSlider.drawButton(GuiEntityList.this.mc, mouseX, mouseY);
            }

            @Override
            public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                if (this.onOffButton.mousePressed(GuiEntityList.this.mc, x2, y2)) {
                    this.entityEnabled ^= true;
                    this.onOffButton.playPressSound(GuiEntityList.this.mc.getSoundHandler());
                    this.onOffButton.displayString = this.getButtonText();
                    WDL.worldProps.setProperty("Entity." + this.entity + ".Enabled", Boolean.toString(this.entityEnabled));
                    return true;
                }
                if (this.rangeSlider.mousePressed(GuiEntityList.this.mc, x2, y2)) {
                    this.range = this.rangeSlider.getValue();
                    WDL.worldProps.setProperty("Entity." + this.entity + ".TrackDistance", Integer.toString(this.range));
                    return true;
                }
                return false;
            }

            @Override
            public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                this.rangeSlider.mouseReleased(x2, y2);
                if (this.cachedMode.equals("user")) {
                    this.range = this.rangeSlider.getValue();
                    WDL.worldProps.setProperty("Entity." + this.entity + ".TrackDistance", Integer.toString(this.range));
                }
            }

            @Override
            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }

            private String getButtonText() {
                if (this.category.isGroupEnabled() && this.entityEnabled) {
                    return I18n.format("wdl.gui.entities.entity.included", new Object[0]);
                }
                return I18n.format("wdl.gui.entities.entity.ignored", new Object[0]);
            }
        }
    }
}

