/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.xml.ASMContentHandler$AnnotationDefaultRule;
import org.objectweb.asm.xml.ASMContentHandler$AnnotationParameterRule;
import org.objectweb.asm.xml.ASMContentHandler$AnnotationRule;
import org.objectweb.asm.xml.ASMContentHandler$AnnotationValueAnnotationRule;
import org.objectweb.asm.xml.ASMContentHandler$AnnotationValueArrayRule;
import org.objectweb.asm.xml.ASMContentHandler$AnnotationValueEnumRule;
import org.objectweb.asm.xml.ASMContentHandler$AnnotationValueRule;
import org.objectweb.asm.xml.ASMContentHandler$ClassRule;
import org.objectweb.asm.xml.ASMContentHandler$ExceptionRule;
import org.objectweb.asm.xml.ASMContentHandler$ExceptionsRule;
import org.objectweb.asm.xml.ASMContentHandler$FieldRule;
import org.objectweb.asm.xml.ASMContentHandler$FrameRule;
import org.objectweb.asm.xml.ASMContentHandler$FrameTypeRule;
import org.objectweb.asm.xml.ASMContentHandler$InnerClassRule;
import org.objectweb.asm.xml.ASMContentHandler$InsnAnnotationRule;
import org.objectweb.asm.xml.ASMContentHandler$InterfaceRule;
import org.objectweb.asm.xml.ASMContentHandler$InterfacesRule;
import org.objectweb.asm.xml.ASMContentHandler$InvokeDynamicBsmArgumentsRule;
import org.objectweb.asm.xml.ASMContentHandler$InvokeDynamicRule;
import org.objectweb.asm.xml.ASMContentHandler$LabelRule;
import org.objectweb.asm.xml.ASMContentHandler$LineNumberRule;
import org.objectweb.asm.xml.ASMContentHandler$LocalVarRule;
import org.objectweb.asm.xml.ASMContentHandler$LocalVariableAnnotationRule;
import org.objectweb.asm.xml.ASMContentHandler$LookupSwitchLabelRule;
import org.objectweb.asm.xml.ASMContentHandler$LookupSwitchRule;
import org.objectweb.asm.xml.ASMContentHandler$MaxRule;
import org.objectweb.asm.xml.ASMContentHandler$MethodParameterRule;
import org.objectweb.asm.xml.ASMContentHandler$MethodRule;
import org.objectweb.asm.xml.ASMContentHandler$Opcode;
import org.objectweb.asm.xml.ASMContentHandler$OpcodesRule;
import org.objectweb.asm.xml.ASMContentHandler$OuterClassRule;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.objectweb.asm.xml.ASMContentHandler$RuleSet;
import org.objectweb.asm.xml.ASMContentHandler$SourceRule;
import org.objectweb.asm.xml.ASMContentHandler$TableSwitchLabelRule;
import org.objectweb.asm.xml.ASMContentHandler$TableSwitchRule;
import org.objectweb.asm.xml.ASMContentHandler$TryCatchAnnotationRule;
import org.objectweb.asm.xml.ASMContentHandler$TryCatchRule;
import org.objectweb.asm.xml.ASMContentHandler$TypeAnnotationRule;
import org.objectweb.asm.xml.SAXCodeAdapter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ASMContentHandler
extends DefaultHandler
implements Opcodes {
    private final ArrayList stack = new ArrayList();
    String match = "";
    protected ClassVisitor cv;
    protected Map labels;
    private static final String BASE = "class";
    private final ASMContentHandler$RuleSet RULES = new ASMContentHandler$RuleSet();
    static final HashMap OPCODES;
    static final HashMap TYPES;

    private static void addOpcode(String string, int n2, int n3) {
        OPCODES.put(string, new ASMContentHandler$Opcode(n2, n3));
    }

    public ASMContentHandler(ClassVisitor classVisitor) {
        this.RULES.add(BASE, new ASMContentHandler$ClassRule(this));
        this.RULES.add("class/interfaces/interface", new ASMContentHandler$InterfaceRule(this));
        this.RULES.add("class/interfaces", new ASMContentHandler$InterfacesRule(this));
        this.RULES.add("class/outerclass", new ASMContentHandler$OuterClassRule(this));
        this.RULES.add("class/innerclass", new ASMContentHandler$InnerClassRule(this));
        this.RULES.add("class/source", new ASMContentHandler$SourceRule(this));
        this.RULES.add("class/field", new ASMContentHandler$FieldRule(this));
        this.RULES.add("class/method", new ASMContentHandler$MethodRule(this));
        this.RULES.add("class/method/exceptions/exception", new ASMContentHandler$ExceptionRule(this));
        this.RULES.add("class/method/exceptions", new ASMContentHandler$ExceptionsRule(this));
        this.RULES.add("class/method/parameter", new ASMContentHandler$MethodParameterRule(this));
        this.RULES.add("class/method/annotationDefault", new ASMContentHandler$AnnotationDefaultRule(this));
        this.RULES.add("class/method/code/*", new ASMContentHandler$OpcodesRule(this));
        this.RULES.add("class/method/code/frame", new ASMContentHandler$FrameRule(this));
        this.RULES.add("class/method/code/frame/local", new ASMContentHandler$FrameTypeRule(this));
        this.RULES.add("class/method/code/frame/stack", new ASMContentHandler$FrameTypeRule(this));
        this.RULES.add("class/method/code/TABLESWITCH", new ASMContentHandler$TableSwitchRule(this));
        this.RULES.add("class/method/code/TABLESWITCH/label", new ASMContentHandler$TableSwitchLabelRule(this));
        this.RULES.add("class/method/code/LOOKUPSWITCH", new ASMContentHandler$LookupSwitchRule(this));
        this.RULES.add("class/method/code/LOOKUPSWITCH/label", new ASMContentHandler$LookupSwitchLabelRule(this));
        this.RULES.add("class/method/code/INVOKEDYNAMIC", new ASMContentHandler$InvokeDynamicRule(this));
        this.RULES.add("class/method/code/INVOKEDYNAMIC/bsmArg", new ASMContentHandler$InvokeDynamicBsmArgumentsRule(this));
        this.RULES.add("class/method/code/Label", new ASMContentHandler$LabelRule(this));
        this.RULES.add("class/method/code/TryCatch", new ASMContentHandler$TryCatchRule(this));
        this.RULES.add("class/method/code/LineNumber", new ASMContentHandler$LineNumberRule(this));
        this.RULES.add("class/method/code/LocalVar", new ASMContentHandler$LocalVarRule(this));
        this.RULES.add("class/method/code/Max", new ASMContentHandler$MaxRule(this));
        this.RULES.add("*/annotation", new ASMContentHandler$AnnotationRule(this));
        this.RULES.add("*/typeAnnotation", new ASMContentHandler$TypeAnnotationRule(this));
        this.RULES.add("*/parameterAnnotation", new ASMContentHandler$AnnotationParameterRule(this));
        this.RULES.add("*/insnAnnotation", new ASMContentHandler$InsnAnnotationRule(this));
        this.RULES.add("*/tryCatchAnnotation", new ASMContentHandler$TryCatchAnnotationRule(this));
        this.RULES.add("*/localVariableAnnotation", new ASMContentHandler$LocalVariableAnnotationRule(this));
        this.RULES.add("*/annotationValue", new ASMContentHandler$AnnotationValueRule(this));
        this.RULES.add("*/annotationValueAnnotation", new ASMContentHandler$AnnotationValueAnnotationRule(this));
        this.RULES.add("*/annotationValueEnum", new ASMContentHandler$AnnotationValueEnumRule(this));
        this.RULES.add("*/annotationValueArray", new ASMContentHandler$AnnotationValueArrayRule(this));
        this.cv = classVisitor;
    }

    public final void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
        String string4 = string2 == null || string2.length() == 0 ? string3 : string2;
        StringBuffer stringBuffer = new StringBuffer(this.match);
        if (this.match.length() > 0) {
            stringBuffer.append('/');
        }
        stringBuffer.append(string4);
        this.match = stringBuffer.toString();
        ASMContentHandler$Rule aSMContentHandler$Rule = (ASMContentHandler$Rule)this.RULES.match(this.match);
        if (aSMContentHandler$Rule != null) {
            aSMContentHandler$Rule.begin(string4, attributes);
        }
    }

    public final void endElement(String string, String string2, String string3) throws SAXException {
        int n2;
        String string4 = string2 == null || string2.length() == 0 ? string3 : string2;
        ASMContentHandler$Rule aSMContentHandler$Rule = (ASMContentHandler$Rule)this.RULES.match(this.match);
        if (aSMContentHandler$Rule != null) {
            aSMContentHandler$Rule.end(string4);
        }
        this.match = (n2 = this.match.lastIndexOf(47)) >= 0 ? this.match.substring(0, n2) : "";
    }

    final Object peek() {
        int n2 = this.stack.size();
        return n2 == 0 ? null : this.stack.get(n2 - 1);
    }

    final Object pop() {
        int n2 = this.stack.size();
        return n2 == 0 ? null : this.stack.remove(n2 - 1);
    }

    final void push(Object object) {
        this.stack.add(object);
    }

    static {
        ASMContentHandler._clinit_();
        OPCODES = new HashMap();
        ASMContentHandler.addOpcode("NOP", 0, 0);
        ASMContentHandler.addOpcode("ACONST_NULL", 1, 0);
        ASMContentHandler.addOpcode("ICONST_M1", 2, 0);
        ASMContentHandler.addOpcode("ICONST_0", 3, 0);
        ASMContentHandler.addOpcode("ICONST_1", 4, 0);
        ASMContentHandler.addOpcode("ICONST_2", 5, 0);
        ASMContentHandler.addOpcode("ICONST_3", 6, 0);
        ASMContentHandler.addOpcode("ICONST_4", 7, 0);
        ASMContentHandler.addOpcode("ICONST_5", 8, 0);
        ASMContentHandler.addOpcode("LCONST_0", 9, 0);
        ASMContentHandler.addOpcode("LCONST_1", 10, 0);
        ASMContentHandler.addOpcode("FCONST_0", 11, 0);
        ASMContentHandler.addOpcode("FCONST_1", 12, 0);
        ASMContentHandler.addOpcode("FCONST_2", 13, 0);
        ASMContentHandler.addOpcode("DCONST_0", 14, 0);
        ASMContentHandler.addOpcode("DCONST_1", 15, 0);
        ASMContentHandler.addOpcode("BIPUSH", 16, 1);
        ASMContentHandler.addOpcode("SIPUSH", 17, 1);
        ASMContentHandler.addOpcode("LDC", 18, 7);
        ASMContentHandler.addOpcode("ILOAD", 21, 2);
        ASMContentHandler.addOpcode("LLOAD", 22, 2);
        ASMContentHandler.addOpcode("FLOAD", 23, 2);
        ASMContentHandler.addOpcode("DLOAD", 24, 2);
        ASMContentHandler.addOpcode("ALOAD", 25, 2);
        ASMContentHandler.addOpcode("IALOAD", 46, 0);
        ASMContentHandler.addOpcode("LALOAD", 47, 0);
        ASMContentHandler.addOpcode("FALOAD", 48, 0);
        ASMContentHandler.addOpcode("DALOAD", 49, 0);
        ASMContentHandler.addOpcode("AALOAD", 50, 0);
        ASMContentHandler.addOpcode("BALOAD", 51, 0);
        ASMContentHandler.addOpcode("CALOAD", 52, 0);
        ASMContentHandler.addOpcode("SALOAD", 53, 0);
        ASMContentHandler.addOpcode("ISTORE", 54, 2);
        ASMContentHandler.addOpcode("LSTORE", 55, 2);
        ASMContentHandler.addOpcode("FSTORE", 56, 2);
        ASMContentHandler.addOpcode("DSTORE", 57, 2);
        ASMContentHandler.addOpcode("ASTORE", 58, 2);
        ASMContentHandler.addOpcode("IASTORE", 79, 0);
        ASMContentHandler.addOpcode("LASTORE", 80, 0);
        ASMContentHandler.addOpcode("FASTORE", 81, 0);
        ASMContentHandler.addOpcode("DASTORE", 82, 0);
        ASMContentHandler.addOpcode("AASTORE", 83, 0);
        ASMContentHandler.addOpcode("BASTORE", 84, 0);
        ASMContentHandler.addOpcode("CASTORE", 85, 0);
        ASMContentHandler.addOpcode("SASTORE", 86, 0);
        ASMContentHandler.addOpcode("POP", 87, 0);
        ASMContentHandler.addOpcode("POP2", 88, 0);
        ASMContentHandler.addOpcode("DUP", 89, 0);
        ASMContentHandler.addOpcode("DUP_X1", 90, 0);
        ASMContentHandler.addOpcode("DUP_X2", 91, 0);
        ASMContentHandler.addOpcode("DUP2", 92, 0);
        ASMContentHandler.addOpcode("DUP2_X1", 93, 0);
        ASMContentHandler.addOpcode("DUP2_X2", 94, 0);
        ASMContentHandler.addOpcode("SWAP", 95, 0);
        ASMContentHandler.addOpcode("IADD", 96, 0);
        ASMContentHandler.addOpcode("LADD", 97, 0);
        ASMContentHandler.addOpcode("FADD", 98, 0);
        ASMContentHandler.addOpcode("DADD", 99, 0);
        ASMContentHandler.addOpcode("ISUB", 100, 0);
        ASMContentHandler.addOpcode("LSUB", 101, 0);
        ASMContentHandler.addOpcode("FSUB", 102, 0);
        ASMContentHandler.addOpcode("DSUB", 103, 0);
        ASMContentHandler.addOpcode("IMUL", 104, 0);
        ASMContentHandler.addOpcode("LMUL", 105, 0);
        ASMContentHandler.addOpcode("FMUL", 106, 0);
        ASMContentHandler.addOpcode("DMUL", 107, 0);
        ASMContentHandler.addOpcode("IDIV", 108, 0);
        ASMContentHandler.addOpcode("LDIV", 109, 0);
        ASMContentHandler.addOpcode("FDIV", 110, 0);
        ASMContentHandler.addOpcode("DDIV", 111, 0);
        ASMContentHandler.addOpcode("IREM", 112, 0);
        ASMContentHandler.addOpcode("LREM", 113, 0);
        ASMContentHandler.addOpcode("FREM", 114, 0);
        ASMContentHandler.addOpcode("DREM", 115, 0);
        ASMContentHandler.addOpcode("INEG", 116, 0);
        ASMContentHandler.addOpcode("LNEG", 117, 0);
        ASMContentHandler.addOpcode("FNEG", 118, 0);
        ASMContentHandler.addOpcode("DNEG", 119, 0);
        ASMContentHandler.addOpcode("ISHL", 120, 0);
        ASMContentHandler.addOpcode("LSHL", 121, 0);
        ASMContentHandler.addOpcode("ISHR", 122, 0);
        ASMContentHandler.addOpcode("LSHR", 123, 0);
        ASMContentHandler.addOpcode("IUSHR", 124, 0);
        ASMContentHandler.addOpcode("LUSHR", 125, 0);
        ASMContentHandler.addOpcode("IAND", 126, 0);
        ASMContentHandler.addOpcode("LAND", 127, 0);
        ASMContentHandler.addOpcode("IOR", 128, 0);
        ASMContentHandler.addOpcode("LOR", 129, 0);
        ASMContentHandler.addOpcode("IXOR", 130, 0);
        ASMContentHandler.addOpcode("LXOR", 131, 0);
        ASMContentHandler.addOpcode("IINC", 132, 8);
        ASMContentHandler.addOpcode("I2L", 133, 0);
        ASMContentHandler.addOpcode("I2F", 134, 0);
        ASMContentHandler.addOpcode("I2D", 135, 0);
        ASMContentHandler.addOpcode("L2I", 136, 0);
        ASMContentHandler.addOpcode("L2F", 137, 0);
        ASMContentHandler.addOpcode("L2D", 138, 0);
        ASMContentHandler.addOpcode("F2I", 139, 0);
        ASMContentHandler.addOpcode("F2L", 140, 0);
        ASMContentHandler.addOpcode("F2D", 141, 0);
        ASMContentHandler.addOpcode("D2I", 142, 0);
        ASMContentHandler.addOpcode("D2L", 143, 0);
        ASMContentHandler.addOpcode("D2F", 144, 0);
        ASMContentHandler.addOpcode("I2B", 145, 0);
        ASMContentHandler.addOpcode("I2C", 146, 0);
        ASMContentHandler.addOpcode("I2S", 147, 0);
        ASMContentHandler.addOpcode("LCMP", 148, 0);
        ASMContentHandler.addOpcode("FCMPL", 149, 0);
        ASMContentHandler.addOpcode("FCMPG", 150, 0);
        ASMContentHandler.addOpcode("DCMPL", 151, 0);
        ASMContentHandler.addOpcode("DCMPG", 152, 0);
        ASMContentHandler.addOpcode("IFEQ", 153, 6);
        ASMContentHandler.addOpcode("IFNE", 154, 6);
        ASMContentHandler.addOpcode("IFLT", 155, 6);
        ASMContentHandler.addOpcode("IFGE", 156, 6);
        ASMContentHandler.addOpcode("IFGT", 157, 6);
        ASMContentHandler.addOpcode("IFLE", 158, 6);
        ASMContentHandler.addOpcode("IF_ICMPEQ", 159, 6);
        ASMContentHandler.addOpcode("IF_ICMPNE", 160, 6);
        ASMContentHandler.addOpcode("IF_ICMPLT", 161, 6);
        ASMContentHandler.addOpcode("IF_ICMPGE", 162, 6);
        ASMContentHandler.addOpcode("IF_ICMPGT", 163, 6);
        ASMContentHandler.addOpcode("IF_ICMPLE", 164, 6);
        ASMContentHandler.addOpcode("IF_ACMPEQ", 165, 6);
        ASMContentHandler.addOpcode("IF_ACMPNE", 166, 6);
        ASMContentHandler.addOpcode("GOTO", 167, 6);
        ASMContentHandler.addOpcode("JSR", 168, 6);
        ASMContentHandler.addOpcode("RET", 169, 2);
        ASMContentHandler.addOpcode("IRETURN", 172, 0);
        ASMContentHandler.addOpcode("LRETURN", 173, 0);
        ASMContentHandler.addOpcode("FRETURN", 174, 0);
        ASMContentHandler.addOpcode("DRETURN", 175, 0);
        ASMContentHandler.addOpcode("ARETURN", 176, 0);
        ASMContentHandler.addOpcode("RETURN", 177, 0);
        ASMContentHandler.addOpcode("GETSTATIC", 178, 4);
        ASMContentHandler.addOpcode("PUTSTATIC", 179, 4);
        ASMContentHandler.addOpcode("GETFIELD", 180, 4);
        ASMContentHandler.addOpcode("PUTFIELD", 181, 4);
        ASMContentHandler.addOpcode("INVOKEVIRTUAL", 182, 5);
        ASMContentHandler.addOpcode("INVOKESPECIAL", 183, 5);
        ASMContentHandler.addOpcode("INVOKESTATIC", 184, 5);
        ASMContentHandler.addOpcode("INVOKEINTERFACE", 185, 5);
        ASMContentHandler.addOpcode("NEW", 187, 3);
        ASMContentHandler.addOpcode("NEWARRAY", 188, 1);
        ASMContentHandler.addOpcode("ANEWARRAY", 189, 3);
        ASMContentHandler.addOpcode("ARRAYLENGTH", 190, 0);
        ASMContentHandler.addOpcode("ATHROW", 191, 0);
        ASMContentHandler.addOpcode("CHECKCAST", 192, 3);
        ASMContentHandler.addOpcode("INSTANCEOF", 193, 3);
        ASMContentHandler.addOpcode("MONITORENTER", 194, 0);
        ASMContentHandler.addOpcode("MONITOREXIT", 195, 0);
        ASMContentHandler.addOpcode("MULTIANEWARRAY", 197, 9);
        ASMContentHandler.addOpcode("IFNULL", 198, 6);
        ASMContentHandler.addOpcode("IFNONNULL", 199, 6);
        TYPES = new HashMap();
        String[] stringArray = SAXCodeAdapter.TYPES;
        for (int i2 = 0; i2 < stringArray.length; ++i2) {
            TYPES.put(stringArray[i2], new Integer(i2));
        }
    }

    static /* synthetic */ void _clinit_() {
    }
}

