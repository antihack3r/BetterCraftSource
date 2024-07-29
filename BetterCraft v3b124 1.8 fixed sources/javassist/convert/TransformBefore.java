/*
 * Decompiled with CFR 0.152.
 */
package javassist.convert;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.convert.TransformCall;
import javassist.convert.Transformer;

public class TransformBefore
extends TransformCall {
    protected CtClass[] parameterTypes;
    protected int locals;
    protected int maxLocals;
    protected byte[] saveCode;
    protected byte[] loadCode;

    public TransformBefore(Transformer next, CtMethod origMethod, CtMethod beforeMethod) throws NotFoundException {
        super(next, origMethod, beforeMethod);
        this.methodDescriptor = origMethod.getMethodInfo2().getDescriptor();
        this.parameterTypes = origMethod.getParameterTypes();
        this.locals = 0;
        this.maxLocals = 0;
        this.loadCode = null;
        this.saveCode = null;
    }

    @Override
    public void initialize(ConstPool cp2, CodeAttribute attr) {
        super.initialize(cp2, attr);
        this.locals = 0;
        this.maxLocals = attr.getMaxLocals();
        this.loadCode = null;
        this.saveCode = null;
    }

    @Override
    protected int match(int c2, int pos, CodeIterator iterator, int typedesc, ConstPool cp2) throws BadBytecode {
        if (this.newIndex == 0) {
            String desc = Descriptor.ofParameters(this.parameterTypes) + 'V';
            desc = Descriptor.insertParameter(this.classname, desc);
            int nt2 = cp2.addNameAndTypeInfo(this.newMethodname, desc);
            int ci = cp2.addClassInfo(this.newClassname);
            this.newIndex = cp2.addMethodrefInfo(ci, nt2);
            this.constPool = cp2;
        }
        if (this.saveCode == null) {
            this.makeCode(this.parameterTypes, cp2);
        }
        return this.match2(pos, iterator);
    }

    protected int match2(int pos, CodeIterator iterator) throws BadBytecode {
        iterator.move(pos);
        iterator.insert(this.saveCode);
        iterator.insert(this.loadCode);
        int p2 = iterator.insertGap(3);
        iterator.writeByte(184, p2);
        iterator.write16bit(this.newIndex, p2 + 1);
        iterator.insert(this.loadCode);
        return iterator.next();
    }

    @Override
    public int extraLocals() {
        return this.locals;
    }

    protected void makeCode(CtClass[] paramTypes, ConstPool cp2) {
        Bytecode save = new Bytecode(cp2, 0, 0);
        Bytecode load = new Bytecode(cp2, 0, 0);
        int var = this.maxLocals;
        int len = paramTypes == null ? 0 : paramTypes.length;
        load.addAload(var);
        this.makeCode2(save, load, 0, len, paramTypes, var + 1);
        save.addAstore(var);
        this.saveCode = save.get();
        this.loadCode = load.get();
    }

    private void makeCode2(Bytecode save, Bytecode load, int i2, int n2, CtClass[] paramTypes, int var) {
        if (i2 < n2) {
            int size = load.addLoad(var, paramTypes[i2]);
            this.makeCode2(save, load, i2 + 1, n2, paramTypes, var + size);
            save.addStore(var, paramTypes[i2]);
        } else {
            this.locals = var - this.maxLocals;
        }
    }
}

