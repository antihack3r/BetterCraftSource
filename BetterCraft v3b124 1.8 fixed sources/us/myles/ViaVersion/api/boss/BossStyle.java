/*
 * Decompiled with CFR 0.152.
 */
package us.myles.ViaVersion.api.boss;

@Deprecated
public enum BossStyle {
    SOLID(0),
    SEGMENTED_6(1),
    SEGMENTED_10(2),
    SEGMENTED_12(3),
    SEGMENTED_20(4);

    private final int id;

    private BossStyle(int id2) {
        this.id = id2;
    }

    public int getId() {
        return this.id;
    }
}

