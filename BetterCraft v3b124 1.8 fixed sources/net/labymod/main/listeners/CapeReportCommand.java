/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.listeners;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.main.LabyMod;
import net.labymod.utils.ModColor;

public class CapeReportCommand
implements MessageSendEvent {
    private static long lastReport = 0L;

    @Override
    public boolean onSend(String msg) {
        String m2 = msg.toLowerCase();
        if (m2.startsWith("/capereport") || m2.startsWith("/reportcape")) {
            if (msg.contains(" ")) {
                if (lastReport < System.currentTimeMillis()) {
                    String user = msg.split(" ")[1];
                    CapeReportCommand.report(user);
                } else {
                    LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl("c")) + "You've just reported a cape, please wait for a short while..");
                }
            } else {
                LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl("c")) + msg + " <player>");
            }
            return true;
        }
        return false;
    }

    public static void report(final String user) {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    lastReport = System.currentTimeMillis() + 20000L;
                    LabyMod.getInstance().displayMessageInChat(CapeReportCommand.jsonPost("http://api.labymod.net/capes/capeReport.php", "reporter=" + LabyMod.getInstance().getPlayerName() + "&capeowner=" + user));
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }).start();
    }

    public static String jsonPost(String urlStr, String json) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        httpConnection.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
        out.write(json);
        out.close();
        int code = httpConnection.getResponseCode();
        if (code / 100 == 2) {
            BufferedReader br2 = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            StringBuffer sb2 = new StringBuffer();
            String str = br2.readLine();
            while (str != null) {
                sb2.append(str);
                str = br2.readLine();
            }
            br2.close();
            return sb2.toString().replaceAll("\u00c2", "");
        }
        return "Response: " + code;
    }

    static /* synthetic */ long access$0() {
        return lastReport;
    }
}

