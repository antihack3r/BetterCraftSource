// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.resources.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import com.google.gson.TypeAdapterFactory;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.RegistrySimple;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.IRegistry;

public class IMetadataSerializer
{
    private final IRegistry<String, Registration<? extends IMetadataSection>> metadataSectionSerializerRegistry;
    private final GsonBuilder gsonBuilder;
    private Gson gson;
    
    public IMetadataSerializer() {
        this.metadataSectionSerializerRegistry = new RegistrySimple<String, Registration<? extends IMetadataSection>>();
        (this.gsonBuilder = new GsonBuilder()).registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
        this.gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
        this.gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
    }
    
    public <T extends IMetadataSection> void registerMetadataSectionType(final IMetadataSectionSerializer<T> metadataSectionSerializer, final Class<T> clazz) {
        this.metadataSectionSerializerRegistry.putObject(metadataSectionSerializer.getSectionName(), new Registration<IMetadataSection>((IMetadataSectionSerializer)metadataSectionSerializer, (Class)clazz, (Registration<IMetadataSection>)null));
        this.gsonBuilder.registerTypeAdapter(clazz, metadataSectionSerializer);
        this.gson = null;
    }
    
    public <T extends IMetadataSection> T parseMetadataSection(final String sectionName, final JsonObject json) {
        if (sectionName == null) {
            throw new IllegalArgumentException("Metadata section name cannot be null");
        }
        if (!json.has(sectionName)) {
            return null;
        }
        if (!json.get(sectionName).isJsonObject()) {
            throw new IllegalArgumentException("Invalid metadata for '" + sectionName + "' - expected object, found " + json.get(sectionName));
        }
        final Registration<?> registration = this.metadataSectionSerializerRegistry.getObject(sectionName);
        if (registration == null) {
            throw new IllegalArgumentException("Don't know how to handle metadata section '" + sectionName + "'");
        }
        return this.getGson().fromJson(json.getAsJsonObject(sectionName), registration.clazz);
    }
    
    private Gson getGson() {
        if (this.gson == null) {
            this.gson = this.gsonBuilder.create();
        }
        return this.gson;
    }
    
    class Registration<T extends IMetadataSection>
    {
        final IMetadataSectionSerializer<T> section;
        final Class<T> clazz;
        
        private Registration(final IMetadataSectionSerializer<T> metadataSectionSerializer, final Class<T> clazzToRegister) {
            this.section = metadataSectionSerializer;
            this.clazz = clazzToRegister;
        }
    }
}
