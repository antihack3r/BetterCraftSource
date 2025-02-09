/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.config;

import net.minecraft.block.state.BlockStateBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.MatchBlock;

public class Matches {
    public static boolean block(BlockStateBase blockStateBase, MatchBlock[] matchBlocks) {
        if (matchBlocks == null) {
            return true;
        }
        int i2 = 0;
        while (i2 < matchBlocks.length) {
            MatchBlock matchblock = matchBlocks[i2];
            if (matchblock.matches(blockStateBase)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean block(int blockId, int metadata, MatchBlock[] matchBlocks) {
        if (matchBlocks == null) {
            return true;
        }
        int i2 = 0;
        while (i2 < matchBlocks.length) {
            MatchBlock matchblock = matchBlocks[i2];
            if (matchblock.matches(blockId, metadata)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean blockId(int blockId, MatchBlock[] matchBlocks) {
        if (matchBlocks == null) {
            return true;
        }
        int i2 = 0;
        while (i2 < matchBlocks.length) {
            MatchBlock matchblock = matchBlocks[i2];
            if (matchblock.getBlockId() == blockId) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean metadata(int metadata, int[] metadatas) {
        if (metadatas == null) {
            return true;
        }
        int i2 = 0;
        while (i2 < metadatas.length) {
            if (metadatas[i2] == metadata) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean sprite(TextureAtlasSprite sprite, TextureAtlasSprite[] sprites) {
        if (sprites == null) {
            return true;
        }
        int i2 = 0;
        while (i2 < sprites.length) {
            if (sprites[i2] == sprite) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean biome(BiomeGenBase biome, BiomeGenBase[] biomes) {
        if (biomes == null) {
            return true;
        }
        int i2 = 0;
        while (i2 < biomes.length) {
            if (biomes[i2] == biome) {
                return true;
            }
            ++i2;
        }
        return false;
    }
}

