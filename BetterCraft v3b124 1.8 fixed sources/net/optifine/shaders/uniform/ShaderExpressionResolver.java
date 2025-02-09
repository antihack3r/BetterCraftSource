/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.uniform;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.uniform.ShaderParameterBool;
import net.optifine.shaders.uniform.ShaderParameterFloat;
import net.optifine.shaders.uniform.ShaderParameterIndexed;

public class ShaderExpressionResolver
implements IExpressionResolver {
    private Map<String, IExpression> mapExpressions = new HashMap<String, IExpression>();

    public ShaderExpressionResolver(Map<String, IExpression> map) {
        this.registerExpressions();
        for (String s2 : map.keySet()) {
            IExpression iexpression = map.get(s2);
            this.registerExpression(s2, iexpression);
        }
    }

    private void registerExpressions() {
        ShaderParameterFloat[] ashaderparameterfloat = ShaderParameterFloat.values();
        int i2 = 0;
        while (i2 < ashaderparameterfloat.length) {
            ShaderParameterFloat shaderparameterfloat = ashaderparameterfloat[i2];
            this.addParameterFloat(this.mapExpressions, shaderparameterfloat);
            ++i2;
        }
        ShaderParameterBool[] ashaderparameterbool = ShaderParameterBool.values();
        int k2 = 0;
        while (k2 < ashaderparameterbool.length) {
            ShaderParameterBool shaderparameterbool = ashaderparameterbool[k2];
            this.mapExpressions.put(shaderparameterbool.getName(), shaderparameterbool);
            ++k2;
        }
        for (BiomeGenBase biomegenbase : BiomeGenBase.BIOME_ID_MAP.values()) {
            String s2 = biomegenbase.biomeName.trim();
            s2 = "BIOME_" + s2.toUpperCase().replace(' ', '_');
            int j2 = biomegenbase.biomeID;
            ConstantFloat iexpression = new ConstantFloat(j2);
            this.registerExpression(s2, iexpression);
        }
    }

    private void addParameterFloat(Map<String, IExpression> map, ShaderParameterFloat spf) {
        String[] astring = spf.getIndexNames1();
        if (astring == null) {
            map.put(spf.getName(), new ShaderParameterIndexed(spf));
        } else {
            int i2 = 0;
            while (i2 < astring.length) {
                String s2 = astring[i2];
                String[] astring1 = spf.getIndexNames2();
                if (astring1 == null) {
                    map.put(String.valueOf(spf.getName()) + "." + s2, new ShaderParameterIndexed(spf, i2));
                } else {
                    int j2 = 0;
                    while (j2 < astring1.length) {
                        String s1 = astring1[j2];
                        map.put(String.valueOf(spf.getName()) + "." + s2 + "." + s1, new ShaderParameterIndexed(spf, i2, j2));
                        ++j2;
                    }
                }
                ++i2;
            }
        }
    }

    public boolean registerExpression(String name, IExpression expr) {
        if (this.mapExpressions.containsKey(name)) {
            SMCLog.warning("Expression already defined: " + name);
            return false;
        }
        this.mapExpressions.put(name, expr);
        return true;
    }

    @Override
    public IExpression getExpression(String name) {
        return this.mapExpressions.get(name);
    }

    public boolean hasExpression(String name) {
        return this.mapExpressions.containsKey(name);
    }
}

