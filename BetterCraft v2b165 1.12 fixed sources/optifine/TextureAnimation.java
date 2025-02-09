// 
// Decompiled by Procyon v0.6.0
// 

package optifine;

import java.util.Hashtable;
import net.minecraft.client.renderer.texture.ITextureObject;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GLAllocation;
import java.util.Properties;
import java.nio.ByteBuffer;
import net.minecraft.util.ResourceLocation;

public class TextureAnimation
{
    private String srcTex;
    private String dstTex;
    ResourceLocation dstTexLoc;
    private int dstTextId;
    private int dstX;
    private int dstY;
    private int frameWidth;
    private int frameHeight;
    private TextureAnimationFrame[] frames;
    private int activeFrame;
    byte[] srcData;
    private ByteBuffer imageData;
    
    public TextureAnimation(final String p_i97_1_, final byte[] p_i97_2_, final String p_i97_3_, final ResourceLocation p_i97_4_, final int p_i97_5_, final int p_i97_6_, final int p_i97_7_, final int p_i97_8_, final Properties p_i97_9_, final int p_i97_10_) {
        this.srcTex = null;
        this.dstTex = null;
        this.dstTexLoc = null;
        this.dstTextId = -1;
        this.dstX = 0;
        this.dstY = 0;
        this.frameWidth = 0;
        this.frameHeight = 0;
        this.frames = null;
        this.activeFrame = 0;
        this.srcData = null;
        this.imageData = null;
        this.srcTex = p_i97_1_;
        this.dstTex = p_i97_3_;
        this.dstTexLoc = p_i97_4_;
        this.dstX = p_i97_5_;
        this.dstY = p_i97_6_;
        this.frameWidth = p_i97_7_;
        this.frameHeight = p_i97_8_;
        final int i = p_i97_7_ * p_i97_8_ * 4;
        if (p_i97_2_.length % i != 0) {
            Config.warn("Invalid animated texture length: " + p_i97_2_.length + ", frameWidth: " + p_i97_7_ + ", frameHeight: " + p_i97_8_);
        }
        this.srcData = p_i97_2_;
        int j = p_i97_2_.length / i;
        if (p_i97_9_.get("tile.0") != null) {
            for (int k = 0; p_i97_9_.get("tile." + k) != null; ++k) {
                j = k + 1;
            }
        }
        final String s2 = ((Hashtable<K, String>)p_i97_9_).get("duration");
        final int l = Config.parseInt(s2, p_i97_10_);
        this.frames = new TextureAnimationFrame[j];
        for (int i2 = 0; i2 < this.frames.length; ++i2) {
            final String s3 = ((Hashtable<K, String>)p_i97_9_).get("tile." + i2);
            final int j2 = Config.parseInt(s3, i2);
            final String s4 = ((Hashtable<K, String>)p_i97_9_).get("duration." + i2);
            final int k2 = Config.parseInt(s4, l);
            final TextureAnimationFrame textureanimationframe = new TextureAnimationFrame(j2, k2);
            this.frames[i2] = textureanimationframe;
        }
    }
    
    public boolean nextFrame() {
        if (this.frames.length <= 0) {
            return false;
        }
        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }
        final TextureAnimationFrame textureAnimationFrame;
        final TextureAnimationFrame textureanimationframe = textureAnimationFrame = this.frames[this.activeFrame];
        ++textureAnimationFrame.counter;
        if (textureanimationframe.counter < textureanimationframe.duration) {
            return false;
        }
        textureanimationframe.counter = 0;
        ++this.activeFrame;
        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }
        return true;
    }
    
    public int getActiveFrameIndex() {
        if (this.frames.length <= 0) {
            return 0;
        }
        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }
        final TextureAnimationFrame textureanimationframe = this.frames[this.activeFrame];
        return textureanimationframe.index;
    }
    
    public int getFrameCount() {
        return this.frames.length;
    }
    
    public boolean updateTexture() {
        if (this.dstTextId < 0) {
            final ITextureObject itextureobject = TextureUtils.getTexture(this.dstTexLoc);
            if (itextureobject == null) {
                return false;
            }
            this.dstTextId = itextureobject.getGlTextureId();
        }
        if (this.imageData == null) {
            (this.imageData = GLAllocation.createDirectByteBuffer(this.srcData.length)).put(this.srcData);
            this.srcData = null;
        }
        if (!this.nextFrame()) {
            return false;
        }
        final int k = this.frameWidth * this.frameHeight * 4;
        final int i = this.getActiveFrameIndex();
        final int j = k * i;
        if (j + k > this.imageData.capacity()) {
            return false;
        }
        this.imageData.position(j);
        GlStateManager.bindTexture(this.dstTextId);
        GL11.glTexSubImage2D(3553, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, 6408, 5121, this.imageData);
        return true;
    }
    
    public String getSrcTex() {
        return this.srcTex;
    }
    
    public String getDstTex() {
        return this.dstTex;
    }
    
    public ResourceLocation getDstTexLoc() {
        return this.dstTexLoc;
    }
}
