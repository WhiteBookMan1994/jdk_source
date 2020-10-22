package lang.thread;

/**
 * join 方法实现三个线程的顺序执行
 * @author dingchenchen
 * @since 2020/10/22
 */
public class JoinMethodTest {

    public static void main(String[] args) {
        Thread t1 = new Thread(new Worker(null), "t1");
        Thread t2 = new Thread(new Worker(t1), "t2");
        Thread t3 = new Thread(new Worker(t2), "t3");
        t1.start();
        t2.start();
        t3.start();
    }

    public static class Worker implements Runnable{

        private Thread beforeThread;

        public Worker(Thread beforeThread){
            this.beforeThread = beforeThread;
        }

        @Override
        public void run() {
            if (beforeThread != null) {
                try {
                    beforeThread.join();
                    System.out.println("thread start:" + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("thread run:" + Thread.currentThread().getName());
            }
        }
    }
}
