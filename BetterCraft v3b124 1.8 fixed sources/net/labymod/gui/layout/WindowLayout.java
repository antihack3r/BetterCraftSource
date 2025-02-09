/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.labymod.gui.layout.WindowElement;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public abstract class WindowLayout
extends GuiScreen {
    protected List<WindowElement<?>> windowElements = new ArrayList();

    @Override
    public void initGui() {
        this.buttonList.clear();
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(20, width - 55, 5, 50, 20, LanguageManager.translate("account")));
        this.initLayout();
        LabyMod.getInstance().getLabyConnect().setViaServerList(false);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void initLayout() {
        ArrayList windowElements = new ArrayList();
        this.initLayout(windowElements);
        this.windowElements = windowElements;
    }

    protected abstract void initLayout(List<WindowElement<?>> var1);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (WindowElement<?> element : this.windowElements) {
            element.draw(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (WindowElement<?> element : this.windowElements) {
            element.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        for (WindowElement<?> element : this.windowElements) {
            element.actionPerformed(button);
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (WindowElement<?> element : this.windowElements) {
            element.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        for (WindowElement<?> element : this.windowElements) {
            element.mouseClickMove(mouseX, mouseY);
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (WindowElement<?> element : this.windowElements) {
            element.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        for (WindowElement<?> element : this.windowElements) {
            element.mouseInput();
        }
        super.handleMouseInput();
    }

    @Override
    public void updateScreen() {
        for (WindowElement<?> element : this.windowElements) {
            element.updateScreen();
        }
        super.updateScreen();
    }

    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }
}

