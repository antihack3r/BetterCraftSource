// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import java.io.Serializable;

public class Byte2ByteFunctions
{
    public static final EmptyFunction EMPTY_FUNCTION;
    
    private Byte2ByteFunctions() {
    }
    
    public static Byte2ByteFunction singleton(final byte key, final byte value) {
        return new Singleton(key, value);
    }
    
    public static Byte2ByteFunction singleton(final Byte key, final Byte value) {
        return new Singleton(key, value);
    }
    
    public static Byte2ByteFunction synchronize(final Byte2ByteFunction f) {
        return new SynchronizedFunction(f);
    }
    
    public static Byte2ByteFunction synchronize(final Byte2ByteFunction f, final Object sync) {
        return new SynchronizedFunction(f, sync);
    }
    
    public static Byte2ByteFunction unmodifiable(final Byte2ByteFunction f) {
        return new UnmodifiableFunction(f);
    }
    
    static {
        EMPTY_FUNCTION = new EmptyFunction();
    }
    
    public static class EmptyFunction extends AbstractByte2ByteFunction implements Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        protected EmptyFunction() {
        }
        
        @Override
        public byte get(final byte k) {
            return 0;
        }
        
        @Override
        public boolean containsKey(final byte k) {
            return false;
        }
        
        @Override
        public byte defaultReturnValue() {
            return 0;
        }
        
        @Override
        public void defaultReturnValue(final byte defRetValue) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public Byte get(final Object k) {
            return null;
        }
        
        @Override
        public int size() {
            return 0;
        }
        
        @Override
        public void clear() {
        }
        
        private Object readResolve() {
            return Byte2ByteFunctions.EMPTY_FUNCTION;
        }
        
        public Object clone() {
            return Byte2ByteFunctions.EMPTY_FUNCTION;
        }
    }
    
    public static class Singleton extends AbstractByte2ByteFunction implements Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final byte key;
        protected final byte value;
        
        protected Singleton(final byte key, final byte value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public boolean containsKey(final byte k) {
            return this.key == k;
        }
        
        @Override
        public byte get(final byte k) {
            if (this.key == k) {
                return this.value;
            }
            return this.defRetValue;
        }
        
        @Override
        public int size() {
            return 1;
        }
        
        public Object clone() {
            return this;
        }
    }
    
    public static class SynchronizedFunction extends AbstractByte2ByteFunction implements Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ByteFunction function;
        protected final Object sync;
        
        protected SynchronizedFunction(final Byte2ByteFunction f, final Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }
        
        protected SynchronizedFunction(final Byte2ByteFunction f) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = this;
        }
        
        @Override
        public int size() {
            synchronized (this.sync) {
                return this.function.size();
            }
        }
        
        @Override
        public boolean containsKey(final byte k) {
            synchronized (this.sync) {
                return this.function.containsKey(k);
            }
        }
        
        @Override
        public byte defaultReturnValue() {
            synchronized (this.sync) {
                return this.function.defaultReturnValue();
            }
        }
        
        @Override
        public void defaultReturnValue(final byte defRetValue) {
            synchronized (this.sync) {
                this.function.defaultReturnValue(defRetValue);
            }
        }
        
        @Override
        public byte put(final byte k, final byte v) {
            synchronized (this.sync) {
                return this.function.put(k, v);
            }
        }
        
        @Override
        public void clear() {
            synchronized (this.sync) {
                this.function.clear();
            }
        }
        
        @Override
        public String toString() {
            synchronized (this.sync) {
                return this.function.toString();
            }
        }
        
        @Deprecated
        @Override
        public Byte put(final Byte k, final Byte v) {
            synchronized (this.sync) {
                return this.function.put(k, v);
            }
        }
        
        @Deprecated
        @Override
        public Byte get(final Object k) {
            synchronized (this.sync) {
                return ((Function<K, Byte>)this.function).get(k);
            }
        }
        
        @Deprecated
        @Override
        public Byte remove(final Object k) {
            synchronized (this.sync) {
                return ((Function<K, Byte>)this.function).remove(k);
            }
        }
        
        @Override
        public byte remove(final byte k) {
            synchronized (this.sync) {
                return this.function.remove(k);
            }
        }
        
        @Override
        public byte get(final byte k) {
            synchronized (this.sync) {
                return this.function.get(k);
            }
        }
        
        @Override
        public boolean containsKey(final Object ok) {
            synchronized (this.sync) {
                return this.function.containsKey(ok);
            }
        }
    }
    
    public static class UnmodifiableFunction extends AbstractByte2ByteFunction implements Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ByteFunction function;
        
        protected UnmodifiableFunction(final Byte2ByteFunction f) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
        }
        
        @Override
        public int size() {
            return this.function.size();
        }
        
        @Override
        public boolean containsKey(final byte k) {
            return this.function.containsKey(k);
        }
        
        @Override
        public byte defaultReturnValue() {
            return this.function.defaultReturnValue();
        }
        
        @Override
        public void defaultReturnValue(final byte defRetValue) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public byte put(final byte k, final byte v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            return this.function.toString();
        }
        
        @Deprecated
        @Override
        public byte remove(final byte k) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public byte get(final byte k) {
            return this.function.get(k);
        }
        
        @Override
        public boolean containsKey(final Object ok) {
            return this.function.containsKey(ok);
        }
    }
}
