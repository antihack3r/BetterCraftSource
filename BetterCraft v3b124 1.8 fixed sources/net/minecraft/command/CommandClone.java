/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandClone
extends CommandBase {
    @Override
    public String getCommandName() {
        return "clone";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.clone.usage";
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        void var19_20;
        if (args.length < 9) {
            throw new WrongUsageException("commands.clone.usage", new Object[0]);
        }
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
        BlockPos blockpos = CommandClone.parseBlockPos(sender, args, 0, false);
        BlockPos blockpos1 = CommandClone.parseBlockPos(sender, args, 3, false);
        BlockPos blockpos2 = CommandClone.parseBlockPos(sender, args, 6, false);
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(blockpos, blockpos1);
        StructureBoundingBox structureboundingbox1 = new StructureBoundingBox(blockpos2, blockpos2.add(structureboundingbox.func_175896_b()));
        int i2 = structureboundingbox.getXSize() * structureboundingbox.getYSize() * structureboundingbox.getZSize();
        if (i2 > 32768) {
            throw new CommandException("commands.clone.tooManyBlocks", i2, 32768);
        }
        boolean flag = false;
        Block block = null;
        int j2 = -1;
        if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && structureboundingbox.intersectsWith(structureboundingbox1)) {
            throw new CommandException("commands.clone.noOverlap", new Object[0]);
        }
        if (args.length >= 11 && args[10].equals("move")) {
            flag = true;
        }
        if (structureboundingbox.minY < 0 || structureboundingbox.maxY >= 256 || structureboundingbox1.minY < 0 || structureboundingbox1.maxY >= 256) throw new CommandException("commands.clone.outOfWorld", new Object[0]);
        World world = sender.getEntityWorld();
        if (!world.isAreaLoaded(structureboundingbox) || !world.isAreaLoaded(structureboundingbox1)) throw new CommandException("commands.clone.outOfWorld", new Object[0]);
        boolean flag1 = false;
        if (args.length >= 10) {
            if (args[9].equals("masked")) {
                flag1 = true;
            } else if (args[9].equals("filtered")) {
                if (args.length < 12) {
                    throw new WrongUsageException("commands.clone.usage", new Object[0]);
                }
                block = CommandClone.getBlockByText(sender, args[11]);
                if (args.length >= 13) {
                    j2 = CommandClone.parseInt(args[12], 0, 15);
                }
            }
        }
        ArrayList<StaticCloneData> list = Lists.newArrayList();
        ArrayList<StaticCloneData> list1 = Lists.newArrayList();
        ArrayList<StaticCloneData> list2 = Lists.newArrayList();
        LinkedList<Iterator<Object>> linkedlist = Lists.newLinkedList();
        BlockPos blockpos3 = new BlockPos(structureboundingbox1.minX - structureboundingbox.minX, structureboundingbox1.minY - structureboundingbox.minY, structureboundingbox1.minZ - structureboundingbox.minZ);
        int n2 = structureboundingbox.minZ;
        while (var19_20 <= structureboundingbox.maxZ) {
            int l2 = structureboundingbox.minY;
            while (l2 <= structureboundingbox.maxY) {
                int i1 = structureboundingbox.minX;
                while (i1 <= structureboundingbox.maxX) {
                    Iterator<Object> blockpos4 = new BlockPos(i1, l2, (int)var19_20);
                    BlockPos blockpos5 = ((BlockPos)((Object)blockpos4)).add(blockpos3);
                    IBlockState iblockstate = world.getBlockState((BlockPos)((Object)blockpos4));
                    if (!(flag1 && iblockstate.getBlock() == Blocks.air || block != null && (iblockstate.getBlock() != block || j2 >= 0 && iblockstate.getBlock().getMetaFromState(iblockstate) != j2))) {
                        TileEntity tileentity = world.getTileEntity((BlockPos)((Object)blockpos4));
                        if (tileentity != null) {
                            NBTTagCompound nbttagcompound = new NBTTagCompound();
                            tileentity.writeToNBT(nbttagcompound);
                            list1.add(new StaticCloneData(blockpos5, iblockstate, nbttagcompound));
                            linkedlist.addLast(blockpos4);
                        } else if (!iblockstate.getBlock().isFullBlock() && !iblockstate.getBlock().isFullCube()) {
                            list2.add(new StaticCloneData(blockpos5, iblockstate, null));
                            linkedlist.addFirst(blockpos4);
                        } else {
                            list.add(new StaticCloneData(blockpos5, iblockstate, null));
                            linkedlist.addLast(blockpos4);
                        }
                    }
                    ++i1;
                }
                ++l2;
            }
            ++var19_20;
        }
        if (flag) {
            for (BlockPos blockPos : linkedlist) {
                TileEntity tileentity1 = world.getTileEntity(blockPos);
                if (tileentity1 instanceof IInventory) {
                    ((IInventory)((Object)tileentity1)).clear();
                }
                world.setBlockState(blockPos, Blocks.barrier.getDefaultState(), 2);
            }
            for (BlockPos blockPos : linkedlist) {
                world.setBlockState(blockPos, Blocks.air.getDefaultState(), 3);
            }
        }
        ArrayList<StaticCloneData> arrayList = Lists.newArrayList();
        arrayList.addAll(list);
        arrayList.addAll(list1);
        arrayList.addAll(list2);
        List<StaticCloneData> list4 = Lists.reverse(arrayList);
        for (StaticCloneData commandclone$staticclonedata : list4) {
            TileEntity tileentity2 = world.getTileEntity(commandclone$staticclonedata.pos);
            if (tileentity2 instanceof IInventory) {
                ((IInventory)((Object)tileentity2)).clear();
            }
            world.setBlockState(commandclone$staticclonedata.pos, Blocks.barrier.getDefaultState(), 2);
        }
        i2 = 0;
        for (StaticCloneData commandclone$staticclonedata1 : arrayList) {
            if (!world.setBlockState(commandclone$staticclonedata1.pos, commandclone$staticclonedata1.blockState, 2)) continue;
            ++i2;
        }
        for (StaticCloneData commandclone$staticclonedata2 : list1) {
            TileEntity tileentity3 = world.getTileEntity(commandclone$staticclonedata2.pos);
            if (commandclone$staticclonedata2.compound != null && tileentity3 != null) {
                commandclone$staticclonedata2.compound.setInteger("x", commandclone$staticclonedata2.pos.getX());
                commandclone$staticclonedata2.compound.setInteger("y", commandclone$staticclonedata2.pos.getY());
                commandclone$staticclonedata2.compound.setInteger("z", commandclone$staticclonedata2.pos.getZ());
                tileentity3.readFromNBT(commandclone$staticclonedata2.compound);
                tileentity3.markDirty();
            }
            world.setBlockState(commandclone$staticclonedata2.pos, commandclone$staticclonedata2.blockState, 2);
        }
        for (StaticCloneData commandclone$staticclonedata3 : list4) {
            world.notifyNeighborsRespectDebug(commandclone$staticclonedata3.pos, commandclone$staticclonedata3.blockState.getBlock());
        }
        List<NextTickListEntry> list5 = world.func_175712_a(structureboundingbox, false);
        if (list5 != null) {
            for (NextTickListEntry nextticklistentry : list5) {
                if (!structureboundingbox.isVecInside(nextticklistentry.position)) continue;
                BlockPos blockpos8 = nextticklistentry.position.add(blockpos3);
                world.scheduleBlockUpdate(blockpos8, nextticklistentry.getBlock(), (int)(nextticklistentry.scheduledTime - world.getWorldInfo().getWorldTotalTime()), nextticklistentry.priority);
            }
        }
        if (i2 <= 0) {
            throw new CommandException("commands.clone.failed", new Object[0]);
        }
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, i2);
        CommandClone.notifyOperators(sender, (ICommand)this, "commands.clone.success", i2);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length > 0 && args.length <= 3 ? CommandClone.func_175771_a(args, 0, pos) : (args.length > 3 && args.length <= 6 ? CommandClone.func_175771_a(args, 3, pos) : (args.length > 6 && args.length <= 9 ? CommandClone.func_175771_a(args, 6, pos) : (args.length == 10 ? CommandClone.getListOfStringsMatchingLastWord(args, "replace", "masked", "filtered") : (args.length == 11 ? CommandClone.getListOfStringsMatchingLastWord(args, "normal", "force", "move") : (args.length == 12 && "filtered".equals(args[9]) ? CommandClone.getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)))));
    }

    static class StaticCloneData {
        public final BlockPos pos;
        public final IBlockState blockState;
        public final NBTTagCompound compound;

        public StaticCloneData(BlockPos posIn, IBlockState stateIn, NBTTagCompound compoundIn) {
            this.pos = posIn;
            this.blockState = stateIn;
            this.compound = compoundIn;
        }
    }
}

