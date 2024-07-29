/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.Launcher
 *  cpw.mods.modlauncher.api.TypesafeMap
 *  cpw.mods.modlauncher.api.TypesafeMap$Key
 */
package org.spongepowered.asm.service.modlauncher;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.TypesafeMap;
import java.util.HashMap;
import java.util.Map;
import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

public class Blackboard
implements IGlobalPropertyService {
    private final Map<String, IPropertyKey> keys = new HashMap<String, IPropertyKey>();
    private final TypesafeMap blackboard = Launcher.INSTANCE.blackboard();

    @Override
    public IPropertyKey resolveKey(String name) {
        return this.keys.computeIfAbsent(name, key -> new Key<Object>(this.blackboard, (String)key, Object.class));
    }

    @Override
    public <T> T getProperty(IPropertyKey key) {
        return this.getProperty(key, null);
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        this.blackboard.computeIfAbsent(((Key)key).key, k2 -> value);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return this.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return this.blackboard.get(((Key)key).key).orElse(defaultValue);
    }

    class Key<V>
    implements IPropertyKey {
        final TypesafeMap.Key<V> key;

        public Key(TypesafeMap owner, String name, Class<V> clazz) {
            this.key = TypesafeMap.Key.getOrCreate((TypesafeMap)owner, (String)name, clazz);
        }
    }
}

