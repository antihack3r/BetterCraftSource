/*
 * Decompiled with CFR 0.152.
 */
package us.myles.ViaVersion.api.boss;

@Deprecated
public enum BossColor {
    PINK(0),
    BLUE(1),
    RED(2),
    GREEN(3),
    YELLOW(4),
    PURPLE(5),
    WHITE(6);

    private final int id;

    private BossColor(int id2) {
        this.id = id2;
    }

    public int getId() {
        return this.id;
    }
}

