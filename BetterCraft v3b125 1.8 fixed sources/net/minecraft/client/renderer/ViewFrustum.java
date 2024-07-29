/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.optifine.render.VboRegion;

public class ViewFrustum {
    protected final RenderGlobal renderGlobal;
    protected final World world;
    protected int countChunksY;
    protected int countChunksX;
    protected int countChunksZ;
    public RenderChunk[] renderChunks;
    private Map<ChunkCoordIntPair, VboRegion[]> mapVboRegions = new HashMap<ChunkCoordIntPair, VboRegion[]>();

    public ViewFrustum(World worldIn, int renderDistanceChunks, RenderGlobal p_i46246_3_, IRenderChunkFactory renderChunkFactory) {
        this.renderGlobal = p_i46246_3_;
        this.world = worldIn;
        this.setCountChunksXYZ(renderDistanceChunks);
        this.createRenderChunks(renderChunkFactory);
    }

    protected void createRenderChunks(IRenderChunkFactory renderChunkFactory) {
        int i2 = this.countChunksX * this.countChunksY * this.countChunksZ;
        this.renderChunks = new RenderChunk[i2];
        int j2 = 0;
        int k2 = 0;
        while (k2 < this.countChunksX) {
            int l2 = 0;
            while (l2 < this.countChunksY) {
                int i1 = 0;
                while (i1 < this.countChunksZ) {
                    int j1 = (i1 * this.countChunksY + l2) * this.countChunksX + k2;
                    BlockPos blockpos = new BlockPos(k2 * 16, l2 * 16, i1 * 16);
                    this.renderChunks[j1] = renderChunkFactory.makeRenderChunk(this.world, this.renderGlobal, blockpos, j2++);
                    if (Config.isVbo() && Config.isRenderRegions()) {
                        this.updateVboRegion(this.renderChunks[j1]);
                    }
                    ++i1;
                }
                ++l2;
            }
            ++k2;
        }
        int k1 = 0;
        while (k1 < this.renderChunks.length) {
            RenderChunk renderchunk1 = this.renderChunks[k1];
            int l1 = 0;
            while (l1 < EnumFacing.VALUES.length) {
                EnumFacing enumfacing = EnumFacing.VALUES[l1];
                BlockPos blockpos1 = renderchunk1.getBlockPosOffset16(enumfacing);
                RenderChunk renderchunk = this.getRenderChunk(blockpos1);
                renderchunk1.setRenderChunkNeighbour(enumfacing, renderchunk);
                ++l1;
            }
            ++k1;
        }
    }

    public void deleteGlResources() {
        RenderChunk[] renderChunkArray = this.renderChunks;
        int n2 = this.renderChunks.length;
        int n3 = 0;
        while (n3 < n2) {
            RenderChunk renderchunk = renderChunkArray[n3];
            renderchunk.deleteGlResources();
            ++n3;
        }
        this.deleteVboRegions();
    }

    protected void setCountChunksXYZ(int renderDistanceChunks) {
        int i2;
        this.countChunksX = i2 = renderDistanceChunks * 2 + 1;
        this.countChunksY = 16;
        this.countChunksZ = i2;
    }

    public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
        int i2 = MathHelper.floor_double(viewEntityX) - 8;
        int j2 = MathHelper.floor_double(viewEntityZ) - 8;
        int k2 = this.countChunksX * 16;
        int l2 = 0;
        while (l2 < this.countChunksX) {
            int i1 = this.func_178157_a(i2, k2, l2);
            int j1 = 0;
            while (j1 < this.countChunksZ) {
                int k1 = this.func_178157_a(j2, k2, j1);
                int l1 = 0;
                while (l1 < this.countChunksY) {
                    BlockPos blockpos1;
                    int i22 = l1 * 16;
                    RenderChunk renderchunk = this.renderChunks[(j1 * this.countChunksY + l1) * this.countChunksX + l2];
                    BlockPos blockpos = renderchunk.getPosition();
                    if (!(blockpos.getX() == i1 && blockpos.getY() == i22 && blockpos.getZ() == k1 || (blockpos1 = new BlockPos(i1, i22, k1)).equals(renderchunk.getPosition()))) {
                        renderchunk.setPosition(blockpos1);
                    }
                    ++l1;
                }
                ++j1;
            }
            ++l2;
        }
    }

    private int func_178157_a(int p_178157_1_, int p_178157_2_, int p_178157_3_) {
        int i2 = p_178157_3_ * 16;
        int j2 = i2 - p_178157_1_ + p_178157_2_ / 2;
        if (j2 < 0) {
            j2 -= p_178157_2_ - 1;
        }
        return i2 - j2 / p_178157_2_ * p_178157_2_;
    }

    public void markBlocksForUpdate(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        int i2 = MathHelper.bucketInt(fromX, 16);
        int j2 = MathHelper.bucketInt(fromY, 16);
        int k2 = MathHelper.bucketInt(fromZ, 16);
        int l2 = MathHelper.bucketInt(toX, 16);
        int i1 = MathHelper.bucketInt(toY, 16);
        int j1 = MathHelper.bucketInt(toZ, 16);
        int k1 = i2;
        while (k1 <= l2) {
            int l1 = k1 % this.countChunksX;
            if (l1 < 0) {
                l1 += this.countChunksX;
            }
            int i22 = j2;
            while (i22 <= i1) {
                int j22 = i22 % this.countChunksY;
                if (j22 < 0) {
                    j22 += this.countChunksY;
                }
                int k22 = k2;
                while (k22 <= j1) {
                    int l22 = k22 % this.countChunksZ;
                    if (l22 < 0) {
                        l22 += this.countChunksZ;
                    }
                    int i3 = (l22 * this.countChunksY + j22) * this.countChunksX + l1;
                    RenderChunk renderchunk = this.renderChunks[i3];
                    renderchunk.setNeedsUpdate(true);
                    ++k22;
                }
                ++i22;
            }
            ++k1;
        }
    }

    public RenderChunk getRenderChunk(BlockPos pos) {
        int i2 = pos.getX() >> 4;
        int j2 = pos.getY() >> 4;
        int k2 = pos.getZ() >> 4;
        if (j2 >= 0 && j2 < this.countChunksY) {
            if ((i2 %= this.countChunksX) < 0) {
                i2 += this.countChunksX;
            }
            if ((k2 %= this.countChunksZ) < 0) {
                k2 += this.countChunksZ;
            }
            int l2 = (k2 * this.countChunksY + j2) * this.countChunksX + i2;
            return this.renderChunks[l2];
        }
        return null;
    }

    private void updateVboRegion(RenderChunk p_updateVboRegion_1_) {
        BlockPos blockpos = p_updateVboRegion_1_.getPosition();
        int i2 = blockpos.getX() >> 8 << 8;
        int j2 = blockpos.getZ() >> 8 << 8;
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i2, j2);
        EnumWorldBlockLayer[] aenumworldblocklayer = RenderChunk.ENUM_WORLD_BLOCK_LAYERS;
        VboRegion[] avboregion = this.mapVboRegions.get(chunkcoordintpair);
        if (avboregion == null) {
            avboregion = new VboRegion[aenumworldblocklayer.length];
            int k2 = 0;
            while (k2 < aenumworldblocklayer.length) {
                avboregion[k2] = new VboRegion(aenumworldblocklayer[k2]);
                ++k2;
            }
            this.mapVboRegions.put(chunkcoordintpair, avboregion);
        }
        int l2 = 0;
        while (l2 < aenumworldblocklayer.length) {
            VboRegion vboregion = avboregion[l2];
            if (vboregion != null) {
                p_updateVboRegion_1_.getVertexBufferByLayer(l2).setVboRegion(vboregion);
            }
            ++l2;
        }
    }

    public void deleteVboRegions() {
        for (ChunkCoordIntPair chunkcoordintpair : this.mapVboRegions.keySet()) {
            VboRegion[] avboregion = this.mapVboRegions.get(chunkcoordintpair);
            int i2 = 0;
            while (i2 < avboregion.length) {
                VboRegion vboregion = avboregion[i2];
                if (vboregion != null) {
                    vboregion.deleteGlBuffers();
                }
                avboregion[i2] = null;
                ++i2;
            }
        }
        this.mapVboRegions.clear();
    }
}

