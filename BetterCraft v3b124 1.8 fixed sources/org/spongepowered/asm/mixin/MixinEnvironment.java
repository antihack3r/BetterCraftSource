/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IEnvironmentTokenProvider;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.obfuscation.RemapperChain;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.service.MixinServiceAbstract;
import org.spongepowered.asm.util.IConsumer;
import org.spongepowered.asm.util.ITokenProvider;
import org.spongepowered.asm.util.JavaVersion;
import org.spongepowered.asm.util.PrettyPrinter;
import org.spongepowered.asm.util.asm.ASM;
import org.spongepowered.asm.util.perf.Profiler;

public final class MixinEnvironment
implements ITokenProvider {
    private static MixinEnvironment currentEnvironment;
    private static Phase currentPhase;
    private static CompatibilityLevel compatibility;
    private static boolean showHeader;
    private static final ILogger logger;
    private static IMixinTransformer transformer;
    private final IMixinService service;
    private final Phase phase;
    private final GlobalProperties.Keys configsKey;
    private final boolean[] options;
    private final Set<String> tokenProviderClasses = new HashSet<String>();
    private final List<TokenProviderWrapper> tokenProviders = new ArrayList<TokenProviderWrapper>();
    private final Map<String, Integer> internalTokens = new HashMap<String, Integer>();
    private final RemapperChain remappers = new RemapperChain();
    private Side side;
    private String obfuscationContext = null;

    MixinEnvironment(Phase phase) {
        this.service = MixinService.getService();
        this.phase = phase;
        this.configsKey = GlobalProperties.Keys.of(GlobalProperties.Keys.CONFIGS + "." + this.phase.name.toLowerCase(Locale.ROOT));
        String version = this.getVersion();
        if (version == null || !"0.8.5".equals(version)) {
            throw new MixinException("Environment conflict, mismatched versions or you didn't call MixinBootstrap.init()");
        }
        this.service.checkEnv(this);
        this.options = new boolean[Option.values().length];
        for (Option option : Option.values()) {
            this.options[option.ordinal()] = option.getBooleanValue();
        }
        if (showHeader) {
            showHeader = false;
            this.printHeader(version);
        }
    }

    private void printHeader(Object version) {
        String codeSource = this.getCodeSource();
        String serviceName = this.service.getName();
        Side side = this.getSide();
        logger.info("SpongePowered MIXIN Subsystem Version={} Source={} Service={} Env={}", new Object[]{version, codeSource, serviceName, side});
        boolean verbose = this.getOption(Option.DEBUG_VERBOSE);
        if (verbose || this.getOption(Option.DEBUG_EXPORT) || this.getOption(Option.DEBUG_PROFILER)) {
            PrettyPrinter printer = new PrettyPrinter(32);
            printer.add("SpongePowered MIXIN%s", verbose ? " (Verbose debugging enabled)" : "").centre().hr();
            printer.kv("Code source", codeSource);
            printer.kv("Internal Version", version);
            printer.kv("Java Version", "%s (supports compatibility %s)", JavaVersion.current(), CompatibilityLevel.getSupportedVersions());
            printer.kv("Default Compatibility Level", (Object)MixinEnvironment.getCompatibilityLevel());
            printer.kv("Detected ASM Version", ASM.getVersionString());
            printer.kv("Detected ASM Supports Java", ASM.getClassVersionString()).hr();
            printer.kv("Service Name", serviceName);
            printer.kv("Mixin Service Class", this.service.getClass().getName());
            printer.kv("Global Property Service Class", MixinService.getGlobalPropertyService().getClass().getName());
            printer.kv("Logger Adapter Type", MixinService.getService().getLogger("mixin").getType()).hr();
            for (Option option : Option.values()) {
                StringBuilder indent = new StringBuilder();
                for (int i2 = 0; i2 < option.depth; ++i2) {
                    indent.append("- ");
                }
                printer.kv(option.property, "%s<%s>", new Object[]{indent, option});
            }
            printer.hr().kv("Detected Side", (Object)side);
            printer.print(System.err);
        }
    }

    private String getCodeSource() {
        try {
            return this.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        }
        catch (Throwable th2) {
            return "Unknown";
        }
    }

    private Level getVerboseLoggingLevel() {
        return this.getOption(Option.DEBUG_VERBOSE) ? Level.INFO : Level.DEBUG;
    }

    public Phase getPhase() {
        return this.phase;
    }

    @Deprecated
    public List<String> getMixinConfigs() {
        ArrayList mixinConfigs = (ArrayList)GlobalProperties.get(this.configsKey);
        if (mixinConfigs == null) {
            mixinConfigs = new ArrayList();
            GlobalProperties.put(this.configsKey, mixinConfigs);
        }
        return mixinConfigs;
    }

    @Deprecated
    public MixinEnvironment addConfiguration(String config) {
        logger.warn("MixinEnvironment::addConfiguration is deprecated and will be removed. Use Mixins::addConfiguration instead!", new Object[0]);
        Mixins.addConfiguration(config, this);
        return this;
    }

    void registerConfig(String config) {
        List<String> configs = this.getMixinConfigs();
        if (!configs.contains(config)) {
            configs.add(config);
        }
    }

    public MixinEnvironment registerTokenProviderClass(String providerName) {
        if (!this.tokenProviderClasses.contains(providerName)) {
            try {
                Class<?> providerClass = this.service.getClassProvider().findClass(providerName, true);
                IEnvironmentTokenProvider provider = (IEnvironmentTokenProvider)providerClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                this.registerTokenProvider(provider);
            }
            catch (Throwable th2) {
                logger.error("Error instantiating " + providerName, th2);
            }
        }
        return this;
    }

    public MixinEnvironment registerTokenProvider(IEnvironmentTokenProvider provider) {
        if (provider != null && !this.tokenProviderClasses.contains(provider.getClass().getName())) {
            String providerName = provider.getClass().getName();
            TokenProviderWrapper wrapper = new TokenProviderWrapper(provider, this);
            logger.log(this.getVerboseLoggingLevel(), "Adding new token provider {} to {}", providerName, this);
            this.tokenProviders.add(wrapper);
            this.tokenProviderClasses.add(providerName);
            Collections.sort(this.tokenProviders);
        }
        return this;
    }

    @Override
    public Integer getToken(String token) {
        token = token.toUpperCase(Locale.ROOT);
        for (TokenProviderWrapper provider : this.tokenProviders) {
            Integer value = provider.getToken(token);
            if (value == null) continue;
            return value;
        }
        return this.internalTokens.get(token);
    }

    @Deprecated
    public Set<String> getErrorHandlerClasses() {
        return Mixins.getErrorHandlerClasses();
    }

    public Object getActiveTransformer() {
        return transformer;
    }

    public void setActiveTransformer(IMixinTransformer transformer) {
        if (transformer != null) {
            MixinEnvironment.transformer = transformer;
        }
    }

    public MixinEnvironment setSide(Side side) {
        if (side != null && this.getSide() == Side.UNKNOWN && side != Side.UNKNOWN) {
            this.side = side;
        }
        return this;
    }

    public Side getSide() {
        if (this.side == null) {
            for (Side side : Side.values()) {
                if (!side.detect()) continue;
                this.side = side;
                break;
            }
        }
        return this.side != null ? this.side : Side.UNKNOWN;
    }

    public String getVersion() {
        return (String)GlobalProperties.get(GlobalProperties.Keys.INIT);
    }

    public boolean getOption(Option option) {
        return this.options[option.ordinal()];
    }

    public void setOption(Option option, boolean value) {
        this.options[option.ordinal()] = value;
    }

    public String getOptionValue(Option option) {
        return option.getStringValue();
    }

    public <E extends Enum<E>> E getOption(Option option, E defaultValue) {
        return option.getEnumValue(defaultValue);
    }

    public void setObfuscationContext(String context) {
        this.obfuscationContext = context;
    }

    public String getObfuscationContext() {
        return this.obfuscationContext;
    }

    public String getRefmapObfuscationContext() {
        String overrideObfuscationType = Option.OBFUSCATION_TYPE.getStringValue();
        if (overrideObfuscationType != null) {
            return overrideObfuscationType;
        }
        return this.obfuscationContext;
    }

    public RemapperChain getRemappers() {
        return this.remappers;
    }

    public void audit() {
        Object activeTransformer = this.getActiveTransformer();
        if (activeTransformer instanceof IMixinTransformer) {
            ((IMixinTransformer)activeTransformer).audit(this);
        }
    }

    @Deprecated
    public List<ITransformer> getTransformers() {
        logger.warn("MixinEnvironment::getTransformers is deprecated!", new Object[0]);
        ITransformerProvider transformers = this.service.getTransformerProvider();
        return transformers != null ? (List)transformers.getTransformers() : Collections.emptyList();
    }

    @Deprecated
    public void addTransformerExclusion(String name) {
        logger.warn("MixinEnvironment::addTransformerExclusion is deprecated!", new Object[0]);
        ITransformerProvider transformers = this.service.getTransformerProvider();
        if (transformers != null) {
            transformers.addTransformerExclusion(name);
        }
    }

    public String toString() {
        return String.format("%s[%s]", this.getClass().getSimpleName(), this.phase);
    }

    private static Phase getCurrentPhase() {
        if (currentPhase == Phase.NOT_INITIALISED) {
            MixinEnvironment.init(Phase.PREINIT);
        }
        return currentPhase;
    }

    public static void init(Phase phase) {
        if (currentPhase == Phase.NOT_INITIALISED) {
            currentPhase = phase;
            MixinEnvironment env = MixinEnvironment.getEnvironment(phase);
            Profiler.setActive(env.getOption(Option.DEBUG_PROFILER));
            IMixinService service = MixinService.getService();
            if (service instanceof MixinServiceAbstract) {
                ((MixinServiceAbstract)service).wire(phase, new PhaseConsumer());
            }
        }
    }

    public static MixinEnvironment getEnvironment(Phase phase) {
        if (phase == null) {
            return Phase.DEFAULT.getEnvironment();
        }
        return phase.getEnvironment();
    }

    public static MixinEnvironment getDefaultEnvironment() {
        return MixinEnvironment.getEnvironment(Phase.DEFAULT);
    }

    public static MixinEnvironment getCurrentEnvironment() {
        if (currentEnvironment == null) {
            currentEnvironment = MixinEnvironment.getEnvironment(MixinEnvironment.getCurrentPhase());
        }
        return currentEnvironment;
    }

    public static CompatibilityLevel getCompatibilityLevel() {
        if (compatibility == null) {
            CompatibilityLevel minLevel = MixinEnvironment.getMinCompatibilityLevel();
            CompatibilityLevel optionLevel = Option.DEFAULT_COMPATIBILITY_LEVEL.getEnumValue(minLevel);
            compatibility = optionLevel.isAtLeast(minLevel) ? optionLevel : minLevel;
        }
        return compatibility;
    }

    public static CompatibilityLevel getMinCompatibilityLevel() {
        CompatibilityLevel minLevel = MixinService.getService().getMinCompatibilityLevel();
        return minLevel == null ? CompatibilityLevel.DEFAULT : minLevel;
    }

    @Deprecated
    public static void setCompatibilityLevel(CompatibilityLevel level) throws IllegalArgumentException {
        CompatibilityLevel currentLevel;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (!"org.spongepowered.asm.mixin.transformer.MixinConfig".equals(stackTrace[2].getClassName())) {
            logger.warn("MixinEnvironment::setCompatibilityLevel is deprecated and will be removed. Set level via config instead!", new Object[0]);
        }
        if (level != (currentLevel = MixinEnvironment.getCompatibilityLevel()) && level.isAtLeast(currentLevel)) {
            if (!level.isSupported()) {
                throw new IllegalArgumentException(String.format("The requested compatibility level %s could not be set. Level is not supported by the active JRE or ASM version (Java %s, %s)", new Object[]{level, JavaVersion.current(), ASM.getVersionString()}));
            }
            IMixinService service = MixinService.getService();
            CompatibilityLevel maxLevel = service.getMaxCompatibilityLevel();
            if (maxLevel != null && maxLevel.isLessThan(level)) {
                logger.warn("The requested compatibility level {} is higher than the level supported by the active subsystem '{}' which supports {}. This is not a supported configuration and instability may occur.", new Object[]{level, service.getName(), maxLevel});
            }
            compatibility = level;
            logger.info("Compatibility level set to {}", new Object[]{level});
        }
    }

    @Deprecated
    public static Profiler getProfiler() {
        return Profiler.getProfiler("mixin");
    }

    static void gotoPhase(Phase phase) {
        if (phase == null || phase.ordinal < 0) {
            throw new IllegalArgumentException("Cannot go to the specified phase, phase is null or invalid");
        }
        IMixinService service = MixinService.getService();
        if (phase.ordinal > MixinEnvironment.getCurrentPhase().ordinal) {
            service.beginPhase();
        }
        currentPhase = phase;
        currentEnvironment = MixinEnvironment.getEnvironment(MixinEnvironment.getCurrentPhase());
        if (service instanceof MixinServiceAbstract && phase == Phase.DEFAULT) {
            ((MixinServiceAbstract)service).unwire();
        }
    }

    static {
        currentPhase = Phase.NOT_INITIALISED;
        showHeader = true;
        logger = MixinService.getService().getLogger("mixin");
    }

    static class PhaseConsumer
    implements IConsumer<Phase> {
        PhaseConsumer() {
        }

        @Override
        public void accept(Phase phase) {
            MixinEnvironment.gotoPhase(phase);
        }
    }

    static class TokenProviderWrapper
    implements Comparable<TokenProviderWrapper> {
        private static int nextOrder = 0;
        private final int priority;
        private final int order;
        private final IEnvironmentTokenProvider provider;
        private final MixinEnvironment environment;

        public TokenProviderWrapper(IEnvironmentTokenProvider provider, MixinEnvironment environment) {
            this.provider = provider;
            this.environment = environment;
            this.order = nextOrder++;
            this.priority = provider.getPriority();
        }

        @Override
        public int compareTo(TokenProviderWrapper other) {
            if (other == null) {
                return 0;
            }
            if (other.priority == this.priority) {
                return other.order - this.order;
            }
            return other.priority - this.priority;
        }

        public IEnvironmentTokenProvider getProvider() {
            return this.provider;
        }

        Integer getToken(String token) {
            return this.provider.getToken(token, this.environment);
        }
    }

    public static enum CompatibilityLevel {
        JAVA_6(6, 50, 0),
        JAVA_7(7, 51, 0){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 1.7;
            }
        }
        ,
        JAVA_8(8, 52, 3){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 1.8;
            }
        }
        ,
        JAVA_9(9, 53, 7){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 9.0 && ASM.isAtLeastVersion(6);
            }
        }
        ,
        JAVA_10(10, 54, 7){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 10.0 && ASM.isAtLeastVersion(6, 1);
            }
        }
        ,
        JAVA_11(11, 55, 31){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 11.0 && ASM.isAtLeastVersion(7);
            }
        }
        ,
        JAVA_12(12, 56, 31){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 12.0 && ASM.isAtLeastVersion(7);
            }
        }
        ,
        JAVA_13(13, 57, 31){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 13.0 && ASM.isAtLeastVersion(7);
            }
        }
        ,
        JAVA_14(14, 58, 63){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 14.0 && ASM.isAtLeastVersion(8);
            }
        }
        ,
        JAVA_15(15, 59, 127){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 15.0 && ASM.isAtLeastVersion(9);
            }
        }
        ,
        JAVA_16(16, 60, 127){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 16.0 && ASM.isAtLeastVersion(9);
            }
        }
        ,
        JAVA_17(17, 61, 127){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 17.0 && ASM.isAtLeastVersion(9, 1);
            }
        }
        ,
        JAVA_18(18, 62, 127){

            @Override
            boolean isSupported() {
                return JavaVersion.current() >= 18.0 && ASM.isAtLeastVersion(9, 2);
            }
        };

        public static CompatibilityLevel DEFAULT;
        public static CompatibilityLevel MAX_SUPPORTED;
        private final int ver;
        private final int classVersion;
        private final int languageFeatures;
        private CompatibilityLevel maxCompatibleLevel;

        private CompatibilityLevel(int ver, int classVersion, int languageFeatures) {
            this.ver = ver;
            this.classVersion = classVersion;
            this.languageFeatures = languageFeatures;
        }

        boolean isSupported() {
            return true;
        }

        @Deprecated
        public int classVersion() {
            return this.classVersion;
        }

        public int getClassVersion() {
            return this.classVersion;
        }

        public int getClassMajorVersion() {
            return this.classVersion & 0xFFFF;
        }

        public int getLanguageFeatures() {
            return this.languageFeatures;
        }

        @Deprecated
        public boolean supportsMethodsInInterfaces() {
            return (this.languageFeatures & 1) != 0;
        }

        public boolean supports(int languageFeatures) {
            return (this.languageFeatures & languageFeatures) == languageFeatures;
        }

        public boolean isAtLeast(CompatibilityLevel level) {
            return level == null || this.ver >= level.ver;
        }

        public boolean isLessThan(CompatibilityLevel level) {
            return level == null || this.ver < level.ver;
        }

        public boolean canElevateTo(CompatibilityLevel level) {
            if (level == null || this.maxCompatibleLevel == null) {
                return true;
            }
            return level.ver <= this.maxCompatibleLevel.ver;
        }

        public boolean canSupport(CompatibilityLevel level) {
            if (level == null) {
                return true;
            }
            return level.canElevateTo(this);
        }

        public static CompatibilityLevel requiredFor(int languageFeatures) {
            for (CompatibilityLevel level : CompatibilityLevel.values()) {
                if (!level.supports(languageFeatures)) continue;
                return level;
            }
            return null;
        }

        static String getSupportedVersions() {
            StringBuilder sb2 = new StringBuilder();
            boolean comma = false;
            int rangeStart = 0;
            int rangeEnd = 0;
            for (CompatibilityLevel level : CompatibilityLevel.values()) {
                if (!level.isSupported()) continue;
                if (level.ver == rangeEnd + 1) {
                    rangeEnd = level.ver;
                    continue;
                }
                if (rangeStart > 0) {
                    sb2.append(comma ? "," : "").append(rangeStart);
                    if (rangeEnd > rangeStart) {
                        sb2.append(rangeEnd > rangeStart + 1 ? (char)'-' : ',').append(rangeEnd);
                    }
                    comma = true;
                    rangeStart = rangeEnd = level.ver;
                }
                rangeStart = rangeEnd = level.ver;
            }
            if (rangeStart > 0) {
                sb2.append(comma ? "," : "").append(rangeStart);
                if (rangeEnd > rangeStart) {
                    sb2.append(rangeEnd > rangeStart + 1 ? (char)'-' : ',').append(rangeEnd);
                }
            }
            return sb2.toString();
        }

        static {
            DEFAULT = JAVA_6;
            MAX_SUPPORTED = JAVA_13;
        }
    }

    public static enum Option {
        DEBUG_ALL("debug"),
        DEBUG_EXPORT(DEBUG_ALL, "export"),
        DEBUG_EXPORT_FILTER(DEBUG_EXPORT, "filter", false),
        DEBUG_EXPORT_DECOMPILE(DEBUG_EXPORT, Inherit.ALLOW_OVERRIDE, "decompile"),
        DEBUG_EXPORT_DECOMPILE_THREADED(DEBUG_EXPORT_DECOMPILE, Inherit.ALLOW_OVERRIDE, "async"),
        DEBUG_EXPORT_DECOMPILE_MERGESIGNATURES(DEBUG_EXPORT_DECOMPILE, Inherit.ALLOW_OVERRIDE, "mergeGenericSignatures"),
        DEBUG_VERIFY(DEBUG_ALL, "verify"),
        DEBUG_VERBOSE(DEBUG_ALL, "verbose"),
        DEBUG_INJECTORS(DEBUG_ALL, "countInjections"),
        DEBUG_STRICT(DEBUG_ALL, Inherit.INDEPENDENT, "strict"),
        DEBUG_UNIQUE(DEBUG_STRICT, "unique"),
        DEBUG_TARGETS(DEBUG_STRICT, "targets"),
        DEBUG_PROFILER(DEBUG_ALL, Inherit.ALLOW_OVERRIDE, "profiler"),
        DUMP_TARGET_ON_FAILURE("dumpTargetOnFailure"),
        CHECK_ALL("checks"),
        CHECK_IMPLEMENTS(CHECK_ALL, "interfaces"),
        CHECK_IMPLEMENTS_STRICT(CHECK_IMPLEMENTS, Inherit.ALLOW_OVERRIDE, "strict"),
        IGNORE_CONSTRAINTS("ignoreConstraints"),
        HOT_SWAP("hotSwap"),
        ENVIRONMENT(Inherit.ALWAYS_FALSE, "env"),
        OBFUSCATION_TYPE(ENVIRONMENT, Inherit.ALWAYS_FALSE, "obf"),
        DISABLE_REFMAP(ENVIRONMENT, Inherit.INDEPENDENT, "disableRefMap"),
        REFMAP_REMAP(ENVIRONMENT, Inherit.INDEPENDENT, "remapRefMap"),
        REFMAP_REMAP_RESOURCE(ENVIRONMENT, Inherit.INDEPENDENT, "refMapRemappingFile", ""),
        REFMAP_REMAP_SOURCE_ENV(ENVIRONMENT, Inherit.INDEPENDENT, "refMapRemappingEnv", "searge"),
        REFMAP_REMAP_ALLOW_PERMISSIVE(ENVIRONMENT, Inherit.INDEPENDENT, "allowPermissiveMatch", true, "true"),
        IGNORE_REQUIRED(ENVIRONMENT, Inherit.INDEPENDENT, "ignoreRequired"),
        DEFAULT_COMPATIBILITY_LEVEL(ENVIRONMENT, Inherit.INDEPENDENT, "compatLevel"),
        SHIFT_BY_VIOLATION_BEHAVIOUR(ENVIRONMENT, Inherit.INDEPENDENT, "shiftByViolation", "warn"),
        INITIALISER_INJECTION_MODE("initialiserInjectionMode", "default");

        private static final String PREFIX = "mixin";
        final Option parent;
        final Inherit inheritance;
        final String property;
        final String defaultValue;
        final boolean isFlag;
        final int depth;

        private Option(String property) {
            this(null, property, true);
        }

        private Option(Inherit inheritance, String property) {
            this(null, inheritance, property, true);
        }

        private Option(String property, boolean flag) {
            this(null, property, flag);
        }

        private Option(String property, String defaultStringValue) {
            this(null, Inherit.INDEPENDENT, property, false, defaultStringValue);
        }

        private Option(Option parent, String property) {
            this(parent, Inherit.INHERIT, property, true);
        }

        private Option(Option parent, Inherit inheritance, String property) {
            this(parent, inheritance, property, true);
        }

        private Option(Option parent, String property, boolean isFlag) {
            this(parent, Inherit.INHERIT, property, isFlag, null);
        }

        private Option(Option parent, Inherit inheritance, String property, boolean isFlag) {
            this(parent, inheritance, property, isFlag, null);
        }

        private Option(Option parent, String property, String defaultStringValue) {
            this(parent, Inherit.INHERIT, property, false, defaultStringValue);
        }

        private Option(Option parent, Inherit inheritance, String property, String defaultStringValue) {
            this(parent, inheritance, property, false, defaultStringValue);
        }

        private Option(Option parent, Inherit inheritance, String property, boolean isFlag, String defaultStringValue) {
            this.parent = parent;
            this.inheritance = inheritance;
            this.property = (parent != null ? parent.property : PREFIX) + "." + property;
            this.defaultValue = defaultStringValue;
            this.isFlag = isFlag;
            int depth = 0;
            while (parent != null) {
                parent = parent.parent;
                ++depth;
            }
            this.depth = depth;
        }

        Option getParent() {
            return this.parent;
        }

        String getProperty() {
            return this.property;
        }

        public String toString() {
            return this.isFlag ? String.valueOf(this.getBooleanValue()) : this.getStringValue();
        }

        private boolean getLocalBooleanValue(boolean defaultValue) {
            return Boolean.parseBoolean(System.getProperty(this.property, Boolean.toString(defaultValue)));
        }

        private boolean getInheritedBooleanValue() {
            return this.parent != null && this.parent.getBooleanValue();
        }

        final boolean getBooleanValue() {
            if (this.inheritance == Inherit.ALWAYS_FALSE) {
                return false;
            }
            boolean local = this.getLocalBooleanValue(false);
            if (this.inheritance == Inherit.INDEPENDENT) {
                return local;
            }
            boolean inherited = local || this.getInheritedBooleanValue();
            return this.inheritance == Inherit.INHERIT ? inherited : this.getLocalBooleanValue(inherited);
        }

        final String getStringValue() {
            return this.inheritance == Inherit.INDEPENDENT || this.parent == null || this.parent.getBooleanValue() ? System.getProperty(this.property, this.defaultValue) : this.defaultValue;
        }

        <E extends Enum<E>> E getEnumValue(E defaultValue) {
            String value = System.getProperty(this.property, defaultValue.name());
            try {
                return (E)Enum.valueOf(defaultValue.getClass(), value.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException ex2) {
                return defaultValue;
            }
        }

        private static enum Inherit {
            INHERIT,
            ALLOW_OVERRIDE,
            INDEPENDENT,
            ALWAYS_FALSE;

        }
    }

    public static enum Side {
        UNKNOWN{

            @Override
            protected boolean detect() {
                return false;
            }
        }
        ,
        CLIENT{

            @Override
            protected boolean detect() {
                String sideName = MixinService.getService().getSideName();
                return "CLIENT".equals(sideName);
            }
        }
        ,
        SERVER{

            @Override
            protected boolean detect() {
                String sideName = MixinService.getService().getSideName();
                return "SERVER".equals(sideName) || "DEDICATEDSERVER".equals(sideName);
            }
        };


        protected abstract boolean detect();
    }

    public static final class Phase {
        static final Phase NOT_INITIALISED = new Phase(-1, "NOT_INITIALISED");
        public static final Phase PREINIT = new Phase(0, "PREINIT");
        public static final Phase INIT = new Phase(1, "INIT");
        public static final Phase DEFAULT = new Phase(2, "DEFAULT");
        static final List<Phase> phases = ImmutableList.of(PREINIT, INIT, DEFAULT);
        final int ordinal;
        final String name;
        private MixinEnvironment environment;

        private Phase(int ordinal, String name) {
            this.ordinal = ordinal;
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public static Phase forName(String name) {
            for (Phase phase : phases) {
                if (!phase.name.equals(name)) continue;
                return phase;
            }
            return null;
        }

        MixinEnvironment getEnvironment() {
            if (this.ordinal < 0) {
                throw new IllegalArgumentException("Cannot access the NOT_INITIALISED environment");
            }
            if (this.environment == null) {
                this.environment = new MixinEnvironment(this);
            }
            return this.environment;
        }
    }
}

