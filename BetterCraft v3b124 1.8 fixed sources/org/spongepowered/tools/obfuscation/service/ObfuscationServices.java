/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.tools.obfuscation.service;

import com.google.common.base.Joiner;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import javax.tools.Diagnostic;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.interfaces.IMessagerEx;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.service.IObfuscationService;
import org.spongepowered.tools.obfuscation.service.ObfuscationTypeDescriptor;

public final class ObfuscationServices {
    private static ObfuscationServices instance;
    private final ServiceLoader<IObfuscationService> serviceLoader;
    private final Set<IObfuscationService> services = new HashSet<IObfuscationService>();
    private boolean providerInitDone = false;

    private ObfuscationServices() {
        this.serviceLoader = ServiceLoader.load(IObfuscationService.class, this.getClass().getClassLoader());
    }

    public static ObfuscationServices getInstance() {
        if (instance == null) {
            instance = new ObfuscationServices();
        }
        return instance;
    }

    public void initProviders(IMixinAnnotationProcessor ap2) {
        if (this.providerInitDone) {
            return;
        }
        this.providerInitDone = true;
        boolean defaultIsPresent = false;
        LinkedHashMap<String, LinkedHashSet<String>> supportedTypes = new LinkedHashMap<String, LinkedHashSet<String>>();
        try {
            for (IObfuscationService service : this.serviceLoader) {
                if (this.services.contains(service)) continue;
                this.services.add(service);
                String serviceName = service.getClass().getSimpleName();
                Collection<ObfuscationTypeDescriptor> obfTypes = service.getObfuscationTypes(ap2);
                if (obfTypes == null) continue;
                for (ObfuscationTypeDescriptor obfType : obfTypes) {
                    try {
                        ObfuscationType type = ObfuscationType.create(obfType, ap2);
                        LinkedHashSet<String> types = (LinkedHashSet<String>)supportedTypes.get(serviceName);
                        if (types == null) {
                            types = new LinkedHashSet<String>();
                            supportedTypes.put(serviceName, types);
                        }
                        types.add(type.getKey());
                        defaultIsPresent |= type.isDefault();
                    }
                    catch (Exception ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
        }
        catch (ServiceConfigurationError serviceError) {
            ap2.printMessage(Diagnostic.Kind.ERROR, (CharSequence)(serviceError.getClass().getSimpleName() + ": " + serviceError.getMessage()));
            serviceError.printStackTrace();
        }
        if (supportedTypes.size() > 0) {
            StringBuilder sb2 = new StringBuilder("Supported obfuscation types:");
            for (Map.Entry supportedType : supportedTypes.entrySet()) {
                sb2.append(' ').append((String)supportedType.getKey()).append(" supports [").append(Joiner.on(',').join((Iterable)supportedType.getValue())).append(']');
            }
            ap2.printMessage(IMessagerEx.MessageType.INFO, (CharSequence)sb2.toString());
        }
        if (!defaultIsPresent) {
            String defaultEnv = ap2.getOption("defaultObfuscationEnv");
            if (defaultEnv == null) {
                ap2.printMessage(Diagnostic.Kind.WARNING, (CharSequence)"No default obfuscation environment was specified and \"searge\" is not available. Please ensure defaultObfuscationEnv is specified in your build configuration");
            } else {
                ap2.printMessage(Diagnostic.Kind.WARNING, (CharSequence)("Specified default obfuscation environment \"" + defaultEnv.toLowerCase(Locale.ROOT) + "\" was not defined. This probably means your build configuration is out of date or a required service is missing"));
            }
        }
    }

    public Set<String> getSupportedOptions() {
        HashSet<String> supportedOptions = new HashSet<String>();
        for (IObfuscationService provider : this.services) {
            Set<String> options = provider.getSupportedOptions();
            if (options == null) continue;
            supportedOptions.addAll(options);
        }
        return supportedOptions;
    }

    public IObfuscationService getService(Class<? extends IObfuscationService> serviceClass) {
        for (IObfuscationService service : this.services) {
            if (!serviceClass.getName().equals(service.getClass().getName())) continue;
            return service;
        }
        return null;
    }
}

