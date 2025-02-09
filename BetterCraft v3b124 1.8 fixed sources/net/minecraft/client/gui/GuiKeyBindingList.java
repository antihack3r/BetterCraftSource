/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

public class GuiKeyBindingList
extends GuiListExtended {
    private final GuiControls field_148191_k;
    private final Minecraft mc;
    private final GuiListExtended.IGuiListEntry[] listEntries;
    private int maxListLabelWidth = 0;

    public GuiKeyBindingList(GuiControls controls, Minecraft mcIn) {
        super(mcIn, GuiControls.width, GuiControls.height, 63, GuiControls.height - 32, 20);
        this.field_148191_k = controls;
        this.mc = mcIn;
        Object[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        this.listEntries = new GuiListExtended.IGuiListEntry[akeybinding.length + KeyBinding.getKeybinds().size()];
        Arrays.sort(akeybinding);
        int i2 = 0;
        String s2 = null;
        Object[] objectArray = akeybinding;
        int n2 = akeybinding.length;
        int n3 = 0;
        while (n3 < n2) {
            int j2;
            Object keybinding = objectArray[n3];
            String s1 = ((KeyBinding)keybinding).getKeyCategory();
            if (!s1.equals(s2)) {
                s2 = s1;
                this.listEntries[i2++] = new CategoryEntry(s1);
            }
            if ((j2 = mcIn.fontRendererObj.getStringWidth(I18n.format(((KeyBinding)keybinding).getKeyDescription(), new Object[0]))) > this.maxListLabelWidth) {
                this.maxListLabelWidth = j2;
            }
            this.listEntries[i2++] = new KeyEntry((KeyBinding)keybinding);
            ++n3;
        }
    }

    @Override
    protected int getSize() {
        return this.listEntries.length;
    }

    @Override
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.listEntries[index];
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    public class CategoryEntry
    implements GuiListExtended.IGuiListEntry {
        private final String labelText;
        private final int labelWidth;

        public CategoryEntry(String p_i45028_2_) {
            this.labelText = I18n.format(p_i45028_2_, new Object[0]);
            this.labelWidth = ((GuiKeyBindingList)GuiKeyBindingList.this).mc.fontRendererObj.getStringWidth(this.labelText);
        }

        @Override
        public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            GuiScreen cfr_ignored_0 = ((GuiKeyBindingList)GuiKeyBindingList.this).mc.currentScreen;
            ((GuiKeyBindingList)GuiKeyBindingList.this).mc.fontRendererObj.drawString(this.labelText, GuiScreen.width / 2 - this.labelWidth / 2, y2 + slotHeight - ((GuiKeyBindingList)GuiKeyBindingList.this).mc.fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFF);
        }

        @Override
        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }
    }

    public class KeyEntry
    implements GuiListExtended.IGuiListEntry {
        private final KeyBinding keybinding;
        private final String keyDesc;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnReset;

        private KeyEntry(KeyBinding p_i45029_2_) {
            this.keybinding = p_i45029_2_;
            this.keyDesc = I18n.format(p_i45029_2_.getKeyDescription(), new Object[0]);
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(p_i45029_2_.getKeyDescription(), new Object[0]));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset", new Object[0]));
        }

        @Override
        public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            boolean flag = ((GuiKeyBindingList)GuiKeyBindingList.this).field_148191_k.buttonId == this.keybinding;
            ((GuiKeyBindingList)GuiKeyBindingList.this).mc.fontRendererObj.drawString(this.keyDesc, x2 + 90 - GuiKeyBindingList.this.maxListLabelWidth, y2 + slotHeight / 2 - ((GuiKeyBindingList)GuiKeyBindingList.this).mc.fontRendererObj.FONT_HEIGHT / 2, 0xFFFFFF);
            this.btnReset.xPosition = x2 + 190;
            this.btnReset.yPosition = y2;
            this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault();
            this.btnReset.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
            this.btnChangeKeyBinding.xPosition = x2 + 105;
            this.btnChangeKeyBinding.yPosition = y2;
            this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
            boolean flag1 = false;
            if (this.keybinding.getKeyCode() != 0) {
                KeyBinding[] keyBindingArray = ((GuiKeyBindingList)GuiKeyBindingList.this).mc.gameSettings.keyBindings;
                int n2 = ((GuiKeyBindingList)GuiKeyBindingList.this).mc.gameSettings.keyBindings.length;
                int n3 = 0;
                while (n3 < n2) {
                    KeyBinding keybinding = keyBindingArray[n3];
                    if (keybinding != this.keybinding && keybinding.getKeyCode() == this.keybinding.getKeyCode()) {
                        flag1 = true;
                        break;
                    }
                    ++n3;
                }
            }
            if (flag) {
                this.btnChangeKeyBinding.displayString = (Object)((Object)EnumChatFormatting.WHITE) + "> " + (Object)((Object)EnumChatFormatting.YELLOW) + this.btnChangeKeyBinding.displayString + (Object)((Object)EnumChatFormatting.WHITE) + " <";
            } else if (flag1) {
                this.btnChangeKeyBinding.displayString = (Object)((Object)EnumChatFormatting.RED) + this.btnChangeKeyBinding.displayString;
            }
            this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
        }

        @Override
        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                ((GuiKeyBindingList)GuiKeyBindingList.this).field_148191_k.buttonId = this.keybinding;
                return true;
            }
            if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                ((GuiKeyBindingList)GuiKeyBindingList.this).mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x2, y2);
            this.btnReset.mouseReleased(x2, y2);
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }
    }
}

