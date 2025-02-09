/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tools.autotext;

import me.nzxtercode.bettercraft.client.events.ClientTickEvent;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.tools.autotext.AutoTextKeyBinds;
import net.labymod.main.LabyMod;
import net.lenni0451.eventapi.events.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

public class AutoTextListener {
    @EventTarget
    public void handleEvent(ClientTickEvent event) {
        if (Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        if (!LabyMod.getInstance().getServerManager().isAllowed(Permissions.Permission.CHAT)) {
            return;
        }
        for (AutoTextKeyBinds.AutoText keybind : LabyMod.getInstance().getChatToolManager().getAutoTextKeyBinds()) {
            if (keybind.isAvailable() && keybind.isPressed()) {
                keybind.setAvailable(false);
                if (!keybind.isSendNotInstantly()) {
                    boolean cancelled = false;
                    for (MessageSendEvent messageSend : LabyMod.getInstance().getEventManager().getMessageSend()) {
                        if (!messageSend.onSend(keybind.getMessage()) || cancelled) continue;
                        cancelled = true;
                    }
                    if (!cancelled) {
                        LabyModCore.getMinecraft().getPlayer().sendChatMessage(keybind.getMessage());
                    }
                } else {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiChat(keybind.getMessage()));
                }
            }
            if (keybind.isPressed() || keybind.isAvailable()) continue;
            keybind.setAvailable(true);
        }
    }
}

