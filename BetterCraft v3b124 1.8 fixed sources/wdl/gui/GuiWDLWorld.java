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

public class GuiWDLWorld
extends GuiScreen {
    private String title;
    private GuiScreen parent;
    private GuiButton allowCheatsBtn;
    private GuiButton gamemodeBtn;
    private GuiButton timeBtn;
    private GuiButton weatherBtn;
    private GuiButton spawnBtn;
    private GuiButton pickSpawnBtn;
    private boolean showSpawnFields = false;
    private GuiNumericTextField spawnX;
    private GuiNumericTextField spawnY;
    private GuiNumericTextField spawnZ;
    private int spawnTextY;

    public GuiWDLWorld(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.title = I18n.format("wdl.gui.world.title", WDL.baseFolderName.replace('@', ':'));
        int y2 = height / 4 - 15;
        this.gamemodeBtn = new GuiButton(1, width / 2 - 100, y2, this.getGamemodeText());
        this.buttonList.add(this.gamemodeBtn);
        this.allowCheatsBtn = new GuiButton(6, width / 2 - 100, y2 += 22, this.getAllowCheatsText());
        this.buttonList.add(this.allowCheatsBtn);
        this.timeBtn = new GuiButton(2, width / 2 - 100, y2 += 22, this.getTimeText());
        this.buttonList.add(this.timeBtn);
        this.weatherBtn = new GuiButton(3, width / 2 - 100, y2 += 22, this.getWeatherText());
        this.buttonList.add(this.weatherBtn);
        this.spawnBtn = new GuiButton(4, width / 2 - 100, y2 += 22, this.getSpawnText());
        this.buttonList.add(this.spawnBtn);
        this.spawnTextY = (y2 += 22) + 4;
        this.spawnX = new GuiNumericTextField(40, this.fontRendererObj, width / 2 - 87, y2, 50, 16);
        this.spawnY = new GuiNumericTextField(41, this.fontRendererObj, width / 2 - 19, y2, 50, 16);
        this.spawnZ = new GuiNumericTextField(42, this.fontRendererObj, width / 2 + 48, y2, 50, 16);
        this.spawnX.setText(WDL.worldProps.getProperty("SpawnX"));
        this.spawnY.setText(WDL.worldProps.getProperty("SpawnY"));
        this.spawnZ.setText(WDL.worldProps.getProperty("SpawnZ"));
        this.spawnX.setMaxStringLength(7);
        this.spawnY.setMaxStringLength(7);
        this.spawnZ.setMaxStringLength(7);
        this.pickSpawnBtn = new GuiButton(5, width / 2, y2 += 18, 100, 20, I18n.format("wdl.gui.world.setSpawnToCurrentPosition", new Object[0]));
        this.buttonList.add(this.pickSpawnBtn);
        this.updateSpawnTextBoxVisibility();
        this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 1) {
                this.cycleGamemode();
            } else if (button.id == 2) {
                this.cycleTime();
            } else if (button.id == 3) {
                this.cycleWeather();
            } else if (button.id == 4) {
                this.cycleSpawn();
            } else if (button.id == 5) {
                this.setSpawnToPlayerPosition();
            } else if (button.id == 6) {
                this.cycleAllowCheats();
            } else if (button.id == 100) {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        if (this.showSpawnFields) {
            WDL.worldProps.setProperty("SpawnX", this.spawnX.getText());
            WDL.worldProps.setProperty("SpawnY", this.spawnY.getText());
            WDL.worldProps.setProperty("SpawnZ", this.spawnZ.getText());
        }
        WDL.saveProps();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.showSpawnFields) {
            this.spawnX.mouseClicked(mouseX, mouseY, mouseButton);
            this.spawnY.mouseClicked(mouseX, mouseY, mouseButton);
            this.spawnZ.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.spawnX.textboxKeyTyped(typedChar, keyCode);
        this.spawnY.textboxKeyTyped(typedChar, keyCode);
        this.spawnZ.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        this.spawnX.updateCursorCounter();
        this.spawnY.updateCursorCounter();
        this.spawnZ.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Utils.drawListBackground(23, 32, 0, 0, height, width);
        GuiWDLWorld.drawCenteredString(this.fontRendererObj, this.title, width / 2, 8, 0xFFFFFF);
        if (this.showSpawnFields) {
            this.drawString(this.fontRendererObj, "X:", width / 2 - 99, this.spawnTextY, 0xFFFFFF);
            this.drawString(this.fontRendererObj, "Y:", width / 2 - 31, this.spawnTextY, 0xFFFFFF);
            this.drawString(this.fontRendererObj, "Z:", width / 2 + 37, this.spawnTextY, 0xFFFFFF);
            this.spawnX.drawTextBox();
            this.spawnY.drawTextBox();
            this.spawnZ.drawTextBox();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        String tooltip = null;
        if (this.allowCheatsBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.world.allowCheats.description", new Object[0]);
        } else if (this.gamemodeBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.world.gamemode.description", new Object[0]);
        } else if (this.timeBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.world.time.description", new Object[0]);
        } else if (this.weatherBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.world.weather.description", new Object[0]);
        } else if (this.spawnBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.world.spawn.description", new Object[0]);
        } else if (this.pickSpawnBtn.isMouseOver()) {
            tooltip = I18n.format("wdl.gui.world.setSpawnToCurrentPosition.description", new Object[0]);
        }
        if (this.showSpawnFields) {
            if (Utils.isMouseOverTextBox(mouseX, mouseY, this.spawnX)) {
                tooltip = I18n.format("wdl.gui.world.spawnPos.description", "X");
            } else if (Utils.isMouseOverTextBox(mouseX, mouseY, this.spawnY)) {
                tooltip = I18n.format("wdl.gui.world.spawnPos.description", "Y");
            } else if (Utils.isMouseOverTextBox(mouseX, mouseY, this.spawnZ)) {
                tooltip = I18n.format("wdl.gui.world.spawnPos.description", "Z");
            }
        }
        Utils.drawGuiInfoBox(tooltip, width, height, 48);
    }

    private void cycleAllowCheats() {
        if (WDL.baseProps.getProperty("AllowCheats").equals("true")) {
            WDL.baseProps.setProperty("AllowCheats", "false");
        } else {
            WDL.baseProps.setProperty("AllowCheats", "true");
        }
        this.allowCheatsBtn.displayString = this.getAllowCheatsText();
    }

    private void cycleGamemode() {
        String prop = WDL.baseProps.getProperty("GameType");
        if (prop.equals("keep")) {
            WDL.baseProps.setProperty("GameType", "creative");
        } else if (prop.equals("creative")) {
            WDL.baseProps.setProperty("GameType", "survival");
        } else if (prop.equals("survival")) {
            WDL.baseProps.setProperty("GameType", "hardcore");
        } else if (prop.equals("hardcore")) {
            WDL.baseProps.setProperty("GameType", "keep");
        }
        this.gamemodeBtn.displayString = this.getGamemodeText();
    }

    private void cycleTime() {
        String prop = WDL.baseProps.getProperty("Time");
        if (prop.equals("keep")) {
            WDL.baseProps.setProperty("Time", "23000");
        } else if (prop.equals("23000")) {
            WDL.baseProps.setProperty("Time", "0");
        } else if (prop.equals("0")) {
            WDL.baseProps.setProperty("Time", "6000");
        } else if (prop.equals("6000")) {
            WDL.baseProps.setProperty("Time", "11500");
        } else if (prop.equals("11500")) {
            WDL.baseProps.setProperty("Time", "12500");
        } else if (prop.equals("12500")) {
            WDL.baseProps.setProperty("Time", "18000");
        } else {
            WDL.baseProps.setProperty("Time", "keep");
        }
        this.timeBtn.displayString = this.getTimeText();
    }

    private void cycleWeather() {
        String prop = WDL.baseProps.getProperty("Weather");
        if (prop.equals("keep")) {
            WDL.baseProps.setProperty("Weather", "sunny");
        } else if (prop.equals("sunny")) {
            WDL.baseProps.setProperty("Weather", "rain");
        } else if (prop.equals("rain")) {
            WDL.baseProps.setProperty("Weather", "thunderstorm");
        } else if (prop.equals("thunderstorm")) {
            WDL.baseProps.setProperty("Weather", "keep");
        }
        this.weatherBtn.displayString = this.getWeatherText();
    }

    private void cycleSpawn() {
        String prop = WDL.worldProps.getProperty("Spawn");
        if (prop.equals("auto")) {
            WDL.worldProps.setProperty("Spawn", "player");
        } else if (prop.equals("player")) {
            WDL.worldProps.setProperty("Spawn", "xyz");
        } else if (prop.equals("xyz")) {
            WDL.worldProps.setProperty("Spawn", "auto");
        }
        this.spawnBtn.displayString = this.getSpawnText();
        this.updateSpawnTextBoxVisibility();
    }

    private String getAllowCheatsText() {
        return I18n.format("wdl.gui.world.allowCheats." + WDL.baseProps.getProperty("AllowCheats"), new Object[0]);
    }

    private String getGamemodeText() {
        return I18n.format("wdl.gui.world.gamemode." + WDL.baseProps.getProperty("GameType"), new Object[0]);
    }

    private String getTimeText() {
        String result = I18n.format("wdl.gui.world.time." + WDL.baseProps.getProperty("Time"), new Object[0]);
        if (result.startsWith("wdl.gui.world.time.")) {
            result = I18n.format("wdl.gui.world.time.custom", WDL.baseProps.getProperty("Time"));
        }
        return result;
    }

    private String getWeatherText() {
        return I18n.format("wdl.gui.world.weather." + WDL.baseProps.getProperty("Weather"), new Object[0]);
    }

    private String getSpawnText() {
        return I18n.format("wdl.gui.world.spawn." + WDL.worldProps.getProperty("Spawn"), new Object[0]);
    }

    private void updateSpawnTextBoxVisibility() {
        boolean show;
        this.showSpawnFields = show = WDL.worldProps.getProperty("Spawn").equals("xyz");
        this.pickSpawnBtn.visible = show;
    }

    private void setSpawnToPlayerPosition() {
        this.spawnX.setValue((int)WDL.thePlayer.posX);
        this.spawnY.setValue((int)WDL.thePlayer.posY);
        this.spawnZ.setValue((int)WDL.thePlayer.posZ);
    }
}

