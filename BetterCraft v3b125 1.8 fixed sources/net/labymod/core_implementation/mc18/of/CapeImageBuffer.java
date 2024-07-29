/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.of;

import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer
extends ImageBufferDownload {
    private WeakReference<AbstractClientPlayer> playerReference;
    private ResourceLocation resourceLocation;
    public ImageBufferDownload imageBufferDownload;

    public CapeImageBuffer(AbstractClientPlayer player, ResourceLocation resourceLocation) {
        this.playerReference = new WeakReference<AbstractClientPlayer>(player);
        this.resourceLocation = resourceLocation;
        this.imageBufferDownload = new ImageBufferDownload();
    }

    @Override
    public BufferedImage parseUserSkin(BufferedImage var1) {
        return CapeImageBuffer.parseCape(var1);
    }

    @Override
    public void skinAvailable() {
        AbstractClientPlayer player;
        AbstractClientPlayer abstractClientPlayer = player = this.playerReference == null ? null : (AbstractClientPlayer)this.playerReference.get();
        if (player != null) {
            CapeImageBuffer.setLocationOfCape(player, this.resourceLocation);
        }
    }

    public void cleanup() {
        this.playerReference = null;
    }

    private static void setLocationOfCape(AbstractClientPlayer player2, ResourceLocation resourceLocation2) {
    }

    private static BufferedImage parseCape(BufferedImage var1) {
        return null;
    }
}

