/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.refmap;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.tools.Diagnostic;
import org.spongepowered.asm.mixin.refmap.IReferenceMapper;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.logging.MessageRouter;

public final class ReferenceMapper
implements IReferenceMapper,
Serializable {
    private static final long serialVersionUID = 2L;
    public static final String DEFAULT_RESOURCE = "mixin.refmap.json";
    public static final ReferenceMapper DEFAULT_MAPPER = new ReferenceMapper(true, "invalid");
    private final Map<String, Map<String, String>> mappings = Maps.newHashMap();
    private final Map<String, Map<String, Map<String, String>>> data = Maps.newHashMap();
    private final transient boolean readOnly;
    private transient String context = null;
    private transient String resource;

    public ReferenceMapper() {
        this(false, DEFAULT_RESOURCE);
    }

    private ReferenceMapper(boolean readOnly, String resource) {
        this.readOnly = readOnly;
        this.resource = resource;
    }

    @Override
    public boolean isDefault() {
        return this.readOnly;
    }

    private void setResourceName(String resource) {
        if (!this.readOnly) {
            this.resource = resource != null ? resource : "<unknown resource>";
        }
    }

    @Override
    public String getResourceName() {
        return this.resource;
    }

    @Override
    public String getStatus() {
        return this.isDefault() ? "No refMap loaded." : "Using refmap " + this.getResourceName();
    }

    @Override
    public String getContext() {
        return this.context;
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String remap(String className, String reference) {
        return this.remapWithContext(this.context, className, reference);
    }

    @Override
    public String remapWithContext(String context, String className, String reference) {
        Map<String, Map<String, String>> mappings = this.mappings;
        if (context != null && (mappings = this.data.get(context)) == null) {
            mappings = this.mappings;
        }
        return this.remap(mappings, className, reference);
    }

    private String remap(Map<String, Map<String, String>> mappings, String className, String reference) {
        Map<String, String> classMappings;
        if (className == null) {
            for (Map<String, String> mapping : mappings.values()) {
                if (!mapping.containsKey(reference)) continue;
                return mapping.get(reference);
            }
        }
        if ((classMappings = mappings.get(className)) == null) {
            return reference;
        }
        String remappedReference = classMappings.get(reference);
        return remappedReference != null ? remappedReference : reference;
    }

    public String addMapping(String context, String className, String reference, String newReference) {
        Map<String, String> classMappings;
        if (this.readOnly || reference == null || newReference == null) {
            return null;
        }
        String conformedReference = reference.replaceAll("\\s", "");
        if (conformedReference.equals(newReference)) {
            return null;
        }
        Map<String, Map<String, String>> mappings = this.mappings;
        if (context != null && (mappings = this.data.get(context)) == null) {
            mappings = Maps.newHashMap();
            this.data.put(context, mappings);
        }
        if ((classMappings = mappings.get(className)) == null) {
            classMappings = new HashMap<String, String>();
            mappings.put(className, classMappings);
        }
        return classMappings.put(conformedReference, newReference);
    }

    public void write(Appendable writer) {
        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson((Object)this, writer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static ReferenceMapper read(String resourcePath) {
        InputStreamReader reader;
        block6: {
            ReferenceMapper referenceMapper;
            reader = null;
            try {
                IMixinService service = MixinService.getService();
                InputStream resource = service.getResourceAsStream(resourcePath);
                if (resource == null) break block6;
                reader = new InputStreamReader(resource);
                ReferenceMapper mapper = ReferenceMapper.readJson(reader);
                mapper.setResourceName(resourcePath);
                referenceMapper = mapper;
            }
            catch (JsonParseException ex2) {
                MessageRouter.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Invalid REFMAP JSON in %s: %s %s", resourcePath, ex2.getClass().getName(), ex2.getMessage()));
                Closeables.closeQuietly(reader);
            }
            catch (Exception ex3) {
                MessageRouter.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Failed reading REFMAP JSON from %s: %s %s", resourcePath, ex3.getClass().getName(), ex3.getMessage()));
                {
                    catch (Throwable throwable) {
                        Closeables.closeQuietly(reader);
                        throw throwable;
                    }
                }
                Closeables.closeQuietly(reader);
            }
            Closeables.closeQuietly(reader);
            return referenceMapper;
        }
        Closeables.closeQuietly(reader);
        return DEFAULT_MAPPER;
    }

    public static ReferenceMapper read(Reader reader, String name) {
        try {
            ReferenceMapper mapper = ReferenceMapper.readJson(reader);
            mapper.setResourceName(name);
            return mapper;
        }
        catch (Exception ex2) {
            return DEFAULT_MAPPER;
        }
    }

    private static ReferenceMapper readJson(Reader reader) {
        return new Gson().fromJson(reader, ReferenceMapper.class);
    }
}

