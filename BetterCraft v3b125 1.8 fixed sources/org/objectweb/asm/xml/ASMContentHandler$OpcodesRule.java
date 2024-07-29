/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Opcode;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

final class ASMContentHandler$OpcodesRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$OpcodesRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) throws SAXException {
        ASMContentHandler$Opcode aSMContentHandler$Opcode = (ASMContentHandler$Opcode)ASMContentHandler.OPCODES.get(string);
        if (aSMContentHandler$Opcode == null) {
            throw new SAXException("Invalid element: " + string + " at " + this.this$0.match);
        }
        switch (aSMContentHandler$Opcode.type) {
            case 0: {
                this.getCodeVisitor().visitInsn(aSMContentHandler$Opcode.opcode);
                break;
            }
            case 4: {
                this.getCodeVisitor().visitFieldInsn(aSMContentHandler$Opcode.opcode, attributes.getValue("owner"), attributes.getValue("name"), attributes.getValue("desc"));
                break;
            }
            case 1: {
                this.getCodeVisitor().visitIntInsn(aSMContentHandler$Opcode.opcode, Integer.parseInt(attributes.getValue("value")));
                break;
            }
            case 6: {
                this.getCodeVisitor().visitJumpInsn(aSMContentHandler$Opcode.opcode, this.getLabel(attributes.getValue("label")));
                break;
            }
            case 5: {
                this.getCodeVisitor().visitMethodInsn(aSMContentHandler$Opcode.opcode, attributes.getValue("owner"), attributes.getValue("name"), attributes.getValue("desc"), attributes.getValue("itf").equals("true"));
                break;
            }
            case 3: {
                this.getCodeVisitor().visitTypeInsn(aSMContentHandler$Opcode.opcode, attributes.getValue("desc"));
                break;
            }
            case 2: {
                this.getCodeVisitor().visitVarInsn(aSMContentHandler$Opcode.opcode, Integer.parseInt(attributes.getValue("var")));
                break;
            }
            case 8: {
                this.getCodeVisitor().visitIincInsn(Integer.parseInt(attributes.getValue("var")), Integer.parseInt(attributes.getValue("inc")));
                break;
            }
            case 7: {
                this.getCodeVisitor().visitLdcInsn(this.getValue(attributes.getValue("desc"), attributes.getValue("cst")));
                break;
            }
            case 9: {
                this.getCodeVisitor().visitMultiANewArrayInsn(attributes.getValue("desc"), Integer.parseInt(attributes.getValue("dims")));
                break;
            }
            default: {
                throw new Error("Internal error");
            }
        }
    }
}

