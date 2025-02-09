/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationsWriter;

public class ParameterAnnotationsAttribute
extends AttributeInfo {
    public static final String visibleTag = "RuntimeVisibleParameterAnnotations";
    public static final String invisibleTag = "RuntimeInvisibleParameterAnnotations";

    public ParameterAnnotationsAttribute(ConstPool cp2, String attrname, byte[] info) {
        super(cp2, attrname, info);
    }

    public ParameterAnnotationsAttribute(ConstPool cp2, String attrname) {
        this(cp2, attrname, new byte[]{0});
    }

    ParameterAnnotationsAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    public int numParameters() {
        return this.info[0] & 0xFF;
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        AnnotationsAttribute.Copier copier = new AnnotationsAttribute.Copier(this.info, this.constPool, newCp, classnames);
        try {
            copier.parameters();
            return new ParameterAnnotationsAttribute(newCp, this.getName(), copier.close());
        }
        catch (Exception e2) {
            throw new RuntimeException(e2.toString());
        }
    }

    public Annotation[][] getAnnotations() {
        try {
            return new AnnotationsAttribute.Parser(this.info, this.constPool).parseParameters();
        }
        catch (Exception e2) {
            throw new RuntimeException(e2.toString());
        }
    }

    public void setAnnotations(Annotation[][] params) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);
        try {
            writer.numParameters(params.length);
            for (Annotation[] anno : params) {
                writer.numAnnotations(anno.length);
                for (int j2 = 0; j2 < anno.length; ++j2) {
                    anno[j2].write(writer);
                }
            }
            writer.close();
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
        this.set(output.toByteArray());
    }

    @Override
    void renameClass(String oldname, String newname) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(oldname, newname);
        this.renameClass(map);
    }

    @Override
    void renameClass(Map<String, String> classnames) {
        AnnotationsAttribute.Renamer renamer = new AnnotationsAttribute.Renamer(this.info, this.getConstPool(), classnames);
        try {
            renamer.parameters();
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
        Annotation[][] aa2 = this.getAnnotations();
        StringBuilder sbuf = new StringBuilder();
        Annotation[][] annotationArray = aa2;
        int n2 = annotationArray.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            Annotation[] a2;
            for (Annotation i3 : a2 = annotationArray[i2]) {
                sbuf.append(i3.toString()).append(" ");
            }
            sbuf.append(", ");
        }
        return sbuf.toString().replaceAll(" (?=,)|, $", "");
    }
}

