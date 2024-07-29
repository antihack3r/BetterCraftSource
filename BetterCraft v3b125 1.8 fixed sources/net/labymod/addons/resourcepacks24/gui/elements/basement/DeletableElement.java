/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.elements.basement;

import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.FolderElement;
import net.labymod.addons.resourcepacks24.gui.elements.LocalPackElement;
import net.labymod.addons.resourcepacks24.gui.elements.basement.Element;
import net.labymod.addons.resourcepacks24.loader.PackRepositoryLoader;
import net.labymod.gui.elements.GuiImageButton;
import net.labymod.main.ModTextures;

public abstract class DeletableElement
extends Element {
    protected GuiImageButton buttonDelete;
    protected boolean deleteable;

    public DeletableElement(boolean deleteable) {
        this.deleteable = deleteable;
        this.buttonDelete = new GuiImageButton(ModTextures.MISC_BLOCKED);
        this.buttonDelete.setImageAlpha(0.3f);
    }

    protected abstract String getElementName();

    public void drawControls(double x2, double y2, double width, double height, boolean isFirstEntry, boolean isLastEntry, boolean isSelected, int mouseX, int mouseY, GuiResourcepacks24 gui) {
        if (!this.deleteable) {
            return;
        }
        if (gui.getSharedView().draggingElement == null) {
            this.buttonDelete.draw(x2 + width - 10.0 - 5.0, y2, 10.0, mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton, GuiResourcepacks24 gui) {
        super.mouseClicked(mouseX, mouseY, mouseButton, gui);
        if (!this.buttonDelete.isMouseOver(mouseX, mouseY)) {
            return;
        }
        this.delete(gui);
    }

    private void delete(GuiResourcepacks24 gui) {
        PackRepositoryLoader packLoader = gui.getResourcepacks24().getPackLoader();
        if (this instanceof FolderElement) {
            FolderElement packFolder = (FolderElement)this;
            for (LocalPackElement packEntry : gui.getRepository().getRepository()) {
                if (!packFolder.contains(packEntry)) continue;
                packLoader.movePack(packEntry, null);
            }
            if (packFolder.getDirectory().delete()) {
                gui.getResourcepacks24().getPackLoader().getMinecraftRepositoryHandler().saveResourceList();
                gui.reloadRepositories();
            } else {
                gui.getSharedView().lastErrorMessage = "Can't delete folder because something in there is still loaded";
            }
        }
        if (this instanceof LocalPackElement) {
            LocalPackElement element = (LocalPackElement)this;
            packLoader.getMinecraftRepositoryHandler().unloadUnselectedPacks();
            if (element.getPackMeta().file.delete()) {
                gui.reloadRepositories();
            } else {
                gui.getSharedView().lastErrorMessage = "Can't delete pack because it's still loaded";
            }
        }
    }

    public GuiImageButton getButtonDelete() {
        return this.buttonDelete;
    }
}

