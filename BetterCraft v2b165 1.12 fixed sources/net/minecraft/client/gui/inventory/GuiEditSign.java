// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ChatAllowedCharacters;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.client.gui.GuiScreen;

public class GuiEditSign extends GuiScreen
{
    private final TileEntitySign tileSign;
    private int updateCounter;
    private int editLine;
    private GuiButton doneBtn;
    
    public GuiEditSign(final TileEntitySign teSign) {
        this.tileSign = teSign;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.doneBtn = new GuiButton(0, GuiEditSign.width / 2 - 100, GuiEditSign.height / 4 + 120, I18n.format("gui.done", new Object[0]));
        this.buttonList.add(this.doneBtn);
        this.buttonList.add(new GuiButton(1, GuiEditSign.width / 2 - 100, GuiEditSign.height / 4 + 120 + 24, "Force OP"));
        this.buttonList.add(new GuiButton(2, GuiEditSign.width / 2 - 100, GuiEditSign.height / 4 + 120 + 24 + 24, "Freeze"));
        this.tileSign.setEditable(false);
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        final NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();
        if (nethandlerplayclient != null) {
            nethandlerplayclient.sendPacket(new CPacketUpdateSign(this.tileSign.getPos(), this.tileSign.signText));
        }
        this.tileSign.setEditable(true);
    }
    
    @Override
    public void updateScreen() {
        ++this.updateCounter;
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 0) {
                this.tileSign.markDirty();
                this.mc.displayGuiScreen(null);
            }
            if (button.id == 1) {
                this.tileSign.signText[0].getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/op " + this.mc.player.getName()));
                this.tileSign.signText[1] = new TextComponentString("");
                this.tileSign.signText[2] = new TextComponentString("");
                this.tileSign.markDirty();
                this.mc.displayGuiScreen(null);
            }
            if (button.id == 2) {
                for (int i = 0; i < this.tileSign.signText.length; ++i) {
                    this.tileSign.signText[i] = new TextComponentString(StringUtils.repeat("#", 32764));
                }
                this.tileSign.markDirty();
                this.mc.displayGuiScreen(null);
            }
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 200) {
            this.editLine = (this.editLine - 1 & 0x3);
        }
        if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
            this.editLine = (this.editLine + 1 & 0x3);
        }
        String s = this.tileSign.signText[this.editLine].getUnformattedText();
        if (keyCode == 14 && !s.isEmpty()) {
            s = s.substring(0, s.length() - 1);
        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.fontRendererObj.getStringWidth(String.valueOf(s) + typedChar) <= 90) {
            s = String.valueOf(s) + typedChar;
        }
        this.tileSign.signText[this.editLine] = new TextComponentString(s);
        if (keyCode == 1) {
            this.actionPerformed(this.doneBtn);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        Gui.drawCenteredString(this.fontRendererObj, I18n.format("sign.edit", new Object[0]), GuiEditSign.width / 2, 40, 16777215);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(GuiEditSign.width / 2), 0.0f, 50.0f);
        final float f = 93.75f;
        GlStateManager.scale(-93.75f, -93.75f, -93.75f);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        final Block block = this.tileSign.getBlockType();
        if (block == Blocks.STANDING_SIGN) {
            final float f2 = this.tileSign.getBlockMetadata() * 360 / 16.0f;
            GlStateManager.rotate(f2, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -1.0625f, 0.0f);
        }
        else {
            final int i = this.tileSign.getBlockMetadata();
            float f3 = 0.0f;
            if (i == 2) {
                f3 = 180.0f;
            }
            if (i == 4) {
                f3 = 90.0f;
            }
            if (i == 5) {
                f3 = -90.0f;
            }
            GlStateManager.rotate(f3, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -1.0625f, 0.0f);
        }
        if (this.updateCounter / 6 % 2 == 0) {
            this.tileSign.lineBeingEdited = this.editLine;
        }
        TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileSign, -0.5, -0.75, -0.5, 0.0f);
        this.tileSign.lineBeingEdited = -1;
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
