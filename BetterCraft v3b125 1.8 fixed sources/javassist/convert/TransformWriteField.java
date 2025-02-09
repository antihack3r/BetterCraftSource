/*
 * Decompiled with CFR 0.152.
 */
package javassist.convert;

import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.convert.TransformReadField;
import javassist.convert.Transformer;

public final class TransformWriteField
extends TransformReadField {
    public TransformWriteField(Transformer next, CtField field, String methodClassname, String methodName) {
        super(next, field, methodClassname, methodName);
    }

    @Override
    public int transform(CtClass tclazz, int pos, CodeIterator iterator, ConstPool cp2) throws BadBytecode {
        int c2 = iterator.byteAt(pos);
        if (c2 == 181 || c2 == 179) {
            int index = iterator.u16bitAt(pos + 1);
            String typedesc = TransformWriteField.isField(tclazz.getClassPool(), cp2, this.fieldClass, this.fieldname, this.isPrivate, index);
            if (typedesc != null) {
                if (c2 == 179) {
                    CodeAttribute ca2 = iterator.get();
                    iterator.move(pos);
                    char c0 = typedesc.charAt(0);
                    if (c0 == 'J' || c0 == 'D') {
                        pos = iterator.insertGap(3);
                        iterator.writeByte(1, pos);
                        iterator.writeByte(91, pos + 1);
                        iterator.writeByte(87, pos + 2);
                        ca2.setMaxStack(ca2.getMaxStack() + 2);
                    } else {
                        pos = iterator.insertGap(2);
                        iterator.writeByte(1, pos);
                        iterator.writeByte(95, pos + 1);
                        ca2.setMaxStack(ca2.getMaxStack() + 1);
                    }
                    pos = iterator.next();
                }
                int mi = cp2.addClassInfo(this.methodClassname);
                String type = "(Ljava/lang/Object;" + typedesc + ")V";
                int methodref = cp2.addMethodrefInfo(mi, this.methodName, type);
                iterator.writeByte(184, pos);
                iterator.write16bit(methodref, pos + 1);
            }
        }
        return pos;
    }
}

