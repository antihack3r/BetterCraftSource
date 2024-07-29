/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.SerialVersionUIDAdder$Item;

public class SerialVersionUIDAdder
extends ClassVisitor {
    private boolean computeSVUID;
    private boolean hasSVUID;
    private int access;
    private String name;
    private String[] interfaces;
    private Collection svuidFields = new ArrayList();
    private boolean hasStaticInitializer;
    private Collection svuidConstructors = new ArrayList();
    private Collection svuidMethods = new ArrayList();
    static /* synthetic */ Class class$org$objectweb$asm$commons$SerialVersionUIDAdder;

    public SerialVersionUIDAdder(ClassVisitor classVisitor) {
        this(327680, classVisitor);
        if (this.getClass() != class$org$objectweb$asm$commons$SerialVersionUIDAdder) {
            throw new IllegalStateException();
        }
    }

    protected SerialVersionUIDAdder(int n2, ClassVisitor classVisitor) {
        super(n2, classVisitor);
    }

    public void visit(int n2, int n3, String string, String string2, String string3, String[] stringArray) {
        boolean bl2 = this.computeSVUID = (n3 & 0x4000) == 0;
        if (this.computeSVUID) {
            this.name = string;
            this.access = n3;
            this.interfaces = new String[stringArray.length];
            System.arraycopy(stringArray, 0, this.interfaces, 0, stringArray.length);
        }
        super.visit(n2, n3, string, string2, string3, stringArray);
    }

    public MethodVisitor visitMethod(int n2, String string, String string2, String string3, String[] stringArray) {
        if (this.computeSVUID) {
            if ("<clinit>".equals(string)) {
                this.hasStaticInitializer = true;
            }
            int n3 = n2 & 0xD3F;
            if ((n2 & 2) == 0) {
                if ("<init>".equals(string)) {
                    this.svuidConstructors.add(new SerialVersionUIDAdder$Item(string, n3, string2));
                } else if (!"<clinit>".equals(string)) {
                    this.svuidMethods.add(new SerialVersionUIDAdder$Item(string, n3, string2));
                }
            }
        }
        return super.visitMethod(n2, string, string2, string3, stringArray);
    }

    public FieldVisitor visitField(int n2, String string, String string2, String string3, Object object) {
        if (this.computeSVUID) {
            if ("serialVersionUID".equals(string)) {
                this.computeSVUID = false;
                this.hasSVUID = true;
            }
            if ((n2 & 2) == 0 || (n2 & 0x88) == 0) {
                int n3 = n2 & 0xDF;
                this.svuidFields.add(new SerialVersionUIDAdder$Item(string, n3, string2));
            }
        }
        return super.visitField(n2, string, string2, string3, object);
    }

    public void visitInnerClass(String string, String string2, String string3, int n2) {
        if (this.name != null && this.name.equals(string)) {
            this.access = n2;
        }
        super.visitInnerClass(string, string2, string3, n2);
    }

    public void visitEnd() {
        if (this.computeSVUID && !this.hasSVUID) {
            try {
                this.addSVUID(this.computeSVUID());
            }
            catch (Throwable throwable) {
                throw new RuntimeException("Error while computing SVUID for " + this.name, throwable);
            }
        }
        super.visitEnd();
    }

    public boolean hasSVUID() {
        return this.hasSVUID;
    }

    protected void addSVUID(long l2) {
        FieldVisitor fieldVisitor = super.visitField(24, "serialVersionUID", "J", null, new Long(l2));
        if (fieldVisitor != null) {
            fieldVisitor.visitEnd();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long computeSVUID() throws IOException {
        FilterOutputStream filterOutputStream = null;
        long l2 = 0L;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            filterOutputStream = new DataOutputStream(byteArrayOutputStream);
            ((DataOutputStream)filterOutputStream).writeUTF(this.name.replace('/', '.'));
            int n2 = this.access;
            if ((n2 & 0x200) != 0) {
                n2 = this.svuidMethods.size() > 0 ? n2 | 0x400 : n2 & 0xFFFFFBFF;
            }
            ((DataOutputStream)filterOutputStream).writeInt(n2 & 0x611);
            Arrays.sort(this.interfaces);
            for (int i2 = 0; i2 < this.interfaces.length; ++i2) {
                ((DataOutputStream)filterOutputStream).writeUTF(this.interfaces[i2].replace('/', '.'));
            }
            SerialVersionUIDAdder.writeItems(this.svuidFields, (DataOutput)((Object)filterOutputStream), false);
            if (this.hasStaticInitializer) {
                ((DataOutputStream)filterOutputStream).writeUTF("<clinit>");
                ((DataOutputStream)filterOutputStream).writeInt(8);
                ((DataOutputStream)filterOutputStream).writeUTF("()V");
            }
            SerialVersionUIDAdder.writeItems(this.svuidConstructors, (DataOutput)((Object)filterOutputStream), true);
            SerialVersionUIDAdder.writeItems(this.svuidMethods, (DataOutput)((Object)filterOutputStream), true);
            ((DataOutputStream)filterOutputStream).flush();
            byte[] byArray = this.computeSHAdigest(byteArrayOutputStream.toByteArray());
            for (int i3 = Math.min(byArray.length, 8) - 1; i3 >= 0; --i3) {
                l2 = l2 << 8 | (long)(byArray[i3] & 0xFF);
            }
        }
        finally {
            if (filterOutputStream != null) {
                filterOutputStream.close();
            }
        }
        return l2;
    }

    protected byte[] computeSHAdigest(byte[] byArray) {
        try {
            return MessageDigest.getInstance("SHA").digest(byArray);
        }
        catch (Exception exception) {
            throw new UnsupportedOperationException(exception.toString());
        }
    }

    private static void writeItems(Collection collection, DataOutput dataOutput, boolean bl2) throws IOException {
        int n2 = collection.size();
        Object[] objectArray = collection.toArray(new SerialVersionUIDAdder$Item[n2]);
        Arrays.sort(objectArray);
        for (int i2 = 0; i2 < n2; ++i2) {
            dataOutput.writeUTF(((SerialVersionUIDAdder$Item)objectArray[i2]).name);
            dataOutput.writeInt(((SerialVersionUIDAdder$Item)objectArray[i2]).access);
            dataOutput.writeUTF(bl2 ? ((SerialVersionUIDAdder$Item)objectArray[i2]).desc.replace('/', '.') : ((SerialVersionUIDAdder$Item)objectArray[i2]).desc);
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            String string2 = classNotFoundException.getMessage();
            throw new NoClassDefFoundError(string2);
        }
    }

    static {
        class$org$objectweb$asm$commons$SerialVersionUIDAdder = SerialVersionUIDAdder.class$("org.objectweb.asm.commons.SerialVersionUIDAdder");
    }
}

