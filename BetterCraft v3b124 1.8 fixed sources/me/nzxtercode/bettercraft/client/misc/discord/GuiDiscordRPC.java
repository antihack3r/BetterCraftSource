/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.discord;

import java.io.IOException;
import me.nzxtercode.bettercraft.client.misc.discord.DiscordRPCList;
import me.nzxtercode.bettercraft.client.misc.discord.DiscordRPCManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class GuiDiscordRPC
extends GuiScreen {
    private GuiScreen parent;
    private SlotList list;

    public GuiDiscordRPC(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height - 27, 200, 20, "Back"));
        this.list = new SlotList(this.mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glPushMatrix();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GuiDiscordRPC.drawCenteredString(this.fontRendererObj, "Discord RPC", width / 4, 6, 0xFFFFFF);
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class SlotList
    extends GuiSlot {
        private int selected;

        public SlotList(Minecraft mc2, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
            super(mc2, width, height, topIn, bottomIn, slotHeightIn);
        }

        @Override
        protected int getSize() {
            return DiscordRPCList.values().length;
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            DiscordRPCManager.getInstance().setDiscordRPC(DiscordRPCList.values()[index]);
            this.selected = index;
        }

        @Override
        protected int getContentHeight() {
            return this.getSize() * 12;
        }

        @Override
        protected boolean isSelected(int index) {
            return index == this.selected;
        }

        @Override
        protected void drawBackground() {
            GuiDiscordRPC.this.drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int index, int right, int top, int height, int mouseX, int mouseY) {
            DiscordRPCList currentRPC = DiscordRPCList.values()[index];
            GuiDiscordRPC.drawCenteredString(this.mc.fontRendererObj, (index == this.selected ? EnumChatFormatting.BOLD : "") + currentRPC.getGameName(), this.width / 2, top, -1);
        }
    }
}

