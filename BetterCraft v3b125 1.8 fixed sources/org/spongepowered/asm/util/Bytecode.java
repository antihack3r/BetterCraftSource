/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.util;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.spongepowered.asm.util.asm.ASM;
import org.spongepowered.asm.util.throwables.SyntheticBridgeException;

public final class Bytecode {
    public static final int[] CONSTANTS_INT = new int[]{2, 3, 4, 5, 6, 7, 8};
    public static final int[] CONSTANTS_FLOAT = new int[]{11, 12, 13};
    public static final int[] CONSTANTS_DOUBLE = new int[]{14, 15};
    public static final int[] CONSTANTS_LONG = new int[]{9, 10};
    public static final int[] CONSTANTS_ALL = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 192, 193};
    private static final Object[] CONSTANTS_VALUES = new Object[]{Type.VOID_TYPE, -1, 0, 1, 2, 3, 4, 5, 0L, 1L, Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(2.0f), 0.0, 1.0};
    private static final String[] CONSTANTS_TYPES = new String[]{"V", "I", "I", "I", "I", "I", "I", "I", "J", "J", "F", "F", "F", "D", "D", "I", "I"};
    private static final String[] BOXING_TYPES = new String[]{null, "java/lang/Boolean", "java/lang/Character", "java/lang/Byte", "java/lang/Short", "java/lang/Integer", "java/lang/Float", "java/lang/Long", "java/lang/Double", null, null, null};
    private static final String[] UNBOXING_METHODS = new String[]{null, "booleanValue", "charValue", "byteValue", "shortValue", "intValue", "floatValue", "longValue", "doubleValue", null, null, null};

    private Bytecode() {
    }

    public static MethodNode findMethod(ClassNode classNode, String name, String desc) {
        for (MethodNode method : classNode.methods) {
            if (!method.name.equals(name) || !method.desc.equals(desc)) continue;
            return method;
        }
        return null;
    }

    public static AbstractInsnNode findInsn(MethodNode method, int opcode) {
        ListIterator<AbstractInsnNode> findReturnIter = method.instructions.iterator();
        while (findReturnIter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)findReturnIter.next();
            if (insn.getOpcode() != opcode) continue;
            return insn;
        }
        return null;
    }

    public static DelegateInitialiser findDelegateInit(MethodNode ctor, String superName, String ownerName) {
        if (!"<init>".equals(ctor.name)) {
            return DelegateInitialiser.NONE;
        }
        int news = 0;
        ListIterator<AbstractInsnNode> iter = ctor.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (insn instanceof TypeInsnNode && insn.getOpcode() == 187) {
                ++news;
                continue;
            }
            if (!(insn instanceof MethodInsnNode) || insn.getOpcode() != 183) continue;
            MethodInsnNode methodNode = (MethodInsnNode)insn;
            if (!"<init>".equals(methodNode.name)) continue;
            if (news > 0) {
                --news;
                continue;
            }
            boolean isSuper = methodNode.owner.equals(superName);
            if (!isSuper && !methodNode.owner.equals(ownerName)) continue;
            return new DelegateInitialiser(methodNode, isSuper);
        }
        return DelegateInitialiser.NONE;
    }

    public static void textify(ClassNode classNode, OutputStream out) {
        classNode.accept(new TraceClassVisitor(new PrintWriter(out)));
    }

    public static void textify(MethodNode methodNode, OutputStream out) {
        TraceClassVisitor trace = new TraceClassVisitor(new PrintWriter(out));
        MethodVisitor mv2 = trace.visitMethod(methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, methodNode.exceptions.toArray(new String[0]));
        methodNode.accept(mv2);
        trace.visitEnd();
    }

    public static void dumpClass(ClassNode classNode) {
        ClassWriter cw2 = new ClassWriter(3);
        classNode.accept(cw2);
        Bytecode.dumpClass(cw2.toByteArray());
    }

    public static void dumpClass(byte[] bytes) {
        ClassReader cr2 = new ClassReader(bytes);
        CheckClassAdapter.verify(cr2, true, new PrintWriter(System.out));
    }

    public static void printMethodWithOpcodeIndices(MethodNode method) {
        System.err.printf("%s%s\n", method.name, method.desc);
        int i2 = 0;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            System.err.printf("[%4d] %s\n", i2++, Bytecode.describeNode((AbstractInsnNode)iter.next()));
        }
    }

    public static void printMethod(MethodNode method) {
        System.err.printf("%s%s maxStack=%d maxLocals=%d\n", method.name, method.desc, method.maxStack, method.maxLocals);
        int index = 0;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            System.err.printf("%-4d  ", index++);
            Bytecode.printNode((AbstractInsnNode)iter.next());
        }
    }

    public static void printNode(AbstractInsnNode node) {
        System.err.printf("%s\n", Bytecode.describeNode(node));
    }

    public static String describeNode(AbstractInsnNode node) {
        return Bytecode.describeNode(node, true);
    }

    public static String describeNode(AbstractInsnNode node, boolean listFormat) {
        if (node == null) {
            return listFormat ? String.format("   %-14s ", "null") : "null";
        }
        if (node instanceof LabelNode) {
            return String.format("[%s]", ((LabelNode)node).getLabel());
        }
        String out = String.format(listFormat ? "   %-14s " : "%s ", node.getClass().getSimpleName().replace("Node", ""));
        if (node instanceof JumpInsnNode) {
            out = out + String.format("[%s] [%s]", Bytecode.getOpcodeName(node), ((JumpInsnNode)node).label.getLabel());
        } else if (node instanceof VarInsnNode) {
            out = out + String.format("[%s] %d", Bytecode.getOpcodeName(node), ((VarInsnNode)node).var);
        } else if (node instanceof MethodInsnNode) {
            MethodInsnNode mth = (MethodInsnNode)node;
            out = out + String.format("[%s] %s::%s%s", Bytecode.getOpcodeName(node), mth.owner, mth.name, mth.desc);
        } else if (node instanceof FieldInsnNode) {
            FieldInsnNode fld = (FieldInsnNode)node;
            out = out + String.format("[%s] %s::%s:%s", Bytecode.getOpcodeName(node), fld.owner, fld.name, fld.desc);
        } else if (node instanceof InvokeDynamicInsnNode) {
            InvokeDynamicInsnNode idc = (InvokeDynamicInsnNode)node;
            out = out + String.format("[%s] %s%s { %s %s::%s%s }", Bytecode.getOpcodeName(node), idc.name, idc.desc, Bytecode.getOpcodeName(idc.bsm.getTag(), "H_GETFIELD", 1), idc.bsm.getOwner(), idc.bsm.getName(), idc.bsm.getDesc());
        } else if (node instanceof LineNumberNode) {
            LineNumberNode ln2 = (LineNumberNode)node;
            out = out + String.format("LINE=[%d] LABEL=[%s]", ln2.line, ln2.start.getLabel());
        } else {
            out = node instanceof LdcInsnNode ? out + ((LdcInsnNode)node).cst : (node instanceof IntInsnNode ? out + ((IntInsnNode)node).operand : (node instanceof FrameNode ? out + String.format("[%s] ", Bytecode.getOpcodeName(((FrameNode)node).type, "H_INVOKEINTERFACE", -1)) : (node instanceof TypeInsnNode ? out + String.format("[%s] %s", Bytecode.getOpcodeName(node), ((TypeInsnNode)node).desc) : out + String.format("[%s] ", Bytecode.getOpcodeName(node)))));
        }
        return out;
    }

    public static String getOpcodeName(AbstractInsnNode node) {
        return node != null ? Bytecode.getOpcodeName(node.getOpcode()) : "";
    }

    public static String getOpcodeName(int opcode) {
        return Bytecode.getOpcodeName(opcode, "UNINITIALIZED_THIS", 1);
    }

    private static String getOpcodeName(int opcode, String start, int min) {
        if (opcode >= min) {
            boolean found = false;
            try {
                for (Field f2 : Opcodes.class.getDeclaredFields()) {
                    if (!found && !f2.getName().equals(start)) continue;
                    found = true;
                    if (f2.getType() != Integer.TYPE || f2.getInt(null) != opcode) continue;
                    return f2.getName();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return opcode >= 0 ? String.valueOf(opcode) : "UNKNOWN";
    }

    public static boolean methodHasLineNumbers(MethodNode method) {
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            if (!(iter.next() instanceof LineNumberNode)) continue;
            return true;
        }
        return false;
    }

    public static boolean isStatic(MethodNode method) {
        return (method.access & 8) == 8;
    }

    public static boolean isStatic(FieldNode field) {
        return (field.access & 8) == 8;
    }

    public static int getFirstNonArgLocalIndex(MethodNode method) {
        return Bytecode.getFirstNonArgLocalIndex(Type.getArgumentTypes(method.desc), !Bytecode.isStatic(method));
    }

    public static int getFirstNonArgLocalIndex(Type[] args, boolean includeThis) {
        return Bytecode.getArgsSize(args) + (includeThis ? 1 : 0);
    }

    public static int getArgsSize(Type[] args) {
        return Bytecode.getArgsSize(args, 0, args.length);
    }

    public static int getArgsSize(Type[] args, int startIndex, int endIndex) {
        int size = 0;
        for (int index = startIndex; index < args.length && index < endIndex; ++index) {
            size += args[index].getSize();
        }
        return size;
    }

    public static void loadArgs(Type[] args, InsnList insns, int pos) {
        Bytecode.loadArgs(args, insns, pos, -1);
    }

    public static void loadArgs(Type[] args, InsnList insns, int start, int end) {
        Bytecode.loadArgs(args, insns, start, end, null);
    }

    public static void loadArgs(Type[] args, InsnList insns, int start, int end, Type[] casts) {
        int pos = start;
        for (int index = 0; index < args.length; ++index) {
            insns.add(new VarInsnNode(args[index].getOpcode(21), pos));
            if (casts != null && index < casts.length && casts[index] != null) {
                insns.add(new TypeInsnNode(192, casts[index].getInternalName()));
            }
            if (end < start || (pos += args[index].getSize()) < end) continue;
            return;
        }
    }

    public static Type[] getTypes(Class<?> ... classes) {
        Type[] types = new Type[classes.length];
        for (int index = 0; index < classes.length; ++index) {
            types[index] = Type.getType(classes[index]);
        }
        return types;
    }

    public static Map<LabelNode, LabelNode> cloneLabels(InsnList source) {
        HashMap<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
        ListIterator<AbstractInsnNode> iter = source.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof LabelNode)) continue;
            labels.put((LabelNode)insn, new LabelNode(((LabelNode)insn).getLabel()));
        }
        return labels;
    }

    public static String generateDescriptor(Type returnType, Type ... args) {
        return Bytecode.generateDescriptor((Object)returnType, (Object[])args);
    }

    public static String generateDescriptor(Object returnType, Object ... args) {
        StringBuilder sb2 = new StringBuilder().append('(');
        for (Object arg2 : args) {
            sb2.append(Bytecode.toDescriptor(arg2));
        }
        return sb2.append(')').append(returnType != null ? Bytecode.toDescriptor(returnType) : "V").toString();
    }

    private static String toDescriptor(Object arg2) {
        if (arg2 instanceof String) {
            return (String)arg2;
        }
        if (arg2 instanceof Type) {
            return arg2.toString();
        }
        if (arg2 instanceof Class) {
            return Type.getDescriptor((Class)arg2);
        }
        return arg2 == null ? "" : arg2.toString();
    }

    public static String getDescriptor(Type ... args) {
        return "(" + Joiner.on("").join(args) + ")";
    }

    public static String getDescriptor(Type returnType, Type ... args) {
        return Bytecode.getDescriptor(args) + returnType.toString();
    }

    public static String changeDescriptorReturnType(String desc, String returnType) {
        if (desc == null || !desc.startsWith("(") || desc.lastIndexOf(41) < 1) {
            return null;
        }
        if (returnType == null) {
            return desc;
        }
        return desc.substring(0, desc.lastIndexOf(41) + 1) + returnType;
    }

    public static String getSimpleName(Type type) {
        return type.getSort() < 9 ? type.getDescriptor() : Bytecode.getSimpleName(type.getClassName());
    }

    public static String getSimpleName(String desc) {
        int pos = Math.max(desc.lastIndexOf(47), 0);
        return desc.substring(pos + 1).replace(";", "");
    }

    public static boolean isConstant(AbstractInsnNode insn) {
        if (insn == null) {
            return false;
        }
        return Ints.contains(CONSTANTS_ALL, insn.getOpcode());
    }

    public static Object getConstant(AbstractInsnNode insn) {
        if (insn == null) {
            return null;
        }
        if (insn instanceof LdcInsnNode) {
            return ((LdcInsnNode)insn).cst;
        }
        if (insn instanceof IntInsnNode) {
            int value = ((IntInsnNode)insn).operand;
            if (insn.getOpcode() == 16 || insn.getOpcode() == 17) {
                return value;
            }
            throw new IllegalArgumentException("IntInsnNode with invalid opcode " + insn.getOpcode() + " in getConstant");
        }
        if (insn instanceof TypeInsnNode) {
            if (insn.getOpcode() < 192) {
                return null;
            }
            return Type.getObjectType(((TypeInsnNode)insn).desc);
        }
        int index = Ints.indexOf(CONSTANTS_ALL, insn.getOpcode());
        return index < 0 ? null : CONSTANTS_VALUES[index];
    }

    public static Type getConstantType(AbstractInsnNode insn) {
        if (insn == null) {
            return null;
        }
        if (insn instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode)insn).cst;
            if (cst instanceof Integer) {
                return Type.getType("I");
            }
            if (cst instanceof Float) {
                return Type.getType("F");
            }
            if (cst instanceof Long) {
                return Type.getType("J");
            }
            if (cst instanceof Double) {
                return Type.getType("D");
            }
            if (cst instanceof String) {
                return Type.getType("Ljava/lang/String;");
            }
            if (cst instanceof Type) {
                return Type.getType("Ljava/lang/Class;");
            }
            throw new IllegalArgumentException("LdcInsnNode with invalid payload type " + cst.getClass() + " in getConstant");
        }
        if (insn instanceof TypeInsnNode) {
            if (insn.getOpcode() < 192) {
                return null;
            }
            return Type.getType("Ljava/lang/Class;");
        }
        int index = Ints.indexOf(CONSTANTS_ALL, insn.getOpcode());
        return index < 0 ? null : Type.getType(CONSTANTS_TYPES[index]);
    }

    public static boolean hasFlag(ClassNode classNode, int flag) {
        return (classNode.access & flag) == flag;
    }

    public static boolean hasFlag(MethodNode method, int flag) {
        return (method.access & flag) == flag;
    }

    public static boolean hasFlag(FieldNode field, int flag) {
        return (field.access & flag) == flag;
    }

    public static boolean compareFlags(MethodNode m1, MethodNode m2, int flag) {
        return Bytecode.hasFlag(m1, flag) == Bytecode.hasFlag(m2, flag);
    }

    public static boolean compareFlags(FieldNode f1, FieldNode f2, int flag) {
        return Bytecode.hasFlag(f1, flag) == Bytecode.hasFlag(f2, flag);
    }

    public static boolean isVirtual(MethodNode method) {
        return method != null && !Bytecode.isStatic(method) && Bytecode.getVisibility(method).isAtLeast(Visibility.PROTECTED);
    }

    public static Visibility getVisibility(MethodNode method) {
        return Bytecode.getVisibility(method.access & 7);
    }

    public static Visibility getVisibility(FieldNode field) {
        return Bytecode.getVisibility(field.access & 7);
    }

    private static Visibility getVisibility(int flags) {
        if ((flags & 4) != 0) {
            return Visibility.PROTECTED;
        }
        if ((flags & 2) != 0) {
            return Visibility.PRIVATE;
        }
        if ((flags & 1) != 0) {
            return Visibility.PUBLIC;
        }
        return Visibility.PACKAGE;
    }

    public static void setVisibility(ClassNode classNode, Visibility visibility) {
        classNode.access = Bytecode.setVisibility(classNode.access, visibility.access);
    }

    public static void setVisibility(MethodNode method, Visibility visibility) {
        method.access = Bytecode.setVisibility(method.access, visibility.access);
    }

    public static void setVisibility(FieldNode field, Visibility visibility) {
        field.access = Bytecode.setVisibility(field.access, visibility.access);
    }

    public static void setVisibility(ClassNode classNode, int access) {
        classNode.access = Bytecode.setVisibility(classNode.access, access);
    }

    public static void setVisibility(MethodNode method, int access) {
        method.access = Bytecode.setVisibility(method.access, access);
    }

    public static void setVisibility(FieldNode field, int access) {
        field.access = Bytecode.setVisibility(field.access, access);
    }

    private static int setVisibility(int oldAccess, int newAccess) {
        return oldAccess & 0xFFFFFFF8 | newAccess & 7;
    }

    public static int getMaxLineNumber(ClassNode classNode, int min, int pad) {
        int max = 0;
        for (MethodNode method : classNode.methods) {
            ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
            while (iter.hasNext()) {
                AbstractInsnNode insn = (AbstractInsnNode)iter.next();
                if (!(insn instanceof LineNumberNode)) continue;
                max = Math.max(max, ((LineNumberNode)insn).line);
            }
        }
        return Math.max(min, max + pad);
    }

    public static String getBoxingType(Type type) {
        return type == null ? null : BOXING_TYPES[type.getSort()];
    }

    public static String getUnboxingMethod(Type type) {
        return type == null ? null : UNBOXING_METHODS[type.getSort()];
    }

    public static void compareBridgeMethods(MethodNode a2, MethodNode b2) {
        ListIterator<AbstractInsnNode> ia2 = a2.instructions.iterator();
        ListIterator<AbstractInsnNode> ib2 = b2.instructions.iterator();
        int index = 0;
        while (ia2.hasNext() && ib2.hasNext()) {
            AbstractInsnNode na2 = ia2.next();
            AbstractInsnNode nb2 = ib2.next();
            if (!(na2 instanceof LabelNode)) {
                if (na2 instanceof MethodInsnNode) {
                    MethodInsnNode ma2 = (MethodInsnNode)na2;
                    MethodInsnNode mb2 = (MethodInsnNode)nb2;
                    if (!ma2.name.equals(mb2.name)) {
                        throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_INVOKE_NAME, a2.name, a2.desc, index, na2, nb2);
                    }
                    if (!ma2.desc.equals(mb2.desc)) {
                        throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_INVOKE_DESC, a2.name, a2.desc, index, na2, nb2);
                    }
                } else {
                    if (na2.getOpcode() != nb2.getOpcode()) {
                        throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_INSN, a2.name, a2.desc, index, na2, nb2);
                    }
                    if (na2 instanceof VarInsnNode) {
                        VarInsnNode va2 = (VarInsnNode)na2;
                        VarInsnNode vb2 = (VarInsnNode)nb2;
                        if (va2.var != vb2.var) {
                            throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_LOAD, a2.name, a2.desc, index, na2, nb2);
                        }
                    } else if (na2 instanceof TypeInsnNode) {
                        TypeInsnNode ta2 = (TypeInsnNode)na2;
                        TypeInsnNode tb = (TypeInsnNode)nb2;
                        if (ta2.getOpcode() == 192 && !ta2.desc.equals(tb.desc)) {
                            throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_CAST, a2.name, a2.desc, index, na2, nb2);
                        }
                    }
                }
            }
            ++index;
        }
        if (ia2.hasNext() || ib2.hasNext()) {
            throw new SyntheticBridgeException(SyntheticBridgeException.Problem.BAD_LENGTH, a2.name, a2.desc, index, null, null);
        }
    }

    public static void merge(ClassNode source, ClassNode dest) {
        if (source == null) {
            return;
        }
        if (dest == null) {
            throw new NullPointerException("Target ClassNode for merge must not be null");
        }
        dest.version = Math.max(source.version, dest.version);
        dest.interfaces = Bytecode.merge(source.interfaces, dest.interfaces);
        dest.invisibleAnnotations = Bytecode.merge(source.invisibleAnnotations, dest.invisibleAnnotations);
        dest.visibleAnnotations = Bytecode.merge(source.visibleAnnotations, dest.visibleAnnotations);
        dest.visibleTypeAnnotations = Bytecode.merge(source.visibleTypeAnnotations, dest.visibleTypeAnnotations);
        dest.invisibleTypeAnnotations = Bytecode.merge(source.invisibleTypeAnnotations, dest.invisibleTypeAnnotations);
        dest.attrs = Bytecode.merge(source.attrs, dest.attrs);
        dest.innerClasses = Bytecode.merge(source.innerClasses, dest.innerClasses);
        dest.fields = Bytecode.merge(source.fields, dest.fields);
        dest.methods = Bytecode.merge(source.methods, dest.methods);
    }

    public static void replace(ClassNode source, ClassNode dest) {
        if (source == null) {
            return;
        }
        if (dest == null) {
            throw new NullPointerException("Target ClassNode for replace must not be null");
        }
        dest.name = source.name;
        dest.signature = source.signature;
        dest.superName = source.superName;
        dest.version = source.version;
        dest.access = source.access;
        dest.sourceDebug = source.sourceDebug;
        dest.sourceFile = source.sourceFile;
        dest.outerClass = source.outerClass;
        dest.outerMethod = source.outerMethod;
        dest.outerMethodDesc = source.outerMethodDesc;
        Bytecode.clear(dest.interfaces);
        Bytecode.clear(dest.visibleAnnotations);
        Bytecode.clear(dest.invisibleAnnotations);
        Bytecode.clear(dest.visibleTypeAnnotations);
        Bytecode.clear(dest.invisibleTypeAnnotations);
        Bytecode.clear(dest.attrs);
        Bytecode.clear(dest.innerClasses);
        Bytecode.clear(dest.fields);
        Bytecode.clear(dest.methods);
        if (ASM.API_VERSION >= 393216) {
            dest.module = source.module;
        }
        Bytecode.merge(source, dest);
    }

    private static <T> void clear(List<T> list) {
        if (list != null) {
            list.clear();
        }
    }

    private static <T> List<T> merge(List<T> source, List<T> destination) {
        if (source == null || source.isEmpty()) {
            return destination;
        }
        if (destination == null) {
            return new ArrayList<T>(source);
        }
        destination.addAll(source);
        return destination;
    }

    public static class DelegateInitialiser {
        public static final DelegateInitialiser NONE = new DelegateInitialiser(null, false);
        public final MethodInsnNode insn;
        public final boolean isSuper;
        public final boolean isPresent;

        DelegateInitialiser(MethodInsnNode insn, boolean isSuper) {
            this.insn = insn;
            this.isSuper = isSuper;
            this.isPresent = insn != null;
        }

        public String toString() {
            return this.isSuper ? "super" : "this";
        }
    }

    public static enum Visibility {
        PRIVATE(2),
        PROTECTED(4),
        PACKAGE(0),
        PUBLIC(1);

        static final int MASK = 7;
        final int access;

        private Visibility(int access) {
            this.access = access;
        }

        public boolean isAtLeast(Visibility other) {
            return other == null || other.ordinal() <= this.ordinal();
        }

        public boolean isLessThan(Visibility other) {
            return other != null && this.ordinal() < other.ordinal();
        }
    }
}

