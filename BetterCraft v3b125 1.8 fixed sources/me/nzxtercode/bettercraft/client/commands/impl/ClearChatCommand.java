/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands.impl;

import java.util.ArrayList;
import java.util.List;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;

public class ClearChatCommand
extends Command {
    private String tocopy;

    public ClearChatCommand() {
        super("cc", "clearchat");
    }

    @Override
    public void run(String alias, String[] args) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages();
        GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Chat cleared"));
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }
}

