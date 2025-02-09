/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.chmv8;

import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.chmv8.CountedCompleter;
import io.netty.util.internal.chmv8.ForkJoinTask;
import io.netty.util.internal.chmv8.ForkJoinWorkerThread;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class ForkJoinPool
extends AbstractExecutorService {
    static final ThreadLocal<Submitter> submitters;
    public static final ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
    private static final RuntimePermission modifyThreadPermission;
    static final ForkJoinPool common;
    static final int commonParallelism;
    private static int poolNumberSequence;
    private static final long IDLE_TIMEOUT = 2000000000L;
    private static final long FAST_IDLE_TIMEOUT = 200000000L;
    private static final long TIMEOUT_SLOP = 2000000L;
    private static final int MAX_HELP = 64;
    private static final int SEED_INCREMENT = 1640531527;
    private static final int AC_SHIFT = 48;
    private static final int TC_SHIFT = 32;
    private static final int ST_SHIFT = 31;
    private static final int EC_SHIFT = 16;
    private static final int SMASK = 65535;
    private static final int MAX_CAP = Short.MAX_VALUE;
    private static final int EVENMASK = 65534;
    private static final int SQMASK = 126;
    private static final int SHORT_SIGN = 32768;
    private static final int INT_SIGN = Integer.MIN_VALUE;
    private static final long STOP_BIT = 0x80000000L;
    private static final long AC_MASK = -281474976710656L;
    private static final long TC_MASK = 0xFFFF00000000L;
    private static final long TC_UNIT = 0x100000000L;
    private static final long AC_UNIT = 0x1000000000000L;
    private static final int UAC_SHIFT = 16;
    private static final int UTC_SHIFT = 0;
    private static final int UAC_MASK = -65536;
    private static final int UTC_MASK = 65535;
    private static final int UAC_UNIT = 65536;
    private static final int UTC_UNIT = 1;
    private static final int E_MASK = Integer.MAX_VALUE;
    private static final int E_SEQ = 65536;
    private static final int SHUTDOWN = Integer.MIN_VALUE;
    private static final int PL_LOCK = 2;
    private static final int PL_SIGNAL = 1;
    private static final int PL_SPINS = 256;
    static final int LIFO_QUEUE = 0;
    static final int FIFO_QUEUE = 1;
    static final int SHARED_QUEUE = -1;
    volatile long pad00;
    volatile long pad01;
    volatile long pad02;
    volatile long pad03;
    volatile long pad04;
    volatile long pad05;
    volatile long pad06;
    volatile long stealCount;
    volatile long ctl;
    volatile int plock;
    volatile int indexSeed;
    final short parallelism;
    final short mode;
    WorkQueue[] workQueues;
    final ForkJoinWorkerThreadFactory factory;
    final Thread.UncaughtExceptionHandler ueh;
    final String workerNamePrefix;
    volatile Object pad10;
    volatile Object pad11;
    volatile Object pad12;
    volatile Object pad13;
    volatile Object pad14;
    volatile Object pad15;
    volatile Object pad16;
    volatile Object pad17;
    volatile Object pad18;
    volatile Object pad19;
    volatile Object pad1a;
    volatile Object pad1b;
    private static final Unsafe U;
    private static final long CTL;
    private static final long PARKBLOCKER;
    private static final int ABASE;
    private static final int ASHIFT;
    private static final long STEALCOUNT;
    private static final long PLOCK;
    private static final long INDEXSEED;
    private static final long QBASE;
    private static final long QLOCK;

    private static void checkPermission() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(modifyThreadPermission);
        }
    }

    private static final synchronized int nextPoolId() {
        return ++poolNumberSequence;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int acquirePlock() {
        int spins = 256;
        int nps;
        int ps2;
        while (((ps2 = this.plock) & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps2, nps = ps2 + 2)) {
            if (spins >= 0) {
                if (ThreadLocalRandom.current().nextInt() < 0) continue;
                --spins;
                continue;
            }
            if (!U.compareAndSwapInt(this, PLOCK, ps2, ps2 | 1)) continue;
            ForkJoinPool forkJoinPool = this;
            synchronized (forkJoinPool) {
                if ((this.plock & 1) != 0) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ie2) {
                        try {
                            Thread.currentThread().interrupt();
                        }
                        catch (SecurityException ignore) {}
                    }
                } else {
                    this.notifyAll();
                }
            }
        }
        return nps;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void releasePlock(int ps2) {
        this.plock = ps2;
        ForkJoinPool forkJoinPool = this;
        synchronized (forkJoinPool) {
            this.notifyAll();
        }
    }

    private void tryAddWorker() {
        int e2;
        long c2;
        int u2;
        while ((u2 = (int)((c2 = this.ctl) >>> 32)) < 0 && (u2 & 0x8000) != 0 && (e2 = (int)c2) >= 0) {
            long nc2 = (long)(u2 + 1 & 0xFFFF | u2 + 65536 & 0xFFFF0000) << 32 | (long)e2;
            if (!U.compareAndSwapLong(this, CTL, c2, nc2)) continue;
            Throwable ex2 = null;
            ForkJoinWorkerThread wt2 = null;
            try {
                ForkJoinWorkerThreadFactory fac = this.factory;
                if (fac != null && (wt2 = fac.newThread(this)) != null) {
                    wt2.start();
                    break;
                }
            }
            catch (Throwable rex) {
                ex2 = rex;
            }
            this.deregisterWorker(wt2, ex2);
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final WorkQueue registerWorker(ForkJoinWorkerThread wt2) {
        int s2;
        wt2.setDaemon(true);
        Thread.UncaughtExceptionHandler handler = this.ueh;
        if (handler != null) {
            wt2.setUncaughtExceptionHandler(handler);
        }
        do {
            s2 = this.indexSeed;
        } while (!U.compareAndSwapInt(this, INDEXSEED, s2, s2 += 1640531527) || s2 == 0);
        WorkQueue w2 = new WorkQueue(this, wt2, this.mode, s2);
        int ps2 = this.plock;
        if ((ps2 & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps2, ps2 += 2)) {
            ps2 = this.acquirePlock();
        }
        int nps = ps2 & Integer.MIN_VALUE | ps2 + 2 & Integer.MAX_VALUE;
        try {
            WorkQueue[] ws2 = this.workQueues;
            if (this.workQueues != null) {
                int n2 = ws2.length;
                int m2 = n2 - 1;
                int r2 = s2 << 1 | 1;
                if (ws2[r2 &= m2] != null) {
                    int step;
                    int probes = 0;
                    int n3 = step = n2 <= 4 ? 2 : (n2 >>> 1 & 0xFFFE) + 2;
                    while (ws2[r2 = r2 + step & m2] != null) {
                        if (++probes < n2) continue;
                        this.workQueues = ws2 = Arrays.copyOf(ws2, n2 <<= 1);
                        m2 = n2 - 1;
                        probes = 0;
                    }
                }
                w2.poolIndex = (short)r2;
                w2.eventCount = r2;
                ws2[r2] = w2;
            }
        }
        finally {
            if (!U.compareAndSwapInt(this, PLOCK, ps2, nps)) {
                this.releasePlock(nps);
            }
        }
        wt2.setName(this.workerNamePrefix.concat(Integer.toString(w2.poolIndex >>> 1)));
        return w2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void deregisterWorker(ForkJoinWorkerThread wt2, Throwable ex2) {
        long c2;
        WorkQueue w2 = null;
        if (wt2 != null && (w2 = wt2.workQueue) != null) {
            long sc2;
            w2.qlock = -1;
            while (!U.compareAndSwapLong(this, STEALCOUNT, sc2 = this.stealCount, sc2 + (long)w2.nsteals)) {
            }
            int ps2 = this.plock;
            if ((ps2 & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps2, ps2 += 2)) {
                ps2 = this.acquirePlock();
            }
            int nps = ps2 & Integer.MIN_VALUE | ps2 + 2 & Integer.MAX_VALUE;
            try {
                short idx = w2.poolIndex;
                WorkQueue[] ws2 = this.workQueues;
                if (ws2 != null && idx >= 0 && idx < ws2.length && ws2[idx] == w2) {
                    ws2[idx] = null;
                }
            }
            finally {
                if (!U.compareAndSwapInt(this, PLOCK, ps2, nps)) {
                    this.releasePlock(nps);
                }
            }
        }
        while (!U.compareAndSwapLong(this, CTL, c2 = this.ctl, c2 - 0x1000000000000L & 0xFFFF000000000000L | c2 - 0x100000000L & 0xFFFF00000000L | c2 & 0xFFFFFFFFL)) {
        }
        if (!this.tryTerminate(false, false) && w2 != null && w2.array != null) {
            int e2;
            int u2;
            w2.cancelAll();
            while ((u2 = (int)((c2 = this.ctl) >>> 32)) < 0 && (e2 = (int)c2) >= 0) {
                if (e2 > 0) {
                    WorkQueue v2;
                    int i2;
                    WorkQueue[] ws3 = this.workQueues;
                    if (this.workQueues == null || (i2 = e2 & 0xFFFF) >= ws3.length || (v2 = ws3[i2]) == null) break;
                    long nc2 = (long)(v2.nextWait & Integer.MAX_VALUE) | (long)(u2 + 65536) << 32;
                    if (v2.eventCount != (e2 | Integer.MIN_VALUE)) break;
                    if (!U.compareAndSwapLong(this, CTL, c2, nc2)) continue;
                    v2.eventCount = e2 + 65536 & Integer.MAX_VALUE;
                    Thread p2 = v2.parker;
                    if (p2 == null) break;
                    U.unpark(p2);
                    break;
                }
                if ((short)u2 >= 0) break;
                this.tryAddWorker();
                break;
            }
        }
        if (ex2 == null) {
            ForkJoinTask.helpExpungeStaleExceptions();
        } else {
            ForkJoinTask.rethrow(ex2);
        }
    }

    final void externalPush(ForkJoinTask<?> task) {
        int r2;
        WorkQueue q2;
        int m2;
        Submitter z2 = submitters.get();
        int ps2 = this.plock;
        WorkQueue[] ws2 = this.workQueues;
        if (z2 != null && ps2 > 0 && ws2 != null && (m2 = ws2.length - 1) >= 0 && (q2 = ws2[m2 & (r2 = z2.seed) & 0x7E]) != null && r2 != 0 && U.compareAndSwapInt(q2, QLOCK, 0, 1)) {
            int s2;
            int n2;
            int am2;
            ForkJoinTask<?>[] a2 = q2.array;
            if (q2.array != null && (am2 = a2.length - 1) > (n2 = (s2 = q2.top) - q2.base)) {
                int j2 = ((am2 & s2) << ASHIFT) + ABASE;
                U.putOrderedObject(a2, j2, task);
                q2.top = s2 + 1;
                q2.qlock = 0;
                if (n2 <= 1) {
                    this.signalWork(ws2, q2);
                }
                return;
            }
            q2.qlock = 0;
        }
        this.fullExternalPush(task);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fullExternalPush(ForkJoinTask<?> task) {
        int r2 = 0;
        Submitter z2 = submitters.get();
        while (true) {
            int m2;
            WorkQueue[] ws2;
            int ps2;
            block25: {
                int nps;
                short p2;
                block24: {
                    if (z2 == null) {
                        r2 = this.indexSeed;
                        if (U.compareAndSwapInt(this, INDEXSEED, r2, r2 += 1640531527) && r2 != 0) {
                            z2 = new Submitter(r2);
                            submitters.set(z2);
                        }
                    } else if (r2 == 0) {
                        r2 = z2.seed;
                        r2 ^= r2 << 13;
                        r2 ^= r2 >>> 17;
                        r2 ^= r2 << 5;
                        z2.seed = r2;
                    }
                    if ((ps2 = this.plock) < 0) {
                        throw new RejectedExecutionException();
                    }
                    if (ps2 == 0) break block24;
                    ws2 = this.workQueues;
                    if (this.workQueues != null && (m2 = ws2.length - 1) >= 0) break block25;
                }
                int n2 = (p2 = this.parallelism) > 1 ? p2 - 1 : 1;
                n2 |= n2 >>> 1;
                n2 |= n2 >>> 2;
                n2 |= n2 >>> 4;
                n2 |= n2 >>> 8;
                n2 |= n2 >>> 16;
                n2 = n2 + 1 << 1;
                ws2 = this.workQueues;
                WorkQueue[] nws = this.workQueues == null || ws2.length == 0 ? new WorkQueue[n2] : null;
                ps2 = this.plock;
                if ((ps2 & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps2, ps2 += 2)) {
                    ps2 = this.acquirePlock();
                }
                ws2 = this.workQueues;
                if ((this.workQueues == null || ws2.length == 0) && nws != null) {
                    this.workQueues = nws;
                }
                if (U.compareAndSwapInt(this, PLOCK, ps2, nps = ps2 & Integer.MIN_VALUE | ps2 + 2 & Integer.MAX_VALUE)) continue;
                this.releasePlock(nps);
                continue;
            }
            int k2 = r2 & m2 & 0x7E;
            WorkQueue q2 = ws2[k2];
            if (q2 != null) {
                if (q2.qlock == 0 && U.compareAndSwapInt(q2, QLOCK, 0, 1)) {
                    ForkJoinTask<?>[] a2 = q2.array;
                    int s2 = q2.top;
                    boolean submitted = false;
                    try {
                        if (a2 != null && a2.length > s2 + 1 - q2.base || (a2 = q2.growArray()) != null) {
                            int j2 = ((a2.length - 1 & s2) << ASHIFT) + ABASE;
                            U.putOrderedObject(a2, j2, task);
                            q2.top = s2 + 1;
                            submitted = true;
                        }
                    }
                    finally {
                        q2.qlock = 0;
                    }
                    if (submitted) {
                        this.signalWork(ws2, q2);
                        return;
                    }
                }
                r2 = 0;
                continue;
            }
            ps2 = this.plock;
            if ((ps2 & 2) == 0) {
                int nps;
                q2 = new WorkQueue(this, null, -1, r2);
                q2.poolIndex = (short)k2;
                ps2 = this.plock;
                if ((ps2 & 2) != 0 || !U.compareAndSwapInt(this, PLOCK, ps2, ps2 += 2)) {
                    ps2 = this.acquirePlock();
                }
                ws2 = this.workQueues;
                if (this.workQueues != null && k2 < ws2.length && ws2[k2] == null) {
                    ws2[k2] = q2;
                }
                if (U.compareAndSwapInt(this, PLOCK, ps2, nps = ps2 & Integer.MIN_VALUE | ps2 + 2 & Integer.MAX_VALUE)) continue;
                this.releasePlock(nps);
                continue;
            }
            r2 = 0;
        }
    }

    final void incrementActiveCount() {
        long c2;
        while (!U.compareAndSwapLong(this, CTL, c2 = this.ctl, c2 & 0xFFFFFFFFFFFFL | (c2 & 0xFFFF000000000000L) + 0x1000000000000L)) {
        }
    }

    final void signalWork(WorkQueue[] ws2, WorkQueue q2) {
        long c2;
        int u2;
        while ((u2 = (int)((c2 = this.ctl) >>> 32)) < 0) {
            WorkQueue w2;
            int i2;
            int e2 = (int)c2;
            if (e2 <= 0) {
                if ((short)u2 >= 0) break;
                this.tryAddWorker();
                break;
            }
            if (ws2 == null || ws2.length <= (i2 = e2 & 0xFFFF) || (w2 = ws2[i2]) == null) break;
            long nc2 = (long)(w2.nextWait & Integer.MAX_VALUE) | (long)(u2 + 65536) << 32;
            int ne2 = e2 + 65536 & Integer.MAX_VALUE;
            if (w2.eventCount == (e2 | Integer.MIN_VALUE) && U.compareAndSwapLong(this, CTL, c2, nc2)) {
                w2.eventCount = ne2;
                Thread p2 = w2.parker;
                if (p2 == null) break;
                U.unpark(p2);
                break;
            }
            if (q2 == null || q2.base < q2.top) continue;
            break;
        }
    }

    final void runWorker(WorkQueue w2) {
        w2.growArray();
        int r2 = w2.hint;
        while (this.scan(w2, r2) == 0) {
            r2 ^= r2 << 13;
            r2 ^= r2 >>> 17;
            r2 ^= r2 << 5;
        }
    }

    private final int scan(WorkQueue w2, int r2) {
        block6: {
            int m2;
            long c2 = this.ctl;
            WorkQueue[] ws2 = this.workQueues;
            if (this.workQueues == null || (m2 = ws2.length - 1) < 0 || w2 == null) break block6;
            int j2 = m2 + m2 + 1;
            int ec2 = w2.eventCount;
            do {
                int b2;
                WorkQueue q2;
                if ((q2 = ws2[r2 - j2 & m2]) == null || (b2 = q2.base) - q2.top >= 0) continue;
                ForkJoinTask<?>[] a2 = q2.array;
                if (q2.array == null) continue;
                long i2 = ((a2.length - 1 & b2) << ASHIFT) + ABASE;
                ForkJoinTask t2 = (ForkJoinTask)U.getObjectVolatile(a2, i2);
                if (t2 == null) break block6;
                if (ec2 < 0) {
                    this.helpRelease(c2, ws2, w2, q2, b2);
                    break block6;
                }
                if (q2.base != b2 || !U.compareAndSwapObject(a2, i2, t2, null)) break block6;
                U.putOrderedInt(q2, QBASE, b2 + 1);
                if (b2 + 1 - q2.top < 0) {
                    this.signalWork(ws2, q2);
                }
                w2.runTask(t2);
                break block6;
            } while (--j2 >= 0);
            int e2 = (int)c2;
            if ((ec2 | e2) < 0) {
                return this.awaitWork(w2, c2, ec2);
            }
            if (this.ctl == c2) {
                long nc2 = (long)ec2 | c2 - 0x1000000000000L & 0xFFFFFFFF00000000L;
                w2.nextWait = e2;
                w2.eventCount = ec2 | Integer.MIN_VALUE;
                if (!U.compareAndSwapLong(this, CTL, c2, nc2)) {
                    w2.eventCount = ec2;
                }
            }
        }
        return 0;
    }

    private final int awaitWork(WorkQueue w2, long c2, int ec2) {
        int stat = w2.qlock;
        if (stat >= 0 && w2.eventCount == ec2 && this.ctl == c2 && !Thread.interrupted()) {
            int e2 = (int)c2;
            int u2 = (int)(c2 >>> 32);
            int d2 = (u2 >> 16) + this.parallelism;
            if (e2 < 0 || d2 <= 0 && this.tryTerminate(false, false)) {
                w2.qlock = -1;
                stat = -1;
            } else {
                int ns2 = w2.nsteals;
                if (ns2 != 0) {
                    long sc2;
                    w2.nsteals = 0;
                    while (!U.compareAndSwapLong(this, STEALCOUNT, sc2 = this.stealCount, sc2 + (long)ns2)) {
                    }
                } else {
                    long deadline;
                    long parkTime;
                    long pc2;
                    long l2 = pc2 = d2 > 0 || ec2 != (e2 | Integer.MIN_VALUE) ? 0L : (long)(w2.nextWait & Integer.MAX_VALUE) | (long)(u2 + 65536) << 32;
                    if (pc2 != 0L) {
                        short dc2 = -((short)(c2 >>> 32));
                        parkTime = dc2 < 0 ? 200000000L : (long)(dc2 + 1) * 2000000000L;
                        deadline = System.nanoTime() + parkTime - 2000000L;
                    } else {
                        deadline = 0L;
                        parkTime = 0L;
                    }
                    if (w2.eventCount == ec2 && this.ctl == c2) {
                        Thread wt2 = Thread.currentThread();
                        U.putObject(wt2, PARKBLOCKER, this);
                        w2.parker = wt2;
                        if (w2.eventCount == ec2 && this.ctl == c2) {
                            U.park(false, parkTime);
                        }
                        w2.parker = null;
                        U.putObject(wt2, PARKBLOCKER, null);
                        if (parkTime != 0L && this.ctl == c2 && deadline - System.nanoTime() <= 0L && U.compareAndSwapLong(this, CTL, c2, pc2)) {
                            w2.qlock = -1;
                            stat = -1;
                        }
                    }
                }
            }
        }
        return stat;
    }

    private final void helpRelease(long c2, WorkQueue[] ws2, WorkQueue w2, WorkQueue q2, int b2) {
        WorkQueue v2;
        int i2;
        int e2;
        if (w2 != null && w2.eventCount < 0 && (e2 = (int)c2) > 0 && ws2 != null && ws2.length > (i2 = e2 & 0xFFFF) && (v2 = ws2[i2]) != null && this.ctl == c2) {
            long nc2 = (long)(v2.nextWait & Integer.MAX_VALUE) | (long)((int)(c2 >>> 32) + 65536) << 32;
            int ne2 = e2 + 65536 & Integer.MAX_VALUE;
            if (q2 != null && q2.base == b2 && w2.eventCount < 0 && v2.eventCount == (e2 | Integer.MIN_VALUE) && U.compareAndSwapLong(this, CTL, c2, nc2)) {
                v2.eventCount = ne2;
                Thread p2 = v2.parker;
                if (p2 != null) {
                    U.unpark(p2);
                }
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    private int tryHelpStealer(WorkQueue joiner, ForkJoinTask<?> task) {
        stat = 0;
        steps = 0;
        if (task != null && joiner != null && joiner.base - joiner.top >= 0) {
            block0: while (true) {
                subtask = task;
                j = joiner;
                while (true) {
                    block9: {
                        block8: {
                            if ((s = task.status) < 0) {
                                stat = s;
                                break block0;
                            }
                            ws = this.workQueues;
                            if (this.workQueues == null || (m = ws.length - 1) <= 0) break block0;
                            h = (j.hint | 1) & m;
                            v = ws[h];
                            if (v == null || v.currentSteal != subtask) {
                                origin = h;
                                do {
                                    if (((h = h + 2 & m) & 15) == 1 && (subtask.status < 0 || j.currentJoin != subtask)) continue block0;
                                    v = ws[h];
                                    if (v == null || v.currentSteal != subtask) continue;
                                    j.hint = h;
                                    break block8;
                                } while (h != origin);
                                break block0;
                            }
                        }
                        while (true) {
                            if (subtask.status < 0) continue block0;
                            b = v.base;
                            if (b - v.top >= 0) break block9;
                            a = v.array;
                            if (v.array == null) break block9;
                            i = ((a.length - 1 & b) << ForkJoinPool.ASHIFT) + ForkJoinPool.ABASE;
                            t = (ForkJoinTask<?>)ForkJoinPool.U.getObjectVolatile(a, i);
                            if (subtask.status < 0 || j.currentJoin != subtask || v.currentSteal != subtask) continue block0;
                            stat = 1;
                            if (v.base != b) continue;
                            if (t == null) break block0;
                            if (ForkJoinPool.U.compareAndSwapObject(a, i, t, null)) break;
                        }
                        ForkJoinPool.U.putOrderedInt(v, ForkJoinPool.QBASE, b + 1);
                        ps = joiner.currentSteal;
                        jt = joiner.top;
                        do {
                            joiner.currentSteal = t;
                            t.doExec();
                        } while (task.status >= 0 && joiner.top != jt && (t = joiner.pop()) != null);
                        joiner.currentSteal = ps;
                        break block0;
                    }
                    next = v.currentJoin;
                    if (subtask.status >= 0 && j.currentJoin == subtask && v.currentSteal == subtask) ** break;
                    continue block0;
                    if (next == null || ++steps == 64) break block0;
                    subtask = next;
                    j = v;
                }
                break;
            }
        }
        return stat;
    }

    private int helpComplete(WorkQueue joiner, CountedCompleter<?> task) {
        int m2;
        int s2 = 0;
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null && (m2 = ws2.length - 1) >= 0 && joiner != null && task != null) {
            int j2 = joiner.poolIndex;
            int scans = m2 + m2 + 1;
            long c2 = 0L;
            int k2 = scans;
            while ((s2 = task.status) >= 0) {
                if (joiner.internalPopAndExecCC(task)) {
                    k2 = scans;
                } else {
                    s2 = task.status;
                    if (s2 < 0) break;
                    WorkQueue q2 = ws2[j2 & m2];
                    if (q2 != null && q2.pollAndExecCC(task)) {
                        k2 = scans;
                    } else if (--k2 < 0) {
                        if (c2 == (c2 = this.ctl)) break;
                        k2 = scans;
                    }
                }
                j2 += 2;
            }
        }
        return s2;
    }

    final boolean tryCompensate(long c2) {
        int m2;
        WorkQueue[] ws2 = this.workQueues;
        short pc2 = this.parallelism;
        int e2 = (int)c2;
        if (ws2 != null && (m2 = ws2.length - 1) >= 0 && e2 >= 0 && this.ctl == c2) {
            WorkQueue w2 = ws2[e2 & m2];
            if (e2 != 0 && w2 != null) {
                long nc2 = (long)(w2.nextWait & Integer.MAX_VALUE) | c2 & 0xFFFFFFFF00000000L;
                int ne2 = e2 + 65536 & Integer.MAX_VALUE;
                if (w2.eventCount == (e2 | Integer.MIN_VALUE) && U.compareAndSwapLong(this, CTL, c2, nc2)) {
                    w2.eventCount = ne2;
                    Thread p2 = w2.parker;
                    if (p2 != null) {
                        U.unpark(p2);
                    }
                    return true;
                }
            } else {
                long nc3;
                short tc2 = (short)(c2 >>> 32);
                if (tc2 >= 0 && (int)(c2 >> 48) + pc2 > 1) {
                    long nc4 = c2 - 0x1000000000000L & 0xFFFF000000000000L | c2 & 0xFFFFFFFFFFFFL;
                    if (U.compareAndSwapLong(this, CTL, c2, nc4)) {
                        return true;
                    }
                } else if (tc2 + pc2 < Short.MAX_VALUE && U.compareAndSwapLong(this, CTL, c2, nc3 = c2 + 0x100000000L & 0xFFFF00000000L | c2 & 0xFFFF0000FFFFFFFFL)) {
                    Throwable ex2 = null;
                    ForkJoinWorkerThread wt2 = null;
                    try {
                        ForkJoinWorkerThreadFactory fac = this.factory;
                        if (fac != null && (wt2 = fac.newThread(this)) != null) {
                            wt2.start();
                            return true;
                        }
                    }
                    catch (Throwable rex) {
                        ex2 = rex;
                    }
                    this.deregisterWorker(wt2, ex2);
                }
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final int awaitJoin(WorkQueue joiner, ForkJoinTask<?> task) {
        int s2 = 0;
        if (task != null && (s2 = task.status) >= 0 && joiner != null) {
            ForkJoinTask<?> prevJoin = joiner.currentJoin;
            joiner.currentJoin = task;
            while (joiner.tryRemoveAndExec(task) && (s2 = task.status) >= 0) {
            }
            if (s2 >= 0 && task instanceof CountedCompleter) {
                s2 = this.helpComplete(joiner, (CountedCompleter)task);
            }
            long cc2 = 0L;
            while (s2 >= 0 && (s2 = task.status) >= 0) {
                long c2;
                s2 = this.tryHelpStealer(joiner, task);
                if (s2 != 0 || (s2 = task.status) < 0) continue;
                if (!this.tryCompensate(cc2)) {
                    cc2 = this.ctl;
                    continue;
                }
                if (task.trySetSignal() && (s2 = task.status) >= 0) {
                    ForkJoinTask<?> forkJoinTask = task;
                    synchronized (forkJoinTask) {
                        if (task.status >= 0) {
                            try {
                                task.wait();
                            }
                            catch (InterruptedException ie2) {}
                        } else {
                            task.notifyAll();
                        }
                    }
                }
                while (!U.compareAndSwapLong(this, CTL, c2 = this.ctl, c2 & 0xFFFFFFFFFFFFL | (c2 & 0xFFFF000000000000L) + 0x1000000000000L)) {
                }
            }
            joiner.currentJoin = prevJoin;
        }
        return s2;
    }

    final void helpJoinOnce(WorkQueue joiner, ForkJoinTask<?> task) {
        int s2;
        if (joiner != null && task != null && (s2 = task.status) >= 0) {
            ForkJoinTask<?> prevJoin = joiner.currentJoin;
            joiner.currentJoin = task;
            while (joiner.tryRemoveAndExec(task) && (s2 = task.status) >= 0) {
            }
            if (s2 >= 0) {
                if (task instanceof CountedCompleter) {
                    this.helpComplete(joiner, (CountedCompleter)task);
                }
                while (task.status >= 0 && this.tryHelpStealer(joiner, task) > 0) {
                }
            }
            joiner.currentJoin = prevJoin;
        }
    }

    private WorkQueue findNonEmptyStealQueue() {
        int ps2;
        int r2 = ThreadLocalRandom.current().nextInt();
        do {
            int m2;
            ps2 = this.plock;
            WorkQueue[] ws2 = this.workQueues;
            if (this.workQueues == null || (m2 = ws2.length - 1) < 0) continue;
            for (int j2 = m2 + 1 << 2; j2 >= 0; --j2) {
                WorkQueue q2 = ws2[(r2 - j2 << 1 | 1) & m2];
                if (q2 == null || q2.base - q2.top >= 0) continue;
                return q2;
            }
        } while (this.plock != ps2);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    final void helpQuiescePool(WorkQueue w2) {
        ForkJoinTask<?> ps2 = w2.currentSteal;
        boolean active = true;
        while (true) {
            long c2;
            ForkJoinTask<?> t2;
            if ((t2 = w2.nextLocalTask()) != null) {
                t2.doExec();
                continue;
            }
            WorkQueue q2 = this.findNonEmptyStealQueue();
            if (q2 != null) {
                int b2;
                if (!active) {
                    active = true;
                    while (!U.compareAndSwapLong(this, CTL, c2 = this.ctl, c2 & 0xFFFFFFFFFFFFL | (c2 & 0xFFFF000000000000L) + 0x1000000000000L)) {
                    }
                }
                if ((b2 = q2.base) - q2.top >= 0 || (t2 = q2.pollAt(b2)) == null) continue;
                w2.currentSteal = t2;
                w2.currentSteal.doExec();
                w2.currentSteal = ps2;
                continue;
            }
            if (active) {
                c2 = this.ctl;
                long nc2 = c2 & 0xFFFFFFFFFFFFL | (c2 & 0xFFFF000000000000L) - 0x1000000000000L;
                if ((int)(nc2 >> 48) + this.parallelism == 0) return;
                if (!U.compareAndSwapLong(this, CTL, c2, nc2)) continue;
                active = false;
                continue;
            }
            c2 = this.ctl;
            if ((int)(c2 >> 48) + this.parallelism <= 0 && U.compareAndSwapLong(this, CTL, c2, c2 & 0xFFFFFFFFFFFFL | (c2 & 0xFFFF000000000000L) + 0x1000000000000L)) return;
        }
    }

    final ForkJoinTask<?> nextTaskFor(WorkQueue w2) {
        ForkJoinTask<?> t2;
        WorkQueue q2;
        int b2;
        do {
            if ((t2 = w2.nextLocalTask()) != null) {
                return t2;
            }
            q2 = this.findNonEmptyStealQueue();
            if (q2 != null) continue;
            return null;
        } while ((b2 = q2.base) - q2.top >= 0 || (t2 = q2.pollAt(b2)) == null);
        return t2;
    }

    static int getSurplusQueuedTaskCount() {
        Thread t2 = Thread.currentThread();
        if (t2 instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt2 = (ForkJoinWorkerThread)t2;
            ForkJoinPool pool = wt2.pool;
            int p2 = pool.parallelism;
            WorkQueue q2 = wt2.workQueue;
            int n2 = q2.top - q2.base;
            int a2 = (int)(pool.ctl >> 48) + p2;
            return n2 - (a2 > (p2 >>>= 1) ? 0 : (a2 > (p2 >>>= 1) ? 1 : (a2 > (p2 >>>= 1) ? 2 : (a2 > (p2 >>>= 1) ? 4 : 8))));
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    private boolean tryTerminate(boolean now, boolean enable) {
        if (this == ForkJoinPool.common) {
            return false;
        }
        ps = this.plock;
        if (ps >= 0) {
            if (!enable) {
                return false;
            }
            if ((ps & 2) != 0 || !ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, ps, ps += 2)) {
                ps = this.acquirePlock();
            }
            if (!ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, ps, nps = ps + 2 & 0x7FFFFFFF | -2147483648)) {
                this.releasePlock(nps);
            }
        }
        block5: while (true) {
            if (((c = this.ctl) & 0x80000000L) != 0L) {
                if ((short)(c >>> 32) + this.parallelism <= 0) {
                    var6_6 = this;
                    synchronized (var6_6) {
                        this.notifyAll();
                    }
                }
                return true;
            }
            if (!now) {
                if ((int)(c >> 48) + this.parallelism > 0) {
                    return false;
                }
                ws = this.workQueues;
                if (this.workQueues != null) {
                    for (i = 0; i < ws.length; ++i) {
                        w = ws[i];
                        if (w == null || w.isEmpty() && ((i & 1) == 0 || w.eventCount < 0)) continue;
                        this.signalWork(ws, w);
                        return false;
                    }
                }
            }
            if (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, c, c | 0x80000000L)) continue;
            pass = 0;
            while (true) {
                if (pass < 3) ** break;
                continue block5;
                ws = this.workQueues;
                if (this.workQueues != null) {
                    n = ws.length;
                    for (i = 0; i < n; ++i) {
                        w = ws[i];
                        if (w == null) continue;
                        w.qlock = -1;
                        if (pass <= 0) continue;
                        w.cancelAll();
                        if (pass <= 1 || (wt = w.owner) == null) continue;
                        if (!wt.isInterrupted()) {
                            try {
                                wt.interrupt();
                            }
                            catch (Throwable ignore) {
                                // empty catch block
                            }
                        }
                        ForkJoinPool.U.unpark(wt);
                    }
                    while ((e = (int)(cc = this.ctl) & 0x7FFFFFFF) != 0 && (i = e & 65535) < n && i >= 0 && (w = ws[i]) != null) {
                        nc = (long)(w.nextWait & 0x7FFFFFFF) | cc + 0x1000000000000L & -281474976710656L | cc & 0xFFFF80000000L;
                        if (w.eventCount != (e | -2147483648) || !ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, cc, nc)) continue;
                        w.eventCount = e + 65536 & 0x7FFFFFFF;
                        w.qlock = -1;
                        p = w.parker;
                        if (p == null) continue;
                        ForkJoinPool.U.unpark(p);
                    }
                }
                ++pass;
            }
            break;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static WorkQueue commonSubmitterQueue() {
        Submitter z2 = submitters.get();
        if (z2 == null) return null;
        ForkJoinPool p2 = common;
        if (p2 == null) return null;
        WorkQueue[] ws2 = p2.workQueues;
        if (p2.workQueues == null) return null;
        int m2 = ws2.length - 1;
        if (m2 < 0) return null;
        WorkQueue workQueue = ws2[m2 & z2.seed & 0x7E];
        return workQueue;
    }

    final boolean tryExternalUnpush(ForkJoinTask<?> task) {
        int s2;
        WorkQueue joiner;
        int m2;
        Submitter z2 = submitters.get();
        WorkQueue[] ws2 = this.workQueues;
        boolean popped = false;
        if (z2 != null && ws2 != null && (m2 = ws2.length - 1) >= 0 && (joiner = ws2[z2.seed & m2 & 0x7E]) != null && joiner.base != (s2 = joiner.top)) {
            long j2;
            ForkJoinTask<?>[] a2 = joiner.array;
            if (joiner.array != null && U.getObject(a2, j2 = (long)(((a2.length - 1 & s2 - 1) << ASHIFT) + ABASE)) == task && U.compareAndSwapInt(joiner, QLOCK, 0, 1)) {
                if (joiner.top == s2 && joiner.array == a2 && U.compareAndSwapObject(a2, j2, task, null)) {
                    joiner.top = s2 - 1;
                    popped = true;
                }
                joiner.qlock = 0;
            }
        }
        return popped;
    }

    final int externalHelpComplete(CountedCompleter<?> task) {
        int j2;
        WorkQueue joiner;
        int m2;
        Submitter z2 = submitters.get();
        WorkQueue[] ws2 = this.workQueues;
        int s2 = 0;
        if (z2 != null && ws2 != null && (m2 = ws2.length - 1) >= 0 && (joiner = ws2[(j2 = z2.seed) & m2 & 0x7E]) != null && task != null) {
            int scans = m2 + m2 + 1;
            long c2 = 0L;
            j2 |= 1;
            int k2 = scans;
            while ((s2 = task.status) >= 0) {
                if (joiner.externalPopAndExecCC(task)) {
                    k2 = scans;
                } else {
                    s2 = task.status;
                    if (s2 < 0) break;
                    WorkQueue q2 = ws2[j2 & m2];
                    if (q2 != null && q2.pollAndExecCC(task)) {
                        k2 = scans;
                    } else if (--k2 < 0) {
                        if (c2 == (c2 = this.ctl)) break;
                        k2 = scans;
                    }
                }
                j2 += 2;
            }
        }
        return s2;
    }

    public ForkJoinPool() {
        this(Math.min(Short.MAX_VALUE, Runtime.getRuntime().availableProcessors()), defaultForkJoinWorkerThreadFactory, null, false);
    }

    public ForkJoinPool(int parallelism) {
        this(parallelism, defaultForkJoinWorkerThreadFactory, null, false);
    }

    public ForkJoinPool(int parallelism, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler, boolean asyncMode) {
        this(ForkJoinPool.checkParallelism(parallelism), ForkJoinPool.checkFactory(factory), handler, asyncMode ? 1 : 0, "ForkJoinPool-" + ForkJoinPool.nextPoolId() + "-worker-");
        ForkJoinPool.checkPermission();
    }

    private static int checkParallelism(int parallelism) {
        if (parallelism <= 0 || parallelism > Short.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        return parallelism;
    }

    private static ForkJoinWorkerThreadFactory checkFactory(ForkJoinWorkerThreadFactory factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        return factory;
    }

    private ForkJoinPool(int parallelism, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler, int mode, String workerNamePrefix) {
        this.workerNamePrefix = workerNamePrefix;
        this.factory = factory;
        this.ueh = handler;
        this.mode = (short)mode;
        this.parallelism = (short)parallelism;
        long np2 = -parallelism;
        this.ctl = np2 << 48 & 0xFFFF000000000000L | np2 << 32 & 0xFFFF00000000L;
    }

    public static ForkJoinPool commonPool() {
        return common;
    }

    public <T> T invoke(ForkJoinTask<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        this.externalPush(task);
        return task.join();
    }

    public void execute(ForkJoinTask<?> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        this.externalPush(task);
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        ForkJoinTask job = task instanceof ForkJoinTask ? (ForkJoinTask)((Object)task) : new ForkJoinTask.RunnableExecuteAction(task);
        this.externalPush(job);
    }

    public <T> ForkJoinTask<T> submit(ForkJoinTask<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        this.externalPush(task);
        return task;
    }

    public <T> ForkJoinTask<T> submit(Callable<T> task) {
        ForkJoinTask.AdaptedCallable<T> job = new ForkJoinTask.AdaptedCallable<T>(task);
        this.externalPush(job);
        return job;
    }

    public <T> ForkJoinTask<T> submit(Runnable task, T result) {
        ForkJoinTask.AdaptedRunnable<T> job = new ForkJoinTask.AdaptedRunnable<T>(task, result);
        this.externalPush(job);
        return job;
    }

    public ForkJoinTask<?> submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        ForkJoinTask job = task instanceof ForkJoinTask ? (ForkJoinTask)((Object)task) : new ForkJoinTask.AdaptedRunnableAction(task);
        this.externalPush(job);
        return job;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
        ArrayList<Future<T>> arrayList;
        block7: {
            int size;
            ArrayList<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
            boolean done = false;
            try {
                for (Callable<T> t2 : tasks) {
                    ForkJoinTask.AdaptedCallable<T> f2 = new ForkJoinTask.AdaptedCallable<T>(t2);
                    futures.add(f2);
                    this.externalPush(f2);
                }
                int size2 = futures.size();
                for (int i2 = 0; i2 < size2; ++i2) {
                    ((ForkJoinTask)futures.get(i2)).quietlyJoin();
                }
                done = true;
                arrayList = futures;
                if (done) break block7;
                size = futures.size();
            }
            catch (Throwable throwable) {
                if (!done) {
                    int size3 = futures.size();
                    for (int i3 = 0; i3 < size3; ++i3) {
                        ((Future)futures.get(i3)).cancel(false);
                    }
                }
                throw throwable;
            }
            for (int i4 = 0; i4 < size; ++i4) {
                futures.get(i4).cancel(false);
            }
        }
        return arrayList;
    }

    public ForkJoinWorkerThreadFactory getFactory() {
        return this.factory;
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.ueh;
    }

    public int getParallelism() {
        short par = this.parallelism;
        return par > 0 ? par : (short)1;
    }

    public static int getCommonPoolParallelism() {
        return commonParallelism;
    }

    public int getPoolSize() {
        return this.parallelism + (short)(this.ctl >>> 32);
    }

    public boolean getAsyncMode() {
        return this.mode == 1;
    }

    public int getRunningThreadCount() {
        int rc2 = 0;
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null) {
            for (int i2 = 1; i2 < ws2.length; i2 += 2) {
                WorkQueue w2 = ws2[i2];
                if (w2 == null || !w2.isApparentlyUnblocked()) continue;
                ++rc2;
            }
        }
        return rc2;
    }

    public int getActiveThreadCount() {
        int r2 = this.parallelism + (int)(this.ctl >> 48);
        return r2 <= 0 ? 0 : r2;
    }

    public boolean isQuiescent() {
        return this.parallelism + (int)(this.ctl >> 48) <= 0;
    }

    public long getStealCount() {
        long count = this.stealCount;
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null) {
            for (int i2 = 1; i2 < ws2.length; i2 += 2) {
                WorkQueue w2 = ws2[i2];
                if (w2 == null) continue;
                count += (long)w2.nsteals;
            }
        }
        return count;
    }

    public long getQueuedTaskCount() {
        long count = 0L;
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null) {
            for (int i2 = 1; i2 < ws2.length; i2 += 2) {
                WorkQueue w2 = ws2[i2];
                if (w2 == null) continue;
                count += (long)w2.queueSize();
            }
        }
        return count;
    }

    public int getQueuedSubmissionCount() {
        int count = 0;
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null) {
            for (int i2 = 0; i2 < ws2.length; i2 += 2) {
                WorkQueue w2 = ws2[i2];
                if (w2 == null) continue;
                count += w2.queueSize();
            }
        }
        return count;
    }

    public boolean hasQueuedSubmissions() {
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null) {
            for (int i2 = 0; i2 < ws2.length; i2 += 2) {
                WorkQueue w2 = ws2[i2];
                if (w2 == null || w2.isEmpty()) continue;
                return true;
            }
        }
        return false;
    }

    protected ForkJoinTask<?> pollSubmission() {
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null) {
            for (int i2 = 0; i2 < ws2.length; i2 += 2) {
                ForkJoinTask<?> t2;
                WorkQueue w2 = ws2[i2];
                if (w2 == null || (t2 = w2.poll()) == null) continue;
                return t2;
            }
        }
        return null;
    }

    protected int drainTasksTo(Collection<? super ForkJoinTask<?>> c2) {
        int count = 0;
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null) {
            for (int i2 = 0; i2 < ws2.length; ++i2) {
                ForkJoinTask<?> t2;
                WorkQueue w2 = ws2[i2];
                if (w2 == null) continue;
                while ((t2 = w2.poll()) != null) {
                    c2.add(t2);
                    ++count;
                }
            }
        }
        return count;
    }

    public String toString() {
        long qt2 = 0L;
        long qs2 = 0L;
        int rc2 = 0;
        long st2 = this.stealCount;
        long c2 = this.ctl;
        WorkQueue[] ws2 = this.workQueues;
        if (this.workQueues != null) {
            for (int i2 = 0; i2 < ws2.length; ++i2) {
                WorkQueue w2 = ws2[i2];
                if (w2 == null) continue;
                int size = w2.queueSize();
                if ((i2 & 1) == 0) {
                    qs2 += (long)size;
                    continue;
                }
                qt2 += (long)size;
                st2 += (long)w2.nsteals;
                if (!w2.isApparentlyUnblocked()) continue;
                ++rc2;
            }
        }
        short pc2 = this.parallelism;
        int tc2 = pc2 + (short)(c2 >>> 32);
        int ac2 = pc2 + (int)(c2 >> 48);
        if (ac2 < 0) {
            ac2 = 0;
        }
        String level = (c2 & 0x80000000L) != 0L ? (tc2 == 0 ? "Terminated" : "Terminating") : (this.plock < 0 ? "Shutting down" : "Running");
        return super.toString() + "[" + level + ", parallelism = " + pc2 + ", size = " + tc2 + ", active = " + ac2 + ", running = " + rc2 + ", steals = " + st2 + ", tasks = " + qt2 + ", submissions = " + qs2 + "]";
    }

    @Override
    public void shutdown() {
        ForkJoinPool.checkPermission();
        this.tryTerminate(false, true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        ForkJoinPool.checkPermission();
        this.tryTerminate(true, true);
        return Collections.emptyList();
    }

    @Override
    public boolean isTerminated() {
        long c2 = this.ctl;
        return (c2 & 0x80000000L) != 0L && (short)(c2 >>> 32) + this.parallelism <= 0;
    }

    public boolean isTerminating() {
        long c2 = this.ctl;
        return (c2 & 0x80000000L) != 0L && (short)(c2 >>> 32) + this.parallelism > 0;
    }

    @Override
    public boolean isShutdown() {
        return this.plock < 0;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        if (this == common) {
            this.awaitQuiescence(timeout, unit);
            return false;
        }
        long nanos = unit.toNanos(timeout);
        if (this.isTerminated()) {
            return true;
        }
        if (nanos <= 0L) {
            return false;
        }
        long deadline = System.nanoTime() + nanos;
        ForkJoinPool forkJoinPool = this;
        synchronized (forkJoinPool) {
            while (true) {
                if (this.isTerminated()) {
                    return true;
                }
                if (nanos <= 0L) {
                    return false;
                }
                long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
                this.wait(millis > 0L ? millis : 1L);
                nanos = deadline - System.nanoTime();
            }
        }
    }

    public boolean awaitQuiescence(long timeout, TimeUnit unit) {
        long nanos = unit.toNanos(timeout);
        Thread thread = Thread.currentThread();
        if (thread instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt2 = (ForkJoinWorkerThread)thread;
            if (wt2.pool == this) {
                this.helpQuiescePool(wt2.workQueue);
                return true;
            }
        }
        long startTime = System.nanoTime();
        int r2 = 0;
        boolean found = true;
        block0: while (!this.isQuiescent()) {
            int m2;
            WorkQueue[] ws2 = this.workQueues;
            if (this.workQueues == null || (m2 = ws2.length - 1) < 0) break;
            if (!found) {
                if (System.nanoTime() - startTime > nanos) {
                    return false;
                }
                Thread.yield();
            }
            found = false;
            for (int j2 = m2 + 1 << 2; j2 >= 0; --j2) {
                int b2;
                WorkQueue q2;
                if ((q2 = ws2[r2++ & m2]) == null || (b2 = q2.base) - q2.top >= 0) continue;
                found = true;
                ForkJoinTask<?> t2 = q2.pollAt(b2);
                if (t2 == null) continue block0;
                t2.doExec();
                continue block0;
            }
        }
        return true;
    }

    static void quiesceCommonPool() {
        common.awaitQuiescence(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void managedBlock(ManagedBlocker blocker) throws InterruptedException {
        Thread t2 = Thread.currentThread();
        if (t2 instanceof ForkJoinWorkerThread) {
            ForkJoinPool p2 = ((ForkJoinWorkerThread)t2).pool;
            while (!blocker.isReleasable()) {
                if (!p2.tryCompensate(p2.ctl)) continue;
                try {
                    while (!blocker.isReleasable() && !blocker.block()) {
                    }
                    break;
                }
                finally {
                    p2.incrementActiveCount();
                }
            }
        } else {
            while (!blocker.isReleasable() && !blocker.block()) {
            }
        }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ForkJoinTask.AdaptedRunnable<T>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ForkJoinTask.AdaptedCallable<T>(callable);
    }

    private static ForkJoinPool makeCommonPool() {
        int parallelism = -1;
        ForkJoinWorkerThreadFactory factory = defaultForkJoinWorkerThreadFactory;
        Thread.UncaughtExceptionHandler handler = null;
        try {
            String pp2 = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
            String fp2 = System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
            String hp2 = System.getProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
            if (pp2 != null) {
                parallelism = Integer.parseInt(pp2);
            }
            if (fp2 != null) {
                factory = (ForkJoinWorkerThreadFactory)ClassLoader.getSystemClassLoader().loadClass(fp2).newInstance();
            }
            if (hp2 != null) {
                handler = (Thread.UncaughtExceptionHandler)ClassLoader.getSystemClassLoader().loadClass(hp2).newInstance();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (parallelism < 0 && (parallelism = Runtime.getRuntime().availableProcessors() - 1) < 0) {
            parallelism = 0;
        }
        if (parallelism > Short.MAX_VALUE) {
            parallelism = Short.MAX_VALUE;
        }
        return new ForkJoinPool(parallelism, factory, handler, 0, "ForkJoinPool.commonPool-worker-");
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (SecurityException tryReflectionInstead) {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                    @Override
                    public Unsafe run() throws Exception {
                        Class<Unsafe> k2 = Unsafe.class;
                        for (Field f2 : k2.getDeclaredFields()) {
                            f2.setAccessible(true);
                            Object x2 = f2.get(null);
                            if (!k2.isInstance(x2)) continue;
                            return (Unsafe)k2.cast(x2);
                        }
                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            }
            catch (PrivilegedActionException e2) {
                throw new RuntimeException("Could not initialize intrinsics", e2.getCause());
            }
        }
    }

    static {
        try {
            U = ForkJoinPool.getUnsafe();
            Class<ForkJoinPool> k2 = ForkJoinPool.class;
            CTL = U.objectFieldOffset(k2.getDeclaredField("ctl"));
            STEALCOUNT = U.objectFieldOffset(k2.getDeclaredField("stealCount"));
            PLOCK = U.objectFieldOffset(k2.getDeclaredField("plock"));
            INDEXSEED = U.objectFieldOffset(k2.getDeclaredField("indexSeed"));
            Class<Thread> tk2 = Thread.class;
            PARKBLOCKER = U.objectFieldOffset(tk2.getDeclaredField("parkBlocker"));
            Class<WorkQueue> wk = WorkQueue.class;
            QBASE = U.objectFieldOffset(wk.getDeclaredField("base"));
            QLOCK = U.objectFieldOffset(wk.getDeclaredField("qlock"));
            Class<ForkJoinTask[]> ak2 = ForkJoinTask[].class;
            ABASE = U.arrayBaseOffset(ak2);
            int scale = U.arrayIndexScale(ak2);
            if ((scale & scale - 1) != 0) {
                throw new Error("data type scale not a power of two");
            }
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        }
        catch (Exception e2) {
            throw new Error(e2);
        }
        submitters = new ThreadLocal();
        defaultForkJoinWorkerThreadFactory = new DefaultForkJoinWorkerThreadFactory();
        modifyThreadPermission = new RuntimePermission("modifyThread");
        common = AccessController.doPrivileged(new PrivilegedAction<ForkJoinPool>(){

            @Override
            public ForkJoinPool run() {
                return ForkJoinPool.makeCommonPool();
            }
        });
        short par = ForkJoinPool.common.parallelism;
        commonParallelism = par > 0 ? par : (short)1;
    }

    public static interface ManagedBlocker {
        public boolean block() throws InterruptedException;

        public boolean isReleasable();
    }

    static final class Submitter {
        int seed;

        Submitter(int s2) {
            this.seed = s2;
        }
    }

    static final class WorkQueue {
        static final int INITIAL_QUEUE_CAPACITY = 8192;
        static final int MAXIMUM_QUEUE_CAPACITY = 0x4000000;
        volatile long pad00;
        volatile long pad01;
        volatile long pad02;
        volatile long pad03;
        volatile long pad04;
        volatile long pad05;
        volatile long pad06;
        volatile int eventCount;
        int nextWait;
        int nsteals;
        int hint;
        short poolIndex;
        final short mode;
        volatile int qlock;
        volatile int base;
        int top;
        ForkJoinTask<?>[] array;
        final ForkJoinPool pool;
        final ForkJoinWorkerThread owner;
        volatile Thread parker;
        volatile ForkJoinTask<?> currentJoin;
        ForkJoinTask<?> currentSteal;
        volatile Object pad10;
        volatile Object pad11;
        volatile Object pad12;
        volatile Object pad13;
        volatile Object pad14;
        volatile Object pad15;
        volatile Object pad16;
        volatile Object pad17;
        volatile Object pad18;
        volatile Object pad19;
        volatile Object pad1a;
        volatile Object pad1b;
        volatile Object pad1c;
        volatile Object pad1d;
        private static final Unsafe U;
        private static final long QBASE;
        private static final long QLOCK;
        private static final int ABASE;
        private static final int ASHIFT;

        WorkQueue(ForkJoinPool pool, ForkJoinWorkerThread owner, int mode, int seed) {
            this.pool = pool;
            this.owner = owner;
            this.mode = (short)mode;
            this.hint = seed;
            this.top = 4096;
            this.base = 4096;
        }

        final int queueSize() {
            int n2 = this.base - this.top;
            return n2 >= 0 ? 0 : -n2;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        final boolean isEmpty() {
            int s2 = this.top;
            int n2 = this.base - s2;
            if (n2 >= 0) return true;
            if (n2 != -1) return false;
            ForkJoinTask<?>[] a2 = this.array;
            if (this.array == null) return true;
            int m2 = a2.length - 1;
            if (m2 < 0) return true;
            if (U.getObject(a2, (long)((m2 & s2 - 1) << ASHIFT) + (long)ABASE) != null) return false;
            return true;
        }

        final void push(ForkJoinTask<?> task) {
            int s2 = this.top;
            ForkJoinTask<?>[] a2 = this.array;
            if (this.array != null) {
                int m2 = a2.length - 1;
                U.putOrderedObject(a2, ((m2 & s2) << ASHIFT) + ABASE, task);
                this.top = s2 + 1;
                int n2 = this.top - this.base;
                if (n2 <= 2) {
                    ForkJoinPool p2 = this.pool;
                    p2.signalWork(p2.workQueues, this);
                } else if (n2 >= m2) {
                    this.growArray();
                }
            }
        }

        final ForkJoinTask<?>[] growArray() {
            int b2;
            int t2;
            int oldMask;
            int size;
            ForkJoinTask<?>[] oldA = this.array;
            int n2 = size = oldA != null ? oldA.length << 1 : 8192;
            if (size > 0x4000000) {
                throw new RejectedExecutionException("Queue capacity exceeded");
            }
            this.array = new ForkJoinTask[size];
            ForkJoinTask[] a2 = this.array;
            if (oldA != null && (oldMask = oldA.length - 1) >= 0 && (t2 = this.top) - (b2 = this.base) > 0) {
                int mask = size - 1;
                do {
                    int oldj = ((b2 & oldMask) << ASHIFT) + ABASE;
                    int j2 = ((b2 & mask) << ASHIFT) + ABASE;
                    ForkJoinTask x2 = (ForkJoinTask)U.getObjectVolatile(oldA, oldj);
                    if (x2 == null || !U.compareAndSwapObject(oldA, oldj, x2, null)) continue;
                    U.putObjectVolatile(a2, j2, x2);
                } while (++b2 != t2);
            }
            return a2;
        }

        final ForkJoinTask<?> pop() {
            int m2;
            ForkJoinTask<?>[] a2 = this.array;
            if (this.array != null && (m2 = a2.length - 1) >= 0) {
                long j2;
                ForkJoinTask t2;
                int s2;
                while ((s2 = this.top - 1) - this.base >= 0 && (t2 = (ForkJoinTask)U.getObject(a2, j2 = (long)(((m2 & s2) << ASHIFT) + ABASE))) != null) {
                    if (!U.compareAndSwapObject(a2, j2, t2, null)) continue;
                    this.top = s2;
                    return t2;
                }
            }
            return null;
        }

        final ForkJoinTask<?> pollAt(int b2) {
            int j2;
            ForkJoinTask t2;
            ForkJoinTask<?>[] a2 = this.array;
            if (this.array != null && (t2 = (ForkJoinTask)U.getObjectVolatile(a2, j2 = ((a2.length - 1 & b2) << ASHIFT) + ABASE)) != null && this.base == b2 && U.compareAndSwapObject(a2, j2, t2, null)) {
                U.putOrderedInt(this, QBASE, b2 + 1);
                return t2;
            }
            return null;
        }

        final ForkJoinTask<?> poll() {
            int b2;
            while ((b2 = this.base) - this.top < 0) {
                ForkJoinTask<?>[] a2 = this.array;
                if (this.array == null) break;
                int j2 = ((a2.length - 1 & b2) << ASHIFT) + ABASE;
                ForkJoinTask t2 = (ForkJoinTask)U.getObjectVolatile(a2, j2);
                if (t2 != null) {
                    if (!U.compareAndSwapObject(a2, j2, t2, null)) continue;
                    U.putOrderedInt(this, QBASE, b2 + 1);
                    return t2;
                }
                if (this.base != b2) continue;
                if (b2 + 1 == this.top) break;
                Thread.yield();
            }
            return null;
        }

        final ForkJoinTask<?> nextLocalTask() {
            return this.mode == 0 ? this.pop() : this.poll();
        }

        final ForkJoinTask<?> peek() {
            int m2;
            ForkJoinTask<?>[] a2 = this.array;
            if (a2 == null || (m2 = a2.length - 1) < 0) {
                return null;
            }
            int i2 = this.mode == 0 ? this.top - 1 : this.base;
            int j2 = ((i2 & m2) << ASHIFT) + ABASE;
            return (ForkJoinTask)U.getObjectVolatile(a2, j2);
        }

        final boolean tryUnpush(ForkJoinTask<?> t2) {
            int s2;
            ForkJoinTask<?>[] a2 = this.array;
            if (this.array != null && (s2 = this.top) != this.base && U.compareAndSwapObject(a2, ((a2.length - 1 & --s2) << ASHIFT) + ABASE, t2, null)) {
                this.top = s2;
                return true;
            }
            return false;
        }

        final void cancelAll() {
            ForkJoinTask<?> t2;
            ForkJoinTask.cancelIgnoringExceptions(this.currentJoin);
            ForkJoinTask.cancelIgnoringExceptions(this.currentSteal);
            while ((t2 = this.poll()) != null) {
                ForkJoinTask.cancelIgnoringExceptions(t2);
            }
        }

        final void pollAndExecAll() {
            ForkJoinTask<?> t2;
            while ((t2 = this.poll()) != null) {
                t2.doExec();
            }
        }

        final void runTask(ForkJoinTask<?> task) {
            this.currentSteal = task;
            if (this.currentSteal != null) {
                task.doExec();
                ForkJoinTask<?>[] a2 = this.array;
                short md2 = this.mode;
                ++this.nsteals;
                this.currentSteal = null;
                if (md2 != 0) {
                    this.pollAndExecAll();
                } else if (a2 != null) {
                    long i2;
                    ForkJoinTask t2;
                    int s2;
                    int m2 = a2.length - 1;
                    while ((s2 = this.top - 1) - this.base >= 0 && (t2 = (ForkJoinTask)U.getObject(a2, i2 = (long)(((m2 & s2) << ASHIFT) + ABASE))) != null) {
                        if (!U.compareAndSwapObject(a2, i2, t2, null)) continue;
                        this.top = s2;
                        t2.doExec();
                    }
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        final boolean tryRemoveAndExec(ForkJoinTask<?> task) {
            long j2;
            ForkJoinTask t2;
            if (task == null) return false;
            ForkJoinTask<?>[] a2 = this.array;
            if (this.array == null) return false;
            int m2 = a2.length - 1;
            if (m2 < 0) return false;
            int s2 = this.top;
            int b2 = this.base;
            int n2 = s2 - b2;
            if (n2 <= 0) return false;
            boolean removed = false;
            boolean empty = true;
            boolean stat = true;
            while ((t2 = (ForkJoinTask)U.getObject(a2, j2 = (long)(((--s2 & m2) << ASHIFT) + ABASE))) != null) {
                if (t2 == task) {
                    if (s2 + 1 == this.top) {
                        if (!U.compareAndSwapObject(a2, j2, task, null)) break;
                        this.top = s2;
                        removed = true;
                        break;
                    }
                    if (this.base != b2) break;
                    removed = U.compareAndSwapObject(a2, j2, task, new EmptyTask());
                    break;
                }
                if (t2.status >= 0) {
                    empty = false;
                } else if (s2 + 1 == this.top) {
                    if (!U.compareAndSwapObject(a2, j2, t2, null)) break;
                    this.top = s2;
                    break;
                }
                if (--n2 != 0) continue;
                if (empty || this.base != b2) break;
                stat = false;
                break;
            }
            if (!removed) return stat;
            task.doExec();
            return stat;
        }

        final boolean pollAndExecCC(CountedCompleter<?> root) {
            block5: {
                int b2 = this.base;
                if (b2 - this.top >= 0) break block5;
                ForkJoinTask<?>[] a2 = this.array;
                if (this.array != null) {
                    long j2 = ((a2.length - 1 & b2) << ASHIFT) + ABASE;
                    Object o2 = U.getObjectVolatile(a2, j2);
                    if (o2 == null) {
                        return true;
                    }
                    if (o2 instanceof CountedCompleter) {
                        CountedCompleter<?> t2;
                        CountedCompleter<?> r2 = t2 = (CountedCompleter<?>)o2;
                        do {
                            if (r2 != root) continue;
                            if (this.base == b2 && U.compareAndSwapObject(a2, j2, t2, null)) {
                                U.putOrderedInt(this, QBASE, b2 + 1);
                                t2.doExec();
                            }
                            return true;
                        } while ((r2 = r2.completer) != null);
                    }
                }
            }
            return false;
        }

        final boolean externalPopAndExecCC(CountedCompleter<?> root) {
            block5: {
                long j2;
                Object o2;
                int s2 = this.top;
                if (this.base - s2 >= 0) break block5;
                ForkJoinTask<?>[] a2 = this.array;
                if (this.array != null && (o2 = U.getObject(a2, j2 = (long)(((a2.length - 1 & s2 - 1) << ASHIFT) + ABASE))) instanceof CountedCompleter) {
                    CountedCompleter<?> t2;
                    CountedCompleter<?> r2 = t2 = (CountedCompleter<?>)o2;
                    do {
                        if (r2 != root) continue;
                        if (U.compareAndSwapInt(this, QLOCK, 0, 1)) {
                            if (this.top == s2 && this.array == a2 && U.compareAndSwapObject(a2, j2, t2, null)) {
                                this.top = s2 - 1;
                                this.qlock = 0;
                                t2.doExec();
                            } else {
                                this.qlock = 0;
                            }
                        }
                        return true;
                    } while ((r2 = r2.completer) != null);
                }
            }
            return false;
        }

        final boolean internalPopAndExecCC(CountedCompleter<?> root) {
            block3: {
                long j2;
                Object o2;
                int s2 = this.top;
                if (this.base - s2 >= 0) break block3;
                ForkJoinTask<?>[] a2 = this.array;
                if (this.array != null && (o2 = U.getObject(a2, j2 = (long)(((a2.length - 1 & s2 - 1) << ASHIFT) + ABASE))) instanceof CountedCompleter) {
                    CountedCompleter<?> t2;
                    CountedCompleter<?> r2 = t2 = (CountedCompleter<?>)o2;
                    do {
                        if (r2 != root) continue;
                        if (U.compareAndSwapObject(a2, j2, t2, null)) {
                            this.top = s2 - 1;
                            t2.doExec();
                        }
                        return true;
                    } while ((r2 = r2.completer) != null);
                }
            }
            return false;
        }

        final boolean isApparentlyUnblocked() {
            Thread.State s2;
            ForkJoinWorkerThread wt2;
            return this.eventCount >= 0 && (wt2 = this.owner) != null && (s2 = wt2.getState()) != Thread.State.BLOCKED && s2 != Thread.State.WAITING && s2 != Thread.State.TIMED_WAITING;
        }

        static {
            try {
                U = ForkJoinPool.getUnsafe();
                Class<WorkQueue> k2 = WorkQueue.class;
                Class<ForkJoinTask[]> ak2 = ForkJoinTask[].class;
                QBASE = U.objectFieldOffset(k2.getDeclaredField("base"));
                QLOCK = U.objectFieldOffset(k2.getDeclaredField("qlock"));
                ABASE = U.arrayBaseOffset(ak2);
                int scale = U.arrayIndexScale(ak2);
                if ((scale & scale - 1) != 0) {
                    throw new Error("data type scale not a power of two");
                }
                ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
            }
            catch (Exception e2) {
                throw new Error(e2);
            }
        }
    }

    static final class EmptyTask
    extends ForkJoinTask<Void> {
        private static final long serialVersionUID = -7721805057305804111L;

        EmptyTask() {
            this.status = -268435456;
        }

        @Override
        public final Void getRawResult() {
            return null;
        }

        @Override
        public final void setRawResult(Void x2) {
        }

        @Override
        public final boolean exec() {
            return true;
        }
    }

    static final class DefaultForkJoinWorkerThreadFactory
    implements ForkJoinWorkerThreadFactory {
        DefaultForkJoinWorkerThreadFactory() {
        }

        @Override
        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ForkJoinWorkerThread(pool);
        }
    }

    public static interface ForkJoinWorkerThreadFactory {
        public ForkJoinWorkerThread newThread(ForkJoinPool var1);
    }
}

