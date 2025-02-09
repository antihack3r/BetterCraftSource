/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Range
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.format;

import com.viaversion.viaversion.libs.kyori.adventure.text.format.NamedTextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.StyleBuilderApplicable;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColorImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextFormat;
import com.viaversion.viaversion.libs.kyori.adventure.util.HSVLike;
import com.viaversion.viaversion.libs.kyori.adventure.util.RGBLike;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface TextColor
extends Comparable<TextColor>,
Examinable,
RGBLike,
StyleBuilderApplicable,
TextFormat {
    public static final char HEX_CHARACTER = '#';
    public static final String HEX_PREFIX = "#";

    @NotNull
    public static TextColor color(int value) {
        int truncatedValue = value & 0xFFFFFF;
        NamedTextColor named = NamedTextColor.namedColor(truncatedValue);
        return named != null ? named : new TextColorImpl(truncatedValue);
    }

    @NotNull
    public static TextColor color(@NotNull RGBLike rgb) {
        if (rgb instanceof TextColor) {
            return (TextColor)rgb;
        }
        return TextColor.color(rgb.red(), rgb.green(), rgb.blue());
    }

    @NotNull
    public static TextColor color(@NotNull HSVLike hsv) {
        float s2 = hsv.s();
        float v2 = hsv.v();
        if (s2 == 0.0f) {
            return TextColor.color(v2, v2, v2);
        }
        float h2 = hsv.h() * 6.0f;
        int i2 = (int)Math.floor(h2);
        float f2 = h2 - (float)i2;
        float p2 = v2 * (1.0f - s2);
        float q2 = v2 * (1.0f - s2 * f2);
        float t2 = v2 * (1.0f - s2 * (1.0f - f2));
        if (i2 == 0) {
            return TextColor.color(v2, t2, p2);
        }
        if (i2 == 1) {
            return TextColor.color(q2, v2, p2);
        }
        if (i2 == 2) {
            return TextColor.color(p2, v2, t2);
        }
        if (i2 == 3) {
            return TextColor.color(p2, q2, v2);
        }
        if (i2 == 4) {
            return TextColor.color(t2, p2, v2);
        }
        return TextColor.color(v2, p2, q2);
    }

    @NotNull
    public static TextColor color(@Range(from=0L, to=255L) int r2, @Range(from=0L, to=255L) int g2, @Range(from=0L, to=255L) int b2) {
        return TextColor.color((r2 & 0xFF) << 16 | (g2 & 0xFF) << 8 | b2 & 0xFF);
    }

    @NotNull
    public static TextColor color(float r2, float g2, float b2) {
        return TextColor.color((int)(r2 * 255.0f), (int)(g2 * 255.0f), (int)(b2 * 255.0f));
    }

    @Nullable
    public static TextColor fromHexString(@NotNull String string) {
        if (string.startsWith(HEX_PREFIX)) {
            try {
                int hex = Integer.parseInt(string.substring(1), 16);
                return TextColor.color(hex);
            }
            catch (NumberFormatException e2) {
                return null;
            }
        }
        return null;
    }

    @Nullable
    public static TextColor fromCSSHexString(@NotNull String string) {
        if (string.startsWith(HEX_PREFIX)) {
            int hex;
            String hexString = string.substring(1);
            if (hexString.length() != 3 && hexString.length() != 6) {
                return null;
            }
            try {
                hex = Integer.parseInt(hexString, 16);
            }
            catch (NumberFormatException e2) {
                return null;
            }
            if (hexString.length() == 6) {
                return TextColor.color(hex);
            }
            int red = (hex & 0xF00) >> 8 | (hex & 0xF00) >> 4;
            int green = (hex & 0xF0) >> 4 | hex & 0xF0;
            int blue = (hex & 0xF) << 4 | hex & 0xF;
            return TextColor.color(red, green, blue);
        }
        return null;
    }

    public int value();

    @NotNull
    default public String asHexString() {
        return String.format("%c%06x", Character.valueOf('#'), this.value());
    }

    @Override
    default public @Range(from=0L, to=255L) int red() {
        return this.value() >> 16 & 0xFF;
    }

    @Override
    default public @Range(from=0L, to=255L) int green() {
        return this.value() >> 8 & 0xFF;
    }

    @Override
    default public @Range(from=0L, to=255L) int blue() {
        return this.value() & 0xFF;
    }

    @NotNull
    public static TextColor lerp(float t2, @NotNull RGBLike a2, @NotNull RGBLike b2) {
        float clampedT = Math.min(1.0f, Math.max(0.0f, t2));
        int ar2 = a2.red();
        int br2 = b2.red();
        int ag2 = a2.green();
        int bg2 = b2.green();
        int ab2 = a2.blue();
        int bb2 = b2.blue();
        return TextColor.color(Math.round((float)ar2 + clampedT * (float)(br2 - ar2)), Math.round((float)ag2 + clampedT * (float)(bg2 - ag2)), Math.round((float)ab2 + clampedT * (float)(bb2 - ab2)));
    }

    @NotNull
    public static <C extends TextColor> C nearestColorTo(@NotNull List<C> values, @NotNull TextColor any2) {
        Objects.requireNonNull(any2, "color");
        float matchedDistance = Float.MAX_VALUE;
        TextColor match = (TextColor)values.get(0);
        int length = values.size();
        for (int i2 = 0; i2 < length; ++i2) {
            TextColor potential = (TextColor)values.get(i2);
            float distance = TextColorImpl.distance(any2.asHSV(), potential.asHSV());
            if (distance < matchedDistance) {
                match = potential;
                matchedDistance = distance;
            }
            if (distance == 0.0f) break;
        }
        return (C)match;
    }

    @Override
    default public void styleApply(@NotNull Style.Builder style) {
        style.color(this);
    }

    @Override
    default public int compareTo(TextColor that) {
        return Integer.compare(this.value(), that.value());
    }

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("value", this.asHexString()));
    }
}

