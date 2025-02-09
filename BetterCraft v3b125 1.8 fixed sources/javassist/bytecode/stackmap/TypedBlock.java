/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode.stackmap;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.stackmap.BasicBlock;
import javassist.bytecode.stackmap.TypeData;
import javassist.bytecode.stackmap.TypeTag;

public class TypedBlock
extends BasicBlock {
    public int stackTop;
    public int numLocals;
    public TypeData[] localsTypes = null;
    public TypeData[] stackTypes;

    public static TypedBlock[] makeBlocks(MethodInfo minfo, CodeAttribute ca2, boolean optimize) throws BadBytecode {
        TypedBlock[] blocks = (TypedBlock[])new Maker().make(minfo);
        if (optimize && blocks.length < 2 && (blocks.length == 0 || blocks[0].incoming == 0)) {
            return null;
        }
        ConstPool pool = minfo.getConstPool();
        boolean isStatic = (minfo.getAccessFlags() & 8) != 0;
        blocks[0].initFirstBlock(ca2.getMaxStack(), ca2.getMaxLocals(), pool.getClassName(), minfo.getDescriptor(), isStatic, minfo.isConstructor());
        return blocks;
    }

    protected TypedBlock(int pos) {
        super(pos);
    }

    @Override
    protected void toString2(StringBuffer sbuf) {
        super.toString2(sbuf);
        sbuf.append(",\n stack={");
        this.printTypes(sbuf, this.stackTop, this.stackTypes);
        sbuf.append("}, locals={");
        this.printTypes(sbuf, this.numLocals, this.localsTypes);
        sbuf.append('}');
    }

    private void printTypes(StringBuffer sbuf, int size, TypeData[] types) {
        if (types == null) {
            return;
        }
        for (int i2 = 0; i2 < size; ++i2) {
            TypeData td;
            if (i2 > 0) {
                sbuf.append(", ");
            }
            sbuf.append((td = types[i2]) == null ? "<>" : td.toString());
        }
    }

    public boolean alreadySet() {
        return this.localsTypes != null;
    }

    public void setStackMap(int st2, TypeData[] stack, int nl2, TypeData[] locals) throws BadBytecode {
        this.stackTop = st2;
        this.stackTypes = stack;
        this.numLocals = nl2;
        this.localsTypes = locals;
    }

    public void resetNumLocals() {
        if (this.localsTypes != null) {
            int nl2;
            for (nl2 = this.localsTypes.length; !(nl2 <= 0 || this.localsTypes[nl2 - 1].isBasicType() != TypeTag.TOP || nl2 > 1 && this.localsTypes[nl2 - 2].is2WordType()); --nl2) {
            }
            this.numLocals = nl2;
        }
    }

    void initFirstBlock(int maxStack, int maxLocals, String className, String methodDesc, boolean isStatic, boolean isConstructor) throws BadBytecode {
        if (methodDesc.charAt(0) != '(') {
            throw new BadBytecode("no method descriptor: " + methodDesc);
        }
        this.stackTop = 0;
        this.stackTypes = TypeData.make(maxStack);
        TypeData[] locals = TypeData.make(maxLocals);
        if (isConstructor) {
            locals[0] = new TypeData.UninitThis(className);
        } else if (!isStatic) {
            locals[0] = new TypeData.ClassName(className);
        }
        int n2 = isStatic ? -1 : 0;
        int i2 = 1;
        try {
            while ((i2 = TypedBlock.descToTag(methodDesc, i2, ++n2, locals)) > 0) {
                if (!locals[n2].is2WordType()) continue;
                locals[++n2] = TypeTag.TOP;
            }
        }
        catch (StringIndexOutOfBoundsException e2) {
            throw new BadBytecode("bad method descriptor: " + methodDesc);
        }
        this.numLocals = n2;
        this.localsTypes = locals;
    }

    private static int descToTag(String desc, int i2, int n2, TypeData[] types) throws BadBytecode {
        int i0 = i2;
        int arrayDim = 0;
        char c2 = desc.charAt(i2);
        if (c2 == ')') {
            return 0;
        }
        while (c2 == '[') {
            ++arrayDim;
            c2 = desc.charAt(++i2);
        }
        if (c2 == 'L') {
            int i22 = desc.indexOf(59, ++i2);
            types[n2] = arrayDim > 0 ? new TypeData.ClassName(desc.substring(i0, ++i22)) : new TypeData.ClassName(desc.substring(i0 + 1, ++i22 - 1).replace('/', '.'));
            return i22;
        }
        if (arrayDim > 0) {
            types[n2] = new TypeData.ClassName(desc.substring(i0, ++i2));
            return i2;
        }
        TypeData t2 = TypedBlock.toPrimitiveTag(c2);
        if (t2 == null) {
            throw new BadBytecode("bad method descriptor: " + desc);
        }
        types[n2] = t2;
        return i2 + 1;
    }

    private static TypeData toPrimitiveTag(char c2) {
        switch (c2) {
            case 'B': 
            case 'C': 
            case 'I': 
            case 'S': 
            case 'Z': {
                return TypeTag.INTEGER;
            }
            case 'J': {
                return TypeTag.LONG;
            }
            case 'F': {
                return TypeTag.FLOAT;
            }
            case 'D': {
                return TypeTag.DOUBLE;
            }
        }
        return null;
    }

    public static String getRetType(String desc) {
        int i2 = desc.indexOf(41);
        if (i2 < 0) {
            return "java.lang.Object";
        }
        char c2 = desc.charAt(i2 + 1);
        if (c2 == '[') {
            return desc.substring(i2 + 1);
        }
        if (c2 == 'L') {
            return desc.substring(i2 + 2, desc.length() - 1).replace('/', '.');
        }
        return "java.lang.Object";
    }

    public static class Maker
    extends BasicBlock.Maker {
        @Override
        protected BasicBlock makeBlock(int pos) {
            return new TypedBlock(pos);
        }

        @Override
        protected BasicBlock[] makeArray(int size) {
            return new TypedBlock[size];
        }
    }
}

