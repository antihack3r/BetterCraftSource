/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.invoke.arg;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.transformer.SyntheticClassInfo;
import org.spongepowered.asm.mixin.transformer.ext.IClassGenerator;
import org.spongepowered.asm.service.ISyntheticClassInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.IConsumer;
import org.spongepowered.asm.util.SignaturePrinter;
import org.spongepowered.asm.util.asm.MethodVisitorEx;

public final class ArgsClassGenerator
implements IClassGenerator {
    public static final String ARGS_NAME = Args.class.getName();
    public static final String ARGS_REF = ARGS_NAME.replace('.', '/');
    public static final String GETTER_PREFIX = "$";
    private static final String CLASS_NAME_BASE = "org.spongepowered.asm.synthetic.args.Args$";
    private static final String OBJECT = "java/lang/Object";
    private static final String OBJECT_ARRAY = "[Ljava/lang/Object;";
    private static final String VALUES_FIELD = "values";
    private static final String CTOR_DESC = "([Ljava/lang/Object;)V";
    private static final String SET = "set";
    private static final String SET_DESC = "(ILjava/lang/Object;)V";
    private static final String SETALL = "setAll";
    private static final String SETALL_DESC = "([Ljava/lang/Object;)V";
    private static final String NPE = "java/lang/NullPointerException";
    private static final String NPE_CTOR_DESC = "(Ljava/lang/String;)V";
    private static final String AIOOBE = "org/spongepowered/asm/mixin/injection/invoke/arg/ArgumentIndexOutOfBoundsException";
    private static final String AIOOBE_CTOR_DESC = "(I)V";
    private static final String ACE = "org/spongepowered/asm/mixin/injection/invoke/arg/ArgumentCountException";
    private static final String ACE_CTOR_DESC = "(IILjava/lang/String;)V";
    private static final ILogger logger = MixinService.getService().getLogger("mixin");
    private final IConsumer<ISyntheticClassInfo> registry;
    private int nextIndex = 1;
    private final Map<String, ArgsClassInfo> descToClass = new HashMap<String, ArgsClassInfo>();
    private final Map<String, ArgsClassInfo> nameToClass = new HashMap<String, ArgsClassInfo>();

    public ArgsClassGenerator(IConsumer<ISyntheticClassInfo> registry) {
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "args";
    }

    public ISyntheticClassInfo getArgsClass(String desc, IMixinInfo mixin) {
        String voidDesc = Bytecode.changeDescriptorReturnType(desc, "V");
        ArgsClassInfo info = this.descToClass.get(voidDesc);
        if (info == null) {
            String name = String.format("%s%d", CLASS_NAME_BASE, this.nextIndex++);
            logger.debug("ArgsClassGenerator assigning {} for descriptor {}", name, voidDesc);
            info = new ArgsClassInfo(mixin, name, voidDesc);
            this.descToClass.put(voidDesc, info);
            this.nameToClass.put(name, info);
            this.registry.accept(info);
        }
        return info;
    }

    @Override
    public boolean generate(String name, ClassNode classNode) {
        ArgsClassInfo info = this.nameToClass.get(name);
        if (info == null) {
            return false;
        }
        if (info.loaded > 0) {
            logger.debug("ArgsClassGenerator is re-generating {}, already did this {} times!", name, info.loaded);
        }
        ClassVisitor visitor = classNode;
        if (MixinEnvironment.getCurrentEnvironment().getOption(MixinEnvironment.Option.DEBUG_VERIFY)) {
            visitor = new CheckClassAdapter(classNode);
        }
        visitor.visit(50, 4129, info.getName(), null, ARGS_REF, null);
        visitor.visitSource(name.substring(name.lastIndexOf(46) + 1) + ".java", null);
        this.generateCtor(info, visitor);
        this.generateToString(info, visitor);
        this.generateFactory(info, visitor);
        this.generateSetters(info, visitor);
        this.generateGetters(info, visitor);
        visitor.visitEnd();
        ++info.loaded;
        return true;
    }

    private void generateCtor(ArgsClassInfo info, ClassVisitor writer) {
        MethodVisitor ctor = writer.visitMethod(2, "<init>", "([Ljava/lang/Object;)V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(25, 0);
        ctor.visitVarInsn(25, 1);
        ctor.visitMethodInsn(183, ARGS_REF, "<init>", "([Ljava/lang/Object;)V", false);
        ctor.visitInsn(177);
        ctor.visitMaxs(2, 2);
        ctor.visitEnd();
    }

    private void generateToString(ArgsClassInfo info, ClassVisitor writer) {
        MethodVisitor toString = writer.visitMethod(1, "toString", "()Ljava/lang/String;", null, null);
        toString.visitCode();
        toString.visitLdcInsn("Args" + info.getSignature());
        toString.visitInsn(176);
        toString.visitMaxs(1, 1);
        toString.visitEnd();
    }

    private void generateFactory(ArgsClassInfo info, ClassVisitor writer) {
        String ref = info.getName();
        String factoryDesc = Bytecode.changeDescriptorReturnType(info.desc, "L" + ref + ";");
        MethodVisitorEx of2 = new MethodVisitorEx(writer.visitMethod(9, "of", factoryDesc, null, null));
        of2.visitCode();
        of2.visitTypeInsn(187, ref);
        of2.visitInsn(89);
        of2.visitConstant((byte)info.args.length);
        of2.visitTypeInsn(189, OBJECT);
        int argIndex = 0;
        for (byte index = 0; index < info.args.length; index = (byte)(index + 1)) {
            Type arg2 = info.args[index];
            of2.visitInsn(89);
            of2.visitConstant(index);
            of2.visitVarInsn(arg2.getOpcode(21), argIndex);
            ArgsClassGenerator.box(of2, arg2);
            of2.visitInsn(83);
            argIndex = (byte)(argIndex + arg2.getSize());
        }
        of2.visitMethodInsn(183, ref, "<init>", "([Ljava/lang/Object;)V", false);
        of2.visitInsn(176);
        of2.visitMaxs(6, Bytecode.getArgsSize(info.args));
        of2.visitEnd();
    }

    private void generateGetters(ArgsClassInfo info, ClassVisitor writer) {
        byte argIndex = 0;
        for (Type arg2 : info.args) {
            String name = GETTER_PREFIX + argIndex;
            String sig = "()" + arg2.getDescriptor();
            MethodVisitorEx get = new MethodVisitorEx(writer.visitMethod(1, name, sig, null, null));
            get.visitCode();
            get.visitVarInsn(25, 0);
            get.visitFieldInsn(180, info.getName(), VALUES_FIELD, OBJECT_ARRAY);
            get.visitConstant(argIndex);
            get.visitInsn(50);
            ArgsClassGenerator.unbox(get, arg2);
            get.visitInsn(arg2.getOpcode(172));
            get.visitMaxs(2, 1);
            get.visitEnd();
            argIndex = (byte)(argIndex + 1);
        }
    }

    private void generateSetters(ArgsClassInfo info, ClassVisitor writer) {
        this.generateIndexedSetter(info, writer);
        this.generateMultiSetter(info, writer);
    }

    private void generateIndexedSetter(ArgsClassInfo info, ClassVisitor writer) {
        int index;
        MethodVisitorEx set = new MethodVisitorEx(writer.visitMethod(1, SET, SET_DESC, null, null));
        set.visitCode();
        Label store = new Label();
        Label checkNull = new Label();
        Label[] labels = new Label[info.args.length];
        for (int label = 0; label < labels.length; ++label) {
            labels[label] = new Label();
        }
        set.visitVarInsn(25, 0);
        set.visitFieldInsn(180, info.getName(), VALUES_FIELD, OBJECT_ARRAY);
        for (index = 0; index < info.args.length; index = (byte)(index + 1)) {
            set.visitVarInsn(21, 1);
            set.visitConstant((byte)index);
            set.visitJumpInsn(159, labels[index]);
        }
        ArgsClassGenerator.throwAIOOBE(set, 1);
        for (index = 0; index < info.args.length; ++index) {
            String boxingType = Bytecode.getBoxingType(info.args[index]);
            set.visitLabel(labels[index]);
            set.visitVarInsn(21, 1);
            set.visitVarInsn(25, 2);
            set.visitTypeInsn(192, boxingType != null ? boxingType : info.args[index].getInternalName());
            set.visitJumpInsn(167, boxingType != null ? checkNull : store);
        }
        set.visitLabel(checkNull);
        set.visitInsn(89);
        set.visitJumpInsn(199, store);
        ArgsClassGenerator.throwNPE(set, "Argument with primitive type cannot be set to NULL");
        set.visitLabel(store);
        set.visitInsn(83);
        set.visitInsn(177);
        set.visitMaxs(6, 3);
        set.visitEnd();
    }

    private void generateMultiSetter(ArgsClassInfo info, ClassVisitor writer) {
        MethodVisitorEx set = new MethodVisitorEx(writer.visitMethod(1, SETALL, "([Ljava/lang/Object;)V", null, null));
        set.visitCode();
        Label lengthOk = new Label();
        Label nullPrimitive = new Label();
        int maxStack = 6;
        set.visitVarInsn(25, 1);
        set.visitInsn(190);
        set.visitInsn(89);
        set.visitConstant((byte)info.args.length);
        set.visitJumpInsn(159, lengthOk);
        set.visitTypeInsn(187, ACE);
        set.visitInsn(89);
        set.visitInsn(93);
        set.visitInsn(88);
        set.visitConstant((byte)info.args.length);
        set.visitLdcInsn(info.getSignature());
        set.visitMethodInsn(183, ACE, "<init>", ACE_CTOR_DESC, false);
        set.visitInsn(191);
        set.visitLabel(lengthOk);
        set.visitInsn(87);
        set.visitVarInsn(25, 0);
        set.visitFieldInsn(180, info.getName(), VALUES_FIELD, OBJECT_ARRAY);
        for (byte index = 0; index < info.args.length; index = (byte)(index + 1)) {
            set.visitInsn(89);
            set.visitConstant(index);
            set.visitVarInsn(25, 1);
            set.visitConstant(index);
            set.visitInsn(50);
            String boxingType = Bytecode.getBoxingType(info.args[index]);
            set.visitTypeInsn(192, boxingType != null ? boxingType : info.args[index].getInternalName());
            if (boxingType != null) {
                set.visitInsn(89);
                set.visitJumpInsn(198, nullPrimitive);
                maxStack = 7;
            }
            set.visitInsn(83);
        }
        set.visitInsn(177);
        set.visitLabel(nullPrimitive);
        ArgsClassGenerator.throwNPE(set, "Argument with primitive type cannot be set to NULL");
        set.visitInsn(177);
        set.visitMaxs(maxStack, 2);
        set.visitEnd();
    }

    private static void throwNPE(MethodVisitorEx method, String message) {
        method.visitTypeInsn(187, NPE);
        method.visitInsn(89);
        method.visitLdcInsn(message);
        method.visitMethodInsn(183, NPE, "<init>", NPE_CTOR_DESC, false);
        method.visitInsn(191);
    }

    private static void throwAIOOBE(MethodVisitorEx method, int arg2) {
        method.visitTypeInsn(187, AIOOBE);
        method.visitInsn(89);
        method.visitVarInsn(21, arg2);
        method.visitMethodInsn(183, AIOOBE, "<init>", AIOOBE_CTOR_DESC, false);
        method.visitInsn(191);
    }

    private static void box(MethodVisitor method, Type var) {
        String boxingType = Bytecode.getBoxingType(var);
        if (boxingType != null) {
            String desc = String.format("(%s)L%s;", var.getDescriptor(), boxingType);
            method.visitMethodInsn(184, boxingType, "valueOf", desc, false);
        }
    }

    private static void unbox(MethodVisitor method, Type var) {
        String boxingType = Bytecode.getBoxingType(var);
        if (boxingType != null) {
            String unboxingMethod = Bytecode.getUnboxingMethod(var);
            String desc = "()" + var.getDescriptor();
            method.visitTypeInsn(192, boxingType);
            method.visitMethodInsn(182, boxingType, unboxingMethod, desc, false);
        } else {
            method.visitTypeInsn(192, var.getInternalName());
        }
    }

    class ArgsClassInfo
    extends SyntheticClassInfo {
        final String desc;
        final Type[] args;
        int loaded;

        ArgsClassInfo(IMixinInfo mixin, String name, String desc) {
            super(mixin, name);
            this.loaded = 0;
            this.desc = desc;
            this.args = Type.getArgumentTypes(desc);
        }

        @Override
        public boolean isLoaded() {
            return this.loaded > 0;
        }

        String getSignature() {
            return new SignaturePrinter("", null, this.args).setFullyQualified(true).getFormattedArgs();
        }
    }
}

