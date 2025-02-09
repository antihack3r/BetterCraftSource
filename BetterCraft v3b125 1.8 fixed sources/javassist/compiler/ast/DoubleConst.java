/*
 * Decompiled with CFR 0.152.
 */
package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.IntConst;
import javassist.compiler.ast.Visitor;

public class DoubleConst
extends ASTree {
    private static final long serialVersionUID = 1L;
    protected double value;
    protected int type;

    public DoubleConst(double v2, int tokenId) {
        this.value = v2;
        this.type = tokenId;
    }

    public double get() {
        return this.value;
    }

    public void set(double v2) {
        this.value = v2;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return Double.toString(this.value);
    }

    @Override
    public void accept(Visitor v2) throws CompileError {
        v2.atDoubleConst(this);
    }

    public ASTree compute(int op2, ASTree right) {
        if (right instanceof IntConst) {
            return this.compute0(op2, (IntConst)right);
        }
        if (right instanceof DoubleConst) {
            return this.compute0(op2, (DoubleConst)right);
        }
        return null;
    }

    private DoubleConst compute0(int op2, DoubleConst right) {
        int newType = this.type == 405 || right.type == 405 ? 405 : 404;
        return DoubleConst.compute(op2, this.value, right.value, newType);
    }

    private DoubleConst compute0(int op2, IntConst right) {
        return DoubleConst.compute(op2, this.value, right.value, this.type);
    }

    private static DoubleConst compute(int op2, double value1, double value2, int newType) {
        double newValue;
        switch (op2) {
            case 43: {
                newValue = value1 + value2;
                break;
            }
            case 45: {
                newValue = value1 - value2;
                break;
            }
            case 42: {
                newValue = value1 * value2;
                break;
            }
            case 47: {
                newValue = value1 / value2;
                break;
            }
            case 37: {
                newValue = value1 % value2;
                break;
            }
            default: {
                return null;
            }
        }
        return new DoubleConst(newValue, newType);
    }
}

