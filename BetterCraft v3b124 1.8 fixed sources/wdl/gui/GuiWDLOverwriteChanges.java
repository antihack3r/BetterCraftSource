/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WorldBackup;
import wdl.gui.GuiTurningCameraBase;
import wdl.gui.Utils;

public class GuiWDLOverwriteChanges
extends GuiTurningCameraBase
implements WorldBackup.IBackupProgressMonitor {
    private volatile boolean backingUp = false;
    private volatile String backupData = "";
    private volatile int backupCount;
    private volatile int backupCurrent;
    private volatile String backupFile = "";
    private int infoBoxX;
    private int infoBoxY;
    private int infoBoxWidth;
    private int infoBoxHeight;
    private GuiButton backupAsZipButton;
    private GuiButton backupAsFolderButton;
    private GuiButton downloadNowButton;
    private GuiButton cancelButton;
    private final long lastSaved;
    private final long lastPlayed;
    private String title;
    private String footer;
    private String captionTitle;
    private String captionSubtitle;
    private String overwriteWarning1;
    private String overwriteWarning2;
    private String backingUpTitle;

    public GuiWDLOverwriteChanges(long lastSaved, long lastPlayed) {
        this.lastSaved = lastSaved;
        this.lastPlayed = lastPlayed;
    }

    @Override
    public void initGui() {
        this.backingUp = false;
        this.title = I18n.format("wdl.gui.overwriteChanges.title", new Object[0]);
        this.footer = this.lastSaved != -1L ? I18n.format("wdl.gui.overwriteChanges.footer", this.lastSaved, this.lastPlayed) : I18n.format("wdl.gui.overwriteChanges.footerNeverSaved", this.lastPlayed);
        this.captionTitle = I18n.format("wdl.gui.overwriteChanges.captionTitle", new Object[0]);
        this.captionSubtitle = I18n.format("wdl.gui.overwriteChanges.captionSubtitle", new Object[0]);
        this.overwriteWarning1 = I18n.format("wdl.gui.overwriteChanges.overwriteWarning1", new Object[0]);
        this.overwriteWarning2 = I18n.format("wdl.gui.overwriteChanges.overwriteWarning2", new Object[0]);
        this.backingUpTitle = I18n.format("wdl.gui.overwriteChanges.backingUp.title", new Object[0]);
        this.infoBoxWidth = this.fontRendererObj.getStringWidth(this.overwriteWarning1);
        this.infoBoxHeight = 132;
        if (this.infoBoxWidth < 200) {
            this.infoBoxWidth = 200;
        }
        this.infoBoxY = 48;
        this.infoBoxX = width / 2 - this.infoBoxWidth / 2;
        int x2 = width / 2 - 100;
        int y2 = this.infoBoxY + 22;
        this.backupAsZipButton = new GuiButton(0, x2, y2, I18n.format("wdl.gui.overwriteChanges.asZip.name", new Object[0]));
        this.buttonList.add(this.backupAsZipButton);
        this.backupAsFolderButton = new GuiButton(1, x2, y2 += 22, I18n.format("wdl.gui.overwriteChanges.asFolder.name", new Object[0]));
        this.buttonList.add(this.backupAsFolderButton);
        this.downloadNowButton = new GuiButton(2, x2, y2 += 22, I18n.format("wdl.gui.overwriteChanges.startNow.name", new Object[0]));
        this.buttonList.add(this.downloadNowButton);
        this.cancelButton = new GuiButton(3, x2, y2 += 22, I18n.format("wdl.gui.overwriteChanges.cancel.name", new Object[0]));
        this.buttonList.add(this.cancelButton);
        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (this.backingUp) {
            return;
        }
        if (button.id == 0) {
            this.backingUp = true;
            new BackupThread(true).start();
        }
        if (button.id == 1) {
            this.backingUp = true;
            new BackupThread(false).start();
        }
        if (button.id == 2) {
            WDL.overrideLastModifiedCheck = true;
            this.mc.displayGuiScreen(null);
            WDL.startDownload();
        }
        if (button.id == 3) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.backingUp) {
            this.drawBackground(0);
            GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, this.backingUpTitle, width / 2, height / 4 - 40, 0xFFFFFF);
            GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, this.backupData, width / 2, height / 4 - 10, 0xFFFFFF);
            if (this.backupFile != null) {
                String text = I18n.format("wdl.gui.overwriteChanges.backingUp.progress", this.backupCurrent, this.backupCount, this.backupFile);
                GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, text, width / 2, height / 4 + 10, 0xFFFFFF);
            }
        } else {
            this.drawDefaultBackground();
            Utils.drawBorder(32, 22, 0, 0, height, width);
            GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, this.title, width / 2, 8, 0xFFFFFF);
            GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, this.footer, width / 2, height - 8 - this.fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
            GuiWDLOverwriteChanges.drawRect(this.infoBoxX - 5, this.infoBoxY - 5, this.infoBoxX + this.infoBoxWidth + 5, this.infoBoxY + this.infoBoxHeight + 5, -1342177280);
            GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, this.captionTitle, width / 2, this.infoBoxY, 0xFFFFFF);
            GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, this.captionSubtitle, width / 2, this.infoBoxY + this.fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
            GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, this.overwriteWarning1, width / 2, this.infoBoxY + 115, 0xFFFFFF);
            GuiWDLOverwriteChanges.drawCenteredString(this.fontRendererObj, this.overwriteWarning2, width / 2, this.infoBoxY + 115 + this.fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
            String tooltip = null;
            if (this.backupAsZipButton.isMouseOver()) {
                tooltip = I18n.format("wdl.gui.overwriteChanges.asZip.description", new Object[0]);
            } else if (this.backupAsFolderButton.isMouseOver()) {
                tooltip = I18n.format("wdl.gui.overwriteChanges.asFolder.description", new Object[0]);
            } else if (this.downloadNowButton.isMouseOver()) {
                tooltip = I18n.format("wdl.gui.overwriteChanges.startNow.description", new Object[0]);
            } else if (this.cancelButton.isMouseOver()) {
                tooltip = I18n.format("wdl.gui.overwriteChanges.cancel.description", new Object[0]);
            }
            Utils.drawGuiInfoBox(tooltip, width, height, 48);
        }
    }

    @Override
    public void setNumberOfFiles(int num) {
        this.backupCount = num;
        this.backupCurrent = 0;
    }

    @Override
    public void onNextFile(String name) {
        ++this.backupCurrent;
        this.backupFile = name;
    }

    private class BackupThread
    extends Thread {
        private final DateFormat folderDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        private final boolean zip;

        public BackupThread(boolean zip) {
            this.zip = zip;
        }

        @Override
        public void run() {
            block9: {
                try {
                    try {
                        String backupName = String.valueOf(WDL.getWorldFolderName(WDL.worldName)) + "_" + this.folderDateFormat.format(new Date()) + "_user" + (this.zip ? ".zip" : "");
                        if (this.zip) {
                            GuiWDLOverwriteChanges.this.backupData = I18n.format("wdl.gui.overwriteChanges.backingUp.zip", backupName);
                        } else {
                            GuiWDLOverwriteChanges.this.backupData = I18n.format("wdl.gui.overwriteChanges.backingUp.folder", backupName);
                        }
                        File fromFolder = WDL.saveHandler.getWorldDirectory();
                        File backupFile = new File(fromFolder.getParentFile(), backupName);
                        if (backupFile.exists()) {
                            throw new IOException("Backup target (" + backupFile + ") already exists!");
                        }
                        if (this.zip) {
                            WorldBackup.zipDirectory(fromFolder, backupFile, GuiWDLOverwriteChanges.this);
                            break block9;
                        }
                        WorldBackup.copyDirectory(fromFolder, backupFile, GuiWDLOverwriteChanges.this);
                    }
                    catch (Exception e2) {
                        WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSetUpEntityUI", new Object[0]);
                        GuiWDLOverwriteChanges.this.backingUp = false;
                        WDL.overrideLastModifiedCheck = true;
                        GuiWDLOverwriteChanges.this.mc.displayGuiScreen(null);
                        WDL.startDownload();
                    }
                }
                finally {
                    GuiWDLOverwriteChanges.this.backingUp = false;
                    WDL.overrideLastModifiedCheck = true;
                    GuiWDLOverwriteChanges.this.mc.displayGuiScreen(null);
                    WDL.startDownload();
                }
            }
        }
    }
}

