/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.util;

import com.google.common.base.Strings;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorByName;

public class SignaturePrinter {
    private final String name;
    private final Type returnType;
    private final Type[] argTypes;
    private final String[] argNames;
    private String modifiers = "private void";
    private boolean fullyQualified;

    public SignaturePrinter(MethodNode method) {
        this(method.name, Type.VOID_TYPE, Type.getArgumentTypes(method.desc));
        this.setModifiers(method);
    }

    public SignaturePrinter(MethodNode method, String[] argNames) {
        this(method.name, Type.VOID_TYPE, Type.getArgumentTypes(method.desc), argNames);
        this.setModifiers(method);
    }

    public SignaturePrinter(ITargetSelectorByName member) {
        this(member.getName(), member.getDesc());
    }

    public SignaturePrinter(String name, String desc) {
        this(name, Type.getReturnType(desc), Type.getArgumentTypes(desc));
    }

    public SignaturePrinter(Type[] args) {
        this(null, null, args);
    }

    public SignaturePrinter(Type returnType, Type[] args) {
        this(null, returnType, args);
    }

    public SignaturePrinter(String name, Type returnType, Type[] args) {
        this.name = name;
        this.returnType = returnType;
        this.argTypes = new Type[args.length];
        this.argNames = new String[args.length];
        int v2 = 0;
        for (int l2 = 0; l2 < args.length; ++l2) {
            if (args[l2] == null) continue;
            this.argTypes[l2] = args[l2];
            this.argNames[l2] = "var" + v2++;
        }
    }

    public SignaturePrinter(String name, Type returnType, LocalVariableNode[] args) {
        this.name = name;
        this.returnType = returnType;
        this.argTypes = new Type[args.length];
        this.argNames = new String[args.length];
        for (int l2 = 0; l2 < args.length; ++l2) {
            if (args[l2] == null) continue;
            this.argTypes[l2] = Type.getType(args[l2].desc);
            this.argNames[l2] = args[l2].name;
        }
    }

    public SignaturePrinter(String name, Type returnType, Type[] argTypes, String[] argNames) {
        this.name = name;
        this.returnType = returnType;
        this.argTypes = argTypes;
        this.argNames = argNames;
    }

    public String getFormattedArgs() {
        return this.appendArgs(new StringBuilder(), true, true).toString();
    }

    public String getReturnType() {
        return SignaturePrinter.getTypeName(this.returnType, false, this.fullyQualified);
    }

    public void setModifiers(MethodNode method) {
        String returnType = SignaturePrinter.getTypeName(Type.getReturnType(method.desc), false, this.fullyQualified);
        if ((method.access & 1) != 0) {
            this.setModifiers("public " + returnType);
        } else if ((method.access & 4) != 0) {
            this.setModifiers("protected " + returnType);
        } else if ((method.access & 2) != 0) {
            this.setModifiers("private " + returnType);
        } else {
            this.setModifiers(returnType);
        }
    }

    public SignaturePrinter setModifiers(String modifiers) {
        this.modifiers = modifiers.replace("${returnType}", this.getReturnType());
        return this;
    }

    public SignaturePrinter setFullyQualified(boolean fullyQualified) {
        this.fullyQualified = fullyQualified;
        return this;
    }

    public boolean isFullyQualified() {
        return this.fullyQualified;
    }

    public String toString() {
        String name = this.name != null ? this.name : "method";
        return this.appendArgs(new StringBuilder().append(this.modifiers).append(" ").append(name), false, true).toString();
    }

    public String toDescriptor() {
        StringBuilder args = this.appendArgs(new StringBuilder(), true, false);
        return args.append(SignaturePrinter.getTypeName(this.returnType, false, this.fullyQualified)).toString();
    }

    private StringBuilder appendArgs(StringBuilder sb2, boolean typesOnly, boolean pretty) {
        sb2.append('(');
        for (int var = 0; var < this.argTypes.length; ++var) {
            if (this.argTypes[var] == null) continue;
            if (var > 0) {
                sb2.append(',');
                if (pretty) {
                    sb2.append(' ');
                }
            }
            try {
                String name = typesOnly ? null : (var < this.argNames.length && !Strings.isNullOrEmpty(this.argNames[var]) ? this.argNames[var] : "unnamed" + var);
                this.appendType(sb2, this.argTypes[var], name);
                continue;
            }
            catch (Exception ex2) {
                throw new RuntimeException(ex2);
            }
        }
        return sb2.append(")");
    }

    private StringBuilder appendType(StringBuilder sb2, Type type, String name) {
        switch (type.getSort()) {
            case 9: {
                return SignaturePrinter.appendArraySuffix(this.appendType(sb2, SignaturePrinter.getElementType(type), name), type);
            }
            case 10: {
                return this.appendType(sb2, SignaturePrinter.getClassName(type), name);
            }
        }
        sb2.append(SignaturePrinter.getTypeName(type, false, this.fullyQualified));
        if (name != null) {
            sb2.append(' ').append(name);
        }
        return sb2;
    }

    private StringBuilder appendType(StringBuilder sb2, String typeName, String name) {
        if (!this.fullyQualified) {
            typeName = typeName.substring(typeName.lastIndexOf(46) + 1);
        }
        sb2.append(typeName);
        if (typeName.endsWith("CallbackInfoReturnable")) {
            sb2.append('<').append(SignaturePrinter.getTypeName(this.returnType, true, this.fullyQualified)).append('>');
        }
        if (name != null) {
            sb2.append(' ').append(name);
        }
        return sb2;
    }

    public static String getTypeName(Type type) {
        return SignaturePrinter.getTypeName(type, false, true);
    }

    public static String getTypeName(Type type, boolean box2) {
        return SignaturePrinter.getTypeName(type, box2, false);
    }

    public static String getTypeName(Type type, boolean box2, boolean fullyQualified) {
        if (type == null) {
            return "{null?}";
        }
        switch (type.getSort()) {
            case 0: {
                return box2 ? "Void" : "void";
            }
            case 1: {
                return box2 ? "Boolean" : "boolean";
            }
            case 2: {
                return box2 ? "Character" : "char";
            }
            case 3: {
                return box2 ? "Byte" : "byte";
            }
            case 4: {
                return box2 ? "Short" : "short";
            }
            case 5: {
                return box2 ? "Integer" : "int";
            }
            case 6: {
                return box2 ? "Float" : "float";
            }
            case 7: {
                return box2 ? "Long" : "long";
            }
            case 8: {
                return box2 ? "Double" : "double";
            }
            case 9: {
                return SignaturePrinter.getTypeName(SignaturePrinter.getElementType(type), box2, fullyQualified) + SignaturePrinter.arraySuffix(type);
            }
            case 10: {
                String typeName = SignaturePrinter.getClassName(type);
                if (!fullyQualified) {
                    typeName = typeName.substring(typeName.lastIndexOf(46) + 1);
                }
                return typeName;
            }
        }
        return "Object";
    }

    private static Type getElementType(Type type) {
        try {
            return type.getElementType();
        }
        catch (Exception ex2) {
            return Type.getObjectType("InvalidType");
        }
    }

    private static String getClassName(Type type) {
        try {
            return type.getClassName();
        }
        catch (Exception ex2) {
            return "InvalidType";
        }
    }

    private static String arraySuffix(Type type) {
        return Strings.repeat("[]", type.getDimensions());
    }

    private static StringBuilder appendArraySuffix(StringBuilder sb2, Type type) {
        for (int i2 = 0; i2 < type.getDimensions(); ++i2) {
            sb2.append("[]");
        }
        return sb2;
    }
}

