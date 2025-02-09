/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.launchwrapper;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LogWrapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class LaunchClassLoader
extends URLClassLoader {
    public static final int BUFFER_SIZE = 4096;
    private List<URL> sources;
    private ClassLoader parent = this.getClass().getClassLoader();
    private List<IClassTransformer> transformers = new ArrayList<IClassTransformer>(2);
    private Map<String, Class<?>> cachedClasses = new ConcurrentHashMap();
    private Set<String> invalidClasses = new HashSet<String>(1000);
    private Set<String> classLoaderExceptions = new HashSet<String>();
    private Set<String> transformerExceptions = new HashSet<String>();
    private Map<Package, Manifest> packageManifests = new ConcurrentHashMap<Package, Manifest>();
    private Map<String, byte[]> resourceCache = new ConcurrentHashMap<String, byte[]>(1000);
    private Set<String> negativeResourceCache = Collections.newSetFromMap(new ConcurrentHashMap());
    private IClassNameTransformer renameTransformer;
    private static final Manifest EMPTY = new Manifest();
    private final ThreadLocal<byte[]> loadBuffer = new ThreadLocal();
    private static final String[] RESERVED_NAMES = new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("legacy.debugClassLoading", "false"));
    private static final boolean DEBUG_FINER = DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingFiner", "false"));
    private static final boolean DEBUG_SAVE = DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingSave", "false"));
    private static File tempFolder = null;

    public LaunchClassLoader(URL[] sources) {
        super(sources, (ClassLoader)null);
        this.sources = new ArrayList<URL>(Arrays.asList(sources));
        this.addClassLoaderExclusion("java.");
        this.addClassLoaderExclusion("sun.");
        this.addClassLoaderExclusion("org.lwjgl.");
        this.addClassLoaderExclusion("org.apache.logging.");
        this.addClassLoaderExclusion("net.minecraft.launchwrapper.");
        this.addTransformerExclusion("javax.");
        this.addTransformerExclusion("argo.");
        this.addTransformerExclusion("org.objectweb.asm.");
        this.addTransformerExclusion("com.google.common.");
        this.addTransformerExclusion("org.bouncycastle.");
        this.addTransformerExclusion("net.minecraft.launchwrapper.injector.");
        if (DEBUG_SAVE) {
            int x2 = 1;
            tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP");
            while (tempFolder.exists() && x2 <= 10) {
                tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP" + x2++);
            }
            if (tempFolder.exists()) {
                LogWrapper.info("DEBUG_SAVE enabled, but 10 temp directories already exist, clean them and try again.", new Object[0]);
                tempFolder = null;
            } else {
                LogWrapper.info("DEBUG_SAVE Enabled, saving all classes to \"%s\"", tempFolder.getAbsolutePath().replace('\\', '/'));
                tempFolder.mkdirs();
            }
        }
    }

    public void registerTransformer(String transformerClassName) {
        try {
            IClassTransformer transformer = (IClassTransformer)this.loadClass(transformerClassName).newInstance();
            this.transformers.add(transformer);
            if (transformer instanceof IClassNameTransformer && this.renameTransformer == null) {
                this.renameTransformer = (IClassNameTransformer)((Object)transformer);
            }
        }
        catch (Exception e2) {
            LogWrapper.log(Level.ERROR, e2, "A critical problem occurred registering the ASM transformer class %s", transformerClassName);
        }
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (this.invalidClasses.contains(name)) {
            throw new ClassNotFoundException(name);
        }
        for (String exception : this.classLoaderExceptions) {
            if (!name.startsWith(exception)) continue;
            return this.parent.loadClass(name);
        }
        if (this.cachedClasses.containsKey(name)) {
            return this.cachedClasses.get(name);
        }
        for (String exception : this.transformerExceptions) {
            if (!name.startsWith(exception)) continue;
            try {
                Class<?> clazz = super.findClass(name);
                this.cachedClasses.put(name, clazz);
                return clazz;
            }
            catch (ClassNotFoundException e2) {
                this.invalidClasses.add(name);
                throw e2;
            }
        }
        try {
            String transformedName = this.transformName(name);
            if (this.cachedClasses.containsKey(transformedName)) {
                return this.cachedClasses.get(transformedName);
            }
            String untransformedName = this.untransformName(name);
            int lastDot = untransformedName.lastIndexOf(46);
            String packageName = lastDot == -1 ? "" : untransformedName.substring(0, lastDot);
            String fileName = untransformedName.replace('.', '/').concat(".class");
            URLConnection urlConnection = this.findCodeSourceConnectionFor(fileName);
            CodeSigner[] signers = null;
            if (lastDot > -1 && !untransformedName.startsWith("net.minecraft.")) {
                if (urlConnection instanceof JarURLConnection) {
                    JarURLConnection jarURLConnection = (JarURLConnection)urlConnection;
                    JarFile jarFile = jarURLConnection.getJarFile();
                    if (jarFile != null && jarFile.getManifest() != null) {
                        Manifest manifest = jarFile.getManifest();
                        JarEntry entry = jarFile.getJarEntry(fileName);
                        Package pkg = this.getPackage(packageName);
                        this.getClassBytes(untransformedName);
                        signers = entry.getCodeSigners();
                        if (pkg == null) {
                            pkg = this.definePackage(packageName, manifest, jarURLConnection.getJarFileURL());
                            this.packageManifests.put(pkg, manifest);
                        } else if (pkg.isSealed() && !pkg.isSealed(jarURLConnection.getJarFileURL())) {
                            LogWrapper.severe("The jar file %s is trying to seal already secured path %s", jarFile.getName(), packageName);
                        } else if (this.isSealed(packageName, manifest)) {
                            LogWrapper.severe("The jar file %s has a security seal for path %s, but that path is defined and not secure", jarFile.getName(), packageName);
                        }
                    }
                } else {
                    Package pkg = this.getPackage(packageName);
                    if (pkg == null) {
                        pkg = this.definePackage(packageName, null, null, null, null, null, null, null);
                        this.packageManifests.put(pkg, EMPTY);
                    } else if (pkg.isSealed()) {
                        LogWrapper.severe("The URL %s is defining elements for sealed path %s", urlConnection.getURL(), packageName);
                    }
                }
            }
            byte[] transformedClass = this.runTransformers(untransformedName, transformedName, this.getClassBytes(untransformedName));
            if (DEBUG_SAVE) {
                this.saveTransformedClass(transformedClass, transformedName);
            }
            CodeSource codeSource = urlConnection == null ? null : new CodeSource(urlConnection.getURL(), signers);
            Class<?> clazz = this.defineClass(transformedName, transformedClass, 0, transformedClass.length, codeSource);
            this.cachedClasses.put(transformedName, clazz);
            return clazz;
        }
        catch (Throwable e3) {
            this.invalidClasses.add(name);
            if (DEBUG) {
                LogWrapper.log(Level.TRACE, e3, "Exception encountered attempting classloading of %s", name);
                LogManager.getLogger("LaunchWrapper").log(Level.ERROR, "Exception encountered attempting classloading of %s", e3);
            }
            throw new ClassNotFoundException(name, e3);
        }
    }

    private void saveTransformedClass(byte[] data, String transformedName) {
        if (tempFolder == null) {
            return;
        }
        File outFile = new File(tempFolder, String.valueOf(transformedName.replace('.', File.separatorChar)) + ".class");
        File outDir = outFile.getParentFile();
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        if (outFile.exists()) {
            outFile.delete();
        }
        try {
            LogWrapper.fine("Saving transformed class \"%s\" to \"%s\"", transformedName, outFile.getAbsolutePath().replace('\\', '/'));
            FileOutputStream output = new FileOutputStream(outFile);
            ((OutputStream)output).write(data);
            ((OutputStream)output).close();
        }
        catch (IOException ex2) {
            LogWrapper.log(Level.WARN, ex2, "Could not save transformed class \"%s\"", transformedName);
        }
    }

    private String untransformName(String name) {
        if (this.renameTransformer != null) {
            return this.renameTransformer.unmapClassName(name);
        }
        return name;
    }

    private String transformName(String name) {
        if (this.renameTransformer != null) {
            return this.renameTransformer.remapClassName(name);
        }
        return name;
    }

    private boolean isSealed(String path, Manifest manifest) {
        Attributes attributes = manifest.getAttributes(path);
        String sealed = null;
        if (attributes != null) {
            sealed = attributes.getValue(Attributes.Name.SEALED);
        }
        if (sealed == null && (attributes = manifest.getMainAttributes()) != null) {
            sealed = attributes.getValue(Attributes.Name.SEALED);
        }
        return "true".equalsIgnoreCase(sealed);
    }

    private URLConnection findCodeSourceConnectionFor(String name) {
        URL resource = this.findResource(name);
        if (resource != null) {
            try {
                return resource.openConnection();
            }
            catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
        return null;
    }

    private byte[] runTransformers(String name, String transformedName, byte[] basicClass) {
        if (DEBUG_FINER) {
            LogWrapper.finest("Beginning transform of {%s (%s)} Start Length: %d", name, transformedName, basicClass == null ? 0 : basicClass.length);
            for (IClassTransformer transformer : this.transformers) {
                String transName = transformer.getClass().getName();
                LogWrapper.finest("Before Transformer {%s (%s)} %s: %d", name, transformedName, transName, basicClass == null ? 0 : basicClass.length);
                basicClass = transformer.transform(name, transformedName, basicClass);
                LogWrapper.finest("After  Transformer {%s (%s)} %s: %d", name, transformedName, transName, basicClass == null ? 0 : basicClass.length);
            }
            LogWrapper.finest("Ending transform of {%s (%s)} Start Length: %d", name, transformedName, basicClass == null ? 0 : basicClass.length);
        } else {
            for (IClassTransformer transformer : this.transformers) {
                basicClass = transformer.transform(name, transformedName, basicClass);
            }
        }
        return basicClass;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
        this.sources.add(url);
    }

    public List<URL> getSources() {
        return this.sources;
    }

    private byte[] readFully(InputStream stream) {
        try {
            int read;
            byte[] buffer = this.getOrCreateBuffer();
            int totalLength = 0;
            while ((read = stream.read(buffer, totalLength, buffer.length - totalLength)) != -1) {
                if ((totalLength += read) < buffer.length - 1) continue;
                byte[] newBuffer = new byte[buffer.length + 4096];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = newBuffer;
            }
            byte[] result = new byte[totalLength];
            System.arraycopy(buffer, 0, result, 0, totalLength);
            return result;
        }
        catch (Throwable t2) {
            LogWrapper.log(Level.WARN, t2, "Problem loading class", new Object[0]);
            return new byte[0];
        }
    }

    private byte[] getOrCreateBuffer() {
        byte[] buffer = this.loadBuffer.get();
        if (buffer == null) {
            this.loadBuffer.set(new byte[4096]);
            buffer = this.loadBuffer.get();
        }
        return buffer;
    }

    public List<IClassTransformer> getTransformers() {
        return Collections.unmodifiableList(this.transformers);
    }

    public void addClassLoaderExclusion(String toExclude) {
        this.classLoaderExceptions.add(toExclude);
    }

    public void addTransformerExclusion(String toExclude) {
        this.transformerExceptions.add(toExclude);
    }

    public byte[] getClassBytes(String name) throws IOException {
        URL classResource;
        InputStream classStream;
        block10: {
            if (this.negativeResourceCache.contains(name)) {
                return null;
            }
            if (this.resourceCache.containsKey(name)) {
                return this.resourceCache.get(name);
            }
            if (name.indexOf(46) == -1) {
                String[] stringArray = RESERVED_NAMES;
                int n2 = RESERVED_NAMES.length;
                int n3 = 0;
                while (n3 < n2) {
                    byte[] data;
                    String reservedName = stringArray[n3];
                    if (name.toUpperCase(Locale.ENGLISH).startsWith(reservedName) && (data = this.getClassBytes("_" + name)) != null) {
                        this.resourceCache.put(name, data);
                        return data;
                    }
                    ++n3;
                }
            }
            classStream = null;
            try {
                String resourcePath = name.replace('.', '/').concat(".class");
                classResource = this.findResource(resourcePath);
                if (classResource != null) break block10;
                if (DEBUG) {
                    LogWrapper.finest("Failed to find class resource %s", resourcePath);
                }
                this.negativeResourceCache.add(name);
            }
            catch (Throwable throwable) {
                LaunchClassLoader.closeSilently(classStream);
                throw throwable;
            }
            LaunchClassLoader.closeSilently(classStream);
            return null;
        }
        classStream = classResource.openStream();
        if (DEBUG) {
            LogWrapper.finest("Loading class %s from resource %s", name, classResource.toString());
        }
        byte[] data = this.readFully(classStream);
        this.resourceCache.put(name, data);
        byte[] byArray = data;
        LaunchClassLoader.closeSilently(classStream);
        return byArray;
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public void clearNegativeEntries(Set<String> entriesToClear) {
        this.negativeResourceCache.removeAll(entriesToClear);
    }
}

