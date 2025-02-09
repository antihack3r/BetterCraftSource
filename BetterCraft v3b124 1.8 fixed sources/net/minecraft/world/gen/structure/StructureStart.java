/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public abstract class StructureStart {
    protected LinkedList<StructureComponent> components = new LinkedList();
    protected StructureBoundingBox boundingBox;
    private int chunkPosX;
    private int chunkPosZ;

    public StructureStart() {
    }

    public StructureStart(int chunkX, int chunkZ) {
        this.chunkPosX = chunkX;
        this.chunkPosZ = chunkZ;
    }

    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public LinkedList<StructureComponent> getComponents() {
        return this.components;
    }

    public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb) {
        Iterator iterator = this.components.iterator();
        while (iterator.hasNext()) {
            StructureComponent structurecomponent = (StructureComponent)iterator.next();
            if (!structurecomponent.getBoundingBox().intersectsWith(structurebb) || structurecomponent.addComponentParts(worldIn, rand, structurebb)) continue;
            iterator.remove();
        }
    }

    protected void updateBoundingBox() {
        this.boundingBox = StructureBoundingBox.getNewBoundingBox();
        for (StructureComponent structurecomponent : this.components) {
            this.boundingBox.expandTo(structurecomponent.getBoundingBox());
        }
    }

    public NBTTagCompound writeStructureComponentsToNBT(int chunkX, int chunkZ) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", MapGenStructureIO.getStructureStartName(this));
        nbttagcompound.setInteger("ChunkX", chunkX);
        nbttagcompound.setInteger("ChunkZ", chunkZ);
        nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
        NBTTagList nbttaglist = new NBTTagList();
        for (StructureComponent structurecomponent : this.components) {
            nbttaglist.appendTag(structurecomponent.createStructureBaseNBT());
        }
        nbttagcompound.setTag("Children", nbttaglist);
        this.writeToNBT(nbttagcompound);
        return nbttagcompound;
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
    }

    public void readStructureComponentsFromNBT(World worldIn, NBTTagCompound tagCompound) {
        this.chunkPosX = tagCompound.getInteger("ChunkX");
        this.chunkPosZ = tagCompound.getInteger("ChunkZ");
        if (tagCompound.hasKey("BB")) {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }
        NBTTagList nbttaglist = tagCompound.getTagList("Children", 10);
        int i2 = 0;
        while (i2 < nbttaglist.tagCount()) {
            this.components.add(MapGenStructureIO.getStructureComponent(nbttaglist.getCompoundTagAt(i2), worldIn));
            ++i2;
        }
        this.readFromNBT(tagCompound);
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
    }

    protected void markAvailableHeight(World worldIn, Random rand, int p_75067_3_) {
        int i2 = worldIn.getSeaLevel() - p_75067_3_;
        int j2 = this.boundingBox.getYSize() + 1;
        if (j2 < i2) {
            j2 += rand.nextInt(i2 - j2);
        }
        int k2 = j2 - this.boundingBox.maxY;
        this.boundingBox.offset(0, k2, 0);
        for (StructureComponent structurecomponent : this.components) {
            structurecomponent.func_181138_a(0, k2, 0);
        }
    }

    protected void setRandomHeight(World worldIn, Random rand, int p_75070_3_, int p_75070_4_) {
        int i2 = p_75070_4_ - p_75070_3_ + 1 - this.boundingBox.getYSize();
        int j2 = 1;
        j2 = i2 > 1 ? p_75070_3_ + rand.nextInt(i2) : p_75070_3_;
        int k2 = j2 - this.boundingBox.minY;
        this.boundingBox.offset(0, k2, 0);
        for (StructureComponent structurecomponent : this.components) {
            structurecomponent.func_181138_a(0, k2, 0);
        }
    }

    public boolean isSizeableStructure() {
        return true;
    }

    public boolean func_175788_a(ChunkCoordIntPair pair) {
        return true;
    }

    public void func_175787_b(ChunkCoordIntPair pair) {
    }

    public int getChunkPosX() {
        return this.chunkPosX;
    }

    public int getChunkPosZ() {
        return this.chunkPosZ;
    }
}

