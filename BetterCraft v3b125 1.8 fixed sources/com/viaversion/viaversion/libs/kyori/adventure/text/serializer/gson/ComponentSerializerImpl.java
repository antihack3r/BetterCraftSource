/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.EntityNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScoreComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.SelectorComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.StorageNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.SerializerFactory;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

final class ComponentSerializerImpl
extends TypeAdapter<Component> {
    static final Type COMPONENT_LIST_TYPE = new TypeToken<List<Component>>(){}.getType();
    private final Gson gson;

    static TypeAdapter<Component> create(Gson gson) {
        return new ComponentSerializerImpl(gson).nullSafe();
    }

    private ComponentSerializerImpl(Gson gson) {
        this.gson = gson;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public BuildableComponent<?, ?> read(JsonReader in2) throws IOException {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder;
        JsonToken token = in2.peek();
        if (token == JsonToken.STRING || token == JsonToken.NUMBER || token == JsonToken.BOOLEAN) {
            return Component.text(ComponentSerializerImpl.readString(in2));
        }
        if (token == JsonToken.BEGIN_ARRAY) {
            void var3_4;
            Object var3_3 = null;
            in2.beginArray();
            while (in2.hasNext()) {
                Object child = this.read(in2);
                if (var3_4 == null) {
                    Buildable.Builder builder2 = child.toBuilder();
                    continue;
                }
                var3_4.append((Component)child);
            }
            if (var3_4 == null) {
                throw ComponentSerializerImpl.notSureHowToDeserialize(in2.getPath());
            }
            in2.endArray();
            return var3_4.build();
        }
        if (token != JsonToken.BEGIN_OBJECT) {
            throw ComponentSerializerImpl.notSureHowToDeserialize(in2.getPath());
        }
        JsonObject jsonObject = new JsonObject();
        List extra = Collections.emptyList();
        String text = null;
        String translate = null;
        String translateFallback = null;
        List translateWith = null;
        String scoreName = null;
        String scoreObjective = null;
        String scoreValue = null;
        String selector = null;
        String keybind = null;
        String nbt = null;
        boolean nbtInterpret = false;
        BlockNBTComponent.Pos nbtBlock = null;
        String nbtEntity = null;
        Key nbtStorage = null;
        Object separator = null;
        in2.beginObject();
        while (in2.hasNext()) {
            String fieldName = in2.nextName();
            if (fieldName.equals("text")) {
                text = ComponentSerializerImpl.readString(in2);
                continue;
            }
            if (fieldName.equals("translate")) {
                translate = in2.nextString();
                continue;
            }
            if (fieldName.equals("fallback")) {
                translateFallback = in2.nextString();
                continue;
            }
            if (fieldName.equals("with")) {
                translateWith = (List)this.gson.fromJson(in2, COMPONENT_LIST_TYPE);
                continue;
            }
            if (fieldName.equals("score")) {
                in2.beginObject();
                while (in2.hasNext()) {
                    String scoreFieldName = in2.nextName();
                    if (scoreFieldName.equals("name")) {
                        scoreName = in2.nextString();
                        continue;
                    }
                    if (scoreFieldName.equals("objective")) {
                        scoreObjective = in2.nextString();
                        continue;
                    }
                    if (scoreFieldName.equals("value")) {
                        scoreValue = in2.nextString();
                        continue;
                    }
                    in2.skipValue();
                }
                if (scoreName == null || scoreObjective == null) {
                    throw new JsonParseException("A score component requires a name and objective");
                }
                in2.endObject();
                continue;
            }
            if (fieldName.equals("selector")) {
                selector = in2.nextString();
                continue;
            }
            if (fieldName.equals("keybind")) {
                keybind = in2.nextString();
                continue;
            }
            if (fieldName.equals("nbt")) {
                nbt = in2.nextString();
                continue;
            }
            if (fieldName.equals("interpret")) {
                nbtInterpret = in2.nextBoolean();
                continue;
            }
            if (fieldName.equals("block")) {
                nbtBlock = (BlockNBTComponent.Pos)this.gson.fromJson(in2, SerializerFactory.BLOCK_NBT_POS_TYPE);
                continue;
            }
            if (fieldName.equals("entity")) {
                nbtEntity = in2.nextString();
                continue;
            }
            if (fieldName.equals("storage")) {
                nbtStorage = (Key)this.gson.fromJson(in2, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals("extra")) {
                extra = (List)this.gson.fromJson(in2, COMPONENT_LIST_TYPE);
                continue;
            }
            if (fieldName.equals("separator")) {
                separator = this.read(in2);
                continue;
            }
            jsonObject.add(fieldName, (JsonElement)this.gson.fromJson(in2, (Type)((Object)JsonElement.class)));
        }
        if (text != null) {
            builder = Component.text().content(text);
        } else if (translate != null) {
            builder = translateWith != null ? Component.translatable().key(translate).fallback(translateFallback).args(translateWith) : Component.translatable().key(translate).fallback(translateFallback);
        } else if (scoreName != null && scoreObjective != null) {
            builder = scoreValue == null ? Component.score().name(scoreName).objective(scoreObjective) : Component.score().name(scoreName).objective(scoreObjective).value(scoreValue);
        } else if (selector != null) {
            builder = Component.selector().pattern(selector).separator((ComponentLike)separator);
        } else if (keybind != null) {
            builder = Component.keybind().keybind(keybind);
        } else {
            if (nbt == null) throw ComponentSerializerImpl.notSureHowToDeserialize(in2.getPath());
            if (nbtBlock != null) {
                builder = ComponentSerializerImpl.nbt(Component.blockNBT(), nbt, nbtInterpret, separator).pos(nbtBlock);
            } else if (nbtEntity != null) {
                builder = ComponentSerializerImpl.nbt(Component.entityNBT(), nbt, nbtInterpret, separator).selector(nbtEntity);
            } else {
                if (nbtStorage == null) throw ComponentSerializerImpl.notSureHowToDeserialize(in2.getPath());
                builder = ComponentSerializerImpl.nbt(Component.storageNBT(), nbt, nbtInterpret, separator).storage(nbtStorage);
            }
        }
        builder.style(this.gson.fromJson((JsonElement)jsonObject, SerializerFactory.STYLE_TYPE)).append(extra);
        in2.endObject();
        return builder.build();
    }

    private static String readString(JsonReader in2) throws IOException {
        JsonToken peek = in2.peek();
        if (peek == JsonToken.STRING || peek == JsonToken.NUMBER) {
            return in2.nextString();
        }
        if (peek == JsonToken.BOOLEAN) {
            return String.valueOf(in2.nextBoolean());
        }
        throw new JsonParseException("Token of type " + (Object)((Object)peek) + " cannot be interpreted as a string");
    }

    private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(B builder, String nbt, boolean interpret, @Nullable Component separator) {
        return builder.nbtPath(nbt).interpret(interpret).separator(separator);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void write(JsonWriter out, Component value) throws IOException {
        JsonElement style;
        out.beginObject();
        if (value.hasStyling() && (style = this.gson.toJsonTree(value.style(), SerializerFactory.STYLE_TYPE)).isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : style.getAsJsonObject().entrySet()) {
                out.name(entry.getKey());
                this.gson.toJson(entry.getValue(), out);
            }
        }
        if (!value.children().isEmpty()) {
            out.name("extra");
            this.gson.toJson(value.children(), COMPONENT_LIST_TYPE, out);
        }
        if (value instanceof TextComponent) {
            out.name("text");
            out.value(((TextComponent)value).content());
        } else if (value instanceof TranslatableComponent) {
            TranslatableComponent translatable = (TranslatableComponent)value;
            out.name("translate");
            out.value(translatable.key());
            @Nullable String fallback = translatable.fallback();
            if (fallback != null) {
                out.name("fallback");
                out.value(fallback);
            }
            if (!translatable.args().isEmpty()) {
                out.name("with");
                this.gson.toJson(translatable.args(), COMPONENT_LIST_TYPE, out);
            }
        } else if (value instanceof ScoreComponent) {
            ScoreComponent score = (ScoreComponent)value;
            out.name("score");
            out.beginObject();
            out.name("name");
            out.value(score.name());
            out.name("objective");
            out.value(score.objective());
            if (score.value() != null) {
                out.name("value");
                out.value(score.value());
            }
            out.endObject();
        } else if (value instanceof SelectorComponent) {
            SelectorComponent selector = (SelectorComponent)value;
            out.name("selector");
            out.value(selector.pattern());
            this.serializeSeparator(out, selector.separator());
        } else if (value instanceof KeybindComponent) {
            out.name("keybind");
            out.value(((KeybindComponent)value).keybind());
        } else {
            if (!(value instanceof NBTComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(value);
            NBTComponent nbt = (NBTComponent)value;
            out.name("nbt");
            out.value(nbt.nbtPath());
            out.name("interpret");
            out.value(nbt.interpret());
            this.serializeSeparator(out, nbt.separator());
            if (value instanceof BlockNBTComponent) {
                out.name("block");
                this.gson.toJson((Object)((BlockNBTComponent)value).pos(), SerializerFactory.BLOCK_NBT_POS_TYPE, out);
            } else if (value instanceof EntityNBTComponent) {
                out.name("entity");
                out.value(((EntityNBTComponent)value).selector());
            } else {
                if (!(value instanceof StorageNBTComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(value);
                out.name("storage");
                this.gson.toJson((Object)((StorageNBTComponent)value).storage(), SerializerFactory.KEY_TYPE, out);
            }
        }
        out.endObject();
    }

    private void serializeSeparator(JsonWriter out, @Nullable Component separator) throws IOException {
        if (separator != null) {
            out.name("separator");
            this.write(out, separator);
        }
    }

    static JsonParseException notSureHowToDeserialize(Object element) {
        return new JsonParseException("Don't know how to turn " + element + " into a Component");
    }

    private static IllegalArgumentException notSureHowToSerialize(Component component) {
        return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
    }
}

