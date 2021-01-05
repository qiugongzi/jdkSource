

package com.sun.corba.se.impl.orbutil.threadpool;

import java.io.IOException;
import java.io.Closeable;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;

import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.orbutil.threadpool.WorkQueueImpl;

import com.sun.corba.se.spi.monitoring.MonitoringConstants;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.spi.logging.CORBALogDomains;

public class ThreadPoolImpl implements ThreadPool
{
    private static AtomicInteger threadCounter = new AtomicInteger(0);
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.get(CORBALogDomains.RPC_TRANSPORT);


    private WorkQueue workQueue;

    private int availableWorkerThreads = 0;

    private int currentThreadCount = 0;

    private int minWorkerThreads = 0;

    private int maxWorkerThreads = 0;

    private long inactivityTimeout;

    private boolean boundedThreadPool = false;

    private AtomicLong processedCount = new AtomicLong(1);

    private AtomicLong totalTimeTaken = new AtomicLong(0);

    private String name;

    private MonitoredObject threadpoolMonitoredObject;

    private ThreadGroup threadGroup;

    Object workersLock = new Object();
    List<WorkerThread> workers = new ArrayList<>();


    public ThreadPoolImpl(ThreadGroup tg, String threadpoolName) {
        inactivityTimeout = ORBConstants.DEFAULT_INACTIVITY_TIMEOUT;
        maxWorkerThreads = Integer.MAX_VALUE;
        workQueue = new WorkQueueImpl(this);
        threadGroup = tg;
        name = threadpoolName;
        initializeMonitoring();
    }


    public ThreadPoolImpl(String threadpoolName) {
        this( Thread.currentThread().getThreadGroup(), threadpoolName ) ;
    }


    public ThreadPoolImpl(int minSize, int maxSize, long timeout,
                                            String threadpoolName)
    {
        minWorkerThreads = minSize;
        maxWorkerThreads = maxSize;
        inactivityTimeout = timeout;
        boundedThreadPool = true;
        workQueue = new WorkQueueImpl(this);
        name = threadpoolName;
        for (int i = 0; i < minWorkerThreads; i++) {
            createWorkerThread();
        }
        initializeMonitoring();
    }

    public void close() throws IOException {

        List<WorkerThread> copy = null;
        synchronized (workersLock) {
            copy = new ArrayList<>(workers);
        }

        for (WorkerThread wt : copy) {
            wt.close();
            while (wt.getState() != Thread.State.TERMINATED) {
                try {
                    wt.join();
                } catch (InterruptedException exc) {
                    wrapper.interruptedJoinCallWhileClosingThreadPool(exc, wt, this);
                }
            }
        }

        threadGroup = null;
    }


    private void initializeMonitoring() {
        MonitoredObject root = MonitoringFactories.getMonitoringManagerFactory().
                createMonitoringManager(MonitoringConstants.DEFAULT_MONITORING_ROOT, null).
                getRootMonitoredObject();

        MonitoredObject threadPoolMonitoringObjectRoot = root.getChild(
                    MonitoringConstants.THREADPOOL_MONITORING_ROOT);
        if (threadPoolMonitoringObjectRoot == null) {
            threadPoolMonitoringObjectRoot =  MonitoringFactories.
                    getMonitoredObjectFactory().createMonitoredObject(
                    MonitoringConstants.THREADPOOL_MONITORING_ROOT,
                    MonitoringConstants.THREADPOOL_MONITORING_ROOT_DESCRIPTION);
            root.addChild(threadPoolMonitoringObjectRoot);
        }
        threadpoolMonitoredObject = MonitoringFactories.
                    getMonitoredObjectFactory().
                    createMonitoredObject(name,
                    MonitoringConstants.THREADPOOL_MONITORING_DESCRIPTION);

        threadPoolMonitoringObjectRoot.addChild(threadpoolMonitoredObject);

        LongMonitoredAttributeBase b1 = new
            LongMonitoredAttributeBase(MonitoringConstants.THREADPOOL_CURRENT_NUMBER_OF_THREADS,
                    MonitoringConstants.THREADPOOL_CURRENT_NUMBER_OF_THREADS_DESCRIPTION) {
                public Object getValue() {
                    return new Long(ThreadPoolImpl.this.currentNumberOfThreads());
                }
            };
        threadpoolMonitoredObject.addAttribute(b1);
        LongMonitoredAttributeBase b2 = new
            LongMonitoredAttributeBase(MonitoringConstants.THREADPOOL_NUMBER_OF_AVAILABLE_THREADS,
                    MonitoringConstants.THREADPOOL_CURRENT_NUMBER_OF_THREADS_DESCRIPTION) {
                public Object getValue() {
                    return new Long(ThreadPoolImpl.this.numberOfAvailableThreads());
                }
            };
        threadpoolMonitoredObject.addAttribute(b2);
        LongMonitoredAttributeBase b3 = new
            LongMonitoredAttributeBase(MonitoringConstants.THREADPOOL_NUMBER_OF_BUSY_THREADS,
                    MonitoringConstants.THREADPOOL_NUMBER_OF_BUSY_THREADS_DESCRIPTION) {
                public Object getValue() {
                    return new Long(ThreadPoolImpl.this.numberOfBusyThreads());
                }
            };
        threadpoolMonitoredObject.addAttribute(b3);
        LongMonitoredAttributeBase b4 = new
            LongMonitoredAttributeBase(MonitoringConstants.THREADPOOL_AVERAGE_WORK_COMPLETION_TIME,
                    MonitoringConstants.THREADPOOL_AVERAGE_WORK_COMPLETION_TIME_DESCRIPTION) {
                public Object getValue() {
                    return new Long(ThreadPoolImpl.this.averageWorkCompletionTime());
                }
            };
        threadpoolMonitoredObject.addAttribute(b4);
        LongMonitoredAttributeBase b5 = new
            LongMonitoredAttributeBase(MonitoringConstants.THREADPOOL_CURRENT_PROCESSED_COUNT,
                    MonitoringConstants.THREADPOOL_CURRENT_PROCESSED_COUNT_DESCRIPTION) {
                public Object getValue() {
                    return new Long(ThreadPoolImpl.this.currentProcessedCount());
                }
            };
        threadpoolMonitoredObject.addAttribute(b5);

        threadpoolMonitoredObject.addChild(
                ((WorkQueueImpl)workQueue).getMonitoredObject());
    }

    MonitoredObject getMonitoredObject() {
        return threadpoolMonitoredObject;
    }

    public WorkQueue getAnyWorkQueue()
    {
        return workQueue;
    }

    public WorkQueue getWorkQueue(int queueId)
        throws NoSuchWorkQueueException
    {
        if (queueId != 0)
            throw new NoSuchWorkQueueException();
        return workQueue;
    }


    void notifyForAvailableWork(WorkQueue aWorkQueue) {
        synchronized (aWorkQueue) {
            if (availableWorkerThreads < aWorkQueue.workItemsInQueue()) {
                createWorkerThread();
            } else {
                aWorkQueue.notify();
            }
        }
    }


    private Thread createWorkerThreadHelper( String name ) {
        WorkerThread thread = new WorkerThread(threadGroup, name);
        synchronized (workersLock) {
            workers.add(thread);
        }

        thread.setDaemon(true);

        wrapper.workerThreadCreated(thread, thread.getContextClassLoader());

        thread.start();
        return null;
    }



    void createWorkerThread() {
        final String name = getName();
        synchronized (workQueue) {
            try {
                if (System.getSecurityManager() == null) {
                    createWorkerThreadHelper(name);
                } else {
                    AccessController.doPrivileged(
                            new PrivilegedAction() {
                        public Object run() {
                            return createWorkerThreadHelper(name);
                        }
                    }
                    );
                }
            } catch (Throwable t) {
                decrementCurrentNumberOfThreads();
                wrapper.workerThreadCreationFailure(t);
            } finally {
                incrementCurrentNumberOfThreads();
            }
        }
    }

    public int minimumNumberOfThreads() {
        return minWorkerThreads;
    }

    public int maximumNumberOfThreads() {
        return maxWorkerThreads;
    }

    public long idleTimeoutForThreads() {
        return inactivityTimeout;
    }

    public int currentNumberOfThreads() {
        synchronized (workQueue) {
            return currentThreadCount;
        }
    }

    void decrementCurrentNumberOfThreads() {
        synchronized (workQueue) {
            currentThreadCount--;
        }
    }

    void incrementCurrentNumberOfThreads() {
        synchronized (workQueue) {
            currentThreadCount++;
        }
    }

    public int numberOfAvailableThreads() {
        synchronized (workQueue) {
            return availableWorkerThreads;
        }
    }

    public int numberOfBusyThreads() {
        synchronized (workQueue) {
            return (currentThreadCount - availableWorkerThreads);
        }
    }

    public long averageWorkCompletionTime() {
        synchronized (workQueue) {
            return (totalTimeTaken.get() / processedCount.get());
        }
    }

    public long currentProcessedCount() {
        synchronized (workQueue) {
            return processedCount.get();
        }
    }

    public String getName() {
        return name;
    }


    public int numberOfWorkQueues() {
        return 1;
    }


    private static synchronized int getUniqueThreadId() {
        return ThreadPoolImpl.threadCounter.incrementAndGet();
    }


    void decrementNumberOfAvailableThreads() {
        synchronized (workQueue) {
            availableWorkerThreads--;
        }
    }


    void incrementNumberOfAvailableThreads() {
        synchronized (workQueue) {
            availableWorkerThreads++;
        }
    }


    private class WorkerThread extends Thread implements Closeable
    {
        private Work currentWork;
        private int threadId = 0; private volatile boolean closeCalled = false;
        private String threadPoolName;
        private StringBuffer workerThreadName = new StringBuffer();

        WorkerThread(ThreadGroup tg, String threadPoolName) {
            super(tg, "Idle");
            this.threadId = ThreadPoolImpl.getUniqueThreadId();
            this.threadPoolName = threadPoolName;
            setName(composeWorkerThreadName(threadPoolName, "Idle"));
        }

        public synchronized void close() {
            closeCalled = true;
            interrupt();
        }

        private void resetClassLoader() {

        }

        private void performWork() {
            long start = System.currentTimeMillis();
            try {
                currentWork.doWork();
            } catch (Throwable t) {
                wrapper.workerThreadDoWorkThrowable(this, t);
            }
            long elapsedTime = System.currentTimeMillis() - start;
            totalTimeTaken.addAndGet(elapsedTime);
            processedCount.incrementAndGet();
        }

        public void run() {
            try  {
                while (!closeCalled) {
                    try {
                        currentWork = ((WorkQueueImpl)workQueue).requestWork(
                            inactivityTimeout);
                        if (currentWork == null)
                            continue;
                    } catch (InterruptedException exc) {
                        wrapper.workQueueThreadInterrupted( exc, getName(),
                           Boolean.valueOf(closeCalled));

                        continue ;
                    } catch (Throwable t) {
                         wrapper.workerThreadThrowableFromRequestWork(this, t,
                                workQueue.getName());

                        continue;
                    }

                    performWork();

                    currentWork = null;

                    resetClassLoader();
                }
            } catch (Throwable e) {
                wrapper.workerThreadCaughtUnexpectedThrowable(this,e);
            } finally {
                synchronized (workersLock) {
                    workers.remove(this);
                }
            }
        }

        private String composeWorkerThreadName(String poolName, String workerName) {
            workerThreadName.setLength(0);
            workerThreadName.append("p: ").append(poolName);
            workerThreadName.append("; w: ").append(workerName);
            return workerThreadName.toString();
        }
    } }

