

package java.lang;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.LockSupport;

import sun.nio.ch.Interruptible;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.SecurityConstants;


public class Thread implements Runnable {

  private static native void registerNatives();

  static {
    registerNatives();
  }

  /**
   * 线程名称
   * todo
   * 为什么要用 volatile?
   */
  private volatile String name;

  /**
   * 优先级
   * 1-10之间，默认为5
   */
  private int priority;

  /**
   * what
   */
  private Thread threadQ;

  /**
   * JVM中的JavaThread指针
   * what
   */
  private long eetop;

  /**
   * 是否单步执行此线程
   * what
   */
  private boolean single_step;

  /**
   * 是否是守护线程
   * todo
   * 守护线程作用？
   */
  private boolean daemon = false;

  /**
   * 虚拟机状态
   * what
   */
  private boolean stillborn = false;

  /**
   * 传进来的对象，用于执行
   */
  private Runnable target;

  /**
   * 线程的组
   * what
   */
  private ThreadGroup group;

  /**
   * 线程的上下文加载器，可以用来加载一些资源
   * todo
   * 有啥用？
   */
  private ClassLoader contextClassLoader;

  /**
   * todo
   */
  private AccessControlContext inheritedAccessControlContext;

  /**
   * 给匿名线程命名的整数，
   * 匿名线程会被命名为Thread-1这类名字，
   * 这个int会自增
   */
  private static int threadInitNumber;

  private static synchronized int nextThreadNum() {
    return threadInitNumber++;
  }

  /**
   * ThreadLocal的Map用于保存变量副本
   * what
   */
  ThreadLocal.ThreadLocalMap threadLocals = null;

  /**
   * InheritableThreadLocal用于子线程能够拿到父线程往ThreadLocal里设置的值
   * todo
   * 子线程，父线程
   */
  ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;

  /**
   * 给线程分配的栈的大小
   * what
   */
  private long stackSize;

  /**
   * 本地线程终止之后的专用状态
   * what
   */
  private long nativeParkEventPointer;

  /**
   * 线程ID
   */
  private long tid;

  /**
   * 用于生成线程ID
   * what
   */
  private static long threadSeqNumber;

  /**
   * 线程状态
   * what
   */
  private volatile int threadStatus = 0;


  private static synchronized long nextThreadID() {
    return ++threadSeqNumber;
  }

  // what
  volatile Object parkBlocker;

  // what
  private volatile Interruptible blocker;
  private final Object blockerLock = new Object();


  void blockedOn(Interruptible b) {
    synchronized (blockerLock) {
      blocker = b;
    }
  }

  public final static int MIN_PRIORITY = 1;


  public final static int NORM_PRIORITY = 5;


  public final static int MAX_PRIORITY = 10;

  //获得当前的线程
  public static native Thread currentThread();

  //使当前线程从执行状态（运行状态）变为可执行态（就绪状态）。
  // cpu会从众多的可执行态里选择，
  // 也就是说，当前也就是刚刚的那个线程还是有可能会被再次执行到的，
  // 并不是说一定会执行其他线程而该线程在下一次中不会执行到了
  public static native void yield();

  //静态方法强制当前正在执行的线程休眠（暂停执行），以“减慢线程”。
  // 当线程睡眠时，它睡在某个地方，在苏醒之前不会返回到可运行状态。 当睡眠时间到期，则返回到可运行状态。
  public static native void sleep(long millis) throws InterruptedException;

  /**
   * 在指定的毫秒数加指定的纳秒数内让当前正在执行的线程休眠（暂停执行），
   * 此操作受到系统计时器和调度程序精度和准确性的影响。
   */
  public static void sleep(long millis, int nanos)
      throws InterruptedException {
    if (millis < 0) {
      throw new IllegalArgumentException("timeout value is negative");
    }

    if (nanos < 0 || nanos > 999999) {
      throw new IllegalArgumentException(
          "nanosecond timeout value out of range");
    }

    if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
      millis++;
    }

    sleep(millis);
  }

  /**
   * 分配新的 Thread 对象，以便将 target 作为其运行对象，将指定的 name 作为其名称，
   * 作为 group 所引用的线程组的一员，并具有指定的 堆栈大小。
   */
  private void init(ThreadGroup g, Runnable target, String name,
                    long stackSize) {
    init(g, target, name, stackSize, null, true);
  }

  /**
   * todo
   * 线程初始化方法
   * 分配新的 Thread 对象，以便将 target 作为其运行对象，将指定的 name 作为其名称，并作为 group 所引用的线程组的一员。
   * 如果 group 为 null，并且有安全管理器，则该组由安全管理器的 getThreadGroup 方法确定。
   * 如果 group 为 null，并且没有安全管理器，或安全管理器的 getThreadGroup 方法返回 null，
   * 则该组与创建新线程的线程被设定为相同的 ThreadGroup。
   * <p>
   * 如果有安全管理器，则其 checkAccess 方法通过 ThreadGroup 作为其参数被调用。
   * <p>
   * 此外，当被重写 getContextClassLoader 或 setContextClassLoader 方法的子类构造方法直接或间接调用时，
   * 其 checkPermission 方法通过 RuntimePermission("enableContextClassLoaderOverride") 权限调用。
   * 其结果可能是 SecurityException。
   * <p>
   * 如果 target 参数不是 null，则 target 的 run 方法在启动该线程时调用。
   * 如果 target 参数为 null，则该线程的 run 方法在该线程启动时调用。
   * <p>
   * 新创建线程的优先级被设定为创建该线程的线程的优先级，即当前正在运行的线程的优先级。
   * 方法 setPriority 可用于将优先级更改为一个新值。
   * <p>
   * 当且仅当创建新线程的线程当前被标记为守护线程时，新创建的线程才被标记为守护线程。
   * 方法 setDaemon 可用于改变线程是否为守护线程。
   *
   * @param g                   线程组  (null)
   * @param target              调用run方法的目标对象 (null)
   * @param name                新线程的名字 (Thread- + nextThreadNum())
   * @param stackSize           新线程分配所需堆栈大小 (0)
   *                            为零时表示忽略该参数。
   *                            堆栈大小是虚拟机要为该线程堆栈分配的地址空间的近似字节数。
   *                            stackSize 参数（如果有）的作用具有高度的平台依赖性。
   *                            在某些平台上，指定一个较高的 stackSize 参数值可能使线程在抛出 StackOverflowError
   *                            之前达到较大的递归深度。同样，指定一个较低的值将允许较多的线程并发地存在，
   *                            且不会抛出 OutOfMemoryError（或其他内部错误）。
   *                            stackSize 参数的值与最大递归深度和并发程度之间的关系细节与平台有关。
   *                            在某些平台上，stackSize 参数的值无论如何不会起任何作用。
   *                            作为建议，可以让虚拟机自由处理 stackSize 参数。
   *                            如果指定值对于平台来说过低，则虚拟机可能使用某些特定于平台的最小值；
   *                            如果指定值过高，则虚拟机可能使用某些特定于平台的最大值。
   *                            同样，虚拟机还会视情况自由地舍入指定值（或完全忽略它）。
   * @param acc                 (null)
   * @param inheritThreadLocals (true)
   */
  private void init(ThreadGroup g, Runnable target, String name,
                    long stackSize, AccessControlContext acc,
                    boolean inheritThreadLocals) {
    if (name == null) {
      throw new NullPointerException("name cannot be null");
    }

    this.name = name;

    // 获取当前线程作为父线程
    Thread parent = currentThread();
    // 获得系统的安全管理器 what
    SecurityManager security = System.getSecurityManager();
    if (g == null) {

      // 安全检查 what
      if (security != null) {
        g = security.getThreadGroup();
      }

      // 设置线程组 what
      if (g == null) {
        g = parent.getThreadGroup();
      }
    }

    //检查是否允许调用线程修改线程组参数
    g.checkAccess();

    //是否有权限访问
    if (security != null) {
      if (isCCLOverridden(getClass())) {
        security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
      }
    }

    //往线程组添加线程但未启动
    g.addUnstarted();

    // 线程组
    this.group = g;
    // 是否守护线程
    this.daemon = parent.isDaemon();
    this.priority = parent.getPriority();

    //每个线程都有一个优先级，高优先级线程的执行优先于低优先级线程。
    // 每个线程都可以或不可以标记为一个守护程序。当某个线程中运行的代码创建一个新 Thread 对象时，
    // 该新线程的初始优先级被设定为创建线程的优先级，并且当且仅当创建线程是守护线程时，新线程才是守护程序。
    if (security == null || isCCLOverridden(parent.getClass()))
      this.contextClassLoader = parent.getContextClassLoader();
    else
      this.contextClassLoader = parent.contextClassLoader;
    this.inheritedAccessControlContext =
        acc != null ? acc : AccessController.getContext();
    this.target = target;
    setPriority(priority);
    if (inheritThreadLocals && parent.inheritableThreadLocals != null)
      this.inheritableThreadLocals =
          ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);

    this.stackSize = stackSize;


    tid = nextThreadID();
  }


  /**
   * 覆盖Object clone方法，直接抛出不支持克隆异常
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }


  public Thread() {
    init(null, null, "Thread-" + nextThreadNum(), 0);
  }


  public Thread(Runnable target) {
    init(null, target, "Thread-" + nextThreadNum(), 0);
  }


  Thread(Runnable target, AccessControlContext acc) {
    init(null, target, "Thread-" + nextThreadNum(), 0, acc, false);
  }


  public Thread(ThreadGroup group, Runnable target) {
    init(group, target, "Thread-" + nextThreadNum(), 0);
  }


  public Thread(String name) {
    init(null, null, name, 0);
  }


  public Thread(ThreadGroup group, String name) {
    init(group, null, name, 0);
  }


  public Thread(Runnable target, String name) {
    init(null, target, name, 0);
  }


  public Thread(ThreadGroup group, Runnable target, String name) {
    init(group, target, name, 0);
  }


  public Thread(ThreadGroup group, Runnable target, String name,
                long stackSize) {
    init(group, target, name, stackSize);
  }

  /**
   * 线程的start方法和run方法是两个概念，
   * start是指这个线程可以进入 可执行（runnable状态）的状态
   * 调用start之后线程会开始执行这个方法（不一定是立刻执行，要等待cpu调度），进入了runnable状态。
   * 如果调用run的话相当于直接调用了这个target的方法，这个过程中并没有启动这个线程，还是在原来的线程上执行。
   */
  public synchronized void start() {

    if (threadStatus != 0)
      throw new IllegalThreadStateException();


    group.add(this);

    boolean started = false;
    try {
      start0();
      started = true;
    } finally {
      try {
        if (!started) {
          group.threadStartFailed(this);
        }
      } catch (Throwable ignore) {

      }
    }
  }

  //开始线程
  private native void start0();


  @Override
  public void run() {
    if (target != null) {
      target.run();
    }
  }


  private void exit() {
    if (group != null) {
      group.threadTerminated(this);
      group = null;
    }

    target = null;

    threadLocals = null;
    inheritableThreadLocals = null;
    inheritedAccessControlContext = null;
    blocker = null;
    uncaughtExceptionHandler = null;
  }


  @Deprecated
  public final void stop() {
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      checkAccess();
      if (this != Thread.currentThread()) {
        security.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
      }
    }
    if (threadStatus != 0) {
      resume();
    }

    stop0(new ThreadDeath());
  }


  @Deprecated
  public final synchronized void stop(Throwable obj) {
    throw new UnsupportedOperationException();
  }


  public void interrupt() {
    if (this != Thread.currentThread())
      checkAccess();

    synchronized (blockerLock) {
      Interruptible b = blocker;
      if (b != null) {
        interrupt0();
        b.interrupt(this);
        return;
      }
    }
    interrupt0();
  }


  public static boolean interrupted() {
    return currentThread().isInterrupted(true);
  }


  public boolean isInterrupted() {
    return isInterrupted(false);
  }


  private native boolean isInterrupted(boolean ClearInterrupted);


  @Deprecated
  public void destroy() {
    throw new NoSuchMethodError();
  }


  //判断线程是否存活
  public final native boolean isAlive();


  @Deprecated
  public final void suspend() {
    checkAccess();
    suspend0();
  }


  @Deprecated
  public final void resume() {
    checkAccess();
    resume0();
  }


  public final void setPriority(int newPriority) {
    ThreadGroup g;
    checkAccess();
    if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
      throw new IllegalArgumentException();
    }
    if ((g = getThreadGroup()) != null) {
      if (newPriority > g.getMaxPriority()) {
        newPriority = g.getMaxPriority();
      }
      setPriority0(priority = newPriority);
    }
  }


  public final int getPriority() {
    return priority;
  }


  public final synchronized void setName(String name) {
    checkAccess();
    if (name == null) {
      throw new NullPointerException("name cannot be null");
    }

    this.name = name;
    if (threadStatus != 0) {
      setNativeName(name);
    }
  }


  public final String getName() {
    return name;
  }


  public final ThreadGroup getThreadGroup() {
    return group;
  }


  public static int activeCount() {
    return currentThread().getThreadGroup().activeCount();
  }


  public static int enumerate(Thread tarray[]) {
    return currentThread().getThreadGroup().enumerate(tarray);
  }


  @Deprecated
  public native int countStackFrames();

  //堵塞当前线程，使其处于等待状态，因为子线程执行的时间可能比主线程执行时间还长，
  // 所以join是主线程需要在它执行完后再销毁。当然也可以加参数join(long millis, int nanos)，
  // 使其等待N秒N毫秒，如果它已经处于join方法，则报InterruptedException 。
  // 等待该线程终止的时间最长为 millis 毫秒。
  // 超时为 0 意味着要一直等下去。
  public final synchronized void join(long millis)
      throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
      throw new IllegalArgumentException("timeout value is negative");
    }

    if (millis == 0) {
      while (isAlive()) {
        wait(0);
      }
    } else {
      while (isAlive()) {
        long delay = millis - now;
        if (delay <= 0) {
          break;
        }
        wait(delay);
        now = System.currentTimeMillis() - base;
      }
    }
  }


  public final synchronized void join(long millis, int nanos)
      throws InterruptedException {

    if (millis < 0) {
      throw new IllegalArgumentException("timeout value is negative");
    }

    if (nanos < 0 || nanos > 999999) {
      throw new IllegalArgumentException(
          "nanosecond timeout value out of range");
    }

    if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
      millis++;
    }

    join(millis);
  }


  public final void join() throws InterruptedException {
    join(0);
  }


  public static void dumpStack() {
    new Exception("Stack trace").printStackTrace();
  }

  /**
   * 将该线程标记为守护线程或用户线程。当正在运行的线程都是守护线程时，Java 虚拟机退出。
   * 该方法必须在启动线程前调用。
   * <p>
   * 该方法首先调用该线程的 checkAccess 方法，且不带任何参数。
   * 这可能抛出 SecurityException（在当前线程中）。
   */
  public final void setDaemon(boolean on) {
    checkAccess();
    if (isAlive()) {
      throw new IllegalThreadStateException();
    }
    daemon = on;
  }


  public final boolean isDaemon() {
    return daemon;
  }

  /**
   * 判定当前运行的线程是否有权修改该线程。
   * 如果有安全管理器，则调用其 checkAccess 方法，
   * 并将该线程作为其参数。这可能导致抛出 SecurityException。
   */
  public final void checkAccess() {
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      security.checkAccess(this);
    }
  }


  public String toString() {
    ThreadGroup group = getThreadGroup();
    if (group != null) {
      return "Thread[" + getName() + "," + getPriority() + "," +
          group.getName() + "]";
    } else {
      return "Thread[" + getName() + "," + getPriority() + "," +
          "" + "]";
    }
  }

  /**
   * 返回该线程的上下文 ClassLoader。
   * 上下文 ClassLoader 由线程创建者提供，供运行于该线程中的代码在加载类和资源时使用。
   * 如果未设定，则默认为父线程的 ClassLoader 上下文。
   * 原始线程的上下文 ClassLoader 通常设定为用于加载应用程序的类加载器。
   * 首先，如果有安全管理器，并且调用者的类加载器不是 null，
   * 也不同于其上下文类加载器正在被请求的线程上下文类加载器的祖先，
   * 则通过 RuntimePermission("getClassLoader") 权限调用该安全管理器的 checkPermission 方法，
   * 查看是否可以获取上下文 ClassLoader。
   */
  @CallerSensitive
  public ClassLoader getContextClassLoader() {
    if (contextClassLoader == null)
      return null;
    SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      ClassLoader.checkClassLoaderPermission(contextClassLoader,
          Reflection.getCallerClass());
    }
    return contextClassLoader;
  }


  public void setContextClassLoader(ClassLoader cl) {
    SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      sm.checkPermission(new RuntimePermission("setContextClassLoader"));
    }
    contextClassLoader = cl;
  }

  //当且仅当当前线程在指定的对象上保持监视器锁时，才返回 true。
  public static native boolean holdsLock(Object obj);

  private static final StackTraceElement[] EMPTY_STACK_TRACE
      = new StackTraceElement[0];


  public StackTraceElement[] getStackTrace() {
    if (this != Thread.currentThread()) {
      SecurityManager security = System.getSecurityManager();
      if (security != null) {
        security.checkPermission(
            SecurityConstants.GET_STACK_TRACE_PERMISSION);
      }
      if (!isAlive()) {
        return EMPTY_STACK_TRACE;
      }
      StackTraceElement[][] stackTraceArray = dumpThreads(new Thread[]{this});
      StackTraceElement[] stackTrace = stackTraceArray[0];
      if (stackTrace == null) {
        stackTrace = EMPTY_STACK_TRACE;
      }
      return stackTrace;
    } else {
      return (new Exception()).getStackTrace();
    }
  }


  public static Map<Thread, StackTraceElement[]> getAllStackTraces() {
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      security.checkPermission(
          SecurityConstants.GET_STACK_TRACE_PERMISSION);
      security.checkPermission(
          SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
    }

    Thread[] threads = getThreads();
    StackTraceElement[][] traces = dumpThreads(threads);
    Map<Thread, StackTraceElement[]> m = new HashMap<>(threads.length);
    for (int i = 0; i < threads.length; i++) {
      StackTraceElement[] stackTrace = traces[i];
      if (stackTrace != null) {
        m.put(threads[i], stackTrace);
      }
    }
    return m;
  }


  private static final RuntimePermission SUBCLASS_IMPLEMENTATION_PERMISSION =
      new RuntimePermission("enableContextClassLoaderOverride");


  private static class Caches {

    static final ConcurrentMap<WeakClassKey, Boolean> subclassAudits =
        new ConcurrentHashMap<>();


    static final ReferenceQueue<Class<?>> subclassAuditsQueue =
        new ReferenceQueue<>();
  }


  private static boolean isCCLOverridden(Class<?> cl) {
    if (cl == Thread.class)
      return false;

    processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
    WeakClassKey key = new WeakClassKey(cl, Caches.subclassAuditsQueue);
    Boolean result = Caches.subclassAudits.get(key);
    if (result == null) {
      result = Boolean.valueOf(auditSubclass(cl));
      Caches.subclassAudits.putIfAbsent(key, result);
    }

    return result.booleanValue();
  }


  private static boolean auditSubclass(final Class<?> subcl) {
    Boolean result = AccessController.doPrivileged(
        new PrivilegedAction<Boolean>() {
          public Boolean run() {
            for (Class<?> cl = subcl;
                 cl != Thread.class;
                 cl = cl.getSuperclass()) {
              try {
                cl.getDeclaredMethod("getContextClassLoader", new Class<?>[0]);
                return Boolean.TRUE;
              } catch (NoSuchMethodException ex) {
              }
              try {
                Class<?>[] params = {ClassLoader.class};
                cl.getDeclaredMethod("setContextClassLoader", params);
                return Boolean.TRUE;
              } catch (NoSuchMethodException ex) {
              }
            }
            return Boolean.FALSE;
          }
        }
    );
    return result.booleanValue();
  }

  private native static StackTraceElement[][] dumpThreads(Thread[] threads);

  private native static Thread[] getThreads();


  public long getId() {
    return tid;
  }

  /**
   * 在java中的线程状态一共有6种，
   * 不同于操作系统中定义，java中把 运行状态 和 就绪状态 合称为 可运行状态。
   */
  public enum State {

    // 初始状态，还没有start的线程
    NEW,

    // 可运行状态，保含 运行状态和就绪状态
    RUNNABLE,

    // 线程在等待monitor锁(synchronized关键字)
    BLOCKED,

    //无时间限制的等待，通过调用wait、join等方法进入，
    // 主要是等待其他线程进行一些操作，不同于等待资源的阻塞状态。
    WAITING,

    //有时间限制的等待状态，通过sleep，wait(time)，join(time)等方法进入
    TIMED_WAITING,

    // 终止状态，表示线程已经完成执行。
    TERMINATED;
  }


  public State getState() {
    return sun.misc.VM.toThreadState(threadStatus);
  }

  @FunctionalInterface
  public interface UncaughtExceptionHandler {

    void uncaughtException(Thread t, Throwable e);
  }

  private volatile UncaughtExceptionHandler uncaughtExceptionHandler;

  private static volatile UncaughtExceptionHandler defaultUncaughtExceptionHandler;


  public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
    SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      sm.checkPermission(
          new RuntimePermission("setDefaultUncaughtExceptionHandler")
      );
    }

    defaultUncaughtExceptionHandler = eh;
  }


  public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
    return defaultUncaughtExceptionHandler;
  }


  public UncaughtExceptionHandler getUncaughtExceptionHandler() {
    return uncaughtExceptionHandler != null ?
        uncaughtExceptionHandler : group;
  }


  public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
    checkAccess();
    uncaughtExceptionHandler = eh;
  }


  private void dispatchUncaughtException(Throwable e) {
    getUncaughtExceptionHandler().uncaughtException(this, e);
  }


  static void processQueue(ReferenceQueue<Class<?>> queue,
                           ConcurrentMap<? extends
                               WeakReference<Class<?>>, ?> map) {
    Reference<? extends Class<?>> ref;
    while ((ref = queue.poll()) != null) {
      map.remove(ref);
    }
  }


  static class WeakClassKey extends WeakReference<Class<?>> {

    private final int hash;


    WeakClassKey(Class<?> cl, ReferenceQueue<Class<?>> refQueue) {
      super(cl, refQueue);
      hash = System.identityHashCode(cl);
    }


    @Override
    public int hashCode() {
      return hash;
    }


    @Override
    public boolean equals(Object obj) {
      if (obj == this)
        return true;

      if (obj instanceof WeakClassKey) {
        Object referent = get();
        return (referent != null) &&
            (referent == ((WeakClassKey) obj).get());
      } else {
        return false;
      }
    }
  }


  @sun.misc.Contended("tlr")
  long threadLocalRandomSeed;


  @sun.misc.Contended("tlr")
  int threadLocalRandomProbe;


  @sun.misc.Contended("tlr")
  int threadLocalRandomSecondarySeed;

  //设置线程优先级
  private native void setPriority0(int newPriority);

  //停止线程
  private native void stop0(Object o);

  // 线程挂起(暂停)
  private native void suspend0();

  //将一个挂起线程复活继续执行
  private native void resume0();

  //该线程的中断状态将被设置
  private native void interrupt0();

  private native void setNativeName(String name);
}
