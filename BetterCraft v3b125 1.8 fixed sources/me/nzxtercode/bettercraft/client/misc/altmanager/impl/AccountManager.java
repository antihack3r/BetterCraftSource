/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.Account;

public class AccountManager {
    private static final AccountManager INSTANCE = new AccountManager();
    private ArrayList<Account> accounts = new ArrayList();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String alteningKey;
    private String lastAlteningAlt;
    private Account lastAlt;
    private static File altsFile;

    static {
        BetterCraft.getInstance();
        altsFile = new File(BetterCraft.clientName, "alts.json");
    }

    public static AccountManager getInstance() {
        return INSTANCE;
    }

    public void save() {
        if (altsFile == null) {
            return;
        }
        try {
            if (!altsFile.exists()) {
                altsFile.createNewFile();
            }
            PrintWriter printWriter = new PrintWriter(altsFile);
            printWriter.write(this.gson.toJson(this.toJson()));
            printWriter.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void init() {
        if (!altsFile.exists()) {
            this.save();
            return;
        }
        try {
            JsonObject json = new JsonParser().parse(new FileReader(altsFile)).getAsJsonObject();
            this.fromJson(json);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        this.getAccounts().forEach(account -> jsonArray.add(account.toJson()));
        if (this.alteningKey != null) {
            jsonObject.addProperty("altening", this.alteningKey);
        }
        if (this.lastAlteningAlt != null) {
            jsonObject.addProperty("alteningAlt", this.lastAlteningAlt);
        }
        if (this.lastAlt != null) {
            jsonObject.add("lastalt", this.lastAlt.toJson());
        }
        jsonObject.add("accounts", jsonArray);
        return jsonObject;
    }

    public void fromJson(JsonObject json) {
        if (json.has("altening")) {
            this.alteningKey = json.get("altening").getAsString();
        }
        if (json.has("alteningAlt")) {
            this.lastAlteningAlt = json.get("alteningAlt").getAsString();
        }
        if (json.has("lastalt")) {
            Account account = new Account();
            account.fromJson(json.get("lastalt").getAsJsonObject());
            this.lastAlt = account;
        }
        JsonArray jsonArray = json.get("accounts").getAsJsonArray();
        jsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = (JsonObject)jsonElement;
            Account account = new Account();
            account.fromJson(jsonObject);
            this.getAccounts().add(account);
        });
    }

    public void remove(String username) {
        for (Account account : this.getAccounts()) {
            if (!account.getName().equalsIgnoreCase(username)) continue;
            this.getAccounts().remove(account);
        }
    }

    public Account getAccountByEmail(String email) {
        for (Account account : this.getAccounts()) {
            if (!account.getEmail().equalsIgnoreCase(email)) continue;
            return account;
        }
        return null;
    }

    public String getLastAlteningAlt() {
        return this.lastAlteningAlt;
    }

    public void setLastAlteningAlt(String lastAlteningAlt) {
        this.lastAlteningAlt = lastAlteningAlt;
    }

    public String getAlteningKey() {
        return this.alteningKey;
    }

    public void setAlteningKey(String alteningKey) {
        this.alteningKey = alteningKey;
    }

    public Account getLastAlt() {
        return this.lastAlt;
    }

    public void setLastAlt(Account lastAlt) {
        this.lastAlt = lastAlt;
    }

    public ArrayList<Account> getNotBannedAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>(this.accounts);
        int i2 = 0;
        while (i2 < accounts.size()) {
            if (((Account)accounts.get(i2)).isBanned()) {
                accounts.remove(i2);
            }
            ++i2;
        }
        return this.accounts;
    }

    public ArrayList<Account> getAccounts() {
        return this.accounts;
    }
}

