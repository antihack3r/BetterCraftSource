/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

public class Assert {
    public static void fail(Exception e2) {
        Assert.fail(e2.toString());
    }

    public static void fail(String msg) {
        throw new IllegalStateException("failure '" + msg + "'");
    }

    public static void assrt(boolean val) {
        if (!val) {
            throw new IllegalStateException("assert failed");
        }
    }

    public static void assrt(String msg, boolean val) {
        if (!val) {
            throw new IllegalStateException("assert '" + msg + "' failed");
        }
    }
}

