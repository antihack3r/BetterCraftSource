/*
 * Decompiled with CFR 0.152.
 */
package javassist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPathList;
import javassist.DirClassPath;
import javassist.JarClassPath;
import javassist.JarDirClassPath;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;

final class ClassPoolTail {
    protected ClassPathList pathList = null;

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[class path: ");
        ClassPathList list = this.pathList;
        while (list != null) {
            buf.append(list.path.toString());
            buf.append(File.pathSeparatorChar);
            list = list.next;
        }
        buf.append(']');
        return buf.toString();
    }

    public synchronized ClassPath insertClassPath(ClassPath cp2) {
        this.pathList = new ClassPathList(cp2, this.pathList);
        return cp2;
    }

    public synchronized ClassPath appendClassPath(ClassPath cp2) {
        ClassPathList tail = new ClassPathList(cp2, null);
        ClassPathList list = this.pathList;
        if (list == null) {
            this.pathList = tail;
        } else {
            while (list.next != null) {
                list = list.next;
            }
            list.next = tail;
        }
        return cp2;
    }

    public synchronized void removeClassPath(ClassPath cp2) {
        ClassPathList list = this.pathList;
        if (list != null) {
            if (list.path == cp2) {
                this.pathList = list.next;
            } else {
                while (list.next != null) {
                    if (list.next.path == cp2) {
                        list.next = list.next.next;
                        continue;
                    }
                    list = list.next;
                }
            }
        }
    }

    public ClassPath appendSystemPath() {
        if (ClassFile.MAJOR_VERSION < 53) {
            return this.appendClassPath(new ClassClassPath());
        }
        ClassLoader cl2 = Thread.currentThread().getContextClassLoader();
        return this.appendClassPath(new LoaderClassPath(cl2));
    }

    public ClassPath insertClassPath(String pathname) throws NotFoundException {
        return this.insertClassPath(ClassPoolTail.makePathObject(pathname));
    }

    public ClassPath appendClassPath(String pathname) throws NotFoundException {
        return this.appendClassPath(ClassPoolTail.makePathObject(pathname));
    }

    private static ClassPath makePathObject(String pathname) throws NotFoundException {
        String lower = pathname.toLowerCase();
        if (lower.endsWith(".jar") || lower.endsWith(".zip")) {
            return new JarClassPath(pathname);
        }
        int len = pathname.length();
        if (len > 2 && pathname.charAt(len - 1) == '*' && (pathname.charAt(len - 2) == '/' || pathname.charAt(len - 2) == File.separatorChar)) {
            String dir = pathname.substring(0, len - 2);
            return new JarDirClassPath(dir);
        }
        return new DirClassPath(pathname);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writeClassfile(String classname, OutputStream out) throws NotFoundException, IOException, CannotCompileException {
        InputStream fin = this.openClassfile(classname);
        if (fin == null) {
            throw new NotFoundException(classname);
        }
        try {
            ClassPoolTail.copyStream(fin, out);
        }
        finally {
            fin.close();
        }
    }

    InputStream openClassfile(String classname) throws NotFoundException {
        ClassPathList list = this.pathList;
        InputStream ins = null;
        NotFoundException error = null;
        while (list != null) {
            block5: {
                try {
                    ins = list.path.openClassfile(classname);
                }
                catch (NotFoundException e2) {
                    if (error != null) break block5;
                    error = e2;
                }
            }
            if (ins == null) {
                list = list.next;
                continue;
            }
            return ins;
        }
        if (error != null) {
            throw error;
        }
        return null;
    }

    public URL find(String classname) {
        ClassPathList list = this.pathList;
        URL url = null;
        while (list != null) {
            url = list.path.find(classname);
            if (url == null) {
                list = list.next;
                continue;
            }
            return url;
        }
        return null;
    }

    public static byte[] readStream(InputStream fin) throws IOException {
        byte[][] bufs = new byte[8][];
        int bufsize = 4096;
        for (int i2 = 0; i2 < 8; ++i2) {
            bufs[i2] = new byte[bufsize];
            int size = 0;
            int len = 0;
            do {
                if ((len = fin.read(bufs[i2], size, bufsize - size)) >= 0) continue;
                byte[] result = new byte[bufsize - 4096 + size];
                int s2 = 0;
                for (int j2 = 0; j2 < i2; ++j2) {
                    System.arraycopy(bufs[j2], 0, result, s2, s2 + 4096);
                    s2 = s2 + s2 + 4096;
                }
                System.arraycopy(bufs[i2], 0, result, s2, size);
                return result;
            } while ((size += len) < bufsize);
            bufsize *= 2;
        }
        throw new IOException("too much data");
    }

    public static void copyStream(InputStream fin, OutputStream fout) throws IOException {
        int bufsize = 4096;
        byte[] buf = null;
        for (int i2 = 0; i2 < 64; ++i2) {
            if (i2 < 8) {
                buf = new byte[bufsize *= 2];
            }
            int size = 0;
            int len = 0;
            do {
                if ((len = fin.read(buf, size, bufsize - size)) >= 0) continue;
                fout.write(buf, 0, size);
                return;
            } while ((size += len) < bufsize);
            fout.write(buf);
        }
        throw new IOException("too much data");
    }
}

