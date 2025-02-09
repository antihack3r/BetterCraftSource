/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.VisibleForTesting
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

final class ComponentCompaction {
    @VisibleForTesting
    static final boolean SIMPLIFY_STYLE_FOR_BLANK_COMPONENTS = false;

    private ComponentCompaction() {
    }

    static Component compact(@NotNull Component self, @Nullable Style parentStyle) {
        Component child;
        int i2;
        TextComponent textComponent;
        int childrenSize;
        List<Component> children = self.children();
        Component optimized = self.children(Collections.emptyList());
        if (parentStyle != null) {
            optimized = optimized.style(self.style().unmerge(parentStyle));
        }
        if ((childrenSize = children.size()) == 0) {
            if (ComponentCompaction.isBlank(optimized)) {
                optimized = optimized.style(ComponentCompaction.simplifyStyleForBlank(optimized.style(), parentStyle));
            }
            return optimized;
        }
        if (childrenSize == 1 && optimized instanceof TextComponent && (textComponent = (TextComponent)optimized).content().isEmpty()) {
            Component child2 = children.get(0);
            return child2.style(child2.style().merge(optimized.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).compact();
        }
        Style childParentStyle = optimized.style();
        if (parentStyle != null) {
            childParentStyle = childParentStyle.merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        }
        ArrayList<Component> childrenToAppend = new ArrayList<Component>(children.size());
        for (i2 = 0; i2 < children.size(); ++i2) {
            TextComponent textComponent2;
            child = children.get(i2);
            if ((child = ComponentCompaction.compact(child, childParentStyle)).children().isEmpty() && child instanceof TextComponent && (textComponent2 = (TextComponent)child).content().isEmpty()) continue;
            childrenToAppend.add(child);
        }
        if (optimized instanceof TextComponent) {
            while (!childrenToAppend.isEmpty()) {
                Component child3 = (Component)childrenToAppend.get(0);
                Style childStyle = child3.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
                if (!(child3 instanceof TextComponent) || !Objects.equals(childStyle, childParentStyle)) break;
                optimized = ComponentCompaction.joinText((TextComponent)optimized, (TextComponent)child3);
                childrenToAppend.remove(0);
                childrenToAppend.addAll(0, child3.children());
            }
        }
        i2 = 0;
        while (i2 + 1 < childrenToAppend.size()) {
            Style neighborStyle;
            Style childStyle;
            child = (Component)childrenToAppend.get(i2);
            Component neighbor = (Component)childrenToAppend.get(i2 + 1);
            if (child.children().isEmpty() && child instanceof TextComponent && neighbor instanceof TextComponent && (childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).equals(neighborStyle = neighbor.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET))) {
                TextComponent combined = ComponentCompaction.joinText((TextComponent)child, (TextComponent)neighbor);
                childrenToAppend.set(i2, combined);
                childrenToAppend.remove(i2 + 1);
                continue;
            }
            ++i2;
        }
        if (childrenToAppend.isEmpty() && ComponentCompaction.isBlank(optimized)) {
            optimized = optimized.style(ComponentCompaction.simplifyStyleForBlank(optimized.style(), parentStyle));
        }
        return optimized.children(childrenToAppend);
    }

    private static boolean isBlank(Component component) {
        if (component instanceof TextComponent) {
            TextComponent textComponent = (TextComponent)component;
            String content = textComponent.content();
            for (int i2 = 0; i2 < content.length(); ++i2) {
                char c2 = content.charAt(i2);
                if (c2 == ' ') continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @NotNull
    private static Style simplifyStyleForBlank(@NotNull Style style, @Nullable Style parentStyle) {
        return style;
    }

    private static TextComponent joinText(TextComponent one, TextComponent two) {
        return TextComponentImpl.create(two.children(), one.style(), one.content() + two.content());
    }
}

