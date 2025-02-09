// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.gen.structure;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.tileentity.TileEntityChest;
import java.util.Map;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.util.Rotation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockSandStone;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Random;

public class ComponentScatteredFeaturePieces
{
    public static void registerScatteredFeaturePieces() {
        MapGenStructureIO.registerStructureComponent(DesertPyramid.class, "TeDP");
        MapGenStructureIO.registerStructureComponent(JunglePyramid.class, "TeJP");
        MapGenStructureIO.registerStructureComponent(SwampHut.class, "TeSH");
        MapGenStructureIO.registerStructureComponent(Igloo.class, "Iglu");
    }
    
    public static class DesertPyramid extends Feature
    {
        private final boolean[] hasPlacedChest;
        
        public DesertPyramid() {
            this.hasPlacedChest = new boolean[4];
        }
        
        public DesertPyramid(final Random p_i2062_1_, final int p_i2062_2_, final int p_i2062_3_) {
            super(p_i2062_1_, p_i2062_2_, 64, p_i2062_3_, 21, 15, 21);
            this.hasPlacedChest = new boolean[4];
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
            tagCompound.setBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
            tagCompound.setBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
            tagCompound.setBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.hasPlacedChest[0] = tagCompound.getBoolean("hasPlacedChest0");
            this.hasPlacedChest[1] = tagCompound.getBoolean("hasPlacedChest1");
            this.hasPlacedChest[2] = tagCompound.getBoolean("hasPlacedChest2");
            this.hasPlacedChest[3] = tagCompound.getBoolean("hasPlacedChest3");
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0, this.scatteredFeatureSizeZ - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            for (int i = 1; i <= 9; ++i) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, i, i, i, this.scatteredFeatureSizeX - 1 - i, i, this.scatteredFeatureSizeZ - 1 - i, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, i + 1, i, i + 1, this.scatteredFeatureSizeX - 2 - i, i, this.scatteredFeatureSizeZ - 2 - i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            for (int i2 = 0; i2 < this.scatteredFeatureSizeX; ++i2) {
                for (int j = 0; j < this.scatteredFeatureSizeZ; ++j) {
                    final int k = -5;
                    this.replaceAirAndLiquidDownwards(worldIn, Blocks.SANDSTONE.getDefaultState(), i2, -5, j, structureBoundingBoxIn);
                }
            }
            final IBlockState iblockstate1 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.NORTH);
            final IBlockState iblockstate2 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.SOUTH);
            final IBlockState iblockstate3 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.EAST);
            final IBlockState iblockstate4 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.WEST);
            final int l = ~EnumDyeColor.ORANGE.getDyeDamage() & 0xF;
            final int i3 = ~EnumDyeColor.BLUE.getDyeDamage() & 0xF;
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.setBlockState(worldIn, iblockstate1, 2, 10, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate2, 2, 10, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate3, 0, 10, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate4, 4, 10, 2, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 5, 0, 0, this.scatteredFeatureSizeX - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 4, 10, 1, this.scatteredFeatureSizeX - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.setBlockState(worldIn, iblockstate1, this.scatteredFeatureSizeX - 3, 10, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate2, this.scatteredFeatureSizeX - 3, 10, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate3, this.scatteredFeatureSizeX - 5, 10, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate4, this.scatteredFeatureSizeX - 1, 10, 2, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, 3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 1, 1, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, this.scatteredFeatureSizeX - 6, 4, this.scatteredFeatureSizeZ - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 8, 8, 3, 8, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 8, 12, 3, 8, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 12, 8, 3, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 12, 12, 3, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 5, 1, 5, this.scatteredFeatureSizeX - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 7, 7, 9, this.scatteredFeatureSizeX - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 9, 5, 7, 11, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 6, 5, 9, this.scatteredFeatureSizeX - 6, 7, 11, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 5, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 6, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 6, 6, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), this.scatteredFeatureSizeX - 6, 5, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), this.scatteredFeatureSizeX - 6, 6, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), this.scatteredFeatureSizeX - 7, 6, 10, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 3, 4, 4, this.scatteredFeatureSizeX - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.setBlockState(worldIn, iblockstate1, 2, 4, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate1, 2, 3, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate1, this.scatteredFeatureSizeX - 3, 4, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate1, this.scatteredFeatureSizeX - 3, 3, 4, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 3, 1, 3, this.scatteredFeatureSizeX - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getDefaultState(), this.scatteredFeatureSizeX - 2, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SAND.getMetadata()), 1, 2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SAND.getMetadata()), this.scatteredFeatureSizeX - 2, 2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate4, 2, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate3, this.scatteredFeatureSizeX - 3, 1, 2, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 3, 5, 4, 3, 18, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 5, 3, 5, this.scatteredFeatureSizeX - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.scatteredFeatureSizeX - 6, 1, 5, this.scatteredFeatureSizeX - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            for (int j2 = 5; j2 <= 17; j2 += 2) {
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 4, 1, j2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 4, 2, j2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), this.scatteredFeatureSizeX - 5, 1, j2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), this.scatteredFeatureSizeX - 5, 2, j2, structureBoundingBoxIn);
            }
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 10, 0, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 10, 0, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 9, 0, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 11, 0, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 8, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 12, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 7, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 13, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 9, 0, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 11, 0, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 10, 0, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 10, 0, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(i3), 10, 0, 10, structureBoundingBoxIn);
            for (int j3 = 0; j3 <= this.scatteredFeatureSizeX - 1; j3 += this.scatteredFeatureSizeX - 1) {
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 2, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 2, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 2, 3, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 3, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 3, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 3, 3, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 4, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), j3, 4, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 4, 3, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 5, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 5, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 5, 3, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 6, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), j3, 6, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 6, 3, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 7, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 7, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), j3, 7, 3, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 8, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 8, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j3, 8, 3, structureBoundingBoxIn);
            }
            for (int k2 = 2; k2 <= this.scatteredFeatureSizeX - 3; k2 += this.scatteredFeatureSizeX - 3 - 2) {
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 - 1, 2, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2, 2, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 + 1, 2, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 - 1, 3, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2, 3, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 + 1, 3, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2 - 1, 4, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), k2, 4, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2 + 1, 4, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 - 1, 5, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2, 5, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 + 1, 5, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2 - 1, 6, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), k2, 6, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2 + 1, 6, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2 - 1, 7, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2, 7, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), k2 + 1, 7, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 - 1, 8, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2, 8, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 + 1, 8, 0, structureBoundingBoxIn);
            }
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 4, 0, 12, 6, 0, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 8, 6, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 12, 6, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 9, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(l), 11, 5, 0, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, -14, 8, 12, -11, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, -10, 8, 12, -10, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, -9, 8, 12, -9, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 8, -11, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 8, -10, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 7, -10, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 7, -11, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 12, -11, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 12, -10, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 13, -10, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 13, -11, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, -11, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, -10, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, -10, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, -11, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, -11, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, -10, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, -10, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, -11, 13, structureBoundingBoxIn);
            for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                if (!this.hasPlacedChest[enumfacing.getHorizontalIndex()]) {
                    final int k3 = enumfacing.getFrontOffsetX() * 2;
                    final int l2 = enumfacing.getFrontOffsetZ() * 2;
                    this.hasPlacedChest[enumfacing.getHorizontalIndex()] = this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 10 + k3, -11, 10 + l2, LootTableList.CHESTS_DESERT_PYRAMID);
                }
            }
            return true;
        }
    }
    
    abstract static class Feature extends StructureComponent
    {
        protected int scatteredFeatureSizeX;
        protected int scatteredFeatureSizeY;
        protected int scatteredFeatureSizeZ;
        protected int horizontalPos;
        
        public Feature() {
            this.horizontalPos = -1;
        }
        
        protected Feature(final Random rand, final int x, final int y, final int z, final int sizeX, final int sizeY, final int sizeZ) {
            super(0);
            this.horizontalPos = -1;
            this.scatteredFeatureSizeX = sizeX;
            this.scatteredFeatureSizeY = sizeY;
            this.scatteredFeatureSizeZ = sizeZ;
            this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(rand));
            if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z) {
                this.boundingBox = new StructureBoundingBox(x, y, z, x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1);
            }
            else {
                this.boundingBox = new StructureBoundingBox(x, y, z, x + sizeZ - 1, y + sizeY - 1, z + sizeX - 1);
            }
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            tagCompound.setInteger("Width", this.scatteredFeatureSizeX);
            tagCompound.setInteger("Height", this.scatteredFeatureSizeY);
            tagCompound.setInteger("Depth", this.scatteredFeatureSizeZ);
            tagCompound.setInteger("HPos", this.horizontalPos);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            this.scatteredFeatureSizeX = tagCompound.getInteger("Width");
            this.scatteredFeatureSizeY = tagCompound.getInteger("Height");
            this.scatteredFeatureSizeZ = tagCompound.getInteger("Depth");
            this.horizontalPos = tagCompound.getInteger("HPos");
        }
        
        protected boolean offsetToAverageGroundLevel(final World worldIn, final StructureBoundingBox structurebb, final int yOffset) {
            if (this.horizontalPos >= 0) {
                return true;
            }
            int i = 0;
            int j = 0;
            final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k) {
                for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l) {
                    blockpos$mutableblockpos.setPos(l, 64, k);
                    if (structurebb.isVecInside(blockpos$mutableblockpos)) {
                        i += Math.max(worldIn.getTopSolidOrLiquidBlock(blockpos$mutableblockpos).getY(), worldIn.provider.getAverageGroundLevel());
                        ++j;
                    }
                }
            }
            if (j == 0) {
                return false;
            }
            this.horizontalPos = i / j;
            this.boundingBox.offset(0, this.horizontalPos - this.boundingBox.minY + yOffset, 0);
            return true;
        }
    }
    
    public static class Igloo extends Feature
    {
        private static final ResourceLocation IGLOO_TOP_ID;
        private static final ResourceLocation IGLOO_MIDDLE_ID;
        private static final ResourceLocation IGLOO_BOTTOM_ID;
        
        static {
            IGLOO_TOP_ID = new ResourceLocation("igloo/igloo_top");
            IGLOO_MIDDLE_ID = new ResourceLocation("igloo/igloo_middle");
            IGLOO_BOTTOM_ID = new ResourceLocation("igloo/igloo_bottom");
        }
        
        public Igloo() {
        }
        
        public Igloo(final Random rand, final int x, final int z) {
            super(rand, x, 64, z, 7, 5, 8);
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, -1)) {
                return false;
            }
            final StructureBoundingBox structureboundingbox = this.getBoundingBox();
            final BlockPos blockpos = new BlockPos(structureboundingbox.minX, structureboundingbox.minY, structureboundingbox.minZ);
            final Rotation[] arotation = Rotation.values();
            final MinecraftServer minecraftserver = worldIn.getMinecraftServer();
            final TemplateManager templatemanager = worldIn.getSaveHandler().getStructureTemplateManager();
            final PlacementSettings placementsettings = new PlacementSettings().setRotation(arotation[randomIn.nextInt(arotation.length)]).setReplacedBlock(Blocks.STRUCTURE_VOID).setBoundingBox(structureboundingbox);
            final Template template = templatemanager.getTemplate(minecraftserver, Igloo.IGLOO_TOP_ID);
            template.addBlocksToWorldChunk(worldIn, blockpos, placementsettings);
            if (randomIn.nextDouble() < 0.5) {
                final Template template2 = templatemanager.getTemplate(minecraftserver, Igloo.IGLOO_MIDDLE_ID);
                final Template template3 = templatemanager.getTemplate(minecraftserver, Igloo.IGLOO_BOTTOM_ID);
                final int i = randomIn.nextInt(8) + 4;
                for (int j = 0; j < i; ++j) {
                    final BlockPos blockpos2 = template.calculateConnectedPos(placementsettings, new BlockPos(3, -1 - j * 3, 5), placementsettings, new BlockPos(1, 2, 1));
                    template2.addBlocksToWorldChunk(worldIn, blockpos.add(blockpos2), placementsettings);
                }
                final BlockPos blockpos3 = blockpos.add(template.calculateConnectedPos(placementsettings, new BlockPos(3, -1 - i * 3, 5), placementsettings, new BlockPos(3, 5, 7)));
                template3.addBlocksToWorldChunk(worldIn, blockpos3, placementsettings);
                final Map<BlockPos, String> map = template3.getDataBlocks(blockpos3, placementsettings);
                for (final Map.Entry<BlockPos, String> entry : map.entrySet()) {
                    if ("chest".equals(entry.getValue())) {
                        final BlockPos blockpos4 = entry.getKey();
                        worldIn.setBlockState(blockpos4, Blocks.AIR.getDefaultState(), 3);
                        final TileEntity tileentity = worldIn.getTileEntity(blockpos4.down());
                        if (!(tileentity instanceof TileEntityChest)) {
                            continue;
                        }
                        ((TileEntityChest)tileentity).setLootTable(LootTableList.CHESTS_IGLOO_CHEST, randomIn.nextLong());
                    }
                }
            }
            else {
                final BlockPos blockpos5 = Template.transformedBlockPos(placementsettings, new BlockPos(3, 0, 5));
                worldIn.setBlockState(blockpos.add(blockpos5), Blocks.SNOW.getDefaultState(), 3);
            }
            return true;
        }
    }
    
    public static class JunglePyramid extends Feature
    {
        private boolean placedMainChest;
        private boolean placedHiddenChest;
        private boolean placedTrap1;
        private boolean placedTrap2;
        private static final Stones junglePyramidsRandomScatteredStones;
        
        static {
            junglePyramidsRandomScatteredStones = new Stones(null);
        }
        
        public JunglePyramid() {
        }
        
        public JunglePyramid(final Random rand, final int x, final int z) {
            super(rand, x, 64, z, 12, 10, 15);
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("placedMainChest", this.placedMainChest);
            tagCompound.setBoolean("placedHiddenChest", this.placedHiddenChest);
            tagCompound.setBoolean("placedTrap1", this.placedTrap1);
            tagCompound.setBoolean("placedTrap2", this.placedTrap2);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.placedMainChest = tagCompound.getBoolean("placedMainChest");
            this.placedHiddenChest = tagCompound.getBoolean("placedHiddenChest");
            this.placedTrap1 = tagCompound.getBoolean("placedTrap1");
            this.placedTrap2 = tagCompound.getBoolean("placedTrap2");
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, 0)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0, this.scatteredFeatureSizeZ - 1, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 9, 2, 2, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 12, 9, 2, 12, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 3, 2, 2, 11, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 1, 3, 9, 2, 11, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 10, 6, 1, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 13, 10, 6, 13, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 2, 1, 6, 12, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 10, 3, 2, 10, 6, 12, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 3, 2, 9, 3, 12, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 6, 2, 9, 6, 12, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 3, 7, 3, 8, 7, 11, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 8, 4, 7, 8, 10, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 3, 1, 3, 8, 2, 11);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 4, 3, 6, 7, 3, 9);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 2, 4, 2, 9, 5, 12);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 4, 6, 5, 7, 6, 9);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 7, 6, 6, 7, 8);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 1, 2, 6, 2, 2);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 2, 12, 6, 2, 12);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 5, 1, 6, 5, 1);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 5, 13, 6, 5, 13);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 5, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, 5, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 5, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, 5, 9, structureBoundingBoxIn);
            for (int i = 0; i <= 14; i += 14) {
                this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 4, i, 2, 5, i, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
                this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 4, i, 4, 5, i, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
                this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 4, i, 7, 5, i, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
                this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 4, i, 9, 5, i, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 6, 0, 6, 6, 0, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            for (int l = 0; l <= 11; l += 11) {
                for (int j = 2; j <= 12; j += 2) {
                    this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 4, j, l, 5, j, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
                }
                this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 6, 5, l, 6, 5, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
                this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 6, 9, l, 6, 9, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 7, 2, 2, 9, 2, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 7, 2, 9, 9, 2, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 7, 12, 2, 9, 12, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 7, 12, 9, 9, 12, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 9, 4, 4, 9, 4, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 9, 4, 7, 9, 4, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 9, 10, 4, 9, 10, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 9, 10, 7, 9, 10, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 9, 7, 6, 9, 7, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            final IBlockState iblockstate2 = Blocks.STONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.EAST);
            final IBlockState iblockstate3 = Blocks.STONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.WEST);
            final IBlockState iblockstate4 = Blocks.STONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.SOUTH);
            final IBlockState iblockstate5 = Blocks.STONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.NORTH);
            this.setBlockState(worldIn, iblockstate5, 5, 9, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 6, 9, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate4, 5, 9, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate4, 6, 9, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 4, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 5, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 6, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 7, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 4, 1, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 4, 2, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 4, 3, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 7, 1, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 7, 2, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5, 7, 3, 10, structureBoundingBoxIn);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 4, 1, 9, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 1, 9, 7, 1, 9, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 10, 7, 2, 10, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, 6, 4, 5, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.setBlockState(worldIn, iblockstate2, 4, 4, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate3, 7, 4, 5, structureBoundingBoxIn);
            for (int k = 0; k < 4; ++k) {
                this.setBlockState(worldIn, iblockstate4, 5, 0 - k, 6 + k, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate4, 6, 0 - k, 6 + k, structureBoundingBoxIn);
                this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
            }
            this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 12, 10, -1, 13);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 1, 3, -1, 13);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 1, 9, -1, 5);
            for (int i2 = 1; i2 <= 13; i2 += 2) {
                this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, -3, i2, 1, -2, i2, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            }
            for (int j2 = 2; j2 <= 12; j2 += 2) {
                this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, -1, j2, 3, -1, j2, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, -2, 1, 5, -2, 1, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, -2, 1, 9, -2, 1, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 6, -3, 1, 6, -3, 1, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 6, -1, 1, 6, -1, 1, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWireHook.FACING, EnumFacing.EAST).withProperty((IProperty<Comparable>)BlockTripWireHook.ATTACHED, true), 1, -3, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWireHook.FACING, EnumFacing.WEST).withProperty((IProperty<Comparable>)BlockTripWireHook.ATTACHED, true), 4, -3, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWire.ATTACHED, true), 2, -3, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWire.ATTACHED, true), 3, -3, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 4, -3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, structureBoundingBoxIn);
            if (!this.placedTrap1) {
                this.placedTrap1 = this.createDispenser(worldIn, structureBoundingBoxIn, randomIn, 3, -2, 1, EnumFacing.NORTH, LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER);
            }
            this.setBlockState(worldIn, Blocks.VINE.getDefaultState().withProperty((IProperty<Comparable>)BlockVine.SOUTH, true), 3, -2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWireHook.FACING, EnumFacing.NORTH).withProperty((IProperty<Comparable>)BlockTripWireHook.ATTACHED, true), 7, -3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWireHook.FACING, EnumFacing.SOUTH).withProperty((IProperty<Comparable>)BlockTripWireHook.ATTACHED, true), 7, -3, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWire.ATTACHED, true), 7, -3, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWire.ATTACHED, true), 7, -3, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty((IProperty<Comparable>)BlockTripWire.ATTACHED, true), 7, -3, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -3, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -2, 4, structureBoundingBoxIn);
            if (!this.placedTrap2) {
                this.placedTrap2 = this.createDispenser(worldIn, structureBoundingBoxIn, randomIn, 9, -2, 3, EnumFacing.WEST, LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER);
            }
            this.setBlockState(worldIn, Blocks.VINE.getDefaultState().withProperty((IProperty<Comparable>)BlockVine.EAST, true), 8, -1, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.VINE.getDefaultState().withProperty((IProperty<Comparable>)BlockVine.EAST, true), 8, -2, 3, structureBoundingBoxIn);
            if (!this.placedMainChest) {
                this.placedMainChest = this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 8, -3, 3, LootTableList.CHESTS_JUNGLE_TEMPLE);
            }
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, structureBoundingBoxIn);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, -1, 1, 9, -1, 5, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 8, -3, 8, 10, -1, 10);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getStateFromMeta(BlockStoneBrick.CHISELED_META), 8, -2, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getStateFromMeta(BlockStoneBrick.CHISELED_META), 9, -2, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getStateFromMeta(BlockStoneBrick.CHISELED_META), 10, -2, 11, structureBoundingBoxIn);
            final IBlockState iblockstate6 = Blocks.LEVER.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.NORTH);
            this.setBlockState(worldIn, iblockstate6, 8, -2, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate6, 9, -2, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate6, 10, -2, 12, structureBoundingBoxIn);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, -3, 8, 8, -3, 10, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 10, -3, 8, 10, -3, 10, false, randomIn, JunglePyramid.junglePyramidsRandomScatteredStones);
            this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -2, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -2, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 10, -1, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().withProperty((IProperty<Comparable>)BlockPistonBase.FACING, EnumFacing.UP), 9, -2, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().withProperty((IProperty<Comparable>)BlockPistonBase.FACING, EnumFacing.WEST), 10, -2, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().withProperty((IProperty<Comparable>)BlockPistonBase.FACING, EnumFacing.WEST), 10, -1, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty((IProperty<Comparable>)BlockRedstoneRepeater.FACING, EnumFacing.NORTH), 10, -2, 10, structureBoundingBoxIn);
            if (!this.placedHiddenChest) {
                this.placedHiddenChest = this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 9, -3, 10, LootTableList.CHESTS_JUNGLE_TEMPLE);
            }
            return true;
        }
        
        static class Stones extends BlockSelector
        {
            private Stones() {
            }
            
            @Override
            public void selectBlocks(final Random rand, final int x, final int y, final int z, final boolean p_75062_5_) {
                if (rand.nextFloat() < 0.4f) {
                    this.blockstate = Blocks.COBBLESTONE.getDefaultState();
                }
                else {
                    this.blockstate = Blocks.MOSSY_COBBLESTONE.getDefaultState();
                }
            }
        }
    }
    
    public static class SwampHut extends Feature
    {
        private boolean hasWitch;
        
        public SwampHut() {
        }
        
        public SwampHut(final Random p_i2066_1_, final int p_i2066_2_, final int p_i2066_3_) {
            super(p_i2066_1_, p_i2066_2_, 64, p_i2066_3_, 7, 7, 9);
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("Witch", this.hasWitch);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.hasWitch = tagCompound.getBoolean("Witch");
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, 0)) {
                return false;
            }
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 5, 1, 7, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 5, 4, 7, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 0, 4, 1, 0, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 2, 3, 3, 2, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 3, 1, 3, 6, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 3, 5, 3, 6, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 7, 4, 3, 7, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 2, 1, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 2, 5, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 7, 1, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 7, 5, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 3, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, BlockFlowerPot.EnumFlowerType.MUSHROOM_RED), 1, 3, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, structureBoundingBoxIn);
            final IBlockState iblockstate = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.NORTH);
            final IBlockState iblockstate2 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.EAST);
            final IBlockState iblockstate3 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.WEST);
            final IBlockState iblockstate4 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.SOUTH);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 6, 4, 1, iblockstate, iblockstate, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 2, 0, 4, 7, iblockstate2, iblockstate2, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 2, 6, 4, 7, iblockstate3, iblockstate3, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 8, 6, 4, 8, iblockstate4, iblockstate4, false);
            for (int i = 2; i <= 7; i += 5) {
                for (int j = 1; j <= 5; j += 4) {
                    this.replaceAirAndLiquidDownwards(worldIn, Blocks.LOG.getDefaultState(), j, -1, i, structureBoundingBoxIn);
                }
            }
            if (!this.hasWitch) {
                final int l = this.getXWithOffset(2, 5);
                final int i2 = this.getYWithOffset(2);
                final int k = this.getZWithOffset(2, 5);
                if (structureBoundingBoxIn.isVecInside(new BlockPos(l, i2, k))) {
                    this.hasWitch = true;
                    final EntityWitch entitywitch = new EntityWitch(worldIn);
                    entitywitch.enablePersistence();
                    entitywitch.setLocationAndAngles(l + 0.5, i2, k + 0.5, 0.0f, 0.0f);
                    entitywitch.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(l, i2, k)), null);
                    worldIn.spawnEntityInWorld(entitywitch);
                }
            }
            return true;
        }
    }
}
