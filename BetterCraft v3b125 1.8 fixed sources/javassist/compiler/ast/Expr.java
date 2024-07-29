/*
 * Decompiled with CFR 0.152.
 */
package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.TokenId;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.Visitor;

public class Expr
extends ASTList
implements TokenId {
    private static final long serialVersionUID = 1L;
    protected int operatorId;

    Expr(int op2, ASTree _head, ASTList _tail) {
        super(_head, _tail);
        this.operatorId = op2;
    }

    Expr(int op2, ASTree _head) {
        super(_head);
        this.operatorId = op2;
    }

    public static Expr make(int op2, ASTree oprand1, ASTree oprand2) {
        return new Expr(op2, oprand1, new ASTList(oprand2));
    }

    public static Expr make(int op2, ASTree oprand1) {
        return new Expr(op2, oprand1);
    }

    public int getOperator() {
        return this.operatorId;
    }

    public void setOperator(int op2) {
        this.operatorId = op2;
    }

    public ASTree oprand1() {
        return this.getLeft();
    }

    public void setOprand1(ASTree expr) {
        this.setLeft(expr);
    }

    public ASTree oprand2() {
        return this.getRight().getLeft();
    }

    public void setOprand2(ASTree expr) {
        this.getRight().setLeft(expr);
    }

    @Override
    public void accept(Visitor v2) throws CompileError {
        v2.atExpr(this);
    }

    public String getName() {
        int id2 = this.operatorId;
        if (id2 < 128) {
            return String.valueOf((char)id2);
        }
        if (350 <= id2 && id2 <= 371) {
            return opNames[id2 - 350];
        }
        if (id2 == 323) {
            return "instanceof";
        }
        return String.valueOf(id2);
    }

    @Override
    protected String getTag() {
        return "op:" + this.getName();
    }
}

