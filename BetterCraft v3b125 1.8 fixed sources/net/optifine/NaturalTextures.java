/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.optifine.ConnectedTextures;
import net.optifine.NaturalProperties;
import net.optifine.util.TextureUtils;

public class NaturalTextures {
    private static NaturalProperties[] propertiesByIndex = new NaturalProperties[0];

    public static void update() {
        propertiesByIndex = new NaturalProperties[0];
        if (Config.isNaturalTextures()) {
            String s2 = "optifine/natural.properties";
            try {
                ResourceLocation resourcelocation = new ResourceLocation(s2);
                if (!Config.hasResource(resourcelocation)) {
                    Config.dbg("NaturalTextures: configuration \"" + s2 + "\" not found");
                    return;
                }
                boolean flag = Config.isFromDefaultResourcePack(resourcelocation);
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                ArrayList<NaturalProperties> arraylist = new ArrayList<NaturalProperties>(256);
                String s1 = Config.readInputStream(inputstream);
                inputstream.close();
                String[] astring = Config.tokenize(s1, "\n\r");
                if (flag) {
                    Config.dbg("Natural Textures: Parsing default configuration \"" + s2 + "\"");
                    Config.dbg("Natural Textures: Valid only for textures from default resource pack");
                } else {
                    Config.dbg("Natural Textures: Parsing configuration \"" + s2 + "\"");
                }
                TextureMap texturemap = TextureUtils.getTextureMapBlocks();
                int i2 = 0;
                while (i2 < astring.length) {
                    String s22 = astring[i2].trim();
                    if (!s22.startsWith("#")) {
                        String[] astring1 = Config.tokenize(s22, "=");
                        if (astring1.length != 2) {
                            Config.warn("Natural Textures: Invalid \"" + s2 + "\" line: " + s22);
                        } else {
                            String s3 = astring1[0].trim();
                            String s4 = astring1[1].trim();
                            TextureAtlasSprite textureatlassprite = texturemap.getSpriteSafe("minecraft:blocks/" + s3);
                            if (textureatlassprite == null) {
                                Config.warn("Natural Textures: Texture not found: \"" + s2 + "\" line: " + s22);
                            } else {
                                int j2 = textureatlassprite.getIndexInMap();
                                if (j2 < 0) {
                                    Config.warn("Natural Textures: Invalid \"" + s2 + "\" line: " + s22);
                                } else {
                                    if (flag && !Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/" + s3 + ".png"))) {
                                        return;
                                    }
                                    NaturalProperties naturalproperties = new NaturalProperties(s4);
                                    if (naturalproperties.isValid()) {
                                        while (arraylist.size() <= j2) {
                                            arraylist.add(null);
                                        }
                                        arraylist.set(j2, naturalproperties);
                                        Config.dbg("NaturalTextures: " + s3 + " = " + s4);
                                    }
                                }
                            }
                        }
                    }
                    ++i2;
                }
                propertiesByIndex = arraylist.toArray(new NaturalProperties[arraylist.size()]);
            }
            catch (FileNotFoundException var17) {
                Config.warn("NaturalTextures: configuration \"" + s2 + "\" not found");
                return;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static BakedQuad getNaturalTexture(BlockPos blockPosIn, BakedQuad quad) {
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        if (textureatlassprite == null) {
            return quad;
        }
        NaturalProperties naturalproperties = NaturalTextures.getNaturalProperties(textureatlassprite);
        if (naturalproperties == null) {
            return quad;
        }
        int i2 = ConnectedTextures.getSide(quad.getFace());
        int j2 = Config.getRandom(blockPosIn, i2);
        int k2 = 0;
        boolean flag = false;
        if (naturalproperties.rotation > 1) {
            k2 = j2 & 3;
        }
        if (naturalproperties.rotation == 2) {
            k2 = k2 / 2 * 2;
        }
        if (naturalproperties.flip) {
            flag = (j2 & 4) != 0;
        }
        return naturalproperties.getQuad(quad, k2, flag);
    }

    public static NaturalProperties getNaturalProperties(TextureAtlasSprite icon) {
        if (!(icon instanceof TextureAtlasSprite)) {
            return null;
        }
        int i2 = icon.getIndexInMap();
        if (i2 >= 0 && i2 < propertiesByIndex.length) {
            NaturalProperties naturalproperties = propertiesByIndex[i2];
            return naturalproperties;
        }
        return null;
    }
}

