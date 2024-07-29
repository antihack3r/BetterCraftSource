/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpUtils;
import net.optifine.player.PlayerConfiguration;
import net.optifine.player.PlayerItemModel;
import net.optifine.player.PlayerItemParser;
import net.optifine.util.Json;

public class PlayerConfigurationParser {
    private String player = null;
    public static final String CONFIG_ITEMS = "items";
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_ACTIVE = "active";

    public PlayerConfigurationParser(String player) {
        this.player = player;
    }

    public PlayerConfiguration parsePlayerConfiguration(JsonElement je) {
        PlayerConfiguration playerconfiguration;
        block7: {
            if (je == null) {
                throw new JsonParseException("JSON object is null, player: " + this.player);
            }
            JsonObject jsonobject = (JsonObject)je;
            playerconfiguration = new PlayerConfiguration();
            JsonArray jsonarray = (JsonArray)jsonobject.get(CONFIG_ITEMS);
            if (jsonarray == null) break block7;
            int i2 = 0;
            while (i2 < jsonarray.size()) {
                block8: {
                    PlayerItemModel playeritemmodel;
                    block10: {
                        BufferedImage bufferedimage;
                        String s2;
                        JsonObject jsonobject1;
                        block9: {
                            jsonobject1 = (JsonObject)jsonarray.get(i2);
                            boolean flag = Json.getBoolean(jsonobject1, ITEM_ACTIVE, true);
                            if (!flag) break block8;
                            s2 = Json.getString(jsonobject1, ITEM_TYPE);
                            if (s2 != null) break block9;
                            Config.warn("Item type is null, player: " + this.player);
                            break block8;
                        }
                        String s1 = Json.getString(jsonobject1, "model");
                        if (s1 == null) {
                            s1 = "items/" + s2 + "/model.cfg";
                        }
                        if ((playeritemmodel = this.downloadModel(s1)) == null) break block8;
                        if (playeritemmodel.isUsePlayerTexture()) break block10;
                        String s22 = Json.getString(jsonobject1, "texture");
                        if (s22 == null) {
                            s22 = "items/" + s2 + "/users/" + this.player + ".png";
                        }
                        if ((bufferedimage = this.downloadTextureImage(s22)) == null) break block8;
                        playeritemmodel.setTextureImage(bufferedimage);
                        ResourceLocation resourcelocation = new ResourceLocation("optifine.net", s22);
                        playeritemmodel.setTextureLocation(resourcelocation);
                    }
                    playerconfiguration.addPlayerItemModel(playeritemmodel);
                }
                ++i2;
            }
        }
        return playerconfiguration;
    }

    private BufferedImage downloadTextureImage(String texturePath) {
        String s2 = String.valueOf(HttpUtils.getPlayerItemsUrl()) + "/" + texturePath;
        try {
            byte[] abyte = HttpPipeline.get(s2, Minecraft.getMinecraft().getProxy());
            BufferedImage bufferedimage = ImageIO.read(new ByteArrayInputStream(abyte));
            return bufferedimage;
        }
        catch (IOException ioexception) {
            Config.warn("Error loading item texture " + texturePath + ": " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private PlayerItemModel downloadModel(String modelPath) {
        String s2 = String.valueOf(HttpUtils.getPlayerItemsUrl()) + "/" + modelPath;
        try {
            byte[] abyte = HttpPipeline.get(s2, Minecraft.getMinecraft().getProxy());
            String s1 = new String(abyte, "ASCII");
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = (JsonObject)jsonparser.parse(s1);
            PlayerItemModel playeritemmodel = PlayerItemParser.parseItemModel(jsonobject);
            return playeritemmodel;
        }
        catch (Exception exception) {
            Config.warn("Error loading item model " + modelPath + ": " + exception.getClass().getName() + ": " + exception.getMessage());
            return null;
        }
    }
}

