// 
// Decompiled by Procyon v0.6.0
// 

package me.amkgre.bettercraft.client.utils;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import com.google.gson.Gson;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NameUtils
{
    protected static final String UUIDAPI = "https://api.mojang.com/users/profiles/minecraft/";
    protected static final String PROFILEAPI = "https://api.mojang.com/user/profiles/";
    
    public static String getUUID(final String playername) {
        final JsonObject getUUidObject = new JsonParser().parse(infosFromWebsite("https://api.mojang.com/users/profiles/minecraft/" + playername)).getAsJsonObject();
        return getUUidObject.get("id").getAsString();
    }
    
    public static String[] getNameHistory(final String uuid) {
        final String json = infosFromWebsite("https://api.mojang.com/user/profiles/" + uuid + "/names");
        final ArrayList<String> names = new ArrayList<String>();
        final Gson gson = new Gson();
        final NameArrayHookUtils[] nameClass = gson.fromJson(json, NameArrayHookUtils[].class);
        for (int i = 0; i < nameClass.length; ++i) {
            names.add(nameClass[i].getName());
        }
        return names.toArray(new String[0]);
    }
    
    private static String infosFromWebsite(final String website) {
        try {
            final StringBuilder stringBuilder = new StringBuilder("");
            final URL url = new URL(website);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            return stringBuilder.toString();
        }
        catch (final Exception e) {
            return null;
        }
    }
}
