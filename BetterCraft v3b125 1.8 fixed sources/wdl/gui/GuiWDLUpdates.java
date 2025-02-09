/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import wdl.WDL;
import wdl.gui.TextList;
import wdl.gui.Utils;
import wdl.update.Release;
import wdl.update.WDLUpdateChecker;

public class GuiWDLUpdates
extends GuiScreen {
    private final GuiScreen parent;
    private static final int TOP_MARGIN = 39;
    private static final int BOTTOM_MARGIN = 32;
    private UpdateList list;
    private GuiButton updateMinecraftVersionButton;
    private GuiButton updateAllowBetasButton;

    public GuiWDLUpdates(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.list = new UpdateList();
        this.updateMinecraftVersionButton = new GuiButton(0, width / 2 - 155, 18, 150, 20, this.getUpdateMinecraftVersionText());
        this.buttonList.add(this.updateMinecraftVersionButton);
        this.updateAllowBetasButton = new GuiButton(1, width / 2 + 5, 18, 150, 20, this.getAllowBetasText());
        this.buttonList.add(this.updateAllowBetasButton);
        this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.cycleUpdateMinecraftVersion();
        }
        if (button.id == 1) {
            this.cycleAllowBetas();
        }
        if (button.id == 100) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void onGuiClosed() {
        WDL.saveGlobalProps();
    }

    private void cycleUpdateMinecraftVersion() {
        String prop = WDL.globalProps.getProperty("UpdateMinecraftVersion");
        if (prop.equals("client")) {
            WDL.globalProps.setProperty("UpdateMinecraftVersion", "server");
        } else if (prop.equals("server")) {
            WDL.globalProps.setProperty("UpdateMinecraftVersion", "any");
        } else {
            WDL.globalProps.setProperty("UpdateMinecraftVersion", "client");
        }
        this.updateMinecraftVersionButton.displayString = this.getUpdateMinecraftVersionText();
    }

    private void cycleAllowBetas() {
        if (WDL.globalProps.getProperty("UpdateAllowBetas").equals("true")) {
            WDL.globalProps.setProperty("UpdateAllowBetas", "false");
        } else {
            WDL.globalProps.setProperty("UpdateAllowBetas", "true");
        }
        this.updateAllowBetasButton.displayString = this.getAllowBetasText();
    }

    private String getUpdateMinecraftVersionText() {
        return I18n.format("wdl.gui.updates.updateMinecraftVersion." + WDL.globalProps.getProperty("UpdateMinecraftVersion"), WDL.getMinecraftVersion());
    }

    private String getAllowBetasText() {
        return I18n.format("wdl.gui.updates.updateAllowBetas." + WDL.globalProps.getProperty("UpdateAllowBetas"), new Object[0]);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.list.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.list.mouseReleased(mouseX, mouseY, state)) {
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.list == null) {
            return;
        }
        this.list.regenerateVersionList();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        if (!WDLUpdateChecker.hasFinishedUpdateCheck()) {
            GuiWDLUpdates.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.updates.pleaseWait", new Object[0]), width / 2, height / 2, 0xFFFFFF);
        } else if (WDLUpdateChecker.hasUpdateCheckFailed()) {
            String reason = WDLUpdateChecker.getUpdateCheckFailReason();
            GuiWDLUpdates.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.updates.checkFailed", new Object[0]), width / 2, height / 2 - this.fontRendererObj.FONT_HEIGHT / 2, 0xFF5555);
            GuiWDLUpdates.drawCenteredString(this.fontRendererObj, I18n.format(reason, new Object[0]), width / 2, height / 2 + this.fontRendererObj.FONT_HEIGHT / 2, 0xFF5555);
        }
        GuiWDLUpdates.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.updates.title", new Object[0]), width / 2, 8, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.updateMinecraftVersionButton.isMouseOver()) {
            Utils.drawGuiInfoBox(I18n.format("wdl.gui.updates.updateMinecraftVersion.description", WDL.getMinecraftVersion()), width, height, 32);
        } else if (this.updateAllowBetasButton.isMouseOver()) {
            Utils.drawGuiInfoBox(I18n.format("wdl.gui.updates.updateAllowBetas.description", new Object[0]), width, height, 32);
        }
    }

    private String buildVersionInfo(Release release) {
        String type = "?";
        String mainVersion = "?";
        String supportedVersions = "?";
        if (release.hiddenInfo != null) {
            type = release.hiddenInfo.loader;
            mainVersion = release.hiddenInfo.mainMinecraftVersion;
            String[] versions = release.hiddenInfo.supportedMinecraftVersions;
            if (versions.length == 1) {
                supportedVersions = I18n.format("wdl.gui.updates.update.version.listSingle", versions[0]);
            } else if (versions.length == 2) {
                supportedVersions = I18n.format("wdl.gui.updates.update.version.listDouble", versions[0], versions[1]);
            } else {
                StringBuilder builder = new StringBuilder();
                int i2 = 0;
                while (i2 < versions.length) {
                    if (i2 == 0) {
                        builder.append(I18n.format("wdl.gui.updates.update.version.listStart", versions[i2]));
                    } else if (i2 == versions.length - 1) {
                        builder.append(I18n.format("wdl.gui.updates.update.version.listEnd", versions[i2]));
                    } else {
                        builder.append(I18n.format("wdl.gui.updates.update.version.listMiddle", versions[i2]));
                    }
                    ++i2;
                }
                supportedVersions = builder.toString();
            }
        }
        return I18n.format("wdl.gui.updates.update.version", type, mainVersion, supportedVersions);
    }

    private String buildReleaseTitle(Release release) {
        String version = release.tag;
        String mcVersion = "?";
        if (release.hiddenInfo != null) {
            mcVersion = release.hiddenInfo.mainMinecraftVersion;
        }
        if (release.prerelease) {
            return I18n.format("wdl.gui.updates.update.title.prerelease", version, mcVersion);
        }
        return I18n.format("wdl.gui.updates.update.title.release", version, mcVersion);
    }

    private class GuiWDLSingleUpdate
    extends GuiScreen {
        private final GuiWDLUpdates parent;
        private final Release release;
        private TextList list;

        public GuiWDLSingleUpdate(GuiWDLUpdates parent, Release releaseToShow) {
            this.parent = parent;
            this.release = releaseToShow;
        }

        @Override
        public void initGui() {
            this.buttonList.add(new GuiButton(0, width / 2 - 155, 18, 150, 20, I18n.format("wdl.gui.updates.update.viewOnline", new Object[0])));
            if (this.release.hiddenInfo != null) {
                this.buttonList.add(new GuiButton(1, width / 2 + 5, 18, 150, 20, I18n.format("wdl.gui.updates.update.viewForumPost", new Object[0])));
            }
            this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
            this.list = new TextList(this.mc, width, height, 39, 32);
            this.list.addLine(GuiWDLUpdates.this.buildReleaseTitle(this.release));
            this.list.addLine(I18n.format("wdl.gui.updates.update.releaseDate", this.release.date));
            this.list.addLine(GuiWDLUpdates.this.buildVersionInfo(this.release));
            this.list.addBlankLine();
            this.list.addLine(this.release.textOnlyBody);
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 0) {
                Utils.openLink(this.release.URL);
            }
            if (button.id == 1) {
                Utils.openLink(this.release.hiddenInfo.post);
            }
            if (button.id == 100) {
                this.mc.displayGuiScreen(this.parent);
            }
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            this.list.mouseClicked(mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            this.list.handleMouseInput();
        }

        @Override
        protected void mouseReleased(int mouseX, int mouseY, int state) {
            if (this.list.mouseReleased(mouseX, mouseY, state)) {
                return;
            }
            super.mouseReleased(mouseX, mouseY, state);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.list == null) {
                return;
            }
            this.list.drawScreen(mouseX, mouseY, partialTicks);
            GuiWDLSingleUpdate.drawCenteredString(this.fontRendererObj, GuiWDLUpdates.this.buildReleaseTitle(this.release), width / 2, 8, 0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    private class UpdateList
    extends GuiListExtended {
        private List<VersionEntry> displayedVersions;
        private Release recomendedRelease;

        public UpdateList() {
            super(GuiWDLUpdates.this.mc, width, height, 39, height - 32, (((GuiWDLUpdates)GuiWDLUpdates.this).fontRendererObj.FONT_HEIGHT + 1) * 6 + 2);
            this.showSelectionBox = true;
        }

        private void regenerateVersionList() {
            this.displayedVersions = new ArrayList<VersionEntry>();
            this.recomendedRelease = WDLUpdateChecker.hasNewVersion() ? WDLUpdateChecker.getRecomendedRelease() : null;
            List<Release> releases = WDLUpdateChecker.getReleases();
            if (releases == null) {
                return;
            }
            for (Release release : releases) {
                this.displayedVersions.add(new VersionEntry(release));
            }
        }

        @Override
        public VersionEntry getListEntry(int index) {
            return this.displayedVersions.get(index);
        }

        @Override
        protected int getSize() {
            return this.displayedVersions.size();
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            VersionEntry entry = this.getListEntry(slotIndex);
            return "1.8.9a-beta2".equals(((VersionEntry)entry).release.tag);
        }

        @Override
        public int getListWidth() {
            return this.width - 30;
        }

        @Override
        protected int getScrollBarX() {
            return this.width - 10;
        }

        private class VersionEntry
        implements GuiListExtended.IGuiListEntry {
            private final Release release;
            private String title;
            private String caption;
            private String body1;
            private String body2;
            private String body3;
            private String time;
            private final int fontHeight;

            public VersionEntry(Release release) {
                this.release = release;
                this.fontHeight = ((GuiWDLUpdates)((UpdateList)UpdateList.this).GuiWDLUpdates.this).fontRendererObj.FONT_HEIGHT + 1;
                this.title = GuiWDLUpdates.this.buildReleaseTitle(release);
                this.caption = GuiWDLUpdates.this.buildVersionInfo(release);
                List<String> body = Utils.wordWrap(release.textOnlyBody, UpdateList.this.getListWidth());
                this.body1 = body.size() >= 1 ? body.get(0) : "";
                this.body2 = body.size() >= 2 ? body.get(1) : "";
                this.body3 = body.size() >= 3 ? body.get(2) : "";
                this.time = I18n.format("wdl.gui.updates.update.releaseDate", release.date);
            }

            @Override
            public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                String title = UpdateList.this.isSelected(slotIndex) ? I18n.format("wdl.gui.updates.currentVersion", this.title) : (this.release == UpdateList.this.recomendedRelease ? I18n.format("wdl.gui.updates.recomendedVersion", this.title) : this.title);
                GuiWDLUpdates.this.fontRendererObj.drawString(title, x2, y2 + this.fontHeight * 0, 0xFFFFFF);
                GuiWDLUpdates.this.fontRendererObj.drawString(this.caption, x2, y2 + this.fontHeight * 1, 0x808080);
                GuiWDLUpdates.this.fontRendererObj.drawString(this.body1, x2, y2 + this.fontHeight * 2, 0xFFFFFF);
                GuiWDLUpdates.this.fontRendererObj.drawString(this.body2, x2, y2 + this.fontHeight * 3, 0xFFFFFF);
                GuiWDLUpdates.this.fontRendererObj.drawString(this.body3, x2, y2 + this.fontHeight * 4, 0xFFFFFF);
                GuiWDLUpdates.this.fontRendererObj.drawString(this.time, x2, y2 + this.fontHeight * 5, 0x808080);
                if (mouseX > x2 && mouseX < x2 + listWidth && mouseY > y2 && mouseY < y2 + slotHeight) {
                    GuiWDLUpdates.drawRect(x2 - 2, y2 - 2, x2 + listWidth - 3, y2 + slotHeight + 2, 0x1FFFFFFF);
                }
            }

            @Override
            public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
                if (relativeY > 0 && relativeY < UpdateList.this.slotHeight) {
                    UpdateList.this.mc.displayGuiScreen(new GuiWDLSingleUpdate(GuiWDLUpdates.this, this.release));
                    UpdateList.this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                    return true;
                }
                return false;
            }

            @Override
            public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
            }

            @Override
            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }
        }
    }
}

