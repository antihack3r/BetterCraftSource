/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.config;

import net.minecraft.src.Config;
import net.optifine.config.RangeInt;

public class RangeListInt {
    private RangeInt[] ranges = new RangeInt[0];

    public RangeListInt() {
    }

    public RangeListInt(RangeInt ri2) {
        this.addRange(ri2);
    }

    public void addRange(RangeInt ri2) {
        this.ranges = (RangeInt[])Config.addObjectToArray(this.ranges, ri2);
    }

    public boolean isInRange(int val) {
        int i2 = 0;
        while (i2 < this.ranges.length) {
            RangeInt rangeint = this.ranges[i2];
            if (rangeint.isInRange(val)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public int getCountRanges() {
        return this.ranges.length;
    }

    public RangeInt getRange(int i2) {
        return this.ranges[i2];
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("[");
        int i2 = 0;
        while (i2 < this.ranges.length) {
            RangeInt rangeint = this.ranges[i2];
            if (i2 > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(rangeint.toString());
            ++i2;
        }
        stringbuffer.append("]");
        return stringbuffer.toString();
    }
}

