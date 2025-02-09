/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.AnnotationWriter;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.SymbolTable;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

final class FieldWriter
extends FieldVisitor {
    private final SymbolTable symbolTable;
    private final int accessFlags;
    private final int nameIndex;
    private final int descriptorIndex;
    private int signatureIndex;
    private int constantValueIndex;
    private AnnotationWriter lastRuntimeVisibleAnnotation;
    private AnnotationWriter lastRuntimeInvisibleAnnotation;
    private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
    private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
    private Attribute firstAttribute;

    FieldWriter(SymbolTable symbolTable, int access, String name, String descriptor, String signature, Object constantValue) {
        super(458752);
        this.symbolTable = symbolTable;
        this.accessFlags = access;
        this.nameIndex = symbolTable.addConstantUtf8(name);
        this.descriptorIndex = symbolTable.addConstantUtf8(descriptor);
        if (signature != null) {
            this.signatureIndex = symbolTable.addConstantUtf8(signature);
        }
        if (constantValue != null) {
            this.constantValueIndex = symbolTable.addConstant((Object)constantValue).index;
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        ByteVector annotation = new ByteVector();
        annotation.putShort(this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
        if (visible) {
            this.lastRuntimeVisibleAnnotation = new AnnotationWriter(this.symbolTable, annotation, this.lastRuntimeVisibleAnnotation);
            return this.lastRuntimeVisibleAnnotation;
        }
        this.lastRuntimeInvisibleAnnotation = new AnnotationWriter(this.symbolTable, annotation, this.lastRuntimeInvisibleAnnotation);
        return this.lastRuntimeInvisibleAnnotation;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        ByteVector typeAnnotation = new ByteVector();
        TypeReference.putTarget(typeRef, typeAnnotation);
        TypePath.put(typePath, typeAnnotation);
        typeAnnotation.putShort(this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
        if (visible) {
            this.lastRuntimeVisibleTypeAnnotation = new AnnotationWriter(this.symbolTable, typeAnnotation, this.lastRuntimeVisibleTypeAnnotation);
            return this.lastRuntimeVisibleTypeAnnotation;
        }
        this.lastRuntimeInvisibleTypeAnnotation = new AnnotationWriter(this.symbolTable, typeAnnotation, this.lastRuntimeInvisibleTypeAnnotation);
        return this.lastRuntimeInvisibleTypeAnnotation;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        attribute.nextAttribute = this.firstAttribute;
        this.firstAttribute = attribute;
    }

    @Override
    public void visitEnd() {
    }

    int computeFieldInfoSize() {
        int size = 8;
        if (this.constantValueIndex != 0) {
            this.symbolTable.addConstantUtf8("ConstantValue");
            size += 8;
        }
        if ((this.accessFlags & 0x1000) != 0 && this.symbolTable.getMajorVersion() < 49) {
            this.symbolTable.addConstantUtf8("Synthetic");
            size += 6;
        }
        if (this.signatureIndex != 0) {
            this.symbolTable.addConstantUtf8("Signature");
            size += 8;
        }
        if ((this.accessFlags & 0x20000) != 0) {
            this.symbolTable.addConstantUtf8("Deprecated");
            size += 6;
        }
        if (this.lastRuntimeVisibleAnnotation != null) {
            size += this.lastRuntimeVisibleAnnotation.computeAnnotationsSize("RuntimeVisibleAnnotations");
        }
        if (this.lastRuntimeInvisibleAnnotation != null) {
            size += this.lastRuntimeInvisibleAnnotation.computeAnnotationsSize("RuntimeInvisibleAnnotations");
        }
        if (this.lastRuntimeVisibleTypeAnnotation != null) {
            size += this.lastRuntimeVisibleTypeAnnotation.computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
        }
        if (this.lastRuntimeInvisibleTypeAnnotation != null) {
            size += this.lastRuntimeInvisibleTypeAnnotation.computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
        }
        if (this.firstAttribute != null) {
            size += this.firstAttribute.computeAttributesSize(this.symbolTable);
        }
        return size;
    }

    void putFieldInfo(ByteVector output) {
        boolean useSyntheticAttribute = this.symbolTable.getMajorVersion() < 49;
        int mask = useSyntheticAttribute ? 4096 : 0;
        output.putShort(this.accessFlags & ~mask).putShort(this.nameIndex).putShort(this.descriptorIndex);
        int attributesCount = 0;
        if (this.constantValueIndex != 0) {
            ++attributesCount;
        }
        if ((this.accessFlags & 0x1000) != 0 && useSyntheticAttribute) {
            ++attributesCount;
        }
        if (this.signatureIndex != 0) {
            ++attributesCount;
        }
        if ((this.accessFlags & 0x20000) != 0) {
            ++attributesCount;
        }
        if (this.lastRuntimeVisibleAnnotation != null) {
            ++attributesCount;
        }
        if (this.lastRuntimeInvisibleAnnotation != null) {
            ++attributesCount;
        }
        if (this.lastRuntimeVisibleTypeAnnotation != null) {
            ++attributesCount;
        }
        if (this.lastRuntimeInvisibleTypeAnnotation != null) {
            ++attributesCount;
        }
        if (this.firstAttribute != null) {
            attributesCount += this.firstAttribute.getAttributeCount();
        }
        output.putShort(attributesCount);
        if (this.constantValueIndex != 0) {
            output.putShort(this.symbolTable.addConstantUtf8("ConstantValue")).putInt(2).putShort(this.constantValueIndex);
        }
        if ((this.accessFlags & 0x1000) != 0 && useSyntheticAttribute) {
            output.putShort(this.symbolTable.addConstantUtf8("Synthetic")).putInt(0);
        }
        if (this.signatureIndex != 0) {
            output.putShort(this.symbolTable.addConstantUtf8("Signature")).putInt(2).putShort(this.signatureIndex);
        }
        if ((this.accessFlags & 0x20000) != 0) {
            output.putShort(this.symbolTable.addConstantUtf8("Deprecated")).putInt(0);
        }
        if (this.lastRuntimeVisibleAnnotation != null) {
            this.lastRuntimeVisibleAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeVisibleAnnotations"), output);
        }
        if (this.lastRuntimeInvisibleAnnotation != null) {
            this.lastRuntimeInvisibleAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeInvisibleAnnotations"), output);
        }
        if (this.lastRuntimeVisibleTypeAnnotation != null) {
            this.lastRuntimeVisibleTypeAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeVisibleTypeAnnotations"), output);
        }
        if (this.lastRuntimeInvisibleTypeAnnotation != null) {
            this.lastRuntimeInvisibleTypeAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeInvisibleTypeAnnotations"), output);
        }
        if (this.firstAttribute != null) {
            this.firstAttribute.putAttributes(this.symbolTable, output);
        }
    }

    final void collectAttributePrototypes(Attribute.Set attributePrototypes) {
        attributePrototypes.addAttributes(this.firstAttribute);
    }
}

