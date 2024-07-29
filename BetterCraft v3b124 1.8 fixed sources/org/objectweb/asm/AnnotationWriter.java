/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.Symbol;
import org.objectweb.asm.SymbolTable;
import org.objectweb.asm.Type;

final class AnnotationWriter
extends AnnotationVisitor {
    private final SymbolTable symbolTable;
    private final boolean useNamedValues;
    private final ByteVector annotation;
    private final int numElementValuePairsOffset;
    private final AnnotationWriter previousAnnotation;
    private int numElementValuePairs;
    private AnnotationWriter nextAnnotation;

    AnnotationWriter(SymbolTable symbolTable, boolean useNamedValues, ByteVector annotation, AnnotationWriter previousAnnotation) {
        super(458752);
        this.symbolTable = symbolTable;
        this.useNamedValues = useNamedValues;
        this.annotation = annotation;
        this.numElementValuePairsOffset = annotation.length == 0 ? -1 : annotation.length - 2;
        this.previousAnnotation = previousAnnotation;
        if (previousAnnotation != null) {
            previousAnnotation.nextAnnotation = this;
        }
    }

    AnnotationWriter(SymbolTable symbolTable, ByteVector annotation, AnnotationWriter previousAnnotation) {
        this(symbolTable, true, annotation, previousAnnotation);
    }

    static int computeParameterAnnotationsSize(String attributeName, AnnotationWriter[] annotationWriters, int annotableParameterCount) {
        int attributeSize = 7 + 2 * annotableParameterCount;
        int i2 = 0;
        while (i2 < annotableParameterCount) {
            AnnotationWriter annotationWriter = annotationWriters[i2];
            attributeSize += annotationWriter == null ? 0 : annotationWriter.computeAnnotationsSize(attributeName) - 8;
            ++i2;
        }
        return attributeSize;
    }

    static void putParameterAnnotations(int attributeNameIndex, AnnotationWriter[] annotationWriters, int annotableParameterCount, ByteVector output) {
        AnnotationWriter annotationWriter;
        int attributeLength = 1 + 2 * annotableParameterCount;
        int i2 = 0;
        while (i2 < annotableParameterCount) {
            annotationWriter = annotationWriters[i2];
            attributeLength += annotationWriter == null ? 0 : annotationWriter.computeAnnotationsSize(null) - 8;
            ++i2;
        }
        output.putShort(attributeNameIndex);
        output.putInt(attributeLength);
        output.putByte(annotableParameterCount);
        i2 = 0;
        while (i2 < annotableParameterCount) {
            annotationWriter = annotationWriters[i2];
            AnnotationWriter firstAnnotation = null;
            int numAnnotations = 0;
            while (annotationWriter != null) {
                annotationWriter.visitEnd();
                ++numAnnotations;
                firstAnnotation = annotationWriter;
                annotationWriter = annotationWriter.previousAnnotation;
            }
            output.putShort(numAnnotations);
            annotationWriter = firstAnnotation;
            while (annotationWriter != null) {
                output.putByteArray(annotationWriter.annotation.data, 0, annotationWriter.annotation.length);
                annotationWriter = annotationWriter.nextAnnotation;
            }
            ++i2;
        }
    }

    @Override
    public void visit(String name, Object value) {
        ++this.numElementValuePairs;
        if (this.useNamedValues) {
            this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
        }
        if (value instanceof String) {
            this.annotation.put12(115, this.symbolTable.addConstantUtf8((String)value));
        } else if (value instanceof Byte) {
            this.annotation.put12(66, this.symbolTable.addConstantInteger((int)((Byte)value).byteValue()).index);
        } else if (value instanceof Boolean) {
            int booleanValue = (Boolean)value != false ? 1 : 0;
            this.annotation.put12(90, this.symbolTable.addConstantInteger((int)booleanValue).index);
        } else if (value instanceof Character) {
            this.annotation.put12(67, this.symbolTable.addConstantInteger((int)((Character)value).charValue()).index);
        } else if (value instanceof Short) {
            this.annotation.put12(83, this.symbolTable.addConstantInteger((int)((Short)value).shortValue()).index);
        } else if (value instanceof Type) {
            this.annotation.put12(99, this.symbolTable.addConstantUtf8(((Type)value).getDescriptor()));
        } else if (value instanceof byte[]) {
            byte[] byteArray = (byte[])value;
            this.annotation.put12(91, byteArray.length);
            byte[] byArray = byteArray;
            int n2 = byteArray.length;
            int n3 = 0;
            while (n3 < n2) {
                byte byteValue = byArray[n3];
                this.annotation.put12(66, this.symbolTable.addConstantInteger((int)byteValue).index);
                ++n3;
            }
        } else if (value instanceof boolean[]) {
            boolean[] booleanArray = (boolean[])value;
            this.annotation.put12(91, booleanArray.length);
            boolean[] blArray = booleanArray;
            int n4 = booleanArray.length;
            int n5 = 0;
            while (n5 < n4) {
                boolean booleanValue;
                this.annotation.put12(90, this.symbolTable.addConstantInteger((int)((booleanValue = blArray[n5]) ? 1 : 0)).index);
                ++n5;
            }
        } else if (value instanceof short[]) {
            short[] shortArray = (short[])value;
            this.annotation.put12(91, shortArray.length);
            short[] sArray = shortArray;
            int n6 = shortArray.length;
            int n7 = 0;
            while (n7 < n6) {
                short shortValue = sArray[n7];
                this.annotation.put12(83, this.symbolTable.addConstantInteger((int)shortValue).index);
                ++n7;
            }
        } else if (value instanceof char[]) {
            char[] charArray = (char[])value;
            this.annotation.put12(91, charArray.length);
            char[] cArray = charArray;
            int n8 = charArray.length;
            int n9 = 0;
            while (n9 < n8) {
                char charValue = cArray[n9];
                this.annotation.put12(67, this.symbolTable.addConstantInteger((int)charValue).index);
                ++n9;
            }
        } else if (value instanceof int[]) {
            int[] intArray = (int[])value;
            this.annotation.put12(91, intArray.length);
            int[] nArray = intArray;
            int n10 = intArray.length;
            int n11 = 0;
            while (n11 < n10) {
                int intValue = nArray[n11];
                this.annotation.put12(73, this.symbolTable.addConstantInteger((int)intValue).index);
                ++n11;
            }
        } else if (value instanceof long[]) {
            long[] longArray = (long[])value;
            this.annotation.put12(91, longArray.length);
            long[] lArray = longArray;
            int n12 = longArray.length;
            int n13 = 0;
            while (n13 < n12) {
                long longValue = lArray[n13];
                this.annotation.put12(74, this.symbolTable.addConstantLong((long)longValue).index);
                ++n13;
            }
        } else if (value instanceof float[]) {
            float[] floatArray = (float[])value;
            this.annotation.put12(91, floatArray.length);
            float[] fArray = floatArray;
            int n14 = floatArray.length;
            int n15 = 0;
            while (n15 < n14) {
                float floatValue = fArray[n15];
                this.annotation.put12(70, this.symbolTable.addConstantFloat((float)floatValue).index);
                ++n15;
            }
        } else if (value instanceof double[]) {
            double[] doubleArray = (double[])value;
            this.annotation.put12(91, doubleArray.length);
            double[] dArray = doubleArray;
            int n16 = doubleArray.length;
            int n17 = 0;
            while (n17 < n16) {
                double doubleValue = dArray[n17];
                this.annotation.put12(68, this.symbolTable.addConstantDouble((double)doubleValue).index);
                ++n17;
            }
        } else {
            Symbol symbol = this.symbolTable.addConstant(value);
            this.annotation.put12(".s.IFJDCS".charAt(symbol.tag), symbol.index);
        }
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        ++this.numElementValuePairs;
        if (this.useNamedValues) {
            this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
        }
        this.annotation.put12(101, this.symbolTable.addConstantUtf8(descriptor)).putShort(this.symbolTable.addConstantUtf8(value));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        ++this.numElementValuePairs;
        if (this.useNamedValues) {
            this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
        }
        this.annotation.put12(64, this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
        return new AnnotationWriter(this.symbolTable, this.annotation, null);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        ++this.numElementValuePairs;
        if (this.useNamedValues) {
            this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
        }
        this.annotation.put12(91, 0);
        return new AnnotationWriter(this.symbolTable, false, this.annotation, null);
    }

    @Override
    public void visitEnd() {
        if (this.numElementValuePairsOffset != -1) {
            byte[] data = this.annotation.data;
            data[this.numElementValuePairsOffset] = (byte)(this.numElementValuePairs >>> 8);
            data[this.numElementValuePairsOffset + 1] = (byte)this.numElementValuePairs;
        }
    }

    int computeAnnotationsSize(String attributeName) {
        if (attributeName != null) {
            this.symbolTable.addConstantUtf8(attributeName);
        }
        int attributeSize = 8;
        AnnotationWriter annotationWriter = this;
        while (annotationWriter != null) {
            attributeSize += annotationWriter.annotation.length;
            annotationWriter = annotationWriter.previousAnnotation;
        }
        return attributeSize;
    }

    void putAnnotations(int attributeNameIndex, ByteVector output) {
        int attributeLength = 2;
        int numAnnotations = 0;
        AnnotationWriter annotationWriter = this;
        AnnotationWriter firstAnnotation = null;
        while (annotationWriter != null) {
            annotationWriter.visitEnd();
            attributeLength += annotationWriter.annotation.length;
            ++numAnnotations;
            firstAnnotation = annotationWriter;
            annotationWriter = annotationWriter.previousAnnotation;
        }
        output.putShort(attributeNameIndex);
        output.putInt(attributeLength);
        output.putShort(numAnnotations);
        annotationWriter = firstAnnotation;
        while (annotationWriter != null) {
            output.putByteArray(annotationWriter.annotation.data, 0, annotationWriter.annotation.length);
            annotationWriter = annotationWriter.nextAnnotation;
        }
    }
}

