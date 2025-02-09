/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.uniform;

import java.util.ArrayList;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionCached;
import net.optifine.shaders.uniform.CustomUniform;

public class CustomUniforms {
    private CustomUniform[] uniforms;
    private IExpressionCached[] expressionsCached;

    public CustomUniforms(CustomUniform[] uniforms, Map<String, IExpression> mapExpressions) {
        this.uniforms = uniforms;
        ArrayList<IExpressionCached> list = new ArrayList<IExpressionCached>();
        for (String s2 : mapExpressions.keySet()) {
            IExpression iexpression = mapExpressions.get(s2);
            if (!(iexpression instanceof IExpressionCached)) continue;
            IExpressionCached iexpressioncached = (IExpressionCached)((Object)iexpression);
            list.add(iexpressioncached);
        }
        this.expressionsCached = list.toArray(new IExpressionCached[list.size()]);
    }

    public void setProgram(int program) {
        int i2 = 0;
        while (i2 < this.uniforms.length) {
            CustomUniform customuniform = this.uniforms[i2];
            customuniform.setProgram(program);
            ++i2;
        }
    }

    public void update() {
        this.resetCache();
        int i2 = 0;
        while (i2 < this.uniforms.length) {
            CustomUniform customuniform = this.uniforms[i2];
            customuniform.update();
            ++i2;
        }
    }

    private void resetCache() {
        int i2 = 0;
        while (i2 < this.expressionsCached.length) {
            IExpressionCached iexpressioncached = this.expressionsCached[i2];
            iexpressioncached.reset();
            ++i2;
        }
    }

    public void reset() {
        int i2 = 0;
        while (i2 < this.uniforms.length) {
            CustomUniform customuniform = this.uniforms[i2];
            customuniform.reset();
            ++i2;
        }
    }
}

