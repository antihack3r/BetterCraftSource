/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.MemberValue;

public class AnnotationDefaultAttribute
extends AttributeInfo {
    public static final String tag = "AnnotationDefault";

    public AnnotationDefaultAttribute(ConstPool cp2, byte[] info) {
        super(cp2, tag, info);
    }

    public AnnotationDefaultAttribute(ConstPool cp2) {
        this(cp2, new byte[]{0, 0});
    }

    AnnotationDefaultAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        AnnotationsAttribute.Copier copier = new AnnotationsAttribute.Copier(this.info, this.constPool, newCp, classnames);
        try {
            copier.memberValue(0);
            return new AnnotationDefaultAttribute(newCp, copier.close());
        }
        catch (Exception e2) {
            throw new RuntimeException(e2.toString());
        }
    }

    public MemberValue getDefaultValue() {
        try {
            return new AnnotationsAttribute.Parser(this.info, this.constPool).parseMemberValue();
        }
        catch (Exception e2) {
            throw new RuntimeException(e2.toString());
        }
    }

    public void setDefaultValue(MemberValue value) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);
        try {
            value.write(writer);
            writer.close();
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
        this.set(output.toByteArray());
    }

    public String toString() {
        return this.getDefaultValue().toString();
    }
}

