/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.Launcher
 *  cpw.mods.modlauncher.api.ITransformationService
 */
package org.spongepowered.asm.service.modlauncher;

import com.google.common.collect.ImmutableList;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.ITransformationService;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import org.spongepowered.asm.launch.IClassProcessor;
import org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinServiceAbstract;
import org.spongepowered.asm.service.modlauncher.LoggerAdapterLog4j2;
import org.spongepowered.asm.service.modlauncher.MixinTransformationHandler;
import org.spongepowered.asm.service.modlauncher.ModLauncherAuditTrail;
import org.spongepowered.asm.service.modlauncher.ModLauncherClassProvider;
import org.spongepowered.asm.service.modlauncher.ModLauncherClassTracker;
import org.spongepowered.asm.util.IConsumer;

public class MixinServiceModLauncher
extends MixinServiceAbstract {
    private static final String MODLAUNCHER_4_SPECIFICATION_VERSION = "4.0";
    private static final String MODLAUNCHER_9_SPECIFICATION_VERSION = "8.0";
    private static final String CONTAINER_PACKAGE = "org.spongepowered.asm.launch.platform.container.";
    private static final String MODLAUNCHER_4_ROOT_CONTAINER_CLASS = "org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher";
    private static final String MODLAUNCHER_9_ROOT_CONTAINER_CLASS = "org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncherEx";
    private IClassProvider classProvider;
    private IClassBytecodeProvider bytecodeProvider;
    private MixinTransformationHandler transformationHandler;
    private ModLauncherClassTracker classTracker;
    private ModLauncherAuditTrail auditTrail;
    private IConsumer<MixinEnvironment.Phase> phaseConsumer;
    private volatile boolean initialised;
    private ContainerHandleModLauncher rootContainer;
    private MixinEnvironment.CompatibilityLevel minCompatibilityLevel = MixinEnvironment.CompatibilityLevel.JAVA_8;

    public MixinServiceModLauncher() {
        Package pkg = ITransformationService.class.getPackage();
        if (pkg.isCompatibleWith(MODLAUNCHER_9_SPECIFICATION_VERSION)) {
            this.createRootContainer(MODLAUNCHER_9_ROOT_CONTAINER_CLASS);
            this.minCompatibilityLevel = MixinEnvironment.CompatibilityLevel.JAVA_16;
        } else {
            this.createRootContainer(MODLAUNCHER_4_ROOT_CONTAINER_CLASS);
        }
    }

    public void onInit(IClassBytecodeProvider bytecodeProvider) {
        if (this.initialised) {
            throw new IllegalStateException("Already initialised");
        }
        this.initialised = true;
        this.bytecodeProvider = bytecodeProvider;
    }

    private void createRootContainer(String rootContainerClassName) {
        try {
            Class<?> clRootContainer = this.getClassProvider().findClass(rootContainerClassName);
            Constructor<?> ctor = clRootContainer.getDeclaredConstructor(String.class);
            this.rootContainer = (ContainerHandleModLauncher)ctor.newInstance(this.getName());
        }
        catch (ReflectiveOperationException ex2) {
            ex2.printStackTrace();
        }
    }

    public void onStartup() {
        this.phaseConsumer.accept(MixinEnvironment.Phase.DEFAULT);
    }

    @Override
    public void offer(IMixinInternal internal) {
        if (internal instanceof IMixinTransformerFactory) {
            this.getTransformationHandler().offer((IMixinTransformerFactory)internal);
        }
        super.offer(internal);
    }

    @Override
    public void wire(MixinEnvironment.Phase phase, IConsumer<MixinEnvironment.Phase> phaseConsumer) {
        super.wire(phase, phaseConsumer);
        this.phaseConsumer = phaseConsumer;
    }

    @Override
    public String getName() {
        return "ModLauncher";
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return this.minCompatibilityLevel;
    }

    @Override
    protected ILogger createLogger(String name) {
        return new LoggerAdapterLog4j2(name);
    }

    @Override
    public boolean isValid() {
        try {
            Launcher.INSTANCE.hashCode();
            Package pkg = ITransformationService.class.getPackage();
            if (!pkg.isCompatibleWith(MODLAUNCHER_4_SPECIFICATION_VERSION)) {
                return false;
            }
        }
        catch (Throwable th2) {
            return false;
        }
        return true;
    }

    @Override
    public IClassProvider getClassProvider() {
        if (this.classProvider == null) {
            this.classProvider = new ModLauncherClassProvider();
        }
        return this.classProvider;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        if (this.bytecodeProvider == null) {
            throw new IllegalStateException("Service initialisation incomplete");
        }
        return this.bytecodeProvider;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return null;
    }

    @Override
    public IClassTracker getClassTracker() {
        if (this.classTracker == null) {
            this.classTracker = new ModLauncherClassTracker();
        }
        return this.classTracker;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        if (this.auditTrail == null) {
            this.auditTrail = new ModLauncherAuditTrail();
        }
        return this.auditTrail;
    }

    private MixinTransformationHandler getTransformationHandler() {
        if (this.transformationHandler == null) {
            this.transformationHandler = new MixinTransformationHandler();
        }
        return this.transformationHandler;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return ImmutableList.of("org.spongepowered.asm.launch.platform.MixinPlatformAgentMinecraftForge");
    }

    @Override
    public ContainerHandleModLauncher getPrimaryContainer() {
        return this.rootContainer;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }

    public Collection<IClassProcessor> getProcessors() {
        return ImmutableList.of(this.getTransformationHandler(), (IClassProcessor)((Object)this.getClassTracker()));
    }
}

