/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.SmartAnimations;
import net.optifine.TextureAnimation;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.TextureUtils;

public class TextureAnimations {
    private static TextureAnimation[] textureAnimations = null;
    private static int countAnimationsActive = 0;
    private static int frameCountAnimations = 0;

    public static void reset() {
        textureAnimations = null;
    }

    public static void update() {
        textureAnimations = null;
        countAnimationsActive = 0;
        IResourcePack[] airesourcepack = Config.getResourcePacks();
        textureAnimations = TextureAnimations.getTextureAnimations(airesourcepack);
        TextureAnimations.updateAnimations();
    }

    public static void updateAnimations() {
        if (textureAnimations != null && Config.isAnimatedTextures()) {
            int i2 = 0;
            int j2 = 0;
            while (j2 < textureAnimations.length) {
                TextureAnimation textureanimation = textureAnimations[j2];
                textureanimation.updateTexture();
                if (textureanimation.isActive()) {
                    ++i2;
                }
                ++j2;
            }
            int k2 = Config.getMinecraft().entityRenderer.frameCount;
            if (k2 != frameCountAnimations) {
                countAnimationsActive = i2;
                frameCountAnimations = k2;
            }
            if (SmartAnimations.isActive()) {
                SmartAnimations.resetTexturesRendered();
            }
        } else {
            countAnimationsActive = 0;
        }
    }

    private static TextureAnimation[] getTextureAnimations(IResourcePack[] rps) {
        ArrayList<TextureAnimation> list = new ArrayList<TextureAnimation>();
        int i2 = 0;
        while (i2 < rps.length) {
            IResourcePack iresourcepack = rps[i2];
            TextureAnimation[] atextureanimation = TextureAnimations.getTextureAnimations(iresourcepack);
            if (atextureanimation != null) {
                list.addAll(Arrays.asList(atextureanimation));
            }
            ++i2;
        }
        TextureAnimation[] atextureanimation1 = list.toArray(new TextureAnimation[list.size()]);
        return atextureanimation1;
    }

    private static TextureAnimation[] getTextureAnimations(IResourcePack rp2) {
        String[] astring = ResUtils.collectFiles(rp2, "mcpatcher/anim/", ".properties", null);
        if (astring.length <= 0) {
            return null;
        }
        ArrayList<TextureAnimation> list = new ArrayList<TextureAnimation>();
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            Config.dbg("Texture animation: " + s2);
            try {
                ResourceLocation resourcelocation = new ResourceLocation(s2);
                InputStream inputstream = rp2.getInputStream(resourcelocation);
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                TextureAnimation textureanimation = TextureAnimations.makeTextureAnimation(properties, resourcelocation);
                if (textureanimation != null) {
                    ResourceLocation resourcelocation1 = new ResourceLocation(textureanimation.getDstTex());
                    if (Config.getDefiningResourcePack(resourcelocation1) != rp2) {
                        Config.dbg("Skipped: " + s2 + ", target texture not loaded from same resource pack");
                    } else {
                        list.add(textureanimation);
                    }
                }
            }
            catch (FileNotFoundException filenotfoundexception) {
                Config.warn("File not found: " + filenotfoundexception.getMessage());
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
            ++i2;
        }
        TextureAnimation[] atextureanimation = list.toArray(new TextureAnimation[list.size()]);
        return atextureanimation;
    }

    private static TextureAnimation makeTextureAnimation(Properties props, ResourceLocation propLoc) {
        String s2 = props.getProperty("from");
        String s1 = props.getProperty("to");
        int i2 = Config.parseInt(props.getProperty("x"), -1);
        int j2 = Config.parseInt(props.getProperty("y"), -1);
        int k2 = Config.parseInt(props.getProperty("w"), -1);
        int l2 = Config.parseInt(props.getProperty("h"), -1);
        if (s2 != null && s1 != null) {
            if (i2 >= 0 && j2 >= 0 && k2 >= 0 && l2 >= 0) {
                InputStream inputstream;
                ResourceLocation resourcelocation;
                byte[] abyte;
                block8: {
                    s2 = s2.trim();
                    s1 = s1.trim();
                    String s22 = TextureUtils.getBasePath(propLoc.getResourcePath());
                    s2 = TextureUtils.fixResourcePath(s2, s22);
                    s1 = TextureUtils.fixResourcePath(s1, s22);
                    abyte = TextureAnimations.getCustomTextureData(s2, k2);
                    if (abyte == null) {
                        Config.warn("TextureAnimation: Source texture not found: " + s1);
                        return null;
                    }
                    int i1 = abyte.length / 4;
                    int j1 = i1 / (k2 * l2);
                    int k1 = j1 * k2 * l2;
                    if (i1 != k1) {
                        Config.warn("TextureAnimation: Source texture has invalid number of frames: " + s2 + ", frames: " + (float)i1 / (float)(k2 * l2));
                        return null;
                    }
                    resourcelocation = new ResourceLocation(s1);
                    try {
                        inputstream = Config.getResourceStream(resourcelocation);
                        if (inputstream != null) break block8;
                        Config.warn("TextureAnimation: Target texture not found: " + s1);
                        return null;
                    }
                    catch (IOException var17) {
                        Config.warn("TextureAnimation: Target texture not found: " + s1);
                        return null;
                    }
                }
                BufferedImage bufferedimage = TextureAnimations.readTextureImage(inputstream);
                if (i2 + k2 <= bufferedimage.getWidth() && j2 + l2 <= bufferedimage.getHeight()) {
                    TextureAnimation textureanimation = new TextureAnimation(s2, abyte, s1, resourcelocation, i2, j2, k2, l2, props);
                    return textureanimation;
                }
                Config.warn("TextureAnimation: Animation coordinates are outside the target texture: " + s1);
                return null;
            }
            Config.warn("TextureAnimation: Invalid coordinates");
            return null;
        }
        Config.warn("TextureAnimation: Source or target texture not specified");
        return null;
    }

    private static byte[] getCustomTextureData(String imagePath, int tileWidth) {
        byte[] abyte = TextureAnimations.loadImage(imagePath, tileWidth);
        if (abyte == null) {
            abyte = TextureAnimations.loadImage("/anim" + imagePath, tileWidth);
        }
        return abyte;
    }

    private static byte[] loadImage(String name, int targetWidth) {
        BufferedImage bufferedimage;
        GameSettings gamesettings;
        block9: {
            InputStream inputstream;
            block8: {
                gamesettings = Config.getGameSettings();
                ResourceLocation resourcelocation = new ResourceLocation(name);
                inputstream = Config.getResourceStream(resourcelocation);
                if (inputstream != null) break block8;
                return null;
            }
            bufferedimage = TextureAnimations.readTextureImage(inputstream);
            inputstream.close();
            if (bufferedimage != null) break block9;
            return null;
        }
        try {
            if (targetWidth > 0 && bufferedimage.getWidth() != targetWidth) {
                double d0 = bufferedimage.getHeight() / bufferedimage.getWidth();
                int j2 = (int)((double)targetWidth * d0);
                bufferedimage = TextureAnimations.scaleBufferedImage(bufferedimage, targetWidth, j2);
            }
            int k2 = bufferedimage.getWidth();
            int i2 = bufferedimage.getHeight();
            int[] aint = new int[k2 * i2];
            byte[] abyte = new byte[k2 * i2 * 4];
            bufferedimage.getRGB(0, 0, k2, i2, aint, 0, k2);
            int k3 = 0;
            while (k3 < aint.length) {
                int l2 = aint[k3] >> 24 & 0xFF;
                int i1 = aint[k3] >> 16 & 0xFF;
                int j1 = aint[k3] >> 8 & 0xFF;
                int k1 = aint[k3] & 0xFF;
                if (gamesettings != null && gamesettings.anaglyph) {
                    int l1 = (i1 * 30 + j1 * 59 + k1 * 11) / 100;
                    int i22 = (i1 * 30 + j1 * 70) / 100;
                    int j2 = (i1 * 30 + k1 * 70) / 100;
                    i1 = l1;
                    j1 = i22;
                    k1 = j2;
                }
                abyte[k3 * 4 + 0] = (byte)i1;
                abyte[k3 * 4 + 1] = (byte)j1;
                abyte[k3 * 4 + 2] = (byte)k1;
                abyte[k3 * 4 + 3] = (byte)l2;
                ++k3;
            }
            return abyte;
        }
        catch (FileNotFoundException var18) {
            return null;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static BufferedImage readTextureImage(InputStream par1InputStream) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(par1InputStream);
        par1InputStream.close();
        return bufferedimage;
    }

    private static BufferedImage scaleBufferedImage(BufferedImage image, int width, int height) {
        BufferedImage bufferedimage = new BufferedImage(width, height, 2);
        Graphics2D graphics2d = bufferedimage.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.drawImage(image, 0, 0, width, height, null);
        return bufferedimage;
    }

    public static int getCountAnimations() {
        return textureAnimations == null ? 0 : textureAnimations.length;
    }

    public static int getCountAnimationsActive() {
        return countAnimationsActive;
    }
}

