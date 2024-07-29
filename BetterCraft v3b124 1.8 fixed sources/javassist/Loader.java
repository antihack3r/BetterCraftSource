/*
 * Decompiled with CFR 0.152.
 */
package javassist;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.ClassPoolTail;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.bytecode.ClassFile;

public class Loader
extends ClassLoader {
    private HashMap<String, ClassLoader> notDefinedHere;
    private Vector<String> notDefinedPackages;
    private ClassPool source;
    private Translator translator;
    private ProtectionDomain domain;
    public boolean doDelegation = true;

    public Loader() {
        this((ClassPool)null);
    }

    public Loader(ClassPool cp2) {
        this.init(cp2);
    }

    public Loader(ClassLoader parent, ClassPool cp2) {
        super(parent);
        this.init(cp2);
    }

    private void init(ClassPool cp2) {
        this.notDefinedHere = new HashMap();
        this.notDefinedPackages = new Vector();
        this.source = cp2;
        this.translator = null;
        this.domain = null;
        this.delegateLoadingOf("javassist.Loader");
    }

    public void delegateLoadingOf(String classname) {
        if (classname.endsWith(".")) {
            this.notDefinedPackages.addElement(classname);
        } else {
            this.notDefinedHere.put(classname, this);
        }
    }

    public void setDomain(ProtectionDomain d2) {
        this.domain = d2;
    }

    public void setClassPool(ClassPool cp2) {
        this.source = cp2;
    }

    public void addTranslator(ClassPool cp2, Translator t2) throws NotFoundException, CannotCompileException {
        this.source = cp2;
        this.translator = t2;
        t2.start(cp2);
    }

    public static void main(String[] args) throws Throwable {
        Loader cl2 = new Loader();
        cl2.run(args);
    }

    public void run(String[] args) throws Throwable {
        if (args.length >= 1) {
            this.run(args[0], Arrays.copyOfRange(args, 1, args.length));
        }
    }

    public void run(String classname, String[] args) throws Throwable {
        Class<?> c2 = this.loadClass(classname);
        try {
            c2.getDeclaredMethod("main", String[].class).invoke(null, new Object[]{args});
        }
        catch (InvocationTargetException e2) {
            throw e2.getTargetException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassFormatError, ClassNotFoundException {
        String string = name = name.intern();
        synchronized (string) {
            Class<?> c2 = this.findLoadedClass(name);
            if (c2 == null) {
                c2 = this.loadClassByDelegation(name);
            }
            if (c2 == null) {
                c2 = this.findClass(name);
            }
            if (c2 == null) {
                c2 = this.delegateToParent(name);
            }
            if (resolve) {
                this.resolveClass(c2);
            }
            return c2;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String pname;
        byte[] classfile;
        block11: {
            try {
                if (this.source != null) {
                    if (this.translator != null) {
                        this.translator.onLoad(this.source, name);
                    }
                    try {
                        classfile = this.source.get(name).toBytecode();
                        break block11;
                    }
                    catch (NotFoundException e2) {
                        return null;
                    }
                }
                String jarname = "/" + name.replace('.', '/') + ".class";
                InputStream in2 = this.getClass().getResourceAsStream(jarname);
                if (in2 == null) {
                    return null;
                }
                classfile = ClassPoolTail.readStream(in2);
            }
            catch (Exception e3) {
                throw new ClassNotFoundException("caught an exception while obtaining a class file for " + name, e3);
            }
        }
        int i2 = name.lastIndexOf(46);
        if (i2 != -1 && this.isDefinedPackage(pname = name.substring(0, i2))) {
            try {
                this.definePackage(pname, null, null, null, null, null, null, null);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        if (this.domain == null) {
            return this.defineClass(name, classfile, 0, classfile.length);
        }
        return this.defineClass(name, classfile, 0, classfile.length, this.domain);
    }

    private boolean isDefinedPackage(String name) {
        if (ClassFile.MAJOR_VERSION >= 53) {
            return this.getDefinedPackage(name) == null;
        }
        return this.getPackage(name) == null;
    }

    protected Class<?> loadClassByDelegation(String name) throws ClassNotFoundException {
        Class<?> c2 = null;
        if (this.doDelegation && (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun.") || name.startsWith("com.sun.") || name.startsWith("org.w3c.") || name.startsWith("org.xml.") || this.notDelegated(name))) {
            c2 = this.delegateToParent(name);
        }
        return c2;
    }

    private boolean notDelegated(String name) {
        if (this.notDefinedHere.containsKey(name)) {
            return true;
        }
        for (String pack : this.notDefinedPackages) {
            if (!name.startsWith(pack)) continue;
            return true;
        }
        return false;
    }

    protected Class<?> delegateToParent(String classname) throws ClassNotFoundException {
        ClassLoader cl2 = this.getParent();
        if (cl2 != null) {
            return cl2.loadClass(classname);
        }
        return this.findSystemClass(classname);
    }

    public static class Simple
    extends ClassLoader {
        public Simple() {
        }

        public Simple(ClassLoader parent) {
            super(parent);
        }

        public Class<?> invokeDefineClass(CtClass cc2) throws IOException, CannotCompileException {
            byte[] code = cc2.toBytecode();
            return this.defineClass(cc2.getName(), code, 0, code.length);
        }
    }
}

