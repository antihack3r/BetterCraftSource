// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import javax.annotation.Nullable;

public class IntHashMap<V>
{
    private transient Entry<V>[] slots;
    private transient int count;
    private int threshold;
    private final float growFactor = 0.75f;
    
    public IntHashMap() {
        this.slots = new Entry[16];
        this.threshold = 12;
    }
    
    private static int computeHash(int integer) {
        integer = (integer ^ integer >>> 20 ^ integer >>> 12);
        return integer ^ integer >>> 7 ^ integer >>> 4;
    }
    
    private static int getSlotIndex(final int hash, final int slotCount) {
        return hash & slotCount - 1;
    }
    
    @Nullable
    public V lookup(final int hashEntry) {
        final int i = computeHash(hashEntry);
        for (Entry<V> entry = this.slots[getSlotIndex(i, this.slots.length)]; entry != null; entry = entry.nextEntry) {
            if (entry.hashEntry == hashEntry) {
                return entry.valueEntry;
            }
        }
        return null;
    }
    
    public boolean containsItem(final int hashEntry) {
        return this.lookupEntry(hashEntry) != null;
    }
    
    @Nullable
    final Entry<V> lookupEntry(final int hashEntry) {
        final int i = computeHash(hashEntry);
        for (Entry<V> entry = this.slots[getSlotIndex(i, this.slots.length)]; entry != null; entry = entry.nextEntry) {
            if (entry.hashEntry == hashEntry) {
                return entry;
            }
        }
        return null;
    }
    
    public void addKey(final int hashEntry, final V valueEntry) {
        final int i = computeHash(hashEntry);
        final int j = getSlotIndex(i, this.slots.length);
        for (Entry<V> entry = this.slots[j]; entry != null; entry = entry.nextEntry) {
            if (entry.hashEntry == hashEntry) {
                entry.valueEntry = valueEntry;
                return;
            }
        }
        this.insert(i, hashEntry, valueEntry, j);
    }
    
    private void grow(final int p_76047_1_) {
        final Entry[] entry = this.slots;
        final int i = entry.length;
        if (i == 1073741824) {
            this.threshold = Integer.MAX_VALUE;
        }
        else {
            final Entry[] entry2 = new Entry[p_76047_1_];
            this.copyTo(entry2);
            this.slots = entry2;
            this.threshold = (int)(p_76047_1_ * 0.75f);
        }
    }
    
    private void copyTo(final Entry<V>[] p_76048_1_) {
        final Entry[] entry = this.slots;
        final int i = p_76048_1_.length;
        for (int j = 0; j < entry.length; ++j) {
            Entry<V> entry2 = entry[j];
            if (entry2 != null) {
                entry[j] = null;
                Entry<V> entry3;
                do {
                    entry3 = entry2.nextEntry;
                    final int k = getSlotIndex(entry2.slotHash, i);
                    entry2.nextEntry = p_76048_1_[k];
                    p_76048_1_[k] = entry2;
                } while ((entry2 = entry3) != null);
            }
        }
    }
    
    @Nullable
    public V removeObject(final int p_76049_1_) {
        final Entry<V> entry = this.removeEntry(p_76049_1_);
        return (entry == null) ? null : entry.valueEntry;
    }
    
    @Nullable
    final Entry<V> removeEntry(final int p_76036_1_) {
        final int i = computeHash(p_76036_1_);
        final int j = getSlotIndex(i, this.slots.length);
        Entry<V> entry2;
        Entry<V> entry3;
        for (Entry<V> entry = entry2 = this.slots[j]; entry2 != null; entry2 = entry3) {
            entry3 = entry2.nextEntry;
            if (entry2.hashEntry == p_76036_1_) {
                --this.count;
                if (entry == entry2) {
                    this.slots[j] = entry3;
                }
                else {
                    entry.nextEntry = entry3;
                }
                return entry2;
            }
            entry = entry2;
        }
        return entry2;
    }
    
    public void clearMap() {
        final Entry[] entry = this.slots;
        for (int i = 0; i < entry.length; ++i) {
            entry[i] = null;
        }
        this.count = 0;
    }
    
    private void insert(final int p_76040_1_, final int p_76040_2_, final V p_76040_3_, final int p_76040_4_) {
        final Entry<V> entry = this.slots[p_76040_4_];
        this.slots[p_76040_4_] = new Entry<V>(p_76040_1_, p_76040_2_, p_76040_3_, entry);
        if (this.count++ >= this.threshold) {
            this.grow(2 * this.slots.length);
        }
    }
    
    static class Entry<V>
    {
        final int hashEntry;
        V valueEntry;
        Entry<V> nextEntry;
        final int slotHash;
        
        Entry(final int p_i1552_1_, final int p_i1552_2_, final V p_i1552_3_, final Entry<V> p_i1552_4_) {
            this.valueEntry = p_i1552_3_;
            this.nextEntry = p_i1552_4_;
            this.hashEntry = p_i1552_2_;
            this.slotHash = p_i1552_1_;
        }
        
        public final int getHash() {
            return this.hashEntry;
        }
        
        public final V getValue() {
            return this.valueEntry;
        }
        
        @Override
        public final boolean equals(final Object p_equals_1_) {
            if (!(p_equals_1_ instanceof Entry)) {
                return false;
            }
            final Entry<V> entry = (Entry<V>)p_equals_1_;
            if (this.hashEntry == entry.hashEntry) {
                final Object object = this.getValue();
                final Object object2 = entry.getValue();
                if (object == object2 || (object != null && object.equals(object2))) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public final int hashCode() {
            return computeHash(this.hashEntry);
        }
        
        @Override
        public final String toString() {
            return String.valueOf(this.getHash()) + "=" + this.getValue();
        }
    }
}
