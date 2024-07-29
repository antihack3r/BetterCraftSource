/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import wdl.WorldBackup;
import wdl.gui.GuiTurningCameraBase;
import wdl.gui.Utils;

public class GuiWDLSaveProgress
extends GuiTurningCameraBase
implements WorldBackup.IBackupProgressMonitor {
    private final String title;
    private volatile String majorTaskMessage = "";
    private volatile String minorTaskMessage = "";
    private volatile int majorTaskNumber;
    private final int majorTaskCount;
    private volatile int minorTaskProgress;
    private volatile int minorTaskMaximum;
    private volatile boolean doneWorking = false;

    public GuiWDLSaveProgress(String title, int taskCount) {
        this.title = title;
        this.majorTaskCount = taskCount;
        this.majorTaskNumber = 0;
    }

    public void startMajorTask(String message, int minorTaskMaximum) {
        this.majorTaskMessage = message;
        ++this.majorTaskNumber;
        this.minorTaskMessage = "";
        this.minorTaskProgress = 0;
        this.minorTaskMaximum = minorTaskMaximum;
    }

    public void setMinorTaskProgress(String message, int progress) {
        this.minorTaskMessage = message;
        this.minorTaskProgress = progress;
    }

    public void setMinorTaskProgress(int progress) {
        this.minorTaskProgress = progress;
    }

    public void setDoneWorking() {
        this.doneWorking = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.doneWorking) {
            this.mc.displayGuiScreen(null);
        } else {
            Utils.drawBorder(32, 32, 0, 0, height, width);
            String majorTaskInfo = this.majorTaskMessage;
            if (this.majorTaskCount > 1) {
                majorTaskInfo = I18n.format("wdl.gui.saveProgress.progressInfo", this.majorTaskMessage, this.majorTaskNumber, this.majorTaskCount);
            }
            String minorTaskInfo = this.minorTaskMessage;
            if (this.minorTaskMaximum > 1) {
                majorTaskInfo = I18n.format("wdl.gui.saveProgress.progressInfo", this.minorTaskMessage, this.minorTaskProgress, this.minorTaskMaximum);
            }
            GuiWDLSaveProgress.drawCenteredString(this.fontRendererObj, this.title, width / 2, 8, 0xFFFFFF);
            GuiWDLSaveProgress.drawCenteredString(this.fontRendererObj, majorTaskInfo, width / 2, 100, 0xFFFFFF);
            if (this.minorTaskMaximum > 0) {
                this.drawProgressBar(110, 84, 89, this.majorTaskNumber * this.minorTaskMaximum + this.minorTaskProgress, (this.majorTaskCount + 1) * this.minorTaskMaximum);
            } else {
                this.drawProgressBar(110, 84, 89, this.majorTaskNumber, this.majorTaskCount);
            }
            GuiWDLSaveProgress.drawCenteredString(this.fontRendererObj, minorTaskInfo, width / 2, 130, 0xFFFFFF);
            this.drawProgressBar(140, 64, 69, this.minorTaskProgress, this.minorTaskMaximum);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    private void drawProgressBar(int y2, int emptyV, int filledV, int progress, int maximum) {
        if (maximum == 0) {
            return;
        }
        this.mc.getTextureManager().bindTexture(Gui.icons);
        int fullWidth = 182;
        int currentWidth = progress * 182 / maximum;
        int height = 5;
        int x2 = width / 2 - 91;
        boolean u2 = false;
        this.drawTexturedModalRect(x2, y2, 0, emptyV, 182, 5);
        this.drawTexturedModalRect(x2, y2, 0, filledV, currentWidth, 5);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void setNumberOfFiles(int num) {
        this.minorTaskMaximum = num;
    }

    @Override
    public void onNextFile(String name) {
        ++this.minorTaskProgress;
        this.minorTaskMessage = I18n.format("wdl.saveProgress.backingUp.file", name);
    }
}

