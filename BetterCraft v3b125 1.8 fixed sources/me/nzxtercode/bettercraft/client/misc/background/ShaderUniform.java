/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.background;

import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderUniform {
    private static final String VERTEX_SHADER = "#version 130\n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}";
    private Minecraft mc = Minecraft.getMinecraft();
    private int program = GL20.glCreateProgram();
    private long startTime = System.currentTimeMillis();
    float mousemove = 0.01f;

    public ShaderUniform(String fragment) {
        this.initShader(fragment);
    }

    private void initShader(String frag) {
        int vertex = GL20.glCreateShader(35633);
        int fragment = GL20.glCreateShader(35632);
        GL20.glShaderSource(vertex, VERTEX_SHADER);
        GL20.glShaderSource(fragment, frag);
        GL20.glValidateProgram(this.program);
        GL20.glCompileShader(vertex);
        GL20.glCompileShader(fragment);
        GL20.glAttachShader(this.program, vertex);
        GL20.glAttachShader(this.program, fragment);
        GL20.glLinkProgram(this.program);
    }

    public void renderFirst() {
        GL11.glClear(16640);
        GL20.glUseProgram(this.program);
    }

    public void renderSecond() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        ScaledResolution sr2 = new ScaledResolution(this.mc);
        GL11.glBegin(7);
        GL11.glTexCoord2d(0.0, 1.0);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glTexCoord2d(0.0, 0.0);
        GL11.glVertex2d(0.0, sr2.getScaledHeight());
        GL11.glTexCoord2d(1.0, 0.0);
        GL11.glVertex2d(sr2.getScaledWidth(), sr2.getScaledHeight());
        GL11.glTexCoord2d(1.0, 1.0);
        GL11.glVertex2d(sr2.getScaledWidth(), 0.0);
        GL11.glEnd();
        GL20.glUseProgram(0);
    }

    public void bind() {
        GL20.glUseProgram(this.program);
    }

    public int getProgram() {
        return this.program;
    }

    public ShaderUniform uniform1i(String loc, int i2) {
        GL20.glUniform1i(GL20.glGetUniformLocation(this.program, loc), i2);
        return this;
    }

    public ShaderUniform uniform2i(String loc, int i2, int i1) {
        GL20.glUniform2i(GL20.glGetUniformLocation(this.program, loc), i2, i1);
        return this;
    }

    public ShaderUniform uniform3i(String loc, int i2, int i1, int i22) {
        GL20.glUniform3i(GL20.glGetUniformLocation(this.program, loc), i2, i1, i22);
        return this;
    }

    public ShaderUniform uniform4i(String loc, int i2, int i1, int i22, int i3) {
        GL20.glUniform4i(GL20.glGetUniformLocation(this.program, loc), i2, i1, i22, i3);
        return this;
    }

    public ShaderUniform uniform1f(String loc, float f2) {
        GL20.glUniform1f(GL20.glGetUniformLocation(this.program, loc), f2);
        return this;
    }

    public ShaderUniform uniform2f(String loc, float f2, float f1) {
        GL20.glUniform2f(GL20.glGetUniformLocation(this.program, loc), f2, f1);
        return this;
    }

    public ShaderUniform uniform3f(String loc, float f2, float f1, float f22) {
        GL20.glUniform3f(GL20.glGetUniformLocation(this.program, loc), f2, f1, f22);
        return this;
    }

    public ShaderUniform uniform4f(String loc, float f2, float f1, float f22, float f3) {
        GL20.glUniform4f(GL20.glGetUniformLocation(this.program, loc), f2, f1, f22, f3);
        return this;
    }

    public ShaderUniform uniform1b(String loc, boolean b2) {
        GL20.glUniform1i(GL20.glGetUniformLocation(this.program, loc), b2 ? 1 : 0);
        return this;
    }

    public void addDefaultUniforms(boolean detectmouse) {
        this.mousemove = Mouse.getX() > 957 ? (this.mousemove = this.mousemove - 0.002f) : (this.mousemove = this.mousemove + 0.002f);
        float n3 = (float)Mouse.getX() / (float)this.mc.displayWidth;
        float n4 = (float)Mouse.getY() / (float)this.mc.displayHeight;
        FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(2);
        floatBuffer3.position(0);
        floatBuffer3.put(n3);
        floatBuffer3.put(n4);
        floatBuffer3.flip();
        GL20.glUniform2f(GL20.glGetUniformLocation(this.program, "resolution"), this.mc.displayWidth, this.mc.displayHeight);
        float time = (float)(System.currentTimeMillis() - this.startTime) / 1000.0f;
        GL20.glUniform1f(GL20.glGetUniformLocation(this.program, "time"), time);
        if (detectmouse) {
            GL20.glUniform2f(GL20.glGetUniformLocation(this.program, "mouse"), this.mousemove, 0.0f);
        } else {
            GL20.glUniform2(GL20.glGetUniformLocation(this.program, "mouse"), floatBuffer3);
        }
    }
}

