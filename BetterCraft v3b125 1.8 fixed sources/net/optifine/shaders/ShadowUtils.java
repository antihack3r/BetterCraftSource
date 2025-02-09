/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.optifine.shaders.IteratorRenderChunks;
import net.optifine.shaders.Shaders;

public class ShadowUtils {
    public static Iterator<RenderChunk> makeShadowChunkIterator(WorldClient world, double partialTicks, Entity viewEntity, int renderDistanceChunks, ViewFrustum viewFrustum) {
        float f2 = Shaders.getShadowRenderDistance();
        if (f2 > 0.0f && f2 < (float)((renderDistanceChunks - 1) * 16)) {
            int i2 = MathHelper.ceiling_float_int(f2 / 16.0f) + 1;
            float f6 = world.getCelestialAngleRadians((float)partialTicks);
            float f1 = Shaders.sunPathRotation * MathHelper.deg2Rad;
            float f22 = f6 > MathHelper.PId2 && f6 < 3.0f * MathHelper.PId2 ? f6 + MathHelper.PI : f6;
            float f3 = -MathHelper.sin(f22);
            float f4 = MathHelper.cos(f22) * MathHelper.cos(f1);
            float f5 = -MathHelper.cos(f22) * MathHelper.sin(f1);
            BlockPos blockpos = new BlockPos(MathHelper.floor_double(viewEntity.posX) >> 4, MathHelper.floor_double(viewEntity.posY) >> 4, MathHelper.floor_double(viewEntity.posZ) >> 4);
            BlockPos blockpos1 = blockpos.add(-f3 * (float)i2, -f4 * (float)i2, -f5 * (float)i2);
            BlockPos blockpos2 = blockpos.add(f3 * (float)renderDistanceChunks, f4 * (float)renderDistanceChunks, f5 * (float)renderDistanceChunks);
            IteratorRenderChunks iteratorrenderchunks = new IteratorRenderChunks(viewFrustum, blockpos1, blockpos2, i2, i2);
            return iteratorrenderchunks;
        }
        List<RenderChunk> list = Arrays.asList(viewFrustum.renderChunks);
        Iterator<RenderChunk> iterator = list.iterator();
        return iterator;
    }
}

