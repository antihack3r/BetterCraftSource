/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.internal.Internals;
import com.viaversion.viaversion.libs.kyori.adventure.text.AbstractNBTComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.EntityNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EntityNBTComponentImpl
extends NBTComponentImpl<EntityNBTComponent, EntityNBTComponent.Builder>
implements EntityNBTComponent {
    private final String selector;

    static EntityNBTComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, String selector) {
        return new EntityNBTComponentImpl(ComponentLike.asComponents(children, IS_NOT_EMPTY), Objects.requireNonNull(style, "style"), Objects.requireNonNull(nbtPath, "nbtPath"), interpret, ComponentLike.unbox(separator), Objects.requireNonNull(selector, "selector"));
    }

    EntityNBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator, String selector) {
        super(children, style, nbtPath, interpret, separator);
        this.selector = selector;
    }

    @Override
    @NotNull
    public EntityNBTComponent nbtPath(@NotNull String nbtPath) {
        if (Objects.equals(this.nbtPath, nbtPath)) {
            return this;
        }
        return EntityNBTComponentImpl.create(this.children, this.style, nbtPath, this.interpret, this.separator, this.selector);
    }

    @Override
    @NotNull
    public EntityNBTComponent interpret(boolean interpret) {
        if (this.interpret == interpret) {
            return this;
        }
        return EntityNBTComponentImpl.create(this.children, this.style, this.nbtPath, interpret, this.separator, this.selector);
    }

    @Override
    @Nullable
    public Component separator() {
        return this.separator;
    }

    @Override
    @NotNull
    public EntityNBTComponent separator(@Nullable ComponentLike separator) {
        return EntityNBTComponentImpl.create(this.children, this.style, this.nbtPath, this.interpret, separator, this.selector);
    }

    @Override
    @NotNull
    public String selector() {
        return this.selector;
    }

    @Override
    @NotNull
    public EntityNBTComponent selector(@NotNull String selector) {
        if (Objects.equals(this.selector, selector)) {
            return this;
        }
        return EntityNBTComponentImpl.create(this.children, this.style, this.nbtPath, this.interpret, this.separator, selector);
    }

    @Override
    @NotNull
    public EntityNBTComponent children(@NotNull List<? extends ComponentLike> children) {
        return EntityNBTComponentImpl.create(children, this.style, this.nbtPath, this.interpret, this.separator, this.selector);
    }

    @Override
    @NotNull
    public EntityNBTComponent style(@NotNull Style style) {
        return EntityNBTComponentImpl.create(this.children, style, this.nbtPath, this.interpret, this.separator, this.selector);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EntityNBTComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        EntityNBTComponentImpl that = (EntityNBTComponentImpl)other;
        return Objects.equals(this.selector, that.selector());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.selector.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Internals.toString(this);
    }

    @Override
    public @NotNull EntityNBTComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl
    extends AbstractNBTComponentBuilder<EntityNBTComponent, EntityNBTComponent.Builder>
    implements EntityNBTComponent.Builder {
        @Nullable
        private String selector;

        BuilderImpl() {
        }

        BuilderImpl(@NotNull EntityNBTComponent component) {
            super(component);
            this.selector = component.selector();
        }

        @Override
        public @NotNull EntityNBTComponent.Builder selector(@NotNull String selector) {
            this.selector = Objects.requireNonNull(selector, "selector");
            return this;
        }

        @Override
        @NotNull
        public EntityNBTComponent build() {
            if (this.nbtPath == null) {
                throw new IllegalStateException("nbt path must be set");
            }
            if (this.selector == null) {
                throw new IllegalStateException("selector must be set");
            }
            return EntityNBTComponentImpl.create(this.children, this.buildStyle(), this.nbtPath, this.interpret, this.separator, this.selector);
        }
    }
}

