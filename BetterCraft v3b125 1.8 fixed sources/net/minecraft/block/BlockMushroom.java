/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockMushroom
extends BlockBush
implements IGrowable {
    protected BlockMushroom() {
        float f2 = 0.2f;
        this.setBlockBounds(0.5f - f2, 0.0f, 0.5f - f2, 0.5f + f2, f2 * 2.0f, 0.5f + f2);
        this.setTickRandomly(true);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(25) == 0) {
            void var7_11;
            int i2 = 5;
            int j2 = 4;
            for (BlockPos blockPos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                if (worldIn.getBlockState(blockPos).getBlock() != this || --i2 > 0) continue;
                return;
            }
            BlockPos blockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
            int k2 = 0;
            while (k2 < 4) {
                if (worldIn.isAirBlock((BlockPos)var7_11) && this.canBlockStay(worldIn, (BlockPos)var7_11, this.getDefaultState())) {
                    pos = var7_11;
                }
                BlockPos blockPos2 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
                ++k2;
            }
            if (worldIn.isAirBlock((BlockPos)var7_11) && this.canBlockStay(worldIn, (BlockPos)var7_11, this.getDefaultState())) {
                worldIn.setBlockState((BlockPos)var7_11, this.getDefaultState(), 2);
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
    }

    @Override
    protected boolean canPlaceBlockOn(Block ground) {
        return ground.isFullBlock();
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());
            return iblockstate.getBlock() == Blocks.mycelium ? true : (iblockstate.getBlock() == Blocks.dirt && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL ? true : worldIn.getLight(pos) < 13 && this.canPlaceBlockOn(iblockstate.getBlock()));
        }
        return false;
    }

    public boolean generateBigMushroom(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        worldIn.setBlockToAir(pos);
        WorldGenBigMushroom worldgenerator = null;
        if (this == Blocks.brown_mushroom) {
            worldgenerator = new WorldGenBigMushroom(Blocks.brown_mushroom_block);
        } else if (this == Blocks.red_mushroom) {
            worldgenerator = new WorldGenBigMushroom(Blocks.red_mushroom_block);
        }
        if (worldgenerator != null && ((WorldGenerator)worldgenerator).generate(worldIn, rand, pos)) {
            return true;
        }
        worldIn.setBlockState(pos, state, 3);
        return false;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return (double)rand.nextFloat() < 0.4;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        this.generateBigMushroom(worldIn, pos, state, rand);
    }
}

