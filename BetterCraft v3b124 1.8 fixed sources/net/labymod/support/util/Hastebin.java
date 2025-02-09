/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
import net.labymod.main.LabyMod;

public class Hastebin {
    private static final JsonParser JSON_PARSER = new JsonParser();

    public static void upload(String content) {
        try {
            int c2;
            byte[] postData = content.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            String request = "https://paste.labymod.net/documents";
            URL url = new URL("https://paste.labymod.net/documents");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            DataOutputStream wr2 = new DataOutputStream(conn.getOutputStream());
            wr2.write(postData);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String json = "";
            while ((c2 = ((Reader)in2).read()) >= 0) {
                json = String.valueOf(json) + (char)c2;
            }
            JsonElement jsonElement = JSON_PARSER.parse(json);
            if (jsonElement.getAsJsonObject().has("key")) {
                String key = jsonElement.getAsJsonObject().get("key").getAsString();
                StringSelection selection = new StringSelection("https://paste.labymod.net/" + key);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                LabyMod.getInstance().openWebpage("https://paste.labymod.net/" + key, false);
            } else {
                JOptionPane.showMessageDialog(null, json, "Error while uploading log", 0);
            }
        }
        catch (Exception error) {
            error.printStackTrace();
            JOptionPane.showMessageDialog(null, error.getMessage(), "Error while uploading log", 0);
        }
    }
}

