/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.renderer;

import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.EntityNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScoreComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.SelectorComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.StorageNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.renderer.AbstractComponentRenderer;
import com.viaversion.viaversion.libs.kyori.adventure.translation.Translator;
import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TranslatableComponentRenderer<C>
extends AbstractComponentRenderer<C> {
    private static final Set<Style.Merge> MERGES = Style.Merge.merges(Style.Merge.COLOR, Style.Merge.DECORATIONS, Style.Merge.INSERTION, Style.Merge.FONT);

    @NotNull
    public static TranslatableComponentRenderer<Locale> usingTranslationSource(final @NotNull Translator source) {
        Objects.requireNonNull(source, "source");
        return new TranslatableComponentRenderer<Locale>(){

            @Override
            @Nullable
            protected MessageFormat translate(@NotNull String key, @NotNull Locale context) {
                return source.translate(key, context);
            }

            @Override
            @NotNull
            protected Component renderTranslatable(@NotNull TranslatableComponent component, @NotNull Locale context) {
                @Nullable Component translated = source.translate(component, context);
                if (translated != null) {
                    return translated;
                }
                return super.renderTranslatable(component, context);
            }
        };
    }

    @Nullable
    protected MessageFormat translate(@NotNull String key, @NotNull C context) {
        return null;
    }

    @Nullable
    protected MessageFormat translate(@NotNull String key, @Nullable String fallback, @NotNull C context) {
        return this.translate(key, context);
    }

    @Override
    @NotNull
    protected Component renderBlockNbt(@NotNull BlockNBTComponent component, @NotNull C context) {
        BlockNBTComponent.Builder builder = this.nbt(context, Component.blockNBT(), component).pos(component.pos());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderEntityNbt(@NotNull EntityNBTComponent component, @NotNull C context) {
        EntityNBTComponent.Builder builder = this.nbt(context, Component.entityNBT(), component).selector(component.selector());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderStorageNbt(@NotNull StorageNBTComponent component, @NotNull C context) {
        StorageNBTComponent.Builder builder = this.nbt(context, Component.storageNBT(), component).storage(component.storage());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    protected <O extends NBTComponent<O, B>, B extends NBTComponentBuilder<O, B>> B nbt(@NotNull C context, B builder, O oldComponent) {
        builder.nbtPath(oldComponent.nbtPath()).interpret(oldComponent.interpret());
        @Nullable Component separator = oldComponent.separator();
        if (separator != null) {
            builder.separator(this.render(separator, context));
        }
        return builder;
    }

    @Override
    @NotNull
    protected Component renderKeybind(@NotNull KeybindComponent component, @NotNull C context) {
        KeybindComponent.Builder builder = Component.keybind().keybind(component.keybind());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderScore(@NotNull ScoreComponent component, @NotNull C context) {
        ScoreComponent.Builder builder = Component.score().name(component.name()).objective(component.objective()).value(component.value());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderSelector(@NotNull SelectorComponent component, @NotNull C context) {
        SelectorComponent.Builder builder = Component.selector().pattern(component.pattern());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderText(@NotNull TextComponent component, @NotNull C context) {
        TextComponent.Builder builder = Component.text().content(component.content());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderTranslatable(@NotNull TranslatableComponent component, @NotNull C context) {
        @Nullable MessageFormat format = this.translate(component.key(), component.fallback(), context);
        if (format == null) {
            TranslatableComponent.Builder builder = Component.translatable().key(component.key()).fallback(component.fallback());
            if (!component.args().isEmpty()) {
                ArrayList<Component> args = new ArrayList<Component>(component.args());
                int size = args.size();
                for (int i2 = 0; i2 < size; ++i2) {
                    args.set(i2, this.render((Component)args.get(i2), context));
                }
                builder.args(args);
            }
            return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
        }
        List<Component> args = component.args();
        TextComponent.Builder builder = Component.text();
        this.mergeStyle(component, builder, context);
        if (args.isEmpty()) {
            builder.content(format.format(null, new StringBuffer(), null).toString());
            return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
        }
        Object[] nulls = new Object[args.size()];
        StringBuffer sb2 = format.format(nulls, new StringBuffer(), (FieldPosition)null);
        AttributedCharacterIterator it2 = format.formatToCharacterIterator(nulls);
        while (it2.getIndex() < it2.getEndIndex()) {
            int end = it2.getRunLimit();
            Integer index = (Integer)it2.getAttribute(MessageFormat.Field.ARGUMENT);
            if (index != null) {
                builder.append(this.render(args.get(index), context));
            } else {
                builder.append((Component)Component.text(sb2.substring(it2.getIndex(), end)));
            }
            it2.setIndex(end);
        }
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    }

    protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O mergeStyleAndOptionallyDeepRender(Component component, B builder, C context) {
        this.mergeStyle(component, builder, context);
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    }

    protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O optionallyRenderChildrenAppendAndBuild(List<Component> children, B builder, C context) {
        if (!children.isEmpty()) {
            children.forEach(child -> builder.append(this.render((Component)child, context)));
        }
        return (O)builder.build();
    }

    protected <B extends ComponentBuilder<?, ?>> void mergeStyle(Component component, B builder, C context) {
        builder.mergeStyle(component, MERGES);
        builder.clickEvent(component.clickEvent());
        @Nullable HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null) {
            builder.hoverEvent(hoverEvent.withRenderedValue(this, context));
        }
    }
}

