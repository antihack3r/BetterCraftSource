// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.material.Material;
import com.google.common.collect.Lists;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import java.util.List;

public class BlockNote extends BlockContainer
{
    private static final List<SoundEvent> INSTRUMENTS;
    
    static {
        INSTRUMENTS = Lists.newArrayList(SoundEvents.BLOCK_NOTE_HARP, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundEvents.BLOCK_NOTE_SNARE, SoundEvents.BLOCK_NOTE_HAT, SoundEvents.BLOCK_NOTE_BASS, SoundEvents.field_193809_ey, SoundEvents.field_193807_ew, SoundEvents.field_193810_ez, SoundEvents.field_193808_ex, SoundEvents.field_193785_eE);
    }
    
    public BlockNote() {
        super(Material.WOOD);
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos p_189540_5_) {
        final boolean flag = worldIn.isBlockPowered(pos);
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityNote) {
            final TileEntityNote tileentitynote = (TileEntityNote)tileentity;
            if (tileentitynote.previousRedstoneState != flag) {
                if (flag) {
                    tileentitynote.triggerNote(worldIn, pos);
                }
                tileentitynote.previousRedstoneState = flag;
            }
        }
    }
    
    @Override
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumHand hand, final EnumFacing heldItem, final float side, final float hitX, final float hitY) {
        if (worldIn.isRemote) {
            return true;
        }
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityNote) {
            final TileEntityNote tileentitynote = (TileEntityNote)tileentity;
            tileentitynote.changePitch();
            tileentitynote.triggerNote(worldIn, pos);
            playerIn.addStat(StatList.NOTEBLOCK_TUNED);
        }
        return true;
    }
    
    @Override
    public void onBlockClicked(final World worldIn, final BlockPos pos, final EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            final TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityNote) {
                ((TileEntityNote)tileentity).triggerNote(worldIn, pos);
                playerIn.addStat(StatList.NOTEBLOCK_PLAYED);
            }
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityNote();
    }
    
    private SoundEvent getInstrument(int p_185576_1_) {
        if (p_185576_1_ < 0 || p_185576_1_ >= BlockNote.INSTRUMENTS.size()) {
            p_185576_1_ = 0;
        }
        return BlockNote.INSTRUMENTS.get(p_185576_1_);
    }
    
    @Override
    public boolean eventReceived(final IBlockState state, final World worldIn, final BlockPos pos, final int id, final int param) {
        final float f = (float)Math.pow(2.0, (param - 12) / 12.0);
        worldIn.playSound(null, pos, this.getInstrument(id), SoundCategory.RECORDS, 3.0f, f);
        worldIn.spawnParticle(EnumParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, param / 24.0, 0.0, 0.0, new int[0]);
        return true;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
