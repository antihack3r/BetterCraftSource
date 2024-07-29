/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;
import wdl.MessageTypeCategory;
import wdl.WDL;
import wdl.WDLMessages;
import wdl.api.IWDLMessageType;
import wdl.gui.Utils;

public class GuiWDLMessages
extends GuiScreen {
    private String hoveredButtonDescription = null;
    private GuiScreen parent;
    private GuiMessageTypeList list;
    private GuiButton enableAllButton;
    private GuiButton resetButton;

    public GuiWDLMessages(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.enableAllButton = new GuiButton(100, width / 2 - 155, 18, 150, 20, this.getAllEnabledText());
        this.buttonList.add(this.enableAllButton);
        this.resetButton = new GuiButton(101, width / 2 + 5, 18, 150, 20, I18n.format("wdl.gui.messages.reset", new Object[0]));
        this.buttonList.add(this.resetButton);
        this.list = new GuiMessageTypeList();
        this.buttonList.add(new GuiButton(102, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        if (button.id == 100) {
            WDL.baseProps.setProperty("Messages.enableAll", Boolean.toString(WDLMessages.enableAllMessages ^= true));
            button.displayString = this.getAllEnabledText();
        } else if (button.id == 101) {
            this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("wdl.gui.messages.reset.confirm.title", new Object[0]), I18n.format("wdl.gui.messages.reset.confirm.subtitle", new Object[0]), 101));
        } else if (button.id == 102) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        if (result && id2 == 101) {
            WDLMessages.resetEnabledToDefaults();
        }
        this.mc.displayGuiScreen(this);
    }

    @Override
    public void onGuiClosed() {
        WDL.saveProps();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.list.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.list.mouseReleased(mouseX, mouseY, state)) {
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveredButtonDescription = null;
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        GuiWDLMessages.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.messages.message.title", new Object[0]), width / 2, 8, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hoveredButtonDescription != null) {
            Utils.drawGuiInfoBox(this.hoveredButtonDescription, width, height, 48);
        } else if (this.enableAllButton.isMouseOver()) {
            Utils.drawGuiInfoBox(I18n.format("wdl.gui.messages.all.description", new Object[0]), width, height, 48);
        } else if (this.resetButton.isMouseOver()) {
            Utils.drawGuiInfoBox(I18n.format("wdl.gui.messages.reset.description", new Object[0]), width, height, 48);
        }
    }

    private String getAllEnabledText() {
        return I18n.format("wdl.gui.messages.all." + WDLMessages.enableAllMessages, new Object[0]);
    }

    private class GuiMessageTypeList
    extends GuiListExtended {
        private List<GuiListExtended.IGuiListEntry> entries;

        public GuiMessageTypeList() {
            super(GuiWDLMessages.this.mc, width, height, 39, height - 32, 20);
            this.entries = new ArrayList<GuiListExtended.IGuiListEntry>(){
                {
                    Map<MessageTypeCategory, Collection<IWDLMessageType>> map = WDLMessages.getTypes().asMap();
                    for (Map.Entry<MessageTypeCategory, Collection<IWDLMessageType>> e2 : map.entrySet()) {
                        this.add(new CategoryEntry(e2.getKey()));
                        for (IWDLMessageType type : e2.getValue()) {
                            this.add(new MessageTypeEntry(type, e2.getKey()));
                        }
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

        private class CategoryEntry
        implements GuiListExtended.IGuiListEntry {
            private final GuiButton button;
            private final MessageTypeCategory category;

            public CategoryEntry(MessageTypeCategory category) {
                this.category = category;
                this.button = new GuiButton(0, 0, 0, 80, 20, "");
            }

            @Override
            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }

            @Override
            public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                GuiWDLMessages.drawCenteredString(GuiWDLMessages.this.fontRendererObj, this.category.getDisplayName(), width / 2 - 40, y2 + slotHeight - ((GuiMessageTypeList)GuiMessageTypeList.this).mc.fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFF);
                this.button.xPosition = width / 2 + 20;
                this.button.yPosition = y2;
                this.button.displayString = I18n.format("wdl.gui.messages.group." + WDLMessages.isGroupEnabled(this.category), new Object[0]);
                this.button.enabled = WDLMessages.enableAllMessages;
                this.button.drawButton(GuiMessageTypeList.this.mc, mouseX, mouseY);
            }

            @Override
            public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                if (this.button.mousePressed(GuiMessageTypeList.this.mc, x2, y2)) {
                    WDLMessages.toggleGroupEnabled(this.category);
                    this.button.playPressSound(GuiMessageTypeList.this.mc.getSoundHandler());
                    return true;
                }
                return false;
            }

            @Override
            public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
            }
        }

        private class MessageTypeEntry
        implements GuiListExtended.IGuiListEntry {
            private final GuiButton button;
            private final IWDLMessageType type;
            private final MessageTypeCategory category;

            public MessageTypeEntry(IWDLMessageType type, MessageTypeCategory category) {
                this.type = type;
                this.button = new GuiButton(0, 0, 0, type.toString());
                this.category = category;
            }

            @Override
            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }

            @Override
            public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                this.button.xPosition = width / 2 - 100;
                this.button.yPosition = y2;
                this.button.displayString = I18n.format("wdl.gui.messages.message." + WDLMessages.isEnabled(this.type), this.type.getDisplayName());
                this.button.enabled = WDLMessages.enableAllMessages && WDLMessages.isGroupEnabled(this.category);
                this.button.drawButton(GuiMessageTypeList.this.mc, mouseX, mouseY);
                if (this.button.isMouseOver()) {
                    GuiWDLMessages.this.hoveredButtonDescription = this.type.getDescription();
                }
            }

            @Override
            public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                if (this.button.mousePressed(GuiMessageTypeList.this.mc, x2, y2)) {
                    WDLMessages.toggleEnabled(this.type);
                    this.button.playPressSound(GuiMessageTypeList.this.mc.getSoundHandler());
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

