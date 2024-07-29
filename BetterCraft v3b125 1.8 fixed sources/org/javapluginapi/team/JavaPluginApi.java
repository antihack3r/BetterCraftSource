/*
 * Decompiled with CFR 0.152.
 */
package org.javapluginapi.team;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;
import me.nzxtercode.bettercraft.client.BetterCraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javapluginapi.team.PluginLoader;
import org.javapluginapi.team.api.Plugin;

public class JavaPluginApi {
    private static final Logger logger = LogManager.getLogger();
    private static JavaPluginApi api1 = JavaPluginApi.getApi(pluginloader -> {
        PluginLoader pluginLoader = new PluginLoader();
    });
    public static PluginLoader loader;
    public static String outName;
    private static final File PLUGIN_DIR;

    static {
        outName = "[Plugin] ";
        BetterCraft.getInstance();
        PLUGIN_DIR = new File(BetterCraft.clientName, "plugins");
    }

    public static JavaPluginApi getApi(Consumer<PluginLoader> consumer) {
        loader = new PluginLoader();
        consumer.accept(loader);
        return api1 == null ? new JavaPluginApi() : api1;
    }

    public static final JavaPluginApi getInstance() {
        return api1;
    }

    public static void init() {
        if (!PLUGIN_DIR.exists()) {
            PLUGIN_DIR.mkdir();
        }
        api1.loadAll(PLUGIN_DIR);
    }

    public static void stopPlugin() {
        api1.unloadAll(PLUGIN_DIR);
    }

    public File[] loadAll(File pluginFolder) {
        File[] fileArray = Objects.requireNonNull(pluginFolder.listFiles());
        int n2 = fileArray.length;
        int n3 = 0;
        while (n3 < n2) {
            File fileIndex = fileArray[n3];
            if (fileIndex.getName().endsWith(".jar")) {
                loader.load(fileIndex);
                logger.info(String.valueOf(outName) + "Load (Datei: " + fileIndex.getName() + " Name: " + Plugin.descriptionFile.get().getName() + " Author: " + Plugin.descriptionFile.get().getAuthor() + " Version: " + Plugin.descriptionFile.get().getVersion() + ")");
            }
            ++n3;
        }
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(outName));
        JavaPluginApi.getInstance();
        logger.info(stringBuilder.append(JavaPluginApi.loader.map.size()).append(" loaded").toString());
        return pluginFolder.listFiles();
    }

    public File[] unloadAll(File pluginFolder) {
        File[] fileArray = Objects.requireNonNull(pluginFolder.listFiles());
        int n2 = fileArray.length;
        int n3 = 0;
        while (n3 < n2) {
            File fileIndex = fileArray[n3];
            loader.unload(fileIndex);
            logger.info(String.valueOf(outName) + "Unload (Datei: " + fileIndex.getName() + " Name: " + Plugin.descriptionFile.get().getName() + " Author: " + Plugin.descriptionFile.get().getAuthor() + " Version: " + Plugin.descriptionFile.get().getVersion() + ")");
            ++n3;
        }
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(outName));
        JavaPluginApi.getInstance();
        logger.info(stringBuilder.append(JavaPluginApi.loader.map.size()).append(" unloaded").toString());
        return pluginFolder.listFiles();
    }

    public File[] reloadAll(File pluginFolder) {
        File[] fileArray = Objects.requireNonNull(pluginFolder.listFiles());
        int n2 = fileArray.length;
        int n3 = 0;
        while (n3 < n2) {
            File fileIndex = fileArray[n3];
            loader.reload(fileIndex);
            logger.info(String.valueOf(outName) + "Reload (Datei: " + fileIndex.getName() + " Name: " + Plugin.descriptionFile.get().getName() + " Author: " + Plugin.descriptionFile.get().getAuthor() + " Version: " + Plugin.descriptionFile.get().getVersion() + ")");
            ++n3;
        }
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(outName));
        JavaPluginApi.getInstance();
        logger.info(stringBuilder.append(JavaPluginApi.loader.map.size()).append(" reloaded").toString());
        return pluginFolder.listFiles();
    }
}

