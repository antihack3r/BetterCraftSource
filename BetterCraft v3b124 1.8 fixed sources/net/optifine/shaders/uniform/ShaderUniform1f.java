/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.uniform;

import net.optifine.shaders.uniform.ShaderUniformBase;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform1f
extends ShaderUniformBase {
    private float[] programValues;
    private static final float VALUE_UNKNOWN = -3.4028235E38f;

    public ShaderUniform1f(String name) {
        super(name);
        this.resetValue();
    }

    public void setValue(float valueNew) {
        int i2 = this.getProgram();
        float f2 = this.programValues[i2];
        if (valueNew != f2) {
            this.programValues[i2] = valueNew;
            int j2 = this.getLocation();
            if (j2 >= 0) {
                ARBShaderObjects.glUniform1fARB(j2, valueNew);
                this.checkGLError();
            }
        }
    }

    public float getValue() {
        int i2 = this.getProgram();
        float f2 = this.programValues[i2];
        return f2;
    }

    @Override
    protected void onProgramSet(int program) {
        if (program >= this.programValues.length) {
            float[] afloat = this.programValues;
            float[] afloat1 = new float[program + 10];
            System.arraycopy(afloat, 0, afloat1, 0, afloat.length);
            int i2 = afloat.length;
            while (i2 < afloat1.length) {
                afloat1[i2] = -3.4028235E38f;
                ++i2;
            }
            this.programValues = afloat1;
        }
    }

    @Override
    protected void resetValue() {
        this.programValues = new float[]{-3.4028235E38f};
    }
}

