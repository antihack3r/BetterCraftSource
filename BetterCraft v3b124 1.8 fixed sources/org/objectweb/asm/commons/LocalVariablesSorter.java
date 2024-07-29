/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class LocalVariablesSorter
extends MethodVisitor {
    private static final Type OBJECT_TYPE;
    private int[] mapping = new int[40];
    private Object[] newLocals = new Object[20];
    protected final int firstLocal;
    protected int nextLocal;
    static /* synthetic */ Class class$org$objectweb$asm$commons$LocalVariablesSorter;

    public LocalVariablesSorter(int n2, String string, MethodVisitor methodVisitor) {
        this(327680, n2, string, methodVisitor);
        if (this.getClass() != class$org$objectweb$asm$commons$LocalVariablesSorter) {
            throw new IllegalStateException();
        }
    }

    protected LocalVariablesSorter(int n2, int n3, String string, MethodVisitor methodVisitor) {
        super(n2, methodVisitor);
        Type[] typeArray = Type.getArgumentTypes(string);
        this.nextLocal = (8 & n3) == 0 ? 1 : 0;
        for (int i2 = 0; i2 < typeArray.length; ++i2) {
            this.nextLocal += typeArray[i2].getSize();
        }
        this.firstLocal = this.nextLocal;
    }

    public void visitVarInsn(int n2, int n3) {
        Type type;
        switch (n2) {
            case 22: 
            case 55: {
                type = Type.LONG_TYPE;
                break;
            }
            case 24: 
            case 57: {
                type = Type.DOUBLE_TYPE;
                break;
            }
            case 23: 
            case 56: {
                type = Type.FLOAT_TYPE;
                break;
            }
            case 21: 
            case 54: {
                type = Type.INT_TYPE;
                break;
            }
            default: {
                type = OBJECT_TYPE;
            }
        }
        this.mv.visitVarInsn(n2, this.remap(n3, type));
    }

    public void visitIincInsn(int n2, int n3) {
        this.mv.visitIincInsn(this.remap(n2, Type.INT_TYPE), n3);
    }

    public void visitMaxs(int n2, int n3) {
        this.mv.visitMaxs(n2, this.nextLocal);
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n2) {
        int n3 = this.remap(n2, Type.getType(string2));
        this.mv.visitLocalVariable(string, string2, string3, label, label2, n3);
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int n2, TypePath typePath, Label[] labelArray, Label[] labelArray2, int[] nArray, String string, boolean bl2) {
        Type type = Type.getType(string);
        int[] nArray2 = new int[nArray.length];
        for (int i2 = 0; i2 < nArray2.length; ++i2) {
            nArray2[i2] = this.remap(nArray[i2], type);
        }
        return this.mv.visitLocalVariableAnnotation(n2, typePath, labelArray, labelArray2, nArray2, string, bl2);
    }

    public void visitFrame(int n2, int n3, Object[] objectArray, int n4, Object[] objectArray2) {
        int n5;
        if (n2 != -1) {
            throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
        }
        Object[] objectArray3 = new Object[this.newLocals.length];
        System.arraycopy(this.newLocals, 0, objectArray3, 0, objectArray3.length);
        this.updateNewLocals(this.newLocals);
        int n6 = 0;
        for (n5 = 0; n5 < n3; ++n5) {
            int n7;
            Object object = objectArray[n5];
            int n8 = n7 = object == Opcodes.LONG || object == Opcodes.DOUBLE ? 2 : 1;
            if (object != Opcodes.TOP) {
                Type type = OBJECT_TYPE;
                if (object == Opcodes.INTEGER) {
                    type = Type.INT_TYPE;
                } else if (object == Opcodes.FLOAT) {
                    type = Type.FLOAT_TYPE;
                } else if (object == Opcodes.LONG) {
                    type = Type.LONG_TYPE;
                } else if (object == Opcodes.DOUBLE) {
                    type = Type.DOUBLE_TYPE;
                } else if (object instanceof String) {
                    type = Type.getObjectType((String)object);
                }
                this.setFrameLocal(this.remap(n6, type), object);
            }
            n6 += n7;
        }
        n6 = 0;
        n5 = 0;
        int n9 = 0;
        while (n6 < this.newLocals.length) {
            Object object;
            if ((object = this.newLocals[n6++]) != null && object != Opcodes.TOP) {
                this.newLocals[n9] = object;
                n5 = n9 + 1;
                if (object == Opcodes.LONG || object == Opcodes.DOUBLE) {
                    ++n6;
                }
            } else {
                this.newLocals[n9] = Opcodes.TOP;
            }
            ++n9;
        }
        this.mv.visitFrame(n2, n5, this.newLocals, n4, objectArray2);
        this.newLocals = objectArray3;
    }

    public int newLocal(Type type) {
        Object object;
        switch (type.getSort()) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                object = Opcodes.INTEGER;
                break;
            }
            case 6: {
                object = Opcodes.FLOAT;
                break;
            }
            case 7: {
                object = Opcodes.LONG;
                break;
            }
            case 8: {
                object = Opcodes.DOUBLE;
                break;
            }
            case 9: {
                object = type.getDescriptor();
                break;
            }
            default: {
                object = type.getInternalName();
            }
        }
        int n2 = this.newLocalMapping(type);
        this.setLocalType(n2, type);
        this.setFrameLocal(n2, object);
        return n2;
    }

    protected void updateNewLocals(Object[] objectArray) {
    }

    protected void setLocalType(int n2, Type type) {
    }

    private void setFrameLocal(int n2, Object object) {
        int n3 = this.newLocals.length;
        if (n2 >= n3) {
            Object[] objectArray = new Object[Math.max(2 * n3, n2 + 1)];
            System.arraycopy(this.newLocals, 0, objectArray, 0, n3);
            this.newLocals = objectArray;
        }
        this.newLocals[n2] = object;
    }

    private int remap(int n2, Type type) {
        int n3;
        int n4;
        if (n2 + type.getSize() <= this.firstLocal) {
            return n2;
        }
        int n5 = 2 * n2 + type.getSize() - 1;
        if (n5 >= (n4 = this.mapping.length)) {
            int[] nArray = new int[Math.max(2 * n4, n5 + 1)];
            System.arraycopy(this.mapping, 0, nArray, 0, n4);
            this.mapping = nArray;
        }
        if ((n3 = this.mapping[n5]) == 0) {
            n3 = this.newLocalMapping(type);
            this.setLocalType(n3, type);
            this.mapping[n5] = n3 + 1;
        } else {
            --n3;
        }
        return n3;
    }

    protected int newLocalMapping(Type type) {
        int n2 = this.nextLocal;
        this.nextLocal += type.getSize();
        return n2;
    }

    static {
        LocalVariablesSorter._clinit_();
        OBJECT_TYPE = Type.getObjectType("java/lang/Object");
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
        class$org$objectweb$asm$commons$LocalVariablesSorter = LocalVariablesSorter.class$("org.objectweb.asm.commons.LocalVariablesSorter");
    }
}

