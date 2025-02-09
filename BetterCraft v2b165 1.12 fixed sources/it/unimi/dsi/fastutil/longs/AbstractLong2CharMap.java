// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Set;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.chars.AbstractCharIterator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;

public abstract class AbstractLong2CharMap extends AbstractLong2CharFunction implements Long2CharMap, Serializable
{
    private static final long serialVersionUID = -4940583368468432370L;
    
    protected AbstractLong2CharMap() {
    }
    
    @Override
    public boolean containsValue(final Object ov) {
        return ov != null && this.containsValue((char)ov);
    }
    
    @Override
    public boolean containsValue(final char v) {
        return this.values().contains(v);
    }
    
    @Override
    public boolean containsKey(final long k) {
        return this.keySet().contains(k);
    }
    
    @Override
    public void putAll(final Map<? extends Long, ? extends Character> m) {
        int n = m.size();
        final Iterator<? extends Map.Entry<? extends Long, ? extends Character>> i = m.entrySet().iterator();
        if (m instanceof Long2CharMap) {
            while (n-- != 0) {
                final Entry e = (Entry)i.next();
                this.put(e.getLongKey(), e.getCharValue());
            }
        }
        else {
            while (n-- != 0) {
                final Map.Entry<? extends Long, ? extends Character> e2 = (Map.Entry<? extends Long, ? extends Character>)i.next();
                this.put((Long)e2.getKey(), (Character)e2.getValue());
            }
        }
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public LongSet keySet() {
        return new AbstractLongSet() {
            @Override
            public boolean contains(final long k) {
                return AbstractLong2CharMap.this.containsKey(k);
            }
            
            @Override
            public int size() {
                return AbstractLong2CharMap.this.size();
            }
            
            @Override
            public void clear() {
                AbstractLong2CharMap.this.clear();
            }
            
            @Override
            public LongIterator iterator() {
                return new AbstractLongIterator() {
                    final ObjectIterator<Map.Entry<Long, Character>> i = AbstractLong2CharMap.this.entrySet().iterator();
                    
                    @Override
                    public long nextLong() {
                        return this.i.next().getLongKey();
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
    public CharCollection values() {
        return new AbstractCharCollection() {
            @Override
            public boolean contains(final char k) {
                return AbstractLong2CharMap.this.containsValue(k);
            }
            
            @Override
            public int size() {
                return AbstractLong2CharMap.this.size();
            }
            
            @Override
            public void clear() {
                AbstractLong2CharMap.this.clear();
            }
            
            @Override
            public CharIterator iterator() {
                return new AbstractCharIterator() {
                    final ObjectIterator<Map.Entry<Long, Character>> i = AbstractLong2CharMap.this.entrySet().iterator();
                    
                    @Deprecated
                    @Override
                    public char nextChar() {
                        return this.i.next().getCharValue();
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
    public ObjectSet<Map.Entry<Long, Character>> entrySet() {
        return (ObjectSet<Map.Entry<Long, Character>>)this.long2CharEntrySet();
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        final ObjectIterator<? extends Map.Entry<Long, Character>> i = this.entrySet().iterator();
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
        final ObjectIterator<? extends Map.Entry<Long, Character>> i = this.entrySet().iterator();
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
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }
    
    public static class BasicEntry implements Entry
    {
        protected long key;
        protected char value;
        
        public BasicEntry(final Long key, final Character value) {
            this.key = key;
            this.value = value;
        }
        
        public BasicEntry(final long key, final char value) {
            this.key = key;
            this.value = value;
        }
        
        @Deprecated
        @Override
        public Long getKey() {
            return this.key;
        }
        
        @Override
        public long getLongKey() {
            return this.key;
        }
        
        @Deprecated
        @Override
        public Character getValue() {
            return this.value;
        }
        
        @Override
        public char getCharValue() {
            return this.value;
        }
        
        @Override
        public char setValue(final char value) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public Character setValue(final Character value) {
            return this.setValue((char)value);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            return e.getKey() != null && e.getKey() instanceof Long && e.getValue() != null && e.getValue() instanceof Character && this.key == (long)e.getKey() && this.value == (char)e.getValue();
        }
        
        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ this.value;
        }
        
        @Override
        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}
