/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.labymod.addon.LabyModOFAddon;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.api.LabyModAPI;
import net.labymod.api.LabyModAddon;
import net.labymod.main.Source;
import net.labymod.main.Updater;
import net.labymod.support.util.Debug;
import net.labymod.utils.ModUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class AddonLoader {
    private static final File addonsDirectory = new File("LabyMod/", "addons-" + Source.ABOUT_MC_VERSION.split("\\.")[0] + "." + Source.ABOUT_MC_VERSION.split("\\.")[1]);
    private static final File configDirectory = new File(addonsDirectory, "config");
    private static final File deleteQueueFile = new File(addonsDirectory, ".delete");
    private static List<String> transformerClasses = null;
    private static Map<UUID, String> mainClasses = new HashMap<UUID, String>();
    private static Map<UUID, String> names = new HashMap<UUID, String>();
    private static Map<UUID, File> files = new HashMap<UUID, File>();
    private static Map<UUID, JsonObject> loadedOffline = new HashMap<UUID, JsonObject>();
    private static final JsonParser parser = new JsonParser();
    private static Method addURL = null;
    private static List<LabyModAddon> addons = new ArrayList<LabyModAddon>();
    private static List<AddonInfo> offlineAddons = new ArrayList<AddonInfo>();
    private static List<String> disabledAddons;

    static {
        try {
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
        }
        catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        }
        AddonLoader.loadDisabledAddons();
    }

    public static List<String> getTransformerClasses(URLClassLoader classLoader) {
        File[] listFiles;
        if (transformerClasses != null) {
            return transformerClasses;
        }
        transformerClasses = new ArrayList<String>();
        if (!addonsDirectory.exists()) {
            if (!addonsDirectory.getParentFile().exists()) {
                addonsDirectory.getParentFile().mkdir();
            }
            addonsDirectory.mkdirs();
        }
        if (!configDirectory.exists()) {
            configDirectory.mkdir();
        }
        AddonLoader.checkFilesToDelete();
        File[] dirFiles = listFiles = addonsDirectory.listFiles();
        File[] fileArray = listFiles;
        int n2 = listFiles.length;
        int n3 = 0;
        while (n3 < n2) {
            block15: {
                File addonFile = fileArray[n3];
                if (addonFile.getName().endsWith(".jar")) {
                    block14: {
                        if (disabledAddons != null) {
                            try {
                                String md5 = AddonLoader.getCheckSum(addonFile);
                                Debug.log(Debug.EnumDebugMode.ADDON, "Checksum of " + addonFile.getName() + ": " + md5);
                                if (!disabledAddons.contains(md5) && !disabledAddons.contains(addonFile.getName())) break block14;
                                Debug.log(Debug.EnumDebugMode.ADDON, String.valueOf(addonFile.getName()) + " was blocked by LabyMod! Deleting now..");
                                addonFile.delete();
                                break block15;
                            }
                            catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                    AddonLoader.resolveJarFile(addonFile, classLoader);
                }
            }
            ++n3;
        }
        LabyModOFAddon.addOptifineVersion();
        if (System.getProperty("addonresources") != null) {
            String addonResources = System.getProperty("addonresources");
            Debug.log(Debug.EnumDebugMode.ADDON, "Given addon resources: " + addonResources);
            String[] stringArray = addonResources.split(",");
            int n4 = stringArray.length;
            n2 = 0;
            while (n2 < n4) {
                String resource = stringArray[n2];
                InputStream addonResource = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
                Debug.log(Debug.EnumDebugMode.ADDON, "Loading resource " + resource + ": " + (addonResource != null));
                if (addonResource != null) {
                    String addonResourceContent = AddonLoader.getStringByInputStream(addonResource);
                    try {
                        AddonLoader.loadByAddonInfo(null, null, addonResourceContent);
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                ++n2;
            }
        }
        return transformerClasses;
    }

    private static void checkFilesToDelete() {
        try {
            File file = AddonLoader.getDeleteQueueFile();
            if (!file.exists()) {
                return;
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) continue;
                File jarFile = new File(addonsDirectory, line);
                if (jarFile.exists() && jarFile.delete()) {
                    Debug.log(Debug.EnumDebugMode.ADDON, "Addon " + jarFile.getName() + " successfully deleted!");
                    continue;
                }
                Debug.log(Debug.EnumDebugMode.ADDON, "Error while deleting addon " + jarFile.getName());
            }
            scanner.close();
            file.delete();
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static void resolveJarFile(File addonFile, ClassLoader classLoader) {
        try {
            Debug.log(Debug.EnumDebugMode.ADDON, "Adding " + addonFile.getName() + " to runtime..");
            JarFile jarFile = new JarFile(addonFile);
            if (jarFile.getJarEntry("addon.json") == null) {
                Debug.log(Debug.EnumDebugMode.ADDON, "addon.json not found in " + addonFile.getName());
                jarFile.close();
                return;
            }
            JarEntry addonJsonFile = jarFile.getJarEntry("addon.json");
            String fileContent = ModUtils.getStringByInputStream(jarFile.getInputStream(addonJsonFile));
            addURL.invoke((Object)classLoader, addonFile.toURI().toURL());
            AddonLoader.loadByAddonInfo(jarFile, addonFile, fileContent);
            jarFile.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private static void loadByAddonInfo(JarFile jarFile, File addonFile, String content) throws IOException {
        JsonObject object = (JsonObject)parser.parse(content);
        String name = null;
        UUID uuid = null;
        if (!object.has("name")) {
            if (jarFile != null) {
                Debug.log(Debug.EnumDebugMode.ADDON, "Name not set in " + jarFile.getName());
                jarFile.close();
            }
            return;
        }
        name = object.get("name").getAsString();
        boolean offline = false;
        if (object.has("uuid")) {
            String requiredVersion;
            boolean notCompatible;
            String uuidString = object.get("uuid").getAsString();
            if (uuidString.equals("%uuid%") || addonFile != null && addonFile.getName().equals("debug.jar")) {
                uuid = UUID.randomUUID();
                offline = true;
            } else {
                try {
                    uuid = UUID.fromString(uuidString);
                }
                catch (IllegalArgumentException error) {
                    error.printStackTrace();
                    return;
                }
                if (object.has("offline")) {
                    offline = true;
                }
            }
            if (object.has("requiredVersion") && (notCompatible = Updater.isClientOutdated(Updater.CLIENT_VERSION, Updater.getShortVersionOfString(requiredVersion = object.get("requiredVersion").getAsString())))) {
                Debug.log(Debug.EnumDebugMode.ADDON, String.valueOf(name) + " addon requires LabyMod v" + requiredVersion + " but client is still on v" + "3.6.6" + "!");
                if (jarFile != null) {
                    jarFile.close();
                }
                return;
            }
            if (jarFile == null || offline) {
                loadedOffline.put(uuid, object);
            }
            if (object.has("transformerClass")) {
                String transformer = object.get("transformerClass").getAsString();
                transformerClasses.add(transformer);
                Debug.log(Debug.EnumDebugMode.ADDON, "Added transformer: " + transformer);
            }
            if (object.has("mainClass")) {
                String entryName = name == null ? addonFile.getName().substring(0, addonFile.getName().lastIndexOf(46)) : name;
                mainClasses.put(uuid, object.get("mainClass").getAsString());
                names.put(uuid, entryName);
                files.put(uuid, addonFile);
            } else {
                Debug.log(Debug.EnumDebugMode.ADDON, "Main class not found of " + name);
            }
            if (jarFile != null) {
                jarFile.close();
            }
            return;
        }
        if (jarFile != null) {
            Debug.log(Debug.EnumDebugMode.ADDON, "UUID not set in " + name);
            jarFile.close();
        }
    }

    public static void enableAddons(LabyModAPI labyModAPI) {
        if (mainClasses.size() == 0) {
            Debug.log(Debug.EnumDebugMode.ADDON, "No addons found!");
            return;
        }
        for (UUID mainClassEntry : mainClasses.keySet()) {
            AddonLoader.enableAddon(mainClassEntry, labyModAPI);
        }
        mainClasses.clear();
    }

    public static LabyModAddon enableAddon(UUID addonUUID, LabyModAPI labyModAPI) {
        Class<LabyModAddon> mainClass;
        String addonName;
        block5: {
            String mainClassName = mainClasses.get(addonUUID);
            addonName = names.get(addonUUID);
            if (mainClassName == null || addonName == null) {
                Debug.log(Debug.EnumDebugMode.ADDON, "Addon " + addonUUID + " is not resolved!");
                return null;
            }
            try {
                mainClass = Class.forName(mainClassName);
                if (!mainClass.isAssignableFrom(LabyModAddon.class)) break block5;
                Debug.log(Debug.EnumDebugMode.ADDON, "Main-class " + mainClassName + " of addon " + addonName + " isn't a valid addon-class!");
                return null;
            }
            catch (Throwable e2) {
                Debug.log(Debug.EnumDebugMode.ADDON, "Failed enabling addon " + addonName + ":");
                e2.printStackTrace();
                return null;
            }
        }
        LabyModAddon addon = (LabyModAddon)mainClass.newInstance();
        addon.api = labyModAPI;
        Debug.log(Debug.EnumDebugMode.ADDON, "Enabling and init addon " + addonName + "...");
        long curr = System.currentTimeMillis();
        addons.add(addon);
        addon.onEnable();
        addon.init(addonName, addonUUID);
        if (loadedOffline.containsKey(addonUUID)) {
            JsonObject jsonObject = loadedOffline.get(addonUUID);
            int version = jsonObject.has("version") ? jsonObject.get("version").getAsInt() : 1;
            String author = jsonObject.has("author") ? jsonObject.get("author").getAsString() : "Unknown";
            String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : "No description for this addon.";
            int category = jsonObject.has("category") ? jsonObject.get("category").getAsInt() : 1;
            String icon = jsonObject.has("icon") ? jsonObject.get("icon").getAsString() : null;
            AddonInfo addonInfo = new AddonInfo(addonUUID, addonName, version, author, description, category);
            addonInfo.setOfflineIcon(icon);
            offlineAddons.add(addonInfo);
        }
        Debug.log(Debug.EnumDebugMode.ADDON, "Successfull loaded addon " + addonName + "! (took " + (System.currentTimeMillis() - curr) + " ms)");
        return addon;
    }

    public static boolean hasInstalled(AddonInfo addonInfo) {
        for (LabyModAddon installedAddon : addons) {
            if (addonInfo == null || addonInfo.getUuid() == null || installedAddon == null || installedAddon.about == null || installedAddon.about.uuid == null || !addonInfo.getUuid().equals(installedAddon.about.uuid)) continue;
            return true;
        }
        return false;
    }

    public static LabyModAddon getInstalledAddonByInfo(AddonInfo addonInfo) {
        if (addonInfo != null) {
            for (LabyModAddon installedAddon : addons) {
                if (installedAddon == null || installedAddon.about == null || !addonInfo.getUuid().equals(installedAddon.about.uuid)) continue;
                return installedAddon;
            }
        }
        return null;
    }

    private static String getStringByInputStream(InputStream inputStream) {
        StringBuilder sb2 = new StringBuilder();
        try {
            int ch;
            while ((ch = inputStream.read()) != -1) {
                sb2.append((char)ch);
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return sb2.toString();
    }

    private static void loadDisabledAddons() {
        try {
            String line;
            URLConnection connection = new URL("http://dl.labymod.net/disabled_addons.json").openConnection();
            connection.setRequestProperty("User-Agent", Source.getUserAgent());
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String json = "";
            while ((line = bufferedReader.readLine()) != null) {
                json = String.valueOf(json) + (json.equals("") ? "" : "\n") + line;
            }
            if (json.isEmpty()) {
                return;
            }
            ArrayList<String> list = new ArrayList<String>();
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            if (array.size() == 0) {
                return;
            }
            Iterator<JsonElement> iterator = array.iterator();
            while (iterator.hasNext()) {
                String hash = iterator.next().getAsString();
                list.add(hash);
            }
            disabledAddons = list;
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static LabyModAddon getAddonByUUID(UUID uuid) {
        for (LabyModAddon addon : addons) {
            if (addon == null || addon.about == null || addon.about.uuid == null || !addon.about.uuid.equals(uuid)) continue;
            return addon;
        }
        return null;
    }

    public static UUID getUUIDByClass(Class<?> type) {
        File targetFile = AddonLoader.getJarFileByClass(type);
        if (targetFile == null) {
            return null;
        }
        String targetPath = targetFile.getAbsolutePath();
        for (Map.Entry<UUID, File> entry : files.entrySet()) {
            String path;
            if (entry.getValue() == null || !(path = entry.getValue().getAbsolutePath()).equals(targetPath)) continue;
            return entry.getKey();
        }
        return null;
    }

    public static File getJarFileByClass(Class<?> type) {
        URL resource = type.getResource(String.format("/%s.class", type.getName().replace(".", "/")));
        if (resource == null) {
            return null;
        }
        String uri = resource.toString();
        if (!uri.startsWith("jar:file:")) {
            return null;
        }
        int idx = uri.indexOf(33);
        if (idx == -1) {
            return null;
        }
        try {
            String fileName = URLDecoder.decode(uri.substring("jar:file:".length(), idx), Charset.defaultCharset().name());
            return new File(fileName);
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private static String getCheckSum(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fis);
        fis.close();
        return md5;
    }

    public static File getAddonsDirectory() {
        return addonsDirectory;
    }

    public static File getConfigDirectory() {
        return configDirectory;
    }

    public static File getDeleteQueueFile() {
        return deleteQueueFile;
    }

    public static Map<UUID, File> getFiles() {
        return files;
    }

    public static List<LabyModAddon> getAddons() {
        return addons;
    }

    public static List<AddonInfo> getOfflineAddons() {
        return offlineAddons;
    }
}

