/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import me.nzxtercode.bettercraft.client.BetterCraft;

public class Config {
    private static final Config INSTANCE = new Config();
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    public static File ROOT_DIR;
    public static File ADDON_DIR;
    private static File configFile;
    private static File settingsFile;
    private static File miscFile;
    private static File musicFile;
    private static File cosmeticsFile;
    private static File modsFile;
    private static File backgroundFile;
    private static File colorFile;

    static {
        BetterCraft.getInstance();
        ROOT_DIR = new File(BetterCraft.clientName);
        BetterCraft.getInstance();
        ADDON_DIR = new File(BetterCraft.clientName, "Addons");
        BetterCraft.getInstance();
        configFile = new File(BetterCraft.clientName, "config.json");
        BetterCraft.getInstance();
        settingsFile = new File(BetterCraft.clientName, "settings.json");
        BetterCraft.getInstance();
        miscFile = new File(BetterCraft.clientName, "misc.json");
        BetterCraft.getInstance();
        musicFile = new File(BetterCraft.clientName, "music.json");
        BetterCraft.getInstance();
        cosmeticsFile = new File(BetterCraft.clientName, "cosmetics.json");
        BetterCraft.getInstance();
        modsFile = new File(BetterCraft.clientName, "mods.json");
        BetterCraft.getInstance();
        backgroundFile = new File(BetterCraft.clientName, "background.json");
        BetterCraft.getInstance();
        colorFile = new File(BetterCraft.clientName, "colors.json");
    }

    public static final Config getInstance() {
        return INSTANCE;
    }

    public static void init() {
        if (!ROOT_DIR.exists()) {
            ROOT_DIR.mkdirs();
        }
        if (!(Files.exists(Paths.get(configFile.getPath(), new String[0]), new LinkOption[0]) || Files.exists(Paths.get(settingsFile.getPath(), new String[0]), new LinkOption[0]) || Files.exists(Paths.get(miscFile.getPath(), new String[0]), new LinkOption[0]) || Files.exists(Paths.get(musicFile.getPath(), new String[0]), new LinkOption[0]) || Files.exists(Paths.get(cosmeticsFile.getPath(), new String[0]), new LinkOption[0]) || Files.exists(Paths.get(modsFile.getPath(), new String[0]), new LinkOption[0]) || Files.exists(Paths.get(backgroundFile.getPath(), new String[0]), new LinkOption[0]) || Files.exists(Paths.get(colorFile.getPath(), new String[0]), new LinkOption[0]))) {
            try {
                Files.copy(Config.class.getResourceAsStream("/assets/minecraft/client/jsons/config.json"), Paths.get(configFile.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Config.class.getResourceAsStream("/assets/minecraft/client/jsons/settings.json"), Paths.get(settingsFile.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Config.class.getResourceAsStream("/assets/minecraft/client/jsons/misc.json"), Paths.get(miscFile.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Config.class.getResourceAsStream("/assets/minecraft/client/jsons/music.json"), Paths.get(musicFile.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Config.class.getResourceAsStream("/assets/minecraft/client/jsons/cosmetics.json"), Paths.get(cosmeticsFile.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Config.class.getResourceAsStream("/assets/minecraft/client/jsons/mods.json"), Paths.get(modsFile.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Config.class.getResourceAsStream("/assets/minecraft/client/jsons/background.json"), Paths.get(backgroundFile.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Config.class.getResourceAsStream("/assets/minecraft/client/jsons/colors.json"), Paths.get(colorFile.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public void editConfig(String name, Consumer<JsonObject> consumer) {
        JsonObject json = Config.read(configFile).getAsJsonObject();
        JsonObject jsonObject = json.has(name) ? json.get(name).getAsJsonObject() : new JsonObject();
        consumer.accept(jsonObject);
        json.add(name, jsonObject);
        Config.write(configFile, json);
    }

    public final JsonObject getConfig(String name) {
        JsonObject json = Config.read(configFile).getAsJsonObject();
        return json.get(name).getAsJsonObject();
    }

    public void editMisc(String name, Consumer<JsonObject> consumer) {
        JsonObject json = Config.read(miscFile).getAsJsonObject();
        JsonObject jsonObject = json.has(name) ? json.get(name).getAsJsonObject() : new JsonObject();
        consumer.accept(jsonObject);
        json.add(name, jsonObject);
        Config.write(miscFile, json);
    }

    public final JsonObject getMisc(String name) {
        JsonObject json = Config.read(miscFile).getAsJsonObject();
        return json.get(name).getAsJsonObject();
    }

    public void editSettings(String name, Consumer<JsonObject> consumer) {
        JsonObject json = Config.read(settingsFile).getAsJsonObject();
        JsonObject jsonObject = json.has(name) ? json.get(name).getAsJsonObject() : new JsonObject();
        consumer.accept(jsonObject);
        json.add(name, jsonObject);
        Config.write(settingsFile, json);
    }

    public final JsonObject getSettings(String name) {
        JsonObject json = Config.read(settingsFile).getAsJsonObject();
        return json.get(name).getAsJsonObject();
    }

    public void editMusic(String name, Consumer<JsonObject> consumer) {
        JsonObject json = Config.read(musicFile).getAsJsonObject();
        JsonObject jsonObject = json.has(name) ? json.get(name).getAsJsonObject() : new JsonObject();
        consumer.accept(jsonObject);
        json.add(name, jsonObject);
        Config.write(musicFile, json);
    }

    public final JsonObject getMusic(String name) {
        JsonObject json = Config.read(musicFile).getAsJsonObject();
        return json.get(name).getAsJsonObject();
    }

    public void editBackground(String name, Consumer<JsonObject> consumer) {
        JsonObject json = Config.read(backgroundFile).getAsJsonObject();
        JsonObject jsonObject = json.has(name) ? json.get(name).getAsJsonObject() : new JsonObject();
        consumer.accept(jsonObject);
        json.add(name, jsonObject);
        Config.write(backgroundFile, json);
    }

    public final JsonObject getBackground(String name) {
        JsonObject json = Config.read(backgroundFile).getAsJsonObject();
        return json.get(name).getAsJsonObject();
    }

    public void editColor(String name, Consumer<JsonObject> consumer) {
        JsonObject json = Config.read(colorFile).getAsJsonObject();
        JsonObject jsonObject = json.has(name) ? json.get(name).getAsJsonObject() : new JsonObject();
        consumer.accept(jsonObject);
        json.add(name, jsonObject);
        Config.write(colorFile, json);
    }

    public final JsonObject getColor(String name) {
        JsonObject json = Config.read(colorFile).getAsJsonObject();
        return json.get(name).getAsJsonObject();
    }

    public static final JsonElement read(File file) {
        try {
            FileReader reader = new FileReader(file);
            JsonElement jsonElement = new JsonParser().parse(reader);
            ((Reader)reader).close();
            return jsonElement;
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public static final void write(File file, JsonElement jsonObject) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(GSON_BUILDER.setPrettyPrinting().create().toJson(jsonObject).toString());
            ((Writer)writer).close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

