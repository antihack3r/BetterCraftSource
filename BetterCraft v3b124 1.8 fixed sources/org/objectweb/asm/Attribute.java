/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.SymbolTable;

public class Attribute {
    public final String type;
    Attribute nextAttribute;
    private byte[] content;

    protected Attribute(String type) {
        this.type = type;
    }

    public boolean isUnknown() {
        return true;
    }

    public boolean isCodeAttribute() {
        return false;
    }

    protected Label[] getLabels() {
        return new Label[0];
    }

    protected Attribute read(ClassReader classReader, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
        Attribute attribute = new Attribute(this.type);
        attribute.content = new byte[length];
        System.arraycopy(classReader.b, offset, attribute.content, 0, length);
        return attribute;
    }

    protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
        return new ByteVector(this.content);
    }

    final int getAttributeCount() {
        int count = 0;
        Attribute attribute = this;
        while (attribute != null) {
            ++count;
            attribute = attribute.nextAttribute;
        }
        return count;
    }

    final int computeAttributesSize(SymbolTable symbolTable) {
        byte[] code = null;
        boolean codeLength = false;
        int maxStack = -1;
        int maxLocals = -1;
        return this.computeAttributesSize(symbolTable, code, 0, -1, -1);
    }

    final int computeAttributesSize(SymbolTable symbolTable, byte[] code, int codeLength, int maxStack, int maxLocals) {
        ClassWriter classWriter = symbolTable.classWriter;
        int size = 0;
        Attribute attribute = this;
        while (attribute != null) {
            symbolTable.addConstantUtf8(attribute.type);
            size += 6 + attribute.write((ClassWriter)classWriter, (byte[])code, (int)codeLength, (int)maxStack, (int)maxLocals).length;
            attribute = attribute.nextAttribute;
        }
        return size;
    }

    final void putAttributes(SymbolTable symbolTable, ByteVector output) {
        byte[] code = null;
        boolean codeLength = false;
        int maxStack = -1;
        int maxLocals = -1;
        this.putAttributes(symbolTable, code, 0, -1, -1, output);
    }

    final void putAttributes(SymbolTable symbolTable, byte[] code, int codeLength, int maxStack, int maxLocals, ByteVector output) {
        ClassWriter classWriter = symbolTable.classWriter;
        Attribute attribute = this;
        while (attribute != null) {
            ByteVector attributeContent = attribute.write(classWriter, code, codeLength, maxStack, maxLocals);
            output.putShort(symbolTable.addConstantUtf8(attribute.type)).putInt(attributeContent.length);
            output.putByteArray(attributeContent.data, 0, attributeContent.length);
            attribute = attribute.nextAttribute;
        }
    }

    static final class Set {
        private static final int SIZE_INCREMENT = 6;
        private int size;
        private Attribute[] data = new Attribute[6];

        Set() {
        }

        void addAttributes(Attribute attributeList) {
            Attribute attribute = attributeList;
            while (attribute != null) {
                if (!this.contains(attribute)) {
                    this.add(attribute);
                }
                attribute = attribute.nextAttribute;
            }
        }

        Attribute[] toArray() {
            Attribute[] result = new Attribute[this.size];
            System.arraycopy(this.data, 0, result, 0, this.size);
            return result;
        }

        private boolean contains(Attribute attribute) {
            int i2 = 0;
            while (i2 < this.size) {
                if (this.data[i2].type.equals(attribute.type)) {
                    return true;
                }
                ++i2;
            }
            return false;
        }

        private void add(Attribute attribute) {
            if (this.size >= this.data.length) {
                Attribute[] newData = new Attribute[this.data.length + 6];
                System.arraycopy(this.data, 0, newData, 0, this.size);
                this.data = newData;
            }
            this.data[this.size++] = attribute;
        }
    }
}

