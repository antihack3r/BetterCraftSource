// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.api.data;

import java.util.concurrent.ConcurrentHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import java.util.Iterator;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.util.Int2IntBiMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.viaversion.viaversion.libs.gson.JsonIOException;
import java.io.IOException;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import java.io.Reader;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.FileReader;
import java.io.File;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.Map;

public class MappingDataLoader
{
    private static final Map<String, JsonObject> MAPPINGS_CACHE;
    private static boolean cacheJsonMappings;
    
    public static boolean isCacheJsonMappings() {
        return MappingDataLoader.cacheJsonMappings;
    }
    
    public static void enableMappingsCache() {
        MappingDataLoader.cacheJsonMappings = true;
    }
    
    public static Map<String, JsonObject> getMappingsCache() {
        return MappingDataLoader.MAPPINGS_CACHE;
    }
    
    public static JsonObject loadFromDataDir(final String name) {
        final File file = new File(Via.getPlatform().getDataFolder(), name);
        if (!file.exists()) {
            return loadData(name);
        }
        try (final FileReader reader = new FileReader(file)) {
            return GsonUtil.getGson().fromJson(reader, JsonObject.class);
        }
        catch (final JsonSyntaxException e) {
            Via.getPlatform().getLogger().warning(name + " is badly formatted!");
            e.printStackTrace();
        }
        catch (final IOException | JsonIOException e2) {
            e2.printStackTrace();
        }
        return null;
    }
    
    public static JsonObject loadData(final String name) {
        return loadData(name, false);
    }
    
    public static JsonObject loadData(final String name, final boolean cacheIfEnabled) {
        if (MappingDataLoader.cacheJsonMappings) {
            final JsonObject cached = MappingDataLoader.MAPPINGS_CACHE.get(name);
            if (cached != null) {
                return cached;
            }
        }
        final InputStream stream = getResource(name);
        if (stream == null) {
            return null;
        }
        final InputStreamReader reader = new InputStreamReader(stream);
        try {
            final JsonObject object = GsonUtil.getGson().fromJson(reader, JsonObject.class);
            if (cacheIfEnabled && MappingDataLoader.cacheJsonMappings) {
                MappingDataLoader.MAPPINGS_CACHE.put(name, object);
            }
            return object;
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public static void mapIdentifiers(final Int2IntBiMap output, final JsonObject oldIdentifiers, final JsonObject newIdentifiers, final JsonObject diffIdentifiers) {
        final Object2IntMap<String> newIdentifierMap = indexedObjectToMap(newIdentifiers);
        for (final Map.Entry<String, JsonElement> entry : oldIdentifiers.entrySet()) {
            final int value = mapIdentifierEntry(entry, newIdentifierMap, diffIdentifiers);
            if (value != -1) {
                output.put(Integer.parseInt(entry.getKey()), value);
            }
        }
    }
    
    public static void mapIdentifiers(final int[] output, final JsonObject oldIdentifiers, final JsonObject newIdentifiers) {
        mapIdentifiers(output, oldIdentifiers, newIdentifiers, null);
    }
    
    public static void mapIdentifiers(final int[] output, final JsonObject oldIdentifiers, final JsonObject newIdentifiers, final JsonObject diffIdentifiers) {
        final Object2IntMap<String> newIdentifierMap = indexedObjectToMap(newIdentifiers);
        for (final Map.Entry<String, JsonElement> entry : oldIdentifiers.entrySet()) {
            final int value = mapIdentifierEntry(entry, newIdentifierMap, diffIdentifiers);
            if (value != -1) {
                output[Integer.parseInt(entry.getKey())] = value;
            }
        }
    }
    
    private static int mapIdentifierEntry(final Map.Entry<String, JsonElement> entry, final Object2IntMap newIdentifierMap, final JsonObject diffIdentifiers) {
        int value = newIdentifierMap.getInt(entry.getValue().getAsString());
        if (value == -1) {
            if (diffIdentifiers != null) {
                final JsonElement diffElement = diffIdentifiers.get(entry.getKey());
                if (diffElement != null) {
                    value = newIdentifierMap.getInt(diffElement.getAsString());
                }
            }
            if (value == -1) {
                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    Via.getPlatform().getLogger().warning("No key for " + entry.getValue() + " :( ");
                }
                return -1;
            }
        }
        return value;
    }
    
    public static void mapIdentifiers(final int[] output, final JsonArray oldIdentifiers, final JsonArray newIdentifiers, final boolean warnOnMissing) {
        mapIdentifiers(output, oldIdentifiers, newIdentifiers, null, warnOnMissing);
    }
    
    public static void mapIdentifiers(final int[] output, final JsonArray oldIdentifiers, final JsonArray newIdentifiers, final JsonObject diffIdentifiers, final boolean warnOnMissing) {
        final Object2IntMap<String> newIdentifierMap = arrayToMap(newIdentifiers);
        for (int i = 0; i < oldIdentifiers.size(); ++i) {
            final JsonElement oldIdentifier = oldIdentifiers.get(i);
            int mappedId = newIdentifierMap.getInt(oldIdentifier.getAsString());
            if (mappedId == -1) {
                if (diffIdentifiers != null) {
                    final JsonElement diffElement = diffIdentifiers.get(oldIdentifier.getAsString());
                    if (diffElement != null) {
                        final String mappedName = diffElement.getAsString();
                        if (mappedName.isEmpty()) {
                            continue;
                        }
                        mappedId = newIdentifierMap.getInt(mappedName);
                    }
                }
                if (mappedId == -1) {
                    if ((warnOnMissing && !Via.getConfig().isSuppressConversionWarnings()) || Via.getManager().isDebug()) {
                        Via.getPlatform().getLogger().warning("No key for " + oldIdentifier + " :( ");
                    }
                    continue;
                }
            }
            output[i] = mappedId;
        }
    }
    
    public static Object2IntMap<String> indexedObjectToMap(final JsonObject object) {
        final Object2IntMap<String> map = new Object2IntOpenHashMap<String>(object.size(), 1.0f);
        map.defaultReturnValue(-1);
        for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
            map.put(entry.getValue().getAsString(), Integer.parseInt(entry.getKey()));
        }
        return map;
    }
    
    public static Object2IntMap<String> arrayToMap(final JsonArray array) {
        final Object2IntMap<String> map = new Object2IntOpenHashMap<String>(array.size(), 1.0f);
        map.defaultReturnValue(-1);
        for (int i = 0; i < array.size(); ++i) {
            map.put(array.get(i).getAsString(), i);
        }
        return map;
    }
    
    public static InputStream getResource(final String name) {
        return MappingDataLoader.class.getClassLoader().getResourceAsStream("assets/viaversion/data/" + name);
    }
    
    static {
        MAPPINGS_CACHE = new ConcurrentHashMap<String, JsonObject>();
    }
}
