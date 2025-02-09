// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.optifine.CustomLoadingScreens;
import net.optifine.CustomLoadingScreen;
import net.minecraft.util.IProgressUpdate;

public class GuiScreenWorking extends GuiScreen implements IProgressUpdate
{
    private String field_146591_a;
    private String field_146589_f;
    private int progress;
    private boolean doneWorking;
    private CustomLoadingScreen customLoadingScreen;
    
    public GuiScreenWorking() {
        this.field_146591_a = "";
        this.field_146589_f = "";
        this.customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();
    }
    
    @Override
    public void displaySavingString(final String message) {
        this.resetProgressAndMessage(message);
    }
    
    @Override
    public void resetProgressAndMessage(final String message) {
        this.field_146591_a = message;
        this.displayLoadingString("Working...");
    }
    
    @Override
    public void displayLoadingString(final String message) {
        this.field_146589_f = message;
        this.setLoadingProgress(0);
    }
    
    @Override
    public void setLoadingProgress(final int progress) {
        this.progress = progress;
    }
    
    @Override
    public void setDoneWorking() {
        this.doneWorking = true;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (this.doneWorking) {
            if (!this.mc.isConnectedToRealms()) {
                this.mc.displayGuiScreen(null);
            }
        }
        else {
            if (this.customLoadingScreen != null && this.mc.theWorld == null) {
                this.customLoadingScreen.drawBackground(GuiScreenWorking.width, GuiScreenWorking.height);
            }
            else {
                this.drawDefaultBackground();
            }
            if (this.progress > 0) {
                Gui.drawCenteredString(this.fontRendererObj, this.field_146591_a, GuiScreenWorking.width / 2, 70, 16777215);
                Gui.drawCenteredString(this.fontRendererObj, String.valueOf(this.field_146589_f) + " " + this.progress + "%", GuiScreenWorking.width / 2, 90, 16777215);
            }
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
