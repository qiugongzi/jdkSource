

package java.lang;

/**
 * 所有类都继承了Object
 * 接口不是类，不继承Object
 * 接口是对类的一系列需求的描述 《java核心技术卷1》
 *
 * 但是接口中隐式声明了Object的抽象方法，所以接口可以调用toString方法，
 * 具体实现由接口的实现类决定。
 *
 */
public class Object {

    /**
     * registerNatives 是一个本地方法
     * 它是一个特殊的本地方法，用来注册本地方法的本地方法
     * 只要一个类中有本地方法就都要有该方法
     * 该方法在类加载时调用，注册该类的所有本地方法
     *
     * <p>
     *   第一，通过System.loadLibrary()将包含本地方法实现的动态文件加载进内存；
     *   第二，当Java程序需要调用本地方法时，虚拟机在加载的动态文件中定位并链接该本地方法，从而得以执行本地方法。
     *   registerNatives()方法的作用就是取代第二步，让程序主动将本地方法链接到调用方，
     *   当Java程序需要调用本地方法时就可以直接调用，而不需要虚拟机再去定位并链接。
     * </p>
     */
    private static native void registerNatives();
    static {
        registerNatives();
    }

    /**
     * 返回Object的运行时类型
     * @return
     */
    public final native Class<?> getClass();


    /**
     * @see <a href="https://blog.csdn.net/JasonChen3318/article/details/100150009"></a>
     * @see <a href="https://www.cnblogs.com/shangxiaofei/p/8143272.html"></a>
     * 对象的hashcode 是由对象地址转换得到的一个值,并不是地址本身
     * hashcode是用来在散列存储结构中确定对象的存储地址的
     *
     * @return
     */
    public native int hashCode();


    /**
     * 用来比较内存地址是否相等
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        return (this == obj);
    }


    /**
     * 浅拷贝
     * 只有实现了Cloneable接口，才能调用，否则抛出异常
     *
     * 浅拷贝：只拷贝这个对象的所有属性值，
     * 如果属性值是地址，不会拷贝地址指向的对象
     * 浅拷贝拷贝出来的对象和原始对象还是共用了一部分内存（成员对象只有一份）
     *
     * 深拷贝：只有对象的域含有reference变量，才存在深拷贝
     *
     * 访问修饰符为protected
     * 保证了只有在该类及其子类中才能调用clone()方法
     *
     * 设置为protected的原因是
     * 为了让在调用一个类的clone方法时，必须重写该类的clone方法，
     *  也就是实现cloneable接口，访问修饰为public
     * 因为Object的clone方法是浅拷贝，不安全
     *
     * 重写方法时，访问修饰符必须大于等于父类，这是由继承多态的含义决定的
     * @return
     * @throws CloneNotSupportedException
     */
    protected native Object clone() throws CloneNotSupportedException;


    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }


    /**
     * @see <a href="https://www.runoob.com/java/java-object-wait.html"></a>
     */
    public final native void notify();


    public final native void notifyAll();


    public final native void wait(long timeout) throws InterruptedException;


    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos > 0) {
            timeout++;
        }

        wait(timeout);
    }


    public final void wait() throws InterruptedException {
        wait(0);
    }


    /**
     * 释放资源，基本不用调用
     * @throws Throwable
     */
    protected void finalize() throws Throwable { }
}
