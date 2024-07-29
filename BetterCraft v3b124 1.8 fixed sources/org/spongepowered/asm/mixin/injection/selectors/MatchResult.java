/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.selectors;

public enum MatchResult {
    NONE,
    WEAK,
    MATCH,
    EXACT_MATCH;


    public boolean isAtLeast(MatchResult other) {
        return other == null || other.ordinal() <= this.ordinal();
    }

    public boolean isMatch() {
        return this.ordinal() >= MATCH.ordinal();
    }

    public boolean isExactMatch() {
        return this == EXACT_MATCH;
    }
}

