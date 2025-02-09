/*
 * Decompiled with CFR 0.152.
 */
package javassist.expr;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.compiler.JvstCodeGen;
import javassist.compiler.JvstTypeChecker;
import javassist.compiler.ProceedHandler;
import javassist.compiler.ast.ASTList;
import javassist.expr.Expr;

public class Instanceof
extends Expr {
    protected Instanceof(int pos, CodeIterator i2, CtClass declaring, MethodInfo m2) {
        super(pos, i2, declaring, m2);
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

    public CtClass getType() throws NotFoundException {
        ConstPool cp2 = this.getConstPool();
        int pos = this.currentPos;
        int index = this.iterator.u16bitAt(pos + 1);
        String name = cp2.getClassInfo(index);
        return this.thisClass.getClassPool().getCtClass(name);
    }

    @Override
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }

    @Override
    public void replace(String statement) throws CannotCompileException {
        this.thisClass.getClassFile();
        ConstPool constPool = this.getConstPool();
        int pos = this.currentPos;
        int index = this.iterator.u16bitAt(pos + 1);
        Javac jc2 = new Javac(this.thisClass);
        ClassPool cp2 = this.thisClass.getClassPool();
        CodeAttribute ca2 = this.iterator.get();
        try {
            CtClass[] params = new CtClass[]{cp2.get("java.lang.Object")};
            CtClass retType = CtClass.booleanType;
            int paramVar = ca2.getMaxLocals();
            jc2.recordParams("java.lang.Object", params, true, paramVar, this.withinStatic());
            int retVar = jc2.recordReturnType(retType, true);
            jc2.recordProceed(new ProceedForInstanceof(index));
            jc2.recordType(this.getType());
            Instanceof.checkResultValue(retType, statement);
            Bytecode bytecode = jc2.getBytecode();
            Instanceof.storeStack(params, true, paramVar, bytecode);
            jc2.recordLocalVariables(ca2, pos);
            bytecode.addConstZero(retType);
            bytecode.addStore(retVar, retType);
            jc2.compileStmnt(statement);
            bytecode.addLoad(retVar, retType);
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

    static class ProceedForInstanceof
    implements ProceedHandler {
        int index;

        ProceedForInstanceof(int i2) {
            this.index = i2;
        }

        @Override
        public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
            if (gen.getMethodArgsLength(args) != 1) {
                throw new CompileError("$proceed() cannot take more than one parameter for instanceof");
            }
            gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
            bytecode.addOpcode(193);
            bytecode.addIndex(this.index);
            gen.setType(CtClass.booleanType);
        }

        @Override
        public void setReturnType(JvstTypeChecker c2, ASTList args) throws CompileError {
            c2.atMethodArgs(args, new int[1], new int[1], new String[1]);
            c2.setType(CtClass.booleanType);
        }
    }
}

