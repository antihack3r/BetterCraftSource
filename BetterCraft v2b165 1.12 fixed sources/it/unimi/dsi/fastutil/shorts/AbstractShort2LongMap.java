// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Set;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.longs.AbstractLongIterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;

public abstract class AbstractShort2LongMap extends AbstractShort2LongFunction implements Short2LongMap, Serializable
{
    private static final long serialVersionUID = -4940583368468432370L;
    
    protected AbstractShort2LongMap() {
    }
    
    @Override
    public boolean containsValue(final Object ov) {
        return ov != null && this.containsValue((long)ov);
    }
    
    @Override
    public boolean containsValue(final long v) {
        return this.values().contains(v);
    }
    
    @Override
    public boolean containsKey(final short k) {
        return this.keySet().contains(k);
    }
    
    @Override
    public void putAll(final Map<? extends Short, ? extends Long> m) {
        int n = m.size();
        final Iterator<? extends Map.Entry<? extends Short, ? extends Long>> i = m.entrySet().iterator();
        if (m instanceof Short2LongMap) {
            while (n-- != 0) {
                final Entry e = (Entry)i.next();
                this.put(e.getShortKey(), e.getLongValue());
            }
        }
        else {
            while (n-- != 0) {
                final Map.Entry<? extends Short, ? extends Long> e2 = (Map.Entry<? extends Short, ? extends Long>)i.next();
                this.put((Short)e2.getKey(), (Long)e2.getValue());
            }
        }
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public ShortSet keySet() {
        return new AbstractShortSet() {
            @Override
            public boolean contains(final short k) {
                return AbstractShort2LongMap.this.containsKey(k);
            }
            
            @Override
            public int size() {
                return AbstractShort2LongMap.this.size();
            }
            
            @Override
            public void clear() {
                AbstractShort2LongMap.this.clear();
            }
            
            @Override
            public ShortIterator iterator() {
                return new AbstractShortIterator() {
                    final ObjectIterator<Map.Entry<Short, Long>> i = AbstractShort2LongMap.this.entrySet().iterator();
                    
                    @Override
                    public short nextShort() {
                        return this.i.next().getShortKey();
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }
                    
                    @Override
                    public void remove() {
                        this.i.remove();
                    }
                };
            }
        };
    }
    
    @Override
    public LongCollection values() {
        return new AbstractLongCollection() {
            @Override
            public boolean contains(final long k) {
                return AbstractShort2LongMap.this.containsValue(k);
            }
            
            @Override
            public int size() {
                return AbstractShort2LongMap.this.size();
            }
            
            @Override
            public void clear() {
                AbstractShort2LongMap.this.clear();
            }
            
            @Override
            public LongIterator iterator() {
                return new AbstractLongIterator() {
                    final ObjectIterator<Map.Entry<Short, Long>> i = AbstractShort2LongMap.this.entrySet().iterator();
                    
                    @Deprecated
                    @Override
                    public long nextLong() {
                        return this.i.next().getLongValue();
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }
                };
            }
        };
    }
    
    @Override
    public ObjectSet<Map.Entry<Short, Long>> entrySet() {
        return (ObjectSet<Map.Entry<Short, Long>>)this.short2LongEntrySet();
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        final ObjectIterator<? extends Map.Entry<Short, Long>> i = this.entrySet().iterator();
        while (n-- != 0) {
            h += i.next().hashCode();
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        final Map<?, ?> m = (Map<?, ?>)o;
        return m.size() == this.size() && this.entrySet().containsAll(m.entrySet());
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        final ObjectIterator<? extends Map.Entry<Short, Long>> i = this.entrySet().iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            }
            else {
                s.append(", ");
            }
            final Entry e = i.next();
            s.append(String.valueOf(e.getShortKey()));
            s.append("=>");
            s.append(String.valueOf(e.getLongValue()));
        }
        s.append("}");
        return s.toString();
    }
    
    public static class BasicEntry implements Entry
    {
        protected short key;
        protected long value;
        
        public BasicEntry(final Short key, final Long value) {
            this.key = key;
            this.value = value;
        }
        
        public BasicEntry(final short key, final long value) {
            this.key = key;
            this.value = value;
        }
        
        @Deprecated
        @Override
        public Short getKey() {
            return this.key;
        }
        
        @Override
        public short getShortKey() {
            return this.key;
        }
        
        @Deprecated
        @Override
        public Long getValue() {
            return this.value;
        }
        
        @Override
        public long getLongValue() {
            return this.value;
        }
        
        @Override
        public long setValue(final long value) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public Long setValue(final Long value) {
            return this.setValue((long)value);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            return e.getKey() != null && e.getKey() instanceof Short && e.getValue() != null && e.getValue() instanceof Long && this.key == (short)e.getKey() && this.value == (long)e.getValue();
        }
        
        @Override
        public int hashCode() {
            return this.key ^ HashCommon.long2int(this.value);
        }
        
        @Override
        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}
