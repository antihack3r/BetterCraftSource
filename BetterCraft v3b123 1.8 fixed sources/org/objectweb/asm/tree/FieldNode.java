// 
// Decompiled by Procyon v0.6.0
// 

package org.objectweb.asm.tree;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.TypePath;
import java.util.ArrayList;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import java.util.List;
import org.objectweb.asm.FieldVisitor;

public class FieldNode extends FieldVisitor
{
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
    
    public FieldNode(final int access, final String name, final String descriptor, final String signature, final Object value) {
        this(458752, access, name, descriptor, signature, value);
        if (this.getClass() != FieldNode.class) {
            throw new IllegalStateException();
        }
    }
    
    public FieldNode(final int api, final int access, final String name, final String descriptor, final String signature, final Object value) {
        super(api);
        this.access = access;
        this.name = name;
        this.desc = descriptor;
        this.signature = signature;
        this.value = value;
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        final AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.visibleAnnotations.add(annotation);
        }
        else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.invisibleAnnotations.add(annotation);
        }
        return annotation;
    }
    
    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        final TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            if (this.visibleTypeAnnotations == null) {
                this.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.visibleTypeAnnotations.add(typeAnnotation);
        }
        else {
            if (this.invisibleTypeAnnotations == null) {
                this.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.invisibleTypeAnnotations.add(typeAnnotation);
        }
        return typeAnnotation;
    }
    
    @Override
    public void visitAttribute(final Attribute attribute) {
        if (this.attrs == null) {
            this.attrs = new ArrayList<Attribute>(1);
        }
        this.attrs.add(attribute);
    }
    
    @Override
    public void visitEnd() {
    }
    
    public void check(final int api) {
        if (api == 262144) {
            if (this.visibleTypeAnnotations != null && !this.visibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.invisibleTypeAnnotations != null && !this.invisibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
        }
    }
    
    public void accept(final ClassVisitor classVisitor) {
        final FieldVisitor fieldVisitor = classVisitor.visitField(this.access, this.name, this.desc, this.signature, this.value);
        if (fieldVisitor == null) {
            return;
        }
        if (this.visibleAnnotations != null) {
            for (int i = 0, n = this.visibleAnnotations.size(); i < n; ++i) {
                final AnnotationNode annotation = this.visibleAnnotations.get(i);
                annotation.accept(fieldVisitor.visitAnnotation(annotation.desc, true));
            }
        }
        if (this.invisibleAnnotations != null) {
            for (int i = 0, n = this.invisibleAnnotations.size(); i < n; ++i) {
                final AnnotationNode annotation = this.invisibleAnnotations.get(i);
                annotation.accept(fieldVisitor.visitAnnotation(annotation.desc, false));
            }
        }
        if (this.visibleTypeAnnotations != null) {
            for (int i = 0, n = this.visibleTypeAnnotations.size(); i < n; ++i) {
                final TypeAnnotationNode typeAnnotation = this.visibleTypeAnnotations.get(i);
                typeAnnotation.accept(fieldVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            for (int i = 0, n = this.invisibleTypeAnnotations.size(); i < n; ++i) {
                final TypeAnnotationNode typeAnnotation = this.invisibleTypeAnnotations.get(i);
                typeAnnotation.accept(fieldVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
            }
        }
        if (this.attrs != null) {
            for (int i = 0, n = this.attrs.size(); i < n; ++i) {
                fieldVisitor.visitAttribute(this.attrs.get(i));
            }
        }
        fieldVisitor.visitEnd();
    }
}
