/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.MathHelper;

public class TextureClock
extends TextureAtlasSprite {
    private double currentAngle;
    private double angleDelta;

    public TextureClock(String iconName) {
        super(iconName);
    }

    @Override
    public void updateAnimation() {
        if (!this.framesTextureData.isEmpty()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            double d0 = 0.0;
            if (minecraft.theWorld != null && minecraft.thePlayer != null) {
                d0 = minecraft.theWorld.getCelestialAngle(1.0f);
                if (!minecraft.theWorld.provider.isSurfaceWorld()) {
                    d0 = Math.random();
                }
            }
            double d1 = d0 - this.currentAngle;
            while (d1 < -0.5) {
                d1 += 1.0;
            }
            while (d1 >= 0.5) {
                d1 -= 1.0;
            }
            d1 = MathHelper.clamp_double(d1, -1.0, 1.0);
            this.angleDelta += d1 * 0.1;
            this.angleDelta *= 0.8;
            this.currentAngle += this.angleDelta;
            int i2 = (int)((this.currentAngle + 1.0) * (double)this.framesTextureData.size()) % this.framesTextureData.size();
            while (i2 < 0) {
                i2 = (i2 + this.framesTextureData.size()) % this.framesTextureData.size();
            }
            if (i2 != this.frameCounter) {
                this.frameCounter = i2;
                TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
    }
}

