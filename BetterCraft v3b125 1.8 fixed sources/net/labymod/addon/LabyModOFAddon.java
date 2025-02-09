/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarFile;
import net.labymod.addon.About;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.main.Source;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.support.util.Debug;

public class LabyModOFAddon
extends LabyModAddon {
    public static final Map<String, UUID> OPTIFINE_VERSIONS = new HashMap<String, UUID>(){
        {
            this.put("1.8.9", UUID.fromString("2cc09032-995f-4b57-a2a1-f1399addbb21"));
            this.put("1.12.2", UUID.fromString("7d62bffd-fe3f-4667-8200-e8decb384fa0"));
        }
    };
    private static final File FILE_OF_HANDLER = new File("LabyMod/ofhandler/", "ofhandler.jar");
    private static final JsonParser parser = new JsonParser();
    private static File OPTIFINE_FORGE = null;
    public static boolean INSTALL = false;
    public static String INSTALLED_VERSION = null;

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void init(String addonName, UUID uuid) {
        this.about = new About(uuid, addonName);
        this.about.loaded = true;
        INSTALLED_VERSION = addonName == null ? null : addonName.replaceAll("_", "");
    }

    public static void downloadOFHandler() {
        if (!FILE_OF_HANDLER.exists()) {
            new Thread(new Runnable(){

                @Override
                public void run() {
                    try {
                        FILE_OF_HANDLER.getParentFile().mkdir();
                        FileOutputStream fos = new FileOutputStream(FILE_OF_HANDLER);
                        HttpURLConnection web = (HttpURLConnection)new URL("http://dl.labymod.net/latest/install/ofhandler.jar").openConnection();
                        web.setRequestProperty("User-Agent", Source.getUserAgent());
                        ReadableByteChannel readableByteChannel = Channels.newChannel(web.getInputStream());
                        fos.getChannel().transferFrom(readableByteChannel, 0L, Long.MAX_VALUE);
                        fos.close();
                    }
                    catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void executeOfHandler(boolean installOptifine) {
        boolean isVanillaForge = !LabyModCoreMod.isForge();
        try {
            Debug.log(Debug.EnumDebugMode.ADDON, installOptifine ? (isVanillaForge ? "Installing optifine for vanillaforge" : "Uninstall optifine for vanillaforge") : (isVanillaForge ? "Installing optifine for forge" : "Uninstall optifine for forge"));
            if (isVanillaForge) {
                if (!FILE_OF_HANDLER.exists()) {
                    Debug.log(Debug.EnumDebugMode.ADDON, "OfHandler not found:" + FILE_OF_HANDLER.getAbsolutePath());
                    return;
                }
                if (Source.RUNNING_JAR == null) {
                    Debug.log(Debug.EnumDebugMode.ADDON, "Can't execute ofhandler: Running jar not found!");
                    return;
                }
                if (!Source.RUNNING_JAR.getName().endsWith(".jar")) {
                    Debug.log(Debug.EnumDebugMode.ADDON, "Can't execute ofhandler: " + Source.RUNNING_JAR.getAbsolutePath() + " is not a jar file!");
                    return;
                }
                String argument = "java -jar " + FILE_OF_HANDLER.getAbsolutePath() + " " + installOptifine + " " + Source.ABOUT_MC_VERSION + " " + Source.RUNNING_JAR.getAbsolutePath();
                Debug.log(Debug.EnumDebugMode.ADDON, "Execute ofhandler: " + argument);
                Runtime.getRuntime().exec(argument);
                Debug.log(Debug.EnumDebugMode.ADDON, "OfHandler executed!");
            } else if (installOptifine) {
                File optifineMods = new File("mods/", "optifine.jar");
                File optifineTemp = new File("LabyMod/ofhandler/", "optifine.jar");
                Debug.log(Debug.EnumDebugMode.ADDON, "Copy " + optifineTemp.getAbsolutePath() + " to " + optifineMods.getAbsolutePath());
                if (optifineTemp.exists()) {
                    optifineTemp.renameTo(optifineMods);
                }
                new File("LabyMod/ofhandler/").delete();
            } else if (OPTIFINE_FORGE != null) {
                if (!FILE_OF_HANDLER.exists()) {
                    Debug.log(Debug.EnumDebugMode.ADDON, "OfHandler not found:" + FILE_OF_HANDLER.getAbsolutePath());
                    return;
                }
                String argument = "java -jar " + FILE_OF_HANDLER.getAbsolutePath() + " del " + OPTIFINE_FORGE.getAbsolutePath();
                Debug.log(Debug.EnumDebugMode.ADDON, "Execute ofhandler: " + argument);
                Runtime.getRuntime().exec(argument);
                Debug.log(Debug.EnumDebugMode.ADDON, "OfHandler executed!");
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void addOptifineVersion() {
        if (FILE_OF_HANDLER.getParentFile().exists()) {
            FILE_OF_HANDLER.getParentFile().delete();
        }
        if (LabyModCoreMod.isForge()) {
            File modsFolder = new File("mods/");
            if (!modsFolder.exists()) {
                return;
            }
            File[] fileArray = modsFolder.listFiles();
            int n2 = fileArray.length;
            int n3 = 0;
            while (n3 < n2) {
                File mod = fileArray[n3];
                if (mod.getName().endsWith(".jar")) {
                    try {
                        JarFile jarFile = new JarFile(mod);
                        if (jarFile.getJarEntry("changelog.txt") == null || jarFile.getJarEntry("buildof.txt") == null) {
                            jarFile.close();
                        } else {
                            LabyModOFAddon.addOptifineUsingJarFile(jarFile);
                            OPTIFINE_FORGE = mod;
                        }
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                ++n3;
            }
        }
    }

    public static void addOptifineByVanillaForge(String version) {
        if (FILE_OF_HANDLER.getParentFile().exists()) {
            FILE_OF_HANDLER.getParentFile().delete();
        }
        try {
            Debug.log(Debug.EnumDebugMode.ADDON, "Detected game version: " + version);
            if (version == null || version.isEmpty()) {
                Debug.log(Debug.EnumDebugMode.ADDON, "Can't check installed optifine version: version is not present!");
                return;
            }
            File runningJar = new File("versions/" + version + "/" + version + ".jar");
            if (!runningJar.exists()) {
                Debug.log(Debug.EnumDebugMode.ADDON, "Can't check installed optifine version: running jar file " + runningJar.getAbsolutePath() + " doesn't exists!");
                return;
            }
            Source.RUNNING_JAR = runningJar;
            JarFile jarFile = new JarFile(runningJar);
            if (jarFile.getJarEntry("changelog.txt") == null || jarFile.getJarEntry("buildof.txt") == null) {
                Debug.log(Debug.EnumDebugMode.ADDON, "Optifine is not installed");
                jarFile.close();
                return;
            }
            LabyModOFAddon.addOptifineUsingJarFile(jarFile);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void addOptifineUsingJarFile(JarFile jarFile) throws Exception {
        String fileContent = null;
        String changeLogContents = LabyModOFAddon.getStringByInputStream(jarFile.getInputStream(jarFile.getJarEntry("changelog.txt")));
        changeLogContents = changeLogContents.replace("\r", "");
        String[] stringArray = changeLogContents.split("\n");
        int n2 = stringArray.length;
        int n3 = 0;
        while (n3 < n2) {
            String version;
            String line = stringArray[n3];
            if (line.startsWith("OptiFine ") && (version = line.split("OptiFine ")[1].split("_")[0]) != null && !version.isEmpty() && OPTIFINE_VERSIONS.containsKey(version)) {
                fileContent = "{\"uuid\":\"" + OPTIFINE_VERSIONS.get(version) + "\",\"name\":\"" + line + "\"}";
                break;
            }
            ++n3;
        }
        jarFile.close();
        if (fileContent == null) {
            return;
        }
        JsonObject object = (JsonObject)parser.parse(fileContent);
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
        if (object.has("uuid")) {
            uuid = UUID.fromString(object.get("uuid").getAsString());
            Debug.log(Debug.EnumDebugMode.ADDON, "Optifine is installed!");
            LabyModOFAddon ofAddon = new LabyModOFAddon();
            ofAddon.init(name, uuid);
            AddonLoader.getAddons().add(ofAddon);
            return;
        }
        if (jarFile != null) {
            Debug.log(Debug.EnumDebugMode.ADDON, "UUID not set in " + name);
            jarFile.close();
        }
    }

    public static String getStringByInputStream(InputStream inputStream) {
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

    @Override
    public void loadConfig() {
    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
    }
}

