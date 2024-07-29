/*
 * Decompiled with CFR 0.152.
 */
package de.florianmichael.viamcp.gui;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.protocolinfo.ProtocolInfo;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class GuiProtocolSelector
extends GuiScreen {
    private final GuiScreen parent;
    public SlotList list;

    public GuiProtocolSelector(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height - 25, 200, 20, "Back"));
        this.list = new SlotList(this.mc, width, height, 32, height - 32);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        this.list.actionPerformed(guiButton);
        if (guiButton.id == 1) {
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
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        String title = (Object)((Object)EnumChatFormatting.BOLD) + "ViaVersion";
        this.drawString(this.fontRendererObj, title, (width - this.fontRendererObj.getStringWidth(title) * 2) / 4, 5, -1);
        GlStateManager.popMatrix();
        ProtocolInfo protocolInfo = ProtocolInfo.fromProtocolVersion(ViaLoadingBase.getInstance().getTargetVersion());
        String versionTitle = "Version: " + ViaLoadingBase.getInstance().getTargetVersion().getName() + " - " + protocolInfo.getName();
        String versionReleased = "Released: " + protocolInfo.getReleaseDate();
        int fixedHeight = (5 + this.fontRendererObj.FONT_HEIGHT) * 2 + 2;
        this.drawString(this.fontRendererObj, (Object)((Object)EnumChatFormatting.GRAY) + (Object)((Object)EnumChatFormatting.BOLD) + "Version Information", (width - this.fontRendererObj.getStringWidth("Version Information")) / 2, fixedHeight, -1);
        this.drawString(this.fontRendererObj, versionTitle, (width - this.fontRendererObj.getStringWidth(versionTitle)) / 2, fixedHeight + this.fontRendererObj.FONT_HEIGHT, -1);
        this.drawString(this.fontRendererObj, versionReleased, (width - this.fontRendererObj.getStringWidth(versionReleased)) / 2, fixedHeight + this.fontRendererObj.FONT_HEIGHT * 2, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class SlotList
    extends GuiSlot {
        public SlotList(Minecraft mc2, int width, int height, int top, int bottom) {
            super(mc2, width, height, top + 30, bottom, 18);
        }

        @Override
        protected int getSize() {
            return ViaLoadingBase.getProtocols().size();
        }

        @Override
        protected void elementClicked(int i2, boolean b2, int i1, int i22) {
            ProtocolVersion protocolVersion = ViaLoadingBase.getProtocols().get(i2);
            ViaLoadingBase.getInstance().reload(protocolVersion);
        }

        @Override
        protected boolean isSelected(int i2) {
            return false;
        }

        @Override
        protected void drawBackground() {
            GuiProtocolSelector.this.drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int i2, int i1, int i22, int i3, int i4, int i5) {
            GuiProtocolSelector.drawCenteredString(this.mc.fontRendererObj, String.valueOf(ViaLoadingBase.getInstance().getTargetVersion().getIndex() == i2 ? String.valueOf(EnumChatFormatting.GREEN.toString()) + (Object)((Object)EnumChatFormatting.BOLD) : EnumChatFormatting.GRAY.toString()) + ViaLoadingBase.getProtocols().get(i2).getName(), this.width / 2, i22 + 2, -1);
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GuiProtocolSelector.drawCenteredString(this.mc.fontRendererObj, "PVN: " + ViaLoadingBase.getProtocols().get(i2).getVersion(), this.width, (i22 + 2) * 2 + 20, -1);
            GlStateManager.popMatrix();
        }
    }
}

