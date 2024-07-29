/*
 * Decompiled with CFR 0.152.
 */
package javassist.convert;

import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.convert.TransformReadField;
import javassist.convert.Transformer;

public final class TransformFieldAccess
extends Transformer {
    private String newClassname;
    private String newFieldname;
    private String fieldname;
    private CtClass fieldClass;
    private boolean isPrivate;
    private int newIndex;
    private ConstPool constPool;

    public TransformFieldAccess(Transformer next, CtField field, String newClassname, String newFieldname) {
        super(next);
        this.fieldClass = field.getDeclaringClass();
        this.fieldname = field.getName();
        this.isPrivate = Modifier.isPrivate(field.getModifiers());
        this.newClassname = newClassname;
        this.newFieldname = newFieldname;
        this.constPool = null;
    }

    @Override
    public void initialize(ConstPool cp2, CodeAttribute attr) {
        if (this.constPool != cp2) {
            this.newIndex = 0;
        }
    }

    @Override
    public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp2) {
        int c2 = iterator.byteAt(pos);
        if (c2 == 180 || c2 == 178 || c2 == 181 || c2 == 179) {
            int index = iterator.u16bitAt(pos + 1);
            String typedesc = TransformReadField.isField(clazz.getClassPool(), cp2, this.fieldClass, this.fieldname, this.isPrivate, index);
            if (typedesc != null) {
                if (this.newIndex == 0) {
                    int nt2 = cp2.addNameAndTypeInfo(this.newFieldname, typedesc);
                    this.newIndex = cp2.addFieldrefInfo(cp2.addClassInfo(this.newClassname), nt2);
                    this.constPool = cp2;
                }
                iterator.write16bit(this.newIndex, pos + 1);
            }
        }
        return pos;
    }
}

