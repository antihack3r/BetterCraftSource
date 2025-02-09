// 
// Decompiled by Procyon v0.6.0
// 

package org.spongepowered.tools.obfuscation.mapping.common;

import com.google.common.collect.HashBiMap;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import com.google.common.collect.BiMap;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import org.spongepowered.tools.obfuscation.mapping.IMappingProvider;

public abstract class MappingProvider implements IMappingProvider
{
    protected final Messager messager;
    protected final Filer filer;
    protected final BiMap<String, String> packageMap;
    protected final BiMap<String, String> classMap;
    protected final BiMap<MappingField, MappingField> fieldMap;
    protected final BiMap<MappingMethod, MappingMethod> methodMap;
    
    public MappingProvider(final Messager messager, final Filer filer) {
        this.packageMap = (BiMap<String, String>)HashBiMap.create();
        this.classMap = (BiMap<String, String>)HashBiMap.create();
        this.fieldMap = (BiMap<MappingField, MappingField>)HashBiMap.create();
        this.methodMap = (BiMap<MappingMethod, MappingMethod>)HashBiMap.create();
        this.messager = messager;
        this.filer = filer;
    }
    
    @Override
    public void clear() {
        this.packageMap.clear();
        this.classMap.clear();
        this.fieldMap.clear();
        this.methodMap.clear();
    }
    
    @Override
    public boolean isEmpty() {
        return this.packageMap.isEmpty() && this.classMap.isEmpty() && this.fieldMap.isEmpty() && this.methodMap.isEmpty();
    }
    
    @Override
    public MappingMethod getMethodMapping(final MappingMethod method) {
        return this.methodMap.get(method);
    }
    
    @Override
    public MappingField getFieldMapping(final MappingField field) {
        return this.fieldMap.get(field);
    }
    
    @Override
    public String getClassMapping(final String className) {
        return this.classMap.get(className);
    }
    
    @Override
    public String getPackageMapping(final String packageName) {
        return this.packageMap.get(packageName);
    }
}
