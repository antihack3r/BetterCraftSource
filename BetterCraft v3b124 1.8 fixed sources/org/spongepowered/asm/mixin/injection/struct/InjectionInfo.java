/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.struct;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.tools.Diagnostic;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.code.InjectorTarget;
import org.spongepowered.asm.mixin.injection.code.MethodSlice;
import org.spongepowered.asm.mixin.injection.code.MethodSlices;
import org.spongepowered.asm.mixin.injection.selectors.ElementNode;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.InvalidSelectorException;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.throwables.SelectorConstraintException;
import org.spongepowered.asm.mixin.injection.selectors.throwables.SelectorException;
import org.spongepowered.asm.mixin.injection.struct.CallbackInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import org.spongepowered.asm.mixin.injection.struct.InvalidMemberDescriptorException;
import org.spongepowered.asm.mixin.injection.struct.ModifyArgInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.ModifyArgsInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.ModifyConstantInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.ModifyVariableInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.RedirectInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.struct.TargetNotSupportedException;
import org.spongepowered.asm.mixin.injection.throwables.InjectionError;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.struct.SpecialMethodInfo;
import org.spongepowered.asm.mixin.throwables.MixinError;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.asm.ASM;
import org.spongepowered.asm.util.asm.MethodNodeEx;
import org.spongepowered.asm.util.logging.MessageRouter;

public abstract class InjectionInfo
extends SpecialMethodInfo
implements ISliceContext {
    public static final String DEFAULT_PREFIX = "handler";
    private static Map<String, InjectorEntry> registry = new LinkedHashMap<String, InjectorEntry>();
    private static Class<? extends Annotation>[] registeredAnnotations = new Class[0];
    protected final boolean isStatic;
    protected final Set<ITargetSelector> selectors = new LinkedHashSet<ITargetSelector>();
    protected final List<SelectedTarget> targets = new ArrayList<SelectedTarget>();
    protected final MethodSlices slices;
    protected final String atKey;
    protected final List<InjectionPoint> injectionPoints = new ArrayList<InjectionPoint>();
    protected final Map<Target, List<InjectionNodes.InjectionNode>> targetNodes = new LinkedHashMap<Target, List<InjectionNodes.InjectionNode>>();
    protected int targetCount = 0;
    protected Injector injector;
    protected InjectorGroupInfo group;
    private final List<MethodNode> injectedMethods = new ArrayList<MethodNode>(0);
    private int expectedCallbackCount = 1;
    private int requiredCallbackCount = 0;
    private int maxCallbackCount = Integer.MAX_VALUE;
    private int injectedCallbackCount = 0;
    private List<String> messages;

    protected InjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
        this(mixin, method, annotation, "at");
    }

    protected InjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation, String atKey) {
        super(mixin, method, annotation);
        this.isStatic = Bytecode.isStatic(method);
        this.slices = MethodSlices.parse(this);
        this.atKey = atKey;
        this.readAnnotation();
    }

    protected void readAnnotation() {
        if (this.annotation == null) {
            return;
        }
        List<AnnotationNode> injectionPoints = this.readInjectionPoints();
        this.parseRequirements();
        this.parseSelectors();
        this.findTargets();
        this.parseInjectionPoints(injectionPoints);
        this.injector = this.parseInjector(this.annotation);
    }

    protected void parseSelectors() {
        LinkedHashSet<ITargetSelector> selectors = new LinkedHashSet<ITargetSelector>();
        TargetSelector.parse(Annotations.getValue(this.annotation, "method", false), this, selectors);
        TargetSelector.parse(Annotations.getValue(this.annotation, "target", false), this, selectors);
        if (selectors.size() == 0) {
            throw new InvalidInjectionException((ISelectorContext)this, String.format("%s annotation on %s is missing 'method' or 'target' to specify targets", this.annotationType, this.methodName));
        }
        for (ITargetSelector selector : selectors) {
            try {
                this.selectors.add(selector.validate().attach(this));
            }
            catch (InvalidMemberDescriptorException ex2) {
                throw new InvalidInjectionException((ISelectorContext)this, String.format("%s annotation on %s, has invalid target descriptor: %s. %s", this.annotationType, this.methodName, ex2.getMessage(), this.mixin.getReferenceMapper().getStatus()));
            }
            catch (TargetNotSupportedException ex3) {
                throw new InvalidInjectionException((ISelectorContext)this, String.format("%s annotation on %s specifies a target class '%s', which is not supported", this.annotationType, this.methodName, ex3.getMessage()));
            }
            catch (InvalidSelectorException ex4) {
                throw new InvalidInjectionException((ISelectorContext)this, String.format("%s annotation on %s is decorated with an invalid selector: %s", this.annotationType, this.methodName, ex4.getMessage()));
            }
        }
    }

    protected List<AnnotationNode> readInjectionPoints() {
        List<AnnotationNode> ats2 = Annotations.getValue(this.annotation, this.atKey, false);
        if (ats2 == null) {
            throw new InvalidInjectionException((ISelectorContext)this, String.format("%s annotation on %s is missing '%s' value(s)", this.annotationType, this.methodName, this.atKey));
        }
        return ats2;
    }

    protected void parseInjectionPoints(List<AnnotationNode> ats2) {
        this.injectionPoints.addAll(InjectionPoint.parse((IInjectionPointContext)this, ats2));
    }

    protected void parseRequirements() {
        Integer require;
        this.group = this.mixin.getInjectorGroups().parseGroup(this.method, this.mixin.getDefaultInjectorGroup()).add(this);
        Integer expect = (Integer)Annotations.getValue(this.annotation, "expect");
        if (expect != null) {
            this.expectedCallbackCount = expect;
        }
        if ((require = (Integer)Annotations.getValue(this.annotation, "require")) != null && require > -1) {
            this.requiredCallbackCount = require;
        } else if (this.group.isDefault()) {
            this.requiredCallbackCount = this.mixin.getDefaultRequiredInjections();
        }
        Integer allow = (Integer)Annotations.getValue(this.annotation, "allow");
        if (allow != null) {
            this.maxCallbackCount = Math.max(Math.max(this.requiredCallbackCount, 1), allow);
        }
    }

    protected abstract Injector parseInjector(AnnotationNode var1);

    public boolean isValid() {
        return this.targets.size() > 0 && this.injectionPoints.size() > 0;
    }

    public void prepare() {
        this.targetNodes.clear();
        for (SelectedTarget targetMethod : this.targets) {
            Target target = this.mixin.getTargetMethod(targetMethod.method);
            InjectorTarget injectorTarget = new InjectorTarget(this, target, targetMethod.selector);
            try {
                this.targetNodes.put(target, this.injector.find(injectorTarget, this.injectionPoints));
            }
            catch (SelectorException ex2) {
                throw new InvalidInjectionException((ISelectorContext)this, String.format("Injection validation failed: %s on %s: %s. %s%s", this.annotationType, this.methodName, ex2.getMessage(), this.mixin.getReferenceMapper().getStatus(), this.getDynamicInfo()));
            }
            finally {
                injectorTarget.dispose();
            }
        }
    }

    public void preInject() {
        for (Map.Entry<Target, List<InjectionNodes.InjectionNode>> entry : this.targetNodes.entrySet()) {
            this.injector.preInject(entry.getKey(), entry.getValue());
        }
    }

    public void inject() {
        for (Map.Entry<Target, List<InjectionNodes.InjectionNode>> entry : this.targetNodes.entrySet()) {
            this.injector.inject(entry.getKey(), entry.getValue());
        }
        this.targets.clear();
    }

    public void postInject() {
        for (MethodNode method : this.injectedMethods) {
            this.classNode.methods.add(method);
        }
        String description = this.getDescription();
        String refMapStatus = this.mixin.getReferenceMapper().getStatus();
        String extraInfo = this.getDynamicInfo() + this.getMessages();
        if (this.mixin.getOption(MixinEnvironment.Option.DEBUG_INJECTORS) && this.injectedCallbackCount < this.expectedCallbackCount) {
            throw new InvalidInjectionException((ISelectorContext)this, String.format("Injection validation failed: %s %s%s in %s expected %d invocation(s) but %d succeeded. Scanned %d target(s). %s%s", description, this.methodName, this.method.desc, this.mixin, this.expectedCallbackCount, this.injectedCallbackCount, this.targetCount, refMapStatus, extraInfo));
        }
        if (this.injectedCallbackCount < this.requiredCallbackCount) {
            throw new InjectionError(String.format("Critical injection failure: %s %s%s in %s failed injection check, (%d/%d) succeeded. Scanned %d target(s). %s%s", description, this.methodName, this.method.desc, this.mixin, this.injectedCallbackCount, this.requiredCallbackCount, this.targetCount, refMapStatus, extraInfo));
        }
        if (this.injectedCallbackCount > this.maxCallbackCount) {
            throw new InjectionError(String.format("Critical injection failure: %s %s%s in %s failed injection check, %d succeeded of %d allowed.%s", description, this.methodName, this.method.desc, this.mixin, this.injectedCallbackCount, this.maxCallbackCount, extraInfo));
        }
    }

    public void notifyInjected(Target target) {
    }

    protected String getDescription() {
        return "Callback method";
    }

    public String toString() {
        return InjectionInfo.describeInjector(this.mixin, this.annotation, this.method);
    }

    public int getTargetCount() {
        return this.targets.size();
    }

    @Override
    public MethodSlice getSlice(String id2) {
        return this.slices.get(this.getSliceId(id2));
    }

    public String getSliceId(String id2) {
        return "";
    }

    public int getInjectedCallbackCount() {
        return this.injectedCallbackCount;
    }

    public MethodNode addMethod(int access, String name, String desc) {
        MethodNode method = new MethodNode(ASM.API_VERSION, access | 0x1000, name, desc, null, null);
        this.injectedMethods.add(method);
        return method;
    }

    public void addCallbackInvocation(MethodNode handler) {
        ++this.injectedCallbackCount;
    }

    @Override
    public void addMessage(String format, Object ... args) {
        super.addMessage(format, args);
        if (this.messages == null) {
            this.messages = new ArrayList<String>();
        }
        String message = String.format(format, args);
        this.messages.add(message);
    }

    protected String getMessages() {
        return this.messages != null ? " Messages: { " + Joiner.on(" ").join(this.messages) + "}" : "";
    }

    protected void findTargets() {
        this.targets.clear();
        this.findRootTargets();
        this.validateTargets();
    }

    private void findRootTargets() {
        int passes = this.mixin.getOption(MixinEnvironment.Option.REFMAP_REMAP) ? 2 : 1;
        for (ITargetSelector selector : this.selectors) {
            selector = selector.configure(ITargetSelector.Configure.SELECT_MEMBER, new String[0]);
            int matchCount = 0;
            int maxCount = selector.getMaxMatchCount();
            ITargetSelector permissiveSelector = selector.configure(ITargetSelector.Configure.PERMISSIVE, new String[0]);
            int selectorPasses = permissiveSelector == selector ? 1 : passes;
            block1: for (int pass = 0; pass < selectorPasses && matchCount < 1; ++pass) {
                ITargetSelector passSelector = pass == 0 ? selector : permissiveSelector;
                for (MethodNode target : this.classNode.methods) {
                    boolean isMixinMethod;
                    if (!passSelector.match(ElementNode.of(this.classNode, target)).isExactMatch()) continue;
                    ++matchCount;
                    boolean bl2 = isMixinMethod = Annotations.getVisible(target, MixinMerged.class) != null;
                    if (maxCount <= 1 || (this.isStatic || !Bytecode.isStatic(target)) && target != this.method && !isMixinMethod) {
                        this.checkTarget(target);
                        this.targets.add(new SelectedTarget(passSelector, target));
                    }
                    if (matchCount < maxCount) continue;
                    break block1;
                }
            }
            if (matchCount >= selector.getMinMatchCount()) continue;
            throw new InvalidInjectionException((ISelectorContext)this, (Throwable)new SelectorConstraintException(selector, String.format("Injection validation failed: %s for %s on %s did not match the required number of targets (required=%d, matched=%d). %s%s", selector, this.annotationType, this.methodName, selector.getMinMatchCount(), matchCount, this.mixin.getReferenceMapper().getStatus(), this.getDynamicInfo())));
        }
    }

    protected void validateTargets() {
        this.targetCount = this.targets.size();
        if (this.targetCount > 0) {
            return;
        }
        if (this.mixin.getOption(MixinEnvironment.Option.DEBUG_INJECTORS) && this.expectedCallbackCount > 0) {
            throw new InvalidInjectionException((ISelectorContext)this, String.format("Injection validation failed: %s annotation on %s could not find any targets matching %s in %s. %s%s", this.annotationType, this.methodName, InjectionInfo.namesOf(this.selectors), this.mixin.getTarget(), this.mixin.getReferenceMapper().getStatus(), this.getDynamicInfo()));
        }
        if (this.requiredCallbackCount > 0) {
            throw new InvalidInjectionException((ISelectorContext)this, String.format("Critical injection failure: %s annotation on %s could not find any targets matching %s in %s. %s%s", this.annotationType, this.methodName, InjectionInfo.namesOf(this.selectors), this.mixin.getTarget(), this.mixin.getReferenceMapper().getStatus(), this.getDynamicInfo()));
        }
    }

    protected void checkTarget(MethodNode target) {
        AnnotationNode merged = Annotations.getVisible(target, MixinMerged.class);
        if (merged == null) {
            return;
        }
        if (Annotations.getVisible(target, Final.class) != null) {
            throw new InvalidInjectionException((ISelectorContext)this, String.format("%s cannot inject into @Final method %s::%s%s merged by %s", this, this.classNode.name, target.name, target.desc, Annotations.getValue(merged, "mixin")));
        }
    }

    protected String getDynamicInfo() {
        AnnotationNode annotation = Annotations.getInvisible(this.method, Dynamic.class);
        String description = Strings.nullToEmpty((String)Annotations.getValue(annotation));
        Type upstream = (Type)Annotations.getValue(annotation, "mixin");
        if (upstream != null) {
            description = String.format("{%s} %s", upstream.getClassName(), description).trim();
        }
        return description.length() > 0 ? String.format(" Method is @Dynamic(%s).", description) : "";
    }

    public static InjectionInfo parse(MixinTargetContext mixin, MethodNode method) {
        AnnotationNode annotation = InjectionInfo.getInjectorAnnotation(mixin.getMixin(), method);
        if (annotation == null) {
            return null;
        }
        for (InjectorEntry injector : registry.values()) {
            if (!annotation.desc.equals(injector.annotationDesc)) continue;
            return injector.create(mixin, method, annotation);
        }
        return null;
    }

    public static AnnotationNode getInjectorAnnotation(IMixinInfo mixin, MethodNode method) {
        AnnotationNode annotation = null;
        try {
            annotation = Annotations.getSingleVisible(method, registeredAnnotations);
        }
        catch (IllegalArgumentException ex2) {
            throw new InvalidMixinException(mixin, String.format("Error parsing annotations on %s in %s: %s", method.name, mixin.getClassName(), ex2.getMessage()));
        }
        return annotation;
    }

    public static String getInjectorPrefix(AnnotationNode annotation) {
        if (annotation == null) {
            return DEFAULT_PREFIX;
        }
        for (InjectorEntry injector : registry.values()) {
            if (!annotation.desc.endsWith(injector.annotationDesc)) continue;
            return injector.prefix;
        }
        return DEFAULT_PREFIX;
    }

    static String describeInjector(IMixinContext mixin, AnnotationNode annotation, MethodNode method) {
        return String.format("%s->@%s::%s%s", mixin.toString(), Annotations.getSimpleName(annotation), MethodNodeEx.getName(method), method.desc);
    }

    private static String namesOf(Collection<ITargetSelector> selectors) {
        int index = 0;
        int count = selectors.size();
        StringBuilder sb2 = new StringBuilder();
        for (ITargetSelector selector : selectors) {
            if (index > 0) {
                if (index == count - 1) {
                    sb2.append(" or ");
                } else {
                    sb2.append(", ");
                }
            }
            sb2.append('\'').append(selector.toString()).append('\'');
            ++index;
        }
        return sb2.toString();
    }

    public static void register(Class<? extends InjectionInfo> type) {
        InjectorEntry entry;
        AnnotationType annotationType = type.getAnnotation(AnnotationType.class);
        if (annotationType == null) {
            throw new IllegalArgumentException("Injection info class " + type + " is not annotated with @AnnotationType");
        }
        try {
            entry = new InjectorEntry(annotationType.value(), type);
        }
        catch (NoSuchMethodException ex2) {
            throw new MixinError("InjectionInfo class " + type.getName() + " is missing a valid constructor");
        }
        InjectorEntry existing = registry.get(entry.annotationDesc);
        if (existing != null) {
            MessageRouter.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format("Overriding InjectionInfo for @%s with %s (previously %s)", annotationType.value().getSimpleName(), type.getName(), existing.injectorType.getName()));
        } else {
            MessageRouter.getMessager().printMessage(Diagnostic.Kind.OTHER, String.format("Registering new injector for @%s with %s", annotationType.value().getSimpleName(), type.getName()));
        }
        registry.put(entry.annotationDesc, entry);
        ArrayList<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
        for (InjectorEntry injector : registry.values()) {
            annotations.add(injector.annotationType);
        }
        registeredAnnotations = annotations.toArray(registeredAnnotations);
    }

    public static Set<Class<? extends Annotation>> getRegisteredAnnotations() {
        return ImmutableSet.copyOf(registeredAnnotations);
    }

    static {
        InjectionInfo.register(CallbackInjectionInfo.class);
        InjectionInfo.register(ModifyArgInjectionInfo.class);
        InjectionInfo.register(ModifyArgsInjectionInfo.class);
        InjectionInfo.register(RedirectInjectionInfo.class);
        InjectionInfo.register(ModifyVariableInjectionInfo.class);
        InjectionInfo.register(ModifyConstantInjectionInfo.class);
    }

    static class SelectedTarget {
        private final ITargetSelector root;
        final ITargetSelector selector;
        final MethodNode method;

        SelectedTarget(ITargetSelector root, ITargetSelector selector, MethodNode method) {
            this.root = root;
            this.selector = selector;
            this.method = method;
        }

        SelectedTarget(ITargetSelector selector, MethodNode method) {
            this(null, selector, method);
        }

        ITargetSelector getRoot() {
            return this.root != null ? this.root : this.selector;
        }
    }

    static class InjectorEntry {
        final Class<? extends Annotation> annotationType;
        final Class<? extends InjectionInfo> injectorType;
        final Constructor<? extends InjectionInfo> ctor;
        final String annotationDesc;
        final String prefix;

        InjectorEntry(Class<? extends Annotation> annotationType, Class<? extends InjectionInfo> type) throws NoSuchMethodException {
            this.annotationType = annotationType;
            this.injectorType = type;
            this.ctor = type.getDeclaredConstructor(MixinTargetContext.class, MethodNode.class, AnnotationNode.class);
            this.annotationDesc = Type.getDescriptor(annotationType);
            HandlerPrefix handlerPrefix = type.getAnnotation(HandlerPrefix.class);
            this.prefix = handlerPrefix != null ? handlerPrefix.value() : InjectionInfo.DEFAULT_PREFIX;
        }

        InjectionInfo create(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
            try {
                return this.ctor.newInstance(mixin, method, annotation);
            }
            catch (InvocationTargetException itex) {
                Throwable cause = itex.getCause();
                if (cause instanceof MixinException) {
                    throw (MixinException)cause;
                }
                Throwable ex2 = cause != null ? cause : itex;
                throw new MixinError("Error initialising injector metaclass [" + this.injectorType + "] for annotation " + annotation.desc, ex2);
            }
            catch (ReflectiveOperationException ex3) {
                throw new MixinError("Failed to instantiate injector metaclass [" + this.injectorType + "] for annotation " + annotation.desc, ex3);
            }
        }
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(value={ElementType.TYPE})
    public static @interface HandlerPrefix {
        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(value={ElementType.TYPE})
    public static @interface AnnotationType {
        public Class<? extends Annotation> value();
    }
}

