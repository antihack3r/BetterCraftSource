// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.util;

import java.util.Locale;
import net.minecraft.util.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;

public class JsonBlendingMode
{
    private static JsonBlendingMode lastApplied;
    private final int srcColorFactor;
    private final int srcAlphaFactor;
    private final int destColorFactor;
    private final int destAlphaFactor;
    private final int blendFunction;
    private final boolean separateBlend;
    private final boolean opaque;
    
    private JsonBlendingMode(final boolean p_i45084_1_, final boolean p_i45084_2_, final int p_i45084_3_, final int p_i45084_4_, final int p_i45084_5_, final int p_i45084_6_, final int p_i45084_7_) {
        this.separateBlend = p_i45084_1_;
        this.srcColorFactor = p_i45084_3_;
        this.destColorFactor = p_i45084_4_;
        this.srcAlphaFactor = p_i45084_5_;
        this.destAlphaFactor = p_i45084_6_;
        this.opaque = p_i45084_2_;
        this.blendFunction = p_i45084_7_;
    }
    
    public JsonBlendingMode() {
        this(false, true, 1, 0, 1, 0, 32774);
    }
    
    public JsonBlendingMode(final int p_i45085_1_, final int p_i45085_2_, final int p_i45085_3_) {
        this(false, false, p_i45085_1_, p_i45085_2_, p_i45085_1_, p_i45085_2_, p_i45085_3_);
    }
    
    public JsonBlendingMode(final int p_i45086_1_, final int p_i45086_2_, final int p_i45086_3_, final int p_i45086_4_, final int p_i45086_5_) {
        this(true, false, p_i45086_1_, p_i45086_2_, p_i45086_3_, p_i45086_4_, p_i45086_5_);
    }
    
    public void apply() {
        if (!this.equals(JsonBlendingMode.lastApplied)) {
            if (JsonBlendingMode.lastApplied == null || this.opaque != JsonBlendingMode.lastApplied.isOpaque()) {
                JsonBlendingMode.lastApplied = this;
                if (this.opaque) {
                    GlStateManager.disableBlend();
                    return;
                }
                GlStateManager.enableBlend();
            }
            GlStateManager.glBlendEquation(this.blendFunction);
            if (this.separateBlend) {
                GlStateManager.tryBlendFuncSeparate(this.srcColorFactor, this.destColorFactor, this.srcAlphaFactor, this.destAlphaFactor);
            }
            else {
                GlStateManager.blendFunc(this.srcColorFactor, this.destColorFactor);
            }
        }
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof JsonBlendingMode)) {
            return false;
        }
        final JsonBlendingMode jsonblendingmode = (JsonBlendingMode)p_equals_1_;
        return this.blendFunction == jsonblendingmode.blendFunction && this.destAlphaFactor == jsonblendingmode.destAlphaFactor && this.destColorFactor == jsonblendingmode.destColorFactor && this.opaque == jsonblendingmode.opaque && this.separateBlend == jsonblendingmode.separateBlend && this.srcAlphaFactor == jsonblendingmode.srcAlphaFactor && this.srcColorFactor == jsonblendingmode.srcColorFactor;
    }
    
    @Override
    public int hashCode() {
        int i = this.srcColorFactor;
        i = 31 * i + this.srcAlphaFactor;
        i = 31 * i + this.destColorFactor;
        i = 31 * i + this.destAlphaFactor;
        i = 31 * i + this.blendFunction;
        i = 31 * i + (this.separateBlend ? 1 : 0);
        i = 31 * i + (this.opaque ? 1 : 0);
        return i;
    }
    
    public boolean isOpaque() {
        return this.opaque;
    }
    
    public static JsonBlendingMode parseBlendNode(final JsonObject json) {
        if (json == null) {
            return new JsonBlendingMode();
        }
        int i = 32774;
        int j = 1;
        int k = 0;
        int l = 1;
        int i2 = 0;
        boolean flag = true;
        boolean flag2 = false;
        if (JsonUtils.isString(json, "func")) {
            i = stringToBlendFunction(json.get("func").getAsString());
            if (i != 32774) {
                flag = false;
            }
        }
        if (JsonUtils.isString(json, "srcrgb")) {
            j = stringToBlendFactor(json.get("srcrgb").getAsString());
            if (j != 1) {
                flag = false;
            }
        }
        if (JsonUtils.isString(json, "dstrgb")) {
            k = stringToBlendFactor(json.get("dstrgb").getAsString());
            if (k != 0) {
                flag = false;
            }
        }
        if (JsonUtils.isString(json, "srcalpha")) {
            l = stringToBlendFactor(json.get("srcalpha").getAsString());
            if (l != 1) {
                flag = false;
            }
            flag2 = true;
        }
        if (JsonUtils.isString(json, "dstalpha")) {
            i2 = stringToBlendFactor(json.get("dstalpha").getAsString());
            if (i2 != 0) {
                flag = false;
            }
            flag2 = true;
        }
        if (flag) {
            return new JsonBlendingMode();
        }
        return flag2 ? new JsonBlendingMode(j, k, l, i2, i) : new JsonBlendingMode(j, k, i);
    }
    
    private static int stringToBlendFunction(final String p_148108_0_) {
        final String s = p_148108_0_.trim().toLowerCase(Locale.ROOT);
        if ("add".equals(s)) {
            return 32774;
        }
        if ("subtract".equals(s)) {
            return 32778;
        }
        if ("reversesubtract".equals(s)) {
            return 32779;
        }
        if ("reverse_subtract".equals(s)) {
            return 32779;
        }
        if ("min".equals(s)) {
            return 32775;
        }
        return "max".equals(s) ? 32776 : 32774;
    }
    
    private static int stringToBlendFactor(final String p_148107_0_) {
        String s = p_148107_0_.trim().toLowerCase(Locale.ROOT);
        s = s.replaceAll("_", "");
        s = s.replaceAll("one", "1");
        s = s.replaceAll("zero", "0");
        s = s.replaceAll("minus", "-");
        if ("0".equals(s)) {
            return 0;
        }
        if ("1".equals(s)) {
            return 1;
        }
        if ("srccolor".equals(s)) {
            return 768;
        }
        if ("1-srccolor".equals(s)) {
            return 769;
        }
        if ("dstcolor".equals(s)) {
            return 774;
        }
        if ("1-dstcolor".equals(s)) {
            return 775;
        }
        if ("srcalpha".equals(s)) {
            return 770;
        }
        if ("1-srcalpha".equals(s)) {
            return 771;
        }
        if ("dstalpha".equals(s)) {
            return 772;
        }
        return "1-dstalpha".equals(s) ? 773 : -1;
    }
}
