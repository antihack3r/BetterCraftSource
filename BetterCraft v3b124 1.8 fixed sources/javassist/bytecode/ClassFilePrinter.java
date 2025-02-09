/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.PrintWriter;
import java.util.List;
import javassist.Modifier;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.StackMap;
import javassist.bytecode.StackMapTable;

public class ClassFilePrinter {
    public static void print(ClassFile cf2) {
        ClassFilePrinter.print(cf2, new PrintWriter(System.out, true));
    }

    public static void print(ClassFile cf2, PrintWriter out) {
        int mod = AccessFlag.toModifier(cf2.getAccessFlags() & 0xFFFFFFDF);
        out.println("major: " + cf2.major + ", minor: " + cf2.minor + " modifiers: " + Integer.toHexString(cf2.getAccessFlags()));
        out.println(Modifier.toString(mod) + " class " + cf2.getName() + " extends " + cf2.getSuperclass());
        String[] infs = cf2.getInterfaces();
        if (infs != null && infs.length > 0) {
            out.print("    implements ");
            out.print(infs[0]);
            for (int i2 = 1; i2 < infs.length; ++i2) {
                out.print(", " + infs[i2]);
            }
            out.println();
        }
        out.println();
        List<FieldInfo> fields = cf2.getFields();
        for (FieldInfo finfo : fields) {
            int acc2 = finfo.getAccessFlags();
            out.println(Modifier.toString(AccessFlag.toModifier(acc2)) + " " + finfo.getName() + "\t" + finfo.getDescriptor());
            ClassFilePrinter.printAttributes(finfo.getAttributes(), out, 'f');
        }
        out.println();
        List<MethodInfo> methods = cf2.getMethods();
        for (MethodInfo minfo : methods) {
            int acc3 = minfo.getAccessFlags();
            out.println(Modifier.toString(AccessFlag.toModifier(acc3)) + " " + minfo.getName() + "\t" + minfo.getDescriptor());
            ClassFilePrinter.printAttributes(minfo.getAttributes(), out, 'm');
            out.println();
        }
        out.println();
        ClassFilePrinter.printAttributes(cf2.getAttributes(), out, 'c');
    }

    static void printAttributes(List<AttributeInfo> list, PrintWriter out, char kind) {
        if (list == null) {
            return;
        }
        for (AttributeInfo ai2 : list) {
            if (ai2 instanceof CodeAttribute) {
                CodeAttribute ca2 = (CodeAttribute)ai2;
                out.println("attribute: " + ai2.getName() + ": " + ai2.getClass().getName());
                out.println("max stack " + ca2.getMaxStack() + ", max locals " + ca2.getMaxLocals() + ", " + ca2.getExceptionTable().size() + " catch blocks");
                out.println("<code attribute begin>");
                ClassFilePrinter.printAttributes(ca2.getAttributes(), out, kind);
                out.println("<code attribute end>");
                continue;
            }
            if (ai2 instanceof AnnotationsAttribute) {
                out.println("annnotation: " + ai2.toString());
                continue;
            }
            if (ai2 instanceof ParameterAnnotationsAttribute) {
                out.println("parameter annnotations: " + ai2.toString());
                continue;
            }
            if (ai2 instanceof StackMapTable) {
                out.println("<stack map table begin>");
                StackMapTable.Printer.print((StackMapTable)ai2, out);
                out.println("<stack map table end>");
                continue;
            }
            if (ai2 instanceof StackMap) {
                out.println("<stack map begin>");
                ((StackMap)ai2).print(out);
                out.println("<stack map end>");
                continue;
            }
            if (ai2 instanceof SignatureAttribute) {
                SignatureAttribute sa2 = (SignatureAttribute)ai2;
                String sig = sa2.getSignature();
                out.println("signature: " + sig);
                try {
                    String s2 = kind == 'c' ? SignatureAttribute.toClassSignature(sig).toString() : (kind == 'm' ? SignatureAttribute.toMethodSignature(sig).toString() : SignatureAttribute.toFieldSignature(sig).toString());
                    out.println("           " + s2);
                }
                catch (BadBytecode e2) {
                    out.println("           syntax error");
                }
                continue;
            }
            out.println("attribute: " + ai2.getName() + " (" + ai2.get().length + " byte): " + ai2.getClass().getName());
        }
    }
}

