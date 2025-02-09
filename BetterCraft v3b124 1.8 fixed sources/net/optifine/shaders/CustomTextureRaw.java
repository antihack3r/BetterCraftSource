/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.nio.ByteBuffer;
import net.optifine.shaders.ICustomTexture;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CustomTextureRaw
implements ICustomTexture {
    private TextureType type;
    private int textureUnit;
    private int textureId;

    public CustomTextureRaw(TextureType type, InternalFormat internalFormat, int width, int height, int depth, PixelFormat pixelFormat, PixelType pixelType, ByteBuffer data, int textureUnit, boolean blur, boolean clamp) {
        this.type = type;
        this.textureUnit = textureUnit;
        this.textureId = GL11.glGenTextures();
        GL11.glBindTexture(this.getTarget(), this.textureId);
        int i2 = clamp ? 33071 : 10497;
        int j2 = blur ? 9729 : 9728;
        switch (type) {
            case TEXTURE_1D: {
                GL11.glTexImage1D(3552, 0, internalFormat.getId(), width, 0, pixelFormat.getId(), pixelType.getId(), data);
                GL11.glTexParameteri(3552, 10242, i2);
                GL11.glTexParameteri(3552, 10240, j2);
                GL11.glTexParameteri(3552, 10241, j2);
                break;
            }
            case TEXTURE_2D: {
                GL11.glTexImage2D(3553, 0, internalFormat.getId(), width, height, 0, pixelFormat.getId(), pixelType.getId(), data);
                GL11.glTexParameteri(3553, 10242, i2);
                GL11.glTexParameteri(3553, 10243, i2);
                GL11.glTexParameteri(3553, 10240, j2);
                GL11.glTexParameteri(3553, 10241, j2);
                break;
            }
            case TEXTURE_3D: {
                GL12.glTexImage3D(32879, 0, internalFormat.getId(), width, height, depth, 0, pixelFormat.getId(), pixelType.getId(), data);
                GL11.glTexParameteri(32879, 10242, i2);
                GL11.glTexParameteri(32879, 10243, i2);
                GL11.glTexParameteri(32879, 32882, i2);
                GL11.glTexParameteri(32879, 10240, j2);
                GL11.glTexParameteri(32879, 10241, j2);
                break;
            }
            case TEXTURE_RECTANGLE: {
                GL11.glTexImage2D(34037, 0, internalFormat.getId(), width, height, 0, pixelFormat.getId(), pixelType.getId(), data);
                GL11.glTexParameteri(34037, 10242, i2);
                GL11.glTexParameteri(34037, 10243, i2);
                GL11.glTexParameteri(34037, 10240, j2);
                GL11.glTexParameteri(34037, 10241, j2);
            }
        }
        GL11.glBindTexture(this.getTarget(), 0);
    }

    @Override
    public int getTarget() {
        return this.type.getId();
    }

    @Override
    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureUnit() {
        return this.textureUnit;
    }

    @Override
    public void deleteTexture() {
        if (this.textureId > 0) {
            GL11.glDeleteTextures(this.textureId);
            this.textureId = 0;
        }
    }
}

