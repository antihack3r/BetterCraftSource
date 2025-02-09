/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.opengl;

import java.nio.LongBuffer;
import org.lwjgl.opengl.GL33;

public final class ARBTimerQuery {
    public static final int GL_TIME_ELAPSED = 35007;
    public static final int GL_TIMESTAMP = 36392;

    private ARBTimerQuery() {
    }

    public static void glQueryCounter(int id2, int target) {
        GL33.glQueryCounter(id2, target);
    }

    public static void glGetQueryObject(int id2, int pname, LongBuffer params) {
        GL33.glGetQueryObject(id2, pname, params);
    }

    public static long glGetQueryObjecti64(int id2, int pname) {
        return GL33.glGetQueryObjecti64(id2, pname);
    }

    public static void glGetQueryObjectu(int id2, int pname, LongBuffer params) {
        GL33.glGetQueryObjectu(id2, pname, params);
    }

    public static long glGetQueryObjectui64(int id2, int pname) {
        return GL33.glGetQueryObjectui64(id2, pname);
    }
}

