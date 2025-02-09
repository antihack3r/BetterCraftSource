/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.optifine.BetterSnow;
import net.optifine.CustomColors;
import net.optifine.model.BlockModelCustomizer;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;

public class BlockModelRenderer {
    private static float aoLightValueOpaque = 0.2f;
    private static boolean separateAoLightValue = false;
    private static final EnumWorldBlockLayer[] OVERLAY_LAYERS = new EnumWorldBlockLayer[]{EnumWorldBlockLayer.CUTOUT, EnumWorldBlockLayer.CUTOUT_MIPPED, EnumWorldBlockLayer.TRANSLUCENT};

    public BlockModelRenderer() {
        if (Reflector.ForgeModContainer_forgeLightPipelineEnabled.exists()) {
            Reflector.setFieldValue(Reflector.ForgeModContainer_forgeLightPipelineEnabled, false);
        }
    }

    public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn) {
        Block block = blockStateIn.getBlock();
        block.setBlockBoundsBasedOnState(blockAccessIn, blockPosIn);
        return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, true);
    }

    public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides) {
        boolean flag = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();
        try {
            boolean flag1;
            if (Config.isShaders()) {
                SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccessIn, worldRendererIn);
            }
            RenderEnv renderenv = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
            modelIn = BlockModelCustomizer.getRenderModel(modelIn, blockStateIn, renderenv);
            boolean bl2 = flag1 = flag ? this.renderModelSmooth(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides) : this.renderModelFlat(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides);
            if (flag1) {
                this.renderOverlayModels(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides, 0L, renderenv, flag);
            }
            if (Config.isShaders()) {
                SVertexBuilder.popEntity(worldRendererIn);
            }
            return flag1;
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, blockPosIn, blockStateIn);
            crashreportcategory.addCrashSection("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }

    public boolean renderModelAmbientOcclusion(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides) {
        IBlockState iblockstate = blockAccessIn.getBlockState(blockPosIn);
        return this.renderModelSmooth(blockAccessIn, modelIn, iblockstate, blockPosIn, worldRendererIn, checkSides);
    }

    private boolean renderModelSmooth(IBlockAccess p_renderModelSmooth_1_, IBakedModel p_renderModelSmooth_2_, IBlockState p_renderModelSmooth_3_, BlockPos p_renderModelSmooth_4_, WorldRenderer p_renderModelSmooth_5_, boolean p_renderModelSmooth_6_) {
        boolean flag = false;
        Block block = p_renderModelSmooth_3_.getBlock();
        RenderEnv renderenv = p_renderModelSmooth_5_.getRenderEnv(p_renderModelSmooth_3_, p_renderModelSmooth_4_);
        EnumWorldBlockLayer enumworldblocklayer = p_renderModelSmooth_5_.getBlockLayer();
        EnumFacing[] enumFacingArray = EnumFacing.VALUES;
        int n2 = EnumFacing.VALUES.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumFacing enumfacing = enumFacingArray[n3];
            List<BakedQuad> list = p_renderModelSmooth_2_.getFaceQuads(enumfacing);
            if (!list.isEmpty()) {
                BlockPos blockpos = p_renderModelSmooth_4_.offset(enumfacing);
                if (!p_renderModelSmooth_6_ || block.shouldSideBeRendered(p_renderModelSmooth_1_, blockpos, enumfacing)) {
                    list = BlockModelCustomizer.getRenderQuads(list, p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, enumfacing, enumworldblocklayer, 0L, renderenv);
                    this.renderQuadsSmooth(p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, p_renderModelSmooth_5_, list, renderenv);
                    flag = true;
                }
            }
            ++n3;
        }
        List<BakedQuad> list1 = p_renderModelSmooth_2_.getGeneralQuads();
        if (list1.size() > 0) {
            list1 = BlockModelCustomizer.getRenderQuads(list1, p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, null, enumworldblocklayer, 0L, renderenv);
            this.renderQuadsSmooth(p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, p_renderModelSmooth_5_, list1, renderenv);
            flag = true;
        }
        return flag;
    }

    public boolean renderModelStandard(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides) {
        IBlockState iblockstate = blockAccessIn.getBlockState(blockPosIn);
        return this.renderModelFlat(blockAccessIn, modelIn, iblockstate, blockPosIn, worldRendererIn, checkSides);
    }

    public boolean renderModelFlat(IBlockAccess p_renderModelFlat_1_, IBakedModel p_renderModelFlat_2_, IBlockState p_renderModelFlat_3_, BlockPos p_renderModelFlat_4_, WorldRenderer p_renderModelFlat_5_, boolean p_renderModelFlat_6_) {
        boolean flag = false;
        Block block = p_renderModelFlat_3_.getBlock();
        RenderEnv renderenv = p_renderModelFlat_5_.getRenderEnv(p_renderModelFlat_3_, p_renderModelFlat_4_);
        EnumWorldBlockLayer enumworldblocklayer = p_renderModelFlat_5_.getBlockLayer();
        EnumFacing[] enumFacingArray = EnumFacing.VALUES;
        int n2 = EnumFacing.VALUES.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumFacing enumfacing = enumFacingArray[n3];
            List<BakedQuad> list = p_renderModelFlat_2_.getFaceQuads(enumfacing);
            if (!list.isEmpty()) {
                BlockPos blockpos = p_renderModelFlat_4_.offset(enumfacing);
                if (!p_renderModelFlat_6_ || block.shouldSideBeRendered(p_renderModelFlat_1_, blockpos, enumfacing)) {
                    int i2 = block.getMixedBrightnessForBlock(p_renderModelFlat_1_, blockpos);
                    list = BlockModelCustomizer.getRenderQuads(list, p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, enumfacing, enumworldblocklayer, 0L, renderenv);
                    this.renderQuadsFlat(p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, enumfacing, i2, false, p_renderModelFlat_5_, list, renderenv);
                    flag = true;
                }
            }
            ++n3;
        }
        List<BakedQuad> list1 = p_renderModelFlat_2_.getGeneralQuads();
        if (list1.size() > 0) {
            list1 = BlockModelCustomizer.getRenderQuads(list1, p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, null, enumworldblocklayer, 0L, renderenv);
            this.renderQuadsFlat(p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, null, -1, true, p_renderModelFlat_5_, list1, renderenv);
            flag = true;
        }
        return flag;
    }

    private void renderQuadsSmooth(IBlockAccess p_renderQuadsSmooth_1_, IBlockState p_renderQuadsSmooth_2_, BlockPos p_renderQuadsSmooth_3_, WorldRenderer p_renderQuadsSmooth_4_, List<BakedQuad> p_renderQuadsSmooth_5_, RenderEnv p_renderQuadsSmooth_6_) {
        Block block = p_renderQuadsSmooth_2_.getBlock();
        float[] afloat = p_renderQuadsSmooth_6_.getQuadBounds();
        BitSet bitset = p_renderQuadsSmooth_6_.getBoundsFlags();
        AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = p_renderQuadsSmooth_6_.getAoFace();
        double d0 = p_renderQuadsSmooth_3_.getX();
        double d1 = p_renderQuadsSmooth_3_.getY();
        double d2 = p_renderQuadsSmooth_3_.getZ();
        Block.EnumOffsetType block$enumoffsettype = block.getOffsetType();
        if (block$enumoffsettype != Block.EnumOffsetType.NONE) {
            long i2 = MathHelper.getPositionRandom(p_renderQuadsSmooth_3_);
            d0 += ((double)((float)(i2 >> 16 & 0xFL) / 15.0f) - 0.5) * 0.5;
            d2 += ((double)((float)(i2 >> 24 & 0xFL) / 15.0f) - 0.5) * 0.5;
            if (block$enumoffsettype == Block.EnumOffsetType.XYZ) {
                d1 += ((double)((float)(i2 >> 20 & 0xFL) / 15.0f) - 1.0) * 0.2;
            }
        }
        for (BakedQuad bakedquad : p_renderQuadsSmooth_5_) {
            this.fillQuadBounds(block, bakedquad.getVertexData(), bakedquad.getFace(), afloat, bitset);
            blockmodelrenderer$ambientocclusionface.updateVertexBrightness(p_renderQuadsSmooth_1_, block, p_renderQuadsSmooth_3_, bakedquad.getFace(), afloat, bitset);
            if (bakedquad.getSprite().isEmissive) {
                blockmodelrenderer$ambientocclusionface.setMaxBlockLight();
            }
            if (p_renderQuadsSmooth_4_.isMultiTexture()) {
                p_renderQuadsSmooth_4_.addVertexData(bakedquad.getVertexDataSingle());
            } else {
                p_renderQuadsSmooth_4_.addVertexData(bakedquad.getVertexData());
            }
            p_renderQuadsSmooth_4_.putSprite(bakedquad.getSprite());
            p_renderQuadsSmooth_4_.putBrightness4(blockmodelrenderer$ambientocclusionface.vertexBrightness[0], blockmodelrenderer$ambientocclusionface.vertexBrightness[1], blockmodelrenderer$ambientocclusionface.vertexBrightness[2], blockmodelrenderer$ambientocclusionface.vertexBrightness[3]);
            int j2 = CustomColors.getColorMultiplier(bakedquad, p_renderQuadsSmooth_2_, p_renderQuadsSmooth_1_, p_renderQuadsSmooth_3_, p_renderQuadsSmooth_6_);
            if (!bakedquad.hasTintIndex() && j2 == -1) {
                if (separateAoLightValue) {
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(1.0f, 1.0f, 1.0f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], 4);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(1.0f, 1.0f, 1.0f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], 3);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(1.0f, 1.0f, 1.0f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], 2);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(1.0f, 1.0f, 1.0f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], 1);
                } else {
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], 4);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], 3);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], 2);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], 1);
                }
            } else {
                int k2 = j2 != -1 ? j2 : block.colorMultiplier(p_renderQuadsSmooth_1_, p_renderQuadsSmooth_3_, bakedquad.getTintIndex());
                if (EntityRenderer.anaglyphEnable) {
                    k2 = TextureUtil.anaglyphColor(k2);
                }
                float f2 = (float)(k2 >> 16 & 0xFF) / 255.0f;
                float f1 = (float)(k2 >> 8 & 0xFF) / 255.0f;
                float f22 = (float)(k2 & 0xFF) / 255.0f;
                if (separateAoLightValue) {
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(f2, f1, f22, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], 4);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(f2, f1, f22, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], 3);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(f2, f1, f22, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], 2);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(f2, f1, f22, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], 1);
                } else {
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f2, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f22, 4);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f2, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f22, 3);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f2, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f22, 2);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f2, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f22, 1);
                }
            }
            p_renderQuadsSmooth_4_.putPosition(d0, d1, d2);
        }
    }

    private void fillQuadBounds(Block blockIn, int[] vertexData, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags) {
        float f2 = 32.0f;
        float f1 = 32.0f;
        float f22 = 32.0f;
        float f3 = -32.0f;
        float f4 = -32.0f;
        float f5 = -32.0f;
        int i2 = vertexData.length / 4;
        int j2 = 0;
        while (j2 < 4) {
            float f6 = Float.intBitsToFloat(vertexData[j2 * i2]);
            float f7 = Float.intBitsToFloat(vertexData[j2 * i2 + 1]);
            float f8 = Float.intBitsToFloat(vertexData[j2 * i2 + 2]);
            f2 = Math.min(f2, f6);
            f1 = Math.min(f1, f7);
            f22 = Math.min(f22, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
            ++j2;
        }
        if (quadBounds != null) {
            quadBounds[EnumFacing.WEST.getIndex()] = f2;
            quadBounds[EnumFacing.EAST.getIndex()] = f3;
            quadBounds[EnumFacing.DOWN.getIndex()] = f1;
            quadBounds[EnumFacing.UP.getIndex()] = f4;
            quadBounds[EnumFacing.NORTH.getIndex()] = f22;
            quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
            int k2 = EnumFacing.VALUES.length;
            quadBounds[EnumFacing.WEST.getIndex() + k2] = 1.0f - f2;
            quadBounds[EnumFacing.EAST.getIndex() + k2] = 1.0f - f3;
            quadBounds[EnumFacing.DOWN.getIndex() + k2] = 1.0f - f1;
            quadBounds[EnumFacing.UP.getIndex() + k2] = 1.0f - f4;
            quadBounds[EnumFacing.NORTH.getIndex() + k2] = 1.0f - f22;
            quadBounds[EnumFacing.SOUTH.getIndex() + k2] = 1.0f - f5;
        }
        float f9 = 1.0E-4f;
        float f10 = 0.9999f;
        switch (facingIn) {
            case DOWN: {
                boundsFlags.set(1, f2 >= 1.0E-4f || f22 >= 1.0E-4f || f3 <= 0.9999f || f5 <= 0.9999f);
                boundsFlags.set(0, (f1 < 1.0E-4f || blockIn.isFullCube()) && f1 == f4);
                break;
            }
            case UP: {
                boundsFlags.set(1, f2 >= 1.0E-4f || f22 >= 1.0E-4f || f3 <= 0.9999f || f5 <= 0.9999f);
                boundsFlags.set(0, (f4 > 0.9999f || blockIn.isFullCube()) && f1 == f4);
                break;
            }
            case NORTH: {
                boundsFlags.set(1, f2 >= 1.0E-4f || f1 >= 1.0E-4f || f3 <= 0.9999f || f4 <= 0.9999f);
                boundsFlags.set(0, (f22 < 1.0E-4f || blockIn.isFullCube()) && f22 == f5);
                break;
            }
            case SOUTH: {
                boundsFlags.set(1, f2 >= 1.0E-4f || f1 >= 1.0E-4f || f3 <= 0.9999f || f4 <= 0.9999f);
                boundsFlags.set(0, (f5 > 0.9999f || blockIn.isFullCube()) && f22 == f5);
                break;
            }
            case WEST: {
                boundsFlags.set(1, f1 >= 1.0E-4f || f22 >= 1.0E-4f || f4 <= 0.9999f || f5 <= 0.9999f);
                boundsFlags.set(0, (f2 < 1.0E-4f || blockIn.isFullCube()) && f2 == f3);
                break;
            }
            case EAST: {
                boundsFlags.set(1, f1 >= 1.0E-4f || f22 >= 1.0E-4f || f4 <= 0.9999f || f5 <= 0.9999f);
                boundsFlags.set(0, (f3 > 0.9999f || blockIn.isFullCube()) && f2 == f3);
            }
        }
    }

    private void renderQuadsFlat(IBlockAccess p_renderQuadsFlat_1_, IBlockState p_renderQuadsFlat_2_, BlockPos p_renderQuadsFlat_3_, EnumFacing p_renderQuadsFlat_4_, int p_renderQuadsFlat_5_, boolean p_renderQuadsFlat_6_, WorldRenderer p_renderQuadsFlat_7_, List<BakedQuad> p_renderQuadsFlat_8_, RenderEnv p_renderQuadsFlat_9_) {
        Block block = p_renderQuadsFlat_2_.getBlock();
        BitSet bitset = p_renderQuadsFlat_9_.getBoundsFlags();
        double d0 = p_renderQuadsFlat_3_.getX();
        double d1 = p_renderQuadsFlat_3_.getY();
        double d2 = p_renderQuadsFlat_3_.getZ();
        Block.EnumOffsetType block$enumoffsettype = block.getOffsetType();
        if (block$enumoffsettype != Block.EnumOffsetType.NONE) {
            int i2 = p_renderQuadsFlat_3_.getX();
            int j2 = p_renderQuadsFlat_3_.getZ();
            long k2 = (long)(i2 * 3129871) ^ (long)j2 * 116129781L;
            k2 = k2 * k2 * 42317861L + k2 * 11L;
            d0 += ((double)((float)(k2 >> 16 & 0xFL) / 15.0f) - 0.5) * 0.5;
            d2 += ((double)((float)(k2 >> 24 & 0xFL) / 15.0f) - 0.5) * 0.5;
            if (block$enumoffsettype == Block.EnumOffsetType.XYZ) {
                d1 += ((double)((float)(k2 >> 20 & 0xFL) / 15.0f) - 1.0) * 0.2;
            }
        }
        for (BakedQuad bakedquad : p_renderQuadsFlat_8_) {
            if (p_renderQuadsFlat_6_) {
                this.fillQuadBounds(block, bakedquad.getVertexData(), bakedquad.getFace(), null, bitset);
                int n2 = p_renderQuadsFlat_5_ = bitset.get(0) ? block.getMixedBrightnessForBlock(p_renderQuadsFlat_1_, p_renderQuadsFlat_3_.offset(bakedquad.getFace())) : block.getMixedBrightnessForBlock(p_renderQuadsFlat_1_, p_renderQuadsFlat_3_);
            }
            if (bakedquad.getSprite().isEmissive) {
                p_renderQuadsFlat_5_ |= 0xF0;
            }
            if (p_renderQuadsFlat_7_.isMultiTexture()) {
                p_renderQuadsFlat_7_.addVertexData(bakedquad.getVertexDataSingle());
            } else {
                p_renderQuadsFlat_7_.addVertexData(bakedquad.getVertexData());
            }
            p_renderQuadsFlat_7_.putSprite(bakedquad.getSprite());
            p_renderQuadsFlat_7_.putBrightness4(p_renderQuadsFlat_5_, p_renderQuadsFlat_5_, p_renderQuadsFlat_5_, p_renderQuadsFlat_5_);
            int i1 = CustomColors.getColorMultiplier(bakedquad, p_renderQuadsFlat_2_, p_renderQuadsFlat_1_, p_renderQuadsFlat_3_, p_renderQuadsFlat_9_);
            if (bakedquad.hasTintIndex() || i1 != -1) {
                int l2 = i1 != -1 ? i1 : block.colorMultiplier(p_renderQuadsFlat_1_, p_renderQuadsFlat_3_, bakedquad.getTintIndex());
                if (EntityRenderer.anaglyphEnable) {
                    l2 = TextureUtil.anaglyphColor(l2);
                }
                float f2 = (float)(l2 >> 16 & 0xFF) / 255.0f;
                float f1 = (float)(l2 >> 8 & 0xFF) / 255.0f;
                float f22 = (float)(l2 & 0xFF) / 255.0f;
                p_renderQuadsFlat_7_.putColorMultiplier(f2, f1, f22, 4);
                p_renderQuadsFlat_7_.putColorMultiplier(f2, f1, f22, 3);
                p_renderQuadsFlat_7_.putColorMultiplier(f2, f1, f22, 2);
                p_renderQuadsFlat_7_.putColorMultiplier(f2, f1, f22, 1);
            }
            p_renderQuadsFlat_7_.putPosition(d0, d1, d2);
        }
    }

    public void renderModelBrightnessColor(IBakedModel bakedModel, float p_178262_2_, float red, float green, float blue) {
        EnumFacing[] enumFacingArray = EnumFacing.VALUES;
        int n2 = EnumFacing.VALUES.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumFacing enumfacing = enumFacingArray[n3];
            this.renderModelBrightnessColorQuads(p_178262_2_, red, green, blue, bakedModel.getFaceQuads(enumfacing));
            ++n3;
        }
        this.renderModelBrightnessColorQuads(p_178262_2_, red, green, blue, bakedModel.getGeneralQuads());
    }

    public void renderModelBrightness(IBakedModel model, IBlockState p_178266_2_, float brightness, boolean p_178266_4_) {
        Block block = p_178266_2_.getBlock();
        block.setBlockBoundsForItemRender();
        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        int i2 = block.getRenderColor(block.getStateForEntityRender(p_178266_2_));
        if (EntityRenderer.anaglyphEnable) {
            i2 = TextureUtil.anaglyphColor(i2);
        }
        float f2 = (float)(i2 >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(i2 >> 8 & 0xFF) / 255.0f;
        float f22 = (float)(i2 & 0xFF) / 255.0f;
        if (!p_178266_4_) {
            GlStateManager.color(brightness, brightness, brightness, 1.0f);
        }
        this.renderModelBrightnessColor(model, brightness, f2, f1, f22);
    }

    private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        for (BakedQuad bakedquad : listQuads) {
            worldrenderer.begin(7, DefaultVertexFormats.ITEM);
            worldrenderer.addVertexData(bakedquad.getVertexData());
            worldrenderer.putSprite(bakedquad.getSprite());
            if (bakedquad.hasTintIndex()) {
                worldrenderer.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            } else {
                worldrenderer.putColorRGB_F4(brightness, brightness, brightness);
            }
            Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            worldrenderer.putNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            tessellator.draw();
        }
    }

    public static float fixAoLightValue(float p_fixAoLightValue_0_) {
        return p_fixAoLightValue_0_ == 0.2f ? aoLightValueOpaque : p_fixAoLightValue_0_;
    }

    public static void updateAoLightValue() {
        aoLightValueOpaque = 1.0f - Config.getAmbientOcclusionLevel() * 0.8f;
        separateAoLightValue = Config.isShaders() && Shaders.isSeparateAo();
    }

    private void renderOverlayModels(IBlockAccess p_renderOverlayModels_1_, IBakedModel p_renderOverlayModels_2_, IBlockState p_renderOverlayModels_3_, BlockPos p_renderOverlayModels_4_, WorldRenderer p_renderOverlayModels_5_, boolean p_renderOverlayModels_6_, long p_renderOverlayModels_7_, RenderEnv p_renderOverlayModels_9_, boolean p_renderOverlayModels_10_) {
        if (p_renderOverlayModels_9_.isOverlaysRendered()) {
            int i2 = 0;
            while (i2 < OVERLAY_LAYERS.length) {
                EnumWorldBlockLayer enumworldblocklayer = OVERLAY_LAYERS[i2];
                ListQuadsOverlay listquadsoverlay = p_renderOverlayModels_9_.getListQuadsOverlay(enumworldblocklayer);
                if (listquadsoverlay.size() > 0) {
                    RegionRenderCacheBuilder regionrendercachebuilder = p_renderOverlayModels_9_.getRegionRenderCacheBuilder();
                    if (regionrendercachebuilder != null) {
                        WorldRenderer worldrenderer = regionrendercachebuilder.getWorldRendererByLayer(enumworldblocklayer);
                        if (!worldrenderer.isDrawing()) {
                            worldrenderer.begin(7, DefaultVertexFormats.BLOCK);
                            worldrenderer.setTranslation(p_renderOverlayModels_5_.getXOffset(), p_renderOverlayModels_5_.getYOffset(), p_renderOverlayModels_5_.getZOffset());
                        }
                        int j2 = 0;
                        while (j2 < listquadsoverlay.size()) {
                            BakedQuad bakedquad = listquadsoverlay.getQuad(j2);
                            List<BakedQuad> list = listquadsoverlay.getListQuadsSingle(bakedquad);
                            IBlockState iblockstate = listquadsoverlay.getBlockState(j2);
                            if (bakedquad.getQuadEmissive() != null) {
                                listquadsoverlay.addQuad(bakedquad.getQuadEmissive(), iblockstate);
                            }
                            p_renderOverlayModels_9_.reset(iblockstate, p_renderOverlayModels_4_);
                            if (p_renderOverlayModels_10_) {
                                this.renderQuadsSmooth(p_renderOverlayModels_1_, iblockstate, p_renderOverlayModels_4_, worldrenderer, list, p_renderOverlayModels_9_);
                            } else {
                                int k2 = iblockstate.getBlock().getMixedBrightnessForBlock(p_renderOverlayModels_1_, p_renderOverlayModels_4_.offset(bakedquad.getFace()));
                                this.renderQuadsFlat(p_renderOverlayModels_1_, iblockstate, p_renderOverlayModels_4_, bakedquad.getFace(), k2, false, worldrenderer, list, p_renderOverlayModels_9_);
                            }
                            ++j2;
                        }
                    }
                    listquadsoverlay.clear();
                }
                ++i2;
            }
        }
        if (Config.isBetterSnow() && !p_renderOverlayModels_9_.isBreakingAnimation() && BetterSnow.shouldRender(p_renderOverlayModels_1_, p_renderOverlayModels_3_, p_renderOverlayModels_4_)) {
            IBakedModel ibakedmodel = BetterSnow.getModelSnowLayer();
            IBlockState iblockstate1 = BetterSnow.getStateSnowLayer();
            this.renderModel(p_renderOverlayModels_1_, ibakedmodel, iblockstate1, p_renderOverlayModels_4_, p_renderOverlayModels_5_, p_renderOverlayModels_6_);
        }
    }

    public static class AmbientOcclusionFace {
        private final float[] vertexColorMultiplier = new float[4];
        private final int[] vertexBrightness = new int[4];

        public AmbientOcclusionFace() {
            this(null);
        }

        public AmbientOcclusionFace(BlockModelRenderer p_i46235_1_) {
        }

        public void setMaxBlockLight() {
            int i2 = 240;
            this.vertexBrightness[0] = this.vertexBrightness[0] | i2;
            this.vertexBrightness[1] = this.vertexBrightness[1] | i2;
            this.vertexBrightness[2] = this.vertexBrightness[2] | i2;
            this.vertexBrightness[3] = this.vertexBrightness[3] | i2;
            this.vertexColorMultiplier[0] = 1.0f;
            this.vertexColorMultiplier[1] = 1.0f;
            this.vertexColorMultiplier[2] = 1.0f;
            this.vertexColorMultiplier[3] = 1.0f;
        }

        public void updateVertexBrightness(IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags) {
            int l1;
            float f28;
            int k1;
            float f27;
            int j1;
            float f26;
            int i1;
            float f4;
            BlockPos blockpos = boundsFlags.get(0) ? blockPosIn.offset(facingIn) : blockPosIn;
            EnumNeighborInfo blockmodelrenderer$enumneighborinfo = EnumNeighborInfo.getNeighbourInfo(facingIn);
            BlockPos blockpos1 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[0]);
            BlockPos blockpos2 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[1]);
            BlockPos blockpos3 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
            BlockPos blockpos4 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
            int i2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos1);
            int j2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos2);
            int k2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos3);
            int l2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos4);
            float f2 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos1).getBlock().getAmbientOcclusionLightValue());
            float f1 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos2).getBlock().getAmbientOcclusionLightValue());
            float f22 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos3).getBlock().getAmbientOcclusionLightValue());
            float f3 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos4).getBlock().getAmbientOcclusionLightValue());
            boolean flag = blockAccessIn.getBlockState(blockpos1.offset(facingIn)).getBlock().isTranslucent();
            boolean flag1 = blockAccessIn.getBlockState(blockpos2.offset(facingIn)).getBlock().isTranslucent();
            boolean flag2 = blockAccessIn.getBlockState(blockpos3.offset(facingIn)).getBlock().isTranslucent();
            boolean flag3 = blockAccessIn.getBlockState(blockpos4.offset(facingIn)).getBlock().isTranslucent();
            if (!flag2 && !flag) {
                f4 = f2;
                i1 = i2;
            } else {
                BlockPos blockpos5 = blockpos1.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
                f4 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos5).getBlock().getAmbientOcclusionLightValue());
                i1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos5);
            }
            if (!flag3 && !flag) {
                f26 = f2;
                j1 = i2;
            } else {
                BlockPos blockpos6 = blockpos1.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
                f26 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos6).getBlock().getAmbientOcclusionLightValue());
                j1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos6);
            }
            if (!flag2 && !flag1) {
                f27 = f1;
                k1 = j2;
            } else {
                BlockPos blockpos7 = blockpos2.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
                f27 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos7).getBlock().getAmbientOcclusionLightValue());
                k1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos7);
            }
            if (!flag3 && !flag1) {
                f28 = f1;
                l1 = j2;
            } else {
                BlockPos blockpos8 = blockpos2.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
                f28 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos8).getBlock().getAmbientOcclusionLightValue());
                l1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos8);
            }
            int i3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);
            if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockPosIn.offset(facingIn)).getBlock().isOpaqueCube()) {
                i3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(facingIn));
            }
            float f5 = boundsFlags.get(0) ? blockAccessIn.getBlockState(blockpos).getBlock().getAmbientOcclusionLightValue() : blockAccessIn.getBlockState(blockPosIn).getBlock().getAmbientOcclusionLightValue();
            f5 = BlockModelRenderer.fixAoLightValue(f5);
            VertexTranslations blockmodelrenderer$vertextranslations = VertexTranslations.getVertexTranslations(facingIn);
            if (boundsFlags.get(1) && blockmodelrenderer$enumneighborinfo.field_178289_i) {
                float f29 = (f3 + f2 + f26 + f5) * 0.25f;
                float f30 = (f22 + f2 + f4 + f5) * 0.25f;
                float f31 = (f22 + f1 + f27 + f5) * 0.25f;
                float f32 = (f3 + f1 + f28 + f5) * 0.25f;
                float f10 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[1].field_178229_m];
                float f11 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[3].field_178229_m];
                float f12 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[5].field_178229_m];
                float f13 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[7].field_178229_m];
                float f14 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[1].field_178229_m];
                float f15 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[3].field_178229_m];
                float f16 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[5].field_178229_m];
                float f17 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[7].field_178229_m];
                float f18 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[1].field_178229_m];
                float f19 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[3].field_178229_m];
                float f20 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[5].field_178229_m];
                float f21 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[7].field_178229_m];
                float f222 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[1].field_178229_m];
                float f23 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[3].field_178229_m];
                float f24 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[5].field_178229_m];
                float f25 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[7].field_178229_m];
                this.vertexColorMultiplier[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178191_g] = f29 * f10 + f30 * f11 + f31 * f12 + f32 * f13;
                this.vertexColorMultiplier[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178200_h] = f29 * f14 + f30 * f15 + f31 * f16 + f32 * f17;
                this.vertexColorMultiplier[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178201_i] = f29 * f18 + f30 * f19 + f31 * f20 + f32 * f21;
                this.vertexColorMultiplier[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178198_j] = f29 * f222 + f30 * f23 + f31 * f24 + f32 * f25;
                int i22 = this.getAoBrightness(l2, i2, j1, i3);
                int j22 = this.getAoBrightness(k2, i2, i1, i3);
                int k22 = this.getAoBrightness(k2, j2, k1, i3);
                int l22 = this.getAoBrightness(l2, j2, l1, i3);
                this.vertexBrightness[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178191_g] = this.getVertexBrightness(i22, j22, k22, l22, f10, f11, f12, f13);
                this.vertexBrightness[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178200_h] = this.getVertexBrightness(i22, j22, k22, l22, f14, f15, f16, f17);
                this.vertexBrightness[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178201_i] = this.getVertexBrightness(i22, j22, k22, l22, f18, f19, f20, f21);
                this.vertexBrightness[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178198_j] = this.getVertexBrightness(i22, j22, k22, l22, f222, f23, f24, f25);
            } else {
                float f6 = (f3 + f2 + f26 + f5) * 0.25f;
                float f7 = (f22 + f2 + f4 + f5) * 0.25f;
                float f8 = (f22 + f1 + f27 + f5) * 0.25f;
                float f9 = (f3 + f1 + f28 + f5) * 0.25f;
                this.vertexBrightness[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178191_g] = this.getAoBrightness(l2, i2, j1, i3);
                this.vertexBrightness[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178200_h] = this.getAoBrightness(k2, i2, i1, i3);
                this.vertexBrightness[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178201_i] = this.getAoBrightness(k2, j2, k1, i3);
                this.vertexBrightness[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178198_j] = this.getAoBrightness(l2, j2, l1, i3);
                this.vertexColorMultiplier[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178191_g] = f6;
                this.vertexColorMultiplier[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178200_h] = f7;
                this.vertexColorMultiplier[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178201_i] = f8;
                this.vertexColorMultiplier[((VertexTranslations)blockmodelrenderer$vertextranslations).field_178198_j] = f9;
            }
        }

        private int getAoBrightness(int br1, int br2, int br3, int br4) {
            if (br1 == 0) {
                br1 = br4;
            }
            if (br2 == 0) {
                br2 = br4;
            }
            if (br3 == 0) {
                br3 = br4;
            }
            return br1 + br2 + br3 + br4 >> 2 & 0xFF00FF;
        }

        private int getVertexBrightness(int p_178203_1_, int p_178203_2_, int p_178203_3_, int p_178203_4_, float p_178203_5_, float p_178203_6_, float p_178203_7_, float p_178203_8_) {
            int i2 = (int)((float)(p_178203_1_ >> 16 & 0xFF) * p_178203_5_ + (float)(p_178203_2_ >> 16 & 0xFF) * p_178203_6_ + (float)(p_178203_3_ >> 16 & 0xFF) * p_178203_7_ + (float)(p_178203_4_ >> 16 & 0xFF) * p_178203_8_) & 0xFF;
            int j2 = (int)((float)(p_178203_1_ & 0xFF) * p_178203_5_ + (float)(p_178203_2_ & 0xFF) * p_178203_6_ + (float)(p_178203_3_ & 0xFF) * p_178203_7_ + (float)(p_178203_4_ & 0xFF) * p_178203_8_) & 0xFF;
            return i2 << 16 | j2;
        }
    }

    public static enum EnumNeighborInfo {
        DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5f, false, new Orientation[0], new Orientation[0], new Orientation[0], new Orientation[0]),
        UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0f, false, new Orientation[0], new Orientation[0], new Orientation[0], new Orientation[0]),
        NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8f, true, new Orientation[]{Orientation.UP, Orientation.FLIP_WEST, Orientation.UP, Orientation.WEST, Orientation.FLIP_UP, Orientation.WEST, Orientation.FLIP_UP, Orientation.FLIP_WEST}, new Orientation[]{Orientation.UP, Orientation.FLIP_EAST, Orientation.UP, Orientation.EAST, Orientation.FLIP_UP, Orientation.EAST, Orientation.FLIP_UP, Orientation.FLIP_EAST}, new Orientation[]{Orientation.DOWN, Orientation.FLIP_EAST, Orientation.DOWN, Orientation.EAST, Orientation.FLIP_DOWN, Orientation.EAST, Orientation.FLIP_DOWN, Orientation.FLIP_EAST}, new Orientation[]{Orientation.DOWN, Orientation.FLIP_WEST, Orientation.DOWN, Orientation.WEST, Orientation.FLIP_DOWN, Orientation.WEST, Orientation.FLIP_DOWN, Orientation.FLIP_WEST}),
        SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8f, true, new Orientation[]{Orientation.UP, Orientation.FLIP_WEST, Orientation.FLIP_UP, Orientation.FLIP_WEST, Orientation.FLIP_UP, Orientation.WEST, Orientation.UP, Orientation.WEST}, new Orientation[]{Orientation.DOWN, Orientation.FLIP_WEST, Orientation.FLIP_DOWN, Orientation.FLIP_WEST, Orientation.FLIP_DOWN, Orientation.WEST, Orientation.DOWN, Orientation.WEST}, new Orientation[]{Orientation.DOWN, Orientation.FLIP_EAST, Orientation.FLIP_DOWN, Orientation.FLIP_EAST, Orientation.FLIP_DOWN, Orientation.EAST, Orientation.DOWN, Orientation.EAST}, new Orientation[]{Orientation.UP, Orientation.FLIP_EAST, Orientation.FLIP_UP, Orientation.FLIP_EAST, Orientation.FLIP_UP, Orientation.EAST, Orientation.UP, Orientation.EAST}),
        WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6f, true, new Orientation[]{Orientation.UP, Orientation.SOUTH, Orientation.UP, Orientation.FLIP_SOUTH, Orientation.FLIP_UP, Orientation.FLIP_SOUTH, Orientation.FLIP_UP, Orientation.SOUTH}, new Orientation[]{Orientation.UP, Orientation.NORTH, Orientation.UP, Orientation.FLIP_NORTH, Orientation.FLIP_UP, Orientation.FLIP_NORTH, Orientation.FLIP_UP, Orientation.NORTH}, new Orientation[]{Orientation.DOWN, Orientation.NORTH, Orientation.DOWN, Orientation.FLIP_NORTH, Orientation.FLIP_DOWN, Orientation.FLIP_NORTH, Orientation.FLIP_DOWN, Orientation.NORTH}, new Orientation[]{Orientation.DOWN, Orientation.SOUTH, Orientation.DOWN, Orientation.FLIP_SOUTH, Orientation.FLIP_DOWN, Orientation.FLIP_SOUTH, Orientation.FLIP_DOWN, Orientation.SOUTH}),
        EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6f, true, new Orientation[]{Orientation.FLIP_DOWN, Orientation.SOUTH, Orientation.FLIP_DOWN, Orientation.FLIP_SOUTH, Orientation.DOWN, Orientation.FLIP_SOUTH, Orientation.DOWN, Orientation.SOUTH}, new Orientation[]{Orientation.FLIP_DOWN, Orientation.NORTH, Orientation.FLIP_DOWN, Orientation.FLIP_NORTH, Orientation.DOWN, Orientation.FLIP_NORTH, Orientation.DOWN, Orientation.NORTH}, new Orientation[]{Orientation.FLIP_UP, Orientation.NORTH, Orientation.FLIP_UP, Orientation.FLIP_NORTH, Orientation.UP, Orientation.FLIP_NORTH, Orientation.UP, Orientation.NORTH}, new Orientation[]{Orientation.FLIP_UP, Orientation.SOUTH, Orientation.FLIP_UP, Orientation.FLIP_SOUTH, Orientation.UP, Orientation.FLIP_SOUTH, Orientation.UP, Orientation.SOUTH});

        protected final EnumFacing[] field_178276_g;
        protected final float field_178288_h;
        protected final boolean field_178289_i;
        protected final Orientation[] field_178286_j;
        protected final Orientation[] field_178287_k;
        protected final Orientation[] field_178284_l;
        protected final Orientation[] field_178285_m;
        private static final EnumNeighborInfo[] VALUES;

        static {
            VALUES = new EnumNeighborInfo[6];
            EnumNeighborInfo.VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
            EnumNeighborInfo.VALUES[EnumFacing.UP.getIndex()] = UP;
            EnumNeighborInfo.VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
            EnumNeighborInfo.VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
            EnumNeighborInfo.VALUES[EnumFacing.WEST.getIndex()] = WEST;
            EnumNeighborInfo.VALUES[EnumFacing.EAST.getIndex()] = EAST;
        }

        private EnumNeighborInfo(EnumFacing[] p_i46236_3_, float p_i46236_4_, boolean p_i46236_5_, Orientation[] p_i46236_6_, Orientation[] p_i46236_7_, Orientation[] p_i46236_8_, Orientation[] p_i46236_9_) {
            this.field_178276_g = p_i46236_3_;
            this.field_178288_h = p_i46236_4_;
            this.field_178289_i = p_i46236_5_;
            this.field_178286_j = p_i46236_6_;
            this.field_178287_k = p_i46236_7_;
            this.field_178284_l = p_i46236_8_;
            this.field_178285_m = p_i46236_9_;
        }

        public static EnumNeighborInfo getNeighbourInfo(EnumFacing p_178273_0_) {
            return VALUES[p_178273_0_.getIndex()];
        }
    }

    public static enum Orientation {
        DOWN(EnumFacing.DOWN, false),
        UP(EnumFacing.UP, false),
        NORTH(EnumFacing.NORTH, false),
        SOUTH(EnumFacing.SOUTH, false),
        WEST(EnumFacing.WEST, false),
        EAST(EnumFacing.EAST, false),
        FLIP_DOWN(EnumFacing.DOWN, true),
        FLIP_UP(EnumFacing.UP, true),
        FLIP_NORTH(EnumFacing.NORTH, true),
        FLIP_SOUTH(EnumFacing.SOUTH, true),
        FLIP_WEST(EnumFacing.WEST, true),
        FLIP_EAST(EnumFacing.EAST, true);

        protected final int field_178229_m;

        private Orientation(EnumFacing p_i46233_3_, boolean p_i46233_4_) {
            this.field_178229_m = p_i46233_3_.getIndex() + (p_i46233_4_ ? EnumFacing.values().length : 0);
        }
    }

    static enum VertexTranslations {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int field_178191_g;
        private final int field_178200_h;
        private final int field_178201_i;
        private final int field_178198_j;
        private static final VertexTranslations[] VALUES;

        static {
            VALUES = new VertexTranslations[6];
            VertexTranslations.VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
            VertexTranslations.VALUES[EnumFacing.UP.getIndex()] = UP;
            VertexTranslations.VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
            VertexTranslations.VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
            VertexTranslations.VALUES[EnumFacing.WEST.getIndex()] = WEST;
            VertexTranslations.VALUES[EnumFacing.EAST.getIndex()] = EAST;
        }

        private VertexTranslations(int p_i46234_3_, int p_i46234_4_, int p_i46234_5_, int p_i46234_6_) {
            this.field_178191_g = p_i46234_3_;
            this.field_178200_h = p_i46234_4_;
            this.field_178201_i = p_i46234_5_;
            this.field_178198_j = p_i46234_6_;
        }

        public static VertexTranslations getVertexTranslations(EnumFacing p_178184_0_) {
            return VALUES[p_178184_0_.getIndex()];
        }
    }
}

