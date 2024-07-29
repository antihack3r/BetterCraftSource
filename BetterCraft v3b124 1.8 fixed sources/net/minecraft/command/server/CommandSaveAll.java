/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll
extends CommandBase {
    @Override
    public String getCommandName() {
        return "save-all";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.save.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        MinecraftServer minecraftserver = MinecraftServer.getServer();
        sender.addChatMessage(new ChatComponentTranslation("commands.save.start", new Object[0]));
        if (minecraftserver.getConfigurationManager() != null) {
            minecraftserver.getConfigurationManager().saveAllPlayerData();
        }
        try {
            int i2 = 0;
            while (i2 < minecraftserver.worldServers.length) {
                if (minecraftserver.worldServers[i2] != null) {
                    WorldServer worldserver = minecraftserver.worldServers[i2];
                    boolean flag = worldserver.disableLevelSaving;
                    worldserver.disableLevelSaving = false;
                    worldserver.saveAllChunks(true, null);
                    worldserver.disableLevelSaving = flag;
                }
                ++i2;
            }
            if (args.length > 0 && "flush".equals(args[0])) {
                sender.addChatMessage(new ChatComponentTranslation("commands.save.flushStart", new Object[0]));
                int j2 = 0;
                while (j2 < minecraftserver.worldServers.length) {
                    if (minecraftserver.worldServers[j2] != null) {
                        WorldServer worldserver1 = minecraftserver.worldServers[j2];
                        boolean flag1 = worldserver1.disableLevelSaving;
                        worldserver1.disableLevelSaving = false;
                        worldserver1.saveChunkData();
                        worldserver1.disableLevelSaving = flag1;
                    }
                    ++j2;
                }
                sender.addChatMessage(new ChatComponentTranslation("commands.save.flushEnd", new Object[0]));
            }
        }
        catch (MinecraftException minecraftexception) {
            CommandSaveAll.notifyOperators(sender, (ICommand)this, "commands.save.failed", minecraftexception.getMessage());
            return;
        }
        CommandSaveAll.notifyOperators(sender, (ICommand)this, "commands.save.success", new Object[0]);
    }
}

