/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.optifine.shaders.ICustomTexture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture
implements ICustomTexture {
    private int texID = GL11.glGenTextures();
    private int textureUnit = 15;

    public HFNoiseTexture(int width, int height) {
        byte[] abyte = this.genHFNoiseImage(width, height);
        ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
        bytebuffer.put(abyte);
        bytebuffer.flip();
        GlStateManager.bindTexture(this.texID);
        GL11.glTexImage2D(3553, 0, 6407, width, height, 0, 6407, 5121, bytebuffer);
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GlStateManager.bindTexture(0);
    }

    public int getID() {
        return this.texID;
    }

    @Override
    public void deleteTexture() {
        GlStateManager.deleteTexture(this.texID);
        this.texID = 0;
    }

    private int random(int seed) {
        seed ^= seed << 13;
        seed ^= seed >> 17;
        seed ^= seed << 5;
        return seed;
    }

    private byte random(int x2, int y2, int z2) {
        int i2 = (this.random(x2) + this.random(y2 * 19)) * this.random(z2 * 23) - z2;
        return (byte)(this.random(i2) % 128);
    }

    private byte[] genHFNoiseImage(int width, int height) {
        byte[] abyte = new byte[width * height * 3];
        int i2 = 0;
        int j2 = 0;
        while (j2 < height) {
            int k2 = 0;
            while (k2 < width) {
                int l2 = 1;
                while (l2 < 4) {
                    abyte[i2++] = this.random(k2, j2, l2);
                    ++l2;
                }
                ++k2;
            }
            ++j2;
        }
        return abyte;
    }

    @Override
    public int getTextureId() {
        return this.texID;
    }

    @Override
    public int getTextureUnit() {
        return this.textureUnit;
    }

    @Override
    public int getTarget() {
        return 3553;
    }
}

