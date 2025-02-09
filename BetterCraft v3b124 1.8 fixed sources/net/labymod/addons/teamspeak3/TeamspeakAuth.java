/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.commons.io.IOUtils;

public class TeamspeakAuth {
    public static void auth(OutputStream outputStream) {
        String s2 = "";
        String s1 = System.getProperty("os.name").toUpperCase();
        s2 = s1.contains("MAC") ? String.valueOf(System.getProperty("user.home")) + "/Library/Application Support/TeamSpeak 3" : (s1.contains("WIN") ? String.valueOf(System.getenv("AppData")) + "\\TS3Client" : String.valueOf(System.getProperty("user.home")) + "/.ts3client");
        File file1 = new File(s2, "clientquery.ini");
        if (!file1.exists()) {
            System.err.println("[TeamSpeak] Couldn't find teamspeak's clientquery.ini!");
        } else {
            String s22 = null;
            try {
                String s3 = IOUtils.toString(new FileReader(file1));
                String[] stringArray = s3.split("\n");
                int n2 = stringArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    String s4 = stringArray[n3];
                    if (s4.startsWith("api_key")) {
                        String s5 = s4.split("api_key=")[1].replace("\r", "").replace("\n", "");
                        if (s5.length() != 29) {
                            System.err.println("[TeamSpeak] Invalid TeamSpeak3 api_key! Length: " + s5.length() + " but it should be 29");
                        } else {
                            s22 = s5;
                        }
                        break;
                    }
                    ++n3;
                }
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
                return;
            }
            if (s22 != null) {
                PrintWriter printwriter = new PrintWriter(outputStream, true);
                printwriter.println("auth apikey=" + s22);
                System.out.println("[TeamSpeak] Authed with api-key " + s22);
            }
        }
    }
}

