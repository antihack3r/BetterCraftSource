// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.kyori.adventure.text;

import org.jetbrains.annotations.Contract;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@FunctionalInterface
public interface ComponentLike
{
    @NotNull
    default List<Component> asComponents(@NotNull final List<? extends ComponentLike> likes) {
        return asComponents(likes, null);
    }
    
    @NotNull
    default List<Component> asComponents(@NotNull final List<? extends ComponentLike> likes, @Nullable final Predicate<? super Component> filter) {
        Objects.requireNonNull(likes, "likes");
        final int size = likes.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        ArrayList<Component> components = null;
        for (int i = 0; i < size; ++i) {
            final ComponentLike like = (ComponentLike)likes.get(i);
            if (like == null) {
                throw new NullPointerException("likes[" + i + "]");
            }
            final Component component = like.asComponent();
            if (filter == null || filter.test(component)) {
                if (components == null) {
                    components = new ArrayList<Component>(size);
                }
                components.add(component);
            }
        }
        if (components == null) {
            return Collections.emptyList();
        }
        components.trimToSize();
        return Collections.unmodifiableList((List<? extends Component>)components);
    }
    
    @Nullable
    default Component unbox(@Nullable final ComponentLike like) {
        return (like != null) ? like.asComponent() : null;
    }
    
    @Contract(pure = true)
    @NotNull
    Component asComponent();
}
