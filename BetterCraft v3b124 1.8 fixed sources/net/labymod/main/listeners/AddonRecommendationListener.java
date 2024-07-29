/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.online.AddonInfoManager;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.gui.GuiAddonRecommendation;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.utils.ModUtils;
import net.minecraft.client.Minecraft;

public class AddonRecommendationListener
implements ServerMessageEvent,
MessageSendEvent {
    private LabyMod labymod;

    public AddonRecommendationListener(LabyMod labymod) {
        this.labymod = labymod;
    }

    @Override
    public void onServerMessage(String messageKey, JsonElement serverMessage) {
        if (messageKey.equals("addon_recommendation")) {
            ArrayList<GuiAddonRecommendation.RecommendedAddon> addons = new ArrayList<GuiAddonRecommendation.RecommendedAddon>();
            JsonObject obj = serverMessage.getAsJsonObject();
            if (obj.has("addons")) {
                JsonArray array = obj.get("addons").getAsJsonArray();
                int i2 = 0;
                while (i2 < array.size()) {
                    JsonObject addonObject = array.get(i2).getAsJsonObject();
                    UUID uuid = UUID.fromString(addonObject.get("uuid").getAsString());
                    boolean required = addonObject.get("required").getAsBoolean();
                    GuiAddonRecommendation.RecommendedAddon addon = new GuiAddonRecommendation.RecommendedAddon(uuid, required, null);
                    addons.add(addon);
                    ++i2;
                }
            }
            if (!addons.isEmpty()) {
                List<String> list;
                if (this.labymod.getCurrentServerData() != null && (list = Arrays.asList(LabyMod.getSettings().ignoredAddonRecommendationServers)).contains(ModUtils.getProfileNameByIp(this.labymod.getCurrentServerData().getIp()))) {
                    AddonRecommendationListener.sendMissingStatus(addons, true);
                    return;
                }
                this.openGui(addons);
            }
        }
    }

    private void openGui(final List<GuiAddonRecommendation.RecommendedAddon> addons) {
        AddonInfoManager manager = AddonInfoManager.getInstance();
        manager.init();
        if (AddonRecommendationListener.sendMissingStatus(addons, false)) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiAddonRecommendation(addons));
                }
            });
        }
    }

    public static boolean sendMissingStatus(List<GuiAddonRecommendation.RecommendedAddon> addons, boolean isGuiClosed) {
        AddonInfoManager manager = AddonInfoManager.getInstance();
        JsonArray missingArray = new JsonArray();
        boolean addonIsMissing = false;
        for (GuiAddonRecommendation.RecommendedAddon addon : addons) {
            addon.bindAddon(manager);
            if (addon.getOnlineAddonInfo() == null || AddonLoader.hasInstalled(addon.getOnlineAddonInfo())) continue;
            addonIsMissing = true;
            JsonObject addonObject = new JsonObject();
            addonObject.addProperty("uuid", addon.getUuid().toString());
            addonObject.addProperty("name", addon.getOnlineAddonInfo().getName());
            missingArray.add(addonObject);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gui_closed", isGuiClosed);
        jsonObject.addProperty("all_installed", !addonIsMissing);
        if (addonIsMissing) {
            jsonObject.add("missing", missingArray);
        }
        LabyMod.getInstance().getLabyModAPI().sendJsonMessageToServer("addon_recommendation", jsonObject);
        return addonIsMissing;
    }

    @Override
    public boolean onSend(String msg) {
        if (Debug.isActive() && msg.startsWith("/recommend")) {
            JsonObject object = new JsonObject();
            JsonArray addons = new JsonArray();
            JsonObject addon = new JsonObject();
            JsonObject addon2 = new JsonObject();
            new Thread(() -> {
                try {
                    Thread.sleep(10L);
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        addon.addProperty("uuid", "2485081f-b50a-47b8-82ed-6aed3952f8ab");
                        addon.addProperty("required", false);
                        addons.add(addon);
                        addon2.addProperty("uuid", "3bd2a3c0-0309-47d4-a6f9-320b0e93bce1");
                        addon2.addProperty("required", true);
                        addons.add(addon2);
                        object.add("addons", addons);
                        this.onServerMessage("addon_recommendation", object);
                    });
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }).start();
            return true;
        }
        return false;
    }
}

