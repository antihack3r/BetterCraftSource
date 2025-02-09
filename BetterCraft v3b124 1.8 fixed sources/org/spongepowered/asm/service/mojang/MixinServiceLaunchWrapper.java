/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.service.mojang;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.launch.platform.MainAttributes;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.ILegacyClassTransformer;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinServiceAbstract;
import org.spongepowered.asm.service.mojang.LaunchClassLoaderUtil;
import org.spongepowered.asm.service.mojang.LegacyTransformerHandle;
import org.spongepowered.asm.service.mojang.LoggerAdapterLog4j2;
import org.spongepowered.asm.transformers.MixinClassReader;
import org.spongepowered.asm.util.Files;
import org.spongepowered.asm.util.perf.Profiler;

public class MixinServiceLaunchWrapper
extends MixinServiceAbstract
implements IClassProvider,
IClassBytecodeProvider,
ITransformerProvider {
    public static final GlobalProperties.Keys BLACKBOARD_KEY_TWEAKCLASSES = GlobalProperties.Keys.of("TweakClasses");
    public static final GlobalProperties.Keys BLACKBOARD_KEY_TWEAKS = GlobalProperties.Keys.of("Tweaks");
    private static final String MIXIN_TWEAKER_CLASS = "org.spongepowered.asm.launch.MixinTweaker";
    private static final String STATE_TWEAKER = "org.spongepowered.asm.mixin.EnvironmentStateTweaker";
    private static final String TRANSFORMER_PROXY_CLASS = "org.spongepowered.asm.mixin.transformer.Proxy";
    private static final Set<String> excludeTransformers = Sets.newHashSet("net.minecraftforge.fml.common.asm.transformers.EventSubscriptionTransformer", "cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer", "net.minecraftforge.fml.common.asm.transformers.TerminalTransformer", "cpw.mods.fml.common.asm.transformers.TerminalTransformer");
    private static final Logger logger = LogManager.getLogger();
    private final LaunchClassLoaderUtil classLoaderUtil = new LaunchClassLoaderUtil(Launch.classLoader);
    private List<ILegacyClassTransformer> delegatedTransformers;
    private IClassNameTransformer nameTransformer;

    @Override
    public String getName() {
        return "LaunchWrapper";
    }

    @Override
    public boolean isValid() {
        try {
            Launch.classLoader.hashCode();
        }
        catch (Throwable ex2) {
            return false;
        }
        return true;
    }

    @Override
    public void prepare() {
        Launch.classLoader.addClassLoaderExclusion("org.spongepowered.asm.launch.");
    }

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        String command = System.getProperty("sun.java.command");
        if (command != null && command.contains("GradleStart")) {
            System.setProperty("mixin.env.remapRefMap", "true");
        }
        if (MixinServiceLaunchWrapper.findInStackTrace("net.minecraft.launchwrapper.Launch", "launch") > 132) {
            return MixinEnvironment.Phase.DEFAULT;
        }
        return MixinEnvironment.Phase.PREINIT;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_8;
    }

    @Override
    protected ILogger createLogger(String name) {
        return new LoggerAdapterLog4j2(name);
    }

    @Override
    public void init() {
        List tweakClasses;
        if (MixinServiceLaunchWrapper.findInStackTrace("net.minecraft.launchwrapper.Launch", "launch") < 4) {
            logger.error("MixinBootstrap.doInit() called during a tweak constructor!");
        }
        if ((tweakClasses = (List)GlobalProperties.get(BLACKBOARD_KEY_TWEAKCLASSES)) != null) {
            tweakClasses.add(STATE_TWEAKER);
        }
        super.init();
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return ImmutableList.of("org.spongepowered.asm.launch.platform.MixinPlatformAgentFMLLegacy", "org.spongepowered.asm.launch.platform.MixinPlatformAgentLiteLoaderLegacy");
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        URI uri = null;
        try {
            uri = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            if (uri != null) {
                return new ContainerHandleURI(uri);
            }
        }
        catch (URISyntaxException ex2) {
            ex2.printStackTrace();
        }
        return new ContainerHandleVirtual(this.getName());
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        ImmutableList.Builder<IContainerHandle> list = ImmutableList.builder();
        this.getContainersFromClassPath(list);
        this.getContainersFromAgents(list);
        return list.build();
    }

    private void getContainersFromClassPath(ImmutableList.Builder<IContainerHandle> list) {
        URL[] sources = this.getClassPath();
        if (sources != null) {
            for (URL url : sources) {
                try {
                    MainAttributes attributes;
                    String tweaker;
                    URI uri = url.toURI();
                    logger.debug("Scanning {} for mixin tweaker", uri);
                    if (!"file".equals(uri.getScheme()) || !Files.toFile(uri).exists() || !MIXIN_TWEAKER_CLASS.equals(tweaker = (attributes = MainAttributes.of(uri)).get("TweakClass"))) continue;
                    list.add((Object)new ContainerHandleURI(uri));
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return this;
    }

    @Override
    public IClassTracker getClassTracker() {
        return this.classLoaderUtil;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return Launch.classLoader.findClass(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Launch.classLoader);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Launch.class.getClassLoader());
    }

    @Override
    public void beginPhase() {
        Launch.classLoader.registerTransformer(TRANSFORMER_PROXY_CLASS);
        this.delegatedTransformers = null;
    }

    @Override
    public void checkEnv(Object bootSource) {
        if (bootSource.getClass().getClassLoader() != Launch.class.getClassLoader()) {
            throw new MixinException("Attempted to init the mixin environment in the wrong classloader");
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return Launch.classLoader.getResourceAsStream(name);
    }

    @Override
    @Deprecated
    public URL[] getClassPath() {
        return Launch.classLoader.getSources().toArray(new URL[0]);
    }

    @Override
    public Collection<ITransformer> getTransformers() {
        List<IClassTransformer> transformers = Launch.classLoader.getTransformers();
        ArrayList<ITransformer> wrapped = new ArrayList<ITransformer>(transformers.size());
        for (IClassTransformer transformer : transformers) {
            if (transformer instanceof ITransformer) {
                wrapped.add((ITransformer)((Object)transformer));
            } else {
                wrapped.add(new LegacyTransformerHandle(transformer));
            }
            if (!(transformer instanceof IClassNameTransformer)) continue;
            logger.debug("Found name transformer: {}", transformer.getClass().getName());
            this.nameTransformer = (IClassNameTransformer)((Object)transformer);
        }
        return wrapped;
    }

    public List<ITransformer> getDelegatedTransformers() {
        return Collections.unmodifiableList(this.getDelegatedLegacyTransformers());
    }

    private List<ILegacyClassTransformer> getDelegatedLegacyTransformers() {
        if (this.delegatedTransformers == null) {
            this.buildTransformerDelegationList();
        }
        return this.delegatedTransformers;
    }

    private void buildTransformerDelegationList() {
        logger.debug("Rebuilding transformer delegation list:");
        this.delegatedTransformers = new ArrayList<ILegacyClassTransformer>();
        for (ITransformer transformer : this.getTransformers()) {
            if (!(transformer instanceof ILegacyClassTransformer)) continue;
            ILegacyClassTransformer legacyTransformer = (ILegacyClassTransformer)transformer;
            String transformerName = legacyTransformer.getName();
            boolean include = true;
            for (String excludeClass : excludeTransformers) {
                if (!transformerName.contains(excludeClass)) continue;
                include = false;
                break;
            }
            if (include && !legacyTransformer.isDelegationExcluded()) {
                logger.debug("  Adding:    {}", transformerName);
                this.delegatedTransformers.add(legacyTransformer);
                continue;
            }
            logger.debug("  Excluding: {}", transformerName);
        }
        logger.debug("Transformer delegation list created with {} entries", this.delegatedTransformers.size());
    }

    @Override
    public void addTransformerExclusion(String name) {
        excludeTransformers.add(name);
        this.delegatedTransformers = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public byte[] getClassBytes(String name, String transformedName) throws IOException {
        byte[] classBytes = Launch.classLoader.getClassBytes(name);
        if (classBytes != null) {
            return classBytes;
        }
        URLClassLoader appClassLoader = Launch.class.getClassLoader() instanceof URLClassLoader ? (URLClassLoader)Launch.class.getClassLoader() : new URLClassLoader(new URL[0], Launch.class.getClassLoader());
        InputStream classStream = null;
        try {
            String resourcePath = transformedName.replace('.', '/').concat(".class");
            classStream = appClassLoader.getResourceAsStream(resourcePath);
            byte[] byArray = ByteStreams.toByteArray(classStream);
            Closeables.closeQuietly(classStream);
            return byArray;
        }
        catch (Exception ex2) {
            byte[] byArray = null;
            return byArray;
        }
        finally {
            Closeables.closeQuietly(classStream);
        }
    }

    @Deprecated
    public byte[] getClassBytes(String className, boolean runTransformers) throws ClassNotFoundException, IOException {
        String transformedName = className.replace('/', '.');
        String name = this.unmapClassName(transformedName);
        Profiler profiler = Profiler.getProfiler("mixin");
        Profiler.Section loadTime = profiler.begin(1, "class.load");
        byte[] classBytes = this.getClassBytes(name, transformedName);
        loadTime.end();
        if (runTransformers) {
            Profiler.Section transformTime = profiler.begin(1, "class.transform");
            classBytes = this.applyTransformers(name, transformedName, classBytes, profiler);
            transformTime.end();
        }
        if (classBytes == null) {
            throw new ClassNotFoundException(String.format("The specified class '%s' was not found", transformedName));
        }
        return classBytes;
    }

    private byte[] applyTransformers(String name, String transformedName, byte[] basicClass, Profiler profiler) {
        if (this.classLoaderUtil.isClassExcluded(name, transformedName)) {
            return basicClass;
        }
        for (ILegacyClassTransformer transformer : this.getDelegatedLegacyTransformers()) {
            this.lock.clear();
            int pos = transformer.getName().lastIndexOf(46);
            String simpleName = transformer.getName().substring(pos + 1);
            Profiler.Section transformTime = profiler.begin(2, simpleName.toLowerCase(Locale.ROOT));
            transformTime.setInfo(transformer.getName());
            basicClass = transformer.transformClassBytes(name, transformedName, basicClass);
            transformTime.end();
            if (!this.lock.isSet()) continue;
            this.addTransformerExclusion(transformer.getName());
            this.lock.clear();
            logger.info("A re-entrant transformer '{}' was detected and will no longer process meta class data", transformer.getName());
        }
        return basicClass;
    }

    private String unmapClassName(String className) {
        if (this.nameTransformer == null) {
            this.findNameTransformer();
        }
        if (this.nameTransformer != null) {
            return this.nameTransformer.unmapClassName(className);
        }
        return className;
    }

    private void findNameTransformer() {
        List<IClassTransformer> transformers = Launch.classLoader.getTransformers();
        for (IClassTransformer transformer : transformers) {
            if (!(transformer instanceof IClassNameTransformer)) continue;
            logger.debug("Found name transformer: {}", transformer.getClass().getName());
            this.nameTransformer = (IClassNameTransformer)((Object)transformer);
        }
    }

    @Override
    public ClassNode getClassNode(String className) throws ClassNotFoundException, IOException {
        return this.getClassNode(className, this.getClassBytes(className, true), 8);
    }

    @Override
    public ClassNode getClassNode(String className, boolean runTransformers) throws ClassNotFoundException, IOException {
        return this.getClassNode(className, this.getClassBytes(className, true), 8);
    }

    private ClassNode getClassNode(String className, byte[] classBytes, int flags) {
        ClassNode classNode = new ClassNode();
        MixinClassReader classReader = new MixinClassReader(classBytes, className);
        classReader.accept(classNode, flags);
        return classNode;
    }

    private static int findInStackTrace(String className, String methodName) {
        StackTraceElement[] stackTrace;
        Thread currentThread = Thread.currentThread();
        if (!"main".equals(currentThread.getName())) {
            return 0;
        }
        for (StackTraceElement s2 : stackTrace = currentThread.getStackTrace()) {
            if (!className.equals(s2.getClassName()) || !methodName.equals(s2.getMethodName())) continue;
            return s2.getLineNumber();
        }
        return 0;
    }
}

