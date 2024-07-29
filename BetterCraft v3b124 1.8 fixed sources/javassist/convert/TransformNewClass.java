/*
 * Decompiled with CFR 0.152.
 */
package javassist.convert;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.convert.Transformer;

public final class TransformNewClass
extends Transformer {
    private int nested;
    private String classname;
    private String newClassName;
    private int newClassIndex;
    private int newMethodNTIndex;
    private int newMethodIndex;

    public TransformNewClass(Transformer next, String classname, String newClassName) {
        super(next);
        this.classname = classname;
        this.newClassName = newClassName;
    }

    @Override
    public void initialize(ConstPool cp2, CodeAttribute attr) {
        this.nested = 0;
        this.newMethodIndex = 0;
        this.newMethodNTIndex = 0;
        this.newClassIndex = 0;
    }

    @Override
    public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp2) throws CannotCompileException {
        int index;
        int typedesc;
        int c2 = iterator.byteAt(pos);
        if (c2 == 187) {
            int index2 = iterator.u16bitAt(pos + 1);
            if (cp2.getClassInfo(index2).equals(this.classname)) {
                if (iterator.byteAt(pos + 3) != 89) {
                    throw new CannotCompileException("NEW followed by no DUP was found");
                }
                if (this.newClassIndex == 0) {
                    this.newClassIndex = cp2.addClassInfo(this.newClassName);
                }
                iterator.write16bit(this.newClassIndex, pos + 1);
                ++this.nested;
            }
        } else if (c2 == 183 && (typedesc = cp2.isConstructor(this.classname, index = iterator.u16bitAt(pos + 1))) != 0 && this.nested > 0) {
            int nt2 = cp2.getMethodrefNameAndType(index);
            if (this.newMethodNTIndex != nt2) {
                this.newMethodNTIndex = nt2;
                this.newMethodIndex = cp2.addMethodrefInfo(this.newClassIndex, nt2);
            }
            iterator.write16bit(this.newMethodIndex, pos + 1);
            --this.nested;
        }
        return pos;
    }
}

