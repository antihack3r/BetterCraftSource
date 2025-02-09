/*
 * Decompiled with CFR 0.152.
 */
package javassist.convert;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.convert.Transformer;

public class TransformReadField
extends Transformer {
    protected String fieldname;
    protected CtClass fieldClass;
    protected boolean isPrivate;
    protected String methodClassname;
    protected String methodName;

    public TransformReadField(Transformer next, CtField field, String methodClassname, String methodName) {
        super(next);
        this.fieldClass = field.getDeclaringClass();
        this.fieldname = field.getName();
        this.methodClassname = methodClassname;
        this.methodName = methodName;
        this.isPrivate = Modifier.isPrivate(field.getModifiers());
    }

    static String isField(ClassPool pool, ConstPool cp2, CtClass fclass, String fname, boolean is_private, int index) {
        if (!cp2.getFieldrefName(index).equals(fname)) {
            return null;
        }
        try {
            CtClass c2 = pool.get(cp2.getFieldrefClassName(index));
            if (c2 == fclass || !is_private && TransformReadField.isFieldInSuper(c2, fclass, fname)) {
                return cp2.getFieldrefType(index);
            }
        }
        catch (NotFoundException notFoundException) {
            // empty catch block
        }
        return null;
    }

    static boolean isFieldInSuper(CtClass clazz, CtClass fclass, String fname) {
        if (!clazz.subclassOf(fclass)) {
            return false;
        }
        try {
            CtField f2 = clazz.getField(fname);
            return f2.getDeclaringClass() == fclass;
        }
        catch (NotFoundException notFoundException) {
            return false;
        }
    }

    @Override
    public int transform(CtClass tclazz, int pos, CodeIterator iterator, ConstPool cp2) throws BadBytecode {
        int c2 = iterator.byteAt(pos);
        if (c2 == 180 || c2 == 178) {
            int index = iterator.u16bitAt(pos + 1);
            String typedesc = TransformReadField.isField(tclazz.getClassPool(), cp2, this.fieldClass, this.fieldname, this.isPrivate, index);
            if (typedesc != null) {
                if (c2 == 178) {
                    iterator.move(pos);
                    pos = iterator.insertGap(1);
                    iterator.writeByte(1, pos);
                    pos = iterator.next();
                }
                String type = "(Ljava/lang/Object;)" + typedesc;
                int mi = cp2.addClassInfo(this.methodClassname);
                int methodref = cp2.addMethodrefInfo(mi, this.methodName, type);
                iterator.writeByte(184, pos);
                iterator.write16bit(methodref, pos + 1);
                return pos;
            }
        }
        return pos;
    }
}

