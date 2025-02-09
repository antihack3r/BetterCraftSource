/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.NamedTextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TextColorSerializer
extends TypeAdapter<TextColor> {
    static final TypeAdapter<TextColor> INSTANCE = new TextColorSerializer(false).nullSafe();
    static final TypeAdapter<TextColor> DOWNSAMPLE_COLOR = new TextColorSerializer(true).nullSafe();
    private final boolean downsampleColor;

    private TextColorSerializer(boolean downsampleColor) {
        this.downsampleColor = downsampleColor;
    }

    @Override
    public void write(JsonWriter out, TextColor value) throws IOException {
        if (value instanceof NamedTextColor) {
            out.value(NamedTextColor.NAMES.key((NamedTextColor)value));
        } else if (this.downsampleColor) {
            out.value(NamedTextColor.NAMES.key(NamedTextColor.nearestTo(value)));
        } else {
            out.value(TextColorSerializer.asUpperCaseHexString(value));
        }
    }

    private static String asUpperCaseHexString(TextColor color) {
        return String.format(Locale.ROOT, "%c%06X", Character.valueOf('#'), color.value());
    }

    @Override
    @Nullable
    public TextColor read(JsonReader in2) throws IOException {
        @Nullable TextColor color = TextColorSerializer.fromString(in2.nextString());
        if (color == null) {
            return null;
        }
        return this.downsampleColor ? NamedTextColor.nearestTo(color) : color;
    }

    @Nullable
    static TextColor fromString(@NotNull String value) {
        if (value.startsWith("#")) {
            return TextColor.fromHexString(value);
        }
        return NamedTextColor.NAMES.value(value);
    }
}

