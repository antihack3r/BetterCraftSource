/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.pattern.BlockPattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern {
    private static final Joiner COMMA_JOIN = Joiner.on(",");
    private final List<String[]> depth = Lists.newArrayList();
    private final Map<Character, Predicate<BlockWorldState>> symbolMap = Maps.newHashMap();
    private int aisleHeight;
    private int rowWidth;

    private FactoryBlockPattern() {
        this.symbolMap.put(Character.valueOf(' '), Predicates.alwaysTrue());
    }

    public FactoryBlockPattern aisle(String ... aisle) {
        if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0])) {
            if (this.depth.isEmpty()) {
                this.aisleHeight = aisle.length;
                this.rowWidth = aisle[0].length();
            }
            if (aisle.length != this.aisleHeight) {
                throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + aisle.length + ")");
            }
            String[] stringArray = aisle;
            int n2 = aisle.length;
            int n3 = 0;
            while (n3 < n2) {
                String s2 = stringArray[n3];
                if (s2.length() != this.rowWidth) {
                    throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + s2.length() + ")");
                }
                char[] cArray = s2.toCharArray();
                int n4 = cArray.length;
                int n5 = 0;
                while (n5 < n4) {
                    char c0 = cArray[n5];
                    if (!this.symbolMap.containsKey(Character.valueOf(c0))) {
                        this.symbolMap.put(Character.valueOf(c0), null);
                    }
                    ++n5;
                }
                ++n3;
            }
            this.depth.add(aisle);
            return this;
        }
        throw new IllegalArgumentException("Empty pattern for aisle");
    }

    public static FactoryBlockPattern start() {
        return new FactoryBlockPattern();
    }

    public FactoryBlockPattern where(char symbol, Predicate<BlockWorldState> blockMatcher) {
        this.symbolMap.put(Character.valueOf(symbol), blockMatcher);
        return this;
    }

    public BlockPattern build() {
        return new BlockPattern(this.makePredicateArray());
    }

    private Predicate<BlockWorldState>[][][] makePredicateArray() {
        this.checkMissingPredicates();
        Predicate[][][] predicate = (Predicate[][][])Array.newInstance(Predicate.class, this.depth.size(), this.aisleHeight, this.rowWidth);
        int i2 = 0;
        while (i2 < this.depth.size()) {
            int j2 = 0;
            while (j2 < this.aisleHeight) {
                int k2 = 0;
                while (k2 < this.rowWidth) {
                    predicate[i2][j2][k2] = this.symbolMap.get(Character.valueOf(this.depth.get(i2)[j2].charAt(k2)));
                    ++k2;
                }
                ++j2;
            }
            ++i2;
        }
        return predicate;
    }

    private void checkMissingPredicates() {
        ArrayList<Character> list = Lists.newArrayList();
        for (Map.Entry<Character, Predicate<BlockWorldState>> entry : this.symbolMap.entrySet()) {
            if (entry.getValue() != null) continue;
            list.add(entry.getKey());
        }
        if (!list.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(list) + " are missing");
        }
    }
}

