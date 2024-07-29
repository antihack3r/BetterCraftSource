/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.ThreadInformation;

class BasicThreadInformation
implements ThreadInformation {
    private static final int HASH_SHIFT = 32;
    private static final int HASH_MULTIPLIER = 31;
    private final long id;
    private final String name;
    private final String longName;
    private final Thread.State state;
    private final int priority;
    private final boolean isAlive;
    private final boolean isDaemon;
    private final String threadGroupName;

    public BasicThreadInformation(Thread thread) {
        this.id = thread.getId();
        this.name = thread.getName();
        this.longName = thread.toString();
        this.state = thread.getState();
        this.priority = thread.getPriority();
        this.isAlive = thread.isAlive();
        this.isDaemon = thread.isDaemon();
        ThreadGroup group = thread.getThreadGroup();
        this.threadGroupName = group == null ? null : group.getName();
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        BasicThreadInformation that = (BasicThreadInformation)o2;
        if (this.id != that.id) {
            return false;
        }
        return !(this.name != null ? !this.name.equals(that.name) : that.name != null);
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }

    @Override
    public void printThreadInfo(StringBuilder sb2) {
        sb2.append("\"").append(this.name).append("\" ");
        if (this.isDaemon) {
            sb2.append("daemon ");
        }
        sb2.append("prio=").append(this.priority).append(" tid=").append(this.id).append(" ");
        if (this.threadGroupName != null) {
            sb2.append("group=\"").append(this.threadGroupName).append("\"");
        }
        sb2.append("\n");
        sb2.append("\tThread state: ").append(this.state.name()).append("\n");
    }

    @Override
    public void printStack(StringBuilder sb2, StackTraceElement[] trace) {
        for (StackTraceElement element : trace) {
            sb2.append("\tat ").append(element).append("\n");
        }
    }
}

