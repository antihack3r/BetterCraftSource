/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ModuleNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.UnsupportedClassVersionException;

public class ClassNode
extends ClassVisitor {
    public int version;
    public int access;
    public String name;
    public String signature;
    public String superName;
    public List<String> interfaces = Collections.synchronizedList(new CopyOnWriteArrayList());
    public String sourceFile;
    public String sourceDebug;
    public ModuleNode module;
    public String outerClass;
    public String outerMethod;
    public String outerMethodDesc;
    public List<AnnotationNode> visibleAnnotations;
    public List<AnnotationNode> invisibleAnnotations;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;
    public List<Attribute> attrs;
    public List<InnerClassNode> innerClasses = Collections.synchronizedList(new CopyOnWriteArrayList());
    public String nestHostClass;
    public List<String> nestMembers;
    public List<FieldNode> fields = Collections.synchronizedList(new CopyOnWriteArrayList());
    public List<MethodNode> methods = Collections.synchronizedList(new CopyOnWriteArrayList());

    public ClassNode() {
        this(458752);
        if (this.getClass() != ClassNode.class) {
            throw new IllegalStateException();
        }
    }

    public ClassNode(int api2) {
        super(api2);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = Collections.synchronizedList(Lists.newCopyOnWriteArrayList(Arrays.asList(interfaces)));
    }

    @Override
    public void visitSource(String file, String debug) {
        this.sourceFile = file;
        this.sourceDebug = debug;
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        this.module = new ModuleNode(name, access, version);
        return this.module;
    }

    @Override
    public void visitNestHost(String nestHost) {
        this.nestHostClass = nestHost;
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        this.outerClass = owner;
        this.outerMethod = name;
        this.outerMethodDesc = descriptor;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = Collections.synchronizedList(new ArrayList(1));
            }
            this.visibleAnnotations.add(annotation);
        } else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = Collections.synchronizedList(new ArrayList(1));
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
                this.visibleTypeAnnotations = Collections.synchronizedList(new ArrayList(1));
            }
            this.visibleTypeAnnotations.add(typeAnnotation);
        } else {
            if (this.invisibleTypeAnnotations == null) {
                this.invisibleTypeAnnotations = Collections.synchronizedList(new ArrayList(1));
            }
            this.invisibleTypeAnnotations.add(typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        if (this.attrs == null) {
            this.attrs = Collections.synchronizedList(new ArrayList(1));
        }
        this.attrs.add(attribute);
    }

    @Override
    public void visitNestMember(String nestMember) {
        if (this.nestMembers == null) {
            this.nestMembers = Collections.synchronizedList(new ArrayList());
        }
        this.nestMembers.add(nestMember);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        InnerClassNode innerClass = new InnerClassNode(name, outerName, innerName, access);
        this.innerClasses.add(innerClass);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        FieldNode field = new FieldNode(access, name, descriptor, signature, value);
        this.fields.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodNode method = new MethodNode(access, name, descriptor, signature, exceptions);
        this.methods.add(method);
        return method;
    }

    @Override
    public void visitEnd() {
    }

    public void check(int api2) {
        int i2;
        if (api2 < 458752 && (this.nestHostClass != null || this.nestMembers != null)) {
            throw new UnsupportedClassVersionException();
        }
        if (api2 < 393216 && this.module != null) {
            throw new UnsupportedClassVersionException();
        }
        if (api2 < 327680) {
            if (this.visibleTypeAnnotations != null && !this.visibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.invisibleTypeAnnotations != null && !this.invisibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
        }
        if (this.visibleAnnotations != null) {
            i2 = this.visibleAnnotations.size() - 1;
            while (i2 >= 0) {
                this.visibleAnnotations.get(i2).check(api2);
                --i2;
            }
        }
        if (this.invisibleAnnotations != null) {
            i2 = this.invisibleAnnotations.size() - 1;
            while (i2 >= 0) {
                this.invisibleAnnotations.get(i2).check(api2);
                --i2;
            }
        }
        if (this.visibleTypeAnnotations != null) {
            i2 = this.visibleTypeAnnotations.size() - 1;
            while (i2 >= 0) {
                this.visibleTypeAnnotations.get(i2).check(api2);
                --i2;
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            i2 = this.invisibleTypeAnnotations.size() - 1;
            while (i2 >= 0) {
                this.invisibleTypeAnnotations.get(i2).check(api2);
                --i2;
            }
        }
        i2 = this.fields.size() - 1;
        while (i2 >= 0) {
            this.fields.get(i2).check(api2);
            --i2;
        }
        i2 = this.methods.size() - 1;
        while (i2 >= 0) {
            this.methods.get(i2).check(api2);
            --i2;
        }
    }

    public void accept(ClassVisitor classVisitor) {
        TypeAnnotationNode typeAnnotation;
        AnnotationNode annotation;
        int n2;
        int i2;
        String[] interfacesArray = new String[this.interfaces.size()];
        this.interfaces.toArray(interfacesArray);
        classVisitor.visit(this.version, this.access, this.name, this.signature, this.superName, interfacesArray);
        if (this.sourceFile != null || this.sourceDebug != null) {
            classVisitor.visitSource(this.sourceFile, this.sourceDebug);
        }
        if (this.module != null) {
            this.module.accept(classVisitor);
        }
        if (this.nestHostClass != null) {
            classVisitor.visitNestHost(this.nestHostClass);
        }
        if (this.outerClass != null) {
            classVisitor.visitOuterClass(this.outerClass, this.outerMethod, this.outerMethodDesc);
        }
        if (this.visibleAnnotations != null) {
            i2 = 0;
            n2 = this.visibleAnnotations.size();
            while (i2 < n2) {
                annotation = this.visibleAnnotations.get(i2);
                annotation.accept(classVisitor.visitAnnotation(annotation.desc, true));
                ++i2;
            }
        }
        if (this.invisibleAnnotations != null) {
            i2 = 0;
            n2 = this.invisibleAnnotations.size();
            while (i2 < n2) {
                annotation = this.invisibleAnnotations.get(i2);
                annotation.accept(classVisitor.visitAnnotation(annotation.desc, false));
                ++i2;
            }
        }
        if (this.visibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.visibleTypeAnnotations.size();
            while (i2 < n2) {
                typeAnnotation = this.visibleTypeAnnotations.get(i2);
                typeAnnotation.accept(classVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
                ++i2;
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            i2 = 0;
            n2 = this.invisibleTypeAnnotations.size();
            while (i2 < n2) {
                typeAnnotation = this.invisibleTypeAnnotations.get(i2);
                typeAnnotation.accept(classVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
                ++i2;
            }
        }
        if (this.attrs != null) {
            i2 = 0;
            n2 = this.attrs.size();
            while (i2 < n2) {
                classVisitor.visitAttribute(this.attrs.get(i2));
                ++i2;
            }
        }
        if (this.nestMembers != null) {
            i2 = 0;
            n2 = this.nestMembers.size();
            while (i2 < n2) {
                classVisitor.visitNestMember(this.nestMembers.get(i2));
                ++i2;
            }
        }
        i2 = 0;
        n2 = this.innerClasses.size();
        while (i2 < n2) {
            this.innerClasses.get(i2).accept(classVisitor);
            ++i2;
        }
        i2 = 0;
        n2 = this.fields.size();
        while (i2 < n2) {
            this.fields.get(i2).accept(classVisitor);
            ++i2;
        }
        i2 = 0;
        n2 = this.methods.size();
        while (i2 < n2) {
            this.methods.get(i2).accept(classVisitor);
            ++i2;
        }
        classVisitor.visitEnd();
    }
}

