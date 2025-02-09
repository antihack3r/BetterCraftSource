/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.asm.ASM;
import org.spongepowered.asm.util.asm.MixinVerifier;
import org.spongepowered.asm.util.throwables.LVTGeneratorError;

public final class Locals {
    private static final String[] FRAME_TYPES = new String[]{"TOP", "INTEGER", "FLOAT", "DOUBLE", "LONG", "NULL", "UNINITIALIZED_THIS"};
    private static final Map<String, List<LocalVariableNode>> calculatedLocalVariables = new HashMap<String, List<LocalVariableNode>>();

    private Locals() {
    }

    public static void loadLocals(Type[] locals, InsnList insns, int pos, int limit) {
        while (pos < locals.length && limit > 0) {
            if (locals[pos] != null) {
                insns.add(new VarInsnNode(locals[pos].getOpcode(21), pos));
                --limit;
            }
            ++pos;
        }
    }

    public static LocalVariableNode[] getLocalsAt(ClassNode classNode, MethodNode method, AbstractInsnNode node) {
        return Locals.getLocalsAt(classNode, method, node, Settings.DEFAULT);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static LocalVariableNode[] getLocalsAt(ClassNode classNode, MethodNode method, AbstractInsnNode node, Settings settings) {
        AbstractInsnNode nextNode;
        for (int i2 = 0; i2 < 3 && (node instanceof LabelNode || node instanceof LineNumberNode) && !((nextNode = Locals.nextNode(method.instructions, node)) instanceof FrameNode); ++i2) {
            node = nextNode;
        }
        ClassInfo classInfo = ClassInfo.forName(classNode.name);
        if (classInfo == null) {
            throw new LVTGeneratorError("Could not load class metadata for " + classNode.name + " generating LVT for " + method.name);
        }
        ClassInfo.Method methodInfo = classInfo.findMethod(method, method.access | 0x40000);
        if (methodInfo == null) {
            throw new LVTGeneratorError("Could not locate method metadata for " + method.name + " generating LVT in " + classNode.name);
        }
        List<ClassInfo.FrameData> frames = methodInfo.getFrames();
        LocalVariableNode[] frame = new LocalVariableNode[method.maxLocals];
        int local = 0;
        int index = 0;
        if ((method.access & 8) == 0) {
            frame[local++] = new LocalVariableNode("this", Type.getObjectType(classNode.name).toString(), null, null, null, 0);
        }
        for (Type argType : Type.getArgumentTypes(method.desc)) {
            frame[local] = new LocalVariableNode("arg" + index++, argType.toString(), null, null, null, local);
            local += argType.getSize();
        }
        int initialFrameSize = local;
        int frameSize = local;
        int frameIndex = -1;
        int lastFrameSize = local;
        int knownFrameSize = local;
        VarInsnNode storeInsn = null;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            for (int l2 = 0; l2 < frame.length; ++l2) {
                if (!(frame[l2] instanceof ZombieLocalVariableNode)) continue;
                ZombieLocalVariableNode zombie = (ZombieLocalVariableNode)frame[l2];
                ++zombie.lifetime;
                if (!(insn instanceof FrameNode)) continue;
                ++zombie.frames;
            }
            if (storeInsn != null) {
                LocalVariableNode storedLocal;
                frame[storeInsn.var] = storedLocal = Locals.getLocalVariableAt(classNode, method, insn, storeInsn.var);
                knownFrameSize = Math.max(knownFrameSize, storeInsn.var + 1);
                if (storedLocal != null && storeInsn.var < method.maxLocals - 1 && storedLocal.desc != null && Type.getType(storedLocal.desc).getSize() == 2) {
                    frame[storeInsn.var + 1] = null;
                    knownFrameSize = Math.max(knownFrameSize, storeInsn.var + 2);
                    if (settings.hasFlags(Settings.RESURRECT_EXPOSED_ON_STORE)) {
                        Locals.resurrect(frame, knownFrameSize, settings);
                    }
                }
                storeInsn = null;
            }
            if (insn instanceof FrameNode) {
                ++frameIndex;
                FrameNode frameNode = (FrameNode)insn;
                if (frameNode.type != 3 && frameNode.type != 4) {
                    int framePos;
                    ClassInfo.FrameData frameData;
                    int frameNodeSize = Locals.computeFrameSize(frameNode, initialFrameSize);
                    ClassInfo.FrameData frameData2 = frameData = frameIndex < frames.size() ? frames.get(frameIndex) : null;
                    if (frameData != null) {
                        if (frameData.type == 0) {
                            lastFrameSize = frameSize = Math.max(initialFrameSize, Math.min(frameNodeSize, frameData.size));
                            knownFrameSize = frameSize;
                        } else {
                            frameSize = Locals.getAdjustedFrameSize(frameSize, frameData, initialFrameSize);
                        }
                    } else {
                        frameSize = Locals.getAdjustedFrameSize(frameSize, frameNode, initialFrameSize);
                    }
                    if (frameSize < initialFrameSize) {
                        throw new IllegalStateException(String.format("Locals entered an invalid state evaluating %s::%s%s at instruction %d (%s). Initial frame size is %d, calculated a frame size of %d with %s", classNode.name, method.name, method.desc, method.instructions.indexOf(insn), Bytecode.describeNode(insn, false), initialFrameSize, frameSize, frameData));
                    }
                    if (frameData == null && (frameNode.type == 2 || frameNode.type == -1) || frameData != null && frameData.type == 2) {
                        for (framePos = frameSize; framePos < frame.length; ++framePos) {
                            frame[framePos] = ZombieLocalVariableNode.of(frame[framePos], 'C');
                        }
                        knownFrameSize = lastFrameSize = frameSize;
                    } else {
                        framePos = frameNode.type == 1 ? lastFrameSize : 0;
                        lastFrameSize = frameSize;
                        int localPos = 0;
                        while (framePos < frame.length) {
                            Object localType;
                            Object object = localType = localPos < frameNode.local.size() ? frameNode.local.get(localPos) : null;
                            if (localType instanceof String) {
                                frame[framePos] = Locals.getLocalVariableAt(classNode, method, insn, framePos);
                            } else if (localType instanceof Integer) {
                                boolean is64bitValue;
                                boolean isMarkerType = localType == Opcodes.UNINITIALIZED_THIS || localType == Opcodes.NULL;
                                boolean is32bitValue = localType == Opcodes.INTEGER || localType == Opcodes.FLOAT;
                                boolean bl2 = is64bitValue = localType == Opcodes.DOUBLE || localType == Opcodes.LONG;
                                if (localType == Opcodes.TOP) {
                                    if (frame[framePos] instanceof ZombieLocalVariableNode && settings.hasFlags(Settings.RESURRECT_FOR_BOGUS_TOP)) {
                                        ZombieLocalVariableNode zombie = (ZombieLocalVariableNode)frame[framePos];
                                        if (zombie.type == 'X') {
                                            frame[framePos] = zombie.ancestor;
                                        }
                                    }
                                } else if (isMarkerType) {
                                    frame[framePos] = null;
                                } else {
                                    if (!is32bitValue && !is64bitValue) throw new LVTGeneratorError("Unrecognised locals opcode " + localType + " in locals array at position " + localPos + " in " + classNode.name + "." + method.name + method.desc);
                                    frame[framePos] = Locals.getLocalVariableAt(classNode, method, insn, framePos);
                                    if (is64bitValue) {
                                        frame[++framePos] = null;
                                    }
                                }
                            } else if (localType == null) {
                                if (framePos >= initialFrameSize && framePos >= frameSize && frameSize > 0) {
                                    frame[framePos] = framePos < knownFrameSize ? Locals.getLocalVariableAt(classNode, method, insn, framePos) : ZombieLocalVariableNode.of(frame[framePos], 'X');
                                }
                            } else if (!(localType instanceof LabelNode)) {
                                throw new LVTGeneratorError("Invalid value " + localType + " in locals array at position " + localPos + " in " + classNode.name + "." + method.name + method.desc);
                            }
                            ++framePos;
                            ++localPos;
                        }
                    }
                }
            } else if (insn instanceof VarInsnNode) {
                boolean isLoad;
                VarInsnNode varInsn = (VarInsnNode)insn;
                boolean bl3 = isLoad = insn.getOpcode() >= 21 && insn.getOpcode() <= 53;
                if (isLoad) {
                    frame[varInsn.var] = Locals.getLocalVariableAt(classNode, method, insn, varInsn.var);
                    int varSize = frame[varInsn.var].desc != null ? Type.getType(frame[varInsn.var].desc).getSize() : 1;
                    knownFrameSize = Math.max(knownFrameSize, varInsn.var + varSize);
                    if (settings.hasFlags(Settings.RESURRECT_EXPOSED_ON_LOAD)) {
                        Locals.resurrect(frame, knownFrameSize, settings);
                    }
                } else {
                    storeInsn = varInsn;
                }
            }
            if (insn != node) continue;
            break;
        }
        for (int l3 = 0; l3 < frame.length; ++l3) {
            if (frame[l3] instanceof ZombieLocalVariableNode) {
                ZombieLocalVariableNode zombie = (ZombieLocalVariableNode)frame[l3];
                LocalVariableNode localVariableNode = frame[l3] = zombie.lifetime > 1 ? null : zombie.ancestor;
            }
            if ((frame[l3] == null || frame[l3].desc != null) && !(frame[l3] instanceof SyntheticLocalVariableNode)) continue;
            frame[l3] = null;
        }
        return frame;
    }

    private static void resurrect(LocalVariableNode[] frame, int knownFrameSize, Settings settings) {
        for (int l2 = 0; l2 < knownFrameSize && l2 < frame.length; ++l2) {
            ZombieLocalVariableNode zombie;
            if (!(frame[l2] instanceof ZombieLocalVariableNode) || !(zombie = (ZombieLocalVariableNode)frame[l2]).checkResurrect(settings)) continue;
            frame[l2] = zombie.ancestor;
        }
    }

    public static LocalVariableNode getLocalVariableAt(ClassNode classNode, MethodNode method, AbstractInsnNode node, int var) {
        return Locals.getLocalVariableAt(classNode, method, method.instructions.indexOf(node), var);
    }

    private static LocalVariableNode getLocalVariableAt(ClassNode classNode, MethodNode method, int pos, int var) {
        LocalVariableNode localVariableNode = null;
        LocalVariableNode fallbackNode = null;
        for (LocalVariableNode local : Locals.getLocalVariableTable(classNode, method)) {
            if (local.index != var) continue;
            if (Locals.isOpcodeInRange(method.instructions, local, pos)) {
                localVariableNode = local;
                continue;
            }
            if (localVariableNode != null) continue;
            fallbackNode = local;
        }
        if (localVariableNode == null && !method.localVariables.isEmpty()) {
            for (LocalVariableNode local : Locals.getGeneratedLocalVariableTable(classNode, method)) {
                if (local.index != var || !Locals.isOpcodeInRange(method.instructions, local, pos)) continue;
                localVariableNode = local;
            }
        }
        return localVariableNode != null ? localVariableNode : fallbackNode;
    }

    private static boolean isOpcodeInRange(InsnList insns, LocalVariableNode local, int pos) {
        return insns.indexOf(local.start) <= pos && insns.indexOf(local.end) > pos;
    }

    public static List<LocalVariableNode> getLocalVariableTable(ClassNode classNode, MethodNode method) {
        if (method.localVariables.isEmpty()) {
            return Locals.getGeneratedLocalVariableTable(classNode, method);
        }
        return Collections.unmodifiableList(method.localVariables);
    }

    public static List<LocalVariableNode> getGeneratedLocalVariableTable(ClassNode classNode, MethodNode method) {
        String methodId = String.format("%s.%s%s", classNode.name, method.name, method.desc);
        List<LocalVariableNode> localVars = calculatedLocalVariables.get(methodId);
        if (localVars != null) {
            return localVars;
        }
        localVars = Locals.generateLocalVariableTable(classNode, method);
        calculatedLocalVariables.put(methodId, localVars);
        return Collections.unmodifiableList(localVars);
    }

    public static List<LocalVariableNode> generateLocalVariableTable(ClassNode classNode, MethodNode method) {
        ArrayList<Type> interfaces = null;
        if (classNode.interfaces != null) {
            interfaces = new ArrayList<Type>();
            for (String iface : classNode.interfaces) {
                interfaces.add(Type.getObjectType(iface));
            }
        }
        Type objectType = null;
        if (classNode.superName != null) {
            objectType = Type.getObjectType(classNode.superName);
        }
        Analyzer analyzer = new Analyzer(new MixinVerifier(ASM.API_VERSION, Type.getObjectType(classNode.name), objectType, interfaces, false));
        try {
            analyzer.analyze(classNode.name, method);
        }
        catch (AnalyzerException ex2) {
            ex2.printStackTrace();
        }
        Frame[] frames = analyzer.getFrames();
        int methodSize = method.instructions.size();
        ArrayList<LocalVariableNode> localVariables = new ArrayList<LocalVariableNode>();
        LocalVariableNode[] localNodes = new LocalVariableNode[method.maxLocals];
        BasicValue[] locals = new BasicValue[method.maxLocals];
        LabelNode[] labels = new LabelNode[methodSize];
        String[] lastKnownType = new String[method.maxLocals];
        for (int i2 = 0; i2 < methodSize; ++i2) {
            Frame f2 = frames[i2];
            if (f2 == null) continue;
            LabelNode label = null;
            for (int j2 = 0; j2 < f2.getLocals(); ++j2) {
                BasicValue local = (BasicValue)f2.getLocal(j2);
                if (local == null && locals[j2] == null || local != null && local.equals(locals[j2])) continue;
                if (label == null) {
                    AbstractInsnNode existingLabel = method.instructions.get(i2);
                    if (existingLabel instanceof LabelNode) {
                        label = (LabelNode)existingLabel;
                    } else {
                        labels[i2] = label = new LabelNode();
                    }
                }
                if (local == null && locals[j2] != null) {
                    localVariables.add(localNodes[j2]);
                    localNodes[j2].end = label;
                    localNodes[j2] = null;
                } else if (local != null) {
                    if (locals[j2] != null) {
                        localVariables.add(localNodes[j2]);
                        localNodes[j2].end = label;
                        localNodes[j2] = null;
                    }
                    String desc = lastKnownType[j2];
                    Type localType = local.getType();
                    if (localType != null) {
                        desc = localType.getSort() >= 9 && "null".equals(localType.getInternalName()) ? "Ljava/lang/Object;" : localType.getDescriptor();
                    }
                    localNodes[j2] = new LocalVariableNode("var" + j2, desc, null, label, null, j2);
                    if (desc != null) {
                        lastKnownType[j2] = desc;
                    }
                }
                locals[j2] = local;
            }
        }
        LabelNode label = null;
        for (int k2 = 0; k2 < localNodes.length; ++k2) {
            if (localNodes[k2] == null) continue;
            if (label == null) {
                label = new LabelNode();
                method.instructions.add(label);
            }
            localNodes[k2].end = label;
            localVariables.add(localNodes[k2]);
        }
        for (int n2 = methodSize - 1; n2 >= 0; --n2) {
            if (labels[n2] == null) continue;
            method.instructions.insert(method.instructions.get(n2), labels[n2]);
        }
        return localVariables;
    }

    private static AbstractInsnNode nextNode(InsnList insns, AbstractInsnNode insn) {
        int index = insns.indexOf(insn) + 1;
        if (index > 0 && index < insns.size()) {
            return insns.get(index);
        }
        return insn;
    }

    private static int getAdjustedFrameSize(int currentSize, FrameNode frameNode, int initialFrameSize) {
        return Locals.getAdjustedFrameSize(currentSize, frameNode.type, Locals.computeFrameSize(frameNode, initialFrameSize), initialFrameSize);
    }

    private static int getAdjustedFrameSize(int currentSize, ClassInfo.FrameData frameData, int initialFrameSize) {
        return Locals.getAdjustedFrameSize(currentSize, frameData.type, frameData.size, initialFrameSize);
    }

    private static int getAdjustedFrameSize(int currentSize, int type, int size, int initialFrameSize) {
        switch (type) {
            case -1: 
            case 0: {
                return Math.max(initialFrameSize, size);
            }
            case 1: {
                return currentSize + size;
            }
            case 2: {
                return Math.max(initialFrameSize, currentSize - size);
            }
            case 3: 
            case 4: {
                return currentSize;
            }
        }
        return currentSize;
    }

    public static int computeFrameSize(FrameNode frameNode, int initialFrameSize) {
        if (frameNode.local == null) {
            return initialFrameSize;
        }
        int size = 0;
        for (Object local : frameNode.local) {
            if (local instanceof Integer) {
                size += local == Opcodes.DOUBLE || local == Opcodes.LONG ? 2 : 1;
                continue;
            }
            ++size;
        }
        return Math.max(initialFrameSize, size);
    }

    public static String getFrameTypeName(Object frameEntry) {
        if (frameEntry == null) {
            return "NULL";
        }
        if (frameEntry instanceof String) {
            return Bytecode.getSimpleName(frameEntry.toString());
        }
        if (frameEntry instanceof Integer) {
            int type = (Integer)frameEntry;
            if (type >= FRAME_TYPES.length) {
                return "INVALID";
            }
            return FRAME_TYPES[type];
        }
        return "?";
    }

    public static class Settings {
        public static int RESURRECT_FOR_BOGUS_TOP = 1;
        public static int RESURRECT_EXPOSED_ON_LOAD = 2;
        public static int RESURRECT_EXPOSED_ON_STORE = 4;
        public static int DEFAULT_FLAGS = RESURRECT_FOR_BOGUS_TOP | RESURRECT_EXPOSED_ON_LOAD | RESURRECT_EXPOSED_ON_STORE;
        public static Settings DEFAULT = new Settings(DEFAULT_FLAGS, 0, -1, 1, -1, -1);
        final int flags;
        final int flagsCustom;
        final int choppedInsnThreshold;
        final int choppedFrameThreshold;
        final int trimmedInsnThreshold;
        final int trimmedFrameThreshold;

        public Settings(int flags, int flagsCustom, int insnThreshold, int frameThreshold) {
            this(flags, flagsCustom, insnThreshold, frameThreshold, insnThreshold, frameThreshold);
        }

        public Settings(int flags, int flagsCustom, int choppedInsnThreshold, int choppedFrameThreshold, int trimmedInsnThreshold, int trimmedFrameThreshold) {
            this.flags = flags;
            this.flagsCustom = flagsCustom;
            this.choppedInsnThreshold = choppedInsnThreshold;
            this.choppedFrameThreshold = choppedFrameThreshold;
            this.trimmedInsnThreshold = trimmedInsnThreshold;
            this.trimmedFrameThreshold = trimmedFrameThreshold;
        }

        boolean hasFlags(int flags) {
            return (this.flags & flags) == flags;
        }

        boolean hasCustomFlags(int flagsCustom) {
            return (this.flagsCustom & flagsCustom) == flagsCustom;
        }
    }

    static class ZombieLocalVariableNode
    extends LocalVariableNode {
        static final char CHOP = 'C';
        static final char TRIM = 'X';
        final LocalVariableNode ancestor;
        final char type;
        int lifetime;
        int frames;

        ZombieLocalVariableNode(LocalVariableNode ancestor, char type) {
            super(ancestor.name, ancestor.desc, ancestor.signature, ancestor.start, ancestor.end, ancestor.index);
            this.ancestor = ancestor;
            this.type = type;
        }

        boolean checkResurrect(Settings settings) {
            int insnThreshold;
            int n2 = insnThreshold = this.type == 'C' ? settings.choppedInsnThreshold : settings.trimmedInsnThreshold;
            if (insnThreshold > -1 && this.lifetime > insnThreshold) {
                return false;
            }
            int frameThreshold = this.type == 'C' ? settings.choppedFrameThreshold : settings.trimmedFrameThreshold;
            return frameThreshold == -1 || this.frames <= frameThreshold;
        }

        static ZombieLocalVariableNode of(LocalVariableNode ancestor, char type) {
            if (ancestor instanceof ZombieLocalVariableNode) {
                return (ZombieLocalVariableNode)ancestor;
            }
            return ancestor != null ? new ZombieLocalVariableNode(ancestor, type) : null;
        }

        public String toString() {
            return String.format("Z(%s,%-2d)", Character.valueOf(this.type), this.lifetime);
        }
    }

    public static class SyntheticLocalVariableNode
    extends LocalVariableNode {
        public SyntheticLocalVariableNode(String name, String descriptor, String signature, LabelNode start, LabelNode end, int index) {
            super(name, descriptor, signature, start, end, index);
        }
    }
}

