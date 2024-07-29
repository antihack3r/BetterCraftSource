/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import me.nzxtercode.bettercraft.client.BetterCraft;
import org.apache.commons.io.FileUtils;

public class Manager {
    public static String url = "https://pastebin.com/raw/BQexz5ht";
    private static final Manager INSTANCE = new Manager();

    public static final Manager getInstance() {
        return INSTANCE;
    }

    public static boolean isLicenseVailed() {
        try {
            return !Manager.readJsonFromUrl().getAsJsonObject().get("License").getAsBoolean();
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public static void downloadFile() {
        try {
            String fileUrl = Manager.readJsonFromUrl().getAsJsonObject().get("Link").getAsString();
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File zipFile = new File(tempDir, String.valueOf(BetterCraft.clientName) + ".zip");
            File targetDir = new File(tempDir, BetterCraft.clientName);
            JOptionPane.showMessageDialog(null, "Client update available\n\nClick (OK) to start the update\n\nWait for the update to finish");
            FileUtils.copyURLToFile(new URL(fileUrl), zipFile);
            Manager.unzipFile(zipFile, targetDir);
            zipFile.delete();
            File jarFile = new File(targetDir, String.valueOf(BetterCraft.clientName) + ".jar");
            File destination = new File(String.valueOf(System.getenv("APPDATA")) + "\\.minecraft\\versions\\" + BetterCraft.clientName + "\\" + BetterCraft.clientName + ".jar");
            FileUtils.copyFile(jarFile, destination);
            FileUtils.deleteDirectory(targetDir);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static JsonObject readJsonFromUrl() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc2 = SSLContext.getInstance("SSL");
            sc2.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc2.getSocketFactory());
            URL urlObj = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection)urlObj.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:103.0) Gecko/20100101 Firefox/103.0");
            try {
                Throwable throwable = null;
                Object var5_8 = null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));){
                    JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
                    return jsonObject;
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                    } else if (throwable != throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
            }
            catch (IOException e2) {
                throw new RuntimeException("Failed to read JSON from URL", e2);
            }
        }
        catch (Exception e3) {
            throw new RuntimeException("Missing JsonObject");
        }
    }

    private static void unzipFile(File zipFile, File targetDir) throws IOException {
        Throwable throwable = null;
        Object var3_4 = null;
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));){
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    File entryFile = new File(targetDir, entry.getName());
                    Files.createDirectories(entryFile.getParentFile().toPath(), new FileAttribute[0]);
                    Files.copy(zipInputStream, entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                zipInputStream.closeEntry();
            }
        }
        catch (Throwable throwable2) {
            if (throwable == null) {
                throwable = throwable2;
            } else if (throwable != throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
    }
}

