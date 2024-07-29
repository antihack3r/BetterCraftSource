/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.selectors.dynamic;

import com.google.common.base.Strings;
import java.util.List;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.selectors.ElementNode;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorByName;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorDynamic;
import org.spongepowered.asm.mixin.injection.selectors.InvalidSelectorException;
import org.spongepowered.asm.mixin.injection.selectors.MatchResult;
import org.spongepowered.asm.mixin.injection.selectors.dynamic.DescriptorResolver;
import org.spongepowered.asm.mixin.injection.selectors.dynamic.IResolvedDescriptor;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.Quantifier;
import org.spongepowered.asm.util.SignaturePrinter;
import org.spongepowered.asm.util.asm.IAnnotationHandle;

@ITargetSelectorDynamic.SelectorId(value="Desc")
@ITargetSelectorDynamic.SelectorAnnotation(value=Desc.class)
public class DynamicSelectorDesc
implements ITargetSelectorDynamic,
ITargetSelectorByName {
    private final InvalidSelectorException parseException;
    private final String id;
    private final Type owner;
    private final String name;
    private final Type[] args;
    private final Type returnType;
    private final String methodDesc;
    private final Quantifier matches;
    private final List<IAnnotationHandle> next;

    private DynamicSelectorDesc(IResolvedDescriptor desc) {
        this(null, desc.getId(), desc.getOwner(), desc.getName(), desc.getArgs(), desc.getReturnType(), desc.getMatches(), desc.getNext());
    }

    private DynamicSelectorDesc(DynamicSelectorDesc desc, Quantifier quantifier) {
        this(desc.parseException, desc.id, desc.owner, desc.name, desc.args, desc.returnType, quantifier, desc.next);
    }

    private DynamicSelectorDesc(DynamicSelectorDesc desc, Type owner) {
        this(desc.parseException, desc.id, owner, desc.name, desc.args, desc.returnType, desc.matches, desc.next);
    }

    private DynamicSelectorDesc(InvalidSelectorException ex2) {
        this(ex2, null, null, null, null, null, Quantifier.NONE, null);
    }

    protected DynamicSelectorDesc(InvalidSelectorException ex2, String id2, Type owner, String name, Type[] args, Type returnType, Quantifier matches, List<IAnnotationHandle> then) {
        this.parseException = ex2;
        this.id = id2;
        this.owner = owner;
        this.name = Strings.emptyToNull(name);
        this.args = args;
        this.returnType = returnType;
        this.methodDesc = returnType != null ? Bytecode.getDescriptor(returnType, args) : null;
        this.matches = matches;
        this.next = then;
    }

    public static DynamicSelectorDesc parse(String input, ISelectorContext context) {
        IResolvedDescriptor descriptor = DescriptorResolver.resolve(input, context);
        if (!descriptor.isResolved()) {
            String extra = input.length() == 0 ? ". " + descriptor.getResolutionInfo() : "";
            return new DynamicSelectorDesc(new InvalidSelectorException("Could not resolve @Desc(" + input + ") for " + context + extra));
        }
        return DynamicSelectorDesc.of(descriptor);
    }

    public static DynamicSelectorDesc parse(IAnnotationHandle desc, ISelectorContext context) {
        IResolvedDescriptor descriptor = DescriptorResolver.resolve(desc, context);
        if (!descriptor.isResolved()) {
            return new DynamicSelectorDesc(new InvalidSelectorException("Invalid descriptor"));
        }
        return DynamicSelectorDesc.of(descriptor);
    }

    public static DynamicSelectorDesc resolve(ISelectorContext context) {
        IResolvedDescriptor descriptor = DescriptorResolver.resolve("", context);
        if (!descriptor.isResolved()) {
            return null;
        }
        return DynamicSelectorDesc.of(descriptor);
    }

    public static DynamicSelectorDesc of(IAnnotationHandle desc, ISelectorContext context) {
        IResolvedDescriptor descriptor = DescriptorResolver.resolve(desc, context);
        if (!descriptor.isResolved()) {
            return null;
        }
        return DynamicSelectorDesc.of(descriptor);
    }

    public static DynamicSelectorDesc of(IResolvedDescriptor desc) {
        return new DynamicSelectorDesc(desc);
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder("@Desc(");
        boolean started = false;
        if (!Strings.isNullOrEmpty(this.id)) {
            sb2.append("id = \"").append(this.id).append("\"");
            started = true;
        }
        if (this.owner != Type.VOID_TYPE) {
            if (started) {
                sb2.append(", ");
            }
            sb2.append("owner = ").append(SignaturePrinter.getTypeName(this.owner, false, false)).append(".class");
            started = true;
        }
        if (started) {
            sb2.append(", ");
        }
        if (this.name != null) {
            sb2.append("value = \"").append(this.name).append("\"");
        }
        if (this.args.length > 0) {
            sb2.append(", args = { ");
            for (int i2 = 0; i2 < this.args.length; ++i2) {
                if (i2 > 0) {
                    sb2.append(", ");
                }
                sb2.append(SignaturePrinter.getTypeName(this.args[i2], false, false)).append(".class");
            }
            sb2.append(" }");
        }
        if (this.returnType != Type.VOID_TYPE) {
            sb2.append(", ret = ").append(SignaturePrinter.getTypeName(this.returnType, false, false)).append(".class");
        }
        sb2.append(")");
        return sb2.toString();
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String getOwner() {
        return this.owner.getInternalName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Type[] getArgs() {
        return this.args;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    @Override
    public String getDesc() {
        return this.methodDesc;
    }

    @Override
    public String toDescriptor() {
        return new SignaturePrinter(this).setFullyQualified(true).toDescriptor();
    }

    @Override
    public ITargetSelector validate() throws InvalidSelectorException {
        if (this.parseException != null) {
            throw this.parseException;
        }
        return this;
    }

    @Override
    public ITargetSelector next() {
        return this.next(0);
    }

    protected ITargetSelector next(int index) {
        if (index >= 0 && index < this.next.size()) {
            IAnnotationHandle nextAnnotation = this.next.get(index);
            IResolvedDescriptor descriptor = DescriptorResolver.resolve(nextAnnotation, null);
            return new Next(index, descriptor);
        }
        return null;
    }

    @Override
    public ITargetSelector configure(ITargetSelector.Configure request, String ... args) {
        request.checkArgs(args);
        switch (request) {
            case SELECT_MEMBER: {
                if (!this.matches.isDefault()) break;
                return new DynamicSelectorDesc(this, Quantifier.SINGLE);
            }
            case SELECT_INSTRUCTION: {
                if (!this.matches.isDefault()) break;
                return new DynamicSelectorDesc(this, Quantifier.ANY);
            }
            case MOVE: {
                return new DynamicSelectorDesc(this, Type.getObjectType(args[0]));
            }
            case CLEAR_LIMITS: {
                if (this.getMinMatchCount() == 0 && this.getMaxMatchCount() >= Integer.MAX_VALUE) break;
                return new DynamicSelectorDesc(this, Quantifier.ANY);
            }
        }
        return this;
    }

    @Override
    public ITargetSelector attach(ISelectorContext context) throws InvalidSelectorException {
        return this;
    }

    @Override
    public int getMinMatchCount() {
        return this.matches.getClampedMin();
    }

    @Override
    public int getMaxMatchCount() {
        return this.matches.getClampedMax();
    }

    @Override
    public MatchResult matches(String owner, String name, String desc) {
        return this.matches(owner, name, desc, this.methodDesc);
    }

    @Override
    public <TNode> MatchResult match(ElementNode<TNode> node) {
        if (node == null) {
            return MatchResult.NONE;
        }
        if (node.isField()) {
            return this.matches(node.getOwner(), node.getName(), node.getDesc(), this.returnType.getInternalName());
        }
        return this.matches(node.getOwner(), node.getName(), node.getDesc(), this.methodDesc);
    }

    private MatchResult matches(String owner, String name, String desc, String compareWithDesc) {
        if (!compareWithDesc.equals(desc)) {
            return MatchResult.NONE;
        }
        if (this.owner != Type.VOID_TYPE && !this.owner.getInternalName().equals(owner)) {
            return MatchResult.NONE;
        }
        if (this.name != null && this.name.equals(name)) {
            return MatchResult.EXACT_MATCH;
        }
        if (this.name != null && this.name.equalsIgnoreCase(name)) {
            return MatchResult.MATCH;
        }
        if (this.name == null) {
            return MatchResult.EXACT_MATCH;
        }
        return MatchResult.NONE;
    }

    final class Next
    extends DynamicSelectorDesc {
        private final int index;

        Next(int index, IResolvedDescriptor next) {
            super(null, null, next.getOwner(), next.getName(), next.getArgs(), next.getReturnType(), next.getMatches(), null);
            this.index = index;
        }

        @Override
        public ITargetSelector next() {
            return DynamicSelectorDesc.this.next(this.index + 1);
        }
    }
}

