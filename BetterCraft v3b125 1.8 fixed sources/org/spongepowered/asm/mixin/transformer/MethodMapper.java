/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Counter;

class MethodMapper {
    private static final ILogger logger = MixinService.getService().getLogger("mixin");
    private static final List<String> classes = new ArrayList<String>();
    private static final Map<String, Counter> methods = new HashMap<String, Counter>();
    private final ClassInfo info;
    private int nextUniqueMethodIndex;
    private int nextUniqueFieldIndex;

    public MethodMapper(MixinEnvironment env, ClassInfo info) {
        this.info = info;
    }

    public ClassInfo getClassInfo() {
        return this.info;
    }

    public void remapHandlerMethod(MixinInfo mixin, MethodNode handler, ClassInfo.Method method) {
        if (!(handler instanceof MixinInfo.MixinMethodNode) || !((MixinInfo.MixinMethodNode)handler).isInjector()) {
            return;
        }
        if (method.isUnique()) {
            logger.warn("Redundant @Unique on injector method {} in {}. Injectors are implicitly unique", method, mixin);
        }
        if (method.isRenamed()) {
            handler.name = method.getName();
            return;
        }
        String handlerName = this.getHandlerName((MixinInfo.MixinMethodNode)handler);
        handler.name = method.conform(handlerName);
    }

    public String getHandlerName(MixinInfo.MixinMethodNode method) {
        String prefix = InjectionInfo.getInjectorPrefix(method.getInjectorAnnotation());
        String classUID = MethodMapper.getClassUID(method.getOwner().getClassRef());
        String methodUID = MethodMapper.getMethodUID(method.name, method.desc, !method.isSurrogate());
        return String.format("%s$%s%s$%s", prefix, classUID, methodUID, method.name);
    }

    public String getUniqueName(MethodNode method, String sessionId, boolean preservePrefix) {
        String uniqueIndex = Integer.toHexString(this.nextUniqueMethodIndex++);
        String pattern = preservePrefix ? "%2$s_$md$%1$s$%3$s" : "md%s$%s$%s";
        return String.format(pattern, sessionId.substring(30), method.name, uniqueIndex);
    }

    public String getUniqueName(FieldNode field, String sessionId) {
        String uniqueIndex = Integer.toHexString(this.nextUniqueFieldIndex++);
        return String.format("fd%s$%s$%s", sessionId.substring(30), field.name, uniqueIndex);
    }

    private static String getClassUID(String classRef) {
        int index = classes.indexOf(classRef);
        if (index < 0) {
            index = classes.size();
            classes.add(classRef);
        }
        return MethodMapper.finagle(index);
    }

    private static String getMethodUID(String name, String desc, boolean increment) {
        String descriptor = String.format("%s%s", name, desc);
        Counter id2 = methods.get(descriptor);
        if (id2 == null) {
            id2 = new Counter();
            methods.put(descriptor, id2);
        } else if (increment) {
            ++id2.value;
        }
        return String.format("%03x", id2.value);
    }

    private static String finagle(int index) {
        String hex = Integer.toHexString(index);
        StringBuilder sb2 = new StringBuilder();
        for (int pos = 0; pos < hex.length(); ++pos) {
            char c2;
            c2 = (char)(c2 + ((c2 = hex.charAt(pos)) < ':' ? 49 : 10));
            sb2.append(c2);
        }
        return Strings.padStart(sb2.toString(), 3, 'z');
    }
}

