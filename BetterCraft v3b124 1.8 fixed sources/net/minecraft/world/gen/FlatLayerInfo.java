/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class FlatLayerInfo {
    private final int field_175902_a;
    private IBlockState layerMaterial;
    private int layerCount = 1;
    private int layerMinimumY;

    public FlatLayerInfo(int p_i45467_1_, Block p_i45467_2_) {
        this(3, p_i45467_1_, p_i45467_2_);
    }

    public FlatLayerInfo(int p_i45627_1_, int height, Block layerMaterialIn) {
        this.field_175902_a = p_i45627_1_;
        this.layerCount = height;
        this.layerMaterial = layerMaterialIn.getDefaultState();
    }

    public FlatLayerInfo(int p_i45628_1_, int p_i45628_2_, Block p_i45628_3_, int p_i45628_4_) {
        this(p_i45628_1_, p_i45628_2_, p_i45628_3_);
        this.layerMaterial = p_i45628_3_.getStateFromMeta(p_i45628_4_);
    }

    public int getLayerCount() {
        return this.layerCount;
    }

    public IBlockState getLayerMaterial() {
        return this.layerMaterial;
    }

    private Block getLayerMaterialBlock() {
        return this.layerMaterial.getBlock();
    }

    private int getFillBlockMeta() {
        return this.layerMaterial.getBlock().getMetaFromState(this.layerMaterial);
    }

    public int getMinY() {
        return this.layerMinimumY;
    }

    public void setMinY(int minY) {
        this.layerMinimumY = minY;
    }

    public String toString() {
        int i2;
        String s2;
        if (this.field_175902_a >= 3) {
            ResourceLocation resourcelocation = (ResourceLocation)Block.blockRegistry.getNameForObject(this.getLayerMaterialBlock());
            String string = s2 = resourcelocation == null ? "null" : resourcelocation.toString();
            if (this.layerCount > 1) {
                s2 = String.valueOf(this.layerCount) + "*" + s2;
            }
        } else {
            s2 = Integer.toString(Block.getIdFromBlock(this.getLayerMaterialBlock()));
            if (this.layerCount > 1) {
                s2 = String.valueOf(this.layerCount) + "x" + s2;
            }
        }
        if ((i2 = this.getFillBlockMeta()) > 0) {
            s2 = String.valueOf(s2) + ":" + i2;
        }
        return s2;
    }
}

