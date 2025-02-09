// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import java.util.function.ObjIntConsumer;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class EnumMultiset<E extends Enum<E>> extends AbstractMapBasedMultiset<E>
{
    private transient Class<E> type;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;
    
    public static <E extends Enum<E>> EnumMultiset<E> create(final Class<E> type) {
        return new EnumMultiset<E>(type);
    }
    
    public static <E extends Enum<E>> EnumMultiset<E> create(final Iterable<E> elements) {
        final Iterator<E> iterator = elements.iterator();
        Preconditions.checkArgument(iterator.hasNext(), (Object)"EnumMultiset constructor passed empty Iterable");
        final EnumMultiset<E> multiset = new EnumMultiset<E>(iterator.next().getDeclaringClass());
        Iterables.addAll(multiset, (Iterable<? extends E>)elements);
        return multiset;
    }
    
    public static <E extends Enum<E>> EnumMultiset<E> create(final Iterable<E> elements, final Class<E> type) {
        final EnumMultiset<E> result = create(type);
        Iterables.addAll(result, (Iterable<? extends E>)elements);
        return result;
    }
    
    private EnumMultiset(final Class<E> type) {
        super((Map<Object, Count>)WellBehavedMap.wrap(new EnumMap<Object, Object>((Class<Object>)type)));
        this.type = type;
    }
    
    @GwtIncompatible
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.type);
        Serialization.writeMultiset((Multiset<Object>)this, stream);
    }
    
    @GwtIncompatible
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        final Class<E> localType = (Class<E>)stream.readObject();
        this.type = localType;
        this.setBackingMap((Map<E, Count>)WellBehavedMap.wrap(new EnumMap<Object, Object>((Class<Object>)this.type)));
        Serialization.populateMultiset((Multiset<Object>)this, stream);
    }
}
