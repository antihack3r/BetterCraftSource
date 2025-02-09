/*
 * Decompiled with CFR 0.152.
 */
package javassist.compiler;

import java.util.HashMap;
import java.util.Map;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SyntheticAttribute;
import javassist.compiler.CompileError;

public class AccessorMaker {
    private CtClass clazz;
    private int uniqueNumber;
    private Map<String, Object> accessors;
    static final String lastParamType = "javassist.runtime.Inner";

    public AccessorMaker(CtClass c2) {
        this.clazz = c2;
        this.uniqueNumber = 1;
        this.accessors = new HashMap<String, Object>();
    }

    public String getConstructor(CtClass c2, String desc, MethodInfo orig) throws CompileError {
        String key = "<init>:" + desc;
        String consDesc = (String)this.accessors.get(key);
        if (consDesc != null) {
            return consDesc;
        }
        consDesc = Descriptor.appendParameter(lastParamType, desc);
        ClassFile cf2 = this.clazz.getClassFile();
        try {
            ConstPool cp2 = cf2.getConstPool();
            ClassPool pool = this.clazz.getClassPool();
            MethodInfo minfo = new MethodInfo(cp2, "<init>", consDesc);
            minfo.setAccessFlags(0);
            minfo.addAttribute(new SyntheticAttribute(cp2));
            ExceptionsAttribute ea2 = orig.getExceptionsAttribute();
            if (ea2 != null) {
                minfo.addAttribute(ea2.copy(cp2, null));
            }
            CtClass[] params = Descriptor.getParameterTypes(desc, pool);
            Bytecode code = new Bytecode(cp2);
            code.addAload(0);
            int regno = 1;
            for (int i2 = 0; i2 < params.length; ++i2) {
                regno += code.addLoad(regno, params[i2]);
            }
            code.setMaxLocals(regno + 1);
            code.addInvokespecial(this.clazz, "<init>", desc);
            code.addReturn(null);
            minfo.setCodeAttribute(code.toCodeAttribute());
            cf2.addMethod(minfo);
        }
        catch (CannotCompileException e2) {
            throw new CompileError(e2);
        }
        catch (NotFoundException e3) {
            throw new CompileError(e3);
        }
        this.accessors.put(key, consDesc);
        return consDesc;
    }

    public String getMethodAccessor(String name, String desc, String accDesc, MethodInfo orig) throws CompileError {
        String key = name + ":" + desc;
        String accName = (String)this.accessors.get(key);
        if (accName != null) {
            return accName;
        }
        ClassFile cf2 = this.clazz.getClassFile();
        accName = this.findAccessorName(cf2);
        try {
            ConstPool cp2 = cf2.getConstPool();
            ClassPool pool = this.clazz.getClassPool();
            MethodInfo minfo = new MethodInfo(cp2, accName, accDesc);
            minfo.setAccessFlags(8);
            minfo.addAttribute(new SyntheticAttribute(cp2));
            ExceptionsAttribute ea2 = orig.getExceptionsAttribute();
            if (ea2 != null) {
                minfo.addAttribute(ea2.copy(cp2, null));
            }
            CtClass[] params = Descriptor.getParameterTypes(accDesc, pool);
            int regno = 0;
            Bytecode code = new Bytecode(cp2);
            for (int i2 = 0; i2 < params.length; ++i2) {
                regno += code.addLoad(regno, params[i2]);
            }
            code.setMaxLocals(regno);
            if (desc == accDesc) {
                code.addInvokestatic(this.clazz, name, desc);
            } else {
                code.addInvokevirtual(this.clazz, name, desc);
            }
            code.addReturn(Descriptor.getReturnType(desc, pool));
            minfo.setCodeAttribute(code.toCodeAttribute());
            cf2.addMethod(minfo);
        }
        catch (CannotCompileException e2) {
            throw new CompileError(e2);
        }
        catch (NotFoundException e3) {
            throw new CompileError(e3);
        }
        this.accessors.put(key, accName);
        return accName;
    }

    public MethodInfo getFieldGetter(FieldInfo finfo, boolean is_static) throws CompileError {
        String fieldName = finfo.getName();
        String key = fieldName + ":getter";
        Object res = this.accessors.get(key);
        if (res != null) {
            return (MethodInfo)res;
        }
        ClassFile cf2 = this.clazz.getClassFile();
        String accName = this.findAccessorName(cf2);
        try {
            ConstPool cp2 = cf2.getConstPool();
            ClassPool pool = this.clazz.getClassPool();
            String fieldType = finfo.getDescriptor();
            String accDesc = is_static ? "()" + fieldType : "(" + Descriptor.of(this.clazz) + ")" + fieldType;
            MethodInfo minfo = new MethodInfo(cp2, accName, accDesc);
            minfo.setAccessFlags(8);
            minfo.addAttribute(new SyntheticAttribute(cp2));
            Bytecode code = new Bytecode(cp2);
            if (is_static) {
                code.addGetstatic(Bytecode.THIS, fieldName, fieldType);
            } else {
                code.addAload(0);
                code.addGetfield(Bytecode.THIS, fieldName, fieldType);
                code.setMaxLocals(1);
            }
            code.addReturn(Descriptor.toCtClass(fieldType, pool));
            minfo.setCodeAttribute(code.toCodeAttribute());
            cf2.addMethod(minfo);
            this.accessors.put(key, minfo);
            return minfo;
        }
        catch (CannotCompileException e2) {
            throw new CompileError(e2);
        }
        catch (NotFoundException e3) {
            throw new CompileError(e3);
        }
    }

    public MethodInfo getFieldSetter(FieldInfo finfo, boolean is_static) throws CompileError {
        String fieldName = finfo.getName();
        String key = fieldName + ":setter";
        Object res = this.accessors.get(key);
        if (res != null) {
            return (MethodInfo)res;
        }
        ClassFile cf2 = this.clazz.getClassFile();
        String accName = this.findAccessorName(cf2);
        try {
            int reg;
            ConstPool cp2 = cf2.getConstPool();
            ClassPool pool = this.clazz.getClassPool();
            String fieldType = finfo.getDescriptor();
            String accDesc = is_static ? "(" + fieldType + ")V" : "(" + Descriptor.of(this.clazz) + fieldType + ")V";
            MethodInfo minfo = new MethodInfo(cp2, accName, accDesc);
            minfo.setAccessFlags(8);
            minfo.addAttribute(new SyntheticAttribute(cp2));
            Bytecode code = new Bytecode(cp2);
            if (is_static) {
                reg = code.addLoad(0, Descriptor.toCtClass(fieldType, pool));
                code.addPutstatic(Bytecode.THIS, fieldName, fieldType);
            } else {
                code.addAload(0);
                reg = code.addLoad(1, Descriptor.toCtClass(fieldType, pool)) + 1;
                code.addPutfield(Bytecode.THIS, fieldName, fieldType);
            }
            code.addReturn(null);
            code.setMaxLocals(reg);
            minfo.setCodeAttribute(code.toCodeAttribute());
            cf2.addMethod(minfo);
            this.accessors.put(key, minfo);
            return minfo;
        }
        catch (CannotCompileException e2) {
            throw new CompileError(e2);
        }
        catch (NotFoundException e3) {
            throw new CompileError(e3);
        }
    }

    private String findAccessorName(ClassFile cf2) {
        String accName;
        while (cf2.getMethod(accName = "access$" + this.uniqueNumber++) != null) {
        }
        return accName;
    }
}

