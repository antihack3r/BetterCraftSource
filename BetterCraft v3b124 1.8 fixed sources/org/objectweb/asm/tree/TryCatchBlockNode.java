/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.List;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TypeAnnotationNode;

public class TryCatchBlockNode {
    public LabelNode start;
    public LabelNode end;
    public LabelNode handler;
    public String type;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    public TryCatchBlockNode(LabelNode start, LabelNode end, LabelNode handler, String type) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }

    public void updateIndex(int index) {
        int n2;
        int i2;
        int newTypeRef = 0x42000000 | index << 8;
        if (this.visibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.visibleTypeAnnotations.size();
            while (i2 < n2) {
                this.visibleTypeAnnotations.get((int)i2).typeRef = newTypeRef;
                ++i2;
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.invisibleTypeAnnotations.size();
            while (i2 < n2) {
                this.invisibleTypeAnnotations.get((int)i2).typeRef = newTypeRef;
                ++i2;
            }
        }
    }

    public void accept(MethodVisitor methodVisitor) {
        TypeAnnotationNode typeAnnotation;
        int n2;
        int i2;
        methodVisitor.visitTryCatchBlock(this.start.getLabel(), this.end.getLabel(), this.handler == null ? null : this.handler.getLabel(), this.type);
        if (this.visibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.visibleTypeAnnotations.size();
            while (i2 < n2) {
                typeAnnotation = this.visibleTypeAnnotations.get(i2);
                typeAnnotation.accept(methodVisitor.visitTryCatchAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
                ++i2;
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.invisibleTypeAnnotations.size();
            while (i2 < n2) {
                typeAnnotation = this.invisibleTypeAnnotations.get(i2);
                typeAnnotation.accept(methodVisitor.visitTryCatchAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
                ++i2;
            }
        }
    }
}

