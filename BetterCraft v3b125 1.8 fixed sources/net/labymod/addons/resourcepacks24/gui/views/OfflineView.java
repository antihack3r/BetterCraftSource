/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.views;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import net.labymod.addons.resourcepacks24.Resourcepacks24;
import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.FolderElement;
import net.labymod.addons.resourcepacks24.gui.elements.LocalPackElement;
import net.labymod.addons.resourcepacks24.gui.views.SelectedView;
import net.labymod.addons.resourcepacks24.gui.views.View;
import net.labymod.addons.resourcepacks24.loader.PackRepositoryLoader;
import net.labymod.gui.elements.GuiImageButton;
import net.labymod.gui.elements.GuiTextboxPrompt;
import net.labymod.utils.Consumer;
import net.minecraft.client.Minecraft;

public class OfflineView
extends View {
    private PackRepositoryLoader loader;
    private GuiImageButton buttonCreateFolder;
    private GuiImageButton buttonOpenResourceFolder;

    public OfflineView(GuiResourcepacks24 gui) {
        super(gui, "Your library");
        this.loader = gui.getResourcepacks24().getPackLoader();
        this.buttonCreateFolder = new GuiImageButton(FolderElement.TEXTURE_FOLDER, 0, 127, 85, 127);
        this.buttonOpenResourceFolder = new GuiImageButton(FolderElement.TEXTURE_FOLDER, 170, 0, 85, 127);
    }

    @Override
    public void loadRepository(Resourcepacks24 context) {
        this.loaded = false;
        context.getPackLoader().loadAsync(new java.util.function.Consumer<PackRepositoryLoader.Repository>(){

            @Override
            public void accept(PackRepositoryLoader.Repository repo) {
                OfflineView.this.gui.repository = repo.clone();
                OfflineView.this.gui.initGui();
                for (View view : OfflineView.this.gui.getViews()) {
                    view.onLoaded(OfflineView.class);
                    view.onLoaded(SelectedView.class);
                }
            }
        });
    }

    @Override
    public void init(double x2, double y2, double width, double height, double margin) {
        super.init(x2, y2, width, height, margin);
        this.buttonCreateFolder.init(x2 + 2.0, y2 - 12.0, 10.0);
        this.buttonOpenResourceFolder.init(x2 + width - 10.0 - 2.0, y2 - 12.0, 10.0);
    }

    @Override
    public void renderPre(int mouseX, int mouseY) {
        super.renderPre(mouseX, mouseY);
        if (!this.loaded) {
            return;
        }
        double entryWidth = this.width - (double)(this.scrollbar.isHidden() ? 0 : 5);
        double entryHeight = entryWidth / 7.0;
        double listY = this.y + 3.0 + this.scrollbar.getScrollY();
        PackRepositoryLoader.Repository repository = this.gui.getRepository();
        for (FolderElement subFolder : repository.getSubFolders()) {
            int offsetX = 0;
            int folderIconSize = 10;
            if (subFolder.draw(this.x, listY, entryWidth, 10.0, mouseX, mouseY)) {
                subFolder.drawControls(this.x, listY, this.width, this.height, false, false, false, mouseX, mouseY, this.gui);
                this.shared.hoverElement = subFolder;
            }
            listY += 13.0;
            offsetX += 13;
            if (!subFolder.isExpanded()) continue;
            for (LocalPackElement element : repository.getRepository()) {
                if (!subFolder.contains(element) || this.loader.isSelected(element)) continue;
                this.drawPack(element, this.x + (double)offsetX, listY, entryWidth - (double)offsetX, entryHeight, mouseX, mouseY);
                listY += entryHeight + 3.0;
            }
        }
        for (LocalPackElement element2 : repository.getRootPacks()) {
            if (this.loader.isSelected(element2)) continue;
            this.drawPack(element2, this.x, listY, entryWidth, entryHeight, mouseX, mouseY);
            listY += entryHeight + 3.0;
        }
        this.postRenderCursorY = listY - this.scrollbar.getScrollY();
    }

    @Override
    public void renderPost(int mouseX, int mouseY) {
        super.renderPost(mouseX, mouseY);
        this.buttonCreateFolder.draw(mouseX, mouseY);
        this.buttonOpenResourceFolder.draw(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.buttonCreateFolder.isMouseOver(mouseX, mouseY)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiTextboxPrompt(this.gui, "Folder name", "Create", "Cancel", "", new Consumer<String>(){

                @Override
                public void accept(String accepted) {
                    if (accepted.isEmpty()) {
                        return;
                    }
                    File target = new File(OfflineView.this.loader.getDirectory(), accepted);
                    if (!target.exists()) {
                        if (target.mkdir()) {
                            OfflineView.this.gui.getResourcepacks24().getPackLoader().getMinecraftRepositoryHandler().saveResourceList();
                            OfflineView.this.gui.reloadRepositories();
                        } else {
                            OfflineView.this.throwError("Can't create folder " + accepted + "!");
                        }
                    } else {
                        OfflineView.this.throwError("Folder " + accepted + " already exists!");
                    }
                }
            }));
        }
        if (this.buttonOpenResourceFolder.isMouseOver(mouseX, mouseY)) {
            try {
                Desktop.getDesktop().open(Resourcepacks24.getInstance().resourcepacksDir);
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        this.pendingScrollbarUpdate = true;
    }
}

