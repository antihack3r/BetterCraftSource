/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.optifine.CustomColors;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;

public class BlockFluidRenderer {
    private TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
    private TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];

    public BlockFluidRenderer() {
        this.initAtlasSprites();
    }

    protected void initAtlasSprites() {
        TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        this.atlasSpritesLava[0] = texturemap.getAtlasSprite("minecraft:blocks/lava_still");
        this.atlasSpritesLava[1] = texturemap.getAtlasSprite("minecraft:blocks/lava_flow");
        this.atlasSpritesWater[0] = texturemap.getAtlasSprite("minecraft:blocks/water_still");
        this.atlasSpritesWater[1] = texturemap.getAtlasSprite("minecraft:blocks/water_flow");
    }

    public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn) {
        boolean flag2;
        try {
            if (Config.isShaders()) {
                SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccess, worldRendererIn);
            }
            BlockLiquid blockliquid = (BlockLiquid)blockStateIn.getBlock();
            blockliquid.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
            TextureAtlasSprite[] atextureatlassprite = blockliquid.getMaterial() == Material.lava ? this.atlasSpritesLava : this.atlasSpritesWater;
            RenderEnv renderenv = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
            int i2 = CustomColors.getFluidColor(blockAccess, blockStateIn, blockPosIn, renderenv);
            float f2 = (float)(i2 >> 16 & 0xFF) / 255.0f;
            float f1 = (float)(i2 >> 8 & 0xFF) / 255.0f;
            float f22 = (float)(i2 & 0xFF) / 255.0f;
            boolean flag = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP);
            boolean flag1 = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN);
            boolean[] aboolean = renderenv.getBorderFlags();
            aboolean[0] = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH);
            aboolean[1] = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH);
            aboolean[2] = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST);
            aboolean[3] = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST);
            if (flag || flag1 || aboolean[0] || aboolean[1] || aboolean[2] || aboolean[3]) {
                boolean flag3;
                boolean flag22 = false;
                float f3 = 0.5f;
                float f4 = 1.0f;
                float f5 = 0.8f;
                float f6 = 0.6f;
                Material material = blockliquid.getMaterial();
                float f7 = this.getFluidHeight(blockAccess, blockPosIn, material);
                float f8 = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
                float f9 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
                float f10 = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
                double d0 = blockPosIn.getX();
                double d1 = blockPosIn.getY();
                double d2 = blockPosIn.getZ();
                float f11 = 0.001f;
                if (flag) {
                    float f20;
                    float f16;
                    float f19;
                    float f15;
                    float f18;
                    float f14;
                    float f17;
                    float f13;
                    flag22 = true;
                    TextureAtlasSprite textureatlassprite = atextureatlassprite[0];
                    float f12 = (float)BlockLiquid.getFlowDirection(blockAccess, blockPosIn, material);
                    if (f12 > -999.0f) {
                        textureatlassprite = atextureatlassprite[1];
                    }
                    worldRendererIn.setSprite(textureatlassprite);
                    f7 -= f11;
                    f8 -= f11;
                    f9 -= f11;
                    f10 -= f11;
                    if (f12 < -999.0f) {
                        f13 = textureatlassprite.getInterpolatedU(0.0);
                        f17 = textureatlassprite.getInterpolatedV(0.0);
                        f14 = f13;
                        f18 = textureatlassprite.getInterpolatedV(16.0);
                        f15 = textureatlassprite.getInterpolatedU(16.0);
                        f19 = f18;
                        f16 = f15;
                        f20 = f17;
                    } else {
                        float f21 = MathHelper.sin(f12) * 0.25f;
                        float f222 = MathHelper.cos(f12) * 0.25f;
                        float f23 = 8.0f;
                        f13 = textureatlassprite.getInterpolatedU(8.0f + (-f222 - f21) * 16.0f);
                        f17 = textureatlassprite.getInterpolatedV(8.0f + (-f222 + f21) * 16.0f);
                        f14 = textureatlassprite.getInterpolatedU(8.0f + (-f222 + f21) * 16.0f);
                        f18 = textureatlassprite.getInterpolatedV(8.0f + (f222 + f21) * 16.0f);
                        f15 = textureatlassprite.getInterpolatedU(8.0f + (f222 + f21) * 16.0f);
                        f19 = textureatlassprite.getInterpolatedV(8.0f + (f222 - f21) * 16.0f);
                        f16 = textureatlassprite.getInterpolatedU(8.0f + (f222 - f21) * 16.0f);
                        f20 = textureatlassprite.getInterpolatedV(8.0f + (-f222 - f21) * 16.0f);
                    }
                    int k2 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn);
                    int l2 = k2 >> 16 & 0xFFFF;
                    int i3 = k2 & 0xFFFF;
                    float f24 = f4 * f2;
                    float f25 = f4 * f1;
                    float f26 = f4 * f22;
                    worldRendererIn.pos(d0 + 0.0, d1 + (double)f7, d2 + 0.0).color(f24, f25, f26, 1.0f).tex(f13, f17).lightmap(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 0.0, d1 + (double)f8, d2 + 1.0).color(f24, f25, f26, 1.0f).tex(f14, f18).lightmap(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 1.0, d1 + (double)f9, d2 + 1.0).color(f24, f25, f26, 1.0f).tex(f15, f19).lightmap(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 1.0, d1 + (double)f10, d2 + 0.0).color(f24, f25, f26, 1.0f).tex(f16, f20).lightmap(l2, i3).endVertex();
                    if (blockliquid.shouldRenderSides(blockAccess, blockPosIn.up())) {
                        worldRendererIn.pos(d0 + 0.0, d1 + (double)f7, d2 + 0.0).color(f24, f25, f26, 1.0f).tex(f13, f17).lightmap(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 1.0, d1 + (double)f10, d2 + 0.0).color(f24, f25, f26, 1.0f).tex(f16, f20).lightmap(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 1.0, d1 + (double)f9, d2 + 1.0).color(f24, f25, f26, 1.0f).tex(f15, f19).lightmap(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 0.0, d1 + (double)f8, d2 + 1.0).color(f24, f25, f26, 1.0f).tex(f14, f18).lightmap(l2, i3).endVertex();
                    }
                }
                if (flag1) {
                    worldRendererIn.setSprite(atextureatlassprite[0]);
                    float f35 = atextureatlassprite[0].getMinU();
                    float f36 = atextureatlassprite[0].getMaxU();
                    float f37 = atextureatlassprite[0].getMinV();
                    float f38 = atextureatlassprite[0].getMaxV();
                    int l1 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn.down());
                    int i22 = l1 >> 16 & 0xFFFF;
                    int j2 = l1 & 0xFFFF;
                    float f41 = FaceBakery.getFaceBrightness(EnumFacing.DOWN);
                    worldRendererIn.pos(d0, d1, d2 + 1.0).color(f2 * f41, f1 * f41, f22 * f41, 1.0f).tex(f35, f38).lightmap(i22, j2).endVertex();
                    worldRendererIn.pos(d0, d1, d2).color(f2 * f41, f1 * f41, f22 * f41, 1.0f).tex(f35, f37).lightmap(i22, j2).endVertex();
                    worldRendererIn.pos(d0 + 1.0, d1, d2).color(f2 * f41, f1 * f41, f22 * f41, 1.0f).tex(f36, f37).lightmap(i22, j2).endVertex();
                    worldRendererIn.pos(d0 + 1.0, d1, d2 + 1.0).color(f2 * f41, f1 * f41, f22 * f41, 1.0f).tex(f36, f38).lightmap(i22, j2).endVertex();
                    flag22 = true;
                }
                int i1 = 0;
                while (i1 < 4) {
                    int j1 = 0;
                    int k1 = 0;
                    if (i1 == 0) {
                        --k1;
                    }
                    if (i1 == 1) {
                        ++k1;
                    }
                    if (i1 == 2) {
                        --j1;
                    }
                    if (i1 == 3) {
                        ++j1;
                    }
                    BlockPos blockpos = blockPosIn.add(j1, 0, k1);
                    TextureAtlasSprite textureatlassprite1 = atextureatlassprite[1];
                    worldRendererIn.setSprite(textureatlassprite1);
                    if (aboolean[i1]) {
                        double d6;
                        double d4;
                        double d5;
                        double d3;
                        float f40;
                        float f39;
                        if (i1 == 0) {
                            f39 = f7;
                            f40 = f10;
                            d3 = d0;
                            d5 = d0 + 1.0;
                            d4 = d2 + (double)f11;
                            d6 = d2 + (double)f11;
                        } else if (i1 == 1) {
                            f39 = f9;
                            f40 = f8;
                            d3 = d0 + 1.0;
                            d5 = d0;
                            d4 = d2 + 1.0 - (double)f11;
                            d6 = d2 + 1.0 - (double)f11;
                        } else if (i1 == 2) {
                            f39 = f8;
                            f40 = f7;
                            d3 = d0 + (double)f11;
                            d5 = d0 + (double)f11;
                            d4 = d2 + 1.0;
                            d6 = d2;
                        } else {
                            f39 = f10;
                            f40 = f9;
                            d3 = d0 + 1.0 - (double)f11;
                            d5 = d0 + 1.0 - (double)f11;
                            d4 = d2;
                            d6 = d2 + 1.0;
                        }
                        flag22 = true;
                        float f42 = textureatlassprite1.getInterpolatedU(0.0);
                        float f27 = textureatlassprite1.getInterpolatedU(8.0);
                        float f28 = textureatlassprite1.getInterpolatedV((1.0f - f39) * 16.0f * 0.5f);
                        float f29 = textureatlassprite1.getInterpolatedV((1.0f - f40) * 16.0f * 0.5f);
                        float f30 = textureatlassprite1.getInterpolatedV(8.0);
                        int j2 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockpos);
                        int k2 = j2 >> 16 & 0xFFFF;
                        int l2 = j2 & 0xFFFF;
                        float f31 = i1 < 2 ? FaceBakery.getFaceBrightness(EnumFacing.NORTH) : FaceBakery.getFaceBrightness(EnumFacing.WEST);
                        float f32 = f4 * f31 * f2;
                        float f33 = f4 * f31 * f1;
                        float f34 = f4 * f31 * f22;
                        worldRendererIn.pos(d3, d1 + (double)f39, d4).color(f32, f33, f34, 1.0f).tex(f42, f28).lightmap(k2, l2).endVertex();
                        worldRendererIn.pos(d5, d1 + (double)f40, d6).color(f32, f33, f34, 1.0f).tex(f27, f29).lightmap(k2, l2).endVertex();
                        worldRendererIn.pos(d5, d1 + 0.0, d6).color(f32, f33, f34, 1.0f).tex(f27, f30).lightmap(k2, l2).endVertex();
                        worldRendererIn.pos(d3, d1 + 0.0, d4).color(f32, f33, f34, 1.0f).tex(f42, f30).lightmap(k2, l2).endVertex();
                        worldRendererIn.pos(d3, d1 + 0.0, d4).color(f32, f33, f34, 1.0f).tex(f42, f30).lightmap(k2, l2).endVertex();
                        worldRendererIn.pos(d5, d1 + 0.0, d6).color(f32, f33, f34, 1.0f).tex(f27, f30).lightmap(k2, l2).endVertex();
                        worldRendererIn.pos(d5, d1 + (double)f40, d6).color(f32, f33, f34, 1.0f).tex(f27, f29).lightmap(k2, l2).endVertex();
                        worldRendererIn.pos(d3, d1 + (double)f39, d4).color(f32, f33, f34, 1.0f).tex(f42, f28).lightmap(k2, l2).endVertex();
                    }
                    ++i1;
                }
                worldRendererIn.setSprite(null);
                boolean bl2 = flag3 = flag22;
                return bl2;
            }
            flag2 = false;
        }
        finally {
            if (Config.isShaders()) {
                SVertexBuilder.popEntity(worldRendererIn);
            }
        }
        return flag2;
    }

    private float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
        int i2 = 0;
        float f2 = 0.0f;
        int j2 = 0;
        while (j2 < 4) {
            BlockPos blockpos = blockPosIn.add(-(j2 & 1), 0, -(j2 >> 1 & 1));
            if (blockAccess.getBlockState(blockpos.up()).getBlock().getMaterial() == blockMaterial) {
                return 1.0f;
            }
            IBlockState iblockstate = blockAccess.getBlockState(blockpos);
            Material material = iblockstate.getBlock().getMaterial();
            if (material != blockMaterial) {
                if (!material.isSolid()) {
                    f2 += 1.0f;
                    ++i2;
                }
            } else {
                int k2 = iblockstate.getValue(BlockLiquid.LEVEL);
                if (k2 >= 8 || k2 == 0) {
                    f2 += BlockLiquid.getLiquidHeightPercent(k2) * 10.0f;
                    i2 += 10;
                }
                f2 += BlockLiquid.getLiquidHeightPercent(k2);
                ++i2;
            }
            ++j2;
        }
        return 1.0f - f2 / (float)i2;
    }
}

