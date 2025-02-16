/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model.anim;

import net.minecraft.src.Config;
import net.optifine.entity.model.anim.IModelResolver;
import net.optifine.entity.model.anim.ModelVariableFloat;
import net.optifine.expr.ExpressionParser;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.ParseException;

public class ModelVariableUpdater {
    private String modelVariableName;
    private String expressionText;
    private ModelVariableFloat modelVariable;
    private IExpressionFloat expression;

    public boolean initialize(IModelResolver mr2) {
        this.modelVariable = mr2.getModelVariable(this.modelVariableName);
        if (this.modelVariable == null) {
            Config.warn("Model variable not found: " + this.modelVariableName);
            return false;
        }
        try {
            ExpressionParser expressionparser = new ExpressionParser(mr2);
            this.expression = expressionparser.parseFloat(this.expressionText);
            return true;
        }
        catch (ParseException parseexception) {
            Config.warn("Error parsing expression: " + this.expressionText);
            Config.warn(String.valueOf(parseexception.getClass().getName()) + ": " + parseexception.getMessage());
            return false;
        }
    }

    public ModelVariableUpdater(String modelVariableName, String expressionText) {
        this.modelVariableName = modelVariableName;
        this.expressionText = expressionText;
    }

    public void update() {
        float f2 = this.expression.eval();
        this.modelVariable.setValue(f2);
    }
}

