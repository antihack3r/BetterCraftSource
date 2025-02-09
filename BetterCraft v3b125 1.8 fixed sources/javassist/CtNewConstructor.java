/*
 * Decompiled with CFR 0.152.
 */
package javassist;

import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewWrappedConstructor;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

public class CtNewConstructor {
    public static final int PASS_NONE = 0;
    public static final int PASS_ARRAY = 1;
    public static final int PASS_PARAMS = 2;

    public static CtConstructor make(String src, CtClass declaring) throws CannotCompileException {
        Javac compiler = new Javac(declaring);
        try {
            CtMember obj = compiler.compile(src);
            if (obj instanceof CtConstructor) {
                return (CtConstructor)obj;
            }
        }
        catch (CompileError e2) {
            throw new CannotCompileException(e2);
        }
        throw new CannotCompileException("not a constructor");
    }

    public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, String body, CtClass declaring) throws CannotCompileException {
        try {
            CtConstructor cc2 = new CtConstructor(parameters, declaring);
            cc2.setExceptionTypes(exceptions);
            cc2.setBody(body);
            return cc2;
        }
        catch (NotFoundException e2) {
            throw new CannotCompileException(e2);
        }
    }

    public static CtConstructor copy(CtConstructor c2, CtClass declaring, ClassMap map) throws CannotCompileException {
        return new CtConstructor(c2, declaring, map);
    }

    public static CtConstructor defaultConstructor(CtClass declaring) throws CannotCompileException {
        CtConstructor cons = new CtConstructor((CtClass[])null, declaring);
        ConstPool cp2 = declaring.getClassFile2().getConstPool();
        Bytecode code = new Bytecode(cp2, 1, 1);
        code.addAload(0);
        try {
            code.addInvokespecial(declaring.getSuperclass(), "<init>", "()V");
        }
        catch (NotFoundException e2) {
            throw new CannotCompileException(e2);
        }
        code.add(177);
        cons.getMethodInfo2().setCodeAttribute(code.toCodeAttribute());
        return cons;
    }

    public static CtConstructor skeleton(CtClass[] parameters, CtClass[] exceptions, CtClass declaring) throws CannotCompileException {
        return CtNewConstructor.make(parameters, exceptions, 0, null, null, declaring);
    }

    public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, CtClass declaring) throws CannotCompileException {
        return CtNewConstructor.make(parameters, exceptions, 2, null, null, declaring);
    }

    public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, int howto, CtMethod body, CtMethod.ConstParameter cparam, CtClass declaring) throws CannotCompileException {
        return CtNewWrappedConstructor.wrapped(parameters, exceptions, howto, body, cparam, declaring);
    }
}

