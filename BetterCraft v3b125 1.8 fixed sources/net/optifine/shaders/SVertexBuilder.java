/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.optifine.shaders.BlockAliases;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL20;

public class SVertexBuilder {
    int vertexSize;
    int offsetNormal;
    int offsetUV;
    int offsetUVCenter;
    boolean hasNormal;
    boolean hasTangent;
    boolean hasUV;
    boolean hasUVCenter;
    long[] entityData = new long[10];
    int entityDataIndex = 0;

    public SVertexBuilder() {
        this.entityData[this.entityDataIndex] = 0L;
    }

    public static void initVertexBuilder(WorldRenderer wrr) {
        wrr.sVertexBuilder = new SVertexBuilder();
    }

    public void pushEntity(long data) {
        ++this.entityDataIndex;
        this.entityData[this.entityDataIndex] = data;
    }

    public void popEntity() {
        this.entityData[this.entityDataIndex] = 0L;
        --this.entityDataIndex;
    }

    public static void pushEntity(IBlockState blockState, BlockPos blockPos, IBlockAccess blockAccess, WorldRenderer wrr) {
        int j2;
        int i2;
        Block block = blockState.getBlock();
        if (blockState instanceof BlockStateBase) {
            BlockStateBase blockstatebase = (BlockStateBase)blockState;
            i2 = blockstatebase.getBlockId();
            j2 = blockstatebase.getMetadata();
        } else {
            i2 = Block.getIdFromBlock(block);
            j2 = block.getMetaFromState(blockState);
        }
        int j1 = BlockAliases.getBlockAliasId(i2, j2);
        if (j1 >= 0) {
            i2 = j1;
        }
        int k2 = block.getRenderType();
        int l2 = ((k2 & 0xFFFF) << 16) + (i2 & 0xFFFF);
        int i1 = j2 & 0xFFFF;
        wrr.sVertexBuilder.pushEntity(((long)i1 << 32) + (long)l2);
    }

    public static void popEntity(WorldRenderer wrr) {
        wrr.sVertexBuilder.popEntity();
    }

    public static boolean popEntity(boolean value, WorldRenderer wrr) {
        wrr.sVertexBuilder.popEntity();
        return value;
    }

    public static void endSetVertexFormat(WorldRenderer wrr) {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
        VertexFormat vertexformat = wrr.getVertexFormat();
        svertexbuilder.vertexSize = vertexformat.getNextOffset() / 4;
        svertexbuilder.hasTangent = svertexbuilder.hasNormal = vertexformat.hasNormal();
        svertexbuilder.hasUV = vertexformat.hasUvOffset(0);
        svertexbuilder.offsetNormal = svertexbuilder.hasNormal ? vertexformat.getNormalOffset() / 4 : 0;
        svertexbuilder.offsetUV = svertexbuilder.hasUV ? vertexformat.getUvOffsetById(0) / 4 : 0;
        svertexbuilder.offsetUVCenter = 8;
    }

    public static void beginAddVertex(WorldRenderer wrr) {
        if (wrr.vertexCount == 0) {
            SVertexBuilder.endSetVertexFormat(wrr);
        }
    }

    public static void endAddVertex(WorldRenderer wrr) {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
        if (svertexbuilder.vertexSize == 14) {
            if (wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
                svertexbuilder.calcNormal(wrr, wrr.getBufferSize() - 4 * svertexbuilder.vertexSize);
            }
            long i2 = svertexbuilder.entityData[svertexbuilder.entityDataIndex];
            int j2 = wrr.getBufferSize() - 14 + 12;
            wrr.rawIntBuffer.put(j2, (int)i2);
            wrr.rawIntBuffer.put(j2 + 1, (int)(i2 >> 32));
        }
    }

    public static void beginAddVertexData(WorldRenderer wrr, int[] data) {
        if (wrr.vertexCount == 0) {
            SVertexBuilder.endSetVertexFormat(wrr);
        }
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
        if (svertexbuilder.vertexSize == 14) {
            long i2 = svertexbuilder.entityData[svertexbuilder.entityDataIndex];
            int j2 = 12;
            while (j2 + 1 < data.length) {
                data[j2] = (int)i2;
                data[j2 + 1] = (int)(i2 >> 32);
                j2 += 14;
            }
        }
    }

    public static void beginAddVertexData(WorldRenderer wrr, ByteBuffer byteBuffer) {
        if (wrr.vertexCount == 0) {
            SVertexBuilder.endSetVertexFormat(wrr);
        }
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
        if (svertexbuilder.vertexSize == 14) {
            long i2 = svertexbuilder.entityData[svertexbuilder.entityDataIndex];
            int j2 = byteBuffer.limit() / 4;
            int k2 = 12;
            while (k2 + 1 < j2) {
                int l2 = (int)i2;
                int i1 = (int)(i2 >> 32);
                byteBuffer.putInt(k2 * 4, l2);
                byteBuffer.putInt((k2 + 1) * 4, i1);
                k2 += 14;
            }
        }
    }

    public static void endAddVertexData(WorldRenderer wrr) {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
        if (svertexbuilder.vertexSize == 14 && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
            svertexbuilder.calcNormal(wrr, wrr.getBufferSize() - 4 * svertexbuilder.vertexSize);
        }
    }

    public void calcNormal(WorldRenderer wrr, int baseIndex) {
        FloatBuffer floatbuffer = wrr.rawFloatBuffer;
        IntBuffer intbuffer = wrr.rawIntBuffer;
        int i2 = wrr.getBufferSize();
        float f2 = floatbuffer.get(baseIndex + 0 * this.vertexSize);
        float f1 = floatbuffer.get(baseIndex + 0 * this.vertexSize + 1);
        float f22 = floatbuffer.get(baseIndex + 0 * this.vertexSize + 2);
        float f3 = floatbuffer.get(baseIndex + 0 * this.vertexSize + this.offsetUV);
        float f4 = floatbuffer.get(baseIndex + 0 * this.vertexSize + this.offsetUV + 1);
        float f5 = floatbuffer.get(baseIndex + 1 * this.vertexSize);
        float f6 = floatbuffer.get(baseIndex + 1 * this.vertexSize + 1);
        float f7 = floatbuffer.get(baseIndex + 1 * this.vertexSize + 2);
        float f8 = floatbuffer.get(baseIndex + 1 * this.vertexSize + this.offsetUV);
        float f9 = floatbuffer.get(baseIndex + 1 * this.vertexSize + this.offsetUV + 1);
        float f10 = floatbuffer.get(baseIndex + 2 * this.vertexSize);
        float f11 = floatbuffer.get(baseIndex + 2 * this.vertexSize + 1);
        float f12 = floatbuffer.get(baseIndex + 2 * this.vertexSize + 2);
        float f13 = floatbuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV);
        float f14 = floatbuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV + 1);
        float f15 = floatbuffer.get(baseIndex + 3 * this.vertexSize);
        float f16 = floatbuffer.get(baseIndex + 3 * this.vertexSize + 1);
        float f17 = floatbuffer.get(baseIndex + 3 * this.vertexSize + 2);
        float f18 = floatbuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV);
        float f19 = floatbuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV + 1);
        float f21 = f11 - f1;
        float f25 = f17 - f7;
        float f24 = f16 - f6;
        float f222 = f12 - f22;
        float f30 = f21 * f25 - f24 * f222;
        float f23 = f15 - f5;
        float f20 = f10 - f2;
        float f31 = f222 * f23 - f25 * f20;
        float f32 = f20 * f24 - f23 * f21;
        float f33 = f30 * f30 + f31 * f31 + f32 * f32;
        float f34 = (double)f33 != 0.0 ? (float)(1.0 / Math.sqrt(f33)) : 1.0f;
        f30 *= f34;
        f31 *= f34;
        f32 *= f34;
        f20 = f5 - f2;
        f21 = f6 - f1;
        f222 = f7 - f22;
        float f26 = f8 - f3;
        float f27 = f9 - f4;
        f23 = f10 - f2;
        f24 = f11 - f1;
        f25 = f12 - f22;
        float f28 = f13 - f3;
        float f29 = f14 - f4;
        float f35 = f26 * f29 - f28 * f27;
        float f36 = f35 != 0.0f ? 1.0f / f35 : 1.0f;
        float f37 = (f29 * f20 - f27 * f23) * f36;
        float f38 = (f29 * f21 - f27 * f24) * f36;
        float f39 = (f29 * f222 - f27 * f25) * f36;
        float f40 = (f26 * f23 - f28 * f20) * f36;
        float f41 = (f26 * f24 - f28 * f21) * f36;
        float f42 = (f26 * f25 - f28 * f222) * f36;
        f33 = f37 * f37 + f38 * f38 + f39 * f39;
        f34 = (double)f33 != 0.0 ? (float)(1.0 / Math.sqrt(f33)) : 1.0f;
        f37 *= f34;
        f38 *= f34;
        f39 *= f34;
        f33 = f40 * f40 + f41 * f41 + f42 * f42;
        f34 = (double)f33 != 0.0 ? (float)(1.0 / Math.sqrt(f33)) : 1.0f;
        float f43 = f32 * f38 - f31 * f39;
        float f44 = f30 * f39 - f32 * f37;
        float f45 = f31 * f37 - f30 * f38;
        float f46 = (f40 *= f34) * f43 + (f41 *= f34) * f44 + (f42 *= f34) * f45 < 0.0f ? -1.0f : 1.0f;
        int j2 = (int)(f30 * 127.0f) & 0xFF;
        int k2 = (int)(f31 * 127.0f) & 0xFF;
        int l2 = (int)(f32 * 127.0f) & 0xFF;
        int i1 = (l2 << 16) + (k2 << 8) + j2;
        intbuffer.put(baseIndex + 0 * this.vertexSize + this.offsetNormal, i1);
        intbuffer.put(baseIndex + 1 * this.vertexSize + this.offsetNormal, i1);
        intbuffer.put(baseIndex + 2 * this.vertexSize + this.offsetNormal, i1);
        intbuffer.put(baseIndex + 3 * this.vertexSize + this.offsetNormal, i1);
        int j1 = ((int)(f37 * 32767.0f) & 0xFFFF) + (((int)(f38 * 32767.0f) & 0xFFFF) << 16);
        int k1 = ((int)(f39 * 32767.0f) & 0xFFFF) + (((int)(f46 * 32767.0f) & 0xFFFF) << 16);
        intbuffer.put(baseIndex + 0 * this.vertexSize + 10, j1);
        intbuffer.put(baseIndex + 0 * this.vertexSize + 10 + 1, k1);
        intbuffer.put(baseIndex + 1 * this.vertexSize + 10, j1);
        intbuffer.put(baseIndex + 1 * this.vertexSize + 10 + 1, k1);
        intbuffer.put(baseIndex + 2 * this.vertexSize + 10, j1);
        intbuffer.put(baseIndex + 2 * this.vertexSize + 10 + 1, k1);
        intbuffer.put(baseIndex + 3 * this.vertexSize + 10, j1);
        intbuffer.put(baseIndex + 3 * this.vertexSize + 10 + 1, k1);
        float f47 = (f3 + f8 + f13 + f18) / 4.0f;
        float f48 = (f4 + f9 + f14 + f19) / 4.0f;
        floatbuffer.put(baseIndex + 0 * this.vertexSize + 8, f47);
        floatbuffer.put(baseIndex + 0 * this.vertexSize + 8 + 1, f48);
        floatbuffer.put(baseIndex + 1 * this.vertexSize + 8, f47);
        floatbuffer.put(baseIndex + 1 * this.vertexSize + 8 + 1, f48);
        floatbuffer.put(baseIndex + 2 * this.vertexSize + 8, f47);
        floatbuffer.put(baseIndex + 2 * this.vertexSize + 8 + 1, f48);
        floatbuffer.put(baseIndex + 3 * this.vertexSize + 8, f47);
        floatbuffer.put(baseIndex + 3 * this.vertexSize + 8 + 1, f48);
    }

    public static void calcNormalChunkLayer(WorldRenderer wrr) {
        if (wrr.getVertexFormat().hasNormal() && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
            SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
            SVertexBuilder.endSetVertexFormat(wrr);
            int i2 = wrr.vertexCount * svertexbuilder.vertexSize;
            int j2 = 0;
            while (j2 < i2) {
                svertexbuilder.calcNormal(wrr, j2);
                j2 += svertexbuilder.vertexSize * 4;
            }
        }
    }

    public static void drawArrays(int drawMode, int first, int count, WorldRenderer wrr) {
        if (count != 0) {
            VertexFormat vertexformat = wrr.getVertexFormat();
            int i2 = vertexformat.getNextOffset();
            if (i2 == 56) {
                ByteBuffer bytebuffer = wrr.getByteBuffer();
                bytebuffer.position(32);
                GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, i2, bytebuffer);
                bytebuffer.position(40);
                GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, i2, bytebuffer);
                bytebuffer.position(48);
                GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, i2, bytebuffer);
                bytebuffer.position(0);
                GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
                GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
                GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
                GlStateManager.glDrawArrays(drawMode, first, count);
                GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
                GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
                GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
            } else {
                GlStateManager.glDrawArrays(drawMode, first, count);
            }
        }
    }
}

