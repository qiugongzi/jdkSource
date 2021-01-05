

package com.sun.corba.se.impl.orbutil.threadpool;

import java.io.IOException;

import java.security.PrivilegedAction;
import java.security.AccessController;

import java.util.concurrent.atomic.AtomicInteger;

import com.sun.corba.se.spi.orb.ORB;

import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolChooser;

import com.sun.corba.se.impl.orbutil.threadpool.ThreadPoolImpl;
import com.sun.corba.se.impl.orbutil.ORBConstants;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.spi.logging.CORBALogDomains;


public class ThreadPoolManagerImpl implements ThreadPoolManager
{
    private ThreadPool threadPool;
    private ThreadGroup threadGroup;

    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.get(CORBALogDomains.RPC_TRANSPORT);

    public ThreadPoolManagerImpl() {
        threadGroup = getThreadGroup();
        threadPool = new ThreadPoolImpl(threadGroup,
            ORBConstants.THREADPOOL_DEFAULT_NAME);
    }

    private static AtomicInteger tgCount = new AtomicInteger();


    private ThreadGroup getThreadGroup() {
        ThreadGroup tg;

        try {
            tg = AccessController.doPrivileged(
                new PrivilegedAction<ThreadGroup>() {
                    public ThreadGroup run() {
                        ThreadGroup tg = Thread.currentThread().getThreadGroup();
                        ThreadGroup ptg = tg;
                        try {
                            while (ptg != null) {
                                tg = ptg;
                                ptg = tg.getParent();
                            }
                        } catch (SecurityException se) {
                            }
                        return new ThreadGroup(tg, "ORB ThreadGroup " + tgCount.getAndIncrement());
                    }
                }
            );
        } catch (SecurityException e) {
            tg = Thread.currentThread().getThreadGroup();
        }

        return tg;
    }

    public void close() {
        try {
            threadPool.close();
        } catch (IOException exc) {
            wrapper.threadPoolCloseError();
        }

        try {
            boolean isDestroyed = threadGroup.isDestroyed();
            int numThreads = threadGroup.activeCount();
            int numGroups = threadGroup.activeGroupCount();

            if (isDestroyed) {
                wrapper.threadGroupIsDestroyed(threadGroup);
            } else {
                if (numThreads > 0)
                    wrapper.threadGroupHasActiveThreadsInClose(threadGroup, numThreads);

                if (numGroups > 0)
                    wrapper.threadGroupHasSubGroupsInClose(threadGroup, numGroups);

                threadGroup.destroy();
            }
        } catch (IllegalThreadStateException exc) {
            wrapper.threadGroupDestroyFailed(exc, threadGroup);
        }

        threadGroup = null;
    }


    public ThreadPool getThreadPool(String threadpoolId)
        throws NoSuchThreadPoolException {

        return threadPool;
    }


    public ThreadPool getThreadPool(int numericIdForThreadpool)
        throws NoSuchThreadPoolException {

        return threadPool;
    }


    public int  getThreadPoolNumericId(String threadpoolId) {
        return 0;
    }


    public String getThreadPoolStringId(int numericIdForThreadpool) {
       return "";
    }


    public ThreadPool getDefaultThreadPool() {
        return threadPool;
    }


    public ThreadPoolChooser getThreadPoolChooser(String componentId) {
        return null;
    }

    public ThreadPoolChooser getThreadPoolChooser(int componentIndex) {
        return null;
    }


    public void setThreadPoolChooser(String componentId, ThreadPoolChooser aThreadPoolChooser) {
        }


    public int getThreadPoolChooserNumericId(String componentId) {
        return 0;
    }

}

