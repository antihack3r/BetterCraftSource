/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.io.File;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.hud.IRender;
import me.nzxtercode.bettercraft.client.hud.ScreenPosition;
import me.nzxtercode.bettercraft.client.mods.Mod;
import me.nzxtercode.bettercraft.client.mods.ModType;

public abstract class ModRender
extends Mod
implements IRender {
    protected ScreenPosition screenpos;
    private boolean isEnabled;
    private boolean showPrefix;
    private int r;
    private int g;
    private int b;
    private int alpha;
    private boolean isRGBEnabled;
    private Color color;
    private int rBackground;
    private int gBackground;
    private int bBackground;
    private int alphaBackground;
    private Color colorBackground;

    public ModRender() {
        this.setType(ModType.RENDER);
        this.screenpos = this.initPosition();
        this.isEnabled = this.isEnabled();
    }

    @Override
    public ScreenPosition load() {
        return this.screenpos;
    }

    @Override
    public void save(ScreenPosition screenpos) {
        this.screenpos = screenpos;
        this.savePosition();
    }

    public ScreenPosition initPosition() {
        return this.hasPosition() ? this.getPosition() : ScreenPosition.fromAbsolute(0, 0);
    }

    private boolean hasPosition() {
        return this.getModJson().has("positions");
    }

    private ScreenPosition getPosition() {
        JsonObject positions = this.getModJson().get("positions").getAsJsonObject();
        return new ScreenPosition(positions.get("x").getAsInt(), positions.get("y").getAsInt());
    }

    private void savePosition() {
        String modName;
        JsonObject json = this.getJson();
        JsonObject modJson = json.has(modName = this.getClass().getSimpleName()) ? json.get(modName).getAsJsonObject() : new JsonObject();
        JsonObject positions = new JsonObject();
        positions.add("x", new JsonPrimitive(this.screenpos.getAbsoluteX()));
        positions.add("y", new JsonPrimitive(this.screenpos.getAbsoluteY()));
        modJson.add("positions", positions);
        Config.getInstance();
        Config.getInstance();
        Config.write(new File(Config.ROOT_DIR, "mods.json"), json);
    }

    private void saveEnabledState() {
        String modName;
        JsonObject json = this.getJson();
        JsonObject modJson = json.has(modName = this.getClass().getSimpleName()) ? json.get(modName).getAsJsonObject() : new JsonObject();
        modJson.add("enabled", new JsonPrimitive(this.isEnabled));
        Config.getInstance();
        Config.getInstance();
        Config.write(new File(Config.ROOT_DIR, "mods.json"), json);
    }

    public void setEnabledState(boolean state) {
        this.isEnabled = state;
        this.saveEnabledState();
    }

    @Override
    public boolean isEnabled() {
        return this.getModJson().has("enabled") ? this.getModJson().get("enabled").getAsBoolean() : true;
    }

    private final JsonObject getJson() {
        Config.getInstance();
        File file = new File(Config.ROOT_DIR, "mods.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Config.getInstance();
        JsonElement jsonElement = Config.read(file);
        return jsonElement.isJsonNull() ? new JsonObject() : jsonElement.getAsJsonObject();
    }

    private JsonObject getModJson() {
        String modName;
        JsonObject modJson;
        JsonObject json = this.getJson();
        JsonObject jsonObject = modJson = json.has(modName = this.getClass().getSimpleName()) ? json.get(modName).getAsJsonObject() : new JsonObject();
        if (!json.has(modName)) {
            json.add(modName, modJson);
            Config.getInstance();
            Config.getInstance();
            Config.write(new File(Config.ROOT_DIR, "mods.json"), json);
        }
        return modJson;
    }

    public final int getLineOffset(ScreenPosition screenpos, int lineNumber) {
        return screenpos.getAbsoluteY() + this.getLineOffset(lineNumber);
    }

    private int getLineOffset(int lineNumber) {
        return (this.font.FONT_HEIGHT + 3) * lineNumber;
    }

    public void setColor(int r2, int g2, int b2, int alpha) {
        this.color = new Color(r2, g2, b2, alpha);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColorBackground(Color color) {
        this.colorBackground = color;
    }

    public void setColorBackground(int r2, int g2, int b2, int alpha) {
        this.colorBackground = new Color(r2, g2, b2, alpha);
    }

    public Color getColor() {
        return this.color;
    }

    public Color getBackgroundColor() {
        return this.colorBackground;
    }

    public boolean isRGBEnabled() {
        return this.isRGBEnabled;
    }

    public void setRGBEnabled(boolean isRGB) {
        this.isRGBEnabled = isRGB;
    }

    public boolean ShowPrefix() {
        return this.showPrefix;
    }

    public void setShowPrefix(boolean showPrefix) {
        this.showPrefix = showPrefix;
    }
}

