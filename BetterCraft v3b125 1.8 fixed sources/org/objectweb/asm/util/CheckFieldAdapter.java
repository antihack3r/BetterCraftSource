/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.CheckAnnotationAdapter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;

public class CheckFieldAdapter
extends FieldVisitor {
    private boolean end;
    static /* synthetic */ Class class$org$objectweb$asm$util$CheckFieldAdapter;

    public CheckFieldAdapter(FieldVisitor fieldVisitor) {
        this(327680, fieldVisitor);
        if (this.getClass() != class$org$objectweb$asm$util$CheckFieldAdapter) {
            throw new IllegalStateException();
        }
    }

    protected CheckFieldAdapter(int n2, FieldVisitor fieldVisitor) {
        super(n2, fieldVisitor);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        this.checkEnd();
        CheckMethodAdapter.checkDesc(string, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(string, bl2));
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        this.checkEnd();
        int n3 = n2 >>> 24;
        if (n3 != 19) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(n3));
        }
        CheckClassAdapter.checkTypeRefAndPath(n2, typePath);
        CheckMethodAdapter.checkDesc(string, false);
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(n2, typePath, string, bl2));
    }

    public void visitAttribute(Attribute attribute) {
        this.checkEnd();
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid attribute (must not be null)");
        }
        super.visitAttribute(attribute);
    }

    public void visitEnd() {
        this.checkEnd();
        this.end = true;
        super.visitEnd();
    }

    private void checkEnd() {
        if (this.end) {
            throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
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

    static {
        class$org$objectweb$asm$util$CheckFieldAdapter = CheckFieldAdapter.class$("org.objectweb.asm.util.CheckFieldAdapter");
    }
}

