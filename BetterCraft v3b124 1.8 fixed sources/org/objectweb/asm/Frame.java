/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.ByteVector;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Symbol;
import org.objectweb.asm.SymbolTable;
import org.objectweb.asm.Type;

class Frame {
    static final int SAME_FRAME = 0;
    static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
    static final int RESERVED = 128;
    static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
    static final int CHOP_FRAME = 248;
    static final int SAME_FRAME_EXTENDED = 251;
    static final int APPEND_FRAME = 252;
    static final int FULL_FRAME = 255;
    static final int ITEM_TOP = 0;
    static final int ITEM_INTEGER = 1;
    static final int ITEM_FLOAT = 2;
    static final int ITEM_DOUBLE = 3;
    static final int ITEM_LONG = 4;
    static final int ITEM_NULL = 5;
    static final int ITEM_UNINITIALIZED_THIS = 6;
    static final int ITEM_OBJECT = 7;
    static final int ITEM_UNINITIALIZED = 8;
    private static final int ITEM_ASM_BOOLEAN = 9;
    private static final int ITEM_ASM_BYTE = 10;
    private static final int ITEM_ASM_CHAR = 11;
    private static final int ITEM_ASM_SHORT = 12;
    private static final int DIM_MASK = -268435456;
    private static final int KIND_MASK = 0xF000000;
    private static final int FLAGS_MASK = 0xF00000;
    private static final int VALUE_MASK = 1048575;
    private static final int DIM_SHIFT = 28;
    private static final int ARRAY_OF = 0x10000000;
    private static final int ELEMENT_OF = -268435456;
    private static final int CONSTANT_KIND = 0x1000000;
    private static final int REFERENCE_KIND = 0x2000000;
    private static final int UNINITIALIZED_KIND = 0x3000000;
    private static final int LOCAL_KIND = 0x4000000;
    private static final int STACK_KIND = 0x5000000;
    private static final int TOP_IF_LONG_OR_DOUBLE_FLAG = 0x100000;
    private static final int TOP = 0x1000000;
    private static final int BOOLEAN = 0x1000009;
    private static final int BYTE = 0x100000A;
    private static final int CHAR = 0x100000B;
    private static final int SHORT = 0x100000C;
    private static final int INTEGER = 0x1000001;
    private static final int FLOAT = 0x1000002;
    private static final int LONG = 0x1000004;
    private static final int DOUBLE = 0x1000003;
    private static final int NULL = 0x1000005;
    private static final int UNINITIALIZED_THIS = 0x1000006;
    Label owner;
    private int[] inputLocals;
    private int[] inputStack;
    private int[] outputLocals;
    private int[] outputStack;
    private short outputStackStart;
    private short outputStackTop;
    private int initializationCount;
    private int[] initializations;

    Frame(Label owner) {
        this.owner = owner;
    }

    static int getAbstractTypeFromApiFormat(SymbolTable symbolTable, Object type) {
        if (type instanceof Integer) {
            return 0x1000000 | (Integer)type;
        }
        if (type instanceof String) {
            String descriptor = Type.getObjectType((String)type).getDescriptor();
            return Frame.getAbstractTypeFromDescriptor(symbolTable, descriptor, 0);
        }
        return 0x3000000 | symbolTable.addUninitializedType("", ((Label)type).bytecodeOffset);
    }

    static int getAbstractTypeFromInternalName(SymbolTable symbolTable, String internalName) {
        return 0x2000000 | symbolTable.addType(internalName);
    }

    private static int getAbstractTypeFromDescriptor(SymbolTable symbolTable, String buffer, int offset) {
        int typeValue;
        switch (buffer.charAt(offset)) {
            case 'B': 
            case 'C': 
            case 'I': 
            case 'S': 
            case 'Z': {
                return 0x1000001;
            }
            case 'D': {
                return 0x1000003;
            }
            default: {
                throw new IllegalArgumentException();
            }
            case 'F': {
                return 0x1000002;
            }
            case 'J': {
                return 0x1000004;
            }
            case 'L': {
                String internalName = buffer.substring(offset + 1, buffer.length() - 1);
                return 0x2000000 | symbolTable.addType(internalName);
            }
            case 'V': {
                return 0;
            }
            case '[': 
        }
        int elementDescriptorOffset = offset + 1;
        while (buffer.charAt(elementDescriptorOffset) == '[') {
            ++elementDescriptorOffset;
        }
        switch (buffer.charAt(elementDescriptorOffset)) {
            case 'B': {
                typeValue = 0x100000A;
                break;
            }
            case 'C': {
                typeValue = 0x100000B;
                break;
            }
            case 'D': {
                typeValue = 0x1000003;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
            case 'F': {
                typeValue = 0x1000002;
                break;
            }
            case 'I': {
                typeValue = 0x1000001;
                break;
            }
            case 'J': {
                typeValue = 0x1000004;
                break;
            }
            case 'L': {
                String internalName = buffer.substring(elementDescriptorOffset + 1, buffer.length() - 1);
                typeValue = 0x2000000 | symbolTable.addType(internalName);
                break;
            }
            case 'S': {
                typeValue = 0x100000C;
                break;
            }
            case 'Z': {
                typeValue = 0x1000009;
            }
        }
        return elementDescriptorOffset - offset << 28 | typeValue;
    }

    private static boolean merge(SymbolTable symbolTable, int sourceType, int[] dstTypes, int dstIndex) {
        int mergedType;
        int dstType = dstTypes[dstIndex];
        if (dstType == sourceType) {
            return false;
        }
        int srcType = sourceType;
        if ((sourceType & 0xFFFFFFF) == 0x1000005) {
            if (dstType == 0x1000005) {
                return false;
            }
            srcType = 0x1000005;
        }
        if (dstType == 0) {
            dstTypes[dstIndex] = srcType;
            return true;
        }
        if ((dstType & 0xF0000000) == 0 && (dstType & 0xF000000) != 0x2000000) {
            mergedType = dstType == 0x1000005 ? ((srcType & 0xF0000000) == 0 && (srcType & 0xF000000) != 0x2000000 ? 0x1000000 : srcType) : 0x1000000;
        } else {
            if (srcType == 0x1000005) {
                return false;
            }
            if ((srcType & 0xFF000000) == (dstType & 0xFF000000)) {
                if ((dstType & 0xF000000) == 0x2000000) {
                    mergedType = srcType & 0xF0000000 | 0x2000000 | symbolTable.addMergedType(srcType & 0xFFFFF, dstType & 0xFFFFF);
                } else {
                    int srcDim = -268435456 + (srcType & 0xF0000000);
                    mergedType = srcDim | 0x2000000 | symbolTable.addType("java/lang/Object");
                }
            } else if ((srcType & 0xF0000000) == 0 && (srcType & 0xF000000) != 0x2000000) {
                mergedType = 0x1000000;
            } else {
                int dstDim;
                int srcDim = srcType & 0xF0000000;
                if (srcDim != 0 && (srcType & 0xF000000) != 0x2000000) {
                    srcDim += -268435456;
                }
                if ((dstDim = dstType & 0xF0000000) != 0 && (dstType & 0xF000000) != 0x2000000) {
                    dstDim += -268435456;
                }
                mergedType = Math.min(srcDim, dstDim) | 0x2000000 | symbolTable.addType("java/lang/Object");
            }
        }
        if (mergedType != dstType) {
            dstTypes[dstIndex] = mergedType;
            return true;
        }
        return false;
    }

    static void putAbstractType(SymbolTable symbolTable, int abstractType, ByteVector output) {
        block19: {
            int arrayDimensions;
            block18: {
                arrayDimensions = (abstractType & 0xF0000000) >> 28;
                if (arrayDimensions != 0) break block18;
                int typeDescriptor = abstractType & 0xFFFFF;
                switch (abstractType & 0xF000000) {
                    case 0x1000000: {
                        output.putByte(typeDescriptor);
                        break block19;
                    }
                    case 0x2000000: {
                        output.putByte(7).putShort(symbolTable.addConstantClass((String)symbolTable.getType((int)typeDescriptor).value).index);
                        break block19;
                    }
                    case 0x3000000: {
                        output.putByte(8).putShort((int)symbolTable.getType((int)typeDescriptor).data);
                        break block19;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
            }
            StringBuilder var5 = new StringBuilder();
            while (arrayDimensions-- > 0) {
                var5.append('[');
            }
            if ((abstractType & 0xF000000) == 0x2000000) {
                var5.append('L').append(symbolTable.getType((int)(abstractType & 0xFFFFF)).value).append(';');
            } else {
                switch (abstractType & 0xFFFFF) {
                    case 1: {
                        var5.append('I');
                        break;
                    }
                    case 2: {
                        var5.append('F');
                        break;
                    }
                    case 3: {
                        var5.append('D');
                        break;
                    }
                    case 4: {
                        var5.append('J');
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                    case 9: {
                        var5.append('Z');
                        break;
                    }
                    case 10: {
                        var5.append('B');
                        break;
                    }
                    case 11: {
                        var5.append('C');
                        break;
                    }
                    case 12: {
                        var5.append('S');
                    }
                }
            }
            output.putByte(7).putShort(symbolTable.addConstantClass((String)var5.toString()).index);
        }
    }

    final void copyFrom(Frame frame) {
        this.inputLocals = frame.inputLocals;
        this.inputStack = frame.inputStack;
        this.outputStackStart = 0;
        this.outputLocals = frame.outputLocals;
        this.outputStack = frame.outputStack;
        this.outputStackTop = frame.outputStackTop;
        this.initializationCount = frame.initializationCount;
        this.initializations = frame.initializations;
    }

    final void setInputFrameFromDescriptor(SymbolTable symbolTable, int access, String descriptor, int maxLocals) {
        this.inputLocals = new int[maxLocals];
        this.inputStack = new int[0];
        int inputLocalIndex = 0;
        if ((access & 8) == 0) {
            this.inputLocals[inputLocalIndex++] = (access & 0x40000) == 0 ? 0x2000000 | symbolTable.addType(symbolTable.getClassName()) : 0x1000006;
        }
        Type[] var6 = Type.getArgumentTypes(descriptor);
        int var7 = var6.length;
        int var8 = 0;
        while (var8 < var7) {
            Type argumentType = var6[var8];
            int abstractType = Frame.getAbstractTypeFromDescriptor(symbolTable, argumentType.getDescriptor(), 0);
            this.inputLocals[inputLocalIndex++] = abstractType;
            if (abstractType == 0x1000004 || abstractType == 0x1000003) {
                this.inputLocals[inputLocalIndex++] = 0x1000000;
            }
            ++var8;
        }
        while (inputLocalIndex < maxLocals) {
            this.inputLocals[inputLocalIndex++] = 0x1000000;
        }
    }

    final void setInputFrameFromApiFormat(SymbolTable symbolTable, int numLocal, Object[] local, int numStack, Object[] stack) {
        int inputLocalIndex = 0;
        int numStackTop = 0;
        while (numStackTop < numLocal) {
            this.inputLocals[inputLocalIndex++] = Frame.getAbstractTypeFromApiFormat(symbolTable, local[numStackTop]);
            if (local[numStackTop] == Opcodes.LONG || local[numStackTop] == Opcodes.DOUBLE) {
                this.inputLocals[inputLocalIndex++] = 0x1000000;
            }
            ++numStackTop;
        }
        while (inputLocalIndex < this.inputLocals.length) {
            this.inputLocals[inputLocalIndex++] = 0x1000000;
        }
        numStackTop = 0;
        int inputStackIndex = 0;
        while (inputStackIndex < numStack) {
            if (stack[inputStackIndex] == Opcodes.LONG || stack[inputStackIndex] == Opcodes.DOUBLE) {
                ++numStackTop;
            }
            ++inputStackIndex;
        }
        this.inputStack = new int[numStack + numStackTop];
        inputStackIndex = 0;
        int i2 = 0;
        while (i2 < numStack) {
            this.inputStack[inputStackIndex++] = Frame.getAbstractTypeFromApiFormat(symbolTable, stack[i2]);
            if (stack[i2] == Opcodes.LONG || stack[i2] == Opcodes.DOUBLE) {
                this.inputStack[inputStackIndex++] = 0x1000000;
            }
            ++i2;
        }
        this.outputStackTop = 0;
        this.initializationCount = 0;
    }

    final int getInputStackSize() {
        return this.inputStack.length;
    }

    private int getLocal(int localIndex) {
        if (this.outputLocals != null && localIndex < this.outputLocals.length) {
            int abstractType = this.outputLocals[localIndex];
            if (abstractType == 0) {
                abstractType = this.outputLocals[localIndex] = 0x4000000 | localIndex;
            }
            return abstractType;
        }
        return 0x4000000 | localIndex;
    }

    private void setLocal(int localIndex, int abstractType) {
        int outputLocalsLength;
        if (this.outputLocals == null) {
            this.outputLocals = new int[10];
        }
        if (localIndex >= (outputLocalsLength = this.outputLocals.length)) {
            int[] newOutputLocals = new int[Math.max(localIndex + 1, 2 * outputLocalsLength)];
            System.arraycopy(this.outputLocals, 0, newOutputLocals, 0, outputLocalsLength);
            this.outputLocals = newOutputLocals;
        }
        this.outputLocals[localIndex] = abstractType;
    }

    private void push(int abstractType) {
        int outputStackLength;
        if (this.outputStack == null) {
            this.outputStack = new int[10];
        }
        if (this.outputStackTop >= (outputStackLength = this.outputStack.length)) {
            int[] outputStackSize = new int[Math.max(this.outputStackTop + 1, 2 * outputStackLength)];
            System.arraycopy(this.outputStack, 0, outputStackSize, 0, outputStackLength);
            this.outputStack = outputStackSize;
        }
        short s2 = this.outputStackTop;
        this.outputStackTop = (short)(s2 + 1);
        this.outputStack[s2] = abstractType;
        short var4 = (short)(this.outputStackStart + this.outputStackTop);
        if (var4 > this.owner.outputStackMax) {
            this.owner.outputStackMax = var4;
        }
    }

    private void push(SymbolTable symbolTable, String descriptor) {
        int typeDescriptorOffset = descriptor.charAt(0) == '(' ? descriptor.indexOf(41) + 1 : 0;
        int abstractType = Frame.getAbstractTypeFromDescriptor(symbolTable, descriptor, typeDescriptorOffset);
        if (abstractType != 0) {
            this.push(abstractType);
            if (abstractType == 0x1000004 || abstractType == 0x1000003) {
                this.push(0x1000000);
            }
        }
    }

    private int pop() {
        int n2;
        if (this.outputStackTop > 0) {
            this.outputStackTop = (short)(this.outputStackTop - 1);
            n2 = this.outputStack[this.outputStackTop];
        } else {
            this.outputStackStart = (short)(this.outputStackStart - 1);
            n2 = 0x5000000 | -this.outputStackStart;
        }
        return n2;
    }

    private void pop(int elements) {
        if (this.outputStackTop >= elements) {
            this.outputStackTop = (short)(this.outputStackTop - elements);
        } else {
            this.outputStackStart = (short)(this.outputStackStart - (elements - this.outputStackTop));
            this.outputStackTop = 0;
        }
    }

    private void pop(String descriptor) {
        char firstDescriptorChar = descriptor.charAt(0);
        if (firstDescriptorChar == '(') {
            this.pop((Type.getArgumentsAndReturnSizes(descriptor) >> 2) - 1);
        } else if (firstDescriptorChar != 'J' && firstDescriptorChar != 'D') {
            this.pop(1);
        } else {
            this.pop(2);
        }
    }

    private void addInitializedType(int abstractType) {
        int initializationsLength;
        if (this.initializations == null) {
            this.initializations = new int[2];
        }
        if (this.initializationCount >= (initializationsLength = this.initializations.length)) {
            int[] newInitializations = new int[Math.max(this.initializationCount + 1, 2 * initializationsLength)];
            System.arraycopy(this.initializations, 0, newInitializations, 0, initializationsLength);
            this.initializations = newInitializations;
        }
        this.initializations[this.initializationCount++] = abstractType;
    }

    private int getInitializedType(SymbolTable symbolTable, int abstractType) {
        if (abstractType == 0x1000006 || (abstractType & 0xFF000000) == 0x3000000) {
            int i2 = 0;
            while (i2 < this.initializationCount) {
                int initializedType = this.initializations[i2];
                int dim = initializedType & 0xF0000000;
                int kind = initializedType & 0xF000000;
                int value = initializedType & 0xFFFFF;
                if (kind == 0x4000000) {
                    initializedType = dim + this.inputLocals[value];
                } else if (kind == 0x5000000) {
                    initializedType = dim + this.inputStack[this.inputStack.length - value];
                }
                if (abstractType == initializedType) {
                    if (abstractType == 0x1000006) {
                        return 0x2000000 | symbolTable.addType(symbolTable.getClassName());
                    }
                    return 0x2000000 | symbolTable.addType(symbolTable.getType((int)(abstractType & 0xFFFFF)).value);
                }
                ++i2;
            }
        }
        return abstractType;
    }

    void execute(int opcode, int arg2, Symbol argSymbol, SymbolTable symbolTable) {
        switch (opcode) {
            case 0: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 145: 
            case 146: 
            case 147: 
            case 167: 
            case 177: {
                break;
            }
            case 1: {
                this.push(0x1000005);
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 16: 
            case 17: 
            case 21: {
                this.push(0x1000001);
                break;
            }
            case 9: 
            case 10: 
            case 22: {
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 11: 
            case 12: 
            case 13: 
            case 23: {
                this.push(0x1000002);
                break;
            }
            case 14: 
            case 15: 
            case 24: {
                this.push(0x1000003);
                this.push(0x1000000);
                break;
            }
            case 18: {
                switch (argSymbol.tag) {
                    case 3: {
                        this.push(0x1000001);
                        return;
                    }
                    case 4: {
                        this.push(0x1000002);
                        return;
                    }
                    case 5: {
                        this.push(0x1000004);
                        this.push(0x1000000);
                        return;
                    }
                    case 6: {
                        this.push(0x1000003);
                        this.push(0x1000000);
                        return;
                    }
                    case 7: {
                        this.push(0x2000000 | symbolTable.addType("java/lang/Class"));
                        return;
                    }
                    case 8: {
                        this.push(0x2000000 | symbolTable.addType("java/lang/String"));
                        return;
                    }
                    default: {
                        throw new AssertionError();
                    }
                    case 15: {
                        this.push(0x2000000 | symbolTable.addType("java/lang/invoke/MethodHandle"));
                        return;
                    }
                    case 16: {
                        this.push(0x2000000 | symbolTable.addType("java/lang/invoke/MethodType"));
                        return;
                    }
                    case 17: 
                }
                this.push(symbolTable, argSymbol.value);
                return;
            }
            default: {
                throw new IllegalArgumentException();
            }
            case 25: {
                this.push(this.getLocal(arg2));
                break;
            }
            case 46: 
            case 51: 
            case 52: 
            case 53: 
            case 96: 
            case 100: 
            case 104: 
            case 108: 
            case 112: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: 
            case 136: 
            case 142: 
            case 149: 
            case 150: {
                this.pop(2);
                this.push(0x1000001);
                break;
            }
            case 47: 
            case 143: {
                this.pop(2);
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 48: 
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: 
            case 137: 
            case 144: {
                this.pop(2);
                this.push(0x1000002);
                break;
            }
            case 49: 
            case 138: {
                this.pop(2);
                this.push(0x1000003);
                this.push(0x1000000);
                break;
            }
            case 50: {
                this.pop(1);
                int abstractType1 = this.pop();
                this.push(abstractType1 == 0x1000005 ? abstractType1 : -268435456 + abstractType1);
                break;
            }
            case 54: 
            case 56: 
            case 58: {
                int abstractType1 = this.pop();
                this.setLocal(arg2, abstractType1);
                if (arg2 <= 0) break;
                int arrayElementType1 = this.getLocal(arg2 - 1);
                if (arrayElementType1 != 0x1000004 && arrayElementType1 != 0x1000003) {
                    if ((arrayElementType1 & 0xF000000) != 0x4000000 && (arrayElementType1 & 0xF000000) != 0x5000000) break;
                    this.setLocal(arg2 - 1, arrayElementType1 | 0x100000);
                    break;
                }
                this.setLocal(arg2 - 1, 0x1000000);
                break;
            }
            case 55: 
            case 57: {
                this.pop(1);
                int abstractType1 = this.pop();
                this.setLocal(arg2, abstractType1);
                this.setLocal(arg2 + 1, 0x1000000);
                if (arg2 <= 0) break;
                int arrayElementType1 = this.getLocal(arg2 - 1);
                if (arrayElementType1 != 0x1000004 && arrayElementType1 != 0x1000003) {
                    if ((arrayElementType1 & 0xF000000) != 0x4000000 && (arrayElementType1 & 0xF000000) != 0x5000000) break;
                    this.setLocal(arg2 - 1, arrayElementType1 | 0x100000);
                    break;
                }
                this.setLocal(arg2 - 1, 0x1000000);
                break;
            }
            case 79: 
            case 81: 
            case 83: 
            case 84: 
            case 85: 
            case 86: {
                this.pop(3);
                break;
            }
            case 80: 
            case 82: {
                this.pop(4);
                break;
            }
            case 87: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 170: 
            case 171: 
            case 172: 
            case 174: 
            case 176: 
            case 191: 
            case 194: 
            case 195: 
            case 198: 
            case 199: {
                this.pop(1);
                break;
            }
            case 88: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 173: 
            case 175: {
                this.pop(2);
                break;
            }
            case 89: {
                int abstractType1 = this.pop();
                this.push(abstractType1);
                this.push(abstractType1);
                break;
            }
            case 90: {
                int abstractType1 = this.pop();
                int abstractType2 = this.pop();
                this.push(abstractType1);
                this.push(abstractType2);
                this.push(abstractType1);
                break;
            }
            case 91: {
                int abstractType1 = this.pop();
                int abstractType2 = this.pop();
                int abstractType3 = this.pop();
                this.push(abstractType1);
                this.push(abstractType3);
                this.push(abstractType2);
                this.push(abstractType1);
                break;
            }
            case 92: {
                int abstractType1 = this.pop();
                int abstractType2 = this.pop();
                this.push(abstractType2);
                this.push(abstractType1);
                this.push(abstractType2);
                this.push(abstractType1);
                break;
            }
            case 93: {
                int abstractType1 = this.pop();
                int abstractType2 = this.pop();
                int abstractType3 = this.pop();
                this.push(abstractType2);
                this.push(abstractType1);
                this.push(abstractType3);
                this.push(abstractType2);
                this.push(abstractType1);
                break;
            }
            case 94: {
                int abstractType1 = this.pop();
                int abstractType2 = this.pop();
                int abstractType3 = this.pop();
                int abstractType4 = this.pop();
                this.push(abstractType2);
                this.push(abstractType1);
                this.push(abstractType4);
                this.push(abstractType3);
                this.push(abstractType2);
                this.push(abstractType1);
                break;
            }
            case 95: {
                int abstractType1 = this.pop();
                int abstractType2 = this.pop();
                this.push(abstractType1);
                this.push(abstractType2);
                break;
            }
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 127: 
            case 129: 
            case 131: {
                this.pop(4);
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: {
                this.pop(4);
                this.push(0x1000003);
                this.push(0x1000000);
                break;
            }
            case 121: 
            case 123: 
            case 125: {
                this.pop(3);
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 132: {
                this.setLocal(arg2, 0x1000001);
                break;
            }
            case 133: 
            case 140: {
                this.pop(1);
                this.push(0x1000004);
                this.push(0x1000000);
                break;
            }
            case 134: {
                this.pop(1);
                this.push(0x1000002);
                break;
            }
            case 135: 
            case 141: {
                this.pop(1);
                this.push(0x1000003);
                this.push(0x1000000);
                break;
            }
            case 139: 
            case 190: 
            case 193: {
                this.pop(1);
                this.push(0x1000001);
                break;
            }
            case 148: 
            case 151: 
            case 152: {
                this.pop(4);
                this.push(0x1000001);
                break;
            }
            case 168: 
            case 169: {
                throw new IllegalArgumentException("JSR/RET are not supported with computeFrames option");
            }
            case 178: {
                this.push(symbolTable, argSymbol.value);
                break;
            }
            case 179: {
                this.pop(argSymbol.value);
                break;
            }
            case 180: {
                this.pop(1);
                this.push(symbolTable, argSymbol.value);
                break;
            }
            case 181: {
                this.pop(argSymbol.value);
                this.pop();
                break;
            }
            case 182: 
            case 183: 
            case 184: 
            case 185: {
                this.pop(argSymbol.value);
                if (opcode != 184) {
                    int abstractType1 = this.pop();
                    if (opcode == 183 && argSymbol.name.charAt(0) == '<') {
                        this.addInitializedType(abstractType1);
                    }
                }
                this.push(symbolTable, argSymbol.value);
                break;
            }
            case 186: {
                this.pop(argSymbol.value);
                this.push(symbolTable, argSymbol.value);
                break;
            }
            case 187: {
                this.push(0x3000000 | symbolTable.addUninitializedType(argSymbol.value, arg2));
                break;
            }
            case 188: {
                this.pop();
                switch (arg2) {
                    case 4: {
                        this.push(0x11000009);
                        return;
                    }
                    case 5: {
                        this.push(0x1100000B);
                        return;
                    }
                    case 6: {
                        this.push(0x11000002);
                        return;
                    }
                    case 7: {
                        this.push(0x11000003);
                        return;
                    }
                    case 8: {
                        this.push(0x1100000A);
                        return;
                    }
                    case 9: {
                        this.push(0x1100000C);
                        return;
                    }
                    case 10: {
                        this.push(0x11000001);
                        return;
                    }
                    case 11: {
                        this.push(0x11000004);
                        return;
                    }
                }
                throw new IllegalArgumentException();
            }
            case 189: {
                String arrayElementType = argSymbol.value;
                this.pop();
                if (arrayElementType.charAt(0) == '[') {
                    this.push(symbolTable, String.valueOf('[') + arrayElementType);
                    break;
                }
                this.push(0x12000000 | symbolTable.addType(arrayElementType));
                break;
            }
            case 192: {
                String castType = argSymbol.value;
                this.pop();
                if (castType.charAt(0) == '[') {
                    this.push(symbolTable, castType);
                    break;
                }
                this.push(0x2000000 | symbolTable.addType(castType));
                break;
            }
            case 197: {
                this.pop(arg2);
                this.push(symbolTable, argSymbol.value);
            }
        }
    }

    final boolean merge(SymbolTable symbolTable, Frame dstFrame, int catchTypeIndex) {
        int dim;
        int abstractOutputType;
        int i2;
        int concreteOutputType;
        boolean frameChanged = false;
        int numLocal = this.inputLocals.length;
        int numStack = this.inputStack.length;
        if (dstFrame.inputLocals == null) {
            dstFrame.inputLocals = new int[numLocal];
            frameChanged = true;
        }
        int numInputStack = 0;
        while (numInputStack < numLocal) {
            if (this.outputLocals != null && numInputStack < this.outputLocals.length) {
                concreteOutputType = this.outputLocals[numInputStack];
                if (concreteOutputType == 0) {
                    i2 = this.inputLocals[numInputStack];
                } else {
                    abstractOutputType = concreteOutputType & 0xF0000000;
                    dim = concreteOutputType & 0xF000000;
                    if (dim == 0x4000000) {
                        i2 = abstractOutputType + this.inputLocals[concreteOutputType & 0xFFFFF];
                        if ((concreteOutputType & 0x100000) != 0 && (i2 == 0x1000004 || i2 == 0x1000003)) {
                            i2 = 0x1000000;
                        }
                    } else if (dim == 0x5000000) {
                        i2 = abstractOutputType + this.inputStack[numStack - (concreteOutputType & 0xFFFFF)];
                        if ((concreteOutputType & 0x100000) != 0 && (i2 == 0x1000004 || i2 == 0x1000003)) {
                            i2 = 0x1000000;
                        }
                    } else {
                        i2 = concreteOutputType;
                    }
                }
            } else {
                i2 = this.inputLocals[numInputStack];
            }
            if (this.initializations != null) {
                i2 = this.getInitializedType(symbolTable, i2);
            }
            frameChanged |= Frame.merge(symbolTable, i2, dstFrame.inputLocals, numInputStack);
            ++numInputStack;
        }
        if (catchTypeIndex > 0) {
            numInputStack = 0;
            while (numInputStack < numLocal) {
                frameChanged |= Frame.merge(symbolTable, this.inputLocals[numInputStack], dstFrame.inputLocals, numInputStack);
                ++numInputStack;
            }
            if (dstFrame.inputStack == null) {
                dstFrame.inputStack = new int[1];
                frameChanged = true;
            }
            return frameChanged |= Frame.merge(symbolTable, catchTypeIndex, dstFrame.inputStack, 0);
        }
        numInputStack = this.inputStack.length + this.outputStackStart;
        if (dstFrame.inputStack == null) {
            dstFrame.inputStack = new int[numInputStack + this.outputStackTop];
            frameChanged = true;
        }
        i2 = 0;
        while (i2 < numInputStack) {
            concreteOutputType = this.inputStack[i2];
            if (this.initializations != null) {
                concreteOutputType = this.getInitializedType(symbolTable, concreteOutputType);
            }
            frameChanged |= Frame.merge(symbolTable, concreteOutputType, dstFrame.inputStack, i2);
            ++i2;
        }
        i2 = 0;
        while (i2 < this.outputStackTop) {
            abstractOutputType = this.outputStack[i2];
            dim = abstractOutputType & 0xF0000000;
            int kind = abstractOutputType & 0xF000000;
            if (kind == 0x4000000) {
                concreteOutputType = dim + this.inputLocals[abstractOutputType & 0xFFFFF];
                if ((abstractOutputType & 0x100000) != 0 && (concreteOutputType == 0x1000004 || concreteOutputType == 0x1000003)) {
                    concreteOutputType = 0x1000000;
                }
            } else if (kind == 0x5000000) {
                concreteOutputType = dim + this.inputStack[numStack - (abstractOutputType & 0xFFFFF)];
                if ((abstractOutputType & 0x100000) != 0 && (concreteOutputType == 0x1000004 || concreteOutputType == 0x1000003)) {
                    concreteOutputType = 0x1000000;
                }
            } else {
                concreteOutputType = abstractOutputType;
            }
            if (this.initializations != null) {
                concreteOutputType = this.getInitializedType(symbolTable, concreteOutputType);
            }
            frameChanged |= Frame.merge(symbolTable, concreteOutputType, dstFrame.inputStack, numInputStack + i2);
            ++i2;
        }
        return frameChanged;
    }

    final void accept(MethodWriter methodWriter) {
        int stackType;
        int frameIndex;
        int[] localTypes = this.inputLocals;
        int numLocal = 0;
        int numTrailingTop = 0;
        int i2 = 0;
        while (i2 < localTypes.length) {
            int stackTypes;
            i2 += (stackTypes = localTypes[i2]) != 0x1000004 && stackTypes != 0x1000003 ? 1 : 2;
            if (stackTypes == 0x1000000) {
                ++numTrailingTop;
                continue;
            }
            numLocal += numTrailingTop + 1;
            numTrailingTop = 0;
        }
        int[] var10 = this.inputStack;
        int numStack = 0;
        i2 = 0;
        while (i2 < var10.length) {
            i2 += (frameIndex = var10[i2]) != 0x1000004 && frameIndex != 0x1000003 ? 1 : 2;
            ++numStack;
        }
        frameIndex = methodWriter.visitFrameStart(this.owner.bytecodeOffset, numLocal, numStack);
        i2 = 0;
        while (numLocal-- > 0) {
            i2 += (stackType = localTypes[i2]) != 0x1000004 && stackType != 0x1000003 ? 1 : 2;
            methodWriter.visitAbstractType(frameIndex++, stackType);
        }
        i2 = 0;
        while (numStack-- > 0) {
            i2 += (stackType = var10[i2]) != 0x1000004 && stackType != 0x1000003 ? 1 : 2;
            methodWriter.visitAbstractType(frameIndex++, stackType);
        }
        methodWriter.visitFrameEnd();
    }
}

