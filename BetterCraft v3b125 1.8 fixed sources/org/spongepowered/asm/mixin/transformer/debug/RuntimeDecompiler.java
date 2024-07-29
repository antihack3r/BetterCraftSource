/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.java.decompiler.main.Fernflower
 *  org.jetbrains.java.decompiler.main.extern.IBytecodeProvider
 *  org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
 *  org.jetbrains.java.decompiler.main.extern.IFernflowerLogger$Severity
 *  org.jetbrains.java.decompiler.main.extern.IResultSaver
 *  org.jetbrains.java.decompiler.util.InterpreterUtil
 */
package org.spongepowered.asm.mixin.transformer.debug;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.jar.Manifest;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.util.InterpreterUtil;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.mixin.transformer.ext.IDecompiler;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Files;

public class RuntimeDecompiler
extends IFernflowerLogger
implements IDecompiler,
IResultSaver {
    private static final Level[] SEVERITY_LEVELS = new Level[]{Level.TRACE, Level.INFO, Level.WARN, Level.ERROR};
    private final Map<String, Object> options = ImmutableMap.builder().put("din", "0").put("rbr", "0").put("dgs", "1").put("asc", "1").put("den", "1").put("hdc", "1").put("ind", "    ").build();
    private final File outputPath;
    protected final ILogger logger = MixinService.getService().getLogger("fernflower");

    public RuntimeDecompiler(File outputPath) {
        this.outputPath = outputPath;
        if (this.outputPath.exists()) {
            try {
                MoreFiles.deleteRecursively(this.outputPath.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
            }
            catch (IOException ex2) {
                this.logger.debug("Error cleaning output directory: {}", ex2.getMessage());
            }
        }
    }

    public String toString() {
        try {
            URL codeSource = Fernflower.class.getProtectionDomain().getCodeSource().getLocation();
            File file = Files.toFile(codeSource);
            return file.getName();
        }
        catch (Exception ex2) {
            return "unknown source (classpath)";
        }
    }

    @Override
    public void decompile(File file) {
        try {
            Fernflower fernflower = new Fernflower(new IBytecodeProvider(){
                private byte[] byteCode;

                public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
                    if (this.byteCode == null) {
                        this.byteCode = InterpreterUtil.getBytes((File)new File(externalPath));
                    }
                    return this.byteCode;
                }
            }, (IResultSaver)this, this.options, (IFernflowerLogger)this);
            try {
                Method mdAddSource = fernflower.getClass().getDeclaredMethod("addSource", File.class);
                mdAddSource.invoke((Object)fernflower, file);
            }
            catch (ReflectiveOperationException ex2) {
                fernflower.getStructContext().addSpace(file, true);
            }
            fernflower.decompileContext();
        }
        catch (Throwable ex3) {
            this.logger.warn("Decompilation error while processing {}", file.getName());
        }
    }

    public void saveFolder(String path) {
    }

    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        File file = new File(this.outputPath, qualifiedName + ".java");
        file.getParentFile().mkdirs();
        try {
            this.logger.info("Writing {}", file.getAbsolutePath());
            com.google.common.io.Files.write(content, file, Charsets.UTF_8);
        }
        catch (IOException ex2) {
            this.writeMessage("Cannot write source file " + file, ex2);
        }
    }

    public void startReadingClass(String className) {
        this.logger.info("Decompiling {}", className);
    }

    public void writeMessage(String message, IFernflowerLogger.Severity severity) {
        this.logger.log(SEVERITY_LEVELS[severity.ordinal()], message, new Object[0]);
    }

    public void writeMessage(String message, Throwable t2) {
        this.logger.warn("{} {}: {}", message, t2.getClass().getSimpleName(), t2.getMessage());
    }

    public void writeMessage(String message, IFernflowerLogger.Severity severity, Throwable t2) {
        this.logger.log(SEVERITY_LEVELS[severity.ordinal()], message, severity == IFernflowerLogger.Severity.ERROR ? t2 : null);
    }

    public void copyFile(String source, String path, String entryName) {
    }

    public void createArchive(String path, String archiveName, Manifest manifest) {
    }

    public void saveDirEntry(String path, String archiveName, String entryName) {
    }

    public void copyEntry(String source, String path, String archiveName, String entry) {
    }

    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
    }

    public void closeArchive(String path, String archiveName) {
    }
}

