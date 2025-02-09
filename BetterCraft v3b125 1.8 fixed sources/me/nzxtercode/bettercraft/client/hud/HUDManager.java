/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.hud;

import java.util.ArrayList;
import java.util.List;
import me.nzxtercode.bettercraft.client.hud.HUDConfigScreen;
import me.nzxtercode.bettercraft.client.hud.IRender;
import me.nzxtercode.bettercraft.client.hud.ScreenPosition;
import net.lenni0451.eventapi.manager.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;

public class HUDManager {
    private static HUDManager instance = null;
    private static boolean isPaused;
    private List<IRender> registeredRenderers = new ArrayList<IRender>();
    private Minecraft mc = Minecraft.getMinecraft();

    public static HUDManager getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new HUDManager();
        EventManager.register(instance);
        return instance;
    }

    public void register(IRender ... renderers) {
        IRender[] iRenderArray = renderers;
        int n2 = renderers.length;
        int n3 = 0;
        while (n3 < n2) {
            IRender irender = iRenderArray[n3];
            this.registeredRenderers.add(irender);
            ++n3;
        }
    }

    public void unregister(IRender ... renderers) {
        IRender[] iRenderArray = renderers;
        int n2 = renderers.length;
        int n3 = 0;
        while (n3 < n2) {
            IRender irender = iRenderArray[n3];
            this.registeredRenderers.remove(irender);
            ++n3;
        }
    }

    public List<IRender> getRegisteredRenderers() {
        return this.registeredRenderers;
    }

    public void openConfigScreen() {
        isPaused = false;
        this.mc.displayGuiScreen(new HUDConfigScreen(this, true));
    }

    public void openConfigScreenPaused() {
        isPaused = true;
        this.mc.displayGuiScreen(new HUDConfigScreen(this, false));
    }

    public void render() {
        if ((this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiContainer) && !this.mc.gameSettings.showDebugInfo) {
            for (IRender irender : this.registeredRenderers) {
                this.callRenderer(irender);
            }
        }
    }

    private void callRenderer(IRender renderer) {
        if (renderer.isEnabled()) {
            ScreenPosition screenposition = renderer.load();
            if (screenposition == null) {
                screenposition = ScreenPosition.fromAbsolute(0, 0);
            }
            renderer.render(screenposition);
        }
    }

    public static boolean isPaused() {
        return isPaused;
    }

    public static void setPaused(boolean isPaused) {
        HUDManager.isPaused = isPaused;
    }
}

