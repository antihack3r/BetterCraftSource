// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.io.Serializable;

public class Double2FloatFunctions
{
    public static final EmptyFunction EMPTY_FUNCTION;
    
    private Double2FloatFunctions() {
    }
    
    public static Double2FloatFunction singleton(final double key, final float value) {
        return new Singleton(key, value);
    }
    
    public static Double2FloatFunction singleton(final Double key, final Float value) {
        return new Singleton(key, value);
    }
    
    public static Double2FloatFunction synchronize(final Double2FloatFunction f) {
        return new SynchronizedFunction(f);
    }
    
    public static Double2FloatFunction synchronize(final Double2FloatFunction f, final Object sync) {
        return new SynchronizedFunction(f, sync);
    }
    
    public static Double2FloatFunction unmodifiable(final Double2FloatFunction f) {
        return new UnmodifiableFunction(f);
    }
    
    static {
        EMPTY_FUNCTION = new EmptyFunction();
    }
    
    public static class EmptyFunction extends AbstractDouble2FloatFunction implements Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        protected EmptyFunction() {
        }
        
        @Override
        public float get(final double k) {
            return 0.0f;
        }
        
        @Override
        public boolean containsKey(final double k) {
            return false;
        }
        
        @Override
        public float defaultReturnValue() {
            return 0.0f;
        }
        
        @Override
        public void defaultReturnValue(final float defRetValue) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public Float get(final Object k) {
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
            return Double2FloatFunctions.EMPTY_FUNCTION;
        }
        
        public Object clone() {
            return Double2FloatFunctions.EMPTY_FUNCTION;
        }
    }
    
    public static class Singleton extends AbstractDouble2FloatFunction implements Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final double key;
        protected final float value;
        
        protected Singleton(final double key, final float value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public boolean containsKey(final double k) {
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
        }
        
        @Override
        public float get(final double k) {
            if (Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k)) {
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
    
    public static class SynchronizedFunction extends AbstractDouble2FloatFunction implements Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2FloatFunction function;
        protected final Object sync;
        
        protected SynchronizedFunction(final Double2FloatFunction f, final Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }
        
        protected SynchronizedFunction(final Double2FloatFunction f) {
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
        public boolean containsKey(final double k) {
            synchronized (this.sync) {
                return this.function.containsKey(k);
            }
        }
        
        @Override
        public float defaultReturnValue() {
            synchronized (this.sync) {
                return this.function.defaultReturnValue();
            }
        }
        
        @Override
        public void defaultReturnValue(final float defRetValue) {
            synchronized (this.sync) {
                this.function.defaultReturnValue(defRetValue);
            }
        }
        
        @Override
        public float put(final double k, final float v) {
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
        public Float put(final Double k, final Float v) {
            synchronized (this.sync) {
                return this.function.put(k, v);
            }
        }
        
        @Deprecated
        @Override
        public Float get(final Object k) {
            synchronized (this.sync) {
                return ((Function<K, Float>)this.function).get(k);
            }
        }
        
        @Deprecated
        @Override
        public Float remove(final Object k) {
            synchronized (this.sync) {
                return ((Function<K, Float>)this.function).remove(k);
            }
        }
        
        @Override
        public float remove(final double k) {
            synchronized (this.sync) {
                return this.function.remove(k);
            }
        }
        
        @Override
        public float get(final double k) {
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
    
    public static class UnmodifiableFunction extends AbstractDouble2FloatFunction implements Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2FloatFunction function;
        
        protected UnmodifiableFunction(final Double2FloatFunction f) {
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
        public boolean containsKey(final double k) {
            return this.function.containsKey(k);
        }
        
        @Override
        public float defaultReturnValue() {
            return this.function.defaultReturnValue();
        }
        
        @Override
        public void defaultReturnValue(final float defRetValue) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public float put(final double k, final float v) {
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
        public float remove(final double k) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public float get(final double k) {
            return this.function.get(k);
        }
        
        @Override
        public boolean containsKey(final Object ok) {
            return this.function.containsKey(ok);
        }
    }
}
