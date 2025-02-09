/*
 * Decompiled with CFR 0.152.
 */
package org.javapluginapi.team;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.javapluginapi.team.PluginLogger;
import org.javapluginapi.team.api.Plugin;
import org.javapluginapi.team.api.PluginDescription;
import org.javapluginapi.team.api.PluginException;

public class PluginLoader {
    private final PluginLogger logger = new PluginLogger();
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    public final HashMap<File, Plugin> map = new HashMap();

    public Plugin load(File file) {
        if (!file.getName().endsWith(".jar")) {
            throw new PluginException("File have to be a Jar! " + file.getName());
        }
        try {
            if (this.map.containsKey(file)) {
                throw new PluginException(String.valueOf(file.getName()) + " " + "Plugin already loaded.");
            }
            PluginDescription pluginDescriptionFile = this.getPluginDescriptionFile(file);
            URLClassLoader loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
            Class<?> clazz = Class.forName(pluginDescriptionFile.getMain(), true, loader);
            Class<Plugin> instanceClass = clazz.asSubclass(Plugin.class);
            Constructor<Plugin> instanceClassConstructor = instanceClass.getConstructor(new Class[0]);
            Plugin plugin = instanceClassConstructor.newInstance(new Object[0]);
            plugin.setDescriptionFile(pluginDescriptionFile);
            this.map.put(file, plugin);
            plugin.onEnable();
            return plugin;
        }
        catch (MalformedURLException e2) {
            throw new PluginException("Failed to convert the file path to a URL.", e2);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e3) {
            throw new PluginException("Failed to create a new instance of the plugin.", e3);
        }
    }

    public Plugin loadFromURL(String url, File filePath) {
        InputStream in2 = null;
        try {
            in2 = new URL(url).openStream();
            Files.copy(in2, Paths.get(filePath.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        File file = Paths.get(filePath.getPath(), new String[0]).toFile();
        if (!file.getName().endsWith(".jar")) {
            throw new PluginException("File have to be a Jar! " + file.getName());
        }
        try {
            if (this.map.containsKey(file)) {
                throw new PluginException(String.valueOf(file.getName()) + " " + "Plugin already loaded.");
            }
            PluginDescription pluginDescriptionFile = this.getPluginDescriptionFile(file);
            URLClassLoader loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
            Class<?> clazz = Class.forName(pluginDescriptionFile.getMain(), true, loader);
            Class<Plugin> instanceClass = clazz.asSubclass(Plugin.class);
            Constructor<Plugin> instanceClassConstructor = instanceClass.getConstructor(new Class[0]);
            Plugin plugin = instanceClassConstructor.newInstance(new Object[0]);
            plugin.setDescriptionFile(pluginDescriptionFile);
            this.map.put(file, plugin);
            plugin.onEnable();
            return plugin;
        }
        catch (MalformedURLException e3) {
            throw new PluginException("Failed to convert the file path to a URL.", e3);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e4) {
            throw new PluginException("Failed to create a new instance of the plugin.", e4);
        }
    }

    public Plugin unload(File file) {
        if (!file.getName().endsWith(".jar")) {
            throw new PluginException("File have to be a Jar! " + file.getName());
        }
        if (!this.map.containsKey(file)) {
            throw new PluginException("Can't unload a Plugin that wasn't loaded in the first place.");
        }
        Plugin plugin = this.map.get(file);
        plugin.onDisable();
        this.map.remove(file);
        return plugin;
    }

    public void reload(File file) {
        this.unload(file);
        this.load(file);
    }

    public PluginDescription getPluginDescriptionFile(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            PluginDescription pluginJson = null;
            while (entries.hasMoreElements() && pluginJson == null) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().equals("plugin.json")) continue;
                InputStream stream = zipFile.getInputStream(entry);
                try {
                    pluginJson = GSON.fromJson((Reader)new InputStreamReader(stream), PluginDescription.class);
                }
                catch (JsonParseException jsonParseException) {
                    throw new PluginException("Failed to parse JSON:", jsonParseException);
                }
            }
            if (pluginJson == null) {
                zipFile.close();
                throw new PluginException("Failed to find plugin.json in the root of the jar.");
            }
            zipFile.close();
            return pluginJson;
        }
        catch (IOException e2) {
            throw new PluginException("Failed to open the jar as a zip:", e2);
        }
    }
}

