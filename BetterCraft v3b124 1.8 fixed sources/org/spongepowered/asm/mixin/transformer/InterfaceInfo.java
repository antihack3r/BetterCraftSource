/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer;

import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinRenamed;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.util.Annotations;

final class InterfaceInfo {
    private final MixinInfo mixin;
    private final String prefix;
    private final Type iface;
    private final boolean unique;
    private Set<String> methods;

    private InterfaceInfo(MixinInfo mixin, String prefix, Type iface, boolean unique) {
        if (prefix == null || prefix.length() < 2 || !prefix.endsWith("$")) {
            throw new InvalidMixinException((IMixinInfo)mixin, String.format("Prefix %s for iface %s is not valid", prefix, iface.toString()));
        }
        this.mixin = mixin;
        this.prefix = prefix;
        this.iface = iface;
        this.unique = unique;
    }

    private void initMethods() {
        this.methods = new HashSet<String>();
        this.readInterface(this.iface.getInternalName());
    }

    private void readInterface(String ifaceName) {
        ClassInfo interfaceInfo = ClassInfo.forName(ifaceName);
        for (ClassInfo.Method ifaceMethod : interfaceInfo.getMethods()) {
            this.methods.add(ifaceMethod.toString());
        }
        for (String superIface : interfaceInfo.getInterfaces()) {
            this.readInterface(superIface);
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public Type getIface() {
        return this.iface;
    }

    public String getName() {
        return this.iface.getClassName();
    }

    public String getInternalName() {
        return this.iface.getInternalName();
    }

    public boolean isUnique() {
        return this.unique;
    }

    public boolean renameMethod(MethodNode method) {
        if (this.methods == null) {
            this.initMethods();
        }
        if (!method.name.startsWith(this.prefix)) {
            if (this.methods.contains(method.name + method.desc)) {
                this.decorateUniqueMethod(method);
            }
            return false;
        }
        String realName = method.name.substring(this.prefix.length());
        String signature = realName + method.desc;
        if (!this.methods.contains(signature)) {
            throw new InvalidMixinException((IMixinInfo)this.mixin, String.format("%s does not exist in target interface %s", realName, this.getName()));
        }
        if ((method.access & 1) == 0) {
            throw new InvalidMixinException((IMixinInfo)this.mixin, String.format("%s cannot implement %s because it is not visible", realName, this.getName()));
        }
        Annotations.setVisible(method, MixinRenamed.class, "originalName", method.name, "isInterfaceMember", true);
        this.decorateUniqueMethod(method);
        method.name = realName;
        return true;
    }

    private void decorateUniqueMethod(MethodNode method) {
        if (!this.unique) {
            return;
        }
        if (Annotations.getVisible(method, Unique.class) == null) {
            Annotations.setVisible(method, Unique.class, new Object[0]);
            this.mixin.getClassInfo().findMethod(method).setUnique(true);
        }
    }

    static InterfaceInfo fromAnnotation(MixinInfo mixin, AnnotationNode node) {
        String prefix = (String)Annotations.getValue(node, "prefix");
        Type iface = (Type)Annotations.getValue(node, "iface");
        Boolean unique = (Boolean)Annotations.getValue(node, "unique");
        if (prefix == null || iface == null) {
            throw new InvalidMixinException((IMixinInfo)mixin, String.format("@Interface annotation on %s is missing a required parameter", mixin));
        }
        return new InterfaceInfo(mixin, prefix, iface, unique != null && unique != false);
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        InterfaceInfo that = (InterfaceInfo)o2;
        return this.mixin.equals(that.mixin) && this.prefix.equals(that.prefix) && this.iface.equals(that.iface);
    }

    public int hashCode() {
        int result = this.mixin.hashCode();
        result = 31 * result + this.prefix.hashCode();
        result = 31 * result + this.iface.hashCode();
        return result;
    }
}

