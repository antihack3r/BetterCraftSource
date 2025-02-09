/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.util.ChatComponentText;

public class CopyIPCommand
extends Command {
    private String tocopy;

    public CopyIPCommand() {
        super("copyip", "ip");
    }

    @Override
    public void run(String alias, String[] args) {
        if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
            try {
                ServerAddress serveradress = ServerAddress.resolveAddress(Minecraft.getMinecraft().getCurrentServerData().serverIP);
                String address = String.valueOf(InetAddress.getByName(serveradress.getIP()).getHostAddress()) + " " + serveradress.getPort();
                GuiScreen.setClipboardString(address);
                this.tocopy = address;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "IP was copied in clipboard: \u00a7f" + this.tocopy));
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }
}

