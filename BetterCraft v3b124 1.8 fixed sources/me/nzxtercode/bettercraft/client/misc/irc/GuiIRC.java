/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.irc;

import java.io.IOException;
import java.util.Arrays;
import me.nzxtercode.bettercraft.client.misc.irc.IRC;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiIRC
extends GuiScreen {
    private GuiScreen parent;
    private int sidebarX;
    private int sidebarY;
    private GuiSlot chatSlot;
    private GuiTextField messageField;

    public GuiIRC(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width - 88, height - 26, 80, 20, "Back"));
        this.messageField = new GuiTextField(0, this.fontRendererObj, 5, height - 25, width - 100, 20);
        this.messageField.setMaxStringLength(100);
        this.chatSlot = new ChatSlot(this.mc, width, height, 10, height - 32, 20);
        IRC.getInstance().setUnreadMessages(0);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        IRC.getInstance().setUnreadMessages(0);
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.chatSlot.drawScreen(mouseX, mouseY, partialTicks);
        GuiIRC.drawCenteredString(this.fontRendererObj, "Users: " + EnumChatFormatting.RED.toString() + String.valueOf(IRC.getInstance().getUsers(IRC.getInstance().currentChannel).length), width / 2, 1, 0xFFFFFF);
        this.messageField.drawTextBox();
        int fadeSpeed = 10;
        int sidebarWidth = 100;
        this.sidebarX = mouseX >= width - sidebarWidth && mouseX <= width ? (this.sidebarX + fadeSpeed >= sidebarWidth ? sidebarWidth : (this.sidebarX += fadeSpeed)) : (this.sidebarX - fadeSpeed <= 0 ? 0 : (this.sidebarX -= fadeSpeed));
        this.sidebarY += Mouse.getDWheel() / 30;
        Gui.drawRect(width - this.sidebarX, 0, width, height, Integer.MIN_VALUE);
        Arrays.asList(IRC.getInstance().getUsers(IRC.getInstance().currentChannel)).forEach(user -> GuiIRC.drawCenteredString(this.fontRendererObj, String.format("%s%s", new Object[]{user.isOp() ? EnumChatFormatting.RED : EnumChatFormatting.RESET, user.getNick()}), width - this.sidebarX + sidebarWidth / 2, (Arrays.asList(IRC.getInstance().getUsers(IRC.getInstance().currentChannel)).indexOf(user) + 1) * this.fontRendererObj.FONT_HEIGHT + this.sidebarY, -1));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 28 && this.messageField.isFocused() && !this.messageField.getText().isEmpty()) {
            IRC.getInstance().sendClientMessage(this.messageField.getText());
            this.messageField.setText("");
        }
        this.messageField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.messageField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        this.chatSlot.actionPerformed(button);
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.chatSlot.handleMouseInput();
        super.handleMouseInput();
    }

    public class ChatSlot
    extends GuiSlot {
        public ChatSlot(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
            super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
            IRC.getInstance().getMessageListeners().add(messageEntry -> {
                float f2 = this.amountScrolled = this.amountScrolled + (float)this.slotHeight;
            });
        }

        @Override
        protected int getSize() {
            return IRC.getInstance().getMessageList().size();
        }

        @Override
        public int getListWidth() {
            return this.width - 40;
        }

        @Override
        protected int getScrollBarX() {
            return this.width - 6;
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return false;
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        protected void drawSlot(int entryID, int x2, int y2, int p_180791_4_, int mouseXIn, int mouseYIn) {
            String sender = IRC.getInstance().getMessageList().get(entryID).getKey();
            String message = IRC.getInstance().getMessageList().get(entryID).getValue();
            if (sender.equals(IRC.getInstance().getName())) {
                GlStateManager.pushMatrix();
                float scale = 0.7f;
                GlStateManager.scale(scale, scale, scale);
                this.mc.fontRendererObj.drawString(sender, (int)(((float)(x2 + this.getListWidth() - (this.width - this.getScrollBarX())) - ((int)((float)this.mc.fontRendererObj.getStringWidth(sender) * scale) > this.mc.fontRendererObj.getStringWidth(message) ? (float)this.mc.fontRendererObj.getStringWidth(sender) * scale : (float)this.mc.fontRendererObj.getStringWidth(message))) / scale), (int)((float)(y2 + 2) / scale), ColorUtils.rainbowEffect());
                GlStateManager.popMatrix();
                this.mc.fontRendererObj.drawString(String.valueOf(EnumChatFormatting.GRAY.toString()) + message, (int)((float)(x2 + this.getListWidth() - (this.width - this.getScrollBarX())) - ((int)((float)this.mc.fontRendererObj.getStringWidth(sender) * scale) > this.mc.fontRendererObj.getStringWidth(message) ? (float)this.mc.fontRendererObj.getStringWidth(sender) * scale : (float)this.mc.fontRendererObj.getStringWidth(message))), y2 + 8, -1);
            } else {
                GlStateManager.pushMatrix();
                float scale = 0.7f;
                GlStateManager.scale(scale, scale, scale);
                this.mc.fontRendererObj.drawString(sender, (int)((float)x2 / scale), (int)((float)(y2 + 2) / scale), ColorUtils.rainbowEffect());
                GlStateManager.popMatrix();
                this.mc.fontRendererObj.drawString(String.valueOf(EnumChatFormatting.GRAY.toString()) + message, x2, y2 + 8, -1);
            }
        }
    }
}

