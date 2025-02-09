/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.listeners;

import java.util.Objects;
import me.nzxtercode.bettercraft.client.events.RenderEvents;
import me.nzxtercode.bettercraft.client.events.types.TypePrePost;
import net.labymod.api.permissions.Permissions;
import net.labymod.main.LabyMod;
import net.labymod.support.MemoryUpgradeGui;
import net.labymod.utils.DrawUtils;
import net.lenni0451.eventapi.events.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class RenderGameOverlayListener {
    @EventTarget
    public void handleRenderEvent(RenderEvents.ToolTip event) {
        if (event.getType() != TypePrePost.PRE) {
            return;
        }
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        LabyMod.getInstance().getServerManager().draw();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        LabyMod.getInstance().getGuiCustomAchievement().updateAchievementWindow();
        Permissions.getPermissionNotifyRenderer().render(draw.getWidth());
        LabyMod.getInstance().getEmoteRegistry().getEmoteSelectorGui().render();
        LabyMod.getInstance().getStickerRegistry().getStickerSelectorGui().render();
        LabyMod.getInstance().getUserManager().getUserActionGui().render();
        if (Objects.requireNonNull(LabyMod.getSettings()).outOfMemoryWarning && Minecraft.getMinecraft().currentScreen == null) {
            MemoryUpgradeGui.renderTickOutOfMemoryDetector();
        }
    }
}

