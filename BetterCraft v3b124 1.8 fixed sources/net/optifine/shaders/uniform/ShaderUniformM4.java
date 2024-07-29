/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.uniform;

import java.nio.FloatBuffer;
import net.optifine.shaders.uniform.ShaderUniformBase;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniformM4
extends ShaderUniformBase {
    private boolean transpose;
    private FloatBuffer matrix;

    public ShaderUniformM4(String name) {
        super(name);
    }

    public void setValue(boolean transpose, FloatBuffer matrix) {
        this.transpose = transpose;
        this.matrix = matrix;
        int i2 = this.getLocation();
        if (i2 >= 0) {
            ARBShaderObjects.glUniformMatrix4ARB(i2, transpose, matrix);
            this.checkGLError();
        }
    }

    public float getValue(int row, int col) {
        if (this.matrix == null) {
            return 0.0f;
        }
        int i2 = this.transpose ? col * 4 + row : row * 4 + col;
        float f2 = this.matrix.get(i2);
        return f2;
    }

    @Override
    protected void onProgramSet(int program) {
    }

    @Override
    protected void resetValue() {
        this.matrix = null;
    }
}

