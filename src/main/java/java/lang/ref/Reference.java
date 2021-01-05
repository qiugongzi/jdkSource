

package java.lang.ref;

import sun.misc.Cleaner;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;



public abstract class Reference<T> {



    private T referent;

    volatile ReferenceQueue<? super T> queue;


    @SuppressWarnings("rawtypes")
    volatile Reference next;


    transient private Reference<T> discovered;



    static private class Lock { }
    private static Lock lock = new Lock();



    private static Reference<Object> pending = null;


    private static class ReferenceHandler extends Thread {

        private static void ensureClassInitialized(Class<?> clazz) {
            try {
                Class.forName(clazz.getName(), true, clazz.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw (Error) new NoClassDefFoundError(e.getMessage()).initCause(e);
            }
        }

        static {
            ensureClassInitialized(InterruptedException.class);
            ensureClassInitialized(Cleaner.class);
        }

        ReferenceHandler(ThreadGroup g, String name) {
            super(g, name);
        }

        public void run() {
            while (true) {
                tryHandlePending(true);
            }
        }
    }


    static boolean tryHandlePending(boolean waitForNotify) {
        Reference<Object> r;
        Cleaner c;
        try {
            synchronized (lock) {
                if (pending != null) {
                    r = pending;
                    c = r instanceof Cleaner ? (Cleaner) r : null;
                    pending = r.discovered;
                    r.discovered = null;
                } else {
                    if (waitForNotify) {
                        lock.wait();
                    }
                    return waitForNotify;
                }
            }
        } catch (OutOfMemoryError x) {
            Thread.yield();
            return true;
        } catch (InterruptedException x) {
            return true;
        }

        if (c != null) {
            c.clean();
            return true;
        }

        ReferenceQueue<? super Object> q = r.queue;
        if (q != ReferenceQueue.NULL) q.enqueue(r);
        return true;
    }

    static {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        for (ThreadGroup tgn = tg;
             tgn != null;
             tg = tgn, tgn = tg.getParent());
        Thread handler = new ReferenceHandler(tg, "Reference Handler");

        handler.setPriority(Thread.MAX_PRIORITY);
        handler.setDaemon(true);
        handler.start();

        SharedSecrets.setJavaLangRefAccess(new JavaLangRefAccess() {
            @Override
            public boolean tryHandlePendingReference() {
                return tryHandlePending(false);
            }
        });
    }




    public T get() {
        return this.referent;
    }


    public void clear() {
        this.referent = null;
    }





    public boolean isEnqueued() {
        return (this.queue == ReferenceQueue.ENQUEUED);
    }


    public boolean enqueue() {
        return this.queue.enqueue(this);
    }




    Reference(T referent) {
        this(referent, null);
    }

    Reference(T referent, ReferenceQueue<? super T> queue) {
        this.referent = referent;
        this.queue = (queue == null) ? ReferenceQueue.NULL : queue;
    }

}
