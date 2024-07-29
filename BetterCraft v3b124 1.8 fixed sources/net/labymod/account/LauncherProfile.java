/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.account;

import com.mojang.authlib.GameProfile;
import java.beans.ConstructorProperties;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.minecraft.util.Session;

public class LauncherProfile {
    private String mojangUsername;
    private String userid;
    private GameProfile gameProfile;
    private String accessToken;

    public void login(Consumer<Boolean> response) {
        LabyMod.getInstance().getAccountManager().getAccountLoginHandler().loginWithToken(this.gameProfile.getId(), this.gameProfile.getName(), this.accessToken, response);
    }

    public void refresh(final Consumer<Boolean> response) {
        try {
            LabyMod.getInstance().getAccountManager().getAccountLoginHandler().refreshToken(this.accessToken, new Consumer<String>(){

                @Override
                public void accept(String accepted) {
                    if (accepted != null) {
                        LauncherProfile.this.accessToken = accepted;
                    }
                    LabyMod.getInstance().getAccountManager().saveLauncherProfiles(response);
                }
            }, response);
        }
        catch (Exception e2) {
            response.accept(false);
            e2.printStackTrace();
        }
    }

    public Session buildSession() {
        return new Session(this.gameProfile.getName(), this.gameProfile.getId().toString(), this.accessToken, Session.Type.MOJANG.toString());
    }

    @ConstructorProperties(value={"mojangUsername", "userid", "gameProfile", "accessToken"})
    public LauncherProfile(String mojangUsername, String userid, GameProfile gameProfile, String accessToken) {
        this.mojangUsername = mojangUsername;
        this.userid = userid;
        this.gameProfile = gameProfile;
        this.accessToken = accessToken;
    }

    public String getMojangUsername() {
        return this.mojangUsername;
    }

    public String getUserid() {
        return this.userid;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setMojangUsername(String mojangUsername) {
        this.mojangUsername = mojangUsername;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

