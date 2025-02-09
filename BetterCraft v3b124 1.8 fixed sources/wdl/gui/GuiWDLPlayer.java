/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import wdl.WDL;
import wdl.gui.GuiNumericTextField;
import wdl.gui.Utils;

public class GuiWDLPlayer
extends GuiScreen {
    private String title;
    private GuiScreen parent;
    private GuiButton healthBtn;
    private GuiButton hungerBtn;
    private GuiButton playerPosBtn;
    private GuiButton pickPosBtn;
    private boolean showPosFields = false;
    private GuiNumericTextField posX;
    private GuiNumericTextField posY;
    private GuiNumericTextField posZ;
    private int posTextY;

    public GuiWDLPlayer(GuiScreen var1) {
        this.parent = var1;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.title = I18n.format("wdl.gui.player.title", WDL.baseFolderName.replace('@', ':'));
        int y2 = height / 4 - 15;
        this.healthBtn = new GuiButton(1, width / 2 - 100, y2, this.getHealthText());
        this.buttonList.add(this.healthBtn);
        this.hungerBtn = new GuiButton(2, width / 2 - 100, y2 += 22, this.getHungerText());
        this.buttonList.add(this.hungerBtn);
        this.playerPosBtn = new GuiButton(3, width / 2 - 100, y2 += 22, this.getPlayerPosText());
        this.buttonList.add(this.playerPosBtn);
        this.posTextY = (y2 += 22) + 4;
        this.posX = new GuiNumericTextField(40, this.fontRendererObj, width / 2 - 87, y2, 50, 16);
        this.posY = new GuiNumericTextField(41, this.fontRendererObj, width / 2 - 19, y2, 50, 16);
        this.posZ = new GuiNumericTextField(42, this.fontRendererObj, width / 2 + 48, y2, 50, 16);
        this.posX.setText(WDL.worldProps.getProperty("PlayerX"));
        this.posY.setText(WDL.worldProps.getProperty("PlayerY"));
        this.posZ.setText(WDL.worldProps.getProperty("PlayerZ"));
        this.posX.setMaxStringLength(7);
        this.posY.setMaxStringLength(7);
        this.posZ.setMaxStringLength(7);
        this.pickPosBtn = new GuiButton(4, width / 2 - 0, y2 += 18, 100, 20, I18n.format("wdl.gui.player.setPositionToCurrentPosition", new Object[0]));
        this.buttonList.add(this.pickPosBtn);
        this.upadatePlayerPosVisibility();
        this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton var1) {
        if (var1.enabled) {
            if (var1.id == 1) {
                this.cycleHealth();
            } else if (var1.id == 2) {
                this.cycleHunger();
            } else if (var1.id == 3) {
                this.cyclePlayerPos();
            } else if (var1.id == 4) {
                this.setPlayerPosToPlayerPosition();
            } else if (var1.id == 100) {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        if (this.showPosFields) {
            WDL.worldProps.setProperty("PlayerX", this.posX.getText());
            WDL.worldProps.setProperty("PlayerY", this.posY.getText());
            WDL.worldProps.setProperty("PlayerZ", this.posZ.getText());
        }
        WDL.saveProps();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.showPosFields) {
            this.posX.mouseClicked(mouseX, mouseY, mouseButton);
            this.posY.mouseClicked(mouseX, mouseY, mouseButton);
            this.posZ.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.posX.textboxKeyTyped(typedChar, keyCode);
        this.posY.textboxKeyTyped(typedChar, keyCode);
        this.posZ.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        this.posX.updateCursorCounter();
        this.posY.updateCursorCounter();
        this.posZ.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Utils.drawListBackground(23, 32, 0, 0, height, width);
        GuiWDLPlayer.drawCenteredString(this.fontRendererObj, this.title, width / 2, 8, 0xFFFFFF);
        String tooltip = null;
        if (this.showPosFields) {
            this.drawString(this.fontRendererObj, "X:", width / 2 - 99, this.posTextY, 0xFFFFFF);
            this.drawString(this.fontRendererObj, "Y:", width / 2 - 31, this.posTextY, 0xFFFFFF);
            this.drawString(this.fontRendererObj, "Z:", width / 2 + 37, this.posTextY, 0xFFFFFF);
            this.posX.drawTextBox();
            this.posY.drawTextBox();
            this.posZ.drawTextBox();
            if (Utils.isMouseOverTextBox(mouseX, mouseY, this.posX)) {
                tooltip = I18n.format("wdl.gui.player.positionTextBox.description", "X");
            } else if (Utils.isMouseOverTextBox(mouseX, mouseY, this.posY)) {
                tooltip = I18n.format("wdl.gui.player.positionTextBox.description", "Y");
            } else if (Utils.isMouseOverTextBox(mouseX, mouseY, this.posZ)) {
                tooltip = I18n.format("wdl.gui.player.positionTextBox.description", "Z");
            }
            if (this.pickPosBtn.isMouseOver()) {
                tooltip = I18n.format("wdl.gui.player.setPositionToCurrentPosition.description", new Object[0]);
            }
        }
        if (this.healthBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.player.health.description", new Object[0]);
        }
        if (this.hungerBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.player.hunger.description", new Object[0]);
        }
        if (this.playerPosBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.player.position.description", new Object[0]);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (tooltip != null) {
            Utils.drawGuiInfoBox(tooltip, width, height, 48);
        }
    }

    private void cycleHealth() {
        String prop = WDL.baseProps.getProperty("PlayerHealth");
        if (prop.equals("keep")) {
            WDL.baseProps.setProperty("PlayerHealth", "20");
        } else if (prop.equals("20")) {
            WDL.baseProps.setProperty("PlayerHealth", "keep");
        }
        this.healthBtn.displayString = this.getHealthText();
    }

    private void cycleHunger() {
        String prop = WDL.baseProps.getProperty("PlayerFood");
        if (prop.equals("keep")) {
            WDL.baseProps.setProperty("PlayerFood", "20");
        } else if (prop.equals("20")) {
            WDL.baseProps.setProperty("PlayerFood", "keep");
        }
        this.hungerBtn.displayString = this.getHungerText();
    }

    private void cyclePlayerPos() {
        String prop = WDL.worldProps.getProperty("PlayerPos");
        if (prop.equals("keep")) {
            WDL.worldProps.setProperty("PlayerPos", "xyz");
        } else if (prop.equals("xyz")) {
            WDL.worldProps.setProperty("PlayerPos", "keep");
        }
        this.playerPosBtn.displayString = this.getPlayerPosText();
        this.upadatePlayerPosVisibility();
    }

    private String getHealthText() {
        String result = I18n.format("wdl.gui.player.health." + WDL.baseProps.getProperty("PlayerHealth"), new Object[0]);
        if (result.startsWith("wdl.gui.player.health.")) {
            result = I18n.format("wdl.gui.player.health.custom", WDL.baseProps.getProperty("PlayerHealth"));
        }
        return result;
    }

    private String getHungerText() {
        String result = I18n.format("wdl.gui.player.hunger." + WDL.baseProps.getProperty("PlayerFood"), new Object[0]);
        if (result.startsWith("wdl.gui.player.hunger.")) {
            result = I18n.format("wdl.gui.player.hunger.custom", WDL.baseProps.getProperty("PlayerFood"));
        }
        return result;
    }

    private void upadatePlayerPosVisibility() {
        this.pickPosBtn.visible = this.showPosFields = WDL.worldProps.getProperty("PlayerPos").equals("xyz");
    }

    private String getPlayerPosText() {
        return I18n.format("wdl.gui.player.position." + WDL.worldProps.getProperty("PlayerPos"), new Object[0]);
    }

    private void setPlayerPosToPlayerPosition() {
        this.posX.setValue((int)WDL.thePlayer.posX);
        this.posY.setValue((int)WDL.thePlayer.posY);
        this.posZ.setValue((int)WDL.thePlayer.posZ);
    }
}

