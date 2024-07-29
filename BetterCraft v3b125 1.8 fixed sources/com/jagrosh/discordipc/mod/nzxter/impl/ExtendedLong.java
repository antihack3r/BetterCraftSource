/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.impl;

public class ExtendedLong {
    public static int hashCode(long value) {
        return (int)(value ^ value >>> 32);
    }
}

