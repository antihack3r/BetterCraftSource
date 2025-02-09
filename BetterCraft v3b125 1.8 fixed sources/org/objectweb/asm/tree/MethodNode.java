/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableAnnotationNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.UnsupportedClassVersionException;
import org.objectweb.asm.tree.Util;
import org.objectweb.asm.tree.VarInsnNode;

public class MethodNode
extends MethodVisitor {
    public int access;
    public String name;
    public String desc;
    public String signature;
    public List<String> exceptions;
    public List<ParameterNode> parameters;
    public List<AnnotationNode> visibleAnnotations;
    public List<AnnotationNode> invisibleAnnotations;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;
    public List<Attribute> attrs;
    public Object annotationDefault;
    public int visibleAnnotableParameterCount;
    public List<AnnotationNode>[] visibleParameterAnnotations;
    public int invisibleAnnotableParameterCount;
    public List<AnnotationNode>[] invisibleParameterAnnotations;
    public InsnList instructions;
    public List<TryCatchBlockNode> tryCatchBlocks;
    public int maxStack;
    public int maxLocals;
    public List<LocalVariableNode> localVariables;
    public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;
    public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;
    private boolean visited;

    public MethodNode() {
        this(458752);
        if (this.getClass() != MethodNode.class) {
            throw new IllegalStateException();
        }
    }

    public MethodNode(int api2) {
        super(api2);
        this.instructions = new InsnList();
    }

    public MethodNode(int access, String name, String descriptor, String signature, String[] exceptions) {
        this(458752, access, name, descriptor, signature, exceptions);
        if (this.getClass() != MethodNode.class) {
            throw new IllegalStateException();
        }
    }

    public MethodNode(int api2, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api2);
        this.access = access;
        this.name = name;
        this.desc = descriptor;
        this.signature = signature;
        this.exceptions = Util.asArrayList(exceptions);
        if ((access & 0x400) == 0) {
            this.localVariables = new ArrayList<LocalVariableNode>(5);
        }
        this.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
        this.instructions = new InsnList();
    }

    @Override
    public void visitParameter(String name, int access) {
        if (this.parameters == null) {
            this.parameters = new ArrayList<ParameterNode>(5);
        }
        this.parameters.add(new ParameterNode(name, access));
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationNode((List<Object>)new ArrayList<Object>(0){

            @Override
            public boolean add(Object o2) {
                MethodNode.this.annotationDefault = o2;
                return super.add(o2);
            }
        });
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.visibleAnnotations.add(annotation);
        } else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            this.invisibleAnnotations.add(annotation);
        }
        return annotation;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            if (this.visibleTypeAnnotations == null) {
                this.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.visibleTypeAnnotations.add(typeAnnotation);
        } else {
            if (this.invisibleTypeAnnotations == null) {
                this.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            this.invisibleTypeAnnotations.add(typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
        if (visible) {
            this.visibleAnnotableParameterCount = parameterCount;
        } else {
            this.invisibleAnnotableParameterCount = parameterCount;
        }
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            if (this.visibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                this.visibleParameterAnnotations = new List[params];
            }
            if (this.visibleParameterAnnotations[parameter] == null) {
                this.visibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
            }
            this.visibleParameterAnnotations[parameter].add(annotation);
        } else {
            if (this.invisibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                this.invisibleParameterAnnotations = new List[params];
            }
            if (this.invisibleParameterAnnotations[parameter] == null) {
                this.invisibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
            }
            this.invisibleParameterAnnotations[parameter].add(annotation);
        }
        return annotation;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        if (this.attrs == null) {
            this.attrs = new ArrayList<Attribute>(1);
        }
        this.attrs.add(attribute);
    }

    @Override
    public void visitCode() {
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        this.instructions.add(new FrameNode(type, numLocal, local == null ? null : this.getLabelNodes(local), numStack, stack == null ? null : this.getLabelNodes(stack)));
    }

    @Override
    public void visitInsn(int opcode) {
        this.instructions.add(new InsnNode(opcode));
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        this.instructions.add(new IntInsnNode(opcode, operand));
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        this.instructions.add(new VarInsnNode(opcode, var));
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        this.instructions.add(new TypeInsnNode(opcode, type));
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        this.instructions.add(new FieldInsnNode(opcode, owner, name, descriptor));
    }

    @Override
    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        if (this.api >= 327680) {
            super.visitMethodInsn(opcode, owner, name, descriptor);
            return;
        }
        this.instructions.add(new MethodInsnNode(opcode, owner, name, descriptor));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (this.api < 327680) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            return;
        }
        this.instructions.add(new MethodInsnNode(opcode, owner, name, descriptor, isInterface));
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object ... bootstrapMethodArguments) {
        this.instructions.add(new InvokeDynamicInsnNode(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments));
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        this.instructions.add(new JumpInsnNode(opcode, this.getLabelNode(label)));
    }

    @Override
    public void visitLabel(Label label) {
        this.instructions.add(this.getLabelNode(label));
    }

    @Override
    public void visitLdcInsn(Object value) {
        this.instructions.add(new LdcInsnNode(value));
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        this.instructions.add(new IincInsnNode(var, increment));
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.instructions.add(new TableSwitchInsnNode(min, max, this.getLabelNode(dflt), this.getLabelNodes(labels)));
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.instructions.add(new LookupSwitchInsnNode(this.getLabelNode(dflt), keys, this.getLabelNodes(labels)));
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        this.instructions.add(new MultiANewArrayInsnNode(descriptor, numDimensions));
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AbstractInsnNode currentInsn = this.instructions.getLast();
        while (currentInsn.getOpcode() == -1) {
            currentInsn = currentInsn.getPrevious();
        }
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            if (currentInsn.visibleTypeAnnotations == null) {
                currentInsn.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            currentInsn.visibleTypeAnnotations.add(typeAnnotation);
        } else {
            if (currentInsn.invisibleTypeAnnotations == null) {
                currentInsn.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            currentInsn.invisibleTypeAnnotations.add(typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.tryCatchBlocks.add(new TryCatchBlockNode(this.getLabelNode(start), this.getLabelNode(end), this.getLabelNode(handler), type));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        TryCatchBlockNode tryCatchBlock = this.tryCatchBlocks.get((typeRef & 0xFFFF00) >> 8);
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            if (tryCatchBlock.visibleTypeAnnotations == null) {
                tryCatchBlock.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            tryCatchBlock.visibleTypeAnnotations.add(typeAnnotation);
        } else {
            if (tryCatchBlock.invisibleTypeAnnotations == null) {
                tryCatchBlock.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            tryCatchBlock.invisibleTypeAnnotations.add(typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        this.localVariables.add(new LocalVariableNode(name, descriptor, signature, this.getLabelNode(start), this.getLabelNode(end), index));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        LocalVariableAnnotationNode localVariableAnnotation = new LocalVariableAnnotationNode(typeRef, typePath, this.getLabelNodes(start), this.getLabelNodes(end), index, descriptor);
        if (visible) {
            if (this.visibleLocalVariableAnnotations == null) {
                this.visibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(1);
            }
            this.visibleLocalVariableAnnotations.add(localVariableAnnotation);
        } else {
            if (this.invisibleLocalVariableAnnotations == null) {
                this.invisibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(1);
            }
            this.invisibleLocalVariableAnnotations.add(localVariableAnnotation);
        }
        return localVariableAnnotation;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.instructions.add(new LineNumberNode(line, this.getLabelNode(start)));
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }

    @Override
    public void visitEnd() {
    }

    protected LabelNode getLabelNode(Label label) {
        if (!(label.info instanceof LabelNode)) {
            label.info = new LabelNode();
        }
        return (LabelNode)label.info;
    }

    private LabelNode[] getLabelNodes(Label[] labels) {
        LabelNode[] labelNodes = new LabelNode[labels.length];
        int i2 = 0;
        int n2 = labels.length;
        while (i2 < n2) {
            labelNodes[i2] = this.getLabelNode(labels[i2]);
            ++i2;
        }
        return labelNodes;
    }

    private Object[] getLabelNodes(Object[] objects) {
        Object[] labelNodes = new Object[objects.length];
        int i2 = 0;
        int n2 = objects.length;
        while (i2 < n2) {
            Object o2 = objects[i2];
            if (o2 instanceof Label) {
                o2 = this.getLabelNode((Label)o2);
            }
            labelNodes[i2] = o2;
            ++i2;
        }
        return labelNodes;
    }

    public void check(int api2) {
        AbstractInsnNode insn;
        int i2;
        if (api2 == 262144) {
            if (this.parameters != null && !this.parameters.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.visibleTypeAnnotations != null && !this.visibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.invisibleTypeAnnotations != null && !this.invisibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.tryCatchBlocks != null) {
                i2 = this.tryCatchBlocks.size() - 1;
                while (i2 >= 0) {
                    TryCatchBlockNode tryCatchBlock = this.tryCatchBlocks.get(i2);
                    if (tryCatchBlock.visibleTypeAnnotations != null && !tryCatchBlock.visibleTypeAnnotations.isEmpty()) {
                        throw new UnsupportedClassVersionException();
                    }
                    if (tryCatchBlock.invisibleTypeAnnotations != null && !tryCatchBlock.invisibleTypeAnnotations.isEmpty()) {
                        throw new UnsupportedClassVersionException();
                    }
                    --i2;
                }
            }
            i2 = this.instructions.size() - 1;
            while (i2 >= 0) {
                Object value;
                boolean isInterface;
                insn = this.instructions.get(i2);
                if (insn.visibleTypeAnnotations != null && !insn.visibleTypeAnnotations.isEmpty()) {
                    throw new UnsupportedClassVersionException();
                }
                if (insn.invisibleTypeAnnotations != null && !insn.invisibleTypeAnnotations.isEmpty()) {
                    throw new UnsupportedClassVersionException();
                }
                if (insn instanceof MethodInsnNode ? (isInterface = ((MethodInsnNode)insn).itf) != (insn.opcode == 185) : insn instanceof LdcInsnNode && ((value = ((LdcInsnNode)insn).cst) instanceof Handle || value instanceof Type && ((Type)value).getSort() == 11)) {
                    throw new UnsupportedClassVersionException();
                }
                --i2;
            }
            if (this.visibleLocalVariableAnnotations != null && !this.visibleLocalVariableAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (this.invisibleLocalVariableAnnotations != null && !this.invisibleLocalVariableAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
        }
        if (api2 != 458752) {
            i2 = this.instructions.size() - 1;
            while (i2 >= 0) {
                Object value;
                insn = this.instructions.get(i2);
                if (insn instanceof LdcInsnNode && (value = ((LdcInsnNode)insn).cst) instanceof ConstantDynamic) {
                    throw new UnsupportedClassVersionException();
                }
                --i2;
            }
        }
    }

    public void accept(ClassVisitor classVisitor) {
        String[] exceptionsArray = new String[this.exceptions.size()];
        this.exceptions.toArray(exceptionsArray);
        MethodVisitor methodVisitor = classVisitor.visitMethod(this.access, this.name, this.desc, this.signature, exceptionsArray);
        if (methodVisitor != null) {
            this.accept(methodVisitor);
        }
    }

    public void accept(MethodVisitor methodVisitor) {
        AnnotationNode annotation;
        int m2;
        int j2;
        List<AnnotationNode> parameterAnnotations;
        TypeAnnotationNode typeAnnotation;
        AnnotationNode annotation2;
        int n2;
        if (this.parameters != null) {
            int i2 = 0;
            n2 = this.parameters.size();
            while (i2 < n2) {
                this.parameters.get(i2).accept(methodVisitor);
                ++i2;
            }
        }
        if (this.annotationDefault != null) {
            AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
            AnnotationNode.accept(annotationVisitor, null, this.annotationDefault);
            if (annotationVisitor != null) {
                annotationVisitor.visitEnd();
            }
        }
        if (this.visibleAnnotations != null) {
            int i3 = 0;
            n2 = this.visibleAnnotations.size();
            while (i3 < n2) {
                annotation2 = this.visibleAnnotations.get(i3);
                annotation2.accept(methodVisitor.visitAnnotation(annotation2.desc, true));
                ++i3;
            }
        }
        if (this.invisibleAnnotations != null) {
            int i4 = 0;
            n2 = this.invisibleAnnotations.size();
            while (i4 < n2) {
                annotation2 = this.invisibleAnnotations.get(i4);
                annotation2.accept(methodVisitor.visitAnnotation(annotation2.desc, false));
                ++i4;
            }
        }
        if (this.visibleTypeAnnotations != null) {
            int i5 = 0;
            n2 = this.visibleTypeAnnotations.size();
            while (i5 < n2) {
                typeAnnotation = this.visibleTypeAnnotations.get(i5);
                typeAnnotation.accept(methodVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
                ++i5;
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            int i6 = 0;
            n2 = this.invisibleTypeAnnotations.size();
            while (i6 < n2) {
                typeAnnotation = this.invisibleTypeAnnotations.get(i6);
                typeAnnotation.accept(methodVisitor.visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
                ++i6;
            }
        }
        if (this.visibleAnnotableParameterCount > 0) {
            methodVisitor.visitAnnotableParameterCount(this.visibleAnnotableParameterCount, true);
        }
        if (this.visibleParameterAnnotations != null) {
            int i7 = 0;
            n2 = this.visibleParameterAnnotations.length;
            while (i7 < n2) {
                parameterAnnotations = this.visibleParameterAnnotations[i7];
                if (parameterAnnotations != null) {
                    j2 = 0;
                    m2 = parameterAnnotations.size();
                    while (j2 < m2) {
                        annotation = parameterAnnotations.get(j2);
                        annotation.accept(methodVisitor.visitParameterAnnotation(i7, annotation.desc, true));
                        ++j2;
                    }
                }
                ++i7;
            }
        }
        if (this.invisibleAnnotableParameterCount > 0) {
            methodVisitor.visitAnnotableParameterCount(this.invisibleAnnotableParameterCount, false);
        }
        if (this.invisibleParameterAnnotations != null) {
            int i8 = 0;
            n2 = this.invisibleParameterAnnotations.length;
            while (i8 < n2) {
                parameterAnnotations = this.invisibleParameterAnnotations[i8];
                if (parameterAnnotations != null) {
                    j2 = 0;
                    m2 = parameterAnnotations.size();
                    while (j2 < m2) {
                        annotation = parameterAnnotations.get(j2);
                        annotation.accept(methodVisitor.visitParameterAnnotation(i8, annotation.desc, false));
                        ++j2;
                    }
                }
                ++i8;
            }
        }
        if (this.visited) {
            this.instructions.resetLabels();
        }
        if (this.attrs != null) {
            int i9 = 0;
            n2 = this.attrs.size();
            while (i9 < n2) {
                methodVisitor.visitAttribute(this.attrs.get(i9));
                ++i9;
            }
        }
        if (this.instructions.size() > 0) {
            methodVisitor.visitCode();
            if (this.tryCatchBlocks != null) {
                int i10 = 0;
                n2 = this.tryCatchBlocks.size();
                while (i10 < n2) {
                    this.tryCatchBlocks.get(i10).updateIndex(i10);
                    this.tryCatchBlocks.get(i10).accept(methodVisitor);
                    ++i10;
                }
            }
            this.instructions.accept(methodVisitor);
            if (this.localVariables != null) {
                int i11 = 0;
                n2 = this.localVariables.size();
                while (i11 < n2) {
                    this.localVariables.get(i11).accept(methodVisitor);
                    ++i11;
                }
            }
            if (this.visibleLocalVariableAnnotations != null) {
                int i12 = 0;
                n2 = this.visibleLocalVariableAnnotations.size();
                while (i12 < n2) {
                    this.visibleLocalVariableAnnotations.get(i12).accept(methodVisitor, true);
                    ++i12;
                }
            }
            if (this.invisibleLocalVariableAnnotations != null) {
                int i13 = 0;
                n2 = this.invisibleLocalVariableAnnotations.size();
                while (i13 < n2) {
                    this.invisibleLocalVariableAnnotations.get(i13).accept(methodVisitor, false);
                    ++i13;
                }
            }
            methodVisitor.visitMaxs(this.maxStack, this.maxLocals);
            this.visited = true;
        }
        methodVisitor.visitEnd();
    }
}

