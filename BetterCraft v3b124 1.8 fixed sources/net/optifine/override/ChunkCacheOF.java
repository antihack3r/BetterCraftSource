/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.override;

import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.util.ArrayCache;

public class ChunkCacheOF
implements IBlockAccess {
    private final ChunkCache chunkCache;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int sizeXY;
    private int[] combinedLights;
    private IBlockState[] blockStates;
    private final int arraySize;
    private final boolean dynamicLights = Config.isDynamicLights();
    private static final ArrayCache cacheCombinedLights = new ArrayCache(Integer.TYPE, 16);
    private static final ArrayCache cacheBlockStates = new ArrayCache(IBlockState.class, 16);

    public ChunkCacheOF(ChunkCache chunkCache, BlockPos posFromIn, BlockPos posToIn, int subIn) {
        this.chunkCache = chunkCache;
        int i2 = posFromIn.getX() - subIn >> 4;
        int j2 = posFromIn.getY() - subIn >> 4;
        int k2 = posFromIn.getZ() - subIn >> 4;
        int l2 = posToIn.getX() + subIn >> 4;
        int i1 = posToIn.getY() + subIn >> 4;
        int j1 = posToIn.getZ() + subIn >> 4;
        this.sizeX = l2 - i2 + 1 << 4;
        this.sizeY = i1 - j2 + 1 << 4;
        this.sizeZ = j1 - k2 + 1 << 4;
        this.sizeXY = this.sizeX * this.sizeY;
        this.arraySize = this.sizeX * this.sizeY * this.sizeZ;
        this.posX = i2 << 4;
        this.posY = j2 << 4;
        this.posZ = k2 << 4;
    }

    private int getPositionIndex(BlockPos pos) {
        int i2 = pos.getX() - this.posX;
        if (i2 >= 0 && i2 < this.sizeX) {
            int j2 = pos.getY() - this.posY;
            if (j2 >= 0 && j2 < this.sizeY) {
                int k2 = pos.getZ() - this.posZ;
                return k2 >= 0 && k2 < this.sizeZ ? k2 * this.sizeXY + j2 * this.sizeX + i2 : -1;
            }
            return -1;
        }
        return -1;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        int i2 = this.getPositionIndex(pos);
        if (i2 >= 0 && i2 < this.arraySize && this.combinedLights != null) {
            int j2 = this.combinedLights[i2];
            if (j2 == -1) {
                this.combinedLights[i2] = j2 = this.getCombinedLightRaw(pos, lightValue);
            }
            return j2;
        }
        return this.getCombinedLightRaw(pos, lightValue);
    }

    private int getCombinedLightRaw(BlockPos pos, int lightValue) {
        int i2 = this.chunkCache.getCombinedLight(pos, lightValue);
        if (this.dynamicLights && !this.getBlockState(pos).getBlock().isOpaqueCube()) {
            i2 = DynamicLights.getCombinedLight(pos, i2);
        }
        return i2;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        int i2 = this.getPositionIndex(pos);
        if (i2 >= 0 && i2 < this.arraySize && this.blockStates != null) {
            IBlockState iblockstate = this.blockStates[i2];
            if (iblockstate == null) {
                this.blockStates[i2] = iblockstate = this.chunkCache.getBlockState(pos);
            }
            return iblockstate;
        }
        return this.chunkCache.getBlockState(pos);
    }

    public void renderStart() {
        if (this.combinedLights == null) {
            this.combinedLights = (int[])cacheCombinedLights.allocate(this.arraySize);
        }
        Arrays.fill(this.combinedLights, -1);
        if (this.blockStates == null) {
            this.blockStates = (IBlockState[])cacheBlockStates.allocate(this.arraySize);
        }
        Arrays.fill(this.blockStates, null);
    }

    public void renderFinish() {
        cacheCombinedLights.free(this.combinedLights);
        this.combinedLights = null;
        cacheBlockStates.free(this.blockStates);
        this.blockStates = null;
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return this.chunkCache.extendedLevelsInChunkCache();
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
        return this.chunkCache.getBiomeGenForCoords(pos);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.chunkCache.getStrongPower(pos, direction);
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return this.chunkCache.getTileEntity(pos);
    }

    @Override
    public WorldType getWorldType() {
        return this.chunkCache.getWorldType();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.chunkCache.isAirBlock(pos);
    }

    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return Reflector.callBoolean(this.chunkCache, Reflector.ForgeChunkCache_isSideSolid, pos, side, _default);
    }
}

