/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers
extends CommandBase {
    @Override
    public String getCommandName() {
        return "spreadplayers";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.spreadplayers.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 6) {
            throw new WrongUsageException("commands.spreadplayers.usage", new Object[0]);
        }
        int i2 = 0;
        BlockPos blockpos = sender.getPosition();
        double d0 = CommandSpreadPlayers.parseDouble(blockpos.getX(), args[i2++], true);
        double d1 = CommandSpreadPlayers.parseDouble(blockpos.getZ(), args[i2++], true);
        double d2 = CommandSpreadPlayers.parseDouble(args[i2++], 0.0);
        double d3 = CommandSpreadPlayers.parseDouble(args[i2++], d2 + 1.0);
        boolean flag = CommandSpreadPlayers.parseBoolean(args[i2++]);
        ArrayList<Entity> list = Lists.newArrayList();
        while (i2 < args.length) {
            String s2;
            if (PlayerSelector.hasArguments(s2 = args[i2++])) {
                List<Entity> list1 = PlayerSelector.matchEntities(sender, s2, Entity.class);
                if (list1.size() == 0) {
                    throw new EntityNotFoundException();
                }
                list.addAll(list1);
                continue;
            }
            EntityPlayerMP entityplayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(s2);
            if (entityplayer == null) {
                throw new PlayerNotFoundException();
            }
            list.add(entityplayer);
        }
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());
        if (list.isEmpty()) {
            throw new EntityNotFoundException();
        }
        sender.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.spreading." + (flag ? "teams" : "players"), list.size(), d3, d0, d1, d2));
        this.func_110669_a(sender, list, new Position(d0, d1), d2, d3, ((Entity)list.get((int)0)).worldObj, flag);
    }

    private void func_110669_a(ICommandSender p_110669_1_, List<Entity> p_110669_2_, Position p_110669_3_, double p_110669_4_, double p_110669_6_, World worldIn, boolean p_110669_9_) throws CommandException {
        Random random = new Random();
        double d0 = p_110669_3_.field_111101_a - p_110669_6_;
        double d1 = p_110669_3_.field_111100_b - p_110669_6_;
        double d2 = p_110669_3_.field_111101_a + p_110669_6_;
        double d3 = p_110669_3_.field_111100_b + p_110669_6_;
        Position[] acommandspreadplayers$position = this.func_110670_a(random, p_110669_9_ ? this.func_110667_a(p_110669_2_) : p_110669_2_.size(), d0, d1, d2, d3);
        int i2 = this.func_110668_a(p_110669_3_, p_110669_4_, worldIn, random, d0, d1, d2, d3, acommandspreadplayers$position, p_110669_9_);
        double d4 = this.func_110671_a(p_110669_2_, worldIn, acommandspreadplayers$position, p_110669_9_);
        CommandSpreadPlayers.notifyOperators(p_110669_1_, (ICommand)this, "commands.spreadplayers.success." + (p_110669_9_ ? "teams" : "players"), acommandspreadplayers$position.length, p_110669_3_.field_111101_a, p_110669_3_.field_111100_b);
        if (acommandspreadplayers$position.length > 1) {
            p_110669_1_.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.info." + (p_110669_9_ ? "teams" : "players"), String.format("%.2f", d4), i2));
        }
    }

    private int func_110667_a(List<Entity> p_110667_1_) {
        HashSet<Team> set = Sets.newHashSet();
        for (Entity entity : p_110667_1_) {
            if (entity instanceof EntityPlayer) {
                set.add(((EntityPlayer)entity).getTeam());
                continue;
            }
            set.add(null);
        }
        return set.size();
    }

    private int func_110668_a(Position p_110668_1_, double p_110668_2_, World worldIn, Random p_110668_5_, double p_110668_6_, double p_110668_8_, double p_110668_10_, double p_110668_12_, Position[] p_110668_14_, boolean p_110668_15_) throws CommandException {
        boolean flag = true;
        double d0 = 3.4028234663852886E38;
        int i2 = 0;
        while (i2 < 10000 && flag) {
            flag = false;
            d0 = 3.4028234663852886E38;
            int j2 = 0;
            while (j2 < p_110668_14_.length) {
                Position commandspreadplayers$position = p_110668_14_[j2];
                int k2 = 0;
                Position commandspreadplayers$position1 = new Position();
                int l2 = 0;
                while (l2 < p_110668_14_.length) {
                    if (j2 != l2) {
                        Position commandspreadplayers$position2 = p_110668_14_[l2];
                        double d1 = commandspreadplayers$position.func_111099_a(commandspreadplayers$position2);
                        d0 = Math.min(d1, d0);
                        if (d1 < p_110668_2_) {
                            ++k2;
                            commandspreadplayers$position1.field_111101_a += commandspreadplayers$position2.field_111101_a - commandspreadplayers$position.field_111101_a;
                            commandspreadplayers$position1.field_111100_b += commandspreadplayers$position2.field_111100_b - commandspreadplayers$position.field_111100_b;
                        }
                    }
                    ++l2;
                }
                if (k2 > 0) {
                    commandspreadplayers$position1.field_111101_a /= (double)k2;
                    commandspreadplayers$position1.field_111100_b /= (double)k2;
                    double d2 = commandspreadplayers$position1.func_111096_b();
                    if (d2 > 0.0) {
                        commandspreadplayers$position1.func_111095_a();
                        commandspreadplayers$position.func_111094_b(commandspreadplayers$position1);
                    } else {
                        commandspreadplayers$position.func_111097_a(p_110668_5_, p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_);
                    }
                    flag = true;
                }
                if (commandspreadplayers$position.func_111093_a(p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_)) {
                    flag = true;
                }
                ++j2;
            }
            if (!flag) {
                Position[] positionArray = p_110668_14_;
                int n2 = p_110668_14_.length;
                int n3 = 0;
                while (n3 < n2) {
                    Position commandspreadplayers$position3 = positionArray[n3];
                    if (!commandspreadplayers$position3.func_111098_b(worldIn)) {
                        commandspreadplayers$position3.func_111097_a(p_110668_5_, p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_);
                        flag = true;
                    }
                    ++n3;
                }
            }
            ++i2;
        }
        if (i2 >= 10000) {
            throw new CommandException("commands.spreadplayers.failure." + (p_110668_15_ ? "teams" : "players"), p_110668_14_.length, p_110668_1_.field_111101_a, p_110668_1_.field_111100_b, String.format("%.2f", d0));
        }
        return i2;
    }

    private double func_110671_a(List<Entity> p_110671_1_, World worldIn, Position[] p_110671_3_, boolean p_110671_4_) {
        double d0 = 0.0;
        int i2 = 0;
        HashMap<Team, Position> map = Maps.newHashMap();
        int j2 = 0;
        while (j2 < p_110671_1_.size()) {
            Position commandspreadplayers$position;
            Entity entity = p_110671_1_.get(j2);
            if (p_110671_4_) {
                Team team;
                Team team2 = team = entity instanceof EntityPlayer ? ((EntityPlayer)entity).getTeam() : null;
                if (!map.containsKey(team)) {
                    map.put(team, p_110671_3_[i2++]);
                }
                commandspreadplayers$position = (Position)map.get(team);
            } else {
                commandspreadplayers$position = p_110671_3_[i2++];
            }
            entity.setPositionAndUpdate((float)MathHelper.floor_double(commandspreadplayers$position.field_111101_a) + 0.5f, commandspreadplayers$position.func_111092_a(worldIn), (double)MathHelper.floor_double(commandspreadplayers$position.field_111100_b) + 0.5);
            double d2 = Double.MAX_VALUE;
            int k2 = 0;
            while (k2 < p_110671_3_.length) {
                if (commandspreadplayers$position != p_110671_3_[k2]) {
                    double d1 = commandspreadplayers$position.func_111099_a(p_110671_3_[k2]);
                    d2 = Math.min(d1, d2);
                }
                ++k2;
            }
            d0 += d2;
            ++j2;
        }
        return d0 /= (double)p_110671_1_.size();
    }

    private Position[] func_110670_a(Random p_110670_1_, int p_110670_2_, double p_110670_3_, double p_110670_5_, double p_110670_7_, double p_110670_9_) {
        Position[] acommandspreadplayers$position = new Position[p_110670_2_];
        int i2 = 0;
        while (i2 < acommandspreadplayers$position.length) {
            Position commandspreadplayers$position = new Position();
            commandspreadplayers$position.func_111097_a(p_110670_1_, p_110670_3_, p_110670_5_, p_110670_7_, p_110670_9_);
            acommandspreadplayers$position[i2] = commandspreadplayers$position;
            ++i2;
        }
        return acommandspreadplayers$position;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length >= 1 && args.length <= 2 ? CommandSpreadPlayers.func_181043_b(args, 0, pos) : null;
    }

    static class Position {
        double field_111101_a;
        double field_111100_b;

        Position() {
        }

        Position(double p_i1358_1_, double p_i1358_3_) {
            this.field_111101_a = p_i1358_1_;
            this.field_111100_b = p_i1358_3_;
        }

        double func_111099_a(Position p_111099_1_) {
            double d0 = this.field_111101_a - p_111099_1_.field_111101_a;
            double d1 = this.field_111100_b - p_111099_1_.field_111100_b;
            return Math.sqrt(d0 * d0 + d1 * d1);
        }

        void func_111095_a() {
            double d0 = this.func_111096_b();
            this.field_111101_a /= d0;
            this.field_111100_b /= d0;
        }

        float func_111096_b() {
            return MathHelper.sqrt_double(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
        }

        public void func_111094_b(Position p_111094_1_) {
            this.field_111101_a -= p_111094_1_.field_111101_a;
            this.field_111100_b -= p_111094_1_.field_111100_b;
        }

        public boolean func_111093_a(double p_111093_1_, double p_111093_3_, double p_111093_5_, double p_111093_7_) {
            boolean flag = false;
            if (this.field_111101_a < p_111093_1_) {
                this.field_111101_a = p_111093_1_;
                flag = true;
            } else if (this.field_111101_a > p_111093_5_) {
                this.field_111101_a = p_111093_5_;
                flag = true;
            }
            if (this.field_111100_b < p_111093_3_) {
                this.field_111100_b = p_111093_3_;
                flag = true;
            } else if (this.field_111100_b > p_111093_7_) {
                this.field_111100_b = p_111093_7_;
                flag = true;
            }
            return flag;
        }

        public int func_111092_a(World worldIn) {
            BlockPos blockpos = new BlockPos(this.field_111101_a, 256.0, this.field_111100_b);
            while (blockpos.getY() > 0) {
                if (worldIn.getBlockState(blockpos = blockpos.down()).getBlock().getMaterial() == Material.air) continue;
                return blockpos.getY() + 1;
            }
            return 257;
        }

        public boolean func_111098_b(World worldIn) {
            BlockPos blockpos = new BlockPos(this.field_111101_a, 256.0, this.field_111100_b);
            while (blockpos.getY() > 0) {
                Material material = worldIn.getBlockState(blockpos = blockpos.down()).getBlock().getMaterial();
                if (material == Material.air) continue;
                return !material.isLiquid() && material != Material.fire;
            }
            return false;
        }

        public void func_111097_a(Random p_111097_1_, double p_111097_2_, double p_111097_4_, double p_111097_6_, double p_111097_8_) {
            this.field_111101_a = MathHelper.getRandomDoubleInRange(p_111097_1_, p_111097_2_, p_111097_6_);
            this.field_111100_b = MathHelper.getRandomDoubleInRange(p_111097_1_, p_111097_4_, p_111097_8_);
        }
    }
}

