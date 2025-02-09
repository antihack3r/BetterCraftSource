/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerMooshroomMushroom;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.BetterGrass;
import net.optifine.BetterSnow;
import net.optifine.CustomBlockLayers;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomItems;
import net.optifine.CustomLoadingScreens;
import net.optifine.CustomPanorama;
import net.optifine.CustomSky;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.SmartLeaves;
import net.optifine.TextureAnimations;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.shaders.MultiTexID;
import net.optifine.shaders.Shaders;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class TextureUtils {
    public static final String texGrassTop = "grass_top";
    public static final String texStone = "stone";
    public static final String texDirt = "dirt";
    public static final String texCoarseDirt = "coarse_dirt";
    public static final String texGrassSide = "grass_side";
    public static final String texStoneslabSide = "stone_slab_side";
    public static final String texStoneslabTop = "stone_slab_top";
    public static final String texBedrock = "bedrock";
    public static final String texSand = "sand";
    public static final String texGravel = "gravel";
    public static final String texLogOak = "log_oak";
    public static final String texLogBigOak = "log_big_oak";
    public static final String texLogAcacia = "log_acacia";
    public static final String texLogSpruce = "log_spruce";
    public static final String texLogBirch = "log_birch";
    public static final String texLogJungle = "log_jungle";
    public static final String texLogOakTop = "log_oak_top";
    public static final String texLogBigOakTop = "log_big_oak_top";
    public static final String texLogAcaciaTop = "log_acacia_top";
    public static final String texLogSpruceTop = "log_spruce_top";
    public static final String texLogBirchTop = "log_birch_top";
    public static final String texLogJungleTop = "log_jungle_top";
    public static final String texLeavesOak = "leaves_oak";
    public static final String texLeavesBigOak = "leaves_big_oak";
    public static final String texLeavesAcacia = "leaves_acacia";
    public static final String texLeavesBirch = "leaves_birch";
    public static final String texLeavesSpuce = "leaves_spruce";
    public static final String texLeavesJungle = "leaves_jungle";
    public static final String texGoldOre = "gold_ore";
    public static final String texIronOre = "iron_ore";
    public static final String texCoalOre = "coal_ore";
    public static final String texObsidian = "obsidian";
    public static final String texGrassSideOverlay = "grass_side_overlay";
    public static final String texSnow = "snow";
    public static final String texGrassSideSnowed = "grass_side_snowed";
    public static final String texMyceliumSide = "mycelium_side";
    public static final String texMyceliumTop = "mycelium_top";
    public static final String texDiamondOre = "diamond_ore";
    public static final String texRedstoneOre = "redstone_ore";
    public static final String texLapisOre = "lapis_ore";
    public static final String texCactusSide = "cactus_side";
    public static final String texClay = "clay";
    public static final String texFarmlandWet = "farmland_wet";
    public static final String texFarmlandDry = "farmland_dry";
    public static final String texNetherrack = "netherrack";
    public static final String texSoulSand = "soul_sand";
    public static final String texGlowstone = "glowstone";
    public static final String texLeavesSpruce = "leaves_spruce";
    public static final String texLeavesSpruceOpaque = "leaves_spruce_opaque";
    public static final String texEndStone = "end_stone";
    public static final String texSandstoneTop = "sandstone_top";
    public static final String texSandstoneBottom = "sandstone_bottom";
    public static final String texRedstoneLampOff = "redstone_lamp_off";
    public static final String texRedstoneLampOn = "redstone_lamp_on";
    public static final String texWaterStill = "water_still";
    public static final String texWaterFlow = "water_flow";
    public static final String texLavaStill = "lava_still";
    public static final String texLavaFlow = "lava_flow";
    public static final String texFireLayer0 = "fire_layer_0";
    public static final String texFireLayer1 = "fire_layer_1";
    public static final String texPortal = "portal";
    public static final String texGlass = "glass";
    public static final String texGlassPaneTop = "glass_pane_top";
    public static final String texCompass = "compass";
    public static final String texClock = "clock";
    public static TextureAtlasSprite iconGrassTop;
    public static TextureAtlasSprite iconGrassSide;
    public static TextureAtlasSprite iconGrassSideOverlay;
    public static TextureAtlasSprite iconSnow;
    public static TextureAtlasSprite iconGrassSideSnowed;
    public static TextureAtlasSprite iconMyceliumSide;
    public static TextureAtlasSprite iconMyceliumTop;
    public static TextureAtlasSprite iconWaterStill;
    public static TextureAtlasSprite iconWaterFlow;
    public static TextureAtlasSprite iconLavaStill;
    public static TextureAtlasSprite iconLavaFlow;
    public static TextureAtlasSprite iconPortal;
    public static TextureAtlasSprite iconFireLayer0;
    public static TextureAtlasSprite iconFireLayer1;
    public static TextureAtlasSprite iconGlass;
    public static TextureAtlasSprite iconGlassPaneTop;
    public static TextureAtlasSprite iconCompass;
    public static TextureAtlasSprite iconClock;
    public static final String SPRITE_PREFIX_BLOCKS = "minecraft:blocks/";
    public static final String SPRITE_PREFIX_ITEMS = "minecraft:items/";
    private static IntBuffer staticBuffer;

    static {
        staticBuffer = GLAllocation.createDirectIntBuffer(256);
    }

    public static void update() {
        TextureMap texturemap = TextureUtils.getTextureMapBlocks();
        if (texturemap != null) {
            String s2 = SPRITE_PREFIX_BLOCKS;
            iconGrassTop = texturemap.getSpriteSafe(String.valueOf(s2) + texGrassTop);
            iconGrassSide = texturemap.getSpriteSafe(String.valueOf(s2) + texGrassSide);
            iconGrassSideOverlay = texturemap.getSpriteSafe(String.valueOf(s2) + texGrassSideOverlay);
            iconSnow = texturemap.getSpriteSafe(String.valueOf(s2) + texSnow);
            iconGrassSideSnowed = texturemap.getSpriteSafe(String.valueOf(s2) + texGrassSideSnowed);
            iconMyceliumSide = texturemap.getSpriteSafe(String.valueOf(s2) + texMyceliumSide);
            iconMyceliumTop = texturemap.getSpriteSafe(String.valueOf(s2) + texMyceliumTop);
            iconWaterStill = texturemap.getSpriteSafe(String.valueOf(s2) + texWaterStill);
            iconWaterFlow = texturemap.getSpriteSafe(String.valueOf(s2) + texWaterFlow);
            iconLavaStill = texturemap.getSpriteSafe(String.valueOf(s2) + texLavaStill);
            iconLavaFlow = texturemap.getSpriteSafe(String.valueOf(s2) + texLavaFlow);
            iconFireLayer0 = texturemap.getSpriteSafe(String.valueOf(s2) + texFireLayer0);
            iconFireLayer1 = texturemap.getSpriteSafe(String.valueOf(s2) + texFireLayer1);
            iconPortal = texturemap.getSpriteSafe(String.valueOf(s2) + texPortal);
            iconGlass = texturemap.getSpriteSafe(String.valueOf(s2) + texGlass);
            iconGlassPaneTop = texturemap.getSpriteSafe(String.valueOf(s2) + texGlassPaneTop);
            String s1 = SPRITE_PREFIX_ITEMS;
            iconCompass = texturemap.getSpriteSafe(String.valueOf(s1) + texCompass);
            iconClock = texturemap.getSpriteSafe(String.valueOf(s1) + texClock);
        }
    }

    public static BufferedImage fixTextureDimensions(String name, BufferedImage bi2) {
        int j2;
        int i2;
        if ((name.startsWith("/mob/zombie") || name.startsWith("/mob/pigzombie")) && (i2 = bi2.getWidth()) == (j2 = bi2.getHeight()) * 2) {
            BufferedImage bufferedimage = new BufferedImage(i2, j2 * 2, 2);
            Graphics2D graphics2d = bufferedimage.createGraphics();
            graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2d.drawImage(bi2, 0, 0, i2, j2, null);
            return bufferedimage;
        }
        return bi2;
    }

    public static int ceilPowerOfTwo(int val) {
        int i2 = 1;
        while (i2 < val) {
            i2 *= 2;
        }
        return i2;
    }

    public static int getPowerOfTwo(int val) {
        int i2 = 1;
        int j2 = 0;
        while (i2 < val) {
            i2 *= 2;
            ++j2;
        }
        return j2;
    }

    public static int twoToPower(int power) {
        int i2 = 1;
        int j2 = 0;
        while (j2 < power) {
            i2 *= 2;
            ++j2;
        }
        return i2;
    }

    public static ITextureObject getTexture(ResourceLocation loc) {
        ITextureObject itextureobject = Config.getTextureManager().getTexture(loc);
        if (itextureobject != null) {
            return itextureobject;
        }
        if (!Config.hasResource(loc)) {
            return null;
        }
        SimpleTexture simpletexture = new SimpleTexture(loc);
        Config.getTextureManager().loadTexture(loc, simpletexture);
        return simpletexture;
    }

    public static void resourcesReloaded(IResourceManager rm2) {
        if (TextureUtils.getTextureMapBlocks() != null) {
            Config.dbg("*** Reloading custom textures ***");
            CustomSky.reset();
            TextureAnimations.reset();
            TextureUtils.update();
            NaturalTextures.update();
            BetterGrass.update();
            BetterSnow.update();
            TextureAnimations.update();
            CustomColors.update();
            CustomSky.update();
            RandomEntities.update();
            CustomItems.updateModels();
            CustomEntityModels.update();
            Shaders.resourcesReloaded();
            Lang.resourcesReloaded();
            Config.updateTexturePackClouds();
            SmartLeaves.updateLeavesModels();
            CustomPanorama.update();
            CustomGuis.update();
            LayerMooshroomMushroom.update();
            CustomLoadingScreens.update();
            CustomBlockLayers.update();
            Config.getTextureManager().tick();
        }
    }

    public static TextureMap getTextureMapBlocks() {
        return Minecraft.getMinecraft().getTextureMapBlocks();
    }

    public static void registerResourceListener() {
        IResourceManager iresourcemanager = Config.getResourceManager();
        if (iresourcemanager instanceof IReloadableResourceManager) {
            IReloadableResourceManager ireloadableresourcemanager = (IReloadableResourceManager)iresourcemanager;
            IResourceManagerReloadListener iresourcemanagerreloadlistener = new IResourceManagerReloadListener(){

                @Override
                public void onResourceManagerReload(IResourceManager var1) {
                    TextureUtils.resourcesReloaded(var1);
                }
            };
            ireloadableresourcemanager.registerReloadListener(iresourcemanagerreloadlistener);
        }
        ITickableTextureObject itickabletextureobject = new ITickableTextureObject(){

            @Override
            public void tick() {
                TextureAnimations.updateAnimations();
            }

            @Override
            public void loadTexture(IResourceManager var1) throws IOException {
            }

            @Override
            public int getGlTextureId() {
                return 0;
            }

            @Override
            public void setBlurMipmap(boolean p_174936_1, boolean p_174936_2) {
            }

            @Override
            public void restoreLastBlurMipmap() {
            }

            @Override
            public MultiTexID getMultiTexID() {
                return null;
            }
        };
        ResourceLocation resourcelocation = new ResourceLocation("optifine/TickableTextures");
        Config.getTextureManager().loadTickableTexture(resourcelocation, itickabletextureobject);
    }

    public static ResourceLocation fixResourceLocation(ResourceLocation loc, String basePath) {
        if (!loc.getResourceDomain().equals("minecraft")) {
            return loc;
        }
        String s2 = loc.getResourcePath();
        String s1 = TextureUtils.fixResourcePath(s2, basePath);
        if (s1 != s2) {
            loc = new ResourceLocation(loc.getResourceDomain(), s1);
        }
        return loc;
    }

    public static String fixResourcePath(String path, String basePath) {
        String s2 = "assets/minecraft/";
        if (path.startsWith(s2)) {
            path = path.substring(s2.length());
            return path;
        }
        if (path.startsWith("./")) {
            path = path.substring(2);
            if (!basePath.endsWith("/")) {
                basePath = String.valueOf(basePath) + "/";
            }
            path = String.valueOf(basePath) + path;
            return path;
        }
        if (path.startsWith("/~")) {
            path = path.substring(1);
        }
        String s1 = "mcpatcher/";
        if (path.startsWith("~/")) {
            path = path.substring(2);
            path = String.valueOf(s1) + path;
            return path;
        }
        if (path.startsWith("/")) {
            path = String.valueOf(s1) + path.substring(1);
            return path;
        }
        return path;
    }

    public static String getBasePath(String path) {
        int i2 = path.lastIndexOf(47);
        return i2 < 0 ? "" : path.substring(0, i2);
    }

    public static void applyAnisotropicLevel() {
        if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            float f2 = GL11.glGetFloat(34047);
            float f1 = Config.getAnisotropicFilterLevel();
            f1 = Math.min(f1, f2);
            GL11.glTexParameterf(3553, 34046, f1);
        }
    }

    public static void bindTexture(int glTexId) {
        GlStateManager.bindTexture(glTexId);
    }

    public static boolean isPowerOfTwo(int x2) {
        int i2 = MathHelper.roundUpToPowerOfTwo(x2);
        return i2 == x2;
    }

    public static BufferedImage scaleImage(BufferedImage bi2, int w2) {
        int i2 = bi2.getWidth();
        int j2 = bi2.getHeight();
        int k2 = j2 * w2 / i2;
        BufferedImage bufferedimage = new BufferedImage(w2, k2, 2);
        Graphics2D graphics2d = bufferedimage.createGraphics();
        Object object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        if (w2 < i2 || w2 % i2 != 0) {
            object = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        }
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
        graphics2d.drawImage(bi2, 0, 0, w2, k2, null);
        return bufferedimage;
    }

    public static int scaleToGrid(int size, int sizeGrid) {
        if (size == sizeGrid) {
            return size;
        }
        int i2 = size / sizeGrid * sizeGrid;
        while (i2 < size) {
            i2 += sizeGrid;
        }
        return i2;
    }

    public static int scaleToMin(int size, int sizeMin) {
        if (size >= sizeMin) {
            return size;
        }
        int i2 = sizeMin / size * size;
        while (i2 < sizeMin) {
            i2 += size;
        }
        return i2;
    }

    public static Dimension getImageSize(InputStream in2, String suffix) {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix(suffix);
        while (iterator.hasNext()) {
            Dimension dimension;
            ImageReader imagereader = iterator.next();
            try {
                try {
                    ImageInputStream imageinputstream = ImageIO.createImageInputStream(in2);
                    imagereader.setInput(imageinputstream);
                    int i2 = imagereader.getWidth(imagereader.getMinIndex());
                    int j2 = imagereader.getHeight(imagereader.getMinIndex());
                    dimension = new Dimension(i2, j2);
                }
                catch (IOException var11) {
                    imagereader.dispose();
                    continue;
                }
            }
            catch (Throwable throwable) {
                imagereader.dispose();
                throw throwable;
            }
            imagereader.dispose();
            return dimension;
        }
        return null;
    }

    public static void dbgMipmaps(TextureAtlasSprite textureatlassprite) {
        int[][] aint = textureatlassprite.getFrameTextureData(0);
        int i2 = 0;
        while (i2 < aint.length) {
            int[] aint1 = aint[i2];
            if (aint1 == null) {
                Config.dbg(i2 + ": " + aint1);
            } else {
                Config.dbg(i2 + ": " + aint1.length);
            }
            ++i2;
        }
    }

    public static void saveGlTexture(String name, int textureId, int mipmapLevels, int width, int height) {
        TextureUtils.bindTexture(textureId);
        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        File file1 = new File(name);
        File file2 = file1.getParentFile();
        if (file2 != null) {
            file2.mkdirs();
        }
        int i2 = 0;
        while (i2 < 16) {
            File file3 = new File(String.valueOf(name) + "_" + i2 + ".png");
            file3.delete();
            ++i2;
        }
        int i1 = 0;
        while (i1 <= mipmapLevels) {
            File file4 = new File(String.valueOf(name) + "_" + i1 + ".png");
            int j2 = width >> i1;
            int k2 = height >> i1;
            int l2 = j2 * k2;
            IntBuffer intbuffer = BufferUtils.createIntBuffer(l2);
            int[] aint = new int[l2];
            GL11.glGetTexImage(3553, i1, 32993, 33639, intbuffer);
            intbuffer.get(aint);
            BufferedImage bufferedimage = new BufferedImage(j2, k2, 2);
            bufferedimage.setRGB(0, 0, j2, k2, aint, 0, j2);
            try {
                ImageIO.write((RenderedImage)bufferedimage, "png", file4);
                Config.dbg("Exported: " + file4);
            }
            catch (Exception exception) {
                Config.warn("Error writing: " + file4);
                Config.warn(exception.getClass().getName() + ": " + exception.getMessage());
            }
            ++i1;
        }
    }

    public static void generateCustomMipmaps(TextureAtlasSprite tas, int mipmaps) {
        int i2 = tas.getIconWidth();
        int j2 = tas.getIconHeight();
        if (tas.getFrameCount() < 1) {
            ArrayList<int[][]> list = new ArrayList<int[][]>();
            int[][] aint = new int[mipmaps + 1][];
            int[] aint1 = new int[i2 * j2];
            aint[0] = aint1;
            list.add(aint);
            tas.setFramesTextureData(list);
        }
        ArrayList<int[][]> list1 = new ArrayList<int[][]>();
        int l2 = tas.getFrameCount();
        int i1 = 0;
        while (i1 < l2) {
            int[] aint2 = TextureUtils.getFrameData(tas, i1, 0);
            if (aint2 == null || aint2.length < 1) {
                aint2 = new int[i2 * j2];
            }
            if (aint2.length != i2 * j2) {
                int k2 = (int)Math.round(Math.sqrt(aint2.length));
                if (k2 * k2 != aint2.length) {
                    aint2 = new int[1];
                    k2 = 1;
                }
                BufferedImage bufferedimage = new BufferedImage(k2, k2, 2);
                bufferedimage.setRGB(0, 0, k2, k2, aint2, 0, k2);
                BufferedImage bufferedimage1 = TextureUtils.scaleImage(bufferedimage, i2);
                int[] aint3 = new int[i2 * j2];
                bufferedimage1.getRGB(0, 0, i2, j2, aint3, 0, i2);
                aint2 = aint3;
            }
            int[][] aint4 = new int[mipmaps + 1][];
            aint4[0] = aint2;
            list1.add(aint4);
            ++i1;
        }
        tas.setFramesTextureData(list1);
        tas.generateMipmaps(mipmaps);
    }

    public static int[] getFrameData(TextureAtlasSprite tas, int frame, int level) {
        List<int[][]> list = tas.getFramesTextureData();
        if (list.size() <= frame) {
            return null;
        }
        int[][] aint = list.get(frame);
        if (aint != null && aint.length > level) {
            int[] aint1 = aint[level];
            return aint1;
        }
        return null;
    }

    public static int getGLMaximumTextureSize() {
        int i2 = 65536;
        while (i2 > 0) {
            GlStateManager.glTexImage2D(32868, 0, 6408, i2, i2, 0, 6408, 5121, null);
            int j2 = GL11.glGetError();
            int k2 = GlStateManager.glGetTexLevelParameteri(32868, 0, 4096);
            if (k2 != 0) {
                return i2;
            }
            i2 >>= 1;
        }
        return -1;
    }
}

