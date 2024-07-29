/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import net.minecraft.src.Config;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderProfile;

public class ShaderUtils {
    public static ShaderOption getShaderOption(String name, ShaderOption[] opts) {
        if (opts == null) {
            return null;
        }
        int i2 = 0;
        while (i2 < opts.length) {
            ShaderOption shaderoption = opts[i2];
            if (shaderoption.getName().equals(name)) {
                return shaderoption;
            }
            ++i2;
        }
        return null;
    }

    public static ShaderProfile detectProfile(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
        if (profs == null) {
            return null;
        }
        int i2 = 0;
        while (i2 < profs.length) {
            ShaderProfile shaderprofile = profs[i2];
            if (ShaderUtils.matchProfile(shaderprofile, opts, def)) {
                return shaderprofile;
            }
            ++i2;
        }
        return null;
    }

    public static boolean matchProfile(ShaderProfile prof, ShaderOption[] opts, boolean def) {
        if (prof == null) {
            return false;
        }
        if (opts == null) {
            return false;
        }
        String[] astring = prof.getOptions();
        int i2 = 0;
        while (i2 < astring.length) {
            String s2;
            String s1;
            String s3 = astring[i2];
            ShaderOption shaderoption = ShaderUtils.getShaderOption(s3, opts);
            if (shaderoption != null && !Config.equals(s1 = def ? shaderoption.getValueDefault() : shaderoption.getValue(), s2 = prof.getValue(s3))) {
                return false;
            }
            ++i2;
        }
        return true;
    }
}

