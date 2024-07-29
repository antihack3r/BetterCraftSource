/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.points;

import java.util.Collection;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorByName;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

@InjectionPoint.AtCode(value="INVOKE_STRING")
public class BeforeStringInvoke
extends BeforeInvoke {
    private static final String STRING_VOID_SIG = "(Ljava/lang/String;)V";
    private final String ldcValue;
    private boolean foundLdc;

    public BeforeStringInvoke(InjectionPointData data) {
        super(data);
        this.ldcValue = data.get("ldc", null);
        if (this.ldcValue == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires named argument \"ldc\" to specify the desired target");
        }
        if (!(this.target instanceof ITargetSelectorByName) || !STRING_VOID_SIG.equals(((ITargetSelectorByName)this.target).getDesc())) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires target method with with signature " + STRING_VOID_SIG);
        }
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        this.foundLdc = false;
        return super.find(desc, insns, nodes);
    }

    @Override
    protected void inspectInsn(String desc, InsnList insns, AbstractInsnNode insn) {
        if (insn instanceof LdcInsnNode) {
            LdcInsnNode node = (LdcInsnNode)insn;
            if (node.cst instanceof String && this.ldcValue.equals(node.cst)) {
                this.log("{}->{} > found a matching LDC with value {}", this.context, this.className, node.cst);
                this.foundLdc = true;
                return;
            }
        }
        this.foundLdc = false;
    }

    @Override
    protected boolean matchesOrdinal(int ordinal) {
        this.log("{}->{} > > found LDC \"{}\" = {}", this.context, this.className, this.ldcValue, this.foundLdc);
        return this.foundLdc && super.matchesOrdinal(ordinal);
    }
}

