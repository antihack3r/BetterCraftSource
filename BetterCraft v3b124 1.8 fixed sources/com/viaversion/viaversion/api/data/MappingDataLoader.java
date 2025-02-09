/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.data;

import com.google.common.annotations.Beta;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.FullMappings;
import com.viaversion.viaversion.api.data.FullMappingsBase;
import com.viaversion.viaversion.api.data.IdentityMappings;
import com.viaversion.viaversion.api.data.IntArrayMappings;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonIOException;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MappingDataLoader {
    private static final byte DIRECT_ID = 0;
    private static final byte SHIFTS_ID = 1;
    private static final byte CHANGES_ID = 2;
    private static final byte IDENTITY_ID = 3;
    private static final Map<String, CompoundTag> MAPPINGS_CACHE = new HashMap<String, CompoundTag>();
    private static boolean cacheValid = true;

    @Deprecated
    public static void enableMappingsCache() {
    }

    public static void clearCache() {
        MAPPINGS_CACHE.clear();
        cacheValid = false;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static @Nullable JsonObject loadFromDataDir(String name) {
        File file = new File(Via.getPlatform().getDataFolder(), name);
        if (!file.exists()) {
            return MappingDataLoader.loadData(name);
        }
        try (FileReader reader = new FileReader(file);){
            JsonObject jsonObject = GsonUtil.getGson().fromJson((Reader)reader, JsonObject.class);
            return jsonObject;
        }
        catch (JsonSyntaxException e2) {
            Via.getPlatform().getLogger().warning(name + " is badly formatted!");
            throw new RuntimeException(e2);
        }
        catch (JsonIOException | IOException e3) {
            throw new RuntimeException(e3);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static @Nullable JsonObject loadData(String name) {
        InputStream stream = MappingDataLoader.getResource(name);
        if (stream == null) {
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(stream);){
            JsonObject jsonObject = GsonUtil.getGson().fromJson((Reader)reader, JsonObject.class);
            return jsonObject;
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static @Nullable CompoundTag loadNBT(String name, boolean cache) {
        if (!cacheValid) {
            return MappingDataLoader.loadNBTFromFile(name);
        }
        CompoundTag data = MAPPINGS_CACHE.get(name);
        if (data != null) {
            return data;
        }
        data = MappingDataLoader.loadNBTFromFile(name);
        if (cache && data != null) {
            MAPPINGS_CACHE.put(name, data);
        }
        return data;
    }

    public static @Nullable CompoundTag loadNBT(String name) {
        return MappingDataLoader.loadNBT(name, false);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static @Nullable CompoundTag loadNBTFromFile(String name) {
        InputStream resource = MappingDataLoader.getResource(name);
        if (resource == null) {
            return null;
        }
        try (InputStream stream = resource;){
            CompoundTag compoundTag = NBTIO.readTag(stream);
            return compoundTag;
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static @Nullable Mappings loadMappings(CompoundTag mappingsTag, String key) {
        return MappingDataLoader.loadMappings(mappingsTag, key, size -> {
            int[] array = new int[size];
            Arrays.fill(array, -1);
            return array;
        }, (array, id2, mappedId) -> {
            array[id2] = mappedId;
        }, IntArrayMappings::of);
    }

    @Beta
    public static <M extends Mappings, V> @Nullable Mappings loadMappings(CompoundTag mappingsTag, String key, MappingHolderSupplier<V> holderSupplier, AddConsumer<V> addConsumer, MappingsSupplier<M, V> mappingsSupplier) {
        V mappings;
        CompoundTag tag = (CompoundTag)mappingsTag.get(key);
        if (tag == null) {
            return null;
        }
        ByteTag serializationStragetyTag = (ByteTag)tag.get("id");
        IntTag mappedSizeTag = (IntTag)tag.get("mappedSize");
        byte strategy = serializationStragetyTag.asByte();
        if (strategy == 0) {
            IntArrayTag valuesTag = (IntArrayTag)tag.get("val");
            return IntArrayMappings.of(valuesTag.getValue(), mappedSizeTag.asInt());
        }
        if (strategy == 1) {
            IntArrayTag shiftsAtTag = (IntArrayTag)tag.get("at");
            IntArrayTag shiftsTag = (IntArrayTag)tag.get("to");
            IntTag sizeTag = (IntTag)tag.get("size");
            int[] shiftsAt = shiftsAtTag.getValue();
            int[] shiftsTo = shiftsTag.getValue();
            int size = sizeTag.asInt();
            mappings = holderSupplier.get(size);
            if (shiftsAt[0] != 0) {
                int to2 = shiftsAt[0];
                for (int id2 = 0; id2 < to2; ++id2) {
                    addConsumer.addTo(mappings, id2, id2);
                }
            }
            for (int i2 = 0; i2 < shiftsAt.length; ++i2) {
                int from = shiftsAt[i2];
                int to3 = i2 == shiftsAt.length - 1 ? size : shiftsAt[i2 + 1];
                int mappedId = shiftsTo[i2];
                for (int id3 = from; id3 < to3; ++id3) {
                    addConsumer.addTo(mappings, id3, mappedId++);
                }
            }
        } else if (strategy == 2) {
            IntArrayTag changesAtTag = (IntArrayTag)tag.get("at");
            IntArrayTag valuesTag = (IntArrayTag)tag.get("val");
            IntTag sizeTag = (IntTag)tag.get("size");
            boolean fillBetween = tag.get("nofill") == null;
            int[] changesAt = changesAtTag.getValue();
            int[] values = valuesTag.getValue();
            mappings = holderSupplier.get(sizeTag.asInt());
            for (int i3 = 0; i3 < changesAt.length; ++i3) {
                int id4 = changesAt[i3];
                if (fillBetween) {
                    int previousId;
                    for (int identity = previousId = i3 != 0 ? changesAt[i3 - 1] + 1 : 0; identity < id4; ++identity) {
                        addConsumer.addTo(mappings, identity, identity);
                    }
                }
                addConsumer.addTo(mappings, id4, values[i3]);
            }
        } else {
            if (strategy == 3) {
                IntTag sizeTag = (IntTag)tag.get("size");
                return new IdentityMappings(sizeTag.asInt(), mappedSizeTag.asInt());
            }
            throw new IllegalArgumentException("Unknown serialization strategy: " + strategy);
        }
        return mappingsSupplier.create(mappings, mappedSizeTag.asInt());
    }

    public static FullMappings loadFullMappings(CompoundTag mappingsTag, CompoundTag unmappedIdentifiers, CompoundTag mappedIdentifiers, String key) {
        ListTag unmappedElements = (ListTag)unmappedIdentifiers.get(key);
        ListTag mappedElements = (ListTag)mappedIdentifiers.get(key);
        if (unmappedElements == null || mappedElements == null) {
            return null;
        }
        Mappings mappings = MappingDataLoader.loadMappings(mappingsTag, key);
        if (mappings == null) {
            mappings = new IdentityMappings(unmappedElements.size(), mappedElements.size());
        }
        return new FullMappingsBase(unmappedElements.getValue().stream().map(t2 -> (String)t2.getValue()).collect(Collectors.toList()), mappedElements.getValue().stream().map(t2 -> (String)t2.getValue()).collect(Collectors.toList()), mappings);
    }

    @Deprecated
    public static void mapIdentifiers(int[] output, JsonObject unmappedIdentifiers, JsonObject mappedIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
        Object2IntMap<String> newIdentifierMap = MappingDataLoader.indexedObjectToMap(mappedIdentifiers);
        for (Map.Entry<String, JsonElement> entry : unmappedIdentifiers.entrySet()) {
            int id2 = Integer.parseInt(entry.getKey());
            int mappedId = MappingDataLoader.mapIdentifierEntry(id2, entry.getValue().getAsString(), newIdentifierMap, diffIdentifiers, warnOnMissing);
            if (mappedId == -1) continue;
            output[id2] = mappedId;
        }
    }

    private static int mapIdentifierEntry(int id2, String val, Object2IntMap<String> mappedIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
        int mappedId = mappedIdentifiers.getInt(val);
        if (mappedId == -1) {
            JsonElement diffElement;
            if (diffIdentifiers != null && ((diffElement = diffIdentifiers.get(val)) != null || (diffElement = diffIdentifiers.get(Integer.toString(id2))) != null)) {
                String mappedName = diffElement.getAsString();
                if (mappedName.isEmpty()) {
                    return -1;
                }
                mappedId = mappedIdentifiers.getInt(mappedName);
            }
            if (mappedId == -1) {
                if (warnOnMissing && !Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    Via.getPlatform().getLogger().warning("No key for " + val + " :( ");
                }
                return -1;
            }
        }
        return mappedId;
    }

    @Deprecated
    public static void mapIdentifiers(int[] output, JsonArray unmappedIdentifiers, JsonArray mappedIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
        Object2IntMap<String> newIdentifierMap = MappingDataLoader.arrayToMap(mappedIdentifiers);
        for (int id2 = 0; id2 < unmappedIdentifiers.size(); ++id2) {
            JsonElement unmappedIdentifier = unmappedIdentifiers.get(id2);
            int mappedId = MappingDataLoader.mapIdentifierEntry(id2, unmappedIdentifier.getAsString(), newIdentifierMap, diffIdentifiers, warnOnMissing);
            if (mappedId == -1) continue;
            output[id2] = mappedId;
        }
    }

    public static Object2IntMap<String> indexedObjectToMap(JsonObject object) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<String>(object.size(), 0.99f);
        map.defaultReturnValue(-1);
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            map.put(entry.getValue().getAsString(), Integer.parseInt(entry.getKey()));
        }
        return map;
    }

    public static Object2IntMap<String> arrayToMap(JsonArray array) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<String>(array.size(), 0.99f);
        map.defaultReturnValue(-1);
        for (int i2 = 0; i2 < array.size(); ++i2) {
            map.put(array.get(i2).getAsString(), i2);
        }
        return map;
    }

    public static @Nullable InputStream getResource(String name) {
        return MappingDataLoader.class.getClassLoader().getResourceAsStream("assets/viaversion/data/" + name);
    }

    @FunctionalInterface
    public static interface MappingsSupplier<T extends Mappings, V> {
        public T create(V var1, int var2);
    }

    @FunctionalInterface
    public static interface MappingHolderSupplier<T> {
        public T get(int var1);
    }

    @FunctionalInterface
    public static interface AddConsumer<T> {
        public void addTo(T var1, int var2, int var3);
    }
}

