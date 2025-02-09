// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.data.MappingData;
import java.util.ArrayList;
import java.util.Iterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.util.Key;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.EnumMap;
import java.util.Set;
import com.viaversion.viaversion.api.minecraft.TagData;
import java.util.List;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import java.util.Map;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;

public class TagRewriter<C extends ClientboundPacketType>
{
    private static final int[] EMPTY_ARRAY;
    private final Protocol<C, ?, ?, ?> protocol;
    private final Map<RegistryType, List<TagData>> newTags;
    private final Map<RegistryType, Map<String, String>> toRename;
    private final Set<String> toRemove;
    
    public TagRewriter(final Protocol<C, ?, ?, ?> protocol) {
        this.newTags = new EnumMap<RegistryType, List<TagData>>(RegistryType.class);
        this.toRename = new EnumMap<RegistryType, Map<String, String>>(RegistryType.class);
        this.toRemove = new HashSet<String>();
        this.protocol = protocol;
    }
    
    public void loadFromMappingData() {
        for (final RegistryType type : RegistryType.getValues()) {
            final List<TagData> tags = this.protocol.getMappingData().getTags(type);
            if (tags != null) {
                this.getOrComputeNewTags(type).addAll(tags);
            }
        }
    }
    
    public void removeTags(final String registryKey) {
        this.toRemove.add(registryKey);
    }
    
    public void renameTag(final RegistryType type, final String registryKey, final String renameTo) {
        this.toRename.computeIfAbsent(type, t -> new HashMap()).put(registryKey, renameTo);
    }
    
    public void addEmptyTag(final RegistryType tagType, final String tagId) {
        this.getOrComputeNewTags(tagType).add(new TagData(tagId, TagRewriter.EMPTY_ARRAY));
    }
    
    public void addEmptyTags(final RegistryType tagType, final String... tagIds) {
        final List<TagData> tagList = this.getOrComputeNewTags(tagType);
        for (final String id : tagIds) {
            tagList.add(new TagData(id, TagRewriter.EMPTY_ARRAY));
        }
    }
    
    public void addEntityTag(final String tagId, final EntityType... entities) {
        final int[] ids = new int[entities.length];
        for (int i = 0; i < entities.length; ++i) {
            ids[i] = entities[i].getId();
        }
        this.addTagRaw(RegistryType.ENTITY, tagId, ids);
    }
    
    public void addTag(final RegistryType tagType, final String tagId, final int... unmappedIds) {
        final List<TagData> newTags = this.getOrComputeNewTags(tagType);
        final IdRewriteFunction rewriteFunction = this.getRewriter(tagType);
        if (rewriteFunction != null) {
            for (int i = 0; i < unmappedIds.length; ++i) {
                final int unmappedId = unmappedIds[i];
                unmappedIds[i] = rewriteFunction.rewrite(unmappedId);
            }
        }
        newTags.add(new TagData(tagId, unmappedIds));
    }
    
    public void addTagRaw(final RegistryType tagType, final String tagId, final int... ids) {
        this.getOrComputeNewTags(tagType).add(new TagData(tagId, ids));
    }
    
    public void register(final C packetType, final RegistryType readUntilType) {
        this.protocol.registerClientbound(packetType, this.getHandler(readUntilType));
    }
    
    public void registerGeneric(final C packetType) {
        this.protocol.registerClientbound(packetType, this.getGenericHandler());
    }
    
    public PacketHandler getHandler(final RegistryType readUntilType) {
        return wrapper -> {
            RegistryType.getValues();
            final RegistryType[] array;
            final int length = array.length;
            int i = 0;
            while (i < length) {
                final RegistryType type = array[i];
                this.handle(wrapper, this.getRewriter(type), this.getNewTags(type), this.toRename.get(type));
                if (type == readUntilType) {
                    break;
                }
                else {
                    ++i;
                }
            }
        };
    }
    
    public PacketHandler getGenericHandler() {
        return wrapper -> {
            int editedLength;
            for (int length = editedLength = wrapper.passthrough((Type<Integer>)Type.VAR_INT), i = 0; i < length; ++i) {
                final String registryKey = wrapper.read(Type.STRING);
                if (this.toRemove.contains(registryKey)) {
                    wrapper.set(Type.VAR_INT, 0, --editedLength);
                    for (int tagsSize = wrapper.read((Type<Integer>)Type.VAR_INT), j = 0; j < tagsSize; ++j) {
                        wrapper.read(Type.STRING);
                        wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                    }
                }
                else {
                    wrapper.write(Type.STRING, registryKey);
                    final String registryKey2 = Key.stripMinecraftNamespace(registryKey);
                    final RegistryType type = RegistryType.getByKey(registryKey2);
                    if (type != null) {
                        this.handle(wrapper, this.getRewriter(type), this.getNewTags(type), this.toRename.get(type));
                    }
                    else {
                        this.handle(wrapper, null, null, null);
                    }
                }
            }
        };
    }
    
    public void handle(final PacketWrapper wrapper, final IdRewriteFunction rewriteFunction, final List<TagData> newTags) throws Exception {
        this.handle(wrapper, rewriteFunction, newTags, null);
    }
    
    public void handle(final PacketWrapper wrapper, final IdRewriteFunction rewriteFunction, final List<TagData> newTags, final Map<String, String> tagsToRename) throws Exception {
        final int tagsSize = wrapper.read((Type<Integer>)Type.VAR_INT);
        wrapper.write(Type.VAR_INT, (newTags != null) ? (tagsSize + newTags.size()) : tagsSize);
        for (int i = 0; i < tagsSize; ++i) {
            String key = wrapper.read(Type.STRING);
            if (tagsToRename != null) {
                final String renamedKey = tagsToRename.get(key);
                if (renamedKey != null) {
                    key = renamedKey;
                }
            }
            wrapper.write(Type.STRING, key);
            final int[] ids = wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
            if (rewriteFunction != null) {
                final IntList idList = new IntArrayList(ids.length);
                for (final int id : ids) {
                    final int mappedId = rewriteFunction.rewrite(id);
                    if (mappedId != -1) {
                        idList.add(mappedId);
                    }
                }
                wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, idList.toArray(TagRewriter.EMPTY_ARRAY));
            }
            else {
                wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, ids);
            }
        }
        if (newTags != null) {
            for (final TagData tag : newTags) {
                wrapper.write(Type.STRING, tag.identifier());
                wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, tag.entries());
            }
        }
    }
    
    public List<TagData> getNewTags(final RegistryType tagType) {
        return this.newTags.get(tagType);
    }
    
    public List<TagData> getOrComputeNewTags(final RegistryType tagType) {
        return this.newTags.computeIfAbsent(tagType, type -> new ArrayList());
    }
    
    public IdRewriteFunction getRewriter(final RegistryType tagType) {
        final MappingData mappingData = this.protocol.getMappingData();
        switch (tagType) {
            case BLOCK: {
                return (mappingData != null && mappingData.getBlockMappings() != null) ? mappingData::getNewBlockId : null;
            }
            case ITEM: {
                return (mappingData != null && mappingData.getItemMappings() != null) ? mappingData::getNewItemId : null;
            }
            case ENTITY: {
                return (this.protocol.getEntityRewriter() != null) ? (id -> this.protocol.getEntityRewriter().newEntityId(id)) : null;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        EMPTY_ARRAY = new int[0];
    }
}
