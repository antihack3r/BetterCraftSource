/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.CustomSkyLayer;
import net.optifine.render.Blender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;

public class CustomSky {
    private static CustomSkyLayer[][] worldSkyLayers = null;

    public static void reset() {
        worldSkyLayers = null;
    }

    public static void update() {
        CustomSky.reset();
        if (Config.isCustomSky()) {
            worldSkyLayers = CustomSky.readCustomSkies();
        }
    }

    private static CustomSkyLayer[][] readCustomSkies() {
        CustomSkyLayer[][] acustomskylayer = new CustomSkyLayer[10][0];
        String s2 = "mcpatcher/sky/world";
        int i2 = -1;
        int j2 = 0;
        while (j2 < acustomskylayer.length) {
            String s1 = String.valueOf(s2) + j2 + "/sky";
            ArrayList<CustomSkyLayer> list = new ArrayList<CustomSkyLayer>();
            int k2 = 1;
            while (k2 < 1000) {
                String s22 = String.valueOf(s1) + k2 + ".properties";
                try {
                    ResourceLocation resourcelocation = new ResourceLocation(s22);
                    InputStream inputstream = Config.getResourceStream(resourcelocation);
                    if (inputstream == null) break;
                    PropertiesOrdered properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    Config.dbg("CustomSky properties: " + s22);
                    String s3 = String.valueOf(s1) + k2 + ".png";
                    CustomSkyLayer customskylayer = new CustomSkyLayer(properties, s3);
                    if (customskylayer.isValid(s22)) {
                        ResourceLocation resourcelocation1 = new ResourceLocation(customskylayer.source);
                        ITextureObject itextureobject = TextureUtils.getTexture(resourcelocation1);
                        if (itextureobject == null) {
                            Config.log("CustomSky: Texture not found: " + resourcelocation1);
                        } else {
                            customskylayer.textureId = itextureobject.getGlTextureId();
                            list.add(customskylayer);
                            inputstream.close();
                        }
                    }
                }
                catch (FileNotFoundException var15) {
                    break;
                }
                catch (IOException ioexception) {
                    ioexception.printStackTrace();
                }
                ++k2;
            }
            if (list.size() > 0) {
                CustomSkyLayer[] acustomskylayer2 = list.toArray(new CustomSkyLayer[list.size()]);
                acustomskylayer[j2] = acustomskylayer2;
                i2 = j2;
            }
            ++j2;
        }
        if (i2 < 0) {
            return null;
        }
        int l2 = i2 + 1;
        CustomSkyLayer[][] acustomskylayer1 = new CustomSkyLayer[l2][0];
        int i1 = 0;
        while (i1 < acustomskylayer1.length) {
            acustomskylayer1[i1] = acustomskylayer[i1];
            ++i1;
        }
        return acustomskylayer1;
    }

    public static void renderSky(World world, TextureManager re2, float partialTicks) {
        CustomSkyLayer[] acustomskylayer;
        int i2;
        if (worldSkyLayers != null && (i2 = world.provider.getDimensionId()) >= 0 && i2 < worldSkyLayers.length && (acustomskylayer = worldSkyLayers[i2]) != null) {
            long j2 = world.getWorldTime();
            int k2 = (int)(j2 % 24000L);
            float f2 = world.getCelestialAngle(partialTicks);
            float f1 = world.getRainStrength(partialTicks);
            float f22 = world.getThunderStrength(partialTicks);
            if (f1 > 0.0f) {
                f22 /= f1;
            }
            int l2 = 0;
            while (l2 < acustomskylayer.length) {
                CustomSkyLayer customskylayer = acustomskylayer[l2];
                if (customskylayer.isActive(world, k2)) {
                    customskylayer.render(world, k2, f2, f1, f22);
                }
                ++l2;
            }
            float f3 = 1.0f - f1;
            Blender.clearBlend(f3);
        }
    }

    public static boolean hasSkyLayers(World world) {
        if (worldSkyLayers == null) {
            return false;
        }
        int i2 = world.provider.getDimensionId();
        if (i2 >= 0 && i2 < worldSkyLayers.length) {
            CustomSkyLayer[] acustomskylayer = worldSkyLayers[i2];
            return acustomskylayer == null ? false : acustomskylayer.length > 0;
        }
        return false;
    }
}

