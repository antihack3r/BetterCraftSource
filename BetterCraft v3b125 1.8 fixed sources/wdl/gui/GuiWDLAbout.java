/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import wdl.WDL;
import wdl.gui.GuiWDLExtensions;
import wdl.gui.TextList;

public class GuiWDLAbout
extends GuiScreen {
    private final GuiScreen parent;
    private static final String FORUMS_THREAD = "http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2520465";
    private static final String COREMOD_GITHUB = "https://github.com/Pokechu22/WorldDownloader";
    private static final String LITEMOD_GITHUB = "https://github.com/uyjulian/LiteModWDL/";
    private TextList list;

    public GuiWDLAbout(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, width / 2 - 155, 18, 150, 20, I18n.format("wdl.gui.about.extensions", new Object[0])));
        this.buttonList.add(new GuiButton(1, width / 2 + 5, 18, 150, 20, I18n.format("wdl.gui.about.debugInfo", new Object[0])));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
        String wdlVersion = "1.8.9a-beta2";
        String mcVersion = WDL.getMinecraftVersionInfo();
        this.list = new TextList(this.mc, width, height, 39, 32);
        this.list.addLine(I18n.format("wdl.gui.about.blurb", new Object[0]));
        this.list.addBlankLine();
        this.list.addLine(I18n.format("wdl.gui.about.version", wdlVersion, mcVersion));
        this.list.addBlankLine();
        String currentLanguage = WDL.minecraft.getLanguageManager().getCurrentLanguage().toString();
        String translatorCredit = I18n.format("wdl.translatorCredit", currentLanguage);
        if (translatorCredit != null && !translatorCredit.isEmpty()) {
            this.list.addLine(translatorCredit);
            this.list.addBlankLine();
        }
        this.list.addLinkLine(I18n.format("wdl.gui.about.forumThread", new Object[0]), FORUMS_THREAD);
        this.list.addBlankLine();
        this.list.addLinkLine(I18n.format("wdl.gui.about.coremodSrc", new Object[0]), COREMOD_GITHUB);
        this.list.addBlankLine();
        this.list.addLinkLine(I18n.format("wdl.gui.about.litemodSrc", new Object[0]), LITEMOD_GITHUB);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiWDLExtensions(this));
        } else if (button.id == 1) {
            GuiWDLAbout.setClipboardString(WDL.getDebugInfo());
            button.displayString = I18n.format("wdl.gui.about.debugInfo.copied", new Object[0]);
        } else if (button.id == 2) {
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
        super.drawScreen(mouseX, mouseY, partialTicks);
        GuiWDLAbout.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.about.title", new Object[0]), width / 2, 2, 0xFFFFFF);
    }
}

