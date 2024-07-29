/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import wdl.WDL;
import wdl.WorldBackup;
import wdl.gui.Utils;

public class GuiWDLBackup
extends GuiScreen {
    private GuiScreen parent;
    private String description;
    private WorldBackup.WorldBackupType backupType;

    public GuiWDLBackup(GuiScreen parent) {
        this.parent = parent;
        this.description = String.valueOf(I18n.format("wdl.gui.backup.description1", new Object[0])) + "\n\n" + I18n.format("wdl.gui.backup.description2", new Object[0]) + "\n\n" + I18n.format("wdl.gui.backup.description3", new Object[0]);
    }

    @Override
    public void initGui() {
        this.backupType = WorldBackup.WorldBackupType.match(WDL.baseProps.getProperty("Backup", "ZIP"));
        this.buttonList.add(new GuiButton(0, width / 2 - 100, 32, this.getBackupButtonText()));
        this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        if (button.id == 0) {
            switch (this.backupType) {
                case NONE: {
                    this.backupType = WorldBackup.WorldBackupType.FOLDER;
                    break;
                }
                case FOLDER: {
                    this.backupType = WorldBackup.WorldBackupType.ZIP;
                    break;
                }
                case ZIP: {
                    this.backupType = WorldBackup.WorldBackupType.NONE;
                }
            }
            button.displayString = this.getBackupButtonText();
        } else if (button.id == 100) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    private String getBackupButtonText() {
        return I18n.format("wdl.gui.backup.backupMode", this.backupType.getDescription());
    }

    @Override
    public void onGuiClosed() {
        WDL.baseProps.setProperty("Backup", this.backupType.name());
        WDL.saveProps();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Utils.drawListBackground(23, 32, 0, 0, height, width);
        GuiWDLBackup.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.backup.title", new Object[0]), width / 2, 8, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
        Utils.drawGuiInfoBox(this.description, width - 50, 3 * height / 5, width, height, 48);
    }
}

