/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour
extends BakedQuad {
    private final TextureAtlasSprite texture;

    public BreakingFour(BakedQuad quad, TextureAtlasSprite textureIn) {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, FaceBakery.getFacingFromVertexData(quad.getVertexData()));
        this.texture = textureIn;
        this.remapQuad();
        this.fixVertexData();
    }

    private void remapQuad() {
        int i2 = 0;
        while (i2 < 4) {
            this.remapVert(i2);
            ++i2;
        }
    }

    private void remapVert(int vertex) {
        int i2 = this.vertexData.length / 4;
        int j2 = i2 * vertex;
        float f2 = Float.intBitsToFloat(this.vertexData[j2]);
        float f1 = Float.intBitsToFloat(this.vertexData[j2 + 1]);
        float f22 = Float.intBitsToFloat(this.vertexData[j2 + 2]);
        float f3 = 0.0f;
        float f4 = 0.0f;
        switch (this.face) {
            case DOWN: {
                f3 = f2 * 16.0f;
                f4 = (1.0f - f22) * 16.0f;
                break;
            }
            case UP: {
                f3 = f2 * 16.0f;
                f4 = f22 * 16.0f;
                break;
            }
            case NORTH: {
                f3 = (1.0f - f2) * 16.0f;
                f4 = (1.0f - f1) * 16.0f;
                break;
            }
            case SOUTH: {
                f3 = f2 * 16.0f;
                f4 = (1.0f - f1) * 16.0f;
                break;
            }
            case WEST: {
                f3 = f22 * 16.0f;
                f4 = (1.0f - f1) * 16.0f;
                break;
            }
            case EAST: {
                f3 = (1.0f - f22) * 16.0f;
                f4 = (1.0f - f1) * 16.0f;
            }
        }
        this.vertexData[j2 + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU(f3));
        this.vertexData[j2 + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(f4));
    }
}

