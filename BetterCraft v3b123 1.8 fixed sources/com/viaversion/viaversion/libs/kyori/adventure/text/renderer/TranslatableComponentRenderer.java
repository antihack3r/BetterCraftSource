// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.kyori.adventure.text.renderer;

import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.SelectorComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScoreComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.StorageNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.EntityNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponent;
import org.jetbrains.annotations.Nullable;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import com.viaversion.viaversion.libs.kyori.adventure.translation.Translator;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import java.util.Set;

public abstract class TranslatableComponentRenderer<C> extends AbstractComponentRenderer<C>
{
    private static final Set<Style.Merge> MERGES;
    
    @NotNull
    public static TranslatableComponentRenderer<Locale> usingTranslationSource(@NotNull final Translator source) {
        Objects.requireNonNull(source, "source");
        return new TranslatableComponentRenderer<Locale>() {
            @Nullable
            @Override
            protected MessageFormat translate(@NotNull final String key, @NotNull final Locale context) {
                return source.translate(key, context);
            }
        };
    }
    
    @Nullable
    protected abstract MessageFormat translate(@NotNull final String key, @NotNull final C context);
    
    @NotNull
    @Override
    protected Component renderBlockNbt(@NotNull final BlockNBTComponent component, @NotNull final C context) {
        final BlockNBTComponent.Builder builder = this.nbt(context, Component.blockNBT(), component).pos(component.pos());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }
    
    @NotNull
    @Override
    protected Component renderEntityNbt(@NotNull final EntityNBTComponent component, @NotNull final C context) {
        final EntityNBTComponent.Builder builder = this.nbt(context, Component.entityNBT(), component).selector(component.selector());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }
    
    @NotNull
    @Override
    protected Component renderStorageNbt(@NotNull final StorageNBTComponent component, @NotNull final C context) {
        final StorageNBTComponent.Builder builder = this.nbt(context, Component.storageNBT(), component).storage(component.storage());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }
    
    protected <O extends NBTComponent<O, B>, B extends NBTComponentBuilder<O, B>> B nbt(@NotNull final C context, final B builder, final O oldComponent) {
        ((NBTComponentBuilder<C, NBTComponentBuilder<O, B>>)builder).nbtPath(oldComponent.nbtPath()).interpret(oldComponent.interpret());
        final Component separator = oldComponent.separator();
        if (separator != null) {
            builder.separator(this.render(separator, context));
        }
        return builder;
    }
    
    @NotNull
    @Override
    protected Component renderKeybind(@NotNull final KeybindComponent component, @NotNull final C context) {
        final KeybindComponent.Builder builder = Component.keybind().keybind(component.keybind());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }
    
    @NotNull
    @Override
    protected Component renderScore(@NotNull final ScoreComponent component, @NotNull final C context) {
        final ScoreComponent.Builder builder = Component.score().name(component.name()).objective(component.objective()).value(component.value());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }
    
    @NotNull
    @Override
    protected Component renderSelector(@NotNull final SelectorComponent component, @NotNull final C context) {
        final SelectorComponent.Builder builder = Component.selector().pattern(component.pattern());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }
    
    @NotNull
    @Override
    protected Component renderText(@NotNull final TextComponent component, @NotNull final C context) {
        final TextComponent.Builder builder = Component.text().content(component.content());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }
    
    @NotNull
    @Override
    protected Component renderTranslatable(@NotNull final TranslatableComponent component, @NotNull final C context) {
        final MessageFormat format = this.translate(component.key(), context);
        if (format == null) {
            final TranslatableComponent.Builder builder = Component.translatable().key(component.key());
            if (!component.args().isEmpty()) {
                final List<Component> args = new ArrayList<Component>(component.args());
                for (int i = 0, size = args.size(); i < size; ++i) {
                    args.set(i, this.render(args.get(i), context));
                }
                builder.args(args);
            }
            return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
        }
        final List<Component> args2 = component.args();
        final TextComponent.Builder builder2 = Component.text();
        this.mergeStyle(component, builder2, context);
        if (args2.isEmpty()) {
            builder2.content(format.format(null, new StringBuffer(), null).toString());
            return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder2, context);
        }
        final Object[] nulls = new Object[args2.size()];
        final StringBuffer sb = format.format(nulls, new StringBuffer(), null);
        final AttributedCharacterIterator it = format.formatToCharacterIterator(nulls);
        while (it.getIndex() < it.getEndIndex()) {
            final int end = it.getRunLimit();
            final Integer index = (Integer)it.getAttribute(MessageFormat.Field.ARGUMENT);
            if (index != null) {
                builder2.append(this.render(args2.get(index), context));
            }
            else {
                builder2.append(Component.text(sb.substring(it.getIndex(), end)));
            }
            it.setIndex(end);
        }
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder2, context);
    }
    
    protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O mergeStyleAndOptionallyDeepRender(final Component component, final B builder, final C context) {
        this.mergeStyle(component, builder, context);
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    }
    
    protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O optionallyRenderChildrenAppendAndBuild(final List<Component> children, final B builder, final C context) {
        if (!children.isEmpty()) {
            children.forEach(child -> builder.append(this.render(child, (C)context)));
        }
        return ((ComponentBuilder<O, B>)builder).build();
    }
    
    protected <B extends ComponentBuilder<?, ?>> void mergeStyle(final Component component, final B builder, final C context) {
        builder.mergeStyle(component, TranslatableComponentRenderer.MERGES);
        builder.clickEvent(component.clickEvent());
        final HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null) {
            builder.hoverEvent((HoverEventSource<?>)hoverEvent.withRenderedValue(this, context));
        }
    }
    
    static {
        MERGES = Style.Merge.merges(Style.Merge.COLOR, Style.Merge.DECORATIONS, Style.Merge.INSERTION, Style.Merge.FONT);
    }
}
