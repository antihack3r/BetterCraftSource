/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class StructureMineshaftPieces {
    private static final List<WeightedRandomChestContent> CHEST_CONTENT_WEIGHT_LIST = Lists.newArrayList(new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5), new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5), new WeightedRandomChestContent(Items.dye, EnumDyeColor.BLUE.getDyeDamage(), 4, 9, 5), new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 3), new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10), new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15), new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.rail), 0, 4, 8, 1), new WeightedRandomChestContent(Items.melon_seeds, 0, 2, 4, 10), new WeightedRandomChestContent(Items.pumpkin_seeds, 0, 2, 4, 10), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1));

    public static void registerStructurePieces() {
        MapGenStructureIO.registerStructureComponent(Corridor.class, "MSCorridor");
        MapGenStructureIO.registerStructureComponent(Cross.class, "MSCrossing");
        MapGenStructureIO.registerStructureComponent(Room.class, "MSRoom");
        MapGenStructureIO.registerStructureComponent(Stairs.class, "MSStairs");
    }

    private static StructureComponent func_175892_a(List<StructureComponent> listIn, Random rand, int x2, int y2, int z2, EnumFacing facing, int type) {
        int i2 = rand.nextInt(100);
        if (i2 >= 80) {
            StructureBoundingBox structureboundingbox = Cross.func_175813_a(listIn, rand, x2, y2, z2, facing);
            if (structureboundingbox != null) {
                return new Cross(type, rand, structureboundingbox, facing);
            }
        } else if (i2 >= 70) {
            StructureBoundingBox structureboundingbox1 = Stairs.func_175812_a(listIn, rand, x2, y2, z2, facing);
            if (structureboundingbox1 != null) {
                return new Stairs(type, rand, structureboundingbox1, facing);
            }
        } else {
            StructureBoundingBox structureboundingbox2 = Corridor.func_175814_a(listIn, rand, x2, y2, z2, facing);
            if (structureboundingbox2 != null) {
                return new Corridor(type, rand, structureboundingbox2, facing);
            }
        }
        return null;
    }

    private static StructureComponent func_175890_b(StructureComponent componentIn, List<StructureComponent> listIn, Random rand, int x2, int y2, int z2, EnumFacing facing, int type) {
        if (type > 8) {
            return null;
        }
        if (Math.abs(x2 - componentIn.getBoundingBox().minX) <= 80 && Math.abs(z2 - componentIn.getBoundingBox().minZ) <= 80) {
            StructureComponent structurecomponent = StructureMineshaftPieces.func_175892_a(listIn, rand, x2, y2, z2, facing, type + 1);
            if (structurecomponent != null) {
                listIn.add(structurecomponent);
                structurecomponent.buildComponent(componentIn, listIn, rand);
            }
            return structurecomponent;
        }
        return null;
    }

    public static class Corridor
    extends StructureComponent {
        private boolean hasRails;
        private boolean hasSpiders;
        private boolean spawnerPlaced;
        private int sectionCount;

        public Corridor() {
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("hr", this.hasRails);
            tagCompound.setBoolean("sc", this.hasSpiders);
            tagCompound.setBoolean("hps", this.spawnerPlaced);
            tagCompound.setInteger("Num", this.sectionCount);
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound) {
            this.hasRails = tagCompound.getBoolean("hr");
            this.hasSpiders = tagCompound.getBoolean("sc");
            this.spawnerPlaced = tagCompound.getBoolean("hps");
            this.sectionCount = tagCompound.getInteger("Num");
        }

        public Corridor(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
            super(type);
            this.coordBaseMode = facing;
            this.boundingBox = structurebb;
            this.hasRails = rand.nextInt(3) == 0;
            this.hasSpiders = !this.hasRails && rand.nextInt(23) == 0;
            this.sectionCount = this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH ? structurebb.getXSize() / 5 : structurebb.getZSize() / 5;
        }

        public static StructureBoundingBox func_175814_a(List<StructureComponent> p_175814_0_, Random rand, int x2, int y2, int z2, EnumFacing facing) {
            StructureBoundingBox structureboundingbox = new StructureBoundingBox(x2, y2, z2, x2, y2 + 2, z2);
            int i2 = rand.nextInt(3) + 2;
            while (i2 > 0) {
                int j2 = i2 * 5;
                switch (facing) {
                    case NORTH: {
                        structureboundingbox.maxX = x2 + 2;
                        structureboundingbox.minZ = z2 - (j2 - 1);
                        break;
                    }
                    case SOUTH: {
                        structureboundingbox.maxX = x2 + 2;
                        structureboundingbox.maxZ = z2 + (j2 - 1);
                        break;
                    }
                    case WEST: {
                        structureboundingbox.minX = x2 - (j2 - 1);
                        structureboundingbox.maxZ = z2 + 2;
                        break;
                    }
                    case EAST: {
                        structureboundingbox.maxX = x2 + (j2 - 1);
                        structureboundingbox.maxZ = z2 + 2;
                    }
                }
                if (StructureComponent.findIntersecting(p_175814_0_, structureboundingbox) == null) break;
                --i2;
            }
            return i2 > 0 ? structureboundingbox : null;
        }

        @Override
        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
            block25: {
                int i2 = this.getComponentType();
                int j2 = rand.nextInt(4);
                if (this.coordBaseMode != null) {
                    switch (this.coordBaseMode) {
                        case NORTH: {
                            if (j2 <= 1) {
                                StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, this.coordBaseMode, i2);
                                break;
                            }
                            if (j2 == 2) {
                                StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.WEST, i2);
                                break;
                            }
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.EAST, i2);
                            break;
                        }
                        case SOUTH: {
                            if (j2 <= 1) {
                                StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, this.coordBaseMode, i2);
                                break;
                            }
                            if (j2 == 2) {
                                StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.WEST, i2);
                                break;
                            }
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.EAST, i2);
                            break;
                        }
                        case WEST: {
                            if (j2 <= 1) {
                                StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, this.coordBaseMode, i2);
                                break;
                            }
                            if (j2 == 2) {
                                StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, i2);
                                break;
                            }
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2);
                            break;
                        }
                        case EAST: {
                            if (j2 <= 1) {
                                StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, this.coordBaseMode, i2);
                                break;
                            }
                            if (j2 == 2) {
                                StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, i2);
                                break;
                            }
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2);
                        }
                    }
                }
                if (i2 >= 8) break block25;
                if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
                    int i1 = this.boundingBox.minX + 3;
                    while (i1 + 3 <= this.boundingBox.maxX) {
                        int j1 = rand.nextInt(5);
                        if (j1 == 0) {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, i1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i2 + 1);
                        } else if (j1 == 1) {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, i1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2 + 1);
                        }
                        i1 += 5;
                    }
                } else {
                    int k2 = this.boundingBox.minZ + 3;
                    while (k2 + 3 <= this.boundingBox.maxZ) {
                        int l2 = rand.nextInt(5);
                        if (l2 == 0) {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, k2, EnumFacing.WEST, i2 + 1);
                        } else if (l2 == 1) {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, k2, EnumFacing.EAST, i2 + 1);
                        }
                        k2 += 5;
                    }
                }
            }
        }

        @Override
        protected boolean generateChestContents(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x2, int y2, int z2, List<WeightedRandomChestContent> listIn, int max) {
            BlockPos blockpos = new BlockPos(this.getXWithOffset(x2, z2), this.getYWithOffset(y2), this.getZWithOffset(x2, z2));
            if (boundingBoxIn.isVecInside(blockpos) && worldIn.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
                int i2 = rand.nextBoolean() ? 1 : 0;
                worldIn.setBlockState(blockpos, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, i2)), 2);
                EntityMinecartChest entityminecartchest = new EntityMinecartChest(worldIn, (float)blockpos.getX() + 0.5f, (float)blockpos.getY() + 0.5f, (float)blockpos.getZ() + 0.5f);
                WeightedRandomChestContent.generateChestContents(rand, listIn, entityminecartchest, max);
                worldIn.spawnEntityInWorld(entityminecartchest);
                return true;
            }
            return false;
        }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            boolean i2 = false;
            int j2 = 2;
            boolean k2 = false;
            int l2 = 2;
            int i1 = this.sectionCount * 5 - 1;
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 1, i1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            this.func_175805_a(worldIn, structureBoundingBoxIn, randomIn, 0.8f, 0, 2, 0, 2, 2, i1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            if (this.hasSpiders) {
                this.func_175805_a(worldIn, structureBoundingBoxIn, randomIn, 0.6f, 0, 0, 0, 2, 1, i1, Blocks.web.getDefaultState(), Blocks.air.getDefaultState(), false);
            }
            int j1 = 0;
            while (j1 < this.sectionCount) {
                int k1 = 2 + j1 * 5;
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, k1, 0, 1, k1, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, k1, 2, 1, k1, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);
                if (randomIn.nextInt(4) == 0) {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, k1, 0, 2, k1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, k1, 2, 2, k1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                } else {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, k1, 2, 2, k1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                }
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1f, 0, 2, k1 - 1, Blocks.web.getDefaultState());
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1f, 2, 2, k1 - 1, Blocks.web.getDefaultState());
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1f, 0, 2, k1 + 1, Blocks.web.getDefaultState());
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1f, 2, 2, k1 + 1, Blocks.web.getDefaultState());
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05f, 0, 2, k1 - 2, Blocks.web.getDefaultState());
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05f, 2, 2, k1 - 2, Blocks.web.getDefaultState());
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05f, 0, 2, k1 + 2, Blocks.web.getDefaultState());
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05f, 2, 2, k1 + 2, Blocks.web.getDefaultState());
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05f, 1, 2, k1 - 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05f, 1, 2, k1 + 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
                if (randomIn.nextInt(100) == 0) {
                    this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 2, 0, k1 - 1, WeightedRandomChestContent.func_177629_a(CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(randomIn)), 3 + randomIn.nextInt(4));
                }
                if (randomIn.nextInt(100) == 0) {
                    this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 0, 0, k1 + 1, WeightedRandomChestContent.func_177629_a(CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(randomIn)), 3 + randomIn.nextInt(4));
                }
                if (this.hasSpiders && !this.spawnerPlaced) {
                    int l1 = this.getYWithOffset(0);
                    int i22 = k1 - 1 + randomIn.nextInt(3);
                    int j22 = this.getXWithOffset(1, i22);
                    BlockPos blockpos = new BlockPos(j22, l1, i22 = this.getZWithOffset(1, i22));
                    if (structureBoundingBoxIn.isVecInside(blockpos)) {
                        this.spawnerPlaced = true;
                        worldIn.setBlockState(blockpos, Blocks.mob_spawner.getDefaultState(), 2);
                        TileEntity tileentity = worldIn.getTileEntity(blockpos);
                        if (tileentity instanceof TileEntityMobSpawner) {
                            ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic().setEntityName("CaveSpider");
                        }
                    }
                }
                ++j1;
            }
            int k22 = 0;
            while (k22 <= 2) {
                int i3 = 0;
                while (i3 <= i1) {
                    int j3 = -1;
                    IBlockState iblockstate1 = this.getBlockStateFromPos(worldIn, k22, j3, i3, structureBoundingBoxIn);
                    if (iblockstate1.getBlock().getMaterial() == Material.air) {
                        int k3 = -1;
                        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), k22, k3, i3, structureBoundingBoxIn);
                    }
                    ++i3;
                }
                ++k22;
            }
            if (this.hasRails) {
                int l22 = 0;
                while (l22 <= i1) {
                    IBlockState iblockstate = this.getBlockStateFromPos(worldIn, 1, -1, l22, structureBoundingBoxIn);
                    if (iblockstate.getBlock().getMaterial() != Material.air && iblockstate.getBlock().isFullBlock()) {
                        this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.7f, 1, 0, l22, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, 0)));
                    }
                    ++l22;
                }
            }
            return true;
        }
    }

    public static class Cross
    extends StructureComponent {
        private EnumFacing corridorDirection;
        private boolean isMultipleFloors;

        public Cross() {
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("tf", this.isMultipleFloors);
            tagCompound.setInteger("D", this.corridorDirection.getHorizontalIndex());
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound) {
            this.isMultipleFloors = tagCompound.getBoolean("tf");
            this.corridorDirection = EnumFacing.getHorizontal(tagCompound.getInteger("D"));
        }

        public Cross(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
            super(type);
            this.corridorDirection = facing;
            this.boundingBox = structurebb;
            this.isMultipleFloors = structurebb.getYSize() > 3;
        }

        public static StructureBoundingBox func_175813_a(List<StructureComponent> listIn, Random rand, int x2, int y2, int z2, EnumFacing facing) {
            StructureBoundingBox structureboundingbox = new StructureBoundingBox(x2, y2, z2, x2, y2 + 2, z2);
            if (rand.nextInt(4) == 0) {
                structureboundingbox.maxY += 4;
            }
            switch (facing) {
                case NORTH: {
                    structureboundingbox.minX = x2 - 1;
                    structureboundingbox.maxX = x2 + 3;
                    structureboundingbox.minZ = z2 - 4;
                    break;
                }
                case SOUTH: {
                    structureboundingbox.minX = x2 - 1;
                    structureboundingbox.maxX = x2 + 3;
                    structureboundingbox.maxZ = z2 + 4;
                    break;
                }
                case WEST: {
                    structureboundingbox.minX = x2 - 4;
                    structureboundingbox.minZ = z2 - 1;
                    structureboundingbox.maxZ = z2 + 3;
                    break;
                }
                case EAST: {
                    structureboundingbox.maxX = x2 + 4;
                    structureboundingbox.minZ = z2 - 1;
                    structureboundingbox.maxZ = z2 + 3;
                }
            }
            return StructureComponent.findIntersecting(listIn, structureboundingbox) != null ? null : structureboundingbox;
        }

        @Override
        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
            int i2 = this.getComponentType();
            switch (this.corridorDirection) {
                case NORTH: {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i2);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i2);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i2);
                    break;
                }
                case SOUTH: {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i2);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i2);
                    break;
                }
                case WEST: {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i2);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i2);
                    break;
                }
                case EAST: {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i2);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i2);
                }
            }
            if (this.isMultipleFloors) {
                if (rand.nextBoolean()) {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, i2);
                }
                if (rand.nextBoolean()) {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, i2);
                }
                if (rand.nextBoolean()) {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, i2);
                }
                if (rand.nextBoolean()) {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2);
                }
            }
        }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            if (this.isMultipleFloors) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            } else {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            }
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.minX + 1, this.boundingBox.maxY, this.boundingBox.minZ + 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.minX + 1, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.minZ + 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
            int i2 = this.boundingBox.minX;
            while (i2 <= this.boundingBox.maxX) {
                int j2 = this.boundingBox.minZ;
                while (j2 <= this.boundingBox.maxZ) {
                    if (this.getBlockStateFromPos(worldIn, i2, this.boundingBox.minY - 1, j2, structureBoundingBoxIn).getBlock().getMaterial() == Material.air) {
                        this.setBlockState(worldIn, Blocks.planks.getDefaultState(), i2, this.boundingBox.minY - 1, j2, structureBoundingBoxIn);
                    }
                    ++j2;
                }
                ++i2;
            }
            return true;
        }
    }

    public static class Room
    extends StructureComponent {
        private List<StructureBoundingBox> roomsLinkedToTheRoom = Lists.newLinkedList();

        public Room() {
        }

        public Room(int type, Random rand, int x2, int z2) {
            super(type);
            this.boundingBox = new StructureBoundingBox(x2, 50, z2, x2 + 7 + rand.nextInt(6), 54 + rand.nextInt(6), z2 + 7 + rand.nextInt(6));
        }

        @Override
        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
            int i2 = this.getComponentType();
            int j2 = this.boundingBox.getYSize() - 3 - 1;
            if (j2 <= 0) {
                j2 = 1;
            }
            int k2 = 0;
            boolean lvt_5_1_ = false;
            while (k2 < this.boundingBox.getXSize()) {
                if ((k2 += rand.nextInt(this.boundingBox.getXSize())) + 3 > this.boundingBox.getXSize()) break;
                StructureComponent structurecomponent = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + k2, this.boundingBox.minY + rand.nextInt(j2) + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, i2);
                if (structurecomponent != null) {
                    StructureBoundingBox structureboundingbox = structurecomponent.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(structureboundingbox.minX, structureboundingbox.minY, this.boundingBox.minZ, structureboundingbox.maxX, structureboundingbox.maxY, this.boundingBox.minZ + 1));
                }
                k2 += 4;
            }
            k2 = 0;
            while (k2 < this.boundingBox.getXSize()) {
                if ((k2 += rand.nextInt(this.boundingBox.getXSize())) + 3 > this.boundingBox.getXSize()) break;
                StructureComponent structurecomponent1 = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + k2, this.boundingBox.minY + rand.nextInt(j2) + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2);
                if (structurecomponent1 != null) {
                    StructureBoundingBox structureboundingbox1 = structurecomponent1.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(structureboundingbox1.minX, structureboundingbox1.minY, this.boundingBox.maxZ - 1, structureboundingbox1.maxX, structureboundingbox1.maxY, this.boundingBox.maxZ));
                }
                k2 += 4;
            }
            k2 = 0;
            while (k2 < this.boundingBox.getZSize()) {
                if ((k2 += rand.nextInt(this.boundingBox.getZSize())) + 3 > this.boundingBox.getZSize()) break;
                StructureComponent structurecomponent2 = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + rand.nextInt(j2) + 1, this.boundingBox.minZ + k2, EnumFacing.WEST, i2);
                if (structurecomponent2 != null) {
                    StructureBoundingBox structureboundingbox2 = structurecomponent2.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(this.boundingBox.minX, structureboundingbox2.minY, structureboundingbox2.minZ, this.boundingBox.minX + 1, structureboundingbox2.maxY, structureboundingbox2.maxZ));
                }
                k2 += 4;
            }
            k2 = 0;
            while (k2 < this.boundingBox.getZSize()) {
                if ((k2 += rand.nextInt(this.boundingBox.getZSize())) + 3 > this.boundingBox.getZSize()) break;
                StructureComponent structurecomponent3 = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + rand.nextInt(j2) + 1, this.boundingBox.minZ + k2, EnumFacing.EAST, i2);
                if (structurecomponent3 != null) {
                    StructureBoundingBox structureboundingbox3 = structurecomponent3.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(this.boundingBox.maxX - 1, structureboundingbox3.minY, structureboundingbox3.minZ, this.boundingBox.maxX, structureboundingbox3.maxY, structureboundingbox3.maxZ));
                }
                k2 += 4;
            }
        }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.dirt.getDefaultState(), Blocks.air.getDefaultState(), true);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            for (StructureBoundingBox structureboundingbox : this.roomsLinkedToTheRoom) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, structureboundingbox.minX, structureboundingbox.maxY - 2, structureboundingbox.minZ, structureboundingbox.maxX, structureboundingbox.maxY, structureboundingbox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            }
            this.randomlyRareFillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), false);
            return true;
        }

        @Override
        public void func_181138_a(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
            super.func_181138_a(p_181138_1_, p_181138_2_, p_181138_3_);
            for (StructureBoundingBox structureboundingbox : this.roomsLinkedToTheRoom) {
                structureboundingbox.offset(p_181138_1_, p_181138_2_, p_181138_3_);
            }
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
            NBTTagList nbttaglist = new NBTTagList();
            for (StructureBoundingBox structureboundingbox : this.roomsLinkedToTheRoom) {
                nbttaglist.appendTag(structureboundingbox.toNBTTagIntArray());
            }
            tagCompound.setTag("Entrances", nbttaglist);
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound) {
            NBTTagList nbttaglist = tagCompound.getTagList("Entrances", 11);
            int i2 = 0;
            while (i2 < nbttaglist.tagCount()) {
                this.roomsLinkedToTheRoom.add(new StructureBoundingBox(nbttaglist.getIntArrayAt(i2)));
                ++i2;
            }
        }
    }

    public static class Stairs
    extends StructureComponent {
        public Stairs() {
        }

        public Stairs(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
            super(type);
            this.coordBaseMode = facing;
            this.boundingBox = structurebb;
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound) {
        }

        public static StructureBoundingBox func_175812_a(List<StructureComponent> listIn, Random rand, int x2, int y2, int z2, EnumFacing facing) {
            StructureBoundingBox structureboundingbox = new StructureBoundingBox(x2, y2 - 5, z2, x2, y2 + 2, z2);
            switch (facing) {
                case NORTH: {
                    structureboundingbox.maxX = x2 + 2;
                    structureboundingbox.minZ = z2 - 8;
                    break;
                }
                case SOUTH: {
                    structureboundingbox.maxX = x2 + 2;
                    structureboundingbox.maxZ = z2 + 8;
                    break;
                }
                case WEST: {
                    structureboundingbox.minX = x2 - 8;
                    structureboundingbox.maxZ = z2 + 2;
                    break;
                }
                case EAST: {
                    structureboundingbox.maxX = x2 + 8;
                    structureboundingbox.maxZ = z2 + 2;
                }
            }
            return StructureComponent.findIntersecting(listIn, structureboundingbox) != null ? null : structureboundingbox;
        }

        @Override
        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
            int i2 = this.getComponentType();
            if (this.coordBaseMode != null) {
                switch (this.coordBaseMode) {
                    case NORTH: {
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i2);
                        break;
                    }
                    case SOUTH: {
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i2);
                        break;
                    }
                    case WEST: {
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, i2);
                        break;
                    }
                    case EAST: {
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, i2);
                    }
                }
            }
        }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 2, 7, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 7, 2, 2, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
            int i2 = 0;
            while (i2 < 5) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5 - i2 - (i2 < 4 ? 1 : 0), 2 + i2, 2, 7 - i2, 2 + i2, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                ++i2;
            }
            return true;
        }
    }
}

