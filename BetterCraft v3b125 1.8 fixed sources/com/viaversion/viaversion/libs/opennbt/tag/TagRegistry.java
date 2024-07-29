/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.tag;

import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.FloatTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public final class TagRegistry {
    private static final int HIGHEST_ID = 12;
    private static final RegisteredTagType[] TAGS = new RegisteredTagType[13];
    private static final Object2IntMap<Class<? extends Tag>> TAG_TO_ID = new Object2IntOpenHashMap<Class<? extends Tag>>();

    public static void register(int id2, Class<? extends Tag> tag, Supplier<? extends Tag> supplier) {
        if (id2 < 0 || id2 > 12) {
            throw new IllegalArgumentException("Tag ID must be between 0 and 12");
        }
        if (TAGS[id2] != null) {
            throw new IllegalArgumentException("Tag ID \"" + id2 + "\" is already in use.");
        }
        if (TAG_TO_ID.containsKey(tag)) {
            throw new IllegalArgumentException("Tag \"" + tag.getSimpleName() + "\" is already registered.");
        }
        TagRegistry.TAGS[id2] = new RegisteredTagType(tag, supplier);
        TAG_TO_ID.put(tag, id2);
    }

    public static void unregister(int id2) {
        TAG_TO_ID.removeInt(TagRegistry.getClassFor(id2));
        TagRegistry.TAGS[id2] = null;
    }

    @Nullable
    public static Class<? extends Tag> getClassFor(int id2) {
        return id2 >= 0 && id2 < TAGS.length ? TAGS[id2].type : null;
    }

    public static int getIdFor(Class<? extends Tag> clazz) {
        return TAG_TO_ID.getInt(clazz);
    }

    public static Tag createInstance(int id2) {
        Supplier supplier;
        Supplier supplier2 = supplier = id2 > 0 && id2 < TAGS.length ? TAGS[id2].supplier : null;
        if (supplier == null) {
            throw new IllegalArgumentException("Could not find tag with ID \"" + id2 + "\".");
        }
        return (Tag)supplier.get();
    }

    static {
        TAG_TO_ID.defaultReturnValue(-1);
        TagRegistry.register(1, ByteTag.class, ByteTag::new);
        TagRegistry.register(2, ShortTag.class, ShortTag::new);
        TagRegistry.register(3, IntTag.class, IntTag::new);
        TagRegistry.register(4, LongTag.class, LongTag::new);
        TagRegistry.register(5, FloatTag.class, FloatTag::new);
        TagRegistry.register(6, DoubleTag.class, DoubleTag::new);
        TagRegistry.register(7, ByteArrayTag.class, ByteArrayTag::new);
        TagRegistry.register(8, StringTag.class, StringTag::new);
        TagRegistry.register(9, ListTag.class, ListTag::new);
        TagRegistry.register(10, CompoundTag.class, CompoundTag::new);
        TagRegistry.register(11, IntArrayTag.class, IntArrayTag::new);
        TagRegistry.register(12, LongArrayTag.class, LongArrayTag::new);
    }

    private static final class RegisteredTagType {
        private final Class<? extends Tag> type;
        private final Supplier<? extends Tag> supplier;

        private RegisteredTagType(Class<? extends Tag> type, Supplier<? extends Tag> supplier) {
            this.type = type;
            this.supplier = supplier;
        }
    }
}

