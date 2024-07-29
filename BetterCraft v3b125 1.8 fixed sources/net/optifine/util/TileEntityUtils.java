/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.optifine.reflect.Reflector;
import net.optifine.util.IntegratedServerUtils;

public class TileEntityUtils {
    public static String getTileEntityName(IBlockAccess blockAccess, BlockPos blockPos) {
        TileEntity tileentity = blockAccess.getTileEntity(blockPos);
        return TileEntityUtils.getTileEntityName(tileentity);
    }

    public static String getTileEntityName(TileEntity te2) {
        if (!(te2 instanceof IWorldNameable)) {
            return null;
        }
        IWorldNameable iworldnameable = (IWorldNameable)((Object)te2);
        TileEntityUtils.updateTileEntityName(te2);
        return !iworldnameable.hasCustomName() ? null : iworldnameable.getName();
    }

    public static void updateTileEntityName(TileEntity te2) {
        BlockPos blockpos = te2.getPos();
        String s2 = TileEntityUtils.getTileEntityRawName(te2);
        if (s2 == null) {
            String s1 = TileEntityUtils.getServerTileEntityRawName(blockpos);
            s1 = Config.normalize(s1);
            TileEntityUtils.setTileEntityRawName(te2, s1);
        }
    }

    public static String getServerTileEntityRawName(BlockPos blockPos) {
        TileEntity tileentity = IntegratedServerUtils.getTileEntity(blockPos);
        return tileentity == null ? null : TileEntityUtils.getTileEntityRawName(tileentity);
    }

    public static String getTileEntityRawName(TileEntity te2) {
        IWorldNameable iworldnameable;
        if (te2 instanceof TileEntityBeacon) {
            return (String)Reflector.getFieldValue(te2, Reflector.TileEntityBeacon_customName);
        }
        if (te2 instanceof TileEntityBrewingStand) {
            return (String)Reflector.getFieldValue(te2, Reflector.TileEntityBrewingStand_customName);
        }
        if (te2 instanceof TileEntityEnchantmentTable) {
            return (String)Reflector.getFieldValue(te2, Reflector.TileEntityEnchantmentTable_customName);
        }
        if (te2 instanceof TileEntityFurnace) {
            return (String)Reflector.getFieldValue(te2, Reflector.TileEntityFurnace_customName);
        }
        if (te2 instanceof IWorldNameable && (iworldnameable = (IWorldNameable)((Object)te2)).hasCustomName()) {
            return iworldnameable.getName();
        }
        return null;
    }

    public static boolean setTileEntityRawName(TileEntity te2, String name) {
        if (te2 instanceof TileEntityBeacon) {
            return Reflector.setFieldValue(te2, Reflector.TileEntityBeacon_customName, name);
        }
        if (te2 instanceof TileEntityBrewingStand) {
            return Reflector.setFieldValue(te2, Reflector.TileEntityBrewingStand_customName, name);
        }
        if (te2 instanceof TileEntityEnchantmentTable) {
            return Reflector.setFieldValue(te2, Reflector.TileEntityEnchantmentTable_customName, name);
        }
        if (te2 instanceof TileEntityFurnace) {
            return Reflector.setFieldValue(te2, Reflector.TileEntityFurnace_customName, name);
        }
        if (te2 instanceof TileEntityChest) {
            ((TileEntityChest)te2).setCustomName(name);
            return true;
        }
        if (te2 instanceof TileEntityDispenser) {
            ((TileEntityDispenser)te2).setCustomName(name);
            return true;
        }
        if (te2 instanceof TileEntityHopper) {
            ((TileEntityHopper)te2).setCustomName(name);
            return true;
        }
        return false;
    }
}

