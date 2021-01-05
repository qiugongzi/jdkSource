

package com.sun.corba.se.spi.orbutil.threadpool;

import java.io.Closeable;

public interface ThreadPoolManager extends Closeable
{

    public ThreadPool getThreadPool(String threadpoolId) throws NoSuchThreadPoolException;


    public ThreadPool getThreadPool(int numericIdForThreadpool) throws NoSuchThreadPoolException;


    public int  getThreadPoolNumericId(String threadpoolId);


    public String getThreadPoolStringId(int numericIdForThreadpool);


    public ThreadPool getDefaultThreadPool();


    public ThreadPoolChooser getThreadPoolChooser(String componentId);


    public ThreadPoolChooser getThreadPoolChooser(int componentIndex);


    public void setThreadPoolChooser(String componentId, ThreadPoolChooser aThreadPoolChooser);


    public int getThreadPoolChooserNumericId(String componentId);
}

