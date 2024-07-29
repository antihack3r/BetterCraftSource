/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.NativeUnixSocket;
import org.newsclub.net.unix.SuppressFBWarnings;

@SuppressFBWarnings(value={"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
final class NativeLibraryLoader
implements Closeable {
    private static final String PROP_LIBRARY_OVERRIDE = "org.newsclub.net.unix.library.override";
    private static final String PROP_LIBRARY_TMPDIR = "org.newsclub.net.unix.library.tmpdir";
    private static final File TEMP_DIR;
    private static final String ARCHITECTURE_AND_OS;
    private static final String LIBRARY_NAME = "junixsocket-native";
    private static boolean loaded;

    NativeLibraryLoader() {
    }

    private List<LibraryCandidate> tryProviderClass(String providerClassname, String artifactName) throws IOException, ClassNotFoundException {
        Class<?> providerClass = Class.forName(providerClassname);
        String version = NativeLibraryLoader.getArtifactVersion(providerClass, artifactName);
        String libraryNameAndVersion = "junixsocket-native-" + version;
        return this.findLibraryCandidates(artifactName, libraryNameAndVersion, providerClass);
    }

    public static String getJunixsocketVersion() throws IOException {
        return NativeLibraryLoader.getArtifactVersion(AFUNIXSocket.class, "junixsocket-common");
    }

    private static String getArtifactVersion(Class<?> providerClass, String ... artifactNames) throws IOException {
        int n2 = 0;
        String[] stringArray = artifactNames;
        int n3 = stringArray.length;
        if (n2 < n3) {
            String artifactName = stringArray[n2];
            Properties p2 = new Properties();
            String resource = "/META-INF/maven/com.kohlschutter.junixsocket/" + artifactName + "/pom.properties";
            try (InputStream in2 = providerClass.getResourceAsStream(resource);){
                if (in2 == null) {
                    throw new FileNotFoundException("Could not find resource " + resource + " relative to " + providerClass);
                }
                p2.load(in2);
                String version = p2.getProperty("version");
                Objects.requireNonNull(version, "Could not read version from pom.properties");
                String string = version;
                return string;
            }
        }
        throw new IllegalStateException("No artifact names specified");
    }

    private synchronized void setLoaded(String library) {
        if (!loaded) {
            loaded = true;
            AFUNIXSocket.loadedLibrary = library;
            try {
                NativeUnixSocket.init();
            }
            catch (RuntimeException e2) {
                throw e2;
            }
            catch (Exception e3) {
                throw new IllegalStateException(e3);
            }
        }
    }

    private Throwable loadLibraryOverride() {
        String libraryOverride = System.getProperty(PROP_LIBRARY_OVERRIDE, "");
        if (!libraryOverride.isEmpty()) {
            try {
                System.load(libraryOverride);
                this.setLoaded(libraryOverride);
                return null;
            }
            catch (Exception | LinkageError e2) {
                return e2;
            }
        }
        return new Exception("No library specified with -Dorg.newsclub.net.unix.library.override=");
    }

    private static Object loadLibrarySyncMonitor() {
        ClassLoader monitor = NativeLibraryLoader.class.getClassLoader();
        if (monitor == null) {
            return NativeLibraryLoader.class;
        }
        return monitor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void loadLibrary() {
        Object object = NativeLibraryLoader.loadLibrarySyncMonitor();
        synchronized (object) {
            if (loaded) {
                return;
            }
            ArrayList<Throwable> suppressedThrowables = new ArrayList<Throwable>();
            Throwable ex2 = this.loadLibraryOverride();
            if (ex2 == null) {
                return;
            }
            suppressedThrowables.add(ex2);
            List<LibraryCandidate> candidates = this.initLibraryCandidates(suppressedThrowables);
            String loadedLibraryId = null;
            for (LibraryCandidate candidate : candidates) {
                try {
                    loadedLibraryId = candidate.load();
                    if (loadedLibraryId == null) continue;
                    break;
                }
                catch (Exception | LinkageError e2) {
                    suppressedThrowables.add(e2);
                }
            }
            for (LibraryCandidate candidate : candidates) {
                candidate.close();
            }
            if (loadedLibraryId == null) {
                throw this.initCantLoadLibraryError(suppressedThrowables);
            }
            this.setLoaded(loadedLibraryId);
        }
    }

    private UnsatisfiedLinkError initCantLoadLibraryError(List<Throwable> suppressedThrowables) {
        String message = "Could not load native library junixsocket-native for architecture " + ARCHITECTURE_AND_OS;
        String cp2 = System.getProperty("java.class.path", "");
        if (cp2.contains("junixsocket-native-custom/target-eclipse") || cp2.contains("junixsocket-native-common/target-eclipse")) {
            message = message + "\n\n*** ECLIPSE USERS ***\nIf you're running from within Eclipse, please close the projects \"junixsocket-native-common\" and \"junixsocket-native-custom\"\n";
        }
        UnsatisfiedLinkError e2 = new UnsatisfiedLinkError(message);
        for (Throwable suppressed : suppressedThrowables) {
            e2.addSuppressed(suppressed);
        }
        throw e2;
    }

    private List<LibraryCandidate> initLibraryCandidates(List<Throwable> suppressedThrowables) {
        ArrayList<LibraryCandidate> candidates = new ArrayList<LibraryCandidate>();
        try {
            candidates.add(new StandardLibraryCandidate(NativeLibraryLoader.getArtifactVersion(this.getClass(), "junixsocket-common", "junixsocket-core")));
        }
        catch (Exception e2) {
            suppressedThrowables.add(e2);
        }
        try {
            candidates.addAll(this.tryProviderClass("org.newsclub.lib.junixsocket.custom.NarMetadata", "junixsocket-native-custom"));
        }
        catch (Exception e3) {
            suppressedThrowables.add(e3);
        }
        try {
            candidates.addAll(this.tryProviderClass("org.newsclub.lib.junixsocket.common.NarMetadata", "junixsocket-native-common"));
        }
        catch (Exception e4) {
            suppressedThrowables.add(e4);
        }
        return candidates;
    }

    private static String architectureAndOS() {
        return System.getProperty("os.arch") + "-" + System.getProperty("os.name").replaceAll(" ", "");
    }

    private List<LibraryCandidate> findLibraryCandidates(String artifactName, String libraryNameAndVersion, Class<?> providerClass) {
        String mappedName = System.mapLibraryName(libraryNameAndVersion);
        ArrayList<LibraryCandidate> list = new ArrayList<LibraryCandidate>();
        for (String compiler : new String[]{"gpp", "g++", "linker", "clang", "gcc", "cc", "CC", "icpc", "icc", "xlC", "xlC_r", "msvc", "icl", "ecpc", "ecc"}) {
            String nodepsPath;
            String path = "/lib/" + ARCHITECTURE_AND_OS + "-" + compiler + "/jni/" + mappedName;
            InputStream in2 = providerClass.getResourceAsStream(path);
            if (in2 != null) {
                list.add(new ClasspathLibraryCandidate(artifactName, libraryNameAndVersion, path, in2));
            }
            if ((nodepsPath = this.nodepsPath(path)) == null || (in2 = providerClass.getResourceAsStream(nodepsPath)) == null) continue;
            list.add(new ClasspathLibraryCandidate(artifactName, libraryNameAndVersion, nodepsPath, in2));
        }
        return list;
    }

    private String nodepsPath(String path) {
        int lastDot = path.lastIndexOf(46);
        if (lastDot == -1) {
            return null;
        }
        return path.substring(0, lastDot) + ".nodeps" + path.substring(lastDot);
    }

    private static File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix, TEMP_DIR);
    }

    @Override
    public void close() {
    }

    static {
        ARCHITECTURE_AND_OS = NativeLibraryLoader.architectureAndOS();
        loaded = false;
        String dir = System.getProperty(PROP_LIBRARY_TMPDIR, null);
        TEMP_DIR = dir == null ? null : new File(dir);
    }

    private static final class ClasspathLibraryCandidate
    extends LibraryCandidate {
        private final String artifactName;
        private final InputStream libraryIn;
        private final String path;

        ClasspathLibraryCandidate(String artifactName, String libraryNameAndVersion, String path, InputStream libraryIn) {
            super(libraryNameAndVersion);
            this.artifactName = artifactName;
            this.path = path;
            this.libraryIn = libraryIn;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        synchronized String load() throws IOException, LinkageError {
            if (this.libraryNameAndVersion == null) {
                return null;
            }
            File libFile = NativeLibraryLoader.createTempFile("libtmp", System.mapLibraryName(this.libraryNameAndVersion));
            try (FileOutputStream out = new FileOutputStream(libFile);){
                int read;
                byte[] buf = new byte[4096];
                while ((read = this.libraryIn.read(buf)) >= 0) {
                    ((OutputStream)out).write(buf, 0, read);
                }
            }
            finally {
                this.libraryIn.close();
            }
            System.load(libFile.getAbsolutePath());
            if (!libFile.delete()) {
                libFile.deleteOnExit();
            }
            return this.artifactName + "/" + this.libraryNameAndVersion;
        }

        @Override
        public void close() {
            try {
                this.libraryIn.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }

        @Override
        public String toString() {
            return super.toString() + "(" + this.artifactName + ":" + this.path + ")";
        }
    }

    private static final class StandardLibraryCandidate
    extends LibraryCandidate {
        StandardLibraryCandidate(String version) {
            super(version == null ? null : "junixsocket-native-" + version);
        }

        @Override
        String load() throws Exception, LinkageError {
            if (this.libraryNameAndVersion != null) {
                System.loadLibrary(this.libraryNameAndVersion);
                return this.libraryNameAndVersion;
            }
            return null;
        }

        @Override
        public void close() {
        }

        @Override
        public String toString() {
            return super.toString() + "(standard library path)";
        }
    }

    private static abstract class LibraryCandidate
    implements Closeable {
        protected final String libraryNameAndVersion;

        protected LibraryCandidate(String libraryNameAndVersion) {
            this.libraryNameAndVersion = libraryNameAndVersion;
        }

        abstract String load() throws Exception;

        @Override
        public abstract void close();

        public String toString() {
            return super.toString() + "[" + this.libraryNameAndVersion + "]";
        }
    }
}

