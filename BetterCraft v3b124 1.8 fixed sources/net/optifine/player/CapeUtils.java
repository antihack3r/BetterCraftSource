/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.player;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.player.CapeImageBuffer;

public class CapeUtils {
    private static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9_]+");

    public static void downloadCape(AbstractClientPlayer player) {
        String s2 = player.getNameClear();
        if (s2 != null && !s2.isEmpty() && !s2.contains("\u0000") && PATTERN_USERNAME.matcher(s2).matches()) {
            String s1 = "http://s.optifine.net/capes/" + s2 + ".png";
            ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s2);
            TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);
            if (itextureobject != null && itextureobject instanceof ThreadDownloadImageData) {
                ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData)itextureobject;
                if (threaddownloadimagedata.imageFound != null) {
                    if (threaddownloadimagedata.imageFound.booleanValue()) {
                        player.setLocationOfCape(resourcelocation);
                        if (threaddownloadimagedata.getImageBuffer() instanceof CapeImageBuffer) {
                            CapeImageBuffer capeimagebuffer1 = (CapeImageBuffer)threaddownloadimagedata.getImageBuffer();
                            player.setElytraOfCape(capeimagebuffer1.isElytraOfCape());
                        }
                    }
                    return;
                }
            }
            CapeImageBuffer capeimagebuffer = new CapeImageBuffer(player, resourcelocation);
            ThreadDownloadImageData threaddownloadimagedata1 = new ThreadDownloadImageData(null, s1, null, capeimagebuffer);
            threaddownloadimagedata1.pipeline = true;
            texturemanager.loadTexture(resourcelocation, threaddownloadimagedata1);
        }
    }

    public static BufferedImage parseCape(BufferedImage img) {
        int i2 = 64;
        int j2 = 32;
        int k2 = img.getWidth();
        int l2 = img.getHeight();
        while (i2 < k2 || j2 < l2) {
            i2 *= 2;
            j2 *= 2;
        }
        BufferedImage bufferedimage = new BufferedImage(i2, j2, 2);
        Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(img, 0, 0, null);
        graphics.dispose();
        return bufferedimage;
    }

    public static boolean isElytraCape(BufferedImage imageRaw, BufferedImage imageFixed) {
        return imageRaw.getWidth() > imageFixed.getHeight();
    }

    public static void reloadCape(AbstractClientPlayer player) {
        String s2 = player.getNameClear();
        ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s2);
        TextureManager texturemanager = Config.getTextureManager();
        ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);
        if (itextureobject instanceof SimpleTexture) {
            SimpleTexture simpletexture = (SimpleTexture)itextureobject;
            simpletexture.deleteGlTexture();
            texturemanager.deleteTexture(resourcelocation);
        }
        player.setLocationOfCape(null);
        player.setElytraOfCape(false);
        CapeUtils.downloadCape(player);
    }
}

