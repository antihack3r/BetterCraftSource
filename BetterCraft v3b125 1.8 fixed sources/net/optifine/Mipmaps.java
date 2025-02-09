/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.awt.Dimension;
import java.nio.IntBuffer;
import java.util.ArrayList;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.src.Config;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class Mipmaps {
    private final String iconName;
    private final int width;
    private final int height;
    private final int[] data;
    private final boolean direct;
    private int[][] mipmapDatas;
    private IntBuffer[] mipmapBuffers;
    private Dimension[] mipmapDimensions;

    public Mipmaps(String iconName, int width, int height, int[] data, boolean direct) {
        this.iconName = iconName;
        this.width = width;
        this.height = height;
        this.data = data;
        this.direct = direct;
        this.mipmapDimensions = Mipmaps.makeMipmapDimensions(width, height, iconName);
        this.mipmapDatas = Mipmaps.generateMipMapData(data, width, height, this.mipmapDimensions);
        if (direct) {
            this.mipmapBuffers = Mipmaps.makeMipmapBuffers(this.mipmapDimensions, this.mipmapDatas);
        }
    }

    public static Dimension[] makeMipmapDimensions(int width, int height, String iconName) {
        int i2 = TextureUtils.ceilPowerOfTwo(width);
        int j2 = TextureUtils.ceilPowerOfTwo(height);
        if (i2 == width && j2 == height) {
            ArrayList<Dimension> list = new ArrayList<Dimension>();
            int k2 = i2;
            int l2 = j2;
            while (true) {
                if ((k2 /= 2) <= 0 && (l2 /= 2) <= 0) {
                    Dimension[] adimension = list.toArray(new Dimension[list.size()]);
                    return adimension;
                }
                if (k2 <= 0) {
                    k2 = 1;
                }
                if (l2 <= 0) {
                    l2 = 1;
                }
                int i1 = k2 * l2 * 4;
                Dimension dimension = new Dimension(k2, l2);
                list.add(dimension);
            }
        }
        Config.warn("Mipmaps not possible (power of 2 dimensions needed), texture: " + iconName + ", dim: " + width + "x" + height);
        return new Dimension[0];
    }

    public static int[][] generateMipMapData(int[] data, int width, int height, Dimension[] mipmapDimensions) {
        int[] aint = data;
        int i2 = width;
        boolean flag = true;
        int[][] aint1 = new int[mipmapDimensions.length][];
        int j2 = 0;
        while (j2 < mipmapDimensions.length) {
            Dimension dimension = mipmapDimensions[j2];
            int k2 = dimension.width;
            int l2 = dimension.height;
            int[] aint2 = new int[k2 * l2];
            aint1[j2] = aint2;
            int i1 = j2 + 1;
            if (flag) {
                int j1 = 0;
                while (j1 < k2) {
                    int k1 = 0;
                    while (k1 < l2) {
                        int l22;
                        int l1 = aint[j1 * 2 + 0 + (k1 * 2 + 0) * i2];
                        int i22 = aint[j1 * 2 + 1 + (k1 * 2 + 0) * i2];
                        int j22 = aint[j1 * 2 + 1 + (k1 * 2 + 1) * i2];
                        int k22 = aint[j1 * 2 + 0 + (k1 * 2 + 1) * i2];
                        aint2[j1 + k1 * k2] = l22 = Mipmaps.alphaBlend(l1, i22, j22, k22);
                        ++k1;
                    }
                    ++j1;
                }
            }
            aint = aint2;
            i2 = k2;
            if (k2 <= 1 || l2 <= 1) {
                flag = false;
            }
            ++j2;
        }
        return aint1;
    }

    public static int alphaBlend(int c1, int c2, int c3, int c4) {
        int i2 = Mipmaps.alphaBlend(c1, c2);
        int j2 = Mipmaps.alphaBlend(c3, c4);
        int k2 = Mipmaps.alphaBlend(i2, j2);
        return k2;
    }

    private static int alphaBlend(int c1, int c2) {
        int i2 = (c1 & 0xFF000000) >> 24 & 0xFF;
        int j2 = (c2 & 0xFF000000) >> 24 & 0xFF;
        int k2 = (i2 + j2) / 2;
        if (i2 == 0 && j2 == 0) {
            i2 = 1;
            j2 = 1;
        } else {
            if (i2 == 0) {
                c1 = c2;
                k2 /= 2;
            }
            if (j2 == 0) {
                c2 = c1;
                k2 /= 2;
            }
        }
        int l2 = (c1 >> 16 & 0xFF) * i2;
        int i1 = (c1 >> 8 & 0xFF) * i2;
        int j1 = (c1 & 0xFF) * i2;
        int k1 = (c2 >> 16 & 0xFF) * j2;
        int l1 = (c2 >> 8 & 0xFF) * j2;
        int i22 = (c2 & 0xFF) * j2;
        int j22 = (l2 + k1) / (i2 + j2);
        int k22 = (i1 + l1) / (i2 + j2);
        int l22 = (j1 + i22) / (i2 + j2);
        return k2 << 24 | j22 << 16 | k22 << 8 | l22;
    }

    private int averageColor(int i2, int j2) {
        int k2 = (i2 & 0xFF000000) >> 24 & 0xFF;
        int p2 = (j2 & 0xFF000000) >> 24 & 0xFF;
        return (k2 + j2 >> 1 << 24) + ((k2 & 0xFEFEFE) + (p2 & 0xFEFEFE) >> 1);
    }

    public static IntBuffer[] makeMipmapBuffers(Dimension[] mipmapDimensions, int[][] mipmapDatas) {
        if (mipmapDimensions == null) {
            return null;
        }
        IntBuffer[] aintbuffer = new IntBuffer[mipmapDimensions.length];
        int i2 = 0;
        while (i2 < mipmapDimensions.length) {
            Dimension dimension = mipmapDimensions[i2];
            int j2 = dimension.width * dimension.height;
            IntBuffer intbuffer = GLAllocation.createDirectIntBuffer(j2);
            int[] aint = mipmapDatas[i2];
            intbuffer.clear();
            intbuffer.put(aint);
            intbuffer.clear();
            aintbuffer[i2] = intbuffer;
            ++i2;
        }
        return aintbuffer;
    }

    public static void allocateMipmapTextures(int width, int height, String name) {
        Dimension[] adimension = Mipmaps.makeMipmapDimensions(width, height, name);
        int i2 = 0;
        while (i2 < adimension.length) {
            Dimension dimension = adimension[i2];
            int j2 = dimension.width;
            int k2 = dimension.height;
            int l2 = i2 + 1;
            GL11.glTexImage2D(3553, l2, 6408, j2, k2, 0, 32993, 33639, null);
            ++i2;
        }
    }
}

