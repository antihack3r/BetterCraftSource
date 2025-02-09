/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import wdl.gui.LinkEntry;
import wdl.gui.TextEntry;
import wdl.gui.Utils;

class TextList
extends GuiListExtended {
    public final int topMargin;
    public final int bottomMargin;
    private List<GuiListExtended.IGuiListEntry> entries;

    public TextList(Minecraft mc2, int width, int height, int topMargin, int bottomMargin) {
        super(mc2, width, height, topMargin, height - bottomMargin, mc2.fontRendererObj.FONT_HEIGHT + 1);
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
        this.entries = new ArrayList<GuiListExtended.IGuiListEntry>();
    }

    @Override
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    protected int getSize() {
        return this.entries.size();
    }

    @Override
    protected int getScrollBarX() {
        return this.width - 10;
    }

    @Override
    public int getListWidth() {
        return this.width - 18;
    }

    public void addLine(String text) {
        List<String> lines = Utils.wordWrap(text, this.getListWidth());
        for (String line : lines) {
            this.entries.add(new TextEntry(this.mc, line, 0xFFFFFF));
        }
    }

    public void addBlankLine() {
        this.entries.add(new TextEntry(this.mc, "", 0xFFFFFF));
    }

    public void addLinkLine(String text, String URL2) {
        List<String> lines = Utils.wordWrap(text, this.getListWidth());
        for (String line : lines) {
            this.entries.add(new LinkEntry(this.mc, line, URL2));
        }
    }

    public void clearLines() {
        this.entries.clear();
    }
}

