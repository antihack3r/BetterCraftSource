/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.MathHelper;

public class Stitcher {
    private final int mipmapLevelStitcher;
    private final Set<Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
    private final List<Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
    private int currentWidth;
    private int currentHeight;
    private final int maxWidth;
    private final int maxHeight;
    private final boolean forcePowerOf2;
    private final int maxTileDimension;

    public Stitcher(int maxTextureWidth, int maxTextureHeight, boolean p_i45095_3_, int p_i45095_4_, int mipmapLevel) {
        this.mipmapLevelStitcher = mipmapLevel;
        this.maxWidth = maxTextureWidth;
        this.maxHeight = maxTextureHeight;
        this.forcePowerOf2 = p_i45095_3_;
        this.maxTileDimension = p_i45095_4_;
    }

    public int getCurrentWidth() {
        return this.currentWidth;
    }

    public int getCurrentHeight() {
        return this.currentHeight;
    }

    public void addSprite(TextureAtlasSprite p_110934_1_) {
        Holder stitcher$holder = new Holder(p_110934_1_, this.mipmapLevelStitcher);
        if (this.maxTileDimension > 0) {
            stitcher$holder.setNewDimension(this.maxTileDimension);
        }
        this.setStitchHolders.add(stitcher$holder);
    }

    public void doStitch() {
        Object[] astitcher$holder = this.setStitchHolders.toArray(new Holder[this.setStitchHolders.size()]);
        Arrays.sort(astitcher$holder);
        Object[] objectArray = astitcher$holder;
        int n2 = astitcher$holder.length;
        int n3 = 0;
        while (n3 < n2) {
            Object stitcher$holder = objectArray[n3];
            if (!this.allocateSlot((Holder)stitcher$holder)) {
                String s2 = String.format("Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?", ((Holder)stitcher$holder).getAtlasSprite().getIconName(), ((Holder)stitcher$holder).getAtlasSprite().getIconWidth(), ((Holder)stitcher$holder).getAtlasSprite().getIconHeight(), this.currentWidth, this.currentHeight, this.maxWidth, this.maxHeight);
                throw new StitcherException((Holder)stitcher$holder, s2);
            }
            ++n3;
        }
        if (this.forcePowerOf2) {
            this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
        }
    }

    public List<TextureAtlasSprite> getStichSlots() {
        ArrayList<Slot> list = Lists.newArrayList();
        for (Slot stitcher$slot : this.stitchSlots) {
            stitcher$slot.getAllStitchSlots(list);
        }
        ArrayList<TextureAtlasSprite> list1 = Lists.newArrayList();
        for (Slot stitcher$slot1 : list) {
            Holder stitcher$holder = stitcher$slot1.getStitchHolder();
            TextureAtlasSprite textureatlassprite = stitcher$holder.getAtlasSprite();
            textureatlassprite.initSprite(this.currentWidth, this.currentHeight, stitcher$slot1.getOriginX(), stitcher$slot1.getOriginY(), stitcher$holder.isRotated());
            list1.add(textureatlassprite);
        }
        return list1;
    }

    private static int getMipmapDimension(int p_147969_0_, int p_147969_1_) {
        return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
    }

    private boolean allocateSlot(Holder p_94310_1_) {
        int i2 = 0;
        while (i2 < this.stitchSlots.size()) {
            if (this.stitchSlots.get(i2).addSlot(p_94310_1_)) {
                return true;
            }
            p_94310_1_.rotate();
            if (this.stitchSlots.get(i2).addSlot(p_94310_1_)) {
                return true;
            }
            p_94310_1_.rotate();
            ++i2;
        }
        return this.expandAndAllocateSlot(p_94310_1_);
    }

    private boolean expandAndAllocateSlot(Holder p_94311_1_) {
        Slot stitcher$slot;
        boolean flag1;
        boolean flag;
        int i2 = Math.min(p_94311_1_.getWidth(), p_94311_1_.getHeight());
        boolean bl2 = flag = this.currentWidth == 0 && this.currentHeight == 0;
        if (this.forcePowerOf2) {
            boolean flag5;
            boolean flag3;
            int j2 = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            int k2 = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
            int l2 = MathHelper.roundUpToPowerOfTwo(this.currentWidth + i2);
            int i1 = MathHelper.roundUpToPowerOfTwo(this.currentHeight + i2);
            boolean flag2 = l2 <= this.maxWidth;
            boolean bl3 = flag3 = i1 <= this.maxHeight;
            if (!flag2 && !flag3) {
                return false;
            }
            boolean flag4 = j2 != l2;
            boolean bl4 = flag5 = k2 != i1;
            flag1 = flag4 ^ flag5 ? !flag4 : flag2 && j2 <= k2;
        } else {
            boolean flag7;
            boolean flag6 = this.currentWidth + i2 <= this.maxWidth;
            boolean bl5 = flag7 = this.currentHeight + i2 <= this.maxHeight;
            if (!flag6 && !flag7) {
                return false;
            }
            flag1 = flag6 && (flag || this.currentWidth <= this.currentHeight);
        }
        int j1 = Math.max(p_94311_1_.getWidth(), p_94311_1_.getHeight());
        if (MathHelper.roundUpToPowerOfTwo((!flag1 ? this.currentHeight : this.currentWidth) + j1) > (!flag1 ? this.maxHeight : this.maxWidth)) {
            return false;
        }
        if (flag1) {
            if (p_94311_1_.getWidth() > p_94311_1_.getHeight()) {
                p_94311_1_.rotate();
            }
            if (this.currentHeight == 0) {
                this.currentHeight = p_94311_1_.getHeight();
            }
            stitcher$slot = new Slot(this.currentWidth, 0, p_94311_1_.getWidth(), this.currentHeight);
            this.currentWidth += p_94311_1_.getWidth();
        } else {
            stitcher$slot = new Slot(0, this.currentHeight, this.currentWidth, p_94311_1_.getHeight());
            this.currentHeight += p_94311_1_.getHeight();
        }
        stitcher$slot.addSlot(p_94311_1_);
        this.stitchSlots.add(stitcher$slot);
        return true;
    }

    public static class Holder
    implements Comparable<Holder> {
        private final TextureAtlasSprite theTexture;
        private final int width;
        private final int height;
        private final int mipmapLevelHolder;
        private boolean rotated;
        private float scaleFactor = 1.0f;

        public Holder(TextureAtlasSprite p_i45094_1_, int p_i45094_2_) {
            this.theTexture = p_i45094_1_;
            this.width = p_i45094_1_.getIconWidth();
            this.height = p_i45094_1_.getIconHeight();
            this.mipmapLevelHolder = p_i45094_2_;
            this.rotated = Stitcher.getMipmapDimension(this.height, p_i45094_2_) > Stitcher.getMipmapDimension(this.width, p_i45094_2_);
        }

        public TextureAtlasSprite getAtlasSprite() {
            return this.theTexture;
        }

        public int getWidth() {
            return this.rotated ? Stitcher.getMipmapDimension((int)((float)this.height * this.scaleFactor), this.mipmapLevelHolder) : Stitcher.getMipmapDimension((int)((float)this.width * this.scaleFactor), this.mipmapLevelHolder);
        }

        public int getHeight() {
            return this.rotated ? Stitcher.getMipmapDimension((int)((float)this.width * this.scaleFactor), this.mipmapLevelHolder) : Stitcher.getMipmapDimension((int)((float)this.height * this.scaleFactor), this.mipmapLevelHolder);
        }

        public void rotate() {
            this.rotated = !this.rotated;
        }

        public boolean isRotated() {
            return this.rotated;
        }

        public void setNewDimension(int p_94196_1_) {
            if (this.width > p_94196_1_ && this.height > p_94196_1_) {
                this.scaleFactor = (float)p_94196_1_ / (float)Math.min(this.width, this.height);
            }
        }

        public String toString() {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }

        @Override
        public int compareTo(Holder p_compareTo_1_) {
            int i2;
            if (this.getHeight() == p_compareTo_1_.getHeight()) {
                if (this.getWidth() == p_compareTo_1_.getWidth()) {
                    if (this.theTexture.getIconName() == null) {
                        return p_compareTo_1_.theTexture.getIconName() == null ? 0 : -1;
                    }
                    return this.theTexture.getIconName().compareTo(p_compareTo_1_.theTexture.getIconName());
                }
                i2 = this.getWidth() < p_compareTo_1_.getWidth() ? 1 : -1;
            } else {
                i2 = this.getHeight() < p_compareTo_1_.getHeight() ? 1 : -1;
            }
            return i2;
        }
    }

    public static class Slot {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        private List<Slot> subSlots;
        private Holder holder;

        public Slot(int p_i1277_1_, int p_i1277_2_, int widthIn, int heightIn) {
            this.originX = p_i1277_1_;
            this.originY = p_i1277_2_;
            this.width = widthIn;
            this.height = heightIn;
        }

        public Holder getStitchHolder() {
            return this.holder;
        }

        public int getOriginX() {
            return this.originX;
        }

        public int getOriginY() {
            return this.originY;
        }

        public boolean addSlot(Holder holderIn) {
            if (this.holder != null) {
                return false;
            }
            int i2 = holderIn.getWidth();
            int j2 = holderIn.getHeight();
            if (i2 <= this.width && j2 <= this.height) {
                if (i2 == this.width && j2 == this.height) {
                    this.holder = holderIn;
                    return true;
                }
                if (this.subSlots == null) {
                    this.subSlots = Lists.newArrayListWithCapacity(1);
                    this.subSlots.add(new Slot(this.originX, this.originY, i2, j2));
                    int k2 = this.width - i2;
                    int l2 = this.height - j2;
                    if (l2 > 0 && k2 > 0) {
                        int j1;
                        int i1 = Math.max(this.height, k2);
                        if (i1 >= (j1 = Math.max(this.width, l2))) {
                            this.subSlots.add(new Slot(this.originX, this.originY + j2, i2, l2));
                            this.subSlots.add(new Slot(this.originX + i2, this.originY, k2, this.height));
                        } else {
                            this.subSlots.add(new Slot(this.originX + i2, this.originY, k2, j2));
                            this.subSlots.add(new Slot(this.originX, this.originY + j2, this.width, l2));
                        }
                    } else if (k2 == 0) {
                        this.subSlots.add(new Slot(this.originX, this.originY + j2, i2, l2));
                    } else if (l2 == 0) {
                        this.subSlots.add(new Slot(this.originX + i2, this.originY, k2, j2));
                    }
                }
                for (Slot stitcher$slot : this.subSlots) {
                    if (!stitcher$slot.addSlot(holderIn)) continue;
                    return true;
                }
                return false;
            }
            return false;
        }

        public void getAllStitchSlots(List<Slot> p_94184_1_) {
            if (this.holder != null) {
                p_94184_1_.add(this);
            } else if (this.subSlots != null) {
                for (Slot stitcher$slot : this.subSlots) {
                    stitcher$slot.getAllStitchSlots(p_94184_1_);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
        }
    }
}

