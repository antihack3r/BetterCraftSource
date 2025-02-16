/*
 * Decompiled with CFR 0.152.
 */
package javassist.scopedpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import javassist.scopedpool.ScopedClassPool;
import javassist.scopedpool.ScopedClassPoolFactory;
import javassist.scopedpool.ScopedClassPoolFactoryImpl;
import javassist.scopedpool.ScopedClassPoolRepository;

public class ScopedClassPoolRepositoryImpl
implements ScopedClassPoolRepository {
    private static final ScopedClassPoolRepositoryImpl instance = new ScopedClassPoolRepositoryImpl();
    private boolean prune = true;
    boolean pruneWhenCached;
    protected Map<ClassLoader, ScopedClassPool> registeredCLs = Collections.synchronizedMap(new WeakHashMap());
    protected ClassPool classpool;
    protected ScopedClassPoolFactory factory = new ScopedClassPoolFactoryImpl();

    public static ScopedClassPoolRepository getInstance() {
        return instance;
    }

    private ScopedClassPoolRepositoryImpl() {
        this.classpool = ClassPool.getDefault();
        ClassLoader cl2 = Thread.currentThread().getContextClassLoader();
        this.classpool.insertClassPath(new LoaderClassPath(cl2));
    }

    @Override
    public boolean isPrune() {
        return this.prune;
    }

    @Override
    public void setPrune(boolean prune) {
        this.prune = prune;
    }

    @Override
    public ScopedClassPool createScopedClassPool(ClassLoader cl2, ClassPool src) {
        return this.factory.create(cl2, src, this);
    }

    @Override
    public ClassPool findClassPool(ClassLoader cl2) {
        if (cl2 == null) {
            return this.registerClassLoader(ClassLoader.getSystemClassLoader());
        }
        return this.registerClassLoader(cl2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ClassPool registerClassLoader(ClassLoader ucl) {
        Map<ClassLoader, ScopedClassPool> map = this.registeredCLs;
        synchronized (map) {
            if (this.registeredCLs.containsKey(ucl)) {
                return this.registeredCLs.get(ucl);
            }
            ScopedClassPool pool = this.createScopedClassPool(ucl, this.classpool);
            this.registeredCLs.put(ucl, pool);
            return pool;
        }
    }

    @Override
    public Map<ClassLoader, ScopedClassPool> getRegisteredCLs() {
        this.clearUnregisteredClassLoaders();
        return this.registeredCLs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearUnregisteredClassLoaders() {
        ArrayList<ClassLoader> toUnregister = null;
        Map<ClassLoader, ScopedClassPool> map = this.registeredCLs;
        synchronized (map) {
            for (Map.Entry<ClassLoader, ScopedClassPool> reg : this.registeredCLs.entrySet()) {
                if (!reg.getValue().isUnloadedClassLoader()) continue;
                ClassLoader cl2 = reg.getValue().getClassLoader();
                if (cl2 != null) {
                    if (toUnregister == null) {
                        toUnregister = new ArrayList<ClassLoader>();
                    }
                    toUnregister.add(cl2);
                }
                this.registeredCLs.remove(reg.getKey());
            }
            if (toUnregister != null) {
                for (ClassLoader cl3 : toUnregister) {
                    this.unregisterClassLoader(cl3);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterClassLoader(ClassLoader cl2) {
        Map<ClassLoader, ScopedClassPool> map = this.registeredCLs;
        synchronized (map) {
            ScopedClassPool pool = this.registeredCLs.remove(cl2);
            if (pool != null) {
                pool.close();
            }
        }
    }

    public void insertDelegate(ScopedClassPoolRepository delegate) {
    }

    @Override
    public void setClassPoolFactory(ScopedClassPoolFactory factory) {
        this.factory = factory;
    }

    @Override
    public ScopedClassPoolFactory getClassPoolFactory() {
        return this.factory;
    }
}

