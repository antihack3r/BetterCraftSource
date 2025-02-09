/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft;

public enum Environment {
    NORMAL(0),
    NETHER(-1),
    END(1);

    private final int id;

    private Environment(int id2) {
        this.id = id2;
    }

    public int id() {
        return this.id;
    }

    @Deprecated
    public int getId() {
        return this.id;
    }

    public static Environment getEnvironmentById(int id2) {
        switch (id2) {
            default: {
                return NETHER;
            }
            case 0: {
                return NORMAL;
            }
            case 1: 
        }
        return END;
    }
}

