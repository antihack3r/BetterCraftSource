// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import javax.annotation.Nullable;
import io.netty.buffer.ByteBuf;
import java.util.Date;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.crash.CrashReport;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.command.CommandResultStats;
import net.minecraft.util.text.ITextComponent;
import java.text.SimpleDateFormat;
import net.minecraft.command.ICommandSender;

public abstract class CommandBlockBaseLogic implements ICommandSender
{
    private static final SimpleDateFormat TIMESTAMP_FORMAT;
    private long field_193041_b;
    private boolean field_193042_c;
    private int successCount;
    private boolean trackOutput;
    private ITextComponent lastOutput;
    private String commandStored;
    private String customName;
    private final CommandResultStats resultStats;
    
    static {
        TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");
    }
    
    public CommandBlockBaseLogic() {
        this.field_193041_b = -1L;
        this.field_193042_c = true;
        this.trackOutput = true;
        this.commandStored = "";
        this.customName = "@";
        this.resultStats = new CommandResultStats();
    }
    
    public int getSuccessCount() {
        return this.successCount;
    }
    
    public void setSuccessCount(final int successCountIn) {
        this.successCount = successCountIn;
    }
    
    public ITextComponent getLastOutput() {
        return (this.lastOutput == null) ? new TextComponentString("") : this.lastOutput;
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound p_189510_1_) {
        p_189510_1_.setString("Command", this.commandStored);
        p_189510_1_.setInteger("SuccessCount", this.successCount);
        p_189510_1_.setString("CustomName", this.customName);
        p_189510_1_.setBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            p_189510_1_.setString("LastOutput", ITextComponent.Serializer.componentToJson(this.lastOutput));
        }
        p_189510_1_.setBoolean("UpdateLastExecution", this.field_193042_c);
        if (this.field_193042_c && this.field_193041_b > 0L) {
            p_189510_1_.setLong("LastExecution", this.field_193041_b);
        }
        this.resultStats.writeStatsToNBT(p_189510_1_);
        return p_189510_1_;
    }
    
    public void readDataFromNBT(final NBTTagCompound nbt) {
        this.commandStored = nbt.getString("Command");
        this.successCount = nbt.getInteger("SuccessCount");
        if (nbt.hasKey("CustomName", 8)) {
            this.customName = nbt.getString("CustomName");
        }
        if (nbt.hasKey("TrackOutput", 1)) {
            this.trackOutput = nbt.getBoolean("TrackOutput");
        }
        if (nbt.hasKey("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = ITextComponent.Serializer.jsonToComponent(nbt.getString("LastOutput"));
            }
            catch (final Throwable throwable) {
                this.lastOutput = new TextComponentString(throwable.getMessage());
            }
        }
        else {
            this.lastOutput = null;
        }
        if (nbt.hasKey("UpdateLastExecution")) {
            this.field_193042_c = nbt.getBoolean("UpdateLastExecution");
        }
        if (this.field_193042_c && nbt.hasKey("LastExecution")) {
            this.field_193041_b = nbt.getLong("LastExecution");
        }
        else {
            this.field_193041_b = -1L;
        }
        this.resultStats.readStatsFromNBT(nbt);
    }
    
    @Override
    public boolean canCommandSenderUseCommand(final int permLevel, final String commandName) {
        return permLevel <= 2;
    }
    
    public void setCommand(final String command) {
        this.commandStored = command;
        this.successCount = 0;
    }
    
    public String getCommand() {
        return this.commandStored;
    }
    
    public boolean trigger(final World worldIn) {
        if (worldIn.isRemote || worldIn.getTotalWorldTime() == this.field_193041_b) {
            return false;
        }
        if ("Searge".equalsIgnoreCase(this.commandStored)) {
            this.lastOutput = new TextComponentString("#itzlipofutzli");
            this.successCount = 1;
            return true;
        }
        final MinecraftServer minecraftserver = this.getServer();
        Label_0163: {
            if (minecraftserver != null && minecraftserver.isAnvilFileSet() && minecraftserver.isCommandBlockEnabled()) {
                try {
                    this.lastOutput = null;
                    this.successCount = minecraftserver.getCommandManager().executeCommand(this, this.commandStored);
                    break Label_0163;
                }
                catch (final Throwable throwable) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Executing command block");
                    final CrashReportCategory crashreportcategory = crashreport.makeCategory("Command to be executed");
                    crashreportcategory.setDetail("Command", new ICrashReportDetail<String>() {
                        @Override
                        public String call() throws Exception {
                            return CommandBlockBaseLogic.this.getCommand();
                        }
                    });
                    crashreportcategory.setDetail("Name", new ICrashReportDetail<String>() {
                        @Override
                        public String call() throws Exception {
                            return CommandBlockBaseLogic.this.getName();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
            this.successCount = 0;
        }
        if (this.field_193042_c) {
            this.field_193041_b = worldIn.getTotalWorldTime();
        }
        else {
            this.field_193041_b = -1L;
        }
        return true;
    }
    
    @Override
    public String getName() {
        return this.customName;
    }
    
    public void setName(final String name) {
        this.customName = name;
    }
    
    @Override
    public void addChatMessage(final ITextComponent component) {
        if (this.trackOutput && this.getEntityWorld() != null && !this.getEntityWorld().isRemote) {
            this.lastOutput = new TextComponentString("[" + CommandBlockBaseLogic.TIMESTAMP_FORMAT.format(new Date()) + "] ").appendSibling(component);
            this.updateCommand();
        }
    }
    
    @Override
    public boolean sendCommandFeedback() {
        final MinecraftServer minecraftserver = this.getServer();
        return minecraftserver == null || !minecraftserver.isAnvilFileSet() || minecraftserver.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
    }
    
    @Override
    public void setCommandStat(final CommandResultStats.Type type, final int amount) {
        this.resultStats.setCommandStatForSender(this.getServer(), this, type, amount);
    }
    
    public abstract void updateCommand();
    
    public abstract int getCommandBlockType();
    
    public abstract void fillInInfo(final ByteBuf p0);
    
    public void setLastOutput(@Nullable final ITextComponent lastOutputMessage) {
        this.lastOutput = lastOutputMessage;
    }
    
    public void setTrackOutput(final boolean shouldTrackOutput) {
        this.trackOutput = shouldTrackOutput;
    }
    
    public boolean shouldTrackOutput() {
        return this.trackOutput;
    }
    
    public boolean tryOpenEditCommandBlock(final EntityPlayer playerIn) {
        if (!playerIn.canUseCommandBlock()) {
            return false;
        }
        if (playerIn.getEntityWorld().isRemote) {
            playerIn.displayGuiEditCommandCart(this);
        }
        return true;
    }
    
    public CommandResultStats getCommandResultStats() {
        return this.resultStats;
    }
}
