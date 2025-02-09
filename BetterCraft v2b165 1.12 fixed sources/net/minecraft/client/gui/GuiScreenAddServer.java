// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import java.net.IDN;
import net.minecraft.util.StringUtils;
import javax.annotation.Nullable;
import com.google.common.base.Predicate;
import net.minecraft.client.multiplayer.ServerData;

public class GuiScreenAddServer extends GuiScreen
{
    private final GuiScreen parentScreen;
    private final ServerData serverData;
    private GuiTextField serverIPField;
    private GuiTextField serverNameField;
    private GuiButton serverResourcePacks;
    private final Predicate<String> addressFilter;
    
    public GuiScreenAddServer(final GuiScreen p_i1033_1_, final ServerData p_i1033_2_) {
        this.addressFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable final String p_apply_1_) {
                if (StringUtils.isNullOrEmpty(p_apply_1_)) {
                    return true;
                }
                final String[] astring = p_apply_1_.split(":");
                if (astring.length == 0) {
                    return true;
                }
                try {
                    final String s = IDN.toASCII(astring[0]);
                    return true;
                }
                catch (final IllegalArgumentException var4) {
                    return false;
                }
            }
        };
        this.parentScreen = p_i1033_1_;
        this.serverData = p_i1033_2_;
    }
    
    @Override
    public void updateScreen() {
        this.serverNameField.updateCursorCounter();
        this.serverIPField.updateCursorCounter();
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, GuiScreenAddServer.width / 2 - 100, GuiScreenAddServer.height / 4 + 96 + 18, I18n.format("addServer.add", new Object[0])));
        this.buttonList.add(new GuiButton(1, GuiScreenAddServer.width / 2 - 100, GuiScreenAddServer.height / 4 + 120 + 18, I18n.format("gui.cancel", new Object[0])));
        this.serverResourcePacks = this.addButton(new GuiButton(2, GuiScreenAddServer.width / 2 - 100, GuiScreenAddServer.height / 4 + 72, String.valueOf(I18n.format("addServer.resourcePack", new Object[0])) + ": " + this.serverData.getResourceMode().getMotd().getFormattedText()));
        (this.serverNameField = new GuiTextField(0, this.fontRendererObj, GuiScreenAddServer.width / 2 - 100, 66, 200, 20)).setFocused(true);
        this.serverNameField.setText(this.serverData.serverName);
        (this.serverIPField = new GuiTextField(1, this.fontRendererObj, GuiScreenAddServer.width / 2 - 100, 106, 200, 20)).setMaxStringLength(128);
        this.serverIPField.setText(this.serverData.serverIP);
        this.serverIPField.setValidator(this.addressFilter);
        this.buttonList.get(0).enabled = (!this.serverIPField.getText().isEmpty() && this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty());
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                this.serverData.setResourceMode(ServerData.ServerResourceMode.values()[(this.serverData.getResourceMode().ordinal() + 1) % ServerData.ServerResourceMode.values().length]);
                this.serverResourcePacks.displayString = String.valueOf(I18n.format("addServer.resourcePack", new Object[0])) + ": " + this.serverData.getResourceMode().getMotd().getFormattedText();
            }
            else if (button.id == 1) {
                this.parentScreen.confirmClicked(false, 0);
            }
            else if (button.id == 0) {
                this.serverData.serverName = this.serverNameField.getText();
                this.serverData.serverIP = this.serverIPField.getText();
                this.parentScreen.confirmClicked(true, 0);
            }
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        this.serverNameField.textboxKeyTyped(typedChar, keyCode);
        this.serverIPField.textboxKeyTyped(typedChar, keyCode);
        if (!this.serverNameField.isFocused()) {
            this.serverNameField.setText(this.serverIPField.getText());
        }
        if (keyCode == 15) {
            this.serverNameField.setFocused(!this.serverNameField.isFocused());
            this.serverIPField.setFocused(!this.serverIPField.isFocused());
        }
        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.buttonList.get(0).enabled = (!this.serverIPField.getText().isEmpty() && this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty());
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverIPField.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverNameField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        Gui.drawCenteredString(this.fontRendererObj, I18n.format("addServer.title", new Object[0]), GuiScreenAddServer.width / 2, 17, 16777215);
        Gui.drawString(this.fontRendererObj, I18n.format("addServer.enterName", new Object[0]), GuiScreenAddServer.width / 2 - 100, 53, 10526880);
        Gui.drawString(this.fontRendererObj, I18n.format("addServer.enterIp", new Object[0]), GuiScreenAddServer.width / 2 - 100, 94, 10526880);
        this.serverNameField.drawTextBox();
        this.serverIPField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
