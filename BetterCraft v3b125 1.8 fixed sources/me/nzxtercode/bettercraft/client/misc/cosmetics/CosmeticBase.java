/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticInstance;
import me.nzxtercode.bettercraft.client.misc.irc.IRC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public abstract class CosmeticBase
implements LayerRenderer<AbstractClientPlayer> {
    private boolean isEnabled;
    protected final RenderPlayer playerRenderer;

    public CosmeticBase(RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (player.hasPlayerInfo() && !player.isInvisible() && IRC.getInstance().isUserConnected(player.getGameProfile().getName())) {
            if (player == Minecraft.getMinecraft().thePlayer && this.isEnabled()) {
                this.render(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                return;
            }
            if (CosmeticInstance.USER_COSMETICS.containsKey(player.getGameProfile().getName()) && CosmeticInstance.USER_COSMETICS.get(player.getGameProfile().getName()).stream().anyMatch(cos -> this.getId() == cos.getId())) {
                this.render(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }
        }
    }

    public abstract int getId();

    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    private void saveEnabledState() {
        String CosmeticName;
        JsonObject json = this.getJson();
        JsonObject CosmeticJson = json.has(CosmeticName = this.getClass().getSimpleName()) ? json.get(CosmeticName).getAsJsonObject() : new JsonObject();
        CosmeticJson.add("enabled", new JsonPrimitive(this.isEnabled));
        Config.getInstance();
        Config.getInstance();
        Config.write(new File(Config.ROOT_DIR, "cosmetics.json"), json);
    }

    public void setEnabledState(boolean state) {
        this.isEnabled = state;
        this.saveEnabledState();
    }

    public boolean isEnabled() {
        return this.getCosmeticJson().has("enabled") ? this.getCosmeticJson().get("enabled").getAsBoolean() : true;
    }

    private final JsonObject getJson() {
        Config.getInstance();
        File file = new File(Config.ROOT_DIR, "cosmetics.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Config.getInstance();
        JsonElement jsonElement = Config.read(file);
        return jsonElement.isJsonNull() ? new JsonObject() : jsonElement.getAsJsonObject();
    }

    private JsonObject getCosmeticJson() {
        String CosmeticName;
        JsonObject CosmeticJson;
        JsonObject json = this.getJson();
        JsonObject jsonObject = CosmeticJson = json.has(CosmeticName = this.getClass().getSimpleName()) ? json.get(CosmeticName).getAsJsonObject() : new JsonObject();
        if (!json.has(CosmeticName)) {
            json.add(CosmeticName, CosmeticJson);
            Config.getInstance();
            Config.getInstance();
            Config.write(new File(Config.ROOT_DIR, "cosmetics.json"), json);
        }
        return CosmeticJson;
    }
}

