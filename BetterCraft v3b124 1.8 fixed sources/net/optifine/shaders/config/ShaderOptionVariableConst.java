/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionVariable;
import net.optifine.util.StrUtils;

public class ShaderOptionVariableConst
extends ShaderOptionVariable {
    private String type = null;
    private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*(float|int)\\s*([A-Za-z0-9_]+)\\s*=\\s*(-?[0-9\\.]+f?F?)\\s*;\\s*(//.*)?$");

    public ShaderOptionVariableConst(String name, String type, String description, String value, String[] values, String path) {
        super(name, description, value, values, path);
        this.type = type;
    }

    @Override
    public String getSourceLine() {
        return "const " + this.type + " " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
    }

    @Override
    public boolean matchesLine(String line) {
        Matcher matcher = PATTERN_CONST.matcher(line);
        if (!matcher.matches()) {
            return false;
        }
        String s2 = matcher.group(2);
        return s2.matches(this.getName());
    }

    public static ShaderOption parseOption(String line, String path) {
        Matcher matcher = PATTERN_CONST.matcher(line);
        if (!matcher.matches()) {
            return null;
        }
        String s2 = matcher.group(1);
        String s1 = matcher.group(2);
        String s22 = matcher.group(3);
        String s3 = matcher.group(4);
        String s4 = StrUtils.getSegment(s3, "[", "]");
        if (s4 != null && s4.length() > 0) {
            s3 = s3.replace(s4, "").trim();
        }
        String[] astring = ShaderOptionVariableConst.parseValues(s22, s4);
        if (s1 != null && s1.length() > 0) {
            path = StrUtils.removePrefix(path, "/shaders/");
            ShaderOptionVariableConst shaderoption = new ShaderOptionVariableConst(s1, s2, s3, s22, astring, path);
            return shaderoption;
        }
        return null;
    }
}

