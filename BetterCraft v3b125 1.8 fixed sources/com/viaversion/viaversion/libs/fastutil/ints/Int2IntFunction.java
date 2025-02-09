/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.libs.fastutil.bytes.Byte2IntFunction
 *  com.viaversion.viaversion.libs.fastutil.chars.Char2IntFunction
 *  com.viaversion.viaversion.libs.fastutil.doubles.Double2IntFunction
 *  com.viaversion.viaversion.libs.fastutil.floats.Float2IntFunction
 *  com.viaversion.viaversion.libs.fastutil.ints.Int2ByteFunction
 *  com.viaversion.viaversion.libs.fastutil.ints.Int2CharFunction
 *  com.viaversion.viaversion.libs.fastutil.ints.Int2DoubleFunction
 *  com.viaversion.viaversion.libs.fastutil.ints.Int2FloatFunction
 *  com.viaversion.viaversion.libs.fastutil.ints.Int2LongFunction
 *  com.viaversion.viaversion.libs.fastutil.ints.Int2ReferenceFunction
 *  com.viaversion.viaversion.libs.fastutil.ints.Int2ShortFunction
 *  com.viaversion.viaversion.libs.fastutil.longs.Long2IntFunction
 *  com.viaversion.viaversion.libs.fastutil.objects.Reference2IntFunction
 *  com.viaversion.viaversion.libs.fastutil.shorts.Short2IntFunction
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Function;
import com.viaversion.viaversion.libs.fastutil.bytes.Byte2IntFunction;
import com.viaversion.viaversion.libs.fastutil.chars.Char2IntFunction;
import com.viaversion.viaversion.libs.fastutil.doubles.Double2IntFunction;
import com.viaversion.viaversion.libs.fastutil.floats.Float2IntFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ByteFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2CharFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2DoubleFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2FloatFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2LongFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ReferenceFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ShortFunction;
import com.viaversion.viaversion.libs.fastutil.longs.Long2IntFunction;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntFunction;
import com.viaversion.viaversion.libs.fastutil.objects.Reference2IntFunction;
import com.viaversion.viaversion.libs.fastutil.shorts.Short2IntFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Int2IntFunction
extends Function<Integer, Integer>,
IntUnaryOperator {
    @Override
    default public int applyAsInt(int operand) {
        return this.get(operand);
    }

    @Override
    default public int put(int key, int value) {
        throw new UnsupportedOperationException();
    }

    public int get(int var1);

    default public int getOrDefault(int key, int defaultValue) {
        int v2 = this.get(key);
        return v2 != this.defaultReturnValue() || this.containsKey(key) ? v2 : defaultValue;
    }

    default public int remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Integer put(Integer key, Integer value) {
        int k2 = key;
        boolean containsKey = this.containsKey(k2);
        int v2 = this.put(k2, (int)value);
        return containsKey ? Integer.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    default public Integer get(Object key) {
        if (key == null) {
            return null;
        }
        int k2 = (Integer)key;
        int v2 = this.get(k2);
        return v2 != this.defaultReturnValue() || this.containsKey(k2) ? Integer.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    default public Integer getOrDefault(Object key, Integer defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        int k2 = (Integer)key;
        int v2 = this.get(k2);
        return v2 != this.defaultReturnValue() || this.containsKey(k2) ? Integer.valueOf(v2) : defaultValue;
    }

    @Override
    @Deprecated
    default public Integer remove(Object key) {
        if (key == null) {
            return null;
        }
        int k2 = (Integer)key;
        return this.containsKey(k2) ? Integer.valueOf(this.remove(k2)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(int rv2) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }

    public static Int2IntFunction identity() {
        return k2 -> k2;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Integer> compose(java.util.function.Function<? super T, ? extends Integer> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Integer, T> andThen(java.util.function.Function<? super Integer, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Int2ByteFunction andThenByte(Int2ByteFunction after) {
        return k2 -> after.get(this.get(k2));
    }

    default public Byte2IntFunction composeByte(Byte2IntFunction before) {
        return k2 -> this.get(before.get(k2));
    }

    default public Int2ShortFunction andThenShort(Int2ShortFunction after) {
        return k2 -> after.get(this.get(k2));
    }

    default public Short2IntFunction composeShort(Short2IntFunction before) {
        return k2 -> this.get(before.get(k2));
    }

    default public Int2IntFunction andThenInt(Int2IntFunction after) {
        return k2 -> after.get(this.get(k2));
    }

    default public Int2IntFunction composeInt(Int2IntFunction before) {
        return k2 -> this.get(before.get(k2));
    }

    default public Int2LongFunction andThenLong(Int2LongFunction after) {
        return k2 -> after.get(this.get(k2));
    }

    default public Long2IntFunction composeLong(Long2IntFunction before) {
        return k2 -> this.get(before.get(k2));
    }

    default public Int2CharFunction andThenChar(Int2CharFunction after) {
        return k2 -> after.get(this.get(k2));
    }

    default public Char2IntFunction composeChar(Char2IntFunction before) {
        return k2 -> this.get(before.get(k2));
    }

    default public Int2FloatFunction andThenFloat(Int2FloatFunction after) {
        return k2 -> after.get(this.get(k2));
    }

    default public Float2IntFunction composeFloat(Float2IntFunction before) {
        return k2 -> this.get(before.get(k2));
    }

    default public Int2DoubleFunction andThenDouble(Int2DoubleFunction after) {
        return k2 -> after.get(this.get(k2));
    }

    default public Double2IntFunction composeDouble(Double2IntFunction before) {
        return k2 -> this.get(before.get(k2));
    }

    default public <T> Int2ObjectFunction<T> andThenObject(Int2ObjectFunction<? extends T> after) {
        return k2 -> after.get(this.get(k2));
    }

    default public <T> Object2IntFunction<T> composeObject(Object2IntFunction<? super T> before) {
        return k2 -> this.get(before.getInt(k2));
    }

    default public <T> Int2ReferenceFunction<T> andThenReference(Int2ReferenceFunction<? extends T> after) {
        return k2 -> after.get(this.get(k2));
    }

    default public <T> Reference2IntFunction<T> composeReference(Reference2IntFunction<? super T> before) {
        return k2 -> this.get(before.getInt(k2));
    }
}

