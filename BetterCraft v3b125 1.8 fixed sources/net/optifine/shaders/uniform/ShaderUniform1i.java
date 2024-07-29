/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.uniform;

import net.optifine.shaders.uniform.ShaderUniformBase;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform1i
extends ShaderUniformBase {
    private int[] programValues;
    private static final int VALUE_UNKNOWN = Integer.MIN_VALUE;

    public ShaderUniform1i(String name) {
        super(name);
        this.resetValue();
    }

    public void setValue(int valueNew) {
        int i2 = this.getProgram();
        int j2 = this.programValues[i2];
        if (valueNew != j2) {
            this.programValues[i2] = valueNew;
            int k2 = this.getLocation();
            if (k2 >= 0) {
                ARBShaderObjects.glUniform1iARB(k2, valueNew);
                this.checkGLError();
            }
        }
    }

    public int getValue() {
        int i2 = this.getProgram();
        int j2 = this.programValues[i2];
        return j2;
    }

    @Override
    protected void onProgramSet(int program) {
        if (program >= this.programValues.length) {
            int[] aint = this.programValues;
            int[] aint1 = new int[program + 10];
            System.arraycopy(aint, 0, aint1, 0, aint.length);
            int i2 = aint.length;
            while (i2 < aint1.length) {
                aint1[i2] = Integer.MIN_VALUE;
                ++i2;
            }
            this.programValues = aint1;
        }
    }

    @Override
    protected void resetValue() {
        this.programValues = new int[]{Integer.MIN_VALUE};
    }
}

