/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.src.Config;

public enum ModelVariableType {
    POS_X("tx"),
    POS_Y("ty"),
    POS_Z("tz"),
    ANGLE_X("rx"),
    ANGLE_Y("ry"),
    ANGLE_Z("rz"),
    OFFSET_X("ox"),
    OFFSET_Y("oy"),
    OFFSET_Z("oz"),
    SCALE_X("sx"),
    SCALE_Y("sy"),
    SCALE_Z("sz");

    private String name;
    public static ModelVariableType[] VALUES;

    static {
        VALUES = ModelVariableType.values();
    }

    private ModelVariableType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public float getFloat(ModelRenderer mr2) {
        switch (this) {
            case POS_X: {
                return mr2.rotationPointX;
            }
            case POS_Y: {
                return mr2.rotationPointY;
            }
            case POS_Z: {
                return mr2.rotationPointZ;
            }
            case ANGLE_X: {
                return mr2.rotateAngleX;
            }
            case ANGLE_Y: {
                return mr2.rotateAngleY;
            }
            case ANGLE_Z: {
                return mr2.rotateAngleZ;
            }
            case OFFSET_X: {
                return mr2.offsetX;
            }
            case OFFSET_Y: {
                return mr2.offsetY;
            }
            case OFFSET_Z: {
                return mr2.offsetZ;
            }
            case SCALE_X: {
                return mr2.scaleX;
            }
            case SCALE_Y: {
                return mr2.scaleY;
            }
            case SCALE_Z: {
                return mr2.scaleZ;
            }
        }
        Config.warn("GetFloat not supported for: " + (Object)((Object)this));
        return 0.0f;
    }

    public void setFloat(ModelRenderer mr2, float val) {
        switch (this) {
            case POS_X: {
                mr2.rotationPointX = val;
                return;
            }
            case POS_Y: {
                mr2.rotationPointY = val;
                return;
            }
            case POS_Z: {
                mr2.rotationPointZ = val;
                return;
            }
            case ANGLE_X: {
                mr2.rotateAngleX = val;
                return;
            }
            case ANGLE_Y: {
                mr2.rotateAngleY = val;
                return;
            }
            case ANGLE_Z: {
                mr2.rotateAngleZ = val;
                return;
            }
            case OFFSET_X: {
                mr2.offsetX = val;
                return;
            }
            case OFFSET_Y: {
                mr2.offsetY = val;
                return;
            }
            case OFFSET_Z: {
                mr2.offsetZ = val;
                return;
            }
            case SCALE_X: {
                mr2.scaleX = val;
                return;
            }
            case SCALE_Y: {
                mr2.scaleY = val;
                return;
            }
            case SCALE_Z: {
                mr2.scaleZ = val;
                return;
            }
        }
        Config.warn("SetFloat not supported for: " + (Object)((Object)this));
    }

    public static ModelVariableType parse(String str) {
        int i2 = 0;
        while (i2 < VALUES.length) {
            ModelVariableType modelvariabletype = VALUES[i2];
            if (modelvariabletype.getName().equals(str)) {
                return modelvariabletype;
            }
            ++i2;
        }
        return null;
    }
}

