// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockLeaves;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityCreature;

public class EntityAIWanderAvoidWaterFlying extends EntityAIWanderAvoidWater
{
    public EntityAIWanderAvoidWaterFlying(final EntityCreature p_i47413_1_, final double p_i47413_2_) {
        super(p_i47413_1_, p_i47413_2_);
    }
    
    @Nullable
    @Override
    protected Vec3d func_190864_f() {
        Vec3d vec3d = null;
        if (this.entity.isInWater() || this.entity.func_191953_am()) {
            vec3d = RandomPositionGenerator.func_191377_b(this.entity, 15, 15);
        }
        if (this.entity.getRNG().nextFloat() >= this.field_190865_h) {
            vec3d = this.func_192385_j();
        }
        return (vec3d == null) ? super.func_190864_f() : vec3d;
    }
    
    @Nullable
    private Vec3d func_192385_j() {
        final BlockPos blockpos = new BlockPos(this.entity);
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos blockpos$mutableblockpos2 = new BlockPos.MutableBlockPos();
        final Iterable<BlockPos.MutableBlockPos> iterable = BlockPos.func_191531_b(MathHelper.floor(this.entity.posX - 3.0), MathHelper.floor(this.entity.posY - 6.0), MathHelper.floor(this.entity.posZ - 3.0), MathHelper.floor(this.entity.posX + 3.0), MathHelper.floor(this.entity.posY + 6.0), MathHelper.floor(this.entity.posZ + 3.0));
        for (final BlockPos blockpos2 : iterable) {
            if (!blockpos.equals(blockpos2)) {
                final Block block = this.entity.world.getBlockState(blockpos$mutableblockpos2.setPos(blockpos2).move(EnumFacing.DOWN)).getBlock();
                final boolean flag = block instanceof BlockLeaves || block == Blocks.LOG || block == Blocks.LOG2;
                if (flag && this.entity.world.isAirBlock(blockpos2) && this.entity.world.isAirBlock(blockpos$mutableblockpos.setPos(blockpos2).move(EnumFacing.UP))) {
                    return new Vec3d(blockpos2.getX(), blockpos2.getY(), blockpos2.getZ());
                }
                continue;
            }
        }
        return null;
    }
}
