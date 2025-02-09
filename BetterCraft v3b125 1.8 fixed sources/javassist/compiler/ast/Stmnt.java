/*
 * Decompiled with CFR 0.152.
 */
package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.TokenId;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.Visitor;

public class Stmnt
extends ASTList
implements TokenId {
    private static final long serialVersionUID = 1L;
    protected int operatorId;

    public Stmnt(int op2, ASTree _head, ASTList _tail) {
        super(_head, _tail);
        this.operatorId = op2;
    }

    public Stmnt(int op2, ASTree _head) {
        super(_head);
        this.operatorId = op2;
    }

    public Stmnt(int op2) {
        this(op2, null);
    }

    public static Stmnt make(int op2, ASTree oprand1, ASTree oprand2) {
        return new Stmnt(op2, oprand1, new ASTList(oprand2));
    }

    public static Stmnt make(int op2, ASTree op1, ASTree op22, ASTree op3) {
        return new Stmnt(op2, op1, new ASTList(op22, new ASTList(op3)));
    }

    @Override
    public void accept(Visitor v2) throws CompileError {
        v2.atStmnt(this);
    }

    public int getOperator() {
        return this.operatorId;
    }

    @Override
    protected String getTag() {
        if (this.operatorId < 128) {
            return "stmnt:" + (char)this.operatorId;
        }
        return "stmnt:" + this.operatorId;
    }
}

