/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.uniform;

import net.optifine.shaders.uniform.ShaderUniformBase;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform2i
extends ShaderUniformBase {
    private int[][] programValues;
    private static final int VALUE_UNKNOWN = Integer.MIN_VALUE;

    public ShaderUniform2i(String name) {
        super(name);
        this.resetValue();
    }

    public void setValue(int v0, int v1) {
        int i2 = this.getProgram();
        int[] aint = this.programValues[i2];
        if (aint[0] != v0 || aint[1] != v1) {
            aint[0] = v0;
            aint[1] = v1;
            int j2 = this.getLocation();
            if (j2 >= 0) {
                ARBShaderObjects.glUniform2iARB(j2, v0, v1);
                this.checkGLError();
            }
        }
    }

    public int[] getValue() {
        int i2 = this.getProgram();
        int[] aint = this.programValues[i2];
        return aint;
    }

    @Override
    protected void onProgramSet(int program) {
        if (program >= this.programValues.length) {
            int[][] aint = this.programValues;
            int[][] aint1 = new int[program + 10][];
            System.arraycopy(aint, 0, aint1, 0, aint.length);
            this.programValues = aint1;
        }
        if (this.programValues[program] == null) {
            this.programValues[program] = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
        }
    }

    @Override
    protected void resetValue() {
        this.programValues = new int[][]{{Integer.MIN_VALUE, Integer.MIN_VALUE}};
    }
}
