/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.Converter
 *  javax.persistence.PersistenceException
 */
package org.apache.logging.log4j.core.appender.db.jpa.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.PersistenceException;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.appender.db.jpa.converter.ContextMapJsonAttributeConverter;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.spi.DefaultThreadContextStack;

@Converter(autoApply=false)
public class ContextStackJsonAttributeConverter
implements AttributeConverter<ThreadContext.ContextStack, String> {
    public String convertToDatabaseColumn(ThreadContext.ContextStack contextStack) {
        if (contextStack == null) {
            return null;
        }
        try {
            return ContextMapJsonAttributeConverter.OBJECT_MAPPER.writeValueAsString(contextStack.asList());
        }
        catch (IOException e2) {
            throw new PersistenceException("Failed to convert stack list to JSON string.", (Throwable)e2);
        }
    }

    public ThreadContext.ContextStack convertToEntityAttribute(String s2) {
        List list;
        if (Strings.isEmpty(s2)) {
            return null;
        }
        try {
            list = (List)ContextMapJsonAttributeConverter.OBJECT_MAPPER.readValue(s2, (TypeReference)new TypeReference<List<String>>(){});
        }
        catch (IOException e2) {
            throw new PersistenceException("Failed to convert JSON string to list for stack.", (Throwable)e2);
        }
        DefaultThreadContextStack result = new DefaultThreadContextStack(true);
        result.addAll(list);
        return result;
    }
}

