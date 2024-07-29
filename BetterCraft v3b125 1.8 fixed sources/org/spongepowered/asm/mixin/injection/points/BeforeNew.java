/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.points;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorConstructor;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionPointException;

@InjectionPoint.AtCode(value="NEW")
public class BeforeNew
extends InjectionPoint {
    private final String target;
    private final String desc;
    private final int ordinal;

    public BeforeNew(InjectionPointData data) {
        super(data);
        this.ordinal = data.getOrdinal();
        String target = Strings.emptyToNull(data.get("class", data.get("target", "")).replace('.', '/'));
        ITargetSelector member = TargetSelector.parseAndValidate(target, (ISelectorContext)data.getContext());
        if (!(member instanceof ITargetSelectorConstructor)) {
            throw new InvalidInjectionPointException(data.getMixin(), "Failed parsing @At(\"NEW\") target descriptor \"%s\" on %s", target, data.getDescription());
        }
        ITargetSelectorConstructor targetSelector = (ITargetSelectorConstructor)member;
        this.target = targetSelector.toCtorType();
        this.desc = targetSelector.toCtorDesc();
    }

    public boolean hasDescriptor() {
        return this.desc != null;
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        boolean found = false;
        int ordinal = 0;
        ArrayList newNodes = new ArrayList();
        Collection<Object> candidates = this.desc != null ? newNodes : nodes;
        ListIterator<AbstractInsnNode> iter = insns.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = iter.next();
            if (!(insn instanceof TypeInsnNode) || insn.getOpcode() != 187 || !this.matchesOwner((TypeInsnNode)insn)) continue;
            if (this.ordinal == -1 || this.ordinal == ordinal) {
                candidates.add(insn);
                found = this.desc == null;
            }
            ++ordinal;
        }
        if (this.desc != null) {
            for (TypeInsnNode newNode : newNodes) {
                if (!this.findCtor(insns, newNode)) continue;
                nodes.add(newNode);
                found = true;
            }
        }
        return found;
    }

    protected boolean findCtor(InsnList insns, TypeInsnNode newNode) {
        int indexOf = insns.indexOf(newNode);
        ListIterator<AbstractInsnNode> iter = insns.iterator(indexOf);
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof MethodInsnNode) || insn.getOpcode() != 183) continue;
            MethodInsnNode methodNode = (MethodInsnNode)insn;
            if (!"<init>".equals(methodNode.name) || !methodNode.owner.equals(newNode.desc) || !methodNode.desc.equals(this.desc)) continue;
            return true;
        }
        return false;
    }

    private boolean matchesOwner(TypeInsnNode insn) {
        return this.target == null || this.target.equals(insn.desc);
    }
}

