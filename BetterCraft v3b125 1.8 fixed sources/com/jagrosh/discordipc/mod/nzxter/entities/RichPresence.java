/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RichPresence {
    private String state;
    private String details;
    private long startTimestamp;
    private long endTimestamp;
    private String largeImageKey;
    private String largeImageText;
    private String smallImageKey;
    private String smallImageText;
    private String partyId;
    private int partySize;
    private int partyMax;
    private int partyPrivacy;
    private String matchSecret;
    private String joinSecret;
    private String spectateSecret;
    private JsonArray buttons;
    private boolean instance;

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return this.endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getLargeImageKey() {
        return this.largeImageKey;
    }

    public void setLargeImageKey(String largeImageKey) {
        this.largeImageKey = largeImageKey;
    }

    public String getLargeImageText() {
        return this.largeImageText;
    }

    public void setLargeImageText(String largeImageText) {
        this.largeImageText = largeImageText;
    }

    public String getSmallImageKey() {
        return this.smallImageKey;
    }

    public void setSmallImageKey(String smallImageKey) {
        this.smallImageKey = smallImageKey;
    }

    public String getSmallImageText() {
        return this.smallImageText;
    }

    public void setSmallImageText(String smallImageText) {
        this.smallImageText = smallImageText;
    }

    public String getPartyId() {
        return this.partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public int getPartySize() {
        return this.partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public int getPartyMax() {
        return this.partyMax;
    }

    public void setPartyMax(int partyMax) {
        this.partyMax = partyMax;
    }

    public int getPartyPrivacy() {
        return this.partyPrivacy;
    }

    public void setPartyPrivacy(int partyPrivacy) {
        this.partyPrivacy = partyPrivacy;
    }

    public String getMatchSecret() {
        return this.matchSecret;
    }

    public void setMatchSecret(String matchSecret) {
        this.matchSecret = matchSecret;
    }

    public String getJoinSecret() {
        return this.joinSecret;
    }

    public void setJoinSecret(String joinSecret) {
        this.joinSecret = joinSecret;
    }

    public String getSpectateSecret() {
        return this.spectateSecret;
    }

    public void setSpectateSecret(String spectateSecret) {
        this.spectateSecret = spectateSecret;
    }

    public JsonArray getButtons() {
        return this.buttons;
    }

    public void setButtons(JsonArray buttons) {
        this.buttons = buttons;
    }

    public boolean isInstance() {
        return this.instance;
    }

    public void setInstance(boolean instance) {
        this.instance = instance;
    }

    public JsonObject toJson() {
        JsonObject timestamps = new JsonObject();
        JsonObject assets = new JsonObject();
        JsonObject party = new JsonObject();
        JsonObject secrets = new JsonObject();
        JsonObject finalObject = new JsonObject();
        if (this.startTimestamp > 0L) {
            timestamps.addProperty("start", this.startTimestamp);
            if (this.endTimestamp > this.startTimestamp) {
                timestamps.addProperty("end", this.endTimestamp);
            }
        }
        if (this.largeImageKey != null && !this.largeImageKey.isEmpty()) {
            assets.addProperty("large_image", this.largeImageKey);
            if (this.largeImageText != null && !this.largeImageText.isEmpty()) {
                assets.addProperty("large_text", this.largeImageText);
            }
        }
        if (this.smallImageKey != null && !this.smallImageKey.isEmpty()) {
            assets.addProperty("small_image", this.smallImageKey);
            if (this.smallImageText != null && !this.smallImageText.isEmpty()) {
                assets.addProperty("small_text", this.smallImageText);
            }
        }
        if (this.partyId != null && !this.partyId.isEmpty() || this.partySize > 0 && this.partyMax > 0 || this.partyPrivacy >= 0) {
            if (this.partyId != null && !this.partyId.isEmpty()) {
                party.addProperty("id", this.partyId);
            }
            JsonArray partyData = new JsonArray();
            if (this.partySize > 0) {
                partyData.add(new JsonPrimitive(this.partySize));
                if (this.partyMax >= this.partySize) {
                    partyData.add(new JsonPrimitive(this.partyMax));
                }
            }
            party.add("size", partyData);
            if (this.partyPrivacy >= 0) {
                party.add("privacy", new JsonPrimitive(this.partyPrivacy));
            }
        }
        if (this.joinSecret != null && !this.joinSecret.isEmpty()) {
            secrets.addProperty("join", this.joinSecret);
        }
        if (this.spectateSecret != null && !this.spectateSecret.isEmpty()) {
            secrets.addProperty("spectate", this.spectateSecret);
        }
        if (this.matchSecret != null && !this.matchSecret.isEmpty()) {
            secrets.addProperty("match", this.matchSecret);
        }
        if (this.state != null && !this.state.isEmpty()) {
            finalObject.addProperty("state", this.state);
        }
        if (this.details != null && !this.details.isEmpty()) {
            finalObject.addProperty("details", this.details);
        }
        if (timestamps.has("start")) {
            finalObject.add("timestamps", timestamps);
        }
        if (assets.has("large_image")) {
            finalObject.add("assets", assets);
        }
        if (party.has("id")) {
            finalObject.add("party", party);
        }
        if (secrets.has("join") || secrets.has("spectate") || secrets.has("match")) {
            finalObject.add("secrets", secrets);
        }
        if (this.buttons != null && !this.buttons.isJsonNull() && this.buttons.size() > 0 && this.buttons.size() < 3) {
            finalObject.add("buttons", this.buttons);
        }
        finalObject.addProperty("instance", this.instance);
        return finalObject;
    }

    public String toDecodedJson(String encoding) {
        try {
            String jsonString = this.toJson().toString();
            byte[] encodedBytes = jsonString.getBytes(StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(encodedBytes);
        }
        catch (Exception ex2) {
            return this.toJson().toString();
        }
    }
}

