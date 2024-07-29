/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources.model;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.vecmath.Matrix4f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ITransformation;
import net.minecraftforge.client.model.TRSRTransformation;
import net.optifine.reflect.Reflector;
import org.lwjgl.util.vector.Vector3f;

public enum ModelRotation implements IModelState,
ITransformation
{
    X0_Y0(0, 0),
    X0_Y90(0, 90),
    X0_Y180(0, 180),
    X0_Y270(0, 270),
    X90_Y0(90, 0),
    X90_Y90(90, 90),
    X90_Y180(90, 180),
    X90_Y270(90, 270),
    X180_Y0(180, 0),
    X180_Y90(180, 90),
    X180_Y180(180, 180),
    X180_Y270(180, 270),
    X270_Y0(270, 0),
    X270_Y90(270, 90),
    X270_Y180(270, 180),
    X270_Y270(270, 270);

    private static final Map<Integer, ModelRotation> mapRotations;
    private final int combinedXY;
    private final org.lwjgl.util.vector.Matrix4f matrix4d;
    private final int quartersX;
    private final int quartersY;

    static {
        mapRotations = Maps.newHashMap();
        ModelRotation[] modelRotationArray = ModelRotation.values();
        int n2 = modelRotationArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ModelRotation modelrotation = modelRotationArray[n3];
            mapRotations.put(modelrotation.combinedXY, modelrotation);
            ++n3;
        }
    }

    private static int combineXY(int p_177521_0_, int p_177521_1_) {
        return p_177521_0_ * 360 + p_177521_1_;
    }

    private ModelRotation(int p_i46087_3_, int p_i46087_4_) {
        this.combinedXY = ModelRotation.combineXY(p_i46087_3_, p_i46087_4_);
        this.matrix4d = new org.lwjgl.util.vector.Matrix4f();
        org.lwjgl.util.vector.Matrix4f matrix4f = new org.lwjgl.util.vector.Matrix4f();
        matrix4f.setIdentity();
        org.lwjgl.util.vector.Matrix4f.rotate((float)(-p_i46087_3_) * ((float)Math.PI / 180), new Vector3f(1.0f, 0.0f, 0.0f), matrix4f, matrix4f);
        this.quartersX = MathHelper.abs_int(p_i46087_3_ / 90);
        org.lwjgl.util.vector.Matrix4f matrix4f1 = new org.lwjgl.util.vector.Matrix4f();
        matrix4f1.setIdentity();
        org.lwjgl.util.vector.Matrix4f.rotate((float)(-p_i46087_4_) * ((float)Math.PI / 180), new Vector3f(0.0f, 1.0f, 0.0f), matrix4f1, matrix4f1);
        this.quartersY = MathHelper.abs_int(p_i46087_4_ / 90);
        org.lwjgl.util.vector.Matrix4f.mul(matrix4f1, matrix4f, this.matrix4d);
    }

    public org.lwjgl.util.vector.Matrix4f getMatrix4d() {
        return this.matrix4d;
    }

    public EnumFacing rotateFace(EnumFacing p_177523_1_) {
        EnumFacing enumfacing = p_177523_1_;
        int i2 = 0;
        while (i2 < this.quartersX) {
            enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
            ++i2;
        }
        if (enumfacing.getAxis() != EnumFacing.Axis.Y) {
            int j2 = 0;
            while (j2 < this.quartersY) {
                enumfacing = enumfacing.rotateAround(EnumFacing.Axis.Y);
                ++j2;
            }
        }
        return enumfacing;
    }

    public int rotateVertex(EnumFacing facing, int vertexIndex) {
        int i2 = vertexIndex;
        if (facing.getAxis() == EnumFacing.Axis.X) {
            i2 = (vertexIndex + this.quartersX) % 4;
        }
        EnumFacing enumfacing = facing;
        int j2 = 0;
        while (j2 < this.quartersX) {
            enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
            ++j2;
        }
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            i2 = (i2 + this.quartersY) % 4;
        }
        return i2;
    }

    public static ModelRotation getModelRotation(int p_177524_0_, int p_177524_1_) {
        return mapRotations.get(ModelRotation.combineXY(MathHelper.normalizeAngle(p_177524_0_, 360), MathHelper.normalizeAngle(p_177524_1_, 360)));
    }

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> p_apply_1_) {
        return (Optional)Reflector.call(Reflector.ForgeHooksClient_applyTransform, this.getMatrix(), p_apply_1_);
    }

    @Override
    public Matrix4f getMatrix() {
        return Reflector.ForgeHooksClient_getMatrix.exists() ? (Matrix4f)Reflector.call(Reflector.ForgeHooksClient_getMatrix, this) : new Matrix4f(this.getMatrix4d());
    }

    @Override
    public EnumFacing rotate(EnumFacing p_rotate_1_) {
        return this.rotateFace(p_rotate_1_);
    }

    @Override
    public int rotate(EnumFacing p_rotate_1_, int p_rotate_2_) {
        return this.rotateVertex(p_rotate_1_, p_rotate_2_);
    }
}

