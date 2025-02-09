/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSilverfish
extends ModelBase {
    private ModelRenderer[] silverfishBodyParts = new ModelRenderer[7];
    private ModelRenderer[] silverfishWings;
    private float[] field_78170_c = new float[7];
    private static final int[][] silverfishBoxLength = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] silverfishTexturePositions;

    static {
        int[][] nArrayArray = new int[7][];
        nArrayArray[0] = new int[2];
        int[] nArray = new int[2];
        nArray[1] = 4;
        nArrayArray[1] = nArray;
        int[] nArray2 = new int[2];
        nArray2[1] = 9;
        nArrayArray[2] = nArray2;
        int[] nArray3 = new int[2];
        nArray3[1] = 16;
        nArrayArray[3] = nArray3;
        int[] nArray4 = new int[2];
        nArray4[1] = 22;
        nArrayArray[4] = nArray4;
        int[] nArray5 = new int[2];
        nArray5[0] = 11;
        nArrayArray[5] = nArray5;
        nArrayArray[6] = new int[]{13, 4};
        silverfishTexturePositions = nArrayArray;
    }

    public ModelSilverfish() {
        float f2 = -3.5f;
        int i2 = 0;
        while (i2 < this.silverfishBodyParts.length) {
            this.silverfishBodyParts[i2] = new ModelRenderer(this, silverfishTexturePositions[i2][0], silverfishTexturePositions[i2][1]);
            this.silverfishBodyParts[i2].addBox((float)silverfishBoxLength[i2][0] * -0.5f, 0.0f, (float)silverfishBoxLength[i2][2] * -0.5f, silverfishBoxLength[i2][0], silverfishBoxLength[i2][1], silverfishBoxLength[i2][2]);
            this.silverfishBodyParts[i2].setRotationPoint(0.0f, 24 - silverfishBoxLength[i2][1], f2);
            this.field_78170_c[i2] = f2;
            if (i2 < this.silverfishBodyParts.length - 1) {
                f2 += (float)(silverfishBoxLength[i2][2] + silverfishBoxLength[i2 + 1][2]) * 0.5f;
            }
            ++i2;
        }
        this.silverfishWings = new ModelRenderer[3];
        this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
        this.silverfishWings[0].addBox(-5.0f, 0.0f, (float)silverfishBoxLength[2][2] * -0.5f, 10, 8, silverfishBoxLength[2][2]);
        this.silverfishWings[0].setRotationPoint(0.0f, 16.0f, this.field_78170_c[2]);
        this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
        this.silverfishWings[1].addBox(-3.0f, 0.0f, (float)silverfishBoxLength[4][2] * -0.5f, 6, 4, silverfishBoxLength[4][2]);
        this.silverfishWings[1].setRotationPoint(0.0f, 20.0f, this.field_78170_c[4]);
        this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
        this.silverfishWings[2].addBox(-3.0f, 0.0f, (float)silverfishBoxLength[4][2] * -0.5f, 6, 5, silverfishBoxLength[1][2]);
        this.silverfishWings[2].setRotationPoint(0.0f, 19.0f, this.field_78170_c[1]);
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        int i2 = 0;
        while (i2 < this.silverfishBodyParts.length) {
            this.silverfishBodyParts[i2].render(scale);
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.silverfishWings.length) {
            this.silverfishWings[j2].render(scale);
            ++j2;
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        int i2 = 0;
        while (i2 < this.silverfishBodyParts.length) {
            this.silverfishBodyParts[i2].rotateAngleY = MathHelper.cos(ageInTicks * 0.9f + (float)i2 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.05f * (float)(1 + Math.abs(i2 - 2));
            this.silverfishBodyParts[i2].rotationPointX = MathHelper.sin(ageInTicks * 0.9f + (float)i2 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.2f * (float)Math.abs(i2 - 2);
            ++i2;
        }
        this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
        this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
        this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
        this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
        this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
    }
}

