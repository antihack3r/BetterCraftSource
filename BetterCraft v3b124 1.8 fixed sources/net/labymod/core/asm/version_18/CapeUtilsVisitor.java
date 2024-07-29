/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm.version_18;

import net.labymod.core.asm.global.ClassEditor;

public class CapeUtilsVisitor
extends ClassEditor {
    public CapeUtilsVisitor() {
        super(ClassEditor.ClassEditorType.CLASS_VISITOR_AND_REMAPPER);
    }

    @Override
    public String visitMapping(String typeName) {
        if (typeName.equals("CapeUtils$1")) {
            return "net/labymod/core_implementation/mc18/of/CapeImageBuffer";
        }
        return super.visitMapping(typeName);
    }
}

