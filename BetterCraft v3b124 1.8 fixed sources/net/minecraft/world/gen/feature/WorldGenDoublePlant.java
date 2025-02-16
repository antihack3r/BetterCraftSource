/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenDoublePlant
extends WorldGenerator {
    private BlockDoublePlant.EnumPlantType field_150549_a;

    public void setPlantType(BlockDoublePlant.EnumPlantType p_180710_1_) {
        this.field_150549_a = p_180710_1_;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        boolean flag = false;
        int i2 = 0;
        while (i2 < 64) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.getHasNoSky() || blockpos.getY() < 254) && Blocks.double_plant.canPlaceBlockAt(worldIn, blockpos)) {
                Blocks.double_plant.placeAt(worldIn, blockpos, this.field_150549_a, 2);
                flag = true;
            }
            ++i2;
        }
        return flag;
    }
}

