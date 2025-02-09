/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.FieldNamingStrategy;
import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonIOException;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.ReflectionAccessFilter;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.annotations.JsonAdapter;
import com.viaversion.viaversion.libs.gson.annotations.SerializedName;
import com.viaversion.viaversion.libs.gson.internal.$Gson$Types;
import com.viaversion.viaversion.libs.gson.internal.ConstructorConstructor;
import com.viaversion.viaversion.libs.gson.internal.Excluder;
import com.viaversion.viaversion.libs.gson.internal.ObjectConstructor;
import com.viaversion.viaversion.libs.gson.internal.Primitives;
import com.viaversion.viaversion.libs.gson.internal.ReflectionAccessFilterHelper;
import com.viaversion.viaversion.libs.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.bind.TypeAdapterRuntimeTypeWrapper;
import com.viaversion.viaversion.libs.gson.internal.reflect.ReflectionHelper;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory
implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final Excluder excluder;
    private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
    private final List<ReflectionAccessFilter> reflectionFilters;

    public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder, JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory, List<ReflectionAccessFilter> reflectionFilters) {
        this.constructorConstructor = constructorConstructor;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.excluder = excluder;
        this.jsonAdapterFactory = jsonAdapterFactory;
        this.reflectionFilters = reflectionFilters;
    }

    private boolean includeField(Field f2, boolean serialize) {
        return !this.excluder.excludeClass(f2.getType(), serialize) && !this.excluder.excludeField(f2, serialize);
    }

    private List<String> getFieldNames(Field f2) {
        SerializedName annotation = f2.getAnnotation(SerializedName.class);
        if (annotation == null) {
            String name = this.fieldNamingPolicy.translateName(f2);
            return Collections.singletonList(name);
        }
        String serializedName = annotation.value();
        String[] alternates = annotation.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
        }
        ArrayList<String> fieldNames = new ArrayList<String>(alternates.length + 1);
        fieldNames.add(serializedName);
        Collections.addAll(fieldNames, alternates);
        return fieldNames;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        boolean blockInaccessible;
        Class<T> raw = type.getRawType();
        if (!Object.class.isAssignableFrom(raw)) {
            return null;
        }
        ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
        if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
            throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + raw + ". Register a TypeAdapter for this type or adjust the access filter.");
        }
        boolean bl2 = blockInaccessible = filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
        if (ReflectionHelper.isRecord(raw)) {
            RecordAdapter<T> adapter = new RecordAdapter<T>(raw, this.getBoundFields(gson, type, raw, blockInaccessible, true), blockInaccessible);
            return adapter;
        }
        ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
        return new FieldReflectionAdapter<T>(constructor, this.getBoundFields(gson, type, raw, blockInaccessible, false));
    }

    private static <M extends AccessibleObject> void checkAccessible(Object object, M member) {
        if (!ReflectionAccessFilterHelper.canAccess(member, Modifier.isStatic(((Member)((Object)member)).getModifiers()) ? null : object)) {
            String memberDescription = ReflectionHelper.getAccessibleObjectDescription(member, true);
            throw new JsonIOException(memberDescription + " is not accessible and ReflectionAccessFilter does not permit making it accessible. Register a TypeAdapter for the declaring type, adjust the access filter or increase the visibility of the element and its declaring type.");
        }
    }

    private BoundField createBoundField(final Gson context, Field field, final Method accessor, String name, final TypeToken<?> fieldType, boolean serialize, boolean deserialize, final boolean blockInaccessible) {
        boolean jsonAdapterPresent;
        final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        int modifiers = field.getModifiers();
        final boolean isStaticFinalField = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
        JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        TypeAdapter<?> mapped = null;
        if (annotation != null) {
            mapped = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, context, fieldType, annotation);
        }
        boolean bl2 = jsonAdapterPresent = mapped != null;
        if (mapped == null) {
            mapped = context.getAdapter(fieldType);
        }
        final TypeAdapter<?> typeAdapter = mapped;
        return new BoundField(name, field, serialize, deserialize){

            @Override
            void write(JsonWriter writer, Object source) throws IOException, IllegalAccessException {
                Object fieldValue;
                if (!this.serialized) {
                    return;
                }
                if (blockInaccessible) {
                    if (accessor == null) {
                        ReflectiveTypeAdapterFactory.checkAccessible(source, this.field);
                    } else {
                        ReflectiveTypeAdapterFactory.checkAccessible(source, accessor);
                    }
                }
                if (accessor != null) {
                    try {
                        fieldValue = accessor.invoke(source, new Object[0]);
                    }
                    catch (InvocationTargetException e2) {
                        String accessorDescription = ReflectionHelper.getAccessibleObjectDescription(accessor, false);
                        throw new JsonIOException("Accessor " + accessorDescription + " threw exception", e2.getCause());
                    }
                } else {
                    fieldValue = this.field.get(source);
                }
                if (fieldValue == source) {
                    return;
                }
                writer.name(this.name);
                TypeAdapter t2 = jsonAdapterPresent ? typeAdapter : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
                t2.write(writer, fieldValue);
            }

            @Override
            void readIntoArray(JsonReader reader, int index, Object[] target) throws IOException, JsonParseException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue == null && isPrimitive) {
                    throw new JsonParseException("null is not allowed as value for record component '" + this.fieldName + "' of primitive type; at path " + reader.getPath());
                }
                target[index] = fieldValue;
            }

            @Override
            void readIntoField(JsonReader reader, Object target) throws IOException, IllegalAccessException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    if (blockInaccessible) {
                        ReflectiveTypeAdapterFactory.checkAccessible(target, this.field);
                    } else if (isStaticFinalField) {
                        String fieldDescription = ReflectionHelper.getAccessibleObjectDescription(this.field, false);
                        throw new JsonIOException("Cannot set value of 'static final' " + fieldDescription);
                    }
                    this.field.set(target, fieldValue);
                }
            }
        };
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw, boolean blockInaccessible, boolean isRecord) {
        LinkedHashMap<String, BoundField> result = new LinkedHashMap<String, BoundField>();
        if (raw.isInterface()) {
            return result;
        }
        Class<?> originalRaw = raw;
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            if (raw != originalRaw && fields.length > 0) {
                ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
                if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
                    throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + raw + " (supertype of " + originalRaw + "). Register a TypeAdapter for this type or adjust the access filter.");
                }
                blockInaccessible = filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
            }
            for (Field field : fields) {
                boolean serialize = this.includeField(field, true);
                boolean deserialize = this.includeField(field, false);
                if (!serialize && !deserialize) continue;
                Method accessor = null;
                if (isRecord) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        deserialize = false;
                    } else {
                        accessor = ReflectionHelper.getAccessor(raw, field);
                        if (!blockInaccessible) {
                            ReflectionHelper.makeAccessible(accessor);
                        }
                        if (accessor.getAnnotation(SerializedName.class) != null && field.getAnnotation(SerializedName.class) == null) {
                            String methodDescription = ReflectionHelper.getAccessibleObjectDescription(accessor, false);
                            throw new JsonIOException("@SerializedName on " + methodDescription + " is not supported");
                        }
                    }
                }
                if (!blockInaccessible && accessor == null) {
                    ReflectionHelper.makeAccessible(field);
                }
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                List<String> fieldNames = this.getFieldNames(field);
                BoundField previous = null;
                int size = fieldNames.size();
                for (int i2 = 0; i2 < size; ++i2) {
                    String name = fieldNames.get(i2);
                    if (i2 != 0) {
                        serialize = false;
                    }
                    BoundField boundField = this.createBoundField(context, field, accessor, name, TypeToken.get(fieldType), serialize, deserialize, blockInaccessible);
                    BoundField replaced = result.put(name, boundField);
                    if (previous != null) continue;
                    previous = replaced;
                }
                if (previous == null) continue;
                throw new IllegalArgumentException("Class " + originalRaw.getName() + " declares multiple JSON fields named '" + previous.name + "'; conflict is caused by fields " + ReflectionHelper.fieldToString(previous.field) + " and " + ReflectionHelper.fieldToString(field));
            }
            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }

    private static final class RecordAdapter<T>
    extends Adapter<T, Object[]> {
        static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = RecordAdapter.primitiveDefaults();
        private final Constructor<T> constructor;
        private final Object[] constructorArgsDefaults;
        private final Map<String, Integer> componentIndices = new HashMap<String, Integer>();

        RecordAdapter(Class<T> raw, Map<String, BoundField> boundFields, boolean blockInaccessible) {
            super(boundFields);
            this.constructor = ReflectionHelper.getCanonicalRecordConstructor(raw);
            if (blockInaccessible) {
                ReflectiveTypeAdapterFactory.checkAccessible(null, this.constructor);
            } else {
                ReflectionHelper.makeAccessible(this.constructor);
            }
            String[] componentNames = ReflectionHelper.getRecordComponentNames(raw);
            for (int i2 = 0; i2 < componentNames.length; ++i2) {
                this.componentIndices.put(componentNames[i2], i2);
            }
            Class<?>[] parameterTypes = this.constructor.getParameterTypes();
            this.constructorArgsDefaults = new Object[parameterTypes.length];
            for (int i3 = 0; i3 < parameterTypes.length; ++i3) {
                this.constructorArgsDefaults[i3] = PRIMITIVE_DEFAULTS.get(parameterTypes[i3]);
            }
        }

        private static Map<Class<?>, Object> primitiveDefaults() {
            HashMap zeroes = new HashMap();
            zeroes.put(Byte.TYPE, (byte)0);
            zeroes.put(Short.TYPE, (short)0);
            zeroes.put(Integer.TYPE, 0);
            zeroes.put(Long.TYPE, 0L);
            zeroes.put(Float.TYPE, Float.valueOf(0.0f));
            zeroes.put(Double.TYPE, 0.0);
            zeroes.put(Character.TYPE, Character.valueOf('\u0000'));
            zeroes.put(Boolean.TYPE, false);
            return zeroes;
        }

        @Override
        Object[] createAccumulator() {
            return (Object[])this.constructorArgsDefaults.clone();
        }

        @Override
        void readField(Object[] accumulator, JsonReader in2, BoundField field) throws IOException {
            Integer componentIndex = this.componentIndices.get(field.fieldName);
            if (componentIndex == null) {
                throw new IllegalStateException("Could not find the index in the constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' for field with name '" + field.fieldName + "', unable to determine which argument in the constructor the field corresponds to. This is unexpected behavior, as we expect the RecordComponents to have the same names as the fields in the Java class, and that the order of the RecordComponents is the same as the order of the canonical constructor parameters.");
            }
            field.readIntoArray(in2, componentIndex, accumulator);
        }

        @Override
        T finalize(Object[] accumulator) {
            try {
                return this.constructor.newInstance(accumulator);
            }
            catch (IllegalAccessException e2) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e2);
            }
            catch (IllegalArgumentException | InstantiationException e3) {
                throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' with args " + Arrays.toString(accumulator), e3);
            }
            catch (InvocationTargetException e4) {
                throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' with args " + Arrays.toString(accumulator), e4.getCause());
            }
        }
    }

    private static final class FieldReflectionAdapter<T>
    extends Adapter<T, T> {
        private final ObjectConstructor<T> constructor;

        FieldReflectionAdapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields) {
            super(boundFields);
            this.constructor = constructor;
        }

        @Override
        T createAccumulator() {
            return this.constructor.construct();
        }

        @Override
        void readField(T accumulator, JsonReader in2, BoundField field) throws IllegalAccessException, IOException {
            field.readIntoField(in2, accumulator);
        }

        @Override
        T finalize(T accumulator) {
            return accumulator;
        }
    }

    public static abstract class Adapter<T, A>
    extends TypeAdapter<T> {
        final Map<String, BoundField> boundFields;

        Adapter(Map<String, BoundField> boundFields) {
            this.boundFields = boundFields;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            try {
                for (BoundField boundField : this.boundFields.values()) {
                    boundField.write(out, value);
                }
            }
            catch (IllegalAccessException e2) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e2);
            }
            out.endObject();
        }

        @Override
        public T read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            A accumulator = this.createAccumulator();
            try {
                in2.beginObject();
                while (in2.hasNext()) {
                    String name = in2.nextName();
                    BoundField field = this.boundFields.get(name);
                    if (field == null || !field.deserialized) {
                        in2.skipValue();
                        continue;
                    }
                    this.readField(accumulator, in2, field);
                }
            }
            catch (IllegalStateException e2) {
                throw new JsonSyntaxException(e2);
            }
            catch (IllegalAccessException e3) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e3);
            }
            in2.endObject();
            return this.finalize(accumulator);
        }

        abstract A createAccumulator();

        abstract void readField(A var1, JsonReader var2, BoundField var3) throws IllegalAccessException, IOException;

        abstract T finalize(A var1);
    }

    static abstract class BoundField {
        final String name;
        final Field field;
        final String fieldName;
        final boolean serialized;
        final boolean deserialized;

        protected BoundField(String name, Field field, boolean serialized, boolean deserialized) {
            this.name = name;
            this.field = field;
            this.fieldName = field.getName();
            this.serialized = serialized;
            this.deserialized = deserialized;
        }

        abstract void write(JsonWriter var1, Object var2) throws IOException, IllegalAccessException;

        abstract void readIntoArray(JsonReader var1, int var2, Object[] var3) throws IOException, JsonParseException;

        abstract void readIntoField(JsonReader var1, Object var2) throws IOException, IllegalAccessException;
    }
}

