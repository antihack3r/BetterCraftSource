/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.NVProgram;

public final class NVFragmentProgram
extends NVProgram {
    public static final int GL_FRAGMENT_PROGRAM_NV = 34928;
    public static final int GL_MAX_TEXTURE_COORDS_NV = 34929;
    public static final int GL_MAX_TEXTURE_IMAGE_UNITS_NV = 34930;
    public static final int GL_FRAGMENT_PROGRAM_BINDING_NV = 34931;
    public static final int GL_MAX_FRAGMENT_PROGRAM_LOCAL_PARAMETERS_NV = 34920;

    private NVFragmentProgram() {
    }

    public static void glProgramNamedParameter4fNV(int id2, ByteBuffer name, float x2, float y2, float z2, float w2) {
        ContextCapabilities caps = GLContext.getCapabilities();
        long function_pointer = caps.glProgramNamedParameter4fNV;
        BufferChecks.checkFunctionAddress(function_pointer);
        BufferChecks.checkDirect(name);
        NVFragmentProgram.nglProgramNamedParameter4fNV(id2, name.remaining(), MemoryUtil.getAddress(name), x2, y2, z2, w2, function_pointer);
    }

    static native void nglProgramNamedParameter4fNV(int var0, int var1, long var2, float var4, float var5, float var6, float var7, long var8);

    public static void glProgramNamedParameter4dNV(int id2, ByteBuffer name, double x2, double y2, double z2, double w2) {
        ContextCapabilities caps = GLContext.getCapabilities();
        long function_pointer = caps.glProgramNamedParameter4dNV;
        BufferChecks.checkFunctionAddress(function_pointer);
        BufferChecks.checkDirect(name);
        NVFragmentProgram.nglProgramNamedParameter4dNV(id2, name.remaining(), MemoryUtil.getAddress(name), x2, y2, z2, w2, function_pointer);
    }

    static native void nglProgramNamedParameter4dNV(int var0, int var1, long var2, double var4, double var6, double var8, double var10, long var12);

    public static void glGetProgramNamedParameterNV(int id2, ByteBuffer name, FloatBuffer params) {
        ContextCapabilities caps = GLContext.getCapabilities();
        long function_pointer = caps.glGetProgramNamedParameterfvNV;
        BufferChecks.checkFunctionAddress(function_pointer);
        BufferChecks.checkDirect(name);
        BufferChecks.checkBuffer(params, 4);
        NVFragmentProgram.nglGetProgramNamedParameterfvNV(id2, name.remaining(), MemoryUtil.getAddress(name), MemoryUtil.getAddress(params), function_pointer);
    }

    static native void nglGetProgramNamedParameterfvNV(int var0, int var1, long var2, long var4, long var6);

    public static void glGetProgramNamedParameterNV(int id2, ByteBuffer name, DoubleBuffer params) {
        ContextCapabilities caps = GLContext.getCapabilities();
        long function_pointer = caps.glGetProgramNamedParameterdvNV;
        BufferChecks.checkFunctionAddress(function_pointer);
        BufferChecks.checkDirect(name);
        BufferChecks.checkBuffer(params, 4);
        NVFragmentProgram.nglGetProgramNamedParameterdvNV(id2, name.remaining(), MemoryUtil.getAddress(name), MemoryUtil.getAddress(params), function_pointer);
    }

    static native void nglGetProgramNamedParameterdvNV(int var0, int var1, long var2, long var4, long var6);
}

