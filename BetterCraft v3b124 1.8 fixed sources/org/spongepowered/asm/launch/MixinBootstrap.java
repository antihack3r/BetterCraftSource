/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.launch;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.launch.MixinInitialisationError;
import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.throwables.MixinError;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;

public abstract class MixinBootstrap {
    public static final String VERSION = "0.8.5";
    private static final String MIXIN_TRANSFORMER_FACTORY_CLASS = "org.spongepowered.asm.mixin.transformer.MixinTransformer$Factory";
    private static boolean initialised = false;
    private static boolean initState = true;
    private static ILogger logger;
    private static MixinPlatformManager platform;

    private MixinBootstrap() {
    }

    @Deprecated
    public static void addProxy() {
        MixinService.getService().beginPhase();
    }

    public static MixinPlatformManager getPlatform() {
        if (platform == null) {
            Object globalPlatformManager = GlobalProperties.get(GlobalProperties.Keys.PLATFORM_MANAGER);
            if (globalPlatformManager instanceof MixinPlatformManager) {
                platform = (MixinPlatformManager)globalPlatformManager;
            } else {
                platform = new MixinPlatformManager();
                GlobalProperties.put(GlobalProperties.Keys.PLATFORM_MANAGER, platform);
                platform.init();
            }
        }
        return platform;
    }

    public static void init() {
        if (!MixinBootstrap.start()) {
            return;
        }
        MixinBootstrap.doInit(CommandLineOptions.defaultArgs());
    }

    static boolean start() {
        if (MixinBootstrap.isSubsystemRegistered()) {
            if (!MixinBootstrap.checkSubsystemVersion()) {
                throw new MixinInitialisationError("Mixin subsystem version " + MixinBootstrap.getActiveSubsystemVersion() + " was already initialised. Cannot bootstrap version " + VERSION);
            }
            return false;
        }
        MixinBootstrap.registerSubsystem(VERSION);
        MixinBootstrap.offerInternals();
        if (!initialised) {
            initialised = true;
            MixinEnvironment.Phase initialPhase = MixinService.getService().getInitialPhase();
            if (initialPhase == MixinEnvironment.Phase.DEFAULT) {
                logger.error("Initialising mixin subsystem after game pre-init phase! Some mixins may be skipped.", new Object[0]);
                MixinEnvironment.init(initialPhase);
                MixinBootstrap.getPlatform().prepare(CommandLineOptions.defaultArgs());
                initState = false;
            } else {
                MixinEnvironment.init(initialPhase);
            }
            MixinService.getService().beginPhase();
        }
        MixinBootstrap.getPlatform();
        return true;
    }

    @Deprecated
    static void doInit(List<String> args) {
        MixinBootstrap.doInit(CommandLineOptions.ofArgs(args));
    }

    static void doInit(CommandLineOptions args) {
        if (!initialised) {
            if (MixinBootstrap.isSubsystemRegistered()) {
                logger.warn("Multiple Mixin containers present, init suppressed for {}", VERSION);
                return;
            }
            throw new IllegalStateException("MixinBootstrap.doInit() called before MixinBootstrap.start()");
        }
        MixinBootstrap.getPlatform().getPhaseProviderClasses();
        if (initState) {
            MixinBootstrap.getPlatform().prepare(args);
            MixinService.getService().init();
        }
    }

    static void inject() {
        MixinBootstrap.getPlatform().inject();
    }

    private static boolean isSubsystemRegistered() {
        return GlobalProperties.get(GlobalProperties.Keys.INIT) != null;
    }

    private static boolean checkSubsystemVersion() {
        return VERSION.equals(MixinBootstrap.getActiveSubsystemVersion());
    }

    private static Object getActiveSubsystemVersion() {
        Object version = GlobalProperties.get(GlobalProperties.Keys.INIT);
        return version != null ? version : "";
    }

    private static void registerSubsystem(String version) {
        GlobalProperties.put(GlobalProperties.Keys.INIT, version);
    }

    private static void offerInternals() {
        IMixinService service = MixinService.getService();
        try {
            for (IMixinInternal internal : MixinBootstrap.getInternals()) {
                service.offer(internal);
            }
        }
        catch (AbstractMethodError ex2) {
            ex2.printStackTrace();
        }
    }

    private static List<IMixinInternal> getInternals() throws MixinError {
        ArrayList<IMixinInternal> internals = new ArrayList<IMixinInternal>();
        try {
            Class<?> clTransformerFactory = Class.forName(MIXIN_TRANSFORMER_FACTORY_CLASS);
            Constructor<?> ctor = clTransformerFactory.getDeclaredConstructor(new Class[0]);
            ctor.setAccessible(true);
            internals.add((IMixinInternal)ctor.newInstance(new Object[0]));
        }
        catch (ReflectiveOperationException ex2) {
            throw new MixinError(ex2);
        }
        return internals;
    }

    static {
        MixinService.boot();
        MixinService.getService().prepare();
        logger = MixinService.getService().getLogger("mixin");
    }
}

