



package javax.swing;



import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.io.*;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.event.EventListenerList;




@SuppressWarnings("serial")
public class Timer implements Serializable
{


    protected EventListenerList listenerList = new EventListenerList();

    private transient final AtomicBoolean notify = new AtomicBoolean(false);

    private volatile int     initialDelay, delay;
    private volatile boolean repeats = true, coalesce = true;

    private transient final Runnable doPostEvent;

    private static volatile boolean logTimers;

    private transient final Lock lock = new ReentrantLock();

    transient TimerQueue.DelayedTimer delayedTimer = null;

    private volatile String actionCommand;


    public Timer(int delay, ActionListener listener) {
        super();
        this.delay = delay;
        this.initialDelay = delay;

        doPostEvent = new DoPostEvent();

        if (listener != null) {
            addActionListener(listener);
        }
    }


     private transient volatile AccessControlContext acc =
            AccessController.getContext();


     final AccessControlContext getAccessControlContext() {
       if (acc == null) {
           throw new SecurityException(
                   "Timer is missing AccessControlContext");
       }
       return acc;
     }


    class DoPostEvent implements Runnable
    {
        public void run() {
            if (logTimers) {
                System.out.println("Timer ringing: " + Timer.this);
            }
            if(notify.get()) {
                fireActionPerformed(new ActionEvent(Timer.this, 0, getActionCommand(),
                                                    System.currentTimeMillis(),
                                                    0));
                if (coalesce) {
                    cancelEvent();
                }
            }
        }

        Timer getTimer() {
            return Timer.this;
        }
    }


    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }



    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }



    public ActionListener[] getActionListeners() {
        return listenerList.getListeners(ActionListener.class);
    }



    protected void fireActionPerformed(ActionEvent e) {
        Object[] listeners = listenerList.getListenerList();

        for (int i=listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }
        }
    }


    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }


    private TimerQueue timerQueue() {
        return TimerQueue.sharedInstance();
    }



    public static void setLogTimers(boolean flag) {
        logTimers = flag;
    }



    public static boolean getLogTimers() {
        return logTimers;
    }



    public void setDelay(int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Invalid delay: " + delay);
        }
        else {
            this.delay = delay;
        }
    }



    public int getDelay() {
        return delay;
    }



    public void setInitialDelay(int initialDelay) {
        if (initialDelay < 0) {
            throw new IllegalArgumentException("Invalid initial delay: " +
                                               initialDelay);
        }
        else {
            this.initialDelay = initialDelay;
        }
    }



    public int getInitialDelay() {
        return initialDelay;
    }



    public void setRepeats(boolean flag) {
        repeats = flag;
    }



    public boolean isRepeats() {
        return repeats;
    }



    public void setCoalesce(boolean flag) {
        boolean old = coalesce;
        coalesce = flag;
        if (!old && coalesce) {
            cancelEvent();
        }
    }



    public boolean isCoalesce() {
        return coalesce;
    }



    public void setActionCommand(String command) {
        this.actionCommand = command;
    }



    public String getActionCommand() {
        return actionCommand;
    }



     public void start() {
        timerQueue().addTimer(this, getInitialDelay());
    }



    public boolean isRunning() {
        return timerQueue().containsTimer(this);
    }



    public void stop() {
        getLock().lock();
        try {
            cancelEvent();
            timerQueue().removeTimer(this);
        } finally {
            getLock().unlock();
        }
    }



    public void restart() {
        getLock().lock();
        try {
            stop();
            start();
        } finally {
            getLock().unlock();
        }
    }



    void cancelEvent() {
        notify.set(false);
    }


    void post() {
         if (notify.compareAndSet(false, true) || !coalesce) {
             AccessController.doPrivileged(new PrivilegedAction<Void>() {
                 public Void run() {
                     SwingUtilities.invokeLater(doPostEvent);
                     return null;
                }
            }, getAccessControlContext());
        }
    }

    Lock getLock() {
        return lock;
    }

    private void readObject(ObjectInputStream in)
        throws ClassNotFoundException, IOException
    {
        this.acc = AccessController.getContext();
        in.defaultReadObject();
    }


    private Object readResolve() {
        Timer timer = new Timer(getDelay(), null);
        timer.listenerList = listenerList;
        timer.initialDelay = initialDelay;
        timer.delay = delay;
        timer.repeats = repeats;
        timer.coalesce = coalesce;
        timer.actionCommand = actionCommand;
        return timer;
    }
}
