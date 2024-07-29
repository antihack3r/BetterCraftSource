/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import net.labymod.main.Source;
import net.labymod.support.report.ReportArguments;
import net.labymod.support.util.Debug;

public class Updater {
    public static final short[] CLIENT_VERSION = Updater.getShortVersionOfString("3.6.6");
    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final Gson GSON = new Gson();
    private String latestVersionString = "Unknown";
    private short[] latestVersion = null;

    public boolean isUpdateAvailable() {
        return this.latestVersion != null && Updater.isClientOutdated(CLIENT_VERSION, this.latestVersion);
    }

    public void checkUpdate() throws Exception {
        String line;
        URLConnection connection = new URL("http://dl.labymod.net/versions.json").openConnection();
        connection.setRequestProperty("User-Agent", Source.getUserAgent());
        connection.connect();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String latestVersionsContents = "";
        while ((line = bufferedReader.readLine()) != null) {
            latestVersionsContents = String.valueOf(latestVersionsContents) + (latestVersionsContents.equals("") ? "" : "\n") + line;
        }
        if (latestVersionsContents.isEmpty()) {
            return;
        }
        HashMap<String, String> mcVerToLMVerMap = new HashMap<String, String>();
        JsonObject jsonObject = JSON_PARSER.parse(latestVersionsContents).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String mcVersion = entry.getKey();
            JsonObject object = entry.getValue().getAsJsonObject();
            JsonElement versionElement = object.get("version");
            if (versionElement == null || mcVersion == null) continue;
            mcVerToLMVerMap.put(mcVersion, versionElement.getAsString());
        }
        String newVersionString = (String)mcVerToLMVerMap.get(Source.ABOUT_MC_VERSION);
        if (newVersionString != null) {
            this.latestVersionString = newVersionString;
            this.latestVersion = Updater.getShortVersionOfString(newVersionString);
            boolean updateAvailable = this.isUpdateAvailable();
            this.printInfo(updateAvailable);
            if (updateAvailable) {
                this.downloadUpdaterFile();
                this.addUpdaterHook();
            }
        } else {
            Debug.log(Debug.EnumDebugMode.UPDATER, "Minecraft " + Source.ABOUT_MC_VERSION + " has no valid version entry");
        }
    }

    public void addUpdaterHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){

            @Override
            public void run() {
                Debug.log(Debug.EnumDebugMode.UPDATER, "Executing LabyMod Updater");
                Updater.this.executeUpdater();
                Debug.log(Debug.EnumDebugMode.UPDATER, "Updating complete");
            }
        }));
    }

    public void executeReport(ReportArguments reportArguments) {
        String json = GSON.toJson(reportArguments);
        this.execute(true, json);
    }

    public void executeUpdater() {
        this.execute(false, null);
    }

    public void execute(boolean report, String json) {
        try {
            ArrayList<String> arguments = new ArrayList<String>();
            arguments.add("java");
            arguments.add("-jar");
            arguments.add(new File("LabyMod/Updater.jar").getAbsolutePath());
            if (report) {
                arguments.add("report");
                arguments.add(Base64.getEncoder().encodeToString(json.getBytes()));
            }
            ProcessBuilder pb2 = new ProcessBuilder(arguments);
            pb2.start();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void printInfo(boolean updateAvailable) {
        Debug.log(Debug.EnumDebugMode.UPDATER, "The latest LabyMod version is v" + this.latestVersionString + ", you are currently using LabyMod version v" + "3.6.6");
        if (updateAvailable) {
            Debug.log(Debug.EnumDebugMode.UPDATER, "You are outdated!");
        } else {
            Debug.log(Debug.EnumDebugMode.UPDATER, "You are using the latest version.");
        }
    }

    public void downloadUpdaterFile() throws Exception {
        FileOutputStream fos = new FileOutputStream("LabyMod/Updater.jar");
        HttpURLConnection web = (HttpURLConnection)new URL("http://dl.labymod.net/latest/install/updater.jar").openConnection();
        web.setRequestProperty("User-Agent", Source.getUserAgent());
        ReadableByteChannel readableByteChannel = Channels.newChannel(web.getInputStream());
        fos.getChannel().transferFrom(readableByteChannel, 0L, Long.MAX_VALUE);
        fos.close();
        Debug.log(Debug.EnumDebugMode.UPDATER, "Downloaded latest Updater http://dl.labymod.net/latest/install/updater.jar");
    }

    public static short[] getShortVersionOfString(String versionString) {
        if (!versionString.contains(".")) {
            return new short[0];
        }
        String[] versionParts = versionString.split("\\.");
        short[] shortArray = new short[versionParts.length];
        int slot = 0;
        String[] stringArray = versionParts;
        int n2 = versionParts.length;
        int n3 = 0;
        while (n3 < n2) {
            String part = stringArray[n3];
            shortArray[slot] = Short.valueOf(part);
            ++slot;
            ++n3;
        }
        return shortArray;
    }

    public static boolean isClientOutdated(short[] clientVersion, short[] latestVersion) {
        short latestVersionMajor = latestVersion[0];
        short latestVersionMinor = latestVersion.length > 1 ? latestVersion[1] : (short)0;
        short latestVersionPatch = latestVersion.length > 2 ? latestVersion[2] : (short)0;
        short clientVersionMajor = clientVersion[0];
        short clientVersionMinor = clientVersion[1];
        short clientVersionPatch = clientVersion[2];
        return latestVersionMajor > clientVersionMajor || latestVersionMajor == clientVersionMajor && latestVersionMinor > clientVersionMinor || latestVersionMajor == clientVersionMajor && latestVersionMinor == clientVersionMinor && latestVersionPatch > clientVersionPatch;
    }

    public String getLatestVersionString() {
        return this.latestVersionString;
    }

    public short[] getLatestVersion() {
        return this.latestVersion;
    }
}

