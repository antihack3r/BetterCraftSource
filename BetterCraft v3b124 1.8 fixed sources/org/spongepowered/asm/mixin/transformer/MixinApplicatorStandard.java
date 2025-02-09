/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer;

import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.extensibility.IActivityContext;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.throwables.MixinError;
import org.spongepowered.asm.mixin.transformer.ActivityStack;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.mixin.transformer.TargetClassContext;
import org.spongepowered.asm.mixin.transformer.ext.extensions.ExtensionClassExporter;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.mixin.transformer.meta.MixinRenamed;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.mixin.transformer.throwables.MixinApplicatorException;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.ConstraintParser;
import org.spongepowered.asm.util.perf.Profiler;
import org.spongepowered.asm.util.throwables.ConstraintViolationException;
import org.spongepowered.asm.util.throwables.InvalidConstraintException;

class MixinApplicatorStandard {
    protected static final List<Class<? extends Annotation>> CONSTRAINED_ANNOTATIONS = ImmutableList.of(Overwrite.class, Inject.class, ModifyArg.class, ModifyArgs.class, Redirect.class, ModifyVariable.class, ModifyConstant.class);
    protected static final int[] INITIALISER_OPCODE_BLACKLIST = new int[]{177, 21, 22, 23, 24, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 79, 80, 81, 82, 83, 84, 85, 86};
    protected final ILogger logger = MixinService.getService().getLogger("mixin");
    protected final TargetClassContext context;
    protected final String targetName;
    protected final ClassNode targetClass;
    protected final ClassInfo targetClassInfo;
    protected final Profiler profiler = Profiler.getProfiler("mixin");
    protected final IMixinAuditTrail auditTrail;
    protected final ActivityStack activities = new ActivityStack();
    protected final boolean mergeSignatures;

    MixinApplicatorStandard(TargetClassContext context) {
        this.context = context;
        this.targetName = context.getClassName();
        this.targetClass = context.getClassNode();
        this.targetClassInfo = context.getClassInfo();
        ExtensionClassExporter exporter = (ExtensionClassExporter)context.getExtensions().getExtension(ExtensionClassExporter.class);
        this.mergeSignatures = exporter.isDecompilerActive() && MixinEnvironment.getCurrentEnvironment().getOption(MixinEnvironment.Option.DEBUG_EXPORT_DECOMPILE_MERGESIGNATURES);
        this.auditTrail = MixinService.getService().getAuditTrail();
    }

    final void apply(SortedSet<MixinInfo> mixins) {
        ArrayList<MixinTargetContext> mixinContexts = new ArrayList<MixinTargetContext>();
        Iterator iter = mixins.iterator();
        while (iter.hasNext()) {
            MixinInfo mixin = (MixinInfo)iter.next();
            try {
                this.logger.log(mixin.getLoggingLevel(), "Mixing {} from {} into {}", mixin.getName(), mixin.getParent(), this.targetName);
                mixinContexts.add(mixin.createContextFor(this.context));
                if (this.auditTrail == null) continue;
                this.auditTrail.onApply(this.targetName, mixin.toString());
            }
            catch (InvalidMixinException ex2) {
                if (mixin.isRequired()) {
                    throw ex2;
                }
                this.context.addSuppressed(ex2);
                iter.remove();
            }
        }
        MixinTargetContext current = null;
        this.activities.clear();
        try {
            IActivityContext.IActivity activity = this.activities.begin("PreApply Phase");
            IActivityContext.IActivity preApplyActivity = this.activities.begin("Mixin");
            for (MixinTargetContext context : mixinContexts) {
                preApplyActivity.next(context.toString());
                current = context;
                current.preApply(this.targetName, this.targetClass);
            }
            preApplyActivity.end();
            for (ApplicatorPass pass : ApplicatorPass.values()) {
                activity.next("%s Applicator Phase", new Object[]{pass});
                Profiler.Section timer = this.profiler.begin("pass", pass.name().toLowerCase(Locale.ROOT));
                IActivityContext.IActivity applyActivity = this.activities.begin("Mixin");
                Iterator iter2 = mixinContexts.iterator();
                while (iter2.hasNext()) {
                    current = (MixinTargetContext)iter2.next();
                    applyActivity.next(current.toString());
                    try {
                        this.applyMixin(current, pass);
                    }
                    catch (InvalidMixinException ex3) {
                        if (current.isRequired()) {
                            throw ex3;
                        }
                        this.context.addSuppressed(ex3);
                        iter2.remove();
                    }
                }
                applyActivity.end();
                timer.end();
            }
            activity.next("PostApply Phase");
            IActivityContext.IActivity postApplyActivity = this.activities.begin("Mixin");
            Iterator iter3 = mixinContexts.iterator();
            while (iter3.hasNext()) {
                current = (MixinTargetContext)iter3.next();
                postApplyActivity.next(current.toString());
                try {
                    current.postApply(this.targetName, this.targetClass);
                }
                catch (InvalidMixinException ex4) {
                    if (current.isRequired()) {
                        throw ex4;
                    }
                    this.context.addSuppressed(ex4);
                    iter3.remove();
                }
            }
            activity.end();
        }
        catch (InvalidMixinException ex5) {
            ex5.prepend(this.activities);
            throw ex5;
        }
        catch (Exception ex6) {
            throw new MixinApplicatorException(current, "Unexpecteded " + ex6.getClass().getSimpleName() + " whilst applying the mixin class:", (Throwable)ex6, (IActivityContext)this.activities);
        }
        this.applySourceMap(this.context);
        this.context.processDebugTasks();
    }

    protected final void applyMixin(MixinTargetContext mixin, ApplicatorPass pass) {
        IActivityContext.IActivity activity = this.activities.begin("Apply");
        switch (pass) {
            case MAIN: {
                activity.next("Apply Signature");
                this.applySignature(mixin);
                activity.next("Apply Interfaces");
                this.applyInterfaces(mixin);
                activity.next("Apply Attributess");
                this.applyAttributes(mixin);
                activity.next("Apply Annotations");
                this.applyAnnotations(mixin);
                activity.next("Apply Fields");
                this.applyFields(mixin);
                activity.next("Apply Methods");
                this.applyMethods(mixin);
                activity.next("Apply Initialisers");
                this.applyInitialisers(mixin);
                break;
            }
            case PREINJECT: {
                activity.next("Prepare Injections");
                this.prepareInjections(mixin);
                break;
            }
            case INJECT: {
                activity.next("Apply Accessors");
                this.applyAccessors(mixin);
                activity.next("Apply Injections");
                this.applyInjections(mixin);
                break;
            }
            default: {
                throw new IllegalStateException("Invalid pass specified " + (Object)((Object)pass));
            }
        }
        activity.end();
    }

    protected void applySignature(MixinTargetContext mixin) {
        if (this.mergeSignatures) {
            this.context.mergeSignature(mixin.getSignature());
        }
    }

    protected void applyInterfaces(MixinTargetContext mixin) {
        for (String interfaceName : mixin.getInterfaces()) {
            if (this.targetClass.interfaces.contains(interfaceName)) continue;
            this.targetClass.interfaces.add(interfaceName);
            this.targetClassInfo.addInterface(interfaceName);
        }
    }

    protected void applyAttributes(MixinTargetContext mixin) {
        int requiredVersion;
        if (mixin.shouldSetSourceFile()) {
            this.targetClass.sourceFile = mixin.getSourceFile();
        }
        if (((requiredVersion = mixin.getMinRequiredClassVersion()) & 0xFFFF) > (this.targetClass.version & 0xFFFF)) {
            this.targetClass.version = requiredVersion;
        }
    }

    protected void applyAnnotations(MixinTargetContext mixin) {
        ClassNode sourceClass = mixin.getClassNode();
        Annotations.merge(sourceClass, this.targetClass);
    }

    protected void applyFields(MixinTargetContext mixin) {
        this.mergeShadowFields(mixin);
        this.mergeNewFields(mixin);
    }

    protected void mergeShadowFields(MixinTargetContext mixin) {
        for (Map.Entry<FieldNode, ClassInfo.Field> entry : mixin.getShadowFields()) {
            FieldNode shadow = entry.getKey();
            FieldNode target = this.findTargetField(shadow);
            if (target == null) continue;
            Annotations.merge(shadow, target);
            if (!entry.getValue().isDecoratedMutable()) continue;
            target.access &= 0xFFFFFFEF;
        }
    }

    protected void mergeNewFields(MixinTargetContext mixin) {
        for (FieldNode field : mixin.getFields()) {
            FieldNode target = this.findTargetField(field);
            if (target != null) continue;
            this.targetClass.fields.add(field);
            if (field.signature == null) continue;
            if (this.mergeSignatures) {
                SignatureVisitor sv2 = mixin.getSignature().getRemapper();
                new SignatureReader(field.signature).accept(sv2);
                field.signature = sv2.toString();
                continue;
            }
            field.signature = null;
        }
    }

    protected void applyMethods(MixinTargetContext mixin) {
        IActivityContext.IActivity activity = this.activities.begin("?");
        for (MethodNode shadow : mixin.getShadowMethods()) {
            activity.next("@Shadow %s:%s", shadow.desc, shadow.name);
            this.applyShadowMethod(mixin, shadow);
        }
        for (MethodNode mixinMethod : mixin.getMethods()) {
            activity.next("%s:%s", mixinMethod.desc, mixinMethod.name);
            this.applyNormalMethod(mixin, mixinMethod);
        }
        activity.end();
    }

    protected void applyShadowMethod(MixinTargetContext mixin, MethodNode shadow) {
        MethodNode target = this.findTargetMethod(shadow);
        if (target != null) {
            Annotations.merge(shadow, target);
        }
    }

    protected void applyNormalMethod(MixinTargetContext mixin, MethodNode mixinMethod) {
        mixin.transformMethod(mixinMethod);
        if (!mixinMethod.name.startsWith("<")) {
            this.checkMethodVisibility(mixin, mixinMethod);
            this.checkMethodConstraints(mixin, mixinMethod);
            this.mergeMethod(mixin, mixinMethod);
        } else if ("<clinit>".equals(mixinMethod.name)) {
            IActivityContext.IActivity activity = this.activities.begin("Merge CLINIT insns");
            this.appendInsns(mixin, mixinMethod);
            activity.end();
        }
    }

    protected void mergeMethod(MixinTargetContext mixin, MethodNode method) {
        boolean isOverwrite = Annotations.getVisible(method, Overwrite.class) != null;
        MethodNode target = this.findTargetMethod(method);
        if (target != null) {
            if (this.isAlreadyMerged(mixin, method, isOverwrite, target)) {
                return;
            }
            AnnotationNode intrinsic = Annotations.getInvisible(method, Intrinsic.class);
            if (intrinsic != null) {
                if (this.mergeIntrinsic(mixin, method, isOverwrite, target, intrinsic)) {
                    mixin.getTarget().methodMerged(method);
                    return;
                }
            } else {
                if (mixin.requireOverwriteAnnotations() && !isOverwrite) {
                    throw new InvalidMixinException((IMixinContext)mixin, String.format("%s%s in %s cannot overwrite method in %s because @Overwrite is required by the parent configuration", method.name, method.desc, mixin, mixin.getTarget().getClassName()));
                }
                this.targetClass.methods.remove(target);
            }
        } else if (isOverwrite) {
            throw new InvalidMixinException((IMixinContext)mixin, String.format("Overwrite target \"%s\" was not located in target class %s", method.name, mixin.getTargetClassRef()));
        }
        this.targetClass.methods.add(method);
        mixin.methodMerged(method);
        if (method.signature != null) {
            if (this.mergeSignatures) {
                SignatureVisitor sv2 = mixin.getSignature().getRemapper();
                new SignatureReader(method.signature).accept(sv2);
                method.signature = sv2.toString();
            } else {
                method.signature = null;
            }
        }
    }

    protected boolean isAlreadyMerged(MixinTargetContext mixin, MethodNode method, boolean isOverwrite, MethodNode target) {
        AnnotationNode accTarget;
        AnnotationNode merged = Annotations.getVisible(target, MixinMerged.class);
        if (merged == null) {
            if (Annotations.getVisible(target, Final.class) != null) {
                this.logger.warn("Overwrite prohibited for @Final method {} in {}. Skipping method.", method.name, mixin);
                return true;
            }
            return false;
        }
        String sessionId = (String)Annotations.getValue(merged, "sessionId");
        if (!this.context.getSessionId().equals(sessionId)) {
            throw new ClassFormatError("Invalid @MixinMerged annotation found in" + mixin + " at " + method.name + " in " + this.targetClass.name);
        }
        if (Bytecode.hasFlag(target, 4160) && Bytecode.hasFlag(method, 4160)) {
            if (mixin.getEnvironment().getOption(MixinEnvironment.Option.DEBUG_VERBOSE)) {
                this.logger.warn("Synthetic bridge method clash for {} in {}", method.name, mixin);
            }
            return true;
        }
        String owner = (String)Annotations.getValue(merged, "mixin");
        int priority = (Integer)Annotations.getValue(merged, "priority");
        AnnotationNode accMethod = Annotations.getSingleVisible(method, Accessor.class, Invoker.class);
        if (accMethod != null && (accTarget = Annotations.getSingleVisible(target, Accessor.class, Invoker.class)) != null) {
            String myTarget = (String)Annotations.getValue(accMethod, "target");
            String trTarget = (String)Annotations.getValue(accTarget, "target");
            if (myTarget == null) {
                throw new MixinError("Encountered undecorated Accessor method in " + mixin + " applying to " + this.targetName);
            }
            if (myTarget.equals(trTarget)) {
                return true;
            }
            throw new InvalidMixinException((IMixinContext)mixin, String.format("Incompatible @%s %s (for %s) in %s previously written by %s (for %s)", Annotations.getSimpleName(accMethod), method.name, myTarget, mixin, owner, trTarget));
        }
        if (priority >= mixin.getPriority() && !owner.equals(mixin.getClassName())) {
            this.logger.warn("Method overwrite conflict for {} in {}, previously written by {}. Skipping method.", method.name, mixin, owner);
            return true;
        }
        if (Annotations.getVisible(target, Final.class) != null) {
            this.logger.warn("Method overwrite conflict for @Final method {} in {} declared by {}. Skipping method.", method.name, mixin, owner);
            return true;
        }
        return false;
    }

    protected boolean mergeIntrinsic(MixinTargetContext mixin, MethodNode method, boolean isOverwrite, MethodNode target, AnnotationNode intrinsic) {
        AnnotationNode renamed;
        if (isOverwrite) {
            throw new InvalidMixinException((IMixinContext)mixin, "@Intrinsic is not compatible with @Overwrite, remove one of these annotations on " + method.name + " in " + mixin);
        }
        String methodName = method.name + method.desc;
        if (Bytecode.hasFlag(method, 8)) {
            throw new InvalidMixinException((IMixinContext)mixin, "@Intrinsic method cannot be static, found " + methodName + " in " + mixin);
        }
        if (!(Bytecode.hasFlag(method, 4096) || (renamed = Annotations.getVisible(method, MixinRenamed.class)) != null && Annotations.getValue(renamed, "isInterfaceMember", Boolean.FALSE).booleanValue())) {
            throw new InvalidMixinException((IMixinContext)mixin, "@Intrinsic method must be prefixed interface method, no rename encountered on " + methodName + " in " + mixin);
        }
        if (!Annotations.getValue(intrinsic, "displace", Boolean.FALSE).booleanValue()) {
            this.logger.log(mixin.getLoggingLevel(), "Skipping Intrinsic mixin method {} for {}", methodName, mixin.getTargetClassRef());
            return true;
        }
        this.displaceIntrinsic(mixin, method, target);
        return false;
    }

    protected void displaceIntrinsic(MixinTargetContext mixin, MethodNode method, MethodNode target) {
        String proxyName = "proxy+" + target.name;
        ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof MethodInsnNode) || insn.getOpcode() == 184) continue;
            MethodInsnNode methodNode = (MethodInsnNode)insn;
            if (!methodNode.owner.equals(this.targetClass.name) || !methodNode.name.equals(target.name) || !methodNode.desc.equals(target.desc)) continue;
            methodNode.name = proxyName;
        }
        target.name = proxyName;
    }

    protected final void appendInsns(MixinTargetContext mixin, MethodNode method) {
        if (Type.getReturnType(method.desc) != Type.VOID_TYPE) {
            throw new IllegalArgumentException("Attempted to merge insns from a method which does not return void");
        }
        MethodNode target = this.findTargetMethod(method);
        if (target != null) {
            AbstractInsnNode returnNode = Bytecode.findInsn(target, 177);
            if (returnNode != null) {
                ListIterator<AbstractInsnNode> injectIter = method.instructions.iterator();
                while (injectIter.hasNext()) {
                    AbstractInsnNode insn = (AbstractInsnNode)injectIter.next();
                    if (insn instanceof LineNumberNode || insn.getOpcode() == 177) continue;
                    target.instructions.insertBefore(returnNode, insn);
                }
                target.maxLocals = Math.max(target.maxLocals, method.maxLocals);
                target.maxStack = Math.max(target.maxStack, method.maxStack);
            }
            return;
        }
        this.targetClass.methods.add(method);
    }

    protected void applyInitialisers(MixinTargetContext mixin) {
        MethodNode ctor = this.getConstructor(mixin);
        if (ctor == null) {
            return;
        }
        Deque<AbstractInsnNode> initialiser = this.getInitialiser(mixin, ctor);
        if (initialiser == null || initialiser.size() == 0) {
            return;
        }
        String superName = this.context.getClassInfo().getSuperName();
        for (MethodNode method : this.targetClass.methods) {
            if (!"<init>".equals(method.name)) continue;
            Bytecode.DelegateInitialiser superCall = Bytecode.findDelegateInit(method, superName, this.targetClass.name);
            if (superCall.isPresent && !superCall.isSuper) continue;
            method.maxStack = Math.max(method.maxStack, ctor.maxStack);
            this.injectInitialiser(mixin, method, initialiser);
        }
    }

    protected MethodNode getConstructor(MixinTargetContext mixin) {
        MethodNode ctor = null;
        for (MethodNode mixinMethod : mixin.getMethods()) {
            if (!"<init>".equals(mixinMethod.name) || !Bytecode.methodHasLineNumbers(mixinMethod)) continue;
            if (ctor == null) {
                ctor = mixinMethod;
                continue;
            }
            this.logger.warn("Mixin {} has multiple constructors, {} was selected\n", mixin, ctor.desc);
        }
        return ctor;
    }

    private Range getConstructorRange(MethodNode ctor) {
        boolean lineNumberIsValid = false;
        AbstractInsnNode endReturn = null;
        int line = 0;
        int start = 0;
        int end = 0;
        int superIndex = -1;
        ListIterator<AbstractInsnNode> iter = ctor.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (insn instanceof LineNumberNode) {
                line = ((LineNumberNode)insn).line;
                lineNumberIsValid = true;
                continue;
            }
            if (insn instanceof MethodInsnNode) {
                if (insn.getOpcode() != 183 || !"<init>".equals(((MethodInsnNode)insn).name) || superIndex != -1) continue;
                superIndex = ctor.instructions.indexOf(insn);
                start = line;
                continue;
            }
            if (insn.getOpcode() == 181) {
                lineNumberIsValid = false;
                continue;
            }
            if (insn.getOpcode() != 177) continue;
            if (lineNumberIsValid) {
                end = line;
                continue;
            }
            end = start;
            endReturn = insn;
        }
        if (endReturn != null) {
            LabelNode label = new LabelNode(new Label());
            ctor.instructions.insertBefore(endReturn, label);
            ctor.instructions.insertBefore(endReturn, new LineNumberNode(start, label));
        }
        return new Range(start, end, superIndex);
    }

    protected final Deque<AbstractInsnNode> getInitialiser(MixinTargetContext mixin, MethodNode ctor) {
        Range init = this.getConstructorRange(ctor);
        if (!init.isValid()) {
            return null;
        }
        int line = 0;
        ArrayDeque<AbstractInsnNode> initialiser = new ArrayDeque<AbstractInsnNode>();
        boolean gatherNodes = false;
        int trimAtOpcode = -1;
        LabelNode optionalInsn = null;
        ListIterator<AbstractInsnNode> iter = ctor.instructions.iterator(init.marker);
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (insn instanceof LineNumberNode) {
                line = ((LineNumberNode)insn).line;
                AbstractInsnNode next = ctor.instructions.get(ctor.instructions.indexOf(insn) + 1);
                if (line == init.end && next.getOpcode() != 177) {
                    gatherNodes = true;
                    trimAtOpcode = 177;
                    continue;
                }
                gatherNodes = init.excludes(line);
                trimAtOpcode = -1;
                continue;
            }
            if (!gatherNodes) continue;
            if (optionalInsn != null) {
                initialiser.add(optionalInsn);
                optionalInsn = null;
            }
            if (insn instanceof LabelNode) {
                optionalInsn = (LabelNode)insn;
                continue;
            }
            int opcode = insn.getOpcode();
            if (opcode == trimAtOpcode) {
                trimAtOpcode = -1;
                continue;
            }
            for (int ivalidOp : INITIALISER_OPCODE_BLACKLIST) {
                if (opcode != ivalidOp) continue;
                throw new InvalidMixinException((IMixinContext)mixin, "Cannot handle " + Bytecode.getOpcodeName(opcode) + " opcode (0x" + Integer.toHexString(opcode).toUpperCase(Locale.ROOT) + ") in class initialiser");
            }
            initialiser.add(insn);
        }
        AbstractInsnNode last = (AbstractInsnNode)initialiser.peekLast();
        if (last != null && last.getOpcode() != 181) {
            throw new InvalidMixinException((IMixinContext)mixin, "Could not parse initialiser, expected 0xB5, found 0x" + Integer.toHexString(last.getOpcode()) + " in " + mixin);
        }
        return initialiser;
    }

    protected final void injectInitialiser(MixinTargetContext mixin, MethodNode ctor, Deque<AbstractInsnNode> initialiser) {
        Map<LabelNode, LabelNode> labels = Bytecode.cloneLabels(ctor.instructions);
        AbstractInsnNode insn = this.findInitialiserInjectionPoint(mixin, ctor, initialiser);
        if (insn == null) {
            this.logger.warn("Failed to locate initialiser injection point in <init>{}, initialiser was not mixed in.", ctor.desc);
            return;
        }
        for (AbstractInsnNode node : initialiser) {
            if (node instanceof LabelNode) continue;
            if (node instanceof JumpInsnNode) {
                throw new InvalidMixinException((IMixinContext)mixin, "Unsupported JUMP opcode in initialiser in " + mixin);
            }
            AbstractInsnNode imACloneNow = node.clone(labels);
            ctor.instructions.insert(insn, imACloneNow);
            insn = imACloneNow;
        }
    }

    protected AbstractInsnNode findInitialiserInjectionPoint(MixinTargetContext mixin, MethodNode ctor, Deque<AbstractInsnNode> initialiser) {
        HashSet<String> initialisedFields = new HashSet<String>();
        for (AbstractInsnNode initialiserInsn : initialiser) {
            if (initialiserInsn.getOpcode() != 181) continue;
            initialisedFields.add(MixinApplicatorStandard.fieldKey((FieldInsnNode)initialiserInsn));
        }
        InitialiserInjectionMode mode = this.getInitialiserInjectionMode(mixin.getEnvironment());
        String targetName = this.targetClassInfo.getName();
        String targetSuperName = this.targetClassInfo.getSuperName();
        AbstractInsnNode targetInsn = null;
        ListIterator<AbstractInsnNode> iter = ctor.instructions.iterator();
        while (iter.hasNext()) {
            String key;
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (insn.getOpcode() == 183 && "<init>".equals(((MethodInsnNode)insn).name)) {
                String owner = ((MethodInsnNode)insn).owner;
                if (!owner.equals(targetName) && !owner.equals(targetSuperName)) continue;
                targetInsn = insn;
                if (mode != InitialiserInjectionMode.SAFE) continue;
                break;
            }
            if (insn.getOpcode() != 181 || mode != InitialiserInjectionMode.DEFAULT || !initialisedFields.contains(key = MixinApplicatorStandard.fieldKey((FieldInsnNode)insn))) continue;
            targetInsn = insn;
        }
        return targetInsn;
    }

    private InitialiserInjectionMode getInitialiserInjectionMode(MixinEnvironment environment) {
        String strMode = environment.getOptionValue(MixinEnvironment.Option.INITIALISER_INJECTION_MODE);
        if (strMode == null) {
            return InitialiserInjectionMode.DEFAULT;
        }
        try {
            return InitialiserInjectionMode.valueOf(strMode.toUpperCase(Locale.ROOT));
        }
        catch (Exception ex2) {
            this.logger.warn("Could not parse unexpected value \"{}\" for mixin.initialiserInjectionMode, reverting to DEFAULT", strMode);
            return InitialiserInjectionMode.DEFAULT;
        }
    }

    private static String fieldKey(FieldInsnNode fieldNode) {
        return String.format("%s:%s", fieldNode.desc, fieldNode.name);
    }

    protected void prepareInjections(MixinTargetContext mixin) {
        mixin.prepareInjections();
    }

    protected void applyInjections(MixinTargetContext mixin) {
        mixin.applyInjections();
    }

    protected void applyAccessors(MixinTargetContext mixin) {
        List<MethodNode> accessorMethods = mixin.generateAccessors();
        for (MethodNode method : accessorMethods) {
            if (method.name.startsWith("<")) continue;
            this.mergeMethod(mixin, method);
        }
    }

    protected void checkMethodVisibility(MixinTargetContext mixin, MethodNode mixinMethod) {
        if (Bytecode.hasFlag(mixinMethod, 8) && !Bytecode.hasFlag(mixinMethod, 2) && !Bytecode.hasFlag(mixinMethod, 4096) && Annotations.getVisible(mixinMethod, Overwrite.class) == null) {
            throw new InvalidMixinException((IMixinContext)mixin, String.format("Mixin %s contains non-private static method %s", mixin, mixinMethod));
        }
    }

    protected void applySourceMap(TargetClassContext context) {
        this.targetClass.sourceDebug = context.getSourceMap().toString();
    }

    protected void checkMethodConstraints(MixinTargetContext mixin, MethodNode method) {
        for (Class<? extends Annotation> annotationType : CONSTRAINED_ANNOTATIONS) {
            AnnotationNode annotation = Annotations.getVisible(method, annotationType);
            if (annotation == null) continue;
            this.checkConstraints(mixin, method, annotation);
        }
    }

    protected final void checkConstraints(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
        try {
            ConstraintParser.Constraint constraint = ConstraintParser.parse(annotation);
            try {
                constraint.check(mixin.getEnvironment());
            }
            catch (ConstraintViolationException ex2) {
                String message = String.format("Constraint violation: %s on %s in %s", ex2.getMessage(), method, mixin);
                this.logger.warn(message, new Object[0]);
                if (!mixin.getEnvironment().getOption(MixinEnvironment.Option.IGNORE_CONSTRAINTS)) {
                    throw new InvalidMixinException((IMixinContext)mixin, message, (Throwable)ex2);
                }
            }
        }
        catch (InvalidConstraintException ex3) {
            throw new InvalidMixinException((IMixinContext)mixin, ex3.getMessage());
        }
    }

    protected final MethodNode findTargetMethod(MethodNode searchFor) {
        for (MethodNode target : this.targetClass.methods) {
            if (!target.name.equals(searchFor.name) || !target.desc.equals(searchFor.desc)) continue;
            return target;
        }
        return null;
    }

    protected final FieldNode findTargetField(FieldNode searchFor) {
        for (FieldNode target : this.targetClass.fields) {
            if (!target.name.equals(searchFor.name) || !target.desc.equals(searchFor.desc)) continue;
            return target;
        }
        return null;
    }

    class Range {
        final int start;
        final int end;
        final int marker;

        Range(int start, int end, int marker) {
            this.start = start;
            this.end = end;
            this.marker = marker;
        }

        boolean isValid() {
            return this.start != 0 && this.end != 0 && this.end >= this.start;
        }

        boolean contains(int value) {
            return value >= this.start && value <= this.end;
        }

        boolean excludes(int value) {
            return value < this.start || value > this.end;
        }

        public String toString() {
            return String.format("Range[%d-%d,%d,valid=%s)", this.start, this.end, this.marker, this.isValid());
        }
    }

    static enum InitialiserInjectionMode {
        DEFAULT,
        SAFE;

    }

    static enum ApplicatorPass {
        MAIN,
        PREINJECT,
        INJECT;

    }
}

