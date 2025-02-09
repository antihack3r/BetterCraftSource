/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree.analysis;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.Value;

public class BasicValue
implements Value {
    public static final BasicValue UNINITIALIZED_VALUE;
    public static final BasicValue INT_VALUE;
    public static final BasicValue FLOAT_VALUE;
    public static final BasicValue LONG_VALUE;
    public static final BasicValue DOUBLE_VALUE;
    public static final BasicValue REFERENCE_VALUE;
    public static final BasicValue RETURNADDRESS_VALUE;
    private final Type type;

    public BasicValue(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public int getSize() {
        return this.type == Type.LONG_TYPE || this.type == Type.DOUBLE_TYPE ? 2 : 1;
    }

    public boolean isReference() {
        return this.type != null && (this.type.getSort() == 10 || this.type.getSort() == 9);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BasicValue) {
            if (this.type == null) {
                return ((BasicValue)object).type == null;
            }
            return this.type.equals(((BasicValue)object).type);
        }
        return false;
    }

    public int hashCode() {
        return this.type == null ? 0 : this.type.hashCode();
    }

    public String toString() {
        if (this == UNINITIALIZED_VALUE) {
            return ".";
        }
        if (this == RETURNADDRESS_VALUE) {
            return "A";
        }
        if (this == REFERENCE_VALUE) {
            return "R";
        }
        return this.type.getDescriptor();
    }

    static {
        BasicValue._clinit_();
        UNINITIALIZED_VALUE = new BasicValue(null);
        INT_VALUE = new BasicValue(Type.INT_TYPE);
        FLOAT_VALUE = new BasicValue(Type.FLOAT_TYPE);
        LONG_VALUE = new BasicValue(Type.LONG_TYPE);
        DOUBLE_VALUE = new BasicValue(Type.DOUBLE_TYPE);
        REFERENCE_VALUE = new BasicValue(Type.getObjectType("java/lang/Object"));
        RETURNADDRESS_VALUE = new BasicValue(Type.VOID_TYPE);
    }

    static /* synthetic */ void _clinit_() {
    }
}

