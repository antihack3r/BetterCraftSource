/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.NumberBinaryTag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BinaryTagType<T extends BinaryTag>
implements Predicate<BinaryTagType<? extends BinaryTag>> {
    private static final List<BinaryTagType<? extends BinaryTag>> TYPES = new ArrayList<BinaryTagType<? extends BinaryTag>>();

    public abstract byte id();

    abstract boolean numeric();

    @NotNull
    public abstract T read(@NotNull DataInput var1) throws IOException;

    public abstract void write(@NotNull T var1, @NotNull DataOutput var2) throws IOException;

    static <T extends BinaryTag> void writeUntyped(BinaryTagType<? extends BinaryTag> type, T tag, DataOutput output) throws IOException {
        type.write(tag, output);
    }

    @NotNull
    static BinaryTagType<? extends BinaryTag> binaryTagType(byte id2) {
        for (int i2 = 0; i2 < TYPES.size(); ++i2) {
            BinaryTagType<? extends BinaryTag> type = TYPES.get(i2);
            if (type.id() != id2) continue;
            return type;
        }
        throw new IllegalArgumentException(String.valueOf(id2));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    static BinaryTagType<? extends BinaryTag> of(byte id2) {
        return BinaryTagType.binaryTagType(id2);
    }

    @NotNull
    static <T extends BinaryTag> BinaryTagType<T> register(Class<T> type, byte id2, Reader<T> reader, @Nullable Writer<T> writer) {
        return BinaryTagType.register(new Impl<T>(type, id2, reader, writer));
    }

    @NotNull
    static <T extends NumberBinaryTag> BinaryTagType<T> registerNumeric(Class<T> type, byte id2, Reader<T> reader, Writer<T> writer) {
        return BinaryTagType.register(new Impl.Numeric<T>(type, id2, reader, writer));
    }

    private static <T extends BinaryTag, Y extends BinaryTagType<T>> Y register(Y type) {
        TYPES.add(type);
        return type;
    }

    @Override
    public boolean test(BinaryTagType<? extends BinaryTag> that) {
        return this == that || this.numeric() && that.numeric();
    }

    static class Impl<T extends BinaryTag>
    extends BinaryTagType<T> {
        final Class<T> type;
        final byte id;
        private final Reader<T> reader;
        @Nullable
        private final Writer<T> writer;

        Impl(Class<T> type, byte id2, Reader<T> reader, @Nullable Writer<T> writer) {
            this.type = type;
            this.id = id2;
            this.reader = reader;
            this.writer = writer;
        }

        @Override
        @NotNull
        public final T read(@NotNull DataInput input) throws IOException {
            return this.reader.read(input);
        }

        @Override
        public final void write(@NotNull T tag, @NotNull DataOutput output) throws IOException {
            if (this.writer != null) {
                this.writer.write(tag, output);
            }
        }

        @Override
        public final byte id() {
            return this.id;
        }

        @Override
        boolean numeric() {
            return false;
        }

        public String toString() {
            return BinaryTagType.class.getSimpleName() + '[' + this.type.getSimpleName() + " " + this.id + "]";
        }

        static class Numeric<T extends BinaryTag>
        extends Impl<T> {
            Numeric(Class<T> type, byte id2, Reader<T> reader, @Nullable Writer<T> writer) {
                super(type, id2, reader, writer);
            }

            @Override
            boolean numeric() {
                return true;
            }

            @Override
            public String toString() {
                return BinaryTagType.class.getSimpleName() + '[' + this.type.getSimpleName() + " " + this.id + " (numeric)]";
            }
        }
    }

    static interface Writer<T extends BinaryTag> {
        public void write(@NotNull T var1, @NotNull DataOutput var2) throws IOException;
    }

    static interface Reader<T extends BinaryTag> {
        @NotNull
        public T read(@NotNull DataInput var1) throws IOException;
    }
}

