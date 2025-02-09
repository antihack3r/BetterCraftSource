/*
 * Decompiled with CFR 0.152.
 */
package javassist.convert;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.convert.Transformer;

public class TransformCall
extends Transformer {
    protected String classname;
    protected String methodname;
    protected String methodDescriptor;
    protected String newClassname;
    protected String newMethodname;
    protected boolean newMethodIsPrivate;
    protected int newIndex;
    protected ConstPool constPool;

    public TransformCall(Transformer next, CtMethod origMethod, CtMethod substMethod) {
        this(next, origMethod.getName(), substMethod);
        this.classname = origMethod.getDeclaringClass().getName();
    }

    public TransformCall(Transformer next, String oldMethodName, CtMethod substMethod) {
        super(next);
        this.methodname = oldMethodName;
        this.methodDescriptor = substMethod.getMethodInfo2().getDescriptor();
        this.classname = this.newClassname = substMethod.getDeclaringClass().getName();
        this.newMethodname = substMethod.getName();
        this.constPool = null;
        this.newMethodIsPrivate = Modifier.isPrivate(substMethod.getModifiers());
    }

    @Override
    public void initialize(ConstPool cp2, CodeAttribute attr) {
        if (this.constPool != cp2) {
            this.newIndex = 0;
        }
    }

    @Override
    public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp2) throws BadBytecode {
        int index;
        String cname;
        int c2 = iterator.byteAt(pos);
        if ((c2 == 185 || c2 == 183 || c2 == 184 || c2 == 182) && (cname = cp2.eqMember(this.methodname, this.methodDescriptor, index = iterator.u16bitAt(pos + 1))) != null && this.matchClass(cname, clazz.getClassPool())) {
            int ntinfo = cp2.getMemberNameAndType(index);
            pos = this.match(c2, pos, iterator, cp2.getNameAndTypeDescriptor(ntinfo), cp2);
        }
        return pos;
    }

    private boolean matchClass(String name, ClassPool pool) {
        if (this.classname.equals(name)) {
            return true;
        }
        try {
            CtClass clazz = pool.get(name);
            CtClass declClazz = pool.get(this.classname);
            if (clazz.subtypeOf(declClazz)) {
                try {
                    CtMethod m2 = clazz.getMethod(this.methodname, this.methodDescriptor);
                    return m2.getDeclaringClass().getName().equals(this.classname);
                }
                catch (NotFoundException e2) {
                    return true;
                }
            }
        }
        catch (NotFoundException e3) {
            return false;
        }
        return false;
    }

    protected int match(int c2, int pos, CodeIterator iterator, int typedesc, ConstPool cp2) throws BadBytecode {
        if (this.newIndex == 0) {
            int nt2 = cp2.addNameAndTypeInfo(cp2.addUtf8Info(this.newMethodname), typedesc);
            int ci = cp2.addClassInfo(this.newClassname);
            if (c2 == 185) {
                this.newIndex = cp2.addInterfaceMethodrefInfo(ci, nt2);
            } else {
                if (this.newMethodIsPrivate && c2 == 182) {
                    iterator.writeByte(183, pos);
                }
                this.newIndex = cp2.addMethodrefInfo(ci, nt2);
            }
            this.constPool = cp2;
        }
        iterator.write16bit(this.newIndex, pos + 1);
        return pos;
    }
}

