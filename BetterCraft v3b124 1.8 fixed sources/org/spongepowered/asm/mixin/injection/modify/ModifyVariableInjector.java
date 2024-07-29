/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.modify;

import java.util.Collection;
import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.code.InjectorTarget;
import org.spongepowered.asm.mixin.injection.modify.InvalidImplicitDiscriminatorException;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.throwables.InjectionError;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.PrettyPrinter;
import org.spongepowered.asm.util.SignaturePrinter;

public class ModifyVariableInjector
extends Injector {
    private final LocalVariableDiscriminator discriminator;

    public ModifyVariableInjector(InjectionInfo info, LocalVariableDiscriminator discriminator) {
        super(info, "@ModifyVariable");
        this.discriminator = discriminator;
    }

    @Override
    protected boolean findTargetNodes(MethodNode into, InjectionPoint injectionPoint, InjectorTarget injectorTarget, Collection<AbstractInsnNode> nodes) {
        if (injectionPoint instanceof LocalVariableInjectionPoint) {
            return ((LocalVariableInjectionPoint)injectionPoint).find(this.info, injectorTarget.getSlice(injectionPoint), nodes, injectorTarget.getTarget());
        }
        return injectionPoint.find(into.desc, injectorTarget.getSlice(injectionPoint), nodes);
    }

    @Override
    protected void sanityCheck(Target target, List<InjectionPoint> injectionPoints) {
        super.sanityCheck(target, injectionPoints);
        int ordinal = this.discriminator.getOrdinal();
        if (ordinal < -1) {
            throw new InvalidInjectionException((ISelectorContext)this.info, "Invalid ordinal " + ordinal + " specified in " + this);
        }
        if (this.discriminator.getIndex() == 0 && !target.isStatic) {
            throw new InvalidInjectionException((ISelectorContext)this.info, "Invalid index 0 specified in non-static variable modifier " + this);
        }
    }

    protected String getTargetNodeKey(Target target, InjectionNodes.InjectionNode node) {
        return String.format("localcontext(%s,%s,#%s)", this.returnType, this.discriminator.isArgsOnly() ? "argsOnly" : "fullFrame", node.getId());
    }

    @Override
    protected void preInject(Target target, InjectionNodes.InjectionNode node) {
        String key = this.getTargetNodeKey(target, node);
        if (node.hasDecoration(key)) {
            return;
        }
        Context context = new Context(this.info, this.returnType, this.discriminator.isArgsOnly(), target, node.getCurrentTarget());
        node.decorate(key, context);
    }

    @Override
    protected void inject(Target target, InjectionNodes.InjectionNode node) {
        if (node.isReplaced()) {
            throw new InvalidInjectionException((ISelectorContext)this.info, "Variable modifier target for " + this + " was removed by another injector");
        }
        Context context = (Context)node.getDecoration(this.getTargetNodeKey(target, node));
        if (context == null) {
            throw new InjectionError(String.format("%s injector target is missing CONTEXT decoration for %s. PreInjection failure or illegal internal state change", this.annotationType, this.info));
        }
        if (context.insns.size() > 0) {
            throw new InjectionError(String.format("%s injector target has contaminated CONTEXT decoration for %s. Check for previous errors.", this.annotationType, this.info));
        }
        if (this.discriminator.printLVT()) {
            this.printLocals(target, context);
        }
        this.checkTargetForNode(target, node, InjectionPoint.RestrictTargetLevel.ALLOW_ALL);
        Injector.InjectorData handler = new Injector.InjectorData(target, "handler", false);
        if (this.returnType == Type.VOID_TYPE) {
            throw new InvalidInjectionException((ISelectorContext)this.info, String.format("%s %s method %s from %s has an invalid signature, cannot return a VOID type.", this.annotationType, handler, this, this.info.getMixin()));
        }
        this.validateParams(handler, this.returnType, this.returnType);
        Target.Extension extraStack = target.extendStack();
        try {
            int local = this.discriminator.findLocal(context);
            if (local > -1) {
                this.inject(context, handler, extraStack, local);
            }
        }
        catch (InvalidImplicitDiscriminatorException ex2) {
            if (this.discriminator.printLVT()) {
                this.info.addCallbackInvocation(this.methodNode);
                return;
            }
            throw new InvalidInjectionException((ISelectorContext)this.info, "Implicit variable modifier injection failed in " + this, (Throwable)ex2);
        }
        extraStack.apply();
        target.insns.insertBefore(context.node, context.insns);
    }

    private void printLocals(Target target, Context context) {
        String matchMode = "EXPLICIT (match by criteria)";
        if (this.discriminator.isImplicit(context)) {
            int candidateCount = context.getCandidateCount();
            matchMode = "IMPLICIT (match single) - " + (candidateCount == 1 ? "VALID (exactly 1 match)" : "INVALID (" + candidateCount + " matches)");
        }
        new PrettyPrinter().kvWidth(20).kv("Target Class", this.classNode.name.replace('/', '.')).kv("Target Method", context.target.method.name).kv("Callback Name", this.info.getMethodName()).kv("Capture Type", SignaturePrinter.getTypeName(this.returnType, false)).kv("Instruction", "[%d] %s %s", target.insns.indexOf(context.node), context.node.getClass().getSimpleName(), Bytecode.getOpcodeName(context.node.getOpcode())).hr().kv("Match mode", matchMode).kv("Match ordinal", this.discriminator.getOrdinal() < 0 ? "any" : Integer.valueOf(this.discriminator.getOrdinal())).kv("Match index", this.discriminator.getIndex() < context.baseArgIndex ? "any" : Integer.valueOf(this.discriminator.getIndex())).kv("Match name(s)", this.discriminator.hasNames() ? this.discriminator.getNames() : "any").kv("Args only", this.discriminator.isArgsOnly()).hr().add(context).print(System.err);
    }

    private void inject(Context context, Injector.InjectorData handler, Target.Extension extraStack, int local) {
        if (!this.isStatic) {
            context.insns.add(new VarInsnNode(25, 0));
            extraStack.add();
        }
        context.insns.add(new VarInsnNode(this.returnType.getOpcode(21), local));
        extraStack.add();
        if (handler.captureTargetArgs > 0) {
            this.pushArgs(handler.target.arguments, context.insns, handler.target.getArgIndices(), 0, handler.captureTargetArgs, extraStack);
        }
        this.invokeHandler(context.insns);
        context.insns.add(new VarInsnNode(this.returnType.getOpcode(54), local));
    }

    static abstract class LocalVariableInjectionPoint
    extends InjectionPoint {
        protected final IMixinContext mixin;

        LocalVariableInjectionPoint(InjectionPointData data) {
            super(data);
            this.mixin = data.getMixin();
        }

        @Override
        public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
            throw new InvalidInjectionException(this.mixin, this.getAtCode() + " injection point must be used in conjunction with @ModifyVariable");
        }

        abstract boolean find(InjectionInfo var1, InsnList var2, Collection<AbstractInsnNode> var3, Target var4);
    }

    static class Context
    extends LocalVariableDiscriminator.Context {
        final InsnList insns = new InsnList();

        public Context(InjectionInfo info, Type returnType, boolean argsOnly, Target target, AbstractInsnNode node) {
            super(info, returnType, argsOnly, target, node);
        }
    }
}

