/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.util.asm;

import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

public class MixinVerifier
extends SimpleVerifier {
    private Type currentClass;
    private Type currentSuperClass;
    private List<Type> currentClassInterfaces;
    private boolean isInterface;

    public MixinVerifier(int api2, Type currentClass, Type currentSuperClass, List<Type> currentClassInterfaces, boolean isInterface) {
        super(api2, currentClass, currentSuperClass, currentClassInterfaces, isInterface);
        this.currentClass = currentClass;
        this.currentSuperClass = currentSuperClass;
        this.currentClassInterfaces = currentClassInterfaces;
        this.isInterface = isInterface;
    }

    @Override
    protected boolean isInterface(Type type) {
        if (this.currentClass != null && type.equals(this.currentClass)) {
            return this.isInterface;
        }
        return ClassInfo.forType(type, ClassInfo.TypeLookup.ELEMENT_TYPE).isInterface();
    }

    @Override
    protected Type getSuperClass(Type type) {
        if (this.currentClass != null && type.equals(this.currentClass)) {
            return this.currentSuperClass;
        }
        ClassInfo c2 = ClassInfo.forType(type, ClassInfo.TypeLookup.ELEMENT_TYPE).getSuperClass();
        return c2 == null ? null : Type.getType("L" + c2.getName() + ";");
    }

    @Override
    protected boolean isAssignableFrom(Type type, Type other) {
        if (type.equals(other)) {
            return true;
        }
        if (this.currentClass != null && type.equals(this.currentClass)) {
            if (this.getSuperClass(other) == null) {
                return false;
            }
            if (this.isInterface) {
                return other.getSort() == 10 || other.getSort() == 9;
            }
            return this.isAssignableFrom(type, this.getSuperClass(other));
        }
        if (this.currentClass != null && other.equals(this.currentClass)) {
            if (this.isAssignableFrom(type, this.currentSuperClass)) {
                return true;
            }
            if (this.currentClassInterfaces != null) {
                for (int i2 = 0; i2 < this.currentClassInterfaces.size(); ++i2) {
                    Type v2 = this.currentClassInterfaces.get(i2);
                    if (!this.isAssignableFrom(type, v2)) continue;
                    return true;
                }
            }
            return false;
        }
        ClassInfo typeInfo = ClassInfo.forType(type, ClassInfo.TypeLookup.ELEMENT_TYPE);
        if (typeInfo == null) {
            return false;
        }
        if (typeInfo.isInterface()) {
            typeInfo = ClassInfo.forName("java/lang/Object");
        }
        return ClassInfo.forType(other, ClassInfo.TypeLookup.ELEMENT_TYPE).hasSuperClass(typeInfo);
    }
}

