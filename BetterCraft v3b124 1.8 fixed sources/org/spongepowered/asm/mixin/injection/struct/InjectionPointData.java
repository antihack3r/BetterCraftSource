/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.struct;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.InvalidSelectorException;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.dynamic.DynamicSelectorDesc;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionPointException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.IMessageSink;
import org.spongepowered.asm.util.asm.IAnnotationHandle;

public class InjectionPointData {
    private static final Pattern AT_PATTERN = InjectionPointData.createPattern();
    private final Map<String, String> args = new HashMap<String, String>();
    private final IInjectionPointContext context;
    private final String at;
    private final String type;
    private final InjectionPoint.Selector selector;
    private final String target;
    private final String slice;
    private final int ordinal;
    private final int opcode;
    private final String id;

    public InjectionPointData(IInjectionPointContext context, String at2, List<String> args, String target, String slice, int ordinal, int opcode, String id2) {
        this.context = context;
        this.at = at2;
        this.target = target;
        this.slice = Strings.nullToEmpty(slice);
        this.ordinal = Math.max(-1, ordinal);
        this.opcode = opcode;
        this.id = id2;
        this.parseArgs(args);
        this.args.put("target", target);
        this.args.put("ordinal", String.valueOf(ordinal));
        this.args.put("opcode", String.valueOf(opcode));
        Matcher matcher = AT_PATTERN.matcher(at2);
        this.type = InjectionPointData.parseType(matcher, at2);
        this.selector = InjectionPointData.parseSelector(matcher);
    }

    private void parseArgs(List<String> args) {
        if (args == null) {
            return;
        }
        for (String arg2 : args) {
            if (arg2 == null) continue;
            int eqPos = arg2.indexOf(61);
            if (eqPos > -1) {
                this.args.put(arg2.substring(0, eqPos), arg2.substring(eqPos + 1));
                continue;
            }
            this.args.put(arg2, "");
        }
    }

    public IMessageSink getMessageSink() {
        return this.context;
    }

    public String getAt() {
        return this.at;
    }

    public String getType() {
        return this.type;
    }

    public InjectionPoint.Selector getSelector() {
        return this.selector;
    }

    public IInjectionPointContext getContext() {
        return this.context;
    }

    public IMixinContext getMixin() {
        return this.context.getMixin();
    }

    public MethodNode getMethod() {
        return this.context.getMethod();
    }

    public Type getMethodReturnType() {
        return Type.getReturnType(this.getMethod().desc);
    }

    public AnnotationNode getParent() {
        return this.context.getAnnotationNode();
    }

    public String getSlice() {
        return this.slice;
    }

    public LocalVariableDiscriminator getLocalVariableDiscriminator() {
        return LocalVariableDiscriminator.parse(this.getParent());
    }

    public String get(String key, String defaultValue) {
        String value = this.args.get(key);
        return value != null ? value : defaultValue;
    }

    public int get(String key, int defaultValue) {
        return InjectionPointData.parseInt(this.get(key, String.valueOf(defaultValue)), defaultValue);
    }

    public boolean get(String key, boolean defaultValue) {
        return InjectionPointData.parseBoolean(this.get(key, String.valueOf(defaultValue)), defaultValue);
    }

    public ITargetSelector get(String key) {
        try {
            return TargetSelector.parseAndValidate(this.get(key, ""), (ISelectorContext)this.context);
        }
        catch (InvalidSelectorException ex2) {
            throw new InvalidInjectionPointException(this.getMixin(), (Throwable)ex2, "Failed parsing @At(\"%s\").%s \"%s\" on %s", this.at, key, this.target, this.getDescription());
        }
    }

    public ITargetSelector getTarget() {
        try {
            String id2;
            IAnnotationHandle selectorAnnotation;
            AnnotationNode desc;
            if (Strings.isNullOrEmpty(this.target) && (desc = (AnnotationNode)Annotations.getValue(((Annotations.Handle)(selectorAnnotation = this.context.getSelectorAnnotation())).getNode(), "desc")) != null && "at".equalsIgnoreCase(id2 = Annotations.getValue(desc, "id", "at"))) {
                return DynamicSelectorDesc.of(Annotations.handleOf(desc), this.context);
            }
            return TargetSelector.parseAndValidate(this.target, (ISelectorContext)this.context);
        }
        catch (InvalidSelectorException ex2) {
            throw new InvalidInjectionPointException(this.getMixin(), (Throwable)ex2, "Failed validating @At(\"%s\").target \"%s\" on %s", this.at, this.target, this.getDescription());
        }
    }

    public String getDescription() {
        return InjectionInfo.describeInjector(this.context.getMixin(), this.context.getAnnotationNode(), this.context.getMethod());
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public int getOpcode(int defaultOpcode) {
        return this.opcode > 0 ? this.opcode : defaultOpcode;
    }

    public int getOpcode(int defaultOpcode, int ... validOpcodes) {
        for (int validOpcode : validOpcodes) {
            if (this.opcode != validOpcode) continue;
            return this.opcode;
        }
        return defaultOpcode;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return this.type;
    }

    private static Pattern createPattern() {
        return Pattern.compile(String.format("^(.+?)(:(%s))?$", Joiner.on('|').join((Object[])InjectionPoint.Selector.values())));
    }

    public static String parseType(String at2) {
        Matcher matcher = AT_PATTERN.matcher(at2);
        return InjectionPointData.parseType(matcher, at2);
    }

    private static String parseType(Matcher matcher, String at2) {
        return matcher.matches() ? matcher.group(1) : at2;
    }

    private static InjectionPoint.Selector parseSelector(Matcher matcher) {
        return matcher.matches() && matcher.group(3) != null ? InjectionPoint.Selector.valueOf(matcher.group(3)) : InjectionPoint.Selector.DEFAULT;
    }

    private static int parseInt(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception ex2) {
            return defaultValue;
        }
    }

    private static boolean parseBoolean(String string, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(string);
        }
        catch (Exception ex2) {
            return defaultValue;
        }
    }
}

