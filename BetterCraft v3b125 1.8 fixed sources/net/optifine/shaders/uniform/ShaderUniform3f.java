/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.uniform;

import net.optifine.shaders.uniform.ShaderUniformBase;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform3f
extends ShaderUniformBase {
    private float[][] programValues;
    private static final float VALUE_UNKNOWN = -3.4028235E38f;

    public ShaderUniform3f(String name) {
        super(name);
        this.resetValue();
    }

    public void setValue(float v0, float v1, float v2) {
        int i2 = this.getProgram();
        float[] afloat = this.programValues[i2];
        if (afloat[0] != v0 || afloat[1] != v1 || afloat[2] != v2) {
            afloat[0] = v0;
            afloat[1] = v1;
            afloat[2] = v2;
            int j2 = this.getLocation();
            if (j2 >= 0) {
                ARBShaderObjects.glUniform3fARB(j2, v0, v1, v2);
                this.checkGLError();
            }
        }
    }

    public float[] getValue() {
        int i2 = this.getProgram();
        float[] afloat = this.programValues[i2];
        return afloat;
    }

    @Override
    protected void onProgramSet(int program) {
        if (program >= this.programValues.length) {
            float[][] afloat = this.programValues;
            float[][] afloat1 = new float[program + 10][];
            System.arraycopy(afloat, 0, afloat1, 0, afloat.length);
            this.programValues = afloat1;
        }
        if (this.programValues[program] == null) {
            this.programValues[program] = new float[]{-3.4028235E38f, -3.4028235E38f, -3.4028235E38f};
        }
    }

    @Override
    protected void resetValue() {
        this.programValues = new float[][]{{-3.4028235E38f, -3.4028235E38f, -3.4028235E38f}};
    }
}

