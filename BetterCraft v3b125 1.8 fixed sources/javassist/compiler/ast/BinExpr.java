/*
 * Decompiled with CFR 0.152.
 */
package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.Visitor;

public class BinExpr
extends Expr {
    private static final long serialVersionUID = 1L;

    private BinExpr(int op2, ASTree _head, ASTList _tail) {
        super(op2, _head, _tail);
    }

    public static BinExpr makeBin(int op2, ASTree oprand1, ASTree oprand2) {
        return new BinExpr(op2, oprand1, new ASTList(oprand2));
    }

    @Override
    public void accept(Visitor v2) throws CompileError {
        v2.atBinExpr(this);
    }
}

