/*
 * Decompiled with CFR 0.152.
 */
package javassist.convert;

import javassist.CtMethod;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.convert.TransformCall;
import javassist.convert.Transformer;

public class TransformCallToStatic
extends TransformCall {
    public TransformCallToStatic(Transformer next, CtMethod origMethod, CtMethod substMethod) {
        super(next, origMethod, substMethod);
        this.methodDescriptor = origMethod.getMethodInfo2().getDescriptor();
    }

    @Override
    protected int match(int c2, int pos, CodeIterator iterator, int typedesc, ConstPool cp2) {
        if (this.newIndex == 0) {
            String desc = Descriptor.insertParameter(this.classname, this.methodDescriptor);
            int nt2 = cp2.addNameAndTypeInfo(this.newMethodname, desc);
            int ci = cp2.addClassInfo(this.newClassname);
            this.newIndex = cp2.addMethodrefInfo(ci, nt2);
            this.constPool = cp2;
        }
        iterator.writeByte(184, pos);
        iterator.write16bit(this.newIndex, pos + 1);
        return pos;
    }
}

