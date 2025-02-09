/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Closeables
implements Closeable {
    private List<WeakReference<Closeable>> list;

    public Closeables() {
    }

    public Closeables(Closeable ... closeable) {
        for (Closeable cl2 : closeable) {
            this.list.add(new HardReference(cl2));
        }
    }

    @Override
    public void close() throws IOException {
        this.close(null);
    }

    public void close(IOException superException) throws IOException {
        IOException exc = superException;
        if (this.list != null) {
            for (WeakReference<Closeable> ref : this.list) {
                Closeable cl2 = (Closeable)ref.get();
                if (cl2 == null) continue;
                try {
                    cl2.close();
                }
                catch (IOException e2) {
                    if (exc == null) {
                        exc = e2;
                        continue;
                    }
                    exc.addSuppressed(e2);
                }
            }
        }
        if (exc != null) {
            throw exc;
        }
    }

    public synchronized boolean add(WeakReference<Closeable> closeable) {
        Closeable cl2 = (Closeable)closeable.get();
        if (cl2 == null) {
            return false;
        }
        if (this.list == null) {
            this.list = new ArrayList<WeakReference<Closeable>>();
        } else {
            for (WeakReference<Closeable> ref : this.list) {
                if (ref.get() != cl2) continue;
                return false;
            }
        }
        this.list.add(closeable);
        return true;
    }

    public synchronized boolean add(Closeable closeable) {
        return this.add(new HardReference<Closeable>(closeable));
    }

    public synchronized boolean remove(Closeable closeable) {
        if (this.list == null || closeable == null) {
            return false;
        }
        Iterator<WeakReference<Closeable>> it2 = this.list.iterator();
        while (it2.hasNext()) {
            if (it2.next().get() != closeable) continue;
            it2.remove();
            return true;
        }
        return false;
    }

    private static final class HardReference<V>
    extends WeakReference<V> {
        private final V strongRef;

        private HardReference(V referent) {
            super(null);
            this.strongRef = referent;
        }

        @Override
        public V get() {
            return this.strongRef;
        }
    }
}

