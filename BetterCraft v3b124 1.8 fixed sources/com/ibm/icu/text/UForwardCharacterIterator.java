/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

public interface UForwardCharacterIterator {
    public static final int DONE = -1;

    public int next();

    public int nextCodePoint();
}

