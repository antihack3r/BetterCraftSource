/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.CheckAnnotationAdapter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.CheckMethodAdapter$1;

public class CheckMethodAdapter
extends MethodVisitor {
    public int version;
    private int access;
    private boolean startCode;
    private boolean endCode;
    private boolean endMethod;
    private int insnCount;
    private final Map labels;
    private Set usedLabels;
    private int expandedFrames;
    private int compressedFrames;
    private int lastFrame = -1;
    private List handlers;
    private static final int[] TYPE;
    private static Field labelStatusField;
    static /* synthetic */ Class class$org$objectweb$asm$util$CheckMethodAdapter;
    static /* synthetic */ Class class$org$objectweb$asm$Label;

    public CheckMethodAdapter(MethodVisitor methodVisitor) {
        this(methodVisitor, new HashMap());
    }

    public CheckMethodAdapter(MethodVisitor methodVisitor, Map map) {
        this(327680, methodVisitor, map);
        if (this.getClass() != class$org$objectweb$asm$util$CheckMethodAdapter) {
            throw new IllegalStateException();
        }
    }

    protected CheckMethodAdapter(int n2, MethodVisitor methodVisitor, Map map) {
        super(n2, methodVisitor);
        this.labels = map;
        this.usedLabels = new HashSet();
        this.handlers = new ArrayList();
    }

    public CheckMethodAdapter(int n2, String string, String string2, MethodVisitor methodVisitor, Map map) {
        this(new CheckMethodAdapter$1(327680, n2, string, string2, null, null, methodVisitor), map);
        this.access = n2;
    }

    public void visitParameter(String string, int n2) {
        if (string != null) {
            CheckMethodAdapter.checkUnqualifiedName(this.version, string, "name");
        }
        CheckClassAdapter.checkAccess(n2, 36880);
        super.visitParameter(string, n2);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        this.checkEndMethod();
        CheckMethodAdapter.checkDesc(string, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(string, bl2));
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        this.checkEndMethod();
        int n3 = n2 >>> 24;
        if (n3 != 1 && n3 != 18 && n3 != 20 && n3 != 21 && n3 != 22 && n3 != 23) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(n3));
        }
        CheckClassAdapter.checkTypeRefAndPath(n2, typePath);
        CheckMethodAdapter.checkDesc(string, false);
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(n2, typePath, string, bl2));
    }

    public AnnotationVisitor visitAnnotationDefault() {
        this.checkEndMethod();
        return new CheckAnnotationAdapter(super.visitAnnotationDefault(), false);
    }

    public AnnotationVisitor visitParameterAnnotation(int n2, String string, boolean bl2) {
        this.checkEndMethod();
        CheckMethodAdapter.checkDesc(string, false);
        return new CheckAnnotationAdapter(super.visitParameterAnnotation(n2, string, bl2));
    }

    public void visitAttribute(Attribute attribute) {
        this.checkEndMethod();
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid attribute (must not be null)");
        }
        super.visitAttribute(attribute);
    }

    public void visitCode() {
        if ((this.access & 0x400) != 0) {
            throw new RuntimeException("Abstract methods cannot have code");
        }
        this.startCode = true;
        super.visitCode();
    }

    public void visitFrame(int n2, int n3, Object[] objectArray, int n4, Object[] objectArray2) {
        int n5;
        int n6;
        int n7;
        if (this.insnCount == this.lastFrame) {
            throw new IllegalStateException("At most one frame can be visited at a given code location.");
        }
        this.lastFrame = this.insnCount;
        switch (n2) {
            case -1: 
            case 0: {
                n7 = Integer.MAX_VALUE;
                n6 = Integer.MAX_VALUE;
                break;
            }
            case 3: {
                n7 = 0;
                n6 = 0;
                break;
            }
            case 4: {
                n7 = 0;
                n6 = 1;
                break;
            }
            case 1: 
            case 2: {
                n7 = 3;
                n6 = 0;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid frame type " + n2);
            }
        }
        if (n3 > n7) {
            throw new IllegalArgumentException("Invalid nLocal=" + n3 + " for frame type " + n2);
        }
        if (n4 > n6) {
            throw new IllegalArgumentException("Invalid nStack=" + n4 + " for frame type " + n2);
        }
        if (n2 != 2) {
            if (n3 > 0 && (objectArray == null || objectArray.length < n3)) {
                throw new IllegalArgumentException("Array local[] is shorter than nLocal");
            }
            for (n5 = 0; n5 < n3; ++n5) {
                this.checkFrameValue(objectArray[n5]);
            }
        }
        if (n4 > 0 && (objectArray2 == null || objectArray2.length < n4)) {
            throw new IllegalArgumentException("Array stack[] is shorter than nStack");
        }
        for (n5 = 0; n5 < n4; ++n5) {
            this.checkFrameValue(objectArray2[n5]);
        }
        if (n2 == -1) {
            ++this.expandedFrames;
        } else {
            ++this.compressedFrames;
        }
        if (this.expandedFrames > 0 && this.compressedFrames > 0) {
            throw new RuntimeException("Expanded and compressed frames must not be mixed.");
        }
        super.visitFrame(n2, n3, objectArray, n4, objectArray2);
    }

    public void visitInsn(int n2) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(n2, 0);
        super.visitInsn(n2);
        ++this.insnCount;
    }

    public void visitIntInsn(int n2, int n3) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(n2, 1);
        switch (n2) {
            case 16: {
                CheckMethodAdapter.checkSignedByte(n3, "Invalid operand");
                break;
            }
            case 17: {
                CheckMethodAdapter.checkSignedShort(n3, "Invalid operand");
                break;
            }
            default: {
                if (n3 >= 4 && n3 <= 11) break;
                throw new IllegalArgumentException("Invalid operand (must be an array type code T_...): " + n3);
            }
        }
        super.visitIntInsn(n2, n3);
        ++this.insnCount;
    }

    public void visitVarInsn(int n2, int n3) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(n2, 2);
        CheckMethodAdapter.checkUnsignedShort(n3, "Invalid variable index");
        super.visitVarInsn(n2, n3);
        ++this.insnCount;
    }

    public void visitTypeInsn(int n2, String string) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(n2, 3);
        CheckMethodAdapter.checkInternalName(string, "type");
        if (n2 == 187 && string.charAt(0) == '[') {
            throw new IllegalArgumentException("NEW cannot be used to create arrays: " + string);
        }
        super.visitTypeInsn(n2, string);
        ++this.insnCount;
    }

    public void visitFieldInsn(int n2, String string, String string2, String string3) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(n2, 4);
        CheckMethodAdapter.checkInternalName(string, "owner");
        CheckMethodAdapter.checkUnqualifiedName(this.version, string2, "name");
        CheckMethodAdapter.checkDesc(string3, false);
        super.visitFieldInsn(n2, string, string2, string3);
        ++this.insnCount;
    }

    public void visitMethodInsn(int n2, String string, String string2, String string3) {
        if (this.api >= 327680) {
            super.visitMethodInsn(n2, string, string2, string3);
            return;
        }
        this.doVisitMethodInsn(n2, string, string2, string3, n2 == 185);
    }

    public void visitMethodInsn(int n2, String string, String string2, String string3, boolean bl2) {
        if (this.api < 327680) {
            super.visitMethodInsn(n2, string, string2, string3, bl2);
            return;
        }
        this.doVisitMethodInsn(n2, string, string2, string3, bl2);
    }

    private void doVisitMethodInsn(int n2, String string, String string2, String string3, boolean bl2) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(n2, 5);
        if (n2 != 183 || !"<init>".equals(string2)) {
            CheckMethodAdapter.checkMethodIdentifier(this.version, string2, "name");
        }
        CheckMethodAdapter.checkInternalName(string, "owner");
        CheckMethodAdapter.checkMethodDesc(string3);
        if (n2 == 182 && bl2) {
            throw new IllegalArgumentException("INVOKEVIRTUAL can't be used with interfaces");
        }
        if (n2 == 185 && !bl2) {
            throw new IllegalArgumentException("INVOKEINTERFACE can't be used with classes");
        }
        if (n2 == 183 && bl2 && (this.version & 0xFFFF) < 52) {
            throw new IllegalArgumentException("INVOKESPECIAL can't be used with interfaces prior to Java 8");
        }
        if (this.mv != null) {
            this.mv.visitMethodInsn(n2, string, string2, string3, bl2);
        }
        ++this.insnCount;
    }

    public void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... objectArray) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkMethodIdentifier(this.version, string, "name");
        CheckMethodAdapter.checkMethodDesc(string2);
        if (handle.getTag() != 6 && handle.getTag() != 8) {
            throw new IllegalArgumentException("invalid handle tag " + handle.getTag());
        }
        for (int i2 = 0; i2 < objectArray.length; ++i2) {
            this.checkLDCConstant(objectArray[i2]);
        }
        super.visitInvokeDynamicInsn(string, string2, handle, objectArray);
        ++this.insnCount;
    }

    public void visitJumpInsn(int n2, Label label) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(n2, 6);
        this.checkLabel(label, false, "label");
        CheckMethodAdapter.checkNonDebugLabel(label);
        super.visitJumpInsn(n2, label);
        this.usedLabels.add(label);
        ++this.insnCount;
    }

    public void visitLabel(Label label) {
        this.checkStartCode();
        this.checkEndCode();
        this.checkLabel(label, false, "label");
        if (this.labels.get(label) != null) {
            throw new IllegalArgumentException("Already visited label");
        }
        this.labels.put(label, new Integer(this.insnCount));
        super.visitLabel(label);
    }

    public void visitLdcInsn(Object object) {
        this.checkStartCode();
        this.checkEndCode();
        this.checkLDCConstant(object);
        super.visitLdcInsn(object);
        ++this.insnCount;
    }

    public void visitIincInsn(int n2, int n3) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkUnsignedShort(n2, "Invalid variable index");
        CheckMethodAdapter.checkSignedShort(n3, "Invalid increment");
        super.visitIincInsn(n2, n3);
        ++this.insnCount;
    }

    public void visitTableSwitchInsn(int n2, int n3, Label label, Label ... labelArray) {
        int n4;
        this.checkStartCode();
        this.checkEndCode();
        if (n3 < n2) {
            throw new IllegalArgumentException("Max = " + n3 + " must be greater than or equal to min = " + n2);
        }
        this.checkLabel(label, false, "default label");
        CheckMethodAdapter.checkNonDebugLabel(label);
        if (labelArray == null || labelArray.length != n3 - n2 + 1) {
            throw new IllegalArgumentException("There must be max - min + 1 labels");
        }
        for (n4 = 0; n4 < labelArray.length; ++n4) {
            this.checkLabel(labelArray[n4], false, "label at index " + n4);
            CheckMethodAdapter.checkNonDebugLabel(labelArray[n4]);
        }
        super.visitTableSwitchInsn(n2, n3, label, labelArray);
        for (n4 = 0; n4 < labelArray.length; ++n4) {
            this.usedLabels.add(labelArray[n4]);
        }
        ++this.insnCount;
    }

    public void visitLookupSwitchInsn(Label label, int[] nArray, Label[] labelArray) {
        int n2;
        this.checkEndCode();
        this.checkStartCode();
        this.checkLabel(label, false, "default label");
        CheckMethodAdapter.checkNonDebugLabel(label);
        if (nArray == null || labelArray == null || nArray.length != labelArray.length) {
            throw new IllegalArgumentException("There must be the same number of keys and labels");
        }
        for (n2 = 0; n2 < labelArray.length; ++n2) {
            this.checkLabel(labelArray[n2], false, "label at index " + n2);
            CheckMethodAdapter.checkNonDebugLabel(labelArray[n2]);
        }
        super.visitLookupSwitchInsn(label, nArray, labelArray);
        this.usedLabels.add(label);
        for (n2 = 0; n2 < labelArray.length; ++n2) {
            this.usedLabels.add(labelArray[n2]);
        }
        ++this.insnCount;
    }

    public void visitMultiANewArrayInsn(String string, int n2) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkDesc(string, false);
        if (string.charAt(0) != '[') {
            throw new IllegalArgumentException("Invalid descriptor (must be an array type descriptor): " + string);
        }
        if (n2 < 1) {
            throw new IllegalArgumentException("Invalid dimensions (must be greater than 0): " + n2);
        }
        if (n2 > string.lastIndexOf(91) + 1) {
            throw new IllegalArgumentException("Invalid dimensions (must not be greater than dims(desc)): " + n2);
        }
        super.visitMultiANewArrayInsn(string, n2);
        ++this.insnCount;
    }

    public AnnotationVisitor visitInsnAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        this.checkStartCode();
        this.checkEndCode();
        int n3 = n2 >>> 24;
        if (n3 != 67 && n3 != 68 && n3 != 69 && n3 != 70 && n3 != 71 && n3 != 72 && n3 != 73 && n3 != 74 && n3 != 75) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(n3));
        }
        CheckClassAdapter.checkTypeRefAndPath(n2, typePath);
        CheckMethodAdapter.checkDesc(string, false);
        return new CheckAnnotationAdapter(super.visitInsnAnnotation(n2, typePath, string, bl2));
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        this.checkStartCode();
        this.checkEndCode();
        this.checkLabel(label, false, "start label");
        this.checkLabel(label2, false, "end label");
        this.checkLabel(label3, false, "handler label");
        CheckMethodAdapter.checkNonDebugLabel(label);
        CheckMethodAdapter.checkNonDebugLabel(label2);
        CheckMethodAdapter.checkNonDebugLabel(label3);
        if (this.labels.get(label) != null || this.labels.get(label2) != null || this.labels.get(label3) != null) {
            throw new IllegalStateException("Try catch blocks must be visited before their labels");
        }
        if (string != null) {
            CheckMethodAdapter.checkInternalName(string, "type");
        }
        super.visitTryCatchBlock(label, label2, label3, string);
        this.handlers.add(label);
        this.handlers.add(label2);
    }

    public AnnotationVisitor visitTryCatchAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        this.checkStartCode();
        this.checkEndCode();
        int n3 = n2 >>> 24;
        if (n3 != 66) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(n3));
        }
        CheckClassAdapter.checkTypeRefAndPath(n2, typePath);
        CheckMethodAdapter.checkDesc(string, false);
        return new CheckAnnotationAdapter(super.visitTryCatchAnnotation(n2, typePath, string, bl2));
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n2) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkUnqualifiedName(this.version, string, "name");
        CheckMethodAdapter.checkDesc(string2, false);
        this.checkLabel(label, true, "start label");
        this.checkLabel(label2, true, "end label");
        CheckMethodAdapter.checkUnsignedShort(n2, "Invalid variable index");
        int n3 = (Integer)this.labels.get(label);
        int n4 = (Integer)this.labels.get(label2);
        if (n4 < n3) {
            throw new IllegalArgumentException("Invalid start and end labels (end must be greater than start)");
        }
        super.visitLocalVariable(string, string2, string3, label, label2, n2);
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int n2, TypePath typePath, Label[] labelArray, Label[] labelArray2, int[] nArray, String string, boolean bl2) {
        this.checkStartCode();
        this.checkEndCode();
        int n3 = n2 >>> 24;
        if (n3 != 64 && n3 != 65) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(n3));
        }
        CheckClassAdapter.checkTypeRefAndPath(n2, typePath);
        CheckMethodAdapter.checkDesc(string, false);
        if (labelArray == null || labelArray2 == null || nArray == null || labelArray2.length != labelArray.length || nArray.length != labelArray.length) {
            throw new IllegalArgumentException("Invalid start, end and index arrays (must be non null and of identical length");
        }
        for (int i2 = 0; i2 < labelArray.length; ++i2) {
            this.checkLabel(labelArray[i2], true, "start label");
            this.checkLabel(labelArray2[i2], true, "end label");
            CheckMethodAdapter.checkUnsignedShort(nArray[i2], "Invalid variable index");
            int n4 = (Integer)this.labels.get(labelArray[i2]);
            int n5 = (Integer)this.labels.get(labelArray2[i2]);
            if (n5 >= n4) continue;
            throw new IllegalArgumentException("Invalid start and end labels (end must be greater than start)");
        }
        return super.visitLocalVariableAnnotation(n2, typePath, labelArray, labelArray2, nArray, string, bl2);
    }

    public void visitLineNumber(int n2, Label label) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkUnsignedShort(n2, "Invalid line number");
        this.checkLabel(label, true, "start label");
        super.visitLineNumber(n2, label);
    }

    public void visitMaxs(int n2, int n3) {
        Object object;
        this.checkStartCode();
        this.checkEndCode();
        this.endCode = true;
        Iterator iterator = this.usedLabels.iterator();
        while (iterator.hasNext()) {
            object = (Label)iterator.next();
            if (this.labels.get(object) != null) continue;
            throw new IllegalStateException("Undefined label used");
        }
        int n4 = 0;
        while (n4 < this.handlers.size()) {
            object = (Integer)this.labels.get(this.handlers.get(n4++));
            Integer n5 = (Integer)this.labels.get(this.handlers.get(n4++));
            if (object == null || n5 == null) {
                throw new IllegalStateException("Undefined try catch block labels");
            }
            if (n5 > (Integer)object) continue;
            throw new IllegalStateException("Emty try catch block handler range");
        }
        CheckMethodAdapter.checkUnsignedShort(n2, "Invalid max stack");
        CheckMethodAdapter.checkUnsignedShort(n3, "Invalid max locals");
        super.visitMaxs(n2, n3);
    }

    public void visitEnd() {
        this.checkEndMethod();
        this.endMethod = true;
        super.visitEnd();
    }

    void checkStartCode() {
        if (!this.startCode) {
            throw new IllegalStateException("Cannot visit instructions before visitCode has been called.");
        }
    }

    void checkEndCode() {
        if (this.endCode) {
            throw new IllegalStateException("Cannot visit instructions after visitMaxs has been called.");
        }
    }

    void checkEndMethod() {
        if (this.endMethod) {
            throw new IllegalStateException("Cannot visit elements after visitEnd has been called.");
        }
    }

    void checkFrameValue(Object object) {
        if (object == Opcodes.TOP || object == Opcodes.INTEGER || object == Opcodes.FLOAT || object == Opcodes.LONG || object == Opcodes.DOUBLE || object == Opcodes.NULL || object == Opcodes.UNINITIALIZED_THIS) {
            return;
        }
        if (object instanceof String) {
            CheckMethodAdapter.checkInternalName((String)object, "Invalid stack frame value");
            return;
        }
        if (!(object instanceof Label)) {
            throw new IllegalArgumentException("Invalid stack frame value: " + object);
        }
        this.usedLabels.add((Label)object);
    }

    static void checkOpcode(int n2, int n3) {
        if (n2 < 0 || n2 > 199 || TYPE[n2] != n3) {
            throw new IllegalArgumentException("Invalid opcode: " + n2);
        }
    }

    static void checkSignedByte(int n2, String string) {
        if (n2 < -128 || n2 > 127) {
            throw new IllegalArgumentException(string + " (must be a signed byte): " + n2);
        }
    }

    static void checkSignedShort(int n2, String string) {
        if (n2 < Short.MIN_VALUE || n2 > Short.MAX_VALUE) {
            throw new IllegalArgumentException(string + " (must be a signed short): " + n2);
        }
    }

    static void checkUnsignedShort(int n2, String string) {
        if (n2 < 0 || n2 > 65535) {
            throw new IllegalArgumentException(string + " (must be an unsigned short): " + n2);
        }
    }

    static void checkConstant(Object object) {
        if (!(object instanceof Integer || object instanceof Float || object instanceof Long || object instanceof Double || object instanceof String)) {
            throw new IllegalArgumentException("Invalid constant: " + object);
        }
    }

    void checkLDCConstant(Object object) {
        if (object instanceof Type) {
            int n2 = ((Type)object).getSort();
            if (n2 != 10 && n2 != 9 && n2 != 11) {
                throw new IllegalArgumentException("Illegal LDC constant value");
            }
            if (n2 != 11 && (this.version & 0xFFFF) < 49) {
                throw new IllegalArgumentException("ldc of a constant class requires at least version 1.5");
            }
            if (n2 == 11 && (this.version & 0xFFFF) < 51) {
                throw new IllegalArgumentException("ldc of a method type requires at least version 1.7");
            }
        } else if (object instanceof Handle) {
            if ((this.version & 0xFFFF) < 51) {
                throw new IllegalArgumentException("ldc of a handle requires at least version 1.7");
            }
            int n3 = ((Handle)object).getTag();
            if (n3 < 1 || n3 > 9) {
                throw new IllegalArgumentException("invalid handle tag " + n3);
            }
        } else {
            CheckMethodAdapter.checkConstant(object);
        }
    }

    static void checkUnqualifiedName(int n2, String string, String string2) {
        if ((n2 & 0xFFFF) < 49) {
            CheckMethodAdapter.checkIdentifier(string, string2);
        } else {
            for (int i2 = 0; i2 < string.length(); ++i2) {
                if (".;[/".indexOf(string.charAt(i2)) == -1) continue;
                throw new IllegalArgumentException("Invalid " + string2 + " (must be a valid unqualified name): " + string);
            }
        }
    }

    static void checkIdentifier(String string, String string2) {
        CheckMethodAdapter.checkIdentifier(string, 0, -1, string2);
    }

    static void checkIdentifier(String string, int n2, int n3, String string2) {
        if (string == null || (n3 == -1 ? string.length() <= n2 : n3 <= n2)) {
            throw new IllegalArgumentException("Invalid " + string2 + " (must not be null or empty)");
        }
        if (!Character.isJavaIdentifierStart(string.charAt(n2))) {
            throw new IllegalArgumentException("Invalid " + string2 + " (must be a valid Java identifier): " + string);
        }
        int n4 = n3 == -1 ? string.length() : n3;
        for (int i2 = n2 + 1; i2 < n4; ++i2) {
            if (Character.isJavaIdentifierPart(string.charAt(i2))) continue;
            throw new IllegalArgumentException("Invalid " + string2 + " (must be a valid Java identifier): " + string);
        }
    }

    static void checkMethodIdentifier(int n2, String string, String string2) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Invalid " + string2 + " (must not be null or empty)");
        }
        if ((n2 & 0xFFFF) >= 49) {
            for (int i2 = 0; i2 < string.length(); ++i2) {
                if (".;[/<>".indexOf(string.charAt(i2)) == -1) continue;
                throw new IllegalArgumentException("Invalid " + string2 + " (must be a valid unqualified name): " + string);
            }
            return;
        }
        if (!Character.isJavaIdentifierStart(string.charAt(0))) {
            throw new IllegalArgumentException("Invalid " + string2 + " (must be a '<init>', '<clinit>' or a valid Java identifier): " + string);
        }
        for (int i3 = 1; i3 < string.length(); ++i3) {
            if (Character.isJavaIdentifierPart(string.charAt(i3))) continue;
            throw new IllegalArgumentException("Invalid " + string2 + " (must be '<init>' or '<clinit>' or a valid Java identifier): " + string);
        }
    }

    static void checkInternalName(String string, String string2) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Invalid " + string2 + " (must not be null or empty)");
        }
        if (string.charAt(0) == '[') {
            CheckMethodAdapter.checkDesc(string, false);
        } else {
            CheckMethodAdapter.checkInternalName(string, 0, -1, string2);
        }
    }

    static void checkInternalName(String string, int n2, int n3, String string2) {
        int n4 = n3 == -1 ? string.length() : n3;
        try {
            int n5;
            int n6 = n2;
            do {
                if ((n5 = string.indexOf(47, n6 + 1)) == -1 || n5 > n4) {
                    n5 = n4;
                }
                CheckMethodAdapter.checkIdentifier(string, n6, n5, null);
                n6 = n5 + 1;
            } while (n5 != n4);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalArgumentException("Invalid " + string2 + " (must be a fully qualified class name in internal form): " + string);
        }
    }

    static void checkDesc(String string, boolean bl2) {
        int n2 = CheckMethodAdapter.checkDesc(string, 0, bl2);
        if (n2 != string.length()) {
            throw new IllegalArgumentException("Invalid descriptor: " + string);
        }
    }

    static int checkDesc(String string, int n2, boolean bl2) {
        if (string == null || n2 >= string.length()) {
            throw new IllegalArgumentException("Invalid type descriptor (must not be null or empty)");
        }
        switch (string.charAt(n2)) {
            case 'V': {
                if (bl2) {
                    return n2 + 1;
                }
                throw new IllegalArgumentException("Invalid descriptor: " + string);
            }
            case 'B': 
            case 'C': 
            case 'D': 
            case 'F': 
            case 'I': 
            case 'J': 
            case 'S': 
            case 'Z': {
                return n2 + 1;
            }
            case '[': {
                int n3;
                for (n3 = n2 + 1; n3 < string.length() && string.charAt(n3) == '['; ++n3) {
                }
                if (n3 < string.length()) {
                    return CheckMethodAdapter.checkDesc(string, n3, false);
                }
                throw new IllegalArgumentException("Invalid descriptor: " + string);
            }
            case 'L': {
                int n4 = string.indexOf(59, n2);
                if (n4 == -1 || n4 - n2 < 2) {
                    throw new IllegalArgumentException("Invalid descriptor: " + string);
                }
                try {
                    CheckMethodAdapter.checkInternalName(string, n2 + 1, n4, null);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    throw new IllegalArgumentException("Invalid descriptor: " + string);
                }
                return n4 + 1;
            }
        }
        throw new IllegalArgumentException("Invalid descriptor: " + string);
    }

    static void checkMethodDesc(String string) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Invalid method descriptor (must not be null or empty)");
        }
        if (string.charAt(0) != '(' || string.length() < 3) {
            throw new IllegalArgumentException("Invalid descriptor: " + string);
        }
        int n2 = 1;
        if (string.charAt(n2) != ')') {
            do {
                if (string.charAt(n2) != 'V') continue;
                throw new IllegalArgumentException("Invalid descriptor: " + string);
            } while ((n2 = CheckMethodAdapter.checkDesc(string, n2, false)) < string.length() && string.charAt(n2) != ')');
        }
        if ((n2 = CheckMethodAdapter.checkDesc(string, n2 + 1, true)) != string.length()) {
            throw new IllegalArgumentException("Invalid descriptor: " + string);
        }
    }

    void checkLabel(Label label, boolean bl2, String string) {
        if (label == null) {
            throw new IllegalArgumentException("Invalid " + string + " (must not be null)");
        }
        if (bl2 && this.labels.get(label) == null) {
            throw new IllegalArgumentException("Invalid " + string + " (must be visited first)");
        }
    }

    private static void checkNonDebugLabel(Label label) {
        Field field = CheckMethodAdapter.getLabelStatusField();
        int n2 = 0;
        try {
            n2 = field == null ? 0 : (Integer)field.get(label);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new Error("Internal error");
        }
        if ((n2 & 1) != 0) {
            throw new IllegalArgumentException("Labels used for debug info cannot be reused for control flow");
        }
    }

    private static Field getLabelStatusField() {
        if (labelStatusField == null && (labelStatusField = CheckMethodAdapter.getLabelField("a")) == null) {
            labelStatusField = CheckMethodAdapter.getLabelField("status");
        }
        return labelStatusField;
    }

    private static Field getLabelField(String string) {
        try {
            Field field = class$org$objectweb$asm$Label.getDeclaredField(string);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException noSuchFieldException) {
            return null;
        }
    }

    static {
        CheckMethodAdapter._clinit_();
        String string = "BBBBBBBBBBBBBBBBCCIAADDDDDAAAAAAAAAAAAAAAAAAAABBBBBBBBDDDDDAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBJBBBBBBBBBBBBBBBBBBBBHHHHHHHHHHHHHHHHDKLBBBBBBFFFFGGGGAECEBBEEBBAMHHAA";
        TYPE = new int[string.length()];
        for (int i2 = 0; i2 < TYPE.length; ++i2) {
            CheckMethodAdapter.TYPE[i2] = string.charAt(i2) - 65 - 1;
        }
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
        class$org$objectweb$asm$util$CheckMethodAdapter = CheckMethodAdapter.class$("org.objectweb.asm.util.CheckMethodAdapter");
        class$org$objectweb$asm$Label = CheckMethodAdapter.class$("org.objectweb.asm.Label");
    }
}

