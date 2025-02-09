/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.message;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import org.apache.logging.log4j.message.ThreadInformation;

class ExtendedThreadInformation
implements ThreadInformation {
    private final ThreadInfo info;

    public ExtendedThreadInformation(ThreadInfo thread) {
        this.info = thread;
    }

    @Override
    public void printThreadInfo(StringBuilder sb2) {
        sb2.append("\"").append(this.info.getThreadName()).append("\"");
        sb2.append(" Id=").append(this.info.getThreadId()).append(" ");
        this.formatState(sb2, this.info);
        if (this.info.isSuspended()) {
            sb2.append(" (suspended)");
        }
        if (this.info.isInNative()) {
            sb2.append(" (in native)");
        }
        sb2.append('\n');
    }

    @Override
    public void printStack(StringBuilder sb2, StackTraceElement[] stack) {
        int i2 = 0;
        for (StackTraceElement element : stack) {
            sb2.append("\tat ").append(element.toString());
            sb2.append('\n');
            if (i2 == 0 && this.info.getLockInfo() != null) {
                Thread.State ts2 = this.info.getThreadState();
                switch (ts2) {
                    case BLOCKED: {
                        sb2.append("\t-  blocked on ");
                        this.formatLock(sb2, this.info.getLockInfo());
                        sb2.append('\n');
                        break;
                    }
                    case WAITING: {
                        sb2.append("\t-  waiting on ");
                        this.formatLock(sb2, this.info.getLockInfo());
                        sb2.append('\n');
                        break;
                    }
                    case TIMED_WAITING: {
                        sb2.append("\t-  waiting on ");
                        this.formatLock(sb2, this.info.getLockInfo());
                        sb2.append('\n');
                        break;
                    }
                }
            }
            for (MonitorInfo mi : this.info.getLockedMonitors()) {
                if (mi.getLockedStackDepth() != i2) continue;
                sb2.append("\t-  locked ");
                this.formatLock(sb2, mi);
                sb2.append('\n');
            }
            ++i2;
        }
        LockInfo[] locks = this.info.getLockedSynchronizers();
        if (locks.length > 0) {
            sb2.append("\n\tNumber of locked synchronizers = ").append(locks.length).append('\n');
            for (LockInfo li : locks) {
                sb2.append("\t- ");
                this.formatLock(sb2, li);
                sb2.append('\n');
            }
        }
    }

    private void formatLock(StringBuilder sb2, LockInfo lock) {
        sb2.append("<").append(lock.getIdentityHashCode()).append("> (a ");
        sb2.append(lock.getClassName()).append(")");
    }

    private void formatState(StringBuilder sb2, ThreadInfo info) {
        Thread.State state = info.getThreadState();
        sb2.append((Object)state);
        switch (state) {
            case BLOCKED: {
                sb2.append(" (on object monitor owned by \"");
                sb2.append(info.getLockOwnerName()).append("\" Id=").append(info.getLockOwnerId()).append(")");
                break;
            }
            case WAITING: {
                StackTraceElement element = info.getStackTrace()[0];
                String className = element.getClassName();
                String method = element.getMethodName();
                if (className.equals("java.lang.Object") && method.equals("wait")) {
                    sb2.append(" (on object monitor");
                    if (info.getLockOwnerName() != null) {
                        sb2.append(" owned by \"");
                        sb2.append(info.getLockOwnerName()).append("\" Id=").append(info.getLockOwnerId());
                    }
                    sb2.append(")");
                    break;
                }
                if (className.equals("java.lang.Thread") && method.equals("join")) {
                    sb2.append(" (on completion of thread ").append(info.getLockOwnerId()).append(")");
                    break;
                }
                sb2.append(" (parking for lock");
                if (info.getLockOwnerName() != null) {
                    sb2.append(" owned by \"");
                    sb2.append(info.getLockOwnerName()).append("\" Id=").append(info.getLockOwnerId());
                }
                sb2.append(")");
                break;
            }
            case TIMED_WAITING: {
                StackTraceElement element = info.getStackTrace()[0];
                String className = element.getClassName();
                String method = element.getMethodName();
                if (className.equals("java.lang.Object") && method.equals("wait")) {
                    sb2.append(" (on object monitor");
                    if (info.getLockOwnerName() != null) {
                        sb2.append(" owned by \"");
                        sb2.append(info.getLockOwnerName()).append("\" Id=").append(info.getLockOwnerId());
                    }
                    sb2.append(")");
                    break;
                }
                if (className.equals("java.lang.Thread") && method.equals("sleep")) {
                    sb2.append(" (sleeping)");
                    break;
                }
                if (className.equals("java.lang.Thread") && method.equals("join")) {
                    sb2.append(" (on completion of thread ").append(info.getLockOwnerId()).append(")");
                    break;
                }
                sb2.append(" (parking for lock");
                if (info.getLockOwnerName() != null) {
                    sb2.append(" owned by \"");
                    sb2.append(info.getLockOwnerName()).append("\" Id=").append(info.getLockOwnerId());
                }
                sb2.append(")");
                break;
            }
        }
    }
}

