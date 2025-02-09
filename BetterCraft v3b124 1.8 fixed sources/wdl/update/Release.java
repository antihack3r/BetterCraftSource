/*
 * Decompiled with CFR 0.152.
 */
package wdl.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Release {
    private static final Pattern HIDDEN_JSON_REGEX = Pattern.compile("^\\[\\]\\(# '(.+?)'\\)");
    private static final JsonParser PARSER = new JsonParser();
    public final JsonObject object;
    public final String URL;
    public final String tag;
    public final String title;
    public final String date;
    public final boolean prerelease;
    public final String markdownBody;
    public final String textOnlyBody;
    public final HiddenInfo hiddenInfo;

    public Release(JsonObject object) {
        this.object = object;
        this.markdownBody = object.get("body").getAsString();
        Matcher hiddenJSONMatcher = HIDDEN_JSON_REGEX.matcher(this.markdownBody);
        if (hiddenJSONMatcher.find()) {
            String hiddenJSONStr = this.markdownBody.substring(hiddenJSONMatcher.start(1), hiddenJSONMatcher.end(1));
            JsonObject hiddenJSON = PARSER.parse(hiddenJSONStr).getAsJsonObject();
            this.hiddenInfo = new HiddenInfo(hiddenJSON);
        } else {
            this.hiddenInfo = null;
        }
        this.URL = object.get("html_url").getAsString();
        this.textOnlyBody = object.get("body_text").getAsString();
        this.tag = object.get("tag_name").getAsString();
        this.title = object.get("name").getAsString();
        this.date = object.get("published_at").getAsString();
        this.prerelease = object.get("prerelease").getAsBoolean();
    }

    public String toString() {
        return "Release [URL=" + this.URL + ", tag=" + this.tag + ", title=" + this.title + ", date=" + this.date + ", prerelease=" + this.prerelease + ", markdownBody=" + this.markdownBody + ", textOnlyBody=" + this.textOnlyBody + ", hiddenInfo=" + this.hiddenInfo + "]";
    }

    public class HashData {
        public final String relativeTo;
        public final String file;
        public final String[] validHashes;

        public HashData(JsonObject object) {
            this.relativeTo = object.get("RelativeTo").getAsString();
            this.file = object.get("File").getAsString();
            JsonArray hashes = object.get("Hash").getAsJsonArray();
            this.validHashes = new String[hashes.size()];
            int i2 = 0;
            while (i2 < this.validHashes.length) {
                this.validHashes[i2] = hashes.get(i2).getAsString();
                ++i2;
            }
        }

        public String toString() {
            return "HashData [relativeTo=" + this.relativeTo + ", file=" + this.file + ", validHashes=" + Arrays.toString(this.validHashes) + "]";
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + this.getOuterType().hashCode();
            result = 31 * result + (this.file == null ? 0 : this.file.hashCode());
            result = 31 * result + (this.relativeTo == null ? 0 : this.relativeTo.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof HashData)) {
                return false;
            }
            HashData other = (HashData)obj;
            if (!this.getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.file == null ? other.file != null : !this.file.equals(other.file)) {
                return false;
            }
            return !(this.relativeTo == null ? other.relativeTo != null : !this.relativeTo.equals(other.relativeTo));
        }

        private Release getOuterType() {
            return Release.this;
        }
    }

    public class HiddenInfo {
        public final String mainMinecraftVersion;
        public final String[] supportedMinecraftVersions;
        public final String loader;
        public final String post;
        public final HashData[] hashes;

        private HiddenInfo(JsonObject object) {
            this.mainMinecraftVersion = object.get("Minecraft").getAsString();
            JsonArray compatibleVersions = object.get("MinecraftCompatible").getAsJsonArray();
            this.supportedMinecraftVersions = new String[compatibleVersions.size()];
            int i2 = 0;
            while (i2 < compatibleVersions.size()) {
                this.supportedMinecraftVersions[i2] = compatibleVersions.get(i2).getAsString();
                ++i2;
            }
            this.loader = object.get("Loader").getAsString();
            JsonElement post = object.get("Post");
            this.post = post.isJsonNull() ? null : post.getAsString();
            JsonArray hashes = object.get("Hashes").getAsJsonArray();
            this.hashes = new HashData[hashes.size()];
            int i3 = 0;
            while (i3 < hashes.size()) {
                this.hashes[i3] = new HashData(hashes.get(i3).getAsJsonObject());
                ++i3;
            }
        }

        public String toString() {
            return "HiddenInfo [mainMinecraftVersion=" + this.mainMinecraftVersion + ", supportedMinecraftVersions=" + Arrays.toString(this.supportedMinecraftVersions) + ", loader=" + this.loader + ", post=" + this.post + ", hashes=" + Arrays.toString(this.hashes) + "]";
        }
    }
}

