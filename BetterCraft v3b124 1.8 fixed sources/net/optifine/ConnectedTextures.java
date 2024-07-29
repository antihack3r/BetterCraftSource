/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.BetterGrass;
import net.optifine.BlockDir;
import net.optifine.ConnectedProperties;
import net.optifine.ConnectedTexturesCompact;
import net.optifine.config.Matches;
import net.optifine.model.BlockModelUtils;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.TileEntityUtils;

public class ConnectedTextures {
    private static Map[] spriteQuadMaps = null;
    private static Map[] spriteQuadFullMaps = null;
    private static Map[][] spriteQuadCompactMaps = null;
    private static ConnectedProperties[][] blockProperties = null;
    private static ConnectedProperties[][] tileProperties = null;
    private static boolean multipass = false;
    protected static final int UNKNOWN = -1;
    protected static final int Y_NEG_DOWN = 0;
    protected static final int Y_POS_UP = 1;
    protected static final int Z_NEG_NORTH = 2;
    protected static final int Z_POS_SOUTH = 3;
    protected static final int X_NEG_WEST = 4;
    protected static final int X_POS_EAST = 5;
    private static final int Y_AXIS = 0;
    private static final int Z_AXIS = 1;
    private static final int X_AXIS = 2;
    public static final IBlockState AIR_DEFAULT_STATE = Blocks.air.getDefaultState();
    private static TextureAtlasSprite emptySprite = null;
    private static final BlockDir[] SIDES_Y_NEG_DOWN = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.NORTH, BlockDir.SOUTH};
    private static final BlockDir[] SIDES_Y_POS_UP = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.SOUTH, BlockDir.NORTH};
    private static final BlockDir[] SIDES_Z_NEG_NORTH = new BlockDir[]{BlockDir.EAST, BlockDir.WEST, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_Z_POS_SOUTH = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_X_NEG_WEST = new BlockDir[]{BlockDir.NORTH, BlockDir.SOUTH, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_X_POS_EAST = new BlockDir[]{BlockDir.SOUTH, BlockDir.NORTH, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_Z_NEG_NORTH_Z_AXIS = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.UP, BlockDir.DOWN};
    private static final BlockDir[] SIDES_X_POS_EAST_X_AXIS = new BlockDir[]{BlockDir.NORTH, BlockDir.SOUTH, BlockDir.UP, BlockDir.DOWN};
    private static final BlockDir[] EDGES_Y_NEG_DOWN = new BlockDir[]{BlockDir.NORTH_EAST, BlockDir.NORTH_WEST, BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST};
    private static final BlockDir[] EDGES_Y_POS_UP = new BlockDir[]{BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST, BlockDir.NORTH_EAST, BlockDir.NORTH_WEST};
    private static final BlockDir[] EDGES_Z_NEG_NORTH = new BlockDir[]{BlockDir.DOWN_WEST, BlockDir.DOWN_EAST, BlockDir.UP_WEST, BlockDir.UP_EAST};
    private static final BlockDir[] EDGES_Z_POS_SOUTH = new BlockDir[]{BlockDir.DOWN_EAST, BlockDir.DOWN_WEST, BlockDir.UP_EAST, BlockDir.UP_WEST};
    private static final BlockDir[] EDGES_X_NEG_WEST = new BlockDir[]{BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH, BlockDir.UP_SOUTH, BlockDir.UP_NORTH};
    private static final BlockDir[] EDGES_X_POS_EAST = new BlockDir[]{BlockDir.DOWN_NORTH, BlockDir.DOWN_SOUTH, BlockDir.UP_NORTH, BlockDir.UP_SOUTH};
    private static final BlockDir[] EDGES_Z_NEG_NORTH_Z_AXIS = new BlockDir[]{BlockDir.UP_EAST, BlockDir.UP_WEST, BlockDir.DOWN_EAST, BlockDir.DOWN_WEST};
    private static final BlockDir[] EDGES_X_POS_EAST_X_AXIS = new BlockDir[]{BlockDir.UP_SOUTH, BlockDir.UP_NORTH, BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH};
    public static final TextureAtlasSprite SPRITE_DEFAULT = new TextureAtlasSprite("<default>");

    public static BakedQuad[] getConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv) {
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        if (textureatlassprite == null) {
            return renderEnv.getArrayQuadsCtm(quad);
        }
        Block block = blockState.getBlock();
        if (ConnectedTextures.skipConnectedTexture(blockAccess, blockState, blockPos, quad, renderEnv)) {
            quad = ConnectedTextures.getQuad(emptySprite, quad);
            return renderEnv.getArrayQuadsCtm(quad);
        }
        EnumFacing enumfacing = quad.getFace();
        BakedQuad[] abakedquad = ConnectedTextures.getConnectedTextureMultiPass(blockAccess, blockState, blockPos, enumfacing, quad, renderEnv);
        return abakedquad;
    }

    private static boolean skipConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv) {
        TextureAtlasSprite textureatlassprite;
        Block block = blockState.getBlock();
        if (block instanceof BlockPane && (textureatlassprite = quad.getSprite()).getIconName().startsWith("minecraft:blocks/glass_pane_top")) {
            IBlockState iblockstate1 = blockAccess.getBlockState(blockPos.offset(quad.getFace()));
            return iblockstate1 == blockState;
        }
        if (block instanceof BlockPane) {
            EnumFacing enumfacing = quad.getFace();
            if (enumfacing != EnumFacing.UP && enumfacing != EnumFacing.DOWN) {
                return false;
            }
            if (!quad.isFaceQuad()) {
                return false;
            }
            BlockPos blockpos = blockPos.offset(quad.getFace());
            IBlockState iblockstate = blockAccess.getBlockState(blockpos);
            if (iblockstate.getBlock() != block) {
                return false;
            }
            if (block == Blocks.stained_glass_pane && iblockstate.getValue(BlockStainedGlassPane.COLOR) != blockState.getValue(BlockStainedGlassPane.COLOR)) {
                return false;
            }
            iblockstate = iblockstate.getBlock().getActualState(iblockstate, blockAccess, blockpos);
            double d0 = quad.getMidX();
            if (d0 < 0.4) {
                if (iblockstate.getValue(BlockPane.WEST).booleanValue()) {
                    return true;
                }
            } else if (d0 > 0.6) {
                if (iblockstate.getValue(BlockPane.EAST).booleanValue()) {
                    return true;
                }
            } else {
                double d1 = quad.getMidZ();
                if (d1 < 0.4) {
                    if (iblockstate.getValue(BlockPane.NORTH).booleanValue()) {
                        return true;
                    }
                } else {
                    if (d1 <= 0.6) {
                        return true;
                    }
                    if (iblockstate.getValue(BlockPane.SOUTH).booleanValue()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected static BakedQuad[] getQuads(TextureAtlasSprite sprite, BakedQuad quadIn, RenderEnv renderEnv) {
        if (sprite == null) {
            return null;
        }
        if (sprite == SPRITE_DEFAULT) {
            return renderEnv.getArrayQuadsCtm(quadIn);
        }
        BakedQuad bakedquad = ConnectedTextures.getQuad(sprite, quadIn);
        BakedQuad[] abakedquad = renderEnv.getArrayQuadsCtm(bakedquad);
        return abakedquad;
    }

    private static synchronized BakedQuad getQuad(TextureAtlasSprite sprite, BakedQuad quadIn) {
        if (spriteQuadMaps == null) {
            return quadIn;
        }
        int i2 = sprite.getIndexInMap();
        if (i2 >= 0 && i2 < spriteQuadMaps.length) {
            BakedQuad bakedquad;
            IdentityHashMap<BakedQuad, BakedQuad> map = spriteQuadMaps[i2];
            if (map == null) {
                ConnectedTextures.spriteQuadMaps[i2] = map = new IdentityHashMap<BakedQuad, BakedQuad>(1);
            }
            if ((bakedquad = (BakedQuad)map.get(quadIn)) == null) {
                bakedquad = ConnectedTextures.makeSpriteQuad(quadIn, sprite);
                map.put(quadIn, bakedquad);
            }
            return bakedquad;
        }
        return quadIn;
    }

    private static synchronized BakedQuad getQuadFull(TextureAtlasSprite sprite, BakedQuad quadIn, int tintIndex) {
        if (spriteQuadFullMaps == null) {
            return null;
        }
        if (sprite == null) {
            return null;
        }
        int i2 = sprite.getIndexInMap();
        if (i2 >= 0 && i2 < spriteQuadFullMaps.length) {
            EnumFacing enumfacing;
            BakedQuad bakedquad;
            EnumMap<EnumFacing, BakedQuad> map = spriteQuadFullMaps[i2];
            if (map == null) {
                ConnectedTextures.spriteQuadFullMaps[i2] = map = new EnumMap<EnumFacing, BakedQuad>(EnumFacing.class);
            }
            if ((bakedquad = (BakedQuad)map.get(enumfacing = quadIn.getFace())) == null) {
                bakedquad = BlockModelUtils.makeBakedQuad(enumfacing, sprite, tintIndex);
                map.put(enumfacing, bakedquad);
            }
            return bakedquad;
        }
        return null;
    }

    private static BakedQuad makeSpriteQuad(BakedQuad quad, TextureAtlasSprite sprite) {
        int[] aint = (int[])quad.getVertexData().clone();
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        int i2 = 0;
        while (i2 < 4) {
            ConnectedTextures.fixVertex(aint, i2, textureatlassprite, sprite);
            ++i2;
        }
        BakedQuad bakedquad = new BakedQuad(aint, quad.getTintIndex(), quad.getFace(), sprite);
        return bakedquad;
    }

    private static void fixVertex(int[] data, int vertex, TextureAtlasSprite spriteFrom, TextureAtlasSprite spriteTo) {
        int i2 = data.length / 4;
        int j2 = i2 * vertex;
        float f2 = Float.intBitsToFloat(data[j2 + 4]);
        float f1 = Float.intBitsToFloat(data[j2 + 4 + 1]);
        double d0 = spriteFrom.getSpriteU16(f2);
        double d1 = spriteFrom.getSpriteV16(f1);
        data[j2 + 4] = Float.floatToRawIntBits(spriteTo.getInterpolatedU(d0));
        data[j2 + 4 + 1] = Float.floatToRawIntBits(spriteTo.getInterpolatedV(d1));
    }

    private static BakedQuad[] getConnectedTextureMultiPass(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing side, BakedQuad quad, RenderEnv renderEnv) {
        BakedQuad[] abakedquad = ConnectedTextures.getConnectedTextureSingle(blockAccess, blockState, blockPos, side, quad, true, 0, renderEnv);
        if (!multipass) {
            return abakedquad;
        }
        if (abakedquad.length == 1 && abakedquad[0] == quad) {
            return abakedquad;
        }
        List<BakedQuad> list = renderEnv.getListQuadsCtmMultipass(abakedquad);
        int i2 = 0;
        while (i2 < list.size()) {
            BakedQuad bakedquad;
            BakedQuad bakedquad1 = bakedquad = list.get(i2);
            int j2 = 0;
            while (j2 < 3) {
                BakedQuad[] abakedquad1 = ConnectedTextures.getConnectedTextureSingle(blockAccess, blockState, blockPos, side, bakedquad1, false, j2 + 1, renderEnv);
                if (abakedquad1.length != 1 || abakedquad1[0] == bakedquad1) break;
                bakedquad1 = abakedquad1[0];
                ++j2;
            }
            list.set(i2, bakedquad1);
            ++i2;
        }
        int k2 = 0;
        while (k2 < abakedquad.length) {
            abakedquad[k2] = list.get(k2);
            ++k2;
        }
        return abakedquad;
    }

    public static BakedQuad[] getConnectedTextureSingle(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, BakedQuad quad, boolean checkBlocks, int pass, RenderEnv renderEnv) {
        ConnectedProperties[] aconnectedproperties1;
        int l2;
        ConnectedProperties[] aconnectedproperties;
        int i2;
        Block block = blockState.getBlock();
        if (!(blockState instanceof BlockStateBase)) {
            return renderEnv.getArrayQuadsCtm(quad);
        }
        BlockStateBase blockstatebase = (BlockStateBase)blockState;
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        if (tileProperties != null && (i2 = textureatlassprite.getIndexInMap()) >= 0 && i2 < tileProperties.length && (aconnectedproperties = tileProperties[i2]) != null) {
            int j2 = ConnectedTextures.getSide(facing);
            int k2 = 0;
            while (k2 < aconnectedproperties.length) {
                BakedQuad[] abakedquad;
                ConnectedProperties connectedproperties = aconnectedproperties[k2];
                if (connectedproperties != null && connectedproperties.matchesBlockId(blockstatebase.getBlockId()) && (abakedquad = ConnectedTextures.getConnectedTexture(connectedproperties, blockAccess, blockstatebase, blockPos, j2, quad, pass, renderEnv)) != null) {
                    return abakedquad;
                }
                ++k2;
            }
        }
        if (blockProperties != null && checkBlocks && (l2 = renderEnv.getBlockId()) >= 0 && l2 < blockProperties.length && (aconnectedproperties1 = blockProperties[l2]) != null) {
            int i1 = ConnectedTextures.getSide(facing);
            int j1 = 0;
            while (j1 < aconnectedproperties1.length) {
                BakedQuad[] abakedquad1;
                ConnectedProperties connectedproperties1 = aconnectedproperties1[j1];
                if (connectedproperties1 != null && connectedproperties1.matchesIcon(textureatlassprite) && (abakedquad1 = ConnectedTextures.getConnectedTexture(connectedproperties1, blockAccess, blockstatebase, blockPos, i1, quad, pass, renderEnv)) != null) {
                    return abakedquad1;
                }
                ++j1;
            }
        }
        return renderEnv.getArrayQuadsCtm(quad);
    }

    public static int getSide(EnumFacing facing) {
        if (facing == null) {
            return -1;
        }
        switch (facing) {
            case DOWN: {
                return 0;
            }
            case UP: {
                return 1;
            }
            case EAST: {
                return 5;
            }
            case WEST: {
                return 4;
            }
            case NORTH: {
                return 2;
            }
            case SOUTH: {
                return 3;
            }
        }
        return -1;
    }

    private static EnumFacing getFacing(int side) {
        switch (side) {
            case 0: {
                return EnumFacing.DOWN;
            }
            case 1: {
                return EnumFacing.UP;
            }
            case 2: {
                return EnumFacing.NORTH;
            }
            case 3: {
                return EnumFacing.SOUTH;
            }
            case 4: {
                return EnumFacing.WEST;
            }
            case 5: {
                return EnumFacing.EAST;
            }
        }
        return EnumFacing.UP;
    }

    private static BakedQuad[] getConnectedTexture(ConnectedProperties cp2, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, BakedQuad quad, int pass, RenderEnv renderEnv) {
        String s2;
        BiomeGenBase biomegenbase;
        int j2;
        int i2 = 0;
        int k2 = j2 = blockState.getMetadata();
        Block block = blockState.getBlock();
        if (block instanceof BlockRotatedPillar) {
            i2 = ConnectedTextures.getWoodAxis(side, j2);
            if (cp2.getMetadataMax() <= 3) {
                k2 = j2 & 3;
            }
        }
        if (block instanceof BlockQuartz) {
            i2 = ConnectedTextures.getQuartzAxis(side, j2);
            if (cp2.getMetadataMax() <= 2 && k2 > 2) {
                k2 = 2;
            }
        }
        if (!cp2.matchesBlock(blockState.getBlockId(), k2)) {
            return null;
        }
        if (side >= 0 && cp2.faces != 63) {
            int l2 = side;
            if (i2 != 0) {
                l2 = ConnectedTextures.fixSideByAxis(side, i2);
            }
            if ((1 << l2 & cp2.faces) == 0) {
                return null;
            }
        }
        int i1 = blockPos.getY();
        if (cp2.heights != null && !cp2.heights.isInRange(i1)) {
            return null;
        }
        if (cp2.biomes != null && !cp2.matchesBiome(biomegenbase = blockAccess.getBiomeGenForCoords(blockPos))) {
            return null;
        }
        if (cp2.nbtName != null && !cp2.nbtName.matchesValue(s2 = TileEntityUtils.getTileEntityName(blockAccess, blockPos))) {
            return null;
        }
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        switch (cp2.method) {
            case 1: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureCtm(cp2, blockAccess, blockState, blockPos, i2, side, textureatlassprite, j2, renderEnv), quad, renderEnv);
            }
            case 2: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureHorizontal(cp2, blockAccess, blockState, blockPos, i2, side, textureatlassprite, j2), quad, renderEnv);
            }
            case 3: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureTop(cp2, blockAccess, blockState, blockPos, i2, side, textureatlassprite, j2), quad, renderEnv);
            }
            case 4: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureRandom(cp2, blockAccess, blockState, blockPos, side), quad, renderEnv);
            }
            case 5: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureRepeat(cp2, blockPos, side), quad, renderEnv);
            }
            case 6: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureVertical(cp2, blockAccess, blockState, blockPos, i2, side, textureatlassprite, j2), quad, renderEnv);
            }
            case 7: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureFixed(cp2), quad, renderEnv);
            }
            case 8: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureHorizontalVertical(cp2, blockAccess, blockState, blockPos, i2, side, textureatlassprite, j2), quad, renderEnv);
            }
            case 9: {
                return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureVerticalHorizontal(cp2, blockAccess, blockState, blockPos, i2, side, textureatlassprite, j2), quad, renderEnv);
            }
            case 10: {
                if (pass == 0) {
                    return ConnectedTextures.getConnectedTextureCtmCompact(cp2, blockAccess, blockState, blockPos, i2, side, quad, j2, renderEnv);
                }
            }
            default: {
                return null;
            }
            case 11: {
                return ConnectedTextures.getConnectedTextureOverlay(cp2, blockAccess, blockState, blockPos, i2, side, quad, j2, renderEnv);
            }
            case 12: {
                return ConnectedTextures.getConnectedTextureOverlayFixed(cp2, quad, renderEnv);
            }
            case 13: {
                return ConnectedTextures.getConnectedTextureOverlayRandom(cp2, blockAccess, blockState, blockPos, side, quad, renderEnv);
            }
            case 14: {
                return ConnectedTextures.getConnectedTextureOverlayRepeat(cp2, blockPos, side, quad, renderEnv);
            }
            case 15: 
        }
        return ConnectedTextures.getConnectedTextureOverlayCtm(cp2, blockAccess, blockState, blockPos, i2, side, quad, j2, renderEnv);
    }

    private static int fixSideByAxis(int side, int vertAxis) {
        switch (vertAxis) {
            case 0: {
                return side;
            }
            case 1: {
                switch (side) {
                    case 0: {
                        return 2;
                    }
                    case 1: {
                        return 3;
                    }
                    case 2: {
                        return 1;
                    }
                    case 3: {
                        return 0;
                    }
                }
                return side;
            }
            case 2: {
                switch (side) {
                    case 0: {
                        return 4;
                    }
                    case 1: {
                        return 5;
                    }
                    default: {
                        return side;
                    }
                    case 4: {
                        return 1;
                    }
                    case 5: 
                }
                return 0;
            }
        }
        return side;
    }

    private static int getWoodAxis(int side, int metadata) {
        int i2 = (metadata & 0xC) >> 2;
        switch (i2) {
            case 1: {
                return 2;
            }
            case 2: {
                return 1;
            }
        }
        return 0;
    }

    private static int getQuartzAxis(int side, int metadata) {
        switch (metadata) {
            case 3: {
                return 2;
            }
            case 4: {
                return 1;
            }
        }
        return 0;
    }

    private static TextureAtlasSprite getConnectedTextureRandom(ConnectedProperties cp2, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side) {
        if (cp2.tileIcons.length == 1) {
            return cp2.tileIcons[0];
        }
        int i2 = side / cp2.symmetry * cp2.symmetry;
        if (cp2.linked) {
            BlockPos blockpos = blockPos.down();
            IBlockState iblockstate = blockAccess.getBlockState(blockpos);
            while (iblockstate.getBlock() == blockState.getBlock()) {
                blockPos = blockpos;
                if ((blockpos = blockpos.down()).getY() < 0) break;
                iblockstate = blockAccess.getBlockState(blockpos);
            }
        }
        int l2 = Config.getRandom(blockPos, i2) & Integer.MAX_VALUE;
        int i1 = 0;
        while (i1 < cp2.randomLoops) {
            l2 = Config.intHash(l2);
            ++i1;
        }
        int j1 = 0;
        if (cp2.weights == null) {
            j1 = l2 % cp2.tileIcons.length;
        } else {
            int j2 = l2 % cp2.sumAllWeights;
            int[] aint = cp2.sumWeights;
            int k2 = 0;
            while (k2 < aint.length) {
                if (j2 < aint[k2]) {
                    j1 = k2;
                    break;
                }
                ++k2;
            }
        }
        return cp2.tileIcons[j1];
    }

    private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties cp2) {
        return cp2.tileIcons[0];
    }

    private static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties cp2, BlockPos blockPos, int side) {
        if (cp2.tileIcons.length == 1) {
            return cp2.tileIcons[0];
        }
        int i2 = blockPos.getX();
        int j2 = blockPos.getY();
        int k2 = blockPos.getZ();
        int l2 = 0;
        int i1 = 0;
        switch (side) {
            case 0: {
                l2 = i2;
                i1 = -k2 - 1;
                break;
            }
            case 1: {
                l2 = i2;
                i1 = k2;
                break;
            }
            case 2: {
                l2 = -i2 - 1;
                i1 = -j2;
                break;
            }
            case 3: {
                l2 = i2;
                i1 = -j2;
                break;
            }
            case 4: {
                l2 = k2;
                i1 = -j2;
                break;
            }
            case 5: {
                l2 = -k2 - 1;
                i1 = -j2;
            }
        }
        i1 %= cp2.height;
        if ((l2 %= cp2.width) < 0) {
            l2 += cp2.width;
        }
        if (i1 < 0) {
            i1 += cp2.height;
        }
        int j1 = i1 * cp2.width + l2;
        return cp2.tileIcons[j1];
    }

    private static TextureAtlasSprite getConnectedTextureCtm(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata, RenderEnv renderEnv) {
        int i2 = ConnectedTextures.getConnectedTextureCtmIndex(cp2, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata, renderEnv);
        return cp2.tileIcons[i2];
    }

    private static synchronized BakedQuad[] getConnectedTextureCtmCompact(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, BakedQuad quad, int metadata, RenderEnv renderEnv) {
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        int i2 = ConnectedTextures.getConnectedTextureCtmIndex(cp2, blockAccess, blockState, blockPos, vertAxis, side, textureatlassprite, metadata, renderEnv);
        return ConnectedTexturesCompact.getConnectedTextureCtmCompact(i2, cp2, side, quad, renderEnv);
    }

    private static BakedQuad[] getConnectedTextureOverlay(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, BakedQuad quad, int metadata, RenderEnv renderEnv) {
        Object dirEdges;
        if (!quad.isFullQuad()) {
            return null;
        }
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        BlockDir[] ablockdir = ConnectedTextures.getSideDirections(side, vertAxis);
        boolean[] aboolean = renderEnv.getBorderFlags();
        int i2 = 0;
        while (i2 < 4) {
            aboolean[i2] = ConnectedTextures.isNeighbourOverlay(cp2, blockAccess, blockState, ablockdir[i2].offset(blockPos), side, textureatlassprite, metadata);
            ++i2;
        }
        ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp2.layer);
        try {
            if (!(aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3])) {
                if (aboolean[0] && aboolean[1] && aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[5], quad, cp2.tintIndex), cp2.tintBlockState);
                    Object dirEdges2 = null;
                    BakedQuad[] bakedQuadArray = dirEdges2;
                    return bakedQuadArray;
                }
                if (aboolean[0] && aboolean[2] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[6], quad, cp2.tintIndex), cp2.tintBlockState);
                    Object dirEdges3 = null;
                    BakedQuad[] bakedQuadArray = dirEdges3;
                    return bakedQuadArray;
                }
                if (aboolean[1] && aboolean[2] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[12], quad, cp2.tintIndex), cp2.tintBlockState);
                    Object dirEdges4 = null;
                    BakedQuad[] bakedQuadArray = dirEdges4;
                    return bakedQuadArray;
                }
                if (aboolean[0] && aboolean[1] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[13], quad, cp2.tintIndex), cp2.tintBlockState);
                    Object dirEdges5 = null;
                    BakedQuad[] bakedQuadArray = dirEdges5;
                    return bakedQuadArray;
                }
                BlockDir[] ablockdir1 = ConnectedTextures.getEdgeDirections(side, vertAxis);
                boolean[] aboolean1 = renderEnv.getBorderFlags2();
                int j2 = 0;
                while (j2 < 4) {
                    aboolean1[j2] = ConnectedTextures.isNeighbourOverlay(cp2, blockAccess, blockState, ablockdir1[j2].offset(blockPos), side, textureatlassprite, metadata);
                    ++j2;
                }
                if (aboolean[1] && aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[3], quad, cp2.tintIndex), cp2.tintBlockState);
                    if (aboolean1[3]) {
                        listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[16], quad, cp2.tintIndex), cp2.tintBlockState);
                    }
                    Object object4 = null;
                    BakedQuad[] bakedQuadArray = object4;
                    return bakedQuadArray;
                }
                if (aboolean[0] && aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[4], quad, cp2.tintIndex), cp2.tintBlockState);
                    if (aboolean1[2]) {
                        listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[14], quad, cp2.tintIndex), cp2.tintBlockState);
                    }
                    Object object3 = null;
                    BakedQuad[] bakedQuadArray = object3;
                    return bakedQuadArray;
                }
                if (aboolean[1] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[10], quad, cp2.tintIndex), cp2.tintBlockState);
                    if (aboolean1[1]) {
                        listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[2], quad, cp2.tintIndex), cp2.tintBlockState);
                    }
                    Object object2 = null;
                    BakedQuad[] bakedQuadArray = object2;
                    return bakedQuadArray;
                }
                if (aboolean[0] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[11], quad, cp2.tintIndex), cp2.tintBlockState);
                    if (aboolean1[0]) {
                        listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[0], quad, cp2.tintIndex), cp2.tintBlockState);
                    }
                    Object object1 = null;
                    BakedQuad[] bakedQuadArray = object1;
                    return bakedQuadArray;
                }
                boolean[] aboolean2 = renderEnv.getBorderFlags3();
                int k2 = 0;
                while (k2 < 4) {
                    aboolean2[k2] = ConnectedTextures.isNeighbourMatching(cp2, blockAccess, blockState, ablockdir[k2].offset(blockPos), side, textureatlassprite, metadata);
                    ++k2;
                }
                if (aboolean[0]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[9], quad, cp2.tintIndex), cp2.tintBlockState);
                }
                if (aboolean[1]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[7], quad, cp2.tintIndex), cp2.tintBlockState);
                }
                if (aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[1], quad, cp2.tintIndex), cp2.tintBlockState);
                }
                if (aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[15], quad, cp2.tintIndex), cp2.tintBlockState);
                }
                if (aboolean1[0] && (aboolean2[1] || aboolean2[2]) && !aboolean[1] && !aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[0], quad, cp2.tintIndex), cp2.tintBlockState);
                }
                if (aboolean1[1] && (aboolean2[0] || aboolean2[2]) && !aboolean[0] && !aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[2], quad, cp2.tintIndex), cp2.tintBlockState);
                }
                if (aboolean1[2] && (aboolean2[1] || aboolean2[3]) && !aboolean[1] && !aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[14], quad, cp2.tintIndex), cp2.tintBlockState);
                }
                if (aboolean1[3] && (aboolean2[0] || aboolean2[3]) && !aboolean[0] && !aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[16], quad, cp2.tintIndex), cp2.tintBlockState);
                }
                Object object5 = null;
                BakedQuad[] bakedQuadArray = object5;
                return bakedQuadArray;
            }
            listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(cp2.tileIcons[8], quad, cp2.tintIndex), cp2.tintBlockState);
            dirEdges = null;
        }
        finally {
            if (listquadsoverlay.size() > 0) {
                renderEnv.setOverlaysRendered(true);
            }
        }
        return dirEdges;
    }

    private static BakedQuad[] getConnectedTextureOverlayFixed(ConnectedProperties cp2, BakedQuad quad, RenderEnv renderEnv) {
        Object object;
        if (!quad.isFullQuad()) {
            return null;
        }
        ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp2.layer);
        try {
            TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureFixed(cp2);
            if (textureatlassprite != null) {
                listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(textureatlassprite, quad, cp2.tintIndex), cp2.tintBlockState);
            }
            object = null;
        }
        finally {
            if (listquadsoverlay.size() > 0) {
                renderEnv.setOverlaysRendered(true);
            }
        }
        return object;
    }

    private static BakedQuad[] getConnectedTextureOverlayRandom(ConnectedProperties cp2, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, BakedQuad quad, RenderEnv renderEnv) {
        Object object;
        if (!quad.isFullQuad()) {
            return null;
        }
        ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp2.layer);
        try {
            TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureRandom(cp2, blockAccess, blockState, blockPos, side);
            if (textureatlassprite != null) {
                listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(textureatlassprite, quad, cp2.tintIndex), cp2.tintBlockState);
            }
            object = null;
        }
        finally {
            if (listquadsoverlay.size() > 0) {
                renderEnv.setOverlaysRendered(true);
            }
        }
        return object;
    }

    private static BakedQuad[] getConnectedTextureOverlayRepeat(ConnectedProperties cp2, BlockPos blockPos, int side, BakedQuad quad, RenderEnv renderEnv) {
        Object object;
        if (!quad.isFullQuad()) {
            return null;
        }
        ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp2.layer);
        try {
            TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureRepeat(cp2, blockPos, side);
            if (textureatlassprite != null) {
                listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(textureatlassprite, quad, cp2.tintIndex), cp2.tintBlockState);
            }
            object = null;
        }
        finally {
            if (listquadsoverlay.size() > 0) {
                renderEnv.setOverlaysRendered(true);
            }
        }
        return object;
    }

    private static BakedQuad[] getConnectedTextureOverlayCtm(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, BakedQuad quad, int metadata, RenderEnv renderEnv) {
        Object object;
        if (!quad.isFullQuad()) {
            return null;
        }
        ListQuadsOverlay listquadsoverlay = renderEnv.getListQuadsOverlay(cp2.layer);
        try {
            TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureCtm(cp2, blockAccess, blockState, blockPos, vertAxis, side, quad.getSprite(), metadata, renderEnv);
            if (textureatlassprite != null) {
                listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(textureatlassprite, quad, cp2.tintIndex), cp2.tintBlockState);
            }
            object = null;
        }
        finally {
            if (listquadsoverlay.size() > 0) {
                renderEnv.setOverlaysRendered(true);
            }
        }
        return object;
    }

    private static BlockDir[] getSideDirections(int side, int vertAxis) {
        switch (side) {
            case 0: {
                return SIDES_Y_NEG_DOWN;
            }
            case 1: {
                return SIDES_Y_POS_UP;
            }
            case 2: {
                if (vertAxis == 1) {
                    return SIDES_Z_NEG_NORTH_Z_AXIS;
                }
                return SIDES_Z_NEG_NORTH;
            }
            case 3: {
                return SIDES_Z_POS_SOUTH;
            }
            case 4: {
                return SIDES_X_NEG_WEST;
            }
            case 5: {
                if (vertAxis == 2) {
                    return SIDES_X_POS_EAST_X_AXIS;
                }
                return SIDES_X_POS_EAST;
            }
        }
        throw new IllegalArgumentException("Unknown side: " + side);
    }

    private static BlockDir[] getEdgeDirections(int side, int vertAxis) {
        switch (side) {
            case 0: {
                return EDGES_Y_NEG_DOWN;
            }
            case 1: {
                return EDGES_Y_POS_UP;
            }
            case 2: {
                if (vertAxis == 1) {
                    return EDGES_Z_NEG_NORTH_Z_AXIS;
                }
                return EDGES_Z_NEG_NORTH;
            }
            case 3: {
                return EDGES_Z_POS_SOUTH;
            }
            case 4: {
                return EDGES_X_NEG_WEST;
            }
            case 5: {
                if (vertAxis == 2) {
                    return EDGES_X_POS_EAST_X_AXIS;
                }
                return EDGES_X_POS_EAST;
            }
        }
        throw new IllegalArgumentException("Unknown side: " + side);
    }

    protected static Map[][] getSpriteQuadCompactMaps() {
        return spriteQuadCompactMaps;
    }

    private static int getConnectedTextureCtmIndex(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata, RenderEnv renderEnv) {
        boolean[] aboolean = renderEnv.getBorderFlags();
        switch (side) {
            case 0: {
                aboolean[0] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[3] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                if (!cp2.innerSeams) break;
                BlockPos blockpos6 = blockPos.down();
                aboolean[0] = aboolean[0] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos6.west(), side, icon, metadata);
                aboolean[1] = aboolean[1] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos6.east(), side, icon, metadata);
                aboolean[2] = aboolean[2] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos6.north(), side, icon, metadata);
                aboolean[3] = aboolean[3] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos6.south(), side, icon, metadata);
                break;
            }
            case 1: {
                aboolean[0] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[3] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                if (!cp2.innerSeams) break;
                BlockPos blockpos5 = blockPos.up();
                aboolean[0] = aboolean[0] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos5.west(), side, icon, metadata);
                aboolean[1] = aboolean[1] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos5.east(), side, icon, metadata);
                aboolean[2] = aboolean[2] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos5.south(), side, icon, metadata);
                aboolean[3] = aboolean[3] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos5.north(), side, icon, metadata);
                break;
            }
            case 2: {
                aboolean[0] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[1] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[2] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                if (cp2.innerSeams) {
                    BlockPos blockpos4 = blockPos.north();
                    aboolean[0] = aboolean[0] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos4.east(), side, icon, metadata);
                    aboolean[1] = aboolean[1] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos4.west(), side, icon, metadata);
                    aboolean[2] = aboolean[2] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos4.down(), side, icon, metadata);
                    boolean bl2 = aboolean[3] = aboolean[3] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos4.up(), side, icon, metadata);
                }
                if (vertAxis != 1) break;
                ConnectedTextures.switchValues(0, 1, aboolean);
                ConnectedTextures.switchValues(2, 3, aboolean);
                break;
            }
            case 3: {
                aboolean[0] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                if (!cp2.innerSeams) break;
                BlockPos blockpos3 = blockPos.south();
                aboolean[0] = aboolean[0] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos3.west(), side, icon, metadata);
                aboolean[1] = aboolean[1] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos3.east(), side, icon, metadata);
                aboolean[2] = aboolean[2] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos3.down(), side, icon, metadata);
                aboolean[3] = aboolean[3] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos3.up(), side, icon, metadata);
                break;
            }
            case 4: {
                aboolean[0] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[1] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[2] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                if (!cp2.innerSeams) break;
                BlockPos blockpos2 = blockPos.west();
                aboolean[0] = aboolean[0] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos2.north(), side, icon, metadata);
                aboolean[1] = aboolean[1] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos2.south(), side, icon, metadata);
                aboolean[2] = aboolean[2] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos2.down(), side, icon, metadata);
                aboolean[3] = aboolean[3] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos2.up(), side, icon, metadata);
                break;
            }
            case 5: {
                aboolean[0] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[1] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[2] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                if (cp2.innerSeams) {
                    BlockPos blockpos = blockPos.east();
                    aboolean[0] = aboolean[0] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos.south(), side, icon, metadata);
                    aboolean[1] = aboolean[1] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos.north(), side, icon, metadata);
                    aboolean[2] = aboolean[2] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos.down(), side, icon, metadata);
                    boolean bl3 = aboolean[3] = aboolean[3] && !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos.up(), side, icon, metadata);
                }
                if (vertAxis != 2) break;
                ConnectedTextures.switchValues(0, 1, aboolean);
                ConnectedTextures.switchValues(2, 3, aboolean);
            }
        }
        int i2 = 0;
        if (aboolean[0] & !aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i2 = 3;
        } else if (!aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i2 = 1;
        } else if (!aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3]) {
            i2 = 12;
        } else if (!aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3]) {
            i2 = 36;
        } else if (aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i2 = 2;
        } else if (!aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3]) {
            i2 = 24;
        } else if (aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3]) {
            i2 = 15;
        } else if (aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3]) {
            i2 = 39;
        } else if (!aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3]) {
            i2 = 13;
        } else if (!aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3]) {
            i2 = 37;
        } else if (!aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3]) {
            i2 = 25;
        } else if (aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3]) {
            i2 = 27;
        } else if (aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3]) {
            i2 = 38;
        } else if (aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3]) {
            i2 = 14;
        } else if (aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3]) {
            i2 = 26;
        }
        if (i2 == 0) {
            return i2;
        }
        if (!Config.isConnectedTexturesFancy()) {
            return i2;
        }
        switch (side) {
            case 0: {
                aboolean[0] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
                aboolean[1] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
                aboolean[2] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
                boolean bl4 = aboolean[3] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
                if (!cp2.innerSeams) break;
                BlockPos blockpos11 = blockPos.down();
                aboolean[0] = aboolean[0] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos11.east().north(), side, icon, metadata);
                aboolean[1] = aboolean[1] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos11.west().north(), side, icon, metadata);
                aboolean[2] = aboolean[2] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos11.east().south(), side, icon, metadata);
                aboolean[3] = aboolean[3] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos11.west().south(), side, icon, metadata);
                break;
            }
            case 1: {
                aboolean[0] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
                aboolean[1] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
                aboolean[2] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
                boolean bl5 = aboolean[3] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
                if (!cp2.innerSeams) break;
                BlockPos blockpos10 = blockPos.up();
                aboolean[0] = aboolean[0] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos10.east().south(), side, icon, metadata);
                aboolean[1] = aboolean[1] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos10.west().south(), side, icon, metadata);
                aboolean[2] = aboolean[2] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos10.east().north(), side, icon, metadata);
                aboolean[3] = aboolean[3] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos10.west().north(), side, icon, metadata);
                break;
            }
            case 2: {
                aboolean[0] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
                aboolean[1] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
                aboolean[2] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
                boolean bl6 = aboolean[3] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
                if (cp2.innerSeams) {
                    BlockPos blockpos9 = blockPos.north();
                    aboolean[0] = aboolean[0] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos9.west().down(), side, icon, metadata);
                    aboolean[1] = aboolean[1] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos9.east().down(), side, icon, metadata);
                    aboolean[2] = aboolean[2] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos9.west().up(), side, icon, metadata);
                    boolean bl7 = aboolean[3] = aboolean[3] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos9.east().up(), side, icon, metadata);
                }
                if (vertAxis != 1) break;
                ConnectedTextures.switchValues(0, 3, aboolean);
                ConnectedTextures.switchValues(1, 2, aboolean);
                break;
            }
            case 3: {
                aboolean[0] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
                aboolean[1] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
                aboolean[2] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
                boolean bl8 = aboolean[3] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
                if (!cp2.innerSeams) break;
                BlockPos blockpos8 = blockPos.south();
                aboolean[0] = aboolean[0] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos8.east().down(), side, icon, metadata);
                aboolean[1] = aboolean[1] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos8.west().down(), side, icon, metadata);
                aboolean[2] = aboolean[2] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos8.east().up(), side, icon, metadata);
                aboolean[3] = aboolean[3] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos8.west().up(), side, icon, metadata);
                break;
            }
            case 4: {
                aboolean[0] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
                aboolean[1] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
                aboolean[2] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
                boolean bl9 = aboolean[3] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
                if (!cp2.innerSeams) break;
                BlockPos blockpos7 = blockPos.west();
                aboolean[0] = aboolean[0] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos7.down().south(), side, icon, metadata);
                aboolean[1] = aboolean[1] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos7.down().north(), side, icon, metadata);
                aboolean[2] = aboolean[2] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos7.up().south(), side, icon, metadata);
                aboolean[3] = aboolean[3] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos7.up().north(), side, icon, metadata);
                break;
            }
            case 5: {
                aboolean[0] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
                aboolean[1] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
                aboolean[2] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
                boolean bl10 = aboolean[3] = !ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
                if (cp2.innerSeams) {
                    BlockPos blockpos1 = blockPos.east();
                    aboolean[0] = aboolean[0] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos1.down().north(), side, icon, metadata);
                    aboolean[1] = aboolean[1] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos1.down().south(), side, icon, metadata);
                    aboolean[2] = aboolean[2] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos1.up().north(), side, icon, metadata);
                    boolean bl11 = aboolean[3] = aboolean[3] || ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockpos1.up().south(), side, icon, metadata);
                }
                if (vertAxis != 2) break;
                ConnectedTextures.switchValues(0, 3, aboolean);
                ConnectedTextures.switchValues(1, 2, aboolean);
            }
        }
        if (i2 == 13 && aboolean[0]) {
            i2 = 4;
        } else if (i2 == 15 && aboolean[1]) {
            i2 = 5;
        } else if (i2 == 37 && aboolean[2]) {
            i2 = 16;
        } else if (i2 == 39 && aboolean[3]) {
            i2 = 17;
        } else if (i2 == 14 && aboolean[0] && aboolean[1]) {
            i2 = 7;
        } else if (i2 == 25 && aboolean[0] && aboolean[2]) {
            i2 = 6;
        } else if (i2 == 27 && aboolean[3] && aboolean[1]) {
            i2 = 19;
        } else if (i2 == 38 && aboolean[3] && aboolean[2]) {
            i2 = 18;
        } else if (i2 == 14 && !aboolean[0] && aboolean[1]) {
            i2 = 31;
        } else if (i2 == 25 && aboolean[0] && !aboolean[2]) {
            i2 = 30;
        } else if (i2 == 27 && !aboolean[3] && aboolean[1]) {
            i2 = 41;
        } else if (i2 == 38 && aboolean[3] && !aboolean[2]) {
            i2 = 40;
        } else if (i2 == 14 && aboolean[0] && !aboolean[1]) {
            i2 = 29;
        } else if (i2 == 25 && !aboolean[0] && aboolean[2]) {
            i2 = 28;
        } else if (i2 == 27 && aboolean[3] && !aboolean[1]) {
            i2 = 43;
        } else if (i2 == 38 && !aboolean[3] && aboolean[2]) {
            i2 = 42;
        } else if (i2 == 26 && aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3]) {
            i2 = 46;
        } else if (i2 == 26 && !aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3]) {
            i2 = 9;
        } else if (i2 == 26 && aboolean[0] && !aboolean[1] && aboolean[2] && aboolean[3]) {
            i2 = 21;
        } else if (i2 == 26 && aboolean[0] && aboolean[1] && !aboolean[2] && aboolean[3]) {
            i2 = 8;
        } else if (i2 == 26 && aboolean[0] && aboolean[1] && aboolean[2] && !aboolean[3]) {
            i2 = 20;
        } else if (i2 == 26 && aboolean[0] && aboolean[1] && !aboolean[2] && !aboolean[3]) {
            i2 = 11;
        } else if (i2 == 26 && !aboolean[0] && !aboolean[1] && aboolean[2] && aboolean[3]) {
            i2 = 22;
        } else if (i2 == 26 && !aboolean[0] && aboolean[1] && !aboolean[2] && aboolean[3]) {
            i2 = 23;
        } else if (i2 == 26 && aboolean[0] && !aboolean[1] && aboolean[2] && !aboolean[3]) {
            i2 = 10;
        } else if (i2 == 26 && aboolean[0] && !aboolean[1] && !aboolean[2] && aboolean[3]) {
            i2 = 34;
        } else if (i2 == 26 && !aboolean[0] && aboolean[1] && aboolean[2] && !aboolean[3]) {
            i2 = 35;
        } else if (i2 == 26 && aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3]) {
            i2 = 32;
        } else if (i2 == 26 && !aboolean[0] && aboolean[1] && !aboolean[2] && !aboolean[3]) {
            i2 = 33;
        } else if (i2 == 26 && !aboolean[0] && !aboolean[1] && aboolean[2] && !aboolean[3]) {
            i2 = 44;
        } else if (i2 == 26 && !aboolean[0] && !aboolean[1] && !aboolean[2] && aboolean[3]) {
            i2 = 45;
        }
        return i2;
    }

    private static void switchValues(int ix1, int ix2, boolean[] arr2) {
        boolean flag = arr2[ix1];
        arr2[ix1] = arr2[ix2];
        arr2[ix2] = flag;
    }

    private static boolean isNeighbourOverlay(ConnectedProperties cp2, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata) {
        TextureAtlasSprite textureatlassprite;
        BlockStateBase blockstatebase;
        IBlockState iblockstate = iblockaccess.getBlockState(blockPos);
        if (!ConnectedTextures.isFullCubeModel(iblockstate)) {
            return false;
        }
        if (cp2.connectBlocks != null && !Matches.block((blockstatebase = (BlockStateBase)iblockstate).getBlockId(), blockstatebase.getMetadata(), cp2.connectBlocks)) {
            return false;
        }
        if (cp2.connectTileIcons != null && !Config.isSameOne(textureatlassprite = ConnectedTextures.getNeighbourIcon(iblockaccess, blockState, blockPos, iblockstate, side), cp2.connectTileIcons)) {
            return false;
        }
        IBlockState iblockstate1 = iblockaccess.getBlockState(blockPos.offset(ConnectedTextures.getFacing(side)));
        return iblockstate1.getBlock().isOpaqueCube() ? false : (side == 1 && iblockstate1.getBlock() == Blocks.snow_layer ? false : !ConnectedTextures.isNeighbour(cp2, iblockaccess, blockState, blockPos, iblockstate, side, icon, metadata));
    }

    private static boolean isFullCubeModel(IBlockState state) {
        if (state.getBlock().isFullCube()) {
            return true;
        }
        Block block = state.getBlock();
        return block instanceof BlockGlass ? true : block instanceof BlockStainedGlass;
    }

    private static boolean isNeighbourMatching(ConnectedProperties cp2, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata) {
        TextureAtlasSprite textureatlassprite;
        BlockStateBase blockstatebase;
        IBlockState iblockstate = iblockaccess.getBlockState(blockPos);
        if (iblockstate == AIR_DEFAULT_STATE) {
            return false;
        }
        if (cp2.matchBlocks != null && iblockstate instanceof BlockStateBase && !cp2.matchesBlock((blockstatebase = (BlockStateBase)iblockstate).getBlockId(), blockstatebase.getMetadata())) {
            return false;
        }
        if (cp2.matchTileIcons != null && (textureatlassprite = ConnectedTextures.getNeighbourIcon(iblockaccess, blockState, blockPos, iblockstate, side)) != icon) {
            return false;
        }
        IBlockState iblockstate1 = iblockaccess.getBlockState(blockPos.offset(ConnectedTextures.getFacing(side)));
        return iblockstate1.getBlock().isOpaqueCube() ? false : side != 1 || iblockstate1.getBlock() != Blocks.snow_layer;
    }

    private static boolean isNeighbour(ConnectedProperties cp2, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata) {
        IBlockState iblockstate = iblockaccess.getBlockState(blockPos);
        return ConnectedTextures.isNeighbour(cp2, iblockaccess, blockState, blockPos, iblockstate, side, icon, metadata);
    }

    private static boolean isNeighbour(ConnectedProperties cp2, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, IBlockState neighbourState, int side, TextureAtlasSprite icon, int metadata) {
        if (blockState == neighbourState) {
            return true;
        }
        if (cp2.connect == 2) {
            if (neighbourState == null) {
                return false;
            }
            if (neighbourState == AIR_DEFAULT_STATE) {
                return false;
            }
            TextureAtlasSprite textureatlassprite = ConnectedTextures.getNeighbourIcon(iblockaccess, blockState, blockPos, neighbourState, side);
            return textureatlassprite == icon;
        }
        if (cp2.connect == 3) {
            return neighbourState == null ? false : (neighbourState == AIR_DEFAULT_STATE ? false : neighbourState.getBlock().getMaterial() == blockState.getBlock().getMaterial());
        }
        if (!(neighbourState instanceof BlockStateBase)) {
            return false;
        }
        BlockStateBase blockstatebase = (BlockStateBase)neighbourState;
        Block block = blockstatebase.getBlock();
        int i2 = blockstatebase.getMetadata();
        return block == blockState.getBlock() && i2 == metadata;
    }

    private static TextureAtlasSprite getNeighbourIcon(IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, IBlockState neighbourState, int side) {
        EnumFacing enumfacing;
        List list;
        neighbourState = neighbourState.getBlock().getActualState(neighbourState, iblockaccess, blockPos);
        IBakedModel ibakedmodel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(neighbourState);
        if (ibakedmodel == null) {
            return null;
        }
        if (Reflector.ForgeBlock_getExtendedState.exists()) {
            neighbourState = (IBlockState)Reflector.call(neighbourState.getBlock(), Reflector.ForgeBlock_getExtendedState, neighbourState, iblockaccess, blockPos);
        }
        if ((list = ibakedmodel.getFaceQuads(enumfacing = ConnectedTextures.getFacing(side))) == null) {
            return null;
        }
        if (Config.isBetterGrass()) {
            list = BetterGrass.getFaceQuads(iblockaccess, neighbourState, blockPos, enumfacing, list);
        }
        if (list.size() > 0) {
            BakedQuad bakedquad1 = list.get(0);
            return bakedquad1.getSprite();
        }
        List<BakedQuad> list1 = ibakedmodel.getGeneralQuads();
        if (list1 == null) {
            return null;
        }
        int i2 = 0;
        while (i2 < list1.size()) {
            BakedQuad bakedquad = list1.get(i2);
            if (bakedquad.getFace() == enumfacing) {
                return bakedquad.getSprite();
            }
            ++i2;
        }
        return null;
    }

    private static TextureAtlasSprite getConnectedTextureHorizontal(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata) {
        boolean flag = false;
        boolean flag1 = false;
        block0 : switch (vertAxis) {
            case 0: {
                switch (side) {
                    case 0: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break;
                    }
                    case 1: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break;
                    }
                    case 2: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        break;
                    }
                    case 3: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break;
                    }
                    case 4: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break;
                    }
                    case 5: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                    }
                }
                break;
            }
            case 1: {
                switch (side) {
                    case 0: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        break;
                    }
                    case 1: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break;
                    }
                    case 2: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break;
                    }
                    case 3: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break;
                    }
                    case 4: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        break;
                    }
                    case 5: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    }
                }
                break;
            }
            case 2: {
                switch (side) {
                    case 0: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        break block0;
                    }
                    case 1: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break block0;
                    }
                    case 2: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        break block0;
                    }
                    case 3: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        break block0;
                    }
                    case 4: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break block0;
                    }
                    case 5: {
                        flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                    }
                }
            }
        }
        int i2 = 3;
        i2 = flag ? (flag1 ? 1 : 2) : (flag1 ? 0 : 3);
        return cp2.tileIcons[i2];
    }

    private static TextureAtlasSprite getConnectedTextureVertical(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata) {
        boolean flag = false;
        boolean flag1 = false;
        switch (vertAxis) {
            case 0: {
                if (side == 1) {
                    flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                    flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                    break;
                }
                if (side == 0) {
                    flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                    flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                    break;
                }
                flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                break;
            }
            case 1: {
                if (side == 3) {
                    flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                    break;
                }
                if (side == 2) {
                    flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                    flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    break;
                }
                flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                break;
            }
            case 2: {
                if (side == 5) {
                    flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                    flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    break;
                }
                if (side == 4) {
                    flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                    break;
                }
                flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                flag1 = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            }
        }
        int i2 = 3;
        i2 = flag ? (flag1 ? 1 : 2) : (flag1 ? 0 : 3);
        return cp2.tileIcons[i2];
    }

    private static TextureAtlasSprite getConnectedTextureHorizontalVertical(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata) {
        TextureAtlasSprite[] atextureatlassprite = cp2.tileIcons;
        TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureHorizontal(cp2, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
        if (textureatlassprite != null && textureatlassprite != icon && textureatlassprite != atextureatlassprite[3]) {
            return textureatlassprite;
        }
        TextureAtlasSprite textureatlassprite1 = ConnectedTextures.getConnectedTextureVertical(cp2, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
        return textureatlassprite1 == atextureatlassprite[0] ? atextureatlassprite[4] : (textureatlassprite1 == atextureatlassprite[1] ? atextureatlassprite[5] : (textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6] : textureatlassprite1));
    }

    private static TextureAtlasSprite getConnectedTextureVerticalHorizontal(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata) {
        TextureAtlasSprite[] atextureatlassprite = cp2.tileIcons;
        TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureVertical(cp2, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
        if (textureatlassprite != null && textureatlassprite != icon && textureatlassprite != atextureatlassprite[3]) {
            return textureatlassprite;
        }
        TextureAtlasSprite textureatlassprite1 = ConnectedTextures.getConnectedTextureHorizontal(cp2, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
        return textureatlassprite1 == atextureatlassprite[0] ? atextureatlassprite[4] : (textureatlassprite1 == atextureatlassprite[1] ? atextureatlassprite[5] : (textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6] : textureatlassprite1));
    }

    private static TextureAtlasSprite getConnectedTextureTop(ConnectedProperties cp2, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata) {
        boolean flag = false;
        switch (vertAxis) {
            case 0: {
                if (side == 1 || side == 0) {
                    return null;
                }
                flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                break;
            }
            case 1: {
                if (side == 3 || side == 2) {
                    return null;
                }
                flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                break;
            }
            case 2: {
                if (side == 5 || side == 4) {
                    return null;
                }
                flag = ConnectedTextures.isNeighbour(cp2, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            }
        }
        if (flag) {
            return cp2.tileIcons[0];
        }
        return null;
    }

    public static void updateIcons(TextureMap textureMap) {
        blockProperties = null;
        tileProperties = null;
        spriteQuadMaps = null;
        spriteQuadCompactMaps = null;
        if (Config.isConnectedTextures()) {
            IResourcePack[] airesourcepack = Config.getResourcePacks();
            int i2 = airesourcepack.length - 1;
            while (i2 >= 0) {
                IResourcePack iresourcepack = airesourcepack[i2];
                ConnectedTextures.updateIcons(textureMap, iresourcepack);
                --i2;
            }
            ConnectedTextures.updateIcons(textureMap, Config.getDefaultResourcePack());
            ResourceLocation resourcelocation = new ResourceLocation("mcpatcher/ctm/default/empty");
            emptySprite = textureMap.registerSprite(resourcelocation);
            spriteQuadMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
            spriteQuadFullMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
            spriteQuadCompactMaps = new Map[textureMap.getCountRegisteredSprites() + 1][];
            if (blockProperties.length <= 0) {
                blockProperties = null;
            }
            if (tileProperties.length <= 0) {
                tileProperties = null;
            }
        }
    }

    private static void updateIconEmpty(TextureMap textureMap) {
    }

    public static void updateIcons(TextureMap textureMap, IResourcePack rp2) {
        Object[] astring = ResUtils.collectFiles(rp2, "mcpatcher/ctm/", ".properties", ConnectedTextures.getDefaultCtmPaths());
        Arrays.sort(astring);
        List list = ConnectedTextures.makePropertyList(tileProperties);
        List list1 = ConnectedTextures.makePropertyList(blockProperties);
        int i2 = 0;
        while (i2 < astring.length) {
            Object s2 = astring[i2];
            Config.dbg("ConnectedTextures: " + (String)s2);
            try {
                ResourceLocation resourcelocation = new ResourceLocation((String)s2);
                InputStream inputstream = rp2.getInputStream(resourcelocation);
                if (inputstream == null) {
                    Config.warn("ConnectedTextures file not found: " + (String)s2);
                } else {
                    PropertiesOrdered properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    ConnectedProperties connectedproperties = new ConnectedProperties(properties, (String)s2);
                    if (connectedproperties.isValid((String)s2)) {
                        connectedproperties.updateIcons(textureMap);
                        ConnectedTextures.addToTileList(connectedproperties, list);
                        ConnectedTextures.addToBlockList(connectedproperties, list1);
                    }
                }
            }
            catch (FileNotFoundException var11) {
                Config.warn("ConnectedTextures file not found: " + (String)s2);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            ++i2;
        }
        blockProperties = ConnectedTextures.propertyListToArray(list1);
        tileProperties = ConnectedTextures.propertyListToArray(list);
        multipass = ConnectedTextures.detectMultipass();
        Config.dbg("Multipass connected textures: " + multipass);
    }

    private static List makePropertyList(ConnectedProperties[][] propsArr) {
        ArrayList<ArrayList<ConnectedProperties>> list = new ArrayList<ArrayList<ConnectedProperties>>();
        if (propsArr != null) {
            int i2 = 0;
            while (i2 < propsArr.length) {
                ConnectedProperties[] aconnectedproperties = propsArr[i2];
                ArrayList<ConnectedProperties> list1 = null;
                if (aconnectedproperties != null) {
                    list1 = new ArrayList<ConnectedProperties>(Arrays.asList(aconnectedproperties));
                }
                list.add(list1);
                ++i2;
            }
        }
        return list;
    }

    private static boolean detectMultipass() {
        ArrayList<ConnectedProperties> list = new ArrayList<ConnectedProperties>();
        int i2 = 0;
        while (i2 < tileProperties.length) {
            ConnectedProperties[] aconnectedproperties = tileProperties[i2];
            if (aconnectedproperties != null) {
                list.addAll(Arrays.asList(aconnectedproperties));
            }
            ++i2;
        }
        int k2 = 0;
        while (k2 < blockProperties.length) {
            ConnectedProperties[] aconnectedproperties2 = blockProperties[k2];
            if (aconnectedproperties2 != null) {
                list.addAll(Arrays.asList(aconnectedproperties2));
            }
            ++k2;
        }
        ConnectedProperties[] aconnectedproperties1 = list.toArray(new ConnectedProperties[list.size()]);
        HashSet<TextureAtlasSprite> set1 = new HashSet<TextureAtlasSprite>();
        HashSet<TextureAtlasSprite> set = new HashSet<TextureAtlasSprite>();
        int j2 = 0;
        while (j2 < aconnectedproperties1.length) {
            ConnectedProperties connectedproperties = aconnectedproperties1[j2];
            if (connectedproperties.matchTileIcons != null) {
                set1.addAll(Arrays.asList(connectedproperties.matchTileIcons));
            }
            if (connectedproperties.tileIcons != null) {
                set.addAll(Arrays.asList(connectedproperties.tileIcons));
            }
            ++j2;
        }
        set1.retainAll(set);
        return !set1.isEmpty();
    }

    private static ConnectedProperties[][] propertyListToArray(List list) {
        ConnectedProperties[][] aconnectedproperties = new ConnectedProperties[list.size()][];
        int i2 = 0;
        while (i2 < list.size()) {
            List sublist = (List)list.get(i2);
            if (sublist != null) {
                ConnectedProperties[] aconnectedproperties1 = sublist.toArray(new ConnectedProperties[sublist.size()]);
                aconnectedproperties[i2] = aconnectedproperties1;
            }
            ++i2;
        }
        return aconnectedproperties;
    }

    private static void addToTileList(ConnectedProperties cp2, List tileList) {
        if (cp2.matchTileIcons != null) {
            int i2 = 0;
            while (i2 < cp2.matchTileIcons.length) {
                TextureAtlasSprite textureatlassprite = cp2.matchTileIcons[i2];
                if (!(textureatlassprite instanceof TextureAtlasSprite)) {
                    Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + textureatlassprite + ", name: " + textureatlassprite.getIconName());
                } else {
                    int j2 = textureatlassprite.getIndexInMap();
                    if (j2 < 0) {
                        Config.warn("Invalid tile ID: " + j2 + ", icon: " + textureatlassprite.getIconName());
                    } else {
                        ConnectedTextures.addToList(cp2, tileList, j2);
                    }
                }
                ++i2;
            }
        }
    }

    private static void addToBlockList(ConnectedProperties cp2, List blockList) {
        if (cp2.matchBlocks != null) {
            int i2 = 0;
            while (i2 < cp2.matchBlocks.length) {
                int j2 = cp2.matchBlocks[i2].getBlockId();
                if (j2 < 0) {
                    Config.warn("Invalid block ID: " + j2);
                } else {
                    ConnectedTextures.addToList(cp2, blockList, j2);
                }
                ++i2;
            }
        }
    }

    private static void addToList(ConnectedProperties cp2, List lists, int id2) {
        while (id2 >= lists.size()) {
            lists.add(null);
        }
        ArrayList<ConnectedProperties> list = (ArrayList<ConnectedProperties>)lists.get(id2);
        if (list == null) {
            list = new ArrayList<ConnectedProperties>();
            lists.set(id2, list);
        }
        list.add(cp2);
    }

    private static String[] getDefaultCtmPaths() {
        ArrayList<String> list = new ArrayList<String>();
        String s2 = "mcpatcher/ctm/default/";
        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
            list.add(String.valueOf(s2) + "glass.properties");
            list.add(String.valueOf(s2) + "glasspane.properties");
        }
        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
            list.add(String.valueOf(s2) + "bookshelf.properties");
        }
        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
            list.add(String.valueOf(s2) + "sandstone.properties");
        }
        String[] astring = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"};
        int i2 = 0;
        while (i2 < astring.length) {
            String s1 = astring[i2];
            if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + s1 + ".png"))) {
                list.add(String.valueOf(s2) + i2 + "_glass_" + s1 + "/glass_" + s1 + ".properties");
                list.add(String.valueOf(s2) + i2 + "_glass_" + s1 + "/glass_pane_" + s1 + ".properties");
            }
            ++i2;
        }
        String[] astring1 = list.toArray(new String[list.size()]);
        return astring1;
    }
}

