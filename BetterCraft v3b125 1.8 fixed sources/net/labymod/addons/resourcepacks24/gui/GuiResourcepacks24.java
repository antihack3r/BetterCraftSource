/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.labymod.addons.resourcepacks24.Resourcepacks24;
import net.labymod.addons.resourcepacks24.gui.views.OfflineView;
import net.labymod.addons.resourcepacks24.gui.views.OnlineView;
import net.labymod.addons.resourcepacks24.gui.views.SelectedView;
import net.labymod.addons.resourcepacks24.gui.views.View;
import net.labymod.addons.resourcepacks24.gui.views.shared.SharedView;
import net.labymod.addons.resourcepacks24.loader.PackRepositoryLoader;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiResourcepacks24
extends GuiScreen {
    private GuiScreen lastScreen;
    private Resourcepacks24 resourcepacks24;
    public PackRepositoryLoader.Repository repository = new PackRepositoryLoader.Repository();
    private SharedView sharedView = new SharedView(this);
    private List<View> views = new ArrayList<View>();
    private GuiButton buttonApply;

    public GuiResourcepacks24(GuiScreen lastScreen, Resourcepacks24 resourcepacks24) {
        this.lastScreen = lastScreen;
        this.resourcepacks24 = resourcepacks24;
        this.views.add(new OnlineView(this));
        this.views.add(new OfflineView(this));
        this.views.add(new SelectedView(this));
        this.reloadRepositories();
    }

    public void reloadRepositories() {
        for (View view : this.views) {
            view.loadRepository(this.resourcepacks24);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.repository = this.resourcepacks24.getPackLoader().getRepository().clone();
        double margin = 10.0;
        double panelWidth = (int)(((double)width - 40.0) / 3.0);
        int index = 0;
        for (View view : this.views) {
            view.init(10.0 + (double)index * (panelWidth + 10.0), 24.0, panelWidth, (double)height - 20.0 - 14.0 - (double)(index != 1 ? (index == 0 ? 10 : 25) : 0), 10.0);
            ++index;
        }
        double buttonWidth = panelWidth / 2.0 - 10.0;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, (int)((double)width - 10.0 - panelWidth), height - 30, (int)buttonWidth, 20, "Cancel"));
        this.buttonApply = new GuiButton(1, (int)((double)width - 10.0 - buttonWidth), height - 30, (int)buttonWidth, 20, "Apply");
        this.buttonList.add(this.buttonApply);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.sharedView.preRender(mouseX, mouseY);
        for (View view : this.views) {
            DrawUtils draw = LabyMod.getInstance().getDrawUtils();
            DrawUtils.startScissor((float)view.x, (float)view.y, (float)(view.x + view.width), (float)(view.y + view.height));
            view.renderPre(mouseX, mouseY);
            DrawUtils.stopScissor();
            view.renderPost(mouseX, mouseY);
        }
        this.sharedView.postRender(mouseX, mouseY);
        this.buttonApply.hovered = this.sharedView.changes;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0: {
                this.resourcepacks24.getRp24Api().getDynamicIconManager().unloadAll();
                this.resourcepacks24.getPackLoader().loadAsync();
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
                break;
            }
            case 1: {
                this.resourcepacks24.getRp24Api().getDynamicIconManager().unloadAll();
                this.resourcepacks24.getPackLoader().getMinecraftRepositoryHandler().saveResourceList();
                this.resourcepacks24.getPackLoader().getMinecraftRepositoryHandler().refreshResources();
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.resourcepacks24.getRp24Api().getDynamicIconManager().unloadAll();
            this.resourcepacks24.getPackLoader().loadAsync();
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.sharedView.mouseClicked(mouseX, mouseY, mouseButton);
        for (View view : this.views) {
            view.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.sharedView.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (View view : this.views) {
            view.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.sharedView.mouseReleased(mouseX, mouseY, state);
        for (View view : this.views) {
            view.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        for (View view : this.views) {
            view.handleMouseInput();
        }
    }

    public GuiScreen getLastScreen() {
        return this.lastScreen;
    }

    public Resourcepacks24 getResourcepacks24() {
        return this.resourcepacks24;
    }

    public PackRepositoryLoader.Repository getRepository() {
        return this.repository;
    }

    public SharedView getSharedView() {
        return this.sharedView;
    }

    public List<View> getViews() {
        return this.views;
    }

    public GuiButton getButtonApply() {
        return this.buttonApply;
    }
}

