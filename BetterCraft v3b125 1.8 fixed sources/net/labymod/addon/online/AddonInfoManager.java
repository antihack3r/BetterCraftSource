/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addon.online;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.LabyModOFAddon;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.addon.online.info.OnlineAddonInfo;
import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.CheckBox;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.labymod.utils.JsonParse;

public class AddonInfoManager {
    private static AddonInfoManager instance;
    private Map<UUID, AddonInfo> addonInfoMap = new HashMap<UUID, AddonInfo>();
    private List<AddonInfo> addonInfoList = new ArrayList<AddonInfo>();
    private List<String> categories = new ArrayList<String>(Arrays.asList("All"));
    private CheckBox[] categorieCheckboxList = new CheckBox[this.categories.size()];
    private boolean loaded = false;
    private boolean calledInit = false;

    public static AddonInfoManager getInstance() {
        if (instance == null) {
            instance = new AddonInfoManager();
        }
        return instance;
    }

    public void init() {
        if (this.calledInit) {
            return;
        }
        this.calledInit = true;
        try {
            String line;
            HttpURLConnection connection = (HttpURLConnection)new URL("http://dl.labymod.net/addons.json").openConnection();
            connection.setRequestProperty("User-Agent", Source.getUserAgent());
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            HashMap<UUID, AddonInfo> addonInfoMap = new HashMap<UUID, AddonInfo>();
            ArrayList<AddonInfo> addonInfoList = new ArrayList<AddonInfo>();
            String addonContents = "";
            while ((line = bufferedReader.readLine()) != null) {
                addonContents = String.valueOf(addonContents) + (addonContents.equals("") ? "" : "\n") + line;
            }
            new JsonParse();
            JsonObject jsonObject = (JsonObject)JsonParse.parse(addonContents);
            JsonArray array = jsonObject.get("categories").getAsJsonArray();
            this.categorieCheckboxList = new CheckBox[array.size()];
            int i2 = 0;
            for (JsonElement element : array) {
                String displayName = element.getAsString();
                this.categories.add(displayName);
                this.categorieCheckboxList[i2] = new CheckBox(displayName, CheckBox.EnumCheckBoxValue.ENABLED, null, 0, 0, 15, 15);
                ++i2;
            }
            Set<Map.Entry<String, JsonElement>> addonsSet = jsonObject.get("addons").getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> entry : addonsSet) {
                if (!Source.getMajorVersion().equals(entry.getKey())) continue;
                JsonArray jsonArray = entry.getValue().getAsJsonArray();
                Iterator<JsonElement> addonIterator = jsonArray.iterator();
                while (addonIterator.hasNext()) {
                    JsonObject object = addonIterator.next().getAsJsonObject();
                    JsonElement addonUUID = object.get("uuid");
                    JsonElement addonName = object.get("name");
                    JsonElement addonVersion = object.get("version");
                    JsonElement addonHash = object.get("hash");
                    JsonElement addonAuthor = object.get("author");
                    JsonElement addonDescription = object.get("description");
                    JsonElement addonCategory = object.get("category");
                    JsonElement addonEnabled = object.get("enabled");
                    JsonElement addonIncludeInJar = object.get("includeInJar");
                    JsonElement addonRestart = object.get("restart");
                    JsonElement addonVerified = object.get("verified");
                    int[] sorting = new int[this.categories.size()];
                    if (object.has("sorting")) {
                        JsonArray addonSorting = object.get("sorting").getAsJsonArray();
                        sorting = new int[addonSorting.size()];
                        int index = 0;
                        for (JsonElement element2 : addonSorting) {
                            sorting[index] = element2.getAsInt();
                            ++index;
                        }
                    }
                    if (addonUUID == null || addonName == null || addonVersion == null || addonHash == null || addonAuthor == null || addonDescription == null || addonCategory == null || addonEnabled == null || addonIncludeInJar == null || addonRestart == null || addonVerified == null) continue;
                    OnlineAddonInfo addonInfo = new OnlineAddonInfo(UUID.fromString(addonUUID.getAsString()), addonName.getAsString(), addonVersion.getAsInt(), addonHash.getAsString(), addonAuthor.getAsString(), addonDescription.getAsString(), addonCategory.getAsInt(), addonEnabled.getAsBoolean(), addonIncludeInJar.getAsBoolean(), addonRestart.getAsBoolean(), addonVerified.getAsBoolean(), sorting);
                    UUID uuid = addonInfo.getUuid();
                    addonInfoMap.put(uuid, addonInfo);
                    addonInfoList.add(addonInfo);
                    if (LabyModOFAddon.INSTALLED_VERSION != null && LabyModOFAddon.OPTIFINE_VERSIONS.containsValue(uuid)) {
                        addonInfo.setName(LabyModOFAddon.INSTALLED_VERSION);
                    }
                    AddonLoader.getOfflineAddons().remove(uuid);
                    Debug.log(Debug.EnumDebugMode.ADDON, "Resolved addon " + addonInfo.getName() + " " + addonInfo.getVersion() + " by " + addonInfo.getAuthor());
                }
            }
            this.addonInfoMap = addonInfoMap;
            this.addonInfoList = addonInfoList;
            for (AddonInfo offlineAddon : AddonLoader.getOfflineAddons()) {
                this.addonInfoList.add(offlineAddon);
                this.addonInfoMap.put(offlineAddon.getUuid(), offlineAddon);
            }
            this.loaded = true;
        }
        catch (Exception e2) {
            Debug.log(Debug.EnumDebugMode.ADDON, "Failed getting available addons!");
            e2.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    boolean deleteOptifine = false;
                    boolean deleteAction = false;
                    for (LabyModAddon labyModAddon : AddonLoader.getAddons()) {
                        if (!labyModAddon.about.deleted) continue;
                        deleteAction = true;
                    }
                    if (deleteAction) {
                        File file = AddonLoader.getDeleteQueueFile();
                        FileWriter fileWriter = new FileWriter(file, true);
                        try {
                            Path path = file.toPath();
                            Files.setAttribute(path, "dos:hidden", true, new LinkOption[0]);
                        }
                        catch (Exception error) {
                            error.printStackTrace();
                        }
                        for (LabyModAddon labyModAddon2 : AddonLoader.getAddons()) {
                            if (!labyModAddon2.about.deleted) continue;
                            if (labyModAddon2 instanceof LabyModOFAddon) {
                                deleteOptifine = true;
                                continue;
                            }
                            File foundFile = AddonLoader.getFiles().get(labyModAddon2.about.uuid);
                            if (foundFile != null && foundFile.exists()) {
                                fileWriter.write(String.valueOf(foundFile.getName()) + "\n");
                                continue;
                            }
                            Debug.log(Debug.EnumDebugMode.ADDON, "Error while adding " + labyModAddon2.about.name + " to delete queue");
                        }
                        fileWriter.close();
                    }
                    if (deleteOptifine) {
                        LabyModOFAddon.executeOfHandler(false);
                    } else if (LabyModOFAddon.INSTALL) {
                        LabyModOFAddon.executeOfHandler(true);
                    }
                }
                catch (Exception error2) {
                    error2.printStackTrace();
                }
            }
        }));
    }

    public Map<UUID, AddonInfo> getAddonInfoMap() {
        return this.addonInfoMap;
    }

    public List<AddonInfo> getAddonInfoList() {
        return this.addonInfoList;
    }

    public List<String> getCategories() {
        return this.categories;
    }

    public CheckBox[] getCategorieCheckboxList() {
        return this.categorieCheckboxList;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}

