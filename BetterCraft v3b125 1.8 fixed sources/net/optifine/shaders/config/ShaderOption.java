/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.config;

import java.util.Arrays;
import java.util.List;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;
import net.optifine.util.StrUtils;

public abstract class ShaderOption {
    private String name = null;
    private String description = null;
    private String value = null;
    private String[] values = null;
    private String valueDefault = null;
    private String[] paths = null;
    private boolean enabled = true;
    private boolean visible = true;
    public static final String COLOR_GREEN = "\u00a7a";
    public static final String COLOR_RED = "\u00a7c";
    public static final String COLOR_BLUE = "\u00a79";

    public ShaderOption(String name, String description, String value, String[] values, String valueDefault, String path) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.values = values;
        this.valueDefault = valueDefault;
        if (path != null) {
            this.paths = new String[]{path};
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDescriptionText() {
        String s2 = Config.normalize(this.description);
        s2 = StrUtils.removePrefix(s2, "//");
        s2 = Shaders.translate("option." + this.getName() + ".comment", s2);
        return s2;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public boolean setValue(String value) {
        int i2 = ShaderOption.getIndex(value, this.values);
        if (i2 < 0) {
            return false;
        }
        this.value = value;
        return true;
    }

    public String getValueDefault() {
        return this.valueDefault;
    }

    public void resetValue() {
        this.value = this.valueDefault;
    }

    public void nextValue() {
        int i2 = ShaderOption.getIndex(this.value, this.values);
        if (i2 >= 0) {
            i2 = (i2 + 1) % this.values.length;
            this.value = this.values[i2];
        }
    }

    public void prevValue() {
        int i2 = ShaderOption.getIndex(this.value, this.values);
        if (i2 >= 0) {
            i2 = (i2 - 1 + this.values.length) % this.values.length;
            this.value = this.values[i2];
        }
    }

    private static int getIndex(String str, String[] strs) {
        int i2 = 0;
        while (i2 < strs.length) {
            String s2 = strs[i2];
            if (s2.equals(str)) {
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    public String[] getPaths() {
        return this.paths;
    }

    public void addPaths(String[] newPaths) {
        List<String> list = Arrays.asList(this.paths);
        int i2 = 0;
        while (i2 < newPaths.length) {
            String s2 = newPaths[i2];
            if (!list.contains(s2)) {
                this.paths = (String[])Config.addObjectToArray(this.paths, s2);
            }
            ++i2;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isChanged() {
        return !Config.equals(this.value, this.valueDefault);
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isValidValue(String val) {
        return ShaderOption.getIndex(val, this.values) >= 0;
    }

    public String getNameText() {
        return Shaders.translate("option." + this.name, this.name);
    }

    public String getValueText(String val) {
        return Shaders.translate("value." + this.name + "." + val, val);
    }

    public String getValueColor(String val) {
        return "";
    }

    public boolean matchesLine(String line) {
        return false;
    }

    public boolean checkUsed() {
        return false;
    }

    public boolean isUsedInLine(String line) {
        return false;
    }

    public String getSourceLine() {
        return null;
    }

    public String[] getValues() {
        return (String[])this.values.clone();
    }

    public float getIndexNormalized() {
        if (this.values.length <= 1) {
            return 0.0f;
        }
        int i2 = ShaderOption.getIndex(this.value, this.values);
        if (i2 < 0) {
            return 0.0f;
        }
        float f2 = 1.0f * (float)i2 / ((float)this.values.length - 1.0f);
        return f2;
    }

    public void setIndexNormalized(float f2) {
        if (this.values.length > 1) {
            f2 = Config.limit(f2, 0.0f, 1.0f);
            int i2 = Math.round(f2 * (float)(this.values.length - 1));
            this.value = this.values[i2];
        }
    }

    public String toString() {
        return this.name + ", value: " + this.value + ", valueDefault: " + this.valueDefault + ", paths: " + Config.arrayToString(this.paths);
    }
}

