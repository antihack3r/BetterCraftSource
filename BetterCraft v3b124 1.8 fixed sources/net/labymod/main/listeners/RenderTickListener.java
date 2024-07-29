/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.listeners;

import net.labymod.main.LabyMod;
import net.labymod.settings.PreviewRenderer;
import net.lenni0451.eventapi.events.EventTarget;
import net.lenni0451.eventapi.manager.ASMEventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class RenderTickListener {
    public RenderTickListener() {
        ASMEventManager.register(this);
    }

    @EventTarget
    public void handleEvent(RenderTickListener event) {
        LabyMod.getInstance().getDrawUtils().setScaledResolution(new ScaledResolution(Minecraft.getMinecraft()));
        if (Minecraft.getMinecraft().currentScreen != null) {
            PreviewRenderer.getInstance().createFrame();
        }
    }

    public void drawMenuOverlay(int mouseX, int mouseY, float partialTicks) {
        if (!LabyMod.getInstance().isInGame()) {
            LabyMod.getInstance().getGuiCustomAchievement().updateAchievementWindow();
        }
    }
}

