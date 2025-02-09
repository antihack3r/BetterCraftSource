/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.opengl;

import org.lwjgl.opengl.GL42;

public final class ARBTransformFeedbackInstanced {
    private ARBTransformFeedbackInstanced() {
    }

    public static void glDrawTransformFeedbackInstanced(int mode, int id2, int primcount) {
        GL42.glDrawTransformFeedbackInstanced(mode, id2, primcount);
    }

    public static void glDrawTransformFeedbackStreamInstanced(int mode, int id2, int stream, int primcount) {
        GL42.glDrawTransformFeedbackStreamInstanced(mode, id2, stream, primcount);
    }
}

