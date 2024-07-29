/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.UnsupportedClassVersionException;

public class FieldNode
extends FieldVisitor {
    public int access;
    public String name;
    public String desc;
    public String signature;
    public Object value;
    public List<AnnotationNode> visibleAnnotations;
    public List<AnnotationNode> invisibleAnnotations;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;
    public List<Attribute> attrs;

    public FieldNode(int access, String name, String descriptor, String signature, Object value) {
        this(458752, access, name, descriptor, signature, value);
        if (this.getClass() != FieldNode.class) {
            throw new IllegalStateException();
        }
    }

    public FieldNode(int api2, int access, String name, String descriptor, String signature, Object value) {
        super(api2);
        this.access = access;
        this.name = name;
        this.desc = descriptor;
        this.signature = signature;
        this.value = value;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.visibleAnnotations.add(annotation);
        } else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.invisibleAnnotations.add(annotation);
        }
        return annotation;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            if (this.visibleTypeAnnotations == null) {
                this.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.visibleTypeAnnotations.add(typeAnnotation);
        } else {
            if (this.invisibleTypeAnnotations == null) {
                this.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.invisibleTypeAnnotations.add(typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        if (this.attrs == null) {
            this.attrs = new ArrayList<Attribute>(1);
        }
        this.attrs.add(attribute);
    }

    @Override
    public void visitEnd() {
    }

    public void check(int api2) {
        if (api2 == 262144) {
            if (this.visibleTypeAnnotations != null && !this.visibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.invisibleTypeAnnotations != null && !this.invisibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
        }
    }

    public void accept(ClassVisitor classVisitor) {
        TypeAnnotationNode typeAnnotation;
        AnnotationNode annotation;
        int n2;
        int i2;
        FieldVisitor fieldVisitor = classVisitor.visitField(this.access, this.name, this.desc, this.signature, this.value);
        if (fieldVisitor == null) {
            return;
        }
        if (this.visibleAnnotations != null) {
            i2 = 0;
            n2 = this.visibleAnnotations.size();
            while (i2 < n2) {
                annotation = this.visibleAnnotations.get(i2);
                annotation.accept(fieldVisitor.visitAnnotation(annotation.desc, true));
                ++i2;
            }
        }
        if (this.invisibleAnnotations != null) {
            i2 = 0;
            n2 = this.invisibleAnnotations.size();
            while (i2 < n2) {
                annotation = this.invisibleAnnotations.get(i2);
                annotation.accept(fieldVisitor.visitAnnotation(annotation.desc, false));
                ++i2;
            }
        }
        if (this.visibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.visibleTypeAnnotations.size();
            while (i2 < n2) {
                typeAnnotation = this.visibleTypeAnnotations.get(i2);
                typeAnnotation.accept(fieldVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
                ++i2;
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.invisibleTypeAnnotations.size();
            while (i2 < n2) {
                typeAnnotation = this.invisibleTypeAnnotations.get(i2);
                typeAnnotation.accept(fieldVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
                ++i2;
            }
        }
        if (this.attrs != null) {
            i2 = 0;
            n2 = this.attrs.size();
            while (i2 < n2) {
                fieldVisitor.visitAttribute(this.attrs.get(i2));
                ++i2;
            }
        }
        fieldVisitor.visitEnd();
    }
}

