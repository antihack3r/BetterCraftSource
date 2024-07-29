/*
 * Decompiled with CFR 0.152.
 */
package wdl.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import net.minecraft.client.Minecraft;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.update.Release;

public class GithubInfoGrabber {
    private static final String USER_AGENT;
    private static final JsonParser PARSER;
    private static final String RELEASE_LIST_LOCATION = "https://api.github.com/repos/Pokechu22/WorldDownloader/releases?per_page=100";
    private static final File CACHED_RELEASES_FILE;

    static {
        PARSER = new JsonParser();
        CACHED_RELEASES_FILE = new File(Minecraft.getMinecraft().mcDataDir, "WorldDownloader_Update_Cache.json");
        String mcVersion = WDL.getMinecraftVersionInfo();
        String wdlVersion = "1.8.9a-beta2";
        USER_AGENT = String.format("WorldDownloader mod by Pokechu22 (Minecraft %s; WDL %s) ", mcVersion, wdlVersion);
    }

    public static List<Release> getReleases() throws Exception {
        JsonArray array = GithubInfoGrabber.query(RELEASE_LIST_LOCATION).getAsJsonArray();
        ArrayList<Release> returned = new ArrayList<Release>();
        for (JsonElement element : array) {
            returned.add(new Release(element.getAsJsonObject()));
        }
        return returned;
    }

    public static JsonElement query(String path) throws Exception {
        try (InputStream stream = null;){
            JsonElement jsonElement;
            block24: {
                String etag;
                HttpsURLConnection connection = (HttpsURLConnection)new URL(path).openConnection();
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setRequestProperty("Accept", "application/vnd.github.v3.full+json");
                if (WDL.globalProps.getProperty("UpdateETag") != null && !(etag = WDL.globalProps.getProperty("UpdateETag")).isEmpty()) {
                    connection.setRequestProperty("If-None-Match", etag);
                }
                connection.connect();
                if (connection.getResponseCode() == 304) {
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATE_DEBUG, "wdl.messages.updates.usingCachedUpdates", new Object[0]);
                    stream = new FileInputStream(CACHED_RELEASES_FILE);
                } else if (connection.getResponseCode() == 200) {
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.UPDATE_DEBUG, "wdl.messages.updates.grabingUpdatesFromGithub", new Object[0]);
                    stream = connection.getInputStream();
                } else {
                    throw new Exception("Unexpected response while getting " + path + ": " + connection.getResponseCode() + " " + connection.getResponseMessage());
                }
                InputStreamReader reader = null;
                try {
                    reader = new InputStreamReader(stream);
                    JsonElement element = PARSER.parse(reader);
                    PrintStream output = null;
                    String etag2 = null;
                    try {
                        try {
                            output = new PrintStream(CACHED_RELEASES_FILE);
                            output.println(element.toString());
                            etag2 = connection.getHeaderField("ETag");
                        }
                        catch (Exception e2) {
                            etag2 = null;
                            throw e2;
                        }
                    }
                    catch (Throwable throwable) {
                        if (output != null) {
                            output.close();
                        }
                        if (etag2 != null) {
                            WDL.globalProps.setProperty("UpdateETag", etag2);
                        } else {
                            WDL.globalProps.remove("UpdateETag");
                        }
                        WDL.saveGlobalProps();
                        throw throwable;
                    }
                    if (output != null) {
                        output.close();
                    }
                    if (etag2 != null) {
                        WDL.globalProps.setProperty("UpdateETag", etag2);
                    } else {
                        WDL.globalProps.remove("UpdateETag");
                    }
                    WDL.saveGlobalProps();
                    jsonElement = element;
                    if (reader == null) break block24;
                }
                catch (Throwable throwable) {
                    if (reader != null) {
                        reader.close();
                    }
                    throw throwable;
                }
                reader.close();
            }
            return jsonElement;
        }
    }
}

