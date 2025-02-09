// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.command.server;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;

public class CommandListPlayers extends CommandBase
{
    @Override
    public String getCommandName() {
        return "list";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.players.usage";
    }
    
    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        final int i = server.getCurrentPlayerCount();
        sender.addChatMessage(new TextComponentTranslation("commands.players.list", new Object[] { i, server.getMaxPlayers() }));
        sender.addChatMessage(new TextComponentString(server.getPlayerList().getFormattedListOfPlayers(args.length > 0 && "uuids".equalsIgnoreCase(args[0]))));
        sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, i);
    }
}
