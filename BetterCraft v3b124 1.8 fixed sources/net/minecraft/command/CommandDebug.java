/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug
extends CommandBase {
    private static final Logger logger = LogManager.getLogger();
    private long profileStartTime;
    private int profileStartTick;

    @Override
    public String getCommandName() {
        return "debug";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.debug.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("commands.debug.usage", new Object[0]);
        }
        if (args[0].equals("start")) {
            if (args.length != 1) {
                throw new WrongUsageException("commands.debug.usage", new Object[0]);
            }
            CommandDebug.notifyOperators(sender, (ICommand)this, "commands.debug.start", new Object[0]);
            MinecraftServer.getServer().enableProfiling();
            this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
            this.profileStartTick = MinecraftServer.getServer().getTickCounter();
        } else {
            if (!args[0].equals("stop")) {
                throw new WrongUsageException("commands.debug.usage", new Object[0]);
            }
            if (args.length != 1) {
                throw new WrongUsageException("commands.debug.usage", new Object[0]);
            }
            if (!MinecraftServer.getServer().theProfiler.profilingEnabled) {
                throw new CommandException("commands.debug.notStarted", new Object[0]);
            }
            long i2 = MinecraftServer.getCurrentTimeMillis();
            int j2 = MinecraftServer.getServer().getTickCounter();
            long k2 = i2 - this.profileStartTime;
            int l2 = j2 - this.profileStartTick;
            this.saveProfileResults(k2, l2);
            MinecraftServer.getServer().theProfiler.profilingEnabled = false;
            CommandDebug.notifyOperators(sender, (ICommand)this, "commands.debug.stop", Float.valueOf((float)k2 / 1000.0f), l2);
        }
    }

    private void saveProfileResults(long timeSpan, int tickSpan) {
        File file1 = new File(MinecraftServer.getServer().getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
        file1.getParentFile().mkdirs();
        try {
            FileWriter filewriter = new FileWriter(file1);
            filewriter.write(this.getProfileResults(timeSpan, tickSpan));
            filewriter.close();
        }
        catch (Throwable throwable) {
            logger.error("Could not save profiler results to " + file1, throwable);
        }
    }

    private String getProfileResults(long timeSpan, int tickSpan) {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Profiler Results ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(CommandDebug.getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time span: ").append(timeSpan).append(" ms\n");
        stringbuilder.append("Tick span: ").append(tickSpan).append(" ticks\n");
        stringbuilder.append("// This is approximately ").append(String.format("%.2f", Float.valueOf((float)tickSpan / ((float)timeSpan / 1000.0f)))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.func_147202_a(0, "root", stringbuilder);
        stringbuilder.append("--- END PROFILE DUMP ---\n\n");
        return stringbuilder.toString();
    }

    private void func_147202_a(int p_147202_1_, String p_147202_2_, StringBuilder stringBuilder) {
        List<Profiler.Result> list = MinecraftServer.getServer().theProfiler.getProfilingData(p_147202_2_);
        if (list != null && list.size() >= 3) {
            int i2 = 1;
            while (i2 < list.size()) {
                Profiler.Result profiler$result = list.get(i2);
                stringBuilder.append(String.format("[%02d] ", p_147202_1_));
                int j2 = 0;
                while (j2 < p_147202_1_) {
                    stringBuilder.append(" ");
                    ++j2;
                }
                stringBuilder.append(profiler$result.field_76331_c).append(" - ").append(String.format("%.2f", profiler$result.field_76332_a)).append("%/").append(String.format("%.2f", profiler$result.field_76330_b)).append("%\n");
                if (!profiler$result.field_76331_c.equals("unspecified")) {
                    try {
                        this.func_147202_a(p_147202_1_ + 1, String.valueOf(p_147202_2_) + "." + profiler$result.field_76331_c, stringBuilder);
                    }
                    catch (Exception exception) {
                        stringBuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
                    }
                }
                ++i2;
            }
        }
    }

    private static String getWittyComment() {
        String[] astring = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};
        try {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable var2) {
            return "Witty comment unavailable :(";
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandDebug.getListOfStringsMatchingLastWord(args, "start", "stop") : null;
    }
}

