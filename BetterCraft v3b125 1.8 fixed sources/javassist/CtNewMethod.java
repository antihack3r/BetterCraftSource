/*
 * Decompiled with CFR 0.152.
 */
package javassist;

import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewWrappedMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

public class CtNewMethod {
    public static CtMethod make(String src, CtClass declaring) throws CannotCompileException {
        return CtNewMethod.make(src, declaring, null, null);
    }

    public static CtMethod make(String src, CtClass declaring, String delegateObj, String delegateMethod) throws CannotCompileException {
        Javac compiler = new Javac(declaring);
        try {
            CtMember obj;
            if (delegateMethod != null) {
                compiler.recordProceed(delegateObj, delegateMethod);
            }
            if ((obj = compiler.compile(src)) instanceof CtMethod) {
                return (CtMethod)obj;
            }
        }
        catch (CompileError e2) {
            throw new CannotCompileException(e2);
        }
        throw new CannotCompileException("not a method");
    }

    public static CtMethod make(CtClass returnType, String mname, CtClass[] parameters, CtClass[] exceptions, String body, CtClass declaring) throws CannotCompileException {
        return CtNewMethod.make(1, returnType, mname, parameters, exceptions, body, declaring);
    }

    public static CtMethod make(int modifiers, CtClass returnType, String mname, CtClass[] parameters, CtClass[] exceptions, String body, CtClass declaring) throws CannotCompileException {
        try {
            CtMethod cm2 = new CtMethod(returnType, mname, parameters, declaring);
            cm2.setModifiers(modifiers);
            cm2.setExceptionTypes(exceptions);
            cm2.setBody(body);
            return cm2;
        }
        catch (NotFoundException e2) {
            throw new CannotCompileException(e2);
        }
    }

    public static CtMethod copy(CtMethod src, CtClass declaring, ClassMap map) throws CannotCompileException {
        return new CtMethod(src, declaring, map);
    }

    public static CtMethod copy(CtMethod src, String name, CtClass declaring, ClassMap map) throws CannotCompileException {
        CtMethod cm2 = new CtMethod(src, declaring, map);
        cm2.setName(name);
        return cm2;
    }

    public static CtMethod abstractMethod(CtClass returnType, String mname, CtClass[] parameters, CtClass[] exceptions, CtClass declaring) throws NotFoundException {
        CtMethod cm2 = new CtMethod(returnType, mname, parameters, declaring);
        cm2.setExceptionTypes(exceptions);
        return cm2;
    }

    public static CtMethod getter(String methodName, CtField field) throws CannotCompileException {
        FieldInfo finfo = field.getFieldInfo2();
        String fieldType = finfo.getDescriptor();
        String desc = "()" + fieldType;
        ConstPool cp2 = finfo.getConstPool();
        MethodInfo minfo = new MethodInfo(cp2, methodName, desc);
        minfo.setAccessFlags(1);
        Bytecode code = new Bytecode(cp2, 2, 1);
        try {
            String fieldName = finfo.getName();
            if ((finfo.getAccessFlags() & 8) == 0) {
                code.addAload(0);
                code.addGetfield(Bytecode.THIS, fieldName, fieldType);
            } else {
                code.addGetstatic(Bytecode.THIS, fieldName, fieldType);
            }
            code.addReturn(field.getType());
        }
        catch (NotFoundException e2) {
            throw new CannotCompileException(e2);
        }
        minfo.setCodeAttribute(code.toCodeAttribute());
        CtClass cc2 = field.getDeclaringClass();
        return new CtMethod(minfo, cc2);
    }

    public static CtMethod setter(String methodName, CtField field) throws CannotCompileException {
        FieldInfo finfo = field.getFieldInfo2();
        String fieldType = finfo.getDescriptor();
        String desc = "(" + fieldType + ")V";
        ConstPool cp2 = finfo.getConstPool();
        MethodInfo minfo = new MethodInfo(cp2, methodName, desc);
        minfo.setAccessFlags(1);
        Bytecode code = new Bytecode(cp2, 3, 3);
        try {
            String fieldName = finfo.getName();
            if ((finfo.getAccessFlags() & 8) == 0) {
                code.addAload(0);
                code.addLoad(1, field.getType());
                code.addPutfield(Bytecode.THIS, fieldName, fieldType);
            } else {
                code.addLoad(1, field.getType());
                code.addPutstatic(Bytecode.THIS, fieldName, fieldType);
            }
            code.addReturn(null);
        }
        catch (NotFoundException e2) {
            throw new CannotCompileException(e2);
        }
        minfo.setCodeAttribute(code.toCodeAttribute());
        CtClass cc2 = field.getDeclaringClass();
        return new CtMethod(minfo, cc2);
    }

    public static CtMethod delegator(CtMethod delegate, CtClass declaring) throws CannotCompileException {
        try {
            return CtNewMethod.delegator0(delegate, declaring);
        }
        catch (NotFoundException e2) {
            throw new CannotCompileException(e2);
        }
    }

    private static CtMethod delegator0(CtMethod delegate, CtClass declaring) throws CannotCompileException, NotFoundException {
        int s2;
        MethodInfo deleInfo = delegate.getMethodInfo2();
        String methodName = deleInfo.getName();
        String desc = deleInfo.getDescriptor();
        ConstPool cp2 = declaring.getClassFile2().getConstPool();
        MethodInfo minfo = new MethodInfo(cp2, methodName, desc);
        minfo.setAccessFlags(deleInfo.getAccessFlags());
        ExceptionsAttribute eattr = deleInfo.getExceptionsAttribute();
        if (eattr != null) {
            minfo.setExceptionsAttribute((ExceptionsAttribute)eattr.copy(cp2, null));
        }
        Bytecode code = new Bytecode(cp2, 0, 0);
        boolean isStatic = Modifier.isStatic(delegate.getModifiers());
        CtClass deleClass = delegate.getDeclaringClass();
        CtClass[] params = delegate.getParameterTypes();
        if (isStatic) {
            s2 = code.addLoadParameters(params, 0);
            code.addInvokestatic(deleClass, methodName, desc);
        } else {
            code.addLoad(0, deleClass);
            s2 = code.addLoadParameters(params, 1);
            code.addInvokespecial(deleClass, methodName, desc);
        }
        code.addReturn(delegate.getReturnType());
        code.setMaxLocals(++s2);
        code.setMaxStack(s2 < 2 ? 2 : s2);
        minfo.setCodeAttribute(code.toCodeAttribute());
        return new CtMethod(minfo, declaring);
    }

    public static CtMethod wrapped(CtClass returnType, String mname, CtClass[] parameterTypes, CtClass[] exceptionTypes, CtMethod body, CtMethod.ConstParameter constParam, CtClass declaring) throws CannotCompileException {
        return CtNewWrappedMethod.wrapped(returnType, mname, parameterTypes, exceptionTypes, body, constParam, declaring);
    }
}

