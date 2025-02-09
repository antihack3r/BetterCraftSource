/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.api.IEnvironment
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService$ITransformerLoader
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService$Phase
 */
package org.spongepowered.asm.launch;

import com.google.common.io.Resources;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.IClassProcessor;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.Phases;
import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher;
import org.spongepowered.asm.service.modlauncher.ModLauncherAuditTrail;
import org.spongepowered.asm.transformers.MixinClassReader;

public class MixinLaunchPluginLegacy
implements ILaunchPluginService,
IClassBytecodeProvider {
    public static final String NAME = "mixin";
    private final List<IClassProcessor> processors = new ArrayList<IClassProcessor>();
    private List<String> commandLineMixins;
    private ILaunchPluginService.ITransformerLoader transformerLoader;
    private MixinServiceModLauncher service;
    private ModLauncherAuditTrail auditTrail;

    public String name() {
        return NAME;
    }

    public EnumSet<ILaunchPluginService.Phase> handlesClass(Type classType, boolean isEmpty) {
        throw new IllegalStateException("Outdated ModLauncher");
    }

    public boolean processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType) {
        throw new IllegalStateException("Outdated ModLauncher");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public EnumSet<ILaunchPluginService.Phase> handlesClass(Type classType, boolean isEmpty, String reason) {
        if (NAME.equals(reason)) {
            return Phases.NONE;
        }
        EnumSet<ILaunchPluginService.Phase> phases = EnumSet.noneOf(ILaunchPluginService.Phase.class);
        List<IClassProcessor> list = this.processors;
        synchronized (list) {
            for (IClassProcessor postProcessor : this.processors) {
                EnumSet<ILaunchPluginService.Phase> processorVote = postProcessor.handlesClass(classType, isEmpty, reason);
                if (processorVote == null) continue;
                phases.addAll(processorVote);
            }
        }
        return phases;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, String reason) {
        boolean processed = false;
        List<IClassProcessor> list = this.processors;
        synchronized (list) {
            for (IClassProcessor processor : this.processors) {
                processed |= processor.processClass(phase, classNode, classType, reason);
            }
        }
        return processed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void init(IEnvironment environment, List<String> commandLineMixins) {
        IMixinService service = MixinService.getService();
        if (!(service instanceof MixinServiceModLauncher)) {
            throw new IllegalStateException("Unsupported service type for ModLauncher Mixin Service");
        }
        this.service = (MixinServiceModLauncher)service;
        this.auditTrail = (ModLauncherAuditTrail)this.service.getAuditTrail();
        List<IClassProcessor> list = this.processors;
        synchronized (list) {
            this.processors.addAll(this.service.getProcessors());
        }
        this.commandLineMixins = commandLineMixins;
        this.service.onInit(this);
    }

    public void customAuditConsumer(String className, Consumer<String[]> auditDataAcceptor) {
        if (this.auditTrail != null) {
            this.auditTrail.setConsumer(className, auditDataAcceptor);
        }
    }

    @Deprecated
    public void addResource(Path resource, String name) {
        this.service.getPrimaryContainer().addResource(name, resource);
    }

    public void offerResource(Path resource, String name) {
        this.service.getPrimaryContainer().addResource(name, resource);
    }

    public void addResources(List resources) {
        this.service.getPrimaryContainer().addResources(resources);
    }

    public <T> T getExtension() {
        return null;
    }

    public void initializeLaunch(ILaunchPluginService.ITransformerLoader transformerLoader, Path[] specialPaths) {
        this.initializeLaunch(transformerLoader);
    }

    protected void initializeLaunch(ILaunchPluginService.ITransformerLoader transformerLoader) {
        this.transformerLoader = transformerLoader;
        MixinBootstrap.doInit(CommandLineOptions.of(this.commandLineMixins));
        MixinBootstrap.inject();
        this.service.onStartup();
    }

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return this.getClassNode(name, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        byte[] classBytes;
        if (!runTransformers) {
            throw new IllegalArgumentException("ModLauncher service does not currently support retrieval of untransformed bytecode");
        }
        String canonicalName = name.replace('/', '.');
        String internalName = name.replace('.', '/');
        try {
            classBytes = this.transformerLoader.buildTransformedClassNodeFor(canonicalName);
        }
        catch (ClassNotFoundException ex2) {
            URL url = Thread.currentThread().getContextClassLoader().getResource(internalName + ".class");
            if (url == null) {
                throw ex2;
            }
            try {
                classBytes = Resources.asByteSource(url).read();
            }
            catch (IOException ioex) {
                throw ex2;
            }
        }
        if (classBytes != null && classBytes.length != 0) {
            ClassNode classNode = new ClassNode();
            MixinClassReader classReader = new MixinClassReader(classBytes, canonicalName);
            classReader.accept(classNode, 8);
            return classNode;
        }
        Type classType = Type.getObjectType(internalName);
        List<IClassProcessor> list = this.processors;
        synchronized (list) {
            for (IClassProcessor processor : this.processors) {
                ClassNode classNode;
                if (!processor.generatesClass(classType) || !processor.generateClass(classType, classNode = new ClassNode())) continue;
                return classNode;
            }
        }
        throw new ClassNotFoundException(canonicalName);
    }
}

