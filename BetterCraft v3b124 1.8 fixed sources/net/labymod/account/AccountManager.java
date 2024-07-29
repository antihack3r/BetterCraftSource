/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.account;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.labymod.account.AccountLoginHandler;
import net.labymod.account.AuthError;
import net.labymod.account.LauncherProfile;
import net.labymod.account.MCBaseFolder;
import net.labymod.utils.Consumer;
import net.labymod.utils.JsonParse;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;

public class AccountManager {
    private static final File launcherProfilesFile = new File(MCBaseFolder.getWorkingDirectory(), "launcher_profiles.json");
    private GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
    private AuthError lastErrorType;
    private String lastErrorMessage;
    private List<LauncherProfile> launcherProfiles = new ArrayList<LauncherProfile>();
    private AccountLoginHandler accountLoginHandler = new AccountLoginHandler(this);
    private String clientToken;

    public AccountManager() {
        this.loadLauncherProfiles();
    }

    public void loadLauncherProfiles() {
        if (!launcherProfilesFile.exists()) {
            this.throwError(AuthError.NO_FILE, new String[0]);
            return;
        }
        ArrayList<LauncherProfile> launcherProfiles = new ArrayList<LauncherProfile>();
        try {
            int profilesFormat;
            JsonObject mainObject = JsonParse.parse(IOUtils.toString(new FileInputStream(launcherProfilesFile))).getAsJsonObject();
            int n2 = profilesFormat = mainObject.has("launcherVersion") ? mainObject.get("launcherVersion").getAsJsonObject().get("profilesFormat").getAsInt() : 1;
            if (mainObject.has("authenticationDatabase") && mainObject.has("clientToken")) {
                JsonObject authDatabase = mainObject.getAsJsonObject("authenticationDatabase");
                this.clientToken = mainObject.get("clientToken").getAsString();
                for (Map.Entry<String, JsonElement> authEntry : authDatabase.entrySet()) {
                    try {
                        JsonObject currentProfile = authEntry.getValue().getAsJsonObject();
                        if (currentProfile == null) continue;
                        String accessToken = currentProfile.has("accessToken") ? currentProfile.get("accessToken").getAsString() : "";
                        String mojangUsername = currentProfile.get("username").getAsString();
                        String userId = authEntry.getKey();
                        GameProfile gameProfile = null;
                        if (profilesFormat == 1) {
                            String minecraftName = currentProfile.get("displayName").getAsString();
                            UUID minecraftUUID = UUID.fromString(currentProfile.get("uuid").getAsString());
                            userId = currentProfile.get("userid").getAsString();
                            gameProfile = new GameProfile(minecraftUUID, minecraftName);
                        } else {
                            JsonObject profilesObject = currentProfile.get("profiles").getAsJsonObject();
                            JsonObject mcProfile = null;
                            String id2 = null;
                            Iterator<Map.Entry<String, JsonElement>> iterator2 = profilesObject.entrySet().iterator();
                            if (iterator2.hasNext()) {
                                Map.Entry<String, JsonElement> profileEntry = iterator2.next();
                                id2 = profileEntry.getKey();
                                mcProfile = profileEntry.getValue().getAsJsonObject();
                            }
                            UUID minecraftUUID2 = UUID.fromString(String.valueOf(id2.substring(0, 8)) + "-" + id2.substring(8, 12) + "-" + id2.substring(12, 16) + "-" + id2.substring(16, 20) + "-" + id2.substring(20, 32));
                            String minecraftName2 = mcProfile.get("displayName").getAsString();
                            gameProfile = new GameProfile(minecraftUUID2, minecraftName2);
                        }
                        LauncherProfile launcherProfile = new LauncherProfile(mojangUsername, userId, gameProfile, accessToken);
                        launcherProfiles.add(launcherProfile);
                    }
                    catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            } else {
                this.throwError(AuthError.INVALID_CONTENTS, new String[0]);
            }
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
            this.throwError(AuthError.EXCEPTION, ex2.getMessage());
        }
        this.launcherProfiles = launcherProfiles;
    }

    public void saveLauncherProfiles(Consumer<Boolean> response) {
        if (!launcherProfilesFile.exists()) {
            response.accept(false);
            this.throwError(AuthError.NO_FILE, new String[0]);
            return;
        }
        try {
            int profilesFormat;
            JsonObject mainObject = JsonParse.parse(IOUtils.toString(new FileInputStream(launcherProfilesFile))).getAsJsonObject();
            int n2 = profilesFormat = mainObject.has("launcherVersion") ? mainObject.get("launcherVersion").getAsJsonObject().get("profilesFormat").getAsInt() : 1;
            if (mainObject.has("authenticationDatabase") && mainObject.has("clientToken")) {
                JsonObject authenticationDatabase = new JsonObject();
                for (LauncherProfile launcherProfile : this.launcherProfiles) {
                    JsonObject userEntry;
                    if (profilesFormat == 1) {
                        userEntry = new JsonObject();
                        userEntry.addProperty("displayName", launcherProfile.getGameProfile().getName());
                        userEntry.addProperty("accessToken", launcherProfile.getAccessToken());
                        userEntry.addProperty("userid", launcherProfile.getUserid());
                        userEntry.addProperty("uuid", launcherProfile.getGameProfile().getId().toString());
                        userEntry.addProperty("username", launcherProfile.getMojangUsername());
                        authenticationDatabase.add(launcherProfile.getGameProfile().getId().toString().replaceAll("-", ""), userEntry);
                        continue;
                    }
                    userEntry = new JsonObject();
                    userEntry.addProperty("accessToken", launcherProfile.getAccessToken());
                    userEntry.addProperty("username", launcherProfile.getMojangUsername());
                    JsonObject profileEntry = new JsonObject();
                    JsonObject gameProfileEntry = new JsonObject();
                    gameProfileEntry.addProperty("displayName", launcherProfile.getGameProfile().getName());
                    profileEntry.add(launcherProfile.getGameProfile().getId().toString().replaceAll("-", ""), gameProfileEntry);
                    userEntry.add("profiles", profileEntry);
                    authenticationDatabase.add(launcherProfile.getUserid(), userEntry);
                }
                mainObject.add("authenticationDatabase", authenticationDatabase);
                IOUtils.write(this.gsonBuilder.create().toJson(mainObject), (OutputStream)new FileOutputStream(launcherProfilesFile), Charset.forName("UTF-8"));
                response.accept(true);
            } else {
                response.accept(false);
                this.throwError(AuthError.INVALID_CONTENTS, new String[0]);
            }
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
            response.accept(false);
            this.throwError(AuthError.EXCEPTION, ex2.getMessage());
        }
    }

    public void addAccount(String mojangUsername, Session session) {
        LauncherProfile launcherProfile = new LauncherProfile(mojangUsername, session.getProfile().getId().toString().replaceAll("-", ""), session.getProfile(), session.getToken());
        this.launcherProfiles.add(launcherProfile);
    }

    public void removeAccount(UUID uuid) {
        Iterator<LauncherProfile> iterator = this.launcherProfiles.iterator();
        while (iterator.hasNext()) {
            LauncherProfile next = iterator.next();
            if (!next.getGameProfile().getId().equals(uuid)) continue;
            iterator.remove();
        }
    }

    public LauncherProfile getAccount(UUID uuid) {
        for (LauncherProfile next : this.launcherProfiles) {
            if (!next.getGameProfile().getId().equals(uuid)) continue;
            return next;
        }
        return null;
    }

    protected void throwError(AuthError type, String ... format) {
        this.lastErrorType = type;
        this.lastErrorMessage = type.toMessage(format);
    }

    public String getLastErrorMessage() {
        return this.lastErrorMessage;
    }

    public List<LauncherProfile> getLauncherProfiles() {
        return this.launcherProfiles;
    }

    public void setLauncherProfiles(List<LauncherProfile> launcherProfiles) {
        this.launcherProfiles = launcherProfiles;
    }

    public AccountLoginHandler getAccountLoginHandler() {
        return this.accountLoginHandler;
    }

    public String getClientToken() {
        return this.clientToken;
    }
}

