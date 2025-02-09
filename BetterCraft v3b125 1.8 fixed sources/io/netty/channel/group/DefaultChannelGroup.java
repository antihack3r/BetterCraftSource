/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.group;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;
import io.netty.channel.group.CombinedIterator;
import io.netty.channel.group.DefaultChannelGroupFuture;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ConcurrentSet;
import io.netty.util.internal.StringUtil;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultChannelGroup
extends AbstractSet<Channel>
implements ChannelGroup {
    private static final AtomicInteger nextId = new AtomicInteger();
    private final String name;
    private final EventExecutor executor;
    private final ConcurrentSet<Channel> serverChannels = new ConcurrentSet();
    private final ConcurrentSet<Channel> nonServerChannels = new ConcurrentSet();
    private final ChannelFutureListener remover = new ChannelFutureListener(){

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            DefaultChannelGroup.this.remove(future.channel());
        }
    };

    public DefaultChannelGroup(EventExecutor executor) {
        this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), executor);
    }

    public DefaultChannelGroup(String name, EventExecutor executor) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.executor = executor;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isEmpty() {
        return this.nonServerChannels.isEmpty() && this.serverChannels.isEmpty();
    }

    @Override
    public int size() {
        return this.nonServerChannels.size() + this.serverChannels.size();
    }

    @Override
    public boolean contains(Object o2) {
        if (o2 instanceof Channel) {
            Channel c2 = (Channel)o2;
            if (o2 instanceof ServerChannel) {
                return this.serverChannels.contains(c2);
            }
            return this.nonServerChannels.contains(c2);
        }
        return false;
    }

    @Override
    public boolean add(Channel channel) {
        ConcurrentSet<Channel> set = channel instanceof ServerChannel ? this.serverChannels : this.nonServerChannels;
        boolean added = set.add(channel);
        if (added) {
            channel.closeFuture().addListener(this.remover);
        }
        return added;
    }

    @Override
    public boolean remove(Object o2) {
        if (!(o2 instanceof Channel)) {
            return false;
        }
        Channel c2 = (Channel)o2;
        boolean removed = c2 instanceof ServerChannel ? this.serverChannels.remove(c2) : this.nonServerChannels.remove(c2);
        if (!removed) {
            return false;
        }
        c2.closeFuture().removeListener(this.remover);
        return true;
    }

    @Override
    public void clear() {
        this.nonServerChannels.clear();
        this.serverChannels.clear();
    }

    @Override
    public Iterator<Channel> iterator() {
        return new CombinedIterator<Channel>(this.serverChannels.iterator(), this.nonServerChannels.iterator());
    }

    @Override
    public Object[] toArray() {
        ArrayList<Channel> channels = new ArrayList<Channel>(this.size());
        channels.addAll(this.serverChannels);
        channels.addAll(this.nonServerChannels);
        return channels.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a2) {
        ArrayList<Channel> channels = new ArrayList<Channel>(this.size());
        channels.addAll(this.serverChannels);
        channels.addAll(this.nonServerChannels);
        return channels.toArray(a2);
    }

    @Override
    public ChannelGroupFuture close() {
        return this.close(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture disconnect() {
        return this.disconnect(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture deregister() {
        return this.deregister(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture write(Object message) {
        return this.write(message, ChannelMatchers.all());
    }

    private static Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf)message).duplicate().retain();
        }
        if (message instanceof ByteBufHolder) {
            return ((ByteBufHolder)message).duplicate().retain();
        }
        return ReferenceCountUtil.retain(message);
    }

    @Override
    public ChannelGroupFuture write(Object message, ChannelMatcher matcher) {
        if (message == null) {
            throw new NullPointerException("message");
        }
        if (matcher == null) {
            throw new NullPointerException("matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        for (Channel c2 : this.nonServerChannels) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.write(DefaultChannelGroup.safeDuplicate(message)));
        }
        ReferenceCountUtil.release(message);
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public ChannelGroup flush() {
        return this.flush(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object message) {
        return this.writeAndFlush(message);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message) {
        return this.writeAndFlush(message, ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture disconnect(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException("matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        for (Channel c2 : this.serverChannels) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.disconnect());
        }
        for (Channel c2 : this.nonServerChannels) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.disconnect());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public ChannelGroupFuture close(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException("matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        for (Channel c2 : this.serverChannels) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.close());
        }
        for (Channel c2 : this.nonServerChannels) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.close());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public ChannelGroupFuture deregister(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException("matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        for (Channel c2 : this.serverChannels) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.deregister());
        }
        for (Channel c2 : this.nonServerChannels) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.deregister());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public ChannelGroup flush(ChannelMatcher matcher) {
        for (Channel c2 : this.nonServerChannels) {
            if (!matcher.matches(c2)) continue;
            c2.flush();
        }
        return this;
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object message, ChannelMatcher matcher) {
        return this.writeAndFlush(message, matcher);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher) {
        if (message == null) {
            throw new NullPointerException("message");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        for (Channel c2 : this.nonServerChannels) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.writeAndFlush(DefaultChannelGroup.safeDuplicate(message)));
        }
        ReferenceCountUtil.release(message);
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object o2) {
        return this == o2;
    }

    @Override
    public int compareTo(ChannelGroup o2) {
        int v2 = this.name().compareTo(o2.name());
        if (v2 != 0) {
            return v2;
        }
        return System.identityHashCode(this) - System.identityHashCode(o2);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(name: " + this.name() + ", size: " + this.size() + ')';
    }
}

