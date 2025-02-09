/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL40;

public final class ARBTransformFeedback3 {
    public static final int GL_MAX_TRANSFORM_FEEDBACK_BUFFERS = 36464;
    public static final int GL_MAX_VERTEX_STREAMS = 36465;

    private ARBTransformFeedback3() {
    }

    public static void glDrawTransformFeedbackStream(int mode, int id2, int stream) {
        GL40.glDrawTransformFeedbackStream(mode, id2, stream);
    }

    public static void glBeginQueryIndexed(int target, int index, int id2) {
        GL40.glBeginQueryIndexed(target, index, id2);
    }

    public static void glEndQueryIndexed(int target, int index) {
        GL40.glEndQueryIndexed(target, index);
    }

    public static void glGetQueryIndexed(int target, int index, int pname, IntBuffer params) {
        GL40.glGetQueryIndexed(target, index, pname, params);
    }

    public static int glGetQueryIndexedi(int target, int index, int pname) {
        return GL40.glGetQueryIndexedi(target, index, pname);
    }
}

