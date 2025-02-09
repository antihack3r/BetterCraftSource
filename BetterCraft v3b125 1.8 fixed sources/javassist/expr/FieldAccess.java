/*
 * Decompiled with CFR 0.152.
 */
package javassist.expr;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtPrimitiveType;
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

public class FieldAccess
extends Expr {
    int opcode;

    protected FieldAccess(int pos, CodeIterator i2, CtClass declaring, MethodInfo m2, int op2) {
        super(pos, i2, declaring, m2);
        this.opcode = op2;
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

    public boolean isStatic() {
        return FieldAccess.isStatic(this.opcode);
    }

    static boolean isStatic(int c2) {
        return c2 == 178 || c2 == 179;
    }

    public boolean isReader() {
        return this.opcode == 180 || this.opcode == 178;
    }

    public boolean isWriter() {
        return this.opcode == 181 || this.opcode == 179;
    }

    private CtClass getCtClass() throws NotFoundException {
        return this.thisClass.getClassPool().get(this.getClassName());
    }

    public String getClassName() {
        int index = this.iterator.u16bitAt(this.currentPos + 1);
        return this.getConstPool().getFieldrefClassName(index);
    }

    public String getFieldName() {
        int index = this.iterator.u16bitAt(this.currentPos + 1);
        return this.getConstPool().getFieldrefName(index);
    }

    public CtField getField() throws NotFoundException {
        CtClass cc2 = this.getCtClass();
        int index = this.iterator.u16bitAt(this.currentPos + 1);
        ConstPool cp2 = this.getConstPool();
        return cc2.getField(cp2.getFieldrefName(index), cp2.getFieldrefType(index));
    }

    @Override
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }

    public String getSignature() {
        int index = this.iterator.u16bitAt(this.currentPos + 1);
        return this.getConstPool().getFieldrefType(index);
    }

    @Override
    public void replace(String statement) throws CannotCompileException {
        this.thisClass.getClassFile();
        ConstPool constPool = this.getConstPool();
        int pos = this.currentPos;
        int index = this.iterator.u16bitAt(pos + 1);
        Javac jc2 = new Javac(this.thisClass);
        CodeAttribute ca2 = this.iterator.get();
        try {
            CtClass retType;
            CtClass[] params;
            CtClass fieldType = Descriptor.toCtClass(constPool.getFieldrefType(index), this.thisClass.getClassPool());
            boolean read = this.isReader();
            if (read) {
                params = new CtClass[]{};
                retType = fieldType;
            } else {
                params = new CtClass[]{fieldType};
                retType = CtClass.voidType;
            }
            int paramVar = ca2.getMaxLocals();
            jc2.recordParams(constPool.getFieldrefClassName(index), params, true, paramVar, this.withinStatic());
            boolean included = FieldAccess.checkResultValue(retType, statement);
            if (read) {
                included = true;
            }
            int retVar = jc2.recordReturnType(retType, included);
            if (read) {
                jc2.recordProceed(new ProceedForRead(retType, this.opcode, index, paramVar));
            } else {
                jc2.recordType(fieldType);
                jc2.recordProceed(new ProceedForWrite(params[0], this.opcode, index, paramVar));
            }
            Bytecode bytecode = jc2.getBytecode();
            FieldAccess.storeStack(params, this.isStatic(), paramVar, bytecode);
            jc2.recordLocalVariables(ca2, pos);
            if (included) {
                if (retType == CtClass.voidType) {
                    bytecode.addOpcode(1);
                    bytecode.addAstore(retVar);
                } else {
                    bytecode.addConstZero(retType);
                    bytecode.addStore(retVar, retType);
                }
            }
            jc2.compileStmnt(statement);
            if (read) {
                bytecode.addLoad(retVar, retType);
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

    static class ProceedForWrite
    implements ProceedHandler {
        CtClass fieldType;
        int opcode;
        int targetVar;
        int index;

        ProceedForWrite(CtClass type, int op2, int i2, int var) {
            this.fieldType = type;
            this.targetVar = var;
            this.opcode = op2;
            this.index = i2;
        }

        @Override
        public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
            int stack;
            if (gen.getMethodArgsLength(args) != 1) {
                throw new CompileError("$proceed() cannot take more than one parameter for field writing");
            }
            if (FieldAccess.isStatic(this.opcode)) {
                stack = 0;
            } else {
                stack = -1;
                bytecode.addAload(this.targetVar);
            }
            gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
            gen.doNumCast(this.fieldType);
            stack = this.fieldType instanceof CtPrimitiveType ? (stack -= ((CtPrimitiveType)this.fieldType).getDataSize()) : --stack;
            bytecode.add(this.opcode);
            bytecode.addIndex(this.index);
            bytecode.growStack(stack);
            gen.setType(CtClass.voidType);
            gen.addNullIfVoid();
        }

        @Override
        public void setReturnType(JvstTypeChecker c2, ASTList args) throws CompileError {
            c2.atMethodArgs(args, new int[1], new int[1], new String[1]);
            c2.setType(CtClass.voidType);
            c2.addNullIfVoid();
        }
    }

    static class ProceedForRead
    implements ProceedHandler {
        CtClass fieldType;
        int opcode;
        int targetVar;
        int index;

        ProceedForRead(CtClass type, int op2, int i2, int var) {
            this.fieldType = type;
            this.targetVar = var;
            this.opcode = op2;
            this.index = i2;
        }

        @Override
        public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
            int stack;
            if (args != null && !gen.isParamListName(args)) {
                throw new CompileError("$proceed() cannot take a parameter for field reading");
            }
            if (FieldAccess.isStatic(this.opcode)) {
                stack = 0;
            } else {
                stack = -1;
                bytecode.addAload(this.targetVar);
            }
            stack = this.fieldType instanceof CtPrimitiveType ? (stack += ((CtPrimitiveType)this.fieldType).getDataSize()) : ++stack;
            bytecode.add(this.opcode);
            bytecode.addIndex(this.index);
            bytecode.growStack(stack);
            gen.setType(this.fieldType);
        }

        @Override
        public void setReturnType(JvstTypeChecker c2, ASTList args) throws CompileError {
            c2.setType(this.fieldType);
        }
    }
}

