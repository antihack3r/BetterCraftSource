/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.global;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

public abstract class ClassEditor
extends ClassVisitor {
    private ClassEditorType type;

    public ClassEditor(ClassEditorType type) {
        super(262144);
        this.type = type;
    }

    public void accept(String name, ClassNode node) {
    }

    public void accept(String name, ClassVisitor visitor) {
        this.cv = visitor;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public String visitMapping(String typeName) {
        return typeName;
    }

    public ClassEditorType getType() {
        return this.type;
    }

    public static enum ClassEditorType {
        CLASS_VISITOR,
        CLASS_NODE,
        CLASS_VISITOR_AND_REMAPPER;

    }
}

