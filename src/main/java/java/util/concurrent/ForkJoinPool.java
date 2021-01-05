



package java.util.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.security.AccessControlContext;
import java.security.ProtectionDomain;
import java.security.Permissions;


@sun.misc.Contended
public class ForkJoinPool extends AbstractExecutorService {



    private static void checkPermission() {
        SecurityManager security = System.getSecurityManager();
        if (security != null)
            security.checkPermission(modifyThreadPermission);
    }

    public static interface ForkJoinWorkerThreadFactory {

        public ForkJoinWorkerThread newThread(ForkJoinPool pool);
    }


    static final class DefaultForkJoinWorkerThreadFactory
        implements ForkJoinWorkerThreadFactory {
        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ForkJoinWorkerThread(pool);
        }
    }


    static final class EmptyTask extends ForkJoinTask<Void> {
        private static final long serialVersionUID = -7721805057305804111L;
        EmptyTask() { status = ForkJoinTask.NORMAL; } public final Void getRawResult() { return null; }
        public final void setRawResult(Void x) {}
        public final boolean exec() { return true; }
    }

    static final int SMASK        = 0xffff;        static final int MAX_CAP      = 0x7fff;        static final int EVENMASK     = 0xfffe;        static final int SQMASK       = 0x007e;        static final int SCANNING     = 1;             static final int INACTIVE     = 1 << 31;       static final int SS_SEQ       = 1 << 16;       static final int MODE_MASK    = 0xffff << 16;  static final int LIFO_QUEUE   = 0;
    static final int FIFO_QUEUE   = 1 << 16;
    static final int SHARED_QUEUE = 1 << 31;       @sun.misc.Contended
    static final class WorkQueue {


        static final int INITIAL_QUEUE_CAPACITY = 1 << 13;


        static final int MAXIMUM_QUEUE_CAPACITY = 1 << 26; volatile int scanState;    int stackPred;             int nsteals;               int hint;                  int config;                volatile int qlock;        volatile int base;         int top;                   ForkJoinTask<?>[] array;   final ForkJoinPool pool;   final ForkJoinWorkerThread owner; volatile Thread parker;    volatile ForkJoinTask<?> currentJoin;  volatile ForkJoinTask<?> currentSteal; WorkQueue(ForkJoinPool pool, ForkJoinWorkerThread owner) {
            this.pool = pool;
            this.owner = owner;
            base = top = INITIAL_QUEUE_CAPACITY >>> 1;
        }


        final int getPoolIndex() {
            return (config & 0xffff) >>> 1; }


        final int queueSize() {
            int n = base - top;       return (n >= 0) ? 0 : -n; }


        final boolean isEmpty() {
            ForkJoinTask<?>[] a; int n, m, s;
            return ((n = base - (s = top)) >= 0 ||
                    (n == -1 &&           ((a = array) == null || (m = a.length - 1) < 0 ||
                      U.getObject
                      (a, (long)((m & (s - 1)) << ASHIFT) + ABASE) == null)));
        }


        final void push(ForkJoinTask<?> task) {
            ForkJoinTask<?>[] a; ForkJoinPool p;
            int b = base, s = top, n;
            if ((a = array) != null) {    int m = a.length - 1;     U.putOrderedObject(a, ((m & s) << ASHIFT) + ABASE, task);
                U.putOrderedInt(this, QTOP, s + 1);
                if ((n = s - b) <= 1) {
                    if ((p = pool) != null)
                        p.signalWork(p.workQueues, this);
                }
                else if (n >= m)
                    growArray();
            }
        }


        final ForkJoinTask<?>[] growArray() {
            ForkJoinTask<?>[] oldA = array;
            int size = oldA != null ? oldA.length << 1 : INITIAL_QUEUE_CAPACITY;
            if (size > MAXIMUM_QUEUE_CAPACITY)
                throw new RejectedExecutionException("Queue capacity exceeded");
            int oldMask, t, b;
            ForkJoinTask<?>[] a = array = new ForkJoinTask<?>[size];
            if (oldA != null && (oldMask = oldA.length - 1) >= 0 &&
                (t = top) - (b = base) > 0) {
                int mask = size - 1;
                do { ForkJoinTask<?> x;
                    int oldj = ((b & oldMask) << ASHIFT) + ABASE;
                    int j    = ((b &    mask) << ASHIFT) + ABASE;
                    x = (ForkJoinTask<?>)U.getObjectVolatile(oldA, oldj);
                    if (x != null &&
                        U.compareAndSwapObject(oldA, oldj, x, null))
                        U.putObjectVolatile(a, j, x);
                } while (++b != t);
            }
            return a;
        }


        final ForkJoinTask<?> pop() {
            ForkJoinTask<?>[] a; ForkJoinTask<?> t; int m;
            if ((a = array) != null && (m = a.length - 1) >= 0) {
                for (int s; (s = top - 1) - base >= 0;) {
                    long j = ((m & s) << ASHIFT) + ABASE;
                    if ((t = (ForkJoinTask<?>)U.getObject(a, j)) == null)
                        break;
                    if (U.compareAndSwapObject(a, j, t, null)) {
                        U.putOrderedInt(this, QTOP, s);
                        return t;
                    }
                }
            }
            return null;
        }


        final ForkJoinTask<?> pollAt(int b) {
            ForkJoinTask<?> t; ForkJoinTask<?>[] a;
            if ((a = array) != null) {
                int j = (((a.length - 1) & b) << ASHIFT) + ABASE;
                if ((t = (ForkJoinTask<?>)U.getObjectVolatile(a, j)) != null &&
                    base == b && U.compareAndSwapObject(a, j, t, null)) {
                    base = b + 1;
                    return t;
                }
            }
            return null;
        }


        final ForkJoinTask<?> poll() {
            ForkJoinTask<?>[] a; int b; ForkJoinTask<?> t;
            while ((b = base) - top < 0 && (a = array) != null) {
                int j = (((a.length - 1) & b) << ASHIFT) + ABASE;
                t = (ForkJoinTask<?>)U.getObjectVolatile(a, j);
                if (base == b) {
                    if (t != null) {
                        if (U.compareAndSwapObject(a, j, t, null)) {
                            base = b + 1;
                            return t;
                        }
                    }
                    else if (b + 1 == top) break;
                }
            }
            return null;
        }


        final ForkJoinTask<?> nextLocalTask() {
            return (config & FIFO_QUEUE) == 0 ? pop() : poll();
        }


        final ForkJoinTask<?> peek() {
            ForkJoinTask<?>[] a = array; int m;
            if (a == null || (m = a.length - 1) < 0)
                return null;
            int i = (config & FIFO_QUEUE) == 0 ? top - 1 : base;
            int j = ((i & m) << ASHIFT) + ABASE;
            return (ForkJoinTask<?>)U.getObjectVolatile(a, j);
        }


        final boolean tryUnpush(ForkJoinTask<?> t) {
            ForkJoinTask<?>[] a; int s;
            if ((a = array) != null && (s = top) != base &&
                U.compareAndSwapObject
                (a, (((a.length - 1) & --s) << ASHIFT) + ABASE, t, null)) {
                U.putOrderedInt(this, QTOP, s);
                return true;
            }
            return false;
        }


        final void cancelAll() {
            ForkJoinTask<?> t;
            if ((t = currentJoin) != null) {
                currentJoin = null;
                ForkJoinTask.cancelIgnoringExceptions(t);
            }
            if ((t = currentSteal) != null) {
                currentSteal = null;
                ForkJoinTask.cancelIgnoringExceptions(t);
            }
            while ((t = poll()) != null)
                ForkJoinTask.cancelIgnoringExceptions(t);
        }

        final void pollAndExecAll() {
            for (ForkJoinTask<?> t; (t = poll()) != null;)
                t.doExec();
        }


        final void execLocalTasks() {
            int b = base, m, s;
            ForkJoinTask<?>[] a = array;
            if (b - (s = top - 1) <= 0 && a != null &&
                (m = a.length - 1) >= 0) {
                if ((config & FIFO_QUEUE) == 0) {
                    for (ForkJoinTask<?> t;;) {
                        if ((t = (ForkJoinTask<?>)U.getAndSetObject
                             (a, ((m & s) << ASHIFT) + ABASE, null)) == null)
                            break;
                        U.putOrderedInt(this, QTOP, s);
                        t.doExec();
                        if (base - (s = top - 1) > 0)
                            break;
                    }
                }
                else
                    pollAndExecAll();
            }
        }


        final void runTask(ForkJoinTask<?> task) {
            if (task != null) {
                scanState &= ~SCANNING; (currentSteal = task).doExec();
                U.putOrderedObject(this, QCURRENTSTEAL, null); execLocalTasks();
                ForkJoinWorkerThread thread = owner;
                if (++nsteals < 0)      transferStealCount(pool);
                scanState |= SCANNING;
                if (thread != null)
                    thread.afterTopLevelExec();
            }
        }


        final void transferStealCount(ForkJoinPool p) {
            AtomicLong sc;
            if (p != null && (sc = p.stealCounter) != null) {
                int s = nsteals;
                nsteals = 0;            sc.getAndAdd((long)(s < 0 ? Integer.MAX_VALUE : s));
            }
        }


        final boolean tryRemoveAndExec(ForkJoinTask<?> task) {
            ForkJoinTask<?>[] a; int m, s, b, n;
            if ((a = array) != null && (m = a.length - 1) >= 0 &&
                task != null) {
                while ((n = (s = top) - (b = base)) > 0) {
                    for (ForkJoinTask<?> t;;) {      long j = ((--s & m) << ASHIFT) + ABASE;
                        if ((t = (ForkJoinTask<?>)U.getObject(a, j)) == null)
                            return s + 1 == top;     else if (t == task) {
                            boolean removed = false;
                            if (s + 1 == top) {      if (U.compareAndSwapObject(a, j, task, null)) {
                                    U.putOrderedInt(this, QTOP, s);
                                    removed = true;
                                }
                            }
                            else if (base == b)      removed = U.compareAndSwapObject(
                                    a, j, task, new EmptyTask());
                            if (removed)
                                task.doExec();
                            break;
                        }
                        else if (t.status < 0 && s + 1 == top) {
                            if (U.compareAndSwapObject(a, j, t, null))
                                U.putOrderedInt(this, QTOP, s);
                            break;                  }
                        if (--n == 0)
                            return false;
                    }
                    if (task.status < 0)
                        return false;
                }
            }
            return true;
        }


        final CountedCompleter<?> popCC(CountedCompleter<?> task, int mode) {
            int s; ForkJoinTask<?>[] a; Object o;
            if (base - (s = top) < 0 && (a = array) != null) {
                long j = (((a.length - 1) & (s - 1)) << ASHIFT) + ABASE;
                if ((o = U.getObjectVolatile(a, j)) != null &&
                    (o instanceof CountedCompleter)) {
                    CountedCompleter<?> t = (CountedCompleter<?>)o;
                    for (CountedCompleter<?> r = t;;) {
                        if (r == task) {
                            if (mode < 0) { if (U.compareAndSwapInt(this, QLOCK, 0, 1)) {
                                    if (top == s && array == a &&
                                        U.compareAndSwapObject(a, j, t, null)) {
                                        U.putOrderedInt(this, QTOP, s - 1);
                                        U.putOrderedInt(this, QLOCK, 0);
                                        return t;
                                    }
                                    U.compareAndSwapInt(this, QLOCK, 1, 0);
                                }
                            }
                            else if (U.compareAndSwapObject(a, j, t, null)) {
                                U.putOrderedInt(this, QTOP, s - 1);
                                return t;
                            }
                            break;
                        }
                        else if ((r = r.completer) == null) break;
                    }
                }
            }
            return null;
        }


        final int pollAndExecCC(CountedCompleter<?> task) {
            int b, h; ForkJoinTask<?>[] a; Object o;
            if ((b = base) - top >= 0 || (a = array) == null)
                h = b | Integer.MIN_VALUE;  else {
                long j = (((a.length - 1) & b) << ASHIFT) + ABASE;
                if ((o = U.getObjectVolatile(a, j)) == null)
                    h = 2;                  else if (!(o instanceof CountedCompleter))
                    h = -1;                 else {
                    CountedCompleter<?> t = (CountedCompleter<?>)o;
                    for (CountedCompleter<?> r = t;;) {
                        if (r == task) {
                            if (base == b &&
                                U.compareAndSwapObject(a, j, t, null)) {
                                base = b + 1;
                                t.doExec();
                                h = 1;      }
                            else
                                h = 2;      break;
                        }
                        else if ((r = r.completer) == null) {
                            h = -1;         break;
                        }
                    }
                }
            }
            return h;
        }


        final boolean isApparentlyUnblocked() {
            Thread wt; Thread.State s;
            return (scanState >= 0 &&
                    (wt = owner) != null &&
                    (s = wt.getState()) != Thread.State.BLOCKED &&
                    s != Thread.State.WAITING &&
                    s != Thread.State.TIMED_WAITING);
        }

        private static final sun.misc.Unsafe U;
        private static final int  ABASE;
        private static final int  ASHIFT;
        private static final long QTOP;
        private static final long QLOCK;
        private static final long QCURRENTSTEAL;
        static {
            try {
                U = sun.misc.Unsafe.getUnsafe();
                Class<?> wk = WorkQueue.class;
                Class<?> ak = ForkJoinTask[].class;
                QTOP = U.objectFieldOffset
                    (wk.getDeclaredField("top"));
                QLOCK = U.objectFieldOffset
                    (wk.getDeclaredField("qlock"));
                QCURRENTSTEAL = U.objectFieldOffset
                    (wk.getDeclaredField("currentSteal"));
                ABASE = U.arrayBaseOffset(ak);
                int scale = U.arrayIndexScale(ak);
                if ((scale & (scale - 1)) != 0)
                    throw new Error("data type scale not a power of two");
                ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    public static final ForkJoinWorkerThreadFactory
        defaultForkJoinWorkerThreadFactory;


    private static final RuntimePermission modifyThreadPermission;


    static final ForkJoinPool common;


    static final int commonParallelism;


    private static int commonMaxSpares;


    private static int poolNumberSequence;


    private static final synchronized int nextPoolId() {
        return ++poolNumberSequence;
    }

    private static final long IDLE_TIMEOUT = 2000L * 1000L * 1000L; private static final long TIMEOUT_SLOP = 20L * 1000L * 1000L;  private static final int DEFAULT_COMMON_MAX_SPARES = 256;


    private static final int SPINS  = 0;


    private static final int SEED_INCREMENT = 0x9e3779b9;



    private static final long SP_MASK    = 0xffffffffL;
    private static final long UC_MASK    = ~SP_MASK;

    private static final int  AC_SHIFT   = 48;
    private static final long AC_UNIT    = 0x0001L << AC_SHIFT;
    private static final long AC_MASK    = 0xffffL << AC_SHIFT;

    private static final int  TC_SHIFT   = 32;
    private static final long TC_UNIT    = 0x0001L << TC_SHIFT;
    private static final long TC_MASK    = 0xffffL << TC_SHIFT;
    private static final long ADD_WORKER = 0x0001L << (TC_SHIFT + 15); private static final int  RSLOCK     = 1;
    private static final int  RSIGNAL    = 1 << 1;
    private static final int  STARTED    = 1 << 2;
    private static final int  STOP       = 1 << 29;
    private static final int  TERMINATED = 1 << 30;
    private static final int  SHUTDOWN   = 1 << 31;

    volatile long ctl;                   volatile int runState;               final int config;                    int indexSeed;                       volatile WorkQueue[] workQueues;     final ForkJoinWorkerThreadFactory factory;
    final UncaughtExceptionHandler ueh;  final String workerNamePrefix;       volatile AtomicLong stealCounter;    private int lockRunState() {
        int rs;
        return ((((rs = runState) & RSLOCK) != 0 ||
                 !U.compareAndSwapInt(this, RUNSTATE, rs, rs |= RSLOCK)) ?
                awaitRunStateLock() : rs);
    }


    private int awaitRunStateLock() {
        Object lock;
        boolean wasInterrupted = false;
        for (int spins = SPINS, r = 0, rs, ns;;) {
            if (((rs = runState) & RSLOCK) == 0) {
                if (U.compareAndSwapInt(this, RUNSTATE, rs, ns = rs | RSLOCK)) {
                    if (wasInterrupted) {
                        try {
                            Thread.currentThread().interrupt();
                        } catch (SecurityException ignore) {
                        }
                    }
                    return ns;
                }
            }
            else if (r == 0)
                r = ThreadLocalRandom.nextSecondarySeed();
            else if (spins > 0) {
                r ^= r << 6; r ^= r >>> 21; r ^= r << 7; if (r >= 0)
                    --spins;
            }
            else if ((rs & STARTED) == 0 || (lock = stealCounter) == null)
                Thread.yield();   else if (U.compareAndSwapInt(this, RUNSTATE, rs, rs | RSIGNAL)) {
                synchronized (lock) {
                    if ((runState & RSIGNAL) != 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ie) {
                            if (!(Thread.currentThread() instanceof
                                  ForkJoinWorkerThread))
                                wasInterrupted = true;
                        }
                    }
                    else
                        lock.notifyAll();
                }
            }
        }
    }


    private void unlockRunState(int oldRunState, int newRunState) {
        if (!U.compareAndSwapInt(this, RUNSTATE, oldRunState, newRunState)) {
            Object lock = stealCounter;
            runState = newRunState;              if (lock != null)
                synchronized (lock) { lock.notifyAll(); }
        }
    }

    private boolean createWorker() {
        ForkJoinWorkerThreadFactory fac = factory;
        Throwable ex = null;
        ForkJoinWorkerThread wt = null;
        try {
            if (fac != null && (wt = fac.newThread(this)) != null) {
                wt.start();
                return true;
            }
        } catch (Throwable rex) {
            ex = rex;
        }
        deregisterWorker(wt, ex);
        return false;
    }


    private void tryAddWorker(long c) {
        boolean add = false;
        do {
            long nc = ((AC_MASK & (c + AC_UNIT)) |
                       (TC_MASK & (c + TC_UNIT)));
            if (ctl == c) {
                int rs, stop;                 if ((stop = (rs = lockRunState()) & STOP) == 0)
                    add = U.compareAndSwapLong(this, CTL, c, nc);
                unlockRunState(rs, rs & ~RSLOCK);
                if (stop != 0)
                    break;
                if (add) {
                    createWorker();
                    break;
                }
            }
        } while (((c = ctl) & ADD_WORKER) != 0L && (int)c == 0);
    }


    final WorkQueue registerWorker(ForkJoinWorkerThread wt) {
        UncaughtExceptionHandler handler;
        wt.setDaemon(true);                           if ((handler = ueh) != null)
            wt.setUncaughtExceptionHandler(handler);
        WorkQueue w = new WorkQueue(this, wt);
        int i = 0;                                    int mode = config & MODE_MASK;
        int rs = lockRunState();
        try {
            WorkQueue[] ws; int n;                    if ((ws = workQueues) != null && (n = ws.length) > 0) {
                int s = indexSeed += SEED_INCREMENT;  int m = n - 1;
                i = ((s << 1) | 1) & m;               if (ws[i] != null) {                  int probes = 0;                   int step = (n <= 4) ? 2 : ((n >>> 1) & EVENMASK) + 2;
                    while (ws[i = (i + step) & m] != null) {
                        if (++probes >= n) {
                            workQueues = ws = Arrays.copyOf(ws, n <<= 1);
                            m = n - 1;
                            probes = 0;
                        }
                    }
                }
                w.hint = s;                           w.config = i | mode;
                w.scanState = i;                      ws[i] = w;
            }
        } finally {
            unlockRunState(rs, rs & ~RSLOCK);
        }
        wt.setName(workerNamePrefix.concat(Integer.toString(i >>> 1)));
        return w;
    }


    final void deregisterWorker(ForkJoinWorkerThread wt, Throwable ex) {
        WorkQueue w = null;
        if (wt != null && (w = wt.workQueue) != null) {
            WorkQueue[] ws;                           int idx = w.config & SMASK;
            int rs = lockRunState();
            if ((ws = workQueues) != null && ws.length > idx && ws[idx] == w)
                ws[idx] = null;
            unlockRunState(rs, rs & ~RSLOCK);
        }
        long c;                                       do {} while (!U.compareAndSwapLong
                     (this, CTL, c = ctl, ((AC_MASK & (c - AC_UNIT)) |
                                           (TC_MASK & (c - TC_UNIT)) |
                                           (SP_MASK & c))));
        if (w != null) {
            w.qlock = -1;                             w.transferStealCount(this);
            w.cancelAll();                            }
        for (;;) {                                    WorkQueue[] ws; int m, sp;
            if (tryTerminate(false, false) || w == null || w.array == null ||
                (runState & STOP) != 0 || (ws = workQueues) == null ||
                (m = ws.length - 1) < 0)              break;
            if ((sp = (int)(c = ctl)) != 0) {         if (tryRelease(c, ws[sp & m], AC_UNIT))
                    break;
            }
            else if (ex != null && (c & ADD_WORKER) != 0L) {
                tryAddWorker(c);                      break;
            }
            else                                      break;
        }
        if (ex == null)                               ForkJoinTask.helpExpungeStaleExceptions();
        else                                          ForkJoinTask.rethrow(ex);
    }

    final void signalWork(WorkQueue[] ws, WorkQueue q) {
        long c; int sp, i; WorkQueue v; Thread p;
        while ((c = ctl) < 0L) {                       if ((sp = (int)c) == 0) {                  if ((c & ADD_WORKER) != 0L)            tryAddWorker(c);
                break;
            }
            if (ws == null)                            break;
            if (ws.length <= (i = sp & SMASK))         break;
            if ((v = ws[i]) == null)                   break;
            int vs = (sp + SS_SEQ) & ~INACTIVE;        int d = sp - v.scanState;                  long nc = (UC_MASK & (c + AC_UNIT)) | (SP_MASK & v.stackPred);
            if (d == 0 && U.compareAndSwapLong(this, CTL, c, nc)) {
                v.scanState = vs;                      if ((p = v.parker) != null)
                    U.unpark(p);
                break;
            }
            if (q != null && q.base == q.top)          break;
        }
    }


    private boolean tryRelease(long c, WorkQueue v, long inc) {
        int sp = (int)c, vs = (sp + SS_SEQ) & ~INACTIVE; Thread p;
        if (v != null && v.scanState == sp) {          long nc = (UC_MASK & (c + inc)) | (SP_MASK & v.stackPred);
            if (U.compareAndSwapLong(this, CTL, c, nc)) {
                v.scanState = vs;
                if ((p = v.parker) != null)
                    U.unpark(p);
                return true;
            }
        }
        return false;
    }

    final void runWorker(WorkQueue w) {
        w.growArray();                   int seed = w.hint;               int r = (seed == 0) ? 1 : seed;  for (ForkJoinTask<?> t;;) {
            if ((t = scan(w, r)) != null)
                w.runTask(t);
            else if (!awaitWork(w, r))
                break;
            r ^= r << 13; r ^= r >>> 17; r ^= r << 5; }
    }


    private ForkJoinTask<?> scan(WorkQueue w, int r) {
        WorkQueue[] ws; int m;
        if ((ws = workQueues) != null && (m = ws.length - 1) > 0 && w != null) {
            int ss = w.scanState;                     for (int origin = r & m, k = origin, oldSum = 0, checkSum = 0;;) {
                WorkQueue q; ForkJoinTask<?>[] a; ForkJoinTask<?> t;
                int b, n; long c;
                if ((q = ws[k]) != null) {
                    if ((n = (b = q.base) - q.top) < 0 &&
                        (a = q.array) != null) {      long i = (((a.length - 1) & b) << ASHIFT) + ABASE;
                        if ((t = ((ForkJoinTask<?>)
                                  U.getObjectVolatile(a, i))) != null &&
                            q.base == b) {
                            if (ss >= 0) {
                                if (U.compareAndSwapObject(a, i, t, null)) {
                                    q.base = b + 1;
                                    if (n < -1)       signalWork(ws, q);
                                    return t;
                                }
                            }
                            else if (oldSum == 0 &&   w.scanState < 0)
                                tryRelease(c = ctl, ws[m & (int)c], AC_UNIT);
                        }
                        if (ss < 0)                   ss = w.scanState;
                        r ^= r << 1; r ^= r >>> 3; r ^= r << 10;
                        origin = k = r & m;           oldSum = checkSum = 0;
                        continue;
                    }
                    checkSum += b;
                }
                if ((k = (k + 1) & m) == origin) {    if ((ss >= 0 || (ss == (ss = w.scanState))) &&
                        oldSum == (oldSum = checkSum)) {
                        if (ss < 0 || w.qlock < 0)    break;
                        int ns = ss | INACTIVE;       long nc = ((SP_MASK & ns) |
                                   (UC_MASK & ((c = ctl) - AC_UNIT)));
                        w.stackPred = (int)c;         U.putInt(w, QSCANSTATE, ns);
                        if (U.compareAndSwapLong(this, CTL, c, nc))
                            ss = ns;
                        else
                            w.scanState = ss;         }
                    checkSum = 0;
                }
            }
        }
        return null;
    }


    private boolean awaitWork(WorkQueue w, int r) {
        if (w == null || w.qlock < 0)                 return false;
        for (int pred = w.stackPred, spins = SPINS, ss;;) {
            if ((ss = w.scanState) >= 0)
                break;
            else if (spins > 0) {
                r ^= r << 6; r ^= r >>> 21; r ^= r << 7;
                if (r >= 0 && --spins == 0) {         WorkQueue v; WorkQueue[] ws; int s, j; AtomicLong sc;
                    if (pred != 0 && (ws = workQueues) != null &&
                        (j = pred & SMASK) < ws.length &&
                        (v = ws[j]) != null &&        (v.parker == null || v.scanState >= 0))
                        spins = SPINS;                }
            }
            else if (w.qlock < 0)                     return false;
            else if (!Thread.interrupted()) {
                long c, prevctl, parkTime, deadline;
                int ac = (int)((c = ctl) >> AC_SHIFT) + (config & SMASK);
                if ((ac <= 0 && tryTerminate(false, false)) ||
                    (runState & STOP) != 0)           return false;
                if (ac <= 0 && ss == (int)c) {        prevctl = (UC_MASK & (c + AC_UNIT)) | (SP_MASK & pred);
                    int t = (short)(c >>> TC_SHIFT);  if (t > 2 && U.compareAndSwapLong(this, CTL, c, prevctl))
                        return false;                 parkTime = IDLE_TIMEOUT * ((t >= 0) ? 1 : 1 - t);
                    deadline = System.nanoTime() + parkTime - TIMEOUT_SLOP;
                }
                else
                    prevctl = parkTime = deadline = 0L;
                Thread wt = Thread.currentThread();
                U.putObject(wt, PARKBLOCKER, this);   w.parker = wt;
                if (w.scanState < 0 && ctl == c)      U.park(false, parkTime);
                U.putOrderedObject(w, QPARKER, null);
                U.putObject(wt, PARKBLOCKER, null);
                if (w.scanState >= 0)
                    break;
                if (parkTime != 0L && ctl == c &&
                    deadline - System.nanoTime() <= 0L &&
                    U.compareAndSwapLong(this, CTL, c, prevctl))
                    return false;                     }
        }
        return true;
    }

    final int helpComplete(WorkQueue w, CountedCompleter<?> task,
                           int maxTasks) {
        WorkQueue[] ws; int s = 0, m;
        if ((ws = workQueues) != null && (m = ws.length - 1) >= 0 &&
            task != null && w != null) {
            int mode = w.config;                 int r = w.hint ^ w.top;              int origin = r & m;                  int h = 1;                           for (int k = origin, oldSum = 0, checkSum = 0;;) {
                CountedCompleter<?> p; WorkQueue q;
                if ((s = task.status) < 0)
                    break;
                if (h == 1 && (p = w.popCC(task, mode)) != null) {
                    p.doExec();                  if (maxTasks != 0 && --maxTasks == 0)
                        break;
                    origin = k;                  oldSum = checkSum = 0;
                }
                else {                           if ((q = ws[k]) == null)
                        h = 0;
                    else if ((h = q.pollAndExecCC(task)) < 0)
                        checkSum += h;
                    if (h > 0) {
                        if (h == 1 && maxTasks != 0 && --maxTasks == 0)
                            break;
                        r ^= r << 13; r ^= r >>> 17; r ^= r << 5; origin = k = r & m;      oldSum = checkSum = 0;
                    }
                    else if ((k = (k + 1) & m) == origin) {
                        if (oldSum == (oldSum = checkSum))
                            break;
                        checkSum = 0;
                    }
                }
            }
        }
        return s;
    }


    private void helpStealer(WorkQueue w, ForkJoinTask<?> task) {
        WorkQueue[] ws = workQueues;
        int oldSum = 0, checkSum, m;
        if (ws != null && (m = ws.length - 1) >= 0 && w != null &&
            task != null) {
            do {                                       checkSum = 0;                          ForkJoinTask<?> subtask;
                WorkQueue j = w, v;                    descent: for (subtask = task; subtask.status >= 0; ) {
                    for (int h = j.hint | 1, k = 0, i; ; k += 2) {
                        if (k > m)                     break descent;
                        if ((v = ws[i = (h + k) & m]) != null) {
                            if (v.currentSteal == subtask) {
                                j.hint = i;
                                break;
                            }
                            checkSum += v.base;
                        }
                    }
                    for (;;) {                         ForkJoinTask<?>[] a; int b;
                        checkSum += (b = v.base);
                        ForkJoinTask<?> next = v.currentJoin;
                        if (subtask.status < 0 || j.currentJoin != subtask ||
                            v.currentSteal != subtask) break descent;
                        if (b - v.top >= 0 || (a = v.array) == null) {
                            if ((subtask = next) == null)
                                break descent;
                            j = v;
                            break;
                        }
                        int i = (((a.length - 1) & b) << ASHIFT) + ABASE;
                        ForkJoinTask<?> t = ((ForkJoinTask<?>)
                                             U.getObjectVolatile(a, i));
                        if (v.base == b) {
                            if (t == null)             break descent;
                            if (U.compareAndSwapObject(a, i, t, null)) {
                                v.base = b + 1;
                                ForkJoinTask<?> ps = w.currentSteal;
                                int top = w.top;
                                do {
                                    U.putOrderedObject(w, QCURRENTSTEAL, t);
                                    t.doExec();        } while (task.status >= 0 &&
                                         w.top != top &&
                                         (t = w.pop()) != null);
                                U.putOrderedObject(w, QCURRENTSTEAL, ps);
                                if (w.base != w.top)
                                    return;            }
                        }
                    }
                }
            } while (task.status >= 0 && oldSum != (oldSum = checkSum));
        }
    }


    private boolean tryCompensate(WorkQueue w) {
        boolean canBlock;
        WorkQueue[] ws; long c; int m, pc, sp;
        if (w == null || w.qlock < 0 ||           (ws = workQueues) == null || (m = ws.length - 1) <= 0 ||
            (pc = config & SMASK) == 0)           canBlock = false;
        else if ((sp = (int)(c = ctl)) != 0)      canBlock = tryRelease(c, ws[sp & m], 0L);
        else {
            int ac = (int)(c >> AC_SHIFT) + pc;
            int tc = (short)(c >> TC_SHIFT) + pc;
            int nbusy = 0;                        for (int i = 0; i <= m; ++i) {        WorkQueue v;
                if ((v = ws[((i << 1) | 1) & m]) != null) {
                    if ((v.scanState & SCANNING) != 0)
                        break;
                    ++nbusy;
                }
            }
            if (nbusy != (tc << 1) || ctl != c)
                canBlock = false;                 else if (tc >= pc && ac > 1 && w.isEmpty()) {
                long nc = ((AC_MASK & (c - AC_UNIT)) |
                           (~AC_MASK & c));       canBlock = U.compareAndSwapLong(this, CTL, c, nc);
            }
            else if (tc >= MAX_CAP ||
                     (this == common && tc >= pc + commonMaxSpares))
                throw new RejectedExecutionException(
                    "Thread limit exceeded replacing blocked worker");
            else {                                boolean add = false; int rs;      long nc = ((AC_MASK & c) |
                           (TC_MASK & (c + TC_UNIT)));
                if (((rs = lockRunState()) & STOP) == 0)
                    add = U.compareAndSwapLong(this, CTL, c, nc);
                unlockRunState(rs, rs & ~RSLOCK);
                canBlock = add && createWorker(); }
        }
        return canBlock;
    }


    final int awaitJoin(WorkQueue w, ForkJoinTask<?> task, long deadline) {
        int s = 0;
        if (task != null && w != null) {
            ForkJoinTask<?> prevJoin = w.currentJoin;
            U.putOrderedObject(w, QCURRENTJOIN, task);
            CountedCompleter<?> cc = (task instanceof CountedCompleter) ?
                (CountedCompleter<?>)task : null;
            for (;;) {
                if ((s = task.status) < 0)
                    break;
                if (cc != null)
                    helpComplete(w, cc, 0);
                else if (w.base == w.top || w.tryRemoveAndExec(task))
                    helpStealer(w, task);
                if ((s = task.status) < 0)
                    break;
                long ms, ns;
                if (deadline == 0L)
                    ms = 0L;
                else if ((ns = deadline - System.nanoTime()) <= 0L)
                    break;
                else if ((ms = TimeUnit.NANOSECONDS.toMillis(ns)) <= 0L)
                    ms = 1L;
                if (tryCompensate(w)) {
                    task.internalWait(ms);
                    U.getAndAddLong(this, CTL, AC_UNIT);
                }
            }
            U.putOrderedObject(w, QCURRENTJOIN, prevJoin);
        }
        return s;
    }

    private WorkQueue findNonEmptyStealQueue() {
        WorkQueue[] ws; int m;  int r = ThreadLocalRandom.nextSecondarySeed();
        if ((ws = workQueues) != null && (m = ws.length - 1) >= 0) {
            for (int origin = r & m, k = origin, oldSum = 0, checkSum = 0;;) {
                WorkQueue q; int b;
                if ((q = ws[k]) != null) {
                    if ((b = q.base) - q.top < 0)
                        return q;
                    checkSum += b;
                }
                if ((k = (k + 1) & m) == origin) {
                    if (oldSum == (oldSum = checkSum))
                        break;
                    checkSum = 0;
                }
            }
        }
        return null;
    }


    final void helpQuiescePool(WorkQueue w) {
        ForkJoinTask<?> ps = w.currentSteal; for (boolean active = true;;) {
            long c; WorkQueue q; ForkJoinTask<?> t; int b;
            w.execLocalTasks();     if ((q = findNonEmptyStealQueue()) != null) {
                if (!active) {      active = true;
                    U.getAndAddLong(this, CTL, AC_UNIT);
                }
                if ((b = q.base) - q.top < 0 && (t = q.pollAt(b)) != null) {
                    U.putOrderedObject(w, QCURRENTSTEAL, t);
                    t.doExec();
                    if (++w.nsteals < 0)
                        w.transferStealCount(this);
                }
            }
            else if (active) {      long nc = (AC_MASK & ((c = ctl) - AC_UNIT)) | (~AC_MASK & c);
                if ((int)(nc >> AC_SHIFT) + (config & SMASK) <= 0)
                    break;          if (U.compareAndSwapLong(this, CTL, c, nc))
                    active = false;
            }
            else if ((int)((c = ctl) >> AC_SHIFT) + (config & SMASK) <= 0 &&
                     U.compareAndSwapLong(this, CTL, c, c + AC_UNIT))
                break;
        }
        U.putOrderedObject(w, QCURRENTSTEAL, ps);
    }


    final ForkJoinTask<?> nextTaskFor(WorkQueue w) {
        for (ForkJoinTask<?> t;;) {
            WorkQueue q; int b;
            if ((t = w.nextLocalTask()) != null)
                return t;
            if ((q = findNonEmptyStealQueue()) == null)
                return null;
            if ((b = q.base) - q.top < 0 && (t = q.pollAt(b)) != null)
                return t;
        }
    }


    static int getSurplusQueuedTaskCount() {
        Thread t; ForkJoinWorkerThread wt; ForkJoinPool pool; WorkQueue q;
        if (((t = Thread.currentThread()) instanceof ForkJoinWorkerThread)) {
            int p = (pool = (wt = (ForkJoinWorkerThread)t).pool).
                config & SMASK;
            int n = (q = wt.workQueue).top - q.base;
            int a = (int)(pool.ctl >> AC_SHIFT) + p;
            return n - (a > (p >>>= 1) ? 0 :
                        a > (p >>>= 1) ? 1 :
                        a > (p >>>= 1) ? 2 :
                        a > (p >>>= 1) ? 4 :
                        8);
        }
        return 0;
    }

    private boolean tryTerminate(boolean now, boolean enable) {
        int rs;
        if (this == common)                       return false;
        if ((rs = runState) >= 0) {
            if (!enable)
                return false;
            rs = lockRunState();                  unlockRunState(rs, (rs & ~RSLOCK) | SHUTDOWN);
        }

        if ((rs & STOP) == 0) {
            if (!now) {                           for (long oldSum = 0L;;) {        WorkQueue[] ws; WorkQueue w; int m, b; long c;
                    long checkSum = ctl;
                    if ((int)(checkSum >> AC_SHIFT) + (config & SMASK) > 0)
                        return false;             if ((ws = workQueues) == null || (m = ws.length - 1) <= 0)
                        break;                    for (int i = 0; i <= m; ++i) {
                        if ((w = ws[i]) != null) {
                            if ((b = w.base) != w.top || w.scanState >= 0 ||
                                w.currentSteal != null) {
                                tryRelease(c = ctl, ws[m & (int)c], AC_UNIT);
                                return false;     }
                            checkSum += b;
                            if ((i & 1) == 0)
                                w.qlock = -1;     }
                    }
                    if (oldSum == (oldSum = checkSum))
                        break;
                }
            }
            if ((runState & STOP) == 0) {
                rs = lockRunState();              unlockRunState(rs, (rs & ~RSLOCK) | STOP);
            }
        }

        int pass = 0;                             for (long oldSum = 0L;;) {                WorkQueue[] ws; WorkQueue w; ForkJoinWorkerThread wt; int m;
            long checkSum = ctl;
            if ((short)(checkSum >>> TC_SHIFT) + (config & SMASK) <= 0 ||
                (ws = workQueues) == null || (m = ws.length - 1) <= 0) {
                if ((runState & TERMINATED) == 0) {
                    rs = lockRunState();          unlockRunState(rs, (rs & ~RSLOCK) | TERMINATED);
                    synchronized (this) { notifyAll(); } }
                break;
            }
            for (int i = 0; i <= m; ++i) {
                if ((w = ws[i]) != null) {
                    checkSum += w.base;
                    w.qlock = -1;                 if (pass > 0) {
                        w.cancelAll();            if (pass > 1 && (wt = w.owner) != null) {
                            if (!wt.isInterrupted()) {
                                try {             wt.interrupt();
                                } catch (Throwable ignore) {
                                }
                            }
                            if (w.scanState < 0)
                                U.unpark(wt);     }
                    }
                }
            }
            if (checkSum != oldSum) {             oldSum = checkSum;
                pass = 0;
            }
            else if (pass > 3 && pass > m)        break;
            else if (++pass > 1) {                long c; int j = 0, sp;            while (j++ <= m && (sp = (int)(c = ctl)) != 0)
                    tryRelease(c, ws[sp & m], AC_UNIT);
            }
        }
        return true;
    }

    private void externalSubmit(ForkJoinTask<?> task) {
        int r;                                    if ((r = ThreadLocalRandom.getProbe()) == 0) {
            ThreadLocalRandom.localInit();
            r = ThreadLocalRandom.getProbe();
        }
        for (;;) {
            WorkQueue[] ws; WorkQueue q; int rs, m, k;
            boolean move = false;
            if ((rs = runState) < 0) {
                tryTerminate(false, false);     throw new RejectedExecutionException();
            }
            else if ((rs & STARTED) == 0 ||     ((ws = workQueues) == null || (m = ws.length - 1) < 0)) {
                int ns = 0;
                rs = lockRunState();
                try {
                    if ((rs & STARTED) == 0) {
                        U.compareAndSwapObject(this, STEALCOUNTER, null,
                                               new AtomicLong());
                        int p = config & SMASK; int n = (p > 1) ? p - 1 : 1;
                        n |= n >>> 1; n |= n >>> 2;  n |= n >>> 4;
                        n |= n >>> 8; n |= n >>> 16; n = (n + 1) << 1;
                        workQueues = new WorkQueue[n];
                        ns = STARTED;
                    }
                } finally {
                    unlockRunState(rs, (rs & ~RSLOCK) | ns);
                }
            }
            else if ((q = ws[k = r & m & SQMASK]) != null) {
                if (q.qlock == 0 && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
                    ForkJoinTask<?>[] a = q.array;
                    int s = q.top;
                    boolean submitted = false; try {                      if ((a != null && a.length > s + 1 - q.base) ||
                            (a = q.growArray()) != null) {
                            int j = (((a.length - 1) & s) << ASHIFT) + ABASE;
                            U.putOrderedObject(a, j, task);
                            U.putOrderedInt(q, QTOP, s + 1);
                            submitted = true;
                        }
                    } finally {
                        U.compareAndSwapInt(q, QLOCK, 1, 0);
                    }
                    if (submitted) {
                        signalWork(ws, q);
                        return;
                    }
                }
                move = true;                   }
            else if (((rs = runState) & RSLOCK) == 0) { q = new WorkQueue(this, null);
                q.hint = r;
                q.config = k | SHARED_QUEUE;
                q.scanState = INACTIVE;
                rs = lockRunState();           if (rs > 0 &&  (ws = workQueues) != null &&
                    k < ws.length && ws[k] == null)
                    ws[k] = q;                 unlockRunState(rs, rs & ~RSLOCK);
            }
            else
                move = true;                   if (move)
                r = ThreadLocalRandom.advanceProbe(r);
        }
    }


    final void externalPush(ForkJoinTask<?> task) {
        WorkQueue[] ws; WorkQueue q; int m;
        int r = ThreadLocalRandom.getProbe();
        int rs = runState;
        if ((ws = workQueues) != null && (m = (ws.length - 1)) >= 0 &&
            (q = ws[m & r & SQMASK]) != null && r != 0 && rs > 0 &&
            U.compareAndSwapInt(q, QLOCK, 0, 1)) {
            ForkJoinTask<?>[] a; int am, n, s;
            if ((a = q.array) != null &&
                (am = a.length - 1) > (n = (s = q.top) - q.base)) {
                int j = ((am & s) << ASHIFT) + ABASE;
                U.putOrderedObject(a, j, task);
                U.putOrderedInt(q, QTOP, s + 1);
                U.putIntVolatile(q, QLOCK, 0);
                if (n <= 1)
                    signalWork(ws, q);
                return;
            }
            U.compareAndSwapInt(q, QLOCK, 1, 0);
        }
        externalSubmit(task);
    }


    static WorkQueue commonSubmitterQueue() {
        ForkJoinPool p = common;
        int r = ThreadLocalRandom.getProbe();
        WorkQueue[] ws; int m;
        return (p != null && (ws = p.workQueues) != null &&
                (m = ws.length - 1) >= 0) ?
            ws[m & r & SQMASK] : null;
    }


    final boolean tryExternalUnpush(ForkJoinTask<?> task) {
        WorkQueue[] ws; WorkQueue w; ForkJoinTask<?>[] a; int m, s;
        int r = ThreadLocalRandom.getProbe();
        if ((ws = workQueues) != null && (m = ws.length - 1) >= 0 &&
            (w = ws[m & r & SQMASK]) != null &&
            (a = w.array) != null && (s = w.top) != w.base) {
            long j = (((a.length - 1) & (s - 1)) << ASHIFT) + ABASE;
            if (U.compareAndSwapInt(w, QLOCK, 0, 1)) {
                if (w.top == s && w.array == a &&
                    U.getObject(a, j) == task &&
                    U.compareAndSwapObject(a, j, task, null)) {
                    U.putOrderedInt(w, QTOP, s - 1);
                    U.putOrderedInt(w, QLOCK, 0);
                    return true;
                }
                U.compareAndSwapInt(w, QLOCK, 1, 0);
            }
        }
        return false;
    }


    final int externalHelpComplete(CountedCompleter<?> task, int maxTasks) {
        WorkQueue[] ws; int n;
        int r = ThreadLocalRandom.getProbe();
        return ((ws = workQueues) == null || (n = ws.length) == 0) ? 0 :
            helpComplete(ws[(n - 1) & r & SQMASK], task, maxTasks);
    }

    public ForkJoinPool() {
        this(Math.min(MAX_CAP, Runtime.getRuntime().availableProcessors()),
             defaultForkJoinWorkerThreadFactory, null, false);
    }


    public ForkJoinPool(int parallelism) {
        this(parallelism, defaultForkJoinWorkerThreadFactory, null, false);
    }


    public ForkJoinPool(int parallelism,
                        ForkJoinWorkerThreadFactory factory,
                        UncaughtExceptionHandler handler,
                        boolean asyncMode) {
        this(checkParallelism(parallelism),
             checkFactory(factory),
             handler,
             asyncMode ? FIFO_QUEUE : LIFO_QUEUE,
             "ForkJoinPool-" + nextPoolId() + "-worker-");
        checkPermission();
    }

    private static int checkParallelism(int parallelism) {
        if (parallelism <= 0 || parallelism > MAX_CAP)
            throw new IllegalArgumentException();
        return parallelism;
    }

    private static ForkJoinWorkerThreadFactory checkFactory
        (ForkJoinWorkerThreadFactory factory) {
        if (factory == null)
            throw new NullPointerException();
        return factory;
    }


    private ForkJoinPool(int parallelism,
                         ForkJoinWorkerThreadFactory factory,
                         UncaughtExceptionHandler handler,
                         int mode,
                         String workerNamePrefix) {
        this.workerNamePrefix = workerNamePrefix;
        this.factory = factory;
        this.ueh = handler;
        this.config = (parallelism & SMASK) | mode;
        long np = (long)(-parallelism); this.ctl = ((np << AC_SHIFT) & AC_MASK) | ((np << TC_SHIFT) & TC_MASK);
    }


    public static ForkJoinPool commonPool() {
        return common;
    }

    public <T> T invoke(ForkJoinTask<T> task) {
        if (task == null)
            throw new NullPointerException();
        externalPush(task);
        return task.join();
    }


    public void execute(ForkJoinTask<?> task) {
        if (task == null)
            throw new NullPointerException();
        externalPush(task);
    }

    public void execute(Runnable task) {
        if (task == null)
            throw new NullPointerException();
        ForkJoinTask<?> job;
        if (task instanceof ForkJoinTask<?>) job = (ForkJoinTask<?>) task;
        else
            job = new ForkJoinTask.RunnableExecuteAction(task);
        externalPush(job);
    }


    public <T> ForkJoinTask<T> submit(ForkJoinTask<T> task) {
        if (task == null)
            throw new NullPointerException();
        externalPush(task);
        return task;
    }


    public <T> ForkJoinTask<T> submit(Callable<T> task) {
        ForkJoinTask<T> job = new ForkJoinTask.AdaptedCallable<T>(task);
        externalPush(job);
        return job;
    }


    public <T> ForkJoinTask<T> submit(Runnable task, T result) {
        ForkJoinTask<T> job = new ForkJoinTask.AdaptedRunnable<T>(task, result);
        externalPush(job);
        return job;
    }


    public ForkJoinTask<?> submit(Runnable task) {
        if (task == null)
            throw new NullPointerException();
        ForkJoinTask<?> job;
        if (task instanceof ForkJoinTask<?>) job = (ForkJoinTask<?>) task;
        else
            job = new ForkJoinTask.AdaptedRunnableAction(task);
        externalPush(job);
        return job;
    }


    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
        ArrayList<Future<T>> futures = new ArrayList<>(tasks.size());

        boolean done = false;
        try {
            for (Callable<T> t : tasks) {
                ForkJoinTask<T> f = new ForkJoinTask.AdaptedCallable<T>(t);
                futures.add(f);
                externalPush(f);
            }
            for (int i = 0, size = futures.size(); i < size; i++)
                ((ForkJoinTask<?>)futures.get(i)).quietlyJoin();
            done = true;
            return futures;
        } finally {
            if (!done)
                for (int i = 0, size = futures.size(); i < size; i++)
                    futures.get(i).cancel(false);
        }
    }


    public ForkJoinWorkerThreadFactory getFactory() {
        return factory;
    }


    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return ueh;
    }


    public int getParallelism() {
        int par;
        return ((par = config & SMASK) > 0) ? par : 1;
    }


    public static int getCommonPoolParallelism() {
        return commonParallelism;
    }


    public int getPoolSize() {
        return (config & SMASK) + (short)(ctl >>> TC_SHIFT);
    }


    public boolean getAsyncMode() {
        return (config & FIFO_QUEUE) != 0;
    }


    public int getRunningThreadCount() {
        int rc = 0;
        WorkQueue[] ws; WorkQueue w;
        if ((ws = workQueues) != null) {
            for (int i = 1; i < ws.length; i += 2) {
                if ((w = ws[i]) != null && w.isApparentlyUnblocked())
                    ++rc;
            }
        }
        return rc;
    }


    public int getActiveThreadCount() {
        int r = (config & SMASK) + (int)(ctl >> AC_SHIFT);
        return (r <= 0) ? 0 : r; }


    public boolean isQuiescent() {
        return (config & SMASK) + (int)(ctl >> AC_SHIFT) <= 0;
    }


    public long getStealCount() {
        AtomicLong sc = stealCounter;
        long count = (sc == null) ? 0L : sc.get();
        WorkQueue[] ws; WorkQueue w;
        if ((ws = workQueues) != null) {
            for (int i = 1; i < ws.length; i += 2) {
                if ((w = ws[i]) != null)
                    count += w.nsteals;
            }
        }
        return count;
    }


    public long getQueuedTaskCount() {
        long count = 0;
        WorkQueue[] ws; WorkQueue w;
        if ((ws = workQueues) != null) {
            for (int i = 1; i < ws.length; i += 2) {
                if ((w = ws[i]) != null)
                    count += w.queueSize();
            }
        }
        return count;
    }


    public int getQueuedSubmissionCount() {
        int count = 0;
        WorkQueue[] ws; WorkQueue w;
        if ((ws = workQueues) != null) {
            for (int i = 0; i < ws.length; i += 2) {
                if ((w = ws[i]) != null)
                    count += w.queueSize();
            }
        }
        return count;
    }


    public boolean hasQueuedSubmissions() {
        WorkQueue[] ws; WorkQueue w;
        if ((ws = workQueues) != null) {
            for (int i = 0; i < ws.length; i += 2) {
                if ((w = ws[i]) != null && !w.isEmpty())
                    return true;
            }
        }
        return false;
    }


    protected ForkJoinTask<?> pollSubmission() {
        WorkQueue[] ws; WorkQueue w; ForkJoinTask<?> t;
        if ((ws = workQueues) != null) {
            for (int i = 0; i < ws.length; i += 2) {
                if ((w = ws[i]) != null && (t = w.poll()) != null)
                    return t;
            }
        }
        return null;
    }


    protected int drainTasksTo(Collection<? super ForkJoinTask<?>> c) {
        int count = 0;
        WorkQueue[] ws; WorkQueue w; ForkJoinTask<?> t;
        if ((ws = workQueues) != null) {
            for (int i = 0; i < ws.length; ++i) {
                if ((w = ws[i]) != null) {
                    while ((t = w.poll()) != null) {
                        c.add(t);
                        ++count;
                    }
                }
            }
        }
        return count;
    }


    public String toString() {
        long qt = 0L, qs = 0L; int rc = 0;
        AtomicLong sc = stealCounter;
        long st = (sc == null) ? 0L : sc.get();
        long c = ctl;
        WorkQueue[] ws; WorkQueue w;
        if ((ws = workQueues) != null) {
            for (int i = 0; i < ws.length; ++i) {
                if ((w = ws[i]) != null) {
                    int size = w.queueSize();
                    if ((i & 1) == 0)
                        qs += size;
                    else {
                        qt += size;
                        st += w.nsteals;
                        if (w.isApparentlyUnblocked())
                            ++rc;
                    }
                }
            }
        }
        int pc = (config & SMASK);
        int tc = pc + (short)(c >>> TC_SHIFT);
        int ac = pc + (int)(c >> AC_SHIFT);
        if (ac < 0) ac = 0;
        int rs = runState;
        String level = ((rs & TERMINATED) != 0 ? "Terminated" :
                        (rs & STOP)       != 0 ? "Terminating" :
                        (rs & SHUTDOWN)   != 0 ? "Shutting down" :
                        "Running");
        return super.toString() +
            "[" + level +
            ", parallelism = " + pc +
            ", size = " + tc +
            ", active = " + ac +
            ", running = " + rc +
            ", steals = " + st +
            ", tasks = " + qt +
            ", submissions = " + qs +
            "]";
    }


    public void shutdown() {
        checkPermission();
        tryTerminate(false, true);
    }


    public List<Runnable> shutdownNow() {
        checkPermission();
        tryTerminate(true, true);
        return Collections.emptyList();
    }


    public boolean isTerminated() {
        return (runState & TERMINATED) != 0;
    }


    public boolean isTerminating() {
        int rs = runState;
        return (rs & STOP) != 0 && (rs & TERMINATED) == 0;
    }


    public boolean isShutdown() {
        return (runState & SHUTDOWN) != 0;
    }


    public boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (this == common) {
            awaitQuiescence(timeout, unit);
            return false;
        }
        long nanos = unit.toNanos(timeout);
        if (isTerminated())
            return true;
        if (nanos <= 0L)
            return false;
        long deadline = System.nanoTime() + nanos;
        synchronized (this) {
            for (;;) {
                if (isTerminated())
                    return true;
                if (nanos <= 0L)
                    return false;
                long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
                wait(millis > 0L ? millis : 1L);
                nanos = deadline - System.nanoTime();
            }
        }
    }


    public boolean awaitQuiescence(long timeout, TimeUnit unit) {
        long nanos = unit.toNanos(timeout);
        ForkJoinWorkerThread wt;
        Thread thread = Thread.currentThread();
        if ((thread instanceof ForkJoinWorkerThread) &&
            (wt = (ForkJoinWorkerThread)thread).pool == this) {
            helpQuiescePool(wt.workQueue);
            return true;
        }
        long startTime = System.nanoTime();
        WorkQueue[] ws;
        int r = 0, m;
        boolean found = true;
        while (!isQuiescent() && (ws = workQueues) != null &&
               (m = ws.length - 1) >= 0) {
            if (!found) {
                if ((System.nanoTime() - startTime) > nanos)
                    return false;
                Thread.yield(); }
            found = false;
            for (int j = (m + 1) << 2; j >= 0; --j) {
                ForkJoinTask<?> t; WorkQueue q; int b, k;
                if ((k = r++ & m) <= m && k >= 0 && (q = ws[k]) != null &&
                    (b = q.base) - q.top < 0) {
                    found = true;
                    if ((t = q.pollAt(b)) != null)
                        t.doExec();
                    break;
                }
            }
        }
        return true;
    }


    static void quiesceCommonPool() {
        common.awaitQuiescence(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }


    public static interface ManagedBlocker {

        boolean block() throws InterruptedException;


        boolean isReleasable();
    }


    public static void managedBlock(ManagedBlocker blocker)
        throws InterruptedException {
        ForkJoinPool p;
        ForkJoinWorkerThread wt;
        Thread t = Thread.currentThread();
        if ((t instanceof ForkJoinWorkerThread) &&
            (p = (wt = (ForkJoinWorkerThread)t).pool) != null) {
            WorkQueue w = wt.workQueue;
            while (!blocker.isReleasable()) {
                if (p.tryCompensate(w)) {
                    try {
                        do {} while (!blocker.isReleasable() &&
                                     !blocker.block());
                    } finally {
                        U.getAndAddLong(p, CTL, AC_UNIT);
                    }
                    break;
                }
            }
        }
        else {
            do {} while (!blocker.isReleasable() &&
                         !blocker.block());
        }
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ForkJoinTask.AdaptedRunnable<T>(runnable, value);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ForkJoinTask.AdaptedCallable<T>(callable);
    }

    private static final sun.misc.Unsafe U;
    private static final int  ABASE;
    private static final int  ASHIFT;
    private static final long CTL;
    private static final long RUNSTATE;
    private static final long STEALCOUNTER;
    private static final long PARKBLOCKER;
    private static final long QTOP;
    private static final long QLOCK;
    private static final long QSCANSTATE;
    private static final long QPARKER;
    private static final long QCURRENTSTEAL;
    private static final long QCURRENTJOIN;

    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ForkJoinPool.class;
            CTL = U.objectFieldOffset
                (k.getDeclaredField("ctl"));
            RUNSTATE = U.objectFieldOffset
                (k.getDeclaredField("runState"));
            STEALCOUNTER = U.objectFieldOffset
                (k.getDeclaredField("stealCounter"));
            Class<?> tk = Thread.class;
            PARKBLOCKER = U.objectFieldOffset
                (tk.getDeclaredField("parkBlocker"));
            Class<?> wk = WorkQueue.class;
            QTOP = U.objectFieldOffset
                (wk.getDeclaredField("top"));
            QLOCK = U.objectFieldOffset
                (wk.getDeclaredField("qlock"));
            QSCANSTATE = U.objectFieldOffset
                (wk.getDeclaredField("scanState"));
            QPARKER = U.objectFieldOffset
                (wk.getDeclaredField("parker"));
            QCURRENTSTEAL = U.objectFieldOffset
                (wk.getDeclaredField("currentSteal"));
            QCURRENTJOIN = U.objectFieldOffset
                (wk.getDeclaredField("currentJoin"));
            Class<?> ak = ForkJoinTask[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }

        commonMaxSpares = DEFAULT_COMMON_MAX_SPARES;
        defaultForkJoinWorkerThreadFactory =
            new DefaultForkJoinWorkerThreadFactory();
        modifyThreadPermission = new RuntimePermission("modifyThread");

        common = java.security.AccessController.doPrivileged
            (new java.security.PrivilegedAction<ForkJoinPool>() {
                public ForkJoinPool run() { return makeCommonPool(); }});
        int par = common.config & SMASK; commonParallelism = par > 0 ? par : 1;
    }


    private static ForkJoinPool makeCommonPool() {
        int parallelism = -1;
        ForkJoinWorkerThreadFactory factory = null;
        UncaughtExceptionHandler handler = null;
        try {  String pp = System.getProperty
                ("java.util.concurrent.ForkJoinPool.common.parallelism");
            String fp = System.getProperty
                ("java.util.concurrent.ForkJoinPool.common.threadFactory");
            String hp = System.getProperty
                ("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
            if (pp != null)
                parallelism = Integer.parseInt(pp);
            if (fp != null)
                factory = ((ForkJoinWorkerThreadFactory)ClassLoader.
                           getSystemClassLoader().loadClass(fp).newInstance());
            if (hp != null)
                handler = ((UncaughtExceptionHandler)ClassLoader.
                           getSystemClassLoader().loadClass(hp).newInstance());
        } catch (Exception ignore) {
        }
        if (factory == null) {
            if (System.getSecurityManager() == null)
                factory = defaultForkJoinWorkerThreadFactory;
            else factory = new InnocuousForkJoinWorkerThreadFactory();
        }
        if (parallelism < 0 && (parallelism = Runtime.getRuntime().availableProcessors() - 1) <= 0)
            parallelism = 1;
        if (parallelism > MAX_CAP)
            parallelism = MAX_CAP;
        return new ForkJoinPool(parallelism, factory, handler, LIFO_QUEUE,
                                "ForkJoinPool.commonPool-worker-");
    }


    static final class InnocuousForkJoinWorkerThreadFactory
        implements ForkJoinWorkerThreadFactory {


        private static final AccessControlContext innocuousAcc;
        static {
            Permissions innocuousPerms = new Permissions();
            innocuousPerms.add(modifyThreadPermission);
            innocuousPerms.add(new RuntimePermission(
                                   "enableContextClassLoaderOverride"));
            innocuousPerms.add(new RuntimePermission(
                                   "modifyThreadGroup"));
            innocuousAcc = new AccessControlContext(new ProtectionDomain[] {
                    new ProtectionDomain(null, innocuousPerms)
                });
        }

        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return (ForkJoinWorkerThread.InnocuousForkJoinWorkerThread)
                java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<ForkJoinWorkerThread>() {
                    public ForkJoinWorkerThread run() {
                        return new ForkJoinWorkerThread.
                            InnocuousForkJoinWorkerThread(pool);
                    }}, innocuousAcc);
        }
    }

}
