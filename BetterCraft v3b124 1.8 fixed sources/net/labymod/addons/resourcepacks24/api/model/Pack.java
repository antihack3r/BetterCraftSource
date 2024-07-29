/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.api.model;

import com.google.gson.Gson;
import net.labymod.addons.resourcepacks24.api.model.Screenshot;

public class Pack {
    private static final Gson GSON = new Gson();
    public int rp_id;
    public String website_name;
    public String ingame_name;
    public int size;
    public String thumbnail;
    public Screenshot[] screenshots;
    public int download;
    public String description;
    public int rating;
    public String tags;
    public String creator;
    public String created_at;
    public String updated_at;
    public String category;
    public String[] tagsArray;

    public String[] getTags() {
        String[] stringArray;
        if (this.tagsArray == null) {
            this.tagsArray = GSON.fromJson(this.tags, String[].class);
            stringArray = this.tagsArray;
        } else {
            stringArray = this.tagsArray;
        }
        return stringArray;
    }
}

