/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.tools.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.IHotSwap;
import org.spongepowered.asm.mixin.transformer.throwables.MixinReloadException;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.transformers.MixinClassReader;
import org.spongepowered.asm.util.asm.ASM;
import org.spongepowered.tools.agent.MixinAgentClassLoader;

public class MixinAgent
implements IHotSwap {
    public static final byte[] ERROR_BYTECODE = new byte[]{1};
    static final MixinAgentClassLoader classLoader = new MixinAgentClassLoader();
    static final ILogger logger = MixinService.getService().getLogger("mixin.agent");
    static Instrumentation instrumentation = null;
    private static List<MixinAgent> agents = new ArrayList<MixinAgent>();
    final IMixinTransformer classTransformer;

    public MixinAgent(IMixinTransformer classTransformer) {
        this.classTransformer = classTransformer;
        agents.add(this);
        if (instrumentation != null) {
            this.initTransformer();
        }
    }

    private void initTransformer() {
        instrumentation.addTransformer(new Transformer(), true);
    }

    @Override
    public void registerMixinClass(String name) {
        classLoader.addMixinClass(name);
    }

    @Override
    public void registerTargetClass(String name, ClassNode classNode) {
        classLoader.addTargetClass(name, classNode);
    }

    public static void init(Instrumentation instrumentation) {
        MixinAgent.instrumentation = instrumentation;
        if (!MixinAgent.instrumentation.isRedefineClassesSupported()) {
            logger.error("The instrumentation doesn't support re-definition of classes", new Object[0]);
        }
        for (MixinAgent agent : agents) {
            agent.initTransformer();
        }
    }

    public static void premain(String arg2, Instrumentation instrumentation) {
        System.setProperty("mixin.hotSwap", "true");
        MixinAgent.init(instrumentation);
    }

    public static void agentmain(String arg2, Instrumentation instrumentation) {
        MixinAgent.init(instrumentation);
    }

    class Transformer
    implements ClassFileTransformer {
        Transformer() {
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain domain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (classBeingRedefined == null) {
                return null;
            }
            byte[] mixinBytecode = classLoader.getFakeMixinBytecode(classBeingRedefined);
            if (mixinBytecode != null) {
                ClassNode classNode = new ClassNode(ASM.API_VERSION);
                MixinClassReader cr2 = new MixinClassReader(classfileBuffer, className);
                cr2.accept(classNode, 8);
                List<String> targets = this.reloadMixin(className, classNode);
                if (targets == null || !this.reApplyMixins(targets)) {
                    return ERROR_BYTECODE;
                }
                return mixinBytecode;
            }
            try {
                logger.info("Redefining class {}", className);
                return MixinAgent.this.classTransformer.transformClassBytes(null, className, classfileBuffer);
            }
            catch (Throwable th2) {
                logger.error("Error while re-transforming class {}", className, th2);
                return ERROR_BYTECODE;
            }
        }

        private List<String> reloadMixin(String className, ClassNode classNode) {
            logger.info("Redefining mixin {}", className);
            try {
                return MixinAgent.this.classTransformer.reload(className.replace('/', '.'), classNode);
            }
            catch (MixinReloadException e2) {
                logger.error("Mixin {} cannot be reloaded, needs a restart to be applied: {} ", e2.getMixinInfo(), e2.getMessage());
            }
            catch (Throwable th2) {
                logger.error("Error while finding targets for mixin {}", className, th2);
            }
            return null;
        }

        private boolean reApplyMixins(List<String> targets) {
            IMixinService service = MixinService.getService();
            for (String target : targets) {
                String targetName = target.replace('/', '.');
                logger.debug("Re-transforming target class {}", target);
                try {
                    Class<?> targetClass = service.getClassProvider().findClass(targetName);
                    byte[] targetBytecode = classLoader.getOriginalTargetBytecode(targetName);
                    if (targetBytecode == null) {
                        logger.error("Target class {} bytecode is not registered", targetName);
                        return false;
                    }
                    targetBytecode = MixinAgent.this.classTransformer.transformClassBytes(null, targetName, targetBytecode);
                    instrumentation.redefineClasses(new ClassDefinition(targetClass, targetBytecode));
                }
                catch (Throwable th2) {
                    logger.error("Error while re-transforming target class {}", target, th2);
                    return false;
                }
            }
            return true;
        }
    }
}

