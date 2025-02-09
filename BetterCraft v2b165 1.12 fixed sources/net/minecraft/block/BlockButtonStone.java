// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;

public class BlockButtonStone extends BlockButton
{
    protected BlockButtonStone() {
        super(false);
    }
    
    @Override
    protected void playClickSound(@Nullable final EntityPlayer player, final World worldIn, final BlockPos pos) {
        worldIn.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.6f);
    }
    
    @Override
    protected void playReleaseSound(final World worldIn, final BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.5f);
    }
}
