/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.render;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkVisibility {
    public static final int MASK_FACINGS = 63;
    public static final EnumFacing[][] enumFacingArrays = ChunkVisibility.makeEnumFacingArrays(false);
    public static final EnumFacing[][] enumFacingOppositeArrays = ChunkVisibility.makeEnumFacingArrays(true);
    private static int counter = 0;
    private static int iMaxStatic = -1;
    private static int iMaxStaticFinal = 16;
    private static World worldLast = null;
    private static int pcxLast = Integer.MIN_VALUE;
    private static int pczLast = Integer.MIN_VALUE;

    public static int getMaxChunkY(World world, Entity viewEntity, int renderDistanceChunks) {
        int i2 = MathHelper.floor_double(viewEntity.posX) >> 4;
        int j2 = MathHelper.floor_double(viewEntity.posY) >> 4;
        int k2 = MathHelper.floor_double(viewEntity.posZ) >> 4;
        Chunk chunk = world.getChunkFromChunkCoords(i2, k2);
        int l2 = i2 - renderDistanceChunks;
        int i1 = i2 + renderDistanceChunks;
        int j1 = k2 - renderDistanceChunks;
        int k1 = k2 + renderDistanceChunks;
        if (world != worldLast || i2 != pcxLast || k2 != pczLast) {
            counter = 0;
            iMaxStaticFinal = 16;
            worldLast = world;
            pcxLast = i2;
            pczLast = k2;
        }
        if (counter == 0) {
            iMaxStatic = -1;
        }
        int l1 = iMaxStatic;
        switch (counter) {
            case 0: {
                i1 = i2;
                k1 = k2;
                break;
            }
            case 1: {
                l2 = i2;
                k1 = k2;
                break;
            }
            case 2: {
                i1 = i2;
                j1 = k2;
                break;
            }
            case 3: {
                l2 = i2;
                j1 = k2;
            }
        }
        int i22 = l2;
        while (i22 < i1) {
            int j22 = j1;
            while (j22 < k1) {
                Chunk chunk1 = world.getChunkFromChunkCoords(i22, j22);
                if (!chunk1.isEmpty()) {
                    ExtendedBlockStorage[] aextendedblockstorage = chunk1.getBlockStorageArray();
                    int k22 = aextendedblockstorage.length - 1;
                    while (k22 > l1) {
                        ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[k22];
                        if (extendedblockstorage != null && !extendedblockstorage.isEmpty()) {
                            if (k22 <= l1) break;
                            l1 = k22;
                            break;
                        }
                        --k22;
                    }
                    try {
                        Map<BlockPos, TileEntity> map = chunk1.getTileEntityMap();
                        if (!map.isEmpty()) {
                            for (BlockPos blockpos : map.keySet()) {
                                int l22 = blockpos.getY() >> 4;
                                if (l22 <= l1) continue;
                                l1 = l22;
                            }
                        }
                    }
                    catch (ConcurrentModificationException map) {
                        // empty catch block
                    }
                    ClassInheritanceMultiMap<Entity>[] classinheritancemultimap = chunk1.getEntityLists();
                    int i3 = classinheritancemultimap.length - 1;
                    while (i3 > l1) {
                        ClassInheritanceMultiMap<Entity> classinheritancemultimap1 = classinheritancemultimap[i3];
                        if (!(classinheritancemultimap1.isEmpty() || chunk1 == chunk && i3 == j2 && classinheritancemultimap1.size() == 1)) {
                            if (i3 <= l1) break;
                            l1 = i3;
                            break;
                        }
                        --i3;
                    }
                }
                ++j22;
            }
            ++i22;
        }
        if (counter < 3) {
            iMaxStatic = l1;
            l1 = iMaxStaticFinal;
        } else {
            iMaxStaticFinal = l1;
            iMaxStatic = -1;
        }
        counter = (counter + 1) % 4;
        return l1 << 4;
    }

    public static boolean isFinished() {
        return counter == 0;
    }

    private static EnumFacing[][] makeEnumFacingArrays(boolean opposite) {
        int i2 = 64;
        EnumFacing[][] aenumfacing = new EnumFacing[i2][];
        int j2 = 0;
        while (j2 < i2) {
            ArrayList<EnumFacing> list = new ArrayList<EnumFacing>();
            int k2 = 0;
            while (k2 < EnumFacing.VALUES.length) {
                EnumFacing enumfacing = EnumFacing.VALUES[k2];
                EnumFacing enumfacing1 = opposite ? enumfacing.getOpposite() : enumfacing;
                int l2 = 1 << enumfacing1.ordinal();
                if ((j2 & l2) != 0) {
                    list.add(enumfacing);
                }
                ++k2;
            }
            EnumFacing[] aenumfacing1 = list.toArray(new EnumFacing[list.size()]);
            aenumfacing[j2] = aenumfacing1;
            ++j2;
        }
        return aenumfacing;
    }

    public static EnumFacing[] getFacingsNotOpposite(int setDisabled) {
        int i2 = ~setDisabled & 0x3F;
        return enumFacingOppositeArrays[i2];
    }

    public static void reset() {
        worldLast = null;
    }
}

