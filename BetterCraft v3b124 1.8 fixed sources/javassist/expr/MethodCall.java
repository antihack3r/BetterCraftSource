/*
 * Decompiled with CFR 0.152.
 */
package javassist.expr;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
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
import javassist.expr.Expr;

public class MethodCall
extends Expr {
    protected MethodCall(int pos, CodeIterator i2, CtClass declaring, MethodInfo m2) {
        super(pos, i2, declaring, m2);
    }

    private int getNameAndType(ConstPool cp2) {
        int pos = this.currentPos;
        int c2 = this.iterator.byteAt(pos);
        int index = this.iterator.u16bitAt(pos + 1);
        if (c2 == 185) {
            return cp2.getInterfaceMethodrefNameAndType(index);
        }
        return cp2.getMethodrefNameAndType(index);
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

    protected CtClass getCtClass() throws NotFoundException {
        return this.thisClass.getClassPool().get(this.getClassName());
    }

    public String getClassName() {
        ConstPool cp2 = this.getConstPool();
        int pos = this.currentPos;
        int c2 = this.iterator.byteAt(pos);
        int index = this.iterator.u16bitAt(pos + 1);
        String cname = c2 == 185 ? cp2.getInterfaceMethodrefClassName(index) : cp2.getMethodrefClassName(index);
        if (cname.charAt(0) == '[') {
            cname = Descriptor.toClassName(cname);
        }
        return cname;
    }

    public String getMethodName() {
        ConstPool cp2 = this.getConstPool();
        int nt2 = this.getNameAndType(cp2);
        return cp2.getUtf8Info(cp2.getNameAndTypeName(nt2));
    }

    public CtMethod getMethod() throws NotFoundException {
        return this.getCtClass().getMethod(this.getMethodName(), this.getSignature());
    }

    public String getSignature() {
        ConstPool cp2 = this.getConstPool();
        int nt2 = this.getNameAndType(cp2);
        return cp2.getUtf8Info(cp2.getNameAndTypeDescriptor(nt2));
    }

    @Override
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }

    public boolean isSuper() {
        return this.iterator.byteAt(this.currentPos) == 183 && !this.where().getDeclaringClass().getName().equals(this.getClassName());
    }

    @Override
    public void replace(String statement) throws CannotCompileException {
        String signature;
        String methodname;
        String classname;
        int opcodeSize;
        this.thisClass.getClassFile();
        ConstPool constPool = this.getConstPool();
        int pos = this.currentPos;
        int index = this.iterator.u16bitAt(pos + 1);
        int c2 = this.iterator.byteAt(pos);
        if (c2 == 185) {
            opcodeSize = 5;
            classname = constPool.getInterfaceMethodrefClassName(index);
            methodname = constPool.getInterfaceMethodrefName(index);
            signature = constPool.getInterfaceMethodrefType(index);
        } else if (c2 == 184 || c2 == 183 || c2 == 182) {
            opcodeSize = 3;
            classname = constPool.getMethodrefClassName(index);
            methodname = constPool.getMethodrefName(index);
            signature = constPool.getMethodrefType(index);
        } else {
            throw new CannotCompileException("not method invocation");
        }
        Javac jc2 = new Javac(this.thisClass);
        ClassPool cp2 = this.thisClass.getClassPool();
        CodeAttribute ca2 = this.iterator.get();
        try {
            CtClass[] params = Descriptor.getParameterTypes(signature, cp2);
            CtClass retType = Descriptor.getReturnType(signature, cp2);
            int paramVar = ca2.getMaxLocals();
            jc2.recordParams(classname, params, true, paramVar, this.withinStatic());
            int retVar = jc2.recordReturnType(retType, true);
            if (c2 == 184) {
                jc2.recordStaticProceed(classname, methodname);
            } else if (c2 == 183) {
                jc2.recordSpecialProceed("$0", classname, methodname, signature, index);
            } else {
                jc2.recordProceed("$0", methodname);
            }
            MethodCall.checkResultValue(retType, statement);
            Bytecode bytecode = jc2.getBytecode();
            MethodCall.storeStack(params, c2 == 184, paramVar, bytecode);
            jc2.recordLocalVariables(ca2, pos);
            if (retType != CtClass.voidType) {
                bytecode.addConstZero(retType);
                bytecode.addStore(retVar, retType);
            }
            jc2.compileStmnt(statement);
            if (retType != CtClass.voidType) {
                bytecode.addLoad(retVar, retType);
            }
            this.replace0(pos, bytecode, opcodeSize);
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
}

