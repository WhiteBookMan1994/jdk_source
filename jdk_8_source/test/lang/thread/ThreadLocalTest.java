package lang.thread;

/**
 * @author dingchenchen
 * @since 2021/3/30
 */
public class ThreadLocalTest {

    /**
     * 测试场景：
     * 在线程执行的某一个方法中使用ThreadLocal，随后调用GC，
     * 在该线程执行另外的方法时，就会出现内存泄漏情况
     */
    public static void main(String[] args) throws InterruptedException {
        Thread main = Thread.currentThread();
        test1();
        System.gc();
        // 打断点，就能看到ThreadLocalMap中的value值为"hello world"、12的referent为null，就产生了内存泄漏
        System.out.println(main);
    }

    public static void test1() throws InterruptedException {
        ThreadLocal<String> threadLocalString = ThreadLocal.withInitial(() -> "hello world");
        ThreadLocal<Integer> threadLocalInt = ThreadLocal.withInitial(() -> 12);
        System.out.println("main 线程threadLocalString值：" + threadLocalString.get());
        System.out.println("main 线程threadLocalInt值：" + threadLocalInt.get());
        System.gc();
        Thread.sleep(2000);
        Thread main = Thread.currentThread();
        // 下面仍会打印出值，因为threadLocalString、threadLocalInt 都是强引用指向ThreadLocalMap Entry中的Key
        // 可打断点观察main线程中的ThreadLocalMap
        System.out.println("main 线程threadLocalString值：" + threadLocalString.get());
        System.out.println("main 线程threadLocalInt值：" + threadLocalInt.get());
    }
}
