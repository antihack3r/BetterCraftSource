/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wdl.api.IWDLModWithGui;
import wdl.api.WDLApi;
import wdl.gui.TextList;

public class GuiWDLExtensions
extends GuiScreen {
    private int bottomLocation;
    private static final int TOP_HEIGHT = 23;
    private static final int MIDDLE_HEIGHT = 19;
    private static final int BOTTOM_HEIGHT = 32;
    private int selectedModIndex = -1;
    private final GuiScreen parent;
    private ModList list;
    private ModDetailList detailsList;
    private boolean dragging = false;
    private int dragOffset;

    private void updateDetailsList(WDLApi.ModInfo<?> selectedMod) {
        this.detailsList.clearLines();
        if (selectedMod != null) {
            String info = selectedMod.getInfo();
            this.detailsList.addLine(info);
        }
    }

    public GuiWDLExtensions(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.bottomLocation = height - 100;
        this.dragging = false;
        this.list = new ModList();
        this.detailsList = new ModDetailList();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
        this.detailsList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseY > this.bottomLocation && mouseY < this.bottomLocation + 19) {
            this.dragging = true;
            this.dragOffset = mouseY - this.bottomLocation;
            return;
        }
        if (this.list.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }
        if (this.detailsList.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
        if (this.list.mouseReleased(mouseX, mouseY, state)) {
            return;
        }
        if (this.detailsList.mouseReleased(mouseX, mouseY, state)) {
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.dragging) {
            this.bottomLocation = mouseY - this.dragOffset;
        }
        if (this.bottomLocation < 31) {
            this.bottomLocation = 31;
        }
        if (this.bottomLocation > height - 32 - 8) {
            this.bottomLocation = height - 32 - 8;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (this.bottomLocation < 56) {
            this.bottomLocation = 56;
        }
        if (this.bottomLocation > height - 19 - 32 - 33) {
            this.bottomLocation = height - 19 - 32 - 33;
        }
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.detailsList.drawScreen(mouseX, mouseY, partialTicks);
        GuiWDLExtensions.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.extensions.title", new Object[0]), width / 2, 8, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private class ModDetailList
    extends TextList {
        public ModDetailList() {
            super(GuiWDLExtensions.this.mc, width, height - GuiWDLExtensions.this.bottomLocation, 19, 32);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            GlStateManager.translate(0.0f, GuiWDLExtensions.this.bottomLocation, 0.0f);
            this.height = height - GuiWDLExtensions.this.bottomLocation;
            this.bottom = this.height - 32;
            super.drawScreen(mouseX, mouseY, partialTicks);
            GuiWDLExtensions.drawCenteredString(GuiWDLExtensions.this.fontRendererObj, I18n.format("wdl.gui.extensions.detailsCaption", new Object[0]), width / 2, 5, 0xFFFFFF);
            GlStateManager.translate(0.0f, -GuiWDLExtensions.this.bottomLocation, 0.0f);
        }

        @Override
        protected void overlayBackground(int y1, int y2, int alpha1, int alpha2) {
            if (y1 == 0) {
                super.overlayBackground(y1, y2, alpha1, alpha2);
                return;
            }
            GlStateManager.translate(0.0f, -GuiWDLExtensions.this.bottomLocation, 0.0f);
            super.overlayBackground(y1 + GuiWDLExtensions.this.bottomLocation, y2 + GuiWDLExtensions.this.bottomLocation, alpha1, alpha2);
            GlStateManager.translate(0.0f, GuiWDLExtensions.this.bottomLocation, 0.0f);
        }

        @Override
        public void handleMouseInput() {
            this.mouseY -= GuiWDLExtensions.this.bottomLocation;
            if (this.mouseY > 0) {
                super.handleMouseInput();
            }
            this.mouseY += GuiWDLExtensions.this.bottomLocation;
        }
    }

    private class ModList
    extends GuiListExtended {
        private List<GuiListExtended.IGuiListEntry> entries;

        public ModList() {
            super(GuiWDLExtensions.this.mc, width, GuiWDLExtensions.this.bottomLocation, 23, GuiWDLExtensions.this.bottomLocation, 22);
            this.entries = new ArrayList<GuiListExtended.IGuiListEntry>(){
                {
                    for (WDLApi.ModInfo<?> mod : WDLApi.getWDLMods().values()) {
                        this.add(new ModEntry(mod));
                    }
                }
            };
            this.showSelectionBox = true;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.height = this.bottom = GuiWDLExtensions.this.bottomLocation;
            super.drawScreen(mouseX, mouseY, partialTicks);
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
        protected boolean isSelected(int slotIndex) {
            return slotIndex == GuiWDLExtensions.this.selectedModIndex;
        }

        @Override
        public int getListWidth() {
            return width - 20;
        }

        @Override
        protected int getScrollBarX() {
            return width - 10;
        }

        @Override
        public void handleMouseInput() {
            if (this.mouseY < GuiWDLExtensions.this.bottomLocation) {
                super.handleMouseInput();
            }
        }

        private class ModEntry
        implements GuiListExtended.IGuiListEntry {
            public final WDLApi.ModInfo<?> mod;
            private final String modDescription;
            private String label;
            private GuiButton button;
            private GuiButton disableButton;

            public ModEntry(WDLApi.ModInfo<?> mod) {
                this.mod = mod;
                String name = mod.getDisplayName();
                this.modDescription = I18n.format("wdl.gui.extensions.modVersion", name, mod.version);
                this.label = !mod.isEnabled() ? (Object)((Object)EnumChatFormatting.GRAY) + (Object)((Object)EnumChatFormatting.ITALIC) + this.modDescription : this.modDescription;
                if (mod.mod instanceof IWDLModWithGui) {
                    IWDLModWithGui guiMod = (IWDLModWithGui)mod.mod;
                    String buttonName = guiMod.getButtonName();
                    if (buttonName == null || buttonName.isEmpty()) {
                        buttonName = I18n.format("wdl.gui.extensions.defaultSettingsButtonText", new Object[0]);
                    }
                    this.button = new GuiButton(0, 0, 0, 80, 20, guiMod.getButtonName());
                }
                this.disableButton = new GuiButton(0, 0, 0, 80, 20, I18n.format("wdl.gui.extensions." + (mod.isEnabled() ? "enabled" : "disabled"), new Object[0]));
            }

            @Override
            public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                if (this.button != null) {
                    this.button.xPosition = width - 180;
                    this.button.yPosition = y2 - 1;
                    this.button.drawButton(ModList.this.mc, mouseX, mouseY);
                }
                this.disableButton.xPosition = width - 92;
                this.disableButton.yPosition = y2 - 1;
                this.disableButton.drawButton(ModList.this.mc, mouseX, mouseY);
                int centerY = y2 + slotHeight / 2 - ((GuiWDLExtensions)((ModList)ModList.this).GuiWDLExtensions.this).fontRendererObj.FONT_HEIGHT / 2;
                GuiWDLExtensions.this.fontRendererObj.drawString(this.label, x2, centerY, 0xFFFFFF);
            }

            @Override
            public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                if (this.button != null && this.button.mousePressed(ModList.this.mc, x2, y2)) {
                    if (this.mod.mod instanceof IWDLModWithGui) {
                        ((IWDLModWithGui)this.mod.mod).openGui(GuiWDLExtensions.this);
                    }
                    this.button.playPressSound(ModList.this.mc.getSoundHandler());
                    return true;
                }
                if (this.disableButton.mousePressed(ModList.this.mc, x2, y2)) {
                    this.mod.toggleEnabled();
                    this.disableButton.playPressSound(ModList.this.mc.getSoundHandler());
                    this.disableButton.displayString = I18n.format("wdl.gui.extensions." + (this.mod.isEnabled() ? "enabled" : "disabled"), new Object[0]);
                    this.label = !this.mod.isEnabled() ? (Object)((Object)EnumChatFormatting.GRAY) + (Object)((Object)EnumChatFormatting.ITALIC) + this.modDescription : this.modDescription;
                    return true;
                }
                if (GuiWDLExtensions.this.selectedModIndex != slotIndex) {
                    GuiWDLExtensions.this.selectedModIndex = slotIndex;
                    ModList.this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                    GuiWDLExtensions.this.updateDetailsList(this.mod);
                    return true;
                }
                return false;
            }

            @Override
            public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                if (this.button != null) {
                    this.button.mouseReleased(x2, y2);
                }
            }

            @Override
            public void setSelected(int slotIndex, int p_178011_2_, int p_178011_3_) {
            }
        }
    }
}

