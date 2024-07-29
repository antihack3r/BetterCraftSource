/*
 * Decompiled with CFR 0.152.
 */
package javassist;

import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import javassist.ClassPath;
import javassist.NotFoundException;

public class LoaderClassPath
implements ClassPath {
    private Reference<ClassLoader> clref;

    public LoaderClassPath(ClassLoader cl2) {
        this.clref = new WeakReference<ClassLoader>(cl2);
    }

    public String toString() {
        return this.clref.get() == null ? "<null>" : this.clref.get().toString();
    }

    @Override
    public InputStream openClassfile(String classname) throws NotFoundException {
        String cname = classname.replace('.', '/') + ".class";
        ClassLoader cl2 = this.clref.get();
        if (cl2 == null) {
            return null;
        }
        InputStream is2 = cl2.getResourceAsStream(cname);
        return is2;
    }

    @Override
    public URL find(String classname) {
        String cname = classname.replace('.', '/') + ".class";
        ClassLoader cl2 = this.clref.get();
        if (cl2 == null) {
            return null;
        }
        URL url = cl2.getResource(cname);
        return url;
    }
}

