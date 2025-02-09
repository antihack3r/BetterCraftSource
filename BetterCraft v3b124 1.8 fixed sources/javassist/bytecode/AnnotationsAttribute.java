/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ByteArray;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class AnnotationsAttribute
extends AttributeInfo {
    public static final String visibleTag = "RuntimeVisibleAnnotations";
    public static final String invisibleTag = "RuntimeInvisibleAnnotations";

    public AnnotationsAttribute(ConstPool cp2, String attrname, byte[] info) {
        super(cp2, attrname, info);
    }

    public AnnotationsAttribute(ConstPool cp2, String attrname) {
        this(cp2, attrname, new byte[]{0, 0});
    }

    AnnotationsAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    public int numAnnotations() {
        return ByteArray.readU16bit(this.info, 0);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        Copier copier = new Copier(this.info, this.constPool, newCp, classnames);
        try {
            copier.annotationArray();
            return new AnnotationsAttribute(newCp, this.getName(), copier.close());
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    public Annotation getAnnotation(String type) {
        Annotation[] annotations = this.getAnnotations();
        for (int i2 = 0; i2 < annotations.length; ++i2) {
            if (!annotations[i2].getTypeName().equals(type)) continue;
            return annotations[i2];
        }
        return null;
    }

    public void addAnnotation(Annotation annotation) {
        String type = annotation.getTypeName();
        Annotation[] annotations = this.getAnnotations();
        for (int i2 = 0; i2 < annotations.length; ++i2) {
            if (!annotations[i2].getTypeName().equals(type)) continue;
            annotations[i2] = annotation;
            this.setAnnotations(annotations);
            return;
        }
        Annotation[] newlist = new Annotation[annotations.length + 1];
        System.arraycopy(annotations, 0, newlist, 0, annotations.length);
        newlist[annotations.length] = annotation;
        this.setAnnotations(newlist);
    }

    public boolean removeAnnotation(String type) {
        Annotation[] annotations = this.getAnnotations();
        for (int i2 = 0; i2 < annotations.length; ++i2) {
            if (!annotations[i2].getTypeName().equals(type)) continue;
            Annotation[] newlist = new Annotation[annotations.length - 1];
            System.arraycopy(annotations, 0, newlist, 0, i2);
            if (i2 < annotations.length - 1) {
                System.arraycopy(annotations, i2 + 1, newlist, i2, annotations.length - i2 - 1);
            }
            this.setAnnotations(newlist);
            return true;
        }
        return false;
    }

    public Annotation[] getAnnotations() {
        try {
            return new Parser(this.info, this.constPool).parseAnnotations();
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    public void setAnnotations(Annotation[] annotations) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);
        try {
            int n2 = annotations.length;
            writer.numAnnotations(n2);
            for (int i2 = 0; i2 < n2; ++i2) {
                annotations[i2].write(writer);
            }
            writer.close();
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
        this.set(output.toByteArray());
    }

    public void setAnnotation(Annotation annotation) {
        this.setAnnotations(new Annotation[]{annotation});
    }

    @Override
    void renameClass(String oldname, String newname) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(oldname, newname);
        this.renameClass(map);
    }

    @Override
    void renameClass(Map<String, String> classnames) {
        Renamer renamer = new Renamer(this.info, this.getConstPool(), classnames);
        try {
            renamer.annotationArray();
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    @Override
    void getRefClasses(Map<String, String> classnames) {
        this.renameClass(classnames);
    }

    public String toString() {
        Annotation[] a2 = this.getAnnotations();
        StringBuilder sbuf = new StringBuilder();
        int i2 = 0;
        while (i2 < a2.length) {
            sbuf.append(a2[i2++].toString());
            if (i2 == a2.length) continue;
            sbuf.append(", ");
        }
        return sbuf.toString();
    }

    static class Parser
    extends Walker {
        ConstPool pool;
        Annotation[][] allParams;
        Annotation[] allAnno;
        Annotation currentAnno;
        MemberValue currentMember;

        Parser(byte[] info, ConstPool cp2) {
            super(info);
            this.pool = cp2;
        }

        Annotation[][] parseParameters() throws Exception {
            this.parameters();
            return this.allParams;
        }

        Annotation[] parseAnnotations() throws Exception {
            this.annotationArray();
            return this.allAnno;
        }

        MemberValue parseMemberValue() throws Exception {
            this.memberValue(0);
            return this.currentMember;
        }

        @Override
        void parameters(int numParam, int pos) throws Exception {
            Annotation[][] params = new Annotation[numParam][];
            for (int i2 = 0; i2 < numParam; ++i2) {
                pos = this.annotationArray(pos);
                params[i2] = this.allAnno;
            }
            this.allParams = params;
        }

        @Override
        int annotationArray(int pos, int num) throws Exception {
            Annotation[] array = new Annotation[num];
            for (int i2 = 0; i2 < num; ++i2) {
                pos = this.annotation(pos);
                array[i2] = this.currentAnno;
            }
            this.allAnno = array;
            return pos;
        }

        @Override
        int annotation(int pos, int type, int numPairs) throws Exception {
            this.currentAnno = new Annotation(type, this.pool);
            return super.annotation(pos, type, numPairs);
        }

        @Override
        int memberValuePair(int pos, int nameIndex) throws Exception {
            pos = super.memberValuePair(pos, nameIndex);
            this.currentAnno.addMemberValue(nameIndex, this.currentMember);
            return pos;
        }

        @Override
        void constValueMember(int tag, int index) throws Exception {
            MemberValue m2;
            ConstPool cp2 = this.pool;
            switch (tag) {
                case 66: {
                    m2 = new ByteMemberValue(index, cp2);
                    break;
                }
                case 67: {
                    m2 = new CharMemberValue(index, cp2);
                    break;
                }
                case 68: {
                    m2 = new DoubleMemberValue(index, cp2);
                    break;
                }
                case 70: {
                    m2 = new FloatMemberValue(index, cp2);
                    break;
                }
                case 73: {
                    m2 = new IntegerMemberValue(index, cp2);
                    break;
                }
                case 74: {
                    m2 = new LongMemberValue(index, cp2);
                    break;
                }
                case 83: {
                    m2 = new ShortMemberValue(index, cp2);
                    break;
                }
                case 90: {
                    m2 = new BooleanMemberValue(index, cp2);
                    break;
                }
                case 115: {
                    m2 = new StringMemberValue(index, cp2);
                    break;
                }
                default: {
                    throw new RuntimeException("unknown tag:" + tag);
                }
            }
            this.currentMember = m2;
            super.constValueMember(tag, index);
        }

        @Override
        void enumMemberValue(int pos, int typeNameIndex, int constNameIndex) throws Exception {
            this.currentMember = new EnumMemberValue(typeNameIndex, constNameIndex, this.pool);
            super.enumMemberValue(pos, typeNameIndex, constNameIndex);
        }

        @Override
        void classMemberValue(int pos, int index) throws Exception {
            this.currentMember = new ClassMemberValue(index, this.pool);
            super.classMemberValue(pos, index);
        }

        @Override
        int annotationMemberValue(int pos) throws Exception {
            Annotation anno = this.currentAnno;
            pos = super.annotationMemberValue(pos);
            this.currentMember = new AnnotationMemberValue(this.currentAnno, this.pool);
            this.currentAnno = anno;
            return pos;
        }

        @Override
        int arrayMemberValue(int pos, int num) throws Exception {
            ArrayMemberValue amv2 = new ArrayMemberValue(this.pool);
            MemberValue[] elements = new MemberValue[num];
            for (int i2 = 0; i2 < num; ++i2) {
                pos = this.memberValue(pos);
                elements[i2] = this.currentMember;
            }
            amv2.setValue(elements);
            this.currentMember = amv2;
            return pos;
        }
    }

    static class Copier
    extends Walker {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AnnotationsWriter writer;
        ConstPool srcPool;
        ConstPool destPool;
        Map<String, String> classnames;

        Copier(byte[] info, ConstPool src, ConstPool dest, Map<String, String> map) {
            this(info, src, dest, map, true);
        }

        Copier(byte[] info, ConstPool src, ConstPool dest, Map<String, String> map, boolean makeWriter) {
            super(info);
            if (makeWriter) {
                this.writer = new AnnotationsWriter(this.output, dest);
            }
            this.srcPool = src;
            this.destPool = dest;
            this.classnames = map;
        }

        byte[] close() throws IOException {
            this.writer.close();
            return this.output.toByteArray();
        }

        @Override
        void parameters(int numParam, int pos) throws Exception {
            this.writer.numParameters(numParam);
            super.parameters(numParam, pos);
        }

        @Override
        int annotationArray(int pos, int num) throws Exception {
            this.writer.numAnnotations(num);
            return super.annotationArray(pos, num);
        }

        @Override
        int annotation(int pos, int type, int numPairs) throws Exception {
            this.writer.annotation(this.copyType(type), numPairs);
            return super.annotation(pos, type, numPairs);
        }

        @Override
        int memberValuePair(int pos, int nameIndex) throws Exception {
            this.writer.memberValuePair(this.copy(nameIndex));
            return super.memberValuePair(pos, nameIndex);
        }

        @Override
        void constValueMember(int tag, int index) throws Exception {
            this.writer.constValueIndex(tag, this.copy(index));
            super.constValueMember(tag, index);
        }

        @Override
        void enumMemberValue(int pos, int typeNameIndex, int constNameIndex) throws Exception {
            this.writer.enumConstValue(this.copyType(typeNameIndex), this.copy(constNameIndex));
            super.enumMemberValue(pos, typeNameIndex, constNameIndex);
        }

        @Override
        void classMemberValue(int pos, int index) throws Exception {
            this.writer.classInfoIndex(this.copyType(index));
            super.classMemberValue(pos, index);
        }

        @Override
        int annotationMemberValue(int pos) throws Exception {
            this.writer.annotationValue();
            return super.annotationMemberValue(pos);
        }

        @Override
        int arrayMemberValue(int pos, int num) throws Exception {
            this.writer.arrayValue(num);
            return super.arrayMemberValue(pos, num);
        }

        int copy(int srcIndex) {
            return this.srcPool.copy(srcIndex, this.destPool, this.classnames);
        }

        int copyType(int srcIndex) {
            String name = this.srcPool.getUtf8Info(srcIndex);
            String newName = Descriptor.rename(name, this.classnames);
            return this.destPool.addUtf8Info(newName);
        }
    }

    static class Renamer
    extends Walker {
        ConstPool cpool;
        Map<String, String> classnames;

        Renamer(byte[] info, ConstPool cp2, Map<String, String> map) {
            super(info);
            this.cpool = cp2;
            this.classnames = map;
        }

        @Override
        int annotation(int pos, int type, int numPairs) throws Exception {
            this.renameType(pos - 4, type);
            return super.annotation(pos, type, numPairs);
        }

        @Override
        void enumMemberValue(int pos, int typeNameIndex, int constNameIndex) throws Exception {
            this.renameType(pos + 1, typeNameIndex);
            super.enumMemberValue(pos, typeNameIndex, constNameIndex);
        }

        @Override
        void classMemberValue(int pos, int index) throws Exception {
            this.renameType(pos + 1, index);
            super.classMemberValue(pos, index);
        }

        private void renameType(int pos, int index) {
            String newName;
            String name = this.cpool.getUtf8Info(index);
            if (!name.equals(newName = Descriptor.rename(name, this.classnames))) {
                int index2 = this.cpool.addUtf8Info(newName);
                ByteArray.write16bit(index2, this.info, pos);
            }
        }
    }

    static class Walker {
        byte[] info;

        Walker(byte[] attrInfo) {
            this.info = attrInfo;
        }

        final void parameters() throws Exception {
            int numParam = this.info[0] & 0xFF;
            this.parameters(numParam, 1);
        }

        void parameters(int numParam, int pos) throws Exception {
            for (int i2 = 0; i2 < numParam; ++i2) {
                pos = this.annotationArray(pos);
            }
        }

        final void annotationArray() throws Exception {
            this.annotationArray(0);
        }

        final int annotationArray(int pos) throws Exception {
            int num = ByteArray.readU16bit(this.info, pos);
            return this.annotationArray(pos + 2, num);
        }

        int annotationArray(int pos, int num) throws Exception {
            for (int i2 = 0; i2 < num; ++i2) {
                pos = this.annotation(pos);
            }
            return pos;
        }

        final int annotation(int pos) throws Exception {
            int type = ByteArray.readU16bit(this.info, pos);
            int numPairs = ByteArray.readU16bit(this.info, pos + 2);
            return this.annotation(pos + 4, type, numPairs);
        }

        int annotation(int pos, int type, int numPairs) throws Exception {
            for (int j2 = 0; j2 < numPairs; ++j2) {
                pos = this.memberValuePair(pos);
            }
            return pos;
        }

        final int memberValuePair(int pos) throws Exception {
            int nameIndex = ByteArray.readU16bit(this.info, pos);
            return this.memberValuePair(pos + 2, nameIndex);
        }

        int memberValuePair(int pos, int nameIndex) throws Exception {
            return this.memberValue(pos);
        }

        final int memberValue(int pos) throws Exception {
            int tag = this.info[pos] & 0xFF;
            if (tag == 101) {
                int typeNameIndex = ByteArray.readU16bit(this.info, pos + 1);
                int constNameIndex = ByteArray.readU16bit(this.info, pos + 3);
                this.enumMemberValue(pos, typeNameIndex, constNameIndex);
                return pos + 5;
            }
            if (tag == 99) {
                int index = ByteArray.readU16bit(this.info, pos + 1);
                this.classMemberValue(pos, index);
                return pos + 3;
            }
            if (tag == 64) {
                return this.annotationMemberValue(pos + 1);
            }
            if (tag == 91) {
                int num = ByteArray.readU16bit(this.info, pos + 1);
                return this.arrayMemberValue(pos + 3, num);
            }
            int index = ByteArray.readU16bit(this.info, pos + 1);
            this.constValueMember(tag, index);
            return pos + 3;
        }

        void constValueMember(int tag, int index) throws Exception {
        }

        void enumMemberValue(int pos, int typeNameIndex, int constNameIndex) throws Exception {
        }

        void classMemberValue(int pos, int index) throws Exception {
        }

        int annotationMemberValue(int pos) throws Exception {
            return this.annotation(pos);
        }

        int arrayMemberValue(int pos, int num) throws Exception {
            for (int i2 = 0; i2 < num; ++i2) {
                pos = this.memberValue(pos);
            }
            return pos;
        }
    }
}

