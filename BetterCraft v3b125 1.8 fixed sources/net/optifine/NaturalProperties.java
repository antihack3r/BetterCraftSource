/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class NaturalProperties {
    public int rotation = 1;
    public boolean flip = false;
    private Map[] quadMaps = new Map[8];

    public NaturalProperties(String type) {
        if (type.equals("4")) {
            this.rotation = 4;
        } else if (type.equals("2")) {
            this.rotation = 2;
        } else if (type.equals("F")) {
            this.flip = true;
        } else if (type.equals("4F")) {
            this.rotation = 4;
            this.flip = true;
        } else if (type.equals("2F")) {
            this.rotation = 2;
            this.flip = true;
        } else {
            Config.warn("NaturalTextures: Unknown type: " + type);
        }
    }

    public boolean isValid() {
        return this.rotation != 2 && this.rotation != 4 ? this.flip : true;
    }

    public synchronized BakedQuad getQuad(BakedQuad quadIn, int rotate, boolean flipU) {
        int i2 = rotate;
        if (flipU) {
            i2 = rotate | 4;
        }
        if (i2 > 0 && i2 < this.quadMaps.length) {
            BakedQuad bakedquad;
            IdentityHashMap<BakedQuad, BakedQuad> map = this.quadMaps[i2];
            if (map == null) {
                this.quadMaps[i2] = map = new IdentityHashMap<BakedQuad, BakedQuad>(1);
            }
            if ((bakedquad = (BakedQuad)map.get(quadIn)) == null) {
                bakedquad = this.makeQuad(quadIn, rotate, flipU);
                map.put(quadIn, bakedquad);
            }
            return bakedquad;
        }
        return quadIn;
    }

    private BakedQuad makeQuad(BakedQuad quad, int rotate, boolean flipU) {
        int[] aint = quad.getVertexData();
        int i2 = quad.getTintIndex();
        EnumFacing enumfacing = quad.getFace();
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        if (!this.isFullSprite(quad)) {
            rotate = 0;
        }
        aint = this.transformVertexData(aint, rotate, flipU);
        BakedQuad bakedquad = new BakedQuad(aint, i2, enumfacing, textureatlassprite);
        return bakedquad;
    }

    private int[] transformVertexData(int[] vertexData, int rotate, boolean flipU) {
        int[] aint = (int[])vertexData.clone();
        int i2 = 4 - rotate;
        if (flipU) {
            i2 += 3;
        }
        i2 %= 4;
        int j2 = aint.length / 4;
        int k2 = 0;
        while (k2 < 4) {
            int l2 = k2 * j2;
            int i1 = i2 * j2;
            aint[i1 + 4] = vertexData[l2 + 4];
            aint[i1 + 4 + 1] = vertexData[l2 + 4 + 1];
            if (flipU) {
                if (--i2 < 0) {
                    i2 = 3;
                }
            } else if (++i2 > 3) {
                i2 = 0;
            }
            ++k2;
        }
        return aint;
    }

    private boolean isFullSprite(BakedQuad quad) {
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        float f2 = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMaxU();
        float f22 = f1 - f2;
        float f3 = f22 / 256.0f;
        float f4 = textureatlassprite.getMinV();
        float f5 = textureatlassprite.getMaxV();
        float f6 = f5 - f4;
        float f7 = f6 / 256.0f;
        int[] aint = quad.getVertexData();
        int i2 = aint.length / 4;
        int j2 = 0;
        while (j2 < 4) {
            int k2 = j2 * i2;
            float f8 = Float.intBitsToFloat(aint[k2 + 4]);
            float f9 = Float.intBitsToFloat(aint[k2 + 4 + 1]);
            if (!this.equalsDelta(f8, f2, f3) && !this.equalsDelta(f8, f1, f3)) {
                return false;
            }
            if (!this.equalsDelta(f9, f4, f7) && !this.equalsDelta(f9, f5, f7)) {
                return false;
            }
            ++j2;
        }
        return true;
    }

    private boolean equalsDelta(float x1, float x2, float deltaMax) {
        float f2 = MathHelper.abs(x1 - x2);
        return f2 < deltaMax;
    }
}

