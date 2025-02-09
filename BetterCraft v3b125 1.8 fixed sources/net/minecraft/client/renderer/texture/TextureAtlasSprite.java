/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.renderer.texture.TextureClock;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.SmartAnimations;
import net.optifine.shaders.Shaders;
import net.optifine.util.CounterInt;
import net.optifine.util.TextureUtils;

public class TextureAtlasSprite {
    private final String iconName;
    protected List<int[][]> framesTextureData = Lists.newArrayList();
    protected int[][] interpolatedFrameData;
    private AnimationMetadataSection animationMetadata;
    protected boolean rotated;
    protected int originX;
    protected int originY;
    protected int width;
    protected int height;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected int frameCounter;
    protected int tickCounter;
    private static String locationNameClock = "builtin/clock";
    private static String locationNameCompass = "builtin/compass";
    private int indexInMap = -1;
    public float baseU;
    public float baseV;
    public int sheetWidth;
    public int sheetHeight;
    public int glSpriteTextureId = -1;
    public TextureAtlasSprite spriteSingle = null;
    public boolean isSpriteSingle = false;
    public int mipmapLevels = 0;
    public TextureAtlasSprite spriteNormal = null;
    public TextureAtlasSprite spriteSpecular = null;
    public boolean isShadersSprite = false;
    public boolean isEmissive = false;
    public TextureAtlasSprite spriteEmissive = null;
    private int animationIndex = -1;
    private boolean animationActive = false;

    private TextureAtlasSprite(String p_i7_1_, boolean p_i7_2_) {
        this.iconName = p_i7_1_;
        this.isSpriteSingle = p_i7_2_;
    }

    public TextureAtlasSprite(String spriteName) {
        this.iconName = spriteName;
        if (Config.isMultiTexture()) {
            this.spriteSingle = new TextureAtlasSprite(String.valueOf(this.getIconName()) + ".spriteSingle", true);
        }
    }

    protected static TextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation) {
        String s2 = spriteResourceLocation.toString();
        return locationNameClock.equals(s2) ? new TextureClock(s2) : (locationNameCompass.equals(s2) ? new TextureCompass(s2) : new TextureAtlasSprite(s2));
    }

    public static void setLocationNameClock(String clockName) {
        locationNameClock = clockName;
    }

    public static void setLocationNameCompass(String compassName) {
        locationNameCompass = compassName;
    }

    public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn) {
        this.originX = originInX;
        this.originY = originInY;
        this.rotated = rotatedIn;
        float f2 = (float)((double)0.01f / (double)inX);
        float f1 = (float)((double)0.01f / (double)inY);
        this.minU = (float)originInX / (float)((double)inX) + f2;
        this.maxU = (float)(originInX + this.width) / (float)((double)inX) - f2;
        this.minV = (float)originInY / (float)inY + f1;
        this.maxV = (float)(originInY + this.height) / (float)inY - f1;
        this.baseU = Math.min(this.minU, this.maxU);
        this.baseV = Math.min(this.minV, this.maxV);
        if (this.spriteSingle != null) {
            this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
        }
        if (this.spriteNormal != null) {
            this.spriteNormal.copyFrom(this);
        }
        if (this.spriteSpecular != null) {
            this.spriteSpecular.copyFrom(this);
        }
    }

    public void copyFrom(TextureAtlasSprite atlasSpirit) {
        this.originX = atlasSpirit.originX;
        this.originY = atlasSpirit.originY;
        this.width = atlasSpirit.width;
        this.height = atlasSpirit.height;
        this.rotated = atlasSpirit.rotated;
        this.minU = atlasSpirit.minU;
        this.maxU = atlasSpirit.maxU;
        this.minV = atlasSpirit.minV;
        this.maxV = atlasSpirit.maxV;
        if (atlasSpirit != Config.getTextureMap().getMissingSprite()) {
            this.indexInMap = atlasSpirit.indexInMap;
        }
        this.baseU = atlasSpirit.baseU;
        this.baseV = atlasSpirit.baseV;
        this.sheetWidth = atlasSpirit.sheetWidth;
        this.sheetHeight = atlasSpirit.sheetHeight;
        this.glSpriteTextureId = atlasSpirit.glSpriteTextureId;
        this.mipmapLevels = atlasSpirit.mipmapLevels;
        if (this.spriteSingle != null) {
            this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
        }
        this.animationIndex = atlasSpirit.animationIndex;
    }

    public int getOriginX() {
        return this.originX;
    }

    public int getOriginY() {
        return this.originY;
    }

    public int getIconWidth() {
        return this.width;
    }

    public int getIconHeight() {
        return this.height;
    }

    public float getMinU() {
        return this.minU;
    }

    public float getMaxU() {
        return this.maxU;
    }

    public float getInterpolatedU(double u2) {
        float f2 = this.maxU - this.minU;
        return this.minU + f2 * (float)u2 / 16.0f;
    }

    public float getMinV() {
        return this.minV;
    }

    public float getMaxV() {
        return this.maxV;
    }

    public float getInterpolatedV(double v2) {
        float f2 = this.maxV - this.minV;
        return this.minV + f2 * ((float)v2 / 16.0f);
    }

    public String getIconName() {
        return this.iconName;
    }

    public void updateAnimation() {
        if (this.animationMetadata != null) {
            this.animationActive = SmartAnimations.isActive() ? SmartAnimations.isSpriteRendered(this.animationIndex) : true;
            ++this.tickCounter;
            if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
                int i2 = this.animationMetadata.getFrameIndex(this.frameCounter);
                int j2 = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
                this.frameCounter = (this.frameCounter + 1) % j2;
                this.tickCounter = 0;
                int k2 = this.animationMetadata.getFrameIndex(this.frameCounter);
                boolean flag = false;
                boolean flag1 = this.isSpriteSingle;
                if (!this.animationActive) {
                    return;
                }
                if (i2 != k2 && k2 >= 0 && k2 < this.framesTextureData.size()) {
                    TextureUtil.uploadTextureMipmap(this.framesTextureData.get(k2), this.width, this.height, this.originX, this.originY, flag, flag1);
                }
            } else if (this.animationMetadata.isInterpolate()) {
                if (!this.animationActive) {
                    return;
                }
                this.updateAnimationInterpolated();
            }
        }
    }

    private void updateAnimationInterpolated() {
        int j2;
        int k2;
        double d0 = 1.0 - (double)this.tickCounter / (double)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
        int i2 = this.animationMetadata.getFrameIndex(this.frameCounter);
        if (i2 != (k2 = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % (j2 = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount()))) && k2 >= 0 && k2 < this.framesTextureData.size()) {
            int[][] aint = this.framesTextureData.get(i2);
            int[][] aint1 = this.framesTextureData.get(k2);
            if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != aint.length) {
                this.interpolatedFrameData = new int[aint.length][];
            }
            int l2 = 0;
            while (l2 < aint.length) {
                if (this.interpolatedFrameData[l2] == null) {
                    this.interpolatedFrameData[l2] = new int[aint[l2].length];
                }
                if (l2 < aint1.length && aint1[l2].length == aint[l2].length) {
                    int i1 = 0;
                    while (i1 < aint[l2].length) {
                        int j1 = aint[l2][i1];
                        int k1 = aint1[l2][i1];
                        int l1 = (int)((double)((j1 & 0xFF0000) >> 16) * d0 + (double)((k1 & 0xFF0000) >> 16) * (1.0 - d0));
                        int i22 = (int)((double)((j1 & 0xFF00) >> 8) * d0 + (double)((k1 & 0xFF00) >> 8) * (1.0 - d0));
                        int j22 = (int)((double)(j1 & 0xFF) * d0 + (double)(k1 & 0xFF) * (1.0 - d0));
                        this.interpolatedFrameData[l2][i1] = j1 & 0xFF000000 | l1 << 16 | i22 << 8 | j22;
                        ++i1;
                    }
                }
                ++l2;
            }
            TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
        }
    }

    public int[][] getFrameTextureData(int index) {
        return this.framesTextureData.get(index);
    }

    public int getFrameCount() {
        return this.framesTextureData.size();
    }

    public void setIconWidth(int newWidth) {
        this.width = newWidth;
        if (this.spriteSingle != null) {
            this.spriteSingle.setIconWidth(this.width);
        }
    }

    public void setIconHeight(int newHeight) {
        this.height = newHeight;
        if (this.spriteSingle != null) {
            this.spriteSingle.setIconHeight(this.height);
        }
    }

    public void loadSprite(BufferedImage[] images, AnimationMetadataSection meta) throws IOException {
        this.resetSprite();
        int i2 = images[0].getWidth();
        int j2 = images[0].getHeight();
        this.width = i2;
        this.height = j2;
        if (this.spriteSingle != null) {
            this.spriteSingle.width = this.width;
            this.spriteSingle.height = this.height;
        }
        int[][] aint = new int[images.length][];
        int k2 = 0;
        while (k2 < images.length) {
            BufferedImage bufferedimage = images[k2];
            if (bufferedimage != null) {
                if (this.width >> k2 != bufferedimage.getWidth()) {
                    bufferedimage = TextureUtils.scaleImage(bufferedimage, this.width >> k2);
                }
                if (k2 > 0 && (bufferedimage.getWidth() != i2 >> k2 || bufferedimage.getHeight() != j2 >> k2)) {
                    throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", k2, bufferedimage.getWidth(), bufferedimage.getHeight(), i2 >> k2, j2 >> k2));
                }
                aint[k2] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
                bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k2], 0, bufferedimage.getWidth());
            }
            ++k2;
        }
        if (meta == null) {
            if (j2 != i2) {
                throw new RuntimeException("broken aspect ratio and not an animation");
            }
            this.framesTextureData.add(aint);
        } else {
            int j1 = j2 / i2;
            int l1 = i2;
            int l2 = i2;
            this.height = this.width;
            if (meta.getFrameCount() > 0) {
                for (int i1 : meta.getFrameIndexSet()) {
                    if (i1 >= j1) {
                        throw new RuntimeException("invalid frameindex " + i1);
                    }
                    this.allocateFrameTextureData(i1);
                    this.framesTextureData.set(i1, TextureAtlasSprite.getFrameTextureData(aint, l1, l2, i1));
                }
                this.animationMetadata = meta;
            } else {
                ArrayList<AnimationFrame> list = Lists.newArrayList();
                int j22 = 0;
                while (j22 < j1) {
                    this.framesTextureData.add(TextureAtlasSprite.getFrameTextureData(aint, l1, l2, j22));
                    list.add(new AnimationFrame(j22, -1));
                    ++j22;
                }
                this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
            }
        }
        if (!this.isShadersSprite) {
            if (Config.isShaders()) {
                this.loadShadersSprites();
            }
            int k1 = 0;
            while (k1 < this.framesTextureData.size()) {
                int[][] aint1 = this.framesTextureData.get(k1);
                if (aint1 != null && !this.iconName.startsWith("minecraft:blocks/leaves_")) {
                    int i22 = 0;
                    while (i22 < aint1.length) {
                        int[] aint2 = aint1[i22];
                        this.fixTransparentColor(aint2);
                        ++i22;
                    }
                }
                ++k1;
            }
            if (this.spriteSingle != null) {
                this.spriteSingle.loadSprite(images, meta);
            }
        }
    }

    public void generateMipmaps(int level) {
        ArrayList<int[][]> list = Lists.newArrayList();
        int i2 = 0;
        while (i2 < this.framesTextureData.size()) {
            final int[][] aint = this.framesTextureData.get(i2);
            if (aint != null) {
                try {
                    list.add(TextureUtil.generateMipmapData(level, this.width, aint));
                }
                catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
                    crashreportcategory.addCrashSection("Frame index", i2);
                    crashreportcategory.addCrashSectionCallable("Frame sizes", new Callable<String>(){

                        @Override
                        public String call() throws Exception {
                            StringBuilder stringbuilder = new StringBuilder();
                            int[][] nArray = aint;
                            int n2 = aint.length;
                            int n3 = 0;
                            while (n3 < n2) {
                                int[] aint1 = nArray[n3];
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append(", ");
                                }
                                stringbuilder.append(aint1 == null ? "null" : Integer.valueOf(aint1.length));
                                ++n3;
                            }
                            return stringbuilder.toString();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
            ++i2;
        }
        this.setFramesTextureData(list);
        if (this.spriteSingle != null) {
            this.spriteSingle.generateMipmaps(level);
        }
    }

    private void allocateFrameTextureData(int index) {
        if (this.framesTextureData.size() <= index) {
            int i2 = this.framesTextureData.size();
            while (i2 <= index) {
                this.framesTextureData.add(null);
                ++i2;
            }
        }
        if (this.spriteSingle != null) {
            this.spriteSingle.allocateFrameTextureData(index);
        }
    }

    private static int[][] getFrameTextureData(int[][] data, int rows, int columns, int p_147962_3_) {
        int[][] aint = new int[data.length][];
        int i2 = 0;
        while (i2 < data.length) {
            int[] aint1 = data[i2];
            if (aint1 != null) {
                aint[i2] = new int[(rows >> i2) * (columns >> i2)];
                System.arraycopy(aint1, p_147962_3_ * aint[i2].length, aint[i2], 0, aint[i2].length);
            }
            ++i2;
        }
        return aint;
    }

    public void clearFramesTextureData() {
        this.framesTextureData.clear();
        if (this.spriteSingle != null) {
            this.spriteSingle.clearFramesTextureData();
        }
    }

    public boolean hasAnimationMetadata() {
        return this.animationMetadata != null;
    }

    public void setFramesTextureData(List<int[][]> newFramesTextureData) {
        this.framesTextureData = newFramesTextureData;
        if (this.spriteSingle != null) {
            this.spriteSingle.setFramesTextureData(newFramesTextureData);
        }
    }

    private void resetSprite() {
        this.animationMetadata = null;
        this.setFramesTextureData(Lists.newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
        if (this.spriteSingle != null) {
            this.spriteSingle.resetSprite();
        }
    }

    public String toString() {
        return "TextureAtlasSprite{name='" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size() + ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
    }

    public boolean hasCustomLoader(IResourceManager p_hasCustomLoader_1_, ResourceLocation p_hasCustomLoader_2_) {
        return false;
    }

    public boolean load(IResourceManager p_load_1_, ResourceLocation p_load_2_) {
        return true;
    }

    public int getIndexInMap() {
        return this.indexInMap;
    }

    public void setIndexInMap(int p_setIndexInMap_1_) {
        this.indexInMap = p_setIndexInMap_1_;
    }

    public void updateIndexInMap(CounterInt p_updateIndexInMap_1_) {
        if (this.indexInMap < 0) {
            this.indexInMap = p_updateIndexInMap_1_.nextValue();
        }
    }

    public int getAnimationIndex() {
        return this.animationIndex;
    }

    public void setAnimationIndex(int p_setAnimationIndex_1_) {
        this.animationIndex = p_setAnimationIndex_1_;
        if (this.spriteNormal != null) {
            this.spriteNormal.setAnimationIndex(p_setAnimationIndex_1_);
        }
        if (this.spriteSpecular != null) {
            this.spriteSpecular.setAnimationIndex(p_setAnimationIndex_1_);
        }
    }

    public boolean isAnimationActive() {
        return this.animationActive;
    }

    private void fixTransparentColor(int[] p_fixTransparentColor_1_) {
        if (p_fixTransparentColor_1_ != null) {
            long i2 = 0L;
            long j2 = 0L;
            long k2 = 0L;
            long l2 = 0L;
            int i1 = 0;
            while (i1 < p_fixTransparentColor_1_.length) {
                int j1 = p_fixTransparentColor_1_[i1];
                int k1 = j1 >> 24 & 0xFF;
                if (k1 >= 16) {
                    int l1 = j1 >> 16 & 0xFF;
                    int i22 = j1 >> 8 & 0xFF;
                    int j22 = j1 & 0xFF;
                    i2 += (long)l1;
                    j2 += (long)i22;
                    k2 += (long)j22;
                    ++l2;
                }
                ++i1;
            }
            if (l2 > 0L) {
                int l22 = (int)(i2 / l2);
                int i3 = (int)(j2 / l2);
                int j3 = (int)(k2 / l2);
                int k3 = l22 << 16 | i3 << 8 | j3;
                int l3 = 0;
                while (l3 < p_fixTransparentColor_1_.length) {
                    int i4 = p_fixTransparentColor_1_[l3];
                    int k22 = i4 >> 24 & 0xFF;
                    if (k22 <= 16) {
                        p_fixTransparentColor_1_[l3] = k3;
                    }
                    ++l3;
                }
            }
        }
    }

    public double getSpriteU16(float p_getSpriteU16_1_) {
        float f2 = this.maxU - this.minU;
        return (p_getSpriteU16_1_ - this.minU) / f2 * 16.0f;
    }

    public double getSpriteV16(float p_getSpriteV16_1_) {
        float f2 = this.maxV - this.minV;
        return (p_getSpriteV16_1_ - this.minV) / f2 * 16.0f;
    }

    public void bindSpriteTexture() {
        if (this.glSpriteTextureId < 0) {
            this.glSpriteTextureId = TextureUtil.glGenTextures();
            TextureUtil.allocateTextureImpl(this.glSpriteTextureId, this.mipmapLevels, this.width, this.height);
            TextureUtils.applyAnisotropicLevel();
        }
        TextureUtils.bindTexture(this.glSpriteTextureId);
    }

    public void deleteSpriteTexture() {
        if (this.glSpriteTextureId >= 0) {
            TextureUtil.deleteTexture(this.glSpriteTextureId);
            this.glSpriteTextureId = -1;
        }
    }

    public float toSingleU(float p_toSingleU_1_) {
        p_toSingleU_1_ -= this.baseU;
        float f2 = (float)this.sheetWidth / (float)this.width;
        return p_toSingleU_1_ *= f2;
    }

    public float toSingleV(float p_toSingleV_1_) {
        p_toSingleV_1_ -= this.baseV;
        float f2 = (float)this.sheetHeight / (float)this.height;
        return p_toSingleV_1_ *= f2;
    }

    public List<int[][]> getFramesTextureData() {
        ArrayList<int[][]> list = new ArrayList<int[][]>();
        list.addAll(this.framesTextureData);
        return list;
    }

    public AnimationMetadataSection getAnimationMetadata() {
        return this.animationMetadata;
    }

    public void setAnimationMetadata(AnimationMetadataSection p_setAnimationMetadata_1_) {
        this.animationMetadata = p_setAnimationMetadata_1_;
    }

    private void loadShadersSprites() {
        if (Shaders.configNormalMap) {
            String s2 = String.valueOf(this.iconName) + "_n";
            ResourceLocation resourcelocation = new ResourceLocation(s2);
            resourcelocation = Config.getTextureMap().completeResourceLocation(resourcelocation);
            if (Config.hasResource(resourcelocation)) {
                this.spriteNormal = new TextureAtlasSprite(s2);
                this.spriteNormal.isShadersSprite = true;
                this.spriteNormal.copyFrom(this);
                this.spriteNormal.generateMipmaps(this.mipmapLevels);
            }
        }
        if (Shaders.configSpecularMap) {
            String s1 = String.valueOf(this.iconName) + "_s";
            ResourceLocation resourcelocation1 = new ResourceLocation(s1);
            resourcelocation1 = Config.getTextureMap().completeResourceLocation(resourcelocation1);
            if (Config.hasResource(resourcelocation1)) {
                this.spriteSpecular = new TextureAtlasSprite(s1);
                this.spriteSpecular.isShadersSprite = true;
                this.spriteSpecular.copyFrom(this);
                this.spriteSpecular.generateMipmaps(this.mipmapLevels);
            }
        }
    }
}

