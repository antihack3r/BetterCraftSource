/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.views.shared;

import java.util.List;
import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.FolderElement;
import net.labymod.addons.resourcepacks24.gui.elements.LocalPackElement;
import net.labymod.addons.resourcepacks24.gui.elements.basement.DraggableElement;
import net.labymod.addons.resourcepacks24.gui.elements.basement.Element;
import net.labymod.addons.resourcepacks24.gui.elements.basement.PackElement;
import net.labymod.addons.resourcepacks24.gui.views.OfflineView;
import net.labymod.addons.resourcepacks24.gui.views.SelectedView;
import net.labymod.addons.resourcepacks24.gui.views.View;
import net.labymod.addons.resourcepacks24.loader.PackRepositoryLoader;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;

public class SharedView {
    private GuiResourcepacks24 gui;
    public Element hoverElement = null;
    public View hoverView = null;
    public DraggableElement.Dragging draggingElement = null;
    public String lastErrorMessage = null;
    public boolean changes = false;

    public SharedView(GuiResourcepacks24 gui) {
        this.gui = gui;
    }

    public void preRender(int mouseX, int mouseY) {
        this.hoverView = null;
        this.hoverElement = null;
    }

    public void postRender(int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        if (this.draggingElement != null && this.draggingElement.getElement() instanceof PackElement && this.draggingElement.isValid()) {
            this.draggingElement.getElement().draw((double)mouseX - this.draggingElement.getOffsetX(), (double)mouseY - this.draggingElement.getOffsetY(), this.draggingElement.getWidth(), this.draggingElement.getHeight(), mouseX, mouseY);
        }
        if (this.lastErrorMessage != null) {
            int errorWidth = draw.getWidth() / 3;
            List<String> list = draw.listFormattedStringToWidth(this.lastErrorMessage, errorWidth);
            int errorY = draw.getHeight() - 15 - list.size() * 10;
            DrawUtils.drawRect((double)draw.getWidth() / 2.0 - (double)errorWidth / 2.0, (double)(errorY - 5), (double)draw.getWidth() / 2.0 + (double)errorWidth / 2.0, (double)(errorY + list.size() * 10 + 5), Integer.MIN_VALUE);
            for (String line : list) {
                draw.drawCenteredString(String.valueOf(ModColor.cl('c')) + line, draw.getWidth() / 2, errorY);
                errorY += 10;
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.lastErrorMessage = null;
        if (this.hoverElement != null) {
            this.hoverElement.mouseClicked(mouseX, mouseY, mouseButton, this.gui);
            if (this.hoverElement != PackRepositoryLoader.DEFAULT_MINECRAFT && this.hoverElement instanceof LocalPackElement) {
                this.draggingElement = new DraggableElement.Dragging((DraggableElement)this.hoverElement, mouseX, mouseY);
            }
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.draggingElement != null && !this.draggingElement.getElement().getButtonDelete().isMouseOver(mouseX, mouseY)) {
            this.draggingElement.setValid(true);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        try {
            PackRepositoryLoader packLoader = this.gui.getResourcepacks24().getPackLoader();
            if (this.draggingElement != null && this.draggingElement.isValid()) {
                if (this.draggingElement.getElement() instanceof LocalPackElement) {
                    LocalPackElement draggingElement = (LocalPackElement)this.draggingElement.getElement();
                    boolean draggingSelected = packLoader.isSelected(draggingElement);
                    if (this.hoverElement == null) {
                        if (this.hoverView != null) {
                            if (this.hoverView instanceof OfflineView) {
                                if (packLoader.unselectPack(draggingElement)) {
                                    this.changes = true;
                                }
                                if (!packLoader.movePack(draggingElement, null)) {
                                    this.lastErrorMessage = "Unnable to move pack because it's still loaded or it's an pack-directory!";
                                }
                            }
                            if (this.hoverView instanceof SelectedView && packLoader.selectPack(draggingElement)) {
                                this.changes = true;
                            }
                            this.gui.initGui();
                        }
                    } else if (this.hoverElement instanceof LocalPackElement) {
                        LocalPackElement targetElement = (LocalPackElement)this.hoverElement;
                        boolean targetSelected = packLoader.isSelected(targetElement);
                        if (targetSelected && draggingSelected) {
                            packLoader.swap(draggingElement, targetElement);
                        } else if (targetSelected != draggingSelected) {
                            packLoader.swapSelection(targetElement, draggingElement);
                        }
                        this.changes = true;
                    } else if (this.hoverElement instanceof FolderElement) {
                        FolderElement folder = (FolderElement)this.hoverElement;
                        if (!packLoader.movePack(draggingElement, folder)) {
                            this.lastErrorMessage = "Unnable to move pack because it's still loaded or it's an pack-directory!";
                        }
                        if (packLoader.unselectPack(draggingElement)) {
                            this.changes = true;
                        }
                    }
                }
                this.gui.initGui();
            } else if (this.hoverElement != null && this.hoverElement instanceof LocalPackElement) {
                LocalPackElement packElement = (LocalPackElement)this.hoverElement;
                packElement.mouseReleased(mouseX, mouseY, state, this.gui);
                this.changes = true;
            }
            this.draggingElement = null;
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public boolean isInsideView(int mouseX, int mouseY) {
        for (View view : this.gui.getViews()) {
            if (!view.isInsideView(mouseX, mouseY)) continue;
            return true;
        }
        return false;
    }
}

