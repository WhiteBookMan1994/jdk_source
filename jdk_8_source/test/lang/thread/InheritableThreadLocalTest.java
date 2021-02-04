package lang.thread;

/**
 * @author dingchenchen
 * @since 2020/12/15
 */
public class InheritableThreadLocalTest {

    public static void main(String[] args) {
        //直接使用ThreadLocal，不能在父子线程之间传递数据
        //输出结果：null
       testThreadLocal();

       //InheritableThreadLocal，支持父子线程之间传递参数
        testInheritableThreadLocalTest();

        //阿里巴巴的 TransmittableThreadLocal
        //使用场景：在使用线程池等会池化复用线程的执行组件情况下传递ThreadLocal。因为此时线程不会初始化执行Thread内部的init方法
    }

    public static void testThreadLocal() {
        ThreadLocal<String> local = new ThreadLocal<>();
        local.set("hello main");

        Thread thread = new Thread(()->{
            System.out.println(local.get());
        });
        thread.start();
    }

    public static void testInheritableThreadLocalTest(){
        InheritableThreadLocal<String> inheritable = new InheritableThreadLocal<>();
        inheritable.set("hello main");
        //原理：InheritableThreadLocal extends ThreadLocal，覆盖了一些方法
        // Thread 类中不止维护了 ThreadLocal.ThreadLocalMap threadLocals，
        // 还维护了一个 ThreadLocal.ThreadLocalMap inheritableThreadLocals 用来存储从父线程获得的 inheritableThreadLocals
        // new Thread()初始化执行内部init方法会把父线程中的 inheritThreadLocals 写到 子线程中，完成数据传递
        Thread thread = new Thread(() ->{
            System.out.println(inheritable.get());
        }
        );
        thread.start();
    }
}
