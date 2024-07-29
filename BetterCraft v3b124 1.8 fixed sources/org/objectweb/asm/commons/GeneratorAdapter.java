/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.commons.TableSwitchGenerator;

public class GeneratorAdapter
extends LocalVariablesSorter {
    private static final String CLDESC = "Ljava/lang/Class;";
    private static final Type BYTE_TYPE;
    private static final Type BOOLEAN_TYPE;
    private static final Type SHORT_TYPE;
    private static final Type CHARACTER_TYPE;
    private static final Type INTEGER_TYPE;
    private static final Type FLOAT_TYPE;
    private static final Type LONG_TYPE;
    private static final Type DOUBLE_TYPE;
    private static final Type NUMBER_TYPE;
    private static final Type OBJECT_TYPE;
    private static final Method BOOLEAN_VALUE;
    private static final Method CHAR_VALUE;
    private static final Method INT_VALUE;
    private static final Method FLOAT_VALUE;
    private static final Method LONG_VALUE;
    private static final Method DOUBLE_VALUE;
    public static final int ADD = 96;
    public static final int SUB = 100;
    public static final int MUL = 104;
    public static final int DIV = 108;
    public static final int REM = 112;
    public static final int NEG = 116;
    public static final int SHL = 120;
    public static final int SHR = 122;
    public static final int USHR = 124;
    public static final int AND = 126;
    public static final int OR = 128;
    public static final int XOR = 130;
    public static final int EQ = 153;
    public static final int NE = 154;
    public static final int LT = 155;
    public static final int GE = 156;
    public static final int GT = 157;
    public static final int LE = 158;
    private final int access;
    private final Type returnType;
    private final Type[] argumentTypes;
    private final List localTypes = new ArrayList();
    static /* synthetic */ Class class$org$objectweb$asm$commons$GeneratorAdapter;

    public GeneratorAdapter(MethodVisitor methodVisitor, int n2, String string, String string2) {
        this(327680, methodVisitor, n2, string, string2);
        if (this.getClass() != class$org$objectweb$asm$commons$GeneratorAdapter) {
            throw new IllegalStateException();
        }
    }

    protected GeneratorAdapter(int n2, MethodVisitor methodVisitor, int n3, String string, String string2) {
        super(n2, n3, string2, methodVisitor);
        this.access = n3;
        this.returnType = Type.getReturnType(string2);
        this.argumentTypes = Type.getArgumentTypes(string2);
    }

    public GeneratorAdapter(int n2, Method method, MethodVisitor methodVisitor) {
        this(methodVisitor, n2, null, method.getDescriptor());
    }

    public GeneratorAdapter(int n2, Method method, String string, Type[] typeArray, ClassVisitor classVisitor) {
        this(n2, method, classVisitor.visitMethod(n2, method.getName(), method.getDescriptor(), string, GeneratorAdapter.getInternalNames(typeArray)));
    }

    private static String[] getInternalNames(Type[] typeArray) {
        if (typeArray == null) {
            return null;
        }
        String[] stringArray = new String[typeArray.length];
        for (int i2 = 0; i2 < stringArray.length; ++i2) {
            stringArray[i2] = typeArray[i2].getInternalName();
        }
        return stringArray;
    }

    public void push(boolean bl2) {
        this.push(bl2 ? 1 : 0);
    }

    public void push(int n2) {
        if (n2 >= -1 && n2 <= 5) {
            this.mv.visitInsn(3 + n2);
        } else if (n2 >= -128 && n2 <= 127) {
            this.mv.visitIntInsn(16, n2);
        } else if (n2 >= Short.MIN_VALUE && n2 <= Short.MAX_VALUE) {
            this.mv.visitIntInsn(17, n2);
        } else {
            this.mv.visitLdcInsn(new Integer(n2));
        }
    }

    public void push(long l2) {
        if (l2 == 0L || l2 == 1L) {
            this.mv.visitInsn(9 + (int)l2);
        } else {
            this.mv.visitLdcInsn(new Long(l2));
        }
    }

    public void push(float f2) {
        int n2 = Float.floatToIntBits(f2);
        if ((long)n2 == 0L || n2 == 1065353216 || n2 == 0x40000000) {
            this.mv.visitInsn(11 + (int)f2);
        } else {
            this.mv.visitLdcInsn(new Float(f2));
        }
    }

    public void push(double d2) {
        long l2 = Double.doubleToLongBits(d2);
        if (l2 == 0L || l2 == 0x3FF0000000000000L) {
            this.mv.visitInsn(14 + (int)d2);
        } else {
            this.mv.visitLdcInsn(new Double(d2));
        }
    }

    public void push(String string) {
        if (string == null) {
            this.mv.visitInsn(1);
        } else {
            this.mv.visitLdcInsn(string);
        }
    }

    public void push(Type type) {
        if (type == null) {
            this.mv.visitInsn(1);
        } else {
            switch (type.getSort()) {
                case 1: {
                    this.mv.visitFieldInsn(178, "java/lang/Boolean", "TYPE", CLDESC);
                    break;
                }
                case 2: {
                    this.mv.visitFieldInsn(178, "java/lang/Character", "TYPE", CLDESC);
                    break;
                }
                case 3: {
                    this.mv.visitFieldInsn(178, "java/lang/Byte", "TYPE", CLDESC);
                    break;
                }
                case 4: {
                    this.mv.visitFieldInsn(178, "java/lang/Short", "TYPE", CLDESC);
                    break;
                }
                case 5: {
                    this.mv.visitFieldInsn(178, "java/lang/Integer", "TYPE", CLDESC);
                    break;
                }
                case 6: {
                    this.mv.visitFieldInsn(178, "java/lang/Float", "TYPE", CLDESC);
                    break;
                }
                case 7: {
                    this.mv.visitFieldInsn(178, "java/lang/Long", "TYPE", CLDESC);
                    break;
                }
                case 8: {
                    this.mv.visitFieldInsn(178, "java/lang/Double", "TYPE", CLDESC);
                    break;
                }
                default: {
                    this.mv.visitLdcInsn(type);
                }
            }
        }
    }

    public void push(Handle handle) {
        this.mv.visitLdcInsn(handle);
    }

    private int getArgIndex(int n2) {
        int n3 = (this.access & 8) == 0 ? 1 : 0;
        for (int i2 = 0; i2 < n2; ++i2) {
            n3 += this.argumentTypes[i2].getSize();
        }
        return n3;
    }

    private void loadInsn(Type type, int n2) {
        this.mv.visitVarInsn(type.getOpcode(21), n2);
    }

    private void storeInsn(Type type, int n2) {
        this.mv.visitVarInsn(type.getOpcode(54), n2);
    }

    public void loadThis() {
        if ((this.access & 8) != 0) {
            throw new IllegalStateException("no 'this' pointer within static method");
        }
        this.mv.visitVarInsn(25, 0);
    }

    public void loadArg(int n2) {
        this.loadInsn(this.argumentTypes[n2], this.getArgIndex(n2));
    }

    public void loadArgs(int n2, int n3) {
        int n4 = this.getArgIndex(n2);
        for (int i2 = 0; i2 < n3; ++i2) {
            Type type = this.argumentTypes[n2 + i2];
            this.loadInsn(type, n4);
            n4 += type.getSize();
        }
    }

    public void loadArgs() {
        this.loadArgs(0, this.argumentTypes.length);
    }

    public void loadArgArray() {
        this.push(this.argumentTypes.length);
        this.newArray(OBJECT_TYPE);
        for (int i2 = 0; i2 < this.argumentTypes.length; ++i2) {
            this.dup();
            this.push(i2);
            this.loadArg(i2);
            this.box(this.argumentTypes[i2]);
            this.arrayStore(OBJECT_TYPE);
        }
    }

    public void storeArg(int n2) {
        this.storeInsn(this.argumentTypes[n2], this.getArgIndex(n2));
    }

    public Type getLocalType(int n2) {
        return (Type)this.localTypes.get(n2 - this.firstLocal);
    }

    protected void setLocalType(int n2, Type type) {
        int n3 = n2 - this.firstLocal;
        while (this.localTypes.size() < n3 + 1) {
            this.localTypes.add(null);
        }
        this.localTypes.set(n3, type);
    }

    public void loadLocal(int n2) {
        this.loadInsn(this.getLocalType(n2), n2);
    }

    public void loadLocal(int n2, Type type) {
        this.setLocalType(n2, type);
        this.loadInsn(type, n2);
    }

    public void storeLocal(int n2) {
        this.storeInsn(this.getLocalType(n2), n2);
    }

    public void storeLocal(int n2, Type type) {
        this.setLocalType(n2, type);
        this.storeInsn(type, n2);
    }

    public void arrayLoad(Type type) {
        this.mv.visitInsn(type.getOpcode(46));
    }

    public void arrayStore(Type type) {
        this.mv.visitInsn(type.getOpcode(79));
    }

    public void pop() {
        this.mv.visitInsn(87);
    }

    public void pop2() {
        this.mv.visitInsn(88);
    }

    public void dup() {
        this.mv.visitInsn(89);
    }

    public void dup2() {
        this.mv.visitInsn(92);
    }

    public void dupX1() {
        this.mv.visitInsn(90);
    }

    public void dupX2() {
        this.mv.visitInsn(91);
    }

    public void dup2X1() {
        this.mv.visitInsn(93);
    }

    public void dup2X2() {
        this.mv.visitInsn(94);
    }

    public void swap() {
        this.mv.visitInsn(95);
    }

    public void swap(Type type, Type type2) {
        if (type2.getSize() == 1) {
            if (type.getSize() == 1) {
                this.swap();
            } else {
                this.dupX2();
                this.pop();
            }
        } else if (type.getSize() == 1) {
            this.dup2X1();
            this.pop2();
        } else {
            this.dup2X2();
            this.pop2();
        }
    }

    public void math(int n2, Type type) {
        this.mv.visitInsn(type.getOpcode(n2));
    }

    public void not() {
        this.mv.visitInsn(4);
        this.mv.visitInsn(130);
    }

    public void iinc(int n2, int n3) {
        this.mv.visitIincInsn(n2, n3);
    }

    public void cast(Type type, Type type2) {
        if (type != type2) {
            if (type == Type.DOUBLE_TYPE) {
                if (type2 == Type.FLOAT_TYPE) {
                    this.mv.visitInsn(144);
                } else if (type2 == Type.LONG_TYPE) {
                    this.mv.visitInsn(143);
                } else {
                    this.mv.visitInsn(142);
                    this.cast(Type.INT_TYPE, type2);
                }
            } else if (type == Type.FLOAT_TYPE) {
                if (type2 == Type.DOUBLE_TYPE) {
                    this.mv.visitInsn(141);
                } else if (type2 == Type.LONG_TYPE) {
                    this.mv.visitInsn(140);
                } else {
                    this.mv.visitInsn(139);
                    this.cast(Type.INT_TYPE, type2);
                }
            } else if (type == Type.LONG_TYPE) {
                if (type2 == Type.DOUBLE_TYPE) {
                    this.mv.visitInsn(138);
                } else if (type2 == Type.FLOAT_TYPE) {
                    this.mv.visitInsn(137);
                } else {
                    this.mv.visitInsn(136);
                    this.cast(Type.INT_TYPE, type2);
                }
            } else if (type2 == Type.BYTE_TYPE) {
                this.mv.visitInsn(145);
            } else if (type2 == Type.CHAR_TYPE) {
                this.mv.visitInsn(146);
            } else if (type2 == Type.DOUBLE_TYPE) {
                this.mv.visitInsn(135);
            } else if (type2 == Type.FLOAT_TYPE) {
                this.mv.visitInsn(134);
            } else if (type2 == Type.LONG_TYPE) {
                this.mv.visitInsn(133);
            } else if (type2 == Type.SHORT_TYPE) {
                this.mv.visitInsn(147);
            }
        }
    }

    private static Type getBoxedType(Type type) {
        switch (type.getSort()) {
            case 3: {
                return BYTE_TYPE;
            }
            case 1: {
                return BOOLEAN_TYPE;
            }
            case 4: {
                return SHORT_TYPE;
            }
            case 2: {
                return CHARACTER_TYPE;
            }
            case 5: {
                return INTEGER_TYPE;
            }
            case 6: {
                return FLOAT_TYPE;
            }
            case 7: {
                return LONG_TYPE;
            }
            case 8: {
                return DOUBLE_TYPE;
            }
        }
        return type;
    }

    public void box(Type type) {
        if (type.getSort() == 10 || type.getSort() == 9) {
            return;
        }
        if (type == Type.VOID_TYPE) {
            this.push((String)null);
        } else {
            Type type2 = GeneratorAdapter.getBoxedType(type);
            this.newInstance(type2);
            if (type.getSize() == 2) {
                this.dupX2();
                this.dupX2();
                this.pop();
            } else {
                this.dupX1();
                this.swap();
            }
            this.invokeConstructor(type2, new Method("<init>", Type.VOID_TYPE, new Type[]{type}));
        }
    }

    public void valueOf(Type type) {
        if (type.getSort() == 10 || type.getSort() == 9) {
            return;
        }
        if (type == Type.VOID_TYPE) {
            this.push((String)null);
        } else {
            Type type2 = GeneratorAdapter.getBoxedType(type);
            this.invokeStatic(type2, new Method("valueOf", type2, new Type[]{type}));
        }
    }

    public void unbox(Type type) {
        Type type2 = NUMBER_TYPE;
        Method method = null;
        switch (type.getSort()) {
            case 0: {
                return;
            }
            case 2: {
                type2 = CHARACTER_TYPE;
                method = CHAR_VALUE;
                break;
            }
            case 1: {
                type2 = BOOLEAN_TYPE;
                method = BOOLEAN_VALUE;
                break;
            }
            case 8: {
                method = DOUBLE_VALUE;
                break;
            }
            case 6: {
                method = FLOAT_VALUE;
                break;
            }
            case 7: {
                method = LONG_VALUE;
                break;
            }
            case 3: 
            case 4: 
            case 5: {
                method = INT_VALUE;
            }
        }
        if (method == null) {
            this.checkCast(type);
        } else {
            this.checkCast(type2);
            this.invokeVirtual(type2, method);
        }
    }

    public Label newLabel() {
        return new Label();
    }

    public void mark(Label label) {
        this.mv.visitLabel(label);
    }

    public Label mark() {
        Label label = new Label();
        this.mv.visitLabel(label);
        return label;
    }

    public void ifCmp(Type type, int n2, Label label) {
        switch (type.getSort()) {
            case 7: {
                this.mv.visitInsn(148);
                break;
            }
            case 8: {
                this.mv.visitInsn(n2 == 156 || n2 == 157 ? 151 : 152);
                break;
            }
            case 6: {
                this.mv.visitInsn(n2 == 156 || n2 == 157 ? 149 : 150);
                break;
            }
            case 9: 
            case 10: {
                switch (n2) {
                    case 153: {
                        this.mv.visitJumpInsn(165, label);
                        return;
                    }
                    case 154: {
                        this.mv.visitJumpInsn(166, label);
                        return;
                    }
                }
                throw new IllegalArgumentException("Bad comparison for type " + type);
            }
            default: {
                int n3 = -1;
                switch (n2) {
                    case 153: {
                        n3 = 159;
                        break;
                    }
                    case 154: {
                        n3 = 160;
                        break;
                    }
                    case 156: {
                        n3 = 162;
                        break;
                    }
                    case 155: {
                        n3 = 161;
                        break;
                    }
                    case 158: {
                        n3 = 164;
                        break;
                    }
                    case 157: {
                        n3 = 163;
                    }
                }
                this.mv.visitJumpInsn(n3, label);
                return;
            }
        }
        this.mv.visitJumpInsn(n2, label);
    }

    public void ifICmp(int n2, Label label) {
        this.ifCmp(Type.INT_TYPE, n2, label);
    }

    public void ifZCmp(int n2, Label label) {
        this.mv.visitJumpInsn(n2, label);
    }

    public void ifNull(Label label) {
        this.mv.visitJumpInsn(198, label);
    }

    public void ifNonNull(Label label) {
        this.mv.visitJumpInsn(199, label);
    }

    public void goTo(Label label) {
        this.mv.visitJumpInsn(167, label);
    }

    public void ret(int n2) {
        this.mv.visitVarInsn(169, n2);
    }

    public void tableSwitch(int[] nArray, TableSwitchGenerator tableSwitchGenerator) {
        float f2 = nArray.length == 0 ? 0.0f : (float)nArray.length / (float)(nArray[nArray.length - 1] - nArray[0] + 1);
        this.tableSwitch(nArray, tableSwitchGenerator, f2 >= 0.5f);
    }

    public void tableSwitch(int[] nArray, TableSwitchGenerator tableSwitchGenerator, boolean bl2) {
        for (int i2 = 1; i2 < nArray.length; ++i2) {
            if (nArray[i2] >= nArray[i2 - 1]) continue;
            throw new IllegalArgumentException("keys must be sorted ascending");
        }
        Label label = this.newLabel();
        Label label2 = this.newLabel();
        if (nArray.length > 0) {
            int n2 = nArray.length;
            int n3 = nArray[0];
            int n4 = nArray[n2 - 1];
            int n5 = n4 - n3 + 1;
            if (bl2) {
                int n6;
                Object[] objectArray = new Label[n5];
                Arrays.fill(objectArray, label);
                for (n6 = 0; n6 < n2; ++n6) {
                    objectArray[nArray[n6] - n3] = this.newLabel();
                }
                this.mv.visitTableSwitchInsn(n3, n4, label, (Label[])objectArray);
                for (n6 = 0; n6 < n5; ++n6) {
                    Object object = objectArray[n6];
                    if (object == label) continue;
                    this.mark((Label)object);
                    tableSwitchGenerator.generateCase(n6 + n3, label2);
                }
            } else {
                int n7;
                Label[] labelArray = new Label[n2];
                for (n7 = 0; n7 < n2; ++n7) {
                    labelArray[n7] = this.newLabel();
                }
                this.mv.visitLookupSwitchInsn(label, nArray, labelArray);
                for (n7 = 0; n7 < n2; ++n7) {
                    this.mark(labelArray[n7]);
                    tableSwitchGenerator.generateCase(nArray[n7], label2);
                }
            }
        }
        this.mark(label);
        tableSwitchGenerator.generateDefault();
        this.mark(label2);
    }

    public void returnValue() {
        this.mv.visitInsn(this.returnType.getOpcode(172));
    }

    private void fieldInsn(int n2, Type type, String string, Type type2) {
        this.mv.visitFieldInsn(n2, type.getInternalName(), string, type2.getDescriptor());
    }

    public void getStatic(Type type, String string, Type type2) {
        this.fieldInsn(178, type, string, type2);
    }

    public void putStatic(Type type, String string, Type type2) {
        this.fieldInsn(179, type, string, type2);
    }

    public void getField(Type type, String string, Type type2) {
        this.fieldInsn(180, type, string, type2);
    }

    public void putField(Type type, String string, Type type2) {
        this.fieldInsn(181, type, string, type2);
    }

    private void invokeInsn(int n2, Type type, Method method, boolean bl2) {
        String string = type.getSort() == 9 ? type.getDescriptor() : type.getInternalName();
        this.mv.visitMethodInsn(n2, string, method.getName(), method.getDescriptor(), bl2);
    }

    public void invokeVirtual(Type type, Method method) {
        this.invokeInsn(182, type, method, false);
    }

    public void invokeConstructor(Type type, Method method) {
        this.invokeInsn(183, type, method, false);
    }

    public void invokeStatic(Type type, Method method) {
        this.invokeInsn(184, type, method, false);
    }

    public void invokeInterface(Type type, Method method) {
        this.invokeInsn(185, type, method, true);
    }

    public void invokeDynamic(String string, String string2, Handle handle, Object ... objectArray) {
        this.mv.visitInvokeDynamicInsn(string, string2, handle, objectArray);
    }

    private void typeInsn(int n2, Type type) {
        this.mv.visitTypeInsn(n2, type.getInternalName());
    }

    public void newInstance(Type type) {
        this.typeInsn(187, type);
    }

    public void newArray(Type type) {
        int n2;
        switch (type.getSort()) {
            case 1: {
                n2 = 4;
                break;
            }
            case 2: {
                n2 = 5;
                break;
            }
            case 3: {
                n2 = 8;
                break;
            }
            case 4: {
                n2 = 9;
                break;
            }
            case 5: {
                n2 = 10;
                break;
            }
            case 6: {
                n2 = 6;
                break;
            }
            case 7: {
                n2 = 11;
                break;
            }
            case 8: {
                n2 = 7;
                break;
            }
            default: {
                this.typeInsn(189, type);
                return;
            }
        }
        this.mv.visitIntInsn(188, n2);
    }

    public void arrayLength() {
        this.mv.visitInsn(190);
    }

    public void throwException() {
        this.mv.visitInsn(191);
    }

    public void throwException(Type type, String string) {
        this.newInstance(type);
        this.dup();
        this.push(string);
        this.invokeConstructor(type, Method.getMethod("void <init> (String)"));
        this.throwException();
    }

    public void checkCast(Type type) {
        if (!type.equals(OBJECT_TYPE)) {
            this.typeInsn(192, type);
        }
    }

    public void instanceOf(Type type) {
        this.typeInsn(193, type);
    }

    public void monitorEnter() {
        this.mv.visitInsn(194);
    }

    public void monitorExit() {
        this.mv.visitInsn(195);
    }

    public void endMethod() {
        if ((this.access & 0x400) == 0) {
            this.mv.visitMaxs(0, 0);
        }
        this.mv.visitEnd();
    }

    public void catchException(Label label, Label label2, Type type) {
        Label label3 = new Label();
        if (type == null) {
            this.mv.visitTryCatchBlock(label, label2, label3, null);
        } else {
            this.mv.visitTryCatchBlock(label, label2, label3, type.getInternalName());
        }
        this.mark(label3);
    }

    static {
        GeneratorAdapter._clinit_();
        BYTE_TYPE = Type.getObjectType("java/lang/Byte");
        BOOLEAN_TYPE = Type.getObjectType("java/lang/Boolean");
        SHORT_TYPE = Type.getObjectType("java/lang/Short");
        CHARACTER_TYPE = Type.getObjectType("java/lang/Character");
        INTEGER_TYPE = Type.getObjectType("java/lang/Integer");
        FLOAT_TYPE = Type.getObjectType("java/lang/Float");
        LONG_TYPE = Type.getObjectType("java/lang/Long");
        DOUBLE_TYPE = Type.getObjectType("java/lang/Double");
        NUMBER_TYPE = Type.getObjectType("java/lang/Number");
        OBJECT_TYPE = Type.getObjectType("java/lang/Object");
        BOOLEAN_VALUE = Method.getMethod("boolean booleanValue()");
        CHAR_VALUE = Method.getMethod("char charValue()");
        INT_VALUE = Method.getMethod("int intValue()");
        FLOAT_VALUE = Method.getMethod("float floatValue()");
        LONG_VALUE = Method.getMethod("long longValue()");
        DOUBLE_VALUE = Method.getMethod("double doubleValue()");
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            String string2 = classNotFoundException.getMessage();
            throw new NoClassDefFoundError(string2);
        }
    }

    private static void _clinit_() {
        class$org$objectweb$asm$commons$GeneratorAdapter = GeneratorAdapter.class$("org.objectweb.asm.commons.GeneratorAdapter");
    }
}

