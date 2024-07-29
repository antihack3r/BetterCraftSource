/*
 * Decompiled with CFR 0.152.
 */
package javassist.expr;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.compiler.JvstCodeGen;
import javassist.compiler.JvstTypeChecker;
import javassist.compiler.ProceedHandler;
import javassist.compiler.ast.ASTList;
import javassist.expr.Expr;

public class NewExpr
extends Expr {
    String newTypeName;
    int newPos;

    protected NewExpr(int pos, CodeIterator i2, CtClass declaring, MethodInfo m2, String type, int np2) {
        super(pos, i2, declaring, m2);
        this.newTypeName = type;
        this.newPos = np2;
    }

    @Override
    public CtBehavior where() {
        return super.where();
    }

    @Override
    public int getLineNumber() {
        return super.getLineNumber();
    }

    @Override
    public String getFileName() {
        return super.getFileName();
    }

    private CtClass getCtClass() throws NotFoundException {
        return this.thisClass.getClassPool().get(this.newTypeName);
    }

    public String getClassName() {
        return this.newTypeName;
    }

    public String getSignature() {
        ConstPool constPool = this.getConstPool();
        int methodIndex = this.iterator.u16bitAt(this.currentPos + 1);
        return constPool.getMethodrefType(methodIndex);
    }

    public CtConstructor getConstructor() throws NotFoundException {
        ConstPool cp2 = this.getConstPool();
        int index = this.iterator.u16bitAt(this.currentPos + 1);
        String desc = cp2.getMethodrefType(index);
        return this.getCtClass().getConstructor(desc);
    }

    @Override
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }

    private int canReplace() throws CannotCompileException {
        int op2 = this.iterator.byteAt(this.newPos + 3);
        if (op2 == 89) {
            return this.iterator.byteAt(this.newPos + 4) == 94 && this.iterator.byteAt(this.newPos + 5) == 88 ? 6 : 4;
        }
        if (op2 == 90 && this.iterator.byteAt(this.newPos + 4) == 95) {
            return 5;
        }
        return 3;
    }

    @Override
    public void replace(String statement) throws CannotCompileException {
        this.thisClass.getClassFile();
        int bytecodeSize = 3;
        int pos = this.newPos;
        int newIndex = this.iterator.u16bitAt(pos + 1);
        int codeSize = this.canReplace();
        int end = pos + codeSize;
        for (int i2 = pos; i2 < end; ++i2) {
            this.iterator.writeByte(0, i2);
        }
        ConstPool constPool = this.getConstPool();
        pos = this.currentPos;
        int methodIndex = this.iterator.u16bitAt(pos + 1);
        String signature = constPool.getMethodrefType(methodIndex);
        Javac jc2 = new Javac(this.thisClass);
        ClassPool cp2 = this.thisClass.getClassPool();
        CodeAttribute ca2 = this.iterator.get();
        try {
            CtClass[] params = Descriptor.getParameterTypes(signature, cp2);
            CtClass newType = cp2.get(this.newTypeName);
            int paramVar = ca2.getMaxLocals();
            jc2.recordParams(this.newTypeName, params, true, paramVar, this.withinStatic());
            int retVar = jc2.recordReturnType(newType, true);
            jc2.recordProceed(new ProceedForNew(newType, newIndex, methodIndex));
            NewExpr.checkResultValue(newType, statement);
            Bytecode bytecode = jc2.getBytecode();
            NewExpr.storeStack(params, true, paramVar, bytecode);
            jc2.recordLocalVariables(ca2, pos);
            bytecode.addConstZero(newType);
            bytecode.addStore(retVar, newType);
            jc2.compileStmnt(statement);
            if (codeSize > 3) {
                bytecode.addAload(retVar);
            }
            this.replace0(pos, bytecode, 3);
        }
        catch (CompileError e2) {
            throw new CannotCompileException(e2);
        }
        catch (NotFoundException e3) {
            throw new CannotCompileException(e3);
        }
        catch (BadBytecode e4) {
            throw new CannotCompileException("broken method");
        }
    }

    static class ProceedForNew
    implements ProceedHandler {
        CtClass newType;
        int newIndex;
        int methodIndex;

        ProceedForNew(CtClass nt2, int ni2, int mi) {
            this.newType = nt2;
            this.newIndex = ni2;
            this.methodIndex = mi;
        }

        @Override
        public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
            bytecode.addOpcode(187);
            bytecode.addIndex(this.newIndex);
            bytecode.addOpcode(89);
            gen.atMethodCallCore(this.newType, "<init>", args, false, true, -1, null);
            gen.setType(this.newType);
        }

        @Override
        public void setReturnType(JvstTypeChecker c2, ASTList args) throws CompileError {
            c2.atMethodCallCore(this.newType, "<init>", args);
            c2.setType(this.newType);
        }
    }
}

