/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.views;

import java.util.List;
import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.LocalPackElement;
import net.labymod.addons.resourcepacks24.gui.views.View;
import net.labymod.addons.resourcepacks24.loader.PackRepositoryLoader;

public class SelectedView
extends View {
    public SelectedView(GuiResourcepacks24 gui) {
        super(gui, "Selected packs");
    }

    @Override
    public void renderPre(int mouseX, int mouseY) {
        super.renderPre(mouseX, mouseY);
        if (!this.loaded) {
            return;
        }
        double entryWidth = this.width - (double)(this.scrollbar.isHidden() ? 0 : 5);
        double entryHeight = 32.0;
        double listY = this.y + 3.0 + this.scrollbar.getScrollY();
        List<LocalPackElement> entries = this.gui.getRepository().getSelected();
        int total = entries.size();
        int index = total - 1;
        while (index >= 0) {
            this.drawPack(entries.get(index), this.x, listY, entryWidth, 32.0, true, total - index - 1, total, mouseX, mouseY);
            listY += 35.0;
            --index;
        }
        this.drawPack(PackRepositoryLoader.DEFAULT_MINECRAFT, this.x, listY, this.width, 32.0, true, -1, -1, mouseX, mouseY);
        this.postRenderCursorY = listY - this.scrollbar.getScrollY();
    }
}

