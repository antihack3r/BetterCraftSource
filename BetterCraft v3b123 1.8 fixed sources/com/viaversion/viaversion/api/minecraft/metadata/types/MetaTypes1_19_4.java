// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.api.minecraft.metadata.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.ParticleType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;

public final class MetaTypes1_19_4 extends AbstractMetaTypes
{
    public final MetaType byteType;
    public final MetaType varIntType;
    public final MetaType longType;
    public final MetaType floatType;
    public final MetaType stringType;
    public final MetaType componentType;
    public final MetaType optionalComponentType;
    public final MetaType itemType;
    public final MetaType booleanType;
    public final MetaType rotationType;
    public final MetaType positionType;
    public final MetaType optionalPositionType;
    public final MetaType directionType;
    public final MetaType optionalUUIDType;
    public final MetaType blockStateType;
    public final MetaType optionalBlockStateType;
    public final MetaType nbtType;
    public final MetaType particleType;
    public final MetaType villagerDatatType;
    public final MetaType optionalVarIntType;
    public final MetaType poseType;
    public final MetaType catVariantType;
    public final MetaType frogVariantType;
    public final MetaType optionalGlobalPosition;
    public final MetaType paintingVariantType;
    public final MetaType snifferState;
    public final MetaType vectorType;
    public final MetaType quaternionType;
    
    public MetaTypes1_19_4(final ParticleType particleType) {
        super(28);
        this.byteType = this.add(0, Type.BYTE);
        this.varIntType = this.add(1, Type.VAR_INT);
        this.longType = this.add(2, Type.VAR_LONG);
        this.floatType = this.add(3, Type.FLOAT);
        this.stringType = this.add(4, Type.STRING);
        this.componentType = this.add(5, Type.COMPONENT);
        this.optionalComponentType = this.add(6, Type.OPTIONAL_COMPONENT);
        this.itemType = this.add(7, Type.FLAT_VAR_INT_ITEM);
        this.booleanType = this.add(8, Type.BOOLEAN);
        this.rotationType = this.add(9, Type.ROTATION);
        this.positionType = this.add(10, Type.POSITION1_14);
        this.optionalPositionType = this.add(11, Type.OPTIONAL_POSITION_1_14);
        this.directionType = this.add(12, Type.VAR_INT);
        this.optionalUUIDType = this.add(13, Type.OPTIONAL_UUID);
        this.blockStateType = this.add(14, Type.VAR_INT);
        this.optionalBlockStateType = this.add(15, Type.VAR_INT);
        this.nbtType = this.add(16, Type.NBT);
        this.villagerDatatType = this.add(18, Type.VILLAGER_DATA);
        this.optionalVarIntType = this.add(19, Type.OPTIONAL_VAR_INT);
        this.poseType = this.add(20, Type.VAR_INT);
        this.catVariantType = this.add(21, Type.VAR_INT);
        this.frogVariantType = this.add(22, Type.VAR_INT);
        this.optionalGlobalPosition = this.add(23, Type.OPTIONAL_GLOBAL_POSITION);
        this.paintingVariantType = this.add(24, Type.VAR_INT);
        this.snifferState = this.add(25, Type.VAR_INT);
        this.vectorType = this.add(26, Type.VECTOR3F);
        this.quaternionType = this.add(27, Type.QUATERNION);
        this.particleType = this.add(17, particleType);
    }
}
