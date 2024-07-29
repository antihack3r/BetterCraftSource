/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.party.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.beans.ConstructorProperties;
import java.net.ProtocolException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.labymod.labyplay.party.model.PartyActionTypes;
import net.labymod.main.LabyMod;

public class PartyMessage {
    private static final Gson GSON = new Gson();
    private final String action;
    private final PartyMessageData[] data;

    public PartyMessage(String action, PartyMessageData[] data) {
        this.action = action;
        this.data = data;
    }

    public int getInt(String key) throws ProtocolException {
        JsonElement element = this.getElement(key);
        if (element == null) {
            throw new ProtocolException(String.format("Malformed JSON in Addon message / INT @ %s:%s", this.getAction(), key));
        }
        return element.getAsInt();
    }

    public UUID getUUID(String key) throws ProtocolException {
        return UUID.fromString(this.getString(key));
    }

    public boolean getBoolean(String key) throws ProtocolException {
        JsonElement element = this.getElement(key);
        if (element == null) {
            throw new ProtocolException(String.format("Malformed JSON in Addon message / BOOL @ %s:%s", this.getAction(), key));
        }
        return element.getAsBoolean();
    }

    public String getString(String key) throws ProtocolException {
        String ret = this.getString(key, null);
        if (ret == null) {
            throw new ProtocolException(String.format("Malformed JSON in Addon message / String @ %s:%s", this.getAction(), key));
        }
        return ret;
    }

    public String getString(String key, String def) {
        JsonElement element = this.getElement(key);
        return element == null ? def : element.getAsString();
    }

    public JsonElement getElement(String key) {
        PartyMessageData[] partyMessageDataArray = this.data;
        int n2 = this.data.length;
        int n3 = 0;
        while (n3 < n2) {
            PartyMessageData datum = partyMessageDataArray[n3];
            if (datum.key.equalsIgnoreCase(key)) {
                return datum.value;
            }
            ++n3;
        }
        return null;
    }

    public String getAction() {
        return this.action;
    }

    public PartyMessageData[] getData() {
        return this.data;
    }

    public static class Builder {
        private final PartyActionTypes.Client action;
        private List<Pair> data = new LinkedList<Pair>();

        public Builder(PartyActionTypes.Client action) {
            this.action = action;
        }

        public Builder putUUID(String key, UUID content) {
            this.data.add(new Pair(key, new JsonPrimitive(content.toString())));
            return this;
        }

        public Builder putString(String key, String content) {
            this.data.add(new Pair(key, new JsonPrimitive(content)));
            return this;
        }

        public Builder putInt(String key, int content) {
            this.data.add(new Pair(key, new JsonPrimitive(content)));
            return this;
        }

        public Builder putBoolean(String key, boolean content) {
            this.data.add(new Pair(key, new JsonPrimitive(content)));
            return this;
        }

        public Builder setResource(String key, String ... args) {
            this.data.add(new Pair("key", new JsonPrimitive(key)));
            JsonArray array = new JsonArray();
            String[] stringArray = args;
            int n2 = args.length;
            int n3 = 0;
            while (n3 < n2) {
                String arg2 = stringArray[n3];
                array.add(new JsonPrimitive(arg2));
                ++n3;
            }
            return this.putArray("args", array);
        }

        public Builder putArray(String key, JsonArray array) {
            this.data.add(new Pair(key, array));
            return this;
        }

        public PartyMessage build() {
            PartyMessageData[] data = new PartyMessageData[this.data.size()];
            int i2 = 0;
            for (Pair datum : this.data) {
                data[i2] = new PartyMessageData(datum.getKey(), datum.getElement());
                ++i2;
            }
            return new PartyMessage(this.action.getKey(), data);
        }

        public void send() {
            PartyMessage partyMessage = this.build();
            String json = GSON.toJson(partyMessage);
            System.out.println("[OUT] " + json);
            LabyMod.getInstance().getLabyModAPI().sendAddonMessage("party", json);
        }

        public static class Pair {
            private String key;
            private JsonElement element;

            public String getKey() {
                return this.key;
            }

            public JsonElement getElement() {
                return this.element;
            }

            @ConstructorProperties(value={"key", "element"})
            public Pair(String key, JsonElement element) {
                this.key = key;
                this.element = element;
            }
        }
    }

    public static class PartyMessageData {
        private String key;
        private JsonElement value;

        public PartyMessageData(String key, JsonElement value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public JsonElement getValue() {
            return this.value;
        }
    }
}

