


package com.sun.org.apache.xml.internal.utils;


public class ThreadControllerWrapper
{


  private static ThreadController m_tpool = new ThreadController();

  public static Thread runThread(Runnable runnable, int priority)
  {
    return m_tpool.run(runnable, priority);
  }

  public static void waitThread(Thread worker, Runnable task)
    throws InterruptedException
  {
    m_tpool.waitThread(worker, task);
  }


  public static class ThreadController
  {


    final class SafeThread extends Thread {
         private volatile boolean ran = false;

         public SafeThread(Runnable target) {
             super(target);
         }

         public final void run() {
             if (Thread.currentThread() != this) {
                 throw new IllegalStateException("The run() method in a"
                     + " SafeThread cannot be called from another thread.");
             }
             synchronized (this) {
                if (!ran) {
                    ran = true;
                }
                else {
                 throw new IllegalStateException("The run() method in a"
                     + " SafeThread cannot be called more than once.");
                 }
             }
             super.run();
         }
    }


    public Thread run(Runnable task, int priority)
    {

      Thread t = new SafeThread(task);

      t.start();

      return t;
    }


    public void waitThread(Thread worker, Runnable task)
            throws InterruptedException
    {

      worker.join();
    }
  }

}
