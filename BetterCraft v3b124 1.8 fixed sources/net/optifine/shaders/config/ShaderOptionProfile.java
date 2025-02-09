/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.config;

import java.util.ArrayList;
import net.optifine.Lang;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderProfile;

public class ShaderOptionProfile
extends ShaderOption {
    private ShaderProfile[] profiles = null;
    private ShaderOption[] options = null;
    private static final String NAME_PROFILE = "<profile>";
    private static final String VALUE_CUSTOM = "<custom>";

    public ShaderOptionProfile(ShaderProfile[] profiles, ShaderOption[] options) {
        super(NAME_PROFILE, "", ShaderOptionProfile.detectProfileName(profiles, options), ShaderOptionProfile.getProfileNames(profiles), ShaderOptionProfile.detectProfileName(profiles, options, true), null);
        this.profiles = profiles;
        this.options = options;
    }

    @Override
    public void nextValue() {
        super.nextValue();
        if (this.getValue().equals(VALUE_CUSTOM)) {
            super.nextValue();
        }
        this.applyProfileOptions();
    }

    public void updateProfile() {
        ShaderProfile shaderprofile = this.getProfile(this.getValue());
        if (shaderprofile == null || !ShaderUtils.matchProfile(shaderprofile, this.options, false)) {
            String s2 = ShaderOptionProfile.detectProfileName(this.profiles, this.options);
            this.setValue(s2);
        }
    }

    private void applyProfileOptions() {
        ShaderProfile shaderprofile = this.getProfile(this.getValue());
        if (shaderprofile != null) {
            String[] astring = shaderprofile.getOptions();
            int i2 = 0;
            while (i2 < astring.length) {
                String s2 = astring[i2];
                ShaderOption shaderoption = this.getOption(s2);
                if (shaderoption != null) {
                    String s1 = shaderprofile.getValue(s2);
                    shaderoption.setValue(s1);
                }
                ++i2;
            }
        }
    }

    private ShaderOption getOption(String name) {
        int i2 = 0;
        while (i2 < this.options.length) {
            ShaderOption shaderoption = this.options[i2];
            if (shaderoption.getName().equals(name)) {
                return shaderoption;
            }
            ++i2;
        }
        return null;
    }

    private ShaderProfile getProfile(String name) {
        int i2 = 0;
        while (i2 < this.profiles.length) {
            ShaderProfile shaderprofile = this.profiles[i2];
            if (shaderprofile.getName().equals(name)) {
                return shaderprofile;
            }
            ++i2;
        }
        return null;
    }

    @Override
    public String getNameText() {
        return Lang.get("of.shaders.profile");
    }

    @Override
    public String getValueText(String val) {
        return val.equals(VALUE_CUSTOM) ? Lang.get("of.general.custom", VALUE_CUSTOM) : Shaders.translate("profile." + val, val);
    }

    @Override
    public String getValueColor(String val) {
        return val.equals(VALUE_CUSTOM) ? "\u00a7c" : "\u00a7a";
    }

    @Override
    public String getDescriptionText() {
        String s2 = Shaders.translate("profile.comment", null);
        if (s2 != null) {
            return s2;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i2 = 0;
        while (i2 < this.profiles.length) {
            String s22;
            String s1 = this.profiles[i2].getName();
            if (s1 != null && (s22 = Shaders.translate("profile." + s1 + ".comment", null)) != null) {
                stringbuffer.append(s22);
                if (!s22.endsWith(". ")) {
                    stringbuffer.append(". ");
                }
            }
            ++i2;
        }
        return stringbuffer.toString();
    }

    private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts) {
        return ShaderOptionProfile.detectProfileName(profs, opts, false);
    }

    private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
        ShaderProfile shaderprofile = ShaderUtils.detectProfile(profs, opts, def);
        return shaderprofile == null ? VALUE_CUSTOM : shaderprofile.getName();
    }

    private static String[] getProfileNames(ShaderProfile[] profs) {
        ArrayList<String> list = new ArrayList<String>();
        int i2 = 0;
        while (i2 < profs.length) {
            ShaderProfile shaderprofile = profs[i2];
            list.add(shaderprofile.getName());
            ++i2;
        }
        list.add(VALUE_CUSTOM);
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}

