/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScopedComponent;
import com.viaversion.viaversion.libs.kyori.adventure.translation.Translatable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TranslatableComponent
extends BuildableComponent<TranslatableComponent, Builder>,
ScopedComponent<TranslatableComponent> {
    @NotNull
    public String key();

    @Contract(pure=true)
    @NotNull
    default public TranslatableComponent key(@NotNull Translatable translatable) {
        return this.key(Objects.requireNonNull(translatable, "translatable").translationKey());
    }

    @Contract(pure=true)
    @NotNull
    public TranslatableComponent key(@NotNull String var1);

    @NotNull
    public List<Component> args();

    @Contract(pure=true)
    @NotNull
    public TranslatableComponent args(ComponentLike ... var1);

    @Contract(pure=true)
    @NotNull
    public TranslatableComponent args(@NotNull List<? extends ComponentLike> var1);

    @Nullable
    public String fallback();

    @Contract(pure=true)
    @NotNull
    public TranslatableComponent fallback(@Nullable String var1);

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("key", this.key()), ExaminableProperty.of("args", this.args()), ExaminableProperty.of("fallback", this.fallback())), BuildableComponent.super.examinableProperties());
    }

    public static interface Builder
    extends ComponentBuilder<TranslatableComponent, Builder> {
        @Contract(pure=true)
        @NotNull
        default public Builder key(@NotNull Translatable translatable) {
            return this.key(Objects.requireNonNull(translatable, "translatable").translationKey());
        }

        @Contract(value="_ -> this")
        @NotNull
        public Builder key(@NotNull String var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder args(@NotNull ComponentBuilder<?, ?> var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder args(ComponentBuilder<?, ?> ... var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder args(@NotNull Component var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder args(ComponentLike ... var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder args(@NotNull List<? extends ComponentLike> var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder fallback(@Nullable String var1);
    }
}

