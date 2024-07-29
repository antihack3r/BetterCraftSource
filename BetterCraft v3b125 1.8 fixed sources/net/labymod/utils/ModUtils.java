/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.net.ssl.HttpsURLConnection;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.apache.commons.io.IOUtils;

public class ModUtils {
    public static String getProfileNameByIp(String ip2) {
        String[] parts;
        if (ip2 == null) {
            return null;
        }
        if (ip2.contains(":")) {
            ip2 = ip2.split(":")[0];
        }
        if (ip2.contains(".") && (parts = ip2.split("\\.")).length >= 2) {
            return String.valueOf(parts[parts.length - 2]) + "." + parts[parts.length - 1];
        }
        return ip2.toLowerCase();
    }

    public static List<String> extractUrls(String text) {
        ArrayList<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile("((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", 2);
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }
        return containedUrls;
    }

    public static String download(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            String encoding = conn.getContentEncoding();
            InputStream inStr = null;
            inStr = encoding != null && encoding.equalsIgnoreCase("gzip") ? new GZIPInputStream(conn.getInputStream()) : (encoding != null && encoding.equalsIgnoreCase("deflate") ? new InflaterInputStream(conn.getInputStream(), new Inflater(true)) : conn.getInputStream());
            return IOUtils.toString(inStr);
        }
        catch (Exception ex2) {
            return null;
        }
    }

    public static String getContentString(String page) {
        try {
            String line;
            HttpURLConnection connection = (HttpURLConnection)new URL(page).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            BufferedReader r2 = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            String s2 = "";
            while ((line = r2.readLine()) != null) {
                s2 = String.valueOf(s2) + line;
            }
            return s2;
        }
        catch (Exception error) {
            error.printStackTrace();
            return "";
        }
    }

    public static ArrayList<String> getContentList(String page) {
        try {
            String line;
            HttpURLConnection connection = (HttpURLConnection)new URL(page).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            BufferedReader r2 = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            ArrayList<String> s2 = new ArrayList<String>();
            while ((line = r2.readLine()) != null) {
                s2.add(line);
            }
            return s2;
        }
        catch (Exception error) {
            error.printStackTrace();
            return new ArrayList<String>();
        }
    }

    public static String parseTimeNormalize(long time) {
        long formatb = time / 60L % 60L;
        long formatc = time % 60L;
        long formatd = time / 600L / 60L % 24L;
        long formate = time / 600L / 60L / 24L;
        String out = "";
        if (formate != 0L) {
            out = String.valueOf(out) + formate + "d ";
        }
        if (formatd != 0L) {
            out = String.valueOf(out) + formatd + "h ";
        }
        if (formatb != 0L) {
            out = String.valueOf(out) + formatb + "m ";
        }
        if (formatc != 0L) {
            out = String.valueOf(out) + formatc + "s";
        }
        return out;
    }

    public static String performPost(URL url, String parameters, String contentType, boolean returnErrorPage) throws IOException {
        String line;
        BufferedReader reader;
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        byte[] paramAsBytes = parameters.getBytes(Charset.forName("UTF-8"));
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", String.valueOf(contentType) + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + paramAsBytes.length);
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.write(paramAsBytes);
        writer.flush();
        writer.close();
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }
        catch (IOException e2) {
            if (!returnErrorPage) {
                throw e2;
            }
            InputStream stream = connection.getErrorStream();
            if (stream == null) {
                throw e2;
            }
            reader = new BufferedReader(new InputStreamReader(stream));
        }
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        reader.close();
        return response.toString();
    }

    public static URL constantURL(String input) {
        try {
            return new URL(input);
        }
        catch (MalformedURLException ex2) {
            return null;
        }
    }

    public static String jsonPost(String urlStr, String json) throws Exception {
        URL url = new URL(urlStr);
        HttpsURLConnection httpConnection = (HttpsURLConnection)url.openConnection();
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
        out.write(json);
        out.close();
        BufferedReader br2 = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        StringBuffer sb2 = new StringBuffer();
        String str = br2.readLine();
        while (str != null) {
            sb2.append(str);
            str = br2.readLine();
        }
        br2.close();
        return sb2.toString();
    }

    public static String normalizeString(String input) {
        char[] c2 = input.toLowerCase().toCharArray();
        c2[0] = Character.toUpperCase(c2[0]);
        return new String(c2);
    }

    public static String sha1(String string) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string.getBytes("UTF-8"));
            sha1 = ModUtils.byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        byte[] byArray = hash;
        int n2 = hash.length;
        int n3 = 0;
        while (n3 < n2) {
            byte b2 = byArray[n3];
            formatter.format("%02x", b2);
            ++n3;
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b2 = textToTranslate.toCharArray();
        int i2 = 0;
        while (i2 < b2.length - 1) {
            if (b2[i2] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b2[i2 + 1]) > -1) {
                b2[i2] = 167;
                b2[i2 + 1] = Character.toLowerCase(b2[i2 + 1]);
            }
            ++i2;
        }
        return new String(b2);
    }

    public static String parseTimer(int seconds) {
        return seconds >= 3600 ? String.format("%02d:%02d", seconds / 60 / 60, seconds / 60, seconds % 60) : String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public static String humanReadableByteCount(long bytes, boolean si2, boolean space) {
        int unit;
        int n2 = unit = si2 ? 1000 : 1024;
        if (bytes < (long)unit) {
            return String.valueOf(bytes) + " B";
        }
        int exp = (int)(Math.log(bytes) / Math.log(unit));
        String pre = String.valueOf((si2 ? "kMGTPE" : "KMGTPE").charAt(exp - 1)) + (si2 ? "" : "i");
        return String.format("%.1f" + (space ? " " : "") + "%sB", (double)bytes / Math.pow(unit, exp), pre);
    }

    public static String getTimeDiff(long timestamp) {
        if (timestamp == 0L) {
            return LanguageManager.translate("time_unknown");
        }
        long time = System.currentTimeMillis() - timestamp;
        if (time == 0L) {
            return LanguageManager.translate("time_now");
        }
        if (time < 0L) {
            return LanguageManager.translate("time_future");
        }
        long secs = time / 1000L;
        long mins = secs / 60L;
        long hours = mins / 60L;
        long days = hours / 24L;
        long months = days / 31L;
        long years = months / 12L;
        String date = null;
        date = months >= 12L ? String.valueOf(years) + " " + LanguageManager.translate("time_" + (years == 1L ? "year" : "years")) : (days >= 31L ? String.valueOf(months) + " " + LanguageManager.translate("time_" + (months == 1L ? "month" : "months")) : (hours >= 24L ? String.valueOf(days) + " " + LanguageManager.translate("time_" + (days == 1L ? "day" : "days")) : (mins >= 60L ? String.valueOf(hours) + " " + LanguageManager.translate("time_" + (hours == 1L ? "hour" : "hours")) : (secs >= 60L ? String.valueOf(mins) + " " + LanguageManager.translate("time_" + (mins == 1L ? "minute" : "minutes")) : String.valueOf(secs) + " " + LanguageManager.translate("time_" + (secs == 1L ? "second" : "seconds"))))));
        date = LanguageManager.translate("time_ago", date).toLowerCase();
        return date;
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

    public static void openWebpage(final URI uri, boolean request) {
        if (request) {
            final GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
            Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                @Override
                public void confirmClicked(boolean result, int id2) {
                    if (result) {
                        Desktop desktop1;
                        Desktop desktop = desktop1 = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                        if (desktop1 != null && desktop1.isSupported(Desktop.Action.BROWSE)) {
                            try {
                                desktop1.browse(uri);
                            }
                            catch (Exception exception1) {
                                exception1.printStackTrace();
                            }
                        }
                    }
                    Minecraft.getMinecraft().displayGuiScreen(guiscreen);
                }
            }, "Do you want to open this link in your default browser?", String.valueOf(ModColor.cl("b")) + uri.toString(), 31102009));
        } else {
            Desktop desktop;
            Desktop desktop2 = desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(uri);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<String> extractDomains(String value) {
        value = value.replaceAll((Object)((Object)ModColor.RED) + "[a-z0-9]", "");
        ArrayList<String> arraylist = new ArrayList<String>();
        if (value == null) {
            return arraylist;
        }
        String s2 = "(?i)\\b((?:[a-z][\\w-]+:(?:\\/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}\\/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?\u00ab\u00bb\u201c\u201d\u2018\u2019]))";
        Pattern pattern = Pattern.compile(s2, 2);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            arraylist.add(value.substring(matcher.start(0), matcher.end(0)));
        }
        return arraylist;
    }

    public static class ConvertJsonToObject {
        private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

        public static final <T> T getFromJSON(String json, Class<T> clazz) {
            return gson.fromJson(json, clazz);
        }

        public static final <T> String toJSON(T clazz) {
            return gson.toJson(clazz);
        }
    }
}

