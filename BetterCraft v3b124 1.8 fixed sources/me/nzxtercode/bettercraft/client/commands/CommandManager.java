/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.commands.Command;
import me.nzxtercode.bettercraft.client.commands.CommandException;
import me.nzxtercode.bettercraft.client.commands.impl.ClearChatCommand;
import me.nzxtercode.bettercraft.client.commands.impl.CompletionCrashCommand;
import me.nzxtercode.bettercraft.client.commands.impl.CopyIPCommand;
import me.nzxtercode.bettercraft.client.commands.impl.GameModeCommand;
import me.nzxtercode.bettercraft.client.commands.impl.GetGeoServerDataCommand;
import me.nzxtercode.bettercraft.client.commands.impl.GiveCommand;
import me.nzxtercode.bettercraft.client.commands.impl.HelpCommand;
import me.nzxtercode.bettercraft.client.commands.impl.IRCCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;

public class CommandManager {
    private static final CommandManager INSTANCE = new CommandManager();
    public List<Command> commands = new ArrayList<Command>();
    public List<String> commandsNames = new ArrayList<String>();
    public String prefix = Config.getInstance().getConfig("Command").get("prefix").getAsString();

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    public void addCommands() {
        this.addCommand(new HelpCommand());
        this.addCommand(new IRCCommand());
        this.addCommand(new CopyIPCommand());
        this.addCommand(new GameModeCommand());
        this.addCommand(new ClearChatCommand());
        this.addCommand(new GetGeoServerDataCommand());
        this.addCommand(new GiveCommand());
        this.addCommand(new CompletionCrashCommand());
    }

    private void addCommand(Command cmd) {
        this.commands.add(cmd);
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public boolean executeCommand(String string) {
        Command command;
        String[] split;
        block4: {
            String raw = string.substring(1);
            split = raw.split(" ");
            if (split.length == 0) {
                return false;
            }
            String cmdName = split[0];
            command = this.commands.stream().filter(cmd -> cmd.match(cmdName)).findFirst().orElse(null);
            try {
                if (command != null) break block4;
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77Command \u00a7f'" + cmdName + "'\u00a77 doesn't exist"));
                return false;
            }
            catch (CommandException e2) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("\u00a7c" + e2.getMessage()));
                return true;
            }
        }
        String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);
        command.run(split[0], args);
        return true;
    }

    public Collection<String> autoComplete(String currCmd) {
        Command currentCommand;
        String raw = currCmd.substring(1);
        String[] split = raw.split(" ");
        ArrayList<String> ret = new ArrayList<String>();
        Command command = currentCommand = split.length >= 1 ? (Command)this.commands.stream().filter(cmd -> cmd.match(split[0])).findFirst().orElse(null) : null;
        if (split.length >= 2 || currentCommand != null && currCmd.endsWith(" ")) {
            if (currentCommand == null) {
                return ret;
            }
            String[] args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, split.length - 1);
            ArrayList autocomplete = currentCommand.autocomplete(args.length + (currCmd.endsWith(" ") ? 1 : 0), args);
            return autocomplete == null ? new ArrayList() : autocomplete;
        }
        if (split.length == 1) {
            for (Command command2 : this.commands) {
                ret.addAll(command2.getNameAndAliases());
            }
            return ret.stream().map(str -> "." + str).filter(str -> str.toLowerCase().startsWith(currCmd.toLowerCase())).collect(Collectors.toList());
        }
        return ret;
    }
}

