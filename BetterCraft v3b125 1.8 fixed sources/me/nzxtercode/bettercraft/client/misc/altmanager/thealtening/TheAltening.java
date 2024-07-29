/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager.thealtening;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.AlteningAlt;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.User;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.Utilities;

public final class TheAltening {
    private final String website = "http://api.thealtening.com/v1/";
    private static String apiKey = Config.getInstance().getConfig("Alterning").get("apikey").getAsString();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TheAltening(String apiKey) {
        TheAltening.apiKey = apiKey;
    }

    public User getUser() throws IOException {
        this.getClass();
        URLConnection licenseEndpoint = new URL(this.attach("http://api.thealtening.com/v1/license")).openConnection();
        String userInfo = new String(Utilities.getInstance().readAllBytes(licenseEndpoint.getInputStream()));
        return this.gson.fromJson(userInfo, User.class);
    }

    public AlteningAlt generateAccount(User user) throws IOException {
        this.getClass();
        URLConnection generateEndpoint = new URL(this.attach("http://api.thealtening.com/v1/generate")).openConnection();
        String accountInfo = new String(Utilities.getInstance().readAllBytes(generateEndpoint.getInputStream()));
        if (user.isPremium()) {
            return this.gson.fromJson(accountInfo, AlteningAlt.class);
        }
        return null;
    }

    public boolean favoriteAccount(AlteningAlt account) throws IOException {
        this.getClass();
        URLConnection favoriteAccount = new URL(this.attachAccount("http://api.thealtening.com/v1/favorite", account)).openConnection();
        String info = new String(Utilities.getInstance().readAllBytes(favoriteAccount.getInputStream()));
        return info.isEmpty();
    }

    public boolean privateAccount(AlteningAlt account) throws IOException {
        this.getClass();
        URLConnection privateAccount = new URL(this.attachAccount("http://api.thealtening.com/v1/private", account)).openConnection();
        String info = new String(Utilities.getInstance().readAllBytes(privateAccount.getInputStream()));
        return info.isEmpty();
    }

    private String attach(String website) {
        return String.valueOf(website) + "?token=" + apiKey;
    }

    private String attachAccount(String website, AlteningAlt account) {
        return String.valueOf(website) + "?token=" + apiKey + "&acctoken=" + account.getToken();
    }
}

