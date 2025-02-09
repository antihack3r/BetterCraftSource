/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.expr;

import java.util.ArrayList;
import java.util.Arrays;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IParameters;

public class ParametersVariable
implements IParameters {
    private ExpressionType[] first;
    private ExpressionType[] repeat;
    private ExpressionType[] last;
    private int maxCount = Integer.MAX_VALUE;
    private static final ExpressionType[] EMPTY = new ExpressionType[0];

    public ParametersVariable() {
        this(null, null, null);
    }

    public ParametersVariable(ExpressionType[] first, ExpressionType[] repeat, ExpressionType[] last) {
        this(first, repeat, last, Integer.MAX_VALUE);
    }

    public ParametersVariable(ExpressionType[] first, ExpressionType[] repeat, ExpressionType[] last, int maxCount) {
        this.first = ParametersVariable.normalize(first);
        this.repeat = ParametersVariable.normalize(repeat);
        this.last = ParametersVariable.normalize(last);
        this.maxCount = maxCount;
    }

    private static ExpressionType[] normalize(ExpressionType[] exprs) {
        return exprs == null ? EMPTY : exprs;
    }

    public ExpressionType[] getFirst() {
        return this.first;
    }

    public ExpressionType[] getRepeat() {
        return this.repeat;
    }

    public ExpressionType[] getLast() {
        return this.last;
    }

    public int getCountRepeat() {
        return this.first == null ? 0 : this.first.length;
    }

    @Override
    public ExpressionType[] getParameterTypes(IExpression[] arguments) {
        int i2 = this.first.length + this.last.length;
        int j2 = arguments.length - i2;
        int k2 = 0;
        int l2 = 0;
        while (l2 + this.repeat.length <= j2 && i2 + l2 + this.repeat.length <= this.maxCount) {
            ++k2;
            l2 += this.repeat.length;
        }
        ArrayList<ExpressionType> list = new ArrayList<ExpressionType>();
        list.addAll(Arrays.asList(this.first));
        int i1 = 0;
        while (i1 < k2) {
            list.addAll(Arrays.asList(this.repeat));
            ++i1;
        }
        list.addAll(Arrays.asList(this.last));
        ExpressionType[] aexpressiontype = list.toArray(new ExpressionType[list.size()]);
        return aexpressiontype;
    }

    public ParametersVariable first(ExpressionType ... first) {
        return new ParametersVariable(first, this.repeat, this.last);
    }

    public ParametersVariable repeat(ExpressionType ... repeat) {
        return new ParametersVariable(this.first, repeat, this.last);
    }

    public ParametersVariable last(ExpressionType ... last) {
        return new ParametersVariable(this.first, this.repeat, last);
    }

    public ParametersVariable maxCount(int maxCount) {
        return new ParametersVariable(this.first, this.repeat, this.last, maxCount);
    }
}

